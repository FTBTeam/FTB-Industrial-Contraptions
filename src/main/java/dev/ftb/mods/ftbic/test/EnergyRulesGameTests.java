package dev.ftb.mods.ftbic.test;

import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.block.ElectricBlock;
import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.block.entity.ElectricBlockEntity;
import dev.ftb.mods.ftbic.block.entity.generator.GeneratorBlockEntity;
import dev.ftb.mods.ftbic.block.entity.storage.BatteryBoxBlockEntity;
import dev.ftb.mods.ftbic.util.CachedEnergyStorage;
import dev.ftb.mods.ftbic.util.CachedEnergyStorageOrigin;
import dev.ftb.mods.ftbic.util.ZapFEConversion;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.transaction.Transaction;

final class EnergyRulesGameTests {
    private static final BlockPos POS = new BlockPos(2, 2, 2);
    private static final double EPSILON = 1.0E-9D;

    static void directionalFEFaces(GameTestHelper helper) {
        helper.setBlock(POS, FTBICElectricBlocks.LV_BATTERY_BOX.block.get());
        BatteryBoxBlockEntity box = helper.getBlockEntity(POS, BatteryBoxBlockEntity.class);
        Direction front = box.getFacing(Direction.NORTH);
        BlockPos boxPos = helper.absolutePos(POS);
        var output = helper.getLevel().getCapability(Capabilities.Energy.BLOCK, boxPos, front);
        var input = helper.getLevel().getCapability(Capabilities.Energy.BLOCK, boxPos, front.getOpposite());
        var internal = helper.getLevel().getCapability(Capabilities.Energy.BLOCK, boxPos, null);
        if (output == null || input == null || internal == null) {
            helper.assertFalse(FTBICConfig.ENERGY.FULL_FE_MODE.get(), "Full FE mode exposes battery box FE");
            helper.succeed();
            return;
        }
        double stored = box.energyCapacity / 2D;
        box.energy = stored;
        try (var tx = Transaction.openRoot()) {
            helper.assertValueEqual(0, output.insert(80, tx), "Battery box output face refuses FE input");
            helper.assertValueEqual(0, input.extract(80, tx), "Battery box input face refuses FE extraction");
            helper.assertTrue(input.insert(80, tx) > 0, "Battery box input face accepts FE");
            helper.assertTrue(output.extract(80, tx) > 0, "Battery box output face provides FE");
            helper.assertTrue(internal.insert(80, tx) > 0, "Unsided access still inserts FE");
            helper.assertTrue(internal.extract(80, tx) > 0, "Unsided access still extracts FE");
        }
        helper.assertValueEqual(stored, box.energy, "Aborted FE transfers leave the battery box unchanged");
        try (var tx = Transaction.openRoot()) {
            helper.assertValueEqual(7, output.extract(7, tx), "Small FE extraction is served exactly");
            tx.commit();
        }
        assertClose(
                helper, stored - ZapFEConversion.feToZaps(7), box.energy, "Extracted FE costs exactly fe / rate zaps");

        helper.setBlock(POS.east(2), FTBICElectricBlocks.LV_TRANSFORMER.block.get());
        ElectricBlockEntity transformer = helper.getBlockEntity(POS.east(2), ElectricBlockEntity.class);
        Direction transformerFront = transformer.getFacing(Direction.NORTH);
        BlockPos transformerPos = helper.absolutePos(POS.east(2));
        var transformerInput =
                helper.getLevel().getCapability(Capabilities.Energy.BLOCK, transformerPos, transformerFront);
        var transformerOutput = helper.getLevel()
                .getCapability(Capabilities.Energy.BLOCK, transformerPos, transformerFront.getOpposite());
        helper.assertTrue(transformerInput != null && transformerOutput != null, "Full FE mode exposes transformer FE");
        transformer.energy = transformer.energyCapacity / 2D;
        try (var tx = Transaction.openRoot()) {
            helper.assertValueEqual(0, transformerOutput.insert(80, tx), "Transformer output face refuses FE input");
            helper.assertValueEqual(
                    0, transformerInput.extract(80, tx), "Transformer input face refuses FE extraction");
            helper.assertTrue(transformerInput.insert(80, tx) > 0, "Transformer input face accepts FE");
            helper.assertTrue(transformerOutput.extract(80, tx) > 0, "Transformer output face provides FE");
        }
        helper.succeed();
    }

    static void smallFEInsertsAreExact(GameTestHelper helper) {
        helper.setBlock(POS, FTBICElectricBlocks.MACERATOR.block.get());
        ElectricBlockEntity machine = helper.getBlockEntity(POS, ElectricBlockEntity.class);
        var fe = helper.getLevel().getCapability(Capabilities.Energy.BLOCK, helper.absolutePos(POS), Direction.UP);
        if (fe == null) {
            helper.assertFalse(FTBICConfig.ENERGY.FULL_FE_MODE.get(), "Full FE mode exposes machine FE input");
            helper.succeed();
            return;
        }
        machine.energy = 0D;
        try (var tx = Transaction.openRoot()) {
            helper.assertValueEqual(7, fe.insert(7, tx), "7 FE is accepted in full");
            helper.assertValueEqual(15, fe.insert(15, tx), "15 FE is accepted in full");
            tx.commit();
        }
        assertClose(helper, ZapFEConversion.feToZaps(22), machine.energy, "Accepted FE becomes exactly fe / rate zaps");
        helper.succeed();
    }

    static void feConsumerChargedExactly(GameTestHelper helper) {
        helper.setBlock(POS, FTBICElectricBlocks.MACERATOR.block.get());
        ElectricBlockEntity machine = helper.getBlockEntity(POS, ElectricBlockEntity.class);
        var cache = BlockCapabilityCache.create(
                Capabilities.Energy.BLOCK, helper.getLevel(), helper.absolutePos(POS), Direction.UP);
        if (cache.getCapability() == null) {
            helper.assertFalse(FTBICConfig.ENERGY.FULL_FE_MODE.get(), "Full FE mode exposes machine FE input");
            helper.succeed();
            return;
        }
        double tenFE = ZapFEConversion.feToZaps(10);
        machine.energy = 0D;
        machine.energyCapacity = tenFE;
        CachedEnergyStorage storage = new CachedEnergyStorage();
        storage.origin = new CachedEnergyStorageOrigin();
        storage.blockEntity = machine;
        storage.feHandlerCache = cache;
        double charged = storage.insertZaps(ZapFEConversion.feToZaps(100));
        assertClose(helper, tenFE, charged, "A consumer taking 10 FE costs exactly 10 / rate zaps");
        assertClose(helper, tenFE, machine.energy, "The consumer receives exactly what the source paid");
        helper.succeed();
    }

    static void plainNeighbourUpdateKeepsNetwork(GameTestHelper helper) {
        BlockPos machinePos = POS.east();
        BlockPos neighbourPos = POS.above();
        helper.setBlock(POS, FTBICElectricBlocks.BASIC_GENERATOR.block.get());
        helper.setBlock(machinePos, FTBICElectricBlocks.MACERATOR.block.get());
        GeneratorBlockEntity generator = helper.getBlockEntity(POS, GeneratorBlockEntity.class);
        ElectricBlockEntity machine = helper.getBlockEntity(machinePos, ElectricBlockEntity.class);
        BlockPos generatorPos = helper.absolutePos(POS);
        generator.getConnectedEnergyBlocks();
        long network = ElectricBlockEntity.getCurrentElectricNetwork(helper.getLevel(), generatorPos);

        helper.setBlock(neighbourPos, Blocks.STONE);
        helper.setBlock(neighbourPos, Blocks.AIR);
        helper.getLevel().updateNeighborsAt(helper.absolutePos(neighbourPos), Blocks.STONE);
        BlockState machineState = helper.getBlockState(machinePos);
        helper.setBlock(
                machinePos, machineState.setValue(ElectricBlock.ACTIVE, !machineState.getValue(ElectricBlock.ACTIVE)));
        helper.assertValueEqual(
                network,
                ElectricBlockEntity.getCurrentElectricNetwork(helper.getLevel(), generatorPos),
                "Plain neighbour updates do not bump the electric network counter");

        generator.energy = 16D;
        machine.energy = 0D;
        generator.handleEnergyOutput();
        helper.assertTrue(machine.energy > 0D, "The generator still powers its neighbour after plain updates");

        helper.setBlock(POS.west(), FTBICElectricBlocks.MACERATOR.block.get());
        helper.assertTrue(
                ElectricBlockEntity.getCurrentElectricNetwork(helper.getLevel(), generatorPos) != network,
                "Placing an electric block still bumps the electric network counter");
        helper.succeed();
    }

    private static void assertClose(GameTestHelper helper, double expected, double actual, String message) {
        helper.assertTrue(
                Math.abs(expected - actual) < EPSILON, message + ": expected " + expected + ", got " + actual);
    }
}
