package dev.ftb.mods.ftbic.test;

import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.block.CableBlock;
import dev.ftb.mods.ftbic.block.FTBICBlocks;
import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.block.entity.ElectricBlockEntity;
import dev.ftb.mods.ftbic.block.entity.SuperconductingCableBlockEntity;
import dev.ftb.mods.ftbic.util.SideConfiguration;
import dev.ftb.mods.ftbic.util.ZapFEConversion;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.transaction.Transaction;

final class CableFEGameTests {
    static void cableVariants(GameTestHelper helper) {
        BlockPos input = new BlockPos(1, 2, 2);
        BlockPos target = input.east();
        helper.setBlock(target, FTBICElectricBlocks.MACERATOR.block.get());
        var machine = helper.getBlockEntity(target, ElectricBlockEntity.class);
        for (var entry : Stream.concat(FTBICBlocks.CABLES.stream(), FTBICBlocks.REINFORCED_CABLES.stream())
                .toList()) {
            var state = entry.get().defaultBlockState();
            for (var connection : CableBlock.CONNECTION) state = state.setValue(connection, true);
            helper.setBlock(input, state);
            var cable = helper.getLevel()
                    .getCapability(Capabilities.Energy.BLOCK, helper.absolutePos(input), Direction.WEST);
            if (!FTBICConfig.ENERGY.FULL_FE_MODE.get()) {
                helper.assertTrue(cable == null, "All cable variants keep FE input disabled in native mode");
                continue;
            }
            helper.assertTrue(cable != null, "Every cable variant exposes FE input");
            try (var tx = Transaction.openRoot()) {
                helper.assertTrue(cable.insert(Integer.MAX_VALUE, tx) > 0, "Every cable variant routes external FE");
            }
            helper.assertValueEqual(0D, machine.getEnergy(), "Variant simulation rolls back");
            helper.assertFalse(machine.isBurnt(), "Higher tiers respect machine input limits");
            if (helper.getLevel().getBlockEntity(helper.absolutePos(input))
                    instanceof SuperconductingCableBlockEntity beam) {
                helper.assertFalse(beam.isTransferring(), "Simulation does not pulse superconducting cable");
                try (var tx = Transaction.openRoot()) {
                    cable.insert(Integer.MAX_VALUE, tx);
                    tx.commit();
                }
                helper.assertTrue(beam.isTransferring(), "Committed FE transfer pulses superconducting cable");
                machine.setEnergyRaw(0D);
            }
        }
        helper.succeed();
    }

    static void cachedLookupSeesPlacedCable(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 2, 2);
        for (var entry : Stream.concat(FTBICBlocks.CABLES.stream(), FTBICBlocks.REINFORCED_CABLES.stream())
                .toList()) {
            helper.setBlock(pos, Blocks.AIR);
            var cache = BlockCapabilityCache.create(
                    Capabilities.Energy.BLOCK, helper.getLevel(), helper.absolutePos(pos), Direction.WEST);
            helper.assertTrue(cache.getCapability() == null, "Empty position exposes no FE capability");
            helper.setBlock(pos, entry.get());
            if (!FTBICConfig.ENERGY.FULL_FE_MODE.get()) {
                helper.assertTrue(cache.getCapability() == null, "Native mode keeps cable FE input disabled");
                continue;
            }
            helper.assertTrue(
                    cache.getCapability() != null, "Placing " + entry.getId() + " refreshes cached FE lookups");
            helper.setBlock(pos, Blocks.AIR);
            helper.assertTrue(
                    cache.getCapability() == null, "Removing " + entry.getId() + " refreshes cached FE lookups");
        }
        helper.succeed();
    }

    static void externalInput(GameTestHelper helper) {
        BlockPos input = new BlockPos(1, 2, 2);
        BlockPos middle = input.east();
        BlockPos target = middle.east();
        helper.setBlock(target, FTBICElectricBlocks.MACERATOR.block.get());
        var state = FTBICBlocks.LV_CABLE.get().defaultBlockState();
        for (var connection : CableBlock.CONNECTION) state = state.setValue(connection, true);
        helper.setBlock(middle, state);
        helper.setBlock(input, state);
        var cable =
                helper.getLevel().getCapability(Capabilities.Energy.BLOCK, helper.absolutePos(input), Direction.WEST);
        if (!FTBICConfig.ENERGY.FULL_FE_MODE.get()) {
            helper.assertTrue(cable == null, "Native mode does not expose cable FE input");
            helper.succeed();
            return;
        }
        helper.assertTrue(cable != null, "External sources can find the cable FE capability");
        var machine = helper.getBlockEntity(target, ElectricBlockEntity.class);
        int limit = ZapFEConversion.zapsToFEFloor(FTBICConfig.ENERGY.LV_TRANSFER_RATE.get());
        try (var tx = Transaction.openRoot()) {
            helper.assertValueEqual(
                    limit, cable.insert(Integer.MAX_VALUE, tx), "Creative source offer is capped by cable tier");
        }
        helper.assertValueEqual(0D, machine.getEnergy(), "Simulation leaves the receiver empty");
        helper.assertFalse(machine.isBurnt(), "Large FE offers do not burn the machine");
        try (var tx = Transaction.openRoot()) {
            try (var nested = Transaction.open(tx)) {
                cable.insert(limit, nested);
                nested.commit();
            }
        }
        helper.assertValueEqual(0D, machine.getEnergy(), "Parent rollback reverts nested insertion");
        try (var tx = Transaction.openRoot()) {
            helper.assertValueEqual(limit, cable.insert(Integer.MAX_VALUE, tx), "Committed insertion accepts energy");
            tx.commit();
        }
        helper.assertValueEqual(
                FTBICConfig.ENERGY.LV_TRANSFER_RATE.get(),
                machine.getEnergy(),
                "FE reaches the machine through two cables");
        SideConfiguration disabled = SideConfiguration.DEFAULT;
        for (var face : SideConfiguration.Face.values()) {
            disabled = disabled.with(SideConfiguration.Resource.ENERGY, face, SideConfiguration.Mode.DISABLED);
        }
        machine.setSideConfiguration(disabled);
        try (var tx = Transaction.openRoot()) {
            helper.assertValueEqual(0, cable.insert(limit, tx), "Disabled receiver rejects FE");
        }
        machine.setSideConfiguration(SideConfiguration.DEFAULT);
        helper.setBlock(middle, Blocks.AIR);
        try (var tx = Transaction.openRoot()) {
            helper.assertValueEqual(
                    0, cable.insert(limit, tx), "Cached cable capability sees broken routes immediately");
        }
        helper.setBlock(middle, FTBICBlocks.MV_CABLE.get());
        try (var tx = Transaction.openRoot()) {
            helper.assertValueEqual(0, cable.insert(limit, tx), "Mismatched cable tiers do not bridge");
        }
        helper.succeed();
    }
}
