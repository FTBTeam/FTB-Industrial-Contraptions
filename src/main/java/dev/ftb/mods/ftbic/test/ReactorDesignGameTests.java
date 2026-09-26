package dev.ftb.mods.ftbic.test;

import com.mojang.authlib.GameProfile;
import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.block.entity.generator.NuclearReactorBlockEntity;
import dev.ftb.mods.ftbic.block.entity.machine.ReactorSimulatorBlockEntity;
import dev.ftb.mods.ftbic.item.FTBICItems;
import dev.ftb.mods.ftbic.item.ReactorBlueprintItem;
import dev.ftb.mods.ftbic.item.reactor.NuclearReactor;
import dev.ftb.mods.ftbic.registry.ModDataComponents;
import dev.ftb.mods.ftbic.screen.NuclearReactorMenu;
import dev.ftb.mods.ftbic.util.ReactorDesign;
import java.util.Arrays;
import java.util.List;
import java.util.OptionalInt;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.storage.TagValueInput;

final class ReactorDesignGameTests {
    private static final BlockPos POS = new BlockPos(2, 2, 2);

    static void fuelRodBaseOutput(GameTestHelper helper) {
        Item[] rods = {
            FTBICItems.URANIUM_FUEL_ROD.get(),
            FTBICItems.DUAL_URANIUM_FUEL_ROD.get(),
            FTBICItems.QUAD_URANIUM_FUEL_ROD.get()
        };
        int[] expectedEnergy = {20, 80, 240};
        int[] expectedHeat = {4, 24, 96};
        for (int i = 0; i < rods.length; i++) {
            ItemStack[] items = new ItemStack[NuclearReactor.MAX_SLOTS];
            Arrays.fill(items, ItemStack.EMPTY);
            NuclearReactor reactor = new NuclearReactor(items);
            reactor.paused = false;
            reactor.setAt(0, 0, new ItemStack(rods[i]));
            reactor.tick();
            helper.assertValueEqual((double) expectedEnergy[i], reactor.energyOutput, "Fuel rod base output");
            helper.assertValueEqual(expectedHeat[i], reactor.heat, "Fuel rod heat remains unchanged");
        }
        helper.succeed();
    }

    private static ReactorDesign design(int chambers, ReactorDesign.DesignSlot... slots) {
        return new ReactorDesign(1, chambers, 0, List.of(slots));
    }

    private static ReactorDesign.DesignSlot slot(int slot, Item item) {
        return new ReactorDesign.DesignSlot(slot, BuiltInRegistries.ITEM.getKey(item));
    }

    static void fillsWithoutReplacingOrLosingData(GameTestHelper helper) {
        helper.setBlock(POS, FTBICElectricBlocks.NUCLEAR_REACTOR.block.get());
        var reactor = helper.getBlockEntity(POS, NuclearReactorBlockEntity.class);
        var inventory = helper.makeMockPlayer(GameType.SURVIVAL).getInventory();
        var rod = FTBICItems.URANIUM_FUEL_ROD.get();
        var vent = FTBICItems.HEAT_VENT.get();
        var plan = design(0, slot(0, rod), slot(1, vent), slot(9, vent), slot(10, rod));
        helper.assertTrue(reactor.setPlannedDesign(plan), "Valid design accepted");
        ItemStack usedRods = new ItemStack(rod, 5);
        usedRods.setDamageValue(123);
        usedRods.set(DataComponents.CUSTOM_NAME, Component.literal("Used fuel"));
        inventory.setItem(0, usedRods);
        inventory.setItem(1, new ItemStack(vent));
        ItemStack conflict = new ItemStack(FTBICItems.REACTOR_PLATING.get());
        reactor.setStackInSlot(9, conflict);
        helper.assertValueEqual(3, reactor.buildPlannedDesign(inventory), "Filled three empty slots");
        helper.assertValueEqual(3, inventory.getItem(0).getCount(), "Exactly two rods consumed");
        helper.assertTrue(inventory.getItem(1).isEmpty(), "Exactly one vent consumed");
        helper.assertTrue(reactor.getStackInSlot(9) == conflict, "Conflicting part is untouched");
        helper.assertValueEqual(1, reactor.getStackInSlot(0).getCount(), "One item per reactor slot");
        helper.assertTrue(
                ItemStack.isSameItemSameComponents(reactor.getStackInSlot(0), usedRods),
                "Wear and custom data preserved");
        helper.assertValueEqual(0, reactor.buildPlannedDesign(inventory), "Repeated build consumes nothing");
        reactor.setStackInSlot(9, ItemStack.EMPTY);
        helper.assertValueEqual(0, reactor.buildPlannedDesign(inventory), "Missing material leaves slot empty");
        inventory.setItem(2, new ItemStack(vent, 4));
        helper.assertValueEqual(1, reactor.buildPlannedDesign(inventory), "Later build fills only the missing slot");
        helper.assertValueEqual(3, inventory.getItem(2).getCount(), "Remainder stays in inventory");
        helper.succeed();
    }

    static void enforcesBuildRequirements(GameTestHelper helper) {
        helper.setBlock(POS, FTBICElectricBlocks.NUCLEAR_REACTOR.block.get());
        var reactor = helper.getBlockEntity(POS, NuclearReactorBlockEntity.class);
        var inventory = helper.makeMockPlayer(GameType.SURVIVAL).getInventory();
        var rod = FTBICItems.URANIUM_FUEL_ROD.get();
        inventory.setItem(0, new ItemStack(rod, 10));
        reactor.setPlannedDesign(design(0, slot(0, rod)));
        reactor.reactor.paused = false;
        helper.assertValueEqual(-1, reactor.buildPlannedDesign(inventory), "Running reactor blocks build");
        reactor.reactor.paused = true;
        reactor.reactor.allowRedstoneControl = true;
        helper.assertValueEqual(-1, reactor.buildPlannedDesign(inventory), "Redstone control blocks build");
        reactor.reactor.allowRedstoneControl = false;
        reactor.setPlannedDesign(design(1, slot(3, rod)));
        helper.assertValueEqual(-2, reactor.buildPlannedDesign(inventory), "Insufficient chambers block entire build");
        helper.assertValueEqual(10, inventory.getItem(0).getCount(), "Rejected builds consume nothing");
        helper.assertTrue(reactor.getStackInSlot(0).isEmpty(), "Rejected builds leave reactor untouched");
        helper.succeed();
    }

    static void persistsBlueprintAndPreview(GameTestHelper helper) {
        helper.setBlock(POS, FTBICElectricBlocks.NUCLEAR_REACTOR.block.get());
        var reactor = helper.getBlockEntity(POS, NuclearReactorBlockEntity.class);
        var inventory = helper.makeMockPlayer(GameType.SURVIVAL).getInventory();
        var plan = design(0, slot(0, FTBICItems.URANIUM_FUEL_ROD.get()));
        inventory.setItem(0, new ItemStack(FTBICItems.REACTOR_BLUEPRINT.get()));
        helper.assertTrue(ReactorBlueprintItem.hasBlank(inventory), "Blank blueprint is detected before writing");
        helper.assertTrue(ReactorBlueprintItem.writeBlank(inventory, plan), "Writes owned blank blueprint");
        helper.assertFalse(
                ReactorBlueprintItem.hasBlank(inventory), "Filled blueprint is not mistaken for a blank one");
        helper.assertFalse(
                ReactorBlueprintItem.writeBlank(inventory, design(0)),
                "Write button never overwrites a filled blueprint");
        var ops = helper.getLevel().registryAccess().createSerializationContext(NbtOps.INSTANCE);
        var encoded = ItemStack.CODEC.encodeStart(ops, inventory.getItem(0)).getOrThrow();
        var reloaded = ItemStack.CODEC.parse(ops, encoded).getOrThrow();
        helper.assertValueEqual(
                plan, reloaded.get(ModDataComponents.REACTOR_DESIGN.get()), "Blueprint design survives item save/load");
        reactor.setPlannedDesign(reloaded.get(ModDataComponents.REACTOR_DESIGN.get()));
        helper.assertTrue(reactor.getStackInSlot(0).isEmpty(), "Loading blueprint creates no real components");
        var tag = reactor.saveCustomOnly(helper.getLevel().registryAccess());
        reactor.setPlannedDesign(null);
        reactor.loadCustomOnly(TagValueInput.create(
                ProblemReporter.DISCARDING, helper.getLevel().registryAccess(), tag));
        helper.assertValueEqual(plan, reactor.getPlannedDesign(), "Preview survives reactor save/load");
        helper.assertValueEqual(1, reloaded.getCount(), "Blueprint is reusable");
        helper.succeed();
    }

    static void rejectsInvalidDesigns(GameTestHelper helper) {
        var rod = FTBICItems.URANIUM_FUEL_ROD.get();
        helper.assertFalse(design(0, slot(0, Items.DIAMOND)).isValid(), "Non-reactor items rejected");
        helper.assertFalse(design(0, slot(0, rod), slot(0, rod)).isValid(), "Duplicate slots rejected");
        helper.assertFalse(design(0, slot(3, rod)).isValid(), "Slots outside design's chambers rejected");
        helper.assertFalse(design(6, slot(54, rod)).isValid(), "Out-of-bounds slots rejected");
        helper.assertFalse(new ReactorDesign(2, 0, 0, List.of()).isValid(), "Unknown version rejected");
        helper.assertFalse(new ReactorDesign(1, 0, Double.NaN, List.of()).isValid(), "Non-finite cooling rejected");
        var plan = design(0, slot(0, rod));
        helper.assertValueEqual(plan, ReactorDesign.fromJson(plan.toJson()), "Valid exported design round trips");
        helper.setBlock(POS, FTBICElectricBlocks.REACTOR_SIMULATOR.block.get());
        var planner = helper.getBlockEntity(POS, ReactorSimulatorBlockEntity.class);
        planner.setSlotItem(0, new ItemStack(rod));
        helper.assertFalse(
                planner.applyDesign(design(0, slot(0, Items.DIAMOND))), "Planner rejects invalid blueprint too");
        helper.assertTrue(planner.inputItems[0].is(rod), "Invalid design does not reset planner");
        helper.succeed();
    }

    static void blueprintUseLoadsAndCopies(GameTestHelper helper) {
        helper.setBlock(POS, FTBICElectricBlocks.REACTOR_SIMULATOR.block.get());
        BlockPos reactorPos = POS.offset(2, 0, 0);
        helper.setBlock(reactorPos, FTBICElectricBlocks.NUCLEAR_REACTOR.block.get());
        var planner = helper.getBlockEntity(POS, ReactorSimulatorBlockEntity.class);
        var reactor = helper.getBlockEntity(reactorPos, NuclearReactorBlockEntity.class);
        var cookie = CommonListenerCookie.createInitial(new GameProfile(UUID.randomUUID(), "blueprint-test"), false);
        var player =
                new ServerPlayer(
                        helper.getLevel().getServer(),
                        helper.getLevel(),
                        cookie.gameProfile(),
                        cookie.clientInformation()) {
                    @Override
                    public void sendOverlayMessage(Component message) {}

                    @Override
                    public OptionalInt openMenu(
                            MenuProvider provider, Consumer<RegistryFriendlyByteBuf> extraDataWriter) {
                        // Exercise menu selection without a client connection or its login handshake.
                        containerMenu = provider.createMenu(1, getInventory(), this);
                        return OptionalInt.of(1);
                    }
                };
        try {
            player.setPos(helper.absolutePos(POS).getCenter());
            planner.setSlotItem(0, new ItemStack(FTBICItems.URANIUM_FUEL_ROD.get()));
            ItemStack blueprint = new ItemStack(FTBICItems.REACTOR_BLUEPRINT.get());
            player.setItemInHand(InteractionHand.MAIN_HAND, blueprint);
            helper.useBlock(POS, player);
            helper.assertValueEqual(
                    planner.exportDesign(),
                    blueprint.get(ModDataComponents.REACTOR_DESIGN.get()),
                    "Using a blank blueprint on planner writes the design");
            helper.useBlock(reactorPos, player);
            helper.assertValueEqual(
                    planner.exportDesign(), reactor.getPlannedDesign(), "Using blueprint on reactor loads the preview");
            helper.assertTrue(player.containerMenu instanceof NuclearReactorMenu, "Blueprint opens reactor GUI");
            helper.assertTrue(reactor.inputItems[0].isEmpty(), "Blueprint use never creates components");
            planner.reset();
            helper.useBlock(POS, player);
            helper.assertTrue(
                    planner.inputItems[0].is(FTBICItems.URANIUM_FUEL_ROD.get()),
                    "Filled blueprint loads back into planner");
            planner.setSlotItem(1, new ItemStack(FTBICItems.HEAT_VENT.get()));
            player.setShiftKeyDown(true);
            helper.useBlock(POS, player);
            helper.assertValueEqual(
                    2,
                    blueprint
                            .get(ModDataComponents.REACTOR_DESIGN.get())
                            .slots()
                            .size(),
                    "Sneak-use overwrites blueprint");
            helper.assertValueEqual(1, blueprint.getCount(), "Using and rewriting blueprint does not consume it");
        } finally {
            player.discard();
        }
        helper.succeed();
    }

    static void quickMoveRespectsActiveSlots(GameTestHelper helper) {
        helper.setBlock(POS, FTBICElectricBlocks.NUCLEAR_REACTOR.block.get());
        var reactor = helper.getBlockEntity(POS, NuclearReactorBlockEntity.class);
        var player = helper.makeMockPlayer(GameType.SURVIVAL);
        var inventory = player.getInventory();
        ItemStack rods = new ItemStack(FTBICItems.URANIUM_FUEL_ROD.get(), 32);
        rods.setDamageValue(123);
        inventory.setItem(9, rods);
        var menu = new NuclearReactorMenu(1, inventory, reactor);
        // Three active columns by six rows occupy the first 18 menu slots.
        menu.clicked(18, 0, ContainerInput.QUICK_MOVE, player);
        int installed = 0;
        for (int i = 0; i < reactor.inputItems.length; i++) {
            ItemStack item = reactor.inputItems[i];
            if (i % 9 >= 3) helper.assertTrue(item.isEmpty(), "Inactive column remains empty");
            if (!item.isEmpty()) {
                helper.assertValueEqual(1, item.getCount(), "Shift-click inserts one per slot");
                helper.assertValueEqual(123, item.getDamageValue(), "Shift-click preserves wear");
                installed++;
            }
        }
        helper.assertValueEqual(18, installed, "Shift-click fills all active slots");
        helper.assertValueEqual(14, inventory.getItem(9).getCount(), "Remaining items stay in player inventory");
        helper.succeed();
    }
}
