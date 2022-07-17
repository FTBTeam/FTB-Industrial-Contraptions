package dev.ftb.mods.ftbic.block.entity;

import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.block.FTBICBlocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public interface FTBICBlockEntities {
	DeferredRegister<BlockEntityType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, FTBIC.MOD_ID);

	static Supplier<BlockEntityType<?>> register(String id, BlockEntityType.BlockEntitySupplier<?> supplier, Supplier<Block> block) {
		return REGISTRY.register(id, () -> BlockEntityType.Builder.of(supplier, block.get()).build(null));
	}

	Supplier<BlockEntityType<?>> IRON_FURNACE = register("iron_furnace", IronFurnaceBlockEntity::new, FTBICBlocks.IRON_FURNACE);
}
