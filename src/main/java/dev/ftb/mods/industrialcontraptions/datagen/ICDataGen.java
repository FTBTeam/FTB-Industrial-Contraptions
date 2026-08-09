package dev.ftb.mods.industrialcontraptions.datagen;

import dev.ftb.mods.industrialcontraptions.IC;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = IC.MOD_ID)
public final class ICDataGen {

	@SubscribeEvent
	public static void gatherClientData(GatherDataEvent.Client event) {
		event.createProvider(ICModelProvider::new);
		event.createProvider(ICRecipeProvider.Runner::new);
		event.createProvider(ICLanguageProvider::new);
		event.createProvider(ICBlockTagsProvider::new);
		event.createProvider(ICItemTagsProvider::new);
		event.createProvider(ICLootTableProvider::new);
	}

	private ICDataGen() {}
}
