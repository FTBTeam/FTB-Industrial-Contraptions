package dev.ftb.mods.ftbic.block.entity.machine;

import dev.ftb.mods.ftbic.block.ElectricBlockInstance;
import dev.ftb.mods.ftbic.recipe.MachineRecipe;
import dev.ftb.mods.ftbic.recipe.MachineRecipeType;
import dev.ftb.mods.ftbic.util.FluidMachineTankHandler;
import dev.ftb.mods.ftbic.util.SideConfiguration.Face;
import dev.ftb.mods.ftbic.util.SideConfiguration.Resource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.fluids.FluidStack;

public class FluidMachineBlockEntity extends MachineBlockEntity {
    public static final int TANK_CAPACITY = 16_000;
    private FluidStack inputFluid = FluidStack.EMPTY;
    private FluidStack outputFluid = FluidStack.EMPTY;
    public final FluidMachineTankHandler fluidHandler = new FluidMachineTankHandler(this);
    private boolean fluidsDirty;

    protected FluidMachineBlockEntity(
            ElectricBlockInstance type, MachineRecipeType recipeType, BlockPos pos, BlockState state) {
        super(type, recipeType, pos, state);
    }

    public FluidStack getInputFluid() {
        return inputFluid.copy();
    }

    public FluidStack getOutputFluid() {
        return outputFluid.copy();
    }

    /** Used by the shared transaction journal, including rollback. */
    public void setFluids(FluidStack input, FluidStack output) {
        inputFluid = input.copy();
        outputFluid = output.copy();
        markRecipeDirty();
    }

    public void fluidsChanged() {
        markRecipeDirty();
        fluidsDirty = true;
        setChanged();
    }

    @Override
    public int supportedTransfers(Resource resource, Face face) {
        return resource == Resource.FLUIDS ? 3 : super.supportedTransfers(resource, face);
    }

    @Override
    protected boolean matchesFluidInputs(MachineRecipe recipe, int operations) {
        if (recipe.inputFluids.size() > 1
                || recipe.outputFluids.size() > 1
                || recipe.outputs.size() > outputItems.length) return false;
        return recipe.inputFluids.isEmpty()
                || (recipe.inputFluids.getFirst().test(inputFluid)
                        && inputFluid.getAmount()
                                >= (long) recipe.inputFluids.getFirst().amount() * operations);
    }

    @Override
    protected boolean canFitFluidOutputs(MachineRecipe recipe, int operations) {
        if (recipe.outputFluids.isEmpty()) return true;
        FluidStack result = recipe.outputFluids.getFirst();
        return (outputFluid.isEmpty() || FluidStack.isSameFluidSameComponents(outputFluid, result))
                && (long) result.getAmount() * operations <= TANK_CAPACITY - outputFluid.getAmount();
    }

    @Override
    protected void processFluids(MachineRecipe recipe) {
        if (!recipe.inputFluids.isEmpty())
            inputFluid.shrink(recipe.inputFluids.getFirst().amount());
        if (!recipe.outputFluids.isEmpty()) {
            FluidStack result = recipe.outputFluids.getFirst();
            if (outputFluid.isEmpty()) outputFluid = result.copy();
            else outputFluid.grow(result.getAmount());
        }
        if (!recipe.inputFluids.isEmpty() || !recipe.outputFluids.isEmpty()) fluidsChanged();
    }

    @Override
    public void tick() {
        super.tick();
        if (fluidsDirty && level != null && !level.isClientSide()) {
            fluidsDirty = false;
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.store("InputFluid", FluidStack.OPTIONAL_CODEC, inputFluid);
        output.store("OutputFluid", FluidStack.OPTIONAL_CODEC, outputFluid);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        setFluids(
                input.read("InputFluid", FluidStack.OPTIONAL_CODEC).orElse(FluidStack.EMPTY),
                input.read("OutputFluid", FluidStack.OPTIONAL_CODEC).orElse(FluidStack.EMPTY));
    }
}
