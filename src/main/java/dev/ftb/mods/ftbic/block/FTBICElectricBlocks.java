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
import dev.ftb.mods.ftbic.block.entity.machine.ChargePadBlockEntity;
import dev.ftb.mods.ftbic.block.entity.machine.CompressorBlockEntity;
import dev.ftb.mods.ftbic.block.entity.machine.ExtruderBlockEntity;
import dev.ftb.mods.ftbic.block.entity.machine.MaceratorBlockEntity;
import dev.ftb.mods.ftbic.block.entity.machine.PoweredCraftingTableBlockEntity;
import dev.ftb.mods.ftbic.block.entity.machine.PoweredFurnaceBlockEntity;
import dev.ftb.mods.ftbic.block.entity.machine.QuarryBlockEntity;
import dev.ftb.mods.ftbic.block.entity.machine.ReprocessorBlockEntity;
import dev.ftb.mods.ftbic.block.entity.machine.RollerBlockEntity;
import dev.ftb.mods.ftbic.block.entity.machine.TeleporterBlockEntity;
import dev.ftb.mods.ftbic.block.entity.storage.EVBatteryBoxBlockEntity;
import dev.ftb.mods.ftbic.block.entity.storage.EVTransformerBlockEntity;
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
			.energyCapacity(FTBICConfig.BASIC_GENERATOR_CAPACITY)
			.maxEnergyOutput(FTBICConfig.BASIC_GENERATOR_OUTPUT)
			.io(1, 0);

	ElectricBlockInstance GEOTHERMAL_GENERATOR = register("geothermal_generator", GeothermalGeneratorBlockEntity::new)
			.energyCapacity(FTBICConfig.GEOTHERMAL_GENERATOR_CAPACITY)
			.maxEnergyOutput(FTBICConfig.GEOTHERMAL_GENERATOR_OUTPUT)
			.io(1, 1);

	ElectricBlockInstance WIND_MILL = register("wind_mill", WindMillBlockEntity::new)
			.cantBeActive()
			.energyCapacity(FTBICConfig.WIND_MILL_CAPACITY)
			.maxEnergyOutput(FTBICConfig.WIND_MILL_MAX_OUTPUT);

	ElectricBlockInstance LV_SOLAR_PANEL = register("lv_solar_panel", LVSolarPanelBlockEntity::new)
			.name("LV Solar Panel")
			.noRotation()
			.cantBeActive()
			.energyCapacity(FTBICConfig.LV_SOLAR_PANEL_OUTPUT * 60)
			.maxEnergyOutput(FTBICConfig.LV_SOLAR_PANEL_OUTPUT);

	ElectricBlockInstance MV_SOLAR_PANEL = register("mv_solar_panel", MVSolarPanelBlockEntity::new)
			.name("MV Solar Panel")
			.noRotation()
			.cantBeActive()
			.energyCapacity(FTBICConfig.MV_SOLAR_PANEL_OUTPUT * 60)
			.maxEnergyOutput(FTBICConfig.MV_SOLAR_PANEL_OUTPUT);

	ElectricBlockInstance HV_SOLAR_PANEL = register("hv_solar_panel", HVSolarPanelBlockEntity::new)
			.advanced()
			.name("HV Solar Panel")
			.noRotation()
			.cantBeActive()
			.energyCapacity(FTBICConfig.HV_SOLAR_PANEL_OUTPUT * 60)
			.maxEnergyOutput(FTBICConfig.HV_SOLAR_PANEL_OUTPUT);

	ElectricBlockInstance EV_SOLAR_PANEL = register("ev_solar_panel", EVSolarPanelBlockEntity::new)
			.advanced()
			.name("EV Solar Panel")
			.noRotation()
			.cantBeActive()
			.energyCapacity(FTBICConfig.EV_SOLAR_PANEL_OUTPUT * 60)
			.maxEnergyOutput(FTBICConfig.EV_SOLAR_PANEL_OUTPUT);

	ElectricBlockInstance NUCLEAR_REACTOR = register("nuclear_reactor", NuclearReactorBlockEntity::new)
			.wip()
			.advanced()
			.energyCapacity(FTBICConfig.NUCLEAR_REACTOR_CAPACITY)
			.io(54, 0);

	// Machines //

	ElectricBlockInstance POWERED_FURNACE = register("powered_furnace", PoweredFurnaceBlockEntity::new)
			.canBurn()
			.energyCapacity(FTBICConfig.POWERED_FURNACE_CAPACITY)
			.maxEnergyInput(FTBICConfig.LV_TRANSFER_RATE)
			.energyUsage(FTBICConfig.POWERED_FURNACE_USE)
			.io(1, 1);

	ElectricBlockInstance MACERATOR = register("macerator", MaceratorBlockEntity::new)
			.canBurn()
			.energyCapacity(FTBICConfig.MACERATOR_CAPACITY)
			.maxEnergyInput(FTBICConfig.LV_TRANSFER_RATE)
			.energyUsage(FTBICConfig.MACERATOR_USE)
			.io(1, 2);

	ElectricBlockInstance CENTRIFUGE = register("centrifuge", CentrifugeBlockEntity::new)
			.canBurn()
			.energyCapacity(FTBICConfig.CENTRIFUGE_CAPACITY)
			.maxEnergyInput(FTBICConfig.LV_TRANSFER_RATE)
			.energyUsage(FTBICConfig.CENTRIFUGE_USE)
			.io(1, 2);

	ElectricBlockInstance COMPRESSOR = register("compressor", CompressorBlockEntity::new)
			.canBurn()
			.energyCapacity(FTBICConfig.COMPRESSOR_CAPACITY)
			.maxEnergyInput(FTBICConfig.LV_TRANSFER_RATE)
			.energyUsage(FTBICConfig.COMPRESSOR_USE)
			.io(1, 1);

	ElectricBlockInstance REPROCESSOR = register("reprocessor", ReprocessorBlockEntity::new)
			.advanced()
			.canBurn()
			.energyCapacity(FTBICConfig.REPROCESSOR_CAPACITY)
			.maxEnergyInput(FTBICConfig.MV_TRANSFER_RATE)
			.energyUsage(FTBICConfig.REPROCESSOR_USE)
			.io(1, 1);

	ElectricBlockInstance CANNING_MACHINE = register("canning_machine", CanningMachineBlockEntity::new)
			.canBurn()
			.energyCapacity(FTBICConfig.CANNING_MACHINE_CAPACITY)
			.maxEnergyInput(FTBICConfig.LV_TRANSFER_RATE)
			.energyUsage(FTBICConfig.CANNING_MACHINE_USE)
			.io(2, 1);

	ElectricBlockInstance ROLLER = register("roller", RollerBlockEntity::new)
			.canBurn()
			.energyCapacity(FTBICConfig.ROLLER_CAPACITY)
			.maxEnergyInput(FTBICConfig.LV_TRANSFER_RATE)
			.energyUsage(FTBICConfig.ROLLER_USE)
			.io(1, 1);

	ElectricBlockInstance EXTRUDER = register("extruder", ExtruderBlockEntity::new)
			.canBurn()
			.energyCapacity(FTBICConfig.EXTRUDER_CAPACITY)
			.maxEnergyInput(FTBICConfig.LV_TRANSFER_RATE)
			.energyUsage(FTBICConfig.EXTRUDER_USE)
			.io(1, 1);

	ElectricBlockInstance ANTIMATTER_CONSTRUCTOR = register("antimatter_constructor", AntimatterConstructorBlockEntity::new)
			.advanced()
			.energyCapacity(FTBICConfig.ANTIMATTER_CONSTRUCTOR_CAPACITY)
			.maxEnergyInput(FTBICConfig.IV_TRANSFER_RATE)
			.energyUsage(1D)
			.io(1, 1);

	ElectricBlockInstance ADVANCED_POWERED_FURNACE = register("advanced_powered_furnace", AdvancedPoweredFurnaceBlockEntity::new)
			.wip()
			.advanced()
			.canBurn()
			.energyCapacity(FTBICConfig.ADVANCED_POWERED_FURNACE_CAPACITY)
			.maxEnergyInput(FTBICConfig.MV_TRANSFER_RATE)
			.energyUsage(FTBICConfig.ADVANCED_POWERED_FURNACE_USE)
			.io(1, 1);

	ElectricBlockInstance ADVANCED_MACERATOR = register("advanced_macerator", AdvancedMaceratorBlockEntity::new)
			.wip()
			.advanced()
			.canBurn()
			.energyCapacity(FTBICConfig.ADVANCED_MACERATOR_CAPACITY)
			.maxEnergyInput(FTBICConfig.MV_TRANSFER_RATE)
			.energyUsage(FTBICConfig.ADVANCED_MACERATOR_USE)
			.io(1, 2);

	ElectricBlockInstance ADVANCED_CENTRIFUGE = register("advanced_centrifuge", AdvancedCentrifugeBlockEntity::new)
			.wip()
			.advanced()
			.canBurn()
			.energyCapacity(FTBICConfig.ADVANCED_CENTRIFUGE_CAPACITY)
			.maxEnergyInput(FTBICConfig.MV_TRANSFER_RATE)
			.energyUsage(FTBICConfig.ADVANCED_CENTRIFUGE_USE)
			.io(1, 2);

	ElectricBlockInstance ADVANCED_COMPRESSOR = register("advanced_compressor", AdvancedCompressorBlockEntity::new)
			.wip()
			.advanced()
			.canBurn()
			.energyCapacity(FTBICConfig.ADVANCED_COMPRESSOR_CAPACITY)
			.maxEnergyInput(FTBICConfig.MV_TRANSFER_RATE)
			.energyUsage(FTBICConfig.ADVANCED_COMPRESSOR_USE)
			.io(1, 1);

	ElectricBlockInstance TELEPORTER = register("teleporter", TeleporterBlockEntity::new)
			.advanced()
			.energyCapacity(FTBICConfig.TELEPORTER_CAPACITY)
			.maxEnergyInput(FTBICConfig.IV_TRANSFER_RATE)
			.energyUsage(FTBICConfig.TELEPORTER_USE)
			.energyUsageIsntPerTick();

	ElectricBlockInstance CHARGE_PAD = register("charge_pad", ChargePadBlockEntity::new)
			.advanced()
			.noRotation()
			.energyCapacity(FTBICConfig.CHARGE_PAD_CAPACITY)
			.maxEnergyInput(FTBICConfig.IV_TRANSFER_RATE);

	ElectricBlockInstance POWERED_CRAFTING_TABLE = register("powered_crafting_table", PoweredCraftingTableBlockEntity::new)
			.wip()
			.noRotation()
			.cantBeActive()
			.canBurn()
			.energyCapacity(FTBICConfig.POWERED_CRAFTING_TABLE_CAPACITY)
			.maxEnergyInput(FTBICConfig.LV_TRANSFER_RATE)
			.energyUsage(FTBICConfig.POWERED_CRAFTING_TABLE_USE)
			.io(9, 1);

	ElectricBlockInstance QUARRY = register("quarry", QuarryBlockEntity::new)
			.wip()
			.advanced()
			.energyCapacity(FTBICConfig.QUARRY_CAPACITY)
			.maxEnergyInput(FTBICConfig.HV_TRANSFER_RATE)
			.energyUsage(FTBICConfig.QUARRY_USE)
			.io(0, 18);

	// Battery Boxes //

	ElectricBlockInstance LV_BATTERY_BOX = register("lv_battery_box", LVBatteryBoxBlockEntity::new)
			.name("LV Battery Box")
			.rotate3D()
			.cantBeActive()
			.canBurn()
			.energyCapacity(FTBICConfig.LV_BATTERY_BOX_CAPACITY)
			.maxEnergyInput(FTBICConfig.LV_TRANSFER_RATE)
			.maxEnergyOutput(FTBICConfig.LV_TRANSFER_RATE);

	ElectricBlockInstance MV_BATTERY_BOX = register("mv_battery_box", MVBatteryBoxBlockEntity::new)
			.name("MV Battery Box")
			.rotate3D()
			.cantBeActive()
			.canBurn()
			.energyCapacity(FTBICConfig.MV_BATTERY_BOX_CAPACITY)
			.maxEnergyInput(FTBICConfig.MV_TRANSFER_RATE)
			.maxEnergyOutput(FTBICConfig.MV_TRANSFER_RATE);

	ElectricBlockInstance HV_BATTERY_BOX = register("hv_battery_box", HVBatteryBoxBlockEntity::new)
			.advanced()
			.name("HV Battery Box")
			.rotate3D()
			.cantBeActive()
			.canBurn()
			.energyCapacity(FTBICConfig.HV_BATTERY_BOX_CAPACITY)
			.maxEnergyInput(FTBICConfig.HV_TRANSFER_RATE)
			.maxEnergyOutput(FTBICConfig.HV_TRANSFER_RATE);

	ElectricBlockInstance EV_BATTERY_BOX = register("ev_battery_box", EVBatteryBoxBlockEntity::new)
			.advanced()
			.name("EV Battery Box")
			.rotate3D()
			.cantBeActive()
			.canBurn()
			.energyCapacity(FTBICConfig.EV_BATTERY_BOX_CAPACITY)
			.maxEnergyInput(FTBICConfig.EV_TRANSFER_RATE)
			.maxEnergyOutput(FTBICConfig.EV_TRANSFER_RATE);

	// Transformers //

	ElectricBlockInstance LV_TRANSFORMER = register("lv_transformer", LVTransformerBlockEntity::new)
			.name("LV Transformer")
			.rotate3D()
			.cantBeActive()
			.canBurn()
			.energyCapacity(FTBICConfig.MV_TRANSFER_RATE)
			.maxEnergyInput(FTBICConfig.MV_TRANSFER_RATE)
			.maxEnergyOutput(FTBICConfig.LV_TRANSFER_RATE);

	ElectricBlockInstance MV_TRANSFORMER = register("mv_transformer", MVTransformerBlockEntity::new)
			.name("MV Transformer")
			.rotate3D()
			.cantBeActive()
			.canBurn()
			.energyCapacity(FTBICConfig.HV_TRANSFER_RATE)
			.maxEnergyInput(FTBICConfig.HV_TRANSFER_RATE)
			.maxEnergyOutput(FTBICConfig.MV_TRANSFER_RATE);

	ElectricBlockInstance HV_TRANSFORMER = register("hv_transformer", HVTransformerBlockEntity::new)
			.advanced()
			.name("HV Transformer")
			.rotate3D()
			.cantBeActive()
			.canBurn()
			.energyCapacity(FTBICConfig.EV_TRANSFER_RATE)
			.maxEnergyInput(FTBICConfig.EV_TRANSFER_RATE)
			.maxEnergyOutput(FTBICConfig.HV_TRANSFER_RATE);

	ElectricBlockInstance EV_TRANSFORMER = register("ev_transformer", EVTransformerBlockEntity::new)
			.advanced()
			.name("EV Transformer")
			.rotate3D()
			.cantBeActive()
			.canBurn()
			.energyCapacity(FTBICConfig.IV_TRANSFER_RATE)
			.maxEnergyInput(FTBICConfig.IV_TRANSFER_RATE)
			.maxEnergyOutput(FTBICConfig.EV_TRANSFER_RATE);

	static void init() {
	}
}
