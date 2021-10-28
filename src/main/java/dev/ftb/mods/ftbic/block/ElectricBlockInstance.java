package dev.ftb.mods.ftbic.block;

import dev.ftb.mods.ftbic.block.entity.FTBICBlockEntities;
import dev.ftb.mods.ftbic.item.FTBICItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;

import java.util.Arrays;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ElectricBlockInstance {
	public static ElectricBlockInstance current;

	public final String id;
	public String name;
	public boolean advanced = false;
	public DirectionProperty facingProperty = BlockStateProperties.HORIZONTAL_FACING;
	public boolean noModel = false;
	public EnumProperty<ElectricBlockState> stateProperty = ElectricBlockState.ON_OFF;
	public final Supplier<Block> block;
	public final Supplier<BlockItem> item;
	public final Supplier<BlockEntityType<?>> blockEntity;

	public ElectricBlockInstance(String i, Supplier<BlockEntity> blockEntitySupplier) {
		id = i;
		name = Arrays.stream(id.split("_")).map(s -> Character.toUpperCase(s.charAt(0)) + s.substring(1)).collect(Collectors.joining(" "));

		block = FTBICBlocks.REGISTRY.register(id, () -> {
			current = this;
			ElectricBlock b = new ElectricBlock(this);
			current = null;
			return b;
		});

		item = FTBICItems.blockItem(id, block);

		blockEntity = FTBICBlockEntities.register(id, blockEntitySupplier, block);
	}

	public ElectricBlockInstance advanced() {
		advanced = true;
		return this;
	}

	public ElectricBlockInstance name(String n) {
		name = n;
		return this;
	}

	public ElectricBlockInstance noRotation() {
		facingProperty = null;
		return this;
	}

	public ElectricBlockInstance rotate3D() {
		facingProperty = BlockStateProperties.FACING;
		return this;
	}

	public ElectricBlockInstance noModel() {
		noModel = true;
		return this;
	}

	public ElectricBlockInstance canBurn() {
		stateProperty = ElectricBlockState.ON_OFF_BURNT;
		return this;
	}

	public ElectricBlockInstance canBurnOrOff() {
		stateProperty = ElectricBlockState.OFF_BURNT;
		return this;
	}

	public ElectricBlockInstance noState() {
		stateProperty = null;
		return this;
	}

	public boolean hasOnState() {
		return stateProperty == ElectricBlockState.ON_OFF || stateProperty == ElectricBlockState.ON_OFF_BURNT;
	}
}
