package dev.ftb.mods.ftbic.block.entity.machine;

import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.screen.PoweredCraftingTableMenu;
import java.util.Arrays;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeMap;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class PoweredCraftingTableBlockEntity extends BasicMachineBlockEntity {
    private final ItemStack[] checkedGrid = new ItemStack[9];

    @Nullable
    private RecipeMap checkedRecipeMap;

    @Nullable
    private CraftingRecipe cachedRecipe;

    public PoweredCraftingTableBlockEntity(BlockPos pos, BlockState state) {
        super(FTBICElectricBlocks.POWERED_CRAFTING_TABLE, pos, state);
        Arrays.fill(checkedGrid, ItemStack.EMPTY);
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
        for (int i = 0; i < 9; i++) {
            grid.set(i, inputItems[i]);
        }
        CraftingInput.Positioned positioned = CraftingInput.ofPositioned(3, 3, grid);
        CraftingInput craft = positioned.input();
        if (craft.isEmpty()) return;

        CraftingRecipe recipe = findRecipe(server, craft);
        if (recipe == null) return;

        ItemStack result = recipe.assemble(craft);
        if (result.isEmpty()) return;

        ItemStack[] nextOutputs = new ItemStack[outputItems.length];
        for (int i = 0; i < outputItems.length; i++) {
            nextOutputs[i] = outputItems[i].copy();
        }
        if (!insertOutput(nextOutputs, result)) return;

        ItemStack[] nextInputs = new ItemStack[9];
        for (int i = 0; i < 9; i++) {
            nextInputs[i] = inputItems[i].getCount() > 1
                    ? inputItems[i].copyWithCount(inputItems[i].getCount() - 1)
                    : ItemStack.EMPTY;
        }

        NonNullList<ItemStack> remainders = recipe.getRemainingItems(craft);
        for (int y = 0; y < craft.height(); y++) {
            for (int x = 0; x < craft.width(); x++) {
                ItemStack remainder = remainders.get(x + y * craft.width());
                if (remainder.isEmpty()) continue;
                int slot = x + positioned.left() + (y + positioned.top()) * 3;
                ItemStack current = nextInputs[slot];
                if (current.isEmpty()) {
                    nextInputs[slot] = remainder.copy();
                } else if (ItemStack.isSameItemSameComponents(current, remainder)
                        && current.getCount() + remainder.getCount() <= current.getMaxStackSize()) {
                    current.grow(remainder.getCount());
                } else if (!insertOutput(nextOutputs, remainder)) {
                    return;
                }
            }
        }

        energy -= energyUse;
        active = true;
        System.arraycopy(nextInputs, 0, inputItems, 0, 9);
        System.arraycopy(nextOutputs, 0, outputItems, 0, outputItems.length);
        setChanged();
    }

    @Nullable
    private CraftingRecipe findRecipe(ServerLevel server, CraftingInput craft) {
        RecipeMap recipes = server.recipeAccess().recipeMap();
        if (recipes != checkedRecipeMap || !gridMatchesCheck()) {
            checkedRecipeMap = recipes;
            for (int i = 0; i < 9; i++) {
                checkedGrid[i] = inputItems[i].copy();
            }
            cachedRecipe = recipes.getRecipesFor(RecipeType.CRAFTING, craft, server)
                    .findFirst()
                    .map(RecipeHolder::value)
                    .orElse(null);
        }
        return cachedRecipe != null && cachedRecipe.matches(craft, server) ? cachedRecipe : null;
    }

    private boolean gridMatchesCheck() {
        for (int i = 0; i < 9; i++) {
            if (!ItemStack.isSameItemSameComponents(checkedGrid[i], inputItems[i])) return false;
        }
        return true;
    }

    private static boolean insertOutput(ItemStack[] outputs, ItemStack stack) {
        int remaining = stack.getCount();
        for (int i = 0; i < outputs.length && remaining > 0; i++) {
            if (!outputs[i].isEmpty() && ItemStack.isSameItemSameComponents(outputs[i], stack)) {
                int move = Math.min(remaining, outputs[i].getMaxStackSize() - outputs[i].getCount());
                if (move > 0) {
                    outputs[i].grow(move);
                    remaining -= move;
                }
            }
        }
        for (int i = 0; i < outputs.length && remaining > 0; i++) {
            if (outputs[i].isEmpty()) {
                int move = Math.min(remaining, stack.getMaxStackSize());
                outputs[i] = stack.copyWithCount(move);
                remaining -= move;
            }
        }
        return remaining <= 0;
    }
}
