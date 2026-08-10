package dev.ftb.mods.industrialcontraptions.test;

import com.mojang.serialization.MapCodec;
import dev.ftb.mods.industrialcontraptions.IC;
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

@EventBusSubscriber(modid = IC.MOD_ID)
public class ICGameTests {

	private static final Identifier EMPTY_STRUCTURE = IC.id("empty");

	public static final DeferredRegister<MapCodec<? extends GameTestInstance>> TEST_INSTANCE_TYPES =
			DeferredRegister.create(Registries.TEST_INSTANCE_TYPE, IC.MOD_ID);

	public static final DeferredHolder<MapCodec<? extends GameTestInstance>, MapCodec<DirectGameTestInstance>> DIRECT_TYPE =
			TEST_INSTANCE_TYPES.register("direct", () -> DirectGameTestInstance.CODEC);

	@SubscribeEvent
	public static void registerTests(RegisterGameTestsEvent event) {
		Holder<TestEnvironmentDefinition<?>> env = event.registerEnvironment(
				IC.id("default"),
				new TestEnvironmentDefinition.AllOf());

		reg(event, "basic_generator_burns_coal", ICGameTestFunctions::basicGeneratorBurnsCoal, env, 200);
		reg(event, "basic_generator_empty_stays_idle", ICGameTestFunctions::basicGeneratorEmptyStaysIdle, env, 200);
		reg(event, "basic_generator_stops_when_full", ICGameTestFunctions::basicGeneratorStopsWhenFull, env, 200);
		reg(event, "solar_panel_day", ICGameTestFunctions::solarPanelDay, env, 200);
		reg(event, "solar_panel_night", ICGameTestFunctions::solarPanelNight, env, 200);
		reg(event, "solar_panel_obstructed", ICGameTestFunctions::solarPanelObstructed, env, 200);
		reg(event, "ev_solar_outputs_more_than_lv", ICGameTestFunctions::evSolarOutputsMoreThanLv, env, 200);
		reg(event, "geothermal_consumes_lava", ICGameTestFunctions::geothermalConsumesLava, env, 200);
		reg(event, "windmill_outputs", ICGameTestFunctions::windmillOutputs, env, 200);

		reg(event, "macerator_produces_bone_meal", ICGameTestFunctions::maceratorProducesBoneMeal, env, 400);
		reg(event, "advanced_macerator_produces_bone_meal", ICGameTestFunctions::advancedMaceratorProducesBoneMeal, env, 400);
		reg(event, "compressor_produces_sandstone", ICGameTestFunctions::compressorProducesSandstone, env, 400);
		reg(event, "advanced_compressor_produces_sandstone", ICGameTestFunctions::advancedCompressorProducesSandstone, env, 400);
		reg(event, "centrifuge_produces_flint", ICGameTestFunctions::centrifugeProducesFlint, env, 400);
		reg(event, "centrifuge_rejects_input_below_recipe_count", ICGameTestFunctions::centrifugeRejectsInputBelowRecipeCount, env, 400);
		reg(event, "centrifuge_consumes_count_from_input_kelp", ICGameTestFunctions::centrifugeConsumesCountFromInputKelp, env, 400);
		reg(event, "advanced_centrifuge_produces_flint", ICGameTestFunctions::advancedCentrifugeProducesFlint, env, 400);
		reg(event, "powered_furnace_smelts_raw_iron", ICGameTestFunctions::poweredFurnaceSmeltsRawIron, env, 400);
		reg(event, "advanced_powered_furnace_smelts_raw_iron", ICGameTestFunctions::advancedPoweredFurnaceSmeltsRawIron, env, 400);
		reg(event, "macerator_no_recipe_no_progress", ICGameTestFunctions::maceratorNoRecipeNoProgress, env, 200);
		reg(event, "machine_sleeps_when_output_full", ICGameTestFunctions::machineSleepsWhenOutputFull, env, 200);
		reg(event, "machine_consumes_energy_per_tick", ICGameTestFunctions::machineConsumesEnergyPerTick, env, 200);
		reg(event, "machine_starving_flag_set_when_energy_depleted", ICGameTestFunctions::machineStarvingFlagSetWhenEnergyDepleted, env, 100);
		reg(event, "machine_not_starving_without_recipe", ICGameTestFunctions::machineNotStarvingWithoutRecipe, env, 100);
		reg(event, "machine_starving_clears_when_energy_restored", ICGameTestFunctions::machineStarvingClearsWhenEnergyRestored, env, 100);

		reg(event, "overclocker_increases_speed", ICGameTestFunctions::overclockerIncreasesSpeed, env, 100);
		reg(event, "overclocker_increases_energy_use", ICGameTestFunctions::overclockerIncreasesEnergyUse, env, 100);
		reg(event, "stacked_overclockers_multiplicative", ICGameTestFunctions::stackedOverclockersMultiplicative, env, 100);
		reg(event, "transformer_upgrade_increases_input_cap", ICGameTestFunctions::transformerUpgradeIncreasesInputCap, env, 100);
		reg(event, "storage_upgrade_increases_capacity", ICGameTestFunctions::storageUpgradeIncreasesCapacity, env, 100);
		reg(event, "ejector_upgrade_sets_auto_eject", ICGameTestFunctions::ejectorUpgradeSetsAutoEject, env, 100);
		reg(event, "upgrade_persisted_across_save", ICGameTestFunctions::upgradePersistedAcrossSave, env, 100);

		reg(event, "battery_box_drains_input_battery", ICGameTestFunctions::batteryBoxDrainsInputBattery, env, 200);
		reg(event, "battery_box_charges_output_battery", ICGameTestFunctions::batteryBoxChargesOutputBattery, env, 200);
		reg(event, "battery_box_output_face_only", ICGameTestFunctions::batteryBoxOutputFaceOnly, env, 100);
		reg(event, "transformer_face_geometry", ICGameTestFunctions::transformerFaceGeometry, env, 100);
		reg(event, "energy_tier_transfer_rates_match_config", ICGameTestFunctions::energyTierTransferRatesMatchConfig, env, 20);
		reg(event, "lv_cable_survives_within_rate", ICGameTestFunctions::lvCableSurvivesWithinRate, env, 200);
		reg(event, "lv_cable_burns_when_overloaded", ICGameTestFunctions::lvCableBurnsWhenOverloaded, env, 200);
		reg(event, "overload_burns_entire_lv_subnet", ICGameTestFunctions::overloadBurnsEntireLvSubnet, env, 200);
		reg(event, "transformer_steps_mv_down_to_lv", ICGameTestFunctions::transformerStepsMvDownToLv, env, 200);
		reg(event, "rectifier_converts_fe_to_zaps", ICGameTestFunctions::rectifierConvertsFeToZaps, env, 100);
		reg(event, "rectifier_feeds_downstream_machine", ICGameTestFunctions::rectifierFeedsDownstreamMachine, env, 200);
		reg(event, "rectifier_face_geometry", ICGameTestFunctions::rectifierFaceGeometry, env, 100);
		reg(event, "rectifier_roundtrip_conserves_energy", ICGameTestFunctions::rectifierRoundtripConservesEnergy, env, 200);
		reg(event, "rectifier_fe_insert_is_transactional", ICGameTestFunctions::rectifierFeInsertIsTransactional, env, 100);
		reg(event, "cable_connects_gen_to_machine", ICGameTestFunctions::cableConnectsGenToMachine, env, 200);
		reg(event, "burnt_cable_does_not_conduct", ICGameTestFunctions::burntCableDoesNotConduct, env, 200);
		reg(event, "burnt_cable_state_retains_cable_shape", ICGameTestFunctions::burntCableStateRetainsCableShape, env, 100);
		reg(event, "network_distributes_to_multiple_machines", ICGameTestFunctions::networkDistributesToMultipleMachines, env, 200);
		reg(event, "network_rebuild_on_cable_removal", ICGameTestFunctions::networkRebuildOnCableRemoval, env, 200);
		reg(event, "quarry_paused_without_energy", ICGameTestFunctions::quarryPausedWithoutEnergy, env, 200);
		reg(event, "quarry_redstone_pause_flag_sets_on_signal", ICGameTestFunctions::quarryRedstonePauseFlagSetsOnSignal, env, 100);
		reg(event, "pump_extracts_adjacent_water", ICGameTestFunctions::pumpExtractsAdjacentWater, env, 200);

		reg(event, "reactor_placed_defaults_to_paused", ICGameTestFunctions::reactorPlacedDefaultsToPaused, env, 100);
		reg(event, "reactor_attached_chambers_increase_columns", ICGameTestFunctions::reactorAttachedChambersIncreaseColumns, env, 100);
		reg(event, "reactor_counts_attached_chambers_up_to_6", ICGameTestFunctions::reactorCountsAttachedChambersUpTo6, env, 100);
		reg(event, "reactor_detonates_at_max_heat", ICGameTestFunctions::reactorDetonatesAtMaxHeat, env, 200);

		reg(event, "antimatter_constructor_progresses", ICGameTestFunctions::antimatterConstructorProgresses, env, 100);
		reg(event, "powered_crafting_table_crafts_planks_into_table", ICGameTestFunctions::poweredCraftingTableCraftsPlanksIntoTable, env, 100);
		reg(event, "charge_pad_transfers_energy_from_buffer_to_stack", ICGameTestFunctions::chargePadTransfersEnergyFromBufferToStack, env, 100);

		reg(event, "rechargeable_battery_accepts_and_holds_energy", ICGameTestFunctions::rechargeableBatteryAcceptsAndHoldsEnergy, env, 100);
		reg(event, "rechargeable_battery_clears_component_at_zero", ICGameTestFunctions::rechargeableBatteryClearsComponentAtZero, env, 100);
		reg(event, "single_use_battery_shrinks_at_zero", ICGameTestFunctions::singleUseBatteryShrinksAtZero, env, 100);
		reg(event, "single_use_battery_cannot_be_recharged", ICGameTestFunctions::singleUseBatteryCannotBeRecharged, env, 100);

		reg(event, "fluid_cell_fills_from_water_on_use", ICGameTestFunctions::fluidCellFillsFromWaterOnUse, env, 100);
		reg(event, "fluid_cell_ingredient_displays_filled_stack", ICGameTestFunctions::fluidCellIngredientDisplaysFilledStack, env, 100);

		reg(event, "quarry_pickaxe_silk_touch_produces_stone", ICGameTestFunctions::quarryPickaxeSilkTouchProducesStone, env, 100);
		reg(event, "quarry_without_pickaxe_produces_cobble", ICGameTestFunctions::quarryWithoutPickaxeProducesCobble, env, 100);
		reg(event, "quarry_efficiency_pickaxe_speeds_up_mining", ICGameTestFunctions::quarryEfficiencyPickaxeSpeedsUpMining, env, 100);
		reg(event, "quarry_pickaxe_rejects_non_pickaxe", ICGameTestFunctions::quarryPickaxeRejectsNonPickaxe, env, 100);

		reg(event, "teleporter_pipe_forwards_item_to_peer", ICGameTestFunctions::teleporterPipeForwardsItemToPeer, env, 200);
		reg(event, "teleporter_pipe_extracts_from_peer", ICGameTestFunctions::teleporterPipeExtractsFromPeer, env, 200);
		reg(event, "teleporter_pipe_forwards_fluid_to_peer", ICGameTestFunctions::teleporterPipeForwardsFluidToPeer, env, 200);
		reg(event, "teleporter_pipe_clear_storage", ICGameTestFunctions::teleporterPipeClearStorage, env, 100);
		reg(event, "teleporter_pipe_clear_fluids", ICGameTestFunctions::teleporterPipeClearFluids, env, 100);
		reg(event, "teleporter_pipe_drain_on_activity", ICGameTestFunctions::teleporterPipeDrainOnActivity, env, 200);
		reg(event, "teleporter_pipe_balances_energy", ICGameTestFunctions::teleporterPipeBalancesEnergy, env, 200);
		reg(event, "teleporter_exposes_energy_cap_both_directions", ICGameTestFunctions::teleporterExposesEnergyCapBothDirections, env, 40);
		reg(event, "teleporter_pair_relays_power_for_remote_extract", ICGameTestFunctions::teleporterPairRelaysPowerForRemoteExtract, env, 200);
		reg(event, "teleporter_filters_other_teleporters_from_push_network", ICGameTestFunctions::teleporterFiltersOtherTeleportersFromPushNetwork, env, 40);

		reg(event, "reactor_simulator_runs_and_emits_power", ICGameTestFunctions::reactorSimulatorRunsAndEmitsPower, env, 200);
		reg(event, "reactor_simulator_edit_lock", ICGameTestFunctions::reactorSimulatorEditLock, env, 100);
		reg(event, "reactor_simulator_import_roundtrip", ICGameTestFunctions::reactorSimulatorImportRoundtrip, env, 100);

		reg(event, "insert_accepts_partial_when_slot_overflows", ICGameTestFunctions::insertAcceptsPartialWhenSlotOverflows, env, 40);
		reg(event, "machine_stall_on_full_output_does_not_drop_items", ICGameTestFunctions::machineStallOnFullOutputDoesNotDropItems, env, 220);
		reg(event, "overclocker_progress_speed_scales_as_power", ICGameTestFunctions::overclockerProgressSpeedScalesAsPower, env, 40);
		reg(event, "transaction_abort_restores_multiple_slots", ICGameTestFunctions::transactionAbortRestoresMultipleSlots, env, 40);
		reg(event, "transaction_nested_abort_preserves_outer_writes", ICGameTestFunctions::transactionNestedAbortPreservesOuterWrites, env, 40);
		reg(event, "transaction_nested_commit_merges_into_outer", ICGameTestFunctions::transactionNestedCommitMergesIntoOuter, env, 40);

		reg(event, "zap_cap_present_on_every_electric_block", ICGameTestFunctions::zapCapPresentOnEveryElectricBlock, env, 100);
		reg(event, "zap_cap_null_side_returns_handler", ICGameTestFunctions::zapCapNullSideReturnsHandler, env, 40);
		reg(event, "zap_cap_absent_on_vanilla_blocks", ICGameTestFunctions::zapCapAbsentOnVanillaBlocks, env, 40);
		reg(event, "zap_cap_forwards_through_reactor_chamber", ICGameTestFunctions::zapCapForwardsThroughReactorChamber, env, 40);
		reg(event, "zap_cap_cache_invalidates_on_block_removal", ICGameTestFunctions::zapCapCacheInvalidatesOnBlockRemoval, env, 40);
		reg(event, "zap_cap_cache_updates_on_block_swap", ICGameTestFunctions::zapCapCacheUpdatesOnBlockSwap, env, 40);

		reg(event, "nuclear_explosion_destroys_unshielded_block", ICGameTestFunctions::nuclearExplosionDestroysUnshieldedBlock, env, 20);
		reg(event, "nuclear_explosion_preserves_reinforced_block", ICGameTestFunctions::nuclearExplosionPreservesReinforcedBlock, env, 20);
		reg(event, "nuclear_explosion_shielded_by_reinforced_wall", ICGameTestFunctions::nuclearExplosionShieldedByReinforcedWall, env, 20);
		reg(event, "nuclear_fallout_shielded_by_reinforced_wall", ICGameTestFunctions::nuclearFalloutShieldedByReinforcedWall, env, 20);

		reg(event, "legacy_ids_resolve_to_current_entries", ICGameTestFunctions::legacyIdsResolveToCurrentEntries, env, 20);
	}

	private static void reg(RegisterGameTestsEvent event, String name,
							Consumer<GameTestHelper> function,
							Holder<TestEnvironmentDefinition<?>> environment,
							int timeoutTicks) {
		TestData<Holder<TestEnvironmentDefinition<?>>> testData = new TestData<>(
				environment, EMPTY_STRUCTURE, timeoutTicks, 0, true);
		GameTestInstance instance = new DirectGameTestInstance(name, function, testData);
		event.registerTest(IC.id(name), instance);
	}
}
