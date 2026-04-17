package dev.ftb.mods.ftbic.integration.guideme;

import dev.ftb.mods.ftbic.FTBIC;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.model.standalone.SimpleUnbakedStandaloneModel;
import net.neoforged.neoforge.client.model.standalone.StandaloneModelKey;

@EventBusSubscriber(modid = FTBIC.MOD_ID, value = Dist.CLIENT)
public final class FTBICClientModels {
	public static final StandaloneModelKey<Void> GUIDE_ITEM_MODEL = new StandaloneModelKey<>(() -> "ftbic:guide");

	@SubscribeEvent
	public static void registerStandaloneModels(ModelEvent.RegisterStandalone event) {
		event.register(GUIDE_ITEM_MODEL,
				new SimpleUnbakedStandaloneModel<>(FTBIC.id("item/guide"), (resolved, baker, name) -> null));
	}

	private FTBICClientModels() {}
}
