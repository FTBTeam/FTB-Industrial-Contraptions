package dev.ftb.mods.ftbic.block.entity.generator;

import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.block.FTBICBlocks;
import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.block.NuclearReactorChamberBlock;
import dev.ftb.mods.ftbic.item.reactor.NuclearReactor;
import dev.ftb.mods.ftbic.util.FTBICUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.Arrays;

/**
 * Nuclear reactor — runs the full 1.18.2-style two-pass simulation via {@link NuclearReactor}.
 * Plating raises maxHeat and shrinks explosion, heat vents dissipate, exchangers balance, coolants
 * buffer, fuel rods produce energy and heat. If heat reaches maxHeat the reactor detonates and
 * nearby chamber blocks also explode.
 */
public class NuclearReactorBlockEntity extends GeneratorBlockEntity {
	public final NuclearReactor reactor;
	public int timeUntilNextCycle;
	public int debugSpeed;

	public NuclearReactorBlockEntity(BlockPos pos, BlockState state) {
		super(FTBICElectricBlocks.NUCLEAR_REACTOR, pos, state);
		reactor = new NuclearReactor(inputItems);
	}

	@Override
	public net.minecraft.world.inventory.AbstractContainerMenu createMenu(int id, net.minecraft.world.entity.player.Inventory inv) {
		return new dev.ftb.mods.ftbic.screen.NuclearReactorMenu(id, inv, this);
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
	protected void saveAdditional(ValueOutput output) {
		super.saveAdditional(output);
		output.putInt("TimeUntilNextCycle", timeUntilNextCycle);
		output.putBoolean("Paused", reactor.paused);
		output.putBoolean("AllowRedstoneControl", reactor.allowRedstoneControl);
		output.putDouble("EnergyOutput", reactor.energyOutput);
		output.putInt("Heat", reactor.heat);
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
		debugSpeed = input.getIntOr("DebugSpeed", 0);
	}

	private void checkPoweredState(Level level, BlockPos pos) {
		if (reactor.allowRedstoneControl) {
			reactor.paused = !level.hasNeighborSignal(pos);
		}
	}

	@Override
	public void handleGeneration() {
		// GeneratorBlockEntity.tick() already gated on level != null && !level.isClientSide().
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
						dev.ftb.mods.ftbic.sound.FTBICSounds.RADIATION.get(),
						SoundSource.BLOCKS, 0.5F, 1F);
			}
		}
	}

	private void runCycle() {
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

		dev.ftb.mods.ftbic.util.NuclearFallout.apply(server, worldPosition, reactor.explosionRadius);
	}

	@Override
	public void onBroken(Level level, BlockPos pos) {
		if (debugSpeed <= 0) super.onBroken(level, pos);
	}
}
