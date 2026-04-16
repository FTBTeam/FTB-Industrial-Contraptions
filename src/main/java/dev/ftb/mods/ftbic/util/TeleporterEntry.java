package dev.ftb.mods.ftbic.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public record TeleporterEntry(ResourceKey<Level> dimension, BlockPos pos, String name, double energyUse) {
	public static final StreamCodec<RegistryFriendlyByteBuf, TeleporterEntry> STREAM_CODEC = StreamCodec.composite(
			ResourceKey.streamCodec(Registries.DIMENSION), TeleporterEntry::dimension,
			BlockPos.STREAM_CODEC, TeleporterEntry::pos,
			ByteBufCodecs.STRING_UTF8, TeleporterEntry::name,
			ByteBufCodecs.DOUBLE, TeleporterEntry::energyUse,
			TeleporterEntry::new);
}
