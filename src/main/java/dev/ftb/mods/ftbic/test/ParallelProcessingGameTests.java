package dev.ftb.mods.ftbic.test;

import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.block.ElectricBlockInstance;
import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.block.entity.machine.CentrifugeBlockEntity;
import dev.ftb.mods.ftbic.block.entity.machine.MachineBlockEntity;
import dev.ftb.mods.ftbic.item.FTBICItems;
import dev.ftb.mods.ftbic.recipe.MachineRecipe;
import dev.ftb.mods.ftbic.screen.MachineMenu;
import dev.ftb.mods.ftbic.screen.UpgradeInventoryContainer;
import dev.ftb.mods.ftbic.screen.UpgradeSlot;
import dev.ftb.mods.ftbic.util.IngredientWithCount;
import dev.ftb.mods.ftbic.util.StackWithChance;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.util.ProblemReporter;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.TagValueInput;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

final class ParallelProcessingGameTests {
    private static final BlockPos POS = new BlockPos(2, 2, 2);

    private static MachineBlockEntity place(GameTestHelper h, ElectricBlockInstance type, int upgrades) {
        h.setBlock(POS, type.block.get());
        var machine = h.getBlockEntity(POS, MachineBlockEntity.class);
        if (upgrades > 0)
            machine.upgradeInventory.setStackInSlot(
                    0, new ItemStack(FTBICItems.PARALLEL_PROCESSING_UPGRADE.get(), upgrades));
        machine.energy = machine.getEnergyCapacity();
        return machine;
    }

    private static StackWithChance output(ItemStack stack, double chance) {
        return new StackWithChance(
                new ItemStackTemplate(stack.typeHolder(), stack.getCount(), stack.getComponentsPatch()), chance);
    }

    private static MachineRecipe recipe(MachineBlockEntity machine, int ticks, boolean fluid, double chance) {
        return new MachineRecipe(
                machine.recipeType,
                List.of(new IngredientWithCount(Ingredient.of(Items.CLAY_BALL), 2)),
                fluid ? List.of(SizedFluidIngredient.of(Fluids.WATER, 1000)) : List.of(),
                List.of(output(new ItemStack(Items.IRON_NUGGET), 1), output(new ItemStack(Items.GOLD_NUGGET), chance)),
                fluid ? List.of(new FluidStack(Fluids.LAVA, 500)) : List.of(),
                (ticks + 0.1) / FTBICConfig.MACHINES.MACHINE_RECIPE_BASE_TICKS.get(),
                true);
    }

    static void processing(GameTestHelper h) {
        for (var type : List.of(
                FTBICElectricBlocks.ADVANCED_MACERATOR,
                FTBICElectricBlocks.ADVANCED_COMPRESSOR,
                FTBICElectricBlocks.ADVANCED_POWERED_FURNACE,
                FTBICElectricBlocks.ADVANCED_CENTRIFUGE,
                FTBICElectricBlocks.ALLOY_SMELTER,
                FTBICElectricBlocks.REPROCESSOR)) {
            var machine = place(h, type, 3);
            // One output fits all six processor layouts.
            var recipe = new MachineRecipe(
                    machine.recipeType,
                    List.of(new IngredientWithCount(Ingredient.of(Items.CLAY_BALL), 2)),
                    List.of(),
                    List.of(output(new ItemStack(Items.IRON_NUGGET), 1)),
                    List.of(),
                    0.001,
                    true);
            CentrifugeFluidGameTests.withRecipes(h, List.of(recipe), () -> {
                machine.setStackInSlot(0, new ItemStack(Items.CLAY_BALL, 8));
                double before = machine.energy;
                machine.tick();
                h.assertTrue(machine.inputItems[0].isEmpty(), "Four complete ingredient sets consumed: " + type.id);
                h.assertValueEqual(4, machine.outputItems[0].getCount(), "Four outputs: " + type.id);
                h.assertTrue(
                        Math.abs(before - machine.energy - 4 * machine.energyUse) < 0.001,
                        "Four operations pay four times energy");
                h.assertValueEqual(4, machine.getRunningOperations(), "One-tick recipes report their batch width");
            });
        }
        h.succeed();
    }

    static void fluidsAndLimits(GameTestHelper h) {
        var machine = (CentrifugeBlockEntity) place(h, FTBICElectricBlocks.ADVANCED_CENTRIFUGE, 3);
        CentrifugeFluidGameTests.withRecipes(h, List.of(recipe(machine, 1, true, 1)), () -> {
            machine.setStackInSlot(0, new ItemStack(Items.CLAY_BALL, 8));
            machine.setFluids(new FluidStack(Fluids.WATER, 3500), FluidStack.EMPTY);
            double before = machine.energy;
            machine.tick();
            h.assertValueEqual(2, machine.inputItems[0].getCount(), "Fluid supply limits batch to three");
            h.assertValueEqual(500, machine.getInputFluid().getAmount(), "Three buckets consumed");
            h.assertValueEqual(1500, machine.getOutputFluid().getAmount(), "Three fluid outputs produced");
            h.assertTrue(
                    Math.abs(before - machine.energy - 3 * machine.energyUse) < 0.001,
                    "Partial batch only pays for three");
            machine.setStackInSlot(0, new ItemStack(Items.CLAY_BALL, 8));
            machine.setFluids(new FluidStack(Fluids.WATER, 4000), new FluidStack(Fluids.LAVA, 15_000));
            machine.tick();
            h.assertValueEqual(4, machine.inputItems[0].getCount(), "Fluid output space limits batch to two");
            h.assertValueEqual(16_000, machine.getOutputFluid().getAmount(), "Output tank never overfills");
            before = machine.energy;
            machine.tick();
            h.assertTrue(before == machine.energy, "Full output tank spends no energy");
            machine.setFluids(new FluidStack(Fluids.WATER, 4000), FluidStack.EMPTY);
            machine.outputItems[0] = new ItemStack(Items.IRON_NUGGET, 63);
            machine.outputItems[1] = new ItemStack(Items.GOLD_NUGGET, 63);
            machine.outputItems[2] = new ItemStack(Items.FLINT, 64);
            machine.tick();
            h.assertValueEqual(2, machine.inputItems[0].getCount(), "Item output room limits batch to one");
            before = machine.energy;
            machine.tick();
            h.assertTrue(before == machine.energy, "Full item outputs spend no energy");
        });
        h.succeed();
    }

    static void chanceOutputs(GameTestHelper h) {
        var machine = place(h, FTBICElectricBlocks.ADVANCED_CENTRIFUGE, 3);
        CentrifugeFluidGameTests.withRecipes(h, List.of(recipe(machine, 1, false, 0.5)), () -> {
            machine.setStackInSlot(0, new ItemStack(Items.CLAY_BALL, 8));
            long seed = 12345;
            RandomSource expected = RandomSource.create(seed);
            int gold = 0;
            for (int i = 0; i < 4; i++) if (expected.nextDouble() < 0.5) gold++;
            h.assertTrue(gold > 0 && gold < 4, "Fixture distinguishes independent rolls from multiplied output");
            h.getLevel().getRandom().setSeed(seed);
            machine.tick();
            h.assertValueEqual(4, machine.outputItems[0].getCount(), "Guaranteed outputs from every operation");
            h.assertValueEqual(
                    gold, machine.outputItems[1].getCount(), "Each operation rolls its chance independently");
        });
        h.succeed();
    }

    static void progressAndReload(GameTestHelper h) {
        var machine = place(h, FTBICElectricBlocks.ADVANCED_CENTRIFUGE, 3);
        CentrifugeFluidGameTests.withRecipes(h, List.of(recipe(machine, 4, false, 1)), () -> {
            machine.setStackInSlot(0, new ItemStack(Items.CLAY_BALL, 2));
            machine.tick();
            machine.setStackInSlot(0, new ItemStack(Items.CLAY_BALL, 8));
            machine.tick();
            var tag = machine.saveCustomOnly(h.getLevel().registryAccess());
            machine.loadCustomOnly(TagValueInput.create(
                    ProblemReporter.DISCARDING, h.getLevel().registryAccess(), tag));
            h.assertValueEqual(2, machine.progress, "Progress survives reload");
            machine.tick();
            machine.tick();
            h.assertValueEqual(
                    1,
                    machine.outputItems[0].getCount(),
                    "Added inputs do not inherit progress, including after reload");
            h.assertValueEqual(6, machine.inputItems[0].getCount(), "Only the original operation consumes inputs");
            machine.tick();
            h.assertValueEqual(3, machine.getRunningOperations(), "Next cycle expands to available ingredients");
            machine.outputItems[0] = new ItemStack(Items.IRON_NUGGET, 64);
            machine.outputItems[1] = new ItemStack(Items.GOLD_NUGGET, 64);
            machine.outputItems[2] = new ItemStack(Items.FLINT, 64);
            double energy = machine.energy;
            machine.tick();
            h.assertTrue(
                    machine.progress == 1 && machine.energy == energy,
                    "Mid-cycle output blockage preserves progress without spending energy");
            machine.outputItems[0] = ItemStack.EMPTY;
            machine.outputItems[1] = ItemStack.EMPTY;
            machine.upgradeInventory.setStackInSlot(0, ItemStack.EMPTY);
            h.assertValueEqual(0, machine.progress, "Removing capacity resets the unfinished batch");
            machine.tick();
            h.assertValueEqual(
                    1, machine.getRunningOperations(), "Removed upgrade cannot keep running extra operations");
        });
        h.succeed();
    }

    static void changedInputs(GameTestHelper h) {
        var machine = place(h, FTBICElectricBlocks.ADVANCED_CENTRIFUGE, 3);
        var original = recipe(machine, 4, false, 1);
        var alternate = new MachineRecipe(
                machine.recipeType,
                List.of(new IngredientWithCount(Ingredient.of(Items.RAW_IRON), 1)),
                List.of(),
                List.of(output(new ItemStack(Items.DIAMOND), 1)),
                List.of(),
                original.processingTime,
                true);
        CentrifugeFluidGameTests.withRecipes(h, List.of(original, alternate), () -> {
            machine.setStackInSlot(0, new ItemStack(Items.CLAY_BALL, 8));
            machine.tick();
            var tag = machine.saveCustomOnly(h.getLevel().registryAccess());
            machine.loadCustomOnly(TagValueInput.create(
                    ProblemReporter.DISCARDING, h.getLevel().registryAccess(), tag));
            double energy = machine.energy;
            for (int i = 0; i < 3; i++) machine.tick();
            h.assertValueEqual(4, machine.outputItems[0].getCount(), "Reload preserves all four pending operations");
            h.assertTrue(
                    Math.abs(energy - machine.energy - 12 * machine.energyUse) < 0.001,
                    "Reload preserves per-operation power cost");
            machine.outputItems[0] = ItemStack.EMPTY;
            machine.outputItems[1] = ItemStack.EMPTY;
            machine.setStackInSlot(0, new ItemStack(Items.CLAY_BALL, 8));
            machine.tick();
            machine.tick();
            machine.setStackInSlot(0, new ItemStack(Items.CLAY_BALL, 2));
            machine.tick();
            h.assertValueEqual(1, machine.progress, "Losing ingredients starts a smaller batch from zero");
            machine.setStackInSlot(0, new ItemStack(Items.RAW_IRON, 4));
            machine.tick();
            h.assertTrue(
                    machine.progress == 1 && machine.outputItems[0].isEmpty(),
                    "Switching recipes cannot inherit progress");
            for (int i = 0; i < 3; i++) machine.tick();
            h.assertTrue(
                    machine.outputItems[0].is(Items.DIAMOND) && machine.outputItems[0].getCount() == 4,
                    "Only the new recipe produces outputs");
        });
        h.succeed();
    }

    static void powerAndCompatibility(GameTestHelper h) {
        ItemStack upgrade = new ItemStack(FTBICItems.PARALLEL_PROCESSING_UPGRADE.get(), 3);
        var basic = place(h, FTBICElectricBlocks.MACERATOR, 0);
        h.assertTrue(!basic.upgradeInventory.isItemValid(0, upgrade), "Basic machines reject parallel upgrades");
        var basicSlot = new UpgradeSlot(new UpgradeInventoryContainer(basic.upgradeInventory), 0, 0, 0);
        h.assertValueEqual(0, basicSlot.getMaxStackSize(upgrade), "Shift-click cannot merge unsupported upgrades");
        var machine = place(h, FTBICElectricBlocks.ADVANCED_CENTRIFUGE, 3);
        h.assertValueEqual(0, machine.upgradeInventory.getSlotLimit(1, upgrade), "Three-upgrade limit spans all slots");
        h.assertValueEqual(3, machine.upgradeInventory.getSlotLimit(0, upgrade), "Existing slot retains its capacity");
        CentrifugeFluidGameTests.withRecipes(h, List.of(recipe(machine, 3, false, 1)), () -> {
            machine.setStackInSlot(0, new ItemStack(Items.CLAY_BALL, 8));
            machine.energy = machine.energyUse * 2.5;
            machine.tick();
            h.assertValueEqual(2, machine.getRunningOperations(), "Stored power limits starting batch size");
            machine.tick();
            h.assertTrue(
                    machine.starving && machine.progress == 1 && machine.outputItems[0].isEmpty(),
                    "Starvation pauses the batch without losing progress");
            machine.energy = machine.getEnergyCapacity();
            machine.tick();
            h.assertTrue(
                    machine.progress == 2 && machine.getRunningOperations() == 2,
                    "Power recovery resumes the paused two-operation batch");
            var menu = new MachineMenu(7, h.makeMockPlayer(GameType.SURVIVAL).getInventory(), machine);
            menu.broadcastChanges();
            h.assertValueEqual(2, menu.runningOperations.get(), "Menu synchronizes running operations");
            h.assertValueEqual(4, menu.parallelCapacity.get(), "Menu synchronizes capacity");
        });
        h.succeed();
    }
}
