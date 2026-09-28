package dev.ftb.mods.ftbic.test;

import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.block.CableBlock;
import dev.ftb.mods.ftbic.block.FTBICBlocks;
import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.block.entity.generator.NuclearReactorBlockEntity;
import dev.ftb.mods.ftbic.util.SideConfiguration;
import dev.ftb.mods.ftbic.util.ZapFEConversion;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

@EventBusSubscriber(modid = FTBIC.MOD_ID)
final class ReactorFEGameTests {
    private static final BlockPos POS = new BlockPos(2, 2, 2);

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        if (!Boolean.getBoolean("ftbic.test.reactorFE")) return;
        event.registerBlock(
                Capabilities.Energy.BLOCK,
                (level, pos, state, be, side) -> new ConsumingMeter(),
                Blocks.STONE,
                Blocks.BARREL);
    }

    static void chamberPush(GameTestHelper h) {
        if (!Boolean.getBoolean("ftbic.test.reactorFE")) {
            h.succeed();
            return;
        }
        h.setBlock(POS, FTBICElectricBlocks.NUCLEAR_REACTOR.block.get());
        h.setBlock(POS.east(), FTBICBlocks.NUCLEAR_REACTOR_CHAMBER.get());
        h.setBlock(POS.east().above(), Blocks.STONE);
        h.setBlock(POS.west(), FTBICBlocks.NUCLEAR_REACTOR_CHAMBER.get());
        h.setBlock(POS.west().above(), Blocks.STONE);
        var reactor = h.getBlockEntity(POS, NuclearReactorBlockEntity.class);
        reactor.energy = 1000D;
        reactor.maxEnergyOutputTransfer = 16D;
        var disabled = SideConfiguration.DEFAULT;
        for (var face : SideConfiguration.Face.values()) {
            disabled = disabled.with(SideConfiguration.Resource.ENERGY, face, SideConfiguration.Mode.DISABLED);
        }
        reactor.setSideConfiguration(disabled);
        reactor.handleEnergyOutput();
        h.assertValueEqual(1000D, reactor.energy, "Disabled chamber faces reject FE push");
        var up = SideConfiguration.Face.relative(reactor.getFacing(Direction.NORTH), Direction.UP);
        reactor.setSideConfiguration(
                disabled.with(SideConfiguration.Resource.ENERGY, up, SideConfiguration.Mode.OUTPUT));
        reactor.handleEnergyOutput();
        h.assertValueEqual(984D, reactor.energy, "Chamber FE sinks share one reactor output budget");
        h.setBlock(POS.east(), Blocks.AIR);
        h.setBlock(POS.west(), Blocks.AIR);
        reactor.handleEnergyOutput();
        h.assertValueEqual(984D, reactor.energy, "Detached chambers stop pushing FE");
        h.succeed();
    }

    static void cableToConsumingMeter(GameTestHelper h) {
        if (!Boolean.getBoolean("ftbic.test.reactorFE")) {
            h.succeed();
            return;
        }
        h.setBlock(POS, FTBICElectricBlocks.NUCLEAR_REACTOR.block.get());
        h.setBlock(POS.east(), FTBICBlocks.NUCLEAR_REACTOR_CHAMBER.get());
        var cable = FTBICBlocks.LV_CABLE.get().defaultBlockState();
        for (var connection : CableBlock.CONNECTION) cable = cable.setValue(connection, true);
        h.setBlock(POS.east().above(), cable);
        h.setBlock(POS.east().above(2), Blocks.BARREL);
        var reactor = h.getBlockEntity(POS, NuclearReactorBlockEntity.class);
        reactor.energy = 1000D;
        reactor.maxEnergyOutputTransfer = 16D;
        reactor.handleEnergyOutput();
        h.assertValueEqual(984D, reactor.energy, "Chamber cable powers an FE sink with zero storage capacity");
        h.succeed();
    }

    static void chamberPull(GameTestHelper h) {
        h.setBlock(POS, FTBICElectricBlocks.NUCLEAR_REACTOR.block.get());
        h.setBlock(POS.east(), FTBICBlocks.NUCLEAR_REACTOR_CHAMBER.get());
        var reactor = h.getBlockEntity(POS, NuclearReactorBlockEntity.class);
        reactor.energy = 1000D;
        var fe = h.getLevel().getCapability(Capabilities.Energy.BLOCK, h.absolutePos(POS.east()), Direction.UP);
        h.assertTrue(fe != null, "FE pipes can find the chamber output capability");
        try (var tx = Transaction.openRoot()) {
            h.assertValueEqual(80, fe.extract(80, tx), "Pipe simulation extracts FE");
        }
        h.assertValueEqual(1000D, reactor.energy, "Aborted chamber extraction preserves reactor energy");
        try (var tx = Transaction.openRoot()) {
            fe.extract(80, tx);
            tx.commit();
        }
        h.assertValueEqual(1000D - ZapFEConversion.feToZaps(80), reactor.energy, "Pipe extraction debits the core");
        h.succeed();
    }

    private static final class ConsumingMeter extends SnapshotJournal<Integer> implements EnergyHandler {
        private int consumed;

        @Override
        public long getAmountAsLong() {
            return 0;
        }

        @Override
        public long getCapacityAsLong() {
            return 0;
        }

        @Override
        public int insert(int amount, TransactionContext tx) {
            updateSnapshots(tx);
            consumed += amount;
            return amount;
        }

        @Override
        public int extract(int amount, TransactionContext tx) {
            return 0;
        }

        @Override
        protected Integer createSnapshot() {
            return consumed;
        }

        @Override
        protected void revertToSnapshot(Integer snapshot) {
            consumed = snapshot;
        }
    }
}
