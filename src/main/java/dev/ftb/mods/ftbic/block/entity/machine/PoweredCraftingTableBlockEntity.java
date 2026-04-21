package dev.ftb.mods.ftbic.block.entity.machine;

import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.screen.PoweredCraftingTableMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

public class PoweredCraftingTableBlockEntity extends BasicMachineBlockEntity {
	public PoweredCraftingTableBlockEntity(BlockPos pos, BlockState state) {
		super(FTBICElectricBlocks.POWERED_CRAFTING_TABLE, pos, state);
	}

	@Override
	public AbstractContainerMenu createMenu(int id, Inventory inv) {
		return new PoweredCraftingTableMenu(id, inv, this);
	}

	@Override
	public void tick() {
		super.tick();
		if (level == null || level.isClientSide() || !(level instanceof ServerLevel server)) return;
		if (inputItems.length < 9 || outputItems.length < 1) return;
		if (energy < energyUse) return;

		NonNullList<ItemStack> grid = NonNullList.withSize(9, ItemStack.EMPTY);
		boolean anyInput = false;
		for (int i = 0; i < 9; i++) {
			grid.set(i, inputItems[i]);
			if (!inputItems[i].isEmpty()) anyInput = true;
		}
		if (!anyInput) return;

		CraftingInput craft = CraftingInput.of(3, 3, grid);
		@SuppressWarnings("unchecked")
		RecipeType<CraftingRecipe> craftingType = (RecipeType<CraftingRecipe>) (RecipeType<?>) RecipeType.CRAFTING;
		Optional<RecipeHolder<CraftingRecipe>> match = server.recipeAccess().recipeMap()
				.getRecipesFor(craftingType, craft, level).findFirst();
		if (match.isEmpty()) return;

		CraftingRecipe recipe = match.get().value();
		ItemStack result = recipe.assemble(craft);
		if (result.isEmpty()) return;

		if (outputItems[0].isEmpty()) {
			outputItems[0] = result;
		} else if (ItemStack.isSameItemSameComponents(outputItems[0], result)
				&& outputItems[0].getCount() + result.getCount() <= outputItems[0].getMaxStackSize()) {
			outputItems[0].grow(result.getCount());
		} else {
			return;
		}

		energy -= energyUse;
		active = true;

		for (int i = 0; i < 9; i++) {
			if (!inputItems[i].isEmpty()) {
				inputItems[i].shrink(1);
				if (inputItems[i].getCount() <= 0) inputItems[i] = ItemStack.EMPTY;
			}
		}
		setChanged();
	}
}
