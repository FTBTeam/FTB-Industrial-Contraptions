package dev.ftb.mods.ftbic.util;

import dev.ftb.mods.ftbic.block.entity.machine.TeleporterBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class TeleporterEntry implements Comparable<TeleporterEntry> {
	public final ResourceKey<Level> dimension;
	public final BlockPos pos;
	public final String name;
	public final String placerName;
	public final double energyUse;

	public TeleporterEntry(TeleporterBlockEntity e, double u) {
		dimension = e.getLevel().dimension();
		pos = e.getBlockPos();
		name = e.name;
		placerName = e.isPublic ? e.placerName : "";
		energyUse = u;
	}

	public TeleporterEntry(FriendlyByteBuf buf) {
		dimension = ResourceKey.create(Registry.DIMENSION_REGISTRY, buf.readResourceLocation());
		pos = buf.readBlockPos();
		name = buf.readUtf(Short.MAX_VALUE);
		placerName = buf.readUtf(Short.MAX_VALUE);
		energyUse = buf.readDouble();
	}

	public void write(FriendlyByteBuf buf) {
		buf.writeResourceLocation(dimension.location());
		buf.writeBlockPos(pos);
		buf.writeUtf(name, Short.MAX_VALUE);
		buf.writeUtf(placerName, Short.MAX_VALUE);
		buf.writeDouble(energyUse);
	}

	@Override
	public int compareTo(@NotNull TeleporterEntry o) {
		int i = Boolean.compare(placerName.isEmpty(), o.placerName.isEmpty());

		if (i == 0) {
			i = Double.compare(energyUse, o.energyUse);
		}

		if (i == 0) {
			i = name.compareToIgnoreCase(o.name);
		}

		return i;
	}
}
