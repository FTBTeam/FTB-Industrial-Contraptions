package dev.ftb.mods.industrialcontraptions.registry;

import dev.ftb.mods.industrialcontraptions.IC;
import net.neoforged.fml.loading.FMLPaths;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class LegacyConfigMigration {
	private LegacyConfigMigration() {}

	public static void run() {
		Path configDir = FMLPaths.CONFIGDIR.get();
		Path legacy = configDir.resolve(LegacyRegistryAliases.LEGACY_MOD_ID + "-common.toml");
		Path current = configDir.resolve(IC.MOD_ID + "-common.toml");

		if (!Files.isRegularFile(legacy) || Files.exists(current)) {
			return;
		}

		try {
			Files.copy(legacy, current);
			IC.LOGGER.info("Migrated config {} to {}", legacy.getFileName(), current.getFileName());
		} catch (IOException e) {
			IC.LOGGER.warn("Failed to migrate config {} to {}: {}", legacy.getFileName(), current.getFileName(), e.getMessage());
		}
	}
}
