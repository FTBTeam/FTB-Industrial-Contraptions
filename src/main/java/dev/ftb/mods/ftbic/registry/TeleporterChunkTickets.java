package dev.ftb.mods.ftbic.registry;

import dev.ftb.mods.ftbic.FTBIC;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.world.chunk.RegisterTicketControllersEvent;
import net.neoforged.neoforge.common.world.chunk.TicketController;

@EventBusSubscriber(modid = FTBIC.MOD_ID)
public final class TeleporterChunkTickets {
	public static final TicketController CONTROLLER = new TicketController(FTBIC.id("teleporter_link"));

	@SubscribeEvent
	public static void onRegister(RegisterTicketControllersEvent event) {
		event.register(CONTROLLER);
	}

	private TeleporterChunkTickets() {}
}
