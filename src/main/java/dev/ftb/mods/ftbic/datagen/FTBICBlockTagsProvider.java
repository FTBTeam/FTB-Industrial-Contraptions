package dev.ftb.mods.ftbic.datagen;

import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.block.FTBICBlocks;
import dev.ftb.mods.ftbic.world.ResourceElements;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.BlockTagsProvider;

import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public class FTBICBlockTagsProvider extends BlockTagsProvider {
	public FTBICBlockTagsProvider(PackOutput out, CompletableFuture<HolderLookup.Provider> lookup) {
		super(out, lookup, FTBIC.MOD_ID);
	}

	@Override
	protected void addTags(HolderLookup.Provider lookup) {
		var pickaxe = tag(BlockTags.MINEABLE_WITH_PICKAXE);
		var needsIron = tag(BlockTags.NEEDS_IRON_TOOL);
		var oresUmbrella = tag(commonBlockTag("ores"));
		var storageUmbrella = tag(commonBlockTag("storage_blocks"));

		for (ResourceElements el : ResourceElements.VALUES) {
			var ore = FTBICBlocks.RESOURCE_ORES.get(el);
			if (ore != null) {
				pickaxe.add(ore.get());
				needsIron.add(ore.get());
				ResourceElements baseEl = ResourceElements.getNonDeepslateVersion(el).orElse(el);
				TagKey<Block> oreElTag = commonBlockTag("ores/" + baseEl.getName().toLowerCase(Locale.ROOT));
				tag(oreElTag).add(ore.get());
				oresUmbrella.addTag(oreElTag);
			}
			var block = FTBICBlocks.RESOURCE_BLOCKS_OF.get(el);
			if (block != null) {
				pickaxe.add(block.get());
				needsIron.add(block.get());
				TagKey<Block> blockElTag = commonBlockTag("storage_blocks/" + el.getName().toLowerCase(Locale.ROOT));
				tag(blockElTag).add(block.get());
				storageUmbrella.addTag(blockElTag);
			}
		}
	}

	private static TagKey<Block> commonBlockTag(String path) {
		return TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath("c", path));
	}
}
