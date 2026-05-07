package dev.ftb.mods.ftbic.integration.contex;

import dev.ftb.mods.ftbic.FTBIC;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddPackFindersEvent;

@EventBusSubscriber(modid = FTBIC.MOD_ID)
public final class FTBICContexCompat {
	private static final String CONTEX_MOD_ID = "contex";

	@SubscribeEvent
	public static void onAddPackFinders(AddPackFindersEvent event) {
		if (event.getPackType() != PackType.CLIENT_RESOURCES) return;
		if (!ModList.get().isLoaded(CONTEX_MOD_ID)) return;

		event.addPackFinders(
				FTBIC.id("contex_compat"),
				PackType.CLIENT_RESOURCES,
				Component.literal("FTB IC ConTeX Compat"),
				PackSource.BUILT_IN,
				true,
				Pack.Position.TOP);
	}

	private FTBICContexCompat() {}
}
