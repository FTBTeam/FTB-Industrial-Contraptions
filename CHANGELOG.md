# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [26.1.2.2]

### Fixed
* Fixed crash on startup with neoforge 27 beta: `teleporter_capacity` and `charge_pad_capacity` config defaults (1,000,000) exceeded their validator max (100,000). Max raised to 10,000,000.

### Changed
* Reduced uranium overworld spawn rate. Removed the y32–256 surface placement (count 50, no air discard) and halved the y-64–32 cave placement from count 4 to count 2. Uranium now stays rare and primarily underground.
* Reactor chamber wall no longer swallows right-click when holding a block. Block placement works as expected; use an empty hand to open the reactor GUI.

## [26.1.2.1]

### Changed
* Initial refresh release of FTB Industrial Contraptions for 26.1+
* A lot of internals have been rewritten
* Nuclear reactors now work!
* Likely introduced a lot of bugs! Please report them as you find them ❤️