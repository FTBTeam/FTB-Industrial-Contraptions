package dev.ftb.mods.ftbic.block.entity.machine;

import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.screen.PumpMenu;
import dev.ftb.mods.ftbic.util.FTBICUtils;
import dev.ftb.mods.ftbic.util.SideConfiguration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

public class PumpBlockEntity extends DiggingBaseBlockEntity {
    public Fluid storedFluid = Fluids.EMPTY;
    public int fluidAmount = 0;
    private BlockCapabilityCache<ResourceHandler<FluidResource>, Direction>[] fluidEjectCaches;

    public PumpBlockEntity(BlockPos pos, BlockState state) {
        super(FTBICElectricBlocks.PUMP, pos, state);
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv) {
        return new PumpMenu(id, inv, this);
    }

    @Override
    public void initProperties() {
        super.initProperties();
        diggingMineTicks = FTBICConfig.MACHINES.PUMP_MINE_TICKS.get();
        diggingMoveTicks = FTBICConfig.MACHINES.PUMP_MOVE_TICKS.get();
    }

    @Override
    protected boolean replaceFluidWithExfluid() {
        return FTBICConfig.MACHINES.PUMP_REPLACE_FLUID_EXFLUID.get();
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
            storedFluid = BuiltInRegistries.FLUID.getValue(ResourceKey.create(Registries.FLUID, id));
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
        BlockState emptied = state.hasProperty(BlockStateProperties.WATERLOGGED)
                ? state.setValue(BlockStateProperties.WATERLOGGED, false)
                : fluidReplacement();
        level.setBlock(miningPos, emptied, 3);
        setChanged();

        // Opportunistically fill a player-supplied empty bucket.
        tryFillBucket();
    }

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
        if (level != null && !level.isClientSide() && !isEffectivelyPaused()) tryFillBucket();
        if (autoEject) ejectFluid();
    }

    private void ejectFluid() {
        if (!(level instanceof ServerLevel serverLevel)) return;

        for (Direction dir : FTBICUtils.DIRECTIONS) {
            if (fluidAmount <= 0 || storedFluid == Fluids.EMPTY) return;
            if (!allowsTransfer(SideConfiguration.Resource.FLUIDS, dir, false)) continue;
            ResourceHandler<FluidResource> handler =
                    fluidEjectCache(serverLevel, dir).getCapability();
            if (handler == null) continue;

            int inserted;
            try (Transaction txn = Transaction.openRoot()) {
                inserted = handler.insert(FluidResource.of(storedFluid), fluidAmount, txn);
                if (inserted > 0) txn.commit();
            }
            if (inserted > 0) {
                fluidAmount -= inserted;
                if (fluidAmount <= 0) {
                    fluidAmount = 0;
                    storedFluid = Fluids.EMPTY;
                }
                setChanged();
            }
        }
    }

    @SuppressWarnings("unchecked")
    private BlockCapabilityCache<ResourceHandler<FluidResource>, Direction> fluidEjectCache(
            ServerLevel serverLevel, Direction dir) {
        if (fluidEjectCaches == null) {
            fluidEjectCaches = new BlockCapabilityCache[FTBICUtils.DIRECTIONS.length];
        }
        BlockCapabilityCache<ResourceHandler<FluidResource>, Direction> c = fluidEjectCaches[dir.ordinal()];
        if (c == null) {
            c = BlockCapabilityCache.create(
                    Capabilities.Fluid.BLOCK, serverLevel, worldPosition.relative(dir), dir.getOpposite());
            fluidEjectCaches[dir.ordinal()] = c;
        }
        return c;
    }
}
