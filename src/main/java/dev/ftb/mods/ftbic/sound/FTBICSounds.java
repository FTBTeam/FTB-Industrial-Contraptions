package dev.ftb.mods.ftbic.sound;

import dev.ftb.mods.ftbic.FTBIC;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public interface FTBICSounds {
	DeferredRegister<SoundEvent> REGISTRY = DeferredRegister.create(Registries.SOUND_EVENT, FTBIC.MOD_ID);

	static Supplier<SoundEvent> register(String id) {
		return REGISTRY.register(id, () -> SoundEvent.createVariableRangeEvent(FTBIC.id(id)));
	}

	Supplier<SoundEvent> RADIATION = register("radiation");
}
