package dev.ftb.mods.ftbic.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;

public class SuperconductingCableBlockEntity extends BlockEntity {
	public static final int PULSE_DURATION = 16;
	private static final int FADE_TICKS = 4;
	private static final int ACTIVE_EVENT = 0;
	private long lastTransferTick = Long.MIN_VALUE;
	private boolean active;
	private long changeTick = Long.MIN_VALUE;

	public SuperconductingCableBlockEntity(BlockPos pos, BlockState state) {
		super(FTBICBlockEntities.SUPERCONDUCTING_CABLE.get(), pos, state);
	}

	public void recordTransfer() {
		if (level == null || level.isClientSide()) return;
		lastTransferTick = level.getGameTime();
		if (!active) {
			setActive(true);
			level.scheduleTick(worldPosition, getBlockState().getBlock(), PULSE_DURATION);
		}
	}

	public void checkIdle() {
		if (!active || level == null || level.isClientSide()) return;
		long idle = level.getGameTime() - lastTransferTick;
		if (idle >= PULSE_DURATION) {
			setActive(false);
		} else {
			level.scheduleTick(worldPosition, getBlockState().getBlock(), (int) (PULSE_DURATION - idle));
		}
	}

	private void setActive(boolean value) {
		active = value;
		level.blockEvent(worldPosition, getBlockState().getBlock(), ACTIVE_EVENT, value ? 1 : 0);
	}

	public boolean isTransferring() {
		return active;
	}

	public float pulseStrength(float partialTick) {
		if (level == null || changeTick == Long.MIN_VALUE) return active ? 1F : 0F;
		float elapsed = Math.max(0F, level.getGameTime() - changeTick + partialTick) / FADE_TICKS;
		return Math.clamp(active ? elapsed : 1F - elapsed, 0F, 1F);
	}

	public boolean isGlowing() {
		return active || pulseStrength(0F) > 0F;
	}

	@Override
	public boolean triggerEvent(int id, int parameter) {
		if (id != ACTIVE_EVENT || level == null) return false;
		if (level.isClientSide()) {
			active = parameter != 0;
			changeTick = level.getGameTime();
		}
		return true;
	}

	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
		CompoundTag tag = super.getUpdateTag(registries);
		tag.putBoolean("Active", active);
		return tag;
	}

	@Override
	protected void loadAdditional(ValueInput input) {
		super.loadAdditional(input);
		active = input.getBooleanOr("Active", false);
	}
}
