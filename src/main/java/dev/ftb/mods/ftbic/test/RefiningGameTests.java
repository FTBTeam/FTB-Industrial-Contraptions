package dev.ftb.mods.ftbic.test;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.block.ElectricBlockInstance;
import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.block.entity.machine.FluidMachineBlockEntity;
import dev.ftb.mods.ftbic.block.entity.machine.MachineBlockEntity;
import dev.ftb.mods.ftbic.item.FTBICItems;
import dev.ftb.mods.ftbic.item.RefiningItem;
import dev.ftb.mods.ftbic.material.MaterialColor;
import dev.ftb.mods.ftbic.material.RefiningRecipeGenerator;
import dev.ftb.mods.ftbic.recipe.MachineRecipe;
import dev.ftb.mods.ftbic.registry.ModDataComponents;
import dev.ftb.mods.ftbic.util.RefiningIngredient;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.conditions.ConditionalOps;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.event.ModifyRecipeJsonsEvent;
import net.neoforged.neoforge.fluids.FluidStack;

final class RefiningGameTests {
    private static final BlockPos POS = new BlockPos(2, 2, 2);
    private static final Identifier IRON = Identifier.parse("c:iron");
    private static final Identifier COPPER = Identifier.parse("c:copper");

    private static MachineBlockEntity place(GameTestHelper h, ElectricBlockInstance type) {
        h.setBlock(POS, Blocks.AIR);
        h.setBlock(POS, type.block.get());
        return h.getBlockEntity(POS, MachineBlockEntity.class);
    }

    private static ItemStack process(GameTestHelper h, ElectricBlockInstance type, ItemStack input, int water) {
        var machine = place(h, type);
        machine.setStackInSlot(0, input.copy());
        if (machine instanceof FluidMachineBlockEntity fluid)
            fluid.setFluids(new FluidStack(Fluids.WATER, water), FluidStack.EMPTY);
        for (int i = 0; i < 16000 && !machine.inputItems[0].isEmpty(); i++) {
            machine.energy = machine.getEnergyCapacity();
            machine.tick();
        }
        h.assertTrue(machine.inputItems[0].isEmpty(), "All complete batches processed by " + type.id);
        return machine.outputItems[0].copy();
    }

    static void fullChain(GameTestHelper h) {
        ItemStack raw = process(h, FTBICElectricBlocks.MACERATOR, new ItemStack(Items.IRON_ORE), 0);
        h.assertTrue(raw.is(Items.RAW_IRON) && raw.getCount() == 3, "One ore produces exactly three raw iron");
        ItemStack crushed = process(h, FTBICElectricBlocks.MACERATOR, raw, 0);
        h.assertTrue(
                crushed.is(FTBICItems.CRUSHED_ORE.get()) && crushed.getCount() == 6, "Three raw yield six crushed");
        h.assertTrue(IRON.equals(crushed.get(ModDataComponents.REFINING_MATERIAL.get())), "Material survives crushing");
        ItemStack washed = process(h, FTBICElectricBlocks.ORE_WASHER, crushed, 3000);
        h.assertValueEqual(9, washed.getCount(), "Washer yields nine washed iron");
        ItemStack concentrate = process(h, FTBICElectricBlocks.CENTRIFUGE, washed, 0);
        h.assertValueEqual(15, concentrate.getCount(), "Centrifuge yields fifteen concentrate");
        var smelt = h.getLevel()
                .recipeAccess()
                .getRecipeFor(RecipeType.SMELTING, new SingleRecipeInput(concentrate), h.getLevel());
        h.assertTrue(smelt.isPresent(), "Concentrate is supported by vanilla furnaces");
        h.assertTrue(
                smelt.get().value().assemble(new SingleRecipeInput(concentrate)).is(Items.IRON_INGOT),
                "Vanilla furnace preserves material");
        ItemStack ingots = process(h, FTBICElectricBlocks.POWERED_FURNACE, concentrate, 0);
        h.assertTrue(ingots.is(Items.IRON_INGOT) && ingots.getCount() == 15, "One ore completes to fifteen ingots");
        ItemStack singleRaw = process(h, FTBICElectricBlocks.ADVANCED_MACERATOR, new ItemStack(Items.RAW_COPPER), 0);
        ItemStack singleWashed = process(h, FTBICElectricBlocks.ORE_WASHER, singleRaw, 1000);
        ItemStack five = process(h, FTBICElectricBlocks.ADVANCED_CENTRIFUGE, singleWashed, 0);
        h.assertTrue(
                five.getCount() == 5 && COPPER.equals(five.get(ModDataComponents.REFINING_MATERIAL.get())),
                "One raw yields five concentrate using advanced machines");
        ItemStack blockCrushed = process(h, FTBICElectricBlocks.MACERATOR, new ItemStack(Items.RAW_IRON_BLOCK), 0);
        h.assertTrue(
                blockCrushed.is(FTBICItems.CRUSHED_ORE.get()) && blockCrushed.getCount() == 18,
                "One raw block yields eighteen crushed");
        h.assertTrue(
                IRON.equals(blockCrushed.get(ModDataComponents.REFINING_MATERIAL.get())),
                "Material survives raw block crushing");
        h.succeed();
    }

    static void blockingAndIdentity(GameTestHelper h) {
        var machine = (FluidMachineBlockEntity) place(h, FTBICElectricBlocks.ORE_WASHER);
        ItemStack crushed = RefiningItem.stack(FTBICItems.CRUSHED_ORE.get(), IRON, 2);
        machine.setStackInSlot(0, crushed.copy());
        machine.setFluids(new FluidStack(Fluids.WATER, 999), FluidStack.EMPTY);
        machine.energy = machine.getEnergyCapacity();
        double energy = machine.energy;
        machine.tick();
        h.assertTrue(
                machine.progress == 0 && machine.energy == energy && machine.inputItems[0].getCount() == 2,
                "Insufficient water consumes nothing");
        machine.setFluids(new FluidStack(Fluids.WATER, 1000), FluidStack.EMPTY);
        machine.outputItems[0] = RefiningItem.stack(FTBICItems.WASHED_ORE.get(), COPPER, 1);
        machine.tick();
        h.assertTrue(
                machine.getInputFluid().getAmount() == 1000 && machine.energy == energy,
                "Another material blocks the output slot");
        machine.outputItems[0] = ItemStack.EMPTY;
        for (int i = 0; i < 500; i++) {
            machine.energy = machine.getEnergyCapacity();
            machine.tick();
        }
        h.assertTrue(
                machine.getInputFluid().isEmpty() && machine.outputItems[0].getCount() == 3,
                "Successful wash consumes one bucket exactly");
        h.assertTrue(
                !ItemStack.isSameItemSameComponents(
                        machine.outputItems[0], RefiningItem.stack(FTBICItems.WASHED_ORE.get(), COPPER, 3)),
                "Different materials never stack");
        machine.setInputLock(0, crushed);
        h.assertTrue(
                !machine.isItemValid(0, RefiningItem.stack(FTBICItems.CRUSHED_ORE.get(), COPPER, 2)),
                "Ghost lock distinguishes material");
        var ops = h.getLevel().registryAccess().createSerializationContext(JsonOps.INSTANCE);
        var template = ItemStackTemplate.fromNonEmptyStack(crushed);
        var roundTrip = ItemStackTemplate.CODEC
                .parse(ops, ItemStackTemplate.CODEC.encodeStart(ops, template).getOrThrow())
                .getOrThrow()
                .create();
        h.assertTrue(
                ItemStack.isSameItemSameComponents(crushed, roundTrip),
                "Material persists through stack serialization");
        var ingredient = new RefiningIngredient(FTBICItems.CRUSHED_ORE.get(), IRON);
        h.assertTrue(
                ingredient.test(roundTrip) && !ingredient.test(new ItemStack(FTBICItems.CRUSHED_ORE.get())),
                "Missing material is never accepted");
        h.succeed();
    }

    @SuppressWarnings("unchecked")
    static void discoveryAndOverrides(GameTestHelper h) {
        var actual = h.getLevel().registryAccess().lookupOrThrow(Registries.ITEM);
        TagKey<Item> raw = TagKey.create(Registries.ITEM, Identifier.parse("c:raw_materials/testium"));
        TagKey<Item> ingot = TagKey.create(Registries.ITEM, Identifier.parse("c:ingots/testium"));
        TagKey<Item> ore = TagKey.create(Registries.ITEM, Identifier.parse("c:ores/testium"));
        TagKey<Item> rawBlock = TagKey.create(Registries.ITEM, Identifier.parse("c:storage_blocks/raw_testium"));
        Map<TagKey<?>, List<Holder<Item>>> staged = new HashMap<>();
        staged.put(raw, List.of(Items.RAW_IRON.builtInRegistryHolder()));
        staged.put(ingot, List.of(Items.IRON_INGOT.builtInRegistryHolder()));
        staged.put(ore, List.of(Items.IRON_ORE.builtInRegistryHolder()));
        staged.put(rawBlock, List.of(Items.RAW_IRON_BLOCK.builtInRegistryHolder()));
        HolderLookup.RegistryLookup<Item> lookup = new HolderLookup.RegistryLookup.Delegate<>() {
            @Override
            public HolderLookup.RegistryLookup<Item> parent() {
                return actual;
            }

            @Override
            public Stream<HolderSet.Named<Item>> listTags() {
                return Stream.of(
                        HolderSet.emptyNamed(actual, raw),
                        HolderSet.emptyNamed(actual, ingot),
                        HolderSet.emptyNamed(actual, ore));
            }
        };
        var registries = HolderLookup.Provider.create(
                h.getLevel().registryAccess().listRegistries().map(r -> r.key().equals(Registries.ITEM) ? lookup : r));
        var context = new ICondition.IContext() {
            @Override
            public <T> boolean isTagLoaded(TagKey<T> key) {
                return staged.containsKey(key);
            }

            @Override
            public <T> Collection<Holder<T>> getTag(TagKey<T> key) {
                return (Collection<Holder<T>>) (Collection<?>) staged.getOrDefault(key, List.of());
            }
        };
        var ops = new ConditionalOps<>(registries.createSerializationContext(JsonOps.INSTANCE), context);
        Map<Identifier, JsonElement> source = new HashMap<>();
        JsonObject smelt = new JsonObject();
        smelt.addProperty("type", "minecraft:smelting");
        smelt.addProperty("ingredient", "minecraft:raw_iron");
        smelt.addProperty("result", "minecraft:iron_ingot");
        source.put(FTBIC.id("test/smelting"), smelt);
        Map<Identifier, JsonElement> generated = new HashMap<>(source);
        RefiningRecipeGenerator.recipes(new ModifyRecipeJsonsEvent(ops, generated));
        Identifier id = Identifier.parse("c:testium");
        Identifier washId = RefiningRecipeGenerator.recipeId(id, "washing");
        h.assertTrue(generated.containsKey(washId), "Unknown material discovered from newly staged tags");
        MachineRecipe wash =
                (MachineRecipe) Recipe.CODEC.parse(ops, generated.get(washId)).getOrThrow();
        h.assertTrue(
                wash.inputs.getFirst().count() == 2
                        && wash.outputs.getFirst().stack().getCount() == 3,
                "Generated washer recipe decodes with exact batches");
        h.assertTrue(
                wash.inputs.getFirst().ingredient().test(RefiningItem.stack(FTBICItems.CRUSHED_ORE.get(), id, 2)),
                "Generated ingredient includes material identity");
        Identifier blockId = RefiningRecipeGenerator.recipeId(id, "raw_block_crushing");
        JsonObject blockCrush = generated.get(blockId).getAsJsonObject();
        h.assertValueEqual(
                "#c:storage_blocks/raw_testium",
                blockCrush
                        .getAsJsonArray("inputs")
                        .get(0)
                        .getAsJsonObject()
                        .get("ingredient")
                        .getAsString(),
                "Raw block stage uses the raw block tag");
        h.assertValueEqual(
                18,
                blockCrush
                        .getAsJsonArray("outputs")
                        .get(0)
                        .getAsJsonObject()
                        .getAsJsonObject("item")
                        .get("count")
                        .getAsInt(),
                "Raw block crushes to nine raw items of crushed ore");
        JsonObject profile = new JsonObject();
        profile.addProperty("type", "ftbic:refining_material");
        JsonObject definition = new JsonObject();
        definition.addProperty("material", id.toString());
        JsonObject yields = new JsonObject();
        yields.addProperty("refine_output", 7);
        yields.addProperty("raw_to_crushed", 8);
        definition.add("yields", yields);
        definition.addProperty("color", 0x123456);
        definition.addProperty("name", "Test Metal");
        profile.add("definition", definition);
        source.put(FTBIC.id("test/profile"), profile);
        generated = new HashMap<>(source);
        RefiningRecipeGenerator.recipes(new ModifyRecipeJsonsEvent(ops, generated));
        MachineRecipe refine = (MachineRecipe) Recipe.CODEC
                .parse(ops, generated.get(RefiningRecipeGenerator.recipeId(id, "centrifuging")))
                .getOrThrow();
        h.assertValueEqual(7, refine.outputs.getFirst().stack().getCount(), "Datapack overrides automatic yield");
        h.assertTrue(!generated.containsKey(blockId), "Raw block stage is skipped when its output exceeds one stack");
        definition.add(
                "byproducts",
                JsonParser.parseString(
                        "[{\"item\":{\"id\":\"minecraft:iron_nugget\"},\"chance\":0.25},{\"item\":{\"id\":\"minecraft:gold_nugget\"},\"chance\":0.5}]"));
        generated = new HashMap<>(source);
        RefiningRecipeGenerator.recipes(new ModifyRecipeJsonsEvent(ops, generated));
        MachineRecipe withByproducts = (MachineRecipe) Recipe.CODEC
                .parse(ops, generated.get(RefiningRecipeGenerator.recipeId(id, "centrifuging")))
                .getOrThrow();
        h.assertTrue(
                withByproducts.outputs.size() == 3
                        && withByproducts.outputs.get(1).chance() == 0.25,
                "Configured byproducts preserve outputs and probabilities");
        staged.remove(raw);
        generated = new HashMap<>(source);
        RefiningRecipeGenerator.recipes(new ModifyRecipeJsonsEvent(ops, generated));
        h.assertTrue(
                !generated.containsKey(washId) && !generated.containsKey(FTBIC.id("test/profile")),
                "Removed raw tag stops generating recipes and client variants");
        staged.put(raw, List.of(Items.RAW_IRON.builtInRegistryHolder()));
        definition.addProperty("enabled", false);
        generated = new HashMap<>(source);
        RefiningRecipeGenerator.recipes(new ModifyRecipeJsonsEvent(ops, generated));
        h.assertTrue(!generated.containsKey(washId), "Disabling a material on reload removes its generated chain");
        definition.addProperty("enabled", true);
        source.put(washId, smelt);
        generated = new HashMap<>(source);
        RefiningRecipeGenerator.recipes(new ModifyRecipeJsonsEvent(ops, generated));
        h.assertTrue(generated.get(washId) == smelt, "Explicit recipe ID takes precedence over generation");
        h.succeed();
    }

    static void colors(GameTestHelper h) {
        h.assertValueEqual(
                MaterialColor.FALLBACK,
                MaterialColor.sample(new int[] {0, 0xFFFFFFFF, 0xFF000000}),
                "Empty or highlight-only texture uses fallback");
        int copper = MaterialColor.sample(new int[] {0, 0xFFFFFFFF, 0xFF000000, 0xFFC08050, 0xFFC08050, 0xFFC08050});
        h.assertTrue(
                ((copper >> 16) & 255) > ((copper >> 8) & 255) && ((copper >> 8) & 255) > (copper & 255),
                "Copper keeps warm hue despite outlines and highlights");
        int gray = MaterialColor.sample(new int[] {0xFF909090, 0xFF909090});
        h.assertTrue(((gray >> 16) & 255) == (gray & 255), "Neutral metal remains neutral");
        h.succeed();
    }

    private RefiningGameTests() {}
}
