package dev.ftb.mods.ftbic.sound;

import dev.ftb.mods.ftbic.FTBIC;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public interface FTBICSounds {
	DeferredRegister<SoundEvent> REGISTRY = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, FTBIC.MOD_ID);

	static Supplier<SoundEvent> register(String id) {
		return REGISTRY.register(id, () -> new SoundEvent(new ResourceLocation(FTBIC.MOD_ID, id)));
	}

	Supplier<SoundEvent> RADIATION = register("radiation");
}
