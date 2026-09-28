package dev.ftb.mods.ftbic.test;

import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.block.entity.machine.CentrifugeBlockEntity;
import dev.ftb.mods.ftbic.block.entity.machine.HydroponicBlockEntity;
import dev.ftb.mods.ftbic.block.entity.machine.MachineBlockEntity;
import dev.ftb.mods.ftbic.block.entity.machine.PoweredCraftingTableBlockEntity;
import dev.ftb.mods.ftbic.recipe.FTBICRecipes;
import dev.ftb.mods.ftbic.recipe.MachineRecipe;
import dev.ftb.mods.ftbic.recipe.SoilOption;
import dev.ftb.mods.ftbic.screen.ElectricBlockEntityContainer;
import dev.ftb.mods.ftbic.screen.MachineMenu;
import dev.ftb.mods.ftbic.util.IngredientWithCount;
import dev.ftb.mods.ftbic.util.StackWithChance;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidUtil;
import net.neoforged.neoforge.transfer.transaction.Transaction;

final class MachineFixGameTests {
    private static final BlockPos POS = new BlockPos(2, 2, 2);
    private static final BlockPos SECOND_POS = new BlockPos(6, 2, 2);

    private static StackWithChance output(Item item) {
        return new StackWithChance(new ItemStackTemplate(item, 1), 1);
    }

    static void craftingTableRemainders(GameTestHelper h) {
        h.setBlock(POS, FTBICElectricBlocks.POWERED_CRAFTING_TABLE.block.get());
        var table = h.getBlockEntity(POS, PoweredCraftingTableBlockEntity.class);
        table.energy = table.energyCapacity;
        for (int i = 0; i < 3; i++) table.inputItems[i] = new ItemStack(Items.MILK_BUCKET);
        table.inputItems[3] = new ItemStack(Items.SUGAR);
        table.inputItems[4] = new ItemStack(Items.EGG);
        table.inputItems[5] = new ItemStack(Items.SUGAR);
        for (int i = 6; i < 9; i++) table.inputItems[i] = new ItemStack(Items.WHEAT);
        table.tick();
        h.assertTrue(table.outputItems[0].is(Items.CAKE) && table.outputItems[0].getCount() == 1, "Cake is crafted");
        for (int i = 0; i < 3; i++) {
            h.assertTrue(
                    table.inputItems[i].is(Items.BUCKET) && table.inputItems[i].getCount() == 1,
                    "Each milk bucket leaves an empty bucket in its slot");
        }
        for (int i = 3; i < 9; i++) h.assertTrue(table.inputItems[i].isEmpty(), "Plain ingredients are consumed");
        table.tick();
        h.assertValueEqual(1, table.outputItems[0].getCount(), "Returned buckets do not craft again");

        for (int i = 0; i < 9; i++) table.inputItems[i] = ItemStack.EMPTY;
        table.outputItems[0] = ItemStack.EMPTY;
        table.inputItems[4] = new ItemStack(Items.HONEY_BOTTLE, 2);
        table.tick();
        h.assertTrue(table.outputItems[0].isEmpty(), "No craft when the remainder has nowhere to go");
        h.assertValueEqual(2, table.inputItems[4].getCount(), "A blocked craft keeps its inputs");
        table.inputItems[4] = new ItemStack(Items.HONEY_BOTTLE);
        table.tick();
        h.assertTrue(
                table.outputItems[0].is(Items.SUGAR) && table.outputItems[0].getCount() == 3,
                "Honey bottle crafts sugar");
        h.assertTrue(
                table.inputItems[4].is(Items.GLASS_BOTTLE), "The glass bottle returns to the slot the honey came from");
        h.succeed();
    }

    static void toppedUpInputRestartsMachine(GameTestHelper h) {
        h.setBlock(POS, FTBICElectricBlocks.MACERATOR.block.get());
        var machine = h.getBlockEntity(POS, MachineBlockEntity.class);
        machine.energy = machine.getEnergyCapacity();
        var player = h.makeMockPlayer(GameType.SURVIVAL);
        var menu = new MachineMenu(1, player.getInventory(), machine);
        var recipe = new MachineRecipe(
                FTBICRecipes.MACERATING,
                List.of(new IngredientWithCount(Ingredient.of(Items.BLAZE_POWDER), 4)),
                List.of(),
                List.of(output(Items.BLAZE_ROD)),
                List.of(),
                0.001,
                true);
        CentrifugeFluidGameTests.withRecipes(h, List.of(recipe), () -> {
            machine.setStackInSlot(0, new ItemStack(Items.BLAZE_POWDER, 2));
            machine.tick();
            h.assertTrue(machine.outputItems[0].isEmpty(), "Two powder cannot start a four powder recipe");
            machine.inputItems[0].grow(2);
            new ElectricBlockEntityContainer(machine).setChanged();
            machine.tick();
            h.assertTrue(machine.outputItems[0].is(Items.BLAZE_ROD), "An in-place top-up restarts the idle machine");
            h.assertTrue(machine.inputItems[0].isEmpty(), "The topped-up input is consumed");

            machine.setStackInSlot(0, new ItemStack(Items.BLAZE_POWDER, 2));
            machine.tick();
            player.getInventory().setItem(0, new ItemStack(Items.BLAZE_POWDER, 2));
            int playerSlot = -1;
            for (Slot slot : menu.slots) {
                if (slot.container == player.getInventory() && slot.getContainerSlot() == 0) playerSlot = slot.index;
            }
            h.assertTrue(playerSlot >= 0, "The menu exposes the player's first inventory slot");
            menu.quickMoveStack(player, playerSlot);
            h.assertValueEqual(4, machine.inputItems[0].getCount(), "Shift-click merges into the existing stack");
            machine.tick();
            h.assertValueEqual(2, machine.outputItems[0].getCount(), "A shift-click top-up restarts the idle machine");
        });
        h.succeed();
    }

    static void tanksRejectUnusedFluids(GameTestHelper h) {
        var water = FluidResource.of(Fluids.WATER);
        var lava = FluidResource.of(Fluids.LAVA);

        h.setBlock(POS, FTBICElectricBlocks.CENTRIFUGE.block.get());
        var centrifuge = h.getBlockEntity(POS, CentrifugeBlockEntity.class);
        var handler = h.getLevel().getCapability(Capabilities.Fluid.BLOCK, centrifuge.getBlockPos(), Direction.UP);
        var separating = new MachineRecipe(
                FTBICRecipes.SEPARATING,
                List.of(),
                List.of(SizedFluidIngredient.of(Fluids.LAVA, 1000)),
                List.of(output(Items.FLINT)),
                List.of(),
                0.001,
                true);
        CentrifugeFluidGameTests.withRecipes(h, List.of(separating), () -> {
            h.assertFalse(handler.isValid(0, water), "A fluid no recipe uses is not valid");
            h.assertTrue(handler.isValid(0, lava), "A recipe input fluid is valid");
            try (var tx = Transaction.openRoot()) {
                h.assertValueEqual(0, handler.insert(water, 1000, tx), "Pipes cannot insert a fluid no recipe uses");
                h.assertValueEqual(1000, handler.insert(lava, 1000, tx), "Pipes can insert a recipe input fluid");
                tx.commit();
            }
            try (var tx = Transaction.openRoot()) {
                h.assertValueEqual(0, handler.extract(lava, 1000, tx), "A usable input fluid stays in the machine");
            }

            centrifuge.setFluids(new FluidStack(Fluids.WATER, 3000), FluidStack.EMPTY);
            try (var tx = Transaction.openRoot()) {
                h.assertValueEqual(1000, handler.extract(water, 1000, tx), "A stuck fluid can be drained by pipes");
                tx.commit();
            }
            h.assertValueEqual(2000, centrifuge.getInputFluid().getAmount(), "The pipe drain removes the stuck fluid");
            var player = h.makeMockPlayer(GameType.SURVIVAL);
            player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.BUCKET));
            h.assertTrue(
                    FluidUtil.interactWithFluidHandler(
                            player, InteractionHand.MAIN_HAND, h.getLevel(), centrifuge.getBlockPos(), Direction.UP),
                    "An empty bucket drains a stuck fluid");
            h.assertValueEqual(
                    1000, centrifuge.getInputFluid().getAmount(), "The bucket removes one bucket of the stuck fluid");
            centrifuge.setFluids(FluidStack.EMPTY, FluidStack.EMPTY);
            player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.WATER_BUCKET));
            h.assertFalse(
                    FluidUtil.interactWithFluidHandler(
                            player, InteractionHand.MAIN_HAND, h.getLevel(), centrifuge.getBlockPos(), Direction.UP),
                    "A bucket of an unused fluid is refused");
            h.assertTrue(centrifuge.getInputFluid().isEmpty(), "The refused bucket leaves the tank empty");
        });

        h.setBlock(SECOND_POS, FTBICElectricBlocks.HYDROPONIC_ACCELERATOR.block.get());
        var hydroponic = h.getBlockEntity(SECOND_POS, HydroponicBlockEntity.class);
        var hydroHandler = h.getLevel().getCapability(Capabilities.Fluid.BLOCK, hydroponic.getBlockPos(), Direction.UP);
        var growth = new MachineRecipe(
                FTBICRecipes.HYDROPONIC_GROWTH,
                List.of(new IngredientWithCount(Ingredient.of(Items.WHEAT_SEEDS), 1)),
                List.of(SizedFluidIngredient.of(Fluids.WATER, 250)),
                List.of(output(Items.WHEAT), output(Items.WHEAT_SEEDS)),
                List.of(),
                1,
                false,
                List.of(new SoilOption(Ingredient.of(Items.DIRT), 1)));
        CentrifugeFluidGameTests.withRecipes(h, List.of(growth), () -> {
            try (var tx = Transaction.openRoot()) {
                h.assertValueEqual(0, hydroHandler.insert(lava, 1000, tx), "Hydroponics refuse a fluid no recipe uses");
                h.assertValueEqual(1000, hydroHandler.insert(water, 1000, tx), "Hydroponics accept recipe water");
                h.assertValueEqual(0, hydroHandler.extract(water, 1000, tx), "Usable water cannot be pulled back out");
                tx.commit();
            }
            h.assertValueEqual(1000, hydroponic.getInputFluid().getAmount(), "Accepted water is stored");
            hydroponic.setInputFluid(new FluidStack(Fluids.LAVA, 2000));
            try (var tx = Transaction.openRoot()) {
                h.assertValueEqual(
                        2000, hydroHandler.extract(lava, 2000, tx), "A stuck hydroponic fluid can be drained");
                tx.commit();
            }
            h.assertTrue(hydroponic.getInputFluid().isEmpty(), "The stuck hydroponic fluid is gone");
        });
        h.succeed();
    }
}
