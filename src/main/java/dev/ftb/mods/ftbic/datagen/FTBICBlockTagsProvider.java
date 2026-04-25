package dev.ftb.mods.ftbic.datagen;

import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.block.ElectricBlockInstance;
import dev.ftb.mods.ftbic.block.FTBICBlocks;
import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.util.FTBICUtils;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
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

		tag(BlockTags.DRAGON_IMMUNE).addTag(FTBICUtils.REINFORCED);
		tag(BlockTags.WITHER_IMMUNE).addTag(FTBICUtils.REINFORCED);

		var pickaxe = tag(BlockTags.MINEABLE_WITH_PICKAXE);
		for (ElectricBlockInstance inst : FTBICElectricBlocks.ALL) {
			pickaxe.add(inst.block.get());
		}
		pickaxe.add(FTBICBlocks.MACHINE_BLOCK.get())
				.add(FTBICBlocks.ADVANCED_MACHINE_BLOCK.get())
				.add(FTBICBlocks.REINFORCED_STONE.get())
				.add(FTBICBlocks.REINFORCED_GLASS.get())
				.add(FTBICBlocks.IRON_FURNACE.get())
				.add(FTBICBlocks.NUCLEAR_REACTOR_CHAMBER.get())
				.add(FTBICBlocks.RUBBER_SHEET.get())
				.add(FTBICBlocks.LANDMARK.get())
				.add(FTBICBlocks.EXFLUID.get())
				.add(FTBICBlocks.NUKE.get())
				.add(FTBICBlocks.LV_CABLE.get())
				.add(FTBICBlocks.MV_CABLE.get())
				.add(FTBICBlocks.HV_CABLE.get())
				.add(FTBICBlocks.EV_CABLE.get())
				.add(FTBICBlocks.IV_CABLE.get())
				.add(FTBICBlocks.BURNT_CABLE.get())
				.add(FTBICBlocks.LV_REINFORCED_CABLE.get())
				.add(FTBICBlocks.MV_REINFORCED_CABLE.get())
				.add(FTBICBlocks.HV_REINFORCED_CABLE.get())
				.add(FTBICBlocks.EV_REINFORCED_CABLE.get())
				.add(FTBICBlocks.IV_REINFORCED_CABLE.get())
				.add(FTBICBlocks.BURNT_REINFORCED_CABLE.get());
	}
}
