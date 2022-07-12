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
import dev.ftb.mods.ftbic.block.entity.machine.PumpBlockEntity;
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
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.ArrayList;
import java.util.List;

public interface FTBICElectricBlocks {
	List<ElectricBlockInstance> ALL = new ArrayList<>();

	static ElectricBlockInstance register(String id, BlockEntityType.BlockEntitySupplier<BlockEntity> supplier) {
		ElectricBlockInstance instance = new ElectricBlockInstance(id, supplier);
		ALL.add(instance);
		return instance;
	}

	// Generators //

	ElectricBlockInstance BASIC_GENERATOR = register("basic_generator", BasicGeneratorBlockEntity::new)
			.energyCapacity(FTBICConfig.getRaw(FTBICConfig.MACHINES.BASIC_GENERATOR_CAPACITY))
			.maxEnergyOutput(FTBICConfig.getRaw(FTBICConfig.MACHINES.BASIC_GENERATOR_OUTPUT))
			.io(1, 0);

	ElectricBlockInstance GEOTHERMAL_GENERATOR = register("geothermal_generator", GeothermalGeneratorBlockEntity::new)
			.energyCapacity(FTBICConfig.getRaw(FTBICConfig.MACHINES.GEOTHERMAL_GENERATOR_CAPACITY))
			.maxEnergyOutput(FTBICConfig.getRaw(FTBICConfig.MACHINES.GEOTHERMAL_GENERATOR_OUTPUT))
			.io(1, 1);

	ElectricBlockInstance WIND_MILL = register("wind_mill", WindMillBlockEntity::new)
			.cantBeActive()
			.energyCapacity(FTBICConfig.getRaw(FTBICConfig.MACHINES.WIND_MILL_CAPACITY))
			.maxEnergyOutput(FTBICConfig.getRaw(FTBICConfig.MACHINES.WIND_MILL_MAX_OUTPUT));

	ElectricBlockInstance LV_SOLAR_PANEL = register("lv_solar_panel", LVSolarPanelBlockEntity::new)
			.name("LV Solar Panel")
			.noRotation()
			.cantBeActive()
			.energyCapacity(FTBICConfig.getRaw(FTBICConfig.MACHINES.LV_SOLAR_PANEL_OUTPUT) * 60)
			.maxEnergyOutput(FTBICConfig.getRaw(FTBICConfig.MACHINES.LV_SOLAR_PANEL_OUTPUT));

	ElectricBlockInstance MV_SOLAR_PANEL = register("mv_solar_panel", MVSolarPanelBlockEntity::new)
			.name("MV Solar Panel")
			.noRotation()
			.cantBeActive()
			.energyCapacity(FTBICConfig.getRaw(FTBICConfig.MACHINES.MV_SOLAR_PANEL_OUTPUT) * 60)
			.maxEnergyOutput(FTBICConfig.getRaw(FTBICConfig.MACHINES.MV_SOLAR_PANEL_OUTPUT));

	ElectricBlockInstance HV_SOLAR_PANEL = register("hv_solar_panel", HVSolarPanelBlockEntity::new)
			.advanced()
			.name("HV Solar Panel")
			.noRotation()
			.cantBeActive()
			.energyCapacity(FTBICConfig.getRaw(FTBICConfig.MACHINES.HV_SOLAR_PANEL_OUTPUT) * 60)
			.maxEnergyOutput(FTBICConfig.getRaw(FTBICConfig.MACHINES.HV_SOLAR_PANEL_OUTPUT));

	ElectricBlockInstance EV_SOLAR_PANEL = register("ev_solar_panel", EVSolarPanelBlockEntity::new)
			.advanced()
			.name("EV Solar Panel")
			.noRotation()
			.cantBeActive()
			.energyCapacity(FTBICConfig.getRaw(FTBICConfig.MACHINES.EV_SOLAR_PANEL_OUTPUT) * 60)
			.maxEnergyOutput(FTBICConfig.getRaw(FTBICConfig.MACHINES.EV_SOLAR_PANEL_OUTPUT));

	ElectricBlockInstance NUCLEAR_REACTOR = register("nuclear_reactor", NuclearReactorBlockEntity::new)
			.advanced()
			.energyCapacity(FTBICConfig.getRaw(FTBICConfig.MACHINES.NUCLEAR_REACTOR_CAPACITY))
			.io(54, 0);

	// Machines //

	ElectricBlockInstance POWERED_FURNACE = register("powered_furnace", PoweredFurnaceBlockEntity::new)
			.canBurn()
			.energyCapacity(FTBICConfig.getRaw(FTBICConfig.MACHINES.POWERED_FURNACE_CAPACITY))
			.maxEnergyInput(FTBICConfig.getRaw(FTBICConfig.ENERGY.LV_TRANSFER_RATE))
			.energyUsage(FTBICConfig.getRaw(FTBICConfig.MACHINES.POWERED_FURNACE_USE))
			.io(1, 1);

	ElectricBlockInstance MACERATOR = register("macerator", MaceratorBlockEntity::new)
			.canBurn()
			.energyCapacity(FTBICConfig.getRaw(FTBICConfig.MACHINES.MACERATOR_CAPACITY))
			.maxEnergyInput(FTBICConfig.getRaw(FTBICConfig.ENERGY.LV_TRANSFER_RATE))
			.energyUsage(FTBICConfig.getRaw(FTBICConfig.MACHINES.MACERATOR_USE))
			.io(1, 2);

	ElectricBlockInstance CENTRIFUGE = register("centrifuge", CentrifugeBlockEntity::new)
			.canBurn()
			.energyCapacity(FTBICConfig.getRaw(FTBICConfig.MACHINES.CENTRIFUGE_CAPACITY))
			.maxEnergyInput(FTBICConfig.getRaw(FTBICConfig.ENERGY.LV_TRANSFER_RATE))
			.energyUsage(FTBICConfig.getRaw(FTBICConfig.MACHINES.CENTRIFUGE_USE))
			.io(1, 2);

	ElectricBlockInstance COMPRESSOR = register("compressor", CompressorBlockEntity::new)
			.canBurn()
			.energyCapacity(FTBICConfig.getRaw(FTBICConfig.MACHINES.COMPRESSOR_CAPACITY))
			.maxEnergyInput(FTBICConfig.getRaw(FTBICConfig.ENERGY.LV_TRANSFER_RATE))
			.energyUsage(FTBICConfig.getRaw(FTBICConfig.MACHINES.COMPRESSOR_USE))
			.io(1, 1);

	ElectricBlockInstance REPROCESSOR = register("reprocessor", ReprocessorBlockEntity::new)
			.advanced()
			.canBurn()
			.energyCapacity(FTBICConfig.getRaw(FTBICConfig.MACHINES.REPROCESSOR_CAPACITY))
			.maxEnergyInput(FTBICConfig.getRaw(FTBICConfig.ENERGY.MV_TRANSFER_RATE))
			.energyUsage(FTBICConfig.getRaw(FTBICConfig.MACHINES.REPROCESSOR_USE))
			.io(1, 1);

	ElectricBlockInstance CANNING_MACHINE = register("canning_machine", CanningMachineBlockEntity::new)
			.canBurn()
			.energyCapacity(FTBICConfig.getRaw(FTBICConfig.MACHINES.CANNING_MACHINE_CAPACITY))
			.maxEnergyInput(FTBICConfig.getRaw(FTBICConfig.ENERGY.LV_TRANSFER_RATE))
			.energyUsage(FTBICConfig.getRaw(FTBICConfig.MACHINES.CANNING_MACHINE_USE))
			.io(2, 1);

	ElectricBlockInstance ROLLER = register("roller", RollerBlockEntity::new)
			.canBurn()
			.energyCapacity(FTBICConfig.getRaw(FTBICConfig.MACHINES.ROLLER_CAPACITY))
			.maxEnergyInput(FTBICConfig.getRaw(FTBICConfig.ENERGY.LV_TRANSFER_RATE))
			.energyUsage(FTBICConfig.getRaw(FTBICConfig.MACHINES.ROLLER_USE))
			.io(1, 1);

	ElectricBlockInstance EXTRUDER = register("extruder", ExtruderBlockEntity::new)
			.canBurn()
			.energyCapacity(FTBICConfig.getRaw(FTBICConfig.MACHINES.EXTRUDER_CAPACITY))
			.maxEnergyInput(FTBICConfig.getRaw(FTBICConfig.ENERGY.LV_TRANSFER_RATE))
			.energyUsage(FTBICConfig.getRaw(FTBICConfig.MACHINES.EXTRUDER_USE))
			.io(1, 1);

	ElectricBlockInstance ANTIMATTER_CONSTRUCTOR = register("antimatter_constructor", AntimatterConstructorBlockEntity::new)
			.advanced()
			.energyCapacity(FTBICConfig.getRaw(FTBICConfig.MACHINES.ANTIMATTER_CONSTRUCTOR_CAPACITY))
			.maxEnergyInput(FTBICConfig.getRaw(FTBICConfig.ENERGY.IV_TRANSFER_RATE))
			.energyUsage(1D)
			.io(1, 1);

	ElectricBlockInstance ADVANCED_POWERED_FURNACE = register("advanced_powered_furnace", AdvancedPoweredFurnaceBlockEntity::new)
			.wip()
			.advanced()
			.canBurn()
			.energyCapacity(FTBICConfig.getRaw(FTBICConfig.MACHINES.ADVANCED_POWERED_FURNACE_CAPACITY))
			.maxEnergyInput(FTBICConfig.getRaw(FTBICConfig.ENERGY.MV_TRANSFER_RATE))
			.energyUsage(FTBICConfig.getRaw(FTBICConfig.MACHINES.ADVANCED_POWERED_FURNACE_USE))
			.io(1, 1);

	ElectricBlockInstance ADVANCED_MACERATOR = register("advanced_macerator", AdvancedMaceratorBlockEntity::new)
			.wip()
			.advanced()
			.canBurn()
			.energyCapacity(FTBICConfig.getRaw(FTBICConfig.MACHINES.ADVANCED_MACERATOR_CAPACITY))
			.maxEnergyInput(FTBICConfig.getRaw(FTBICConfig.ENERGY.MV_TRANSFER_RATE))
			.energyUsage(FTBICConfig.getRaw(FTBICConfig.MACHINES.ADVANCED_MACERATOR_USE))
			.io(1, 2);

	ElectricBlockInstance ADVANCED_CENTRIFUGE = register("advanced_centrifuge", AdvancedCentrifugeBlockEntity::new)
			.wip()
			.advanced()
			.canBurn()
			.energyCapacity(FTBICConfig.getRaw(FTBICConfig.MACHINES.ADVANCED_CENTRIFUGE_CAPACITY))
			.maxEnergyInput(FTBICConfig.getRaw(FTBICConfig.ENERGY.MV_TRANSFER_RATE))
			.energyUsage(FTBICConfig.getRaw(FTBICConfig.MACHINES.ADVANCED_CENTRIFUGE_USE))
			.io(1, 2);

	ElectricBlockInstance ADVANCED_COMPRESSOR = register("advanced_compressor", AdvancedCompressorBlockEntity::new)
			.wip()
			.advanced()
			.canBurn()
			.energyCapacity(FTBICConfig.getRaw(FTBICConfig.MACHINES.ADVANCED_COMPRESSOR_CAPACITY))
			.maxEnergyInput(FTBICConfig.getRaw(FTBICConfig.ENERGY.MV_TRANSFER_RATE))
			.energyUsage(FTBICConfig.getRaw(FTBICConfig.MACHINES.ADVANCED_COMPRESSOR_USE))
			.io(1, 1);

	ElectricBlockInstance TELEPORTER = register("teleporter", TeleporterBlockEntity::new)
			.wip()
			.advanced()
			.energyCapacity(FTBICConfig.getRaw(FTBICConfig.MACHINES.TELEPORTER_CAPACITY))
			.maxEnergyInput(FTBICConfig.getRaw(FTBICConfig.ENERGY.IV_TRANSFER_RATE))
			.energyUsage(FTBICConfig.getRaw(FTBICConfig.MACHINES.TELEPORTER_MAX_USE))
			.energyUsageIsntPerTick();

	ElectricBlockInstance CHARGE_PAD = register("charge_pad", ChargePadBlockEntity::new)
			.advanced()
			.noRotation()
			.energyCapacity(FTBICConfig.getRaw(FTBICConfig.MACHINES.CHARGE_PAD_CAPACITY))
			.maxEnergyInput(FTBICConfig.getRaw(FTBICConfig.ENERGY.IV_TRANSFER_RATE));

	ElectricBlockInstance POWERED_CRAFTING_TABLE = register("powered_crafting_table", PoweredCraftingTableBlockEntity::new)
			.wip()
			.noRotation()
			.cantBeActive()
			.canBurn()
			.energyCapacity(FTBICConfig.getRaw(FTBICConfig.MACHINES.POWERED_CRAFTING_TABLE_CAPACITY))
			.maxEnergyInput(FTBICConfig.getRaw(FTBICConfig.ENERGY.LV_TRANSFER_RATE))
			.energyUsage(FTBICConfig.getRaw(FTBICConfig.MACHINES.POWERED_CRAFTING_TABLE_USE))
			.io(9, 1);

	ElectricBlockInstance QUARRY = register("quarry", QuarryBlockEntity::new)
			.advanced()
			.energyCapacity(FTBICConfig.getRaw(FTBICConfig.MACHINES.QUARRY_CAPACITY))
			.maxEnergyInput(FTBICConfig.getRaw(FTBICConfig.ENERGY.HV_TRANSFER_RATE))
			.energyUsage(FTBICConfig.getRaw(FTBICConfig.MACHINES.QUARRY_USE))
			.io(0, 18)
			.tickClientSide();

	ElectricBlockInstance PUMP = register("pump", PumpBlockEntity::new)
			.advanced()
			.energyCapacity(FTBICConfig.getRaw(FTBICConfig.MACHINES.PUMP_CAPACITY))
			.maxEnergyInput(FTBICConfig.getRaw(FTBICConfig.ENERGY.HV_TRANSFER_RATE))
			.energyUsage(FTBICConfig.getRaw(FTBICConfig.MACHINES.PUMP_USE))
			.io(1, 1)
			.tickClientSide();

	// Battery Boxes //

	ElectricBlockInstance LV_BATTERY_BOX = register("lv_battery_box", LVBatteryBoxBlockEntity::new)
			.name("LV Battery Box")
			.rotate3D()
			.cantBeActive()
			.canBurn()
			.energyCapacity(FTBICConfig.getRaw(FTBICConfig.ENERGY.LV_BATTERY_BOX_CAPACITY))
			.maxEnergyInput(FTBICConfig.getRaw(FTBICConfig.ENERGY.LV_TRANSFER_RATE))
			.maxEnergyOutput(FTBICConfig.getRaw(FTBICConfig.ENERGY.LV_TRANSFER_RATE));

	ElectricBlockInstance MV_BATTERY_BOX = register("mv_battery_box", MVBatteryBoxBlockEntity::new)
			.name("MV Battery Box")
			.rotate3D()
			.cantBeActive()
			.canBurn()
			.energyCapacity(FTBICConfig.getRaw(FTBICConfig.ENERGY.MV_BATTERY_BOX_CAPACITY))
			.maxEnergyInput(FTBICConfig.getRaw(FTBICConfig.ENERGY.MV_TRANSFER_RATE))
			.maxEnergyOutput(FTBICConfig.getRaw(FTBICConfig.ENERGY.MV_TRANSFER_RATE));

	ElectricBlockInstance HV_BATTERY_BOX = register("hv_battery_box", HVBatteryBoxBlockEntity::new)
			.advanced()
			.name("HV Battery Box")
			.rotate3D()
			.cantBeActive()
			.canBurn()
			.energyCapacity(FTBICConfig.getRaw(FTBICConfig.ENERGY.HV_BATTERY_BOX_CAPACITY))
			.maxEnergyInput(FTBICConfig.getRaw(FTBICConfig.ENERGY.HV_TRANSFER_RATE))
			.maxEnergyOutput(FTBICConfig.getRaw(FTBICConfig.ENERGY.HV_TRANSFER_RATE));

	ElectricBlockInstance EV_BATTERY_BOX = register("ev_battery_box", EVBatteryBoxBlockEntity::new)
			.advanced()
			.name("EV Battery Box")
			.rotate3D()
			.cantBeActive()
			.canBurn()
			.energyCapacity(FTBICConfig.getRaw(FTBICConfig.ENERGY.EV_BATTERY_BOX_CAPACITY))
			.maxEnergyInput(FTBICConfig.getRaw(FTBICConfig.ENERGY.EV_TRANSFER_RATE))
			.maxEnergyOutput(FTBICConfig.getRaw(FTBICConfig.ENERGY.EV_TRANSFER_RATE));

	// Transformers //

	ElectricBlockInstance LV_TRANSFORMER = register("lv_transformer", LVTransformerBlockEntity::new)
			.name("LV Transformer")
			.rotate3D()
			.cantBeActive()
			.canBurn()
			.energyCapacity(FTBICConfig.getRaw(FTBICConfig.ENERGY.MV_TRANSFER_RATE))
			.maxEnergyInput(FTBICConfig.getRaw(FTBICConfig.ENERGY.MV_TRANSFER_RATE))
			.maxEnergyOutput(FTBICConfig.getRaw(FTBICConfig.ENERGY.LV_TRANSFER_RATE));

	ElectricBlockInstance MV_TRANSFORMER = register("mv_transformer", MVTransformerBlockEntity::new)
			.name("MV Transformer")
			.rotate3D()
			.cantBeActive()
			.canBurn()
			.energyCapacity(FTBICConfig.getRaw(FTBICConfig.ENERGY.HV_TRANSFER_RATE))
			.maxEnergyInput(FTBICConfig.getRaw(FTBICConfig.ENERGY.HV_TRANSFER_RATE))
			.maxEnergyOutput(FTBICConfig.getRaw(FTBICConfig.ENERGY.MV_TRANSFER_RATE));

	ElectricBlockInstance HV_TRANSFORMER = register("hv_transformer", HVTransformerBlockEntity::new)
			.advanced()
			.name("HV Transformer")
			.rotate3D()
			.cantBeActive()
			.canBurn()
			.energyCapacity(FTBICConfig.getRaw(FTBICConfig.ENERGY.EV_TRANSFER_RATE))
			.maxEnergyInput(FTBICConfig.getRaw(FTBICConfig.ENERGY.EV_TRANSFER_RATE))
			.maxEnergyOutput(FTBICConfig.getRaw(FTBICConfig.ENERGY.HV_TRANSFER_RATE));

	ElectricBlockInstance EV_TRANSFORMER = register("ev_transformer", EVTransformerBlockEntity::new)
			.advanced()
			.name("EV Transformer")
			.rotate3D()
			.cantBeActive()
			.canBurn()
			.energyCapacity(FTBICConfig.getRaw(FTBICConfig.ENERGY.IV_TRANSFER_RATE))
			.maxEnergyInput(FTBICConfig.getRaw(FTBICConfig.ENERGY.IV_TRANSFER_RATE))
			.maxEnergyOutput(FTBICConfig.getRaw(FTBICConfig.ENERGY.EV_TRANSFER_RATE));

	static void init() {
	}
}
