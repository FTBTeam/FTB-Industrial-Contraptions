package dev.ftb.mods.ftbic.item;

import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CellData implements IFluidHandlerItem, ICapabilityProvider {
	private final LazyOptional<IFluidHandlerItem> holder = LazyOptional.of(() -> this);
	@Nonnull
	protected ItemStack container;

	public CellData(@Nonnull ItemStack container) {
		this.container = container;
	}

	@Override
	@Nonnull
	public ItemStack getContainer() {
		return this.container;
	}

	public boolean canFillFluidType(FluidStack fluid) {
		return CellItem.FLUID_TO_CELL_MAP.containsKey(fluid.getFluid());
	}

	@Nonnull
	public FluidStack getFluid() {
		Item item = this.container.getItem();
		if (item instanceof CellItem) {
			return new FluidStack(((CellItem) item).fluid, 1000);
		} else {
			return FluidStack.EMPTY;
		}
	}

	protected void setFluid(@Nonnull FluidStack fluidStack) {
		if (fluidStack.isEmpty() || !CellItem.FLUID_TO_CELL_MAP.containsKey(fluidStack.getFluid())) {
			this.container = new ItemStack(FTBICItems.EMPTY_CELL.get());
		} else {
			this.container = new ItemStack(CellItem.FLUID_TO_CELL_MAP.get(fluidStack.getFluid()));
		}

	}

	@Override
	public int getTanks() {
		return 1;
	}

	@Override
	@Nonnull
	public FluidStack getFluidInTank(int tank) {
		return this.getFluid();
	}

	@Override
	public int getTankCapacity(int tank) {
		return 1000;
	}

	@Override
	public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
		return true;
	}

	@Override
	public int fill(FluidStack resource, IFluidHandler.FluidAction action) {
		if (this.container.getCount() == 1 && resource.getAmount() >= 1000 && this.getFluid().isEmpty() && this.canFillFluidType(resource)) {
			if (action.execute()) {
				this.setFluid(resource);
			}

			return 1000;
		} else {
			return 0;
		}
	}

	@Override
	@Nonnull
	public FluidStack drain(FluidStack resource, IFluidHandler.FluidAction action) {
		if (this.container.getCount() == 1 && resource.getAmount() >= 1000) {
			FluidStack fluidStack = this.getFluid();
			if (!fluidStack.isEmpty() && fluidStack.isFluidEqual(resource)) {
				if (action.execute()) {
					this.setFluid(FluidStack.EMPTY);
				}

				return fluidStack;
			} else {
				return FluidStack.EMPTY;
			}
		} else {
			return FluidStack.EMPTY;
		}
	}

	@Override
	@Nonnull
	public FluidStack drain(int maxDrain, IFluidHandler.FluidAction action) {
		if (this.container.getCount() == 1 && maxDrain >= 1000) {
			FluidStack fluidStack = this.getFluid();
			if (!fluidStack.isEmpty()) {
				if (action.execute()) {
					this.setFluid(FluidStack.EMPTY);
				}

				return fluidStack;
			} else {
				return FluidStack.EMPTY;
			}
		} else {
			return FluidStack.EMPTY;
		}
	}

	@Override
	@Nonnull
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
		return CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY.orEmpty(capability, this.holder);
	}
}
