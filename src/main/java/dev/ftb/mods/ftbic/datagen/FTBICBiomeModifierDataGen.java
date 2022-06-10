package dev.ftb.mods.ftbic.datagen;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.world.OreBiomeModifier;
import dev.ftb.mods.ftbic.world.OreGeneration;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;

public class FTBICBiomeModifierDataGen implements DataProvider {
	private static final Logger LOGGER = LogManager.getLogger();

	private final RegistryOps<JsonElement> ops = RegistryOps.create(JsonOps.INSTANCE, RegistryAccess.BUILTIN.get());
	private final Path modifierPath;
	private final OreBiomeModifier oreBiomeModifier;

	public FTBICBiomeModifierDataGen(DataGenerator gen, String modid) {
		Path outputFolder = gen.getOutputFolder();
		String directory = PackType.SERVER_DATA.getDirectory();

		Registry<PlacedFeature> placedFeatures = ops.registry(Registry.PLACED_FEATURE_REGISTRY).get();

		ResourceLocation modifiersRegistry = ForgeRegistries.Keys.BIOME_MODIFIERS.location();
		this.modifierPath = outputFolder.resolve(String.join("/", directory, modid, modifiersRegistry.getNamespace(), modifiersRegistry.getPath(), "ftbic_ore_biome_modifier.json"));

		this.oreBiomeModifier = new OreBiomeModifier(
				GenerationStep.Decoration.UNDERGROUND_ORES,
				HolderSet.direct(OreGeneration.PLACEMENTS.stream().map(e -> placedFeatures.getOrCreateHolderOrThrow(e.getKey())).toList())
		);
	}

	@Override
	public void run(final CachedOutput cache)
	{
		BiomeModifier.DIRECT_CODEC.encodeStart(ops, oreBiomeModifier)
				.resultOrPartial(msg -> LOGGER.error("Failed to encode {}: {}", modifierPath.toAbsolutePath(), msg)) // Log error on encode failure.
				.ifPresent(json -> // Output to file on encode success.
				{
					try
					{
						DataProvider.saveStable(cache, json, modifierPath);
					}
					catch (IOException e) // The throws can't deal with this exception, because we're inside the ifPresent.
					{
						LOGGER.error("Failed to save " + modifierPath.toAbsolutePath(), e);
					}
				});
	}

	@Override
	public String getName()
	{
		return FTBIC.MOD_ID + " biome modifier data provider";
	}
}
