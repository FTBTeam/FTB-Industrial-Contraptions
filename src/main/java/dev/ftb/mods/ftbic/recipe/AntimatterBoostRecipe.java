package dev.ftb.mods.ftbic.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

/**
 * Antimatter constructor boost item — multiplies the machine's construction rate by `boost` when the
 * matching ingredient is present.
 */
public record AntimatterBoostRecipe(Ingredient ingredient, double boost) implements Recipe<NoInput> {

	public static final MapCodec<AntimatterBoostRecipe> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
			Ingredient.CODEC.fieldOf("ingredient").forGetter(AntimatterBoostRecipe::ingredient),
			Codec.DOUBLE.fieldOf("boost").forGetter(AntimatterBoostRecipe::boost)
	).apply(i, AntimatterBoostRecipe::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, AntimatterBoostRecipe> STREAM_CODEC = StreamCodec.composite(
			Ingredient.CONTENTS_STREAM_CODEC, AntimatterBoostRecipe::ingredient,
			ByteBufCodecs.DOUBLE, AntimatterBoostRecipe::boost,
			AntimatterBoostRecipe::new
	);

	@Override public boolean matches(NoInput input, Level level) { return false; }
	@Override public ItemStack assemble(NoInput input) { return ItemStack.EMPTY; }
	@Override public String group() { return ""; }
	@Override public boolean showNotification() { return false; }
	@Override public RecipeSerializer<? extends Recipe<NoInput>> getSerializer() { return FTBICRecipes.ANTIMATTER_BOOST_SERIALIZER.get(); }
	@Override public RecipeType<? extends Recipe<NoInput>> getType() {
		@SuppressWarnings("unchecked")
		RecipeType<? extends Recipe<NoInput>> t = (RecipeType<? extends Recipe<NoInput>>) (RecipeType<?>) FTBICRecipes.ANTIMATTER_BOOST.get();
		return t;
	}
	@Override public PlacementInfo placementInfo() { return PlacementInfo.NOT_PLACEABLE; }
	@Override public RecipeBookCategory recipeBookCategory() { return RecipeBookCategories.CRAFTING_MISC; }
}
