package dev.ftb.mods.ftbic.block.entity.machine;

import dev.ftb.mods.ftbic.recipe.RecipeCache;
import dev.ftb.mods.ftbic.util.CachedItemProcessingResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class SimpleRecipeMachineBlockEntity extends MachineBlockEntity {
	public int energyUse;

	public SimpleRecipeMachineBlockEntity(BlockEntityType<?> type) {
		super(type);
		energyCapacity = 8000;
		energyUse = 20;
	}

	public CachedItemProcessingResult getResult(RecipeCache cache, Item item) {
		return CachedItemProcessingResult.NONE;
	}
}