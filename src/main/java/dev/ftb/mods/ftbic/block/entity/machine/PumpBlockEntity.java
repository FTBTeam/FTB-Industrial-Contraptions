package dev.ftb.mods.ftbic.block.entity.machine;

import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/**
 * Fluid-source pump — walks the same 5×5 column grid as the Quarry, but instead of breaking blocks
 * it targets fluid source blocks (water, lava) and accumulates them into an internal fluid tank
 * ({@link #fluidAmount} mB of {@link #storedFluid}). The tank is exposed via a
 * {@code ResourceHandler<FluidResource>} (see {@code PumpTankHandler}) so foreign pipes can drain
 * the accumulated fluid out.
 *
 * Convenience behaviour: an empty bucket in input slot 0 will be auto-filled with 1000 mB from the
 * tank (matching fluid type) and pushed to the output slot, preserving the 1.18.2 bucket workflow.
 */
public class PumpBlockEntity extends DiggingBaseBlockEntity {
	public Fluid storedFluid = Fluids.EMPTY;
	public int fluidAmount = 0;

	public PumpBlockEntity(BlockPos pos, BlockState state) {
		super(FTBICElectricBlocks.PUMP, pos, state);
	}

	@Override
	public net.minecraft.world.inventory.AbstractContainerMenu createMenu(int id, net.minecraft.world.entity.player.Inventory inv) {
		return new dev.ftb.mods.ftbic.screen.PumpMenu(id, inv, this);
	}

	public int getTankCapacity() {
		return FTBICConfig.MACHINES.PUMP_TANK_CAPACITY.get();
	}

	@Override
	protected void saveAdditional(ValueOutput output) {
		super.saveAdditional(output);
		if (fluidAmount > 0 && storedFluid != Fluids.EMPTY) {
			output.putInt("FluidAmount", fluidAmount);
			Identifier fluidId = BuiltInRegistries.FLUID.getKey(storedFluid);
			output.putString("Fluid", fluidId.toString());
		}
	}

	@Override
	protected void loadAdditional(ValueInput input) {
		super.loadAdditional(input);
		fluidAmount = input.getIntOr("FluidAmount", 0);
		String fluidStr = input.getStringOr("Fluid", "");
		if (!fluidStr.isEmpty()) {
			Identifier id = Identifier.parse(fluidStr);
			storedFluid = BuiltInRegistries.FLUID.getValue(ResourceKey.create(net.minecraft.core.registries.Registries.FLUID, id));
			if (storedFluid == null) storedFluid = Fluids.EMPTY;
		} else {
			storedFluid = Fluids.EMPTY;
			fluidAmount = 0;
		}
	}

	@Override
	public boolean isValidBlock(BlockState state, BlockPos pos) {
		if (level == null) return false;
		FluidState fluidState = state.getFluidState();
		if (!fluidState.isSource()) return false;
		Fluid f = fluidState.getType();
		if (f != Fluids.WATER && f != Fluids.LAVA) return false;
		// Reject fluids that don't match whatever is already in the tank.
		if (storedFluid != Fluids.EMPTY && f != storedFluid) return false;
		return fluidAmount + 1000 <= getTankCapacity();
	}

	@Override
	public void digBlock(BlockState state, BlockPos miningPos) {
		if (level == null) return;
		FluidState fluidState = state.getFluidState();
		Fluid f = fluidState.getType();
		if (f != Fluids.WATER && f != Fluids.LAVA) return;
		if (storedFluid != Fluids.EMPTY && f != storedFluid) return;
		if (fluidAmount + 1000 > getTankCapacity()) return;

		storedFluid = f;
		fluidAmount += 1000;
		level.removeBlock(miningPos, false);
		setChanged();

		// Opportunistically fill a player-supplied empty bucket.
		tryFillBucket();
	}

	/**
	 * If input slot 0 has an empty bucket and the tank has ≥ 1000 mB, swap it into a filled bucket
	 * in the output slot.
	 */
	public void tryFillBucket() {
		if (fluidAmount < 1000 || storedFluid == Fluids.EMPTY) return;
		if (inputItems.length == 0 || inputItems[0].isEmpty()) return;
		if (inputItems[0].getItem() != Items.BUCKET) return;

		ItemStack filled;
		if (storedFluid == Fluids.WATER) filled = new ItemStack(Items.WATER_BUCKET);
		else if (storedFluid == Fluids.LAVA) filled = new ItemStack(Items.LAVA_BUCKET);
		else return;

		ItemStack leftover = addToOutputs(filled);
		if (!leftover.isEmpty()) return;
		inputItems[0].shrink(1);
		if (inputItems[0].getCount() <= 0) inputItems[0] = ItemStack.EMPTY;
		fluidAmount -= 1000;
		if (fluidAmount == 0) storedFluid = Fluids.EMPTY;
		setChanged();
	}

	@Override
	public void tick() {
		super.tick();
		// Regularly try to fill buckets even when the laser is between mining cycles.
		if (level != null && !level.isClientSide()) tryFillBucket();
	}
}
