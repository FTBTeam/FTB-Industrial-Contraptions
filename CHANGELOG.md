# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

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