package dev.ftb.mods.ftbic.net;

import dev.ftb.mods.ftbic.FTBIC;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

/**
 * Central networking hub. Registers all FTBIC payloads at mod init and provides send helpers.
 */
@EventBusSubscriber(modid = FTBIC.MOD_ID)
public final class FTBICNet {

	@SubscribeEvent
	public static void registerPayloads(RegisterPayloadHandlersEvent event) {
		var registrar = event.registrar(FTBIC.MOD_ID);

		registrar.playToClient(
				MoveLaserPayload.TYPE,
				MoveLaserPayload.STREAM_CODEC,
				MoveLaserPayload::handleOnClient);

		registrar.playToServer(
				SelectTeleporterPayload.TYPE,
				SelectTeleporterPayload.STREAM_CODEC,
				SelectTeleporterPayload::handleOnServer);

		registrar.playToServer(
				SelectCraftingRecipePayload.TYPE,
				SelectCraftingRecipePayload.STREAM_CODEC,
				SelectCraftingRecipePayload::handleOnServer);

		registrar.playToClient(
				TeleporterListPayload.TYPE,
				TeleporterListPayload.STREAM_CODEC,
				TeleporterListPayload::handleOnClient);
	}

	public static void sendToPlayer(ServerPlayer player, CustomPacketPayload payload) {
		PacketDistributor.sendToPlayer(player, payload);
	}

	public static void sendToAll(CustomPacketPayload payload) {
		PacketDistributor.sendToAllPlayers(payload);
	}

	public static void sendToServer(CustomPacketPayload payload) {
		ClientPacketDistributor.sendToServer(payload);
	}

	private FTBICNet() {}
}
