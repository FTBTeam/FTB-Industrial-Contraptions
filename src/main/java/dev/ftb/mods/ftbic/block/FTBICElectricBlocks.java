package dev.ftb.mods.ftbic.block;

import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.block.entity.generator.BasicGeneratorBlockEntity;
import dev.ftb.mods.ftbic.block.entity.generator.EVSolarPanelBlockEntity;
import dev.ftb.mods.ftbic.block.entity.generator.GeothermalGeneratorBlockEntity;
import dev.ftb.mods.ftbic.block.entity.generator.HVSolarPanelBlockEntity;
import dev.ftb.mods.ftbic.block.entity.generator.LVSolarPanelBlockEntity;
import dev.ftb.mods.ftbic.block.entity.generator.MVSolarPanelBlockEntity;
import dev.ftb.mods.ftbic.block.entity.generator.NuclearReactorBlockEntity;
import dev.ftb.mods.ftbic.block.entity.generator.WindMillBlockEntity;
import dev.ftb.mods.ftbic.block.entity.machine.AdvancedCentrifugeBlockEntity;
import dev.ftb.mods.ftbic.block.entity.machine.AdvancedCompressorBlockEntity;
import dev.ftb.mods.ftbic.block.entity.machine.AdvancedMaceratorBlockEntity;
import dev.ftb.mods.ftbic.block.entity.machine.AdvancedPoweredFurnaceBlockEntity;
import dev.ftb.mods.ftbic.block.entity.machine.AntimatterConstructorBlockEntity;
import dev.ftb.mods.ftbic.block.entity.machine.CanningMachineBlockEntity;
import dev.ftb.mods.ftbic.block.entity.machine.CentrifugeBlockEntity;
import dev.ftb.mods.ftbic.block.entity.machine.CompressorBlockEntity;
import dev.ftb.mods.ftbic.block.entity.machine.ExtruderBlockEntity;
import dev.ftb.mods.ftbic.block.entity.machine.MaceratorBlockEntity;
import dev.ftb.mods.ftbic.block.entity.machine.PoweredFurnaceBlockEntity;
import dev.ftb.mods.ftbic.block.entity.machine.ReprocessorBlockEntity;
import dev.ftb.mods.ftbic.block.entity.machine.RollerBlockEntity;
import dev.ftb.mods.ftbic.block.entity.storage.HVBatteryBoxBlockEntity;
import dev.ftb.mods.ftbic.block.entity.storage.HVTransformerBlockEntity;
import dev.ftb.mods.ftbic.block.entity.storage.LVBatteryBoxBlockEntity;
import dev.ftb.mods.ftbic.block.entity.storage.LVTransformerBlockEntity;
import dev.ftb.mods.ftbic.block.entity.storage.MVBatteryBoxBlockEntity;
import dev.ftb.mods.ftbic.block.entity.storage.MVTransformerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public interface FTBICElectricBlocks {
	List<ElectricBlockInstance> ALL = new ArrayList<>();

	static ElectricBlockInstance register(String id, Supplier<BlockEntity> supplier) {
		ElectricBlockInstance instance = new ElectricBlockInstance(id, supplier);
		ALL.add(instance);
		return instance;
	}

	// Generators //

	ElectricBlockInstance BASIC_GENERATOR = register("basic_generator", BasicGeneratorBlockEntity::new)
			.energyOutput(FTBICConfig.BASIC_GENERATOR_OUTPUT);

	ElectricBlockInstance GEOTHERMAL_GENERATOR = register("geothermal_generator", GeothermalGeneratorBlockEntity::new)
			.energyOutput(FTBICConfig.GEOTHERMAL_GENERATOR_OUTPUT);

	ElectricBlockInstance WIND_MILL = register("wind_mill", WindMillBlockEntity::new)
			.cantBeActive()
			.maxEnergyOutput(FTBICConfig.WIND_MILL_MAX_OUTPUT);

	ElectricBlockInstance LV_SOLAR_PANEL = register("lv_solar_panel", LVSolarPanelBlockEntity::new)
			.name("LV Solar Panel")
			.noRotation()
			.cantBeActive()
			.maxEnergyOutput(FTBICConfig.LV_SOLAR_PANEL_OUTPUT);

	ElectricBlockInstance MV_SOLAR_PANEL = register("mv_solar_panel", MVSolarPanelBlockEntity::new)
			.name("MV Solar Panel")
			.noRotation()
			.cantBeActive()
			.maxEnergyOutput(FTBICConfig.MV_SOLAR_PANEL_OUTPUT);

	ElectricBlockInstance HV_SOLAR_PANEL = register("hv_solar_panel", HVSolarPanelBlockEntity::new)
			.advanced()
			.name("HV Solar Panel")
			.noRotation()
			.cantBeActive()
			.maxEnergyOutput(FTBICConfig.HV_SOLAR_PANEL_OUTPUT);

	ElectricBlockInstance EV_SOLAR_PANEL = register("ev_solar_panel", EVSolarPanelBlockEntity::new)
			.advanced()
			.name("EV Solar Panel")
			.noRotation()
			.cantBeActive()
			.maxEnergyOutput(FTBICConfig.EV_SOLAR_PANEL_OUTPUT);

	ElectricBlockInstance NUCLEAR_REACTOR = register("nuclear_reactor", NuclearReactorBlockEntity::new)
			.advanced();

	// Machines //

	ElectricBlockInstance POWERED_FURNACE = register("powered_furnace", PoweredFurnaceBlockEntity::new)
			.canBurn()
			.maxInput(FTBICConfig.LV_TRANSFER_RATE)
			.energyUsage(FTBICConfig.POWERED_FURNACE_USE);

	ElectricBlockInstance MACERATOR = register("macerator", MaceratorBlockEntity::new)
			.canBurn()
			.maxInput(FTBICConfig.LV_TRANSFER_RATE)
			.energyUsage(FTBICConfig.MACERATOR_USE);

	ElectricBlockInstance CENTRIFUGE = register("centrifuge", CentrifugeBlockEntity::new)
			.canBurn()
			.maxInput(FTBICConfig.LV_TRANSFER_RATE)
			.energyUsage(FTBICConfig.CENTRIFUGE_USE);

	ElectricBlockInstance COMPRESSOR = register("compressor", CompressorBlockEntity::new)
			.canBurn()
			.maxInput(FTBICConfig.LV_TRANSFER_RATE)
			.energyUsage(FTBICConfig.COMPRESSOR_USE);

	ElectricBlockInstance REPROCESSOR = register("reprocessor", ReprocessorBlockEntity::new)
			.advanced()
			.canBurn()
			.maxInput(FTBICConfig.MV_TRANSFER_RATE)
			.energyUsage(FTBICConfig.REPROCESSOR_USE);

	ElectricBlockInstance CANNING_MACHINE = register("canning_machine", CanningMachineBlockEntity::new)
			.canBurn()
			.maxInput(FTBICConfig.LV_TRANSFER_RATE)
			.energyUsage(FTBICConfig.CANNING_MACHINE_USE);

	ElectricBlockInstance ROLLER = register("roller", RollerBlockEntity::new)
			.canBurn()
			.maxInput(FTBICConfig.LV_TRANSFER_RATE)
			.energyUsage(FTBICConfig.ROLLER_USE);

	ElectricBlockInstance EXTRUDER = register("extruder", ExtruderBlockEntity::new)
			.canBurn()
			.maxInput(FTBICConfig.LV_TRANSFER_RATE)
			.energyUsage(FTBICConfig.EXTRUDER_USE);

	ElectricBlockInstance ANTIMATTER_CONSTRUCTOR = register("antimatter_constructor", AntimatterConstructorBlockEntity::new)
			.advanced()
			.maxInput(FTBICConfig.EV_TRANSFER_RATE)
			.energyUsage(FTBICConfig.ANTIMATTER_CONSTRUCTOR_USE);

	ElectricBlockInstance ADVANCED_POWERED_FURNACE = register("advanced_powered_furnace", AdvancedPoweredFurnaceBlockEntity::new)
			.advanced()
			.canBurn()
			.maxInput(FTBICConfig.MV_TRANSFER_RATE)
			.energyUsage(FTBICConfig.ADVANCED_POWERED_FURNACE_USE);

	ElectricBlockInstance ADVANCED_MACERATOR = register("advanced_macerator", AdvancedMaceratorBlockEntity::new)
			.advanced()
			.canBurn()
			.maxInput(FTBICConfig.MV_TRANSFER_RATE)
			.energyUsage(FTBICConfig.ADVANCED_MACERATOR_USE);

	ElectricBlockInstance ADVANCED_CENTRIFUGE = register("advanced_centrifuge", AdvancedCentrifugeBlockEntity::new)
			.advanced()
			.canBurn()
			.maxInput(FTBICConfig.MV_TRANSFER_RATE)
			.energyUsage(FTBICConfig.ADVANCED_CENTRIFUGE_USE);

	ElectricBlockInstance ADVANCED_COMPRESSOR = register("advanced_compressor", AdvancedCompressorBlockEntity::new)
			.advanced()
			.canBurn()
			.maxInput(FTBICConfig.MV_TRANSFER_RATE)
			.energyUsage(FTBICConfig.ADVANCED_COMPRESSOR_USE);


	// Battery Boxes //

	ElectricBlockInstance LV_BATTERY_BOX = register("lv_battery_box", LVBatteryBoxBlockEntity::new)
			.name("LV Battery Box")
			.rotate3D()
			.cantBeActive()
			.canBurn()
			.maxInput(FTBICConfig.LV_TRANSFER_RATE)
			.energyOutput(FTBICConfig.LV_TRANSFER_RATE);

	ElectricBlockInstance MV_BATTERY_BOX = register("mv_battery_box", MVBatteryBoxBlockEntity::new)
			.name("MV Battery Box")
			.rotate3D()
			.cantBeActive()
			.canBurn()
			.maxInput(FTBICConfig.MV_TRANSFER_RATE)
			.energyOutput(FTBICConfig.MV_TRANSFER_RATE);

	ElectricBlockInstance HV_BATTERY_BOX = register("hv_battery_box", HVBatteryBoxBlockEntity::new)
			.advanced()
			.name("HV Battery Box")
			.rotate3D()
			.cantBeActive()
			.canBurn()
			.maxInput(FTBICConfig.HV_TRANSFER_RATE)
			.energyOutput(FTBICConfig.HV_TRANSFER_RATE);

	// Transformers //

	ElectricBlockInstance LV_TRANSFORMER = register("lv_transformer", LVTransformerBlockEntity::new)
			.name("LV Transformer")
			.rotate3D()
			.cantBeActive()
			.canBurn()
			.maxInput(FTBICConfig.MV_TRANSFER_RATE)
			.energyOutput(FTBICConfig.LV_TRANSFER_RATE);

	ElectricBlockInstance MV_TRANSFORMER = register("mv_transformer", MVTransformerBlockEntity::new)
			.name("MV Transformer")
			.rotate3D()
			.cantBeActive()
			.canBurn()
			.maxInput(FTBICConfig.HV_TRANSFER_RATE)
			.energyOutput(FTBICConfig.MV_TRANSFER_RATE);

	ElectricBlockInstance HV_TRANSFORMER = register("hv_transformer", HVTransformerBlockEntity::new)
			.advanced()
			.name("HV Transformer")
			.rotate3D()
			.cantBeActive()
			.canBurn()
			.maxInput(FTBICConfig.EV_TRANSFER_RATE)
			.energyOutput(FTBICConfig.HV_TRANSFER_RATE);

	static void init() {
	}
}
