package dev.ftb.mods.ftbic.test;

import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.block.entity.machine.HydroponicBlockEntity;
import dev.ftb.mods.ftbic.recipe.FTBICRecipes;
import dev.ftb.mods.ftbic.recipe.MachineRecipe;
import dev.ftb.mods.ftbic.recipe.SoilOption;
import dev.ftb.mods.ftbic.util.IngredientWithCount;
import dev.ftb.mods.ftbic.util.StackWithChance;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

final class HydroponicGameTests {
    private static final BlockPos POS = new BlockPos(2, 2, 2);

    private static HydroponicBlockEntity place(GameTestHelper h, boolean advanced) {
        h.setBlock(
                POS,
                (advanced
                                ? FTBICElectricBlocks.ADVANCED_HYDROPONIC_ACCELERATOR
                                : FTBICElectricBlocks.HYDROPONIC_ACCELERATOR)
                        .block.get());
        HydroponicBlockEntity machine = h.getBlockEntity(POS, HydroponicBlockEntity.class);
        machine.energy = machine.getEnergyCapacity();
        machine.setInputFluid(new FluidStack(Fluids.WATER, 2_000));
        return machine;
    }

    private static StackWithChance output(Item item, int count, double chance) {
        return new StackWithChance(new ItemStackTemplate(item, count), chance);
    }

    private static MachineRecipe growth(Item seed, Item product, double time) {
        return new MachineRecipe(
                FTBICRecipes.HYDROPONIC_GROWTH,
                List.of(new IngredientWithCount(Ingredient.of(seed), 1)),
                List.of(SizedFluidIngredient.of(Fluids.WATER, 250)),
                List.of(output(product, 2, 1), output(seed, 2, 1), output(seed, 1, 0)),
                List.of(),
                time,
                false,
                List.of(
                        new SoilOption(Ingredient.of(Items.DIRT), 1),
                        new SoilOption(Ingredient.of(Items.MOSS_BLOCK), 1.25)));
    }

    private static MachineRecipe mutation() {
        return new MachineRecipe(
                FTBICRecipes.HYDROPONIC_MUTATION,
                List.of(
                        new IngredientWithCount(Ingredient.of(Items.WHEAT_SEEDS), 1),
                        new IngredientWithCount(Ingredient.of(Items.BEETROOT_SEEDS), 1)),
                List.of(SizedFluidIngredient.of(Fluids.WATER, 500)),
                List.of(output(Items.MELON_SEEDS, 1, 0)),
                List.of(),
                0.001,
                false);
    }

    static void catalystAndSpeed(GameTestHelper h) {
        HydroponicBlockEntity machine = place(h, false);
        h.assertValueEqual(2, machine.inputItems.length, "Basic machine has one seed and soil pair");
        h.assertValueEqual(3, machine.outputItems.length, "Basic machine has growth outputs only");
        machine.setMutationMode(true);
        h.assertTrue(!machine.isMutationMode(), "Only the advanced machine can enable mutation");
        CentrifugeFluidGameTests.withRecipes(h, List.of(growth(Items.WHEAT_SEEDS, Items.WHEAT, 0.1)), () -> {
            machine.setStackInSlot(0, new ItemStack(Items.WHEAT_SEEDS, 2));
            machine.setStackInSlot(1, new ItemStack(Items.DIRT));
            machine.tick();
            h.assertValueEqual(20, machine.getDuration(0), "Dirt uses the base time");
            machine.setStackInSlot(1, new ItemStack(Items.MOSS_BLOCK));
            machine.tick();
            h.assertValueEqual(16, machine.getDuration(0), "Moss accelerates wheat by 1.25x");
            for (int i = 0; i < 15; i++) machine.tick();
            h.assertTrue(
                    machine.inputItems[1].is(Items.MOSS_BLOCK) && machine.inputItems[1].getCount() == 1,
                    "Soil remains unchanged after harvest");
            h.assertValueEqual(1, machine.inputItems[0].getCount(), "One seed consumed");
            h.assertValueEqual(2, machine.outputItems[0].getCount(), "Produce output filled");
            h.assertValueEqual(2, machine.outputItems[1].getCount(), "Seed output filled");
            h.assertValueEqual(1_750, machine.getInputFluid().getAmount(), "Water consumed once");
        });
        h.succeed();
    }

    static void mutationAndLanes(GameTestHelper h) {
        HydroponicBlockEntity machine = place(h, true);
        CentrifugeFluidGameTests.withRecipes(
                h,
                List.of(
                        growth(Items.WHEAT_SEEDS, Items.WHEAT, 0.001),
                        growth(Items.BEETROOT_SEEDS, Items.BEETROOT, 0.001),
                        mutation()),
                () -> {
                    machine.setStackInSlot(0, new ItemStack(Items.WHEAT_SEEDS, 2));
                    machine.setStackInSlot(1, new ItemStack(Items.DIRT));
                    machine.setStackInSlot(2, new ItemStack(Items.BEETROOT_SEEDS, 2));
                    machine.setStackInSlot(3, new ItemStack(Items.MOSS_BLOCK));
                    machine.tick();
                    h.assertTrue(
                            machine.outputItems[0].is(Items.WHEAT) && machine.outputItems[3].is(Items.BEETROOT),
                            "Independent advanced lanes grow different crops");
                    machine.setMutationMode(true);
                    machine.tick();
                    h.assertTrue(
                            machine.outputItems[1].is(Items.WHEAT_SEEDS)
                                    && machine.outputItems[4].is(Items.BEETROOT_SEEDS),
                            "Failed mutation returns each parent to its own lane");
                    h.assertTrue(
                            machine.inputItems[1].is(Items.DIRT) && machine.inputItems[3].is(Items.MOSS_BLOCK),
                            "Both soils survive mutation");
                });
        h.succeed();
    }
}
