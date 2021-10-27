package dev.ftb.mods.ftbic.block.entity.generator;

import dev.ftb.mods.ftbic.block.CableBlock;
import dev.ftb.mods.ftbic.block.entity.CachedEnergyStorage;
import dev.ftb.mods.ftbic.block.entity.ElectricBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

public class GeneratorBlockEntity extends ElectricBlockEntity {
	private long currentElectricNetwork = -1L;
	private CachedEnergyStorage[] connectedEnergyBlocks;

	public GeneratorBlockEntity(BlockEntityType<?> type) {
		super(type);
	}

	@Override
	public void tick() {
		if (energy > 0 && !level.isClientSide()) {
			CachedEnergyStorage[] blocks = getConnectedEnergyBlocks();

			if (energy >= blocks.length && blocks.length > 0) {
				int e = energy / blocks.length;

				for (CachedEnergyStorage storage : blocks) {
					if (storage.isInvalid()) {
						electricNetworkUpdated(level, storage.blockEntity.getBlockPos());
						continue;
					}

					int a = storage.energyStorage.receiveEnergy(e, false);

					if (a > 0) {
						energy -= a;
						setChanged();
					}

					if (energy < e) {
						break;
					}
				}
			}
		}

		super.tick();
	}

	public boolean isValidLookupSide(Direction direction) {
		return true;
	}

	public boolean isValidConnectedBlock(IEnergyStorage storage) {
		return true;
	}

	public CachedEnergyStorage[] getConnectedEnergyBlocks() {
		long currentId = getCurrentElectricNetwork(level, getBlockPos());

		if (connectedEnergyBlocks == null || currentElectricNetwork == -1L || currentElectricNetwork != currentId) {
			Set<CachedEnergyStorage> set = new HashSet<>();

			Direction[] directions = Direction.values();

			Set<BlockPos> traversed = new HashSet<>();
			Deque<BlockPos> openSet = new ArrayDeque<>();
			openSet.add(worldPosition);
			traversed.add(worldPosition);

			while (!openSet.isEmpty()) {
				BlockPos ptr = openSet.pop();
				BlockState state = level.getBlockState(ptr);

				if (ptr == worldPosition) {
					for (Direction dir : directions) {
						if (isValidLookupSide(dir)) {
							BlockPos offset = ptr.relative(dir);

							if (traversed.add(offset)) {
								openSet.add(offset);
							}
						}
					}
				} else if (state.getBlock() instanceof CableBlock) {
					for (Direction dir : directions) {
						if (state.getValue(CableBlock.CONNECTION[dir.get3DDataValue()])) {
							BlockPos offset = ptr.relative(dir);

							if (traversed.add(offset)) {
								openSet.add(offset);
							}
						}
					}
				} else if (state.hasTileEntity()) {
					BlockEntity entity = level.getBlockEntity(ptr);
					IEnergyStorage storage = entity instanceof IEnergyStorage ? (IEnergyStorage) entity : entity == null ? null : entity.getCapability(CapabilityEnergy.ENERGY, null).orElse(null);

					if (storage != null && storage != this && storage.canReceive() && isValidConnectedBlock(storage)) {
						CachedEnergyStorage s = new CachedEnergyStorage();
						s.distance = 1D;
						s.blockEntity = entity;
						s.energyStorage = storage;
						set.add(s);
					}
				}
			}

			connectedEnergyBlocks = set.toArray(CachedEnergyStorage.EMPTY);
			currentElectricNetwork = currentId;
		}

		return connectedEnergyBlocks;
	}

	@Override
	public boolean canExtract() {
		return true;
	}
}
