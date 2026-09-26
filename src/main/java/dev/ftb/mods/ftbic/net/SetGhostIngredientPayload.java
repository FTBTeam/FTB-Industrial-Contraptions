package dev.ftb.mods.ftbic.net;

import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.block.entity.machine.BatchFeederBlockEntity;
import dev.ftb.mods.ftbic.block.entity.machine.QuarryBlockEntity;
import dev.ftb.mods.ftbic.screen.ElectricBlockMenu;
import dev.ftb.mods.ftbic.screen.IronFurnaceMenu;
import dev.ftb.mods.ftbic.screen.QuarryMenu;
import dev.ftb.mods.ftbic.util.QuarryFilter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/** JEI supplies ghost data only. This packet never inserts real items or fluids. */
public record SetGhostIngredientPayload(int containerId, int slot, ItemStack item, FluidStack fluid)
        implements CustomPacketPayload {
    public static final Type<SetGhostIngredientPayload> TYPE = new Type<>(FTBIC.id("set_ghost_ingredient"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SetGhostIngredientPayload> STREAM_CODEC = StreamCodec.of(
            (buf, p) -> {
                buf.writeVarInt(p.containerId);
                buf.writeVarInt(p.slot);
                ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, p.item);
                FluidStack.OPTIONAL_STREAM_CODEC.encode(buf, p.fluid);
            },
            buf -> new SetGhostIngredientPayload(
                    buf.readVarInt(),
                    buf.readVarInt(),
                    ItemStack.OPTIONAL_STREAM_CODEC.decode(buf),
                    FluidStack.OPTIONAL_STREAM_CODEC.decode(buf)));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handleOnServer(SetGhostIngredientPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) apply(player, payload);
        });
    }

    public static boolean apply(ServerPlayer player, SetGhostIngredientPayload payload) {
        if (player.containerMenu instanceof IronFurnaceMenu menu) {
            var furnace = menu.blockEntity;
            if (menu.containerId != payload.containerId
                    || furnace == null
                    || !menu.stillValid(player)
                    || !player.mayBuild()
                    || payload.slot < 0
                    || payload.slot > 1
                    || !payload.fluid.isEmpty()) return false;
            var pos = furnace.getBlockPos();
            if (furnace.isRemoved()
                    || furnace.getLevel() != player.level()
                    || player.level().getBlockEntity(pos) != furnace
                    || player.distanceToSqr(pos.getCenter()) > 64
                    || !player.level().mayInteract(player, pos)) return false;
            furnace.setInputLock(payload.slot, payload.item);
            return true;
        }
        if (!(player.containerMenu instanceof ElectricBlockMenu menu)
                || menu.containerId != payload.containerId
                || menu.blockEntity == null
                || !menu.stillValid(player)
                || !player.mayBuild()
                || payload.slot < 0) return false;
        var machine = menu.blockEntity;
        var pos = machine.getBlockPos();
        if (machine.getLevel() != player.level()
                || player.level().getBlockEntity(pos) != machine
                || player.distanceToSqr(pos.getCenter()) > 64
                || !player.level().mayInteract(player, pos)) return false;
        if (machine instanceof QuarryBlockEntity quarry && menu instanceof QuarryMenu) {
            if (payload.slot < QuarryFilter.BLOCK_SLOTS
                    || payload.slot >= QuarryFilter.BLOCK_SLOTS * 2
                    || !(payload.item.getItem() instanceof BlockItem)
                    || !payload.fluid.isEmpty()) return false;
            quarry.setFilter(quarry.getFilter().withBlock(payload.slot - QuarryFilter.BLOCK_SLOTS, payload.item));
            return true;
        }
        if (machine instanceof BatchFeederBlockEntity feeder) {
            if (payload.slot == BatchFeederBlockEntity.PATTERN_SIZE
                    && payload.item.isEmpty()
                    && !payload.fluid.isEmpty()) {
                feeder.setBatchFluid(payload.fluid);
                return true;
            }
            if (payload.slot >= BatchFeederBlockEntity.PATTERN_SIZE
                    || payload.item.isEmpty()
                    || !payload.fluid.isEmpty()) return false;
            feeder.setBatchItem(payload.slot, payload.item);
            return true;
        }
        if (!machine.supportsInputLocks()
                || payload.slot >= machine.inputItems.length
                || payload.item.isEmpty()
                || !payload.fluid.isEmpty()) return false;
        machine.setInputLock(payload.slot, payload.item);
        return true;
    }
}
