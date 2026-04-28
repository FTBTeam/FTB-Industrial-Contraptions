# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

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
