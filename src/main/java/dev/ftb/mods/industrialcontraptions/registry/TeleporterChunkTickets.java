package dev.ftb.mods.industrialcontraptions.registry;

import dev.ftb.mods.industrialcontraptions.IC;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.world.chunk.RegisterTicketControllersEvent;
import net.neoforged.neoforge.common.world.chunk.TicketController;

@EventBusSubscriber(modid = IC.MOD_ID)
public final class TeleporterChunkTickets {
	public static final TicketController CONTROLLER = new TicketController(IC.id("teleporter_link"));

	@SubscribeEvent
	public static void onRegister(RegisterTicketControllersEvent event) {
		event.register(CONTROLLER);
	}

	private TeleporterChunkTickets() {}
}
