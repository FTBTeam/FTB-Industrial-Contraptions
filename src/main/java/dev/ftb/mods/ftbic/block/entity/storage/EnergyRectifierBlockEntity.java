package dev.ftb.mods.ftbic.block.entity.storage;

import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.block.ElectricBlockInstance;
import dev.ftb.mods.ftbic.block.entity.generator.GeneratorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/**
 * FE → zaps converter. Foreign mods push FE into the block on its facing/input side; each tick the
 * accumulated FE is converted into the internal zaps buffer at {@code ZAP_TO_FE_CONVERSION_RATE} (10:1
 * by default — 320 FE = 32 zaps for the LV rectifier). Zaps are then provided to the FTBIC electric
 * network via the standard {@link ElectricBlockEntity} energy interface on the other 5 faces.
 */
public class EnergyRectifierBlockEntity extends GeneratorBlockEntity {
	public long feBuffer = 0L;

	public EnergyRectifierBlockEntity(ElectricBlockInstance type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	public long getFeBufferCapacity() {
		double rate = FTBICConfig.ENERGY.ZAP_TO_FE_CONVERSION_RATE.get();
		return (long) Math.ceil(electricBlockInstance.maxEnergyInput.get() * rate * 4D);
	}

	public long insertFE(long amount, boolean simulate) {
		if (amount <= 0L) return 0L;
		long room = getFeBufferCapacity() - feBuffer;
		if (room <= 0L) return 0L;
		long accepted = Math.min(amount, room);
		if (!simulate) {
			feBuffer += accepted;
			setChanged();
		}
		return accepted;
	}

	@Override
	public void tick() {
		super.tick();
		if (level == null || level.isClientSide()) return;
		drainFEToZaps();
	}

	private void drainFEToZaps() {
		if (feBuffer <= 0L) return;
		double rate = FTBICConfig.ENERGY.ZAP_TO_FE_CONVERSION_RATE.get();
		double zapRoom = energyCapacity - energy;
		if (zapRoom <= 0D) return;
		double feNeeded = zapRoom * rate;
		long feToConsume = (long) Math.min(feBuffer, feNeeded);
		if (feToConsume <= 0L) return;
		double zapsAdded = feToConsume / rate;
		energy += zapsAdded;
		feBuffer -= feToConsume;
		setChanged();
	}

	@Override
	public boolean isValidEnergyOutputSide(net.minecraft.core.Direction direction) {
		// Block the input face (matches the FE-input face) from also pushing zaps to FTBIC cables.
		net.minecraft.world.level.block.state.BlockState st = getBlockState();
		net.minecraft.core.Direction inputFace = st.getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING);
		return direction != inputFace;
	}

	@Override
	protected void saveAdditional(ValueOutput output) {
		super.saveAdditional(output);
		if (feBuffer > 0L) output.putLong("FeBuffer", feBuffer);
	}

	@Override
	protected void loadAdditional(ValueInput input) {
		super.loadAdditional(input);
		feBuffer = input.getLongOr("FeBuffer", 0L);
	}
}
