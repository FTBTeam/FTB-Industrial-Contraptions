package dev.ftb.mods.ftbic.jei;

import dev.ftb.mods.ftbic.screen.FTBICMenus;
import dev.ftb.mods.ftbic.screen.PoweredCraftingTableMenu;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Optional;

public class PoweredCraftingTableTransferHandler implements IRecipeTransferHandler<PoweredCraftingTableMenu, CraftingRecipe> {
	private IRecipeTransferHandlerHelper transferHelper;

	public PoweredCraftingTableTransferHandler(IRecipeTransferHandlerHelper transferHelper) {
		this.transferHelper = transferHelper;
	}

	@Override
	public Class<PoweredCraftingTableMenu> getContainerClass() {
		return PoweredCraftingTableMenu.class;
	}

	public Optional<MenuType<PoweredCraftingTableMenu>> getMenuType() {
		return Optional.of(FTBICMenus.POWERED_CRAFTING_TABLE.get());
	}

	public mezz.jei.api.recipe.RecipeType<CraftingRecipe> getRecipeType() {
		return RecipeTypes.CRAFTING;
	}

	public Class<CraftingRecipe> getRecipeClass() {
		return CraftingRecipe.class;
	}

	@Nullable
	@Override
	public IRecipeTransferError transferRecipe(PoweredCraftingTableMenu container, CraftingRecipe recipe, IRecipeSlotsView recipeLayout, Player player, boolean maxTransfer, boolean doTransfer) {
		if (recipe.getType() == RecipeType.CRAFTING) {
			NonNullList<Ingredient> list = recipe.getIngredients();

			if (list.size() > 9) {
				return this.transferHelper.createInternalError();
			} else if (doTransfer) {
				Ingredient[] in = new Ingredient[9];
				Arrays.fill(in, Ingredient.EMPTY);

				if (recipe instanceof ShapedRecipe) {
					int w = ((ShapedRecipe) recipe).getWidth();
					int h = ((ShapedRecipe) recipe).getHeight();

					for (int y = 0; y < h; y++) {
						for (int x = 0; x < w; x++) {
							in[y * 3 + x] = list.get(y * w + x);
						}
					}
				} else {
					for (int i = 0; i < list.size(); i++) {
						in[i] = list.get(i);
					}
				}

				container.setIngredients(player, in);
			}

			return null;
		}

		return this.transferHelper.createInternalError();
	}
}
