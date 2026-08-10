package dev.ftb.mods.industrialcontraptions.events;

import dev.ftb.mods.industrialcontraptions.IC;
import dev.ftb.mods.industrialcontraptions.ICConfig;
import dev.ftb.mods.industrialcontraptions.block.ElectricBlockInstance;
import dev.ftb.mods.industrialcontraptions.block.ICBlocks;
import dev.ftb.mods.industrialcontraptions.block.ICElectricBlocks;
import dev.ftb.mods.industrialcontraptions.block.entity.ElectricBlockEntity;
import dev.ftb.mods.industrialcontraptions.block.entity.generator.GeothermalGeneratorBlockEntity;
import dev.ftb.mods.industrialcontraptions.block.entity.generator.NuclearReactorBlockEntity;
import dev.ftb.mods.industrialcontraptions.block.entity.machine.PumpBlockEntity;
import dev.ftb.mods.industrialcontraptions.block.entity.machine.TeleporterBlockEntity;
import dev.ftb.mods.industrialcontraptions.block.entity.storage.EnergyRectifierBlockEntity;
import dev.ftb.mods.industrialcontraptions.item.ICItems;
import dev.ftb.mods.industrialcontraptions.util.ElectricBlockEnergyHandler;
import dev.ftb.mods.industrialcontraptions.util.ElectricBlockResourceHandler;
import dev.ftb.mods.industrialcontraptions.util.EnergyRectifierFEHandler;
import dev.ftb.mods.industrialcontraptions.util.ICCapabilities;
import dev.ftb.mods.industrialcontraptions.util.FluidCellHandler;
import dev.ftb.mods.industrialcontraptions.util.GeothermalTankHandler;
import dev.ftb.mods.industrialcontraptions.util.PumpTankHandler;
import dev.ftb.mods.industrialcontraptions.util.TeleporterFluidPassthroughHandler;
import dev.ftb.mods.industrialcontraptions.util.TeleporterItemPassthroughHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

@EventBusSubscriber(modid = IC.MOD_ID)
public final class CapabilityRegistrar {

	@SubscribeEvent
	public static void register(RegisterCapabilitiesEvent event) {
		boolean fullFE = ICConfig.ENERGY.FULL_FE_MODE.get();

		for (var instance : ICElectricBlocks.ALL) {
			@SuppressWarnings("unchecked")
			BlockEntityType<ElectricBlockEntity> type =
					(BlockEntityType<ElectricBlockEntity>) (Object) instance.blockEntity.get();

			event.registerBlockEntity(ICCapabilities.ZAP_ENERGY_BLOCK, type, (be, side) -> be);

			if (instance != ICElectricBlocks.TELEPORTER && instance != ICElectricBlocks.REACTOR_SIMULATOR) {
				event.registerBlockEntity(Capabilities.Item.BLOCK, type,
						(be, side) -> new ElectricBlockResourceHandler(be));
			}

			if (fullFE && instance.feCapMode != ElectricBlockInstance.FECapMode.INSERT_ONLY) {
				boolean canInsert = instance.maxEnergyInput.get() > 0D;
				boolean canExtract = instance.maxEnergyOutput.get() > 0D;
				if (canInsert || canExtract) {
					final boolean ci = canInsert;
					final boolean ce = canExtract;
					event.registerBlockEntity(Capabilities.Energy.BLOCK, type,
							(be, side) -> new ElectricBlockEnergyHandler(be, ci, ce));
				}
				continue;
			}

			switch (instance.feCapMode) {
				case EXTRACT_ONLY -> event.registerBlockEntity(Capabilities.Energy.BLOCK, type,
						(be, side) -> new ElectricBlockEnergyHandler(be, false, true));
				case INSERT_AND_EXTRACT -> event.registerBlockEntity(Capabilities.Energy.BLOCK, type,
						(be, side) -> new ElectricBlockEnergyHandler(be, true, true));
				case INSERT_ONLY -> event.registerBlockEntity(Capabilities.Energy.BLOCK, type,
						(be, side) -> {
							if (!(be instanceof EnergyRectifierBlockEntity rec)) return null;
							Direction inputFace = be.getBlockState().getValue(BlockStateProperties.FACING);
							if (side != null && side != inputFace) return null;
							return new EnergyRectifierFEHandler(rec);
						});
				case NONE -> {
				}
			}
		}

		@SuppressWarnings("unchecked")
		BlockEntityType<GeothermalGeneratorBlockEntity> geoType =
				(BlockEntityType<GeothermalGeneratorBlockEntity>)
						(Object) ICElectricBlocks.GEOTHERMAL_GENERATOR.blockEntity.get();
		event.registerBlockEntity(Capabilities.Fluid.BLOCK, geoType,
				(be, side) -> new GeothermalTankHandler(be));

		@SuppressWarnings("unchecked")
		BlockEntityType<PumpBlockEntity> pumpType =
				(BlockEntityType<PumpBlockEntity>)
						(Object) ICElectricBlocks.PUMP.blockEntity.get();
		event.registerBlockEntity(Capabilities.Fluid.BLOCK, pumpType,
				(be, side) -> new PumpTankHandler(be));

		@SuppressWarnings("unchecked")
		BlockEntityType<TeleporterBlockEntity> teleType =
				(BlockEntityType<TeleporterBlockEntity>)
						(Object) ICElectricBlocks.TELEPORTER.blockEntity.get();
		event.registerBlockEntity(Capabilities.Item.BLOCK, teleType,
				(be, side) -> new TeleporterItemPassthroughHandler(be));
		event.registerBlockEntity(Capabilities.Fluid.BLOCK, teleType,
				(be, side) -> new TeleporterFluidPassthroughHandler(be));

		event.registerItem(Capabilities.Fluid.ITEM,
				(stack, access) -> new FluidCellHandler(access),
				ICItems.FLUID_CELL.get());

		event.registerBlock(Capabilities.Item.BLOCK,
				(level, pos, state, be, side) -> forwardChamber(Capabilities.Item.BLOCK, level, pos),
				ICBlocks.NUCLEAR_REACTOR_CHAMBER.get());
		event.registerBlock(Capabilities.Energy.BLOCK,
				(level, pos, state, be, side) -> forwardChamber(Capabilities.Energy.BLOCK, level, pos),
				ICBlocks.NUCLEAR_REACTOR_CHAMBER.get());
		event.registerBlock(ICCapabilities.ZAP_ENERGY_BLOCK,
				(level, pos, state, be, side) -> forwardChamber(ICCapabilities.ZAP_ENERGY_BLOCK, level, pos),
				ICBlocks.NUCLEAR_REACTOR_CHAMBER.get());
	}

	private static <T> T forwardChamber(BlockCapability<T, Direction> cap, Level level, BlockPos chamberPos) {
		for (Direction dir : Direction.values()) {
			BlockPos neighbor = chamberPos.relative(dir);
			if (level.getBlockEntity(neighbor) instanceof NuclearReactorBlockEntity) {
				T handler = level.getCapability(cap, neighbor, dir.getOpposite());
				if (handler != null) return handler;
			}
		}
		return null;
	}

	private CapabilityRegistrar() {}
}
