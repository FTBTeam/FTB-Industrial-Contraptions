package dev.ftb.mods.ftbic.block;

import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.util.EnergyTier;
import dev.ftb.mods.ftbic.world.ResourceElements;
import dev.ftb.mods.ftbic.world.ResourceType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.GlassBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public interface FTBICBlocks {
	DeferredRegister<Block> REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCKS, FTBIC.MOD_ID);

	Supplier<Block> RUBBER_SHEET = REGISTRY.register("rubber_sheet", RubberSheetBlock::new);
	Supplier<Block> REINFORCED_STONE = REGISTRY.register("reinforced_stone", () -> new Block(BlockBehaviour.Properties.of(Material.STONE).sound(SoundType.STONE).strength(10F, 10000000000F).requiresCorrectToolForDrops()));
	Supplier<Block> REINFORCED_GLASS = REGISTRY.register("reinforced_glass", () -> new GlassBlock(BlockBehaviour.Properties.of(Material.STONE).sound(SoundType.GLASS).strength(10F, 10000000000F).requiresCorrectToolForDrops().noOcclusion()));
	Supplier<Block> MACHINE_BLOCK = REGISTRY.register("machine_block", () -> new Block(BlockBehaviour.Properties.of(Material.METAL).sound(SoundType.METAL).strength(5F, 6F).requiresCorrectToolForDrops()));
	Supplier<Block> ADVANCED_MACHINE_BLOCK = REGISTRY.register("advanced_machine_block", () -> new Block(BlockBehaviour.Properties.of(Material.METAL).sound(SoundType.METAL).strength(5F, 6F).requiresCorrectToolForDrops()));
	Supplier<Block> IRON_FURNACE = REGISTRY.register("iron_furnace", IronFurnaceBlock::new);
	Supplier<Block> LV_CABLE = REGISTRY.register("lv_cable", () -> new CableBlock(EnergyTier.LV, 5, SoundType.WOOL));
	Supplier<Block> MV_CABLE = REGISTRY.register("mv_cable", () -> new CableBlock(EnergyTier.MV, 4, SoundType.WOOL));
	Supplier<Block> HV_CABLE = REGISTRY.register("hv_cable", () -> new CableBlock(EnergyTier.HV, 6, SoundType.WOOL));
	Supplier<Block> EV_CABLE = REGISTRY.register("ev_cable", () -> new CableBlock(EnergyTier.EV, 4, SoundType.WOOL));
	Supplier<Block> IV_CABLE = REGISTRY.register("iv_cable", () -> new CableBlock(EnergyTier.IV, 6, SoundType.GLASS));
	Supplier<Block> BURNT_CABLE = REGISTRY.register("burnt_cable", BurntCableBlock::new);
	Supplier<Block> LANDMARK = REGISTRY.register("landmark", LandmarkBlock::new);
	Supplier<Block> EXFLUID = REGISTRY.register("exfluid", ExFluidBlock::new);
	Supplier<Block> NUCLEAR_REACTOR_CHAMBER = REGISTRY.register("nuclear_reactor_chamber", NuclearReactorChamberBlock::new);
	Supplier<Block> NUKE = REGISTRY.register("nuke", NukeBlock::new);
	Supplier<Block> ACTIVE_NUKE = REGISTRY.register("active_nuke", () -> new Block(BlockBehaviour.Properties.of(Material.EXPLOSIVE).sound(SoundType.GRASS).strength(-1F, 10000000000F).noDrops()));

	List<Supplier<Block>> CABLES = Arrays.asList(LV_CABLE, MV_CABLE, HV_CABLE, EV_CABLE, IV_CABLE, BURNT_CABLE);

	Map<ResourceElements, Supplier<Block>> RESOURCE_ORES = ResourceElements.VALUES.stream().filter(e -> e.requirements().has(ResourceType.ORE)).collect(Collectors.toMap(Function.identity(), e -> REGISTRY.register(e.getName() + "_ore", ResourceBlock::new)));
}
