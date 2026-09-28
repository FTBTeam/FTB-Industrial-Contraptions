package dev.ftb.mods.ftbic.test;

import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.block.FTBICBlocks;
import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.block.entity.machine.DiggingBaseBlockEntity;
import dev.ftb.mods.ftbic.block.entity.machine.PumpBlockEntity;
import dev.ftb.mods.ftbic.block.entity.machine.QuarryBlockEntity;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.TagValueInput;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.event.level.block.BreakBlockEvent;

final class DiggingGameTests {
    private static final BlockPos POS = new BlockPos(3, 5, 3);
    private static final BlockPos COLUMN = new BlockPos(4, 4, 4);
    private static final Set<BlockPos> PROTECTED = ConcurrentHashMap.newKeySet();
    private static volatile UUID lastBreaker;
    private static boolean listening;

    private static <T extends DiggingBaseBlockEntity> T place(
            GameTestHelper h, Block block, Class<T> type, int sizeX, int sizeZ) {
        h.setBlock(POS, block);
        var be = h.getBlockEntity(POS, type);
        be.offsetX = 0;
        be.offsetZ = 0;
        be.sizeX = sizeX;
        be.sizeZ = sizeZ;
        be.diggingMineTicks = 1;
        be.diggingMoveTicks = 1;
        be.energy = be.getEnergyCapacity();
        return be;
    }

    private static void digOnce(DiggingBaseBlockEntity be) {
        be.tick();
        be.tick();
    }

    private static Block fluidReplacement(boolean exfluid) {
        return exfluid ? FTBICBlocks.EXFLUID.get() : Blocks.AIR;
    }

    private static void protect(BlockPos pos) {
        if (!listening) {
            listening = true;
            NeoForge.EVENT_BUS.addListener(BreakBlockEvent.class, event -> {
                if (event.getPlayer() instanceof FakePlayer && PROTECTED.contains(event.getPos())) {
                    lastBreaker = event.getPlayer().getUUID();
                    event.setCanceled(true);
                }
            });
        }
        PROTECTED.add(pos);
    }

    static void areaSurvivesReload(GameTestHelper h) {
        var quarry = place(
                h,
                FTBICElectricBlocks.QUARRY.block.get(),
                QuarryBlockEntity.class,
                200,
                DiggingBaseBlockEntity.MAX_AREA_SIZE);
        quarry.offsetX = -120;
        quarry.offsetZ = 1;
        var registries = h.getLevel().registryAccess();
        CompoundTag saved = quarry.saveCustomOnly(registries);
        CompoundTag synced = quarry.getUpdateTag(registries);
        for (CompoundTag tag : List.of(saved, synced)) {
            quarry.offsetX = 0;
            quarry.offsetZ = 0;
            quarry.sizeX = 0;
            quarry.sizeZ = 0;
            quarry.loadCustomOnly(TagValueInput.create(ProblemReporter.DISCARDING, registries, tag));
            h.assertValueEqual(quarry.offsetX, -120, "Offset X after reload");
            h.assertValueEqual(quarry.offsetZ, 1, "Offset Z after reload");
            h.assertValueEqual(quarry.sizeX, 200, "Width above 64 after reload");
            h.assertValueEqual(
                    quarry.sizeZ, DiggingBaseBlockEntity.MAX_AREA_SIZE, "Largest landmark width after reload");
        }
        saved.putByte("SizeX", (byte) 150);
        quarry.loadCustomOnly(TagValueInput.create(ProblemReporter.DISCARDING, registries, saved));
        h.assertValueEqual(quarry.sizeX, 150, "Width saved by older versions as a byte");
        h.succeed();
    }

    static void noEnergyWithoutArea(GameTestHelper h) {
        var quarry = place(h, FTBICElectricBlocks.QUARRY.block.get(), QuarryBlockEntity.class, 0, 0);
        h.setBlock(COLUMN, Blocks.STONE);
        double full = quarry.energy;
        int[][] areas = {{0, 0}, {2, 5}, {5, 2}};
        for (int[] area : areas) {
            quarry.sizeX = area[0];
            quarry.sizeZ = area[1];
            for (int i = 0; i < 8; i++) {
                quarry.tick();
            }
            h.assertValueEqual(quarry.energy, full, "Energy with a " + area[0] + "x" + area[1] + " area");
            h.assertValueEqual(quarry.tick, 0L, "Progress with a " + area[0] + "x" + area[1] + " area");
        }
        h.assertTrue(h.getBlockState(COLUMN).is(Blocks.STONE), "Nothing is mined without an area");
        h.succeed();
    }

    static void quarryClearsFluid(GameTestHelper h) {
        var quarry = place(h, FTBICElectricBlocks.QUARRY.block.get(), QuarryBlockEntity.class, 3, 3);
        h.setBlock(COLUMN.below(), Blocks.STONE);
        h.setBlock(COLUMN, Blocks.WATER);
        digOnce(quarry);
        Block expected = fluidReplacement(FTBICConfig.MACHINES.QUARRY_REPLACE_FLUID_EXFLUID.get());
        h.assertTrue(h.getBlockState(COLUMN).is(expected), "Water source is replaced with " + expected);
        h.assertTrue(h.getBlockState(COLUMN).getFluidState().isEmpty(), "Water source is gone");
        digOnce(quarry);
        h.assertTrue(h.getBlockState(COLUMN.below()).isAir(), "Quarry mines below the drained water");
        boolean cobble = false;
        for (ItemStack out : quarry.outputItems) {
            cobble |= out.is(Items.COBBLESTONE);
        }
        h.assertTrue(cobble, "Stone below the water reaches the output");
        h.succeed();
    }

    static void pumpUsesOwnSettings(GameTestHelper h) {
        h.setBlock(POS, FTBICElectricBlocks.PUMP.block.get());
        var fresh = h.getBlockEntity(POS, PumpBlockEntity.class);
        h.assertValueEqual(fresh.diggingMineTicks, FTBICConfig.MACHINES.PUMP_MINE_TICKS.get(), "Pump mine ticks");
        h.assertValueEqual(fresh.diggingMoveTicks, FTBICConfig.MACHINES.PUMP_MOVE_TICKS.get(), "Pump move ticks");
        var pump = place(h, FTBICElectricBlocks.PUMP.block.get(), PumpBlockEntity.class, 3, 3);
        h.setBlock(COLUMN.below(), Blocks.STONE);
        h.setBlock(COLUMN, Blocks.WATER);
        digOnce(pump);
        h.assertValueEqual(pump.fluidAmount, 1000, "Pump stores the drained source");
        h.assertTrue(pump.storedFluid == Fluids.WATER, "Pump stores water");
        Block expected = fluidReplacement(FTBICConfig.MACHINES.PUMP_REPLACE_FLUID_EXFLUID.get());
        h.assertTrue(h.getBlockState(COLUMN).is(expected), "Drained source is replaced with " + expected);
        h.succeed();
    }

    static void landmarkArea(GameTestHelper h) {
        BlockPos pos = new BlockPos(4, 2, 4);
        h.setBlock(pos, FTBICElectricBlocks.QUARRY.block.get());
        var quarry = h.getBlockEntity(pos, QuarryBlockEntity.class);
        Direction back = quarry.getFacing(Direction.NORTH).getOpposite();
        BlockPos anchor = pos.relative(back);
        BlockPos corner = anchor.relative(back, 3).relative(back.getClockWise(), 3);
        h.setBlock(anchor, FTBICBlocks.LANDMARK.get());
        h.setBlock(corner, FTBICBlocks.LANDMARK.get());
        quarry.resize();
        h.assertValueEqual(quarry.sizeX, Math.abs(corner.getX() - anchor.getX()) + 1, "Area width between landmarks");
        h.assertValueEqual(quarry.sizeZ, Math.abs(corner.getZ() - anchor.getZ()) + 1, "Area depth between landmarks");
        h.assertValueEqual(quarry.offsetX, Math.min(anchor.getX(), corner.getX()) - pos.getX(), "Area X offset");
        h.assertValueEqual(quarry.offsetZ, Math.min(anchor.getZ(), corner.getZ()) - pos.getZ(), "Area Z offset");
        h.setBlock(corner, Blocks.AIR);
        quarry.resize();
        h.assertValueEqual(quarry.sizeX, 5, "Single landmark falls back to the default width");
        h.assertValueEqual(quarry.sizeZ, 5, "Single landmark falls back to the default depth");
        h.succeed();
    }

    static void protectedBlockSkipped(GameTestHelper h) {
        var quarry = place(h, FTBICElectricBlocks.QUARRY.block.get(), QuarryBlockEntity.class, 4, 3);
        quarry.placerId = UUID.randomUUID();
        quarry.placerName = "digging-test";
        BlockPos claimed = COLUMN;
        BlockPos open = COLUMN.east();
        h.setBlock(claimed, Blocks.STONE);
        h.setBlock(open, Blocks.STONE);
        BlockPos claimedAbs = h.absolutePos(claimed);
        protect(claimedAbs);
        try {
            digOnce(quarry);
            h.assertTrue(h.getBlockState(claimed).is(Blocks.STONE), "Protected block is left in place");
            h.assertValueEqual(lastBreaker, quarry.placerId, "Break check runs as the player who placed the quarry");
            h.assertValueEqual(quarry.skippedBlocks, 1, "Protected block counts as skipped");
            digOnce(quarry);
            h.assertTrue(h.getBlockState(open).isAir(), "Quarry moves on to the next column");
            h.assertTrue(h.getBlockState(claimed).is(Blocks.STONE), "Protected block is still in place");
        } finally {
            PROTECTED.remove(claimedAbs);
        }
        h.succeed();
    }
}
