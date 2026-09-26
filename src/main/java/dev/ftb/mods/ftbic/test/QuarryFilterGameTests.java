package dev.ftb.mods.ftbic.test;

import com.mojang.authlib.GameProfile;
import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.block.entity.machine.QuarryBlockEntity;
import dev.ftb.mods.ftbic.item.FTBICItems;
import dev.ftb.mods.ftbic.net.QuarryFilterPayload;
import dev.ftb.mods.ftbic.net.SetGhostIngredientPayload;
import dev.ftb.mods.ftbic.registry.ModDataComponents;
import dev.ftb.mods.ftbic.screen.QuarryMenu;
import dev.ftb.mods.ftbic.screen.UpgradeInventoryContainer;
import dev.ftb.mods.ftbic.screen.UpgradeSlot;
import dev.ftb.mods.ftbic.util.QuarryFilter;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.TagValueInput;
import net.neoforged.neoforge.fluids.FluidStack;

final class QuarryFilterGameTests {
    private static final BlockPos POS = new BlockPos(3, 5, 3);

    private static QuarryBlockEntity quarry(GameTestHelper h, boolean upgrade) {
        h.setBlock(POS, FTBICElectricBlocks.QUARRY.block.get());
        var quarry = h.getBlockEntity(POS, QuarryBlockEntity.class);
        quarry.offsetX = 0;
        quarry.offsetZ = 0;
        quarry.sizeX = 4;
        quarry.sizeZ = 3;
        if (upgrade) quarry.upgradeInventory.setStackInSlot(0, new ItemStack(FTBICItems.QUARRY_FILTER_UPGRADE.get()));
        quarry.diggingMineTicks = 1;
        quarry.diggingMoveTicks = 1;
        quarry.energy = quarry.getEnergyCapacity();
        return quarry;
    }

    static void selection(GameTestHelper h) {
        var filter = QuarryFilter.DEFAULT.withBlock(0, new ItemStack(Items.IRON_ORE));
        h.assertTrue(filter.matches(Blocks.IRON_ORE.defaultBlockState()), "Whitelist includes selected block");
        h.assertFalse(filter.matches(Blocks.STONE.defaultBlockState()), "Whitelist leaves stone intact");
        h.assertFalse(
                filter.withWhitelist(false).matches(Blocks.IRON_ORE.defaultBlockState()),
                "Blacklist excludes selected block");
        h.assertTrue(
                filter.withWhitelist(false).matches(Blocks.STONE.defaultBlockState()), "Blacklist mines other blocks");
        h.assertTrue(
                filter.withTag(0, "c:ores").matches(Blocks.COAL_ORE.defaultBlockState()), "Block tag includes ore");
        h.assertTrue(
                filter.withOreOnly(true).matches(Blocks.IRON_ORE.defaultBlockState()),
                "Ore-only recognizes shared ore tag");
        h.assertFalse(filter.withOreOnly(true).matches(Blocks.STONE.defaultBlockState()), "Ore-only rejects stone");
        h.assertTrue(
                QuarryFilter.DEFAULT.matches(Blocks.STONE.defaultBlockState()),
                "Blank filter leaves selection unrestricted");
        var quarry = quarry(h, true);
        quarry.setFilter(filter);
        var tag = quarry.saveCustomOnly(h.getLevel().registryAccess());
        quarry.setFilter(QuarryFilter.DEFAULT);
        quarry.loadCustomOnly(
                TagValueInput.create(ProblemReporter.DISCARDING, h.getLevel().registryAccess(), tag));
        h.assertValueEqual(filter, quarry.getFilter(), "Filter survives block entity save and load");
        h.succeed();
    }

    static void freeSkipAndDepth(GameTestHelper h) {
        var quarry = quarry(h, true);
        var first = new BlockPos(4, 4, 4);
        var second = new BlockPos(5, 4, 4);
        h.setBlock(first, Blocks.STONE);
        h.setBlock(second, Blocks.STONE);
        h.setBlock(second.below(), Blocks.IRON_ORE);
        quarry.setFilter(QuarryFilter.DEFAULT.withBlock(0, new ItemStack(Items.IRON_ORE)));
        double before = quarry.energy;
        quarry.tick();
        h.assertTrue(
                before - quarry.energy == quarry.energyUse, "Unmatched column skipped without spending mining energy");
        h.assertValueEqual(3L, quarry.tick, "Scan advances past first column and starts second");
        quarry.tick();
        h.assertTrue(h.getBlockState(first).is(Blocks.STONE), "First unmatched stone remains");
        h.assertTrue(h.getBlockState(second).is(Blocks.STONE), "Stone over matching ore remains");
        h.assertTrue(h.getBlockState(second.below()).isAir(), "Quarry reaches and mines matching ore below stone");
        quarry.tick();
        h.assertTrue(
                quarry.paused && before - quarry.energy == 2 * quarry.energyUse,
                "Finished survey pauses without charging for empty columns");
        h.succeed();
    }

    static void noTargetAndUpgrade(GameTestHelper h) {
        var quarry = quarry(h, false);
        var upgrade = new ItemStack(FTBICItems.QUARRY_FILTER_UPGRADE.get());
        h.assertTrue(quarry.upgradeInventory.isItemValid(0, upgrade), "Quarry accepts filter upgrade");
        quarry.upgradeInventory.setStackInSlot(0, upgrade);
        var slot = new UpgradeSlot(new UpgradeInventoryContainer(quarry.upgradeInventory), 1, 0, 0);
        h.assertValueEqual(0, slot.getMaxStackSize(upgrade), "Second upgrade rejected across slots");
        quarry.setFilter(QuarryFilter.DEFAULT.withBlock(0, new ItemStack(Items.IRON_ORE)));
        var first = new BlockPos(4, 4, 4);
        h.setBlock(first, Blocks.STONE);
        double before = quarry.energy;
        quarry.tick();
        h.assertTrue(quarry.paused && before == quarry.energy, "No matching blocks pause without charging");
        h.assertTrue(h.getBlockState(first).is(Blocks.STONE), "No match leaves world unchanged");
        quarry.upgradeInventory.setStackInSlot(0, ItemStack.EMPTY);
        h.assertFalse(quarry.hasFilterUpgrade(), "Removing upgrade disables filter");
        h.assertValueEqual(
                Items.IRON_ORE, quarry.getFilter().blockAt(0).getItem(), "Configuration remains for later use");
        h.succeed();
    }

    static void packetsAndCard(GameTestHelper h) {
        var quarry = quarry(h, true);
        var cookie =
                CommonListenerCookie.createInitial(new GameProfile(UUID.randomUUID(), "quarry-filter-test"), false);
        var player =
                new ServerPlayer(
                        h.getLevel().getServer(), h.getLevel(), cookie.gameProfile(), cookie.clientInformation()) {
                    @Override
                    public void sendOverlayMessage(Component message) {}
                };
        player.setPos(quarry.getBlockPos().getCenter());
        var menu = new QuarryMenu(11, player.getInventory(), quarry);
        player.containerMenu = menu;
        menu.setCarried(new ItemStack(Items.IRON_ORE));
        h.assertTrue(
                QuarryFilterPayload.apply(player, new QuarryFilterPayload(11, QuarryFilterPayload.SET_BLOCK, 0, "")),
                "Cursor block becomes ghost filter");
        h.assertTrue(menu.getCarried().is(Items.IRON_ORE), "Ghost assignment consumes no item");
        h.assertTrue(
                QuarryFilterPayload.apply(
                        player, new QuarryFilterPayload(11, QuarryFilterPayload.SET_TAG, 1, "#c:ores/iron")),
                "Tag field accepts leading hash");
        h.assertValueEqual("c:ores/iron", quarry.getFilter().tags().get(1), "Tag name stored canonically");
        h.assertTrue(
                QuarryFilterPayload.apply(player, new QuarryFilterPayload(11, QuarryFilterPayload.TOGGLE_ORES, 0, "")),
                "Ore toggle applied");
        h.assertTrue(
                SetGhostIngredientPayload.apply(
                        player, new SetGhostIngredientPayload(11, 10, new ItemStack(Items.COAL_ORE), FluidStack.EMPTY)),
                "JEI ghost can target second slot");
        h.assertValueEqual(Items.COAL_ORE, quarry.getFilter().blockAt(1).getItem(), "JEI block sample persisted");
        h.assertFalse(
                QuarryFilterPayload.apply(player, new QuarryFilterPayload(99, QuarryFilterPayload.CLEAR_BLOCK, 0, "")),
                "Wrong menu rejected");
        h.assertFalse(
                QuarryFilterPayload.apply(
                        player, new QuarryFilterPayload(11, QuarryFilterPayload.SET_TAG, 5, "c:ores")),
                "Out-of-range tag slot rejected");
        h.assertFalse(
                QuarryFilterPayload.apply(
                        player, new QuarryFilterPayload(11, QuarryFilterPayload.SET_TAG, 0, "Invalid Tag")),
                "Malformed tag rejected");
        h.assertFalse(
                SetGhostIngredientPayload.apply(
                        player,
                        new SetGhostIngredientPayload(11, 9, ItemStack.EMPTY, new FluidStack(Fluids.WATER, 1000))),
                "Fluid drag rejected");
        var copyPos = POS.east(5);
        h.setBlock(copyPos, FTBICElectricBlocks.QUARRY.block.get());
        var copy = h.getBlockEntity(copyPos, QuarryBlockEntity.class);
        var card = new ItemStack(FTBICItems.CONFIGURATION_CARD.get());
        player.setItemInHand(InteractionHand.MAIN_HAND, card);
        player.setShiftKeyDown(true);
        h.useBlock(POS, player);
        player.setShiftKeyDown(false);
        h.useBlock(copyPos, player);
        h.assertValueEqual(quarry.getFilter(), copy.getFilter(), "Configuration Card copies all filter rules");
        h.assertValueEqual(
                quarry.getFilter(),
                card.get(ModDataComponents.MACHINE_CONFIGURATION.get()).quarryFilter(),
                "Card stores filter data");
        player.discard();
        h.succeed();
    }
}
