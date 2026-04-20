package dev.ftb.mods.ftbic.net;

import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.block.entity.machine.ReactorSimulatorBlockEntity;
import dev.ftb.mods.ftbic.item.reactor.NuclearReactor;
import dev.ftb.mods.ftbic.screen.ReactorSimulatorMenu;
import dev.ftb.mods.ftbic.util.ReactorDesign;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SimulatorActionPayload(byte action, int intA, int intB, String stringA, ItemStack itemA) implements CustomPacketPayload {
	public static final byte START = 0;
	public static final byte PAUSE = 1;
	public static final byte RESET = 2;
	public static final byte RESTART = 3;
	public static final byte SET_SPEED = 4;
	public static final byte SET_CHAMBERS = 5;
	public static final byte SET_WATER = 6;
	public static final byte SET_SLOT = 7;
	public static final byte CLEAR_SLOT = 8;
	public static final byte ANALYZE = 9;
	public static final byte IMPORT = 10;

	public static final int MAX_IMPORT_JSON_BYTES = 8192;
	public static final TagKey<Item> COMPONENT_TAG = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(FTBIC.MOD_ID, "reactor_component"));

	public static final Type<SimulatorActionPayload> TYPE = new Type<>(FTBIC.id("simulator_action"));

	public static final StreamCodec<RegistryFriendlyByteBuf, SimulatorActionPayload> STREAM_CODEC = StreamCodec.of(
			(buf, pl) -> {
				buf.writeByte(pl.action);
				buf.writeVarInt(pl.intA);
				buf.writeVarInt(pl.intB);
				buf.writeUtf(pl.stringA == null ? "" : pl.stringA, MAX_IMPORT_JSON_BYTES);
				ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, pl.itemA == null ? ItemStack.EMPTY : pl.itemA);
			},
			buf -> new SimulatorActionPayload(
					buf.readByte(),
					buf.readVarInt(),
					buf.readVarInt(),
					buf.readUtf(MAX_IMPORT_JSON_BYTES),
					ItemStack.OPTIONAL_STREAM_CODEC.decode(buf)
			)
	);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static SimulatorActionPayload simple(byte action) {
		return new SimulatorActionPayload(action, 0, 0, "", ItemStack.EMPTY);
	}

	public static SimulatorActionPayload setSpeed(int index) {
		return new SimulatorActionPayload(SET_SPEED, index, 0, "", ItemStack.EMPTY);
	}

	public static SimulatorActionPayload setChambers(int count) {
		return new SimulatorActionPayload(SET_CHAMBERS, count, 0, "", ItemStack.EMPTY);
	}

	public static SimulatorActionPayload setWater(int thousandths) {
		return new SimulatorActionPayload(SET_WATER, thousandths, 0, "", ItemStack.EMPTY);
	}

	public static SimulatorActionPayload setSlot(int slot, ItemStack stack) {
		return new SimulatorActionPayload(SET_SLOT, slot, 0, "", stack == null ? ItemStack.EMPTY : stack.copyWithCount(1));
	}

	public static SimulatorActionPayload clearSlot(int slot) {
		return new SimulatorActionPayload(CLEAR_SLOT, slot, 0, "", ItemStack.EMPTY);
	}

	public static SimulatorActionPayload importDesign(String json) {
		return new SimulatorActionPayload(IMPORT, 0, 0, json == null ? "" : json, ItemStack.EMPTY);
	}

	public static void handleOnServer(SimulatorActionPayload payload, IPayloadContext context) {
		context.enqueueWork(() -> {
			if (!(context.player() instanceof ServerPlayer sp)) return;
			if (!(sp.containerMenu instanceof ReactorSimulatorMenu menu)) return;
			if (!(menu.blockEntity instanceof ReactorSimulatorBlockEntity be)) return;
			switch (payload.action) {
				case START -> be.start();
				case PAUSE -> be.pause();
				case RESET -> be.reset();
				case RESTART -> be.restart();
				case SET_SPEED -> be.setSpeedIndex(payload.intA);
				case SET_CHAMBERS -> be.setChambers(payload.intA);
				case SET_WATER -> be.setWaterThousandths(payload.intA);
				case SET_SLOT -> {
					if (payload.intA < 0 || payload.intA >= NuclearReactor.MAX_SLOTS) return;
					ItemStack s = payload.itemA;
					if (!s.isEmpty() && !s.is(COMPONENT_TAG)) return;
					be.setSlotItem(payload.intA, s);
				}
				case CLEAR_SLOT -> {
					if (payload.intA < 0 || payload.intA >= NuclearReactor.MAX_SLOTS) return;
					be.clearSlot(payload.intA);
				}
				case ANALYZE -> be.runStabilityCheck();
				case IMPORT -> {
					try {
						ReactorDesign design = ReactorDesign.fromJson(payload.stringA);
						if (design.slots().size() > NuclearReactor.MAX_SLOTS * 2) {
							sp.sendSystemMessage(Component.literal("Import failed: too many slots").withStyle(ChatFormatting.RED));
							return;
						}
						be.applyDesign(design);
						sp.sendSystemMessage(Component.literal("Reactor design imported.").withStyle(ChatFormatting.GREEN));
					} catch (IllegalArgumentException ex) {
						sp.sendSystemMessage(Component.literal("Import failed: " + ex.getMessage()).withStyle(ChatFormatting.RED));
					}
				}
			}
		});
	}
}
