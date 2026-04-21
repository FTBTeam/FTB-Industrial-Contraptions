package dev.ftb.mods.ftbic.entity;

import dev.ftb.mods.ftbic.FTBIC;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;

public interface FTBICEntities {
	DeferredRegister<EntityType<?>> REGISTRY = DeferredRegister.create(Registries.ENTITY_TYPE, FTBIC.MOD_ID);

	static <T extends Entity> DeferredHolder<EntityType<?>, EntityType<T>> register(String id, Function<ResourceKey<EntityType<?>>, EntityType.Builder<T>> builder) {
		return REGISTRY.register(id, name -> builder.apply(ResourceKey.create(Registries.ENTITY_TYPE, name)).build(ResourceKey.create(Registries.ENTITY_TYPE, name)));
	}

	DeferredHolder<EntityType<?>, EntityType<NukeArrowEntity>> NUKE_ARROW = register("nuke_arrow",
			key -> EntityType.Builder.<NukeArrowEntity>of(NukeArrowEntity::new, MobCategory.MISC)
					.sized(0.5F, 0.5F)
					.clientTrackingRange(4)
					.updateInterval(20));
}
