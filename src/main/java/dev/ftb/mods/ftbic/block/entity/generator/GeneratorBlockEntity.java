package dev.ftb.mods.ftbic.block.entity.generator;

import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.block.BurntCableBlock;
import dev.ftb.mods.ftbic.block.CableBlock;
import dev.ftb.mods.ftbic.block.ElectricBlockInstance;
import dev.ftb.mods.ftbic.block.entity.ElectricBlockEntity;
import dev.ftb.mods.ftbic.block.entity.machine.BatteryInventory;
import dev.ftb.mods.ftbic.block.NuclearReactorChamberBlock;
import dev.ftb.mods.ftbic.util.CachedEnergyStorage;
import dev.ftb.mods.ftbic.util.CachedEnergyStorageOrigin;
import dev.ftb.mods.ftbic.util.EnergyItemHandler;
import dev.ftb.mods.ftbic.util.FTBICUtils;
import dev.ftb.mods.ftbic.util.ZapEnergyHandler;
import dev.ftb.mods.ftbic.util.ZapFEConversion;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GeneratorBlockEntity extends ElectricBlockEntity {
	public double maxEnergyOutput;
	public double maxEnergyOutputTransfer;

	public final BatteryInventory chargeBatteryInventory;
	private long currentElectricNetwork = -1L;
	private CachedEnergyStorage[] connectedEnergyBlocks;
	private int[] validConsumerIndices;
	private BlockCapabilityCache<EnergyHandler, Direction>[] fePushCaches;
	private final Map<Long, BlockCapabilityCache<EnergyHandler, Direction>> feFindCaches = new HashMap<>();

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

	public void handleGeneration() {
	}

	public void handleEnergyOutput() {
		if (level == null || level.isClientSide()) {
			return;
		}

		pushFEToNeighbours();

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

		double transferable = Math.min(energy, maxEnergyOutputTransfer);
		if (transferable <= 0D) {
			return;
		}

		CachedEnergyStorage[] blocks = getConnectedEnergyBlocks();
		if (blocks.length == 0) {
			return;
		}
		if (validConsumerIndices == null || validConsumerIndices.length < blocks.length) {
			validConsumerIndices = new int[blocks.length];
		}
		int[] valid = validConsumerIndices;
		int validBlocks = 0;
		for (int i = 0; i < blocks.length; i++) {
			CachedEnergyStorage storage = blocks[i];
			if (storage.isInvalid()) {
				electricNetworkUpdated(level, storage.blockEntity.getBlockPos());
			} else if (storage.shouldReceiveEnergy()) {
				valid[validBlocks++] = i;
			}
		}

		if (validBlocks == 0) {
			return;
		}

		double share = transferable / validBlocks;
		boolean changed = false;
		for (int vi = 0; vi < validBlocks; vi++) {
			CachedEnergyStorage storage = blocks[valid[vi]];
			double thisShare = share;
			if (storage.feHandlerCache != null) {
				thisShare = Math.min(thisShare, storage.origin.cableTransferRate);
			} else if (storage.origin.cableTransferRate < share) {
				BlockState cableState = level.getBlockState(storage.origin.cablePos);
				BlockState burntState = cableState.getBlock() instanceof CableBlock cable
						? cable.getBurntState(cableState)
						: BurntCableBlock.getBurntCable(cableState);
				level.setBlock(storage.origin.cablePos, burntState, 3);
				level.levelEvent(1502, storage.origin.cablePos, 0);
				storage.origin.cableBurnt = true;
				continue;
			}

			double accepted = storage.insertZaps(Math.min(thisShare, energy));
			if (accepted > 0D) {
				energy -= accepted;
				active = true;
				changed = true;
			}
			if (energy < share) {
				break;
			}
		}
		if (changed) {
			setChanged();
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

	private void pushFEToNeighbours() {
		boolean canFEOutput = electricBlockInstance.feCapMode == ElectricBlockInstance.FECapMode.EXTRACT_ONLY
				|| (FTBICConfig.ENERGY.FULL_FE_MODE.get() && maxEnergyOutput > 0D);
		if (!canFEOutput) return;
		if (energy <= 0D || maxEnergyOutputTransfer <= 0D) return;
		if (!(level instanceof ServerLevel serverLevel)) return;
		for (Direction dir : FTBICUtils.DIRECTIONS) {
			if (!isValidEnergyOutputSide(dir)) continue;
			BlockPos npos = worldPosition.relative(dir);
			BlockEntity nbe = level.getBlockEntity(npos);
			if (nbe instanceof ZapEnergyHandler) continue;
			EnergyHandler fe = fePushCache(serverLevel, dir).getCapability();
			if (fe == null) continue;
			double zapsAvailable = Math.min(energy, maxEnergyOutputTransfer);
			int feToOffer = ZapFEConversion.zapsToFEFloor(zapsAvailable);
			if (feToOffer <= 0) continue;
			try (Transaction tx = Transaction.openRoot()) {
				int feAccepted = fe.insert(feToOffer, tx);
				if (feAccepted > 0) {
					double zapsConsumed = Math.min(ZapFEConversion.feToZapsCeil(feAccepted), energy);
					energy -= zapsConsumed;
					tx.commit();
					active = true;
					setChanged();
				}
			}
			if (energy <= 0D) return;
		}
	}

	@SuppressWarnings("unchecked")
	private BlockCapabilityCache<EnergyHandler, Direction> fePushCache(ServerLevel serverLevel, Direction dir) {
		if (fePushCaches == null) {
			fePushCaches = new BlockCapabilityCache[FTBICUtils.DIRECTIONS.length];
		}
		BlockCapabilityCache<EnergyHandler, Direction> c = fePushCaches[dir.ordinal()];
		if (c == null) {
			c = BlockCapabilityCache.create(Capabilities.Energy.BLOCK, serverLevel,
					worldPosition.relative(dir), dir.getOpposite());
			fePushCaches[dir.ordinal()] = c;
		}
		return c;
	}

	@Override
	public boolean isValidEnergyInputSide(Direction direction) {
		return false;
	}

	@Override
	public double getMaxOutputEnergy() {
		return maxEnergyOutputTransfer;
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
		LongOpenHashSet traversed = new LongOpenHashSet();
		traversed.add(worldPosition.asLong());
		int maxCableLength = FTBICConfig.ENERGY.MAX_CABLE_LENGTH.get();

		for (Direction direction : FTBICUtils.DIRECTIONS) {
			if (isValidEnergyOutputSide(direction)) {
				CachedEnergyStorageOrigin origin = new CachedEnergyStorageOrigin();
				origin.direction = direction;
				find(traversed, set, origin, 0, maxCableLength, worldPosition, direction);
			}
		}

		connectedEnergyBlocks = set.toArray(CachedEnergyStorage.EMPTY);
		currentElectricNetwork = currentId;
		return connectedEnergyBlocks;
	}

	private void find(LongOpenHashSet traversed, Set<CachedEnergyStorage> set, CachedEnergyStorageOrigin origin,
			int distance, int maxCableLength, BlockPos currentPos, Direction direction) {
		if (level == null || distance > maxCableLength) {
			return;
		}

		BlockPos pos = currentPos.relative(direction);
		if (!traversed.add(pos.asLong())) {
			return;
		}

		BlockState state = level.getBlockState(pos);

		if (state.getBlock() instanceof CableBlock cableBlock) {
			double rate = cableBlock.tier.transferRate();
			if (rate < origin.cableTransferRate) {
				origin.cableTier = cableBlock.tier;
				origin.cableTransferRate = rate;
				origin.cablePos = pos;
			}
			for (Direction dir : FTBICUtils.DIRECTIONS) {
				if (state.getValue(CableBlock.CONNECTION[dir.get3DDataValue()])) {
					find(traversed, set, origin, distance + 1, maxCableLength, pos, dir);
				}
			}
			return;
		}

		if (state.getBlock() instanceof NuclearReactorChamberBlock) {
			for (Direction dir : FTBICUtils.DIRECTIONS) {
				find(traversed, set, origin, distance + 1, maxCableLength, pos, dir);
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

		if (entity instanceof ZapEnergyHandler handler && handler != this) {
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

		if (!(level instanceof ServerLevel serverLevel)) {
			return;
		}
		long key = pos.asLong() ^ ((long) direction.ordinal() << 56);
		BlockCapabilityCache<EnergyHandler, Direction> feCache = feFindCaches.get(key);
		if (feCache == null) {
			feCache = BlockCapabilityCache.create(Capabilities.Energy.BLOCK, serverLevel, pos, direction.getOpposite());
			feFindCaches.put(key, feCache);
		}
		if (feCache.getCapability() == null) {
			return;
		}
		CachedEnergyStorage s = new CachedEnergyStorage();
		s.origin = origin;
		s.distance = distance;
		s.blockEntity = entity;
		s.feHandlerCache = feCache;
		set.add(s);
	}
}
