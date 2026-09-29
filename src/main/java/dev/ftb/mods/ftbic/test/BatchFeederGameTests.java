package dev.ftb.mods.ftbic.test;

import com.mojang.authlib.GameProfile;
import dev.ftb.mods.ftbic.block.FTBICBlocks;
import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.block.entity.ElectricBlockEntity;
import dev.ftb.mods.ftbic.block.entity.IronFurnaceBlockEntity;
import dev.ftb.mods.ftbic.block.entity.generator.GeothermalGeneratorBlockEntity;
import dev.ftb.mods.ftbic.block.entity.machine.BatchFeederBlockEntity;
import dev.ftb.mods.ftbic.block.entity.machine.CentrifugeBlockEntity;
import dev.ftb.mods.ftbic.block.entity.machine.MachineBlockEntity;
import dev.ftb.mods.ftbic.item.FTBICItems;
import dev.ftb.mods.ftbic.material.Material;
import dev.ftb.mods.ftbic.material.MaterialComponent;
import dev.ftb.mods.ftbic.material.MaterialEntries;
import dev.ftb.mods.ftbic.net.BatchFluidPayload;
import dev.ftb.mods.ftbic.net.GhostSlotPayload;
import dev.ftb.mods.ftbic.net.SetGhostIngredientPayload;
import dev.ftb.mods.ftbic.registry.ModDataComponents;
import dev.ftb.mods.ftbic.screen.BatchFeederMenu;
import dev.ftb.mods.ftbic.screen.ElectricBlockMenu;
import dev.ftb.mods.ftbic.screen.IronFurnaceMenu;
import dev.ftb.mods.ftbic.screen.MachineMenu;
import dev.ftb.mods.ftbic.util.FTBICCapabilities;
import dev.ftb.mods.ftbic.util.MachineConfiguration;
import dev.ftb.mods.ftbic.util.SideConfiguration;
import dev.ftb.mods.ftbic.util.SideConfiguration.Face;
import dev.ftb.mods.ftbic.util.SideConfiguration.Mode;
import dev.ftb.mods.ftbic.util.SideConfiguration.Resource;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.TagValueInput;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

final class BatchFeederGameTests {
    private static final BlockPos POS = new BlockPos(2, 2, 2);

    private static ItemStack tin(int count) {
        return new ItemStack(
                MaterialEntries.get(Material.TIN, MaterialComponent.INGOT)
                        .item()
                        .get(),
                count);
    }

    private static BatchFeederBlockEntity feeder(GameTestHelper h) {
        h.setBlock(POS, FTBICElectricBlocks.BATCH_FEEDER.block.get(), Direction.NORTH);
        var feeder = h.getBlockEntity(POS, BatchFeederBlockEntity.class);
        feeder.setBatchItem(0, new ItemStack(Items.COPPER_INGOT, 3));
        feeder.setBatchItem(1, tin(1));
        return feeder;
    }

    static void locks(GameTestHelper h) {
        h.setBlock(POS, FTBICElectricBlocks.MACERATOR.block.get());
        var machine = h.getBlockEntity(POS, MachineBlockEntity.class);
        var handler = h.getLevel().getCapability(Capabilities.Item.BLOCK, machine.getBlockPos(), Direction.UP);
        ItemStack named = new ItemStack(Items.IRON_INGOT, 8);
        named.set(DataComponents.CUSTOM_NAME, Component.literal("Reserved iron"));
        machine.setInputLock(0, named);
        h.assertValueEqual(1, machine.getInputLock(0).getCount(), "Lock stores one ghost item");
        try (var tx = Transaction.openRoot()) {
            h.assertValueEqual(
                    0, handler.insert(ItemResource.of(Items.GOLD_INGOT), 1, tx), "Cached handler rejects wrong item");
            h.assertValueEqual(
                    0, handler.insert(ItemResource.of(Items.IRON_INGOT), 1, tx), "Different item data is rejected");
            h.assertValueEqual(8, handler.insert(ItemResource.of(named), 8, tx), "Matching components accepted");
        }
        h.assertTrue(machine.inputItems[0].isEmpty(), "Insertion rollback keeps inventory empty");
        h.assertFalse(machine.getInputLock(0).isEmpty(), "An empty input retains its lock");
        var player = h.makeMockPlayer(GameType.SURVIVAL);
        var menu = new MachineMenu(1, player.getInventory(), machine);
        h.assertFalse(menu.slots.get(0).mayPlace(new ItemStack(Items.GOLD_INGOT)), "Manual input also respects lock");
        h.assertTrue(menu.slots.get(0).mayPlace(named), "Manual matching input accepted");
        machine.inputItems[0] = new ItemStack(Items.IRON_INGOT);
        player.getInventory().setItem(0, new ItemStack(Items.IRON_INGOT, 4));
        h.assertTrue(
                menu.quickMoveStack(player, menu.slots.size() - 9).isEmpty(),
                "Shift-click cannot merge into contents that no longer match the lock");
        h.assertValueEqual(1, machine.inputItems[0].getCount(), "Existing mismatched contents are preserved");
        h.assertFalse(menu.quickMoveStack(player, 0).isEmpty(), "Mismatched contents can still be removed manually");
        var saved = machine.saveCustomOnly(h.getLevel().registryAccess());
        machine.setInputLocks(List.of());
        machine.loadCustomOnly(
                TagValueInput.create(ProblemReporter.DISCARDING, h.getLevel().registryAccess(), saved));
        h.assertTrue(ItemStack.isSameItemSameComponents(named, machine.getInputLock(0)), "Lock data persists");
        h.assertTrue(
                machine.getUpdateTag(h.getLevel().registryAccess()).contains("InputLocks"),
                "Client sync contains ghosts");
        saved.remove("InputLocks");
        machine.loadCustomOnly(
                TagValueInput.create(ProblemReporter.DISCARDING, h.getLevel().registryAccess(), saved));
        h.assertTrue(machine.getInputLocks().isEmpty(), "Saves without locks remain unrestricted");
        try (var tx = Transaction.openRoot()) {
            h.assertValueEqual(
                    1,
                    handler.insert(ItemResource.of(Items.GOLD_INGOT), 1, tx),
                    "Unlock restores cached handler insertion");
        }
        h.succeed();
    }

    static void atomicDelivery(GameTestHelper h) {
        var feeder = feeder(h);
        h.setBlock(POS.north(), Blocks.CHEST);
        var chest = h.getBlockEntity(POS.north(), ChestBlockEntity.class);
        for (int slot = 0; slot < chest.getContainerSize(); slot++) chest.setItem(slot, new ItemStack(Items.STONE, 64));
        chest.setItem(0, ItemStack.EMPTY);
        feeder.inputItems[0] = new ItemStack(Items.COPPER_INGOT, 3);
        h.assertFalse(feeder.trySendBatch(), "Missing ingredient prevents all transfer");
        feeder.inputItems[1] = tin(1);
        h.assertFalse(feeder.trySendBatch(), "Destination must fit the entire batch");
        h.assertTrue(chest.getItem(0).isEmpty(), "First inserted ingredient rolled back when second cannot fit");
        h.assertValueEqual(3, feeder.inputItems[0].getCount(), "Failed delivery preserves buffer");
        chest.setItem(1, ItemStack.EMPTY);
        h.assertTrue(feeder.trySendBatch(), "Complete batch delivered");
        h.assertTrue(
                chest.getItem(0).is(Items.COPPER_INGOT) && chest.getItem(0).getCount() == 3, "Three copper delivered");
        h.assertTrue(ItemStack.isSameItemSameComponents(chest.getItem(1), tin(1)), "One tin delivered");
        h.assertTrue(feeder.inputItems[0].isEmpty() && feeder.inputItems[1].isEmpty(), "Exact ingredients consumed");
        h.assertValueEqual(0D, feeder.energy, "Complete batch delivered without power");
        feeder.setBatchItem(1, new ItemStack(Items.COPPER_INGOT, 2));
        feeder.inputItems[0] = new ItemStack(Items.COPPER_INGOT, 3);
        h.assertFalse(feeder.trySendBatch(), "Duplicate entries cannot reuse the same three items");
        feeder.inputItems[1] = new ItemStack(Items.COPPER_INGOT, 2);
        h.assertTrue(feeder.trySendBatch(), "Duplicate requirements can draw from separate buffer slots");
        h.assertValueEqual(8, chest.getItem(0).getCount(), "Duplicate requirements deliver exactly five more");
        h.succeed();
    }

    static void machineAndControls(GameTestHelper h) {
        var feeder = feeder(h);
        h.setBlock(POS.north(), FTBICElectricBlocks.ALLOY_SMELTER.block.get());
        var target = h.getBlockEntity(POS.north(), MachineBlockEntity.class);
        target.setInputLock(0, new ItemStack(Items.COPPER_INGOT));
        target.setInputLock(1, new ItemStack(Items.IRON_INGOT));
        target.setInputLock(2, new ItemStack(Items.GOLD_INGOT));
        feeder.inputItems[0] = new ItemStack(Items.COPPER_INGOT, 6);
        feeder.inputItems[1] = tin(2);
        h.assertFalse(feeder.trySendBatch(), "Destination input locks can block a batch");
        h.assertTrue(target.inputItems[0].isEmpty(), "Rollback works with an FTBIC machine handler");
        target.setInputLock(1, tin(1));
        feeder.setSideConfiguration(feeder.getSideConfiguration().with(Resource.ITEMS, Face.FRONT, Mode.DISABLED));
        h.assertFalse(feeder.trySendBatch(), "Disabled front blocks delivery");
        feeder.setSideConfiguration(SideConfiguration.DEFAULT);
        h.setBlock(POS.east(), Blocks.REDSTONE_BLOCK);
        h.assertFalse(feeder.trySendBatch(), "Redstone pauses feeding");
        h.setBlock(POS.east(), Blocks.AIR);
        h.assertFalse(feeder.supportsResource(Resource.ENERGY), "Unpowered feeder has no energy I/O tab");
        h.assertValueEqual(0D, feeder.getEnergyCapacity(), "Unpowered feeder has no energy storage");
        h.assertFalse(feeder.canBurn(), "Unpowered feeder cannot burn from excess voltage");
        for (Direction side : Direction.values()) {
            h.assertTrue(
                    h.getLevel().getCapability(FTBICCapabilities.ZAP_ENERGY_BLOCK, feeder.getBlockPos(), side) == null,
                    "No zap energy connection");
            h.assertTrue(
                    h.getLevel().getCapability(Capabilities.Energy.BLOCK, feeder.getBlockPos(), side) == null,
                    "No FE connection");
        }
        h.assertTrue(feeder.trySendBatch(), "Configured alloy inputs accept a complete batch");
        h.assertValueEqual(3, target.inputItems[0].getCount(), "Copper reaches its locked slot");
        h.assertValueEqual(1, target.inputItems[1].getCount(), "Tin reaches its locked slot");
        var front = h.getLevel().getCapability(Capabilities.Item.BLOCK, feeder.getBlockPos(), Direction.NORTH);
        var top = h.getLevel().getCapability(Capabilities.Item.BLOCK, feeder.getBlockPos(), Direction.UP);
        try (var tx = Transaction.openRoot()) {
            h.assertValueEqual(
                    0, front.insert(ItemResource.of(Items.COPPER_INGOT), 1, tx), "Front does not accept buffer input");
            h.assertValueEqual(1, top.insert(ItemResource.of(Items.COPPER_INGOT), 1, tx), "Other faces accept input");
            h.assertValueEqual(
                    0,
                    front.extract(ItemResource.of(Items.COPPER_INGOT), 1, tx),
                    "Pipes cannot bypass batching by extracting singles");
        }
        var saved = feeder.saveCustomOnly(h.getLevel().registryAccess());
        feeder.setBatch(List.of());
        feeder.loadCustomOnly(
                TagValueInput.create(ProblemReporter.DISCARDING, h.getLevel().registryAccess(), saved));
        h.assertValueEqual(3, feeder.getBatchItem(0).getCount(), "Batch count survives reload");
        var menu = new BatchFeederMenu(1, h.makeMockPlayer(GameType.SURVIVAL).getInventory(), feeder);
        h.assertValueEqual(45, menu.slots.size(), "Ghosts are not real inventory slots");
        h.succeed();
    }

    static void mixedFluids(GameTestHelper h) {
        var feeder = feeder(h);
        feeder.setBatchItem(1, ItemStack.EMPTY);
        feeder.inputItems[0] = new ItemStack(Items.COPPER_INGOT, 6);
        feeder.setBufferFluid(new FluidStack(Fluids.LAVA, 2_000));
        feeder.setBatchFluid(new FluidStack(Fluids.LAVA, 1_000));
        h.setBlock(POS.north(), FTBICElectricBlocks.CENTRIFUGE.block.get());
        var target = h.getBlockEntity(POS.north(), CentrifugeBlockEntity.class);
        target.setFluids(new FluidStack(Fluids.LAVA, 15_500), FluidStack.EMPTY);
        h.assertFalse(feeder.trySendBatch(), "A partially fitting fluid must block the mixed batch");
        h.assertTrue(target.inputItems[0].isEmpty(), "Item insertion rolls back when the fluid cannot fit");
        h.assertValueEqual(15_500, target.getInputFluid().getAmount(), "Partial fluid insertion rolls back");
        h.assertValueEqual(6, feeder.inputItems[0].getCount(), "Failed mixed batch keeps every item");
        h.assertValueEqual(2_000, feeder.getBufferFluid().getAmount(), "Failed mixed batch keeps all fluid");
        target.setFluids(FluidStack.EMPTY, FluidStack.EMPTY);
        target.inputItems[0] = new ItemStack(Items.GOLD_INGOT, 64);
        h.assertFalse(feeder.trySendBatch(), "Blocked items prevent fluid delivery");
        h.assertTrue(target.getInputFluid().isEmpty(), "No fluid delivered without the items");
        target.inputItems[0] = ItemStack.EMPTY;
        FluidStack named = new FluidStack(Fluids.LAVA, 1_000);
        named.set(DataComponents.CUSTOM_NAME, Component.literal("Batch lava"));
        feeder.setBatchFluid(named);
        h.assertFalse(feeder.trySendBatch(), "Fluid component mismatch blocks the batch");
        feeder.setBatchFluid(new FluidStack(Fluids.LAVA, 1_000));
        feeder.setSideConfiguration(SideConfiguration.DEFAULT.with(Resource.FLUIDS, Face.FRONT, Mode.DISABLED));
        h.assertFalse(feeder.trySendBatch(), "Disabled fluid output blocks the entire mixed batch");
        feeder.setSideConfiguration(SideConfiguration.DEFAULT);
        target.setSideConfiguration(SideConfiguration.DEFAULT.with(Resource.FLUIDS, Face.FRONT, Mode.DISABLED));
        h.assertFalse(feeder.trySendBatch(), "Destination fluid face restrictions block the batch");
        h.assertTrue(target.inputItems[0].isEmpty(), "Destination face rejection rolls back items");
        target.setSideConfiguration(SideConfiguration.DEFAULT);
        h.assertTrue(feeder.trySendBatch(), "Mixed batch transfers without power");
        h.assertValueEqual(3, target.inputItems[0].getCount(), "Exact item count arrives");
        h.assertValueEqual(1_000, target.getInputFluid().getAmount(), "Exact fluid amount arrives");
        h.assertValueEqual(3, feeder.inputItems[0].getCount(), "One item batch consumed");
        h.assertValueEqual(1_000, feeder.getBufferFluid().getAmount(), "One fluid batch consumed");
        feeder.setBufferFluid(FluidStack.EMPTY);
        h.assertFalse(feeder.trySendBatch(), "Missing fluid blocks items too");
        h.succeed();
    }

    static void fluidBuffer(GameTestHelper h) {
        var feeder = feeder(h);
        feeder.setBatch(List.of());
        feeder.setBatchFluid(new FluidStack(Fluids.LAVA, 250));
        var top = h.getLevel().getCapability(Capabilities.Fluid.BLOCK, feeder.getBlockPos(), Direction.UP);
        var lava = FluidResource.of(Fluids.LAVA);
        try (var tx = Transaction.openRoot()) {
            h.assertValueEqual(16_000, top.insert(lava, 20_000, tx), "Buffer capacity enforced");
        }
        h.assertTrue(feeder.getBufferFluid().isEmpty(), "Uncommitted buffer fill rolls back");
        try (var tx = Transaction.openRoot()) {
            h.assertValueEqual(2_000, top.insert(lava, 2_000, tx), "Pipes fill buffer");
            tx.commit();
        }
        feeder.setSideConfiguration(SideConfiguration.DEFAULT.with(Resource.FLUIDS, Face.TOP, Mode.DISABLED));
        try (var tx = Transaction.openRoot()) {
            h.assertValueEqual(0, top.insert(lava, 1, tx), "Cached fluid handler obeys live side configuration");
        }
        feeder.setSideConfiguration(SideConfiguration.DEFAULT.with(Resource.ITEMS, Face.FRONT, Mode.DISABLED));
        try (var tx = Transaction.openRoot()) {
            h.assertValueEqual(0, top.insert(FluidResource.of(Fluids.WATER), 1, tx), "Buffer cannot mix fluids");
            for (Direction side : Direction.values()) {
                var handler = h.getLevel().getCapability(Capabilities.Fluid.BLOCK, feeder.getBlockPos(), side);
                h.assertValueEqual(0, handler.extract(lava, 1_000, tx), "Pipes cannot bypass complete batches");
                if (side == Direction.NORTH)
                    h.assertValueEqual(0, handler.insert(lava, 1, tx), "Outlet does not accept fluid");
            }
            h.assertValueEqual(
                    250, feeder.fluidHandler.manualAccess.extract(lava, 250, tx), "Manual drain is available");
        }
        h.assertValueEqual(2_000, feeder.getBufferFluid().getAmount(), "Manual drain rollback restores buffer");
        h.setBlock(POS.north(), FTBICElectricBlocks.GEOTHERMAL_GENERATOR.block.get());
        var target = h.getBlockEntity(POS.north(), GeothermalGeneratorBlockEntity.class);
        h.setBlock(POS.east(), Blocks.REDSTONE_BLOCK);
        h.assertFalse(feeder.trySendBatch(), "Redstone pauses fluid-only batches");
        h.setBlock(POS.east(), Blocks.AIR);
        h.assertTrue(feeder.trySendBatch(), "Fluid-only batch needs no item output");
        h.assertValueEqual(250, target.fluidAmount, "Fluid-only destination received exact amount");
        h.assertValueEqual(1_750, feeder.getBufferFluid().getAmount(), "Buffer consumed exact amount");
        var saved = feeder.saveCustomOnly(h.getLevel().registryAccess());
        feeder.setBufferFluid(FluidStack.EMPTY);
        feeder.setBatchFluid(FluidStack.EMPTY);
        feeder.loadCustomOnly(
                TagValueInput.create(ProblemReporter.DISCARDING, h.getLevel().registryAccess(), saved));
        h.assertValueEqual(1_750, feeder.getBufferFluid().getAmount(), "Fluid buffer persists");
        h.assertValueEqual(250, feeder.getBatchFluid().getAmount(), "Fluid assignment persists");
        h.assertTrue(feeder.getBatchFluid().getFluid() == Fluids.LAVA, "Assigned fluid persists");
        saved.remove("BufferFluid");
        saved.remove("BatchFluid");
        feeder.loadCustomOnly(
                TagValueInput.create(ProblemReporter.DISCARDING, h.getLevel().registryAccess(), saved));
        h.assertTrue(
                feeder.getBufferFluid().isEmpty() && feeder.getBatchFluid().isEmpty(),
                "Item-only saves load empty fluids");
        h.succeed();
    }

    static void packetsAndCards(GameTestHelper h) {
        h.setBlock(POS, FTBICElectricBlocks.MACERATOR.block.get());
        h.setBlock(POS.east(2), FTBICElectricBlocks.MACERATOR.block.get());
        var machine = h.getBlockEntity(POS, MachineBlockEntity.class);
        var copy = h.getBlockEntity(POS.east(2), MachineBlockEntity.class);
        var cookie = CommonListenerCookie.createInitial(new GameProfile(UUID.randomUUID(), "batch-test"), false);
        var player =
                new ServerPlayer(
                        h.getLevel().getServer(), h.getLevel(), cookie.gameProfile(), cookie.clientInformation()) {
                    @Override
                    public void sendOverlayMessage(Component message) {}
                };
        try {
            player.setPos(machine.getBlockPos().getCenter());
            var menu = new MachineMenu(7, player.getInventory(), machine);
            player.containerMenu = menu;
            menu.setCarried(new ItemStack(Items.IRON_INGOT, 12));
            h.assertTrue(GhostSlotPayload.apply(player, new GhostSlotPayload(7, 0, 0)), "Assign from server cursor");
            h.assertValueEqual(12, menu.getCarried().getCount(), "Ghost does not consume carried items");
            h.assertFalse(GhostSlotPayload.apply(player, new GhostSlotPayload(8, 0, 0)), "Wrong menu rejected");
            h.assertFalse(GhostSlotPayload.apply(player, new GhostSlotPayload(7, 99, 0)), "Invalid slot rejected");
            h.assertFalse(
                    GhostSlotPayload.apply(player, new GhostSlotPayload(7, 0, 3)),
                    "Batch-only action rejected on processor");
            var card = new ItemStack(FTBICItems.CONFIGURATION_CARD.get());
            player.setItemInHand(InteractionHand.MAIN_HAND, card);
            player.setShiftKeyDown(true);
            h.useBlock(POS, player);
            player.setShiftKeyDown(false);
            h.useBlock(POS.east(2), player);
            h.assertValueEqual(machine.getInputLocks(), copy.getInputLocks(), "Card copies processor locks");
            var ops = h.getLevel().registryAccess().createSerializationContext(NbtOps.INSTANCE);
            var loaded = ItemStack.CODEC
                    .parse(ops, ItemStack.CODEC.encodeStart(ops, card).getOrThrow())
                    .getOrThrow();
            h.assertValueEqual(
                    card.get(ModDataComponents.MACHINE_CONFIGURATION.get()),
                    loaded.get(ModDataComponents.MACHINE_CONFIGURATION.get()),
                    "Card ghosts survive serialization");
            var oldData = MachineConfiguration.CODEC
                    .encodeStart(
                            ops,
                            new MachineConfiguration(
                                    FTBICElectricBlocks.MACERATOR.block.getId(), SideConfiguration.DEFAULT))
                    .getOrThrow();
            h.assertTrue(
                    MachineConfiguration.CODEC
                            .parse(ops, oldData)
                            .getOrThrow()
                            .inputLocks()
                            .isEmpty(),
                    "Side-only cards remain readable");
            var feeder = feeder(h);
            h.setBlock(POS.east(2), FTBICElectricBlocks.BATCH_FEEDER.block.get());
            var feederCopy = h.getBlockEntity(POS.east(2), BatchFeederBlockEntity.class);
            var batchMenu = new BatchFeederMenu(9, player.getInventory(), feeder);
            player.containerMenu = batchMenu;
            batchMenu.setCarried(new ItemStack(Items.COPPER_INGOT, 3));
            h.assertTrue(GhostSlotPayload.apply(player, new GhostSlotPayload(9, 0, 0)), "Batch copies cursor count");
            batchMenu.setCarried(ItemStack.EMPTY);
            GhostSlotPayload.apply(player, new GhostSlotPayload(9, 0, 0));
            h.assertValueEqual(4, feeder.getBatchItem(0).getCount(), "Empty cursor increments count");
            GhostSlotPayload.apply(player, new GhostSlotPayload(9, 0, 3));
            h.assertValueEqual(3, feeder.getBatchItem(0).getCount(), "Right click decrements count");
            batchMenu.setCarried(new ItemStack(Items.WATER_BUCKET));
            h.assertTrue(
                    BatchFluidPayload.apply(player, new BatchFluidPayload(9, BatchFluidPayload.SELECT, 0)),
                    "Fluid selection comes from cursor container");
            h.assertTrue(batchMenu.getCarried().is(Items.WATER_BUCKET), "Selecting fluid does not consume bucket");
            h.assertTrue(feeder.getBufferFluid().isEmpty(), "Ghost selection does not create fluid");
            h.assertTrue(
                    BatchFluidPayload.apply(player, new BatchFluidPayload(9, BatchFluidPayload.SET_AMOUNT, 81)),
                    "Exact mB amount accepted");
            h.assertValueEqual(
                    81, feeder.getBatchFluid().getAmount(), "Fluid batch amount can be smaller than a bucket");
            h.assertFalse(
                    BatchFluidPayload.apply(player, new BatchFluidPayload(9, BatchFluidPayload.SET_AMOUNT, 16_001)),
                    "Oversized fluid amount rejected");
            h.assertFalse(
                    BatchFluidPayload.apply(player, new BatchFluidPayload(9, BatchFluidPayload.SET_AMOUNT, -1)),
                    "Negative fluid amount rejected");
            h.assertFalse(
                    BatchFluidPayload.apply(player, new BatchFluidPayload(99, BatchFluidPayload.CLEAR, 0)),
                    "Wrong fluid menu rejected");
            h.assertTrue(
                    BatchFluidPayload.apply(player, new BatchFluidPayload(9, BatchFluidPayload.TRANSFER_CONTAINER, 0)),
                    "Cursor bucket fills buffer");
            h.assertTrue(batchMenu.getCarried().is(Items.BUCKET), "Filled bucket becomes empty bucket");
            h.assertValueEqual(1_000, feeder.getBufferFluid().getAmount(), "Bucket fluid reaches buffer");
            h.assertTrue(
                    BatchFluidPayload.apply(player, new BatchFluidPayload(9, BatchFluidPayload.TRANSFER_CONTAINER, 0)),
                    "Empty cursor bucket drains buffer");
            h.assertTrue(batchMenu.getCarried().is(Items.WATER_BUCKET), "Drained fluid reaches cursor bucket");
            h.assertTrue(feeder.getBufferFluid().isEmpty(), "Manual drain removes fluid exactly once");
            player.setShiftKeyDown(true);
            h.useBlock(POS, player);
            player.setShiftKeyDown(false);
            h.useBlock(POS.east(2), player);
            h.assertTrue(
                    FluidStack.matches(feeder.getBatchFluid(), feederCopy.getBatchFluid()),
                    "Card copies fluid identity and exact amount");
            h.assertTrue(feederCopy.getBufferFluid().isEmpty(), "Card does not create buffered fluid");
            var fluidCard = ItemStack.CODEC
                    .parse(ops, ItemStack.CODEC.encodeStart(ops, card).getOrThrow())
                    .getOrThrow();
            h.assertValueEqual(
                    card.get(ModDataComponents.MACHINE_CONFIGURATION.get()),
                    fluidCard.get(ModDataComponents.MACHINE_CONFIGURATION.get()),
                    "Fluid card survives serialization");
            BatchFluidPayload.apply(player, new BatchFluidPayload(9, BatchFluidPayload.CLEAR, 0));
            h.assertTrue(feeder.getBatchFluid().isEmpty(), "Clear removes fluid assignment");
            h.assertValueEqual(feeder.getBatch(), feederCopy.getBatch(), "Card copies complete batch pattern");
            h.assertTrue(feederCopy.inputItems[0].isEmpty(), "Card creates no inventory items");
            GhostSlotPayload.apply(player, new GhostSlotPayload(9, 0, 1));
            h.assertTrue(feeder.getBatchItem(0).isEmpty(), "Clear removes ghost only");
            player.setPos(machine.getBlockPos().getCenter().add(20, 0, 0));
            h.assertFalse(GhostSlotPayload.apply(player, new GhostSlotPayload(9, 1, 0)), "Distant requests rejected");
            h.assertFalse(
                    BatchFluidPayload.apply(player, new BatchFluidPayload(9, BatchFluidPayload.SELECT, 0)),
                    "Distant fluid requests rejected");
        } finally {
            player.discard();
        }
        h.succeed();
    }

    static void ghostIngredients(GameTestHelper h) {
        var cookie = CommonListenerCookie.createInitial(new GameProfile(UUID.randomUUID(), "ghost-test"), false);
        var player = new ServerPlayer(
                h.getLevel().getServer(), h.getLevel(), cookie.gameProfile(), cookie.clientInformation());
        try {
            ItemStack ghost = new ItemStack(Items.IRON_INGOT, 64);
            ghost.set(DataComponents.CUSTOM_NAME, Component.literal("Ghost iron"));
            for (var type : FTBICElectricBlocks.ALL) {
                h.setBlock(POS, type.block.get());
                var machine = h.getBlockEntity(POS, ElectricBlockEntity.class);
                if (!machine.supportsInputLocks()) continue;
                player.setPos(machine.getBlockPos().getCenter());
                var menu = (ElectricBlockMenu) machine.createMenu(20, player.getInventory());
                player.containerMenu = menu;
                int slot = machine.inputItems.length - 1;
                h.assertTrue(
                        SetGhostIngredientPayload.apply(
                                player, new SetGhostIngredientPayload(20, slot, ghost, FluidStack.EMPTY)),
                        "JEI ghost accepted by " + type.id);
                h.assertValueEqual(
                        1, machine.getInputLock(slot).getCount(), "Input lock count normalized for " + type.id);
                h.assertTrue(
                        ItemStack.isSameItemSameComponents(ghost, machine.getInputLock(slot)),
                        "Ghost components preserved for " + type.id);
                h.assertTrue(
                        machine.inputItems[slot].isEmpty() && menu.getCarried().isEmpty(),
                        "No item created for " + type.id);
                h.assertFalse(
                        menu.slots.get(slot).mayPlace(new ItemStack(Items.GOLD_INGOT)),
                        "Manual insertion respects ghost in " + type.id);
                var handler = h.getLevel().getCapability(Capabilities.Item.BLOCK, machine.getBlockPos(), Direction.UP);
                try (var tx = Transaction.openRoot()) {
                    h.assertValueEqual(
                            0,
                            handler.insert(slot, ItemResource.of(Items.GOLD_INGOT), 1, tx),
                            "Automation respects ghost in " + type.id);
                }
                h.assertFalse(
                        SetGhostIngredientPayload.apply(
                                player,
                                new SetGhostIngredientPayload(20, machine.inputItems.length, ghost, FluidStack.EMPTY)),
                        "Outputs and equipment are not ghost targets");
                var saved = machine.saveCustomOnly(h.getLevel().registryAccess());
                machine.setInputLocks(List.of());
                machine.loadCustomOnly(TagValueInput.create(
                        ProblemReporter.DISCARDING, h.getLevel().registryAccess(), saved));
                h.assertTrue(
                        ItemStack.isSameItemSameComponents(ghost, machine.getInputLock(slot)),
                        "Utility input lock persists for " + type.id);
            }
            var feeder = feeder(h);
            var menu = new BatchFeederMenu(21, player.getInventory(), feeder);
            player.containerMenu = menu;
            h.assertTrue(
                    SetGhostIngredientPayload.apply(
                            player, new SetGhostIngredientPayload(21, 0, ghost, FluidStack.EMPTY)),
                    "JEI sets batch item");
            h.assertValueEqual(64, feeder.getBatchItem(0).getCount(), "Feeder keeps ghost quantity");
            h.assertTrue(
                    SetGhostIngredientPayload.apply(
                            player,
                            new SetGhostIngredientPayload(21, 3, ItemStack.EMPTY, new FluidStack(Fluids.WATER, 250))),
                    "JEI sets batch fluid");
            h.assertValueEqual(250, feeder.getBatchFluid().getAmount(), "JEI fluid amount retained");
            h.assertTrue(
                    feeder.getBufferFluid().isEmpty() && feeder.inputItems[0].isEmpty(),
                    "JEI creates no buffered resources");
            h.assertFalse(
                    SetGhostIngredientPayload.apply(
                            player, new SetGhostIngredientPayload(21, 0, ghost, new FluidStack(Fluids.WATER, 250))),
                    "Mixed packet rejected");
            h.assertFalse(
                    SetGhostIngredientPayload.apply(
                            player, new SetGhostIngredientPayload(99, 0, ghost, FluidStack.EMPTY)),
                    "Wrong ghost menu rejected");
            h.assertFalse(
                    SetGhostIngredientPayload.apply(
                            player, new SetGhostIngredientPayload(21, -1, ghost, FluidStack.EMPTY)),
                    "Negative ghost slot rejected");
            h.assertFalse(
                    SetGhostIngredientPayload.apply(
                            player, new SetGhostIngredientPayload(21, 9, ghost, FluidStack.EMPTY)),
                    "Feeder buffer cannot be a ghost target");
            player.setPos(feeder.getBlockPos().getCenter().add(20, 0, 0));
            h.assertFalse(
                    SetGhostIngredientPayload.apply(
                            player, new SetGhostIngredientPayload(21, 0, ghost, FluidStack.EMPTY)),
                    "Distant ghost packet rejected");
            h.setBlock(POS, FTBICBlocks.IRON_FURNACE.get());
            var furnace = h.getBlockEntity(POS, IronFurnaceBlockEntity.class);
            player.setPos(furnace.getBlockPos().getCenter());
            var furnaceMenu = new IronFurnaceMenu(22, player.getInventory(), furnace, new SimpleContainerData(4));
            player.containerMenu = furnaceMenu;
            h.assertTrue(
                    SetGhostIngredientPayload.apply(
                            player, new SetGhostIngredientPayload(22, 0, ghost, FluidStack.EMPTY)),
                    "Iron Furnace input accepts ghost");
            h.assertFalse(
                    furnaceMenu.slots.get(0).mayPlace(new ItemStack(Items.GOLD_INGOT)),
                    "Iron Furnace manual input respects ghost");
            h.assertTrue(
                    SetGhostIngredientPayload.apply(
                            player, new SetGhostIngredientPayload(22, 1, new ItemStack(Items.COAL), FluidStack.EMPTY)),
                    "Iron Furnace fuel accepts ghost");
            h.assertFalse(furnace.canPlaceItem(1, new ItemStack(Items.CHARCOAL)), "Hopper fuel respects ghost");
            h.assertFalse(
                    furnaceMenu.slots.get(1).mayPlace(new ItemStack(Items.CHARCOAL)), "Manual fuel respects ghost");
            h.assertTrue(furnaceMenu.slots.get(1).mayPlace(new ItemStack(Items.COAL)), "Matching fuel still accepted");
            h.assertFalse(
                    SetGhostIngredientPayload.apply(
                            player, new SetGhostIngredientPayload(22, 2, ghost, FluidStack.EMPTY)),
                    "Furnace output rejects ghost");
            h.assertTrue(
                    furnace.getItem(0).isEmpty() && furnace.getItem(1).isEmpty(),
                    "Iron Furnace ghosts are not real items");
            var saved = furnace.saveCustomOnly(h.getLevel().registryAccess());
            furnace.setInputLock(0, ItemStack.EMPTY);
            furnace.loadCustomOnly(TagValueInput.create(
                    ProblemReporter.DISCARDING, h.getLevel().registryAccess(), saved));
            h.assertTrue(ItemStack.isSameItemSameComponents(ghost, furnace.getInputLock(0)), "Furnace ghost persists");
            h.assertTrue(
                    furnace.getUpdateTag(h.getLevel().registryAccess()).contains("InputLocks"),
                    "Furnace ghost syncs to client");
            h.assertTrue(
                    SetGhostIngredientPayload.apply(
                            player, new SetGhostIngredientPayload(22, 0, ItemStack.EMPTY, FluidStack.EMPTY)),
                    "Furnace ghost can be cleared");
        } finally {
            player.discard();
        }
        h.succeed();
    }
}
