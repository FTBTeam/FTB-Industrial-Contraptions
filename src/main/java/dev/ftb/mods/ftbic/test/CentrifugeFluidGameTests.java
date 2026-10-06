package dev.ftb.mods.ftbic.test;

import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.block.entity.machine.CentrifugeBlockEntity;
import dev.ftb.mods.ftbic.recipe.FTBICRecipes;
import dev.ftb.mods.ftbic.recipe.MachineRecipe;
import dev.ftb.mods.ftbic.screen.MachineMenu;
import dev.ftb.mods.ftbic.util.IngredientWithCount;
import dev.ftb.mods.ftbic.util.SideConfiguration.Face;
import dev.ftb.mods.ftbic.util.SideConfiguration.Mode;
import dev.ftb.mods.ftbic.util.SideConfiguration.Resource;
import dev.ftb.mods.ftbic.util.StackWithChance;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeMap;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidUtil;
import net.neoforged.neoforge.transfer.transaction.Transaction;

final class CentrifugeFluidGameTests {
    private static final BlockPos POS = new BlockPos(2, 2, 2);

    private static CentrifugeBlockEntity place(GameTestHelper h, boolean advanced) {
        h.setBlock(
                POS, (advanced ? FTBICElectricBlocks.ADVANCED_CENTRIFUGE : FTBICElectricBlocks.CENTRIFUGE).block.get());
        var machine = h.getBlockEntity(POS, CentrifugeBlockEntity.class);
        machine.energy = machine.getEnergyCapacity();
        return machine;
    }

    private static StackWithChance output(Item item) {
        ItemStack stack = new ItemStack(item);
        return new StackWithChance(new ItemStackTemplate(stack.typeHolder(), 1, stack.getComponentsPatch()), 1);
    }

    private static MachineRecipe recipe(
            boolean itemInput, boolean fluidInput, List<StackWithChance> outputs, FluidStack fluidOutput) {
        return new MachineRecipe(
                FTBICRecipes.SEPARATING,
                itemInput ? List.of(new IngredientWithCount(Ingredient.of(Items.CLAY_BALL), 2)) : List.of(),
                fluidInput ? List.of(SizedFluidIngredient.of(Fluids.WATER, 1000)) : List.of(),
                outputs,
                fluidOutput.isEmpty() ? List.of() : List.of(fluidOutput),
                0.001,
                true);
    }

    /** Install a fixture only during this synchronous test; restore before any world tick or other test. */
    static void withRecipes(GameTestHelper h, List<? extends Recipe<?>> recipes, Runnable action) {
        try {
            RecipeManager manager = h.getLevel().getServer().getRecipeManager();
            Field field = RecipeManager.class.getDeclaredField("recipes");
            field.setAccessible(true);
            Object original = field.get(manager);
            var holders = new ArrayList<RecipeHolder<?>>();
            for (int i = 0; i < recipes.size(); i++) {
                holders.add(new RecipeHolder<>(
                        ResourceKey.create(Registries.RECIPE, FTBIC.id("test/centrifuge_" + i)), recipes.get(i)));
            }
            try {
                field.set(manager, RecipeMap.create(holders));
                action.run();
            } finally {
                field.set(manager, original);
            }
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Could not install centrifuge recipe fixture", e);
        }
    }

    static void processing(GameTestHelper h) {
        var machine = place(h, true);
        var recipe = recipe(
                true,
                true,
                List.of(output(Items.FLINT), output(Items.IRON_NUGGET), output(Items.GOLD_NUGGET)),
                new FluidStack(Fluids.LAVA, 500));
        withRecipes(h, List.of(recipe), () -> {
            machine.setStackInSlot(0, new ItemStack(Items.CLAY_BALL, 4));
            machine.setFluids(new FluidStack(Fluids.WATER, 999), FluidStack.EMPTY);
            double energy = machine.energy;
            machine.tick();
            h.assertTrue(
                    machine.energy == energy && machine.inputItems[0].getCount() == 4,
                    "Insufficient fluid consumes nothing");
            try (var tx = Transaction.openRoot()) {
                machine.fluidHandler.insert(0, FluidResource.of(Fluids.WATER), 1001, tx);
                tx.commit();
            }
            machine.tick();
            h.assertValueEqual(2, machine.inputItems[0].getCount(), "Exact item input consumed");
            h.assertValueEqual(1000, machine.getInputFluid().getAmount(), "Exact fluid input consumed");
            h.assertValueEqual(500, machine.getOutputFluid().getAmount(), "Fluid output produced");
            h.assertTrue(
                    machine.outputItems[0].is(Items.FLINT)
                            && machine.outputItems[1].is(Items.IRON_NUGGET)
                            && machine.outputItems[2].is(Items.GOLD_NUGGET),
                    "All three distinct outputs retained");
        });
        var basic = place(h, false);
        withRecipes(h, List.of(recipe), () -> {
            basic.setStackInSlot(0, new ItemStack(Items.CLAY_BALL, 2));
            basic.setFluids(new FluidStack(Fluids.WATER, 1000), FluidStack.EMPTY);
            basic.tick();
            h.assertValueEqual(1000, basic.getInputFluid().getAmount(), "Basic machine rejects three-output recipes");
        });
        h.succeed();
    }

    static void outputBlocking(GameTestHelper h) {
        var machine = place(h, false);
        var recipe = recipe(true, true, List.of(output(Items.FLINT)), new FluidStack(Fluids.LAVA, 500));
        withRecipes(h, List.of(recipe), () -> {
            machine.setStackInSlot(0, new ItemStack(Items.CLAY_BALL, 2));
            machine.setFluids(new FluidStack(Fluids.WATER, 1000), new FluidStack(Fluids.LAVA, 15_501));
            double energy = machine.energy;
            machine.tick();
            h.assertTrue(
                    machine.energy == energy
                            && machine.inputItems[0].getCount() == 2
                            && machine.getInputFluid().getAmount() == 1000,
                    "Full fluid output stops energy and both inputs");
            machine.setFluids(machine.getInputFluid(), new FluidStack(Fluids.WATER, 1));
            machine.tick();
            h.assertTrue(machine.energy == energy, "Different output fluid blocks processing");
            machine.setFluids(machine.getInputFluid(), FluidStack.EMPTY);
            machine.outputItems[0] = new ItemStack(Items.FLINT, 64);
            machine.outputItems[1] = new ItemStack(Items.FLINT, 64);
            machine.tick();
            h.assertTrue(
                    machine.getOutputFluid().isEmpty()
                            && machine.getInputFluid().getAmount() == 1000,
                    "Full item output cannot consume or produce fluids");
            machine.outputItems[1] = ItemStack.EMPTY;
            machine.tick();
            h.assertValueEqual(500, machine.getOutputFluid().getAmount(), "Resumes after output space is freed");
        });
        h.succeed();
    }

    static void fluidOnlyAndCache(GameTestHelper h) {
        var machine = place(h, false);
        var fluidOnly = recipe(false, true, List.of(), new FluidStack(Fluids.LAVA, 500));
        withRecipes(h, List.of(fluidOnly), () -> {
            machine.tick();
            try (var tx = Transaction.openRoot()) {
                machine.fluidHandler.insert(0, FluidResource.of(Fluids.WATER), 1000, tx);
                tx.commit();
            }
            machine.tick();
            h.assertValueEqual(500, machine.getOutputFluid().getAmount(), "Fluid-only recipe wakes an idle machine");
        });
        var itemOnly = recipe(true, false, List.of(output(Items.FLINT)), FluidStack.EMPTY);
        var mixed = recipe(true, true, List.of(output(Items.GOLD_NUGGET)), FluidStack.EMPTY);
        withRecipes(h, List.of(itemOnly, mixed), () -> {
            machine.setStackInSlot(0, new ItemStack(Items.CLAY_BALL, 4));
            machine.setFluids(FluidStack.EMPTY, FluidStack.EMPTY);
            machine.tick();
            h.assertTrue(machine.outputItems[0].is(Items.FLINT), "Existing item-only recipes still work");
            machine.setFluids(new FluidStack(Fluids.WATER, 1000), FluidStack.EMPTY);
            machine.tick();
            h.assertTrue(
                    machine.outputItems[1].is(Items.GOLD_NUGGET),
                    "Mixed recipe takes priority after fluid input changes");
        });
        h.succeed();
    }

    static void automationAndPersistence(GameTestHelper h) {
        var machine = place(h, true);
        var handler = h.getLevel().getCapability(Capabilities.Fluid.BLOCK, machine.getBlockPos(), Direction.UP);
        var lava = FluidResource.of(Fluids.LAVA);
        try (var tx = Transaction.openRoot()) {
            handler.insert(lava, 1000, tx);
        }
        h.assertTrue(machine.getInputFluid().isEmpty(), "Aborted insertion rolls back");
        try (var tx = Transaction.openRoot()) {
            h.assertValueEqual(16_000, handler.insert(lava, 20_000, tx), "Input capacity enforced");
            h.assertValueEqual(0, handler.insert(1, lava, 1, tx), "Cannot insert into output tank");
            h.assertValueEqual(0, handler.extract(0, lava, 1, tx), "Cannot extract from input tank");
            tx.commit();
        }
        machine.setSideConfiguration(machine.getSideConfiguration().with(Resource.FLUIDS, Face.TOP, Mode.DISABLED));
        machine.setFluids(FluidStack.EMPTY, new FluidStack(Fluids.LAVA, 1000));
        try (var tx = Transaction.openRoot()) {
            h.assertValueEqual(0, handler.insert(lava, 1, tx), "Cached capability respects disabled side");
            h.assertValueEqual(0, handler.extract(lava, 1, tx), "Disabled side blocks extraction");
        }
        machine.setSideConfiguration(machine.getSideConfiguration().with(Resource.FLUIDS, Face.TOP, Mode.BOTH));
        try (var tx = Transaction.openRoot()) {
            handler.extract(lava, 300, tx);
        }
        h.assertValueEqual(1000, machine.getOutputFluid().getAmount(), "Aborted extraction rolls back");
        try (var tx = Transaction.openRoot()) {
            handler.extract(lava, 300, tx);
            tx.commit();
        }
        h.assertValueEqual(700, machine.getOutputFluid().getAmount(), "Committed output extraction works");
        FluidStack named = new FluidStack(Fluids.WATER, 2300);
        named.set(DataComponents.CUSTOM_NAME, Component.literal("Test fluid"));
        machine.setFluids(named, new FluidStack(Fluids.LAVA, 900));
        machine.outputItems[2] = new ItemStack(Items.GOLD_NUGGET, 7);
        var tag = machine.saveCustomOnly(h.getLevel().registryAccess());
        machine.setFluids(FluidStack.EMPTY, FluidStack.EMPTY);
        machine.loadCustomOnly(
                TagValueInput.create(ProblemReporter.DISCARDING, h.getLevel().registryAccess(), tag));
        h.assertTrue(
                FluidStack.isSameFluidSameComponents(named, machine.getInputFluid()),
                "Fluid components survive save/load");
        h.assertValueEqual(2300, machine.getInputFluid().getAmount(), "Input amount persists");
        h.assertValueEqual(900, machine.getOutputFluid().getAmount(), "Output amount persists");
        h.assertValueEqual(7, machine.outputItems[2].getCount(), "Third output persists");
        h.assertTrue(
                machine.getUpdateTag(h.getLevel().registryAccess()).contains("InputFluid"),
                "Client updates include tanks");
        h.succeed();
    }

    static void blockInteraction(GameTestHelper h) {
        var player = h.makeMockPlayer(GameType.SURVIVAL);
        for (boolean advanced : new boolean[] {false, true}) {
            var machine = place(h, advanced);
            var state = machine.getBlockState();
            var hit = new BlockHitResult(
                    Vec3.atCenterOf(machine.getBlockPos()), Direction.UP, machine.getBlockPos(), false);
            for (InteractionHand hand : InteractionHand.values()) {
                player.setItemInHand(hand, ItemStack.EMPTY);
                h.assertTrue(
                        state.useItemOn(ItemStack.EMPTY, h.getLevel(), player, hand, hit)
                                == InteractionResult.TRY_WITH_EMPTY_HAND,
                        "An empty hand falls through to opening the centrifuge UI");
            }
            player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.STONE));
            h.assertTrue(
                    state.useItemOn(player.getMainHandItem(), h.getLevel(), player, InteractionHand.MAIN_HAND, hit)
                            == InteractionResult.TRY_WITH_EMPTY_HAND,
                    "A non-fluid item also permits opening the UI");
            player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.LAVA_BUCKET));
            h.assertTrue(
                    state.useItemOn(player.getMainHandItem(), h.getLevel(), player, InteractionHand.MAIN_HAND, hit)
                            == InteractionResult.SUCCESS,
                    "A fluid container is handled by the block interaction");
            h.assertValueEqual(
                    1000, machine.getInputFluid().getAmount(), "Bucket interaction still fills the input tank");
        }
        h.succeed();
    }

    static void containersAndSlots(GameTestHelper h) {
        var machine = place(h, true);
        var player = h.makeMockPlayer(GameType.SURVIVAL);
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.LAVA_BUCKET));
        h.assertTrue(
                FluidUtil.interactWithFluidHandler(
                        player, InteractionHand.MAIN_HAND, h.getLevel(), machine.getBlockPos(), Direction.UP),
                "Lava bucket fills input");
        h.assertValueEqual(1000, machine.getInputFluid().getAmount(), "Bucket transfers exact amount");
        machine.setFluids(machine.getInputFluid(), new FluidStack(Fluids.LAVA, 1000));
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.BUCKET));
        h.assertTrue(
                FluidUtil.interactWithFluidHandler(
                        player, InteractionHand.MAIN_HAND, h.getLevel(), machine.getBlockPos(), Direction.UP),
                "Empty bucket drains output");
        h.assertTrue(machine.getOutputFluid().isEmpty(), "Bucket removes output fluid");
        var menu = new MachineMenu(1, player.getInventory(), machine);
        h.assertValueEqual(
                45,
                menu.slots.size(),
                "Advanced menu exposes input, three outputs, battery, four upgrades, and player slots");
        for (int i = 1; i <= 3; i++)
            h.assertFalse(
                    menu.slots.get(i).mayPlace(new ItemStack(Items.STONE)), "All output slots reject manual insertion");
        h.succeed();
    }
}
