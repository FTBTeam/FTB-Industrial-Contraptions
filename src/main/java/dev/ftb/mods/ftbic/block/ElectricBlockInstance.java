package dev.ftb.mods.ftbic.block;

import dev.ftb.mods.ftbic.block.entity.FTBICBlockEntities;
import dev.ftb.mods.ftbic.item.FTBICItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.Arrays;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ElectricBlockInstance {
	public static ElectricBlockInstance current;

	public final String id;
	public String name;
	public boolean horizontal = true;
	public boolean noModel = false;
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

	public ElectricBlockInstance name(String n) {
		name = n;
		return this;
	}

	public ElectricBlockInstance notHorizontal() {
		horizontal = false;
		return this;
	}

	public ElectricBlockInstance noModel() {
		noModel = true;
		return this;
	}
}
