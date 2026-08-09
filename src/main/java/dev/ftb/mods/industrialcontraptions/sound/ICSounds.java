package dev.ftb.mods.industrialcontraptions.sound;

import dev.ftb.mods.industrialcontraptions.IC;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public interface ICSounds {
	DeferredRegister<SoundEvent> REGISTRY = DeferredRegister.create(Registries.SOUND_EVENT, IC.MOD_ID);

	static DeferredHolder<SoundEvent, SoundEvent> register(String id) {
		return REGISTRY.register(id, () -> SoundEvent.createVariableRangeEvent(IC.id(id)));
	}

	DeferredHolder<SoundEvent, SoundEvent> RADIATION = register("radiation");
}
