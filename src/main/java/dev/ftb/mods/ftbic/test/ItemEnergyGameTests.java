package dev.ftb.mods.ftbic.test;

import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.block.entity.machine.BasicMachineBlockEntity;
import dev.ftb.mods.ftbic.block.entity.machine.ChargePadBlockEntity;
import dev.ftb.mods.ftbic.item.FTBICItems;
import dev.ftb.mods.ftbic.util.BatterySlotHelper;
import dev.ftb.mods.ftbic.util.EnergyItemHandler;
import dev.ftb.mods.ftbic.util.EnergyTier;
import dev.ftb.mods.ftbic.util.ZapFEConversion;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

final class ItemEnergyGameTests {
    static void batteryExchangesFE(GameTestHelper helper) {
        ItemStack battery = new ItemStack(FTBICItems.LV_BATTERY.get());
        ItemStack creative = new ItemStack(FTBICItems.CREATIVE_BATTERY.get());
        EnergyHandler fe = itemFE(battery);
        EnergyHandler creativeFE = itemFE(creative);
        if (!FTBICConfig.ENERGY.FULL_FE_MODE.get()) {
            helper.assertTrue(fe == null, "Native mode keeps battery FE disabled");
            helper.assertTrue(creativeFE == null, "Native mode keeps creative battery FE disabled");
            helper.succeed();
            return;
        }
        helper.assertTrue(fe != null, "Full FE mode exposes battery FE");
        var item = (EnergyItemHandler) battery.getItem();
        double rate = ZapFEConversion.rate();
        double capacity = item.getEnergyCapacity(battery);
        int limit = tierLimit(EnergyTier.LV);
        helper.assertValueEqual(0L, fe.getAmountAsLong(), "An empty battery reports no FE");
        helper.assertValueEqual(
                (long) (capacity * rate), fe.getCapacityAsLong(), "Battery FE capacity follows the conversion rate");

        int expectedIn = Math.min(limit, ZapFEConversion.zapsToFEFloor(capacity));
        try (var tx = Transaction.openRoot()) {
            helper.assertValueEqual(
                    expectedIn, fe.insert(Integer.MAX_VALUE, tx), "FE insert is clamped to the tier rate and capacity");
            tx.commit();
        }
        helper.assertTrue(
                approx(ZapFEConversion.feToZaps(expectedIn), item.getEnergy(battery)),
                "Inserted FE is stored as zaps (stored=" + item.getEnergy(battery) + ")");

        double stored = item.getEnergy(battery);
        int expectedOut = Math.min(limit, ZapFEConversion.zapsToFEFloor(stored));
        try (var tx = Transaction.openRoot()) {
            helper.assertValueEqual(
                    expectedOut,
                    fe.extract(Integer.MAX_VALUE, tx),
                    "FE extract is clamped to the tier rate and stored energy");
            tx.commit();
        }
        helper.assertTrue(
                approx(0D, item.getEnergy(battery)),
                "Extracting every FE empties the battery (stored=" + item.getEnergy(battery) + ")");

        item.setEnergy(battery, capacity);
        try (var tx = Transaction.openRoot()) {
            helper.assertValueEqual(
                    Math.min(limit, ZapFEConversion.zapsToFEFloor(capacity)),
                    fe.extract(Integer.MAX_VALUE, tx),
                    "A full battery still gives no more than the tier rate per call");
        }

        helper.assertTrue(creativeFE != null, "Full FE mode exposes creative battery FE");
        try (var tx = Transaction.openRoot()) {
            helper.assertValueEqual(
                    1_000_000, creativeFE.extract(1_000_000, tx), "The creative battery supplies any amount of FE");
            helper.assertValueEqual(0, creativeFE.insert(1_000, tx), "The creative battery does not absorb FE");
        }
        helper.succeed();
    }

    static void abortedTransactionLeavesStack(GameTestHelper helper) {
        ItemStack battery = new ItemStack(FTBICItems.MV_BATTERY.get());
        var item = (EnergyItemHandler) battery.getItem();
        item.setEnergy(battery, item.getEnergyCapacity(battery) / 2D);
        EnergyHandler fe = itemFE(battery);
        if (!FTBICConfig.ENERGY.FULL_FE_MODE.get()) {
            helper.assertTrue(fe == null, "Native mode keeps battery FE disabled");
            helper.succeed();
            return;
        }
        helper.assertTrue(fe != null, "Full FE mode exposes battery FE");

        ItemStack before = battery.copy();
        long amountBefore = fe.getAmountAsLong();
        try (var tx = Transaction.openRoot()) {
            helper.assertTrue(fe.extract(1_000, tx) > 0, "The battery gives FE inside the transaction");
            helper.assertTrue(fe.getAmountAsLong() < amountBefore, "The transaction sees the drained battery");
            helper.assertTrue(fe.insert(Integer.MAX_VALUE, tx) > 0, "The battery accepts FE inside the transaction");
        }
        helper.assertTrue(
                ItemStack.isSameItemSameComponents(before, battery),
                "An aborted transaction leaves the battery components unchanged");
        helper.assertValueEqual(
                before.getCount(), battery.getCount(), "An aborted transaction leaves the stack count unchanged");
        helper.assertValueEqual(
                amountBefore, fe.getAmountAsLong(), "The handler reports the original FE after the abort");

        ItemStack single = new ItemStack(FTBICItems.SINGLE_USE_BATTERY.get());
        var singleItem = (EnergyItemHandler) single.getItem();
        singleItem.setEnergy(single, singleItem.getEnergyCapacity(single));
        EnergyHandler singleFE = itemFE(single);
        helper.assertTrue(singleFE != null, "Full FE mode exposes single-use battery FE");
        try (var tx = Transaction.openRoot()) {
            helper.assertValueEqual(0, singleFE.insert(1_000, tx), "A single-use battery never accepts FE");
            helper.assertTrue(drainAll(singleFE, tx) > 0, "A single-use battery gives FE");
            helper.assertValueEqual(0L, singleFE.getAmountAsLong(), "The transaction sees the used up battery");
        }
        helper.assertFalse(single.isEmpty(), "An aborted drain does not use up a single-use battery");

        try (var tx = Transaction.openRoot()) {
            drainAll(singleFE, tx);
            tx.commit();
        }
        helper.assertTrue(single.isEmpty(), "A fully drained single-use battery is used up");
        helper.succeed();
    }

    static void machineSlotDrainsFEItem(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 2, 1);
        helper.setBlock(pos, FTBICElectricBlocks.MACERATOR.block.get());
        var machine = helper.getBlockEntity(pos, BasicMachineBlockEntity.class);
        BlockPos padPos = new BlockPos(3, 2, 1);
        helper.setBlock(padPos, FTBICElectricBlocks.CHARGE_PAD.block.get());
        var pad = helper.getBlockEntity(padPos, ChargePadBlockEntity.class);

        ItemStack diamond = new ItemStack(Items.DIAMOND);
        helper.assertTrue(
                BatterySlotHelper.foreignEnergyHandler(diamond) == null,
                "Items without FE are not treated as batteries");
        helper.assertFalse(
                machine.batteryInventory.isItemValid(0, diamond),
                "The battery slot rejects items without energy storage");
        helper.assertFalse(pad.isItemValid(0, diamond), "The charge pad rejects items without energy storage");

        ItemStack battery = new ItemStack(FTBICItems.LV_BATTERY.get());
        var item = (EnergyItemHandler) battery.getItem();
        item.setEnergy(battery, item.getEnergyCapacity(battery));
        helper.assertTrue(
                BatterySlotHelper.foreignEnergyHandler(battery) == null, "FTBIC batteries keep the native slot path");
        helper.assertTrue(machine.batteryInventory.isItemValid(0, battery), "The battery slot accepts FTBIC batteries");

        EnergyHandler fe = itemFE(battery);
        if (!FTBICConfig.ENERGY.FULL_FE_MODE.get()) {
            helper.assertTrue(fe == null, "Native mode keeps battery FE disabled");
            helper.succeed();
            return;
        }
        helper.assertTrue(fe != null, "Full FE mode exposes battery FE");

        machine.energy = 0D;
        double stored = item.getEnergy(battery);
        double maxTransfer = machine.getMaxInputEnergy() * FTBICConfig.MACHINES.ITEM_TRANSFER_EFFICIENCY.get();
        int expectedFE = Math.min(
                Math.min(
                        ZapFEConversion.zapsToFEFloor(Math.min(machine.energyCapacity, maxTransfer)),
                        tierLimit(EnergyTier.LV)),
                ZapFEConversion.zapsToFEFloor(stored));
        double drained = BatterySlotHelper.drainHandlerToBuffer(machine, fe, maxTransfer);
        helper.assertTrue(drained > 0D, "The battery slot drains an FE item");
        helper.assertTrue(
                approx(ZapFEConversion.feToZaps(expectedFE), drained),
                "The drained FE is converted to zaps (drained=" + drained + ")");
        helper.assertTrue(approx(drained, machine.energy), "Drained zaps enter the machine buffer");
        helper.assertTrue(approx(stored - drained, item.getEnergy(battery)), "The FE item loses the drained energy");

        ItemStack empty = new ItemStack(FTBICItems.LV_BATTERY.get());
        EnergyHandler target = itemFE(empty);
        double charged = BatterySlotHelper.chargeFE(target, 100D);
        helper.assertTrue(charged > 0D, "A charge slot charges an FE item");
        helper.assertTrue(
                approx(charged, item.getEnergy(empty)), "Charging an FE item costs exactly the zaps it stores");
        helper.succeed();
    }

    private static EnergyHandler itemFE(ItemStack stack) {
        return ItemAccess.forStack(stack).getCapability(Capabilities.Energy.ITEM);
    }

    private static int tierLimit(EnergyTier tier) {
        return ZapFEConversion.zapsToFEFloor(tier.transferRate() * FTBICConfig.MACHINES.ITEM_TRANSFER_EFFICIENCY.get());
    }

    private static int drainAll(EnergyHandler fe, TransactionContext tx) {
        int total = 0;
        for (int i = 0; i < 1_000; i++) {
            int extracted = fe.extract(Integer.MAX_VALUE, tx);
            if (extracted <= 0) break;
            total += extracted;
        }
        return total;
    }

    private static boolean approx(double a, double b) {
        return Math.abs(a - b) < 1.0E-6D;
    }
}
