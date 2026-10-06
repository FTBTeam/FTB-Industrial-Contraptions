package dev.ftb.mods.ftbic.events;

import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.block.ElectricBlockInstance;
import dev.ftb.mods.ftbic.block.FTBICBlocks;
import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.block.entity.ElectricBlockEntity;
import dev.ftb.mods.ftbic.block.entity.generator.GeothermalGeneratorBlockEntity;
import dev.ftb.mods.ftbic.block.entity.generator.NuclearReactorBlockEntity;
import dev.ftb.mods.ftbic.block.entity.machine.BatchFeederBlockEntity;
import dev.ftb.mods.ftbic.block.entity.machine.FluidMachineBlockEntity;
import dev.ftb.mods.ftbic.block.entity.machine.HydroponicBlockEntity;
import dev.ftb.mods.ftbic.block.entity.machine.PumpBlockEntity;
import dev.ftb.mods.ftbic.block.entity.machine.TeleporterBlockEntity;
import dev.ftb.mods.ftbic.block.entity.storage.EnergyRectifierBlockEntity;
import dev.ftb.mods.ftbic.item.FTBICItems;
import dev.ftb.mods.ftbic.util.CableFEHandler;
import dev.ftb.mods.ftbic.util.ElectricBlockEnergyHandler;
import dev.ftb.mods.ftbic.util.ElectricBlockResourceHandler;
import dev.ftb.mods.ftbic.util.EnergyItemFEHandler;
import dev.ftb.mods.ftbic.util.EnergyItemHandler;
import dev.ftb.mods.ftbic.util.EnergyRectifierFEHandler;
import dev.ftb.mods.ftbic.util.FTBICCapabilities;
import dev.ftb.mods.ftbic.util.FluidCellHandler;
import dev.ftb.mods.ftbic.util.GeothermalTankHandler;
import dev.ftb.mods.ftbic.util.PumpTankHandler;
import dev.ftb.mods.ftbic.util.SideConfiguration;
import dev.ftb.mods.ftbic.util.SidedEnergyHandler;
import dev.ftb.mods.ftbic.util.SidedResourceHandler;
import dev.ftb.mods.ftbic.util.SidedZapHandler;
import dev.ftb.mods.ftbic.util.TeleporterFluidPassthroughHandler;
import dev.ftb.mods.ftbic.util.TeleporterItemPassthroughHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import org.jetbrains.annotations.Nullable;

@EventBusSubscriber(modid = FTBIC.MOD_ID)
public final class CapabilityRegistrar {

    @SubscribeEvent
    public static void register(RegisterCapabilitiesEvent event) {
        boolean fullFE = FTBICConfig.ENERGY.FULL_FE_MODE.get();
        if (fullFE) {
            for (var cable : FTBICBlocks.CABLES) {
                event.registerBlock(
                        Capabilities.Energy.BLOCK,
                        (level, pos, state, be, side) ->
                                level instanceof ServerLevel server ? new CableFEHandler(server, pos, side) : null,
                        cable.get());
            }
            for (var cable : FTBICBlocks.REINFORCED_CABLES) {
                event.registerBlock(
                        Capabilities.Energy.BLOCK,
                        (level, pos, state, be, side) ->
                                level instanceof ServerLevel server ? new CableFEHandler(server, pos, side) : null,
                        cable.get());
            }
        }

        for (var instance : FTBICElectricBlocks.ALL) {
            @SuppressWarnings("unchecked")
            BlockEntityType<ElectricBlockEntity> type =
                    (BlockEntityType<ElectricBlockEntity>) (Object) instance.blockEntity.get();

            if (instance != FTBICElectricBlocks.BATCH_FEEDER) {
                event.registerBlockEntity(
                        FTBICCapabilities.ZAP_ENERGY_BLOCK, type, (be, side) -> new SidedZapHandler(be, side));
            }

            if (instance != FTBICElectricBlocks.TELEPORTER && instance != FTBICElectricBlocks.REACTOR_SIMULATOR) {
                event.registerBlockEntity(
                        Capabilities.Item.BLOCK,
                        type,
                        (be, side) -> new SidedResourceHandler<>(
                                be, side, SideConfiguration.Resource.ITEMS, new ElectricBlockResourceHandler(be)));
            }

            if (instance == FTBICElectricBlocks.BATCH_FEEDER) {
                event.registerBlockEntity(
                        Capabilities.Fluid.BLOCK,
                        type,
                        (be, side) -> new SidedResourceHandler<>(
                                be,
                                side,
                                SideConfiguration.Resource.FLUIDS,
                                ((BatchFeederBlockEntity) be).fluidHandler));
            }

            if (instance == FTBICElectricBlocks.CENTRIFUGE
                    || instance == FTBICElectricBlocks.ADVANCED_CENTRIFUGE
                    || instance == FTBICElectricBlocks.ORE_WASHER
                    || instance == FTBICElectricBlocks.ADVANCED_ORE_WASHER) {
                event.registerBlockEntity(
                        Capabilities.Fluid.BLOCK,
                        type,
                        (be, side) -> new SidedResourceHandler<>(
                                be,
                                side,
                                SideConfiguration.Resource.FLUIDS,
                                ((FluidMachineBlockEntity) be).fluidHandler));
            }
            if (instance == FTBICElectricBlocks.HYDROPONIC_ACCELERATOR
                    || instance == FTBICElectricBlocks.ADVANCED_HYDROPONIC_ACCELERATOR) {
                event.registerBlockEntity(
                        Capabilities.Fluid.BLOCK,
                        type,
                        (be, side) -> new SidedResourceHandler<>(
                                be,
                                side,
                                SideConfiguration.Resource.FLUIDS,
                                ((HydroponicBlockEntity) be).fluidHandler));
            }

            if (fullFE && instance.feCapMode != ElectricBlockInstance.FECapMode.INSERT_ONLY) {
                boolean canInsert = instance.maxEnergyInput.get() > 0D
                        || instance.feCapMode == ElectricBlockInstance.FECapMode.INSERT_AND_EXTRACT;
                boolean canExtract = instance.maxEnergyOutput.get() > 0D
                        || instance.feCapMode == ElectricBlockInstance.FECapMode.EXTRACT_ONLY
                        || instance.feCapMode == ElectricBlockInstance.FECapMode.INSERT_AND_EXTRACT;
                if (canInsert || canExtract) {
                    final boolean ci = canInsert;
                    final boolean ce = canExtract;
                    event.registerBlockEntity(
                            Capabilities.Energy.BLOCK,
                            type,
                            (be, side) -> new SidedEnergyHandler(be, side, new ElectricBlockEnergyHandler(be, ci, ce)));
                }
                continue;
            }

            switch (instance.feCapMode) {
                case EXTRACT_ONLY ->
                    event.registerBlockEntity(
                            Capabilities.Energy.BLOCK,
                            type,
                            (be, side) ->
                                    new SidedEnergyHandler(be, side, new ElectricBlockEnergyHandler(be, false, true)));
                case INSERT_AND_EXTRACT ->
                    event.registerBlockEntity(
                            Capabilities.Energy.BLOCK,
                            type,
                            (be, side) ->
                                    new SidedEnergyHandler(be, side, new ElectricBlockEnergyHandler(be, true, true)));
                case INSERT_ONLY ->
                    event.registerBlockEntity(Capabilities.Energy.BLOCK, type, (be, side) -> {
                        if (!(be instanceof EnergyRectifierBlockEntity rec)) return null;
                        Direction inputFace = be.getBlockState().getValue(BlockStateProperties.FACING);
                        if (side != null && side != inputFace) return null;
                        return new SidedEnergyHandler(rec, side, new EnergyRectifierFEHandler(rec));
                    });
                case NONE -> {}
            }
        }

        @SuppressWarnings("unchecked")
        BlockEntityType<GeothermalGeneratorBlockEntity> geoType = (BlockEntityType<GeothermalGeneratorBlockEntity>)
                (Object) FTBICElectricBlocks.GEOTHERMAL_GENERATOR.blockEntity.get();
        event.registerBlockEntity(
                Capabilities.Fluid.BLOCK,
                geoType,
                (be, side) -> new SidedResourceHandler<>(
                        be, side, SideConfiguration.Resource.FLUIDS, new GeothermalTankHandler(be)));

        @SuppressWarnings("unchecked")
        BlockEntityType<PumpBlockEntity> pumpType =
                (BlockEntityType<PumpBlockEntity>) (Object) FTBICElectricBlocks.PUMP.blockEntity.get();
        event.registerBlockEntity(
                Capabilities.Fluid.BLOCK,
                pumpType,
                (be, side) -> new SidedResourceHandler<>(
                        be, side, SideConfiguration.Resource.FLUIDS, new PumpTankHandler(be)));

        @SuppressWarnings("unchecked")
        BlockEntityType<TeleporterBlockEntity> teleType =
                (BlockEntityType<TeleporterBlockEntity>) (Object) FTBICElectricBlocks.TELEPORTER.blockEntity.get();
        event.registerBlockEntity(
                Capabilities.Item.BLOCK,
                teleType,
                (be, side) -> new SidedResourceHandler<>(
                        be, side, SideConfiguration.Resource.ITEMS, new TeleporterItemPassthroughHandler(be)));
        event.registerBlockEntity(
                Capabilities.Fluid.BLOCK,
                teleType,
                (be, side) -> new SidedResourceHandler<>(
                        be, side, SideConfiguration.Resource.FLUIDS, new TeleporterFluidPassthroughHandler(be)));

        event.registerItem(
                Capabilities.Fluid.ITEM, (stack, access) -> new FluidCellHandler(access), FTBICItems.FLUID_CELL.get());

        if (fullFE) {
            for (var entry : FTBICItems.REGISTRY.getEntries()) {
                if (entry.get() instanceof EnergyItemHandler energyItem) {
                    event.registerItem(
                            Capabilities.Energy.ITEM,
                            (stack, access) -> EnergyItemFEHandler.create(access, energyItem),
                            entry.get());
                }
            }
        }

        event.registerBlock(
                Capabilities.Item.BLOCK,
                (level, pos, state, be, side) -> forwardChamber(Capabilities.Item.BLOCK, level, pos, side),
                FTBICBlocks.NUCLEAR_REACTOR_CHAMBER.get());
        event.registerBlock(
                Capabilities.Energy.BLOCK,
                (level, pos, state, be, side) -> forwardChamber(Capabilities.Energy.BLOCK, level, pos, side),
                FTBICBlocks.NUCLEAR_REACTOR_CHAMBER.get());
        event.registerBlock(
                FTBICCapabilities.ZAP_ENERGY_BLOCK,
                (level, pos, state, be, side) -> forwardChamber(FTBICCapabilities.ZAP_ENERGY_BLOCK, level, pos, side),
                FTBICBlocks.NUCLEAR_REACTOR_CHAMBER.get());
    }

    private static <T> T forwardChamber(
            BlockCapability<T, Direction> cap, Level level, BlockPos chamberPos, @Nullable Direction side) {
        for (Direction dir : Direction.values()) {
            BlockPos neighbor = chamberPos.relative(dir);
            if (level.getBlockEntity(neighbor) instanceof NuclearReactorBlockEntity) {
                T handler = level.getCapability(cap, neighbor, side);
                if (handler != null) return handler;
            }
        }
        return null;
    }

    private CapabilityRegistrar() {}
}
