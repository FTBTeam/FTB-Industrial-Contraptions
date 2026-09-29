package dev.ftb.mods.ftbic.test;

import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.block.BatteryBankBlock;
import dev.ftb.mods.ftbic.block.CableBlock;
import dev.ftb.mods.ftbic.block.FTBICBlocks;
import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.block.entity.storage.BankCellBlockEntity;
import dev.ftb.mods.ftbic.block.entity.storage.BankDisplayLayout;
import dev.ftb.mods.ftbic.block.entity.storage.BankDisplayLayout.Tile;
import dev.ftb.mods.ftbic.block.entity.storage.BankPortBlockEntity;
import dev.ftb.mods.ftbic.block.entity.storage.BankPortBlockEntity.FaceStyle;
import dev.ftb.mods.ftbic.block.entity.storage.BankTopology;
import dev.ftb.mods.ftbic.block.entity.storage.EVBatteryBoxBlockEntity;
import dev.ftb.mods.ftbic.util.SideConfiguration;
import dev.ftb.mods.ftbic.util.SideConfiguration.Face;
import dev.ftb.mods.ftbic.util.SideConfiguration.Mode;
import dev.ftb.mods.ftbic.util.SideConfiguration.Resource;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.block.Blocks;

final class BankGameTests {
    private static final BlockPos PORT = new BlockPos(3, 2, 3);
    private static final BlockPos WEST = PORT.west();
    private static final BlockPos EAST = PORT.east();

    static void cellSeamStates(GameTestHelper helper) {
        helper.setBlock(PORT, FTBICElectricBlocks.INDUSTRIAL_BANK_CELL.block.get());
        helper.setBlock(EAST, FTBICElectricBlocks.INDUSTRIAL_BANK_CELL.block.get());
        helper.assertTrue(
                helper.getBlockState(PORT).getValue(BatteryBankBlock.CONNECTION[Direction.EAST.ordinal()]),
                "left cell removes its east perimeter rail");
        helper.assertTrue(
                helper.getBlockState(EAST).getValue(BatteryBankBlock.CONNECTION[Direction.WEST.ordinal()]),
                "right cell removes its west perimeter rail");
        helper.assertFalse(
                helper.getBlockState(PORT).getValue(BatteryBankBlock.CONNECTION[Direction.WEST.ordinal()]),
                "left outside edge keeps its rail");
        helper.assertFalse(
                helper.getBlockState(EAST).getValue(BatteryBankBlock.CONNECTION[Direction.EAST.ordinal()]),
                "right outside edge keeps its rail");
        helper.setBlock(EAST, Blocks.AIR);
        helper.assertFalse(
                helper.getBlockState(PORT).getValue(BatteryBankBlock.CONNECTION[Direction.EAST.ordinal()]),
                "perimeter rail returns when a neighboring cell is removed");
        helper.succeed();
    }

    static void connectedStorage(GameTestHelper helper) {
        helper.setBlock(PORT, FTBICElectricBlocks.INDUSTRIAL_BANK_PORT.block.get());
        helper.setBlock(WEST, FTBICElectricBlocks.INDUSTRIAL_BANK_CELL.block.get());
        helper.setBlock(EAST, FTBICElectricBlocks.INDUSTRIAL_BANK_CELL.block.get());
        BankPortBlockEntity port = helper.getBlockEntity(PORT, BankPortBlockEntity.class);
        BankCellBlockEntity west = helper.getBlockEntity(WEST, BankCellBlockEntity.class);
        BankCellBlockEntity east = helper.getBlockEntity(EAST, BankCellBlockEntity.class);
        helper.assertValueEqual(
                FTBICConfig.ENERGY.BANK_CELL_CAPACITY.get(), west.getEnergyCapacity(), "config cell capacity");
        helper.assertValueEqual(
                FTBICConfig.ENERGY.BANK_PORT_TRANSFER.get(), port.getMaxInputEnergy(), "config port input");
        helper.assertValueEqual(
                2,
                BankTopology.cells(helper.getLevel(), helper.absolutePos(PORT)).size(),
                "connected cell count");
        helper.assertTrue(
                helper.getBlockState(PORT).getValue(BatteryBankBlock.CONNECTION[Direction.WEST.ordinal()]),
                "port connects to west cell");
        west.setEnergyRaw(1000D);
        port.handleGeneration();
        helper.assertValueEqual(
                1000D, port.getEnergy() + west.getEnergy() + east.getEnergy(), "no energy created or lost");
        BankTopology.Snapshot fromCell = BankTopology.snapshot(helper.getLevel(), helper.absolutePos(WEST));
        BankTopology.Snapshot fromPort = BankTopology.snapshot(helper.getLevel(), helper.absolutePos(PORT));
        helper.assertValueEqual(fromPort, fromCell, "cell and port show the same connected-bank totals");
        helper.assertValueEqual(1000D, fromCell.stored(), "connected-bank stored energy");
        helper.assertValueEqual(
                west.getEnergyCapacity() + east.getEnergyCapacity() + port.getEnergyCapacity(),
                fromCell.capacity(),
                "connected-bank capacity");
        helper.setBlock(EAST, Blocks.AIR);
        helper.assertValueEqual(
                1,
                BankTopology.cells(helper.getLevel(), helper.absolutePos(PORT)).size(),
                "split cell count");
        BankTopology.Snapshot afterSplit = BankTopology.snapshot(helper.getLevel(), helper.absolutePos(WEST));
        helper.assertValueEqual(
                west.getEnergyCapacity() + port.getEnergyCapacity(),
                afterSplit.capacity(),
                "Jade and screen totals exclude a disconnected cell");
        helper.assertTrue(
                !helper.getBlockState(PORT).getValue(BatteryBankBlock.CONNECTION[Direction.EAST.ordinal()]),
                "port disconnects visually after split");
        helper.assertValueEqual(1000D, port.getEnergy() + west.getEnergy(), "remaining bank energy");
        helper.succeed();
    }

    static void chargeDisplayTracksBank(GameTestHelper helper) {
        helper.setBlock(PORT, FTBICElectricBlocks.INDUSTRIAL_BANK_PORT.block.get());
        helper.setBlock(WEST, FTBICElectricBlocks.INDUSTRIAL_BANK_CELL.block.get());
        BankPortBlockEntity port = helper.getBlockEntity(PORT, BankPortBlockEntity.class);
        BankCellBlockEntity cell = helper.getBlockEntity(WEST, BankCellBlockEntity.class);
        helper.assertValueEqual(0, port.getDisplayCharge(), "empty bank display");
        port.setEnergyRaw(port.getEnergyCapacity() / 2D);
        cell.setEnergyRaw(cell.getEnergyCapacity() / 2D);
        port.refreshChargeDisplay();
        helper.assertValueEqual(500, port.getDisplayCharge(), "half-full bank display");
        port.setEnergyRaw(port.getEnergyCapacity());
        cell.setEnergyRaw(cell.getEnergyCapacity());
        port.refreshChargeDisplay();
        helper.assertValueEqual(1000, port.getDisplayCharge(), "full bank display");
        port.setEnergyRaw(0D);
        cell.setEnergyRaw(0D);
        port.refreshChargeDisplay();
        helper.assertValueEqual(0, port.getDisplayCharge(), "discharged bank display");
        helper.succeed();
    }

    static void sustainedCableCharging(GameTestHelper helper) {
        helper.setBlock(PORT, FTBICElectricBlocks.INDUSTRIAL_BANK_PORT.block.get());
        helper.setBlock(WEST, FTBICElectricBlocks.INDUSTRIAL_BANK_CELL.block.get());
        helper.setBlock(
                EAST,
                FTBICBlocks.EV_CABLE
                        .get()
                        .defaultBlockState()
                        .setValue(CableBlock.CONNECTION[Direction.WEST.ordinal()], true)
                        .setValue(CableBlock.CONNECTION[Direction.EAST.ordinal()], true));
        BlockPos sourcePos = EAST.east();
        helper.setBlock(sourcePos, FTBICElectricBlocks.EV_BATTERY_BOX.block.get());
        BankPortBlockEntity port = helper.getBlockEntity(PORT, BankPortBlockEntity.class);
        BankCellBlockEntity cell = helper.getBlockEntity(WEST, BankCellBlockEntity.class);
        EVBatteryBoxBlockEntity source = helper.getBlockEntity(sourcePos, EVBatteryBoxBlockEntity.class);
        Face westFace = Face.relative(source.getFacing(Direction.NORTH), Direction.WEST);
        source.setSideConfiguration(SideConfiguration.DEFAULT.with(Resource.ENERGY, westFace, Mode.OUTPUT));
        source.setEnergyRaw(100_000D);
        for (int tick = 0; tick < 8; tick++) {
            source.handleEnergyOutput();
            port.handleGeneration();
            port.handleEnergyOutput();
        }
        BankTopology.Snapshot stored = BankTopology.snapshot(helper.getLevel(), helper.absolutePos(PORT));
        helper.assertTrue(
                stored.stored() > port.getEnergyCapacity(),
                "connected bank keeps charging beyond one port buffer: stored=" + stored.stored() + ", port="
                        + port.getEnergy() + ", cell=" + cell.getEnergy() + ", source=" + source.getEnergy());
        helper.assertValueEqual(100_000D, stored.stored() + source.getEnergy(), "charging conserves energy");
        helper.assertValueEqual(0D, port.getEnergy(), "port leaves room for the next cable transfer");
        helper.assertTrue(cell.getEnergy() > 0D, "incoming charge reaches storage cells");
        helper.succeed();
    }

    static void combinedDisplays(GameTestHelper helper) {
        for (Direction face : Direction.values()) {
            Direction right = BankDisplayLayout.right(face);
            Direction up = BankDisplayLayout.up(face);
            BlockPos lowerRight = PORT.relative(right);
            BlockPos upperLeft = PORT.relative(up);
            BlockPos upperRight = lowerRight.relative(up);
            BlockPos origin = helper.absolutePos(PORT);
            helper.setBlock(PORT, FTBICElectricBlocks.INDUSTRIAL_BANK_PORT.block.get());
            helper.setBlock(upperLeft, FTBICElectricBlocks.INDUSTRIAL_BANK_PORT.block.get());
            helper.getBlockEntity(PORT, BankPortBlockEntity.class).setFaceStyle(FaceStyle.GAUGE);
            helper.getBlockEntity(upperLeft, BankPortBlockEntity.class).setFaceStyle(FaceStyle.GAUGE);
            Map<BlockPos, Tile> vertical = BankDisplayLayout.find(helper.getLevel(), origin, face);
            helper.assertValueEqual(
                    new Tile(0, 0, 1, 2), vertical.get(origin), "stacked ports share a tall display on " + face);
            helper.assertValueEqual(
                    1F, vertical.get(origin).fillTop(0.5F), "half charge reaches exactly the shared tile boundary");
            helper.assertValueEqual(
                    0F,
                    vertical.get(helper.absolutePos(upperLeft)).fillTop(0.5F),
                    "upper tile remains empty at half charge");
            helper.setBlock(lowerRight, FTBICElectricBlocks.INDUSTRIAL_BANK_PORT.block.get());
            helper.setBlock(upperRight, FTBICElectricBlocks.INDUSTRIAL_BANK_PORT.block.get());
            helper.getBlockEntity(lowerRight, BankPortBlockEntity.class).setFaceStyle(FaceStyle.GAUGE);
            helper.getBlockEntity(upperRight, BankPortBlockEntity.class).setFaceStyle(FaceStyle.GAUGE);
            Map<BlockPos, Tile> square = BankDisplayLayout.find(helper.getLevel(), origin, face);
            helper.assertValueEqual(4, square.size(), "four ports share a display");
            helper.assertValueEqual(new Tile(0, 0, 2, 2), square.get(origin), "2 by 2 panel bounds");
            helper.assertValueEqual(
                    new Tile(1, 1, 2, 2), square.get(helper.absolutePos(upperRight)), "opposite panel corner");
            helper.assertValueEqual(
                    square,
                    BankDisplayLayout.find(helper.getLevel(), helper.absolutePos(upperRight), face),
                    "layout is independent of the first port rendered");
            BankPortBlockEntity first = helper.getBlockEntity(PORT, BankPortBlockEntity.class);
            for (BlockPos pos : new BlockPos[] {PORT, lowerRight, upperLeft, upperRight}) {
                BankPortBlockEntity port = helper.getBlockEntity(pos, BankPortBlockEntity.class);
                port.setEnergyRaw(port.getEnergyCapacity() / 2D);
            }
            first.refreshChargeDisplay();
            helper.assertValueEqual(
                    500,
                    helper.getBlockEntity(upperRight, BankPortBlockEntity.class).getDisplayCharge(),
                    "all ports receive the same shared fill in one refresh");
            helper.setBlock(upperRight, Blocks.AIR);
            Map<BlockPos, Tile> corner = BankDisplayLayout.find(helper.getLevel(), origin, face);
            helper.assertValueEqual(3, corner.size(), "missing corner has no display");
            helper.assertValueEqual(
                    new Tile(0, 0, 2, 1), corner.get(origin), "L shape partitions without covering a missing port");
            helper.setBlock(upperLeft, FTBICElectricBlocks.INDUSTRIAL_BANK_CELL.block.get());
            helper.assertValueEqual(
                    2,
                    BankDisplayLayout.find(helper.getLevel(), origin, face).size(),
                    "cells do not become gauge pixels");
            helper.setBlock(PORT.relative(face), Blocks.STONE);
            helper.assertTrue(
                    BankDisplayLayout.find(helper.getLevel(), origin, face).isEmpty(), "covered faces have no display");
            helper.setBlock(PORT.relative(face), Blocks.AIR);
            helper.setBlock(lowerRight, Blocks.AIR);
            helper.assertValueEqual(
                    new Tile(0, 0, 1, 1),
                    BankDisplayLayout.find(helper.getLevel(), origin, face).get(origin),
                    "removing adjacent ports restores an individual gauge");
            helper.setBlock(PORT, Blocks.AIR);
            helper.setBlock(upperLeft, Blocks.AIR);
        }
        helper.succeed();
    }

    static void portFaceStyles(GameTestHelper helper) {
        helper.setBlock(PORT, FTBICElectricBlocks.INDUSTRIAL_BANK_PORT.block.get());
        helper.setBlock(EAST, FTBICElectricBlocks.INDUSTRIAL_BANK_PORT.block.get());
        BankPortBlockEntity first = helper.getBlockEntity(PORT, BankPortBlockEntity.class);
        BankPortBlockEntity second = helper.getBlockEntity(EAST, BankPortBlockEntity.class);
        BlockPos origin = helper.absolutePos(PORT);
        BlockPos east = helper.absolutePos(EAST);
        helper.assertValueEqual(FaceStyle.PORT, first.getFaceStyle(), "new ports use port symbol");
        helper.assertTrue(
                BankDisplayLayout.find(helper.getLevel(), origin, Direction.SOUTH)
                        .isEmpty(),
                "default port symbol does not render a gauge");
        helper.assertValueEqual(FaceStyle.GAUGE, first.cycleFaceStyle(), "first cycle selects gauge");
        helper.assertValueEqual(
                new Tile(0, 0, 1, 1),
                BankDisplayLayout.find(helper.getLevel(), origin, Direction.SOUTH)
                        .get(origin),
                "one gauge port is a single panel");
        second.setFaceStyle(FaceStyle.GAUGE);
        helper.assertValueEqual(
                2,
                BankDisplayLayout.find(helper.getLevel(), origin, Direction.SOUTH)
                        .size(),
                "adjacent gauge ports merge");
        helper.assertValueEqual(FaceStyle.BASIC, first.cycleFaceStyle(), "second cycle selects plain steel");
        helper.assertValueEqual(FaceStyle.GAUGE, second.getFaceStyle(), "other port keeps its style");
        helper.assertTrue(
                BankDisplayLayout.find(helper.getLevel(), origin, Direction.SOUTH)
                        .isEmpty(),
                "plain steel interrupts a shared gauge");
        helper.assertValueEqual(
                new Tile(0, 0, 1, 1),
                BankDisplayLayout.find(helper.getLevel(), east, Direction.SOUTH).get(east),
                "remaining gauge shrinks");
        helper.assertValueEqual(FaceStyle.PORT, first.cycleFaceStyle(), "third cycle returns to port symbol");
        helper.succeed();
    }
}
