package dev.ftb.mods.ftbic.events;

import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.block.BaseCableBlock;
import dev.ftb.mods.ftbic.block.ElectricBlock;
import dev.ftb.mods.ftbic.block.entity.ElectricBlockEntity;
import dev.ftb.mods.ftbic.util.CableRouteCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.event.level.LevelEvent;

@EventBusSubscriber(modid = FTBIC.MOD_ID)
public final class ElectricNetworkLifecycleHandler {
    @SubscribeEvent
    public static void onLevelUnload(LevelEvent.Unload event) {
        if (event.getLevel() instanceof ServerLevel l) {
            ElectricBlockEntity.forgetElectricNetwork(l);
            CableRouteCache.forget(l);
        }
    }

    @SubscribeEvent
    public static void onChunkLoad(ChunkEvent.Load event) {
        if (event.getLevel() instanceof ServerLevel level
                && event.getChunk() instanceof LevelChunk chunk
                && hasElectricBlocks(chunk)) {
            ElectricBlockEntity.electricNetworkUpdated(level, chunk.getPos().getWorldPosition());
        }
    }

    private static boolean hasElectricBlocks(LevelChunk chunk) {
        for (LevelChunkSection section : chunk.getSections()) {
            if (!section.hasOnlyAir()
                    && section.getStates()
                            .maybeHas(state -> state.getBlock() instanceof BaseCableBlock
                                    || state.getBlock() instanceof ElectricBlock)) {
                return true;
            }
        }
        return false;
    }

    private ElectricNetworkLifecycleHandler() {}
}
