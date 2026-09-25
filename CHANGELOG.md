# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [26.1.2.11]

### Fixed

* Upgrade slots now limit each upgrade type separately, so four transformers can coexist with overclockers and ejectors. Excess items remain in the player's inventory when shift-clicking.
* Sneak-right-clicking a machine with an upgrade now installs it, including a stack of up to four, without opening the UI.
* Teleporter destination lists stay inside the machine screen above the inventory, with scrolling for longer lists. Its controls now match the industrial machine UI.
* Jade's energy bar now uses the entire Industrial Battery Bank's stored energy and capacity when looking at either a cell or a port.
* LV, MV, HV, and EV Battery Boxes can output energy from any configured face, with input/output textures that follow each face's energy setting and use the correct face lighting.
* Solar panels no longer generate through a directly adjacent opaque block.
* Lapis Lazuli ore can be macerated into dust again.
* clipped button labels, component names, and material counts in the Reactor Design panel.
* Reactor components and fuel rods now stack when their data matches, with reactor and planner slots limited to one item.
* Carbon and Quantum armor now render their own textures when equipped. {#12410}
* Charge Pads now accept four rechargeable items in their UI while continuing to charge carried and worn items when stood on. ([Issue 2123](https://github.com/FTBTeam/FTB-Mods-Issues/issues/2123))
* Scrap Boxes now give a random reward when used. Reward selection is based on [PR 56](https://github.com/FTBTeam/FTB-Industrial-Contraptions/pull/56) by @jshipley. ([Issue 2124](https://github.com/FTBTeam/FTB-Mods-Issues/issues/2124))
* The Fluid Cell guide now correctly explains that filled cells with identical contents can stack. ([Issue 2125](https://github.com/FTBTeam/FTB-Mods-Issues/issues/2125))
* Charged Quantum Chestplates now enable hovering flight with a double tap of jump. Flight stops when the chestplate is removed or runs out of zaps. ([Issue 2128](https://github.com/FTBTeam/FTB-Mods-Issues/issues/2128))
* Other mods' FE generators and storage not powering FTBIC cable networks in full FE mode.
* FE sources from other mods burning out FTBIC machines, even when only testing a transfer.
* The Nuclear Reactor offering no FE to other mods in full FE mode.
* Reactor screen, Jade and Reactor Simulator output ignoring the reactor output multiplier.
* Jade showing a duplicate energy bar on FTBIC machines.
* Mystical Agriculture erroring on FTBIC recipes that use custom ingredients.
* Wrong solar panel buffer, geothermal output and energy storage upgrade figures in the guide.
* Single-use batteries being used up with no charge, and battery capacity settings having no effect.
* Industrial Battery Bank cells and ports vanishing when broken.
* Idle linked teleporters keeping chunks loaded and draining energy.
* Configuration Cards holding enchanted items disconnecting players.
* Energy held by Energy Storage Upgrades being lost on chunk reload.
* Machines staying idle after their input was topped up with Shift-click.
* Powered Crafting Table destroying recipe remainders such as buckets.
* Machine tanks accepting fluids that no recipe uses.
* Quarry landmark resizing freezing the server.
* Quarry areas wider than 64 blocks breaking after a reload.
* Quarries stopping at water and lava.
* Quarries and pumps ignoring land claims.
* Quarries mining blocks that other mods mark as not movable.
* Quarry and pump exfluid options having no effect.
* FE ignoring battery box and transformer side settings.
* Small FE transfers being refused or overcharged by rounding.
* Ore refining smelting into FTBIC's own ingots instead of unified ones.
* Reactor Simulator total energy reading 20 times too low.
* Wind mills overproducing at low altitude.
* Server lag from generators rescanning their cable networks on unrelated block updates.
* Config screen errors for the quarry and pump tick options.
* Wrong tiers, speeds and recharge rules on several guide pages.
* The `[recipes]` config toggles having no effect.
* Lag from other mods feeding FE into large cable networks.
* Battery boxes having no usable charge slot.
* Underpowered machines restarting their recipe from zero instead of pausing.
* Industrial Battery Bank screen showing wrong values on dedicated servers.
* Reactor output readouts capping at 32,767.
* Cable networks loading chunks when they reach unloaded areas.
* Heavy network traffic from machine, quarry and hydroponic block updates.
* Superconducting cables sending constant update packets and rendering while idle.

### Added

* Hydroponic Accelerator and Advanced Hydroponic Accelerator grow crops with reusable, crop-compatible soil and soil-dependent speed. The advanced machine runs four independent seed and soil pairs with separate output buffers and ghost locks.
* Hydroponic Mutation mode combines two parent plants for a chance at a new seed, returning the parents when a cross fails. Added water use, side configuration, JEI recipes, and an in-game guide.
* Sneak-right-click a machine with upgrades to install up to four at once without opening its UI, respecting the machine's supported upgrade types and remaining capacity.
* Ore refining: macerate ore blocks into three raw ore, then crush, wash, centrifuge, and smelt for five ingots per raw ore or fifteen per ore block. Blocks of raw ore macerate straight into eighteen crushed ore.
* Ore Washer with fluid tanks, side configuration, upgrades, and the shared industrial machine UI.
* Automatic modded-metal discovery, colored intermediates, JEI recipes and ghost filters, and datapack overrides for materials and processing. The in-game guide explains the full chain.

* Superconducting Cables carry unlimited zaps and show a fast emissive cyan pulse that loops seamlessly during energy transfer.
* Industrial battery banks with connected EV-style steel casings: storage cells add configurable capacity, and ports provide configurable energy transfer. Adjacent ports on the same flat face combine into larger emissive gauges showing the whole bank's fill level.
* Battery Bank Ports can cycle between the default port symbol, a shared charge gauge, and plain cell-style steel with sneak-right-click. Each port remembers its style.
* Quarry Filter Upgrades add block and tag whitelist/blacklist rules, ore-only mining, free skipping of unmatched columns, JEI ghost targets, and Configuration Card support.
* Parallel Processing Upgrades let advanced processors run up to four copies of a recipe per cycle, with energy use per operation and support for centrifuge fluids. Includes a running-operation counter and GuideMe instructions.
* Persistent input-slot locks, copied by Configuration Cards.
* JEI ghost dragging for machine input filters and Batch Feeder item and fluid settings.
* Unpowered Batch Feeder for delivering complete item and fluid batches to adjacent machines.
* Centrifuge fluid inputs and outputs, with three item outputs on the Advanced Centrifuge.
* Advanced Centrifuge lava processing: one bucket yields two tin nuggets, one copper nugget, and a 25% chance of a gold nugget. Tin nuggets craft to and from ingots.
* Configurable machine faces for item, fluid, and energy transfers.
* Reusable Configuration Cards for copying machine side settings.
* GuideMe instructions for side configuration and Configuration Cards.
* Reactor design previews and inventory autofill from planner presets or exported layouts.
* Reusable Reactor Blueprints for copying and sharing designs.
* GuideMe instructions for recording and applying Reactor Blueprints.
* Items with the `ftbic:loot_box` data component can open a chosen loot table. Crouching opens the whole stack at once. Based on [PR 56](https://github.com/FTBTeam/FTB-Industrial-Contraptions/pull/56) by @jshipley.
* Full FE mode lets other mods charge FTBIC batteries and armour, and FTBIC battery slots and Charge Pads accept other mods' FE items.
* Battery boxes and Industrial Battery Bank blocks keep their stored energy when broken.
* Industrial Battery Bank ports have four charge slots for batteries and other energy items.

### Changed

* Nuclear fuel rods now generate four times their previous base energy output, without increasing heat generation.
* Refreshed machine and I/O screens with light steel panels, vanilla-style slots, and outlined progress arrows.
* Side configuration now uses a spatial face diagram, a color legend, reverse cycling, and per-face reset.
* Canned Food now uses the standard item remainder behavior to return its empty can after eating.
* Carbon and Quantum armor sets no longer have item durability or permanent armor defense. The chestplate spends zaps to absorb damage, and an empty chestplate leaves the set without protection.
* Quantum Chestplates now support powered gliding with jump to boost and sneak to slow down. Mechanical Elytra uses a visible wing texture and shares the same flight energy handling. Adapted from [PR 55](https://github.com/FTBTeam/FTB-Industrial-Contraptions/pull/55) by @jshipley.
* Full FE mode shows FE instead of zaps in GUIs, tooltips, JEI, Jade and the guide.
* Energy Rectifiers are hidden and uncraftable in full FE mode.
* Guide energy figures follow the active energy mode and the config values.
* Four Transformer Upgrades let a machine accept any amount of power without burning out.
* Pumps use their own speed settings, which are slower than the quarry's by default.

### Removed

* Unused config options `teleporter_balance_rate`, `scrap_chance`, `add_all_fluid_cells` and `nuclear_explosion_daemon_thread`.

## [26.1.2.10]

### Fixed

* **GUI and JEI text can now be translated.** Around 160 strings were hardcoded English. Added `ftbic.gui.*`, `ftbic.jei.*` and `ftbic.reactor.*` keys now.
* JEI machine categories take their title from the machine's block translation key instead of its internal display name, so a translated machine name shows up in JEI too.
* Numbers in JEI and GUI tooltips format through a single pair of helpers under `Locale.ROOT`, so decimal separators no longer follow the system locale and large zap counts are grouped consistently.

### Added

* **Japanese (ja_jp) translation**: 454 entries covering every block, item, tooltip and config string. Thanks to [@Nia1111](https://github.com/Nia1111) ([FTBTeam/FTB-Industrial-Contraptions#48](https://github.com/FTBTeam/FTB-Industrial-Contraptions/pull/48)).
* **Chinese (zh_cn) translation** expanded from 142 to 424 entries, covering the newly translatable GUI and JEI text plus the reactor simulator. Thanks to [@xingluo01](https://github.com/xingluo01) for the translation and for the original localization work in [FTBTeam/FTB-Industrial-Contraptions#50](https://github.com/FTBTeam/FTB-Industrial-Contraptions/pull/50).

## [26.1.2.9]

### Fixed

* **Alloy Smelter** and **Block of Enderium** now drop when mined. Both blocks were missing their block loot tables, so they vanished on break and showed no drop in JEI. The Alloy Smelter also drops its contents (inputs, outputs, upgrades, and battery) as before.

## [26.1.2.8]

### Fixed

* Powered Furnace crash when a third-party mod (for example, GeOre) registers a non-`SmeltingRecipe` subclass under `RecipeType.SMELTING`. The vanilla-recipe fallback now matches `AbstractCookingRecipe` so any cooking-recipe class is accepted.

## [26.1.2.7]

### Added

* **Alloy Smelter**: new MV machine with 3 unique input slots and 1 output. Energy use is 3× the Advanced Powered Furnace (48 z/t). Slot constraint: any item placed in one input slot is rejected by the other two so stacks can't be split across slots. The recipe matcher prefers the highest-input recipe that matches your slots, so a 3-input alloy wins over any 2-input subset. Recipes ship for: bronze (3 copper + 1 tin → 4), electrum (1 silver + 1 gold → 2), invar (2 iron + 1 nickel → 3), constantan (1 copper + 1 nickel → 2), steel (1 industrial grade metal + 1 coal *or* 1 charcoal → 1), netherite (2 gold + 2 netherite scrap → 1 ingot, skips smithing), enderium (3 lead + 1 diamond dust + 2 ender pearls → 2), and three steel-based advanced alloy recipes (steel + 2 bronze + aluminum / steel + electrum + aluminum / steel + 2 invar + aluminum → 1 advanced alloy).
* **Steel material**: new ingot/dust/plate/rod/gear/wire/block set; gateway ingredient for every advanced alloy path.
* **Macerator: advanced alloy → mixed metal blend**, a recovery loop for the existing mixed_metal_blend → advanced_alloy smelting recipe.
* **Smelting + blasting recipes for every material** with both an ingot and a smeltable input (dust / stone_ore / deepslate_ore / raw_ore → ingot). Vanilla-overlap materials (copper, gold, iron) smelt their FTBIC dust to the vanilla ingot.
* **Obsidian alloy chain**: `obsidian_dust` compresses to `obsidian_plate` (Compressor, 1:1), and extrudes to `obsidian_rod` (Extruder, 1 dust → 2 rods).
* **Constantan and silicon material set** restored: silicon as GEM (populates `c:silicon` for advanced circuit / energy crystal / lv solar panel), constantan with the full crafted-only set.
* **Nickel ore worldgen**: middle-band and small-vein placements added to the existing biome modifier alongside aluminum/lead/tin.
* **EnderIO alloying compatibility**: 7 conditional alloy smelter recipes (`conductive_alloy`, `redstone_alloy`, `pulsating_alloy`, `energetic_alloy`, `vibrant_alloy`, `dark_steel`, `end_steel`) gated by `neoforge:mod_loaded` on `enderio`.
* **ConTeX (XFactHD) optional support**: built-in resource pack ships connected-texture variants for reinforced stone, reinforced glass, and all reinforced cables (LV/MV/HV/EV/IV/burnt). Force-loaded only when the `contex` mod is present; uses the `ftbic:reinforced` block tag so all reinforced variants connect.
* GuideME pages: new **Alloy Smelter** machine entry; rewritten **Alloys** materials page covering steel and the new advanced alloy paths.

### Changed

* **Materials migrated off FTB Materials.** `ftbmaterials` is no longer a dependency. FTBIC ships its own ore blocks, raw ore items, ingots, nuggets, dusts, plates, rods, gears, wires, storage blocks, raw blocks, and gem (silicon) variants for every material it touches. Tags use the `c:` (NeoForge common) namespace so third-party mods continue to fill gaps. All conditional `ComponentsAvailableCondition` wrappers and tag-empty checks were removed from the recipe provider.
* **Alloy Smelter recipe matcher** sorts candidates by input count descending, so a 3-ingredient recipe wins over any 2-ingredient subset when all three slots are filled.
* **Recipe sync handler** auto-iterates `FTBICRecipes.TYPES` instead of a hand-maintained list.
* **JEI category dimensions** widen to fit up to N input slots; the alloy smelter category renders all 3 input slots in a row.
* **Alloy Smelter craft recipe** is now 4 carbon plates + advanced circuit + 2 powered furnaces + copper coil + diamond.

### Removed

* Shaped craft recipes for `enderium_ingot`, `enderium_wire`, `enderium_dust`, `mixed_metal_blend_1/2/3`, and shapeless `constantan_dust`. Their replacements live in the Alloy Smelter (enderium ingot, constantan ingot) and in the macerator (mixed metal blend from advanced alloy recovery).

## [26.1.2.6]

### Fixed

* Nuke crater is no longer a tiny vanilla blast. Restored the 1.18.2 spheroid carve (`x² + (y/0.75)² + z² ≤ r²`) with crust extraction and a raytrace pass that respects reinforced blocks (so bunkers shield correctly). Reactor meltdown's bypass-claims path inherits the new behaviour and now plays an explosion sound + particle.
* MV/HV/EV/IV Energy Rectifiers were stuck outputting at LV rate (32 zaps/tick) regardless of tier. They now output at their own tier (IV rectifier: 8192 zaps/tick), so a fully-fed IV rectifier can actually saturate a downstream IV-tier machine. Existing rectifiers will pick up the fix on chunk reload.
* Antimatter Constructor produced antimatter ~2000× faster thanintended.
* Reactor Simulator: components dragged into newly-exposed chamber slots (after raising chambers from a loaded preset) appearing to "not register" components is now fixed.
* Reactor Simulator chamber/water steppers no longer drop rapid clicks.

### Added

* `nuclear.nuke_respects_claims` config (default `false`). `true` falls back to a vanilla explosion that FTB Chunks etc. can cancel (smaller crater as a tradeoff).

### Changed

* Default `antimatter_constructor_boost` lowered from 6 to 3. Each scrap saves 10k zaps (was 25k); each scrap box saves 90k zaps (was 225k). Existing configs are unaffected.

## [26.1.2.5]

### Fixed

* GuideME guide folder renamed from `guide` to `ftbic`. The previous name was non-unique across mods, so when other GuideME-using mods were installed alongside FTBIC their pages would appear inside the FTBIC guide and vice versa.

## [26.1.2.4]

### Fixed

* Fixed a startup crash on the latest NeoForge betas caused by a bad default in the Pump's tank capacity setting.

## [26.1.2.3]

### Added

* Zaps energy is now exposed as a `BlockCapability<ZapEnergyHandler, Direction>` (`ftbic:zap_energy`). Capability proxies, cover blocks, and addon mods can now interop with the FTBIC energy network without importing internal block-entity classes.
* `BlockCapabilityCache` is now used for zap-cap lookups in cable network traversal and generator output, mirroring the existing FE caching path.

### Fixed

* Fixed crash on startup with neoforge 27 beta: `teleporter_capacity` and `charge_pad_capacity` config defaults (1,000,000) exceeded their validator max (100,000). Max raised to 10,000,000.
* Reactor chamber wall no longer swallows right-click when holding a block. Block placement works as expected; use an empty hand to open the reactor GUI.
* Machines now drop their pickaxe, upgrades, battery, and teleporter buffers when broken. The `onBroken` chain was never wired to 26.1's `preRemoveSideEffects` hook, so all electric-block internals were silently lost on removal.
* Energy Rectifiers (LV/MV/HV/EV/IV) are now pickaxe-mineable. They were missing from `#minecraft:mineable/pickaxe`, so they took fist-tier mining time and dropped nothing.

### Changed

* Reduced uranium overworld spawn rate. Removed the y32 to 256 surface placement (count 50, no air discard) and halved the y-64 to 32 cave placement from count 4 to count 2. Uranium now stays rare and primarily underground.
* Iridium is ~25% rarer than diamond. Small placement count 7 → 5, buried count 4 → 3, large rarity filter 9 → 12.
* `data/minecraft/tags/block/{mineable/pickaxe,dragon_immune,wither_immune}` and `data/minecraft/tags/item/arrows` are now emitted from datagen instead of hand-maintained. The pickaxe tag iterates `FTBICElectricBlocks.ALL` so every electric block is auto-tagged on registration.

## [26.1.2.1]

### Changed

* Initial refresh release of FTB Industrial Contraptions for 26.1+
* A lot of internals have been rewritten
* Nuclear reactors now work!
* Likely introduced a lot of bugs! Please report them as you find them ❤️
