package dev.ftb.mods.industrialcontraptions.block.entity;

import dev.ftb.mods.industrialcontraptions.IC;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Set;
import java.util.function.Supplier;
import dev.ftb.mods.industrialcontraptions.block.ICBlocks;

public final class ICBlockEntities {
	public static final DeferredRegister<BlockEntityType<?>> REGISTRY =
			DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, IC.MOD_ID);

	public static <T extends BlockEntity> DeferredHolder<BlockEntityType<?>, BlockEntityType<?>> register(String id,
			BlockEntityType.BlockEntitySupplier<T> supplier, Supplier<? extends Block> block) {
		@SuppressWarnings({"unchecked", "rawtypes"})
		DeferredHolder<BlockEntityType<?>, BlockEntityType<?>> holder =
				(DeferredHolder) REGISTRY.register(id, () -> new BlockEntityType<>(supplier, Set.of(block.get())));
		return holder;
	}

	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<?>> IRON_FURNACE = register(
			"iron_furnace", IronFurnaceBlockEntity::new,
			() -> ICBlocks.IRON_FURNACE.get());

	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<?>> ACTIVE_NUKE = register(
			"active_nuke", ActiveNukeBlockEntity::new,
			() -> ICBlocks.ACTIVE_NUKE.get());

	private ICBlockEntities() {}
}
