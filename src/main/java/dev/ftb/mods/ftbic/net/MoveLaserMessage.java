package dev.ftb.mods.ftbic.net;

import dev.ftb.mods.ftbic.block.entity.machine.DiggingBaseBlockEntity;
import me.shedaniel.architectury.networking.NetworkManager;
import me.shedaniel.architectury.networking.simple.BaseS2CMessage;
import me.shedaniel.architectury.networking.simple.MessageType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;

public class MoveLaserMessage extends BaseS2CMessage {
	private final BlockPos pos;
	private final float x, z;
	private final int y;

	public MoveLaserMessage(BlockPos p, float lx, int ly, float lz) {
		pos = p;
		x = lx;
		y = ly;
		z = lz;
	}

	public MoveLaserMessage(FriendlyByteBuf buf) {
		pos = buf.readBlockPos();
		x = buf.readFloat();
		y = buf.readVarInt();
		z = buf.readFloat();
	}

	@Override
	public MessageType getType() {
		return FTBICNet.MOVE_LASER;
	}

	@Override
	public void write(FriendlyByteBuf buf) {
		buf.writeBlockPos(pos);
		buf.writeFloat(x);
		buf.writeVarInt(y);
		buf.writeFloat(z);
	}

	@Override
	public void handle(NetworkManager.PacketContext ctx) {
		BlockEntity entity = ctx.getPlayer().level.getBlockEntity(pos);

		if (entity instanceof DiggingBaseBlockEntity) {
			((DiggingBaseBlockEntity) entity).moveLaser(x, y, z);
		}
	}
}
