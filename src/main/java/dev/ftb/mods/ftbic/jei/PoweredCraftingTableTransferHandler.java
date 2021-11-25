package dev.ftb.mods.ftbic.jei;

import dev.ftb.mods.ftbic.screen.PoweredCraftingTableMenu;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.transfer.RecipeTransferErrorInternal;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class PoweredCraftingTableTransferHandler implements IRecipeTransferHandler<PoweredCraftingTableMenu> {
	@Override
	public Class<PoweredCraftingTableMenu> getContainerClass() {
		return PoweredCraftingTableMenu.class;
	}

	@Nullable
	@Override
	public IRecipeTransferError transferRecipe(PoweredCraftingTableMenu container, Object recipe, IRecipeLayout recipeLayout, Player player, boolean maxTransfer, boolean doTransfer) {
		if (recipe instanceof Recipe && ((Recipe<?>) recipe).getType() == RecipeType.CRAFTING) {
			NonNullList<Ingredient> list = ((Recipe<?>) recipe).getIngredients();

			if (list.size() > 9) {
				return RecipeTransferErrorInternal.INSTANCE;
			} else if (doTransfer) {
				Ingredient[] in = new Ingredient[9];
				Arrays.fill(in, Ingredient.EMPTY);

				for (int i = 0; i < list.size(); i++) {
					in[i] = list.get(i);
				}

				container.setIngredients(player, in);
			}

			return null;
		}

		return RecipeTransferErrorInternal.INSTANCE;
	}
}
