package dev.ftb.mods.ftbic.material;

import dev.ftb.mods.ftbic.recipe.RefiningMaterialRecipe;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.RecipeHolder;

/** Client presentation metadata. Recipes remain the server authority. */
public final class RefiningCatalog {
    private static Map<Identifier, RefiningDefinition> materials = Map.of();
    public static int revision;

    public static void update(List<RecipeHolder<?>> recipes) {
        Map<Identifier, RefiningDefinition> next = new TreeMap<>();
        for (var recipe : recipes)
            if (recipe.value() instanceof RefiningMaterialRecipe material
                    && material.definition.enabled()
                    && !material.definition.ingot().isEmpty()) {
                next.put(material.definition.material(), material.definition);
            }
        materials = Map.copyOf(next);
        revision++;
    }

    public static Map<Identifier, RefiningDefinition> materials() {
        return materials;
    }

    public static String name(Identifier id) {
        var material = materials.get(id);
        if (material != null && !material.name().isEmpty()) return material.name();
        return Arrays.stream(id.getPath().replace('/', '_').split("_"))
                .filter(s -> !s.isEmpty())
                .map(s -> Character.toUpperCase(s.charAt(0)) + s.substring(1))
                .collect(Collectors.joining(" "));
    }

    private RefiningCatalog() {}
}
