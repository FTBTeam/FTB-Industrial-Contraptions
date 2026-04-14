package dev.ftb.mods.ftbic.integration.jei;

import dev.ftb.mods.ftbic.net.SelectCraftingRecipePayload;
import dev.ftb.mods.ftbic.screen.FTBICMenus;
import dev.ftb.mods.ftbic.screen.PoweredCraftingTableMenu;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PoweredCraftingTableTransferHandler implements IRecipeTransferHandler<PoweredCraftingTableMenu, RecipeHolder<CraftingRecipe>> {
	private final IRecipeTransferHandlerHelper helper;

	public PoweredCraftingTableTransferHandler(IRecipeTransferHandlerHelper helper) {
		this.helper = helper;
	}

	@Override
	public Class<? extends PoweredCraftingTableMenu> getContainerClass() {
		return PoweredCraftingTableMenu.class;
	}

	@Override
	public Optional<MenuType<PoweredCraftingTableMenu>> getMenuType() {
		return Optional.of(FTBICMenus.POWERED_CRAFTING_TABLE.get());
	}

	@Override
	public IRecipeType<RecipeHolder<CraftingRecipe>> getRecipeType() {
		return RecipeTypes.CRAFTING;
	}

	@Nullable
	@Override
	public IRecipeTransferError transferRecipe(PoweredCraftingTableMenu container, RecipeHolder<CraftingRecipe> holder,
			IRecipeSlotsView slots, Player player, boolean maxTransfer, boolean doTransfer) {
		CraftingRecipe recipe = holder.value();

		List<Optional<Ingredient>> grid = new ArrayList<>(9);
		for (int i = 0; i < 9; i++) grid.add(Optional.empty());

		if (recipe instanceof ShapedRecipe shaped) {
			List<Optional<Ingredient>> ings = shaped.getIngredients();
			int w = shaped.getWidth();
			int h = shaped.getHeight();
			if (w > 3 || h > 3) return helper.createInternalError();
			for (int y = 0; y < h; y++) {
				for (int x = 0; x < w; x++) {
					grid.set(y * 3 + x, ings.get(y * w + x));
				}
			}
		} else if (recipe instanceof ShapelessRecipe) {
			List<Ingredient> ings = recipe.placementInfo().ingredients();
			if (ings.size() > 9) return helper.createInternalError();
			for (int i = 0; i < ings.size(); i++) grid.set(i, Optional.of(ings.get(i)));
		} else {
			List<Ingredient> ings = recipe.placementInfo().ingredients();
			if (ings.size() > 9) return helper.createInternalError();
			for (int i = 0; i < ings.size(); i++) grid.set(i, Optional.of(ings.get(i)));
		}

		if (!doTransfer) return null;

		ClientPacketDistributor.sendToServer(new SelectCraftingRecipePayload(grid));
		return null;
	}
}
