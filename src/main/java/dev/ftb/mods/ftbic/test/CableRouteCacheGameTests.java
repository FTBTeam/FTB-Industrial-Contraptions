package dev.ftb.mods.ftbic.test;

import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.block.CableBlock;
import dev.ftb.mods.ftbic.block.FTBICBlocks;
import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.block.entity.ElectricBlockEntity;
import dev.ftb.mods.ftbic.util.CableRouteCache;
import dev.ftb.mods.ftbic.util.SideConfiguration;
import dev.ftb.mods.ftbic.util.SideConfiguration.Face;
import dev.ftb.mods.ftbic.util.SideConfiguration.Mode;
import dev.ftb.mods.ftbic.util.SideConfiguration.Resource;
import dev.ftb.mods.ftbic.util.ZapFEConversion;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;

final class CableRouteCacheGameTests {
    private static final BlockPos INPUT = new BlockPos(1, 2, 2);
    private static final BlockPos MIDDLE = INPUT.east();
    private static final BlockPos TARGET = MIDDLE.east();

    static void repeatedInsertsReuseRoute(GameTestHelper helper) {
        helper.setBlock(INPUT.west(), FTBICElectricBlocks.MACERATOR.block.get());
        helper.setBlock(TARGET, FTBICElectricBlocks.MACERATOR.block.get());
        helper.setBlock(MIDDLE, connectedCable());
        helper.setBlock(INPUT, connectedCable());
        if (nativeMode(helper)) return;
        var source = helper.getBlockEntity(INPUT.west(), ElectricBlockEntity.class);
        var target = helper.getBlockEntity(TARGET, ElectricBlockEntity.class);
        var fromWest = cable(helper, Direction.WEST);
        helper.assertValueEqual(limit(), insert(fromWest, false), "First insert routes FE");
        long scans = CableRouteCache.scanCount();
        for (int i = 0; i < 4; i++) {
            helper.assertValueEqual(limit(), insert(fromWest, false), "Repeated simulations route FE");
        }
        helper.assertValueEqual(0D, target.getEnergy(), "Simulations leave the receiver empty");
        helper.assertValueEqual(limit(), insert(fromWest, true), "Committed insert routes FE");
        helper.assertValueEqual(0D, source.getEnergy(), "Neighbour on the source side is skipped");
        helper.assertValueEqual(rate(), target.getEnergy(), "FE reaches the far receiver");
        helper.assertValueEqual(limit(), insert(cable(helper, Direction.UP), true), "Another side reuses the route");
        helper.assertValueEqual(rate(), source.getEnergy(), "Source-side exclusion is decided per call");
        helper.assertValueEqual(scans, CableRouteCache.scanCount(), "Repeated inserts reuse the cached route");
        helper.succeed();
    }

    static void newReceiverIsPickedUp(GameTestHelper helper) {
        helper.setBlock(MIDDLE, connectedCable());
        helper.setBlock(INPUT, connectedCable());
        if (nativeMode(helper)) return;
        var cable = cable(helper, Direction.WEST);
        helper.assertValueEqual(0, insert(cable, false), "Network without receivers accepts nothing");
        helper.setBlock(TARGET, FTBICElectricBlocks.MACERATOR.block.get());
        helper.assertValueEqual(limit(), insert(cable, true), "Receiver placed at the end of the network is found");
        var far = helper.getBlockEntity(TARGET, ElectricBlockEntity.class);
        helper.assertValueEqual(rate(), far.getEnergy(), "New far receiver gets the FE");
        helper.setBlock(INPUT.north(), FTBICElectricBlocks.MACERATOR.block.get());
        helper.assertValueEqual(limit(), insert(cable, true), "Receiver placed beside the input cable is found");
        helper.assertValueEqual(
                rate(),
                helper.getBlockEntity(INPUT.north(), ElectricBlockEntity.class).getEnergy(),
                "Nearest new receiver is filled first");
        helper.assertValueEqual(rate(), far.getEnergy(), "Far receiver is untouched once the near one takes the offer");
        helper.succeed();
    }

    static void removalsArePickedUp(GameTestHelper helper) {
        helper.setBlock(TARGET, FTBICElectricBlocks.MACERATOR.block.get());
        helper.setBlock(MIDDLE, connectedCable());
        helper.setBlock(INPUT, connectedCable());
        if (nativeMode(helper)) return;
        var cable = cable(helper, Direction.WEST);
        helper.assertValueEqual(limit(), insert(cable, false), "Route reaches the receiver");
        helper.setBlock(TARGET, Blocks.AIR);
        helper.assertValueEqual(0, insert(cable, false), "Removed receiver is no longer offered FE");
        helper.setBlock(TARGET, FTBICElectricBlocks.MACERATOR.block.get());
        helper.assertValueEqual(limit(), insert(cable, false), "Replaced receiver is found again");
        helper.setBlock(MIDDLE, Blocks.AIR);
        helper.assertValueEqual(0, insert(cable, false), "Removed cable breaks the cached route");
        helper.setBlock(MIDDLE, connectedCable());
        helper.assertValueEqual(limit(), insert(cable, false), "Restored cable reconnects the route");
        helper.succeed();
    }

    static void disabledSideIsRespected(GameTestHelper helper) {
        BlockPos branch = MIDDLE.north();
        helper.setBlock(branch, FTBICElectricBlocks.MACERATOR.block.get());
        helper.setBlock(TARGET, FTBICElectricBlocks.MACERATOR.block.get());
        helper.setBlock(MIDDLE, connectedCable());
        helper.setBlock(INPUT, connectedCable());
        if (nativeMode(helper)) return;
        var near = helper.getBlockEntity(branch, ElectricBlockEntity.class);
        var far = helper.getBlockEntity(TARGET, ElectricBlockEntity.class);
        var cable = cable(helper, Direction.WEST);
        helper.assertValueEqual(limit(), insert(cable, false), "Route is cached before the change");
        near.setSideConfiguration(SideConfiguration.DEFAULT.with(
                Resource.ENERGY, Face.relative(near.getFacing(Direction.NORTH), Direction.SOUTH), Mode.DISABLED));
        helper.assertValueEqual(limit(), insert(cable, true), "FE still flows past the disabled face");
        helper.assertValueEqual(0D, near.getEnergy(), "Disabled receiver face gets nothing");
        helper.assertValueEqual(rate(), far.getEnergy(), "FE moves on to the next receiver");
        near.setSideConfiguration(SideConfiguration.DEFAULT);
        helper.assertValueEqual(limit(), insert(cable, true), "Re-enabled face accepts FE");
        helper.assertValueEqual(rate(), near.getEnergy(), "Re-enabled receiver is filled first");
        helper.assertValueEqual(rate(), far.getEnergy(), "Far receiver is untouched once the near one takes the offer");
        helper.succeed();
    }

    private static boolean nativeMode(GameTestHelper helper) {
        if (FTBICConfig.ENERGY.FULL_FE_MODE.get()) return false;
        helper.assertTrue(cable(helper, Direction.WEST) == null, "Native mode does not expose cable FE input");
        helper.succeed();
        return true;
    }

    private static EnergyHandler cable(GameTestHelper helper, Direction side) {
        return helper.getLevel().getCapability(Capabilities.Energy.BLOCK, helper.absolutePos(INPUT), side);
    }

    private static int insert(EnergyHandler cable, boolean commit) {
        try (var tx = Transaction.openRoot()) {
            int accepted = cable.insert(limit(), tx);
            if (commit) tx.commit();
            return accepted;
        }
    }

    private static int limit() {
        return ZapFEConversion.zapsToFEFloor(FTBICConfig.ENERGY.LV_TRANSFER_RATE.get());
    }

    private static double rate() {
        return FTBICConfig.ENERGY.LV_TRANSFER_RATE.get();
    }

    private static BlockState connectedCable() {
        BlockState state = FTBICBlocks.LV_CABLE.get().defaultBlockState();
        for (var connection : CableBlock.CONNECTION) state = state.setValue(connection, true);
        return state;
    }
}
