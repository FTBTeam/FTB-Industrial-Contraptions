# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [26.1.2.10]

> ## ⚠️ BREAKING CHANGE: THE MOD ID CHANGED FROM `ftbic` TO `ic`
>
> **BACK UP YOUR WORLD BEFORE INSTALLING THIS VERSION.** Take a full copy of the
> save folder (and the whole instance if you can) and keep it somewhere the
> launcher will not touch. This update rewrites how every block and item in the
> mod is identified. A compatibility layer is in place and testing has been
> clean, but no compatibility layer is a substitute for a backup you can roll
> back to.
>
> **Pack developers: this WILL break your pack until you update your files.**
> Any recipe, tag, loot table, KubeJS script, quest file, or config that
> references `ftbic:` or lives under `data/ftbic/` has to be changed to `ic:`.
> Registry aliases do not cover data files. Read the Migration section below
> before you ship an update.

### Changed

* **The mod is now called Industrial Contraptions.** The `FTB` prefix has been dropped from the name, the mod ID, and every item and block ID. `ftbic:copper_ingot` is now `ic:copper_ingot`, the config file is `config/ic-common.toml`, the command is `/ic`, and the jar is `industrial-contraptions-<version>.jar`.

### Fixed

* **GUI and JEI text can now be translated.** Around 160 strings were hardcoded English and no language file could reach them: every reactor component tooltip in JEI, the recipe time and energy readouts, machine progress and slot tooltips, the pump, quarry, solar panel and teleporter screens, and the whole reactor simulator UI (buttons, verdict lines, stats, preset management, and the per-component tooltips). All of them are `ic.gui.*`, `ic.jei.*` and `ic.reactor.*` keys now.
* JEI machine categories take their title from the machine's block translation key instead of its internal display name, so a translated machine name shows up in JEI too.

### Added

* **Japanese (ja_jp) translation** — 454 entries covering every block, item, tooltip and config string. Thanks to [@Nia1111](https://github.com/Nia1111) ([FTBTeam/FTB-Industrial-Contraptions#48](https://github.com/FTBTeam/FTB-Industrial-Contraptions/pull/48)).
* **Chinese (zh_cn) translation** expanded from 142 to 424 entries, covering the newly translatable GUI and JEI text plus the reactor simulator. Thanks to [@xingluo01](https://github.com/xingluo01) for the translation and for the original localization work in [FTBTeam/FTB-Industrial-Contraptions#50](https://github.com/FTBTeam/FTB-Industrial-Contraptions/pull/50).

### Migration

**For players**

* **Back up your world first.** This is the one update where it genuinely matters. Copy the save folder before you launch.
* **Existing worlds are expected to load unchanged.** Every block, item, block entity, entity, menu, sound, recipe type, recipe serializer, data component, and creative tab registers a `ftbic:` alias pointing at its new `ic:` ID, so machines, inventories, and saved contraptions all resolve on load. Nothing needs to be broken and replaced before updating.
* **Once you load and save a world on this version, going back to an older FTBIC build is not supported.** Roll back to your backup instead.
* **Your config is carried over.** `config/ftbic-common.toml` is copied to `config/ic-common.toml` the first time the new version starts, if the new file does not already exist. The old file is left in place and can be deleted once you have confirmed the settings came across.
* **Saved reactor layouts are moved** from `local/ftbic/reactor_layout` to `local/ic/reactor_layout`.
* **`/ftbic` still works** as an alias for `/ic`.

**For pack developers and modpack authors**

Registry aliases apply to registry lookups only. They do **not** apply to anything read from a data file path or matched by string, so all of the following need updating by hand:

* **Recipes and recipe overrides** referencing `ftbic:` items, and anything under `data/ftbic/recipe/`.
* **Tags**, both files under `data/ftbic/tags/` and any `ftbic:` entries inside your own tag files. Note `ftbic:reinforced` is now `ic:reinforced`.
* **Loot tables and advancements** under `data/ftbic/`, and any that reference `ftbic:` blocks or items.
* **KubeJS scripts** matching on `ftbic:` IDs, including recipe removals by ID, `ItemStack.of("ftbic:...")` lookups, and event filters.
* **Quest files** (FTB Quests and similar), where task and reward item IDs are stored as plain strings.
* **Config and script references to `config/ftbic-common.toml`**, which is now `config/ic-common.toml`.
* **Anything that depends on the mod ID itself**, such as `neoforge:mod_loaded` conditions on `ftbic`, dependency entries in `neoforge.mods.toml`, resource pack folders named `assets/ftbic/`, and CurseForge or Modrinth automation keyed to the old jar name.

The safest approach is a project-wide search for `ftbic` across your pack, then replace with `ic` and test on a copy of the world.

## [26.1.2.9]

### Fixed

* **Alloy Smelter** and **Block of Enderium** now drop when mined. Both blocks were missing their block loot tables, so they vanished on break and showed no drop in JEI. The Alloy Smelter also drops its contents (inputs, outputs, upgrades, and battery) as before.

## [26.1.2.8]

### Fixed

* Powered Furnace crash when a third-party mod (for example, GeOre) registers a non-`SmeltingRecipe` subclass under `RecipeType.SMELTING`. The vanilla-recipe fallback now matches `AbstractCookingRecipe` so any cooking-recipe class is accepted.

## [26.1.2.7]

### Added

* **Alloy Smelter** — new MV machine with 3 unique input slots and 1 output. Energy use is 3× the Advanced Powered Furnace (48 z/t). Slot constraint: any item placed in one input slot is rejected by the other two so stacks can't be split across slots. The recipe matcher prefers the highest-input recipe that matches your slots, so a 3-input alloy wins over any 2-input subset. Recipes ship for: bronze (3 copper + 1 tin → 4), electrum (1 silver + 1 gold → 2), invar (2 iron + 1 nickel → 3), constantan (1 copper + 1 nickel → 2), steel (1 industrial grade metal + 1 coal *or* 1 charcoal → 1), netherite (2 gold + 2 netherite scrap → 1 ingot, skips smithing), enderium (3 lead + 1 diamond dust + 2 ender pearls → 2), and three steel-based advanced alloy recipes (steel + 2 bronze + aluminum / steel + electrum + aluminum / steel + 2 invar + aluminum → 1 advanced alloy).
* **Steel material** — new ingot/dust/plate/rod/gear/wire/block set; gateway ingredient for every advanced alloy path.
* **Macerator: advanced alloy → mixed metal blend** — recovery loop for the existing mixed_metal_blend → advanced_alloy smelting recipe.
* **Smelting + blasting recipes for every material** with both an ingot and a smeltable input (dust / stone_ore / deepslate_ore / raw_ore → ingot). Vanilla-overlap materials (copper, gold, iron) smelt their FTBIC dust to the vanilla ingot.
* **Obsidian alloy chain** — `obsidian_dust` compresses to `obsidian_plate` (Compressor, 1:1), and extrudes to `obsidian_rod` (Extruder, 1 dust → 2 rods).
* **Constantan and silicon material set** restored — silicon as GEM (populates `c:silicon` for advanced circuit / energy crystal / lv solar panel), constantan with the full crafted-only set.
* **Nickel ore worldgen** — middle-band and small-vein placements wired into the existing biome modifier alongside aluminum/lead/tin.
* **EnderIO alloying compatibility** — 7 conditional alloy smelter recipes (`conductive_alloy`, `redstone_alloy`, `pulsating_alloy`, `energetic_alloy`, `vibrant_alloy`, `dark_steel`, `end_steel`) gated by `neoforge:mod_loaded` on `enderio`.
* **ConTeX (XFactHD) optional support** — built-in resource pack ships connected-texture variants for reinforced stone, reinforced glass, and all reinforced cables (LV/MV/HV/EV/IV/burnt). Force-loaded only when the `contex` mod is present; uses the `ftbic:reinforced` block tag so all reinforced variants connect.
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

* Reduced uranium overworld spawn rate. Removed the y32–256 surface placement (count 50, no air discard) and halved the y-64–32 cave placement from count 4 to count 2. Uranium now stays rare and primarily underground.
* Iridium is ~25% rarer than diamond. Small placement count 7 → 5, buried count 4 → 3, large rarity filter 9 → 12.
* `data/minecraft/tags/block/{mineable/pickaxe,dragon_immune,wither_immune}` and `data/minecraft/tags/item/arrows` are now emitted from datagen instead of hand-maintained. The pickaxe tag iterates `FTBICElectricBlocks.ALL` so every electric block is auto-tagged on registration.

## [26.1.2.1]

### Changed

* Initial refresh release of FTB Industrial Contraptions for 26.1+
* A lot of internals have been rewritten
* Nuclear reactors now work!
* Likely introduced a lot of bugs! Please report them as you find them ❤️
