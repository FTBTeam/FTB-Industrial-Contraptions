package dev.ftb.mods.ftbic.recipe;

import com.google.gson.JsonObject;
import dev.ftb.mods.ftbic.FTBIC;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.ForgeRegistryEntry;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class MachineRecipeSerializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<MachineRecipe> {
	public final Function<ResourceLocation, MachineRecipe> factory;
	public final RecipeType<MachineRecipe> recipeType;

	public MachineRecipeSerializer(String id, Function<ResourceLocation, MachineRecipe> f) {
		factory = f;
		recipeType = RecipeType.register(FTBIC.MOD_ID + ":" + id);
	}

	@Override
	public MachineRecipe fromJson(ResourceLocation id, JsonObject json) {
		MachineRecipe recipe = factory.apply(id);
		recipe.parse(json);
		return recipe;
	}

	@Nullable
	@Override
	public MachineRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
		MachineRecipe recipe = factory.apply(id);
		recipe.read(buf);
		return recipe;
	}

	@Override
	public void toNetwork(FriendlyByteBuf buf, MachineRecipe recipe) {
		recipe.write(buf);
	}
}
