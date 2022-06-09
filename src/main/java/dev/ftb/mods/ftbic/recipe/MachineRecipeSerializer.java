package dev.ftb.mods.ftbic.recipe;

import com.google.gson.JsonObject;
import dev.ftb.mods.ftbic.util.FTBICUtils;
import dev.ftb.mods.ftbic.util.IngredientWithCount;
import dev.ftb.mods.ftbic.util.StackWithChance;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

public class MachineRecipeSerializer implements RecipeSerializer<MachineRecipe> {
	public final RecipeType<MachineRecipe> recipeType;
	public int guiWidth = 82;
	public int guiHeight = 54;
	public int energyX = 2;
	public int energyY = 20;
	public int progressX = 24;
	public int progressY = 18;
	public int batteryX = 0;
	public int batteryY = 36;
	public int outputX = 56;
	public int outputY = 14;
	public int inputSlots = 1;
	public int outputSlots = 1;

	public MachineRecipeSerializer(RegistryObject<RecipeType<Recipe<?>>> id) {
		recipeType = (RecipeType<MachineRecipe>) (Object) id.get();
	}

	public MachineRecipeSerializer twoInputs() {
		energyX = 11;
		progressX = 30;
		batteryX = 8;
		inputSlots = 2;
		return this;
	}

	public MachineRecipeSerializer extraOutput() {
		outputSlots = 2;
		guiWidth = 103;
		return this;
	}

	@Override
	public MachineRecipe fromJson(ResourceLocation id, JsonObject json) {
		MachineRecipe recipe = new MachineRecipe(this, id);
		recipe.realRecipe = true;
		recipe.inputItems = FTBICUtils.listFromJson(json, "inputItems", IngredientWithCount::deserialize);
		recipe.inputFluids = FTBICUtils.listFromJson(json, "inputFluids", FTBICUtils::fluidFromJson);
		recipe.outputItems = FTBICUtils.listFromJson(json, "outputItems", StackWithChance::new);
		recipe.outputFluids = FTBICUtils.listFromJson(json, "outputFluids", FTBICUtils::fluidFromJson);
		recipe.processingTime = json.has("processingTime") ? json.get("processingTime").getAsDouble() : 1D;
		recipe.hideFromJEI = json.has("hideFromJEI") && json.get("hideFromJEI").getAsBoolean();
		return recipe;
	}

	@Nullable
	@Override
	public MachineRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
		MachineRecipe recipe = new MachineRecipe(this, id);
		recipe.realRecipe = true;
		recipe.inputItems = FTBICUtils.listFromNet(buf, IngredientWithCount::new);
		recipe.inputFluids = FTBICUtils.listFromNet(buf, FluidStack::readFromPacket);
		recipe.outputItems = FTBICUtils.listFromNet(buf, StackWithChance::new);
		recipe.outputFluids = FTBICUtils.listFromNet(buf, FluidStack::readFromPacket);
		recipe.processingTime = buf.readDouble();
		recipe.hideFromJEI = buf.readBoolean();
		return recipe;
	}

	@Override
	public void toNetwork(FriendlyByteBuf buf, MachineRecipe recipe) {
		FTBICUtils.listToNet(recipe.inputItems, buf, IngredientWithCount::write);
		FTBICUtils.listToNet(recipe.inputFluids, buf, FluidStack::writeToPacket);
		FTBICUtils.listToNet(recipe.outputItems, buf, StackWithChance::write);
		FTBICUtils.listToNet(recipe.outputFluids, buf, FluidStack::writeToPacket);
		buf.writeDouble(recipe.processingTime);
		buf.writeBoolean(recipe.hideFromJEI);
	}
}
