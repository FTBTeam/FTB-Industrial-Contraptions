package dev.ftb.mods.ftbic.block.entity.generator;

import dev.ftb.mods.ftbic.block.CableBlock;
import dev.ftb.mods.ftbic.block.ElectricBlockState;
import dev.ftb.mods.ftbic.block.entity.CachedEnergyStorage;
import dev.ftb.mods.ftbic.block.entity.ElectricBlockEntity;
import dev.ftb.mods.ftbic.block.entity.machine.BatteryInventory;
import dev.ftb.mods.ftbic.util.EnergyHandler;
import dev.ftb.mods.ftbic.util.EnergyItemHandler;
import dev.ftb.mods.ftbic.util.EnergyTier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

public class GeneratorBlockEntity extends ElectricBlockEntity {
	private long currentElectricNetwork = -1L;
	private CachedEnergyStorage[] connectedEnergyBlocks;
	public final BatteryInventory chargeBatteryInventory;

	public GeneratorBlockEntity(BlockEntityType<?> type, int inItems, int outItems) {
		super(type, inItems, outItems);
		chargeBatteryInventory = new BatteryInventory(this, true);
	}

	@Override
	public void initProperties() {
		super.initProperties();
		outputEnergyTier = EnergyTier.LV;
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

	public void handleEnergyOutput() {
		if (level.isClientSide()) {
			return;
		}

		if (energy > 0D) {
			ItemStack battery = chargeBatteryInventory.getStackInSlot(0);

			if (!battery.isEmpty() && battery.getItem() instanceof EnergyItemHandler) {
				EnergyItemHandler item = (EnergyItemHandler) battery.getItem();
				double e = item.insertEnergy(battery, energy, false);

				if (e > 0) {
					energy -= e;
					changeState = ElectricBlockState.ON;
					setChanged();
				}
			}
		}

		double tenergy = Math.min(energy, outputEnergyTier.transferRate);

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
				}

				double a = storage.energyHandler.insertEnergy(e, false);

				if (a > 0D) {
					energy -= a;
					changeState = ElectricBlockState.ON;
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
		changeState = ElectricBlockState.OFF;
		handleEnergyInput();

		if (!level.isClientSide()) {
			handleGeneration();
		}

		handleEnergyOutput();
		handleChanges();
	}

	public boolean isValidLookupSide(Direction direction) {
		return true;
	}

	public boolean isValidConnectedBlock(EnergyHandler storage) {
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
					EnergyHandler handler = entity instanceof EnergyHandler ? (EnergyHandler) entity : null; // entity.getCapability(CapabilityEnergy.ENERGY, null).orElse(null);

					if (handler != null && handler != this && handler.getInputEnergyTier() != null && isValidConnectedBlock(handler)) {
						CachedEnergyStorage s = new CachedEnergyStorage();
						s.distance = 1D;
						s.blockEntity = entity;
						s.energyHandler = handler;
						set.add(s);
					}
				}
			}

			connectedEnergyBlocks = set.toArray(CachedEnergyStorage.EMPTY);
			currentElectricNetwork = currentId;
		}

		return connectedEnergyBlocks;
	}

	/*
	@Override
	public InteractionResult rightClick(Player player, InteractionHand hand, BlockHitResult hit) {
		if (!level.isClientSide()) {
			player.displayClientMessage(new TextComponent("Energy: ").append(FTBICUtils.formatEnergy(energy, energyCapacity)), false);

			if (player.isCrouching()) {
				player.displayClientMessage(new TextComponent("Connected machines: " + Arrays.toString(getConnectedEnergyBlocks())), false);
			}
		}

		return InteractionResult.SUCCESS;
	}
	*/
}
