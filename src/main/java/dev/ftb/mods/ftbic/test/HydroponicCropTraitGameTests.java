package dev.ftb.mods.ftbic.test;

import cy.jdkdigital.productivefarming.recipe.CropMutationRecipe;
import cy.jdkdigital.productivefarming.registry.FarmingDataComponents;
import cy.jdkdigital.productivefarming.util.TraitsHelper;
import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.block.entity.machine.HydroponicBlockEntity;
import dev.ftb.mods.ftbic.recipe.FTBICRecipes;
import dev.ftb.mods.ftbic.recipe.MachineRecipe;
import dev.ftb.mods.ftbic.recipe.SoilOption;
import dev.ftb.mods.ftbic.util.IngredientWithCount;
import dev.ftb.mods.ftbic.util.StackWithChance;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.TestEnvironmentDefinition;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.event.RegisterGameTestsEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

final class HydroponicCropTraitGameTests {
    private static final BlockPos POS = new BlockPos(2, 2, 2);

    static void register(RegisterGameTestsEvent event, Holder<TestEnvironmentDefinition<?>> env) {
        FTBICGameTests.reg(
                event, "hydroponic_traits_growth", HydroponicCropTraitGameTests::growthShortensCycle, env, 60);
        FTBICGameTests.reg(event, "hydroponic_traits_yield", HydroponicCropTraitGameTests::yieldAndSeedTraits, env, 60);
        FTBICGameTests.reg(
                event, "hydroponic_traits_increase", HydroponicCropTraitGameTests::harvestsRaiseTraits, env, 60);
        FTBICGameTests.reg(
                event, "hydroponic_traits_mutant", HydroponicCropTraitGameTests::mutantInheritsTraits, env, 60);
        FTBICGameTests.reg(
                event,
                "hydroponic_traits_pf_mutation",
                HydroponicCropTraitGameTests::productiveFarmingMutation,
                env,
                60);
    }

    private static HydroponicBlockEntity place(GameTestHelper h, boolean advanced) {
        h.setBlock(
                POS,
                (advanced
                                ? FTBICElectricBlocks.ADVANCED_HYDROPONIC_ACCELERATOR
                                : FTBICElectricBlocks.HYDROPONIC_ACCELERATOR)
                        .block.get());
        HydroponicBlockEntity machine = h.getBlockEntity(POS, HydroponicBlockEntity.class);
        machine.energy = machine.getEnergyCapacity();
        machine.setInputFluid(new FluidStack(Fluids.WATER, 16_000));
        return machine;
    }

    private static StackWithChance output(Item item, int count, double chance) {
        return new StackWithChance(new ItemStackTemplate(item, count), chance);
    }

    private static MachineRecipe growth(Item seed, Item product, double time) {
        return new MachineRecipe(
                FTBICRecipes.HYDROPONIC_GROWTH,
                List.of(new IngredientWithCount(Ingredient.of(seed), 1)),
                List.of(SizedFluidIngredient.of(Fluids.WATER, 1)),
                List.of(output(product, 2, 1), output(seed, 2, 1)),
                List.of(),
                time,
                false,
                List.of(new SoilOption(Ingredient.of(Items.DIRT), 1)));
    }

    private static MachineRecipe mutation(double chance) {
        return new MachineRecipe(
                FTBICRecipes.HYDROPONIC_MUTATION,
                List.of(
                        new IngredientWithCount(Ingredient.of(Items.WHEAT_SEEDS), 1),
                        new IngredientWithCount(Ingredient.of(Items.BEETROOT_SEEDS), 1)),
                List.of(SizedFluidIngredient.of(Fluids.WATER, 1)),
                List.of(output(Items.MELON_SEEDS, 1, chance)),
                List.of(),
                0.001,
                false);
    }

    private static ItemStack seed(Item item, int count, int growth, int yield, int resistance, int mutability) {
        return TraitsHelper.applyTraits(new ItemStack(item, count), growth, yield, resistance, mutability);
    }

    static void growthShortensCycle(GameTestHelper h) {
        HydroponicBlockEntity machine = place(h, false);
        CentrifugeFluidGameTests.withRecipes(h, List.of(growth(Items.WHEAT_SEEDS, Items.WHEAT, 0.1)), () -> {
            machine.setStackInSlot(1, new ItemStack(Items.DIRT));
            machine.setStackInSlot(0, new ItemStack(Items.WHEAT_SEEDS));
            machine.tick();
            h.assertValueEqual(20, machine.getDuration(0), "A seed with no traits uses the base time");
            machine.setStackInSlot(0, seed(Items.WHEAT_SEEDS, 1, 3, 0, 0, 0));
            machine.tick();
            h.assertValueEqual(5, machine.getDuration(0), "Growth 3 grows in a quarter of the time");
        });
        h.succeed();
    }

    static void yieldAndSeedTraits(GameTestHelper h) {
        HydroponicBlockEntity machine = place(h, false);
        CentrifugeFluidGameTests.withRecipes(h, List.of(growth(Items.WHEAT_SEEDS, Items.WHEAT, 0.001)), () -> {
            machine.setStackInSlot(1, new ItemStack(Items.DIRT));
            machine.setStackInSlot(0, seed(Items.WHEAT_SEEDS, 1, 0, 2, 0, 0));
            machine.tick();
            h.assertValueEqual(4, machine.outputItems[0].getCount(), "Yield 2 adds two to the produce");
            h.assertValueEqual(1, machine.inputItems[0].getCount(), "A seed with traits is still returned to its slot");
            ItemStack returned = machine.outputItems[1];
            h.assertValueEqual(1, returned.getCount(), "Seed outputs are not raised by Yield");
            h.assertValueEqual(
                    2, returned.getOrDefault(FarmingDataComponents.YIELD, 0), "Returned seeds keep their traits");
        });
        h.succeed();
    }

    static void harvestsRaiseTraits(GameTestHelper h) {
        HydroponicBlockEntity machine = place(h, false);
        CentrifugeFluidGameTests.withRecipes(h, List.of(growth(Items.WHEAT_SEEDS, Items.WHEAT, 0.001)), () -> {
            machine.setStackInSlot(1, new ItemStack(Items.DIRT));
            machine.setStackInSlot(0, new ItemStack(Items.WHEAT_SEEDS));
            for (int i = 0; i < 400; i++) {
                machine.outputItems[0] = ItemStack.EMPTY;
                machine.outputItems[1] = ItemStack.EMPTY;
                machine.energy = machine.getEnergyCapacity();
                machine.tick();
            }
            ItemStack planted = machine.inputItems[0];
            int total = planted.getOrDefault(FarmingDataComponents.GROWTH, 0)
                    + planted.getOrDefault(FarmingDataComponents.YIELD, 0)
                    + planted.getOrDefault(FarmingDataComponents.RESISTANCE, 0)
                    + planted.getOrDefault(FarmingDataComponents.MUTABILITY, 0);
            h.assertTrue(total > 0, "Repeated harvests raise a trait on the planted seed");
            h.assertValueEqual(1, planted.getCount(), "The planted seed stays in its slot");
        });
        h.succeed();
    }

    static void mutantInheritsTraits(GameTestHelper h) {
        HydroponicBlockEntity machine = place(h, true);
        CentrifugeFluidGameTests.withRecipes(
                h,
                List.of(
                        growth(Items.WHEAT_SEEDS, Items.WHEAT, 0.001),
                        growth(Items.BEETROOT_SEEDS, Items.BEETROOT, 0.001),
                        mutation(1)),
                () -> {
                    machine.setMutationMode(true);
                    machine.setStackInSlot(0, seed(Items.WHEAT_SEEDS, 1, 1, 0, 0, 1));
                    machine.setStackInSlot(1, new ItemStack(Items.DIRT));
                    machine.setStackInSlot(2, seed(Items.BEETROOT_SEEDS, 1, 2, 3, 0, 4));
                    machine.setStackInSlot(3, new ItemStack(Items.DIRT));
                    machine.tick();
                    ItemStack mutant = machine.outputItems[0];
                    h.assertTrue(mutant.is(Items.MELON_SEEDS), "The mutation succeeds at full chance");
                    h.assertValueEqual(
                            4,
                            mutant.getOrDefault(FarmingDataComponents.MUTABILITY, 0),
                            "The mutant takes the traits of the parent with the higher Mutability");
                    h.assertValueEqual(3, mutant.getOrDefault(FarmingDataComponents.YIELD, 0), "Yield is inherited");
                });
        h.succeed();
    }

    static void productiveFarmingMutation(GameTestHelper h) {
        HydroponicBlockEntity machine = place(h, true);
        CropMutationRecipe pumpkin = new CropMutationRecipe(
                Identifier.withDefaultNamespace("wheat"),
                Identifier.withDefaultNamespace("beetroots"),
                Identifier.withDefaultNamespace("pumpkin"),
                1F);
        CentrifugeFluidGameTests.withRecipes(
                h,
                List.of(
                        growth(Items.WHEAT_SEEDS, Items.WHEAT, 0.001),
                        growth(Items.BEETROOT_SEEDS, Items.BEETROOT, 0.001),
                        pumpkin),
                () -> {
                    machine.setMutationMode(true);
                    machine.setStackInSlot(0, new ItemStack(Items.BEETROOT_SEEDS));
                    machine.setStackInSlot(1, new ItemStack(Items.DIRT));
                    machine.setStackInSlot(2, new ItemStack(Items.WHEAT_SEEDS));
                    machine.setStackInSlot(3, new ItemStack(Items.DIRT));
                    machine.tick();
                    h.assertTrue(
                            machine.outputItems[0].is(Items.PUMPKIN_SEEDS),
                            "A Productive Farming crop mutation runs in either slot order");
                });
        h.succeed();
    }
}
