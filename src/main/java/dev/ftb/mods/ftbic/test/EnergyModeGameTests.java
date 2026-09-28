package dev.ftb.mods.ftbic.test;

import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.block.ElectricBlockInstance;
import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.block.entity.ElectricBlockEntity;
import dev.ftb.mods.ftbic.block.entity.generator.NuclearReactorBlockEntity;
import dev.ftb.mods.ftbic.util.EnergyDisplay;
import dev.ftb.mods.ftbic.util.ZapFEConversion;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.RecipeManager;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.transaction.Transaction;

final class EnergyModeGameTests {
    static void rectifiersFollowEnergyMode(GameTestHelper helper) {
        boolean fullFE = FTBICConfig.ENERGY.FULL_FE_MODE.get();
        RecipeManager recipes = helper.getLevel().getServer().getRecipeManager();
        for (ElectricBlockInstance rectifier : FTBICElectricBlocks.RECTIFIERS) {
            var key = ResourceKey.create(Registries.RECIPE, FTBIC.id("shaped/" + rectifier.id));
            helper.assertValueEqual(!fullFE, recipes.byKey(key).isPresent(), rectifier.id + " recipe loaded");
            helper.assertValueEqual(fullFE, FTBICElectricBlocks.isHidden(rectifier), rectifier.id + " hidden");
        }
        helper.assertFalse(
                FTBICElectricBlocks.isHidden(FTBICElectricBlocks.EV_BATTERY_BOX), "Other electric blocks stay visible");
        helper.succeed();
    }

    static void directFEInputClamps(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 2, 1);
        helper.setBlock(pos, FTBICElectricBlocks.MACERATOR.block.get());
        var machine = helper.getBlockEntity(pos, ElectricBlockEntity.class);
        var fe = helper.getLevel().getCapability(Capabilities.Energy.BLOCK, helper.absolutePos(pos), Direction.UP);
        if (!FTBICConfig.ENERGY.FULL_FE_MODE.get()) {
            helper.assertTrue(fe == null, "Native mode keeps machine FE input disabled");
            helper.succeed();
            return;
        }
        helper.assertTrue(fe != null, "Full FE mode exposes machine FE input");
        machine.energy = 0D;
        try (var tx = Transaction.openRoot()) {
            fe.insert(Integer.MAX_VALUE, tx);
        }
        helper.assertFalse(machine.isBurnt(), "An aborted FE offer above the input rate does not burn the machine");
        helper.assertValueEqual(0D, machine.energy, "An aborted FE offer leaves no energy behind");

        int expected = Math.min(
                ZapFEConversion.zapsToFEFloor(machine.getMaxInputEnergy()),
                ZapFEConversion.zapsToFEFloor(machine.energyCapacity));
        try (var tx = Transaction.openRoot()) {
            helper.assertValueEqual(
                    expected, fe.insert(Integer.MAX_VALUE, tx), "FE input is clamped to the input rate");
            tx.commit();
        }
        helper.assertFalse(machine.isBurnt(), "A committed FE offer above the input rate does not burn the machine");
        helper.succeed();
    }

    static void reactorExposesFE(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 2, 1);
        helper.setBlock(pos, FTBICElectricBlocks.NUCLEAR_REACTOR.block.get());
        var reactor = helper.getBlockEntity(pos, NuclearReactorBlockEntity.class);
        var fe = helper.getLevel().getCapability(Capabilities.Energy.BLOCK, helper.absolutePos(pos), Direction.UP);
        helper.assertTrue(fe != null, "The nuclear reactor exposes FE in both energy modes");
        reactor.energy = 1000D;
        try (var tx = Transaction.openRoot()) {
            helper.assertTrue(fe.extract(Integer.MAX_VALUE, tx) > 0, "Other mods can pull FE from the reactor");
            helper.assertValueEqual(0, fe.insert(1000, tx), "The reactor does not accept FE");
        }
        helper.succeed();
    }

    static void energyDisplayFollowsMode(GameTestHelper helper) {
        boolean fullFE = FTBICConfig.ENERGY.FULL_FE_MODE.get();
        double rate = ZapFEConversion.rate();
        helper.assertValueEqual(fullFE, EnergyDisplay.isFE(), "Display mode follows the config");
        helper.assertValueEqual(
                fullFE ? 128D * rate : 128D, EnergyDisplay.convert(128D), "Display values follow the config");
        helper.assertTrue(
                EnergyDisplay.perTick(128D).getString().endsWith(fullFE ? "FE/t" : "z/t"),
                "Rate unit follows the config");

        EnergyDisplay.sync(!fullFE, 4D);
        try {
            helper.assertValueEqual(!fullFE, EnergyDisplay.isFE(), "The synced server mode overrides the local config");
            helper.assertValueEqual(
                    !fullFE ? 512D : 128D, EnergyDisplay.convert(128D), "The synced conversion rate is used");
            helper.assertTrue(
                    EnergyDisplay.amount(128D).getString().endsWith(!fullFE ? "FE" : "zaps"),
                    "Amount unit follows the synced mode");
        } finally {
            EnergyDisplay.clearSync();
        }
        helper.assertValueEqual(fullFE, EnergyDisplay.isFE(), "Clearing the sync restores the local config");
        helper.succeed();
    }
}
