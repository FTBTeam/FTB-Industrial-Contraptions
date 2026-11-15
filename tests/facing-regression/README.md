# Native facing and side-mode regression checks

This optional addon is for a disposable Minecraft 26.1.2 / NeoForge 126 server on Java 25. It is separate from the release and must not be installed on a live server.

1. Put the FTBIC build under test in this directory's `mods/` folder. Build with `gradle -p tests/facing-regression jar` using Gradle 9.2.1.
2. Use a fresh flat world, `server-ip=127.0.0.1`, and no players. Add the test addon and start with `-Ditv.facing.fixture=true`.
3. Run `facingqa` in the local console, read `facing-qa.json`, and stop the server.
4. Repeat with the other FTBIC build in a new fixture. The same assertions must pass before comparing the nine timing measurements.

The checks exercise horizontal and vertical rotations, repeated queries, different defaults, no-rotation machines, runtime property changes, failed property lookups, and subclass overrides. A non-electric state is exposed through a test accessor override: vanilla correctly rejects assigning that state to a machine. All eight encoded side-mode values are checked for every resource and face, including fallback for unrecognized values.

The two benchmarks query 256 distinct machines or configurations, warm for at least one second, and measure nine batches of five million lookups. Machine-facing requests read a volatile default. These are lookup workloads, not transfer-throughput or whole-server TPS measurements. Also run the separate energy-receiver regression addon to check transfer and rollback behavior.
