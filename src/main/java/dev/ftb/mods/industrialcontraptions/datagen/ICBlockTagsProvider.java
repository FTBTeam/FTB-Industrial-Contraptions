package dev.ftb.mods.industrialcontraptions.datagen;

import dev.ftb.mods.industrialcontraptions.IC;
import dev.ftb.mods.industrialcontraptions.block.ElectricBlockInstance;
import dev.ftb.mods.industrialcontraptions.block.ICBlocks;
import dev.ftb.mods.industrialcontraptions.block.ICElectricBlocks;
import dev.ftb.mods.industrialcontraptions.material.Material;
import dev.ftb.mods.industrialcontraptions.material.MaterialComponent;
import dev.ftb.mods.industrialcontraptions.material.MaterialEntries;
import dev.ftb.mods.industrialcontraptions.util.ICUtils;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.data.BlockTagsProvider;

import java.util.concurrent.CompletableFuture;

public class ICBlockTagsProvider extends BlockTagsProvider {
	public ICBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
		super(output, lookup, IC.MOD_ID);
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {
		tag(ICUtils.REINFORCED)
				.add(ICBlocks.REINFORCED_STONE.get())
				.add(ICBlocks.REINFORCED_GLASS.get())
				.add(ICBlocks.LV_REINFORCED_CABLE.get())
				.add(ICBlocks.MV_REINFORCED_CABLE.get())
				.add(ICBlocks.HV_REINFORCED_CABLE.get())
				.add(ICBlocks.EV_REINFORCED_CABLE.get())
				.add(ICBlocks.IV_REINFORCED_CABLE.get())
				.add(ICBlocks.BURNT_REINFORCED_CABLE.get())
				.add(Blocks.BEDROCK)
				.add(Blocks.BARRIER)
				.add(Blocks.COMMAND_BLOCK);

		tag(BlockTags.DRAGON_IMMUNE).addTag(ICUtils.REINFORCED);
		tag(BlockTags.WITHER_IMMUNE).addTag(ICUtils.REINFORCED);

		var pickaxe = tag(BlockTags.MINEABLE_WITH_PICKAXE);
		var incorrectForWooden = tag(BlockTags.INCORRECT_FOR_WOODEN_TOOL);
		var incorrectForStone = tag(BlockTags.INCORRECT_FOR_STONE_TOOL);
		var incorrectForIron = tag(BlockTags.INCORRECT_FOR_IRON_TOOL);

		for (var entry : MaterialEntries.all()) {
			if (!entry.component().isBlock()) continue;
			Block block = entry.block().get();
			pickaxe.add(block);
			incorrectForWooden.add(block);
			switch (entry.material().tool()) {
				case IRON -> incorrectForStone.add(block);
				case DIAMOND -> incorrectForIron.add(block);
				default -> {}
			}
			Material mat = entry.material();
			MaterialComponent comp = entry.component();
			for (String container : comp.containerTags()) {
				tag(commonBlockTag(container)).add(block);
			}
			tag(commonBlockTag(comp.perMaterialTag(mat.tagName()))).add(block);
		}

		for (ElectricBlockInstance inst : ICElectricBlocks.ALL) {
			pickaxe.add(inst.block.get());
		}
		pickaxe.add(ICBlocks.MACHINE_BLOCK.get())
				.add(ICBlocks.ADVANCED_MACHINE_BLOCK.get())
				.add(ICBlocks.REINFORCED_STONE.get())
				.add(ICBlocks.REINFORCED_GLASS.get())
				.add(ICBlocks.IRON_FURNACE.get())
				.add(ICBlocks.NUCLEAR_REACTOR_CHAMBER.get())
				.add(ICBlocks.RUBBER_SHEET.get())
				.add(ICBlocks.LANDMARK.get())
				.add(ICBlocks.EXFLUID.get())
				.add(ICBlocks.NUKE.get())
				.add(ICBlocks.LV_CABLE.get())
				.add(ICBlocks.MV_CABLE.get())
				.add(ICBlocks.HV_CABLE.get())
				.add(ICBlocks.EV_CABLE.get())
				.add(ICBlocks.IV_CABLE.get())
				.add(ICBlocks.BURNT_CABLE.get())
				.add(ICBlocks.LV_REINFORCED_CABLE.get())
				.add(ICBlocks.MV_REINFORCED_CABLE.get())
				.add(ICBlocks.HV_REINFORCED_CABLE.get())
				.add(ICBlocks.EV_REINFORCED_CABLE.get())
				.add(ICBlocks.IV_REINFORCED_CABLE.get())
				.add(ICBlocks.BURNT_REINFORCED_CABLE.get());
	}

	private static TagKey<Block> commonBlockTag(String fullPath) {
		Identifier id;
		if (fullPath.contains(":")) {
			String[] parts = fullPath.split(":", 2);
			id = Identifier.fromNamespaceAndPath(parts[0], parts[1]);
		} else {
			id = Identifier.fromNamespaceAndPath("c", fullPath);
		}
		return TagKey.create(Registries.BLOCK, id);
	}
}
