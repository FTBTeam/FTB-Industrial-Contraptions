package dev.ftb.mods.ftbic.events;

import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.block.entity.machine.TeleporterBlockEntity;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;

@EventBusSubscriber(modid = FTBIC.MOD_ID)
public final class TeleporterLifecycleHandler {
	@SubscribeEvent
	public static void onLevelUnload(LevelEvent.Unload event) {
		TeleporterBlockEntity.purgeLevel(event.getLevel() instanceof Level l ? l : null);
	}

	@SubscribeEvent
	public static void onServerStopping(ServerStoppingEvent event) {
		TeleporterBlockEntity.purgeAll();
	}

	private TeleporterLifecycleHandler() {}
}
