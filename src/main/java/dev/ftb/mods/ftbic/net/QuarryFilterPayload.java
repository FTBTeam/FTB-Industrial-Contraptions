package dev.ftb.mods.ftbic.net;

import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.block.entity.machine.QuarryBlockEntity;
import dev.ftb.mods.ftbic.screen.QuarryMenu;
import dev.ftb.mods.ftbic.util.QuarryFilter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record QuarryFilterPayload(int containerId, int action, int slot, String value) implements CustomPacketPayload {
    public static final int SET_BLOCK = 0, CLEAR_BLOCK = 1, TOGGLE_MODE = 2, TOGGLE_ORES = 3, SET_TAG = 4;
    public static final Type<QuarryFilterPayload> TYPE = new Type<>(FTBIC.id("quarry_filter"));
    public static final StreamCodec<RegistryFriendlyByteBuf, QuarryFilterPayload> STREAM_CODEC = StreamCodec.of(
            (buf, p) -> {
                buf.writeVarInt(p.containerId);
                buf.writeVarInt(p.action);
                buf.writeVarInt(p.slot);
                buf.writeUtf(p.value, 100);
            },
            buf -> new QuarryFilterPayload(buf.readVarInt(), buf.readVarInt(), buf.readVarInt(), buf.readUtf(100)));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handleOnServer(QuarryFilterPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) apply(player, payload);
        });
    }

    public static boolean apply(ServerPlayer player, QuarryFilterPayload payload) {
        if (!(player.containerMenu instanceof QuarryMenu menu)
                || menu.containerId != payload.containerId
                || !(menu.blockEntity instanceof QuarryBlockEntity quarry)
                || !menu.stillValid(player)
                || !player.mayBuild()) return false;
        var pos = quarry.getBlockPos();
        if (quarry.getLevel() != player.level()
                || player.level().getBlockEntity(pos) != quarry
                || player.distanceToSqr(pos.getCenter()) > 64
                || !player.level().mayInteract(player, pos)) return false;
        QuarryFilter old = quarry.getFilter();
        QuarryFilter next;
        switch (payload.action) {
            case SET_BLOCK -> {
                if (payload.slot < 0 || payload.slot >= QuarryFilter.BLOCK_SLOTS) return false;
                ItemStack stack = menu.getCarried();
                if (!(stack.getItem() instanceof BlockItem)) return false;
                next = old.withBlock(payload.slot, stack);
            }
            case CLEAR_BLOCK -> {
                if (payload.slot < 0 || payload.slot >= QuarryFilter.BLOCK_SLOTS) return false;
                next = old.withBlock(payload.slot, ItemStack.EMPTY);
            }
            case TOGGLE_MODE -> next = old.withWhitelist(!old.whitelist());
            case TOGGLE_ORES -> next = old.withOreOnly(!old.oreOnly());
            case SET_TAG -> {
                if (payload.slot < 0 || payload.slot >= QuarryFilter.TAG_SLOTS) return false;
                String name = payload.value.trim();
                if (name.startsWith("#")) name = name.substring(1);
                if (name.length() > 64 || !name.isEmpty() && Identifier.tryParse(name) == null) return false;
                next = old.withTag(payload.slot, name);
            }
            default -> {
                return false;
            }
        }
        quarry.setFilter(next);
        return true;
    }
}
