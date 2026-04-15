package dev.ftb.mods.ftbic.datagen;

import dev.ftb.mods.ftbic.FTBIC;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = FTBIC.MOD_ID)
public final class FTBICDataGen {

	@SubscribeEvent
	public static void gatherClientData(GatherDataEvent.Client event) {
		event.createProvider(FTBICModelProvider::new);
		event.createProvider(FTBICRecipeProvider.Runner::new);
	}

	private FTBICDataGen() {}
}
