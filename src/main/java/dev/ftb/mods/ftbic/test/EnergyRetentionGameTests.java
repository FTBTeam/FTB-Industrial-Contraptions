package dev.ftb.mods.ftbic.test;

import dev.ftb.mods.ftbic.block.ElectricBlockInstance;
import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.block.entity.storage.BankCellBlockEntity;
import dev.ftb.mods.ftbic.block.entity.storage.BankTopology;
import dev.ftb.mods.ftbic.block.entity.storage.EVBatteryBoxBlockEntity;
import dev.ftb.mods.ftbic.registry.ModDataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

final class EnergyRetentionGameTests {
    private static final BlockPos POS = new BlockPos(1, 2, 1);
    private static final BlockPos CELL = POS.east();

    static void batteryBoxDropKeepsEnergy(GameTestHelper helper) {
        helper.setBlock(POS, FTBICElectricBlocks.EV_BATTERY_BOX.block.get());
        EVBatteryBoxBlockEntity box = helper.getBlockEntity(POS, EVBatteryBoxBlockEntity.class);
        double stored = box.getEnergyCapacity() / 2D;
        box.setEnergyRaw(stored);
        ItemStack charged = drop(helper, POS, FTBICElectricBlocks.EV_BATTERY_BOX);
        helper.assertValueEqual(stored, charged.get(ModDataComponents.ENERGY.get()), "Charged battery box drop energy");

        helper.setBlock(POS, Blocks.AIR);
        helper.setBlock(POS, FTBICElectricBlocks.EV_BATTERY_BOX.block.get());
        ItemStack empty = drop(helper, POS, FTBICElectricBlocks.EV_BATTERY_BOX);
        helper.assertFalse(
                empty.has(ModDataComponents.ENERGY.get()), "An empty battery box drops without stored energy");
        helper.assertTrue(
                ItemStack.isSameItemSameComponents(empty, new ItemStack(FTBICElectricBlocks.EV_BATTERY_BOX.item.get())),
                "An empty battery box drop stacks with a fresh one");
        helper.succeed();
    }

    static void batteryBoxPlacementRestoresEnergy(GameTestHelper helper) {
        helper.setBlock(POS, FTBICElectricBlocks.EV_BATTERY_BOX.block.get());
        EVBatteryBoxBlockEntity box = helper.getBlockEntity(POS, EVBatteryBoxBlockEntity.class);
        double capacity = box.getEnergyCapacity();
        double stored = capacity / 2D;
        box.setEnergyRaw(stored);
        ItemStack charged = drop(helper, POS, FTBICElectricBlocks.EV_BATTERY_BOX);
        helper.setBlock(POS, Blocks.AIR);

        place(helper, POS, FTBICElectricBlocks.EV_BATTERY_BOX, charged);
        EVBatteryBoxBlockEntity placed = helper.getBlockEntity(POS, EVBatteryBoxBlockEntity.class);
        helper.assertValueEqual(stored, placed.getEnergy(), "Placed battery box energy");
        helper.assertValueEqual(capacity, placed.getEnergyCapacity(), "Placed battery box capacity");

        helper.setBlock(POS, Blocks.AIR);
        ItemStack overfilled = new ItemStack(FTBICElectricBlocks.EV_BATTERY_BOX.item.get());
        overfilled.set(ModDataComponents.ENERGY.get(), capacity * 2D);
        place(helper, POS, FTBICElectricBlocks.EV_BATTERY_BOX, overfilled);
        helper.assertValueEqual(
                capacity,
                helper.getBlockEntity(POS, EVBatteryBoxBlockEntity.class).getEnergy(),
                "Placed battery box energy clamped to capacity");
        helper.succeed();
    }

    static void bankCellRoundTrip(GameTestHelper helper) {
        helper.setBlock(POS, FTBICElectricBlocks.INDUSTRIAL_BANK_PORT.block.get());
        helper.setBlock(CELL, FTBICElectricBlocks.INDUSTRIAL_BANK_CELL.block.get());
        BankCellBlockEntity cell = helper.getBlockEntity(CELL, BankCellBlockEntity.class);
        double stored = cell.getEnergyCapacity() / 4D;
        cell.setEnergyRaw(stored);
        ItemStack charged = drop(helper, CELL, FTBICElectricBlocks.INDUSTRIAL_BANK_CELL);
        helper.assertValueEqual(stored, charged.get(ModDataComponents.ENERGY.get()), "Charged bank cell drop energy");

        helper.setBlock(CELL, Blocks.AIR);
        BlockPos port = helper.absolutePos(POS);
        helper.assertValueEqual(
                0D, BankTopology.snapshot(helper.getLevel(), port).stored(), "Bank energy after removing the cell");

        place(helper, CELL, FTBICElectricBlocks.INDUSTRIAL_BANK_CELL, charged);
        BankCellBlockEntity placed = helper.getBlockEntity(CELL, BankCellBlockEntity.class);
        helper.assertValueEqual(stored, placed.getEnergy(), "Placed bank cell energy");
        BankTopology.Snapshot snapshot = BankTopology.snapshot(helper.getLevel(), port);
        helper.assertValueEqual(1, snapshot.cells(), "Placed bank cell joins the bank");
        helper.assertValueEqual(stored, snapshot.stored(), "Bank energy after placing the charged cell");

        helper.setBlock(CELL, Blocks.AIR);
        helper.setBlock(CELL, FTBICElectricBlocks.INDUSTRIAL_BANK_CELL.block.get());
        ItemStack empty = drop(helper, CELL, FTBICElectricBlocks.INDUSTRIAL_BANK_CELL);
        helper.assertFalse(empty.has(ModDataComponents.ENERGY.get()), "An empty bank cell drops without stored energy");
        helper.succeed();
    }

    private static ItemStack drop(GameTestHelper helper, BlockPos pos, ElectricBlockInstance instance) {
        BlockPos abs = helper.absolutePos(pos);
        ItemStack dropped = Block.getDrops(
                        helper.getLevel().getBlockState(abs),
                        helper.getLevel(),
                        abs,
                        helper.getLevel().getBlockEntity(abs))
                .stream()
                .filter(stack -> stack.is(instance.item.get()))
                .findFirst()
                .orElse(ItemStack.EMPTY);
        helper.assertFalse(dropped.isEmpty(), instance.id + " drops itself when broken");
        return dropped;
    }

    private static void place(GameTestHelper helper, BlockPos pos, ElectricBlockInstance instance, ItemStack stack) {
        BlockPos abs = helper.absolutePos(pos);
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        BlockHitResult hit = new BlockHitResult(Vec3.atCenterOf(abs), Direction.UP, abs, false);
        instance.item.get().place(new BlockPlaceContext(player, InteractionHand.MAIN_HAND, stack, hit));
        helper.assertTrue(helper.getBlockState(pos).is(instance.block.get()), instance.id + " is placed from its item");
    }

    private EnergyRetentionGameTests() {}
}
