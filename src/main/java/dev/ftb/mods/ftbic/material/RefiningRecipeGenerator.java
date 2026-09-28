package dev.ftb.mods.ftbic.material;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.item.RefiningItem;
import dev.ftb.mods.ftbic.recipe.RefiningMaterialRecipe;
import dev.ftb.mods.ftbic.util.StackWithChance;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Function;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.conditions.ConditionalOps;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.event.ModifyRecipeJsonsEvent;

/** Expands material profiles into normal server recipes using the tags staged for this reload. */
@EventBusSubscriber(modid = FTBIC.MOD_ID)
public final class RefiningRecipeGenerator {
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void recipes(ModifyRecipeJsonsEvent event) {
        var getter = event.lookupOrThrow(Registries.ITEM).getter();
        if (!(getter instanceof HolderLookup<?> lookup)) {
            throw new IllegalStateException("Refining requires the staged item tag lookup");
        }
        var context = ConditionalOps.retrieveContext()
                .codec()
                .parse(event.getOps(), new JsonObject())
                .getOrThrow();
        var tags = new MaterialLookup(getter, context);
        Map<Identifier, RefiningDefinition> definitions = new TreeMap<>();
        lookup.listTags().forEach(tag -> {
            Identifier id = tag.key().location();
            if (id.getNamespace().equals("c")
                    && id.getPath().startsWith("raw_materials/")
                    && !context.getTag(TagKey.create(Registries.ITEM, id)).isEmpty()) {
                Identifier material =
                        Identifier.fromNamespaceAndPath("c", id.getPath().substring("raw_materials/".length()));
                definitions.put(material, RefiningDefinition.automatic(material));
            }
        });
        Map<Identifier, Identifier> profileIds = new HashMap<>();
        var jsons = event.getRecipeJsons();
        for (var entry : new TreeMap<>(jsons).entrySet()) {
            JsonObject json = entry.getValue().isJsonObject() ? entry.getValue().getAsJsonObject() : new JsonObject();
            if (!json.has("type") || !json.get("type").getAsString().equals("ftbic:refining_material")) continue;
            jsons.remove(entry.getKey()); // Only validated, resolved profiles are sent to clients.
            var profile = ICondition.getConditionally(RefiningMaterialRecipe.CODEC.codec(), event.getOps(), json);
            if (profile.isEmpty()) continue;
            RefiningDefinition definition = profile.get().definition;
            if (profileIds.put(definition.material(), entry.getKey()) != null) {
                throw new IllegalArgumentException("Duplicate refining definition for " + definition.material());
            }
            definitions.put(definition.material(), definition);
        }
        List<SmeltInput> smelting = new ArrayList<>();
        for (JsonElement value : jsons.values()) {
            if (value.isJsonObject()
                    && value.getAsJsonObject().has("type")
                    && value.getAsJsonObject().get("type").getAsString().equals("minecraft:smelting")) {
                if (!ICondition.conditionsMatched(event.getOps(), value)) continue;
                JsonObject json = value.getAsJsonObject();
                if (!json.has("result") || !json.has("ingredient")) continue;
                ItemStackTemplate.CODEC
                        .parse(event.getOps(), json.get("result"))
                        .resultOrPartial(error -> FTBIC.LOGGER.warn(
                                "Ignoring invalid smelting result during refining discovery: {}", error))
                        .ifPresent(output -> smelting.add(new SmeltInput(
                                json.get("ingredient"), output.item().value(), output.count())));
            }
        }
        Set<Identifier> ambiguous =
                conflicts(definitions.values(), tags, d -> d.selector("raw_materials/", d.rawInput()));
        Set<Identifier> ambiguousBlocks =
                conflicts(definitions.values(), tags, d -> d.selector("storage_blocks/raw_", d.rawBlockInput()));
        int generated = 0;
        for (var definition : definitions.values()) {
            Identifier material = definition.material();
            if (!definition.enabled()) continue;
            if (ambiguous.contains(material)) {
                FTBIC.LOGGER.warn(
                        "Skipping refining {}: raw input belongs to multiple materials; narrow or disable a profile",
                        material);
                continue;
            }
            List<Item> raws = select(tags, definition.selector("raw_materials/", definition.rawInput()));
            List<Item> ingots = select(tags, "#c:ingots/" + material.getPath());
            Item raw = choose(tags, raws, definition.rawOutput());
            Item ingot = choose(tags, ingots, definition.ingot());
            if (raw == null
                    || ingot == null
                    || ingot instanceof RefiningItem
                    || raw instanceof RefiningItem
                    || raws.isEmpty()
                    || !raws.contains(raw)) {
                FTBIC.LOGGER.warn("Skipping refining {}: missing raw/ingot mapping", material);
                continue;
            }
            if (definition.ingot().isEmpty() && !hasValidSmelting(raws, ingots, smelting, tags)) {
                FTBIC.LOGGER.warn(
                        "Skipping refining {}: raw smelting is missing or conflicts with the ingot tag; supply an explicit ingot",
                        material);
                continue;
            }
            if (definition.byproducts().size() > 2)
                throw new IllegalArgumentException("At most two refining byproducts: " + material);
            var resolved = definition.resolved(id(raw), id(ingot));
            Identifier profileId = profileIds.getOrDefault(material, recipeId(material, "material"));
            JsonObject metadata = RefiningMaterialRecipe.CODEC
                    .codec()
                    .encodeStart(event.getOps(), new RefiningMaterialRecipe(resolved))
                    .getOrThrow()
                    .getAsJsonObject();
            metadata.addProperty("type", "ftbic:refining_material");
            jsons.put(profileId, metadata);
            boolean rawBlocks = !ambiguousBlocks.contains(material);
            if (!rawBlocks)
                FTBIC.LOGGER.warn(
                        "Skipping raw block crushing for {}: raw block input belongs to multiple materials", material);
            generate(jsons, resolved, tags, event.getOps(), rawBlocks);
            generated++;
        }
        FTBIC.LOGGER.info("Loaded {} refining material chains", generated);
    }

    private record MaterialLookup(HolderGetter<Item> getter, ICondition.IContext context) {}

    private record SmeltInput(JsonElement input, Item output, int count) {}

    private static Set<Identifier> conflicts(
            Collection<RefiningDefinition> definitions,
            MaterialLookup tags,
            Function<RefiningDefinition, String> selector) {
        Map<Item, Identifier> owners = new HashMap<>();
        Set<Identifier> ambiguous = new HashSet<>();
        for (var definition : definitions) {
            if (!definition.enabled()) continue;
            for (Item item : select(tags, selector.apply(definition))) {
                Identifier previous = owners.putIfAbsent(item, definition.material());
                if (previous != null && !previous.equals(definition.material())) {
                    ambiguous.add(previous);
                    ambiguous.add(definition.material());
                }
            }
        }
        return ambiguous;
    }

    private static boolean ingredientMatches(JsonElement input, Item raw, MaterialLookup tags) {
        if (input.isJsonPrimitive()) return select(tags, input.getAsString()).contains(raw);
        if (input.isJsonArray()) {
            for (JsonElement entry : input.getAsJsonArray()) if (ingredientMatches(entry, raw, tags)) return true;
        }
        return false;
    }

    private static boolean hasValidSmelting(
            List<Item> raws, List<Item> ingots, List<SmeltInput> recipes, MaterialLookup tags) {
        for (Item raw : raws) {
            boolean found = false;
            for (var recipe : recipes)
                if (ingredientMatches(recipe.input(), raw, tags)) {
                    if (recipe.count() != 1 || !ingots.contains(recipe.output())) return false;
                    found = true;
                }
            if (!found) return false;
        }
        return true;
    }

    private static String id(Item item) {
        return BuiltInRegistries.ITEM.getKey(item).toString();
    }

    private static Item choose(MaterialLookup tags, List<Item> options, String explicit) {
        if (!explicit.isEmpty())
            return tags.getter()
                    .get(ResourceKey.create(Registries.ITEM, Identifier.parse(explicit)))
                    .map(Holder::value)
                    .orElse(null);
        // Stable preference shared by every stage; never depend on tag iteration order.
        return options.stream()
                .min(Comparator.comparingInt((Item item) -> {
                            String namespace =
                                    BuiltInRegistries.ITEM.getKey(item).getNamespace();
                            return namespace.equals("minecraft") ? 0 : namespace.equals("ftbic") ? 1 : 2;
                        })
                        .thenComparing(RefiningRecipeGenerator::id))
                .orElse(null);
    }

    private static List<Item> select(MaterialLookup tags, String selector) {
        if (selector.startsWith("#"))
            return tags
                    .context()
                    .getTag(TagKey.create(Registries.ITEM, Identifier.parse(selector.substring(1))))
                    .stream()
                    .map(Holder::value)
                    .toList();
        return tags.getter()
                .get(ResourceKey.create(Registries.ITEM, Identifier.parse(selector)))
                .map(h -> List.of(h.value()))
                .orElse(List.of());
    }

    public static Identifier recipeId(Identifier material, String stage) {
        return FTBIC.id("refining/" + material.getNamespace() + "/" + material.getPath() + "/" + stage);
    }

    private static void generate(
            Map<Identifier, JsonElement> recipes,
            RefiningDefinition d,
            MaterialLookup tags,
            RegistryOps<JsonElement> ops,
            boolean rawBlocks) {
        var y = d.yields();
        var c = d.costs();
        String ore = d.selector("ores/", d.oreInput());
        String raw = d.selector("raw_materials/", d.rawInput());
        String rawBlock = d.selector("storage_blocks/raw_", d.rawBlockInput());
        // Retire only FTBIC's old generated shortcuts for supported materials. Explicit replacement IDs win below.
        for (String prefix : List.of("ores/", "raw_materials/", "storage_blocks/raw_")) {
            recipes.remove(FTBIC.id("macerating/" + prefix + d.material().getPath() + "_to_dust"));
        }
        if (!select(tags, ore).isEmpty())
            add(
                    recipes,
                    d,
                    "ore_to_raw",
                    machine(
                            "macerating",
                            new JsonPrimitive(ore),
                            1,
                            stack(d.rawOutput(), y.oreToRaw(), null),
                            c.crushTime()));
        add(
                recipes,
                d,
                "crushing",
                machine(
                        "macerating",
                        new JsonPrimitive(raw),
                        1,
                        intermediate("crushed_ore", y.rawToCrushed(), d),
                        c.crushTime()));
        if (rawBlocks && !select(tags, rawBlock).isEmpty()) {
            int blockCrushed = y.rawToCrushed() * 9;
            if (blockCrushed <= Item.DEFAULT_MAX_STACK_SIZE) {
                add(
                        recipes,
                        d,
                        "raw_block_crushing",
                        machine(
                                "macerating",
                                new JsonPrimitive(rawBlock),
                                1,
                                intermediate("crushed_ore", blockCrushed, d),
                                c.crushTime() * 9));
            } else if (!recipes.containsKey(recipeId(d.material(), "raw_block_crushing"))) {
                FTBIC.LOGGER.warn(
                        "Skipping raw block crushing for {}: {} crushed ore per block exceeds one stack; supply {} explicitly",
                        d.material(),
                        blockCrushed,
                        recipeId(d.material(), "raw_block_crushing"));
            }
        }
        JsonObject washing = machine(
                "washing",
                ingredient("crushed_ore", d),
                y.washInput(),
                intermediate("washed_ore", y.washOutput(), d),
                c.washTime());
        JsonObject fluid = new JsonObject();
        fluid.addProperty("ingredient", c.washFluid());
        fluid.addProperty("amount", c.fluidAmount());
        JsonArray fluids = new JsonArray();
        fluids.add(fluid);
        washing.add("input_fluids", fluids);
        add(recipes, d, "washing", washing);
        JsonObject refining = machine(
                "separating",
                ingredient("washed_ore", d),
                y.refineInput(),
                intermediate("refined_concentrate", y.refineOutput(), d),
                c.refineTime());
        for (var byproduct : d.byproducts())
            refining.getAsJsonArray("outputs")
                    .add(StackWithChance.CODEC.encodeStart(ops, byproduct).getOrThrow());
        add(recipes, d, "centrifuging", refining);
        for (String stage : List.of("crushed_ore", "washed_ore", "refined_concentrate")) {
            JsonObject smelt = new JsonObject();
            smelt.addProperty("type", "minecraft:smelting");
            smelt.addProperty("category", "misc");
            smelt.addProperty("experience", 0);
            smelt.addProperty("cookingtime", 200);
            smelt.add("ingredient", ingredient(stage, d));
            smelt.add("result", stack(d.ingot(), 1, null));
            add(recipes, d, "smelting/" + stage, smelt);
        }
    }

    private static void add(Map<Identifier, JsonElement> recipes, RefiningDefinition d, String stage, JsonObject json) {
        recipes.putIfAbsent(recipeId(d.material(), stage), json);
    }

    private static JsonObject intermediate(String item, int count, RefiningDefinition d) {
        return stack("ftbic:" + item, count, d.material());
    }

    private static JsonObject stack(String item, int count, Identifier material) {
        JsonObject stack = new JsonObject();
        stack.addProperty("id", item);
        stack.addProperty("count", count);
        if (material != null) {
            JsonObject components = new JsonObject();
            components.addProperty("ftbic:refining_material", material.toString());
            stack.add("components", components);
        }
        return stack;
    }

    private static JsonObject ingredient(String item, RefiningDefinition d) {
        JsonObject ingredient = new JsonObject();
        ingredient.addProperty("neoforge:ingredient_type", "ftbic:refining");
        ingredient.addProperty("item", "ftbic:" + item);
        ingredient.addProperty("material", d.material().toString());
        return ingredient;
    }

    private static JsonObject machine(String type, JsonElement ingredient, int count, JsonObject output, double time) {
        JsonObject recipe = new JsonObject();
        recipe.addProperty("type", "ftbic:" + type);
        recipe.addProperty("processing_time", time);
        JsonObject input = new JsonObject();
        input.add("ingredient", ingredient);
        input.addProperty("count", count);
        JsonArray inputs = new JsonArray();
        inputs.add(input);
        recipe.add("inputs", inputs);
        JsonObject result = new JsonObject();
        result.add("item", output);
        JsonArray outputs = new JsonArray();
        outputs.add(result);
        recipe.add("outputs", outputs);
        return recipe;
    }

    private RefiningRecipeGenerator() {}
}
