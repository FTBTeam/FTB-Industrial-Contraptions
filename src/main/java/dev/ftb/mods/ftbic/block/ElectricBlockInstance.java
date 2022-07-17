package dev.ftb.mods.ftbic.block;

import dev.ftb.mods.ftbic.block.entity.FTBICBlockEntities;
import dev.ftb.mods.ftbic.item.FTBICItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

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
	public boolean canBeActive = true;
	public boolean canBurn = false;
	public final Supplier<Block> block;
	public final Supplier<BlockItem> item;
	public final Supplier<BlockEntityType<?>> blockEntity;
	public Supplier<Double> energyCapacity = () -> 0D;
	public Supplier<Double> maxEnergyOutput = () -> 0D;
	public Supplier<Double> energyUsage = () -> 0D;
	public boolean energyUsageIsPerTick = false;
	public Supplier<Double> maxEnergyInput = () -> 0D;
	public boolean wip = false;
	public int inputItemCount = 0;
	public int outputItemCount = 0;
	public boolean tickClientSide = false;

	public ElectricBlockInstance(String i, BlockEntityType.BlockEntitySupplier<BlockEntity> blockEntitySupplier) {
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

	public ElectricBlockInstance cantBeActive() {
		canBeActive = false;
		return this;
	}

	public ElectricBlockInstance canBurn() {
		canBurn = true;
		return this;
	}

	public ElectricBlockInstance maxEnergyOutput(Supplier<Double> d) {
		maxEnergyOutput = d;
		return this;
	}

	public ElectricBlockInstance energyCapacity(Supplier<Double> d) {
		energyCapacity = d;
		return this;
	}

	public ElectricBlockInstance energyUsage(Supplier<Double> d) {
		energyUsage = d;
		return this;
	}

	public ElectricBlockInstance maxEnergyInput(Supplier<Double> d) {
		maxEnergyInput = d;
		return this;
	}

	public ElectricBlockInstance wip() {
		wip = true;
		return this;
	}

	public ElectricBlockInstance energyUsageIsntPerTick() {
		energyUsageIsPerTick = false;
		return this;
	}

	public ElectricBlockInstance io(int inItems, int outItems) {
		inputItemCount = inItems;
		outputItemCount = outItems;
		return this;
	}

	public ElectricBlockInstance tickClientSide() {
		tickClientSide = true;
		return this;
	}
}
