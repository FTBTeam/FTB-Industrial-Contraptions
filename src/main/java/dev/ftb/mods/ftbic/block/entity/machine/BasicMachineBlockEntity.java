package dev.ftb.mods.ftbic.block.entity.machine;

import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.block.ElectricBlockInstance;
import dev.ftb.mods.ftbic.block.entity.ElectricBlockEntity;
import dev.ftb.mods.ftbic.item.FTBICItems;
import dev.ftb.mods.ftbic.util.EnergyItemHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public abstract class BasicMachineBlockEntity extends ElectricBlockEntity {
	public final UpgradeInventory upgradeInventory;
	public final BatteryInventory batteryInventory;

	public double energyUse;
	public double progressSpeed;

	public BasicMachineBlockEntity(ElectricBlockInstance type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		upgradeInventory = new UpgradeInventory(this, 4, FTBICConfig.MACHINES.UPGRADE_LIMIT_PER_SLOT.get());
		batteryInventory = new BatteryInventory(this, false);
	}

	@Override
	public void writeData(CompoundTag tag) {
		super.writeData(tag);

		tag.put("Upgrades", upgradeInventory.serializeNBT().getList("Items", Tag.TAG_COMPOUND));

		if (!batteryInventory.getStackInSlot(0).isEmpty()) {
			tag.put("Battery", batteryInventory.getStackInSlot(0).serializeNBT());
		}
	}

	@Override
	public void readData(CompoundTag tag) {
		super.readData(tag);

		CompoundTag tag1 = new CompoundTag();
		tag1.put("Items", tag.getList("Upgrades", Tag.TAG_COMPOUND));
		upgradeInventory.deserializeNBT(tag1);

		if (tag.contains("Battery")) {
			batteryInventory.loadItem(ItemStack.of(tag.getCompound("Battery")));
		} else {
			batteryInventory.loadItem(ItemStack.EMPTY);
		}
	}

	@Override
	public void tick() {
		if (!isBurnt() && !level.isClientSide() && energy < energyCapacity) {
			ItemStack battery = batteryInventory.getStackInSlot(0);

			if (!battery.isEmpty() && battery.getItem() instanceof EnergyItemHandler item) {
				double transfer = item.isCreativeEnergyItem() ? Double.POSITIVE_INFINITY : maxInputEnergy * FTBICConfig.MACHINES.ITEM_TRANSFER_EFFICIENCY.get();
				double e = item.extractEnergy(battery, Math.min(energyCapacity - energy, transfer), false);

				if (e > 0) {
					energy += e;

					if (battery.isEmpty()) {
						batteryInventory.setStackInSlot(0, ItemStack.EMPTY);
					}

					setChanged();
				}
			}
		}

		handleProcessing();
		handleChanges();
	}

	@Override
	public double getTotalPossibleEnergyCapacity() {
		return super.getTotalPossibleEnergyCapacity() + upgradeInventory.getSlots() * Math.min(FTBICConfig.MACHINES.UPGRADE_LIMIT_PER_SLOT.get(), FTBICItems.ENERGY_STORAGE_UPGRADE.get().getMaxStackSize()) * FTBICConfig.MACHINES.STORAGE_UPGRADE.get();
	}

	public void handleProcessing() {
	}

	@Override
	public void onBroken(Level level, BlockPos pos) {
		super.onBroken(level, pos);

		for (int i = 0; i < upgradeInventory.getSlots(); i++) {
			Block.popResource(level, pos, upgradeInventory.getStackInSlot(i));
		}

		Block.popResource(level, pos, batteryInventory.getStackInSlot(0));
	}

	@Override
	public void initProperties() {
		super.initProperties();
		energyUse = electricBlockInstance.energyUsage.get();
		progressSpeed = 1D;
		autoEject = false;
	}

	@Override
	public void upgradesChanged() {
		super.upgradesChanged();

		int overclockers = upgradeInventory.countUpgrades(FTBICItems.OVERCLOCKER_UPGRADE.get());

		for (int i = 0; i < overclockers; i++) {
			energyUse *= FTBICConfig.MACHINES.OVERCLOCKER_ENERGY_USE.get();
			progressSpeed *= FTBICConfig.MACHINES.OVERCLOCKER_SPEED.get();
		}

		int transformers = upgradeInventory.countUpgrades(FTBICItems.TRANSFORMER_UPGRADE.get());

		while (transformers > 0) {
			transformers--;
			maxInputEnergy *= 4D;
		}

		if (maxInputEnergy > FTBICConfig.ENERGY.IV_TRANSFER_RATE.get()) {
			maxInputEnergy = FTBICConfig.ENERGY.IV_TRANSFER_RATE.get();
		}

		energyCapacity += upgradeInventory.countUpgrades(FTBICItems.ENERGY_STORAGE_UPGRADE.get()) * FTBICConfig.MACHINES.STORAGE_UPGRADE.get();
		if (energy > energyCapacity) {
			energy = energyCapacity;
		}

		autoEject = upgradeInventory.countUpgrades(FTBICItems.EJECTOR_UPGRADE.get()) > 0;
	}
}
