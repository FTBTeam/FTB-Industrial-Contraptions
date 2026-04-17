package dev.ftb.mods.ftbic.block;

import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.util.EnergyTier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Arrays;
import java.util.List;

public interface FTBICBlocks {
	DeferredRegister.Blocks REGISTRY = DeferredRegister.createBlocks(FTBIC.MOD_ID);

	static BlockBehaviour.Properties props(net.minecraft.resources.Identifier name) {
		return BlockBehaviour.Properties.of().setId(ResourceKey.create(Registries.BLOCK, name));
	}

	DeferredBlock<RubberSheetBlock> RUBBER_SHEET = REGISTRY.register("rubber_sheet",
			name -> new RubberSheetBlock(props(name)));

	DeferredBlock<ReinforcedBlock> REINFORCED_STONE = REGISTRY.register("reinforced_stone",
			name -> new ReinforcedBlock(props(name).sound(SoundType.STONE).strength(10F, 10_000_000_000F).requiresCorrectToolForDrops()));

	DeferredBlock<ReinforcedGlassBlock> REINFORCED_GLASS = REGISTRY.register("reinforced_glass",
			name -> new ReinforcedGlassBlock(props(name).sound(SoundType.GLASS).strength(10F, 10_000_000_000F).requiresCorrectToolForDrops().noOcclusion()));

	DeferredBlock<Block> MACHINE_BLOCK = REGISTRY.register("machine_block",
			name -> new Block(props(name).sound(SoundType.METAL).strength(5F, 6F).requiresCorrectToolForDrops()));

	DeferredBlock<Block> ADVANCED_MACHINE_BLOCK = REGISTRY.register("advanced_machine_block",
			name -> new Block(props(name).sound(SoundType.METAL).strength(5F, 6F).requiresCorrectToolForDrops()));

	DeferredBlock<IronFurnaceBlock> IRON_FURNACE = REGISTRY.register("iron_furnace",
			name -> new IronFurnaceBlock(props(name)));

	DeferredBlock<CableBlock> LV_CABLE = REGISTRY.register("lv_cable",
			name -> new CableBlock(props(name), EnergyTier.LV, 5, SoundType.WOOL));
	DeferredBlock<CableBlock> MV_CABLE = REGISTRY.register("mv_cable",
			name -> new CableBlock(props(name), EnergyTier.MV, 4, SoundType.WOOL));
	DeferredBlock<CableBlock> HV_CABLE = REGISTRY.register("hv_cable",
			name -> new CableBlock(props(name), EnergyTier.HV, 6, SoundType.WOOL));
	DeferredBlock<CableBlock> EV_CABLE = REGISTRY.register("ev_cable",
			name -> new CableBlock(props(name), EnergyTier.EV, 4, SoundType.WOOL));
	DeferredBlock<CableBlock> IV_CABLE = REGISTRY.register("iv_cable",
			name -> new CableBlock(props(name), EnergyTier.IV, 6, SoundType.GLASS));
	DeferredBlock<BurntCableBlock> BURNT_CABLE = REGISTRY.register("burnt_cable",
			name -> new BurntCableBlock(props(name)));

	DeferredBlock<LandmarkBlock> LANDMARK = REGISTRY.register("landmark",
			name -> new LandmarkBlock(props(name)));

	DeferredBlock<ExFluidBlock> EXFLUID = REGISTRY.register("exfluid",
			name -> new ExFluidBlock(props(name)));

	DeferredBlock<NuclearReactorChamberBlock> NUCLEAR_REACTOR_CHAMBER = REGISTRY.register("nuclear_reactor_chamber",
			name -> new NuclearReactorChamberBlock(props(name)));

	DeferredBlock<NukeBlock> NUKE = REGISTRY.register("nuke",
			name -> new NukeBlock(props(name)));

	DeferredBlock<Block> ACTIVE_NUKE = REGISTRY.register("active_nuke",
			name -> new Block(props(name).sound(SoundType.GRASS).strength(-1F, 10_000_000_000F).noLootTable()));

	List<DeferredBlock<CableBlock>> CABLES = Arrays.asList(LV_CABLE, MV_CABLE, HV_CABLE, EV_CABLE, IV_CABLE);

	DeferredBlock<Block> ENDERIUM_BLOCK = REGISTRY.register("enderium_block",
			name -> new Block(props(name).requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL)));
}
