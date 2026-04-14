package dev.ftb.mods.ftbic.block.entity;

import dev.ftb.mods.ftbic.FTBICConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.FurnaceMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.FuelValues;
import net.minecraft.world.level.block.state.BlockState;

public class IronFurnaceBlockEntity extends AbstractFurnaceBlockEntity {
	public IronFurnaceBlockEntity(BlockPos pos, BlockState state) {
		super(FTBICBlockEntities.IRON_FURNACE.get(), pos, state, RecipeType.SMELTING);
	}

	@Override
	protected Component getDefaultName() {
		return Component.translatable("block.ftbic.iron_furnace");
	}

	@Override
	protected AbstractContainerMenu createMenu(int id, Inventory inv) {
		return new FurnaceMenu(id, inv, this, this.dataAccess);
	}

	@Override
	protected int getBurnDuration(FuelValues fuelValues, ItemStack stack) {
		int base = super.getBurnDuration(fuelValues, stack);
		return Math.round(base * (float) FTBICConfig.MACHINES.IRON_FURNACE_ITEMS_PER_COAL.get() / 8F);
	}
}
