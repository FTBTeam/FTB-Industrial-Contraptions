# Native energy receiver checks

Optional test addon for a disposable Minecraft 26.1.2 / NeoForge 126 dedicated server and Java 25. It is not part of the release and must never be installed on a live server.

1. Place the FTBIC build under test in this directory's `mods/` folder and build with `gradle -p tests/energy-receiver-regression jar` using Gradle 9.2.1.
2. Put the test addon and FTBIC in a separate, disposable server with a fresh flat world. Bind `server-ip=127.0.0.1`; use no players.
3. Start with `-Ditv.ftbenergy.fixture=true`. For the patched build also pass `-Ditv.ftbenergy.expectFast=true` to require zero side queries for a full receiver.
4. Run `ftbenergyqa` in the console and inspect `ftb-energy-qa.json`, then stop the server.
5. Repeat against the original build in a new fixture to compare the same assertions and polling workload.

Checks cover every facing/side mapping, full and drained storage, committed and aborted transfers, nested rollback, disabled sides, input limits, forwarding delegates and subclass overrides. The full-storage benchmark reads volatile requests across 256 distinct receivers, warms for at least one second, and measures nine batches of one million inserts. This deliberately prevents the JVM from deleting a constant empty polling loop. These timings describe this workload; they are not a whole-server TPS prediction.
