package dev.ftb.mods.ftbic.test;

import com.mojang.authlib.GameProfile;
import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.block.CableBlock;
import dev.ftb.mods.ftbic.block.ElectricBlockInstance;
import dev.ftb.mods.ftbic.block.FTBICBlocks;
import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.block.entity.ElectricBlockEntity;
import dev.ftb.mods.ftbic.block.entity.generator.GeneratorBlockEntity;
import dev.ftb.mods.ftbic.block.entity.generator.NuclearReactorBlockEntity;
import dev.ftb.mods.ftbic.block.entity.machine.BasicMachineBlockEntity;
import dev.ftb.mods.ftbic.block.entity.machine.PumpBlockEntity;
import dev.ftb.mods.ftbic.block.entity.machine.TeleporterBlockEntity;
import dev.ftb.mods.ftbic.block.entity.storage.BatteryBoxBlockEntity;
import dev.ftb.mods.ftbic.item.FTBICItems;
import dev.ftb.mods.ftbic.net.SideConfigurationPayload;
import dev.ftb.mods.ftbic.registry.ModDataComponents;
import dev.ftb.mods.ftbic.screen.MachineMenu;
import dev.ftb.mods.ftbic.util.FTBICCapabilities;
import dev.ftb.mods.ftbic.util.SideConfiguration;
import dev.ftb.mods.ftbic.util.SideConfiguration.Face;
import dev.ftb.mods.ftbic.util.SideConfiguration.Mode;
import dev.ftb.mods.ftbic.util.SideConfiguration.Resource;
import java.util.EnumSet;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.TagValueInput;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

final class SideConfigurationGameTests {
    private static final BlockPos POS = new BlockPos(2, 2, 2);

    private static void side(ElectricBlockEntity machine, Resource resource, Direction direction, Mode mode) {
        machine.setSideConfiguration(machine.getSideConfiguration()
                .with(resource, Face.relative(machine.getFacing(Direction.NORTH), direction), mode));
    }

    private static void disable(ElectricBlockEntity machine, Resource resource) {
        for (Direction direction : Direction.values()) side(machine, resource, direction, Mode.DISABLED);
    }

    static void batteryBoxOutputOnAnySide(GameTestHelper h) {
        ElectricBlockInstance[] tiers = {
            FTBICElectricBlocks.LV_BATTERY_BOX, FTBICElectricBlocks.MV_BATTERY_BOX,
            FTBICElectricBlocks.HV_BATTERY_BOX, FTBICElectricBlocks.EV_BATTERY_BOX
        };
        for (ElectricBlockInstance tier : tiers) {
            h.setBlock(POS, tier.block.get());
            BatteryBoxBlockEntity box = h.getBlockEntity(POS, BatteryBoxBlockEntity.class);
            Direction front = box.getFacing(Direction.NORTH);
            for (Direction direction : Direction.values()) {
                Face face = Face.relative(front, direction);
                h.assertTrue(
                        box.supportsSideMode(Resource.ENERGY, face, Mode.OUTPUT),
                        tier.id + " supports output on " + direction);
                h.assertTrue(
                        box.supportsSideMode(Resource.ENERGY, face, Mode.BOTH),
                        tier.id + " supports both on " + direction);
                h.assertValueEqual(direction == front, box.isValidEnergyOutputSide(direction), "default output face");
                h.assertValueEqual(direction != front, box.isValidEnergyInputSide(direction), "default input face");
                side(box, Resource.ENERGY, direction, Mode.OUTPUT);
                h.assertTrue(box.isValidEnergyOutputSide(direction), tier.id + " sends from " + direction);
                h.assertFalse(box.isValidEnergyInputSide(direction), "output face blocks native energy input");
                side(box, Resource.ENERGY, direction, Mode.INPUT);
                h.assertFalse(box.isValidEnergyOutputSide(direction), "input face stops native output");
                h.assertTrue(box.isValidEnergyInputSide(direction), tier.id + " accepts input on " + direction);
                side(box, Resource.ENERGY, direction, Mode.BOTH);
                h.assertTrue(box.isValidEnergyOutputSide(direction), "both mode sends energy");
                h.assertTrue(box.isValidEnergyInputSide(direction), "both mode receives energy");
                side(box, Resource.ENERGY, direction, Mode.DISABLED);
                h.assertFalse(box.isValidEnergyOutputSide(direction), "disabled face stops output");
                h.assertFalse(box.isValidEnergyInputSide(direction), "disabled face stops input");
                side(box, Resource.ENERGY, direction, Mode.DEFAULT);
            }
            side(box, Resource.ENERGY, Direction.UP, Mode.OUTPUT);
            h.setBlock(POS.above(), tier.block.get());
            ElectricBlockEntity receiver = h.getBlockEntity(POS.above(), ElectricBlockEntity.class);
            box.energy = 1000;
            box.handleEnergyOutput();
            h.assertTrue(receiver.energy > 0D, tier.id + " pushes energy through configured top output");
            h.setBlock(POS.above(), Blocks.AIR);
        }
        h.succeed();
    }

    static void itemTransfers(GameTestHelper h) {
        h.setBlock(POS, FTBICElectricBlocks.MACERATOR.block.get());
        var machine = h.getBlockEntity(POS, ElectricBlockEntity.class);
        var handler = h.getLevel().getCapability(Capabilities.Item.BLOCK, machine.getBlockPos(), Direction.UP);
        var item = ItemResource.of(Items.IRON_INGOT);
        try (var tx = Transaction.openRoot()) {
            h.assertValueEqual(4, handler.insert(item, 4, tx), "Default accepts existing input behavior");
        }
        h.assertTrue(machine.inputItems[0].isEmpty(), "Rollback preserves inventory through wrapper");
        disable(machine, Resource.ITEMS);
        try (var tx = Transaction.openRoot()) {
            h.assertValueEqual(0, handler.insert(item, 4, tx), "Already cached handler obeys disabled face");
            var unsided = h.getLevel().getCapability(Capabilities.Item.BLOCK, machine.getBlockPos(), null);
            h.assertValueEqual(0, unsided.insert(item, 4, tx), "Unsided access cannot bypass all disabled faces");
        }
        side(machine, Resource.ITEMS, Direction.UP, Mode.INPUT);
        machine.outputItems[0] = new ItemStack(Items.IRON_INGOT, 3);
        try (var tx = Transaction.openRoot()) {
            h.assertValueEqual(4, handler.insert(item, 4, tx), "Input face accepts input");
            h.assertValueEqual(0, handler.extract(item, 2, tx), "Input face cannot extract outputs");
            tx.commit();
        }
        side(machine, Resource.ITEMS, Direction.UP, Mode.OUTPUT);
        try (var tx = Transaction.openRoot()) {
            h.assertValueEqual(0, handler.insert(item, 1, tx), "Output face rejects inputs");
            h.assertValueEqual(3, handler.extract(item, 10, tx), "Output face extracts only output slots");
            tx.commit();
        }
        h.assertValueEqual(
                4, machine.inputItems[0].getCount(), "Input contents cannot be extracted through output face");
        h.succeed();
    }

    static void ejector(GameTestHelper h) {
        h.setBlock(POS, FTBICElectricBlocks.MACERATOR.block.get());
        h.setBlock(POS.east(), Blocks.CHEST);
        h.setBlock(POS.west(), Blocks.CHEST);
        var machine = h.getBlockEntity(POS, BasicMachineBlockEntity.class);
        disable(machine, Resource.ITEMS);
        side(machine, Resource.ITEMS, Direction.EAST, Mode.OUTPUT);
        machine.outputItems[0] = new ItemStack(Items.IRON_INGOT, 12);
        machine.tick();
        h.assertValueEqual(12, machine.outputItems[0].getCount(), "Side settings alone do not grant auto-ejection");
        machine.upgradeInventory.setStackInSlot(0, new ItemStack(FTBICItems.EJECTOR_UPGRADE.get()));
        machine.upgradesChanged();
        machine.tick();
        h.assertTrue(machine.outputItems[0].isEmpty(), "Ejector pushed configured output");
        h.assertValueEqual(
                12,
                h.getBlockEntity(POS.east(), ChestBlockEntity.class).getItem(0).getCount(),
                "Only east chest receives items");
        h.assertTrue(
                h.getBlockEntity(POS.west(), ChestBlockEntity.class).isEmpty(), "Disabled west face does not eject");
        h.succeed();
    }

    static void fluidsAndTeleporter(GameTestHelper h) {
        h.setBlock(POS, FTBICElectricBlocks.PUMP.block.get());
        var pump = h.getBlockEntity(POS, PumpBlockEntity.class);
        pump.storedFluid = Fluids.WATER;
        pump.fluidAmount = 1000;
        var handler = h.getLevel().getCapability(Capabilities.Fluid.BLOCK, pump.getBlockPos(), Direction.UP);
        var water = FluidResource.of(Fluids.WATER);
        side(pump, Resource.FLUIDS, Direction.UP, Mode.DISABLED);
        try (var tx = Transaction.openRoot()) {
            h.assertValueEqual(0, handler.extract(water, 250, tx), "Pump disabled face blocks extraction");
        }
        side(pump, Resource.FLUIDS, Direction.UP, Mode.OUTPUT);
        try (var tx = Transaction.openRoot()) {
            h.assertValueEqual(250, handler.extract(water, 250, tx), "Pump output works");
            h.assertValueEqual(0, handler.insert(water, 250, tx), "Pump cannot become a fluid input");
        }
        h.assertValueEqual(1000, pump.fluidAmount, "Fluid rollback preserved");
        h.setBlock(POS.east(2), FTBICElectricBlocks.TELEPORTER.block.get());
        var teleporter = h.getBlockEntity(POS.east(2), TeleporterBlockEntity.class);
        var fluid = h.getLevel().getCapability(Capabilities.Fluid.BLOCK, teleporter.getBlockPos(), Direction.UP);
        var items = h.getLevel().getCapability(Capabilities.Item.BLOCK, teleporter.getBlockPos(), Direction.UP);
        side(teleporter, Resource.FLUIDS, Direction.UP, Mode.INPUT);
        side(teleporter, Resource.ITEMS, Direction.UP, Mode.OUTPUT);
        teleporter.receiveFluid = Fluids.WATER;
        teleporter.receiveFluidAmount = 1000;
        teleporter.receiveItems[0] = new ItemStack(Items.DIAMOND, 2);
        try (var tx = Transaction.openRoot()) {
            h.assertValueEqual(250, fluid.insert(water, 250, tx), "Teleporter fills send tank");
            h.assertValueEqual(0, fluid.extract(water, 250, tx), "Input side cannot drain receive tank");
            h.assertValueEqual(
                    0,
                    items.insert(ItemResource.of(Items.DIAMOND), 1, tx),
                    "Teleporter item output blocks send inventory insertion");
            h.assertValueEqual(
                    2,
                    items.extract(ItemResource.of(Items.DIAMOND), 2, tx),
                    "Teleporter output extracts receive inventory");
            tx.commit();
        }
        h.assertValueEqual(250, teleporter.sendFluidAmount, "Items and fluids use independent side settings");
        h.succeed();
    }

    static void nativeEnergy(GameTestHelper h) {
        h.setBlock(POS, FTBICElectricBlocks.MACERATOR.block.get());
        var machine = h.getBlockEntity(POS, ElectricBlockEntity.class);
        var zap = h.getLevel().getCapability(FTBICCapabilities.ZAP_ENERGY_BLOCK, machine.getBlockPos(), Direction.WEST);
        side(machine, Resource.ENERGY, Direction.WEST, Mode.DISABLED);
        h.assertValueEqual(0D, zap.insertEnergy(1000000, false), "Disabled port rejects even an overvoltage packet");
        h.assertFalse(machine.isBurnt(), "Disabled port does not burn machine");
        side(machine, Resource.ENERGY, Direction.WEST, Mode.INPUT);
        h.assertValueEqual(8D, zap.insertEnergy(8, false), "Cached zap handler updates immediately");
        machine.energy = 0;
        h.setBlock(POS.west(), FTBICElectricBlocks.BASIC_GENERATOR.block.get());
        var generator = h.getBlockEntity(POS.west(), GeneratorBlockEntity.class);
        generator.energy = 100;
        disable(generator, Resource.ENERGY);
        generator.handleEnergyOutput();
        h.assertValueEqual(0D, machine.energy, "Disabled generator output blocks native transfer");
        side(generator, Resource.ENERGY, Direction.EAST, Mode.OUTPUT);
        generator.handleEnergyOutput();
        h.assertTrue(machine.energy > 0, "Re-enabling output rebuilds cached routes");
        machine.energy = 0;
        side(machine, Resource.ENERGY, Direction.WEST, Mode.DISABLED);
        generator.handleEnergyOutput();
        h.assertValueEqual(0D, machine.energy, "Changing consumer face invalidates generator route");
        h.succeed();
    }

    static void feAndPorts(GameTestHelper h) {
        h.setBlock(POS, FTBICElectricBlocks.LV_RECTIFIER.block.get());
        var rectifier = h.getBlockEntity(POS, ElectricBlockEntity.class);
        Direction front = rectifier.getFacing(Direction.NORTH);
        var fe = h.getLevel().getCapability(Capabilities.Energy.BLOCK, rectifier.getBlockPos(), front);
        h.assertTrue(fe != null, "Rectifier retains FE input");
        h.assertTrue(
                h.getLevel().getCapability(Capabilities.Energy.BLOCK, rectifier.getBlockPos(), front.getOpposite())
                        == null,
                "Rectifier keeps dedicated front input");
        side(rectifier, Resource.ENERGY, front, Mode.DISABLED);
        try (var tx = Transaction.openRoot()) {
            h.assertValueEqual(0, fe.insert(100, tx), "Cached rectifier FE handler obeys disabled port");
        }
        side(rectifier, Resource.ENERGY, front, Mode.INPUT);
        try (var tx = Transaction.openRoot()) {
            h.assertValueEqual(100, fe.insert(100, tx), "FE input restored");
            tx.commit();
        }
        h.assertFalse(
                rectifier.supportsSideMode(Resource.ENERGY, Face.FRONT, Mode.OUTPUT),
                "Rectifier cannot output from FE input face");
        h.setBlock(POS.east(2), FTBICElectricBlocks.BASIC_GENERATOR.block.get());
        var battery = h.getBlockEntity(POS.east(2), ElectricBlockEntity.class);
        battery.energy = 1000;
        var output = h.getLevel().getCapability(Capabilities.Energy.BLOCK, battery.getBlockPos(), Direction.UP);
        side(battery, Resource.ENERGY, Direction.UP, Mode.DISABLED);
        try (var tx = Transaction.openRoot()) {
            h.assertValueEqual(0, output.extract(100, tx), "Generator FE extraction obeys settings");
        }
        battery.setSideConfiguration(SideConfiguration.DEFAULT);
        try (var tx = Transaction.openRoot()) {
            h.assertTrue(output.extract(100, tx) > 0, "Defaults preserve legacy FE extraction");
        }
        h.succeed();
    }

    static void fullFeMode(GameTestHelper h) {
        if (!FTBICConfig.ENERGY.FULL_FE_MODE.get()) {
            h.succeed();
            return;
        }
        h.setBlock(POS, FTBICElectricBlocks.MACERATOR.block.get());
        var machine = h.getBlockEntity(POS, ElectricBlockEntity.class);
        var fe = h.getLevel().getCapability(Capabilities.Energy.BLOCK, machine.getBlockPos(), Direction.UP);
        h.assertTrue(fe != null, "Full FE mode exposes machine energy input");
        try (var tx = Transaction.openRoot()) {
            h.assertTrue(fe.insert(10, tx) > 0, "Default full FE behavior preserved");
        }
        disable(machine, Resource.ENERGY);
        try (var tx = Transaction.openRoot()) {
            h.assertValueEqual(0, fe.insert(100, tx), "Full FE mode respects disabled inputs");
        }
        side(machine, Resource.ENERGY, Direction.UP, Mode.INPUT);
        try (var tx = Transaction.openRoot()) {
            h.assertTrue(fe.insert(10, tx) > 0, "Full FE input can be re-enabled");
            h.assertValueEqual(0, fe.extract(1, tx), "Processor cannot output energy");
            tx.commit();
        }
        h.setBlock(POS.east(2), FTBICElectricBlocks.LV_BATTERY_BOX.block.get());
        var battery = h.getBlockEntity(POS.east(2), ElectricBlockEntity.class);
        battery.energy = 1000;
        var front = battery.getFacing(Direction.NORTH);
        var batteryFe = h.getLevel().getCapability(Capabilities.Energy.BLOCK, battery.getBlockPos(), front);
        try (var tx = Transaction.openRoot()) {
            h.assertValueEqual(0, batteryFe.insert(10, tx), "Default battery output face refuses FE input");
        }
        side(battery, Resource.ENERGY, front, Mode.OUTPUT);
        try (var tx = Transaction.openRoot()) {
            h.assertValueEqual(0, batteryFe.insert(10, tx), "Explicit output-only blocks FE input");
            h.assertTrue(batteryFe.extract(10, tx) > 0, "Explicit output permits FE extraction");
        }
        h.succeed();
    }

    static void multipleCableRoutes(GameTestHelper h) {
        BlockPos machinePos = POS.east().south();
        h.setBlock(POS, FTBICElectricBlocks.BASIC_GENERATOR.block.get());
        h.setBlock(machinePos, FTBICElectricBlocks.MACERATOR.block.get());
        var cable = FTBICBlocks.LV_CABLE.get().defaultBlockState();
        for (var property : CableBlock.CONNECTION) cable = cable.setValue(property, true);
        h.setBlock(POS.south(), cable);
        h.setBlock(POS.east(), cable);
        var machine = h.getBlockEntity(machinePos, ElectricBlockEntity.class);
        var generator = h.getBlockEntity(POS, GeneratorBlockEntity.class);
        disable(machine, Resource.ENERGY);
        side(machine, Resource.ENERGY, Direction.NORTH, Mode.INPUT);
        generator.energy = 16;
        generator.handleEnergyOutput();
        h.assertTrue(
                machine.energy > 0, "Cable search retries another face after encountering disabled west face first");
        h.succeed();
    }

    static void reactorChambers(GameTestHelper h) {
        h.setBlock(POS, FTBICElectricBlocks.NUCLEAR_REACTOR.block.get());
        h.setBlock(POS.east(), FTBICBlocks.NUCLEAR_REACTOR_CHAMBER.get());
        var reactor = h.getBlockEntity(POS, NuclearReactorBlockEntity.class);
        reactor.recomputeActiveColumns();
        var handler = h.getLevel().getCapability(Capabilities.Item.BLOCK, h.absolutePos(POS.east()), Direction.UP);
        side(reactor, Resource.ITEMS, Direction.UP, Mode.DISABLED);
        try (var tx = Transaction.openRoot()) {
            h.assertValueEqual(
                    0,
                    handler.insert(ItemResource.of(FTBICItems.HEAT_VENT.get()), 10, tx),
                    "Chamber uses external face configuration");
        }
        side(reactor, Resource.ITEMS, Direction.UP, Mode.INPUT);
        try (var tx = Transaction.openRoot()) {
            h.assertValueEqual(
                    1,
                    handler.insert(0, ItemResource.of(FTBICItems.HEAT_VENT.get()), 10, tx),
                    "Chamber retains one component per slot");
            tx.commit();
        }
        var fe = h.getLevel().getCapability(Capabilities.Energy.BLOCK, h.absolutePos(POS.east()), Direction.UP);
        reactor.energy = 1000;
        side(reactor, Resource.ENERGY, Direction.UP, Mode.DISABLED);
        try (var tx = Transaction.openRoot()) {
            h.assertValueEqual(0, fe.extract(100, tx), "Forwarded chamber energy obeys external face");
        }
        h.setBlock(POS.east().above(), FTBICElectricBlocks.LV_BATTERY_BOX.block.get());
        var consumer = h.getBlockEntity(POS.east().above(), ElectricBlockEntity.class);
        disable(reactor, Resource.ENERGY);
        reactor.handleEnergyOutput();
        h.assertValueEqual(0D, consumer.energy, "All disabled reactor faces block native chamber output");
        side(reactor, Resource.ENERGY, Direction.UP, Mode.OUTPUT);
        reactor.maxEnergyOutputTransfer = 16;
        reactor.handleEnergyOutput();
        h.assertTrue(consumer.energy > 0, "Chamber top outputs while internal core-to-chamber east face is disabled");
        h.succeed();
    }

    static void persistenceAndRotation(GameTestHelper h) {
        h.setBlock(
                POS,
                FTBICElectricBlocks.MACERATOR
                        .block
                        .get()
                        .defaultBlockState()
                        .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH));
        var machine = h.getBlockEntity(POS, ElectricBlockEntity.class);
        side(machine, Resource.ITEMS, Direction.NORTH, Mode.DISABLED);
        var settings = machine.getSideConfiguration();
        var tag = machine.saveCustomOnly(h.getLevel().registryAccess());
        machine.setSideConfiguration(SideConfiguration.DEFAULT);
        machine.loadCustomOnly(
                TagValueInput.create(ProblemReporter.DISCARDING, h.getLevel().registryAccess(), tag));
        h.assertValueEqual(settings, machine.getSideConfiguration(), "Settings survive block save/reload");
        h.getLevel()
                .setBlock(
                        machine.getBlockPos(),
                        machine.getBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.EAST),
                        3);
        h.assertFalse(
                machine.allowsTransfer(Resource.ITEMS, Direction.EAST, true),
                "Front configuration rotates with machine");
        h.assertTrue(
                machine.allowsTransfer(Resource.ITEMS, Direction.NORTH, true),
                "Old front now uses another face's settings");
        for (Direction front : Direction.values()) {
            var directions = EnumSet.noneOf(Direction.class);
            for (Face face : Face.values()) {
                directions.add(face.direction(front));
                h.assertValueEqual(face, Face.relative(front, face.direction(front)), "Relative face round trip");
            }
            h.assertValueEqual(6, directions.size(), "Every orientation maps all six distinct faces");
        }
        tag.remove("SideConfiguration");
        machine.loadCustomOnly(
                TagValueInput.create(ProblemReporter.DISCARDING, h.getLevel().registryAccess(), tag));
        h.assertValueEqual(
                SideConfiguration.DEFAULT, machine.getSideConfiguration(), "Older saves retain default behavior");
        h.succeed();
    }

    static void cardAndPackets(GameTestHelper h) {
        h.setBlock(POS, FTBICElectricBlocks.MACERATOR.block.get());
        h.setBlock(POS.east(2), FTBICElectricBlocks.MACERATOR.block.get());
        h.setBlock(POS.south(2), FTBICElectricBlocks.COMPRESSOR.block.get());
        var machine = h.getBlockEntity(POS, ElectricBlockEntity.class);
        var copy = h.getBlockEntity(POS.east(2), ElectricBlockEntity.class);
        var cookie = CommonListenerCookie.createInitial(new GameProfile(UUID.randomUUID(), "side-test"), false);
        var player =
                new ServerPlayer(
                        h.getLevel().getServer(), h.getLevel(), cookie.gameProfile(), cookie.clientInformation()) {
                    @Override
                    public void sendOverlayMessage(Component message) {}
                };
        try {
            player.setPos(h.absolutePos(POS).getCenter());
            player.containerMenu = new MachineMenu(7, player.getInventory(), machine);
            var request = new SideConfigurationPayload(
                    7, Resource.ITEMS.ordinal(), Face.TOP.ordinal(), Mode.DISABLED.ordinal());
            h.assertTrue(SideConfigurationPayload.apply(player, request), "Valid menu request accepted");
            h.assertFalse(
                    SideConfigurationPayload.apply(player, new SideConfigurationPayload(8, 0, 0, 1)),
                    "Wrong container rejected");
            h.assertFalse(
                    SideConfigurationPayload.apply(player, new SideConfigurationPayload(7, 0, 7, 1)),
                    "Invalid face rejected");
            h.assertFalse(
                    SideConfigurationPayload.apply(
                            player, new SideConfigurationPayload(7, 2, 0, Mode.OUTPUT.ordinal())),
                    "Unsupported energy output rejected");
            var card = new ItemStack(FTBICItems.CONFIGURATION_CARD.get());
            player.setItemInHand(InteractionHand.MAIN_HAND, card);
            player.setShiftKeyDown(true);
            h.useBlock(POS, player);
            h.assertTrue(
                    card.has(ModDataComponents.MACHINE_CONFIGURATION.get()),
                    "Sneak-use records configuration through real block interaction");
            player.setShiftKeyDown(false);
            h.useBlock(POS.east(2), player);
            h.assertValueEqual(
                    machine.getSideConfiguration(), copy.getSideConfiguration(), "Card applies to same machine type");
            h.useBlock(POS.south(2), player);
            h.assertValueEqual(
                    SideConfiguration.DEFAULT,
                    h.getBlockEntity(POS.south(2), ElectricBlockEntity.class).getSideConfiguration(),
                    "Different machine type is untouched");
            h.assertValueEqual(1, card.getCount(), "Card not consumed");
            var ops = h.getLevel().registryAccess().createSerializationContext(NbtOps.INSTANCE);
            var loaded = ItemStack.CODEC
                    .parse(ops, ItemStack.CODEC.encodeStart(ops, card).getOrThrow())
                    .getOrThrow();
            h.assertValueEqual(
                    card.get(ModDataComponents.MACHINE_CONFIGURATION.get()),
                    loaded.get(ModDataComponents.MACHINE_CONFIGURATION.get()),
                    "Card data survives serialization");
            h.assertTrue(
                    SideConfigurationPayload.apply(player, new SideConfigurationPayload(7, -1, -1, -1)),
                    "Reset accepted");
            h.assertValueEqual(SideConfiguration.DEFAULT, machine.getSideConfiguration(), "Reset restores defaults");
            player.setPos(h.absolutePos(POS).getCenter().add(20, 0, 0));
            h.assertFalse(SideConfigurationPayload.apply(player, request), "Out-of-range requests rejected");
        } finally {
            player.discard();
        }
        h.succeed();
    }
}
