package dev.ftb.mods.ftbic.net;

import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.block.entity.machine.BatchFeederBlockEntity;
import dev.ftb.mods.ftbic.screen.ElectricBlockMenu;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/** Only intent crosses the wire: ghost items are copied from the server's carried stack. */
public record GhostSlotPayload(int containerId, int slot, int action) implements CustomPacketPayload {
    public static final Type<GhostSlotPayload> TYPE = new Type<>(FTBIC.id("ghost_slot"));
    public static final StreamCodec<RegistryFriendlyByteBuf, GhostSlotPayload> STREAM_CODEC = StreamCodec.of(
            (buf, p) -> {
                buf.writeVarInt(p.containerId);
                buf.writeVarInt(p.slot);
                buf.writeVarInt(p.action);
            },
            buf -> new GhostSlotPayload(buf.readVarInt(), buf.readVarInt(), buf.readVarInt()));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handleOnServer(GhostSlotPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) apply(player, payload);
        });
    }

    public static boolean apply(ServerPlayer player, GhostSlotPayload payload) {
        if (!(player.containerMenu instanceof ElectricBlockMenu menu)
                || menu.containerId != payload.containerId
                || menu.blockEntity == null
                || !menu.stillValid(player)
                || !player.mayBuild()) return false;
        var machine = menu.blockEntity;
        var pos = machine.getBlockPos();
        if (machine.getLevel() != player.level()
                || player.level().getBlockEntity(pos) != machine
                || player.distanceToSqr(pos.getCenter()) > 64
                || !player.level().mayInteract(player, pos)
                || payload.slot < 0
                || payload.action < 0
                || payload.action > 3) return false;
        if (machine.supportsInputLocks()) {
            if (payload.slot >= machine.inputItems.length || payload.action > 1) return false;
            ItemStack assignment = menu.getCarried().isEmpty() ? machine.inputItems[payload.slot] : menu.getCarried();
            if (payload.action == 0 && assignment.isEmpty()) return false;
            machine.setInputLock(payload.slot, payload.action == 1 ? ItemStack.EMPTY : assignment);
            return true;
        }
        if (machine instanceof BatchFeederBlockEntity feeder) {
            if (payload.slot >= BatchFeederBlockEntity.PATTERN_SIZE) return false;
            ItemStack assignment = feeder.getBatchItem(payload.slot);
            if (payload.action == 1) assignment = ItemStack.EMPTY;
            else if (payload.action == 0 && !menu.getCarried().isEmpty()) assignment = menu.getCarried();
            else if (!assignment.isEmpty()) assignment.setCount(assignment.getCount() + (payload.action == 3 ? -1 : 1));
            feeder.setBatchItem(payload.slot, assignment);
            return true;
        }
        return false;
    }
}
