package dev.ftb.mods.ftbic.datagen;

import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.material.MaterialEntries;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class FTBICLanguageProvider extends LanguageProvider {
    public FTBICLanguageProvider(PackOutput output) {
        super(output, FTBIC.MOD_ID, "en_us");
    }

    @Override
    protected void addTranslations() {
        add("subtitles.ftbic.macerator", "Macerator grinds");
        add("subtitles.ftbic.compressor", "Compressor presses");
        add("subtitles.ftbic.centrifuge", "Centrifuge spins");
        add("subtitles.ftbic.ore_washer", "Water circulates");
        add("subtitles.ftbic.reactor_warning", "Reactor heat warning");
        add("subtitles.ftbic.reactor_critical", "Reactor heat critical");
        add("subtitles.ftbic.nuke_arm", "Nuke armed");
        add("subtitles.ftbic.nuke_tick", "Nuke countdown");
        add("subtitles.ftbic.generator", "Generator runs");
        add("subtitles.ftbic.charge_complete", "Charging complete");
        add("subtitles.ftbic.teleport", "Teleporter activates");
        add("subtitles.ftbic.nuclear_explosion", "Nuclear explosion");
        add("subtitles.ftbic.radiation", "Radiation crackles");
        add("block.ftbic.industrial_bank_cell", "Industrial Battery Bank Cell");
        add("item.ftbic.industrial_bank_cell", "Industrial Battery Bank Cell");
        add("block.ftbic.industrial_bank_port", "Industrial Battery Bank Port");
        add("item.ftbic.industrial_bank_port", "Industrial Battery Bank Port");
        add("ftbic.bank.title", "Industrial Battery Bank");
        add("ftbic.bank.stored", "STORED ENERGY");
        add("ftbic.bank.members", "Cells: %s    Ports: %s");
        add("ftbic.bank.charge", "Charge");
        add("ftbic.bank.charge_hint", "Use a port to charge items");
        add("ftbic.bank_port.style.port", "Port texture");
        add("ftbic.bank_port.style.gauge", "Gauge texture");
        add("ftbic.bank_port.style.basic", "Basic texture");
        add("item.ftbic.configuration_card", "Configuration Card");
        add("item.ftbic.configuration_card.copy", "Sneak-use to copy sides, input locks, and batch settings.");
        add("item.ftbic.configuration_card.use", "Use on the same machine type to apply. Reusable.");
        add("item.ftbic.configuration_card.contents", "Settings: %s");
        add("item.ftbic.configuration_card.copied", "Machine settings copied.");
        add("item.ftbic.configuration_card.applied", "Machine settings applied.");
        add("item.ftbic.configuration_card.blank", "Blank card: sneak-use on a machine to copy its settings.");
        add("item.ftbic.configuration_card.mismatch", "These settings belong to a different machine type.");
        add("ftbic.gui.centrifuge.input_tank", "Fluid input: %s (%s / %s mB)");
        add("ftbic.gui.centrifuge.output_tank", "Fluid output: %s (%s / %s mB)");
        add("block.ftbic.hydroponic_accelerator", "Hydroponic Accelerator");
        add("item.ftbic.hydroponic_accelerator", "Hydroponic Accelerator");
        add("block.ftbic.advanced_hydroponic_accelerator", "Advanced Hydroponic Accelerator");
        add("item.ftbic.advanced_hydroponic_accelerator", "Advanced Hydroponic Accelerator");
        add("ftbic.hydro.growth", "Growth");
        add("ftbic.hydro.mutation", "Mutation");
        add("ftbic.hydro.water", "Water");
        add("ftbic.hydro.seed", "Seed input");
        add("ftbic.hydro.soil", "Reusable soil input");
        add("ftbic.hydro.second_seed", "Second parent seed (Mutation mode)");
        add("ftbic.hydro.product", "Produce or mutation result");
        add("ftbic.hydro.returned_seed", "Returned seeds");
        add("ftbic.hydro.second_seed_output", "Second parent's returned seeds");
        add("ftbic.hydro.byproduct", "Chanced byproduct");
        add("ftbic.hydro.soil_speed", "Soil speed: %sx");
        add("ftbic.hydro.mutation_failure", "Failed mutation returns both parent seeds");
        add("ftbic.jei.hydroponic_growth", "Hydroponic Growth");
        add("ftbic.jei.hydroponic_mutation", "Hydroponic Mutation");
        add("ftbic.jei.parents", "Parents");
        add("ftbic.jei.mutation_result", "Result");
        add("ftbic.jei.mutation_failed", "On failure:");
        add("ftbic.jei.mutation_returns", "Both seeds returned");
        add("ftbic.jei.mutation_cost", "%ss | %s");
        add("ftbic.jei.advanced_centrifuge_required", "Advanced Centrifuge only");
        add("ftbic.sides.title", "Side Configuration");
        add("block.ftbic.batch_feeder", "Batch Feeder");
        add("ftbic.locks.title", "Input Slot Locks");
        add("ftbic.locks.slot", "Input %s");
        add("ftbic.locks.assignment", "%s: %s");
        add("ftbic.locks.unlocked", "Unlocked");
        add("ftbic.locks.locked", "Locked to %s. Use L or drag from JEI.");
        add("ftbic.locks.assign_hint", "Click a row or drag an item from JEI");
        add("ftbic.locks.furnace_locked", "Locked to %s.\nRight-click an empty ghost or Shift-right-click to unlock.");
        add("ftbic.locks.clear_hint", "Right-click / Shift-click: unlock");
        add("ftbic.batch.buffer", "Buffer");
        add("ftbic.batch.pattern", "Batch");
        add("ftbic.batch.slot", "Batch ingredient %s");
        add(
                "ftbic.batch.edit_hint",
                "Click with an item or drag from JEI: set item and count.\nEmpty cursor: left +1, right -1.\nShift-click: clear. Items are not consumed.");
        add("ftbic.batch.fluid_buffer", "Fluid buffer");
        add("ftbic.batch.fluid_buffer_hint", "Buffer: %s (%s / %s mB)\nClick with a fluid container to fill or drain.");
        add("ftbic.batch.fluid_ingredient", "Batch fluid");
        add(
                "ftbic.batch.fluid_ingredient_hint",
                "Batch fluid: %s (%s mB)\nClick with a filled container or drag fluid from JEI.\nEmpty cursor: copy buffer fluid.\nRight-click / Shift-click: clear.");
        add("ftbic.batch.fluid_amount", "Fluid mB");
        add("ftbic.batch.fluid_amount_hint", "Amount per batch: 1 to %s mB.\nPress Set or Enter to apply.");
        add("ftbic.batch.set", "Set");
        add(
                "ftbic.batch.transfer_hint",
                "Sends complete batches through the front.\nNo power required; a redstone signal pauses feeding.\nWaits if any ingredient or destination space is missing.");
        add("ftbic.sides.button", "I/O");
        add("ftbic.sides.reset", "Reset to Defaults");
        add("ftbic.sides.default_hint", "* Default uses the machine's original rules.");
        add("ftbic.sides.face_hint", "%s (%s): %s");
        add("ftbic.sides.reset_hint", "Restore every face and resource to Default.");
        add("ftbic.sides.cycle_hint", "Left-click: next | Right-click: previous");
        add("ftbic.sides.shift_hint", "Shift-click: reset this face");
        add("ftbic.sides.modes", "Face modes");
        add("ftbic.sides.front_view", "Viewed from front");
        add("ftbic.sides.items", "Items");
        add("ftbic.sides.fluids", "Fluids");
        add("ftbic.sides.energy", "Energy");
        add("ftbic.sides.default", "Default");
        add("ftbic.sides.disabled", "Disabled");
        add("ftbic.sides.input", "Input");
        add("ftbic.sides.output", "Output");
        add("ftbic.sides.both", "Both");
        add("ftbic.sides.front", "Front");
        add("ftbic.sides.back", "Back");
        add("ftbic.sides.left", "Left");
        add("ftbic.sides.right", "Right");
        add("ftbic.sides.top", "Top");
        add("ftbic.sides.bottom", "Bottom");
        add("ftbic.sides.direction.north", "North");
        add("ftbic.sides.direction.south", "South");
        add("ftbic.sides.direction.east", "East");
        add("ftbic.sides.direction.west", "West");
        add("ftbic.sides.direction.up", "Up");
        add("ftbic.sides.direction.down", "Down");
        add("item.ftbic.reactor_blueprint", "Reactor Blueprint");
        add("item.ftbic.reactor_blueprint.blank", "Blank: use on a Reactor Planner to copy its design.");
        add("item.ftbic.reactor_blueprint.contents", "%s chambers, %s components");
        add("item.ftbic.reactor_blueprint.load", "Use on a reactor or planner to load the design.");
        add("item.ftbic.reactor_blueprint.write", "Sneak-use on a planner to overwrite this blueprint.");
        add("item.ftbic.reactor_blueprint.written", "Reactor design written to blueprint.");
        add(
                "item.ftbic.reactor_blueprint.need_blank",
                "Keep a blank Reactor Blueprint in your inventory to write a design.");
        add("item.ftbic.reactor_blueprint.planner_locked", "Pause the planner before loading a valid blueprint.");
        add("ftbic.reactor.design.title", "Reactor Design");
        add("ftbic.reactor.design.load", "Load Preset");
        add("ftbic.reactor.design.paste", "Paste Design");
        add("ftbic.reactor.design.load_short", "Load");
        add("ftbic.reactor.design.paste_short", "Paste");
        add("ftbic.reactor.design.build", "Build from Inventory");
        add("ftbic.reactor.design.blueprint", "Write Blueprint");
        add("ftbic.reactor.design.clear", "Clear");
        add("ftbic.reactor.design.clear_hint", "Clear the design preview. Installed components stay in the reactor.");
        add("ftbic.reactor.design.no_design", "Load a design to begin.");
        add("ftbic.reactor.design.chambers", "Chambers: %s / %s");
        add("ftbic.reactor.design.cooling", "Water: %s%% / %s%%");
        add("ftbic.reactor.design.remaining", "Missing: %s  Conflicts: %s");
        add("ftbic.reactor.design.materials", "Materials still needed");
        add("ftbic.reactor.design.count", "%s available / %s needed");
        add("ftbic.reactor.design.count_short", "Have %s / Need %s");
        add("ftbic.reactor.design.expected", "Design requires: %s");
        add("ftbic.reactor.design.unexpected", "Design requires this slot to be empty.");
        add(
                "ftbic.reactor.design.build_hint",
                "Fill empty slots from your inventory. Remove red-marked conflicts manually. Components keep their heat and wear.");
        add(
                "ftbic.reactor.design.build_requirements",
                "Load a design, attach the required chambers, pause the reactor and disable redstone control before building.");
        add(
                "ftbic.reactor.design.environment_hint",
                "Actual / planned chambers and water cooling. Match the planner's environment before starting the reactor.");
        add("ftbic.reactor.design.invalid", "Invalid reactor design. Copy a design from the planner and try again.");
        add("ftbic.reactor.design.chambers_missing", "Attach the design's required reactor chambers before building.");
        add(
                "ftbic.reactor.design.pause_required",
                "Load a design, pause the reactor and disable redstone control before building.");
        add(
                "ftbic.reactor.design.built",
                "Placed %s components. Missing parts remain as ghosts; remove conflicting parts manually.");
        MaterialEntries.all().forEach(entry -> {
            String text = entry.component().translation(entry.material().displayName());
            String suffix = FTBIC.MOD_ID + "." + entry.name();
            if (entry.component().isBlock()) {
                add("block." + suffix, text);
                add("item." + suffix, text);
            } else {
                add("item." + suffix, text);
            }
        });

        add("itemGroup.ftbic", "FTB Industrial Contraptions");

        add("block.ftbic.active_nuke", "Active Nuke");
        add("block.ftbic.advanced_centrifuge", "Advanced Centrifuge");
        add("block.ftbic.advanced_compressor", "Advanced Compressor");
        add("block.ftbic.advanced_ore_washer", "Advanced Ore Washer");
        add("block.ftbic.advanced_canning_machine", "Advanced Canning Machine");
        add("block.ftbic.advanced_roller", "Advanced Roller");
        add("block.ftbic.advanced_extruder", "Advanced Extruder");
        add("block.ftbic.advanced_macerator", "Advanced Macerator");
        add("block.ftbic.advanced_machine_block", "Advanced Machine Block");
        add("block.ftbic.advanced_powered_furnace", "Advanced Powered Furnace");
        add("block.ftbic.alloy_smelter", "Alloy Smelter");
        add("block.ftbic.antimatter_constructor", "Antimatter Constructor");
        add("block.ftbic.basic_generator", "Basic Generator");
        add("block.ftbic.burnt_cable", "Burnt Cable");
        add("block.ftbic.burnt_reinforced_cable", "Burnt Reinforced Cable");
        add("block.ftbic.canning_machine", "Canning Machine");
        add("block.ftbic.ore_washer", "Ore Washer");
        add("item.ftbic.ore_washer", "Ore Washer");
        add("item.ftbic.crushed_ore", "Crushed Ore");
        add("item.ftbic.washed_ore", "Washed Ore");
        add("item.ftbic.refined_concentrate", "Refined Concentrate");
        add("item.ftbic.crushed_ore.material", "Crushed %s");
        add("item.ftbic.washed_ore.material", "Washed %s");
        add("item.ftbic.refined_concentrate.material", "Refined %s Concentrate");
        add("ftbic.upgrade.cannot_insert", "This machine cannot accept more of that upgrade");
        add("block.ftbic.centrifuge", "Centrifuge");
        add("block.ftbic.charge_pad", "Charge Pad");
        add("block.ftbic.compressor", "Compressor");
        add("block.ftbic.enderium_block", "Block of Enderium");
        add("block.ftbic.ev_battery_box", "EV Battery Box");
        add("block.ftbic.ev_cable", "EV Cable");
        add("block.ftbic.ev_reinforced_cable", "Reinforced EV Cable");
        add("block.ftbic.ev_rectifier", "EV Energy Rectifier");
        add("block.ftbic.ev_solar_panel", "EV Solar Panel");
        add("block.ftbic.ev_transformer", "EV Transformer");
        add("block.ftbic.exfluid", "Ex-Fluid");
        add("block.ftbic.extruder", "Extruder");
        add("block.ftbic.geothermal_generator", "Geothermal Generator");
        add("block.ftbic.hv_battery_box", "HV Battery Box");
        add("block.ftbic.hv_cable", "HV Cable");
        add("block.ftbic.hv_reinforced_cable", "Reinforced HV Cable");
        add("block.ftbic.hv_rectifier", "HV Energy Rectifier");
        add("block.ftbic.hv_solar_panel", "HV Solar Panel");
        add("block.ftbic.hv_transformer", "HV Transformer");
        add("block.ftbic.iron_furnace", "Iron Furnace");
        add("block.ftbic.iv_cable", "IV Cable");
        add("block.ftbic.superconducting_cable", "Superconducting Cable");
        add("item.ftbic.superconducting_cable", "Superconducting Cable");
        add("block.ftbic.iv_reinforced_cable", "Reinforced IV Cable");
        add("block.ftbic.iv_rectifier", "IV Energy Rectifier");
        add("block.ftbic.landmark", "Landmark");
        add("block.ftbic.lv_battery_box", "LV Battery Box");
        add("block.ftbic.lv_cable", "LV Cable");
        add("block.ftbic.lv_reinforced_cable", "Reinforced LV Cable");
        add("block.ftbic.lv_rectifier", "LV Energy Rectifier");
        add("block.ftbic.lv_solar_panel", "LV Solar Panel");
        add("block.ftbic.lv_transformer", "LV Transformer");
        add("block.ftbic.macerator", "Macerator");
        add("block.ftbic.machine_block", "Machine Block");
        add("block.ftbic.mv_battery_box", "MV Battery Box");
        add("block.ftbic.mv_cable", "MV Cable");
        add("block.ftbic.mv_reinforced_cable", "Reinforced MV Cable");
        add("block.ftbic.mv_rectifier", "MV Energy Rectifier");
        add("block.ftbic.mv_solar_panel", "MV Solar Panel");
        add("block.ftbic.mv_transformer", "MV Transformer");
        add("block.ftbic.nuclear_reactor", "Nuclear Reactor");
        add("block.ftbic.nuclear_reactor.broadcast", "%s forgot to cool their Nuclear Reactor!");
        add("block.ftbic.nuclear_reactor_chamber", "Nuclear Reactor Chamber");
        add("block.ftbic.nuke", "Nuke");
        add("block.ftbic.nuke.broadcast", "%s triggered a nuke!");
        add("block.ftbic.powered_crafting_table", "Powered Crafting Table");
        add("block.ftbic.powered_furnace", "Powered Furnace");
        add("block.ftbic.pump", "Pump");
        add("block.ftbic.quarry", "Quarry");
        add("block.ftbic.reactor_simulator", "Reactor Simulator");
        add("block.ftbic.reinforced_glass", "Reinforced Glass");
        add("block.ftbic.reinforced_stone", "Reinforced Stone");
        add("block.ftbic.reprocessor", "Reprocessor");
        add("block.ftbic.roller", "Roller");
        add("block.ftbic.rubber_sheet", "Rubber Sheet");
        add("block.ftbic.teleporter", "Teleporter");
        add("block.ftbic.teleporter.empty", "No destinations available. Name a peer teleporter and own/publish it.");
        add("block.ftbic.teleporter.load_error", "The destination chunk has to be loaded!");
        add("block.ftbic.teleporter.name_hint", "Teleporter ID");
        add("block.ftbic.teleporter.perm_error", "Only the owner of this teleporter can change its settings!");
        add("block.ftbic.teleporter.pick_header", "Click a destination to link:");
        add("block.ftbic.wind_mill", "Wind Mill");

        add("item.ftbic.active_nuke", "Active Nuke");
        add("item.ftbic.advanced_alloy", "Advanced Alloy");
        add("item.ftbic.advanced_centrifuge", "Advanced Centrifuge");
        add("item.ftbic.advanced_circuit", "Advanced Circuit");
        add("item.ftbic.advanced_compressor", "Advanced Compressor");
        add("item.ftbic.advanced_ore_washer", "Advanced Ore Washer");
        add("item.ftbic.advanced_canning_machine", "Advanced Canning Machine");
        add("item.ftbic.advanced_roller", "Advanced Roller");
        add("item.ftbic.advanced_extruder", "Advanced Extruder");
        add("item.ftbic.advanced_heat_exchanger", "Advanced Heat Exchanger");
        add("item.ftbic.advanced_heat_vent", "Advanced Heat Vent");
        add("item.ftbic.advanced_macerator", "Advanced Macerator");
        add("item.ftbic.advanced_machine_block", "Advanced Machine Block");
        add("item.ftbic.advanced_powered_furnace", "Advanced Powered Furnace");
        add("item.ftbic.alloy_smelter", "Alloy Smelter");
        add("item.ftbic.antimatter", "Antimatter");
        add("item.ftbic.antimatter_constructor", "Antimatter Constructor");
        add("item.ftbic.antimatter_crystal", "Antimatter Crystal");
        add("item.ftbic.basic_generator", "Basic Generator");
        add("item.ftbic.batch_feeder", "Batch Feeder");
        add("item.ftbic.burnt_cable", "Burnt Cable");
        add("item.ftbic.burnt_reinforced_cable", "Burnt Reinforced Cable");
        add("item.ftbic.canned_food", "Canned Food");
        add("item.ftbic.canning_machine", "Canning Machine");
        add("item.ftbic.carbon_boots", "Carbon Boots");
        add("item.ftbic.carbon_chestplate", "Carbon Chestplate");
        add("item.ftbic.carbon_fiber_mesh", "Carbon Fiber Mesh");
        add("item.ftbic.carbon_fibers", "Carbon Fibers");
        add("item.ftbic.carbon_helmet", "Carbon Helmet");
        add("item.ftbic.carbon_leggings", "Carbon Leggings");
        add("item.ftbic.carbon_plate", "Carbon Plate");
        add("item.ftbic.centrifuge", "Centrifuge");
        add("item.ftbic.charge_pad", "Charge Pad");
        add("item.ftbic.coal_ball", "Coal Ball");
        add("item.ftbic.component_heat_exchanger", "Component Heat Exchanger");
        add("item.ftbic.component_heat_vent", "Component Heat Vent");
        add("item.ftbic.compressed_coal_ball", "Compressed Coal Ball");
        add("item.ftbic.compressor", "Compressor");
        add("item.ftbic.containment_reactor_plating", "Containment Reactor Plating");
        add("item.ftbic.copper_coil", "Copper Coil");
        add("item.ftbic.creative_battery", "Creative Battery");
        add("item.ftbic.dark_spray_paint_can", "Spray Paint Can (Dark)");
        add("item.ftbic.dense_copper_plate", "Dense Copper Plate");
        add("item.ftbic.dual_uranium_fuel_rod", "Dual Uranium Fuel Rod");
        add("item.ftbic.quarry_filter_upgrade", "Quarry Filter Upgrade");
        add("ftbic.quarry_filter.title", "Quarry Filter");
        add("ftbic.quarry_filter.blocks", "Blocks");
        add("ftbic.quarry_filter.tags", "Block tags");
        add("ftbic.quarry_filter.tag", "Block tag %s");
        add("ftbic.quarry_filter.tag_hint", "Enter a block tag such as c:ores/iron, then press Enter.");
        add(
                "ftbic.quarry_filter.empty_slot",
                "Click with a block in hand, or drag a block from JEI. Right-click to clear.");
        add("ftbic.quarry_filter.whitelist", "Whitelist");
        add("ftbic.quarry_filter.blacklist", "Blacklist");
        add("ftbic.quarry_filter.ores_on", "Ore-only: On");
        add("ftbic.quarry_filter.ores_off", "Ore-only: Off");
        add("ftbic.quarry_filter.active", "Filter active");
        add("ftbic.quarry_filter.inactive", "Install a Quarry Filter Upgrade.");
        add("item.ftbic.parallel_processing_upgrade", "Parallel Processing Upgrade");
        add(
                "item.ftbic.parallel_processing_upgrade.description",
                "Advanced processors: +1 operation per upgrade (maximum 3 upgrades).");
        add(
                "item.ftbic.parallel_processing_upgrade.energy",
                "Runs copies of one recipe together; each operation uses full energy.");
        add("ftbic.gui.machine.parallel", "%s/%s");
        add("ftbic.gui.machine.parallel_tooltip", "Parallel operations: %s running / %s maximum");
        add("item.ftbic.ejector_upgrade", "Ejector Upgrade");
        add("item.ftbic.electronic_circuit", "Electronic Circuit");
        add("item.ftbic.empty_can", "Empty Can");
        add("item.ftbic.enderium_block", "Block of Enderium");
        add("item.ftbic.enderium_dust", "Enderium Dust");
        add("item.ftbic.enderium_gear", "Enderium Gear");
        add("item.ftbic.enderium_ingot", "Enderium Ingot");
        add("item.ftbic.enderium_nugget", "Enderium Nugget");
        add("item.ftbic.enderium_plate", "Enderium Plate");
        add("item.ftbic.enderium_rod", "Enderium Rod");
        add("item.ftbic.enderium_wire", "Enderium Wire");
        add("item.ftbic.energy_crystal", "Energy Crystal");
        add("item.ftbic.energy_storage_upgrade", "Energy Storage Upgrade");
        add("item.ftbic.ev_battery", "EV Battery");
        add("item.ftbic.ev_battery_box", "EV Battery Box");
        add("item.ftbic.ev_cable", "EV Cable");
        add("item.ftbic.ev_reinforced_cable", "Reinforced EV Cable");
        add("item.ftbic.ev_rectifier", "EV Energy Rectifier");
        add("item.ftbic.ev_solar_panel", "EV Solar Panel");
        add("item.ftbic.ev_transformer", "EV Transformer");
        add("item.ftbic.exfluid", "Ex-Fluid");
        add("item.ftbic.extruder", "Extruder");
        add("item.ftbic.fluid_cell", "Fluid Cell");
        add("item.ftbic.fluid_cell.contents", "%s / %s mB of %s");
        add("item.ftbic.fluid_cell.empty", "Empty");
        add("item.ftbic.fuse", "Fuse");
        add("item.ftbic.guide", "FTB Industrial Contraptions Guide");
        add("item.ftbic.geothermal_generator", "Geothermal Generator");
        add("item.ftbic.graphene", "Graphene");
        add("item.ftbic.heat_capacity_reactor_plating", "Heat-Capacity Reactor Plating");
        add("item.ftbic.heat_exchanger", "Heat Exchanger");
        add("item.ftbic.heat_vent", "Heat Vent");
        add("item.ftbic.hv_battery", "HV Battery");
        add("item.ftbic.hv_battery_box", "HV Battery Box");
        add("item.ftbic.hv_cable", "HV Cable");
        add("item.ftbic.hv_reinforced_cable", "Reinforced HV Cable");
        add("item.ftbic.hv_rectifier", "HV Energy Rectifier");
        add("item.ftbic.hv_solar_panel", "HV Solar Panel");
        add("item.ftbic.hv_transformer", "HV Transformer");
        add("item.ftbic.industrial_grade_metal", "Industrial Grade Metal");
        add("item.ftbic.iridium_alloy", "Iridium Alloy");
        add("item.ftbic.iridium_circuit", "Iridium Circuit");
        add("item.ftbic.iridium_neutron_reflector", "Iridium Neutron Reflector");
        add("item.ftbic.iron_furnace", "Iron Furnace");
        add("item.ftbic.iv_cable", "IV Cable");
        add("item.ftbic.iv_reinforced_cable", "Reinforced IV Cable");
        add("item.ftbic.iv_rectifier", "IV Energy Rectifier");
        add("item.ftbic.landmark", "Landmark");
        add("item.ftbic.large_coolant_cell", "Large Coolant Cell");
        add("item.ftbic.light_spray_paint_can", "Spray Paint Can (Light)");
        add("item.ftbic.location_card", "Location Card");
        add("item.ftbic.location_card.unbound", "Unbound");
        add("item.ftbic.location_card.unnamed", "Unnamed Teleporter");
        add("item.ftbic.location_card.bound", "Saved destination: %s");
        add("item.ftbic.location_card.linked", "Teleporter linked to %s");
        add("item.ftbic.location_card.cleared", "Location cleared!");
        add("item.ftbic.lv_battery", "LV Battery");
        add("item.ftbic.lv_battery_box", "LV Battery Box");
        add("item.ftbic.lv_cable", "LV Cable");
        add("item.ftbic.lv_reinforced_cable", "Reinforced LV Cable");
        add("item.ftbic.lv_rectifier", "LV Energy Rectifier");
        add("item.ftbic.lv_solar_panel", "LV Solar Panel");
        add("item.ftbic.lv_transformer", "LV Transformer");
        add("item.ftbic.macerator", "Macerator");
        add("item.ftbic.machine_block", "Machine Block");
        add("item.ftbic.mechanical_elytra", "Mechanical Elytra");
        add("item.ftbic.medium_coolant_cell", "Medium Coolant Cell");
        add("item.ftbic.mixed_metal_blend", "Mixed Metal Blend");
        add("item.ftbic.mv_battery", "MV Battery");
        add("item.ftbic.mv_battery_box", "MV Battery Box");
        add("item.ftbic.mv_cable", "MV Cable");
        add("item.ftbic.mv_reinforced_cable", "Reinforced MV Cable");
        add("item.ftbic.mv_rectifier", "MV Energy Rectifier");
        add("item.ftbic.mv_solar_panel", "MV Solar Panel");
        add("item.ftbic.mv_transformer", "MV Transformer");
        add("item.ftbic.neutron_reflector", "Neutron Reflector");
        add("item.ftbic.nuclear_reactor", "Nuclear Reactor");
        add("item.ftbic.nuclear_reactor_chamber", "Nuclear Reactor Chamber");
        add("item.ftbic.nuke", "Nuke");
        add("item.ftbic.nuke_arrow", "Nuke Arrow");
        add("item.ftbic.overclocked_heat_vent", "Overclocked Heat Vent");
        add("item.ftbic.overclocker_upgrade", "Overclocker Upgrade");
        add("item.ftbic.powered_crafting_table", "Powered Crafting Table");
        add("item.ftbic.powered_furnace", "Powered Furnace");
        add("item.ftbic.protein_bar", "Feed The Beast\u00e2\u201e\u00a2 Protein Bar");
        add("item.ftbic.pump", "Pump");
        add("item.ftbic.quad_uranium_fuel_rod", "Quad Uranium Fuel Rod");
        add("item.ftbic.quantum_boots", "Quantum Boots");
        add("item.ftbic.quantum_chestplate", "Quantum Chestplate");
        add("item.ftbic.quantum_helmet", "Quantum Helmet");
        add("item.ftbic.quantum_leggings", "Quantum Leggings");
        add("item.ftbic.quarry", "Quarry");
        add("item.ftbic.reactor_heat_exchanger", "Reactor Heat Exchanger");
        add("item.ftbic.reactor_heat_vent", "Reactor Heat Vent");
        add("item.ftbic.reactor_plating", "Reactor Plating");
        add("item.ftbic.reactor_simulator", "Reactor Simulator");
        add("item.ftbic.reinforced_glass", "Reinforced Glass");
        add("item.ftbic.reinforced_stone", "Reinforced Stone");
        add("item.ftbic.reprocessor", "Reprocessor");
        add("item.ftbic.roller", "Roller");
        add("item.ftbic.rubber", "Rubber");
        add("item.ftbic.rubber_sheet", "Rubber Sheet");
        add("item.ftbic.sticky_resin", "Sticky Resin");
        add("item.ftbic.latex_ball", "Latex Ball");
        add("item.ftbic.scrap", "Scrap");
        add("item.ftbic.scrap_box", "Scrap Box");
        add("item.ftbic.single_use_battery", "Single Use Battery");
        add("item.ftbic.small_coolant_cell", "Small Coolant Cell");
        add("item.ftbic.spray_paint_can.tooltip", "Right-click on a machine to change its theme");
        add("item.ftbic.teleporter", "Teleporter");
        add("item.ftbic.thick_neutron_reflector", "Thick Neutron Reflector");
        add("item.ftbic.tooltip.creative_energy", "Infinite Energy");
        add("item.ftbic.tooltip.energy", "%s / %s");
        add("item.ftbic.tooltip.tier", "Tier: %s");
        add("item.ftbic.transformer_upgrade", "Transformer Upgrade");
        add("item.ftbic.uranium_fuel_rod", "Uranium Fuel Rod");
        add("item.ftbic.wind_mill", "Wind Mill");

        add("recipe.ftbic.alloy_smelting", "Alloy Smelting");
        add("recipe.ftbic.canning", "Canning");
        add("recipe.ftbic.compressing", "Compressing");
        add("recipe.ftbic.extruding", "Extruding");
        add("recipe.ftbic.macerating", "Macerating");
        add("recipe.ftbic.reconstructing", "Reconstructing");
        add("recipe.ftbic.reprocessing", "Reprocessing");
        add("recipe.ftbic.rolling", "Rolling");
        add("recipe.ftbic.separating", "Separating");

        add("ftbic.any_item", "Any Item");
        add("ftbic.energy_capacity", "Capacity: %s");
        add("ftbic.energy_output", "Output: %s");
        add("ftbic.energy_stored", "Stored: %s");
        add("ftbic.energy_usage", "Usage: %s");
        add("ftbic.fuse_info", "Right-click with a fuse to repair burnt machines.");
        add("ftbic.max_input", "Max Input: %s");
        add("ftbic.unit.zaps", "%s zaps");
        add("ftbic.unit.zaps_per_tick", "%s z/t");
        add("ftbic.unit.fe", "%s FE");
        add("ftbic.unit.fe_per_tick", "%s FE/t");
        add("ftbic.requires_chestplate", "Requires Chestplate to function");
        add("ftbic.zap_to_fe_conversion", "%s = %s FE");

        add("ftbic.jade.burnt", "\u00c2\u00a7cBurnt");
        add("ftbic.jade.cable_tier", "Tier: %s");
        add("ftbic.jade.fluid", "Fluid: %s / %s mB");
        add("ftbic.jade.fluid_empty", "Tank: Empty");
        add("ftbic.jade.lava", "Lava: %s / %s mB");
        add("ftbic.jade.progress", "Progress: %s%%");
        add("ftbic.jade.starving", "Starving for power");
        add("ftbic.jade.reactor_heat", "Heat: %s%%");
        add("ftbic.jade.reactor_output", "Output: %s/t");
        add("ftbic.jade.reactor_paused", "Paused (%s/t when active)");
        add("ftbic.jade.water", "Water: %s / %s mB");
        add("ftbic.jade.tele_power", "Power: HV (shared with linked pair)");
        add("ftbic.jade.tele_send_items", "Sending: %s items");
        add("ftbic.jade.tele_receive_items", "Received: %s items");
        add("ftbic.jade.tele_send_fluid", "Sending %s: %s / %s mB");
        add("ftbic.jade.tele_send_fluid_empty", "Send tank: Empty (0 / %s mB)");
        add("ftbic.jade.tele_receive_fluid", "Received %s: %s / %s mB");
        add("ftbic.jade.tele_receive_fluid_empty", "Receive tank: Empty (0 / %s mB)");

        add("ftbic.jei.chance", "Chance: %s%%");
        add("ftbic.jei.recipe_time_energy", "%ss · %s");
        add("ftbic.jei.burn_time", "%ss @ %s");
        add("ftbic.jei.total_zaps", "= %s");
        add("ftbic.jei.zaps_per_mb", "%s/mB @ %s/bucket");
        add("ftbic.jei.zaps_per_tank", "= %s / full tank (%s mB)");
        add("ftbic.jei.boost", "+%s boost");

        add("ftbic.jei.antimatter.line1", "Produced by the Antimatter Constructor.");
        add("ftbic.jei.antimatter.line2", "Each antimatter requires %s of progress.");
        add("ftbic.jei.antimatter.line3", "Boost items consumed in the input slot accelerate progress.");
        add("ftbic.jei.antimatter.line4", "See \"Antimatter Constructor\" recipes for boost values.");

        add("ftbic.jei.rod.title", "Nuclear fuel rod");
        add("ftbic.jei.rod.desc", "%s-rod pack: %s pulse(s) per cycle");
        add("ftbic.jei.rod.energy", "Energy: %s base (×(pulses+reflectors))");
        add("ftbic.jei.rod.heat", "Heat: %s/cycle base. Distributed into neighboring heat acceptors.");
        add("ftbic.jei.rod.durability", "Durability: %s cycles before the rod is spent.");

        add("ftbic.jei.coolant.title", "Coolant cell");
        add("ftbic.jei.coolant.desc", "Passive heat buffer. Absorbs heat distributed by adjacent fuel rods.");
        add("ftbic.jei.coolant.capacity", "Capacity: %s heat.");
        add("ftbic.jei.coolant.vent_pair", "Pair with a Component Heat Vent to replenish durability each cycle.");

        add("ftbic.jei.vent.title", "Heat vent");
        add("ftbic.jei.vent.desc", "Removes heat each reactor cycle.");
        add("ftbic.jei.vent.durability", "Durability: %s heat absorption.");
        add("ftbic.jei.vent.self_cool", "Self cooling: %s/cycle (heals own durability).");
        add("ftbic.jei.vent.reactor_cool", "Reactor cooling: %s/cycle removed from reactor heat pool.");
        add("ftbic.jei.vent.component_cool", "Component cooling: %s/cycle to each adjacent coolant cell.");

        add("ftbic.jei.exchanger.title", "Heat exchanger");
        add("ftbic.jei.exchanger.desc", "Balances heat between neighbors and the reactor core.");
        add("ftbic.jei.exchanger.durability", "Durability: %s heat buffer.");
        add("ftbic.jei.exchanger.adjacent", "Adjacent transfer: up to %s/cycle per neighbor.");
        add("ftbic.jei.exchanger.core", "Core transfer: up to %s/cycle to/from the reactor heat pool.");

        add("ftbic.jei.plating.title", "Reactor plating");
        add("ftbic.jei.plating.desc", "Modifies the reactor hull itself.");
        add("ftbic.jei.plating.heat_bonus", "Max heat bonus: +%s (stacks with other plating).");
        add("ftbic.jei.plating.explosion", "Explosion dampening: ×%s (-%s%% radius per plating).");

        add("ftbic.jei.reflector.title", "Neutron reflector");
        add("ftbic.jei.reflector.desc", "Bounces pulses back into adjacent fuel rods.");
        add(
                "ftbic.jei.reflector.pulse_effect",
                "Each reflector adjacent to a rod adds +1 pulse (more energy AND more heat).");
        add("ftbic.jei.reflector.durability_infinite", "Durability: infinite (iridium-reinforced).");
        add("ftbic.jei.reflector.durability", "Durability: %s pulses before the reflector burns out.");

        add("ftbic.reactor.paused", "Paused");
        add("ftbic.reactor.heat_percentage", "%d%%");
        add("ftbic.reactor.tooltip.paused", "Paused (%s when active)");
        add("ftbic.reactor.tooltip.output", "Output: %s");
        add("ftbic.reactor.tooltip.resume", "Resume reactor");
        add("ftbic.reactor.tooltip.pause", "Pause reactor");
        add("ftbic.reactor.tooltip.redstone_enabled", "Redstone control: enabled");
        add("ftbic.reactor.tooltip.redstone_disabled", "Redstone control: disabled");
        add("ftbic.reactor.tooltip.show_jei", "Show reactor components in JEI");

        add("config.jade.plugin_ftbic.cable_tier", "Cable Tier");
        add("config.jade.plugin_ftbic.energy", "Energy");

        add("ftbic.gui.antimatter_constructor.boosted", "Boosted. Click to show boost items.");
        add("ftbic.gui.antimatter_constructor.boost", "Click to show boost items");

        add("ftbic.gui.basic_generator.burn_time", "Burn time: %s s. Click to show fuels.");

        add("ftbic.gui.slot.upgrade", "Upgrade Slot");
        add("ftbic.gui.slot.battery", "Battery Slot");
        add("ftbic.gui.slot.charge", "Charge Slot");
        add("ftbic.gui.slot.pickaxe", "Pickaxe Slot (applies enchantments to mined blocks)");

        add("ftbic.gui.iron_furnace.progress", "Progress: %s%%. Click to show recipes.");

        add("ftbic.gui.machine.progress", "Progress: %s%%. Click to show recipes.");

        add("ftbic.gui.pump.paused", "Paused. Click to resume.");
        add("ftbic.gui.pump.running", "Running. Click to pause.");

        add("ftbic.gui.quarry.paused", "Paused. Click to resume.");
        add("ftbic.gui.quarry.running", "Running. Click to pause.");

        add("ftbic.gui.solar_panel.producing", "Producing");
        add("ftbic.gui.solar_panel.no_sunlight", "No sunlight");

        add("ftbic.gui.teleporter.name_label", "Name");
        add("ftbic.gui.teleporter.public_label", "Public");
        add("ftbic.gui.teleporter.private_label", "Private");
        add("ftbic.gui.teleporter.unnamed", "Unnamed");
        add("ftbic.gui.teleporter.linked_format", "Linked: %s");
        add("ftbic.gui.teleporter.not_linked", "Not linked. Click below.");
        add("ftbic.gui.teleporter.clear_storage", "Clear Storage");
        add("ftbic.gui.teleporter.clear_fluids", "Clear Fluids");
        add("ftbic.gui.teleporter.no_teleporters", "No teleporters available");
        add("ftbic.gui.teleporter.choose_destination", "Choose destination");
        add("ftbic.gui.teleporter.no_teleporters_found", "No teleporters found");
        add(
                "ftbic.gui.teleporter.public_tooltip",
                "Public. Anyone can link to this teleporter. Click to make private.");
        add(
                "ftbic.gui.teleporter.private_tooltip",
                "Private. Only you can link to this teleporter. Click to make public.");
        add("ftbic.gui.teleporter.unlink_tooltip", "Unlink destination");
        add(
                "ftbic.gui.teleporter.name_tooltip",
                "Give this teleporter a name so you can find it in other teleporters' lists.");
        add("ftbic.gui.teleporter.entry_tooltip", "%s\nCost: %s per jump\nClick to link");

        add("ftbic.gui.reactor_sim.start", "Start");
        add("ftbic.gui.reactor_sim.pause", "Pause");
        add("ftbic.gui.reactor_sim.restart", "Restart");
        add("ftbic.gui.reactor_sim.clear", "Clear");
        add("ftbic.gui.reactor_sim.chambers", "Chambers: %d/%d");
        add("ftbic.gui.reactor_sim.water", "Water: %s");
        add("ftbic.gui.reactor_sim.components", "Components");
        add("ftbic.gui.reactor_sim.presets", "Presets");
        add("ftbic.gui.reactor_sim.no_presets", "(none)");
        add("ftbic.gui.reactor_sim.analyze", "Analyze");
        add("ftbic.gui.reactor_sim.import_btn", "Import");
        add("ftbic.gui.reactor_sim.export_btn", "Export");

        add("ftbic.gui.reactor_sim.verdict_stable", "Result: STABLE");
        add("ftbic.gui.reactor_sim.verdict_unstable", "Result: overheats at cycle %d");
        add("ftbic.gui.reactor_sim.verdict_none", "Result: not analyzed");
        add("ftbic.gui.reactor_sim.stats", "%s  |  total %s  |  cycle %d");

        add("ftbic.gui.reactor_sim.save_name_label", "Name");
        add("ftbic.gui.reactor_sim.save_error_invalid_name", "Invalid name");
        add("ftbic.gui.reactor_sim.save_error_reserved", "Reserved name");
        add("ftbic.gui.reactor_sim.save_error_no_sim", "No simulator");
        add("ftbic.gui.reactor_sim.save_error_failed", "Save failed");
        add("ftbic.gui.reactor_sim.save_success", "Preset saved: %s");
        add("ftbic.gui.reactor_sim.remove_success", "Preset removed: %s");
        add("ftbic.gui.reactor_sim.import_error", "Clipboard is not a valid reactor design.");
        add("ftbic.gui.reactor_sim.export_success", "Reactor design copied to clipboard.");

        add("ftbic.gui.reactor_sim.chambers_title", "Chambers");
        add("ftbic.gui.reactor_sim.chambers_desc1", "Number of Nuclear Reactor Chambers attached to the real reactor");
        add(
                "ftbic.gui.reactor_sim.chambers_desc2",
                "Each chamber adds one column to the grid (currently %d columns active)");
        add("ftbic.gui.reactor_sim.chambers_desc3", "Chambers also expose more outer hull faces for water cooling");

        add("ftbic.gui.reactor_sim.water_title", "Water env factor");
        add("ftbic.gui.reactor_sim.water_desc1", "Fraction of outward hull faces touching water (0.00 to 1.00)");
        add("ftbic.gui.reactor_sim.water_desc2", "Current cooling multiplier: x%s");
        add("ftbic.gui.reactor_sim.water_desc3", "Applied to vent \"reactor cool\" values each cycle");
        add("ftbic.gui.reactor_sim.water_desc4", "(1.0 at 0.00 water up to x%s at 1.00 water)");

        add("ftbic.gui.reactor_sim.stats_title", "Simulation stats");
        add("ftbic.gui.reactor_sim.stats_desc1", "Verdict line: stability analysis result");
        add("ftbic.gui.reactor_sim.stats_desc2", "First value: energy output per tick this cycle");
        add("ftbic.gui.reactor_sim.stats_desc3", "total: cumulative energy since Start");
        add("ftbic.gui.reactor_sim.stats_desc4", "cN: cycle counter (1 cycle = 1 reactor tick)");
        add("ftbic.gui.reactor_sim.stats_desc5", "Speed controls cycles per game tick: 20x = 1/t, 1000x = 50/t");

        add("ftbic.gui.reactor_sim.load_title", "Load");
        add("ftbic.gui.reactor_sim.load_desc", "Apply the selected preset to this simulator");
        add("ftbic.gui.reactor_sim.load_hint", "Clears current layout first, then sets chambers and components");

        add("ftbic.gui.reactor_sim.save_title", "Save");
        add("ftbic.gui.reactor_sim.save_desc", "Save the current layout as a new preset");
        add("ftbic.gui.reactor_sim.save_hint", "Stored locally in local/ftbic/reactor_layout/");

        add("ftbic.gui.reactor_sim.remove_title", "Remove");
        add("ftbic.gui.reactor_sim.remove_desc", "Delete the selected preset file");
        add("ftbic.gui.reactor_sim.remove_locked", "Built-in presets cannot be removed");

        add("ftbic.gui.reactor_sim.item.fuel_rod.title", "Fuel rod");
        add("ftbic.gui.reactor_sim.item.fuel_rod.base_pulses", "  Base pulses: %d (+1 per adjacent reflector or rod)");
        add("ftbic.gui.reactor_sim.item.fuel_rod.energy", "  Energy: p x %s");
        add("ftbic.gui.reactor_sim.item.fuel_rod.heat", "  Heat:   p x (p+1) x %s / cycle");
        add("ftbic.gui.reactor_sim.item.fuel_rod.spread", "    spread over adjacent heat acceptors");
        add("ftbic.gui.reactor_sim.item.fuel_rod.heat_example", "  p=%d -> %d heat | p=%d -> %d heat");
        add("ftbic.gui.reactor_sim.item.fuel_rod.durability", "  Durability: %d cycles");

        add("ftbic.gui.reactor_sim.item.heat_vent.title", "Heat vent");
        add("ftbic.gui.reactor_sim.item.heat_vent.heat_buffer", "  Own heat buffer: %s");
        add("ftbic.gui.reactor_sim.item.heat_vent.no_buffer", "  No own heat buffer");
        add("ftbic.gui.reactor_sim.item.heat_vent.self_cool", "  Self-heal: %s heat / cycle");
        add("ftbic.gui.reactor_sim.item.heat_vent.reactor_cool", "  Reactor cool: %s heat / cycle");
        add("ftbic.gui.reactor_sim.item.heat_vent.water_hint", "    multiplied by water env factor");
        add("ftbic.gui.reactor_sim.item.heat_vent.component_cool", "  Adjacent coolant cooling: %s heat / cycle each");

        add("ftbic.gui.reactor_sim.item.exchanger.title", "Heat exchanger");
        add("ftbic.gui.reactor_sim.item.exchanger.heat_buffer", "  Own heat buffer: %s");
        add("ftbic.gui.reactor_sim.item.exchanger.adjacent", "  Adjacent transfer: up to %s / cycle per neighbour");
        add("ftbic.gui.reactor_sim.item.exchanger.core", "  Core transfer: up to %s / cycle vs reactor");
        add("ftbic.gui.reactor_sim.item.exchanger.transfer_direction", "  Moves heat from hotter side to cooler side");

        add("ftbic.gui.reactor_sim.item.coolant.title", "Coolant cell");
        add("ftbic.gui.reactor_sim.item.coolant.capacity", "  Capacity: %s heat");
        add("ftbic.gui.reactor_sim.item.coolant.passive", "  Passive. Soaks heat distributed by adjacent fuel rods");
        add("ftbic.gui.reactor_sim.item.coolant.vent_pair", "  Component heat vents can refill durability");

        add("ftbic.gui.reactor_sim.item.reflector.title", "Neutron reflector");
        add("ftbic.gui.reactor_sim.item.reflector.pulse_boost", "  +1 pulse on each adjacent fuel rod");
        add("ftbic.gui.reactor_sim.item.reflector.energy_heat_note", "  Each added pulse raises energy AND heat");
        add("ftbic.gui.reactor_sim.item.reflector.durability", "  Durability: %d pulses reflected");
        add("ftbic.gui.reactor_sim.item.reflector.infinite", "  Infinite durability");

        add("ftbic.gui.reactor_sim.item.plating.title", "Reactor plating");
        add("ftbic.gui.reactor_sim.item.plating.heat_capacity", "  +%s max reactor heat (raises meltdown threshold)");
        add("ftbic.gui.reactor_sim.item.plating.blast", "  Blast radius: x%s (%s)");
        add("ftbic.gui.reactor_sim.item.plating.multiply_hint", "  Applies once per plating, multiplicative");
    }
}
