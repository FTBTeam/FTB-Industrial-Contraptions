package dev.ftb.mods.ftbic.block.entity.machine;

import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.block.ElectricBlockInstance;
import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.recipe.FTBICRecipes;
import dev.ftb.mods.ftbic.recipe.MachineRecipe;
import dev.ftb.mods.ftbic.recipe.SoilOption;
import dev.ftb.mods.ftbic.screen.HydroponicMenu;
import dev.ftb.mods.ftbic.util.HydroponicWaterHandler;
import dev.ftb.mods.ftbic.util.SideConfiguration.Face;
import dev.ftb.mods.ftbic.util.SideConfiguration.Resource;
import dev.ftb.mods.ftbic.util.StackWithChance;
import java.util.Arrays;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeMap;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.fluids.FluidStack;

public final class HydroponicBlockEntity extends MachineBlockEntity {
    private final boolean advanced;
    private final int[] laneProgress = new int[4];
    private final int[] laneDuration = new int[4];
    private final int[] laneOperations = new int[4];
    private final String[] recipeIds = new String[4];
    private final Match[] cachedMatches = new Match[4];
    private boolean mutationMode;
    private FluidStack inputFluid = FluidStack.EMPTY;
    private int syncTimer;
    private FluidStack syncedFluid = FluidStack.EMPTY;
    private RecipeMap cachedRecipes;
    private int runningOperations;
    public final HydroponicWaterHandler fluidHandler = new HydroponicWaterHandler(this);

    public HydroponicBlockEntity(BlockPos pos, BlockState state) {
        this(FTBICElectricBlocks.HYDROPONIC_ACCELERATOR, false, pos, state);
    }

    public static HydroponicBlockEntity advanced(BlockPos pos, BlockState state) {
        return new HydroponicBlockEntity(FTBICElectricBlocks.ADVANCED_HYDROPONIC_ACCELERATOR, true, pos, state);
    }

    private HydroponicBlockEntity(ElectricBlockInstance instance, boolean advanced, BlockPos pos, BlockState state) {
        super(instance, FTBICRecipes.HYDROPONIC_GROWTH, pos, state);
        this.advanced = advanced;
        Arrays.fill(recipeIds, "");
    }

    public boolean isAdvanced() {
        return advanced;
    }

    public boolean isMutationMode() {
        return mutationMode;
    }

    public int getLaneCount() {
        return advanced ? 4 : 1;
    }

    public int getProgress(int lane) {
        return laneProgress[lane];
    }

    public int getDuration(int lane) {
        return laneDuration[lane];
    }

    public int getOperations(int lane) {
        return laneOperations[lane];
    }

    @Override
    public int getRunningOperations() {
        return active ? runningOperations : 0;
    }

    @Override
    public void upgradesChanged() {
        super.upgradesChanged();
        int capacity = getParallelCapacity();
        for (int lane = 0; lane < 4; lane++) if (laneOperations[lane] > capacity) restart(lane);
    }

    public int getTankCapacity() {
        return advanced ? 32_000 : 16_000;
    }

    public FluidStack getInputFluid() {
        return inputFluid.copy();
    }

    public void setInputFluid(FluidStack stack) {
        inputFluid = stack.copy();
        setChanged();
    }

    public void fluidChanged() {
        setChanged();
        if (level != null && !level.isClientSide())
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }

    public void setMutationMode(boolean mutation) {
        if (!advanced) return;
        if (mutationMode == mutation) return;
        mutationMode = mutation;
        for (int lane = 0; lane < 4; lane++) reset(lane);
        setChanged();
        if (level != null && !level.isClientSide())
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv) {
        return new HydroponicMenu(id, inv, this);
    }

    @Override
    public int supportedTransfers(Resource resource, Face face) {
        return resource == Resource.FLUIDS ? 1 : super.supportedTransfers(resource, face);
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        if (!super.isItemValid(slot, stack)) return false;
        if (advanced) return slot % 2 == 1 ? isSoil(stack) : isSeed(stack);
        return slot == 1 ? isSoil(stack) : isSeed(stack);
    }

    @Override
    public void setStackInSlot(int slot, ItemStack stack) {
        if (slot >= 0 && slot < inputItems.length && !ItemStack.isSameItemSameComponents(inputItems[slot], stack)) {
            int lane = advanced ? slot / 2 : 0;
            reset(mutationMode && advanced ? lane / 2 * 2 : lane);
        }
        super.setStackInSlot(slot, stack);
    }

    private boolean isSeed(ItemStack stack) {
        return stack.isEmpty() || matchesAny(FTBICRecipes.HYDROPONIC_GROWTH.TYPE.get(), stack, false);
    }

    private boolean isSoil(ItemStack stack) {
        return stack.isEmpty() || matchesAny(FTBICRecipes.HYDROPONIC_GROWTH.TYPE.get(), stack, true);
    }

    private boolean matchesAny(RecipeType<MachineRecipe> type, ItemStack stack, boolean soil) {
        if (!(level instanceof ServerLevel server)) return true;
        for (RecipeHolder<MachineRecipe> holder :
                server.recipeAccess().recipeMap().byType(type)) {
            MachineRecipe recipe = holder.value();
            if (soil) {
                for (SoilOption option : recipe.soilOptions) if (option.matches(stack)) return true;
            } else if (!recipe.inputs.isEmpty()
                    && recipe.inputs.getFirst().ingredient().test(stack)) return true;
        }
        return false;
    }

    @Override
    protected void processRecipes() {
        if (!(level instanceof ServerLevel server) || isBurnt()) return;
        RecipeMap currentRecipes = server.recipeAccess().recipeMap();
        if (cachedRecipes != currentRecipes) {
            if (cachedRecipes != null) for (int lane = 0; lane < 4; lane++) reset(lane);
            cachedRecipes = currentRecipes;
        }
        runningOperations = 0;
        int count = mutationMode ? advanced ? 2 : 1 : getLaneCount();
        int first = (int) (server.getGameTime() % count);
        for (int offset = 0; offset < count; offset++) {
            int operation = (first + offset) % count;
            runningOperations += process(server, operation);
        }
        active = runningOperations > 0;
        if (++syncTimer >= 20) {
            syncTimer = 0;
            setChanged();
            if (!FluidStack.matches(syncedFluid, inputFluid)) {
                syncedFluid = inputFluid.copy();
                server.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            }
        }
    }

    private int process(ServerLevel server, int operation) {
        int lane = mutationMode && advanced ? operation * 2 : operation;
        Match match = cachedMatches[lane];
        if (match == null) {
            match = mutationMode ? findMutation(server, operation) : findGrowth(server, lane);
            cachedMatches[lane] = match;
        }
        if (match == null || !hasSeedInputs(match.recipe, lane, 1)) {
            reset(lane);
            return 0;
        }
        int ticks = Math.max(1, (int) Math.ceil(match.recipe.processingTime
                * FTBICConfig.MACHINES.MACHINE_RECIPE_BASE_TICKS.get()
                / (match.speed * progressSpeed)));
        if (!recipeIds[lane].equals(match.id) || laneDuration[lane] != ticks) {
            reset(lane);
            cachedMatches[lane] = match;
            recipeIds[lane] = match.id;
            laneDuration[lane] = ticks;
        }
        int operations = laneOperations[lane];
        if (operations > getParallelCapacity() || operations > 0 && !hasSeedInputs(match.recipe, lane, operations)) {
            restart(lane);
            operations = 0;
        }
        if (operations == 0) {
            for (int n = getParallelCapacity(); n > 0; n--) {
                if (energy >= energyUse * n
                        && hasSeedInputs(match.recipe, lane, n)
                        && hasFluid(match.recipe, n)
                        && canFit(match, lane, n)) {
                    operations = n;
                    laneOperations[lane] = n;
                    break;
                }
            }
            if (operations == 0) return 0;
        }
        if (!hasFluid(match.recipe, operations) || !canFit(match, lane, operations) || energy < energyUse * operations)
            return 0;
        energy -= energyUse * operations;
        laneProgress[lane]++;
        if (laneProgress[lane] >= ticks) {
            if (mutationMode) finishMutation(match, lane, operations);
            else finishGrowth(match, lane, operations);
            consumeFluid(match.recipe, operations);
            reset(lane);
            setChanged();
            fluidChanged();
        }
        return operations;
    }

    private boolean hasSeedInputs(MachineRecipe recipe, int lane, int operations) {
        if (!mutationMode) {
            ItemStack seed = inputItems[advanced ? lane * 2 : 0];
            return recipe.inputs.getFirst().ingredient().test(seed)
                    && seed.getCount() >= (long) recipe.inputs.getFirst().count() * operations;
        }
        return inputItems[lane * 2].getCount() >= operations && inputItems[(lane + 1) * 2].getCount() >= operations;
    }

    private Match findGrowth(ServerLevel server, int lane) {
        int seedSlot = advanced ? lane * 2 : 0;
        int soilSlot = seedSlot + 1;
        ItemStack seed = inputItems[seedSlot], soil = inputItems[soilSlot];
        if (seed.isEmpty() || soil.isEmpty()) return null;
        for (RecipeHolder<MachineRecipe> holder :
                server.recipeAccess().recipeMap().byType(FTBICRecipes.HYDROPONIC_GROWTH.TYPE.get())) {
            MachineRecipe recipe = holder.value();
            if (recipe.inputs.size() != 1
                    || recipe.outputs.size() < 2
                    || recipe.outputs.size() > 3
                    || !recipe.outputFluids.isEmpty()
                    || recipe.inputFluids.size() > 1
                    || !recipe.inputs.getFirst().matches(seed)) continue;
            for (SoilOption option : recipe.soilOptions) {
                if (option.matches(soil))
                    return new Match(recipe, holder.id().identifier().toString(), option.speed());
            }
        }
        return null;
    }

    private Match findMutation(ServerLevel server, int operation) {
        int lane = operation * 2;
        ItemStack a = inputItems[lane * 2];
        ItemStack b = inputItems[(lane + 1) * 2];
        if (a.isEmpty() || b.isEmpty()) return null;
        Match parentA = findGrowth(server, lane);
        Match parentB = findGrowth(server, lane + 1);
        if (parentA == null || parentB == null) return null;
        for (RecipeHolder<MachineRecipe> holder :
                server.recipeAccess().recipeMap().byType(FTBICRecipes.HYDROPONIC_MUTATION.TYPE.get())) {
            MachineRecipe recipe = holder.value();
            if (recipe.inputs.size() != 2
                    || recipe.outputs.size() != 1
                    || !recipe.outputFluids.isEmpty()
                    || recipe.inputFluids.size() > 1
                    || recipe.inputs.get(0).count() != 1
                    || recipe.inputs.get(1).count() != 1) continue;
            boolean direct = recipe.inputs.get(0).ingredient().test(a)
                    && recipe.inputs.get(1).ingredient().test(b);
            boolean reverse = recipe.inputs.get(0).ingredient().test(b)
                    && recipe.inputs.get(1).ingredient().test(a);
            if (direct || reverse)
                return new Match(recipe, holder.id().identifier().toString(), Math.min(parentA.speed, parentB.speed));
        }
        return null;
    }

    private boolean hasFluid(MachineRecipe recipe, int operations) {
        if (recipe.inputFluids.isEmpty()) return true;
        if (recipe.inputFluids.size() != 1) return false;
        var required = recipe.inputFluids.getFirst();
        return required.test(inputFluid) && inputFluid.getAmount() >= (long) required.amount() * operations;
    }

    private void consumeFluid(MachineRecipe recipe, int operations) {
        if (!recipe.inputFluids.isEmpty())
            inputFluid.shrink(recipe.inputFluids.getFirst().amount() * operations);
    }

    private boolean canFit(Match match, int lane, int operations) {
        MachineRecipe recipe = match.recipe;
        if (!mutationMode) {
            int base = advanced ? lane * 3 : 0;
            int kept = keptSeedOutput(recipe, lane);
            for (int i = 0; i < recipe.outputs.size(); i++) {
                ItemStack stack = growthOutput(recipe, i, kept);
                if (!fits(base + i, stack.copyWithCount(stack.getCount() * operations))) return false;
            }
            return true;
        }
        ItemStack mutant = recipe.outputs.getFirst().stack();
        return fits(lane * 3, mutant.copyWithCount(mutant.getCount() * operations))
                && fits(lane * 3 + 1, inputItems[lane * 2].copyWithCount(operations))
                && fits((lane + 1) * 3 + 1, inputItems[(lane + 1) * 2].copyWithCount(operations));
    }

    private boolean fits(int slot, ItemStack add) {
        if (add.isEmpty()) return true;
        ItemStack current = outputItems[slot];
        return current.isEmpty()
                ? add.getCount() <= add.getMaxStackSize()
                : ItemStack.isSameItemSameComponents(current, add)
                        && current.getCount() + add.getCount() <= current.getMaxStackSize();
    }

    private void insert(int slot, ItemStack add) {
        if (add.isEmpty()) return;
        if (outputItems[slot].isEmpty()) outputItems[slot] = add.copy();
        else outputItems[slot].grow(add.getCount());
    }

    private void finishGrowth(Match match, int lane, int operations) {
        int kept = keptSeedOutput(match.recipe, lane);
        if (kept < 0)
            inputItems[advanced ? lane * 2 : 0].shrink(
                    match.recipe.inputs.getFirst().count() * operations);
        int base = advanced ? lane * 3 : 0;
        for (int op = 0; op < operations; op++) {
            for (int i = 0; i < match.recipe.outputs.size(); i++) {
                StackWithChance output = match.recipe.outputs.get(i);
                if (output.chance() < 1 && level.getRandom().nextDouble() >= output.chance()) continue;
                insert(base + i, growthOutput(match.recipe, i, kept));
            }
        }
    }

    private int keptSeedOutput(MachineRecipe recipe, int lane) {
        ItemStack seed = inputItems[advanced ? lane * 2 : 0];
        int consumed = recipe.inputs.getFirst().count();
        for (int i = 0; i < recipe.outputs.size(); i++) {
            StackWithChance output = recipe.outputs.get(i);
            ItemStack stack = output.stack();
            if (output.chance() >= 1 && stack.getCount() >= consumed && ItemStack.isSameItemSameComponents(stack, seed))
                return i;
        }
        return -1;
    }

    private ItemStack growthOutput(MachineRecipe recipe, int index, int kept) {
        ItemStack stack = recipe.outputs.get(index).stack();
        if (index != kept) return stack;
        return stack.copyWithCount(stack.getCount() - recipe.inputs.getFirst().count());
    }

    private void finishMutation(Match match, int lane, int operations) {
        int first = lane * 2;
        int second = (lane + 1) * 2;
        StackWithChance output = match.recipe.outputs.getFirst();
        for (int op = 0; op < operations; op++) {
            ItemStack a = inputItems[first].copyWithCount(1);
            ItemStack b = inputItems[second].copyWithCount(1);
            inputItems[first].shrink(1);
            inputItems[second].shrink(1);
            if (level.getRandom().nextDouble() < output.chance()) insert(lane * 3, output.stack());
            else {
                insert(lane * 3 + 1, a);
                insert((lane + 1) * 3 + 1, b);
            }
        }
    }

    private void restart(int lane) {
        laneProgress[lane] = 0;
        laneOperations[lane] = 0;
    }

    private void reset(int lane) {
        restart(lane);
        laneDuration[lane] = 0;
        recipeIds[lane] = "";
        cachedMatches[lane] = null;
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putBoolean("MutationMode", mutationMode);
        output.store("InputFluid", FluidStack.OPTIONAL_CODEC, inputFluid);
        for (int i = 0; i < 4; i++) {
            output.putInt("HydroProgress" + i, laneProgress[i]);
            output.putInt("HydroDuration" + i, laneDuration[i]);
            output.putInt("HydroOperations" + i, laneOperations[i]);
            output.putString("HydroRecipe" + i, recipeIds[i]);
        }
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        mutationMode = advanced && input.getBooleanOr("MutationMode", false);
        inputFluid = input.read("InputFluid", FluidStack.OPTIONAL_CODEC).orElse(FluidStack.EMPTY);
        for (int i = 0; i < 4; i++) {
            laneProgress[i] = Math.max(0, input.getIntOr("HydroProgress" + i, 0));
            laneDuration[i] = Math.max(0, input.getIntOr("HydroDuration" + i, 0));
            laneOperations[i] = Math.clamp(input.getIntOr("HydroOperations" + i, 0), 0, 4);
            recipeIds[i] = input.getStringOr("HydroRecipe" + i, "");
        }
    }

    private record Match(MachineRecipe recipe, String id, double speed) {}
}
