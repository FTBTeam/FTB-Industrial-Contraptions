package dev.ftb.mods.ftbic.test;

import dev.ftb.mods.ftbic.block.CableBlock;
import dev.ftb.mods.ftbic.block.FTBICBlocks;
import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.block.entity.SuperconductingCableBlockEntity;
import dev.ftb.mods.ftbic.block.entity.storage.EVBatteryBoxBlockEntity;
import dev.ftb.mods.ftbic.util.SideConfiguration;
import dev.ftb.mods.ftbic.util.SideConfiguration.Face;
import dev.ftb.mods.ftbic.util.SideConfiguration.Mode;
import dev.ftb.mods.ftbic.util.SideConfiguration.Resource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

final class SuperconductingCableGameTests {
    private static final BlockPos SOURCE = new BlockPos(1, 2, 2);
    private static final BlockPos CABLE = SOURCE.east();
    private static final BlockPos TARGET = CABLE.east();

    static void unlimitedTransfer(GameTestHelper helper) {
        EVBatteryBoxBlockEntity source = box(helper, SOURCE, Direction.EAST, Mode.OUTPUT);
        EVBatteryBoxBlockEntity target = box(helper, TARGET, Direction.WEST, Mode.INPUT);
        helper.setBlock(CABLE, cableState());
        double amount = 1_000_000_000_000D;
        source.maxEnergyOutputTransfer = amount;
        source.energyCapacity = amount * 2D;
        target.energyCapacity = amount * 2D;
        target.maxInputEnergy = amount;
        SuperconductingCableBlockEntity cable = helper.getBlockEntity(CABLE, SuperconductingCableBlockEntity.class);
        helper.assertFalse(cable.isTransferring(), "idle cable does not pulse");
        for (int transfer = 0; transfer < 2; transfer++) {
            source.setEnergyRaw(amount);
            source.handleEnergyOutput();
            helper.assertValueEqual(0D, source.getEnergy(), "trillion-zap transfer is not capped by the cable");
            helper.assertValueEqual(
                    amount * (transfer + 1D), target.getEnergy(), "energy reaches the receiver without loss");
        }
        helper.assertTrue(
                helper.getBlockState(CABLE).is(FTBICBlocks.SUPERCONDUCTING_CABLE.get()), "cable cannot overload");
        helper.assertTrue(cable.isTransferring(), "successful transfer activates the beam");
        helper.runAfterDelay(20, () -> {
            helper.assertFalse(cable.isTransferring(), "pulse stops after transfers cease");
            helper.succeed();
        });
    }

    static void onlyUsedRoutesPulse(GameTestHelper helper) {
        EVBatteryBoxBlockEntity source = box(helper, SOURCE, Direction.EAST, Mode.OUTPUT);
        box(helper, TARGET, Direction.WEST, Mode.INPUT);
        EVBatteryBoxBlockEntity full = box(helper, CABLE.south(2), Direction.NORTH, Mode.INPUT);
        full.setEnergyRaw(full.getEnergyCapacity());
        helper.setBlock(CABLE, cableState());
        helper.setBlock(CABLE.south(), cableState());
        source.setEnergyRaw(100D);
        source.handleEnergyOutput();
        helper.assertTrue(
                helper.getBlockEntity(CABLE, SuperconductingCableBlockEntity.class)
                        .isTransferring(),
                "used route pulses");
        helper.assertFalse(
                helper.getBlockEntity(CABLE.south(), SuperconductingCableBlockEntity.class)
                        .isTransferring(),
                "full receiver's branch stays idle");
        helper.succeed();
    }

    static void connectionsAndRemoval(GameTestHelper helper) {
        EVBatteryBoxBlockEntity source = box(helper, SOURCE, Direction.EAST, Mode.OUTPUT);
        EVBatteryBoxBlockEntity target = box(helper, TARGET.east(), Direction.WEST, Mode.INPUT);
        helper.setBlock(CABLE, cableState());
        helper.setBlock(TARGET, cableState());
        helper.assertTrue(
                helper.getBlockState(CABLE).getValue(CableBlock.CONNECTION[Direction.EAST.ordinal()]),
                "superconducting neighbors connect");
        source.setEnergyRaw(100D);
        source.handleEnergyOutput();
        helper.assertValueEqual(100D, target.getEnergy(), "two-segment route transfers");
        helper.setBlock(TARGET, Blocks.AIR);
        source.setEnergyRaw(100D);
        source.handleEnergyOutput();
        helper.assertValueEqual(100D, source.getEnergy(), "broken route stops transfer immediately");
        helper.setBlock(TARGET, FTBICBlocks.IV_CABLE.get());
        helper.assertFalse(
                helper.getBlockState(CABLE).getValue(CableBlock.CONNECTION[Direction.EAST.ordinal()]),
                "different cable tiers do not connect");
        helper.succeed();
    }

    private static BlockState cableState() {
        BlockState state = FTBICBlocks.SUPERCONDUCTING_CABLE.get().defaultBlockState();
        for (var connection : CableBlock.CONNECTION) state = state.setValue(connection, true);
        return state;
    }

    private static EVBatteryBoxBlockEntity box(GameTestHelper helper, BlockPos pos, Direction side, Mode mode) {
        helper.setBlock(pos, FTBICElectricBlocks.EV_BATTERY_BOX.block.get());
        EVBatteryBoxBlockEntity box = helper.getBlockEntity(pos, EVBatteryBoxBlockEntity.class);
        SideConfiguration config = SideConfiguration.DEFAULT;
        for (Face face : Face.values()) config = config.with(Resource.ENERGY, face, Mode.DISABLED);
        box.setSideConfiguration(
                config.with(Resource.ENERGY, Face.relative(box.getFacing(Direction.NORTH), side), mode));
        return box;
    }
}
