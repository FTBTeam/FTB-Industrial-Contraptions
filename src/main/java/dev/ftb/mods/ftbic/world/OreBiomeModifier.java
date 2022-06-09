package dev.ftb.mods.ftbic.world;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.ftb.mods.ftbic.FTBIC;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.world.BiomeGenerationSettingsBuilder;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ModifiableBiomeInfo;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

// Shameless copy and paste from forge until this is less awful
public record OreBiomeModifier(
		HolderSet<Biome> biomes,
		GenerationStep.Decoration generationStage,
		HolderSet<PlacedFeature> features
) implements BiomeModifier {

	public static Codec<OreBiomeModifier> CODEC = RecordCodecBuilder.create(builder -> builder.group(
			Biome.LIST_CODEC.fieldOf("biomes").forGetter(OreBiomeModifier::biomes),
			Codec.STRING.comapFlatMap(OreBiomeModifier::generationStageFromString, GenerationStep.Decoration::toString).fieldOf("generation_stage").forGetter(OreBiomeModifier::generationStage),
			PlacedFeature.LIST_CODEC.fieldOf("features").forGetter(OreBiomeModifier::features)
	).apply(builder, OreBiomeModifier::new));

	public static final ResourceLocation ADD_ORES_FEATURE = new ResourceLocation(FTBIC.MOD_ID, "ore_biome_modifier");
	private static final RegistryObject<Codec<? extends BiomeModifier>> SERIALIZER = RegistryObject.create(ADD_ORES_FEATURE, ForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, FTBIC.MOD_ID);

	@Override
	public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder)
	{
		if (phase == Phase.ADD && this.biomes.contains(biome))
		{
			BiomeGenerationSettingsBuilder generation = builder.getGenerationSettings();
			this.features.forEach(holder -> generation.addFeature(this.generationStage, holder));
		}
	}

	@Override
	public Codec<? extends BiomeModifier> codec() {
		return SERIALIZER.get();
	}

	private static DataResult<GenerationStep.Decoration> generationStageFromString(String name)
	{
		try
		{
			return DataResult.success(GenerationStep.Decoration.valueOf(name));
		}
		catch (Exception e)
		{
			return DataResult.error("Not a decoration stage: " + name);
		}
	}
}
