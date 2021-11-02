package dev.ftb.mods.ftbic.block;

import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.util.PowerTier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.GlassBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public interface FTBICBlocks {
	DeferredRegister<Block> REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCKS, FTBIC.MOD_ID);

	Supplier<Block> RUBBER_SHEET = REGISTRY.register("rubber_sheet", RubberSheetBlock::new);
	Supplier<Block> REINFORCED_STONE = REGISTRY.register("reinforced_stone", () -> new Block(BlockBehaviour.Properties.of(Material.STONE).sound(SoundType.STONE).strength(10F, 10000000000F).harvestTool(ToolType.PICKAXE).requiresCorrectToolForDrops()));
	Supplier<Block> REINFORCED_GLASS = REGISTRY.register("reinforced_glass", () -> new GlassBlock(BlockBehaviour.Properties.of(Material.STONE).sound(SoundType.GLASS).strength(10F, 10000000000F).harvestTool(ToolType.PICKAXE).requiresCorrectToolForDrops().noOcclusion()));
	Supplier<Block> MACHINE_BLOCK = REGISTRY.register("machine_block", () -> new Block(BlockBehaviour.Properties.of(Material.METAL).sound(SoundType.METAL).strength(5F, 6F).harvestTool(ToolType.PICKAXE).requiresCorrectToolForDrops()));
	Supplier<Block> ADVANCED_MACHINE_BLOCK = REGISTRY.register("advanced_machine_block", () -> new Block(BlockBehaviour.Properties.of(Material.METAL).sound(SoundType.METAL).strength(5F, 6F).harvestTool(ToolType.PICKAXE).requiresCorrectToolForDrops()));
	Supplier<Block> IRON_FURNACE = REGISTRY.register("iron_furnace", IronFurnaceBlock::new);
	Supplier<Block> COPPER_CABLE = REGISTRY.register("copper_cable", () -> new CableBlock(PowerTier.LV, 5, SoundType.WOOL));
	Supplier<Block> GOLD_CABLE = REGISTRY.register("gold_cable", () -> new CableBlock(PowerTier.MV, 6, SoundType.WOOL));
	Supplier<Block> ALUMINUM_CABLE = REGISTRY.register("aluminum_cable", () -> new CableBlock(PowerTier.HV, 4, SoundType.WOOL));
	Supplier<Block> GLASS_CABLE = REGISTRY.register("glass_cable", () -> new CableBlock(PowerTier.EV, 6, SoundType.GLASS));
	List<Supplier<Block>> CABLES = Arrays.asList(COPPER_CABLE, GOLD_CABLE, ALUMINUM_CABLE, GLASS_CABLE);
}
