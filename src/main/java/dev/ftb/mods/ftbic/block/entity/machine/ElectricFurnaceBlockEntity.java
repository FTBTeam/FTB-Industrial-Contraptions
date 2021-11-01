package dev.ftb.mods.ftbic.block.entity.machine;

import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.recipe.RecipeCache;
import dev.ftb.mods.ftbic.util.MachineProcessingResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ElectricFurnaceBlockEntity extends MachineBlockEntity {
	public ElectricFurnaceBlockEntity() {
		super(FTBICElectricBlocks.ELECTRIC_FURNACE.blockEntity.get(), 1, 1);
		energyCapacity = 4160;
		// use 30
	}

	public MachineProcessingResult getResult(Item item) {
		RecipeCache cache = getRecipeCache();
		return cache != null ? cache.getFurnaceResult(level, item) : MachineProcessingResult.NONE;
	}

	@Override
	public boolean isItemValid(int slot, @NotNull ItemStack stack) {
		return slot == 0 && getResult(stack.getItem()).exists();
	}
}