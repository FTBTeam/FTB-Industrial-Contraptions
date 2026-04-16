package dev.ftb.mods.ftbic.block.entity.machine;

import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.block.ElectricBlockInstance;
import dev.ftb.mods.ftbic.block.entity.ElectricBlockEntity;
import dev.ftb.mods.ftbic.item.FTBICItems;
import dev.ftb.mods.ftbic.util.BatterySlotHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class BasicMachineBlockEntity extends ElectricBlockEntity {
	public final UpgradeInventory upgradeInventory;
	public final BatteryInventory batteryInventory;

	public double energyUse;
	public double progressSpeed;
	protected double itemTransferEfficiency;

	public BasicMachineBlockEntity(ElectricBlockInstance type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		upgradeInventory = new UpgradeInventory(this, 4, FTBICConfig.MACHINES.UPGRADE_LIMIT_PER_SLOT.get());
		batteryInventory = new BatteryInventory(this, false);
	}

	@Override
	protected void saveAdditional(ValueOutput output) {
		super.saveAdditional(output);
		upgradeInventory.serialize(output.child("Upgrades"));
		ItemStack battery = batteryInventory.getStackInSlot(0);
		if (!battery.isEmpty()) {
			output.store("Battery", ItemStack.CODEC, battery);
		}
	}

	@Override
	protected void loadAdditional(ValueInput input) {
		super.loadAdditional(input);
		input.child("Upgrades").ifPresent(upgradeInventory::deserialize);
		batteryInventory.loadItem(input.read("Battery", ItemStack.CODEC).orElse(ItemStack.EMPTY));
	}

	@Override
	public void tick() {
		super.tick();
		if (level == null || level.isClientSide()) {
			return;
		}

		// Drain battery slot into our energy buffer.
		if (!isBurnt()) {
			ItemStack battery = batteryInventory.getStackInSlot(0);
			double drained = BatterySlotHelper.drainBatteryToBuffer(this, battery, maxInputEnergy, itemTransferEfficiency);
			if (drained > 0D && battery.isEmpty()) {
				batteryInventory.setStackInSlot(0, ItemStack.EMPTY);
			}
		}

		if (autoEject) {
			ejectOutputs();
		}
	}

	private void ejectOutputs() {
		if (!(level instanceof net.minecraft.server.level.ServerLevel serverLevel) || outputItems.length == 0) return;

		for (int i = 0; i < outputItems.length; i++) {
			if (outputItems[i].isEmpty()) continue;

			for (net.minecraft.core.Direction dir : net.minecraft.core.Direction.values()) {
				if (outputItems[i].isEmpty()) break;
				net.minecraft.core.BlockPos neighbor = worldPosition.relative(dir);

				var handler = serverLevel.getCapability(
						net.neoforged.neoforge.capabilities.Capabilities.Item.BLOCK,
						neighbor, dir.getOpposite());
				if (handler == null) continue;

				ItemStack stack = outputItems[i];
				var resource = net.neoforged.neoforge.transfer.item.ItemResource.of(stack);
				int inserted;
				try (var txn = net.neoforged.neoforge.transfer.transaction.Transaction.openRoot()) {
					inserted = handler.insert(resource, stack.getCount(), txn);
					if (inserted > 0) txn.commit();
				}
				if (inserted > 0) {
					stack.shrink(inserted);
					if (stack.getCount() <= 0) outputItems[i] = ItemStack.EMPTY;
					setChanged();
				}
			}
		}
	}

	public double getTotalPossibleEnergyCapacity() {
		return electricBlockInstance.energyCapacity.get()
				+ upgradeInventory.getSlots()
				* FTBICConfig.MACHINES.UPGRADE_LIMIT_PER_SLOT.get()
				* FTBICConfig.MACHINES.STORAGE_UPGRADE.get();
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
		itemTransferEfficiency = FTBICConfig.MACHINES.ITEM_TRANSFER_EFFICIENCY.get();
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
		double ivCap = FTBICConfig.ENERGY.IV_TRANSFER_RATE.get();
		if (maxInputEnergy > ivCap) {
			maxInputEnergy = ivCap;
		}

		energyCapacity += upgradeInventory.countUpgrades(FTBICItems.ENERGY_STORAGE_UPGRADE.get())
				* FTBICConfig.MACHINES.STORAGE_UPGRADE.get();
		if (energy > energyCapacity) {
			energy = energyCapacity;
		}

		autoEject = upgradeInventory.countUpgrades(FTBICItems.EJECTOR_UPGRADE.get()) > 0;
	}
}
