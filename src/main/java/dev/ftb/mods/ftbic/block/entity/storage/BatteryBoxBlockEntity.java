package dev.ftb.mods.ftbic.block.entity.storage;

import dev.ftb.mods.ftbic.block.entity.generator.GeneratorBlockEntity;
import dev.ftb.mods.ftbic.block.entity.machine.BatteryInventory;
import dev.ftb.mods.ftbic.screen.BatteryBoxMenu;
import dev.ftb.mods.ftbic.util.EnergyItemHandler;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;

public class BatteryBoxBlockEntity extends GeneratorBlockEntity {
	public final BatteryInventory dischargeBatteryInventory;

	public BatteryBoxBlockEntity(BlockEntityType<?> type) {
		super(type, 0, 0);
		dischargeBatteryInventory = new BatteryInventory(this, false);
	}

	@Override
	public void writeData(CompoundTag tag) {
		super.writeData(tag);

		if (!dischargeBatteryInventory.getStackInSlot(0).isEmpty()) {
			tag.put("DischargeBattery", dischargeBatteryInventory.getStackInSlot(0).serializeNBT());
		}
	}

	@Override
	public void readData(CompoundTag tag) {
		super.readData(tag);

		if (tag.contains("DischargeBattery")) {
			dischargeBatteryInventory.loadItem(ItemStack.of(tag.getCompound("DischargeBattery")));
		} else {
			dischargeBatteryInventory.loadItem(ItemStack.EMPTY);
		}
	}

	@Override
	public boolean isValidEnergyOutputSide(Direction direction) {
		return direction == getBlockState().getValue(BlockStateProperties.FACING);
	}

	@Override
	public void handleGeneration() {
		if (!isBurnt() && !level.isClientSide() && energy < energyCapacity) {
			ItemStack battery = dischargeBatteryInventory.getStackInSlot(0);

			if (!battery.isEmpty() && battery.getItem() instanceof EnergyItemHandler) {
				EnergyItemHandler item = (EnergyItemHandler) battery.getItem();
				double e = item.extractEnergy(battery, Math.min(energyCapacity - energy, inputEnergyTier.transferRate), false);

				if (e > 0) {
					energyAdded += e;

					if (battery.isEmpty()) {
						dischargeBatteryInventory.setStackInSlot(0, ItemStack.EMPTY);
					}

					setChanged();
				}
			}
		}
	}

	@Override
	public InteractionResult rightClick(Player player, InteractionHand hand, BlockHitResult hit) {
		if (!level.isClientSide()) {
			openMenu((ServerPlayer) player, (id, inventory) -> new BatteryBoxMenu(id, inventory, this, this));
		}

		return InteractionResult.SUCCESS;
	}
}
