package dev.ftb.mods.ftbic.test;

import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.block.entity.storage.BankCellBlockEntity;
import dev.ftb.mods.ftbic.block.entity.storage.BankPortBlockEntity;
import dev.ftb.mods.ftbic.integration.jade.FTBICEnergyStorageProvider;
import dev.ftb.mods.ftbic.util.ZapFEConversion;
import java.lang.reflect.Proxy;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Blocks;
import snownee.jade.addon.universal.EnergyStorageProvider;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.view.EnergyView;

final class BankJadeGameTests {
    static void combinedEnergyBar(GameTestHelper helper) {
        BlockPos portPos = new BlockPos(3, 2, 3);
        helper.setBlock(portPos, FTBICElectricBlocks.INDUSTRIAL_BANK_PORT.block.get());
        helper.setBlock(portPos.west(), FTBICElectricBlocks.INDUSTRIAL_BANK_CELL.block.get());
        helper.setBlock(portPos.east(), FTBICElectricBlocks.INDUSTRIAL_BANK_CELL.block.get());
        BankPortBlockEntity port = helper.getBlockEntity(portPos, BankPortBlockEntity.class);
        BankCellBlockEntity west = helper.getBlockEntity(portPos.west(), BankCellBlockEntity.class);
        BankCellBlockEntity east = helper.getBlockEntity(portPos.east(), BankCellBlockEntity.class);
        west.energyCapacity = 1_000_000_000D;
        east.energyCapacity = 1_000_000_000D;
        west.setEnergyRaw(west.getEnergyCapacity());
        double rate = FTBICConfig.ENERGY.FULL_FE_MODE.get() ? ZapFEConversion.rate() : 1D;
        long capacity =
                (long) ((west.getEnergyCapacity() + east.getEnergyCapacity() + port.getEnergyCapacity()) * rate);
        for (BlockPos pos : new BlockPos[] {portPos, portPos.west(), portPos.east()}) {
            EnergyView.Data data = jadeData(helper, pos);
            helper.assertValueEqual(
                    capacity,
                    data.capacity(),
                    "Jade bar capacity includes every cell and port without 32-bit clipping");
            helper.assertValueEqual(
                    (long) (west.getEnergy() * rate),
                    data.current(),
                    "empty port and empty cell show the bank's stored charge");
        }
        helper.setBlock(portPos.east(), Blocks.AIR);
        port.setEnergyRaw(port.getEnergyCapacity());
        EnergyView.Data full = jadeData(helper, portPos);
        helper.assertValueEqual(
                (long) ((west.getEnergyCapacity() + port.getEnergyCapacity()) * rate),
                full.capacity(),
                "splitting the bank updates Jade's capacity");
        helper.assertValueEqual(full.capacity(), full.current(), "full bank fills the whole bar");
        port.setEnergyRaw(0D);
        west.setEnergyRaw(0D);
        helper.assertValueEqual(0L, jadeData(helper, portPos).current(), "empty bank empties the bar");
        helper.succeed();
    }

    static void electricBlocksHideDefaultBar(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 2, 1);
        helper.setBlock(pos, FTBICElectricBlocks.EV_BATTERY_BOX.block.get());
        var result = EnergyStorageProvider.BLOCK.streamData(accessor(helper, pos));
        helper.assertTrue(result != null, "Jade finds an energy provider for the battery box");
        helper.assertValueEqual(
                FTBICEnergyStorageProvider.INSTANCE.getUid(),
                result.getKey(),
                "FTBIC's provider takes precedence over the FE capability");
        helper.assertTrue(
                result.getValue().isEmpty(), "Jade's default bar is hidden where FTBIC shows its own energy line");
        helper.succeed();
    }

    private static EnergyView.Data jadeData(GameTestHelper helper, BlockPos relative) {
        var result = EnergyStorageProvider.BLOCK.streamData(accessor(helper, relative));
        helper.assertTrue(result != null, "Jade finds an energy provider for the bank");
        helper.assertValueEqual(
                FTBICEnergyStorageProvider.INSTANCE.getUid(),
                result.getKey(),
                "bank provider takes precedence over the port's FE capability");
        return result.getValue().getFirst().views.getFirst();
    }

    private static BlockAccessor accessor(GameTestHelper helper, BlockPos relative) {
        BlockPos pos = helper.absolutePos(relative);
        return (BlockAccessor) Proxy.newProxyInstance(
                BlockAccessor.class.getClassLoader(),
                new Class<?>[] {BlockAccessor.class},
                (proxy, method, arguments) -> switch (method.getName()) {
                    case "getLevel" -> helper.getLevel();
                    case "getPosition" -> pos;
                    case "getBlock" -> helper.getLevel().getBlockState(pos).getBlock();
                    case "getBlockState" -> helper.getLevel().getBlockState(pos);
                    case "getBlockEntity", "getTarget" -> helper.getLevel().getBlockEntity(pos);
                    case "getAccessorType" -> BlockAccessor.class;
                    case "getSide" -> Direction.NORTH;
                    case "getServerData" -> new CompoundTag();
                    case "isServersideContent", "showDetails" -> true;
                    default -> throw new UnsupportedOperationException(method.getName());
                });
    }
}
