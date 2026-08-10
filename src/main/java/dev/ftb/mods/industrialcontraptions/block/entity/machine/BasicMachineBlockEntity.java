package dev.ftb.mods.industrialcontraptions.block.entity.machine;

import dev.ftb.mods.industrialcontraptions.ICConfig;
import dev.ftb.mods.industrialcontraptions.block.ElectricBlockInstance;
import dev.ftb.mods.industrialcontraptions.block.entity.ElectricBlockEntity;
import dev.ftb.mods.industrialcontraptions.item.ICItems;
import dev.ftb.mods.industrialcontraptions.util.BatterySlotHelper;
import dev.ftb.mods.industrialcontraptions.util.ICUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

public class BasicMachineBlockEntity extends ElectricBlockEntity {
	public final UpgradeInventory upgradeInventory;
	public final BatteryInventory batteryInventory;

	public double energyUse;
	public double progressSpeed;
	protected double itemTransferEfficiency;
	private BlockCapabilityCache<ResourceHandler<ItemResource>, Direction>[] itemEjectCaches;

	public BasicMachineBlockEntity(ElectricBlockInstance type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		upgradeInventory = new UpgradeInventory(this, 4, ICConfig.MACHINES.UPGRADE_LIMIT_PER_SLOT.get());
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
		initProperties();
		upgradesChanged();
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
		if (!(level instanceof ServerLevel serverLevel) || outputItems.length == 0) return;

		for (int i = 0; i < outputItems.length; i++) {
			if (outputItems[i].isEmpty()) continue;

			for (Direction dir : ICUtils.DIRECTIONS) {
				if (outputItems[i].isEmpty()) break;
				ResourceHandler<ItemResource> handler = itemEjectCache(serverLevel, dir).getCapability();
				if (handler == null) continue;

				ItemStack stack = outputItems[i];
				ItemResource resource = ItemResource.of(stack);
				int inserted;
				try (Transaction txn = Transaction.openRoot()) {
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

	@SuppressWarnings("unchecked")
	private BlockCapabilityCache<ResourceHandler<ItemResource>, Direction> itemEjectCache(ServerLevel serverLevel, Direction dir) {
		if (itemEjectCaches == null) {
			itemEjectCaches = new BlockCapabilityCache[ICUtils.DIRECTIONS.length];
		}
		BlockCapabilityCache<ResourceHandler<ItemResource>, Direction> c = itemEjectCaches[dir.ordinal()];
		if (c == null) {
			c = BlockCapabilityCache.create(Capabilities.Item.BLOCK, serverLevel,
					worldPosition.relative(dir), dir.getOpposite());
			itemEjectCaches[dir.ordinal()] = c;
		}
		return c;
	}

	public double getTotalPossibleEnergyCapacity() {
		return electricBlockInstance.energyCapacity.get()
				+ upgradeInventory.getSlots()
				* ICConfig.MACHINES.UPGRADE_LIMIT_PER_SLOT.get()
				* ICConfig.MACHINES.STORAGE_UPGRADE.get();
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
		itemTransferEfficiency = ICConfig.MACHINES.ITEM_TRANSFER_EFFICIENCY.get();
	}

	@Override
	public void upgradesChanged() {
		super.upgradesChanged();

		int overclockers = upgradeInventory.countUpgrades(ICItems.OVERCLOCKER_UPGRADE.get());
		if (overclockers > 0) {
			energyUse *= Math.pow(ICConfig.MACHINES.OVERCLOCKER_ENERGY_USE.get(), overclockers);
			progressSpeed *= Math.pow(ICConfig.MACHINES.OVERCLOCKER_SPEED.get(), overclockers);
		}

		int transformers = upgradeInventory.countUpgrades(ICItems.TRANSFORMER_UPGRADE.get());
		if (transformers > 0) {
			maxInputEnergy *= Math.pow(4D, transformers);
		}
		double ivCap = ICConfig.ENERGY.IV_TRANSFER_RATE.get();
		if (maxInputEnergy > ivCap) {
			maxInputEnergy = ivCap;
		}

		energyCapacity += upgradeInventory.countUpgrades(ICItems.ENERGY_STORAGE_UPGRADE.get())
				* ICConfig.MACHINES.STORAGE_UPGRADE.get();
		if (energy > energyCapacity) {
			energy = energyCapacity;
		}

		autoEject = upgradeInventory.countUpgrades(ICItems.EJECTOR_UPGRADE.get()) > 0;

		energyUse = sanitize(energyUse, 1D);
		progressSpeed = sanitize(progressSpeed, 1D);
		maxInputEnergy = sanitize(maxInputEnergy, electricBlockInstance.maxEnergyInput.get());
		energyCapacity = sanitize(energyCapacity, electricBlockInstance.energyCapacity.get());
	}

	private static double sanitize(double value, double fallback) {
		return (!Double.isFinite(value) || value < 0D) ? fallback : value;
	}
}
