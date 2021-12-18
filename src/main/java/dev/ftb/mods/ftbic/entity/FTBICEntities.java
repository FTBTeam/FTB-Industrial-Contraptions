package dev.ftb.mods.ftbic.entity;

import dev.ftb.mods.ftbic.FTBIC;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public interface FTBICEntities {
	DeferredRegister<EntityType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.ENTITIES, FTBIC.MOD_ID);

	static <T extends Entity> Supplier<EntityType<T>> register(String id, EntityType.Builder<T> builder) {
		return REGISTRY.register(id, () -> builder.build(id));
	}

	Supplier<EntityType<NukeArrowEntity>> NUKE_ARROW = register("nuke_arrow", EntityType.Builder.<NukeArrowEntity>of(NukeArrowEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(4).updateInterval(20));
}
