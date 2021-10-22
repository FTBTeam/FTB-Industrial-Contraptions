package dev.ftb.mods.ftbic.block.entity;

import dev.ftb.mods.ftbic.FTBIC;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public interface FTBICBlockEntities {
	DeferredRegister<BlockEntityType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, FTBIC.MOD_ID);
}
