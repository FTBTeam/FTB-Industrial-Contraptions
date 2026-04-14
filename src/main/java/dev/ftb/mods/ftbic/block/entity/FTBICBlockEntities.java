package dev.ftb.mods.ftbic.block.entity;

import dev.ftb.mods.ftbic.FTBIC;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Set;
import java.util.function.Supplier;

public final class FTBICBlockEntities {
	public static final DeferredRegister<BlockEntityType<?>> REGISTRY =
			DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, FTBIC.MOD_ID);

	public static <T extends BlockEntity> Supplier<BlockEntityType<?>> register(String id,
			BlockEntityType.BlockEntitySupplier<T> supplier, Supplier<? extends Block> block) {
		DeferredHolder<BlockEntityType<?>, BlockEntityType<T>> holder = REGISTRY.register(id, () ->
				new BlockEntityType<>(supplier, Set.of(block.get())));
		return holder::get;
	}

	public static final Supplier<BlockEntityType<?>> IRON_FURNACE = register(
			"iron_furnace", IronFurnaceBlockEntity::new,
			() -> dev.ftb.mods.ftbic.block.FTBICBlocks.IRON_FURNACE.get());

	private FTBICBlockEntities() {}
}
