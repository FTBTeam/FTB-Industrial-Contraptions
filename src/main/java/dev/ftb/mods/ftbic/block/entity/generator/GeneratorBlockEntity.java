package dev.ftb.mods.ftbic.block.entity.generator;

import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.block.BurntCableBlock;
import dev.ftb.mods.ftbic.block.CableBlock;
import dev.ftb.mods.ftbic.block.ElectricBlockInstance;
import dev.ftb.mods.ftbic.block.entity.ElectricBlockEntity;
import dev.ftb.mods.ftbic.block.entity.machine.BatteryInventory;
import dev.ftb.mods.ftbic.util.CachedEnergyStorage;
import dev.ftb.mods.ftbic.util.CachedEnergyStorageOrigin;
import dev.ftb.mods.ftbic.util.EnergyHandler;
import dev.ftb.mods.ftbic.util.EnergyItemHandler;
import dev.ftb.mods.ftbic.util.FTBICUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashSet;
import java.util.Set;

public class GeneratorBlockEntity extends ElectricBlockEntity {
	private long currentElectricNetwork = -1L;
	private CachedEnergyStorage[] connectedEnergyBlocks;
	public final BatteryInventory chargeBatteryInventory;
	public double maxEnergyOutput;
	public double maxEnergyOutputTransfer;

	public GeneratorBlockEntity(ElectricBlockInstance type) {
		super(type);
		chargeBatteryInventory = new BatteryInventory(this, true);
	}

	@Override
	public void initProperties() {
		super.initProperties();
		maxEnergyOutput = electricBlockInstance.maxEnergyOutput;
		maxEnergyOutputTransfer = FTBICConfig.LV_TRANSFER_RATE;
	}

	@Override
	public void writeData(CompoundTag tag) {
		super.writeData(tag);

		if (!chargeBatteryInventory.getStackInSlot(0).isEmpty()) {
			tag.put("ChargeBattery", chargeBatteryInventory.getStackInSlot(0).serializeNBT());
		}
	}

	@Override
	public void readData(CompoundTag tag) {
		super.readData(tag);

		if (tag.contains("ChargeBattery")) {
			chargeBatteryInventory.loadItem(ItemStack.of(tag.getCompound("ChargeBattery")));
		} else {
			chargeBatteryInventory.loadItem(ItemStack.EMPTY);
		}
	}

	@Override
	public void onBroken(Level level, BlockPos pos) {
		super.onBroken(level, pos);

		Block.popResource(level, pos, chargeBatteryInventory.getStackInSlot(0));
	}

	public void handleEnergyOutput() {
		if (level.isClientSide()) {
			return;
		}

		if (energy > 0D) {
			ItemStack battery = chargeBatteryInventory.getStackInSlot(0);

			if (!battery.isEmpty() && battery.getItem() instanceof EnergyItemHandler) {
				EnergyItemHandler item = (EnergyItemHandler) battery.getItem();
				double e = item.insertEnergy(battery, Math.min(energy, maxEnergyOutputTransfer), false);

				if (e > 0) {
					energy -= e;
					active = true;
					setChanged();
				}
			}
		}

		double tenergy = Math.min(energy, maxEnergyOutputTransfer);

		if (tenergy <= 0D) {
			return;
		}

		CachedEnergyStorage[] blocks = getConnectedEnergyBlocks();

		int validBlocks = 0;

		for (CachedEnergyStorage storage : blocks) {
			if (storage.isInvalid()) {
				electricNetworkUpdated(level, storage.blockEntity.getBlockPos());
			} else if (storage.shouldReceiveEnergy()) {
				validBlocks++;
			}
		}

		if (validBlocks > 0) {
			double e = tenergy / (double) validBlocks;

			for (CachedEnergyStorage storage : blocks) {
				if (storage.isInvalid() || !storage.shouldReceiveEnergy()) {
					continue;
				} else if (storage.origin.cableTier != null && storage.origin.cableTier.transferRate < e) {
					level.setBlock(storage.origin.cablePos, BurntCableBlock.getBurntCable(level.getBlockState(storage.origin.cablePos)), 3);
					level.levelEvent(1502, storage.origin.cablePos, 0);
					storage.origin.cableBurnt = true;
					continue;
				}

				double a = storage.energyHandler.insertEnergy(Math.min(e, energy), false);

				if (a > 0D) {
					energy -= a;
					active = true;
					setChanged();
				}

				if (energy < e) {
					break;
				}
			}
		}
	}

	public void handleGeneration() {
	}

	@Override
	public void tick() {
		if (!level.isClientSide()) {
			handleGeneration();
		}

		handleEnergyOutput();
		handleChanges();
	}

	public boolean isValidEnergyOutputSide(Direction direction) {
		return true;
	}

	@Override
	public boolean isValidEnergyInputSide(Direction direction) {
		return false;
	}

	public boolean isValidConnectedBlock(EnergyHandler storage) {
		return true;
	}

	public CachedEnergyStorage[] getConnectedEnergyBlocks() {
		if (level == null || level.isClientSide()) {
			return CachedEnergyStorage.EMPTY;
		}

		long currentId = getCurrentElectricNetwork(level, getBlockPos());

		if (connectedEnergyBlocks == null || currentElectricNetwork == -1L || currentElectricNetwork != currentId) {
			Set<CachedEnergyStorage> set = new HashSet<>();
			Set<BlockPos> traversed = new HashSet<>();
			traversed.add(worldPosition);

			for (Direction direction : FTBICUtils.DIRECTIONS) {
				if (isValidEnergyOutputSide(direction)) {
					CachedEnergyStorageOrigin origin = new CachedEnergyStorageOrigin();
					origin.direction = direction;
					find(traversed, set, origin, 0, worldPosition, direction);
				}
			}

			connectedEnergyBlocks = set.toArray(CachedEnergyStorage.EMPTY);
			currentElectricNetwork = currentId;
		}

		return connectedEnergyBlocks;
	}

	private void find(Set<BlockPos> traversed, Set<CachedEnergyStorage> set, CachedEnergyStorageOrigin origin, int distance, BlockPos currentPos, Direction direction) {
		if (level == null || distance > FTBICConfig.MAX_CABLE_LENGTH) {
			return;
		}

		BlockPos pos = currentPos.relative(direction);

		if (!traversed.add(pos)) {
			return;
		}

		BlockState state = level.getBlockState(pos);

		if (state.getBlock() instanceof CableBlock) {
			CableBlock cableBlock = (CableBlock) state.getBlock();

			if (origin.cableTier == null || cableBlock.tier.itemTransferRate < origin.cableTier.itemTransferRate) {
				origin.cableTier = cableBlock.tier;
				origin.cablePos = pos;
			}

			for (Direction dir : FTBICUtils.DIRECTIONS) {
				if (state.getValue(CableBlock.CONNECTION[dir.get3DDataValue()])) {
					find(traversed, set, origin, distance + 1, pos, dir);
				}
			}
		} else if (state.hasTileEntity()) {
			BlockEntity entity = level.getBlockEntity(pos);
			EnergyHandler handler = entity instanceof EnergyHandler ? (EnergyHandler) entity : null; // entity.getCapability(CapabilityEnergy.ENERGY, null).orElse(null);

			if (handler != null && handler != this && handler.getMaxInputEnergy() > 0D && !handler.isBurnt() && isValidConnectedBlock(handler) && handler.isValidEnergyInputSide(direction.getOpposite())) {
				CachedEnergyStorage s = new CachedEnergyStorage();
				s.origin = origin;
				s.distance = distance;
				s.blockEntity = entity;
				s.energyHandler = handler;
				set.add(s);
			}
		}
	}
}
