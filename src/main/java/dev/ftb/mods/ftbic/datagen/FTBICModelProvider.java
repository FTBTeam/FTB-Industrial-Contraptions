package dev.ftb.mods.ftbic.datagen;

import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.item.FTBICItems;
import dev.ftb.mods.ftbic.item.MaterialItem;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.core.Holder;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public class FTBICModelProvider extends ModelProvider {
	public FTBICModelProvider(PackOutput output) {
		super(output, FTBIC.MOD_ID);
	}

	@Override
	protected Stream<? extends Holder<Block>> getKnownBlocks() {
		return Stream.empty();
	}

	@Override
	protected Stream<? extends Holder<Item>> getKnownItems() {
		Set<Item> seen = new HashSet<>();
		List<Holder<Item>> items = new ArrayList<>();
		for (MaterialItem m : FTBICItems.MATERIALS) {
			if (m.item == null) continue;
			Item i = m.item.get();
			if (i != null && seen.add(i)) items.add(net.minecraft.core.registries.BuiltInRegistries.ITEM.wrapAsHolder(i));
		}
		return items.stream();
	}

	@Override
	protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
		for (MaterialItem m : FTBICItems.MATERIALS) {
			if (m.item == null) continue;
			Item i = m.item.get();
			if (i != null) itemModels.generateFlatItem(i, ModelTemplates.FLAT_ITEM);
		}
	}
}
