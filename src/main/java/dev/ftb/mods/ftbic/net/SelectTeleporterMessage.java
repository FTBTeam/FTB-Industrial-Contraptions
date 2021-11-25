package dev.ftb.mods.ftbic.net;

import dev.ftb.mods.ftbic.block.entity.machine.TeleporterBlockEntity;
import me.shedaniel.architectury.networking.NetworkManager;
import me.shedaniel.architectury.networking.simple.BaseC2SMessage;
import me.shedaniel.architectury.networking.simple.MessageType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class SelectTeleporterMessage extends BaseC2SMessage {
	private final BlockPos teleporter;
	private final ResourceKey<Level> dimension;
	private final BlockPos pos;

	public SelectTeleporterMessage(BlockPos t, ResourceKey<Level> d, BlockPos p) {
		teleporter = t;
		dimension = d;
		pos = p;
	}

	public SelectTeleporterMessage(FriendlyByteBuf buf) {
		teleporter = buf.readBlockPos();
		dimension = ResourceKey.create(Registry.DIMENSION_REGISTRY, buf.readResourceLocation());
		pos = buf.readBlockPos();
	}

	@Override
	public MessageType getType() {
		return FTBICNet.SELECT_TELEPORTER;
	}

	@Override
	public void write(FriendlyByteBuf buf) {
		buf.writeBlockPos(teleporter);
		buf.writeResourceLocation(dimension.location());
		buf.writeBlockPos(pos);
	}

	@Override
	public void handle(NetworkManager.PacketContext ctx) {
		BlockEntity entity = ctx.getPlayer().level.getBlockEntity(teleporter);

		if (entity instanceof TeleporterBlockEntity) {
			((TeleporterBlockEntity) entity).select((ServerPlayer) ctx.getPlayer(), dimension, pos);
		}
	}
}
