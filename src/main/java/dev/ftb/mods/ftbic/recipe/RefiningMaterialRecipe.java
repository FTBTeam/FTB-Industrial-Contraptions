package dev.ftb.mods.ftbic.recipe;

import com.mojang.serialization.MapCodec;
import dev.ftb.mods.ftbic.material.RefiningDefinition;
import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

/** Material metadata travels with the recipe set, including login and successful reloads. */
public final class RefiningMaterialRecipe extends MachineRecipe {
    public static final MapCodec<RefiningMaterialRecipe> CODEC =
            RefiningDefinition.CODEC.fieldOf("definition").xmap(RefiningMaterialRecipe::new, r -> r.definition);
    public static final StreamCodec<RegistryFriendlyByteBuf, RefiningMaterialRecipe> STREAM_CODEC =
            ByteBufCodecs.fromCodecWithRegistries(RefiningDefinition.CODEC)
                    .map(RefiningMaterialRecipe::new, r -> r.definition);
    public final RefiningDefinition definition;

    public RefiningMaterialRecipe(RefiningDefinition definition) {
        super(FTBICRecipes.MACERATING, List.of(), List.of(), List.of(), List.of(), 1, true);
        this.definition = definition;
    }

    @Override
    public RecipeSerializer<? extends Recipe<NoInput>> getSerializer() {
        return FTBICRecipes.REFINING_MATERIAL_SERIALIZER.get();
    }

    @Override
    public RecipeType<MachineRecipe> getType() {
        return FTBICRecipes.REFINING_MATERIAL.get();
    }
}
