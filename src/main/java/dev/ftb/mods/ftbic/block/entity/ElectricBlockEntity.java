package dev.ftb.mods.ftbic.block.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.TickableBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ElectricBlockEntity extends BlockEntity implements TickableBlockEntity {
	public int tick = 0;

	public ElectricBlockEntity(BlockEntityType<?> type) {
		super(type);
	}

	@Override
	public void load(BlockState state, CompoundTag tag) {
		super.load(state, tag);
	}

	@Override
	public CompoundTag save(CompoundTag tag) {
		super.save(tag);
		return tag;
	}

	@Override
	public void tick() {
		tick++;
	}
}
