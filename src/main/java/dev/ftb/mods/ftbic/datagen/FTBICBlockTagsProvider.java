package dev.ftb.mods.ftbic.datagen;

import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.block.FTBICBlocks;
import dev.ftb.mods.ftbic.util.FTBICUtils;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.data.BlockTagsProvider;

import java.util.concurrent.CompletableFuture;

public class FTBICBlockTagsProvider extends BlockTagsProvider {
	public FTBICBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
		super(output, lookup, FTBIC.MOD_ID);
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {
		tag(FTBICUtils.REINFORCED)
				.add(FTBICBlocks.REINFORCED_STONE.get())
				.add(FTBICBlocks.REINFORCED_GLASS.get())
				.add(FTBICBlocks.LV_REINFORCED_CABLE.get())
				.add(FTBICBlocks.MV_REINFORCED_CABLE.get())
				.add(FTBICBlocks.HV_REINFORCED_CABLE.get())
				.add(FTBICBlocks.EV_REINFORCED_CABLE.get())
				.add(FTBICBlocks.IV_REINFORCED_CABLE.get())
				.add(FTBICBlocks.BURNT_REINFORCED_CABLE.get())
				.add(Blocks.BEDROCK)
				.add(Blocks.BARRIER)
				.add(Blocks.COMMAND_BLOCK);
	}
}
