package dev.ftb.mods.ftbic.integration.guideme;

import dev.ftb.mods.ftbic.FTBIC;
import guideme.Guide;
import guideme.GuideItemSettings;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.List;
import java.util.Optional;

public class FTBICGuide {

	public static final Identifier GUIDE_ID = FTBIC.id("guide");

	private static Guide guide;

	public static void init() {
		try {
			guide = Guide.builder(GUIDE_ID)
				.defaultNamespace(FTBIC.MOD_ID)
				.folder("ftbic")
				.itemSettings(new GuideItemSettings(
					Optional.of(Component.translatable("item.ftbic.guide")),
					List.of(),
					Optional.of(FTBIC.id("item/guide"))
				))
				.build();
			FTBIC.LOGGER.info("FTBIC GuideME guide registered");
		} catch (Exception e) {
			FTBIC.LOGGER.warn("Failed to initialize GuideME integration: {}", e.getMessage());
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
