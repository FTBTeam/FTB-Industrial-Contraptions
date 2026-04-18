package dev.ftb.mods.ftbic.test;

import com.mojang.serialization.MapCodec;
import dev.ftb.mods.ftbic.FTBIC;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.GameTestInstance;
import net.minecraft.gametest.framework.TestData;
import net.minecraft.gametest.framework.TestEnvironmentDefinition;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterGameTestsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Consumer;

@EventBusSubscriber(modid = FTBIC.MOD_ID)
public class FTBICGameTests {

	private static final Identifier EMPTY_STRUCTURE = FTBIC.id("empty");

	public static final DeferredRegister<MapCodec<? extends GameTestInstance>> TEST_INSTANCE_TYPES =
			DeferredRegister.create(Registries.TEST_INSTANCE_TYPE, FTBIC.MOD_ID);

	public static final DeferredHolder<MapCodec<? extends GameTestInstance>, MapCodec<DirectGameTestInstance>> DIRECT_TYPE =
			TEST_INSTANCE_TYPES.register("direct", () -> DirectGameTestInstance.CODEC);

	@SubscribeEvent
	public static void registerTests(RegisterGameTestsEvent event) {
		Holder<TestEnvironmentDefinition<?>> env = event.registerEnvironment(
				FTBIC.id("default"),
				new TestEnvironmentDefinition.AllOf());

		reg(event, "basic_generator_burns_coal", FTBICGameTestFunctions::basicGeneratorBurnsCoal, env, 200);
		reg(event, "basic_generator_empty_stays_idle", FTBICGameTestFunctions::basicGeneratorEmptyStaysIdle, env, 200);
		reg(event, "basic_generator_stops_when_full", FTBICGameTestFunctions::basicGeneratorStopsWhenFull, env, 200);
		reg(event, "solar_panel_day", FTBICGameTestFunctions::solarPanelDay, env, 200);
		reg(event, "solar_panel_night", FTBICGameTestFunctions::solarPanelNight, env, 200);
		reg(event, "solar_panel_obstructed", FTBICGameTestFunctions::solarPanelObstructed, env, 200);
		reg(event, "ev_solar_outputs_more_than_lv", FTBICGameTestFunctions::evSolarOutputsMoreThanLv, env, 200);
		reg(event, "geothermal_consumes_lava", FTBICGameTestFunctions::geothermalConsumesLava, env, 200);
		reg(event, "windmill_outputs", FTBICGameTestFunctions::windmillOutputs, env, 200);

		reg(event, "macerator_produces_bone_meal", FTBICGameTestFunctions::maceratorProducesBoneMeal, env, 400);
		reg(event, "advanced_macerator_produces_bone_meal", FTBICGameTestFunctions::advancedMaceratorProducesBoneMeal, env, 400);
		reg(event, "compressor_produces_sandstone", FTBICGameTestFunctions::compressorProducesSandstone, env, 400);
		reg(event, "advanced_compressor_produces_sandstone", FTBICGameTestFunctions::advancedCompressorProducesSandstone, env, 400);
		reg(event, "centrifuge_produces_flint", FTBICGameTestFunctions::centrifugeProducesFlint, env, 400);
		reg(event, "advanced_centrifuge_produces_flint", FTBICGameTestFunctions::advancedCentrifugeProducesFlint, env, 400);
		reg(event, "powered_furnace_smelts_raw_iron", FTBICGameTestFunctions::poweredFurnaceSmeltsRawIron, env, 400);
		reg(event, "advanced_powered_furnace_smelts_raw_iron", FTBICGameTestFunctions::advancedPoweredFurnaceSmeltsRawIron, env, 400);
		reg(event, "macerator_no_recipe_no_progress", FTBICGameTestFunctions::maceratorNoRecipeNoProgress, env, 200);
		reg(event, "machine_sleeps_when_output_full", FTBICGameTestFunctions::machineSleepsWhenOutputFull, env, 200);
		reg(event, "machine_consumes_energy_per_tick", FTBICGameTestFunctions::machineConsumesEnergyPerTick, env, 200);

		reg(event, "overclocker_increases_speed", FTBICGameTestFunctions::overclockerIncreasesSpeed, env, 100);
		reg(event, "overclocker_increases_energy_use", FTBICGameTestFunctions::overclockerIncreasesEnergyUse, env, 100);
		reg(event, "stacked_overclockers_multiplicative", FTBICGameTestFunctions::stackedOverclockersMultiplicative, env, 100);
		reg(event, "transformer_upgrade_increases_input_cap", FTBICGameTestFunctions::transformerUpgradeIncreasesInputCap, env, 100);
		reg(event, "storage_upgrade_increases_capacity", FTBICGameTestFunctions::storageUpgradeIncreasesCapacity, env, 100);
		reg(event, "ejector_upgrade_sets_auto_eject", FTBICGameTestFunctions::ejectorUpgradeSetsAutoEject, env, 100);
		reg(event, "upgrade_persisted_across_save", FTBICGameTestFunctions::upgradePersistedAcrossSave, env, 100);

		reg(event, "battery_box_drains_input_battery", FTBICGameTestFunctions::batteryBoxDrainsInputBattery, env, 200);
		reg(event, "battery_box_charges_output_battery", FTBICGameTestFunctions::batteryBoxChargesOutputBattery, env, 200);
		reg(event, "battery_box_output_face_only", FTBICGameTestFunctions::batteryBoxOutputFaceOnly, env, 100);
		reg(event, "transformer_face_geometry", FTBICGameTestFunctions::transformerFaceGeometry, env, 100);
		reg(event, "cable_connects_gen_to_machine", FTBICGameTestFunctions::cableConnectsGenToMachine, env, 200);
		reg(event, "burnt_cable_does_not_conduct", FTBICGameTestFunctions::burntCableDoesNotConduct, env, 200);
		reg(event, "burnt_cable_state_retains_cable_shape", FTBICGameTestFunctions::burntCableStateRetainsCableShape, env, 100);
		reg(event, "network_distributes_to_multiple_machines", FTBICGameTestFunctions::networkDistributesToMultipleMachines, env, 200);
		reg(event, "network_rebuild_on_cable_removal", FTBICGameTestFunctions::networkRebuildOnCableRemoval, env, 200);
		reg(event, "quarry_paused_without_energy", FTBICGameTestFunctions::quarryPausedWithoutEnergy, env, 200);
		reg(event, "quarry_redstone_pause_flag_sets_on_signal", FTBICGameTestFunctions::quarryRedstonePauseFlagSetsOnSignal, env, 100);
		reg(event, "pump_extracts_adjacent_water", FTBICGameTestFunctions::pumpExtractsAdjacentWater, env, 200);

		reg(event, "reactor_placed_defaults_to_paused", FTBICGameTestFunctions::reactorPlacedDefaultsToPaused, env, 100);
		reg(event, "reactor_attached_chambers_increase_columns", FTBICGameTestFunctions::reactorAttachedChambersIncreaseColumns, env, 100);
		reg(event, "reactor_counts_attached_chambers_up_to_6", FTBICGameTestFunctions::reactorCountsAttachedChambersUpTo6, env, 100);
		reg(event, "reactor_detonates_at_max_heat", FTBICGameTestFunctions::reactorDetonatesAtMaxHeat, env, 200);

		reg(event, "antimatter_constructor_progresses", FTBICGameTestFunctions::antimatterConstructorProgresses, env, 100);
		reg(event, "powered_crafting_table_crafts_planks_into_table", FTBICGameTestFunctions::poweredCraftingTableCraftsPlanksIntoTable, env, 100);
		reg(event, "charge_pad_transfers_energy_from_buffer_to_stack", FTBICGameTestFunctions::chargePadTransfersEnergyFromBufferToStack, env, 100);

		reg(event, "rechargeable_battery_accepts_and_holds_energy", FTBICGameTestFunctions::rechargeableBatteryAcceptsAndHoldsEnergy, env, 100);
		reg(event, "rechargeable_battery_clears_component_at_zero", FTBICGameTestFunctions::rechargeableBatteryClearsComponentAtZero, env, 100);
		reg(event, "single_use_battery_shrinks_at_zero", FTBICGameTestFunctions::singleUseBatteryShrinksAtZero, env, 100);
		reg(event, "single_use_battery_cannot_be_recharged", FTBICGameTestFunctions::singleUseBatteryCannotBeRecharged, env, 100);

		reg(event, "fluid_cell_fills_from_water_on_use", FTBICGameTestFunctions::fluidCellFillsFromWaterOnUse, env, 100);
	}

	private static void reg(RegisterGameTestsEvent event, String name,
							Consumer<GameTestHelper> function,
							Holder<TestEnvironmentDefinition<?>> environment,
							int timeoutTicks) {
		TestData<Holder<TestEnvironmentDefinition<?>>> testData = new TestData<>(
				environment, EMPTY_STRUCTURE, timeoutTicks, 0, true);
		GameTestInstance instance = new DirectGameTestInstance(name, function, testData);
		event.registerTest(FTBIC.id(name), instance);
	}
}
