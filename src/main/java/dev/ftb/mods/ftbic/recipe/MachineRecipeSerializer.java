package dev.ftb.mods.ftbic.recipe;

import com.google.gson.JsonObject;
import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.util.FTBICUtils;
import dev.ftb.mods.ftbic.util.IngredientWithCount;
import dev.ftb.mods.ftbic.util.StackWithChance;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistryEntry;
import org.jetbrains.annotations.Nullable;

public class MachineRecipeSerializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<MachineRecipe> {
	public final RecipeType<MachineRecipe> recipeType;

	public MachineRecipeSerializer(String id) {
		recipeType = RecipeType.register(FTBIC.MOD_ID + ":" + id);
	}

	@Override
	public MachineRecipe fromJson(ResourceLocation id, JsonObject json) {
		MachineRecipe recipe = new MachineRecipe(this, id);
		recipe.inputItems = FTBICUtils.listFromJson(json, "inputItems", IngredientWithCount::new);
		recipe.inputFluids = FTBICUtils.listFromJson(json, "inputFluids", FTBICUtils::fluidFromJson);
		recipe.outputItems = FTBICUtils.listFromJson(json, "outputItems", StackWithChance::new);
		recipe.outputFluids = FTBICUtils.listFromJson(json, "outputFluids", FTBICUtils::fluidFromJson);
		recipe.processingTime = json.has("processingTime") ? json.get("processingTime").getAsInt() : 0;
		return recipe;
	}

	@Nullable
	@Override
	public MachineRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
		MachineRecipe recipe = new MachineRecipe(this, id);
		recipe.inputItems = FTBICUtils.listFromNet(buf, IngredientWithCount::new);
		recipe.inputFluids = FTBICUtils.listFromNet(buf, FluidStack::readFromPacket);
		recipe.outputItems = FTBICUtils.listFromNet(buf, StackWithChance::new);
		recipe.outputFluids = FTBICUtils.listFromNet(buf, FluidStack::readFromPacket);
		recipe.processingTime = buf.readVarInt();
		return recipe;
	}

	@Override
	public void toNetwork(FriendlyByteBuf buf, MachineRecipe recipe) {
		FTBICUtils.listToNet(recipe.inputItems, buf, IngredientWithCount::write);
		FTBICUtils.listToNet(recipe.inputFluids, buf, FluidStack::writeToPacket);
		FTBICUtils.listToNet(recipe.outputItems, buf, StackWithChance::write);
		FTBICUtils.listToNet(recipe.outputFluids, buf, FluidStack::writeToPacket);
		buf.writeVarInt(recipe.processingTime);
	}
}
