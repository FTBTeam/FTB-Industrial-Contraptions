package dev.ftb.mods.ftbic.block;

import dev.ftb.mods.ftbic.block.entity.FTBICBlockEntities;
import dev.ftb.mods.ftbic.item.FTBICItems;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.Arrays;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ElectricBlockInstance {
	public static ElectricBlockInstance current;

	public final String id;
	public String name;
	public boolean advanced = false;
	public EnumProperty<Direction> facingProperty = BlockStateProperties.HORIZONTAL_FACING;
	public boolean noModel = false;
	public boolean canBeActive = true;
	public boolean canBurn = false;
	public final DeferredBlock<ElectricBlock> block;
	public final DeferredItem<BlockItem> item;
	public final DeferredHolder<BlockEntityType<?>, BlockEntityType<?>> blockEntity;
	public Supplier<Double> energyCapacity = () -> 0D;
	public Supplier<Double> maxEnergyOutput = () -> 0D;
	public Supplier<Double> energyUsage = () -> 0D;
	public Supplier<Double> maxEnergyInput = () -> 0D;
	public boolean wip = false;
	public int inputItemCount = 0;
	public int outputItemCount = 0;
	public boolean tickClientSide = false;
	public FECapMode feCapMode = FECapMode.NONE;

	public enum FECapMode { NONE, EXTRACT_ONLY, INSERT_ONLY }

	public ElectricBlockInstance(String i, BlockEntityType.BlockEntitySupplier<BlockEntity> blockEntitySupplier) {
		id = i;
		name = Arrays.stream(id.split("_"))
				.map(s -> Character.toUpperCase(s.charAt(0)) + s.substring(1))
				.collect(Collectors.joining(" "));

		block = FTBICBlocks.REGISTRY.register(id, name -> {
			current = this;
			try {
				ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK, name);
				return new ElectricBlock(this, BlockBehaviour.Properties.of().setId(key));
			} finally {
				current = null;
			}
		});

		item = FTBICItems.electricBlockItem(id, this);

		blockEntity = FTBICBlockEntities.register(id, blockEntitySupplier, block);
	}

	public ElectricBlockInstance advanced() { advanced = true; return this; }
	public ElectricBlockInstance name(String n) { name = n; return this; }
	public ElectricBlockInstance noRotation() { facingProperty = null; return this; }
	public ElectricBlockInstance rotate3D() { facingProperty = BlockStateProperties.FACING; return this; }
	public ElectricBlockInstance noModel() { noModel = true; return this; }
	public ElectricBlockInstance cantBeActive() { canBeActive = false; return this; }
	public ElectricBlockInstance canBurn() { canBurn = true; return this; }
	public ElectricBlockInstance wip() { wip = true; return this; }
	public ElectricBlockInstance tickClientSide() { tickClientSide = true; return this; }
	public ElectricBlockInstance energyUsageIsntPerTick() { return this; }
	public ElectricBlockInstance io(int inItems, int outItems) { inputItemCount = inItems; outputItemCount = outItems; return this; }

	public ElectricBlockInstance maxEnergyOutput(Supplier<Double> d) { maxEnergyOutput = d; return this; }
	public ElectricBlockInstance maxEnergyOutput(double d) { maxEnergyOutput = () -> d; return this; }
	public ElectricBlockInstance energyCapacity(Supplier<Double> d) { energyCapacity = d; return this; }
	public ElectricBlockInstance energyCapacity(double d) { energyCapacity = () -> d; return this; }
	public ElectricBlockInstance energyUsage(Supplier<Double> d) { energyUsage = d; return this; }
	public ElectricBlockInstance energyUsage(double d) { energyUsage = () -> d; return this; }
	public ElectricBlockInstance maxEnergyInput(Supplier<Double> d) { maxEnergyInput = d; return this; }
	public ElectricBlockInstance maxEnergyInput(double d) { maxEnergyInput = () -> d; return this; }
	public ElectricBlockInstance feMode(FECapMode m) { feCapMode = m; return this; }
}
