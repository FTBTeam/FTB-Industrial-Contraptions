package dev.ftb.mods.ftbic.block.entity.generator;

import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.block.FTBICBlocks;
import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.block.BurntReinforcedCableBlock;
import dev.ftb.mods.ftbic.block.NuclearReactorChamberBlock;
import dev.ftb.mods.ftbic.block.ReinforcedCableBlock;
import dev.ftb.mods.ftbic.item.reactor.NuclearReactor;
import dev.ftb.mods.ftbic.item.reactor.ReactorItem;
import dev.ftb.mods.ftbic.screen.NuclearReactorMenu;
import dev.ftb.mods.ftbic.sound.FTBICSounds;
import dev.ftb.mods.ftbic.util.FTBICUtils;
import dev.ftb.mods.ftbic.util.NuclearFallout;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class NuclearReactorBlockEntity extends GeneratorBlockEntity {
	public final NuclearReactor reactor;
	public int timeUntilNextCycle;
	public int debugSpeed;
	private boolean pendingChamberRecompute = true;

	public NuclearReactorBlockEntity(BlockPos pos, BlockState state) {
		super(FTBICElectricBlocks.NUCLEAR_REACTOR, pos, state);
		reactor = new NuclearReactor(inputItems);
	}

	@Override
	public AbstractContainerMenu createMenu(int id, Inventory inv) {
		return new NuclearReactorMenu(id, inv, this);
	}

	@Override
	public void initProperties() {
		super.initProperties();
		maxEnergyOutputTransfer = FTBICConfig.ENERGY.IV_TRANSFER_RATE.get();
	}

	@Override
	public boolean savePlacer() {
		return true;
	}

	@Override
	public boolean isItemValid(int slot, ItemStack stack) {
		if (slot < 0 || slot >= reactor.inputItems.length) return false;
		int col = slot % NuclearReactor.MAX_COLUMNS;
		int row = slot / NuclearReactor.MAX_COLUMNS;
		if (row >= NuclearReactor.ROWS) return false;
		if (col >= reactor.activeColumns) return false;
		return stack.isEmpty() || stack.getItem() instanceof ReactorItem;
	}

	@Override
	public boolean isSlotExtractable(int slot) {
		if (slot < 0 || slot >= reactor.inputItems.length) return false;
		int col = slot % NuclearReactor.MAX_COLUMNS;
		return col < reactor.activeColumns;
	}

	public int countAttachedChambers() {
		if (level == null) return 0;
		int n = 0;
		for (Direction dir : FTBICUtils.DIRECTIONS) {
			if (level.getBlockState(worldPosition.relative(dir)).getBlock() instanceof NuclearReactorChamberBlock) {
				n++;
			}
		}
		return Math.min(6, n);
	}

	public void recomputeActiveColumns() {
		if (level == null) return;
		int target = 3 + countAttachedChambers();
		if (target == reactor.activeColumns) return;
		int previous = reactor.activeColumns;
		reactor.activeColumns = target;
		if (target < previous) {
			ejectFromColumns(target, previous);
		}
		setChanged();
		level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
	}

	public double computeEnvCooling() {
		if (level == null) return 1.0D;
		double max = FTBICConfig.NUCLEAR.WATER_COOLING_MULTIPLIER.get();
		if (max <= 1.0D) return 1.0D;
		int waterFaces = 0;
		int totalFaces = 0;
		for (Direction dir : FTBICUtils.DIRECTIONS) {
			BlockPos neighborPos = worldPosition.relative(dir);
			if (level.getBlockState(neighborPos).getBlock() instanceof NuclearReactorChamberBlock) {
				for (Direction chamberDir : FTBICUtils.DIRECTIONS) {
					if (chamberDir == dir.getOpposite()) continue;
					BlockPos chamberNeighbor = neighborPos.relative(chamberDir);
					if (isNeutralForCooling(chamberNeighbor)) continue;
					totalFaces++;
					if (isWater(level.getFluidState(chamberNeighbor))) waterFaces++;
				}
			} else {
				if (isNeutralForCooling(neighborPos)) continue;
				totalFaces++;
				if (isWater(level.getFluidState(neighborPos))) waterFaces++;
			}
		}
		if (totalFaces <= 0) return 1.0D;
		return 1.0D + ((double) waterFaces / (double) totalFaces) * (max - 1.0D);
	}

	private static boolean isWater(FluidState fluid) {
		return !fluid.isEmpty() && fluid.is(FluidTags.WATER);
	}

	private boolean isNeutralForCooling(BlockPos pos) {
		var block = level.getBlockState(pos).getBlock();
		return block instanceof ReinforcedCableBlock || block instanceof BurntReinforcedCableBlock;
	}

	private void ejectFromColumns(int fromCol, int toColExclusive) {
		if (level == null || level.isClientSide()) return;
		for (int y = 0; y < NuclearReactor.ROWS; y++) {
			for (int x = fromCol; x < toColExclusive; x++) {
				int idx = NuclearReactor.slotIndex(x, y);
				ItemStack stack = inputItems[idx];
				if (!stack.isEmpty()) {
					Containers.dropItemStack(level,
							worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5,
							stack);
					inputItems[idx] = ItemStack.EMPTY;
				}
			}
		}
	}

	@Override
	public void onPlacedBy(@Nullable LivingEntity entity, ItemStack stack) {
		super.onPlacedBy(entity, stack);
		pendingChamberRecompute = true;
	}

	@Override
	public void neighborChanged(BlockPos neighborPos, Block neighborBlock) {
		super.neighborChanged(neighborPos, neighborBlock);
		pendingChamberRecompute = true;
	}

	@Override
	protected void saveAdditional(ValueOutput output) {
		super.saveAdditional(output);
		output.putInt("TimeUntilNextCycle", timeUntilNextCycle);
		output.putBoolean("Paused", reactor.paused);
		output.putBoolean("AllowRedstoneControl", reactor.allowRedstoneControl);
		output.putDouble("EnergyOutput", reactor.energyOutput);
		output.putInt("Heat", reactor.heat);
		output.putInt("ActiveColumns", reactor.activeColumns);
		if (debugSpeed > 0) output.putInt("DebugSpeed", debugSpeed);
	}

	@Override
	protected void loadAdditional(ValueInput input) {
		super.loadAdditional(input);
		timeUntilNextCycle = input.getIntOr("TimeUntilNextCycle", 0);
		reactor.paused = input.getBooleanOr("Paused", true);
		reactor.allowRedstoneControl = input.getBooleanOr("AllowRedstoneControl", false);
		reactor.energyOutput = input.getDoubleOr("EnergyOutput", 0D);
		reactor.heat = input.getIntOr("Heat", 0);
		reactor.activeColumns = Math.max(3, Math.min(NuclearReactor.MAX_COLUMNS, input.getIntOr("ActiveColumns", 3)));
		debugSpeed = input.getIntOr("DebugSpeed", 0);
		pendingChamberRecompute = true;
	}

	private void checkPoweredState(Level level, BlockPos pos) {
		if (reactor.allowRedstoneControl) {
			reactor.paused = !level.hasNeighborSignal(pos);
		}
	}

	@Override
	public void handleGeneration() {
		if (pendingChamberRecompute) {
			pendingChamberRecompute = false;
			recomputeActiveColumns();
		}

		timeUntilNextCycle--;
		if (timeUntilNextCycle <= 0) {
			timeUntilNextCycle = 20;
			if (debugSpeed <= 0) runCycle();
		}
		if (debugSpeed > 0) {
			for (int i = 0; i < debugSpeed; i++) runCycle();
		}

		if (reactor.energyOutput > 0) {
			active = true;
			double produced = reactor.energyOutput * FTBICConfig.MACHINES.NUCLEAR_GENERATOR_OUTPUT.get();
			energy += Math.min(produced, energyCapacity - energy);
			setChanged();
		}

		checkPoweredState(level, worldPosition);
		if (reactor.heat <= 0 || reactor.maxHeat <= 0) return;

		float h = reactor.heat / (float) reactor.maxHeat;
		if (h >= 1F) {
			detonate();
		} else if (h >= 0.75F) {
			if (level.getGameTime() % 25L == 0L && reactor.energyOutput > 0D) {
				level.playSound(null, worldPosition,
						FTBICSounds.RADIATION.get(),
						SoundSource.BLOCKS, 0.5F, 1F);
			}
		}
	}

	private void runCycle() {
		reactor.envCoolingMultiplier = computeEnvCooling();
		double peo = reactor.energyOutput;
		int ph = reactor.heat;
		reactor.tick();
		if (peo != reactor.energyOutput || ph != reactor.heat) {
			setChanged();
		}
	}

	private void detonate() {
		if (!(level instanceof ServerLevel server)) return;
		Arrays.fill(inputItems, ItemStack.EMPTY);
		setChanged();
		level.setBlock(worldPosition, FTBICBlocks.ACTIVE_NUKE.get().defaultBlockState(), 3);
		for (Direction direction : FTBICUtils.DIRECTIONS) {
			BlockPos n = worldPosition.relative(direction);
			if (level.getBlockState(n).getBlock() instanceof NuclearReactorChamberBlock) {
				level.setBlock(n, FTBICBlocks.ACTIVE_NUKE.get().defaultBlockState(), 3);
			}
		}
		server.getServer().getPlayerList().broadcastSystemMessage(
				Component.translatable("block.ftbic.nuclear_reactor.broadcast", placerName).withStyle(ChatFormatting.RED), false);
		level.removeBlock(worldPosition, false);
		server.explode(null, null, null,
				worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5,
				(float) reactor.explosionRadius, true, Level.ExplosionInteraction.BLOCK);

		NuclearFallout.apply(server, worldPosition, reactor.explosionRadius);
	}

	@Override
	public void onBroken(Level level, BlockPos pos) {
		if (debugSpeed <= 0) super.onBroken(level, pos);
	}
}
