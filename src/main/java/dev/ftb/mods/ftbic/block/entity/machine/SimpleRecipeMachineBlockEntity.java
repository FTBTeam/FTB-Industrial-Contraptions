package dev.ftb.mods.ftbic.block.entity.machine;

import dev.ftb.mods.ftbic.recipe.MachineRecipeResults;
import dev.ftb.mods.ftbic.recipe.RecipeCache;
import dev.ftb.mods.ftbic.util.MachineProcessingResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.NotNull;

public abstract class SimpleRecipeMachineBlockEntity extends MachineBlockEntity {
	public int energyUse;

	public SimpleRecipeMachineBlockEntity(BlockEntityType<?> type) {
		super(type, 1, 1);
		energyCapacity = 8000;
		energyUse = 20;
	}

	public abstract MachineRecipeResults getRecipes(RecipeCache cache);

	public MachineProcessingResult getResult(Item item) {
		RecipeCache cache = getRecipeCache();
		return cache != null ? getRecipes(cache).getResult(level, item) : MachineProcessingResult.NONE;
	}

	@Override
	public boolean isItemValid(int slot, @NotNull ItemStack stack) {
		return slot == 0 && getResult(stack.getItem()).exists();
	}
}