package dev.ftb.mods.ftbic.events;

import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.block.entity.ElectricBlockEntity;
import dev.ftb.mods.ftbic.block.entity.generator.GeothermalGeneratorBlockEntity;
import dev.ftb.mods.ftbic.block.entity.machine.PumpBlockEntity;
import dev.ftb.mods.ftbic.item.FTBICItems;
import dev.ftb.mods.ftbic.util.ElectricBlockEnergyHandler;
import dev.ftb.mods.ftbic.util.ElectricBlockResourceHandler;
import dev.ftb.mods.ftbic.util.FluidCellHandler;
import dev.ftb.mods.ftbic.util.GeothermalTankHandler;
import dev.ftb.mods.ftbic.util.PumpTankHandler;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

/**
 * Registers item, energy, and (for the Geothermal generator) fluid capability providers so hoppers,
 * cables, and pipes from other mods can interact with FTBIC machines via the new NeoForge transfer API.
 */
@EventBusSubscriber(modid = FTBIC.MOD_ID)
public final class CapabilityRegistrar {

	@SubscribeEvent
	public static void register(RegisterCapabilitiesEvent event) {
		for (var instance : FTBICElectricBlocks.ALL) {
			@SuppressWarnings("unchecked")
			net.minecraft.world.level.block.entity.BlockEntityType<ElectricBlockEntity> type =
					(net.minecraft.world.level.block.entity.BlockEntityType<ElectricBlockEntity>) (Object) instance.blockEntity.get();
			event.registerBlockEntity(Capabilities.Item.BLOCK, type,
					(be, side) -> new ElectricBlockResourceHandler(be));
			event.registerBlockEntity(Capabilities.Energy.BLOCK, type,
					(be, side) -> new ElectricBlockEnergyHandler(be));
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

		// FluidCell item: accept/provide fluid via Capabilities.Fluid.ITEM.
		event.registerItem(Capabilities.Fluid.ITEM,
				(stack, access) -> new FluidCellHandler(access),
				FTBICItems.FLUID_CELL.get());
	}

	private CapabilityRegistrar() {}
}
