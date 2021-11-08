package dev.ftb.mods.ftbic.block.entity.generator;

import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.block.ElectricBlockState;
import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GeothermalGeneratorBlockEntity extends GeneratorBlockEntity implements IFluidTank {
	public int fluidAmount = 0;
	private int prevFluidAmount = -1;

	public GeothermalGeneratorBlockEntity() {
		super(FTBICElectricBlocks.GEOTHERMAL_GENERATOR.blockEntity.get(), 1, 1);
	}

	@Override
	public void initProperties() {
		super.initProperties();
		energyCapacity = 24000;
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

	@Override
	public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
		return cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY ? getThisOptional().cast() : super.getCapability(cap, side);
	}

	@Override
	public void handleGeneration() {
		if (energy < energyCapacity && fluidAmount > 0) {
			energy += FTBICConfig.GEOTHERMAL_GENERATOR_OUTPUT;
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

	@NotNull
	@Override
	public FluidStack getFluid() {
		return fluidAmount == 0 ? FluidStack.EMPTY : new FluidStack(Fluids.LAVA, fluidAmount);
	}

	@Override
	public int getFluidAmount() {
		return fluidAmount;
	}

	@Override
	public int getCapacity() {
		return FTBICConfig.GEOTHERMAL_GENERATOR_LAVA_TANK;
	}

	@Override
	public boolean isFluidValid(FluidStack fluidStack) {
		return fluidStack.getFluid() == Fluids.LAVA;
	}

	@Override
	public int fill(FluidStack resource, IFluidHandler.FluidAction action) {
		if (!resource.isEmpty() && isFluidValid(resource)) {
			int filled = Math.min(getCapacity() - fluidAmount, resource.getAmount());

			if (filled > 0 && !action.simulate()) {
				fluidAmount += filled;
				setChanged();
			}

			return filled;
		}

		return 0;
	}

	@NotNull
	@Override
	public FluidStack drain(int amount, IFluidHandler.FluidAction action) {
		return FluidStack.EMPTY;
	}

	@NotNull
	@Override
	public FluidStack drain(FluidStack resource, IFluidHandler.FluidAction action) {
		return FluidStack.EMPTY;
	}
}
