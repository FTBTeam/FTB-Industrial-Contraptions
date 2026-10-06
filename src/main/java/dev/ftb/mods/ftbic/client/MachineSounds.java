package dev.ftb.mods.ftbic.client;

import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.block.ElectricBlock;
import dev.ftb.mods.ftbic.block.ElectricBlockInstance;
import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.block.entity.ElectricBlockEntity;
import dev.ftb.mods.ftbic.block.entity.machine.MachineBlockEntity;
import dev.ftb.mods.ftbic.sound.FTBICSounds;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import org.jetbrains.annotations.Nullable;

@EventBusSubscriber(modid = FTBIC.MOD_ID, value = Dist.CLIENT)
public final class MachineSounds {
    private static final int RANGE = 16;
    private static final Map<BlockPos, MachineLoop> LOOPS = new HashMap<>();
    private static ClientLevel level;
    private static int ticks;

    private MachineSounds() {}

    @SubscribeEvent
    public static void tick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (level != mc.level) {
            LOOPS.values().forEach(mc.getSoundManager()::stop);
            LOOPS.clear();
            level = mc.level;
            ticks = 0;
        }
        if (level == null || mc.player == null || mc.isPaused()) return;
        if (++ticks % 10 != 0) return;

        var candidates = new ArrayList<ElectricBlockEntity>();
        BlockPos playerPos = mc.player.blockPosition();
        if (FTBICClientConfig.MACHINE_SOUND_VOLUME.get() > 0D) {
            for (int x = (playerPos.getX() - RANGE) >> 4; x <= (playerPos.getX() + RANGE) >> 4; x++) {
                for (int z = (playerPos.getZ() - RANGE) >> 4; z <= (playerPos.getZ() + RANGE) >> 4; z++) {
                    LevelChunk chunk = level.getChunkSource().getChunk(x, z, ChunkStatus.FULL, false);
                    if (chunk == null) continue;
                    for (var entity : chunk.getBlockEntities().values()) {
                        if (entity instanceof ElectricBlockEntity machine
                                && machine.getBlockPos().distToCenterSqr(mc.player.position()) <= RANGE * RANGE
                                && isRunning(machine)
                                && soundFor(machine) != null) {
                            candidates.add(machine);
                        }
                    }
                }
            }
        }
        candidates.sort(Comparator.comparingDouble(be -> be.getBlockPos().distToCenterSqr(mc.player.position())));
        Set<BlockPos> wanted = new HashSet<>();
        for (int i = 0; i < Math.min(candidates.size(), FTBICClientConfig.MAX_MACHINE_SOUNDS.get()); i++) {
            wanted.add(candidates.get(i).getBlockPos());
        }
        LOOPS.entrySet().removeIf(entry -> {
            MachineLoop loop = entry.getValue();
            loop.wanted = wanted.contains(entry.getKey());
            if (loop.isStopped()
                    || !mc.getSoundManager().isActive(loop)
                    || level.getBlockEntity(entry.getKey()) != loop.machine) {
                mc.getSoundManager().stop(loop);
                return true;
            }
            return false;
        });
        for (ElectricBlockEntity machine : candidates) {
            if (LOOPS.size() < FTBICClientConfig.MAX_MACHINE_SOUNDS.get()
                    && wanted.contains(machine.getBlockPos())
                    && !LOOPS.containsKey(machine.getBlockPos())) {
                MachineLoop loop = new MachineLoop(machine, soundFor(machine));
                LOOPS.put(machine.getBlockPos(), loop);
                mc.getSoundManager().play(loop);
            }
        }
    }

    private static boolean isRunning(ElectricBlockEntity machine) {
        return !machine.isRemoved()
                && !machine.isBurnt()
                && machine.getBlockState().hasProperty(ElectricBlock.ACTIVE)
                && machine.getBlockState().getValue(ElectricBlock.ACTIVE)
                && !(machine instanceof MachineBlockEntity processing && processing.starving);
    }

    @Nullable
    private static SoundEvent soundFor(ElectricBlockEntity machine) {
        ElectricBlockInstance type = machine.electricBlockInstance;
        if (type == FTBICElectricBlocks.MACERATOR || type == FTBICElectricBlocks.ADVANCED_MACERATOR)
            return FTBICSounds.MACERATOR.get();
        if (type == FTBICElectricBlocks.COMPRESSOR || type == FTBICElectricBlocks.ADVANCED_COMPRESSOR)
            return FTBICSounds.COMPRESSOR.get();
        if (type == FTBICElectricBlocks.CENTRIFUGE || type == FTBICElectricBlocks.ADVANCED_CENTRIFUGE)
            return FTBICSounds.CENTRIFUGE.get();
        if (type == FTBICElectricBlocks.ORE_WASHER
                || type == FTBICElectricBlocks.ADVANCED_ORE_WASHER
                || type == FTBICElectricBlocks.PUMP) return FTBICSounds.ORE_WASHER.get();
        if (type == FTBICElectricBlocks.BASIC_GENERATOR) return FTBICSounds.GENERATOR.get();
        return null;
    }

    private static final class MachineLoop extends AbstractTickableSoundInstance {
        private final ElectricBlockEntity machine;
        private boolean wanted = true;

        private MachineLoop(ElectricBlockEntity machine, SoundEvent sound) {
            super(sound, SoundSource.BLOCKS, RandomSource.create());
            this.machine = machine;
            x = machine.getBlockPos().getX() + 0.5;
            y = machine.getBlockPos().getY() + 0.5;
            z = machine.getBlockPos().getZ() + 0.5;
            looping = true;
            volume = 0.01F;
        }

        @Override
        public void tick() {
            Minecraft mc = Minecraft.getInstance();
            if (mc.level != machine.getLevel()
                    || mc.player == null
                    || machine.isRemoved()
                    || !mc.level.hasChunkAt(machine.getBlockPos())
                    || mc.level.getBlockEntity(machine.getBlockPos()) != machine) {
                stop();
                return;
            }
            float target = wanted
                            && isRunning(machine)
                            && machine.getBlockPos().distToCenterSqr(mc.player.position()) <= RANGE * RANGE
                    ? FTBICClientConfig.MACHINE_SOUND_VOLUME.get().floatValue()
                    : 0F;
            volume += Math.clamp(target - volume, -0.04F, 0.04F);
            if (target == 0F && volume <= 0F) stop();
        }
    }
}
