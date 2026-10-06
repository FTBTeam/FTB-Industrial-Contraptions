package dev.ftb.mods.ftbic.block.entity.machine;

import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.block.ElectricBlockInstance;
import dev.ftb.mods.ftbic.item.FTBICItems;
import dev.ftb.mods.ftbic.recipe.FTBICRecipes;
import dev.ftb.mods.ftbic.recipe.MachineRecipe;
import dev.ftb.mods.ftbic.recipe.MachineRecipeType;
import dev.ftb.mods.ftbic.util.IngredientWithCount;
import dev.ftb.mods.ftbic.util.StackWithChance;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeMap;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

public class MachineBlockEntity extends BasicMachineBlockEntity {
    public final MachineRecipeType recipeType;
    public int progress;
    public int maxProgress;
    public boolean starving;
    private static final int STARVING_SYNC_INTERVAL = 20;
    private boolean syncedStarving;
    private long lastStarvingSync = Long.MIN_VALUE;

    @Nullable
    private MachineRecipe cachedRecipe;

    private boolean recipeDirty = true;
    private int dirtyTimer = 0;
    private int parallelOperations;
    private int lastRunningOperations;
    private String runningRecipeId = "";

    @Nullable
    private RecipeMap cachedRecipeMap;

    public boolean supportsParallelProcessing() {
        return electricBlockInstance.advanced;
    }

    public int getParallelCapacity() {
        return supportsParallelProcessing()
                ? 1 + Math.min(3, upgradeInventory.countUpgrades(FTBICItems.PARALLEL_PROCESSING_UPGRADE.get()))
                : 1;
    }

    public int getRunningOperations() {
        return active ? lastRunningOperations : 0;
    }

    private void resetCycle() {
        if (progress != 0 || parallelOperations != 0) setChanged();
        progress = 0;
        parallelOperations = 0;
        lastRunningOperations = 0;
        active = false;
    }

    private void selectRecipe(MachineRecipe recipe, String id) {
        if (!runningRecipeId.equals(id)) resetCycle();
        runningRecipeId = id;
        cachedRecipe = recipe;
        updateMaxProgress();
    }

    public MachineBlockEntity(
            ElectricBlockInstance type, MachineRecipeType recipeType, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.recipeType = recipeType;
    }

    @Override
    public void setStackInSlot(int slot, ItemStack stack) {
        if (slot < inputItems.length) {
            recipeDirty = true;
        }
        super.setStackInSlot(slot, stack);
    }

    public void markRecipeDirty() {
        recipeDirty = true;
    }

    protected boolean matchesFluidInputs(MachineRecipe recipe, int operations) {
        return recipe.inputFluids.isEmpty() && recipe.outputFluids.isEmpty();
    }

    protected boolean canFitFluidOutputs(MachineRecipe recipe, int operations) {
        return recipe.outputFluids.isEmpty();
    }

    protected void processFluids(MachineRecipe recipe) {}

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        if (progress > 0) output.putInt("Progress", progress);
        if (maxProgress > 0) output.putInt("MaxProgress", maxProgress);
        if (starving) output.putBoolean("Starving", true);
        output.putInt("ParallelOperations", parallelOperations);
        output.putString("RunningRecipe", runningRecipeId);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        progress = input.getIntOr("Progress", 0);
        maxProgress = input.getIntOr("MaxProgress", 0);
        starving = input.getBooleanOr("Starving", false);
        parallelOperations = Math.clamp(input.getIntOr("ParallelOperations", 0), 0, 4);
        runningRecipeId = input.getStringOr("RunningRecipe", "");
        cachedRecipe = null;
        cachedRecipeMap = null;
        recipeDirty = true;
    }

    private void setStarving(boolean s) {
        if (starving != s) {
            starving = s;
            setChanged();
        }
    }

    private void syncStarving() {
        if (starving == syncedStarving) return;
        long time = level.getGameTime();
        if (lastStarvingSync != Long.MIN_VALUE && time - lastStarvingSync < STARVING_SYNC_INTERVAL) return;
        syncedStarving = starving;
        lastStarvingSync = time;
        BlockState state = getBlockState();
        level.sendBlockUpdated(worldPosition, state, state, 3);
    }

    @Nullable
    private MachineRecipe findRecipe() {
        if (level == null || !(level instanceof ServerLevel server)) {
            return null;
        }
        RecipeMap currentMap = server.recipeAccess().recipeMap();
        if (cachedRecipeMap != currentMap) {
            if (cachedRecipeMap != null) resetCycle();
            cachedRecipeMap = currentMap;
            cachedRecipe = null;
            recipeDirty = true;
        }
        if (!recipeDirty && cachedRecipe != null && recipeMatchesInputs(cachedRecipe)) {
            return cachedRecipe;
        }
        // Only scan the full recipe map when inputs actually changed, otherwise an idle machine
        // with non-matching inputs would iterate every recipe every tick.
        if (!recipeDirty) {
            return null;
        }
        recipeDirty = false;
        List<RecipeHolder<?>> candidates = new ArrayList<>();
        for (RecipeHolder<?> holder : server.recipeAccess().recipeMap().byType(recipeType.TYPE.get())) {
            if (holder.value() instanceof MachineRecipe) {
                candidates.add(holder);
            }
        }
        candidates.sort((a, b) -> {
            MachineRecipe first = (MachineRecipe) a.value();
            MachineRecipe second = (MachineRecipe) b.value();
            return Integer.compare(
                    second.inputs.size() + second.inputFluids.size(), first.inputs.size() + first.inputFluids.size());
        });
        for (RecipeHolder<?> holder : candidates) {
            MachineRecipe mr = (MachineRecipe) holder.value();
            if (recipeMatchesInputs(mr)) {
                selectRecipe(mr, holder.id().identifier().toString());
                return mr;
            }
        }
        if (recipeType == FTBICRecipes.SMELTING && inputItems.length > 0 && !inputItems[0].isEmpty()) {
            for (RecipeHolder<SmeltingRecipe> holder :
                    server.recipeAccess().recipeMap().byType(RecipeType.SMELTING)) {
                if (!(holder.value() instanceof AbstractCookingRecipe sr)) {
                    continue;
                }
                if (sr.input().test(inputItems[0])) {
                    selectRecipe(
                            adaptCooking(sr, inputItems[0]),
                            holder.id().identifier().toString());
                    return cachedRecipe;
                }
            }
        }
        cachedRecipe = null;
        maxProgress = 0;
        return null;
    }

    private void updateMaxProgress() {
        if (cachedRecipe == null) {
            maxProgress = 0;
            return;
        }
        double speed = Math.max(1D, progressSpeed);
        maxProgress = Math.max(
                1, (int) (cachedRecipe.processingTime * FTBICConfig.MACHINES.MACHINE_RECIPE_BASE_TICKS.get() / speed));
    }

    @Override
    public void upgradesChanged() {
        super.upgradesChanged();
        updateMaxProgress();
        if (parallelOperations > getParallelCapacity()) resetCycle();
    }

    private MachineRecipe adaptCooking(AbstractCookingRecipe sr, ItemStack inputStack) {
        ItemStack result = sr.assemble(new SingleRecipeInput(inputStack));
        ItemStackTemplate template =
                new ItemStackTemplate(result.typeHolder(), result.getCount(), result.getComponentsPatch());
        double baseTicks = FTBICConfig.MACHINES.MACHINE_RECIPE_BASE_TICKS.get();
        return new MachineRecipe(
                recipeType,
                List.of(new IngredientWithCount(sr.input(), 1)),
                List.of(),
                List.of(new StackWithChance(template, 1D)),
                List.of(),
                sr.cookingTime() / baseTicks,
                false);
    }

    private boolean recipeMatchesInputs(MachineRecipe recipe) {
        return recipeMatchesInputs(recipe, 1);
    }

    private boolean recipeMatchesInputs(MachineRecipe recipe, int operations) {
        return !(recipe.inputs.isEmpty() && recipe.inputFluids.isEmpty())
                && matchesFluidInputs(recipe, operations)
                && findInputSlots(recipe, operations) != null;
    }

    @Nullable
    private int[] findInputSlots(MachineRecipe recipe, int operations) {
        int[] slots = new int[recipe.inputs.size()];
        return assignInputSlots(recipe, operations, slots, new boolean[inputItems.length], 0) ? slots : null;
    }

    private boolean assignInputSlots(
            MachineRecipe recipe, int operations, int[] slots, boolean[] used, int ingredient) {
        if (ingredient == slots.length) return true;
        IngredientWithCount need = recipe.inputs.get(ingredient);
        for (int i = 0; i < inputItems.length; i++) {
            if (!used[i]
                    && need.matches(inputItems[i])
                    && inputItems[i].getCount() >= (long) need.count() * operations) {
                used[i] = true;
                slots[ingredient] = i;
                if (assignInputSlots(recipe, operations, slots, used, ingredient + 1)) return true;
                used[i] = false;
            }
        }
        return false;
    }

    private boolean canFitOutputs(MachineRecipe mr, int operations) {
        if (mr.outputs.isEmpty()) {
            return true;
        }
        ItemStack[] virtual = new ItemStack[outputItems.length];
        for (int i = 0; i < outputItems.length; i++) {
            virtual[i] = outputItems[i].copy();
        }
        for (StackWithChance swc : mr.outputs) {
            ItemStack add = swc.stack();
            long remaining = (long) add.getCount() * operations;
            for (int i = 0; i < virtual.length && remaining > 0; i++) {
                if (virtual[i].isEmpty()) {
                    ItemStack put = add.copy();
                    int take = (int) Math.min(remaining, put.getMaxStackSize());
                    put.setCount(take);
                    virtual[i] = put;
                    remaining -= take;
                } else if (ItemStack.isSameItemSameComponents(virtual[i], add)) {
                    int room = virtual[i].getMaxStackSize() - virtual[i].getCount();
                    int move = (int) Math.min(room, remaining);
                    virtual[i].grow(move);
                    remaining -= move;
                }
            }
            if (remaining > 0) return false;
        }
        return true;
    }

    @Override
    public void tick() {
        super.tick();
        if (level == null || level.isClientSide()) {
            return;
        }
        syncStarving();
        processRecipes();
    }

    protected void processRecipes() {
        MachineRecipe recipe = findRecipe();

        if (recipe == null) {
            resetCycle();
            active = false;
            setStarving(false);
            return;
        }

        // Batch width stays fixed until completion. New ingredients cannot inherit paid progress.
        if (parallelOperations > getParallelCapacity()
                || parallelOperations > 0 && !recipeMatchesInputs(recipe, parallelOperations)) {
            resetCycle();
            setChanged();
        }
        if (parallelOperations == 0) {
            for (int count = getParallelCapacity(); count > 0; count--) {
                if (energy >= energyUse * count
                        && recipeMatchesInputs(recipe, count)
                        && canFitOutputs(recipe, count)
                        && canFitFluidOutputs(recipe, count)) {
                    parallelOperations = count;
                    setChanged();
                    break;
                }
            }
        }
        if (parallelOperations == 0 || energy < energyUse * parallelOperations) {
            boolean insufficientPower = energy < energyUse * Math.max(1, parallelOperations);
            active = false;
            setStarving(insufficientPower);
            return;
        }
        if (!canFitOutputs(recipe, parallelOperations) || !canFitFluidOutputs(recipe, parallelOperations)) {
            active = false;
            setStarving(false);
            return;
        }

        lastRunningOperations = parallelOperations;
        energy -= energyUse * parallelOperations;
        progress++;
        active = true;
        setStarving(false);

        if (progress >= maxProgress) {
            consumeInputs(recipe, parallelOperations);
            for (int i = 0; i < parallelOperations; i++) {
                produceOutputs(recipe);
                processFluids(recipe);
            }
            progress = 0;
            // Keep the completed width for the UI until the next tick chooses a new batch.
            parallelOperations = 0;
            cachedRecipe = null;
            recipeDirty = true;
            setChanged();
            dirtyTimer = 0;
        } else if (++dirtyTimer >= 20) {
            setChanged();
            dirtyTimer = 0;
        }
    }

    private void consumeInputs(MachineRecipe recipe, int operations) {
        int[] slots = findInputSlots(recipe, operations);
        if (slots == null) throw new IllegalStateException("Validated machine inputs changed during processing");
        for (int i = 0; i < slots.length; i++) {
            inputItems[slots[i]].shrink(recipe.inputs.get(i).count() * operations);
            if (inputItems[slots[i]].isEmpty()) inputItems[slots[i]] = ItemStack.EMPTY;
        }
    }

    private void produceOutputs(MachineRecipe mr) {
        List<StackWithChance> outs = mr.outputs;
        if (outs.isEmpty() || outputItems.length == 0) return;

        for (StackWithChance swc : outs) {
            if (swc.chance() < 1D && level.getRandom().nextDouble() >= swc.chance()) {
                continue;
            }
            ItemStack toAdd = swc.stack().copy();
            for (int i = 0; i < outputItems.length && !toAdd.isEmpty(); i++) {
                ItemStack existing = outputItems[i];
                if (existing.isEmpty()) {
                    int move = Math.min(toAdd.getCount(), toAdd.getMaxStackSize());
                    outputItems[i] = toAdd.copyWithCount(move);
                    toAdd.shrink(move);
                } else if (ItemStack.isSameItemSameComponents(existing, toAdd)) {
                    int room = existing.getMaxStackSize() - existing.getCount();
                    int move = Math.min(room, toAdd.getCount());
                    existing.grow(move);
                    toAdd.shrink(move);
                }
            }
        }
    }
}
