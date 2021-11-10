package dev.ftb.mods.ftbic.block.entity.generator;

import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.block.ElectricBlockState;
import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GeothermalGeneratorBlockEntity extends GeneratorBlockEntity {
	public int fluidAmount = 0;
	private int prevFluidAmount = -1;
	private LazyOptional<GeothermalGeneratorTank> tankOptional;

	public GeothermalGeneratorBlockEntity() {
		super(FTBICElectricBlocks.GEOTHERMAL_GENERATOR.blockEntity.get(), 1, 1);
	}

	@Override
	public void initProperties() {
		super.initProperties();
		energyCapacity = FTBICConfig.GEOTHERMAL_GENERATOR_CAPACITY;
	}

	@Override
	public void writeData(CompoundTag tag) {
		super.writeData(tag);
		tag.putInt("FluidAmount", fluidAmount);
	}

	@Override
	public void readData(CompoundTag tag) {
		super.readData(tag);
		fluidAmount = tag.getInt("FluidAmount");
	}

	public LazyOptional<?> getTankOptional() {
		if (tankOptional == null) {
			tankOptional = LazyOptional.of(() -> new GeothermalGeneratorTank(this));
		}

		return tankOptional;
	}

	@Override
	public void invalidateCaps() {
		super.invalidateCaps();

		if (tankOptional != null) {
			tankOptional.invalidate();
			tankOptional = null;
		}
	}

	@Override
	public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
		return cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY ? getTankOptional().cast() : super.getCapability(cap, side);
	}

	@Override
	public void handleGeneration() {
		if (energy < energyCapacity && fluidAmount > 0) {
			energy += Math.min(FTBICConfig.GEOTHERMAL_GENERATOR_OUTPUT, energyCapacity - energy);
			fluidAmount--;
		}

		if (prevFluidAmount != fluidAmount) {
			if (prevFluidAmount == 0) {
				changeState = ElectricBlockState.ON;
			} else if (fluidAmount == 0) {
				changeState = ElectricBlockState.OFF;
			}

			prevFluidAmount = fluidAmount;
		}
	}

	@Override
	public InteractionResult rightClick(Player player, InteractionHand hand, BlockHitResult hit) {
		if (FluidUtil.interactWithFluidHandler(player, hand, (IFluidHandler) getTankOptional().orElse(null))) {
			return InteractionResult.SUCCESS;
		}

		return InteractionResult.PASS;
	}
}
