package dev.ftb.mods.industrialcontraptions.integration.guideme;

import dev.ftb.mods.industrialcontraptions.IC;
import guideme.Guide;
import net.minecraft.resources.Identifier;

public class ICGuide {

	public static final Identifier GUIDE_ID = IC.id("guide");

	private static Guide guide;

	public static void init() {
		try {
			guide = Guide.builder(GUIDE_ID)
				.defaultNamespace(IC.MOD_ID)
				.folder("ic")
				.build();
			IC.LOGGER.info("Industrial Contraptions GuideME guide registered");
		} catch (Exception e) {
			IC.LOGGER.warn("Failed to initialize GuideME integration: {}", e.getMessage());
		}
	}

	public static Guide getGuide() {
		return guide;
	}

	public static boolean isGuideAvailable() {
		try {
			Class.forName("guideme.Guide");
			return true;
		} catch (ClassNotFoundException e) {
			return false;
		}
	}
}
