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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.HashSet;
import java.util.Set;

/**
 * Base for generator-style BEs: produces energy and pushes it onto the cable network each tick.
 *
 * Phase 2c/2d port:
 *  - Real electric-network discovery via {@link CachedEnergyStorage} walking cables
 *  - Distribution across connected consumers with cable-burn checks
 *  - FE bridging via {@link ForgeEnergyHandler}
 *  - Charge-slot battery support via {@link BatteryInventory}
 */
public class GeneratorBlockEntity extends ElectricBlockEntity {
	public double maxEnergyOutput;
	public double maxEnergyOutputTransfer;

	public final BatteryInventory chargeBatteryInventory;
	private long currentElectricNetwork = -1L;
	private CachedEnergyStorage[] connectedEnergyBlocks;

	public GeneratorBlockEntity(ElectricBlockInstance type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		chargeBatteryInventory = new BatteryInventory(this, true);
	}

	@Override
	public void initProperties() {
		super.initProperties();
		maxEnergyOutput = electricBlockInstance.maxEnergyOutput.get();
		maxEnergyOutputTransfer = FTBICConfig.ENERGY.LV_TRANSFER_RATE.get();
	}

	@Override
	protected void saveAdditional(ValueOutput output) {
		super.saveAdditional(output);
		ItemStack chargeStack = chargeBatteryInventory.getStackInSlot(0);
		if (!chargeStack.isEmpty()) {
			output.store("ChargeBattery", ItemStack.CODEC, chargeStack);
		}
	}

	@Override
	protected void loadAdditional(ValueInput input) {
		super.loadAdditional(input);
		chargeBatteryInventory.loadItem(input.read("ChargeBattery", ItemStack.CODEC).orElse(ItemStack.EMPTY));
	}

	@Override
	public void onBroken(Level level, BlockPos pos) {
		super.onBroken(level, pos);
		Block.popResource(level, pos, chargeBatteryInventory.getStackInSlot(0));
	}

	/** Override to produce energy. */
	public void handleGeneration() {
	}

	public void handleEnergyOutput() {
		if (level == null || level.isClientSide()) {
			return;
		}

		// 0. Push to adjacent foreign FE consumers (cables/machines) directly via Capabilities.Energy.BLOCK.
		pushFEToNeighbours();

		// 1. Charge slot: drain this BE's buffer into an inserted battery.
		if (energy > 0D) {
			ItemStack battery = chargeBatteryInventory.getStackInSlot(0);
			if (!battery.isEmpty() && battery.getItem() instanceof EnergyItemHandler item) {
				double transfer = item.isCreativeEnergyItem()
						? Double.POSITIVE_INFINITY
						: maxEnergyOutputTransfer * FTBICConfig.MACHINES.ITEM_TRANSFER_EFFICIENCY.get();
				double accepted = item.insertEnergy(battery, Math.min(energy, transfer), false);
				if (accepted > 0) {
					energy -= accepted;
					active = true;
					setChanged();
				}
			}
		}

		// 2. Cable network: distribute to connected consumers.
		double transferable = Math.min(energy, maxEnergyOutputTransfer);
		if (transferable <= 0D) {
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

		if (validBlocks == 0) {
			return;
		}

		double share = transferable / validBlocks;
		for (CachedEnergyStorage storage : blocks) {
			if (storage.isInvalid() || !storage.shouldReceiveEnergy()) {
				continue;
			}
			if (storage.origin.cableTier != null && storage.origin.cableTier.transferRate() < share) {
				// Overload — burn the weakest cable.
				level.setBlock(storage.origin.cablePos,
						BurntCableBlock.getBurntCable(level.getBlockState(storage.origin.cablePos)),
						3);
				level.levelEvent(1502, storage.origin.cablePos, 0);
				storage.origin.cableBurnt = true;
				continue;
			}

			double accepted = storage.energyHandler.insertEnergy(Math.min(share, energy), false);
			if (accepted > 0D) {
				energy -= accepted;
				active = true;
				setChanged();
			}
			if (energy < share) {
				break;
			}
		}
	}

	@Override
	public void tick() {
		if (level != null && !level.isClientSide()) {
			handleGeneration();
		}
		handleEnergyOutput();
		super.tick();
	}

	public boolean isValidEnergyOutputSide(Direction direction) {
		return true;
	}

	/**
	 * Push surplus zaps to adjacent foreign FE consumers (other-mod cables/machines) by querying
	 * {@code Capabilities.Energy.BLOCK} on each neighbour. Skips FTBIC blocks since those are
	 * handled by the in-network distribution loop below.
	 */
	private void pushFEToNeighbours() {
		if (electricBlockInstance.feCapMode != dev.ftb.mods.ftbic.block.ElectricBlockInstance.FECapMode.EXTRACT_ONLY) return;
		if (energy <= 0D || maxEnergyOutputTransfer <= 0D) return;
		double rate = dev.ftb.mods.ftbic.FTBICConfig.ENERGY.ZAP_TO_FE_CONVERSION_RATE.get();
		for (Direction dir : dev.ftb.mods.ftbic.util.FTBICUtils.DIRECTIONS) {
			if (!isValidEnergyOutputSide(dir)) continue;
			BlockPos npos = worldPosition.relative(dir);
			BlockEntity nbe = level.getBlockEntity(npos);
			if (nbe instanceof EnergyHandler) continue; // FTBIC neighbour — handled by network distribution
			net.neoforged.neoforge.transfer.energy.EnergyHandler fe =
					level.getCapability(net.neoforged.neoforge.capabilities.Capabilities.Energy.BLOCK, npos, dir.getOpposite());
			if (fe == null) continue;
			double zapsAvailable = Math.min(energy, maxEnergyOutputTransfer);
			int feToOffer = (int) Math.min(Integer.MAX_VALUE, Math.floor(zapsAvailable * rate));
			if (feToOffer <= 0) continue;
			try (var tx = net.neoforged.neoforge.transfer.transaction.Transaction.openRoot()) {
				int feAccepted = fe.insert(feToOffer, tx);
				if (feAccepted > 0) {
					double zapsConsumed = feAccepted / rate;
					if (zapsConsumed > energy) zapsConsumed = energy;
					energy -= zapsConsumed;
					tx.commit();
					active = true;
					setChanged();
				}
				// no commit = rollback on close
			}
			if (energy <= 0D) return;
		}
	}

	@Override
	public boolean isValidEnergyInputSide(Direction direction) {
		return false;
	}

	public CachedEnergyStorage[] getConnectedEnergyBlocks() {
		if (level == null || level.isClientSide()) {
			return CachedEnergyStorage.EMPTY;
		}

		long currentId = getCurrentElectricNetwork(level, worldPosition);
		if (connectedEnergyBlocks != null && currentElectricNetwork == currentId) {
			return connectedEnergyBlocks;
		}

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
		return connectedEnergyBlocks;
	}

	private void find(Set<BlockPos> traversed, Set<CachedEnergyStorage> set, CachedEnergyStorageOrigin origin,
			int distance, BlockPos currentPos, Direction direction) {
		if (level == null || distance > FTBICConfig.ENERGY.MAX_CABLE_LENGTH.get()) {
			return;
		}

		BlockPos pos = currentPos.relative(direction);
		if (!traversed.add(pos)) {
			return;
		}

		BlockState state = level.getBlockState(pos);

		if (state.getBlock() instanceof CableBlock cableBlock) {
			if (origin.cableTier == null || cableBlock.tier.transferRate() < origin.cableTier.transferRate()) {
				origin.cableTier = cableBlock.tier;
				origin.cablePos = pos;
			}
			for (Direction dir : FTBICUtils.DIRECTIONS) {
				if (state.getValue(CableBlock.CONNECTION[dir.get3DDataValue()])) {
					find(traversed, set, origin, distance + 1, pos, dir);
				}
			}
			return;
		}

		if (!state.hasBlockEntity()) {
			return;
		}
		BlockEntity entity = level.getBlockEntity(pos);
		if (entity == null) {
			return;
		}

		// Prefer FTBIC-native EnergyHandler.
		if (entity instanceof EnergyHandler handler && handler != this) {
			if (handler.getMaxInputEnergy() > 0D && !handler.isBurnt() && handler.isValidEnergyInputSide(direction.getOpposite())) {
				CachedEnergyStorage s = new CachedEnergyStorage();
				s.origin = origin;
				s.distance = distance;
				s.blockEntity = entity;
				s.energyHandler = handler;
				set.add(s);
			}
			return;
		}

		// Foreign energy interop (non-FTBIC neighbours) is plumbed via the NeoForge transfer API
		// elsewhere — see ElectricBlockEnergyHandler + CapabilityRegistrar binding
		// Capabilities.Energy.BLOCK so other mods see our machines as zap/FE-converting sinks.
	}
}
