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

	DeferredHolder<SoundEvent, SoundEvent> RADIATION = register("radiation");
}
