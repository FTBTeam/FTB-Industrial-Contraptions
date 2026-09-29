package dev.ftb.mods.ftbic.recipe;

import com.mojang.serialization.MapCodec;
import dev.ftb.mods.ftbic.FTBIC;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public final class FTBICRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, FTBIC.MOD_ID);

    public static final DeferredRegister<RecipeType<?>> TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, FTBIC.MOD_ID);

    public static final DeferredRegister<MapCodec<? extends ICondition>> CONDITIONS =
            DeferredRegister.create(NeoForgeRegistries.Keys.CONDITION_CODECS, FTBIC.MOD_ID);

    public static final DeferredHolder<MapCodec<? extends ICondition>, MapCodec<FullFEModeCondition>> FULL_FE_MODE =
            CONDITIONS.register("full_fe_mode", () -> FullFEModeCondition.CODEC);

    public static final DeferredHolder<MapCodec<? extends ICondition>, MapCodec<RecipeToggleCondition>> RECIPE_TOGGLE =
            CONDITIONS.register("recipe_toggle", () -> RecipeToggleCondition.CODEC);

    // Machine recipes: MachineRecipeType auto-registers its RecipeType + RecipeSerializer.
    public static final MachineRecipeType SMELTING =
            new MachineRecipeType("smelting", false, false, TYPES, SERIALIZERS);
    public static final MachineRecipeType MACERATING =
            new MachineRecipeType("macerating", false, true, TYPES, SERIALIZERS);
    public static final MachineRecipeType SEPARATING =
            new MachineRecipeType("separating", false, true, TYPES, SERIALIZERS);
    public static final MachineRecipeType COMPRESSING =
            new MachineRecipeType("compressing", false, false, TYPES, SERIALIZERS);
    public static final MachineRecipeType REPROCESSING =
            new MachineRecipeType("reprocessing", false, false, TYPES, SERIALIZERS);
    public static final MachineRecipeType CANNING = new MachineRecipeType("canning", true, false, TYPES, SERIALIZERS);
    public static final MachineRecipeType ROLLING = new MachineRecipeType("rolling", false, false, TYPES, SERIALIZERS);
    public static final MachineRecipeType EXTRUDING =
            new MachineRecipeType("extruding", false, false, TYPES, SERIALIZERS);
    public static final MachineRecipeType ALLOY_SMELTING =
            new MachineRecipeType("alloy_smelting", false, false, TYPES, SERIALIZERS);

    public static final MachineRecipeType WASHING = new MachineRecipeType("washing", false, false, TYPES, SERIALIZERS);
    public static final MachineRecipeType HYDROPONIC_GROWTH =
            new MachineRecipeType("hydroponic_growth", false, true, TYPES, SERIALIZERS);
    public static final MachineRecipeType HYDROPONIC_MUTATION =
            new MachineRecipeType("hydroponic_mutation", true, false, TYPES, SERIALIZERS);
    public static final DeferredHolder<RecipeType<?>, RecipeType<MachineRecipe>> REFINING_MATERIAL =
            TYPES.register("refining_material", () -> new RecipeType<>() {});
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<RefiningMaterialRecipe>>
            REFINING_MATERIAL_SERIALIZER = SERIALIZERS.register(
                    "refining_material",
                    () -> new RecipeSerializer<>(RefiningMaterialRecipe.CODEC, RefiningMaterialRecipe.STREAM_CODEC));

    // Basic generator fuel burn-time recipes.
    public static final DeferredHolder<RecipeType<?>, RecipeType<?>> BASIC_GENERATOR_FUEL =
            TYPES.register("basic_generator_fuel", () -> new RecipeType<>() {
                @Override
                public String toString() {
                    return "ftbic:basic_generator_fuel";
                }
            });

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<BasicGeneratorFuelRecipe>>
            BASIC_GENERATOR_FUEL_SERIALIZER = castSerializer(SERIALIZERS.register(
            "basic_generator_fuel",
            () -> new RecipeSerializer<>(BasicGeneratorFuelRecipe.CODEC, BasicGeneratorFuelRecipe.STREAM_CODEC)));

    // Antimatter constructor boost recipes.
    public static final DeferredHolder<RecipeType<?>, RecipeType<?>> ANTIMATTER_BOOST =
            TYPES.register("antimatter_boost", () -> new RecipeType<>() {
                @Override
                public String toString() {
                    return "ftbic:antimatter_boost";
                }
            });

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<AntimatterBoostRecipe>>
            ANTIMATTER_BOOST_SERIALIZER = castSerializer(SERIALIZERS.register(
            "antimatter_boost",
            () -> new RecipeSerializer<>(AntimatterBoostRecipe.CODEC, AntimatterBoostRecipe.STREAM_CODEC)));

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static <T extends Recipe<?>> DeferredHolder<RecipeSerializer<?>, RecipeSerializer<T>> castSerializer(
            DeferredHolder<RecipeSerializer<?>, ? extends RecipeSerializer<?>> raw) {
        return (DeferredHolder) raw;
    }

    private FTBICRecipes() {}
}
