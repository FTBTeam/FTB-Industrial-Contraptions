package dev.ftb.mods.ftbic.net;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseC2SMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

public class SelectTeleporterMessage extends BaseC2SMessage {
	private final ResourceKey<Level> dimension;
	private final BlockPos pos;

	public SelectTeleporterMessage(ResourceKey<Level> d, BlockPos p) {
		dimension = d;
		pos = p;
	}

	public SelectTeleporterMessage(FriendlyByteBuf buf) {
		dimension = ResourceKey.create(Registry.DIMENSION_REGISTRY, buf.readResourceLocation());
		pos = buf.readBlockPos();
	}

	@Override
	public MessageType getType() {
		return FTBICNet.SELECT_TELEPORTER;
	}

	@Override
	public void write(FriendlyByteBuf buf) {
		buf.writeResourceLocation(dimension.location());
		buf.writeBlockPos(pos);
	}

	@Override
	public void handle(NetworkManager.PacketContext ctx) {
		ServerPlayer p = (ServerPlayer) ctx.getPlayer();

		// Teleporter menu
	}
}
