package dev.ftb.mods.ftbic.block.entity.machine;

import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.block.FTBICBlocks;
import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.screen.PumpMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PumpBlockEntity extends DiggingBaseBlockEntity implements IFluidHandler, IFluidTank {
	private static final float[] LASER_COLOR = {0.2F, 0.5F, 1F};
	public FluidStack fluidStack;
	public Fluid filter;

	public PumpBlockEntity(BlockPos pos, BlockState state) {
		super(FTBICElectricBlocks.PUMP, pos, state);
		fluidStack = FluidStack.EMPTY;
		filter = Fluids.EMPTY;
	}

	@Override
	public void initProperties() {
		super.initProperties();
		diggingMineTicks = FTBICConfig.MACHINES.PUMP_MINE_TICKS.get();
		diggingMoveTicks = FTBICConfig.MACHINES.PUMP_MOVE_TICKS.get();
	}

	@Override
	public void writeData(CompoundTag tag) {
		super.writeData(tag);
		tag.put("Fluid", fluidStack.writeToNBT(new CompoundTag()));
		tag.putString("Filter", Registry.FLUID.getKey(filter).toString());
	}

	@Override
	public void readData(CompoundTag tag) {
		super.readData(tag);
		fluidStack = FluidStack.loadFluidStackFromNBT(tag.getCompound("Fluid"));
		filter = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(tag.getString("Filter")));
	}

	@Override
	public void readNetData(CompoundTag tag) {
		super.readNetData(tag);
		fluidStack = FluidStack.loadFluidStackFromNBT(tag.getCompound("Fluid"));
		filter = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(tag.getString("Filter")));
	}

	@Override
	public void writeNetData(CompoundTag tag) {
		super.writeNetData(tag);
		tag.put("Fluid", fluidStack.writeToNBT(new CompoundTag()));
		tag.putString("Filter", Registry.FLUID.getKey(filter).toString());
	}

	@Override
	public boolean isValidBlock(BlockState state, BlockPos pos) {
		return state.getMaterial().isLiquid() && state.getBlock() instanceof BucketPickup && (filter == Fluids.EMPTY || filter.isSame(state.getFluidState().getType())) && state.getFluidState().isSource();
	}

	@Override
	public void digBlock(BlockState state, BlockPos miningPos, double lx, double ly, double lz) {
		if (state.getBlock() instanceof BucketPickup bucketPickup && fluidStack.getAmount() + FluidType.BUCKET_VOLUME <= getCapacity()) {
			FluidStack fluidStack2 = FluidUtil.getFluidContained(bucketPickup.pickupBlock(level, miningPos, state)).orElse(FluidStack.EMPTY);

			if (!fluidStack2.isEmpty()) {
				if (filter == Fluids.EMPTY) {
					filter = fluidStack2.getFluid();
				} else if (filter != fluidStack2.getFluid()) {
					return;
				}

				if (fluidStack.isEmpty()) {
					fluidStack = new FluidStack(filter, FluidType.BUCKET_VOLUME);
				} else {
					fluidStack.setAmount(fluidStack.getAmount() + FluidType.BUCKET_VOLUME);
				}

				BlockState replaceState = (FTBICConfig.MACHINES.PUMP_REPLACE_FLUID_EXFLUID.get()) ? FTBICBlocks.EXFLUID.get().defaultBlockState() : Blocks.AIR.defaultBlockState();
				level.setBlock(miningPos, replaceState, 2);
				setChanged();
			}
		}
	}

	@Override
	public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
		return cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY ? getThisOptional().cast() : super.getCapability(cap, side);
	}

	@Override
	public InteractionResult rightClick(Player player, InteractionHand hand, BlockHitResult hit) {
		if (!level.isClientSide()) {
			if (player.isCrouching()) {
				paused = !paused;
				syncBlock();
			} else {
				openMenu((ServerPlayer) player, (id, inventory) -> new PumpMenu(id, inventory, this));
			}
		}

		return InteractionResult.SUCCESS;
	}

	@Override
	public void writeMenu(ServerPlayer player, FriendlyByteBuf buf) {
		super.writeMenu(player, buf);
		buf.writeResourceLocation(Registry.FLUID.getKey(filter));
		fluidStack.writeToPacket(buf);
	}

	@Override
	public float[] getLaserColor() {
		return LASER_COLOR;
	}

	@NotNull
	@Override
	public FluidStack getFluid() {
		return fluidStack;
	}

	@Override
	public int getFluidAmount() {
		return fluidStack.getAmount();
	}

	@Override
	public int getCapacity() {
		return FTBICConfig.MACHINES.PUMP_TANK_CAPACITY.get();
	}

	@Override
	public boolean isFluidValid(FluidStack fluidStack) {
		return false;
	}

	@Override
	public int getTanks() {
		return 1;
	}

	@NotNull
	@Override
	public FluidStack getFluidInTank(int i) {
		return fluidStack;
	}

	@Override
	public int getTankCapacity(int i) {
		return FTBICConfig.MACHINES.PUMP_TANK_CAPACITY.get();
	}

	@Override
	public boolean isFluidValid(int i, @NotNull FluidStack fluidStack) {
		return false;
	}

	@Override
	public int fill(FluidStack fluidStack, FluidAction fluidAction) {
		return 0;
	}

	@NotNull
	@Override
	public FluidStack drain(FluidStack resource, FluidAction action) {
		if (level == null || level.isClientSide() || resource.isEmpty() || !resource.isFluidEqual(fluidStack)) {
			return FluidStack.EMPTY;
		}

		return drain(resource.getAmount(), action);
	}

	@NotNull
	@Override
	public FluidStack drain(int maxDrain, FluidAction action) {
		if (level == null || level.isClientSide()) {
			return FluidStack.EMPTY;
		}

		int drained = maxDrain;

		if (fluidStack.getAmount() < drained) {
			drained = fluidStack.getAmount();
		}

		FluidStack stack = new FluidStack(fluidStack, drained);

		if (action.execute() && drained > 0) {
			fluidStack.shrink(drained);
			setChanged();

			if (fluidStack.isEmpty() && paused) {
				paused = false;
				syncBlock();
			}
		}

		return stack;
	}

	@Override
	public boolean isItemValid(int slot, @NotNull ItemStack stack) {
		switch(slot) {
			case 0:
				if (stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).isPresent())
					return true;
				break;
			case 1:
				break;
		}
		return false;
	}

	@Override
	public void handleProcessing() {
		super.handleProcessing();

		IFluidHandlerItem iFluidHandlerItemInput = inputItems[0].getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).orElse(null);
		IFluidHandlerItem iFluidHandlerItemOutput = outputItems[0].getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).orElse(null);
		boolean hasFilledItem = false;
		if (iFluidHandlerItemInput != null) {
			int itemCapacityInput = iFluidHandlerItemInput.getTankCapacity(0);
			int emptyAmount = iFluidHandlerItemInput.getTankCapacity(0) - iFluidHandlerItemInput.getFluidInTank(0).getAmount();
			boolean isBucket = iFluidHandlerItemInput.getContainer().getItem() instanceof BucketItem;
			if (drain(emptyAmount, FluidAction.SIMULATE).getAmount() == emptyAmount && emptyAmount > 0) {
				if (outputItems[0].isEmpty()) {
					ItemStack resultStack = iFluidHandlerItemInput.getContainer().copy();
					IFluidHandlerItem newIFluidHandlerOutput = resultStack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).orElse(null);
					if (newIFluidHandlerOutput != null) {
						iFluidHandlerItemInput.getContainer().shrink(1);
						newIFluidHandlerOutput.getContainer().setCount(1);
						newIFluidHandlerOutput.fill(drain(itemCapacityInput, FluidAction.EXECUTE), FluidAction.EXECUTE);
						outputItems[0] = (!isBucket) ? resultStack : new ItemStack(newIFluidHandlerOutput.getFluidInTank(0).getFluid().getBucket());
						hasFilledItem = true;
					}
				} else if (iFluidHandlerItemOutput != null) {
					int itemCapacityOutput = iFluidHandlerItemOutput.getTankCapacity(0);
					boolean filledOutput = iFluidHandlerItemOutput.getFluidInTank(0).getAmount() == itemCapacityOutput;
					boolean fluidMatch = iFluidHandlerItemOutput.getFluidInTank(0).isFluidEqual(fluidStack);
					boolean sameItem = iFluidHandlerItemOutput.getContainer().sameItem(iFluidHandlerItemInput.getContainer());
					boolean sameCapacity = (itemCapacityInput == itemCapacityOutput);
					boolean stackFull = iFluidHandlerItemOutput.getContainer().getCount() >= iFluidHandlerItemOutput.getContainer().getMaxStackSize();
					if (sameItem && fluidMatch && filledOutput && sameCapacity && !stackFull) {
						drain(itemCapacityInput, FluidAction.EXECUTE);
						iFluidHandlerItemInput.getContainer().shrink(1);
						iFluidHandlerItemOutput.getContainer().grow(1);
						hasFilledItem = true;
					}
				}
			}
		}

		if (hasFilledItem) {
			setChanged();
		}
	}
}
