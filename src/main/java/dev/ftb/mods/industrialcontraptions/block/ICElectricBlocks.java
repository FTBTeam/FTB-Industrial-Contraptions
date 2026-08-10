package dev.ftb.mods.industrialcontraptions.block;

import dev.ftb.mods.industrialcontraptions.ICConfig;
import dev.ftb.mods.industrialcontraptions.block.entity.generator.*;
import dev.ftb.mods.industrialcontraptions.block.entity.machine.*;
import dev.ftb.mods.industrialcontraptions.block.entity.storage.*;
import dev.ftb.mods.industrialcontraptions.item.reactor.NuclearReactor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.ArrayList;
import java.util.List;

public interface ICElectricBlocks {
	List<ElectricBlockInstance> ALL = new ArrayList<>();

	static ElectricBlockInstance register(String id, BlockEntityType.BlockEntitySupplier<BlockEntity> supplier) {
		ElectricBlockInstance instance = new ElectricBlockInstance(id, supplier);
		ALL.add(instance);
		return instance;
	}

	// Generators //

	ElectricBlockInstance BASIC_GENERATOR = register("basic_generator", BasicGeneratorBlockEntity::new)
			.energyCapacity(ICConfig.MACHINES.BASIC_GENERATOR_CAPACITY)
			.maxEnergyOutput(ICConfig.MACHINES.BASIC_GENERATOR_OUTPUT)
			.feMode(ElectricBlockInstance.FECapMode.EXTRACT_ONLY)
			.io(1, 0);

	ElectricBlockInstance GEOTHERMAL_GENERATOR = register("geothermal_generator", GeothermalGeneratorBlockEntity::new)
			.energyCapacity(ICConfig.MACHINES.GEOTHERMAL_GENERATOR_CAPACITY)
			.maxEnergyOutput(ICConfig.MACHINES.GEOTHERMAL_GENERATOR_OUTPUT)
			.feMode(ElectricBlockInstance.FECapMode.EXTRACT_ONLY)
			.io(1, 1);

	ElectricBlockInstance WIND_MILL = register("wind_mill", WindMillBlockEntity::new)
			.cantBeActive()
			.energyCapacity(ICConfig.MACHINES.WIND_MILL_CAPACITY)
			.maxEnergyOutput(ICConfig.MACHINES.WIND_MILL_MAX_OUTPUT)
			.feMode(ElectricBlockInstance.FECapMode.EXTRACT_ONLY);

	ElectricBlockInstance LV_SOLAR_PANEL = register("lv_solar_panel", LVSolarPanelBlockEntity::new)
			.name("LV Solar Panel")
			.noRotation()
			.cantBeActive()
			.energyCapacity(() -> ICConfig.MACHINES.LV_SOLAR_PANEL_CAPACITY.get() * 60)
			.maxEnergyOutput(ICConfig.MACHINES.LV_SOLAR_PANEL_OUTPUT)
			.feMode(ElectricBlockInstance.FECapMode.EXTRACT_ONLY);

	ElectricBlockInstance MV_SOLAR_PANEL = register("mv_solar_panel", MVSolarPanelBlockEntity::new)
			.name("MV Solar Panel")
			.noRotation()
			.cantBeActive()
			.energyCapacity(() -> ICConfig.MACHINES.MV_SOLAR_PANEL_CAPACITY.get() * 60)
			.maxEnergyOutput(ICConfig.MACHINES.MV_SOLAR_PANEL_OUTPUT)
			.feMode(ElectricBlockInstance.FECapMode.EXTRACT_ONLY);

	ElectricBlockInstance HV_SOLAR_PANEL = register("hv_solar_panel", HVSolarPanelBlockEntity::new)
			.advanced()
			.name("HV Solar Panel")
			.noRotation()
			.cantBeActive()
			.energyCapacity(() -> ICConfig.MACHINES.HV_SOLAR_PANEL_CAPACITY.get() * 60)
			.maxEnergyOutput(ICConfig.MACHINES.HV_SOLAR_PANEL_OUTPUT)
			.feMode(ElectricBlockInstance.FECapMode.EXTRACT_ONLY);

	ElectricBlockInstance EV_SOLAR_PANEL = register("ev_solar_panel", EVSolarPanelBlockEntity::new)
			.advanced()
			.name("EV Solar Panel")
			.noRotation()
			.cantBeActive()
			.energyCapacity(() -> ICConfig.MACHINES.EV_SOLAR_PANEL_CAPACITY.get() * 60)
			.maxEnergyOutput(ICConfig.MACHINES.EV_SOLAR_PANEL_OUTPUT)
			.feMode(ElectricBlockInstance.FECapMode.EXTRACT_ONLY);

	ElectricBlockInstance NUCLEAR_REACTOR = register("nuclear_reactor", NuclearReactorBlockEntity::new)
			.advanced()
			.energyCapacity(ICConfig.MACHINES.NUCLEAR_REACTOR_CAPACITY)
			.feMode(ElectricBlockInstance.FECapMode.EXTRACT_ONLY)
			.io(NuclearReactor.MAX_SLOTS, 0);

	ElectricBlockInstance REACTOR_SIMULATOR = register("reactor_simulator", ReactorSimulatorBlockEntity::new)
			.advanced()
			.noRotation()
			.cantBeActive()
			.io(NuclearReactor.MAX_SLOTS, 0);

	// Machines //

	ElectricBlockInstance POWERED_FURNACE = register("powered_furnace", PoweredFurnaceBlockEntity::new)
			.canBurn()
			.energyCapacity(ICConfig.MACHINES.POWERED_FURNACE_CAPACITY)
			.maxEnergyInput(ICConfig.ENERGY.LV_TRANSFER_RATE)
			.energyUsage(ICConfig.MACHINES.POWERED_FURNACE_USE)
			.io(1, 1);

	ElectricBlockInstance MACERATOR = register("macerator", MaceratorBlockEntity::new)
			.canBurn()
			.energyCapacity(ICConfig.MACHINES.MACERATOR_CAPACITY)
			.maxEnergyInput(ICConfig.ENERGY.LV_TRANSFER_RATE)
			.energyUsage(ICConfig.MACHINES.MACERATOR_USE)
			.io(1, 2);

	ElectricBlockInstance CENTRIFUGE = register("centrifuge", CentrifugeBlockEntity::new)
			.canBurn()
			.energyCapacity(ICConfig.MACHINES.CENTRIFUGE_CAPACITY)
			.maxEnergyInput(ICConfig.ENERGY.LV_TRANSFER_RATE)
			.energyUsage(ICConfig.MACHINES.CENTRIFUGE_USE)
			.io(1, 2);

	ElectricBlockInstance COMPRESSOR = register("compressor", CompressorBlockEntity::new)
			.canBurn()
			.energyCapacity(ICConfig.MACHINES.COMPRESSOR_CAPACITY)
			.maxEnergyInput(ICConfig.ENERGY.LV_TRANSFER_RATE)
			.energyUsage(ICConfig.MACHINES.COMPRESSOR_USE)
			.io(1, 1);

	ElectricBlockInstance REPROCESSOR = register("reprocessor", ReprocessorBlockEntity::new)
			.advanced()
			.canBurn()
			.energyCapacity(ICConfig.MACHINES.REPROCESSOR_CAPACITY)
			.maxEnergyInput(ICConfig.ENERGY.MV_TRANSFER_RATE)
			.energyUsage(ICConfig.MACHINES.REPROCESSOR_USE)
			.io(1, 1);

	ElectricBlockInstance CANNING_MACHINE = register("canning_machine", CanningMachineBlockEntity::new)
			.canBurn()
			.energyCapacity(ICConfig.MACHINES.CANNING_MACHINE_CAPACITY)
			.maxEnergyInput(ICConfig.ENERGY.LV_TRANSFER_RATE)
			.energyUsage(ICConfig.MACHINES.CANNING_MACHINE_USE)
			.io(2, 1);

	ElectricBlockInstance ROLLER = register("roller", RollerBlockEntity::new)
			.canBurn()
			.energyCapacity(ICConfig.MACHINES.ROLLER_CAPACITY)
			.maxEnergyInput(ICConfig.ENERGY.LV_TRANSFER_RATE)
			.energyUsage(ICConfig.MACHINES.ROLLER_USE)
			.io(1, 1);

	ElectricBlockInstance EXTRUDER = register("extruder", ExtruderBlockEntity::new)
			.canBurn()
			.energyCapacity(ICConfig.MACHINES.EXTRUDER_CAPACITY)
			.maxEnergyInput(ICConfig.ENERGY.LV_TRANSFER_RATE)
			.energyUsage(ICConfig.MACHINES.EXTRUDER_USE)
			.io(1, 1);

	ElectricBlockInstance ANTIMATTER_CONSTRUCTOR = register("antimatter_constructor", AntimatterConstructorBlockEntity::new)
			.advanced()
			.energyCapacity(ICConfig.MACHINES.ANTIMATTER_CONSTRUCTOR_CAPACITY)
			.maxEnergyInput(ICConfig.ENERGY.IV_TRANSFER_RATE)
			.energyUsage(1D)
			.io(1, 1);

	ElectricBlockInstance ADVANCED_POWERED_FURNACE = register("advanced_powered_furnace", AdvancedPoweredFurnaceBlockEntity::new)
			.wip()
			.advanced()
			.canBurn()
			.energyCapacity(ICConfig.MACHINES.ADVANCED_POWERED_FURNACE_CAPACITY)
			.maxEnergyInput(ICConfig.ENERGY.MV_TRANSFER_RATE)
			.energyUsage(ICConfig.MACHINES.ADVANCED_POWERED_FURNACE_USE)
			.io(1, 1);

	ElectricBlockInstance ALLOY_SMELTER = register("alloy_smelter", AlloySmelterBlockEntity::new)
			.advanced()
			.canBurn()
			.energyCapacity(ICConfig.MACHINES.ALLOY_SMELTER_CAPACITY)
			.maxEnergyInput(ICConfig.ENERGY.MV_TRANSFER_RATE)
			.energyUsage(ICConfig.MACHINES.ALLOY_SMELTER_USE)
			.io(3, 1);

	ElectricBlockInstance ADVANCED_MACERATOR = register("advanced_macerator", AdvancedMaceratorBlockEntity::new)
			.wip()
			.advanced()
			.canBurn()
			.energyCapacity(ICConfig.MACHINES.ADVANCED_MACERATOR_CAPACITY)
			.maxEnergyInput(ICConfig.ENERGY.MV_TRANSFER_RATE)
			.energyUsage(ICConfig.MACHINES.ADVANCED_MACERATOR_USE)
			.io(1, 2);

	ElectricBlockInstance ADVANCED_CENTRIFUGE = register("advanced_centrifuge", AdvancedCentrifugeBlockEntity::new)
			.wip()
			.advanced()
			.canBurn()
			.energyCapacity(ICConfig.MACHINES.ADVANCED_CENTRIFUGE_CAPACITY)
			.maxEnergyInput(ICConfig.ENERGY.MV_TRANSFER_RATE)
			.energyUsage(ICConfig.MACHINES.ADVANCED_CENTRIFUGE_USE)
			.io(1, 2);

	ElectricBlockInstance ADVANCED_COMPRESSOR = register("advanced_compressor", AdvancedCompressorBlockEntity::new)
			.wip()
			.advanced()
			.canBurn()
			.energyCapacity(ICConfig.MACHINES.ADVANCED_COMPRESSOR_CAPACITY)
			.maxEnergyInput(ICConfig.ENERGY.MV_TRANSFER_RATE)
			.energyUsage(ICConfig.MACHINES.ADVANCED_COMPRESSOR_USE)
			.io(1, 1);

	ElectricBlockInstance TELEPORTER = register("teleporter", TeleporterBlockEntity::new)
			.wip()
			.advanced()
			.energyCapacity(ICConfig.MACHINES.TELEPORTER_CAPACITY)
			.maxEnergyInput(ICConfig.ENERGY.HV_TRANSFER_RATE)
			.maxEnergyOutput(ICConfig.ENERGY.HV_TRANSFER_RATE)
			.feMode(ElectricBlockInstance.FECapMode.INSERT_AND_EXTRACT)
			.energyUsage(ICConfig.MACHINES.TELEPORTER_MAX_USE)
			.energyUsageIsntPerTick();

	ElectricBlockInstance CHARGE_PAD = register("charge_pad", ChargePadBlockEntity::new)
			.advanced()
			.noRotation()
			.energyCapacity(ICConfig.MACHINES.CHARGE_PAD_CAPACITY)
			.maxEnergyInput(ICConfig.ENERGY.IV_TRANSFER_RATE);

	ElectricBlockInstance POWERED_CRAFTING_TABLE = register("powered_crafting_table", PoweredCraftingTableBlockEntity::new)
			.wip()
			.noRotation()
			.cantBeActive()
			.canBurn()
			.energyCapacity(ICConfig.MACHINES.POWERED_CRAFTING_TABLE_CAPACITY)
			.maxEnergyInput(ICConfig.ENERGY.LV_TRANSFER_RATE)
			.energyUsage(ICConfig.MACHINES.POWERED_CRAFTING_TABLE_USE)
			.io(9, 1);

	ElectricBlockInstance QUARRY = register("quarry", QuarryBlockEntity::new)
			.advanced()
			.energyCapacity(ICConfig.MACHINES.QUARRY_CAPACITY)
			.maxEnergyInput(ICConfig.ENERGY.HV_TRANSFER_RATE)
			.energyUsage(ICConfig.MACHINES.QUARRY_USE)
			.io(0, 18);

	ElectricBlockInstance PUMP = register("pump", PumpBlockEntity::new)
			.advanced()
			.energyCapacity(ICConfig.MACHINES.PUMP_CAPACITY)
			.maxEnergyInput(ICConfig.ENERGY.HV_TRANSFER_RATE)
			.energyUsage(ICConfig.MACHINES.PUMP_USE)
			.io(1, 1);

	// Battery Boxes //

	ElectricBlockInstance LV_BATTERY_BOX = register("lv_battery_box", LVBatteryBoxBlockEntity::new)
			.name("LV Battery Box").rotate3D().cantBeActive().canBurn()
			.energyCapacity(ICConfig.ENERGY.LV_BATTERY_BOX_CAPACITY)
			.maxEnergyInput(ICConfig.ENERGY.LV_TRANSFER_RATE)
			.maxEnergyOutput(ICConfig.ENERGY.LV_TRANSFER_RATE)
			.io(1, 1);

	ElectricBlockInstance MV_BATTERY_BOX = register("mv_battery_box", MVBatteryBoxBlockEntity::new)
			.name("MV Battery Box").rotate3D().cantBeActive().canBurn()
			.energyCapacity(ICConfig.ENERGY.MV_BATTERY_BOX_CAPACITY)
			.maxEnergyInput(ICConfig.ENERGY.MV_TRANSFER_RATE)
			.maxEnergyOutput(ICConfig.ENERGY.MV_TRANSFER_RATE)
			.io(1, 1);

	ElectricBlockInstance HV_BATTERY_BOX = register("hv_battery_box", HVBatteryBoxBlockEntity::new)
			.advanced().name("HV Battery Box").rotate3D().cantBeActive().canBurn()
			.energyCapacity(ICConfig.ENERGY.HV_BATTERY_BOX_CAPACITY)
			.maxEnergyInput(ICConfig.ENERGY.HV_TRANSFER_RATE)
			.maxEnergyOutput(ICConfig.ENERGY.HV_TRANSFER_RATE)
			.io(1, 1);

	ElectricBlockInstance EV_BATTERY_BOX = register("ev_battery_box", EVBatteryBoxBlockEntity::new)
			.advanced().name("EV Battery Box").rotate3D().cantBeActive().canBurn()
			.energyCapacity(ICConfig.ENERGY.EV_BATTERY_BOX_CAPACITY)
			.maxEnergyInput(ICConfig.ENERGY.EV_TRANSFER_RATE)
			.maxEnergyOutput(ICConfig.ENERGY.EV_TRANSFER_RATE)
			.io(1, 1);

	// Transformers //

	ElectricBlockInstance LV_TRANSFORMER = register("lv_transformer", LVTransformerBlockEntity::new)
			.name("LV Transformer").rotate3D().cantBeActive().canBurn()
			.energyCapacity(ICConfig.ENERGY.MV_TRANSFER_RATE)
			.maxEnergyInput(ICConfig.ENERGY.MV_TRANSFER_RATE)
			.maxEnergyOutput(ICConfig.ENERGY.LV_TRANSFER_RATE);

	ElectricBlockInstance MV_TRANSFORMER = register("mv_transformer", MVTransformerBlockEntity::new)
			.name("MV Transformer").rotate3D().cantBeActive().canBurn()
			.energyCapacity(ICConfig.ENERGY.HV_TRANSFER_RATE)
			.maxEnergyInput(ICConfig.ENERGY.HV_TRANSFER_RATE)
			.maxEnergyOutput(ICConfig.ENERGY.MV_TRANSFER_RATE);

	ElectricBlockInstance HV_TRANSFORMER = register("hv_transformer", HVTransformerBlockEntity::new)
			.advanced().name("HV Transformer").rotate3D().cantBeActive().canBurn()
			.energyCapacity(ICConfig.ENERGY.EV_TRANSFER_RATE)
			.maxEnergyInput(ICConfig.ENERGY.EV_TRANSFER_RATE)
			.maxEnergyOutput(ICConfig.ENERGY.HV_TRANSFER_RATE);

	ElectricBlockInstance EV_TRANSFORMER = register("ev_transformer", EVTransformerBlockEntity::new)
			.advanced().name("EV Transformer").rotate3D().cantBeActive().canBurn()
			.energyCapacity(ICConfig.ENERGY.IV_TRANSFER_RATE)
			.maxEnergyInput(ICConfig.ENERGY.IV_TRANSFER_RATE)
			.maxEnergyOutput(ICConfig.ENERGY.EV_TRANSFER_RATE);

	// Energy Rectifiers — one-way FE → zaps converters. Input on facing direction; output on the other 5 sides. //

	ElectricBlockInstance LV_RECTIFIER = register("lv_rectifier", LVRectifierBlockEntity::new)
			.name("LV Energy Rectifier").rotate3D().cantBeActive()
			.feMode(ElectricBlockInstance.FECapMode.INSERT_ONLY)
			.energyCapacity(ICConfig.ENERGY.LV_TRANSFER_RATE)
			.maxEnergyInput(ICConfig.ENERGY.LV_TRANSFER_RATE)
			.maxEnergyOutput(ICConfig.ENERGY.LV_TRANSFER_RATE);

	ElectricBlockInstance MV_RECTIFIER = register("mv_rectifier", MVRectifierBlockEntity::new)
			.name("MV Energy Rectifier").rotate3D().cantBeActive()
			.feMode(ElectricBlockInstance.FECapMode.INSERT_ONLY)
			.energyCapacity(ICConfig.ENERGY.MV_TRANSFER_RATE)
			.maxEnergyInput(ICConfig.ENERGY.MV_TRANSFER_RATE)
			.maxEnergyOutput(ICConfig.ENERGY.MV_TRANSFER_RATE);

	ElectricBlockInstance HV_RECTIFIER = register("hv_rectifier", HVRectifierBlockEntity::new)
			.advanced().name("HV Energy Rectifier").rotate3D().cantBeActive()
			.feMode(ElectricBlockInstance.FECapMode.INSERT_ONLY)
			.energyCapacity(ICConfig.ENERGY.HV_TRANSFER_RATE)
			.maxEnergyInput(ICConfig.ENERGY.HV_TRANSFER_RATE)
			.maxEnergyOutput(ICConfig.ENERGY.HV_TRANSFER_RATE);

	ElectricBlockInstance EV_RECTIFIER = register("ev_rectifier", EVRectifierBlockEntity::new)
			.advanced().name("EV Energy Rectifier").rotate3D().cantBeActive()
			.feMode(ElectricBlockInstance.FECapMode.INSERT_ONLY)
			.energyCapacity(ICConfig.ENERGY.EV_TRANSFER_RATE)
			.maxEnergyInput(ICConfig.ENERGY.EV_TRANSFER_RATE)
			.maxEnergyOutput(ICConfig.ENERGY.EV_TRANSFER_RATE);

	ElectricBlockInstance IV_RECTIFIER = register("iv_rectifier", IVRectifierBlockEntity::new)
			.advanced().name("IV Energy Rectifier").rotate3D().cantBeActive()
			.feMode(ElectricBlockInstance.FECapMode.INSERT_ONLY)
			.energyCapacity(ICConfig.ENERGY.IV_TRANSFER_RATE)
			.maxEnergyInput(ICConfig.ENERGY.IV_TRANSFER_RATE)
			.maxEnergyOutput(ICConfig.ENERGY.IV_TRANSFER_RATE);

	static void init() {
	}
}
