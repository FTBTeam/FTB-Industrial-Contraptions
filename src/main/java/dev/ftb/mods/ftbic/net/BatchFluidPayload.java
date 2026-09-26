package dev.ftb.mods.ftbic.net;

import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.block.entity.machine.BatchFeederBlockEntity;
import dev.ftb.mods.ftbic.screen.BatchFeederMenu;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidUtil;

/** Fluid identities come from server-side containers or the feeder's buffer. */
public record BatchFluidPayload(int containerId, int action, int amount) implements CustomPacketPayload {
    public static final int SELECT = 0;
    public static final int CLEAR = 1;
    public static final int SET_AMOUNT = 2;
    public static final int TRANSFER_CONTAINER = 3;
    public static final Type<BatchFluidPayload> TYPE = new Type<>(FTBIC.id("batch_fluid"));
    public static final StreamCodec<RegistryFriendlyByteBuf, BatchFluidPayload> STREAM_CODEC = StreamCodec.of(
            (buf, p) -> {
                buf.writeVarInt(p.containerId);
                buf.writeVarInt(p.action);
                buf.writeVarInt(p.amount);
            },
            buf -> new BatchFluidPayload(buf.readVarInt(), buf.readVarInt(), buf.readVarInt()));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handleOnServer(BatchFluidPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) apply(player, payload);
        });
    }

    public static boolean apply(ServerPlayer player, BatchFluidPayload payload) {
        if (!(player.containerMenu instanceof BatchFeederMenu menu)
                || menu.containerId != payload.containerId
                || !(menu.blockEntity instanceof BatchFeederBlockEntity feeder)
                || !menu.stillValid(player)
                || !player.mayBuild()) return false;
        var pos = feeder.getBlockPos();
        if (feeder.getLevel() != player.level()
                || player.level().getBlockEntity(pos) != feeder
                || player.distanceToSqr(pos.getCenter()) > 64
                || !player.level().mayInteract(player, pos)) return false;
        switch (payload.action) {
            case SELECT -> {
                FluidStack selected = menu.getCarried().isEmpty()
                        ? feeder.getBufferFluid()
                        : FluidUtil.getFirstStackContained(menu.getCarried());
                if (selected.isEmpty()) return false;
                if (menu.getCarried().isEmpty()) selected.setAmount(Math.min(1_000, selected.getAmount()));
                feeder.setBatchFluid(selected);
            }
            case CLEAR -> feeder.setBatchFluid(FluidStack.EMPTY);
            case SET_AMOUNT -> {
                FluidStack selected = feeder.getBatchFluid();
                if (selected.isEmpty() || payload.amount < 1 || payload.amount > BatchFeederBlockEntity.TANK_CAPACITY)
                    return false;
                feeder.setBatchFluid(selected.copyWithAmount(payload.amount));
            }
            case TRANSFER_CONTAINER -> {
                if (menu.getCarried().isEmpty()) return false;
                var container =
                        ItemAccess.forPlayerCursor(player, menu).oneByOne().getCapability(Capabilities.Fluid.ITEM);
                if (container == null) return false;
                var tank = feeder.fluidHandler.manualAccess;
                boolean pickup = true;
                var moved = ResourceHandlerUtil.moveFirst(tank, container, fluid -> true, Integer.MAX_VALUE, null);
                if (moved == null) {
                    pickup = false;
                    moved = ResourceHandlerUtil.moveFirst(container, tank, fluid -> true, Integer.MAX_VALUE, null);
                }
                if (moved == null) return false;
                FluidUtil.triggerSoundAndGameEvent(moved.resource(), player.level(), pos.getCenter(), player, pickup);
                menu.broadcastChanges();
            }
            default -> {
                return false;
            }
        }
        return true;
    }
}
