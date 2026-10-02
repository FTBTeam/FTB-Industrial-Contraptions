package dev.ftb.mods.ftbic.test;

import com.mojang.serialization.MapCodec;
import dev.ftb.mods.ftbic.FTBIC;
import java.util.function.Consumer;
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

@EventBusSubscriber(modid = FTBIC.MOD_ID)
public class FTBICGameTests {

    private static final Identifier EMPTY_STRUCTURE = FTBIC.id("empty");

    public static final DeferredRegister<MapCodec<? extends GameTestInstance>> TEST_INSTANCE_TYPES =
            DeferredRegister.create(Registries.TEST_INSTANCE_TYPE, FTBIC.MOD_ID);

    public static final DeferredHolder<MapCodec<? extends GameTestInstance>, MapCodec<DirectGameTestInstance>>
            DIRECT_TYPE = TEST_INSTANCE_TYPES.register("direct", () -> DirectGameTestInstance.CODEC);

    @SubscribeEvent
    public static void registerTests(RegisterGameTestsEvent event) {
        Holder<TestEnvironmentDefinition<?>> env =
                event.registerEnvironment(FTBIC.id("default"), new TestEnvironmentDefinition.AllOf());
        Holder<TestEnvironmentDefinition<?>> nightEnv =
                event.registerEnvironment(FTBIC.id("night"), new TestEnvironmentDefinition.AllOf());
        reg(event, "sound_charge_completion", SoundGameTests::chargingCompletion, env, 60);
        reg(event, "sound_hot_idle_reactor", SoundGameTests::hotIdleReactor, env, 60);
        reg(event, "sound_critical_idle_reactor", SoundGameTests::criticalIdleReactor, env, 60);
        reg(event, "upgrade_shift_click_limit", UpgradeInventoryGameTests::shiftClickLimit, env, 60);
        reg(event, "upgrade_sneak_insert", UpgradeInventoryGameTests::sneakInsert, env, 60);
        reg(event, "upgrade_set_command", UpgradeInventoryGameTests::setUpgradesCommand, env, 60);
        reg(event, "refining_full_chain", RefiningGameTests::fullChain, env, 60);
        reg(event, "refining_blocking_identity", RefiningGameTests::blockingAndIdentity, env, 60);
        reg(event, "refining_discovery_overrides", RefiningGameTests::discoveryAndOverrides, env, 60);
        reg(event, "refining_colors", RefiningGameTests::colors, env, 60);
        reg(event, "hydroponic_catalyst_speed", HydroponicGameTests::catalystAndSpeed, env, 60);
        reg(event, "hydroponic_parallel_lanes", HydroponicGameTests::parallelLanes, env, 60);
        reg(event, "hydroponic_consumed_seed", HydroponicGameTests::consumedSeed, env, 60);
        reg(event, "hydroponic_mutation_lanes", HydroponicGameTests::mutationAndLanes, env, 60);
        reg(event, "bank_connected_storage", BankGameTests::connectedStorage, env, 60);
        reg(event, "bank_cell_seam_states", BankGameTests::cellSeamStates, env, 60);
        reg(event, "bank_charge_display", BankGameTests::chargeDisplayTracksBank, env, 60);
        reg(event, "bank_combined_displays", BankGameTests::combinedDisplays, env, 60);
        reg(event, "bank_port_face_styles", BankGameTests::portFaceStyles, env, 60);
        reg(event, "bank_sustained_cable_charging", BankGameTests::sustainedCableCharging, env, 60);
        reg(event, "bank_jade_energy_bar", BankJadeGameTests::combinedEnergyBar, env, 60);
        reg(event, "jade_hides_default_energy_bar", BankJadeGameTests::electricBlocksHideDefaultBar, env, 60);
        reg(event, "superconducting_unlimited_transfer", SuperconductingCableGameTests::unlimitedTransfer, env, 60);
        reg(event, "superconducting_active_routes", SuperconductingCableGameTests::onlyUsedRoutesPulse, env, 60);
        reg(event, "superconducting_connections", SuperconductingCableGameTests::connectionsAndRemoval, env, 60);
        reg(event, "cable_fe_external_input", CableFEGameTests::externalInput, env, 60);
        reg(event, "cable_fe_variants", CableFEGameTests::cableVariants, env, 60);
        reg(event, "cable_fe_cached_lookup", CableFEGameTests::cachedLookupSeesPlacedCable, env, 60);
        reg(event, "rectifiers_follow_energy_mode", EnergyModeGameTests::rectifiersFollowEnergyMode, env, 60);
        reg(event, "energy_mode_direct_fe_input_clamps", EnergyModeGameTests::directFEInputClamps, env, 60);
        reg(event, "energy_mode_display_follows_mode", EnergyModeGameTests::energyDisplayFollowsMode, env, 60);
        reg(event, "energy_mode_reactor_exposes_fe", EnergyModeGameTests::reactorExposesFE, env, 60);
        reg(event, "reactor_fe_chamber_push", ReactorFEGameTests::chamberPush, env, 60);
        reg(event, "reactor_fe_consuming_meter", ReactorFEGameTests::cableToConsumingMeter, env, 60);
        reg(event, "reactor_fe_chamber_pull", ReactorFEGameTests::chamberPull, env, 60);
        reg(event, "review_batteries_use_configured_charge", ReviewFixGameTests::batteriesUseConfiguredCharge, env, 60);
        reg(event, "review_bank_blocks_drop_themselves", ReviewFixGameTests::bankBlocksDropThemselves, env, 60);
        reg(event, "review_upgraded_energy_survives_reload", ReviewFixGameTests::upgradedEnergySurvivesReload, env, 60);
        reg(
                event,
                "review_configuration_card_syncs_enchanted_items",
                ReviewFixGameTests::configurationCardSyncsEnchantedItems,
                env,
                60);
        reg(event, "review_recipe_toggles_follow_config", ReviewFixGameTests::recipeTogglesFollowConfig, env, 60);
        reg(
                event,
                "review_battery_boxes_expose_charge_slot",
                ReviewFixGameTests::batteryBoxesExposeChargeSlot,
                env,
                60);
        reg(event, "review_bank_port_charge_slots", ReviewFixGameTests::bankPortChargeSlots, env, 100);
        reg(event, "review_underpowered_machine_finishes", ReviewFixGameTests::underpoweredMachineFinishes, env, 60);
        reg(event, "review_update_tag_skips_menu_data", ReviewFixGameTests::updateTagSkipsMenuData, env, 60);
        reg(
                event,
                "review_superconducting_pulse_follows_transfers",
                ReviewFixGameTests::superconductingPulseFollowsTransfers,
                env,
                100);
        reg(event, "energy_retention_battery_box_drop", EnergyRetentionGameTests::batteryBoxDropKeepsEnergy, env, 60);
        reg(
                event,
                "energy_retention_battery_box_place",
                EnergyRetentionGameTests::batteryBoxPlacementRestoresEnergy,
                env,
                60);
        reg(event, "energy_retention_bank_cell", EnergyRetentionGameTests::bankCellRoundTrip, env, 60);
        reg(event, "cable_route_cache_reuse", CableRouteCacheGameTests::repeatedInsertsReuseRoute, env, 60);
        reg(event, "cable_route_cache_new_receiver", CableRouteCacheGameTests::newReceiverIsPickedUp, env, 60);
        reg(event, "cable_route_cache_removals", CableRouteCacheGameTests::removalsArePickedUp, env, 60);
        reg(event, "cable_route_cache_disabled_side", CableRouteCacheGameTests::disabledSideIsRespected, env, 60);
        reg(event, "digging_area_survives_reload", DiggingGameTests::areaSurvivesReload, env, 100);
        reg(event, "digging_no_energy_without_area", DiggingGameTests::noEnergyWithoutArea, env, 100);
        reg(event, "digging_quarry_clears_fluid", DiggingGameTests::quarryClearsFluid, env, 100);
        reg(event, "digging_pump_uses_own_settings", DiggingGameTests::pumpUsesOwnSettings, env, 100);
        reg(event, "digging_landmark_area", DiggingGameTests::landmarkArea, env, 100);
        reg(event, "digging_protected_block_skipped", DiggingGameTests::protectedBlockSkipped, env, 100);
        reg(event, "machine_fix_crafting_remainders", MachineFixGameTests::craftingTableRemainders, env, 100);
        reg(event, "machine_fix_topped_up_input_restarts", MachineFixGameTests::toppedUpInputRestartsMachine, env, 100);
        reg(event, "machine_fix_tanks_reject_unused_fluids", MachineFixGameTests::tanksRejectUnusedFluids, env, 100);
        reg(event, "item_fe_battery_exchange", ItemEnergyGameTests::batteryExchangesFE, env, 60);
        reg(event, "item_fe_aborted_transaction", ItemEnergyGameTests::abortedTransactionLeavesStack, env, 60);
        reg(event, "item_fe_machine_slot_drain", ItemEnergyGameTests::machineSlotDrainsFEItem, env, 60);
        reg(event, "energy_rules_directional_fe_faces", EnergyRulesGameTests::directionalFEFaces, env, 60);
        reg(event, "energy_rules_small_fe_inserts", EnergyRulesGameTests::smallFEInsertsAreExact, env, 60);
        reg(event, "energy_rules_fe_consumer_charge", EnergyRulesGameTests::feConsumerChargedExactly, env, 60);
        reg(event, "energy_rules_neighbour_update", EnergyRulesGameTests::plainNeighbourUpdateKeepsNetwork, env, 60);

        reg(event, "parallel_changed_inputs", ParallelProcessingGameTests::changedInputs, env, 60);
        reg(event, "parallel_processing", ParallelProcessingGameTests::processing, env, 60);
        reg(event, "parallel_fluid_limits", ParallelProcessingGameTests::fluidsAndLimits, env, 60);
        reg(event, "parallel_chance_outputs", ParallelProcessingGameTests::chanceOutputs, env, 60);
        reg(event, "parallel_progress_reload", ParallelProcessingGameTests::progressAndReload, env, 60);
        reg(event, "parallel_power_compatibility", ParallelProcessingGameTests::powerAndCompatibility, env, 60);
        reg(event, "centrifuge_fluid_processing", CentrifugeFluidGameTests::processing, env, 60);
        reg(event, "batch_feeder_jei_ghosts", BatchFeederGameTests::ghostIngredients, env, 60);
        reg(event, "batch_feeder_locks", BatchFeederGameTests::locks, env, 60);
        reg(event, "batch_feeder_atomic_delivery", BatchFeederGameTests::atomicDelivery, env, 60);
        reg(event, "batch_feeder_machine_controls", BatchFeederGameTests::machineAndControls, env, 60);
        reg(event, "batch_feeder_mixed_fluids", BatchFeederGameTests::mixedFluids, env, 60);
        reg(event, "batch_feeder_fluid_buffer", BatchFeederGameTests::fluidBuffer, env, 60);
        reg(event, "batch_feeder_packets_cards", BatchFeederGameTests::packetsAndCards, env, 60);
        reg(event, "centrifuge_fluid_output_blocking", CentrifugeFluidGameTests::outputBlocking, env, 60);
        reg(event, "centrifuge_fluid_fluid_only_cache", CentrifugeFluidGameTests::fluidOnlyAndCache, env, 60);
        reg(
                event,
                "centrifuge_fluid_automation_persistence",
                CentrifugeFluidGameTests::automationAndPersistence,
                env,
                60);
        reg(event, "centrifuge_fluid_containers_slots", CentrifugeFluidGameTests::containersAndSlots, env, 60);
        reg(event, "centrifuge_fluid_block_interaction", CentrifugeFluidGameTests::blockInteraction, env, 60);

        reg(event, "side_configuration_cable_routes", SideConfigurationGameTests::multipleCableRoutes, env, 60);
        reg(event, "side_configuration_full_fe", SideConfigurationGameTests::fullFeMode, env, 60);
        reg(event, "side_configuration_itemtransfers", SideConfigurationGameTests::itemTransfers, env, 60);
        reg(event, "side_configuration_ejector", SideConfigurationGameTests::ejector, env, 60);
        reg(event, "side_configuration_fluidsandteleporter", SideConfigurationGameTests::fluidsAndTeleporter, env, 60);
        reg(event, "side_configuration_nativeenergy", SideConfigurationGameTests::nativeEnergy, env, 60);
        reg(event, "side_configuration_feandports", SideConfigurationGameTests::feAndPorts, env, 60);
        reg(event, "side_configuration_reactorchambers", SideConfigurationGameTests::reactorChambers, env, 60);
        reg(
                event,
                "side_configuration_persistenceandrotation",
                SideConfigurationGameTests::persistenceAndRotation,
                env,
                60);
        reg(event, "side_configuration_cardandpackets", SideConfigurationGameTests::cardAndPackets, env, 60);
        reg(event, "battery_box_configured_output", SideConfigurationGameTests::batteryBoxOutputOnAnySide, env, 60);

        reg(event, "reactor_design_fill", ReactorDesignGameTests::fillsWithoutReplacingOrLosingData, env, 40);
        reg(event, "reactor_fuel_rod_base_output", ReactorDesignGameTests::fuelRodBaseOutput, env, 40);
        reg(event, "reactor_design_requirements", ReactorDesignGameTests::enforcesBuildRequirements, env, 40);
        reg(event, "reactor_design_persistence", ReactorDesignGameTests::persistsBlueprintAndPreview, env, 40);
        reg(event, "reactor_design_validation", ReactorDesignGameTests::rejectsInvalidDesigns, env, 40);
        reg(event, "reactor_design_blueprint_use", ReactorDesignGameTests::blueprintUseLoadsAndCopies, env, 40);
        reg(event, "reactor_design_quick_move", ReactorDesignGameTests::quickMoveRespectsActiveSlots, env, 40);

        reg(event, "basic_generator_burns_coal", FTBICGameTestFunctions::basicGeneratorBurnsCoal, env, 200);
        reg(event, "basic_generator_empty_stays_idle", FTBICGameTestFunctions::basicGeneratorEmptyStaysIdle, env, 200);
        reg(event, "basic_generator_stops_when_full", FTBICGameTestFunctions::basicGeneratorStopsWhenFull, env, 200);
        reg(event, "solar_panel_day", FTBICGameTestFunctions::solarPanelDay, env, 200);
        reg(event, "solar_panel_night", FTBICGameTestFunctions::solarPanelNight, nightEnv, 200);
        reg(event, "solar_panel_obstructed", FTBICGameTestFunctions::solarPanelObstructed, env, 200);
        reg(event, "ev_solar_outputs_more_than_lv", FTBICGameTestFunctions::evSolarOutputsMoreThanLv, env, 200);
        reg(event, "geothermal_consumes_lava", FTBICGameTestFunctions::geothermalConsumesLava, env, 200);
        reg(event, "windmill_outputs", FTBICGameTestFunctions::windmillOutputs, env, 200);

        reg(event, "macerator_produces_bone_meal", FTBICGameTestFunctions::maceratorProducesBoneMeal, env, 400);
        reg(
                event,
                "advanced_macerator_produces_bone_meal",
                FTBICGameTestFunctions::advancedMaceratorProducesBoneMeal,
                env,
                400);
        reg(event, "compressor_produces_sandstone", FTBICGameTestFunctions::compressorProducesSandstone, env, 400);
        reg(
                event,
                "advanced_compressor_produces_sandstone",
                FTBICGameTestFunctions::advancedCompressorProducesSandstone,
                env,
                400);
        reg(event, "centrifuge_produces_flint", FTBICGameTestFunctions::centrifugeProducesFlint, env, 400);
        reg(
                event,
                "centrifuge_rejects_input_below_recipe_count",
                FTBICGameTestFunctions::centrifugeRejectsInputBelowRecipeCount,
                env,
                400);
        reg(
                event,
                "centrifuge_consumes_count_from_input_kelp",
                FTBICGameTestFunctions::centrifugeConsumesCountFromInputKelp,
                env,
                400);
        reg(
                event,
                "advanced_centrifuge_produces_flint",
                FTBICGameTestFunctions::advancedCentrifugeProducesFlint,
                env,
                400);
        reg(event, "powered_furnace_smelts_raw_iron", FTBICGameTestFunctions::poweredFurnaceSmeltsRawIron, env, 400);
        reg(
                event,
                "advanced_powered_furnace_smelts_raw_iron",
                FTBICGameTestFunctions::advancedPoweredFurnaceSmeltsRawIron,
                env,
                400);
        reg(event, "macerator_no_recipe_no_progress", FTBICGameTestFunctions::maceratorNoRecipeNoProgress, env, 200);
        reg(event, "machine_sleeps_when_output_full", FTBICGameTestFunctions::machineSleepsWhenOutputFull, env, 200);
        reg(event, "machine_consumes_energy_per_tick", FTBICGameTestFunctions::machineConsumesEnergyPerTick, env, 200);
        reg(
                event,
                "machine_starving_flag_set_when_energy_depleted",
                FTBICGameTestFunctions::machineStarvingFlagSetWhenEnergyDepleted,
                env,
                100);
        reg(
                event,
                "machine_not_starving_without_recipe",
                FTBICGameTestFunctions::machineNotStarvingWithoutRecipe,
                env,
                100);
        reg(
                event,
                "machine_starving_clears_when_energy_restored",
                FTBICGameTestFunctions::machineStarvingClearsWhenEnergyRestored,
                env,
                100);

        reg(event, "overclocker_increases_speed", FTBICGameTestFunctions::overclockerIncreasesSpeed, env, 100);
        reg(event, "overclocker_increases_energy_use", FTBICGameTestFunctions::overclockerIncreasesEnergyUse, env, 100);
        reg(
                event,
                "stacked_overclockers_multiplicative",
                FTBICGameTestFunctions::stackedOverclockersMultiplicative,
                env,
                100);
        reg(
                event,
                "transformer_upgrade_increases_input_cap",
                FTBICGameTestFunctions::transformerUpgradeIncreasesInputCap,
                env,
                100);
        reg(
                event,
                "max_transformer_upgrades_accept_any_input",
                FTBICGameTestFunctions::maxTransformerUpgradesAcceptAnyInput,
                env,
                100);
        reg(
                event,
                "storage_upgrade_increases_capacity",
                FTBICGameTestFunctions::storageUpgradeIncreasesCapacity,
                env,
                100);
        reg(event, "ejector_upgrade_sets_auto_eject", FTBICGameTestFunctions::ejectorUpgradeSetsAutoEject, env, 100);
        reg(event, "upgrade_persisted_across_save", FTBICGameTestFunctions::upgradePersistedAcrossSave, env, 100);

        reg(event, "battery_box_drains_input_battery", FTBICGameTestFunctions::batteryBoxDrainsInputBattery, env, 200);
        reg(
                event,
                "battery_box_charges_output_battery",
                FTBICGameTestFunctions::batteryBoxChargesOutputBattery,
                env,
                200);
        reg(event, "battery_box_output_face_only", FTBICGameTestFunctions::batteryBoxOutputFaceOnly, env, 100);
        reg(event, "transformer_face_geometry", FTBICGameTestFunctions::transformerFaceGeometry, env, 100);
        reg(
                event,
                "energy_tier_transfer_rates_match_config",
                FTBICGameTestFunctions::energyTierTransferRatesMatchConfig,
                env,
                20);
        reg(event, "lv_cable_survives_within_rate", FTBICGameTestFunctions::lvCableSurvivesWithinRate, env, 200);
        reg(event, "lv_cable_burns_when_overloaded", FTBICGameTestFunctions::lvCableBurnsWhenOverloaded, env, 200);
        reg(event, "overload_burns_entire_lv_subnet", FTBICGameTestFunctions::overloadBurnsEntireLvSubnet, env, 200);
        reg(event, "transformer_steps_mv_down_to_lv", FTBICGameTestFunctions::transformerStepsMvDownToLv, env, 200);
        reg(event, "rectifier_converts_fe_to_zaps", FTBICGameTestFunctions::rectifierConvertsFeToZaps, env, 100);
        reg(
                event,
                "rectifier_feeds_downstream_machine",
                FTBICGameTestFunctions::rectifierFeedsDownstreamMachine,
                env,
                200);
        reg(event, "rectifier_face_geometry", FTBICGameTestFunctions::rectifierFaceGeometry, env, 100);
        reg(
                event,
                "rectifier_roundtrip_conserves_energy",
                FTBICGameTestFunctions::rectifierRoundtripConservesEnergy,
                env,
                200);
        reg(
                event,
                "rectifier_fe_insert_is_transactional",
                FTBICGameTestFunctions::rectifierFeInsertIsTransactional,
                env,
                100);
        reg(event, "cable_connects_gen_to_machine", FTBICGameTestFunctions::cableConnectsGenToMachine, env, 200);
        reg(event, "burnt_cable_does_not_conduct", FTBICGameTestFunctions::burntCableDoesNotConduct, env, 200);
        reg(
                event,
                "burnt_cable_state_retains_cable_shape",
                FTBICGameTestFunctions::burntCableStateRetainsCableShape,
                env,
                100);
        reg(
                event,
                "network_distributes_to_multiple_machines",
                FTBICGameTestFunctions::networkDistributesToMultipleMachines,
                env,
                200);
        reg(event, "network_rebuild_on_cable_removal", FTBICGameTestFunctions::networkRebuildOnCableRemoval, env, 200);
        reg(event, "quarry_filter_selection", QuarryFilterGameTests::selection, env, 100);
        reg(event, "quarry_filter_free_skip", QuarryFilterGameTests::freeSkipAndDepth, env, 100);
        reg(event, "quarry_filter_no_target", QuarryFilterGameTests::noTargetAndUpgrade, env, 100);
        reg(event, "quarry_filter_packets_card", QuarryFilterGameTests::packetsAndCard, env, 100);
        reg(event, "quarry_paused_without_energy", FTBICGameTestFunctions::quarryPausedWithoutEnergy, env, 200);
        reg(
                event,
                "quarry_redstone_pause_flag_sets_on_signal",
                FTBICGameTestFunctions::quarryRedstonePauseFlagSetsOnSignal,
                env,
                100);
        reg(event, "pump_extracts_adjacent_water", FTBICGameTestFunctions::pumpExtractsAdjacentWater, env, 200);

        reg(
                event,
                "reactor_placed_defaults_to_paused",
                FTBICGameTestFunctions::reactorPlacedDefaultsToPaused,
                env,
                100);
        reg(
                event,
                "reactor_attached_chambers_increase_columns",
                FTBICGameTestFunctions::reactorAttachedChambersIncreaseColumns,
                env,
                100);
        reg(
                event,
                "reactor_counts_attached_chambers_up_to_6",
                FTBICGameTestFunctions::reactorCountsAttachedChambersUpTo6,
                env,
                100);
        reg(event, "reactor_detonates_at_max_heat", FTBICGameTestFunctions::reactorDetonatesAtMaxHeat, env, 200);

        reg(
                event,
                "antimatter_constructor_progresses",
                FTBICGameTestFunctions::antimatterConstructorProgresses,
                env,
                100);
        reg(
                event,
                "powered_crafting_table_crafts_planks_into_table",
                FTBICGameTestFunctions::poweredCraftingTableCraftsPlanksIntoTable,
                env,
                100);
        reg(
                event,
                "charge_pad_transfers_energy_from_buffer_to_stack",
                FTBICGameTestFunctions::chargePadTransfersEnergyFromBufferToStack,
                env,
                100);
        reg(
                event,
                "charge_pad_charges_inserted_items",
                FTBICGameTestFunctions::chargePadChargesInsertedItems,
                env,
                100);
        reg(event, "charge_pad_charges_worn_armor", FTBICGameTestFunctions::chargePadChargesWornArmor, env, 100);
        reg(
                event,
                "carbon_armor_needs_power_for_protection",
                FTBICGameTestFunctions::carbonArmorNeedsPowerForProtection,
                env,
                100);
        reg(
                event,
                "quantum_armor_needs_power_for_protection",
                FTBICGameTestFunctions::quantumArmorNeedsPowerForProtection,
                env,
                100);
        reg(
                event,
                "quantum_glider_stops_without_power",
                FTBICGameTestFunctions::quantumGliderStopsWithoutPower,
                env,
                100);
        reg(event, "quantum_hover_flight_needs_power", FTBICGameTestFunctions::quantumHoverFlightNeedsPower, env, 100);
        reg(
                event,
                "quantum_hover_preserves_other_flight",
                FTBICGameTestFunctions::quantumHoverPreservesOtherFlight,
                env,
                100);

        reg(
                event,
                "rechargeable_battery_accepts_and_holds_energy",
                FTBICGameTestFunctions::rechargeableBatteryAcceptsAndHoldsEnergy,
                env,
                100);
        reg(
                event,
                "rechargeable_battery_clears_component_at_zero",
                FTBICGameTestFunctions::rechargeableBatteryClearsComponentAtZero,
                env,
                100);
        reg(
                event,
                "single_use_battery_shrinks_at_zero",
                FTBICGameTestFunctions::singleUseBatteryShrinksAtZero,
                env,
                100);
        reg(
                event,
                "single_use_battery_cannot_be_recharged",
                FTBICGameTestFunctions::singleUseBatteryCannotBeRecharged,
                env,
                100);

        reg(
                event,
                "fluid_cell_fills_from_water_on_use",
                FTBICGameTestFunctions::fluidCellFillsFromWaterOnUse,
                env,
                100);
        reg(
                event,
                "fluid_cell_ingredient_displays_filled_stack",
                FTBICGameTestFunctions::fluidCellIngredientDisplaysFilledStack,
                env,
                100);
        reg(event, "scrap_box_gives_reward_on_use", FTBICGameTestFunctions::scrapBoxGivesRewardOnUse, env, 100);
        reg(
                event,
                "component_loot_box_opens_whole_stack_when_crouching",
                FTBICGameTestFunctions::componentLootBoxOpensWholeStackWhenCrouching,
                env,
                100);
        reg(event, "loot_box_ignores_missing_table", FTBICGameTestFunctions::lootBoxIgnoresMissingTable, env, 100);
        reg(event, "canned_food_returns_empty_can", FTBICGameTestFunctions::cannedFoodReturnsEmptyCan, env, 100);

        reg(
                event,
                "quarry_pickaxe_silk_touch_produces_stone",
                FTBICGameTestFunctions::quarryPickaxeSilkTouchProducesStone,
                env,
                100);
        reg(
                event,
                "quarry_without_pickaxe_produces_cobble",
                FTBICGameTestFunctions::quarryWithoutPickaxeProducesCobble,
                env,
                100);
        reg(
                event,
                "quarry_efficiency_pickaxe_speeds_up_mining",
                FTBICGameTestFunctions::quarryEfficiencyPickaxeSpeedsUpMining,
                env,
                100);
        reg(
                event,
                "quarry_pickaxe_rejects_non_pickaxe",
                FTBICGameTestFunctions::quarryPickaxeRejectsNonPickaxe,
                env,
                100);

        reg(
                event,
                "teleporter_pipe_forwards_item_to_peer",
                FTBICGameTestFunctions::teleporterPipeForwardsItemToPeer,
                env,
                200);
        reg(
                event,
                "teleporter_pipe_extracts_from_peer",
                FTBICGameTestFunctions::teleporterPipeExtractsFromPeer,
                env,
                200);
        reg(
                event,
                "teleporter_pipe_forwards_fluid_to_peer",
                FTBICGameTestFunctions::teleporterPipeForwardsFluidToPeer,
                env,
                200);
        reg(event, "teleporter_pipe_clear_storage", FTBICGameTestFunctions::teleporterPipeClearStorage, env, 100);
        reg(event, "teleporter_pipe_clear_fluids", FTBICGameTestFunctions::teleporterPipeClearFluids, env, 100);
        reg(
                event,
                "teleporter_pipe_drain_on_activity",
                FTBICGameTestFunctions::teleporterPipeDrainOnActivity,
                env,
                200);
        reg(event, "teleporter_pipe_balances_energy", FTBICGameTestFunctions::teleporterPipeBalancesEnergy, env, 200);
        reg(
                event,
                "teleporter_idle_pair_does_not_drain",
                FTBICGameTestFunctions::idleTeleporterPairDoesNotDrain,
                env,
                200);
        reg(
                event,
                "teleporter_exposes_energy_cap_both_directions",
                FTBICGameTestFunctions::teleporterExposesEnergyCapBothDirections,
                env,
                40);
        reg(
                event,
                "teleporter_pair_relays_power_for_remote_extract",
                FTBICGameTestFunctions::teleporterPairRelaysPowerForRemoteExtract,
                env,
                200);
        reg(
                event,
                "teleporter_filters_other_teleporters_from_push_network",
                FTBICGameTestFunctions::teleporterFiltersOtherTeleportersFromPushNetwork,
                env,
                40);

        reg(
                event,
                "reactor_simulator_runs_and_emits_power",
                FTBICGameTestFunctions::reactorSimulatorRunsAndEmitsPower,
                env,
                200);
        reg(event, "reactor_simulator_edit_lock", FTBICGameTestFunctions::reactorSimulatorEditLock, env, 100);
        reg(
                event,
                "reactor_simulator_import_roundtrip",
                FTBICGameTestFunctions::reactorSimulatorImportRoundtrip,
                env,
                100);

        reg(
                event,
                "insert_accepts_partial_when_slot_overflows",
                FTBICGameTestFunctions::insertAcceptsPartialWhenSlotOverflows,
                env,
                40);
        reg(
                event,
                "machine_stall_on_full_output_does_not_drop_items",
                FTBICGameTestFunctions::machineStallOnFullOutputDoesNotDropItems,
                env,
                220);
        reg(
                event,
                "overclocker_progress_speed_scales_as_power",
                FTBICGameTestFunctions::overclockerProgressSpeedScalesAsPower,
                env,
                40);
        reg(
                event,
                "transaction_abort_restores_multiple_slots",
                FTBICGameTestFunctions::transactionAbortRestoresMultipleSlots,
                env,
                40);
        reg(
                event,
                "transaction_nested_abort_preserves_outer_writes",
                FTBICGameTestFunctions::transactionNestedAbortPreservesOuterWrites,
                env,
                40);
        reg(
                event,
                "transaction_nested_commit_merges_into_outer",
                FTBICGameTestFunctions::transactionNestedCommitMergesIntoOuter,
                env,
                40);

        reg(
                event,
                "zap_cap_present_on_every_electric_block",
                FTBICGameTestFunctions::zapCapPresentOnEveryElectricBlock,
                env,
                100);
        reg(event, "zap_cap_null_side_returns_handler", FTBICGameTestFunctions::zapCapNullSideReturnsHandler, env, 40);
        reg(event, "zap_cap_absent_on_vanilla_blocks", FTBICGameTestFunctions::zapCapAbsentOnVanillaBlocks, env, 40);
        reg(
                event,
                "zap_cap_forwards_through_reactor_chamber",
                FTBICGameTestFunctions::zapCapForwardsThroughReactorChamber,
                env,
                40);
        reg(
                event,
                "zap_cap_cache_invalidates_on_block_removal",
                FTBICGameTestFunctions::zapCapCacheInvalidatesOnBlockRemoval,
                env,
                40);
        reg(
                event,
                "zap_cap_cache_updates_on_block_swap",
                FTBICGameTestFunctions::zapCapCacheUpdatesOnBlockSwap,
                env,
                40);

        reg(
                event,
                "nuclear_explosion_destroys_unshielded_block",
                FTBICGameTestFunctions::nuclearExplosionDestroysUnshieldedBlock,
                env,
                20);
        reg(
                event,
                "nuclear_explosion_preserves_reinforced_block",
                FTBICGameTestFunctions::nuclearExplosionPreservesReinforcedBlock,
                env,
                20);
        reg(
                event,
                "nuclear_explosion_shielded_by_reinforced_wall",
                FTBICGameTestFunctions::nuclearExplosionShieldedByReinforcedWall,
                env,
                20);
        reg(
                event,
                "nuclear_fallout_shielded_by_reinforced_wall",
                FTBICGameTestFunctions::nuclearFalloutShieldedByReinforcedWall,
                env,
                20);
    }

    private static void reg(
            RegisterGameTestsEvent event,
            String name,
            Consumer<GameTestHelper> function,
            Holder<TestEnvironmentDefinition<?>> environment,
            int timeoutTicks) {
        TestData<Holder<TestEnvironmentDefinition<?>>> testData =
                new TestData<>(environment, EMPTY_STRUCTURE, timeoutTicks, 0, true);
        GameTestInstance instance = new DirectGameTestInstance(name, function, testData);
        event.registerTest(FTBIC.id(name), instance);
    }
}
