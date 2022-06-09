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
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;

public class FTBICBiomeModifierDataGen implements DataProvider {
	private static final Logger LOGGER = LogManager.getLogger();
	private final RegistryOps<JsonElement> ops = RegistryOps.create(JsonOps.INSTANCE, RegistryAccess.BUILTIN.get());

	private final BiomeModifier oreBiomeModifier;
	private final String biomeModifierPathString;
	private final Path biomeModifierPath;

	public FTBICBiomeModifierDataGen(DataGenerator gen, String modid) {
		Path outputFolder = gen.getOutputFolder();
		String directory = PackType.SERVER_DATA.getDirectory();

		ResourceLocation modifiersRegistry = ForgeRegistries.Keys.BIOME_MODIFIERS.location();
		this.biomeModifierPathString = String.join("/", directory, modid, modifiersRegistry.getNamespace(), modifiersRegistry.getPath(), "ore_features_biome_modifier.json");
		this.biomeModifierPath = outputFolder.resolve(biomeModifierPathString);

		this.oreBiomeModifier = new OreBiomeModifier(
				new HolderSet.Named<>(ops.registry(Registry.BIOME_REGISTRY).get(), BiomeTags.IS_BADLANDS),
				GenerationStep.Decoration.TOP_LAYER_MODIFICATION,
				HolderSet.direct(OreGeneration.PLACEMENTS.stream().toList())
		);
	}

	@Override
	public void run(final CachedOutput cache)
	{
		BiomeModifier.DIRECT_CODEC.encodeStart(ops, oreBiomeModifier)
				.resultOrPartial(msg -> LOGGER.error("Failed to encode {}: {}", biomeModifierPathString, msg)) // Log error on encode failure.
				.ifPresent(json -> // Output to file on encode success.
				{
					try
					{
						DataProvider.saveStable(cache, json, biomeModifierPath);
					}
					catch (IOException e) // The throws can't deal with this exception, because we're inside the ifPresent.
					{
						LOGGER.error("Failed to save " + biomeModifierPathString, e);
					}
				});
	}

	@Override
	public String getName()
	{
		return FTBIC.MOD_ID + " biome modifier data provider";
	}
}
