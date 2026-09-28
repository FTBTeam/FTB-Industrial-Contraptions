package dev.ftb.mods.ftbic.sound;

import dev.ftb.mods.ftbic.FTBIC;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public interface FTBICSounds {
    DeferredRegister<SoundEvent> REGISTRY = DeferredRegister.create(Registries.SOUND_EVENT, FTBIC.MOD_ID);

    static DeferredHolder<SoundEvent, SoundEvent> register(String id) {
        return REGISTRY.register(id, () -> SoundEvent.createVariableRangeEvent(FTBIC.id(id)));
    }

    DeferredHolder<SoundEvent, SoundEvent> MACERATOR = register("macerator");
    DeferredHolder<SoundEvent, SoundEvent> COMPRESSOR = register("compressor");
    DeferredHolder<SoundEvent, SoundEvent> CENTRIFUGE = register("centrifuge");
    DeferredHolder<SoundEvent, SoundEvent> ORE_WASHER = register("ore_washer");
    DeferredHolder<SoundEvent, SoundEvent> REACTOR_WARNING = register("reactor_warning");
    DeferredHolder<SoundEvent, SoundEvent> REACTOR_CRITICAL = register("reactor_critical");
    DeferredHolder<SoundEvent, SoundEvent> NUKE_ARM = register("nuke_arm");
    DeferredHolder<SoundEvent, SoundEvent> NUKE_TICK = register("nuke_tick");
    DeferredHolder<SoundEvent, SoundEvent> GENERATOR = register("generator");
    DeferredHolder<SoundEvent, SoundEvent> CHARGE_COMPLETE = register("charge_complete");
    DeferredHolder<SoundEvent, SoundEvent> TELEPORT = register("teleport");
    DeferredHolder<SoundEvent, SoundEvent> NUCLEAR_EXPLOSION = register("nuclear_explosion");

    DeferredHolder<SoundEvent, SoundEvent> RADIATION = register("radiation");
}
