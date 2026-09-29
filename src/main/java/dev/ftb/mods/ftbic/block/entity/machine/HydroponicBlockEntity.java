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

public final class HydroponicBlockEntity extends BasicMachineBlockEntity {
    private final boolean advanced;
    private final int[] progress = new int[4];
    private final int[] duration = new int[4];
    private final String[] recipeIds = new String[4];
    private final Match[] cachedMatches = new Match[4];
    private boolean mutationMode;
    private FluidStack inputFluid = FluidStack.EMPTY;
    private int syncTimer;
    private FluidStack syncedFluid = FluidStack.EMPTY;
    private RecipeMap cachedRecipes;
    public final HydroponicWaterHandler fluidHandler = new HydroponicWaterHandler(this);

    public HydroponicBlockEntity(BlockPos pos, BlockState state) {
        this(FTBICElectricBlocks.HYDROPONIC_ACCELERATOR, false, pos, state);
    }

    public static HydroponicBlockEntity advanced(BlockPos pos, BlockState state) {
        return new HydroponicBlockEntity(FTBICElectricBlocks.ADVANCED_HYDROPONIC_ACCELERATOR, true, pos, state);
    }

    private HydroponicBlockEntity(ElectricBlockInstance instance, boolean advanced, BlockPos pos, BlockState state) {
        super(instance, pos, state);
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
        return progress[lane];
    }

    public int getDuration(int lane) {
        return duration[lane];
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
    public void tick() {
        super.tick();
        if (!(level instanceof ServerLevel server) || isBurnt()) return;
        RecipeMap currentRecipes = server.recipeAccess().recipeMap();
        if (cachedRecipes != currentRecipes) {
            if (cachedRecipes != null) for (int lane = 0; lane < 4; lane++) reset(lane);
            cachedRecipes = currentRecipes;
        }
        boolean running = false;
        int count = mutationMode ? advanced ? 2 : 1 : getLaneCount();
        int first = (int) (server.getGameTime() % count);
        for (int offset = 0; offset < count; offset++) {
            int operation = (first + offset) % count;
            running |= process(server, operation);
        }
        active = running;
        if (++syncTimer >= 20) {
            syncTimer = 0;
            setChanged();
            if (!FluidStack.matches(syncedFluid, inputFluid)) {
                syncedFluid = inputFluid.copy();
                server.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            }
        }
    }

    private boolean process(ServerLevel server, int operation) {
        int lane = mutationMode && advanced ? operation * 2 : operation;
        Match match = cachedMatches[lane];
        if (match == null) {
            match = mutationMode ? findMutation(server, operation) : findGrowth(server, lane);
            cachedMatches[lane] = match;
        }
        if (match == null) {
            reset(lane);
            return false;
        }
        if (!hasSeedInputs(match.recipe, lane)) {
            reset(lane);
            return false;
        }
        int ticks = Math.max(1, (int) Math.ceil(match.recipe.processingTime
                * FTBICConfig.MACHINES.MACHINE_RECIPE_BASE_TICKS.get()
                / (match.speed * progressSpeed)));
        if (!recipeIds[lane].equals(match.id) || duration[lane] != ticks) {
            reset(lane);
            cachedMatches[lane] = match;
            recipeIds[lane] = match.id;
            duration[lane] = ticks;
        }
        if (!hasFluid(match.recipe) || !canFit(match, lane) || energy < energyUse) return false;
        energy -= energyUse;
        progress[lane]++;
        if (progress[lane] >= ticks) {
            if (mutationMode) finishMutation(match, lane);
            else finishGrowth(match, lane);
            consumeFluid(match.recipe);
            reset(lane);
            setChanged();
            fluidChanged();
        }
        return true;
    }

    private boolean hasSeedInputs(MachineRecipe recipe, int lane) {
        if (!mutationMode) return recipe.inputs.getFirst().matches(inputItems[advanced ? lane * 2 : 0]);
        return !inputItems[lane * 2].isEmpty() && !inputItems[(lane + 1) * 2].isEmpty();
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

    private boolean hasFluid(MachineRecipe recipe) {
        if (recipe.inputFluids.isEmpty()) return true;
        if (recipe.inputFluids.size() != 1) return false;
        var required = recipe.inputFluids.getFirst();
        return required.test(inputFluid) && inputFluid.getAmount() >= required.amount();
    }

    private void consumeFluid(MachineRecipe recipe) {
        if (!recipe.inputFluids.isEmpty())
            inputFluid.shrink(recipe.inputFluids.getFirst().amount());
    }

    private boolean canFit(Match match, int lane) {
        MachineRecipe recipe = match.recipe;
        if (!mutationMode) {
            int base = advanced ? lane * 3 : 0;
            for (int i = 0; i < recipe.outputs.size(); i++) {
                int target = advanced ? base + i : i;
                if (!fits(target, recipe.outputs.get(i).stack())) return false;
            }
            return true;
        }
        ItemStack mutant = recipe.outputs.getFirst().stack();
        return fits(lane * 3, mutant)
                && fits(lane * 3 + 1, inputItems[lane * 2].copyWithCount(1))
                && fits((lane + 1) * 3 + 1, inputItems[(lane + 1) * 2].copyWithCount(1));
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

    private void finishGrowth(Match match, int lane) {
        inputItems[advanced ? lane * 2 : 0].shrink(
                match.recipe.inputs.getFirst().count());
        for (int i = 0; i < match.recipe.outputs.size(); i++) {
            StackWithChance output = match.recipe.outputs.get(i);
            if (output.chance() < 1 && level.getRandom().nextDouble() >= output.chance()) continue;
            insert(advanced ? lane * 3 + i : i, output.stack());
        }
    }

    private void finishMutation(Match match, int lane) {
        int first = lane * 2;
        int second = (lane + 1) * 2;
        ItemStack a = inputItems[first].copyWithCount(1);
        ItemStack b = inputItems[second].copyWithCount(1);
        inputItems[first].shrink(1);
        inputItems[second].shrink(1);
        StackWithChance output = match.recipe.outputs.getFirst();
        if (level.getRandom().nextDouble() < output.chance()) insert(lane * 3, output.stack());
        else {
            insert(lane * 3 + 1, a);
            insert((lane + 1) * 3 + 1, b);
        }
    }

    private void reset(int lane) {
        progress[lane] = 0;
        duration[lane] = 0;
        recipeIds[lane] = "";
        cachedMatches[lane] = null;
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putBoolean("MutationMode", mutationMode);
        output.store("InputFluid", FluidStack.OPTIONAL_CODEC, inputFluid);
        for (int i = 0; i < 4; i++) {
            output.putInt("HydroProgress" + i, progress[i]);
            output.putInt("HydroDuration" + i, duration[i]);
            output.putString("HydroRecipe" + i, recipeIds[i]);
        }
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        mutationMode = advanced && input.getBooleanOr("MutationMode", false);
        inputFluid = input.read("InputFluid", FluidStack.OPTIONAL_CODEC).orElse(FluidStack.EMPTY);
        for (int i = 0; i < 4; i++) {
            progress[i] = Math.max(0, input.getIntOr("HydroProgress" + i, 0));
            duration[i] = Math.max(0, input.getIntOr("HydroDuration" + i, 0));
            recipeIds[i] = input.getStringOr("HydroRecipe" + i, "");
        }
    }

    private record Match(MachineRecipe recipe, String id, double speed) {}
}
