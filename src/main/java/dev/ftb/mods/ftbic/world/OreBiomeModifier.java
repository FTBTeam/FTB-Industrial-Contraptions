package dev.ftb.mods.ftbic.world;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.ftb.mods.ftbic.FTBIC;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.world.BiomeGenerationSettingsBuilder;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ModifiableBiomeInfo;

// Shameless copy and paste from forge until this is less awful
public record OreBiomeModifier(
		GenerationStep.Decoration generationStage,
		HolderSet<PlacedFeature> features
) implements BiomeModifier {

	public static Codec<OreBiomeModifier> CODEC = RecordCodecBuilder.create(builder -> builder.group(
			Codec.STRING.comapFlatMap(OreBiomeModifier::generationStageFromString, GenerationStep.Decoration::toString).fieldOf("generation_stage").forGetter(OreBiomeModifier::generationStage),
			PlacedFeature.LIST_CODEC.fieldOf("features").forGetter(OreBiomeModifier::features)
	).apply(builder, OreBiomeModifier::new));

	@Override
	public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder)
	{
		if (phase == Phase.ADD && (biome.is(BiomeTags.IS_END) && biome.is(BiomeTags.IS_NETHER)))
		{
			BiomeGenerationSettingsBuilder generation = builder.getGenerationSettings();
			this.features.forEach(holder -> generation.addFeature(this.generationStage, holder));
		}
	}

	@Override
	public Codec<? extends BiomeModifier> codec() {
		return FTBIC.ORE_BIOME_MODIFIER.get();
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
