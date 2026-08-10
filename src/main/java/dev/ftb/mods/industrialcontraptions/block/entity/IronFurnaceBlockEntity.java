package dev.ftb.mods.industrialcontraptions.block.entity;

import dev.ftb.mods.industrialcontraptions.ICConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import dev.ftb.mods.industrialcontraptions.screen.IronFurnaceMenu;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.FuelValues;
import net.minecraft.world.level.block.state.BlockState;

public class IronFurnaceBlockEntity extends AbstractFurnaceBlockEntity {
	public IronFurnaceBlockEntity(BlockPos pos, BlockState state) {
		super(ICBlockEntities.IRON_FURNACE.get(), pos, state, RecipeType.SMELTING);
	}

	@Override
	protected Component getDefaultName() {
		return Component.translatable("block.ic.iron_furnace");
	}

	@Override
	protected AbstractContainerMenu createMenu(int id, Inventory inv) {
		return new IronFurnaceMenu(id, inv, this, this.dataAccess);
	}

	@Override
	protected int getBurnDuration(FuelValues fuelValues, ItemStack stack) {
		int base = super.getBurnDuration(fuelValues, stack);
		return Math.round(base * (float) ICConfig.MACHINES.IRON_FURNACE_ITEMS_PER_COAL.get() / 8F);
	}
}
