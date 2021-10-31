package dev.ftb.mods.ftbic.block.entity.machine;

import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.recipe.RecipeCache;
import dev.ftb.mods.ftbic.util.CachedItemProcessingResult;
import net.minecraft.world.item.Item;

public class MaceratorBlockEntity extends SimpleRecipeMachineBlockEntity {
	public MaceratorBlockEntity() {
		super(FTBICElectricBlocks.MACERATOR.blockEntity.get());
		energyCapacity = 12000;
		energyUse = 20;
	}

	@Override
	public CachedItemProcessingResult getResult(RecipeCache cache, Item item) {
		return cache.getMaceratingResult(level, item);
	}
}