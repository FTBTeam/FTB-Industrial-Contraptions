package dev.ftb.mods.ftbic.test;

import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.block.ElectricBlockInstance;
import dev.ftb.mods.ftbic.block.FTBICBlocks;
import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.block.entity.SuperconductingCableBlockEntity;
import dev.ftb.mods.ftbic.block.entity.generator.GeneratorBlockEntity;
import dev.ftb.mods.ftbic.block.entity.machine.BasicMachineBlockEntity;
import dev.ftb.mods.ftbic.block.entity.storage.BankCellBlockEntity;
import dev.ftb.mods.ftbic.block.entity.storage.BankPortBlockEntity;
import dev.ftb.mods.ftbic.block.entity.storage.BankTopology;
import dev.ftb.mods.ftbic.item.BatteryItem;
import dev.ftb.mods.ftbic.item.FTBICItems;
import dev.ftb.mods.ftbic.recipe.RecipeToggleCondition;
import dev.ftb.mods.ftbic.registry.ModDataComponents;
import dev.ftb.mods.ftbic.screen.BankMenu;
import dev.ftb.mods.ftbic.screen.ChargeSlot;
import dev.ftb.mods.ftbic.util.GhostItem;
import dev.ftb.mods.ftbic.util.MachineConfiguration;
import dev.ftb.mods.ftbic.util.SideConfiguration;
import io.netty.buffer.Unpooled;
import java.util.List;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.TagValueInput;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.fluids.SimpleFluidContent;

final class ReviewFixGameTests {
    private static final BlockPos POS = new BlockPos(1, 2, 1);

    static void batteriesUseConfiguredCharge(GameTestHelper helper) {
        ItemStack singleUse = new ItemStack(FTBICItems.SINGLE_USE_BATTERY.get());
        BatteryItem singleUseItem = (BatteryItem) singleUse.getItem();
        double singleUseCapacity = FTBICConfig.ENERGY.SINGLE_USE_BATTERY_CAPACITY.get();
        helper.assertValueEqual(
                singleUseCapacity,
                singleUseItem.getEnergyCapacity(singleUse),
                "Single-use capacity follows the config");
        helper.assertValueEqual(
                singleUseCapacity, singleUseItem.getEnergy(singleUse), "A fresh single-use battery is fully charged");
        helper.assertValueEqual(
                singleUseCapacity,
                singleUseItem.extractEnergy(singleUse, singleUseCapacity * 2D, true),
                "A fresh single-use battery can discharge its full charge");

        ItemStack lv = new ItemStack(FTBICItems.LV_BATTERY.get());
        BatteryItem lvItem = (BatteryItem) lv.getItem();
        helper.assertValueEqual(
                FTBICConfig.ENERGY.LV_BATTERY_CAPACITY.get(),
                lvItem.getEnergyCapacity(lv),
                "LV battery capacity follows the config");
        helper.assertValueEqual(0D, lvItem.getEnergy(lv), "A fresh rechargeable battery starts empty");
        helper.succeed();
    }

    static void bankBlocksDropThemselves(GameTestHelper helper) {
        for (ElectricBlockInstance instance :
                List.of(FTBICElectricBlocks.INDUSTRIAL_BANK_CELL, FTBICElectricBlocks.INDUSTRIAL_BANK_PORT)) {
            helper.setBlock(POS, instance.block.get());
            BlockPos abs = helper.absolutePos(POS);
            List<ItemStack> drops = Block.getDrops(
                    helper.getLevel().getBlockState(abs),
                    helper.getLevel(),
                    abs,
                    helper.getLevel().getBlockEntity(abs));
            helper.assertTrue(
                    drops.stream().anyMatch(stack -> stack.is(instance.item.get())),
                    instance.id + " drops itself when broken");
            helper.setBlock(POS, Blocks.AIR);
        }
        helper.succeed();
    }

    static void upgradedEnergySurvivesReload(GameTestHelper helper) {
        helper.setBlock(POS, FTBICElectricBlocks.MACERATOR.block.get());
        BasicMachineBlockEntity machine = helper.getBlockEntity(POS, BasicMachineBlockEntity.class);
        double baseCapacity = machine.energyCapacity;
        machine.upgradeInventory.setStackInSlot(0, new ItemStack(FTBICItems.ENERGY_STORAGE_UPGRADE.get(), 4));
        helper.assertTrue(machine.energyCapacity > baseCapacity, "Storage upgrades raise capacity");
        machine.energy = machine.energyCapacity;
        double stored = machine.energy;

        HolderLookup.Provider registries = helper.getLevel().registryAccess();
        CompoundTag tag = machine.saveCustomOnly(registries);
        helper.setBlock(POS, Blocks.AIR);
        helper.setBlock(POS, FTBICElectricBlocks.MACERATOR.block.get());
        BasicMachineBlockEntity reloaded = helper.getBlockEntity(POS, BasicMachineBlockEntity.class);
        reloaded.loadCustomOnly(TagValueInput.create(ProblemReporter.DISCARDING, registries, tag));
        helper.assertValueEqual(stored, reloaded.energy, "Energy above the base capacity survives a reload");
        helper.succeed();
    }

    static void configurationCardSyncsEnchantedItems(GameTestHelper helper) {
        ItemStack sword = new ItemStack(Items.DIAMOND_SWORD);
        sword.enchant(helper.getLevel().holderOrThrow(Enchantments.SHARPNESS), 3);
        MachineConfiguration configuration = new MachineConfiguration(
                FTBIC.id("macerator"),
                SideConfiguration.DEFAULT,
                List.of(GhostItem.of(0, sword, 1)),
                List.of(),
                SimpleFluidContent.EMPTY);
        var codec = ModDataComponents.MACHINE_CONFIGURATION.get().streamCodec();
        RegistryFriendlyByteBuf buffer =
                new RegistryFriendlyByteBuf(Unpooled.buffer(), helper.getLevel().registryAccess());
        try {
            codec.encode(buffer, configuration);
            helper.assertValueEqual(
                    configuration,
                    codec.decode(buffer),
                    "Configuration Cards with enchanted items survive network sync");
        } finally {
            buffer.release();
        }
        helper.succeed();
    }

    static void recipeTogglesFollowConfig(GameTestHelper helper) {
        Map<String, ModConfigSpec.BooleanValue> toggles = Map.of(
                "add_dust_from_ore_recipes", FTBICConfig.RECIPES.ADD_DUST_FROM_ORE_RECIPES,
                "add_dust_from_material_recipes", FTBICConfig.RECIPES.ADD_DUST_FROM_MATERIAL_RECIPES,
                "add_gem_from_ore_recipes", FTBICConfig.RECIPES.ADD_GEM_FROM_ORE_RECIPES,
                "add_rod_recipes", FTBICConfig.RECIPES.ADD_ROD_RECIPES,
                "add_plate_recipes", FTBICConfig.RECIPES.ADD_PLATE_RECIPES,
                "add_gear_recipes", FTBICConfig.RECIPES.ADD_GEAR_RECIPES,
                "add_canned_food_recipes", FTBICConfig.RECIPES.ADD_CANNED_FOOD_RECIPES);
        toggles.forEach((option, value) -> helper.assertValueEqual(
                value.get(),
                new RecipeToggleCondition(option).test(ICondition.IContext.EMPTY),
                option + " condition follows the config"));
        helper.assertTrue(
                new RecipeToggleCondition("not_a_real_option").test(ICondition.IContext.EMPTY),
                "Unknown options keep recipes enabled");

        Map<String, ModConfigSpec.BooleanValue> samples = Map.of(
                "macerating/ingots/iron_to_dust", FTBICConfig.RECIPES.ADD_DUST_FROM_MATERIAL_RECIPES,
                "macerating/ores/diamond_to_dust", FTBICConfig.RECIPES.ADD_GEM_FROM_ORE_RECIPES,
                "extruding/ingots/iron_to_iron_rod", FTBICConfig.RECIPES.ADD_ROD_RECIPES,
                "rolling/ingots/iron_to_iron_plate", FTBICConfig.RECIPES.ADD_PLATE_RECIPES,
                "extruding/plates/iron_to_iron_gear", FTBICConfig.RECIPES.ADD_GEAR_RECIPES,
                "canning/apple", FTBICConfig.RECIPES.ADD_CANNED_FOOD_RECIPES);
        RecipeManager recipes = helper.getLevel().getServer().getRecipeManager();
        samples.forEach((path, value) -> helper.assertValueEqual(
                value.get(),
                recipes.byKey(ResourceKey.create(Registries.RECIPE, FTBIC.id(path)))
                        .isPresent(),
                path + " loads only when its toggle is on"));
        helper.succeed();
    }

    static void batteryBoxesExposeChargeSlot(GameTestHelper helper) {
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        for (ElectricBlockInstance instance : List.of(
                FTBICElectricBlocks.BASIC_GENERATOR,
                FTBICElectricBlocks.GEOTHERMAL_GENERATOR,
                FTBICElectricBlocks.LV_SOLAR_PANEL)) {
            helper.setBlock(POS, instance.block.get());
            AbstractContainerMenu menu =
                    helper.getBlockEntity(POS, GeneratorBlockEntity.class).createMenu(0, player.getInventory());
            helper.assertFalse(
                    menu.slots.stream().anyMatch(slot -> slot instanceof ChargeSlot),
                    instance.id + " has no charge slot");
            helper.setBlock(POS, Blocks.AIR);
        }

        for (ElectricBlockInstance instance :
                List.of(FTBICElectricBlocks.LV_BATTERY_BOX, FTBICElectricBlocks.EV_BATTERY_BOX)) {
            helper.setBlock(POS, instance.block.get());
            GeneratorBlockEntity generator = helper.getBlockEntity(POS, GeneratorBlockEntity.class);
            AbstractContainerMenu menu = generator.createMenu(0, player.getInventory());
            helper.assertTrue(
                    menu.slots.stream().anyMatch(slot -> slot instanceof ChargeSlot),
                    instance.id + " shows a charge slot");
            player.getInventory().setItem(0, new ItemStack(FTBICItems.LV_BATTERY.get()));
            menu.quickMoveStack(player, playerSlot(menu, player, 0));
            helper.assertTrue(
                    generator.chargeBatteryInventory.getStackInSlot(0).is(FTBICItems.LV_BATTERY.get()),
                    instance.id + " takes an empty battery into its charge slot on Shift-click");
            generator.chargeBatteryInventory.setStackInSlot(0, ItemStack.EMPTY);
            player.getInventory().setItem(0, ItemStack.EMPTY);
            helper.setBlock(POS, Blocks.AIR);
        }

        helper.setBlock(POS, FTBICElectricBlocks.LV_BATTERY_BOX.block.get());
        GeneratorBlockEntity box = helper.getBlockEntity(POS, GeneratorBlockEntity.class);
        AbstractContainerMenu menu = box.createMenu(0, player.getInventory());
        ItemStack full = new ItemStack(FTBICItems.LV_BATTERY.get());
        BatteryItem battery = (BatteryItem) full.getItem();
        battery.setEnergy(full, battery.getEnergyCapacity(full));
        player.getInventory().setItem(0, full);
        menu.quickMoveStack(player, playerSlot(menu, player, 0));
        helper.assertTrue(
                box.inputItems[0].is(FTBICItems.LV_BATTERY.get()),
                "A full battery goes to the battery box discharge slot");
        helper.assertTrue(
                box.chargeBatteryInventory.getStackInSlot(0).isEmpty(), "A full battery skips the charge slot");
        helper.succeed();
    }

    static void bankPortChargeSlots(GameTestHelper helper) {
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        helper.setBlock(POS, FTBICElectricBlocks.INDUSTRIAL_BANK_PORT.block.get());
        helper.setBlock(POS.east(), FTBICElectricBlocks.INDUSTRIAL_BANK_CELL.block.get());
        BankPortBlockEntity port = helper.getBlockEntity(POS, BankPortBlockEntity.class);
        BankCellBlockEntity cell = helper.getBlockEntity(POS.east(), BankCellBlockEntity.class);

        AbstractContainerMenu portMenu = port.createMenu(0, player.getInventory());
        helper.assertValueEqual(
                BankPortBlockEntity.CHARGE_SLOTS,
                (int) portMenu.slots.stream()
                        .filter(slot -> slot instanceof ChargeSlot)
                        .count(),
                "A bank port shows four charge slots");
        AbstractContainerMenu cellMenu = cell.createMenu(0, player.getInventory());
        helper.assertFalse(
                cellMenu.slots.stream().anyMatch(slot -> slot instanceof ChargeSlot),
                "A bank cell shows no charge slots");

        player.getInventory().setItem(0, new ItemStack(FTBICItems.LV_BATTERY.get()));
        portMenu.quickMoveStack(player, playerSlot(portMenu, player, 0));
        helper.assertTrue(
                port.chargeSlots.get(0).getStackInSlot(0).is(FTBICItems.LV_BATTERY.get()),
                "Shift-click fills the first port charge slot");
        port.chargeSlots.get(3).setStackInSlot(0, new ItemStack(FTBICItems.LV_BATTERY.get()));

        HolderLookup.Provider registries = helper.getLevel().registryAccess();
        CompoundTag tag = port.saveCustomOnly(registries);
        port.chargeSlots.get(3).setStackInSlot(0, ItemStack.EMPTY);
        port.loadCustomOnly(TagValueInput.create(ProblemReporter.DISCARDING, registries, tag));
        helper.assertTrue(
                port.chargeSlots.get(3).getStackInSlot(0).is(FTBICItems.LV_BATTERY.get()),
                "Port charge slots survive a reload");

        cell.setEnergyRaw(cell.getEnergyCapacity());
        double before = cell.getEnergy();
        BankMenu bankMenu = (BankMenu) portMenu;
        bankMenu.broadcastChanges();
        BankTopology.Snapshot snapshot = BankTopology.snapshot(helper.getLevel(), port.getBlockPos());
        helper.assertValueEqual(snapshot.stored(), bankMenu.stored(), "The bank screen carries the full stored value");
        helper.assertValueEqual(snapshot.capacity(), bankMenu.capacity(), "The bank screen carries the full capacity");
        helper.runAfterDelay(20, () -> {
            for (int slot : new int[] {0, 3}) {
                ItemStack battery = port.chargeSlots.get(slot).getStackInSlot(0);
                helper.assertTrue(
                        ((BatteryItem) battery.getItem()).getEnergy(battery) > 0D,
                        "Port charge slot " + slot + " charges from the bank");
            }
            helper.assertTrue(cell.getEnergy() < before, "Charging draws on the bank's cells");
            helper.succeed();
        });
    }

    static void underpoweredMachineFinishes(GameTestHelper helper) {
        helper.setBlock(POS, FTBICElectricBlocks.MACERATOR.block.get());
        BasicMachineBlockEntity machine = helper.getBlockEntity(POS, BasicMachineBlockEntity.class);
        machine.setStackInSlot(0, new ItemStack(Items.BONE, 1));
        machine.energy = 0D;
        for (int tick = 0; tick < 10_000 && machine.outputItems[0].isEmpty(); tick++) {
            machine.energy = Math.min(machine.energyCapacity, machine.energy + machine.energyUse * 0.6D);
            machine.tick();
        }
        helper.assertTrue(
                machine.outputItems[0].is(Items.BONE_MEAL),
                "A machine fed below its usage rate still finishes its recipe");
        helper.succeed();
    }

    static void updateTagSkipsMenuData(GameTestHelper helper) {
        helper.setBlock(POS, FTBICElectricBlocks.MACERATOR.block.get());
        BasicMachineBlockEntity machine = helper.getBlockEntity(POS, BasicMachineBlockEntity.class);
        machine.setStackInSlot(0, new ItemStack(Items.BONE, 5));
        machine.upgradeInventory.setStackInSlot(0, new ItemStack(FTBICItems.OVERCLOCKER_UPGRADE.get(), 2));
        machine.placerName = "Tester";
        HolderLookup.Provider registries = helper.getLevel().registryAccess();

        CompoundTag update = machine.getUpdateTag(registries);
        helper.assertFalse(
                update.contains("Inventory") || update.contains("Upgrades") || update.contains("PlacerName"),
                "The client update leaves out menu-synced and server-only data");
        helper.assertTrue(update.getBooleanOr("ClientSync", false), "The client update is marked as a client sync");
        helper.assertTrue(
                machine.saveCustomOnly(registries).contains("Inventory"), "A normal save still includes the inventory");

        machine.energy = 0D;
        update.putDouble("Energy", 123D);
        machine.loadCustomOnly(TagValueInput.create(ProblemReporter.DISCARDING, registries, update));
        helper.assertValueEqual(123D, machine.energy, "Client updates still apply synced fields");
        helper.assertValueEqual(5, machine.inputItems[0].getCount(), "Client updates keep the existing inventory");
        helper.assertValueEqual(
                2,
                machine.upgradeInventory.countUpgrades(FTBICItems.OVERCLOCKER_UPGRADE.get()),
                "Client updates keep installed upgrades");
        helper.assertValueEqual("Tester", machine.placerName, "Client updates keep the placer");
        helper.succeed();
    }

    static void superconductingPulseFollowsTransfers(GameTestHelper helper) {
        helper.setBlock(POS, FTBICBlocks.SUPERCONDUCTING_CABLE.get());
        SuperconductingCableBlockEntity cable = helper.getBlockEntity(POS, SuperconductingCableBlockEntity.class);
        cable.recordTransfer();
        helper.assertTrue(
                cable.getUpdateTag(helper.getLevel().registryAccess()).getBooleanOr("Active", false),
                "Chunk data tells new viewers the cable is active");
        for (int delay = 5; delay <= 40; delay += 5) {
            helper.runAfterDelay(delay, cable::recordTransfer);
        }
        helper.runAfterDelay(
                41, () -> helper.assertTrue(cable.isTransferring(), "A busy cable stays active past a single pulse"));
        helper.runAfterDelay(70, () -> {
            helper.assertFalse(cable.isTransferring(), "The cable turns off once transfers stop");
            helper.succeed();
        });
    }

    private static int playerSlot(AbstractContainerMenu menu, Player player, int inventorySlot) {
        for (int i = 0; i < menu.slots.size(); i++) {
            Slot slot = menu.slots.get(i);
            if (slot.container == player.getInventory() && slot.getContainerSlot() == inventorySlot) return i;
        }
        throw new IllegalStateException("Player inventory slot " + inventorySlot + " is not in the menu");
    }
}
