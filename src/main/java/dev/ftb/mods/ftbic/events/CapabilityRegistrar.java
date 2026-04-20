package dev.ftb.mods.ftbic.events;

import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.block.ElectricBlockInstance;
import dev.ftb.mods.ftbic.block.FTBICBlocks;
import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.block.entity.ElectricBlockEntity;
import dev.ftb.mods.ftbic.block.entity.generator.GeothermalGeneratorBlockEntity;
import dev.ftb.mods.ftbic.block.entity.generator.NuclearReactorBlockEntity;
import dev.ftb.mods.ftbic.block.entity.machine.PumpBlockEntity;
import dev.ftb.mods.ftbic.block.entity.machine.TeleporterBlockEntity;
import dev.ftb.mods.ftbic.item.FTBICItems;
import dev.ftb.mods.ftbic.util.ElectricBlockEnergyHandler;
import dev.ftb.mods.ftbic.util.ElectricBlockResourceHandler;
import dev.ftb.mods.ftbic.util.FluidCellHandler;
import dev.ftb.mods.ftbic.util.GeothermalTankHandler;
import dev.ftb.mods.ftbic.util.PumpTankHandler;
import dev.ftb.mods.ftbic.util.TeleporterFluidPassthroughHandler;
import dev.ftb.mods.ftbic.util.TeleporterItemPassthroughHandler;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import dev.ftb.mods.ftbic.block.entity.storage.EnergyRectifierBlockEntity;
import dev.ftb.mods.ftbic.util.EnergyRectifierFEHandler;

@EventBusSubscriber(modid = FTBIC.MOD_ID)
public final class CapabilityRegistrar {

	@SubscribeEvent
	public static void register(RegisterCapabilitiesEvent event) {
		boolean fullFE = FTBICConfig.ENERGY.FULL_FE_MODE.get();

		for (var instance : FTBICElectricBlocks.ALL) {
			@SuppressWarnings("unchecked")
			net.minecraft.world.level.block.entity.BlockEntityType<ElectricBlockEntity> type =
					(net.minecraft.world.level.block.entity.BlockEntityType<ElectricBlockEntity>) (Object) instance.blockEntity.get();
			if (instance != FTBICElectricBlocks.TELEPORTER) {
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
				case INSERT_ONLY -> event.registerBlockEntity(Capabilities.Energy.BLOCK, type,
						(be, side) -> {
							if (!(be instanceof EnergyRectifierBlockEntity rec)) return null;
							net.minecraft.core.Direction inputFace = be.getBlockState().getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING);
							if (side != null && side != inputFace) return null;
							return new EnergyRectifierFEHandler(rec);
						});
				case NONE -> {
				}
			}
		}

		// Geothermal generator exposes its lava tank as a FluidResource handler.
		@SuppressWarnings("unchecked")
		net.minecraft.world.level.block.entity.BlockEntityType<GeothermalGeneratorBlockEntity> geoType =
				(net.minecraft.world.level.block.entity.BlockEntityType<GeothermalGeneratorBlockEntity>)
						(Object) FTBICElectricBlocks.GEOTHERMAL_GENERATOR.blockEntity.get();
		event.registerBlockEntity(Capabilities.Fluid.BLOCK, geoType,
				(be, side) -> new GeothermalTankHandler(be));

		// Pump exposes its accumulated water/lava as an extract-only FluidResource handler.
		@SuppressWarnings("unchecked")
		net.minecraft.world.level.block.entity.BlockEntityType<PumpBlockEntity> pumpType =
				(net.minecraft.world.level.block.entity.BlockEntityType<PumpBlockEntity>)
						(Object) FTBICElectricBlocks.PUMP.blockEntity.get();
		event.registerBlockEntity(Capabilities.Fluid.BLOCK, pumpType,
				(be, side) -> new PumpTankHandler(be));

		// Teleporter: item and fluid caps act as a passthrough to the linked peer's 6 neighbor inventories.
		@SuppressWarnings("unchecked")
		net.minecraft.world.level.block.entity.BlockEntityType<TeleporterBlockEntity> teleType =
				(net.minecraft.world.level.block.entity.BlockEntityType<TeleporterBlockEntity>)
						(Object) FTBICElectricBlocks.TELEPORTER.blockEntity.get();
		event.registerBlockEntity(Capabilities.Item.BLOCK, teleType,
				(be, side) -> new TeleporterItemPassthroughHandler(be));
		event.registerBlockEntity(Capabilities.Fluid.BLOCK, teleType,
				(be, side) -> new TeleporterFluidPassthroughHandler(be));

		// FluidCell item: accept/provide fluid via Capabilities.Fluid.ITEM.
		event.registerItem(Capabilities.Fluid.ITEM,
				(stack, access) -> new FluidCellHandler(access),
				FTBICItems.FLUID_CELL.get());

		// Reactor chambers forward item + energy caps to the adjacent reactor so pipes, cables, and
		// hoppers plugged into a chamber flow straight into the central block.
		event.registerBlock(Capabilities.Item.BLOCK,
				(level, pos, state, be, side) -> forwardChamber(Capabilities.Item.BLOCK, level, pos),
				FTBICBlocks.NUCLEAR_REACTOR_CHAMBER.get());
		event.registerBlock(Capabilities.Energy.BLOCK,
				(level, pos, state, be, side) -> forwardChamber(Capabilities.Energy.BLOCK, level, pos),
				FTBICBlocks.NUCLEAR_REACTOR_CHAMBER.get());
	}

	private static <T> T forwardChamber(net.neoforged.neoforge.capabilities.BlockCapability<T, net.minecraft.core.Direction> cap,
			net.minecraft.world.level.Level level, net.minecraft.core.BlockPos chamberPos) {
		for (net.minecraft.core.Direction dir : net.minecraft.core.Direction.values()) {
			net.minecraft.core.BlockPos neighbor = chamberPos.relative(dir);
			if (level.getBlockEntity(neighbor) instanceof NuclearReactorBlockEntity) {
				T handler = level.getCapability(cap, neighbor, dir.getOpposite());
				if (handler != null) return handler;
			}
		}
		return null;
	}

	private CapabilityRegistrar() {}
}
