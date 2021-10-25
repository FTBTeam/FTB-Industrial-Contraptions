package dev.ftb.mods.ftbic.block;

import dev.ftb.mods.ftbic.block.entity.generator.BasicGeneratorBlockEntity;
import dev.ftb.mods.ftbic.block.entity.generator.GeothermalGeneratorBlockEntity;
import dev.ftb.mods.ftbic.block.entity.generator.HVSolarPanelBlockEntity;
import dev.ftb.mods.ftbic.block.entity.generator.LVSolarPanelBlockEntity;
import dev.ftb.mods.ftbic.block.entity.generator.MVSolarPanelBlockEntity;
import dev.ftb.mods.ftbic.block.entity.generator.NuclearReactorBlockEntity;
import dev.ftb.mods.ftbic.block.entity.generator.WaterMillBlockEntity;
import dev.ftb.mods.ftbic.block.entity.generator.WindMillBlockEntity;
import dev.ftb.mods.ftbic.block.entity.machine.CompressorBlockEntity;
import dev.ftb.mods.ftbic.block.entity.machine.ElectricFurnaceBlockEntity;
import dev.ftb.mods.ftbic.block.entity.machine.ElectrolyzerBlockEntity;
import dev.ftb.mods.ftbic.block.entity.machine.ExtractorBlockEntity;
import dev.ftb.mods.ftbic.block.entity.machine.InductionFurnaceBlockEntity;
import dev.ftb.mods.ftbic.block.entity.machine.MaceratorBlockEntity;
import dev.ftb.mods.ftbic.block.entity.machine.RecyclerBlockEntity;
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
	ElectricBlockInstance GEOTHERMAL_GENERATOR = register("geothermal_generator", GeothermalGeneratorBlockEntity::new).noModel();
	ElectricBlockInstance WATER_MILL = register("water_mill", WaterMillBlockEntity::new).noModel();
	ElectricBlockInstance WIND_MILL = register("wind_mill", WindMillBlockEntity::new).noModel();
	ElectricBlockInstance LV_SOLAR_PANEL = register("lv_solar_panel", LVSolarPanelBlockEntity::new).name("LV Solar Panel").noModel();
	ElectricBlockInstance MV_SOLAR_PANEL = register("mv_solar_panel", MVSolarPanelBlockEntity::new).name("MV Solar Panel").noModel();
	ElectricBlockInstance HV_SOLAR_PANEL = register("hv_solar_panel", HVSolarPanelBlockEntity::new).advanced().name("HV Solar Panel").noModel();
	ElectricBlockInstance NUCLEAR_REACTOR = register("nuclear_reactor", NuclearReactorBlockEntity::new).advanced().noModel();

	ElectricBlockInstance ELECTRIC_FURNACE = register("electric_furnace", ElectricFurnaceBlockEntity::new);
	ElectricBlockInstance MACERATOR = register("macerator", MaceratorBlockEntity::new).noModel();
	ElectricBlockInstance EXTRACTOR = register("extractor", ExtractorBlockEntity::new).noModel();
	ElectricBlockInstance COMPRESSOR = register("compressor", CompressorBlockEntity::new).noModel();
	ElectricBlockInstance ELECTROLYZER = register("electrolyzer", ElectrolyzerBlockEntity::new).noModel();
	ElectricBlockInstance RECYCLER = register("recycler", RecyclerBlockEntity::new).advanced().noModel();
	ElectricBlockInstance INDUCTION_FURNACE = register("induction_furnace", InductionFurnaceBlockEntity::new).advanced().noModel();

	static void init() {
	}
}
