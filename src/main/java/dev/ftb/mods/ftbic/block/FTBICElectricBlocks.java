package dev.ftb.mods.ftbic.block;

import dev.ftb.mods.ftbic.block.entity.generator.BasicGeneratorBlockEntity;
import dev.ftb.mods.ftbic.block.entity.generator.GeothermalGeneratorBlockEntity;
import dev.ftb.mods.ftbic.block.entity.generator.HVSolarPanelBlockEntity;
import dev.ftb.mods.ftbic.block.entity.generator.LVSolarPanelBlockEntity;
import dev.ftb.mods.ftbic.block.entity.generator.MVSolarPanelBlockEntity;
import dev.ftb.mods.ftbic.block.entity.generator.NuclearReactorBlockEntity;
import dev.ftb.mods.ftbic.block.entity.generator.WindMillBlockEntity;
import dev.ftb.mods.ftbic.block.entity.machine.CanningMachineBlockEntity;
import dev.ftb.mods.ftbic.block.entity.machine.CompressorBlockEntity;
import dev.ftb.mods.ftbic.block.entity.machine.ElectricFurnaceBlockEntity;
import dev.ftb.mods.ftbic.block.entity.machine.ElectrolyzerBlockEntity;
import dev.ftb.mods.ftbic.block.entity.machine.ExtractorBlockEntity;
import dev.ftb.mods.ftbic.block.entity.machine.InductionFurnaceBlockEntity;
import dev.ftb.mods.ftbic.block.entity.machine.MaceratorBlockEntity;
import dev.ftb.mods.ftbic.block.entity.machine.RecyclerBlockEntity;
import dev.ftb.mods.ftbic.block.entity.machine.RotaryMaceratorBlockEntity;
import dev.ftb.mods.ftbic.block.entity.machine.SingularityCompressorBlockEntity;
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

	ElectricBlockInstance BASIC_GENERATOR = register("basic_generator", BasicGeneratorBlockEntity::new);
	ElectricBlockInstance GEOTHERMAL_GENERATOR = register("geothermal_generator", GeothermalGeneratorBlockEntity::new);
	ElectricBlockInstance WIND_MILL = register("wind_mill", WindMillBlockEntity::new).noState();
	ElectricBlockInstance LV_SOLAR_PANEL = register("lv_solar_panel", LVSolarPanelBlockEntity::new).name("LV Solar Panel").noRotation().noState();
	ElectricBlockInstance MV_SOLAR_PANEL = register("mv_solar_panel", MVSolarPanelBlockEntity::new).name("MV Solar Panel").noRotation().noState();
	ElectricBlockInstance HV_SOLAR_PANEL = register("hv_solar_panel", HVSolarPanelBlockEntity::new).advanced().name("HV Solar Panel").noRotation().noState();
	ElectricBlockInstance NUCLEAR_REACTOR = register("nuclear_reactor", NuclearReactorBlockEntity::new).noRotation().advanced();

	ElectricBlockInstance ELECTRIC_FURNACE = register("electric_furnace", ElectricFurnaceBlockEntity::new).canBurn();
	ElectricBlockInstance MACERATOR = register("macerator", MaceratorBlockEntity::new).canBurn().noModel();
	ElectricBlockInstance EXTRACTOR = register("extractor", ExtractorBlockEntity::new).canBurn().noModel();
	ElectricBlockInstance COMPRESSOR = register("compressor", CompressorBlockEntity::new).canBurn().noModel();
	ElectricBlockInstance ELECTROLYZER = register("electrolyzer", ElectrolyzerBlockEntity::new).canBurn().noModel();
	ElectricBlockInstance RECYCLER = register("recycler", RecyclerBlockEntity::new).canBurn().advanced().noModel();
	ElectricBlockInstance CANNING_MACHINE = register("canning_machine", CanningMachineBlockEntity::new).canBurn().noModel();
	ElectricBlockInstance INDUCTION_FURNACE = register("induction_furnace", InductionFurnaceBlockEntity::new).canBurn().advanced().noModel();
	ElectricBlockInstance ROTARY_MACERATOR = register("rotary_macerator", RotaryMaceratorBlockEntity::new).canBurn().advanced().noModel();
	ElectricBlockInstance SINGULARITY_COMPRESSOR = register("singularity_compressor", SingularityCompressorBlockEntity::new).canBurn().advanced().noModel();

	ElectricBlockInstance LV_BATTERY_BOX = register("lv_battery_box", LVBatteryBoxBlockEntity::new).name("LV Battery Box").rotate3D().canBurn().noModel();
	ElectricBlockInstance MV_BATTERY_BOX = register("mv_battery_box", MVBatteryBoxBlockEntity::new).name("MV Battery Box").rotate3D().canBurn().noModel();
	ElectricBlockInstance HV_BATTERY_BOX = register("hv_battery_box", HVBatteryBoxBlockEntity::new).name("HV Battery Box").rotate3D().canBurn().advanced().noModel();
	ElectricBlockInstance LV_TRANSFORMER = register("lv_transformer", LVTransformerBlockEntity::new).name("LV Transformer").rotate3D().canBurn().noModel();
	ElectricBlockInstance MV_TRANSFORMER = register("mv_transformer", MVTransformerBlockEntity::new).name("MV Transformer").rotate3D().canBurn().noModel();
	ElectricBlockInstance HV_TRANSFORMER = register("hv_transformer", HVTransformerBlockEntity::new).name("HV Transformer").rotate3D().canBurn().advanced().noModel();

	static void init() {
	}
}
