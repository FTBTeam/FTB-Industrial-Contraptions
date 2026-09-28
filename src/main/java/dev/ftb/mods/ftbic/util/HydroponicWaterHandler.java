package dev.ftb.mods.ftbic.util;

import dev.ftb.mods.ftbic.block.entity.machine.HydroponicBlockEntity;
import dev.ftb.mods.ftbic.recipe.FTBICRecipes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.crafting.RecipeMap;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jetbrains.annotations.Nullable;

public final class HydroponicWaterHandler extends SnapshotJournal<FluidStack>
        implements ResourceHandler<FluidResource> {
    private final HydroponicBlockEntity machine;

    @Nullable
    private RecipeMap checkedRecipes;

    private FluidResource checkedFluid = FluidResource.EMPTY;
    private boolean checkedResult;

    public HydroponicWaterHandler(HydroponicBlockEntity machine) {
        this.machine = machine;
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public FluidResource getResource(int index) {
        return index == 0 ? FluidResource.of(machine.getInputFluid()) : FluidResource.EMPTY;
    }

    @Override
    public long getAmountAsLong(int index) {
        return index == 0 ? machine.getInputFluid().getAmount() : 0;
    }

    @Override
    public long getCapacityAsLong(int index, FluidResource resource) {
        return index == 0 ? machine.getTankCapacity() : 0;
    }

    @Override
    public boolean isValid(int index, FluidResource resource) {
        return index == 0 && acceptsFluid(resource);
    }

    public boolean acceptsFluid(FluidResource resource) {
        if (resource.isEmpty()) return false;
        if (!(machine.getLevel() instanceof ServerLevel server)) return true;
        RecipeMap recipes = server.recipeAccess().recipeMap();
        if (recipes != checkedRecipes || !resource.equals(checkedFluid)) {
            checkedRecipes = recipes;
            checkedFluid = resource;
            checkedResult =
                    FluidMachineTankHandler.usesInputFluid(recipes, FTBICRecipes.HYDROPONIC_GROWTH.TYPE.get(), resource)
                            || FluidMachineTankHandler.usesInputFluid(
                                    recipes, FTBICRecipes.HYDROPONIC_MUTATION.TYPE.get(), resource);
        }
        return checkedResult;
    }

    @Override
    public int insert(int index, FluidResource resource, int amount, TransactionContext transaction) {
        if (!isValid(index, resource) || amount <= 0) return 0;
        FluidStack fluid = machine.getInputFluid();
        if (!fluid.isEmpty() && !FluidResource.of(fluid).equals(resource)) return 0;
        int accepted = Math.min(amount, machine.getTankCapacity() - fluid.getAmount());
        if (accepted <= 0) return 0;
        updateSnapshots(transaction);
        machine.setInputFluid(resource.toStack(fluid.getAmount() + accepted));
        return accepted;
    }

    @Override
    public int extract(int index, FluidResource resource, int amount, TransactionContext transaction) {
        if (index != 0 || resource.isEmpty() || amount <= 0) return 0;
        FluidStack fluid = machine.getInputFluid();
        if (!FluidResource.of(fluid).equals(resource) || acceptsFluid(resource)) return 0;
        int extracted = Math.min(amount, fluid.getAmount());
        if (extracted <= 0) return 0;
        updateSnapshots(transaction);
        fluid.shrink(extracted);
        machine.setInputFluid(fluid);
        return extracted;
    }

    @Override
    protected FluidStack createSnapshot() {
        return machine.getInputFluid();
    }

    @Override
    protected void revertToSnapshot(FluidStack snapshot) {
        machine.setInputFluid(snapshot);
    }

    @Override
    protected void onRootCommit(FluidStack original) {
        machine.fluidChanged();
    }
}
