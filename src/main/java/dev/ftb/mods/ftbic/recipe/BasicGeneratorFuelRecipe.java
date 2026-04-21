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

public record BasicGeneratorFuelRecipe(Ingredient ingredient, int ticks) implements Recipe<NoInput> {

	public static final MapCodec<BasicGeneratorFuelRecipe> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
			Ingredient.CODEC.fieldOf("ingredient").forGetter(BasicGeneratorFuelRecipe::ingredient),
			Codec.INT.fieldOf("ticks").forGetter(BasicGeneratorFuelRecipe::ticks)
	).apply(i, BasicGeneratorFuelRecipe::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, BasicGeneratorFuelRecipe> STREAM_CODEC = StreamCodec.composite(
			Ingredient.CONTENTS_STREAM_CODEC, BasicGeneratorFuelRecipe::ingredient,
			ByteBufCodecs.VAR_INT, BasicGeneratorFuelRecipe::ticks,
			BasicGeneratorFuelRecipe::new
	);

	@Override public boolean matches(NoInput input, Level level) { return false; }
	@Override public ItemStack assemble(NoInput input) { return ItemStack.EMPTY; }
	@Override public String group() { return ""; }
	@Override public boolean showNotification() { return false; }
	@Override public boolean isSpecial() { return true; }
	@Override public RecipeSerializer<? extends Recipe<NoInput>> getSerializer() { return FTBICRecipes.BASIC_GENERATOR_FUEL_SERIALIZER.get(); }
	@Override public RecipeType<? extends Recipe<NoInput>> getType() {
		@SuppressWarnings("unchecked")
		RecipeType<? extends Recipe<NoInput>> t = (RecipeType<? extends Recipe<NoInput>>) (RecipeType<?>) FTBICRecipes.BASIC_GENERATOR_FUEL.get();
		return t;
	}
	@Override public PlacementInfo placementInfo() { return PlacementInfo.NOT_PLACEABLE; }
	@Override public RecipeBookCategory recipeBookCategory() { return RecipeBookCategories.CRAFTING_MISC; }
}
