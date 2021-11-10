package dev.ftb.mods.ftbic.block.entity.generator;

import dev.ftb.mods.ftbic.FTBICConfig;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

public class GeothermalGeneratorTank implements IFluidHandler, IFluidTank {
	public final GeothermalGeneratorBlockEntity generator;

	public GeothermalGeneratorTank(GeothermalGeneratorBlockEntity g) {
		generator = g;
	}

	@NotNull
	@Override
	public FluidStack getFluid() {
		return generator.fluidAmount == 0 ? FluidStack.EMPTY : new FluidStack(Fluids.LAVA, generator.fluidAmount);
	}

	@Override
	public int getFluidAmount() {
		return generator.fluidAmount;
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
	public int getTanks() {
		return 1;
	}

	@NotNull
	@Override
	public FluidStack getFluidInTank(int i) {
		return getFluid();
	}

	@Override
	public int getTankCapacity(int i) {
		return FTBICConfig.GEOTHERMAL_GENERATOR_LAVA_TANK;
	}

	@Override
	public boolean isFluidValid(int i, @NotNull FluidStack fluidStack) {
		return isFluidValid(fluidStack);
	}

	@Override
	public int fill(FluidStack resource, IFluidHandler.FluidAction action) {
		if (!resource.isEmpty() && isFluidValid(resource)) {
			int filled = Math.min(getCapacity() - generator.fluidAmount, resource.getAmount());

			if (filled > 0 && !action.simulate()) {
				generator.fluidAmount += filled;
				generator.setChanged();
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
