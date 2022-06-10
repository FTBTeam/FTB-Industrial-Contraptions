package dev.ftb.mods.ftbic.world;

import dev.ftb.mods.ftbic.FTBIC;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

public class ICBiomeTags
{
	public static final TagKey<Biome> ORE_SPAWN_BLACKLIST = TagKey.create(Registry.BIOME_REGISTRY, new ResourceLocation(FTBIC.MOD_ID, "ore_spawn_blacklist"));
}
