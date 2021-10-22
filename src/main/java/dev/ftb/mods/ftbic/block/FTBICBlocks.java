package dev.ftb.mods.ftbic.block;

import dev.ftb.mods.ftbic.FTBIC;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.GlassBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public interface FTBICBlocks {
	DeferredRegister<Block> REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCKS, FTBIC.MOD_ID);

	Supplier<Block> RUBBER_SHEET = REGISTRY.register("rubber_sheet", RubberSheetBlock::new);
	Supplier<Block> REINFORCED_STONE = REGISTRY.register("reinforced_stone", () -> new Block(BlockBehaviour.Properties.of(Material.STONE).sound(SoundType.STONE).strength(10F, 10000000000F).requiresCorrectToolForDrops()));
	Supplier<Block> REINFORCED_GLASS = REGISTRY.register("reinforced_glass", () -> new GlassBlock(BlockBehaviour.Properties.of(Material.STONE).sound(SoundType.GLASS).strength(10F, 10000000000F).requiresCorrectToolForDrops().noOcclusion()));
	Supplier<Block> MACHINE_BLOCK = REGISTRY.register("machine_block", () -> new Block(BlockBehaviour.Properties.of(Material.METAL).sound(SoundType.METAL).strength(5F, 6F).requiresCorrectToolForDrops()));
	Supplier<Block> ADVANCED_MACHINE_BLOCK = REGISTRY.register("advanced_machine_block", () -> new Block(BlockBehaviour.Properties.of(Material.METAL).sound(SoundType.METAL).strength(5F, 6F).requiresCorrectToolForDrops()));
}
