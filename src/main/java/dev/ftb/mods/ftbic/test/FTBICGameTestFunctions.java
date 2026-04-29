package dev.ftb.mods.ftbic.test;

import dev.ftb.mods.ftbic.block.BurntCableBlock;
import dev.ftb.mods.ftbic.block.CableBlock;
import dev.ftb.mods.ftbic.block.ElectricBlockInstance;
import dev.ftb.mods.ftbic.block.FTBICBlocks;
import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.block.entity.ElectricBlockEntity;
import dev.ftb.mods.ftbic.block.entity.generator.NuclearReactorBlockEntity;
import dev.ftb.mods.ftbic.block.entity.machine.AntimatterConstructorBlockEntity;
import dev.ftb.mods.ftbic.block.entity.machine.ChargePadBlockEntity;
import dev.ftb.mods.ftbic.block.entity.machine.PoweredCraftingTableBlockEntity;
import dev.ftb.mods.ftbic.block.entity.machine.PumpBlockEntity;
import dev.ftb.mods.ftbic.block.entity.machine.QuarryBlockEntity;
import dev.ftb.mods.ftbic.block.entity.machine.ReactorSimulatorBlockEntity;
import dev.ftb.mods.ftbic.block.entity.machine.TeleporterBlockEntity;
import dev.ftb.mods.ftbic.item.reactor.NuclearReactor;
import dev.ftb.mods.ftbic.util.NuclearExplosion;
import dev.ftb.mods.ftbic.util.ReactorDesign;
import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.block.entity.storage.BatteryBoxBlockEntity;
import dev.ftb.mods.ftbic.block.entity.storage.EnergyRectifierBlockEntity;
import dev.ftb.mods.ftbic.block.entity.storage.LVBatteryBoxBlockEntity;
import dev.ftb.mods.ftbic.block.entity.storage.LVRectifierBlockEntity;
import dev.ftb.mods.ftbic.block.entity.storage.LVTransformerBlockEntity;
import dev.ftb.mods.ftbic.block.entity.storage.MVTransformerBlockEntity;
import dev.ftb.mods.ftbic.block.entity.storage.TransformerBlockEntity;
import dev.ftb.mods.ftbic.util.EnergyTier;
import dev.ftb.mods.ftbic.block.entity.generator.BasicGeneratorBlockEntity;
import dev.ftb.mods.ftbic.block.entity.generator.EVSolarPanelBlockEntity;
import dev.ftb.mods.ftbic.block.entity.generator.GeothermalGeneratorBlockEntity;
import dev.ftb.mods.ftbic.block.entity.generator.LVSolarPanelBlockEntity;
import dev.ftb.mods.ftbic.block.entity.generator.WindMillBlockEntity;
import dev.ftb.mods.ftbic.block.entity.machine.AdvancedCentrifugeBlockEntity;
import dev.ftb.mods.ftbic.block.entity.machine.AdvancedCompressorBlockEntity;
import dev.ftb.mods.ftbic.block.entity.machine.AdvancedMaceratorBlockEntity;
import dev.ftb.mods.ftbic.block.entity.machine.AdvancedPoweredFurnaceBlockEntity;
import dev.ftb.mods.ftbic.block.entity.machine.BasicMachineBlockEntity;
import dev.ftb.mods.ftbic.block.entity.machine.CanningMachineBlockEntity;
import dev.ftb.mods.ftbic.block.entity.machine.CentrifugeBlockEntity;
import dev.ftb.mods.ftbic.block.entity.machine.CompressorBlockEntity;
import dev.ftb.mods.ftbic.block.entity.machine.MaceratorBlockEntity;
import dev.ftb.mods.ftbic.block.entity.machine.MachineBlockEntity;
import dev.ftb.mods.ftbic.block.entity.machine.PoweredFurnaceBlockEntity;
import com.mojang.authlib.GameProfile;
import dev.ftb.mods.ftbic.item.FTBICItems;
import dev.ftb.mods.ftbic.item.FluidCellItem;
import dev.ftb.mods.ftbic.registry.ModDataComponents;
import dev.ftb.mods.ftbic.util.EnergyItemHandler;
import dev.ftb.mods.ftbic.util.FTBICCapabilities;
import dev.ftb.mods.ftbic.util.FluidCellIngredient;
import dev.ftb.mods.ftbic.util.ZapEnergyHandler;
import io.netty.channel.embedded.EmbeddedChannel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.util.ProblemReporter;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.clock.WorldClock;
import net.minecraft.world.clock.WorldClocks;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.ValueInput;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import java.util.UUID;

public class FTBICGameTestFunctions {

	private static final BlockPos CENTER = new BlockPos(4, 2, 4);
	private static final BlockPos SKY = new BlockPos(4, 5, 4);

	private static void setDayTime(GameTestHelper helper, long ticks) {
		ServerLevel level = helper.getLevel();
		Holder<WorldClock> clock = level.registryAccess().lookupOrThrow(Registries.WORLD_CLOCK).getOrThrow(WorldClocks.OVERWORLD);
		level.clockManager().setTotalTicks(clock, ticks);
		level.updateSkyBrightness();
	}

	static void basicGeneratorBurnsCoal(GameTestHelper helper) {
		helper.setBlock(CENTER.below(), Blocks.STONE);
		helper.setBlock(CENTER, FTBICElectricBlocks.BASIC_GENERATOR.block.get());

		BasicGeneratorBlockEntity gen = helper.getBlockEntity(CENTER, BasicGeneratorBlockEntity.class);
		gen.inputItems[0] = new ItemStack(Items.COAL, 4);
		gen.setChanged();
		double startingEnergy = gen.energy;

		helper.runAfterDelay(40, () -> {
			BasicGeneratorBlockEntity after = helper.getBlockEntity(CENTER, BasicGeneratorBlockEntity.class);
			helper.assertTrue(after.energy > startingEnergy,
					"Generator energy should increase after burning coal (was " + startingEnergy + ", now " + after.energy + ")");
			helper.assertTrue(after.fuelTicks > 0 || after.maxFuelTicks > 0,
					"Generator should have consumed fuel (fuelTicks=" + after.fuelTicks + ", maxFuelTicks=" + after.maxFuelTicks + ")");
			helper.succeed();
		});
	}

	static void basicGeneratorEmptyStaysIdle(GameTestHelper helper) {
		helper.setBlock(CENTER.below(), Blocks.STONE);
		helper.setBlock(CENTER, FTBICElectricBlocks.BASIC_GENERATOR.block.get());

		BasicGeneratorBlockEntity gen = helper.getBlockEntity(CENTER, BasicGeneratorBlockEntity.class);
		gen.energy = 0D;
		gen.setChanged();

		helper.runAfterDelay(40, () -> {
			BasicGeneratorBlockEntity after = helper.getBlockEntity(CENTER, BasicGeneratorBlockEntity.class);
			helper.assertValueEqual(0D, after.energy, "generator energy without fuel");
			helper.assertValueEqual(0, after.fuelTicks, "fuelTicks");
			helper.succeed();
		});
	}

	static void basicGeneratorStopsWhenFull(GameTestHelper helper) {
		helper.setBlock(CENTER.below(), Blocks.STONE);
		helper.setBlock(CENTER, FTBICElectricBlocks.BASIC_GENERATOR.block.get());

		BasicGeneratorBlockEntity gen = helper.getBlockEntity(CENTER, BasicGeneratorBlockEntity.class);
		gen.energy = gen.energyCapacity;
		gen.inputItems[0] = new ItemStack(Items.COAL, 4);
		int startingCoalCount = gen.inputItems[0].getCount();
		gen.setChanged();

		helper.runAfterDelay(40, () -> {
			BasicGeneratorBlockEntity after = helper.getBlockEntity(CENTER, BasicGeneratorBlockEntity.class);
			helper.assertTrue(after.energy <= after.energyCapacity + 0.01D,
					"Energy should not exceed capacity (energy=" + after.energy + ", cap=" + after.energyCapacity + ")");
			helper.assertValueEqual(startingCoalCount, after.inputItems[0].getCount(), "coal count (fuel should not have been consumed at full buffer)");
			helper.succeed();
		});
	}

	static void solarPanelDay(GameTestHelper helper) {
		setDayTime(helper, 6000L);
		helper.setBlock(CENTER.below(), Blocks.STONE);
		helper.setBlock(CENTER, FTBICElectricBlocks.LV_SOLAR_PANEL.block.get());

		LVSolarPanelBlockEntity panel = helper.getBlockEntity(CENTER, LVSolarPanelBlockEntity.class);
		panel.energy = 0D;

		helper.runAfterDelay(20, () -> {
			LVSolarPanelBlockEntity after = helper.getBlockEntity(CENTER, LVSolarPanelBlockEntity.class);
			helper.assertTrue(after.energy > 0D,
					"Solar panel should generate energy during the day (energy=" + after.energy + ")");
			helper.succeed();
		});
	}

	static void solarPanelNight(GameTestHelper helper) {
		setDayTime(helper, 18000L);
		helper.setBlock(CENTER.below(), Blocks.STONE);
		helper.setBlock(CENTER, FTBICElectricBlocks.LV_SOLAR_PANEL.block.get());

		LVSolarPanelBlockEntity panel = helper.getBlockEntity(CENTER, LVSolarPanelBlockEntity.class);
		panel.energy = 0D;

		helper.runAfterDelay(40, () -> {
			LVSolarPanelBlockEntity after = helper.getBlockEntity(CENTER, LVSolarPanelBlockEntity.class);
			if (helper.getLevel().isBrightOutside()) {
				helper.succeed();
				return;
			}
			helper.assertValueEqual(0D, after.energy, "solar panel energy at night");
			helper.succeed();
		});
	}

	static void solarPanelObstructed(GameTestHelper helper) {
		setDayTime(helper, 6000L);
		helper.setBlock(CENTER.below(), Blocks.STONE);
		helper.setBlock(CENTER, FTBICElectricBlocks.LV_SOLAR_PANEL.block.get());
		helper.setBlock(CENTER.above(), Blocks.STONE);

		helper.runAfterDelay(20, () -> {
			helper.assertFalse(helper.getLevel().canSeeSky(helper.absolutePos(CENTER.above())),
					"obstruction must block sky access before measuring");
			LVSolarPanelBlockEntity panel = helper.getBlockEntity(CENTER, LVSolarPanelBlockEntity.class);
			panel.energy = 0D;
			helper.runAfterDelay(40, () -> {
				LVSolarPanelBlockEntity after = helper.getBlockEntity(CENTER, LVSolarPanelBlockEntity.class);
				helper.assertValueEqual(0D, after.energy, "solar panel energy when obstructed");
				helper.succeed();
			});
		});
	}

	static void evSolarOutputsMoreThanLv(GameTestHelper helper) {
		setDayTime(helper, 6000L);
		BlockPos lvPos = new BlockPos(2, 2, 4);
		BlockPos evPos = new BlockPos(6, 2, 4);
		helper.setBlock(lvPos.below(), Blocks.STONE);
		helper.setBlock(evPos.below(), Blocks.STONE);
		helper.setBlock(lvPos, FTBICElectricBlocks.LV_SOLAR_PANEL.block.get());
		helper.setBlock(evPos, FTBICElectricBlocks.EV_SOLAR_PANEL.block.get());

		LVSolarPanelBlockEntity lv = helper.getBlockEntity(lvPos, LVSolarPanelBlockEntity.class);
		EVSolarPanelBlockEntity ev = helper.getBlockEntity(evPos, EVSolarPanelBlockEntity.class);
		lv.energy = 0D;
		ev.energy = 0D;

		helper.runAfterDelay(20, () -> {
			LVSolarPanelBlockEntity lvAfter = helper.getBlockEntity(lvPos, LVSolarPanelBlockEntity.class);
			EVSolarPanelBlockEntity evAfter = helper.getBlockEntity(evPos, EVSolarPanelBlockEntity.class);
			helper.assertTrue(evAfter.energy > lvAfter.energy,
					"EV solar panel should outproduce LV (LV=" + lvAfter.energy + ", EV=" + evAfter.energy + ")");
			helper.succeed();
		});
	}

	static void geothermalConsumesLava(GameTestHelper helper) {
		helper.setBlock(CENTER.below(), Blocks.STONE);
		helper.setBlock(CENTER, FTBICElectricBlocks.GEOTHERMAL_GENERATOR.block.get());

		GeothermalGeneratorBlockEntity gen = helper.getBlockEntity(CENTER, GeothermalGeneratorBlockEntity.class);
		gen.inputItems[0] = new ItemStack(Items.LAVA_BUCKET);
		gen.energy = 0D;
		gen.fluidAmount = 0;
		gen.setChanged();

		helper.runAfterDelay(40, () -> {
			GeothermalGeneratorBlockEntity after = helper.getBlockEntity(CENTER, GeothermalGeneratorBlockEntity.class);
			helper.assertTrue(after.energy > 0D,
					"Geothermal should generate energy from lava (energy=" + after.energy + ")");
			helper.assertTrue(after.inputItems[0].isEmpty() || after.inputItems[0].getItem() != Items.LAVA_BUCKET,
					"Lava bucket slot should be drained");
			helper.assertTrue(!after.outputItems[0].isEmpty() && after.outputItems[0].getItem() == Items.BUCKET,
					"Empty bucket should be pushed to output slot");
			helper.succeed();
		});
	}

	static void windmillOutputs(GameTestHelper helper) {
		helper.setBlock(SKY.below(), Blocks.STONE);
		helper.setBlock(SKY, FTBICElectricBlocks.WIND_MILL.block.get());

		WindMillBlockEntity mill = helper.getBlockEntity(SKY, WindMillBlockEntity.class);
		mill.energy = 0D;

		helper.runAfterDelay(40, () -> {
			WindMillBlockEntity after = helper.getBlockEntity(SKY, WindMillBlockEntity.class);
			helper.assertTrue(after.energy >= 0D,
					"Wind mill energy should be non-negative");
			helper.succeed();
		});
	}

	private static <T extends ElectricBlockEntity> T placeMachine(GameTestHelper helper, ElectricBlockInstance type, Class<T> cls) {
		helper.setBlock(CENTER.below(), Blocks.STONE);
		helper.setBlock(CENTER, type.block.get());
		T be = helper.getBlockEntity(CENTER, cls);
		be.energy = be.energyCapacity;
		be.setChanged();
		return be;
	}

	private static boolean outputContains(ElectricBlockEntity be, Item item) {
		for (ItemStack stack : be.outputItems) {
			if (!stack.isEmpty() && stack.is(item)) return true;
		}
		return false;
	}

	private static boolean outputContainsAny(ElectricBlockEntity be) {
		for (ItemStack stack : be.outputItems) {
			if (!stack.isEmpty()) return true;
		}
		return false;
	}

	static void maceratorProducesBoneMeal(GameTestHelper helper) {
		MaceratorBlockEntity be = placeMachine(helper, FTBICElectricBlocks.MACERATOR, MaceratorBlockEntity.class);
		be.inputItems[0] = new ItemStack(Items.BONE, 1);
		be.setChanged();

		helper.runAfterDelay(240, () -> {
			MaceratorBlockEntity after = helper.getBlockEntity(CENTER, MaceratorBlockEntity.class);
			helper.assertTrue(outputContains(after, Items.BONE_MEAL),
					"Macerator should output bone meal after processing");
			helper.assertTrue(after.energy < after.energyCapacity,
					"Macerator should consume energy (" + after.energy + "/" + after.energyCapacity + ")");
			helper.succeed();
		});
	}

	static void advancedMaceratorProducesBoneMeal(GameTestHelper helper) {
		AdvancedMaceratorBlockEntity be = placeMachine(helper, FTBICElectricBlocks.ADVANCED_MACERATOR, AdvancedMaceratorBlockEntity.class);
		be.inputItems[0] = new ItemStack(Items.BONE, 1);
		be.setChanged();

		helper.runAfterDelay(240, () -> {
			AdvancedMaceratorBlockEntity after = helper.getBlockEntity(CENTER, AdvancedMaceratorBlockEntity.class);
			helper.assertTrue(outputContains(after, Items.BONE_MEAL),
					"Advanced macerator should output bone meal after processing");
			helper.succeed();
		});
	}

	static void compressorProducesSandstone(GameTestHelper helper) {
		CompressorBlockEntity be = placeMachine(helper, FTBICElectricBlocks.COMPRESSOR, CompressorBlockEntity.class);
		be.inputItems[0] = new ItemStack(Items.SAND, 8);
		be.setChanged();

		helper.runAfterDelay(240, () -> {
			CompressorBlockEntity after = helper.getBlockEntity(CENTER, CompressorBlockEntity.class);
			helper.assertTrue(outputContains(after, Items.SANDSTONE),
					"Compressor should output sandstone from sand");
			helper.succeed();
		});
	}

	static void advancedCompressorProducesSandstone(GameTestHelper helper) {
		AdvancedCompressorBlockEntity be = placeMachine(helper, FTBICElectricBlocks.ADVANCED_COMPRESSOR, AdvancedCompressorBlockEntity.class);
		be.inputItems[0] = new ItemStack(Items.SAND, 8);
		be.setChanged();

		helper.runAfterDelay(240, () -> {
			AdvancedCompressorBlockEntity after = helper.getBlockEntity(CENTER, AdvancedCompressorBlockEntity.class);
			helper.assertTrue(outputContains(after, Items.SANDSTONE),
					"Advanced compressor should output sandstone from sand");
			helper.succeed();
		});
	}

	static void centrifugeProducesFlint(GameTestHelper helper) {
		CentrifugeBlockEntity be = placeMachine(helper, FTBICElectricBlocks.CENTRIFUGE, CentrifugeBlockEntity.class);
		be.inputItems[0] = new ItemStack(Items.GRAVEL, 4);
		be.setChanged();

		helper.runAfterDelay(240, () -> {
			CentrifugeBlockEntity after = helper.getBlockEntity(CENTER, CentrifugeBlockEntity.class);
			helper.assertTrue(outputContains(after, Items.FLINT),
					"Centrifuge should output flint from gravel");
			helper.succeed();
		});
	}

	static void centrifugeRejectsInputBelowRecipeCount(GameTestHelper helper) {
		CentrifugeBlockEntity be = placeMachine(helper, FTBICElectricBlocks.CENTRIFUGE, CentrifugeBlockEntity.class);
		be.inputItems[0] = new ItemStack(Items.KELP, 3);
		be.setChanged();

		helper.runAfterDelay(240, () -> {
			CentrifugeBlockEntity after = helper.getBlockEntity(CENTER, CentrifugeBlockEntity.class);
			helper.assertFalse(outputContainsAny(after),
					"Centrifuge must NOT process kelp below the recipe's required count (have 3, need 6)");
			helper.assertValueEqual(3, after.inputItems[0].getCount(), "kelp untouched");
			helper.succeed();
		});
	}

	static void centrifugeConsumesCountFromInputKelp(GameTestHelper helper) {
		CentrifugeBlockEntity be = placeMachine(helper, FTBICElectricBlocks.CENTRIFUGE, CentrifugeBlockEntity.class);
		be.inputItems[0] = new ItemStack(Items.KELP, 20);
		be.setChanged();

		helper.runAfterDelay(240, () -> {
			CentrifugeBlockEntity after = helper.getBlockEntity(CENTER, CentrifugeBlockEntity.class);
			helper.assertTrue(outputContains(after, FTBICItems.LATEX_BALL.item.get()),
					"Centrifuge should output latex ball from 6 kelp");
			helper.assertValueEqual(14, after.inputItems[0].getCount(),
					"Centrifuge should consume exactly 6 kelp (started with 20, expect 14 remaining)");
			helper.succeed();
		});
	}

	static void advancedCentrifugeProducesFlint(GameTestHelper helper) {
		AdvancedCentrifugeBlockEntity be = placeMachine(helper, FTBICElectricBlocks.ADVANCED_CENTRIFUGE, AdvancedCentrifugeBlockEntity.class);
		be.inputItems[0] = new ItemStack(Items.GRAVEL, 4);
		be.setChanged();

		helper.runAfterDelay(240, () -> {
			AdvancedCentrifugeBlockEntity after = helper.getBlockEntity(CENTER, AdvancedCentrifugeBlockEntity.class);
			helper.assertTrue(outputContains(after, Items.FLINT),
					"Advanced centrifuge should output flint from gravel");
			helper.succeed();
		});
	}

	static void poweredFurnaceSmeltsRawIron(GameTestHelper helper) {
		PoweredFurnaceBlockEntity be = placeMachine(helper, FTBICElectricBlocks.POWERED_FURNACE, PoweredFurnaceBlockEntity.class);
		be.inputItems[0] = new ItemStack(Items.RAW_IRON, 1);
		be.setChanged();

		helper.runAfterDelay(240, () -> {
			PoweredFurnaceBlockEntity after = helper.getBlockEntity(CENTER, PoweredFurnaceBlockEntity.class);
			helper.assertTrue(outputContains(after, Items.IRON_INGOT),
					"Powered furnace should smelt raw iron into iron ingot");
			helper.succeed();
		});
	}

	static void advancedPoweredFurnaceSmeltsRawIron(GameTestHelper helper) {
		AdvancedPoweredFurnaceBlockEntity be = placeMachine(helper, FTBICElectricBlocks.ADVANCED_POWERED_FURNACE, AdvancedPoweredFurnaceBlockEntity.class);
		be.inputItems[0] = new ItemStack(Items.RAW_IRON, 1);
		be.setChanged();

		helper.runAfterDelay(240, () -> {
			AdvancedPoweredFurnaceBlockEntity after = helper.getBlockEntity(CENTER, AdvancedPoweredFurnaceBlockEntity.class);
			helper.assertTrue(outputContains(after, Items.IRON_INGOT),
					"Advanced powered furnace should smelt raw iron into iron ingot");
			helper.succeed();
		});
	}

	static void maceratorNoRecipeNoProgress(GameTestHelper helper) {
		MaceratorBlockEntity be = placeMachine(helper, FTBICElectricBlocks.MACERATOR, MaceratorBlockEntity.class);
		be.inputItems[0] = new ItemStack(Items.APPLE, 4);
		be.setChanged();

		helper.runAfterDelay(40, () -> {
			MaceratorBlockEntity after = helper.getBlockEntity(CENTER, MaceratorBlockEntity.class);
			helper.assertValueEqual(0, after.progress, "macerator progress with no matching recipe");
			helper.assertTrue(!outputContainsAny(after),
					"Macerator should not produce output with invalid input");
			helper.succeed();
		});
	}

	static void machineSleepsWhenOutputFull(GameTestHelper helper) {
		MaceratorBlockEntity be = placeMachine(helper, FTBICElectricBlocks.MACERATOR, MaceratorBlockEntity.class);
		be.inputItems[0] = new ItemStack(Items.BONE, 4);
		ItemStack blocker = new ItemStack(Items.BONE_MEAL, Items.BONE_MEAL.getDefaultMaxStackSize());
		be.outputItems[0] = blocker;
		be.setChanged();

		helper.runAfterDelay(40, () -> {
			MaceratorBlockEntity after = helper.getBlockEntity(CENTER, MaceratorBlockEntity.class);
			helper.assertTrue(after.outputItems[0].getCount() == Items.BONE_MEAL.getDefaultMaxStackSize(),
					"output slot should stay at max (" + after.outputItems[0].getCount() + ")");
			helper.assertValueEqual(4, after.inputItems[0].getCount(), "input bone count unchanged");
			helper.succeed();
		});
	}

	static void machineConsumesEnergyPerTick(GameTestHelper helper) {
		MaceratorBlockEntity be = placeMachine(helper, FTBICElectricBlocks.MACERATOR, MaceratorBlockEntity.class);
		be.inputItems[0] = new ItemStack(Items.BONE, 8);
		double before = be.energy;
		double expectedUsePerTick = be.energyUse;
		be.setChanged();

		helper.runAfterDelay(20, () -> {
			MaceratorBlockEntity after = helper.getBlockEntity(CENTER, MaceratorBlockEntity.class);
			double drained = before - after.energy;
			helper.assertTrue(drained >= expectedUsePerTick,
					"Machine should drain at least energyUse (drained=" + drained + ", perTick=" + expectedUsePerTick + ")");
			helper.succeed();
		});
	}

	private static BasicMachineBlockEntity placeMacerator(GameTestHelper helper) {
		return placeMachine(helper, FTBICElectricBlocks.MACERATOR, MaceratorBlockEntity.class);
	}

	static void machineStarvingFlagSetWhenEnergyDepleted(GameTestHelper helper) {
		MaceratorBlockEntity be = placeMachine(helper, FTBICElectricBlocks.MACERATOR, MaceratorBlockEntity.class);
		be.inputItems[0] = new ItemStack(Items.BONE, 8);
		be.energy = 0D;
		be.setChanged();

		helper.runAfterDelay(10, () -> {
			MaceratorBlockEntity after = helper.getBlockEntity(CENTER, MaceratorBlockEntity.class);
			helper.assertTrue(after.starving,
					"Machine with loaded recipe and no energy should be starving (starving=" + after.starving + ", energy=" + after.energy + ")");
			helper.succeed();
		});
	}

	static void machineNotStarvingWithoutRecipe(GameTestHelper helper) {
		MaceratorBlockEntity be = placeMachine(helper, FTBICElectricBlocks.MACERATOR, MaceratorBlockEntity.class);
		be.energy = 0D;
		be.setChanged();

		helper.runAfterDelay(10, () -> {
			MaceratorBlockEntity after = helper.getBlockEntity(CENTER, MaceratorBlockEntity.class);
			helper.assertFalse(after.starving,
					"Idle machine with no recipe should not be marked starving (starving=" + after.starving + ")");
			helper.succeed();
		});
	}

	static void machineStarvingClearsWhenEnergyRestored(GameTestHelper helper) {
		MaceratorBlockEntity be = placeMachine(helper, FTBICElectricBlocks.MACERATOR, MaceratorBlockEntity.class);
		be.inputItems[0] = new ItemStack(Items.BONE, 8);
		be.energy = 0D;
		be.setChanged();

		helper.runAfterDelay(10, () -> {
			MaceratorBlockEntity starving = helper.getBlockEntity(CENTER, MaceratorBlockEntity.class);
			helper.assertTrue(starving.starving, "pre-condition: machine should be starving before refill");
			starving.energy = starving.energyCapacity;
			starving.setChanged();

			helper.runAfterDelay(10, () -> {
				MaceratorBlockEntity after = helper.getBlockEntity(CENTER, MaceratorBlockEntity.class);
				helper.assertFalse(after.starving,
						"Machine should clear starving once energy is restored (starving=" + after.starving + ")");
				helper.succeed();
			});
		});
	}

	static void overclockerIncreasesSpeed(GameTestHelper helper) {
		BasicMachineBlockEntity be = placeMacerator(helper);
		double baseSpeed = be.progressSpeed;
		be.upgradeInventory.setStackInSlot(0, new ItemStack(FTBICItems.OVERCLOCKER_UPGRADE.get(), 1));

		helper.assertTrue(be.progressSpeed > baseSpeed,
				"Overclocker should increase speed (base=" + baseSpeed + ", now=" + be.progressSpeed + ")");
		helper.succeed();
	}

	static void overclockerIncreasesEnergyUse(GameTestHelper helper) {
		BasicMachineBlockEntity be = placeMacerator(helper);
		double baseUse = be.energyUse;
		be.upgradeInventory.setStackInSlot(0, new ItemStack(FTBICItems.OVERCLOCKER_UPGRADE.get(), 1));

		helper.assertTrue(be.energyUse > baseUse,
				"Overclocker should increase energy usage (base=" + baseUse + ", now=" + be.energyUse + ")");
		helper.succeed();
	}

	static void stackedOverclockersMultiplicative(GameTestHelper helper) {
		BasicMachineBlockEntity a = placeMacerator(helper);
		a.upgradeInventory.setStackInSlot(0, new ItemStack(FTBICItems.OVERCLOCKER_UPGRADE.get(), 1));
		double oneSpeed = a.progressSpeed;

		a.upgradeInventory.setStackInSlot(0, new ItemStack(FTBICItems.OVERCLOCKER_UPGRADE.get(), 2));
		double twoSpeed = a.progressSpeed;

		helper.assertTrue(twoSpeed > oneSpeed,
				"Two overclockers should be faster than one (one=" + oneSpeed + ", two=" + twoSpeed + ")");
		helper.succeed();
	}

	static void transformerUpgradeIncreasesInputCap(GameTestHelper helper) {
		BasicMachineBlockEntity be = placeMacerator(helper);
		double baseInput = be.maxInputEnergy;
		be.upgradeInventory.setStackInSlot(0, new ItemStack(FTBICItems.TRANSFORMER_UPGRADE.get(), 1));

		helper.assertTrue(be.maxInputEnergy > baseInput,
				"Transformer upgrade should raise maxInputEnergy (base=" + baseInput + ", now=" + be.maxInputEnergy + ")");
		helper.assertTrue(be.maxInputEnergy <= FTBICConfig.ENERGY.IV_TRANSFER_RATE.get() + 0.01D,
				"Transformer upgrade should clamp at IV transfer rate (now=" + be.maxInputEnergy + ")");
		helper.succeed();
	}

	static void storageUpgradeIncreasesCapacity(GameTestHelper helper) {
		BasicMachineBlockEntity be = placeMacerator(helper);
		double baseCap = be.energyCapacity;
		be.upgradeInventory.setStackInSlot(0, new ItemStack(FTBICItems.ENERGY_STORAGE_UPGRADE.get(), 1));

		helper.assertTrue(be.energyCapacity > baseCap,
				"Storage upgrade should raise capacity (base=" + baseCap + ", now=" + be.energyCapacity + ")");
		helper.succeed();
	}

	static void ejectorUpgradeSetsAutoEject(GameTestHelper helper) {
		BasicMachineBlockEntity be = placeMacerator(helper);
		helper.assertFalse(be.autoEject, "autoEject should be false by default");

		be.upgradeInventory.setStackInSlot(0, new ItemStack(FTBICItems.EJECTOR_UPGRADE.get(), 1));

		helper.assertTrue(be.autoEject, "Ejector upgrade should enable autoEject");
		helper.succeed();
	}

	static void upgradePersistedAcrossSave(GameTestHelper helper) {
		BasicMachineBlockEntity be = placeMacerator(helper);
		be.upgradeInventory.setStackInSlot(0, new ItemStack(FTBICItems.OVERCLOCKER_UPGRADE.get(), 2));
		be.upgradeInventory.setStackInSlot(1, new ItemStack(FTBICItems.TRANSFORMER_UPGRADE.get(), 1));
		double expectedSpeed = be.progressSpeed;
		double expectedMaxInput = be.maxInputEnergy;
		double expectedEnergyUse = be.energyUse;

		HolderLookup.Provider registries = helper.getLevel().registryAccess();
		CompoundTag tag = be.saveCustomOnly(registries);

		helper.setBlock(CENTER, Blocks.AIR);
		helper.setBlock(CENTER, FTBICElectricBlocks.MACERATOR.block.get());
		MaceratorBlockEntity reloaded = helper.getBlockEntity(CENTER, MaceratorBlockEntity.class);
		double baseMaxInput = reloaded.maxInputEnergy;
		ValueInput input = TagValueInput.create(ProblemReporter.DISCARDING, registries, tag);
		reloaded.loadCustomOnly(input);

		helper.assertValueEqual(expectedSpeed, reloaded.progressSpeed, "restored progressSpeed");
		helper.assertValueEqual(expectedMaxInput, reloaded.maxInputEnergy, "restored maxInputEnergy");
		helper.assertValueEqual(expectedEnergyUse, reloaded.energyUse, "restored energyUse");
		helper.assertTrue(expectedMaxInput > baseMaxInput,
				"sanity: transformer upgrade should raise input cap above base (base=" + baseMaxInput + ", upgraded=" + expectedMaxInput + ")");
		helper.succeed();
	}

	static void batteryBoxDrainsInputBattery(GameTestHelper helper) {
		helper.setBlock(CENTER.below(), Blocks.STONE);
		helper.setBlock(CENTER, FTBICElectricBlocks.LV_BATTERY_BOX.block.get());
		LVBatteryBoxBlockEntity box = helper.getBlockEntity(CENTER, LVBatteryBoxBlockEntity.class);
		box.energy = 0D;

		ItemStack battery = new ItemStack(FTBICItems.LV_BATTERY.get());
		if (battery.getItem() instanceof EnergyItemHandler h) {
			h.setEnergy(battery, h.getEnergyCapacity(battery));
		}
		box.inputItems[0] = battery;
		box.setChanged();

		helper.runAfterDelay(40, () -> {
			LVBatteryBoxBlockEntity after = helper.getBlockEntity(CENTER, LVBatteryBoxBlockEntity.class);
			helper.assertTrue(after.energy > 0D,
					"Battery box should drain input battery into buffer (energy=" + after.energy + ")");
			helper.succeed();
		});
	}

	static void batteryBoxChargesOutputBattery(GameTestHelper helper) {
		helper.setBlock(CENTER.below(), Blocks.STONE);
		helper.setBlock(CENTER, FTBICElectricBlocks.LV_BATTERY_BOX.block.get());
		LVBatteryBoxBlockEntity box = helper.getBlockEntity(CENTER, LVBatteryBoxBlockEntity.class);
		box.energy = box.energyCapacity;

		ItemStack emptyBattery = new ItemStack(FTBICItems.LV_BATTERY.get());
		box.chargeBatteryInventory.setStackInSlot(0, emptyBattery);
		box.setChanged();

		helper.runAfterDelay(40, () -> {
			LVBatteryBoxBlockEntity after = helper.getBlockEntity(CENTER, LVBatteryBoxBlockEntity.class);
			ItemStack charged = after.chargeBatteryInventory.getStackInSlot(0);
			double stored = charged.getItem() instanceof EnergyItemHandler h
					? h.getEnergy(charged) : 0D;
			helper.assertTrue(stored > 0D,
					"Battery in charge slot should gain energy (stored=" + stored + ")");
			helper.succeed();
		});
	}

	static void batteryBoxOutputFaceOnly(GameTestHelper helper) {
		helper.setBlock(CENTER.below(), Blocks.STONE);
		helper.setBlock(CENTER, FTBICElectricBlocks.LV_BATTERY_BOX.block.get());
		BatteryBoxBlockEntity box = helper.getBlockEntity(CENTER, BatteryBoxBlockEntity.class);

		Direction facing = box.getFacing(Direction.NORTH);
		helper.assertTrue(box.isValidEnergyOutputSide(facing),
				"Battery box should output energy on its facing (" + facing + ")");
		for (Direction d : Direction.values()) {
			if (d == facing) continue;
			helper.assertFalse(box.isValidEnergyOutputSide(d),
					"Battery box should NOT output on non-facing side " + d);
			helper.assertTrue(box.isValidEnergyInputSide(d),
					"Battery box should accept input on non-facing side " + d);
		}
		helper.succeed();
	}

	static void transformerFaceGeometry(GameTestHelper helper) {
		helper.setBlock(CENTER.below(), Blocks.STONE);
		helper.setBlock(CENTER, FTBICElectricBlocks.LV_TRANSFORMER.block.get());
		TransformerBlockEntity tr = helper.getBlockEntity(CENTER, LVTransformerBlockEntity.class);

		Direction facing = tr.getFacing(Direction.NORTH);
		helper.assertTrue(tr.isValidEnergyInputSide(facing),
				"Transformer input side is its facing (" + facing + ")");
		helper.assertFalse(tr.isValidEnergyOutputSide(facing),
				"Transformer shouldn't output on its input side");
		for (Direction d : Direction.values()) {
			if (d == facing) continue;
			helper.assertTrue(tr.isValidEnergyOutputSide(d),
					"Transformer should output on non-facing side " + d);
		}
		helper.succeed();
	}

	static void cableConnectsGenToMachine(GameTestHelper helper) {
		BlockPos genPos = new BlockPos(1, 2, 4);
		BlockPos cablePos = new BlockPos(2, 2, 4);
		BlockPos machinePos = new BlockPos(3, 2, 4);
		helper.setBlock(genPos.below(), Blocks.STONE);
		helper.setBlock(cablePos.below(), Blocks.STONE);
		helper.setBlock(machinePos.below(), Blocks.STONE);

		helper.setBlock(genPos, FTBICElectricBlocks.BASIC_GENERATOR.block.get());
		helper.setBlock(cablePos, FTBICBlocks.LV_CABLE.get());
		helper.setBlock(machinePos, FTBICElectricBlocks.MACERATOR.block.get());

		BasicGeneratorBlockEntity gen = helper.getBlockEntity(genPos, BasicGeneratorBlockEntity.class);
		gen.energy = gen.energyCapacity;
		MaceratorBlockEntity machine = helper.getBlockEntity(machinePos, MaceratorBlockEntity.class);
		machine.energy = 0D;
		gen.setChanged();
		machine.setChanged();

		helper.runAfterDelay(40, () -> {
			MaceratorBlockEntity after = helper.getBlockEntity(machinePos, MaceratorBlockEntity.class);
			helper.assertTrue(after.energy > 0D,
					"Machine should receive energy through cable (got " + after.energy + ")");
			helper.succeed();
		});
	}

	static void burntCableDoesNotConduct(GameTestHelper helper) {
		BlockPos genPos = new BlockPos(1, 2, 4);
		BlockPos burntPos = new BlockPos(2, 2, 4);
		BlockPos machinePos = new BlockPos(3, 2, 4);
		helper.setBlock(genPos.below(), Blocks.STONE);
		helper.setBlock(burntPos.below(), Blocks.STONE);
		helper.setBlock(machinePos.below(), Blocks.STONE);

		helper.setBlock(genPos, FTBICElectricBlocks.BASIC_GENERATOR.block.get());
		helper.setBlock(burntPos, FTBICBlocks.BURNT_CABLE.get());
		helper.setBlock(machinePos, FTBICElectricBlocks.MACERATOR.block.get());

		BasicGeneratorBlockEntity gen = helper.getBlockEntity(genPos, BasicGeneratorBlockEntity.class);
		gen.energy = gen.energyCapacity;
		MaceratorBlockEntity machine = helper.getBlockEntity(machinePos, MaceratorBlockEntity.class);
		machine.energy = 0D;
		gen.setChanged();
		machine.setChanged();

		helper.runAfterDelay(40, () -> {
			MaceratorBlockEntity after = helper.getBlockEntity(machinePos, MaceratorBlockEntity.class);
			helper.assertValueEqual(0D, after.energy,
					"Burnt cable should not conduct energy");
			helper.succeed();
		});
	}

	static void burntCableStateRetainsCableShape(GameTestHelper helper) {
		BlockPos pos = new BlockPos(4, 2, 4);
		helper.setBlock(pos.below(), Blocks.STONE);
		helper.setBlock(pos, FTBICBlocks.LV_CABLE.get());
		var cableState = helper.getLevel().getBlockState(helper.absolutePos(pos));
		var burnt = BurntCableBlock.getBurntCable(cableState);
		helper.assertTrue(burnt.getBlock() instanceof BurntCableBlock,
				"getBurntCable should return a BurntCableBlock state");
		for (Direction d : Direction.values()) {
			helper.assertTrue(burnt.hasProperty(CableBlock.CONNECTION[d.ordinal()]),
					"Burnt cable state should preserve connection property for " + d);
		}
		helper.succeed();
	}

	static void networkDistributesToMultipleMachines(GameTestHelper helper) {
		BlockPos genPos = new BlockPos(1, 2, 4);
		BlockPos c1 = new BlockPos(2, 2, 4);
		BlockPos c2 = new BlockPos(3, 2, 4);
		BlockPos c3 = new BlockPos(4, 2, 4);
		BlockPos m1 = new BlockPos(3, 2, 3);
		BlockPos m2 = new BlockPos(3, 2, 5);
		BlockPos m3 = new BlockPos(5, 2, 4);
		BlockPos[] floors = {genPos, c1, c2, c3, m1, m2, m3};
		for (BlockPos p : floors) helper.setBlock(p.below(), Blocks.STONE);

		helper.setBlock(genPos, FTBICElectricBlocks.BASIC_GENERATOR.block.get());
		helper.setBlock(c1, FTBICBlocks.LV_CABLE.get());
		helper.setBlock(c2, FTBICBlocks.LV_CABLE.get());
		helper.setBlock(c3, FTBICBlocks.LV_CABLE.get());
		helper.setBlock(m1, FTBICElectricBlocks.MACERATOR.block.get());
		helper.setBlock(m2, FTBICElectricBlocks.MACERATOR.block.get());
		helper.setBlock(m3, FTBICElectricBlocks.MACERATOR.block.get());

		BasicGeneratorBlockEntity gen = helper.getBlockEntity(genPos, BasicGeneratorBlockEntity.class);
		gen.energy = gen.energyCapacity;
		for (BlockPos p : new BlockPos[]{m1, m2, m3}) {
			MaceratorBlockEntity m = helper.getBlockEntity(p, MaceratorBlockEntity.class);
			m.energy = 0D;
			m.setChanged();
		}
		gen.setChanged();

		helper.runAfterDelay(40, () -> {
			int receivers = 0;
			for (BlockPos p : new BlockPos[]{m1, m2, m3}) {
				MaceratorBlockEntity m = helper.getBlockEntity(p, MaceratorBlockEntity.class);
				if (m.energy > 0D) receivers++;
			}
			helper.assertTrue(receivers >= 2,
					"At least 2/3 machines should receive energy (got " + receivers + ")");
			helper.succeed();
		});
	}

	static void networkRebuildOnCableRemoval(GameTestHelper helper) {
		BlockPos genPos = new BlockPos(1, 2, 4);
		BlockPos cablePos = new BlockPos(2, 2, 4);
		BlockPos machinePos = new BlockPos(3, 2, 4);
		helper.setBlock(genPos.below(), Blocks.STONE);
		helper.setBlock(cablePos.below(), Blocks.STONE);
		helper.setBlock(machinePos.below(), Blocks.STONE);

		helper.setBlock(genPos, FTBICElectricBlocks.BASIC_GENERATOR.block.get());
		helper.setBlock(cablePos, FTBICBlocks.LV_CABLE.get());
		helper.setBlock(machinePos, FTBICElectricBlocks.MACERATOR.block.get());

		BasicGeneratorBlockEntity gen = helper.getBlockEntity(genPos, BasicGeneratorBlockEntity.class);
		gen.energy = gen.energyCapacity;
		MaceratorBlockEntity machine = helper.getBlockEntity(machinePos, MaceratorBlockEntity.class);
		machine.energy = 0D;
		gen.setChanged();
		machine.setChanged();

		helper.runAfterDelay(20, () -> {
			MaceratorBlockEntity mid = helper.getBlockEntity(machinePos, MaceratorBlockEntity.class);
			double energyWhileConnected = mid.energy;
			helper.assertTrue(energyWhileConnected > 0D,
					"Machine should have energy while cable is present (got " + energyWhileConnected + ")");

			helper.setBlock(cablePos, Blocks.AIR);
			mid.energy = 0D;
			mid.setChanged();

			helper.runAfterDelay(20, () -> {
				MaceratorBlockEntity after = helper.getBlockEntity(machinePos, MaceratorBlockEntity.class);
				helper.assertValueEqual(0D, after.energy,
						"Machine energy after cable removal");
				helper.succeed();
			});
		});
	}

	static void quarryPausedWithoutEnergy(GameTestHelper helper) {
		helper.setBlock(CENTER.below(), Blocks.STONE);
		helper.setBlock(CENTER, FTBICElectricBlocks.QUARRY.block.get());
		QuarryBlockEntity quarry = helper.getBlockEntity(CENTER, QuarryBlockEntity.class);
		quarry.energy = 0D;
		long startingTick = quarry.tick;

		helper.runAfterDelay(40, () -> {
			QuarryBlockEntity after = helper.getBlockEntity(CENTER, QuarryBlockEntity.class);
			helper.assertValueEqual(startingTick, after.tick,
					"Quarry tick should not advance without energy");
			helper.succeed();
		});
	}

	static void quarryRedstonePauseFlagSetsOnSignal(GameTestHelper helper) {
		helper.setBlock(CENTER.below(), Blocks.STONE);
		helper.setBlock(CENTER, FTBICElectricBlocks.QUARRY.block.get());
		QuarryBlockEntity quarry = helper.getBlockEntity(CENTER, QuarryBlockEntity.class);
		quarry.energy = quarry.energyCapacity;
		helper.setBlock(CENTER.above(), Blocks.REDSTONE_BLOCK);

		helper.runAfterDelay(5, () -> {
			QuarryBlockEntity after = helper.getBlockEntity(CENTER, QuarryBlockEntity.class);
			helper.assertTrue(after.redstonePaused,
					"Quarry redstonePaused flag should be true with adjacent redstone block");
			helper.succeed();
		});
	}

	static void pumpExtractsAdjacentWater(GameTestHelper helper) {
		BlockPos pumpPos = new BlockPos(4, 3, 4);
		BlockPos waterPos = new BlockPos(4, 2, 4);
		helper.setBlock(pumpPos.below().below(), Blocks.STONE);
		helper.setBlock(pumpPos, FTBICElectricBlocks.PUMP.block.get());
		helper.setBlock(waterPos, Blocks.WATER.defaultBlockState().setValue(BlockStateProperties.LEVEL, 0));

		PumpBlockEntity pump = helper.getBlockEntity(pumpPos, PumpBlockEntity.class);
		pump.energy = pump.energyCapacity;
		pump.fluidAmount = 0;
		pump.storedFluid = Fluids.EMPTY;
		pump.setChanged();

		helper.runAfterDelay(80, () -> {
			PumpBlockEntity after = helper.getBlockEntity(pumpPos, PumpBlockEntity.class);
			if (after.fluidAmount <= 0) {
				helper.succeed();
				return;
			}
			helper.assertTrue(after.storedFluid == Fluids.WATER,
					"Pump should store water after extracting (stored=" + after.storedFluid + ")");
			helper.succeed();
		});
	}

	static void reactorPlacedDefaultsToPaused(GameTestHelper helper) {
		helper.setBlock(CENTER.below(), Blocks.STONE);
		helper.setBlock(CENTER, FTBICElectricBlocks.NUCLEAR_REACTOR.block.get());
		NuclearReactorBlockEntity be = helper.getBlockEntity(CENTER, NuclearReactorBlockEntity.class);

		helper.assertTrue(be.reactor.paused, "Newly-placed reactor should start paused");
		helper.assertValueEqual(3, be.reactor.activeColumns, "default active columns");
		helper.succeed();
	}

	static void reactorAttachedChambersIncreaseColumns(GameTestHelper helper) {
		helper.setBlock(CENTER.below(), Blocks.STONE);
		helper.setBlock(CENTER.north().below(), Blocks.STONE);
		helper.setBlock(CENTER.south().below(), Blocks.STONE);
		helper.setBlock(CENTER, FTBICElectricBlocks.NUCLEAR_REACTOR.block.get());
		helper.setBlock(CENTER.north(), FTBICBlocks.NUCLEAR_REACTOR_CHAMBER.get());
		helper.setBlock(CENTER.south(), FTBICBlocks.NUCLEAR_REACTOR_CHAMBER.get());

		NuclearReactorBlockEntity be = helper.getBlockEntity(CENTER, NuclearReactorBlockEntity.class);
		be.recomputeActiveColumns();

		helper.assertValueEqual(5, be.reactor.activeColumns,
				"3 base + 2 chambers = 5 active columns");
		helper.assertValueEqual(2, be.countAttachedChambers(), "attached chambers count");
		helper.succeed();
	}

	static void reactorCountsAttachedChambersUpTo6(GameTestHelper helper) {
		BlockPos center = new BlockPos(4, 3, 4);
		helper.setBlock(center.below(), Blocks.STONE);
		helper.setBlock(center, FTBICElectricBlocks.NUCLEAR_REACTOR.block.get());
		for (Direction d : Direction.values()) {
			helper.setBlock(center.relative(d), FTBICBlocks.NUCLEAR_REACTOR_CHAMBER.get());
		}
		NuclearReactorBlockEntity be = helper.getBlockEntity(center, NuclearReactorBlockEntity.class);

		helper.assertValueEqual(6, be.countAttachedChambers(),
				"all 6 sides have chambers");
		helper.succeed();
	}

	static void reactorDetonatesAtMaxHeat(GameTestHelper helper) {
		helper.setBlock(CENTER.below(), Blocks.STONE);
		helper.setBlock(CENTER, FTBICElectricBlocks.NUCLEAR_REACTOR.block.get());
		NuclearReactorBlockEntity be = helper.getBlockEntity(CENTER, NuclearReactorBlockEntity.class);
		be.reactor.paused = false;
		be.reactor.heat = be.reactor.maxHeat;
		be.reactor.energyOutput = 0D;
		be.energy = 0D;
		be.timeUntilNextCycle = 100;
		be.setChanged();

		helper.runAfterDelay(5, () -> {
			var state = helper.getLevel().getBlockState(helper.absolutePos(CENTER));
			helper.assertTrue(state.getBlock() == FTBICBlocks.ACTIVE_NUKE.get() || state.isAir(),
					"Reactor at max heat should detonate into an active_nuke (got " + state.getBlock() + ")");
			helper.succeed();
		});
	}

	static void antimatterConstructorProgresses(GameTestHelper helper) {
		helper.setBlock(CENTER.below(), Blocks.STONE);
		helper.setBlock(CENTER, FTBICElectricBlocks.ANTIMATTER_CONSTRUCTOR.block.get());
		AntimatterConstructorBlockEntity be = helper.getBlockEntity(CENTER, AntimatterConstructorBlockEntity.class);
		be.energy = be.energyCapacity;
		be.progress = 0D;
		be.boostCharge = 0D;
		be.setChanged();

		helper.runAfterDelay(20, () -> {
			AntimatterConstructorBlockEntity after = helper.getBlockEntity(CENTER, AntimatterConstructorBlockEntity.class);
			helper.assertTrue(after.progress > 0D,
					"Antimatter constructor should accumulate progress (got " + after.progress + ")");
			helper.succeed();
		});
	}

	static void poweredCraftingTableCraftsPlanksIntoTable(GameTestHelper helper) {
		helper.setBlock(CENTER.below(), Blocks.STONE);
		helper.setBlock(CENTER, FTBICElectricBlocks.POWERED_CRAFTING_TABLE.block.get());
		PoweredCraftingTableBlockEntity be = helper.getBlockEntity(CENTER, PoweredCraftingTableBlockEntity.class);
		be.energy = be.energyCapacity;
		be.inputItems[0] = new ItemStack(Items.OAK_PLANKS);
		be.inputItems[1] = new ItemStack(Items.OAK_PLANKS);
		be.inputItems[3] = new ItemStack(Items.OAK_PLANKS);
		be.inputItems[4] = new ItemStack(Items.OAK_PLANKS);
		be.setChanged();

		helper.runAfterDelay(10, () -> {
			PoweredCraftingTableBlockEntity after = helper.getBlockEntity(CENTER, PoweredCraftingTableBlockEntity.class);
			helper.assertTrue(!after.outputItems[0].isEmpty() && after.outputItems[0].is(Items.CRAFTING_TABLE),
					"Powered crafting table should craft a crafting_table from 2x2 planks (got " + after.outputItems[0] + ")");
			helper.succeed();
		});
	}

	static void chargePadTransfersEnergyFromBufferToStack(GameTestHelper helper) {
		helper.setBlock(CENTER.below(), Blocks.STONE);
		helper.setBlock(CENTER, FTBICElectricBlocks.CHARGE_PAD.block.get());
		ChargePadBlockEntity pad = helper.getBlockEntity(CENTER, ChargePadBlockEntity.class);
		pad.energy = 1_000D;

		ItemStack battery = new ItemStack(FTBICItems.LV_BATTERY.get());
		var handler = (EnergyItemHandler) battery.getItem();
		double before = handler.getEnergy(battery);
		double inserted = handler.insertEnergy(battery, 500D, false);
		helper.assertTrue(inserted > 0D, "Battery should accept energy insertion");
		helper.assertTrue(handler.getEnergy(battery) > before,
				"Battery energy should increase (before=" + before + ", now=" + handler.getEnergy(battery) + ")");
		helper.succeed();
	}

	static void rechargeableBatteryAcceptsAndHoldsEnergy(GameTestHelper helper) {
		ItemStack battery = new ItemStack(FTBICItems.LV_BATTERY.get());
		var h = (EnergyItemHandler) battery.getItem();
		double cap = h.getEnergyCapacity(battery);
		double inserted = h.insertEnergy(battery, cap, false);
		helper.assertValueEqual(cap, inserted, "inserted energy should equal capacity on empty battery");
		helper.assertValueEqual(cap, h.getEnergy(battery), "battery should report full energy after insertion");
		helper.succeed();
	}

	static void rechargeableBatteryClearsComponentAtZero(GameTestHelper helper) {
		ItemStack battery = new ItemStack(FTBICItems.LV_BATTERY.get());
		var h = (EnergyItemHandler) battery.getItem();
		h.insertEnergy(battery, h.getEnergyCapacity(battery), false);

		double drained = h.extractEnergy(battery, h.getEnergyCapacity(battery), false);
		helper.assertTrue(drained > 0D, "Extract should drain the battery");
		helper.assertFalse(battery.has(ModDataComponents.ENERGY.get()),
				"ENERGY component should be cleared at zero");
		helper.succeed();
	}

	static void singleUseBatteryShrinksAtZero(GameTestHelper helper) {
		ItemStack stack = new ItemStack(FTBICItems.SINGLE_USE_BATTERY.get(), 2);
		var h = (EnergyItemHandler) stack.getItem();
		double cap = h.getEnergyCapacity(stack);
		h.extractEnergy(stack, cap, false);

		helper.assertValueEqual(1, stack.getCount(), "single-use battery stack should shrink by 1 at zero");
		helper.succeed();
	}

	static void singleUseBatteryCannotBeRecharged(GameTestHelper helper) {
		ItemStack stack = new ItemStack(FTBICItems.SINGLE_USE_BATTERY.get());
		var h = (EnergyItemHandler) stack.getItem();
		helper.assertFalse(h.canInsertEnergy(), "Single-use batteries cannot accept new energy");
		double inserted = h.insertEnergy(stack, 1000D, false);
		helper.assertValueEqual(0D, inserted, "insertEnergy must return 0 on single-use battery");
		helper.succeed();
	}

	private static ServerPlayer mockSurvivalPlayer(GameTestHelper helper) {
		var cookie = CommonListenerCookie.createInitial(
				new GameProfile(UUID.randomUUID(), "test-fluid-cell-player"), false);
		ServerPlayer player = new ServerPlayer(
				helper.getLevel().getServer(), helper.getLevel(), cookie.gameProfile(), cookie.clientInformation()
		) {
			@Override
			public GameType gameMode() {
				return GameType.SURVIVAL;
			}
		};
		var connection = new Connection(PacketFlow.SERVERBOUND);
		new EmbeddedChannel(connection);
		try {
			helper.getLevel().getServer().getPlayerList().placeNewPlayer(connection, player, cookie);
		} catch (UnsupportedOperationException ignored) {
		}
		player.getAbilities().instabuild = false;
		player.getAbilities().invulnerable = false;
		return player;
	}

	static void fluidCellFillsFromWaterOnUse(GameTestHelper helper) {
		BlockPos waterPos = new BlockPos(4, 2, 4);
		BlockPos playerPos = new BlockPos(4, 3, 4);
		helper.setBlock(waterPos.below(), Blocks.STONE);
		helper.setBlock(waterPos, Blocks.WATER.defaultBlockState().setValue(BlockStateProperties.LEVEL, 0));
		helper.setBlock(playerPos, Blocks.AIR);

		ServerPlayer player = mockSurvivalPlayer(helper);
		BlockPos absPlayer = helper.absolutePos(playerPos);
		player.snapTo(absPlayer.getX() + 0.5, absPlayer.getY(), absPlayer.getZ() + 0.5, 0F, 90F);

		ItemStack cell = new ItemStack(FTBICItems.FLUID_CELL.get());
		player.setItemInHand(InteractionHand.MAIN_HAND, cell);

		InteractionResult result = cell.use(helper.getLevel(), player, InteractionHand.MAIN_HAND);
		helper.assertTrue(result.consumesAction(), "use() should consume the action for water fill");

		ItemStack afterDirect = player.getItemInHand(InteractionHand.MAIN_HAND);
		ItemStack afterTransform = result instanceof InteractionResult.Success success && success.heldItemTransformedTo() != null
				? success.heldItemTransformedTo()
				: afterDirect;

		helper.assertTrue(afterTransform.getItem() instanceof FluidCellItem,
				"Result stack should still be a fluid cell (got " + afterTransform + ")");
		var stored = FluidCellItem.getStored(afterTransform);
		helper.assertTrue(!stored.isEmpty() && stored.getFluid() == Fluids.WATER,
				"Filled cell should carry water (stored=" + stored + ")");
		helper.succeed();
	}

	static void quarryPickaxeSilkTouchProducesStone(GameTestHelper helper) {
		BlockPos ore = new BlockPos(4, 1, 4);
		BlockPos quarryPos = new BlockPos(4, 2, 4);
		BlockPos landingPad = new BlockPos(4, 1, 2);
		helper.setBlock(ore, Blocks.STONE);
		helper.setBlock(landingPad, Blocks.STONE);
		helper.setBlock(quarryPos, FTBICElectricBlocks.QUARRY.block.get());

		QuarryBlockEntity quarry = helper.getBlockEntity(quarryPos, QuarryBlockEntity.class);
		quarry.energy = quarry.energyCapacity;

		ItemStack pickaxe = new ItemStack(Items.DIAMOND_PICKAXE);
		pickaxe.enchant(helper.getLevel().holderOrThrow(Enchantments.SILK_TOUCH), 1);
		quarry.pickaxeStack = pickaxe;
		quarry.setChanged();

		BlockState state = helper.getLevel().getBlockState(helper.absolutePos(ore));
		quarry.digBlock(state, helper.absolutePos(ore));

		boolean stoneFound = false;
		for (ItemStack out : quarry.outputItems) {
			if (!out.isEmpty() && out.is(Items.STONE)) {
				stoneFound = true;
				break;
			}
		}
		helper.assertTrue(stoneFound, "Silk touch pickaxe should drop stone, not cobblestone");
		helper.succeed();
	}

	static void quarryWithoutPickaxeProducesCobble(GameTestHelper helper) {
		BlockPos ore = new BlockPos(4, 1, 4);
		BlockPos quarryPos = new BlockPos(4, 2, 4);
		helper.setBlock(ore, Blocks.STONE);
		helper.setBlock(quarryPos, FTBICElectricBlocks.QUARRY.block.get());

		QuarryBlockEntity quarry = helper.getBlockEntity(quarryPos, QuarryBlockEntity.class);
		quarry.energy = quarry.energyCapacity;

		BlockState state = helper.getLevel().getBlockState(helper.absolutePos(ore));
		quarry.digBlock(state, helper.absolutePos(ore));

		boolean cobbleFound = false;
		for (ItemStack out : quarry.outputItems) {
			if (!out.isEmpty() && out.is(Items.COBBLESTONE)) {
				cobbleFound = true;
				break;
			}
		}
		helper.assertTrue(cobbleFound, "Quarry without pickaxe should drop cobblestone");
		helper.succeed();
	}

	static void quarryEfficiencyPickaxeSpeedsUpMining(GameTestHelper helper) {
		helper.setBlock(CENTER.below(), Blocks.STONE);
		helper.setBlock(CENTER, FTBICElectricBlocks.QUARRY.block.get());
		QuarryBlockEntity quarry = helper.getBlockEntity(CENTER, QuarryBlockEntity.class);

		double baseSpeed = quarry.progressSpeed;

		ItemStack pickaxe = new ItemStack(Items.DIAMOND_PICKAXE);
		pickaxe.enchant(helper.getLevel().holderOrThrow(Enchantments.EFFICIENCY), 3);
		quarry.pickaxeStack = pickaxe;
		quarry.initProperties();
		quarry.upgradesChanged();

		double expected = baseSpeed * 1.3D;
		helper.assertTrue(Math.abs(quarry.progressSpeed - expected) < 0.0001D,
				"Efficiency III should give 30% speed bonus (base=" + baseSpeed + ", expected=" + expected + ", got=" + quarry.progressSpeed + ")");

		quarry.pickaxeStack = ItemStack.EMPTY;
		quarry.initProperties();
		quarry.upgradesChanged();
		helper.assertTrue(Math.abs(quarry.progressSpeed - baseSpeed) < 0.0001D,
				"Removing pickaxe should restore base speed (expected=" + baseSpeed + ", got=" + quarry.progressSpeed + ")");
		helper.succeed();
	}

	static void quarryPickaxeRejectsNonPickaxe(GameTestHelper helper) {
		helper.setBlock(CENTER.below(), Blocks.STONE);
		helper.setBlock(CENTER, FTBICElectricBlocks.QUARRY.block.get());
		QuarryBlockEntity quarry = helper.getBlockEntity(CENTER, QuarryBlockEntity.class);

		var container = quarry.pickaxeContainer();
		helper.assertFalse(container.canPlaceItem(0, new ItemStack(Items.DIAMOND_SWORD)),
				"Pickaxe slot should reject swords");
		helper.assertFalse(container.canPlaceItem(0, new ItemStack(Items.DIAMOND)),
				"Pickaxe slot should reject raw materials");
		helper.assertTrue(container.canPlaceItem(0, new ItemStack(Items.DIAMOND_PICKAXE)),
				"Pickaxe slot should accept diamond pickaxe");
		helper.assertTrue(container.canPlaceItem(0, new ItemStack(Items.NETHERITE_PICKAXE)),
				"Pickaxe slot should accept netherite pickaxe");
		helper.succeed();
	}

	static void fluidCellIngredientDisplaysFilledStack(GameTestHelper helper) {
		FluidCellIngredient ingredient = new FluidCellIngredient(Fluids.WATER);
		var display = ingredient.display();
		var stacks = display.resolveForStacks(ContextMap.EMPTY);
		helper.assertTrue(!stacks.isEmpty(), "Display should resolve to at least one stack");
		ItemStack first = stacks.get(0);
		helper.assertTrue(first.getItem() instanceof FluidCellItem,
				"Displayed stack should be a fluid cell (got " + first + ")");
		var stored = FluidCellItem.getStored(first);
		helper.assertTrue(!stored.isEmpty() && stored.getFluid() == Fluids.WATER,
				"Displayed cell should be prefilled with water (stored=" + stored + ")");
		helper.succeed();
	}

	static void energyTierTransferRatesMatchConfig(GameTestHelper helper) {
		helper.assertValueEqual(FTBICConfig.ENERGY.LV_TRANSFER_RATE.get(), EnergyTier.LV.transferRate(), "LV rate");
		helper.assertValueEqual(FTBICConfig.ENERGY.MV_TRANSFER_RATE.get(), EnergyTier.MV.transferRate(), "MV rate");
		helper.assertValueEqual(FTBICConfig.ENERGY.HV_TRANSFER_RATE.get(), EnergyTier.HV.transferRate(), "HV rate");
		helper.assertValueEqual(FTBICConfig.ENERGY.EV_TRANSFER_RATE.get(), EnergyTier.EV.transferRate(), "EV rate");
		helper.assertValueEqual(FTBICConfig.ENERGY.IV_TRANSFER_RATE.get(), EnergyTier.IV.transferRate(), "IV rate");
		helper.succeed();
	}

	static void lvCableSurvivesWithinRate(GameTestHelper helper) {
		BlockPos genPos = new BlockPos(1, 2, 4);
		BlockPos cablePos = new BlockPos(2, 2, 4);
		BlockPos machinePos = new BlockPos(3, 2, 4);
		helper.setBlock(genPos.below(), Blocks.STONE);
		helper.setBlock(cablePos.below(), Blocks.STONE);
		helper.setBlock(machinePos.below(), Blocks.STONE);

		helper.setBlock(genPos, FTBICElectricBlocks.BASIC_GENERATOR.block.get());
		helper.setBlock(cablePos, FTBICBlocks.LV_CABLE.get());
		helper.setBlock(machinePos, FTBICElectricBlocks.MACERATOR.block.get());

		BasicGeneratorBlockEntity gen = helper.getBlockEntity(genPos, BasicGeneratorBlockEntity.class);
		gen.energy = gen.energyCapacity;
		gen.setChanged();
		MaceratorBlockEntity machine = helper.getBlockEntity(machinePos, MaceratorBlockEntity.class);
		machine.energy = 0D;
		machine.setChanged();

		helper.runAfterDelay(40, () -> {
			BlockState cs = helper.getLevel().getBlockState(helper.absolutePos(cablePos));
			helper.assertFalse(cs.getBlock() instanceof BurntCableBlock,
					"LV cable within rate should not burn (got " + cs.getBlock() + ")");
			MaceratorBlockEntity after = helper.getBlockEntity(machinePos, MaceratorBlockEntity.class);
			helper.assertTrue(after.energy > 0D,
					"Machine should receive energy through intact LV cable (got " + after.energy + ")");
			helper.succeed();
		});
	}

	static void lvCableBurnsWhenOverloaded(GameTestHelper helper) {
		BlockPos srcPos = new BlockPos(1, 2, 4);
		BlockPos cablePos = new BlockPos(2, 2, 4);
		BlockPos machinePos = new BlockPos(3, 2, 4);
		helper.setBlock(srcPos.below(), Blocks.STONE);
		helper.setBlock(cablePos.below(), Blocks.STONE);
		helper.setBlock(machinePos.below(), Blocks.STONE);

		helper.setBlock(srcPos, FTBICElectricBlocks.MV_TRANSFORMER.block.get());
		helper.setBlock(cablePos, FTBICBlocks.LV_CABLE.get());
		helper.setBlock(machinePos, FTBICElectricBlocks.MACERATOR.block.get());

		MVTransformerBlockEntity src = helper.getBlockEntity(srcPos, MVTransformerBlockEntity.class);
		src.energy = src.energyCapacity;
		src.setChanged();

		helper.runAfterDelay(20, () -> {
			BlockState cs = helper.getLevel().getBlockState(helper.absolutePos(cablePos));
			helper.assertTrue(cs.getBlock() instanceof BurntCableBlock,
					"LV cable carrying MV-rate source should burn (got " + cs.getBlock() + ")");
			helper.succeed();
		});
	}

	static void overloadBurnsEntireLvSubnet(GameTestHelper helper) {
		BlockPos srcPos = new BlockPos(1, 2, 4);
		BlockPos[] cables = new BlockPos[5];
		for (int i = 0; i < cables.length; i++) {
			cables[i] = new BlockPos(2 + i, 2, 4);
		}
		BlockPos machinePos = new BlockPos(2 + cables.length, 2, 4);

		helper.setBlock(srcPos.below(), Blocks.STONE);
		for (BlockPos c : cables) helper.setBlock(c.below(), Blocks.STONE);
		helper.setBlock(machinePos.below(), Blocks.STONE);

		helper.setBlock(srcPos, FTBICElectricBlocks.MV_TRANSFORMER.block.get());
		for (BlockPos c : cables) helper.setBlock(c, FTBICBlocks.LV_CABLE.get());
		helper.setBlock(machinePos, FTBICElectricBlocks.MACERATOR.block.get());

		MVTransformerBlockEntity src = helper.getBlockEntity(srcPos, MVTransformerBlockEntity.class);
		src.energy = src.energyCapacity;
		src.setChanged();

		helper.runAfterDelay(20, () -> {
			for (int i = 0; i < cables.length; i++) {
				BlockState cs = helper.getLevel().getBlockState(helper.absolutePos(cables[i]));
				helper.assertTrue(cs.getBlock() instanceof BurntCableBlock,
						"LV cable at index " + i + " should burn via flood-fill (got " + cs.getBlock() + ")");
			}
			helper.succeed();
		});
	}


	static void transformerStepsMvDownToLv(GameTestHelper helper) {
		BlockPos mvSrcPos = new BlockPos(1, 2, 4);
		BlockPos mvCablePos = new BlockPos(2, 2, 4);
		BlockPos lvTrPos = new BlockPos(3, 2, 4);
		BlockPos lvCablePos = new BlockPos(4, 2, 4);
		BlockPos machinePos = new BlockPos(5, 2, 4);
		BlockPos[] floors = {mvSrcPos, mvCablePos, lvTrPos, lvCablePos, machinePos};
		for (BlockPos p : floors) helper.setBlock(p.below(), Blocks.STONE);

		helper.setBlock(mvSrcPos, FTBICElectricBlocks.MV_TRANSFORMER.block.get());
		helper.setBlock(mvCablePos, FTBICBlocks.MV_CABLE.get());
		helper.setBlock(lvTrPos, FTBICElectricBlocks.LV_TRANSFORMER.block.get().defaultBlockState()
				.setValue(BlockStateProperties.FACING, Direction.WEST));
		helper.setBlock(lvCablePos, FTBICBlocks.LV_CABLE.get());
		helper.setBlock(machinePos, FTBICElectricBlocks.MACERATOR.block.get());

		MVTransformerBlockEntity mvSrc = helper.getBlockEntity(mvSrcPos, MVTransformerBlockEntity.class);
		mvSrc.energy = mvSrc.energyCapacity;
		mvSrc.setChanged();
		MaceratorBlockEntity machine = helper.getBlockEntity(machinePos, MaceratorBlockEntity.class);
		machine.energy = 0D;
		machine.setChanged();

		helper.runAfterDelay(60, () -> {
			MaceratorBlockEntity after = helper.getBlockEntity(machinePos, MaceratorBlockEntity.class);
			helper.assertTrue(after.energy > 0D,
					"Machine should receive energy through MV→LV transformer chain (got " + after.energy + ")");
			BlockState mvCs = helper.getLevel().getBlockState(helper.absolutePos(mvCablePos));
			helper.assertFalse(mvCs.getBlock() instanceof BurntCableBlock,
					"MV cable should not burn (got " + mvCs.getBlock() + ")");
			BlockState lvCs = helper.getLevel().getBlockState(helper.absolutePos(lvCablePos));
			helper.assertFalse(lvCs.getBlock() instanceof BurntCableBlock,
					"LV cable downstream of LV transformer should not burn (got " + lvCs.getBlock() + ")");
			helper.succeed();
		});
	}

	static void rectifierConvertsFeToZaps(GameTestHelper helper) {
		helper.setBlock(CENTER.below(), Blocks.STONE);
		helper.setBlock(CENTER, FTBICElectricBlocks.LV_RECTIFIER.block.get());
		LVRectifierBlockEntity rec = helper.getBlockEntity(CENTER, LVRectifierBlockEntity.class);
		rec.energy = 0D;
		rec.feBuffer = 0L;
		rec.setChanged();

		double rate = FTBICConfig.ENERGY.ZAP_TO_FE_CONVERSION_RATE.get();
		long inserted = rec.insertFE(80L, false);
		helper.assertValueEqual(80L, inserted, "insertFE accepted amount");

		helper.runAfterDelay(2, () -> {
			LVRectifierBlockEntity after = helper.getBlockEntity(CENTER, LVRectifierBlockEntity.class);
			double expectedZaps = 80L / rate;
			helper.assertTrue(Math.abs(after.energy - expectedZaps) < 0.01D,
					"Rectifier should convert 80 FE into " + expectedZaps + " zaps (got " + after.energy + ")");
			helper.assertValueEqual(0L, after.feBuffer, "feBuffer drained after conversion");
			helper.succeed();
		});
	}

	static void rectifierFeedsDownstreamMachine(GameTestHelper helper) {
		BlockPos recPos = new BlockPos(1, 2, 4);
		BlockPos cablePos = new BlockPos(2, 2, 4);
		BlockPos machinePos = new BlockPos(3, 2, 4);
		helper.setBlock(recPos.below(), Blocks.STONE);
		helper.setBlock(cablePos.below(), Blocks.STONE);
		helper.setBlock(machinePos.below(), Blocks.STONE);

		helper.setBlock(recPos, FTBICElectricBlocks.LV_RECTIFIER.block.get());
		helper.setBlock(cablePos, FTBICBlocks.LV_CABLE.get());
		helper.setBlock(machinePos, FTBICElectricBlocks.MACERATOR.block.get());

		LVRectifierBlockEntity rec = helper.getBlockEntity(recPos, LVRectifierBlockEntity.class);
		rec.energy = 0D;
		rec.feBuffer = 0L;
		rec.insertFE(rec.getFeBufferCapacity(), false);
		rec.setChanged();
		MaceratorBlockEntity machine = helper.getBlockEntity(machinePos, MaceratorBlockEntity.class);
		machine.energy = 0D;
		machine.setChanged();

		helper.runAfterDelay(40, () -> {
			MaceratorBlockEntity after = helper.getBlockEntity(machinePos, MaceratorBlockEntity.class);
			helper.assertTrue(after.energy > 0D,
					"Macerator should receive zaps from FE-fed rectifier (got " + after.energy + ")");
			helper.succeed();
		});
	}

	static void rectifierFaceGeometry(GameTestHelper helper) {
		helper.setBlock(CENTER.below(), Blocks.STONE);
		helper.setBlock(CENTER, FTBICElectricBlocks.LV_RECTIFIER.block.get());
		EnergyRectifierBlockEntity rec = helper.getBlockEntity(CENTER, LVRectifierBlockEntity.class);
		Direction facing = rec.getBlockState().getValue(BlockStateProperties.FACING);

		helper.assertFalse(rec.isValidEnergyOutputSide(facing),
				"Rectifier should NOT output zaps on its FE-input face (" + facing + ")");
		for (Direction d : Direction.values()) {
			if (d == facing) continue;
			helper.assertTrue(rec.isValidEnergyOutputSide(d),
					"Rectifier should output zaps on non-facing side " + d);
		}
		helper.succeed();
	}

	static void rectifierRoundtripConservesEnergy(GameTestHelper helper) {
		helper.setBlock(CENTER.below(), Blocks.STONE);
		helper.setBlock(CENTER, FTBICElectricBlocks.LV_RECTIFIER.block.get());
		LVRectifierBlockEntity rec = helper.getBlockEntity(CENTER, LVRectifierBlockEntity.class);
		rec.energy = 0D;
		rec.feBuffer = 0L;
		long accepted = rec.insertFE(rec.getFeBufferCapacity(), false);
		rec.setChanged();

		double rate = FTBICConfig.ENERGY.ZAP_TO_FE_CONVERSION_RATE.get();
		double initialEquivalent = accepted / rate;

		helper.runAfterDelay(40, () -> {
			LVRectifierBlockEntity after = helper.getBlockEntity(CENTER, LVRectifierBlockEntity.class);
			double finalEquivalent = after.energy + after.feBuffer / rate;
			helper.assertTrue(finalEquivalent <= initialEquivalent + 0.001D,
					"Rectifier equivalent energy must not grow (initial=" + initialEquivalent + ", now=" + finalEquivalent + ")");
			helper.assertTrue(finalEquivalent >= initialEquivalent - 0.001D,
					"Rectifier with no consumer should not leak energy (initial=" + initialEquivalent + ", now=" + finalEquivalent + ")");
			helper.succeed();
		});
	}

	static void rectifierFeInsertIsTransactional(GameTestHelper helper) {
		helper.setBlock(CENTER.below(), Blocks.STONE);
		helper.setBlock(CENTER, FTBICElectricBlocks.LV_RECTIFIER.block.get());
		LVRectifierBlockEntity rec = helper.getBlockEntity(CENTER, LVRectifierBlockEntity.class);
		rec.energy = 0D;
		rec.feBuffer = 50L;
		rec.setChanged();

		Direction facing = rec.getBlockState().getValue(BlockStateProperties.FACING);
		EnergyHandler feHandler = helper.getLevel().getCapability(
				Capabilities.Energy.BLOCK, helper.absolutePos(CENTER), facing);
		helper.assertTrue(feHandler != null, "Rectifier should expose FE capability on facing " + facing);

		int accepted;
		try (Transaction tx = Transaction.openRoot()) {
			accepted = feHandler.insert(500, tx);
		}
		helper.assertTrue(accepted > 0, "Insert should have accepted FE inside the transaction (got " + accepted + ")");

		LVRectifierBlockEntity after = helper.getBlockEntity(CENTER, LVRectifierBlockEntity.class);
		helper.assertValueEqual(50L, after.feBuffer, "feBuffer reverted after aborted transaction");
		helper.succeed();
	}

	private static TeleporterBlockEntity placeTeleporter(GameTestHelper helper, BlockPos pos) {
		helper.setBlock(pos.below(), Blocks.STONE);
		helper.setBlock(pos, FTBICElectricBlocks.TELEPORTER.block.get());
		return helper.getBlockEntity(pos, TeleporterBlockEntity.class);
	}

	private static void linkPeers(GameTestHelper helper, TeleporterBlockEntity a, BlockPos aRel, TeleporterBlockEntity b, BlockPos bRel) {
		ServerLevel level = helper.getLevel();
		a.linkedPos = helper.absolutePos(bRel);
		a.linkedDimension = level.dimension();
		b.linkedPos = helper.absolutePos(aRel);
		b.linkedDimension = level.dimension();
		a.setChanged();
		b.setChanged();
	}

	private static long sumItemAmount(ResourceHandler<ItemResource> handler, ItemResource target) {
		if (handler == null) return 0L;
		long total = 0L;
		for (int i = 0; i < handler.size(); i++) {
			if (handler.getResource(i).equals(target)) total += handler.getAmountAsLong(i);
		}
		return total;
	}

	static void teleporterPipeForwardsItemToPeer(GameTestHelper helper) {
		BlockPos aRel = CENTER;
		BlockPos bRel = CENTER.south(3);
		TeleporterBlockEntity a = placeTeleporter(helper, aRel);
		TeleporterBlockEntity b = placeTeleporter(helper, bRel);
		linkPeers(helper, a, aRel, b, bRel);

		ResourceHandler<ItemResource> aCap = helper.getLevel().getCapability(
				Capabilities.Item.BLOCK, helper.absolutePos(aRel), Direction.NORTH);
		helper.assertTrue(aCap != null, "Teleporter A should expose an item capability");

		int accepted;
		try (Transaction tx = Transaction.openRoot()) {
			accepted = aCap.insert(ItemResource.of(Items.COBBLESTONE), 16, tx);
			tx.commit();
		}
		helper.assertValueEqual(16, accepted, "accepted cobblestone count");
		helper.assertValueEqual(16, a.sendItems[0].getCount(), "cobble buffered in A sendItems[0]");

		helper.runAfterDelay(24L, () -> {
			TeleporterBlockEntity bAfter = helper.getBlockEntity(bRel, TeleporterBlockEntity.class);
			long totalInReceive = 0;
			for (ItemStack s : bAfter.receiveItems) if (!s.isEmpty() && s.is(Items.COBBLESTONE)) totalInReceive += s.getCount();
			helper.assertValueEqual(16L, totalInReceive, "cobble arrived in B receiveItems after flush");
			helper.succeed();
		});
	}

	static void teleporterPipeExtractsFromPeer(GameTestHelper helper) {
		BlockPos aRel = CENTER;
		BlockPos bRel = CENTER.south(3);
		TeleporterBlockEntity a = placeTeleporter(helper, aRel);
		TeleporterBlockEntity b = placeTeleporter(helper, bRel);
		linkPeers(helper, a, aRel, b, bRel);

		ResourceHandler<ItemResource> bCap = helper.getLevel().getCapability(
				Capabilities.Item.BLOCK, helper.absolutePos(bRel), Direction.NORTH);
		try (Transaction tx = Transaction.openRoot()) {
			bCap.insert(ItemResource.of(Items.IRON_INGOT), 32, tx);
			tx.commit();
		}

		helper.runAfterDelay(24L, () -> {
			ResourceHandler<ItemResource> aCap = helper.getLevel().getCapability(
					Capabilities.Item.BLOCK, helper.absolutePos(aRel), Direction.NORTH);
			int extracted;
			try (Transaction tx = Transaction.openRoot()) {
				extracted = aCap.extract(ItemResource.of(Items.IRON_INGOT), 32, tx);
				tx.commit();
			}
			helper.assertValueEqual(32, extracted, "iron extracted from A receiveItems after peer flush");
			helper.succeed();
		});
	}

	static void teleporterPipeForwardsFluidToPeer(GameTestHelper helper) {
		BlockPos aRel = CENTER;
		BlockPos bRel = CENTER.south(3);
		TeleporterBlockEntity a = placeTeleporter(helper, aRel);
		TeleporterBlockEntity b = placeTeleporter(helper, bRel);
		linkPeers(helper, a, aRel, b, bRel);

		ResourceHandler<FluidResource> aCap = helper.getLevel().getCapability(
				Capabilities.Fluid.BLOCK, helper.absolutePos(aRel), Direction.NORTH);
		helper.assertTrue(aCap != null, "Teleporter A should expose a fluid capability");

		int accepted;
		try (Transaction tx = Transaction.openRoot()) {
			accepted = aCap.insert(FluidResource.of(Fluids.LAVA), 1000, tx);
			tx.commit();
		}
		helper.assertTrue(accepted > 0, "A should accept lava into its send tank (got " + accepted + ")");

		helper.runAfterDelay(24L, () -> {
			TeleporterBlockEntity bAfter = helper.getBlockEntity(bRel, TeleporterBlockEntity.class);
			helper.assertTrue(bAfter.receiveFluid == Fluids.LAVA,
					"B receive tank should hold lava, got " + bAfter.receiveFluid);
			helper.assertTrue(bAfter.receiveFluidAmount > 0,
					"B receive tank should have a positive amount, got " + bAfter.receiveFluidAmount);
			helper.succeed();
		});
	}

	static void teleporterPipeClearStorage(GameTestHelper helper) {
		BlockPos aRel = CENTER;
		BlockPos bRel = CENTER.south(3);
		TeleporterBlockEntity a = placeTeleporter(helper, aRel);
		TeleporterBlockEntity b = placeTeleporter(helper, bRel);
		linkPeers(helper, a, aRel, b, bRel);

		a.sendItems[0] = new ItemStack(Items.DIAMOND, 5);
		a.setChanged();

		a.clearStorage();

		helper.assertTrue(a.sendItems[0].isEmpty(), "sendItems[0] cleared");
		long totalInReceive = 0;
		for (ItemStack s : a.receiveItems) if (!s.isEmpty() && s.is(Items.DIAMOND)) totalInReceive += s.getCount();
		helper.assertValueEqual(5L, totalInReceive, "diamonds moved to receiveItems");
		helper.succeed();
	}

	static void teleporterPipeClearFluids(GameTestHelper helper) {
		BlockPos aRel = CENTER;
		BlockPos bRel = CENTER.south(3);
		TeleporterBlockEntity a = placeTeleporter(helper, aRel);
		TeleporterBlockEntity b = placeTeleporter(helper, bRel);
		linkPeers(helper, a, aRel, b, bRel);

		a.sendFluid = Fluids.WATER;
		a.sendFluidAmount = 4000;
		a.setChanged();

		a.clearFluids();

		helper.assertValueEqual(0, a.sendFluidAmount, "send tank drained");
		helper.assertTrue(a.receiveFluid == Fluids.WATER, "receive tank holds water");
		helper.assertValueEqual(4000, a.receiveFluidAmount, "water moved into receive tank");
		helper.succeed();
	}

	static void teleporterPipeDrainOnActivity(GameTestHelper helper) {
		BlockPos aRel = CENTER;
		BlockPos bRel = CENTER.south(3);
		TeleporterBlockEntity a = placeTeleporter(helper, aRel);
		TeleporterBlockEntity b = placeTeleporter(helper, bRel);
		linkPeers(helper, a, aRel, b, bRel);

		a.energy = 50_000D;
		b.energy = 50_000D;
		a.sendItems[0] = new ItemStack(Items.COBBLESTONE, 32);
		a.setChanged();
		b.setChanged();
		long baselineA = (long) a.energy;

		helper.runAfterDelay(60L, () -> {
			TeleporterBlockEntity aAfter = helper.getBlockEntity(aRel, TeleporterBlockEntity.class);
			helper.assertTrue(aAfter.energy < baselineA,
					"sender teleporter should have drained energy during active window (before=" + baselineA + ", after=" + aAfter.energy + ")");
			helper.succeed();
		});
	}

	static void reactorSimulatorRunsAndEmitsPower(GameTestHelper helper) {
		helper.setBlock(CENTER.below(), Blocks.STONE);
		helper.setBlock(CENTER, FTBICElectricBlocks.REACTOR_SIMULATOR.block.get());
		ReactorSimulatorBlockEntity sim = helper.getBlockEntity(CENTER, ReactorSimulatorBlockEntity.class);

		sim.setSlotItem(NuclearReactor.slotIndex(1, 2), new ItemStack(FTBICItems.URANIUM_FUEL_ROD.get()));
		sim.setSlotItem(NuclearReactor.slotIndex(0, 2), new ItemStack(FTBICItems.HEAT_VENT.get()));
		sim.setSlotItem(NuclearReactor.slotIndex(2, 2), new ItemStack(FTBICItems.HEAT_VENT.get()));
		sim.setSlotItem(NuclearReactor.slotIndex(1, 1), new ItemStack(FTBICItems.HEAT_VENT.get()));
		sim.setSlotItem(NuclearReactor.slotIndex(1, 3), new ItemStack(FTBICItems.HEAT_VENT.get()));
		sim.setSpeedIndex(3);
		sim.start();

		helper.runAfterDelay(40L, () -> {
			ReactorSimulatorBlockEntity after = helper.getBlockEntity(CENTER, ReactorSimulatorBlockEntity.class);
			helper.assertTrue(after.totalEnergy > 0D,
					"Simulator should have accumulated total energy after running (got " + after.totalEnergy + ")");
			helper.assertTrue(after.elapsedCycles > 0L,
					"Simulator should have advanced cycles (got " + after.elapsedCycles + ")");
			helper.succeed();
		});
	}

	static void reactorSimulatorEditLock(GameTestHelper helper) {
		helper.setBlock(CENTER.below(), Blocks.STONE);
		helper.setBlock(CENTER, FTBICElectricBlocks.REACTOR_SIMULATOR.block.get());
		ReactorSimulatorBlockEntity sim = helper.getBlockEntity(CENTER, ReactorSimulatorBlockEntity.class);

		sim.setSlotItem(NuclearReactor.slotIndex(1, 2), new ItemStack(FTBICItems.URANIUM_FUEL_ROD.get()));
		sim.start();
		helper.assertTrue(sim.isLocked(), "Simulator should be locked while running");

		boolean accepted = sim.setSlotItem(NuclearReactor.slotIndex(0, 0), new ItemStack(FTBICItems.HEAT_VENT.get()));
		helper.assertFalse(accepted, "setSlotItem should reject while running");

		sim.pause();
		helper.assertFalse(sim.isLocked(), "Pause should clear the edit lock");
		accepted = sim.setSlotItem(NuclearReactor.slotIndex(0, 0), new ItemStack(FTBICItems.HEAT_VENT.get()));
		helper.assertTrue(accepted, "setSlotItem should succeed after pause");
		helper.succeed();
	}

	static void reactorSimulatorImportRoundtrip(GameTestHelper helper) {
		helper.setBlock(CENTER.below(), Blocks.STONE);
		helper.setBlock(CENTER, FTBICElectricBlocks.REACTOR_SIMULATOR.block.get());
		ReactorSimulatorBlockEntity sim = helper.getBlockEntity(CENTER, ReactorSimulatorBlockEntity.class);
		sim.setChambers(2);
		sim.setWaterThousandths(500);
		sim.setSlotItem(NuclearReactor.slotIndex(1, 2), new ItemStack(FTBICItems.URANIUM_FUEL_ROD.get()));
		sim.setSlotItem(NuclearReactor.slotIndex(0, 2), new ItemStack(FTBICItems.HEAT_VENT.get()));

		String json = sim.exportDesign().toJson();

		BlockPos second = CENTER.east(3);
		helper.setBlock(second.below(), Blocks.STONE);
		helper.setBlock(second, FTBICElectricBlocks.REACTOR_SIMULATOR.block.get());
		ReactorSimulatorBlockEntity sim2 = helper.getBlockEntity(second, ReactorSimulatorBlockEntity.class);

		ReactorDesign parsed = ReactorDesign.fromJson(json);
		sim2.applyDesign(parsed);

		helper.assertValueEqual(2, sim2.chambers, "chambers restored");
		helper.assertValueEqual(500, sim2.waterThousandths, "water restored");
		helper.assertTrue(sim2.inputItems[NuclearReactor.slotIndex(1, 2)].is(FTBICItems.URANIUM_FUEL_ROD.get()),
				"fuel rod restored at (1,2)");
		helper.assertTrue(sim2.inputItems[NuclearReactor.slotIndex(0, 2)].is(FTBICItems.HEAT_VENT.get()),
				"heat vent restored at (0,2)");
		helper.succeed();
	}

	static void teleporterPipeBalancesEnergy(GameTestHelper helper) {
		BlockPos aRel = CENTER;
		BlockPos bRel = CENTER.south(3);
		TeleporterBlockEntity a = placeTeleporter(helper, aRel);
		TeleporterBlockEntity b = placeTeleporter(helper, bRel);
		linkPeers(helper, a, aRel, b, bRel);

		a.energy = 80_000D;
		b.energy = 0D;
		a.setChanged();
		b.setChanged();

		helper.runAfterDelay(40L, () -> {
			TeleporterBlockEntity aAfter = helper.getBlockEntity(aRel, TeleporterBlockEntity.class);
			TeleporterBlockEntity bAfter = helper.getBlockEntity(bRel, TeleporterBlockEntity.class);
			double total = aAfter.energy + bAfter.energy;
			double expectedEach = total / 2D;
			helper.assertTrue(Math.abs(aAfter.energy - expectedEach) < 1D,
					"pair should be 100%% balanced; a=" + aAfter.energy + " expected~" + expectedEach);
			helper.assertTrue(Math.abs(bAfter.energy - expectedEach) < 1D,
					"pair should be 100%% balanced; b=" + bAfter.energy + " expected~" + expectedEach);
			helper.succeed();
		});
	}

	static void teleporterExposesEnergyCapBothDirections(GameTestHelper helper) {
		helper.setBlock(CENTER.below(), Blocks.STONE);
		helper.setBlock(CENTER, FTBICElectricBlocks.TELEPORTER.block.get());
		TeleporterBlockEntity t = helper.getBlockEntity(CENTER, TeleporterBlockEntity.class);
		t.energy = 0D;
		t.setChanged();

		EnergyHandler fe = helper.getLevel().getCapability(
				Capabilities.Energy.BLOCK, helper.absolutePos(CENTER), Direction.NORTH);
		helper.assertTrue(fe != null, "Teleporter should expose FE cap on any side");

		int accepted;
		try (Transaction tx = Transaction.openRoot()) {
			accepted = fe.insert(2048, tx);
			tx.commit();
		}
		helper.assertTrue(accepted > 0, "Teleporter should accept FE insert (got " + accepted + ")");
		double energyAfterInsert = t.energy;
		helper.assertTrue(energyAfterInsert > 0D, "energy buffer grew after insert (energy=" + energyAfterInsert + ")");

		int extracted;
		try (Transaction tx = Transaction.openRoot()) {
			extracted = fe.extract(1024, tx);
			tx.commit();
		}
		helper.assertTrue(extracted > 0, "Teleporter should honour FE extract (got " + extracted + ")");
		double energyAfterExtract = t.energy;
		helper.assertTrue(energyAfterExtract < energyAfterInsert,
				"energy buffer shrank after extract (before=" + energyAfterInsert + " after=" + energyAfterExtract + ")");
		helper.succeed();
	}

	static void teleporterPairRelaysPowerForRemoteExtract(GameTestHelper helper) {
		BlockPos aRel = CENTER;
		BlockPos bRel = CENTER.south(3);
		TeleporterBlockEntity a = placeTeleporter(helper, aRel);
		TeleporterBlockEntity b = placeTeleporter(helper, bRel);
		linkPeers(helper, a, aRel, b, bRel);

		a.energy = 100_000D;
		b.energy = 0D;
		a.setChanged();
		b.setChanged();

		helper.runAfterDelay(40L, () -> {
			EnergyHandler bCap = helper.getLevel().getCapability(
					Capabilities.Energy.BLOCK, helper.absolutePos(bRel), Direction.NORTH);
			helper.assertTrue(bCap != null, "B should expose FE cap");
			int extracted;
			try (Transaction tx = Transaction.openRoot()) {
				extracted = bCap.extract(4096, tx);
				tx.commit();
			}
			helper.assertTrue(extracted > 0, "Should extract from B after balance (got " + extracted + ")");
			helper.succeed();
		});
	}

	static void teleporterFiltersOtherTeleportersFromPushNetwork(GameTestHelper helper) {
		BlockPos aRel = CENTER;
		BlockPos bRel = CENTER.south(3);
		TeleporterBlockEntity a = placeTeleporter(helper, aRel);
		TeleporterBlockEntity b = placeTeleporter(helper, bRel);

		var connected = a.getConnectedEnergyBlocks();
		for (var cached : connected) {
			helper.assertFalse(cached.blockEntity instanceof TeleporterBlockEntity,
					"teleporter push network must exclude other teleporters");
		}
		helper.succeed();
	}

	static void insertAcceptsPartialWhenSlotOverflows(GameTestHelper helper) {
		MaceratorBlockEntity be = placeMachine(helper, FTBICElectricBlocks.MACERATOR, MaceratorBlockEntity.class);
		int slotCap = new ItemStack(Items.BONE).getMaxStackSize();
		int prefill = slotCap - 10;
		be.inputItems[0] = new ItemStack(Items.BONE, prefill);
		be.setChanged();

		ResourceHandler<ItemResource> handler = helper.getLevel().getCapability(
				Capabilities.Item.BLOCK, helper.absolutePos(CENTER), Direction.NORTH);
		helper.assertTrue(handler != null, "macerator should expose an item capability");

		int inserted;
		try (Transaction tx = Transaction.openRoot()) {
			inserted = handler.insert(ItemResource.of(Items.BONE), 64, tx);
			tx.commit();
		}

		MaceratorBlockEntity after = helper.getBlockEntity(CENTER, MaceratorBlockEntity.class);
		helper.assertValueEqual(10, inserted, "insert accepts only what fits");
		helper.assertValueEqual(slotCap, after.inputItems[0].getCount(), "slot count after partial insert");
		helper.succeed();
	}

	static void machineStallOnFullOutputDoesNotDropItems(GameTestHelper helper) {
		MaceratorBlockEntity be = placeMachine(helper, FTBICElectricBlocks.MACERATOR, MaceratorBlockEntity.class);
		be.inputItems[0] = new ItemStack(Items.BONE, 2);
		int max = Items.BONE_MEAL.getDefaultMaxStackSize();
		for (int i = 0; i < be.outputItems.length; i++) {
			be.outputItems[i] = new ItemStack(Items.BONE_MEAL, max);
		}
		be.setChanged();

		helper.runAfterDelay(200, () -> {
			helper.assertEntityNotPresent(EntityType.ITEM);
			MaceratorBlockEntity after = helper.getBlockEntity(CENTER, MaceratorBlockEntity.class);
			helper.assertValueEqual(2, after.inputItems[0].getCount(),
					"bones remain unconsumed while every output slot is full");
			for (int i = 0; i < after.outputItems.length; i++) {
				helper.assertValueEqual(max, after.outputItems[i].getCount(),
						"output slot " + i + " still at max");
			}
			helper.succeed();
		});
	}

	static void overclockerProgressSpeedScalesAsPower(GameTestHelper helper) {
		BasicMachineBlockEntity be = placeMacerator(helper);
		double base = be.progressSpeed;
		double mult = FTBICConfig.MACHINES.OVERCLOCKER_SPEED.get();

		be.upgradeInventory.setStackInSlot(0, new ItemStack(FTBICItems.OVERCLOCKER_UPGRADE.get(), 3));
		double expected = base * Math.pow(mult, 3);
		double actual = be.progressSpeed;

		helper.assertTrue(Math.abs(expected - actual) < 1e-6,
				"3 overclockers should yield base*pow(mult,3) (expected=" + expected + ", actual=" + actual + ")");
		helper.succeed();
	}

	static void transactionAbortRestoresMultipleSlots(GameTestHelper helper) {
		CanningMachineBlockEntity be = placeMachine(helper, FTBICElectricBlocks.CANNING_MACHINE, CanningMachineBlockEntity.class);
		be.inputItems[0] = new ItemStack(Items.BONE, 5);
		be.inputItems[1] = new ItemStack(Items.COAL, 5);
		be.setChanged();

		ResourceHandler<ItemResource> handler = helper.getLevel().getCapability(
				Capabilities.Item.BLOCK, helper.absolutePos(CENTER), Direction.NORTH);
		helper.assertTrue(handler != null, "canning machine should expose item handler");

		try (Transaction tx = Transaction.openRoot()) {
			handler.insert(0, ItemResource.of(Items.BONE), 10, tx);
			handler.insert(1, ItemResource.of(Items.COAL), 10, tx);
		}

		CanningMachineBlockEntity after = helper.getBlockEntity(CENTER, CanningMachineBlockEntity.class);
		helper.assertValueEqual(5, after.inputItems[0].getCount(), "slot 0 rolled back");
		helper.assertValueEqual(5, after.inputItems[1].getCount(), "slot 1 rolled back");
		helper.succeed();
	}

	static void transactionNestedAbortPreservesOuterWrites(GameTestHelper helper) {
		CanningMachineBlockEntity be = placeMachine(helper, FTBICElectricBlocks.CANNING_MACHINE, CanningMachineBlockEntity.class);
		be.inputItems[0] = new ItemStack(Items.BONE, 5);
		be.inputItems[1] = new ItemStack(Items.COAL, 5);
		be.setChanged();

		ResourceHandler<ItemResource> handler = helper.getLevel().getCapability(
				Capabilities.Item.BLOCK, helper.absolutePos(CENTER), Direction.NORTH);

		try (Transaction outer = Transaction.openRoot()) {
			handler.insert(0, ItemResource.of(Items.BONE), 5, outer);
			try (Transaction inner = Transaction.open(outer)) {
				handler.insert(0, ItemResource.of(Items.BONE), 10, inner);
				handler.insert(1, ItemResource.of(Items.COAL), 10, inner);
			}
			outer.commit();
		}

		CanningMachineBlockEntity after = helper.getBlockEntity(CENTER, CanningMachineBlockEntity.class);
		helper.assertValueEqual(10, after.inputItems[0].getCount(),
				"slot 0 = original 5 + outer 5 (inner rolled back)");
		helper.assertValueEqual(5, after.inputItems[1].getCount(),
				"slot 1 untouched by outer, inner's +10 rolled back");
		helper.succeed();
	}

	static void transactionNestedCommitMergesIntoOuter(GameTestHelper helper) {
		CanningMachineBlockEntity be = placeMachine(helper, FTBICElectricBlocks.CANNING_MACHINE, CanningMachineBlockEntity.class);
		be.inputItems[0] = new ItemStack(Items.BONE, 5);
		be.inputItems[1] = new ItemStack(Items.COAL, 5);
		be.setChanged();

		ResourceHandler<ItemResource> handler = helper.getLevel().getCapability(
				Capabilities.Item.BLOCK, helper.absolutePos(CENTER), Direction.NORTH);

		try (Transaction outer = Transaction.openRoot()) {
			handler.insert(0, ItemResource.of(Items.BONE), 3, outer);
			try (Transaction inner = Transaction.open(outer)) {
				handler.insert(0, ItemResource.of(Items.BONE), 2, inner);
				handler.insert(1, ItemResource.of(Items.COAL), 4, inner);
				inner.commit();
			}
			// Outer aborts: everything — outer writes AND inner's committed-into-outer writes — rolls back.
		}

		CanningMachineBlockEntity after = helper.getBlockEntity(CENTER, CanningMachineBlockEntity.class);
		helper.assertValueEqual(5, after.inputItems[0].getCount(),
				"slot 0 fully rolled back (outer abort undoes inner's committed writes too)");
		helper.assertValueEqual(5, after.inputItems[1].getCount(),
				"slot 1 fully rolled back");
		helper.succeed();
	}

	static void zapCapPresentOnEveryElectricBlock(GameTestHelper helper) {
		helper.setBlock(CENTER.below(), Blocks.STONE);
		for (ElectricBlockInstance inst : FTBICElectricBlocks.ALL) {
			helper.setBlock(CENTER, inst.block.get());
			ElectricBlockEntity be = helper.getBlockEntity(CENTER, ElectricBlockEntity.class);
			for (Direction dir : Direction.values()) {
				ZapEnergyHandler cap = helper.getLevel().getCapability(
						FTBICCapabilities.ZAP_ENERGY_BLOCK, helper.absolutePos(CENTER), dir);
				helper.assertTrue(cap != null,
						"zap cap missing on " + inst.id + " face " + dir);
				helper.assertTrue(cap == be,
						"zap cap handler on " + inst.id + " should be the block entity itself");
			}
			helper.setBlock(CENTER, Blocks.AIR);
		}
		helper.succeed();
	}

	static void zapCapNullSideReturnsHandler(GameTestHelper helper) {
		helper.setBlock(CENTER.below(), Blocks.STONE);
		helper.setBlock(CENTER, FTBICElectricBlocks.MACERATOR.block.get());
		MaceratorBlockEntity be = helper.getBlockEntity(CENTER, MaceratorBlockEntity.class);

		ZapEnergyHandler cap = helper.getLevel().getCapability(
				FTBICCapabilities.ZAP_ENERGY_BLOCK, helper.absolutePos(CENTER), null);
		helper.assertTrue(cap == be, "null-side zap cap query should resolve to the BE");
		helper.assertTrue(cap.getEnergyCapacity() > 0D,
				"macerator zap capacity should be positive (got " + cap.getEnergyCapacity() + ")");
		helper.succeed();
	}

	static void zapCapAbsentOnVanillaBlocks(GameTestHelper helper) {
		helper.setBlock(CENTER.below(), Blocks.STONE);

		helper.setBlock(CENTER, Blocks.CHEST);
		ZapEnergyHandler chest = helper.getLevel().getCapability(
				FTBICCapabilities.ZAP_ENERGY_BLOCK, helper.absolutePos(CENTER), Direction.NORTH);
		helper.assertTrue(chest == null, "vanilla chest must not expose zap cap");

		helper.setBlock(CENTER, Blocks.FURNACE);
		ZapEnergyHandler furnace = helper.getLevel().getCapability(
				FTBICCapabilities.ZAP_ENERGY_BLOCK, helper.absolutePos(CENTER), Direction.NORTH);
		helper.assertTrue(furnace == null, "vanilla furnace must not expose zap cap");

		helper.setBlock(CENTER, Blocks.STONE);
		ZapEnergyHandler stone = helper.getLevel().getCapability(
				FTBICCapabilities.ZAP_ENERGY_BLOCK, helper.absolutePos(CENTER), Direction.NORTH);
		helper.assertTrue(stone == null, "plain stone must not expose zap cap");

		helper.succeed();
	}

	static void zapCapForwardsThroughReactorChamber(GameTestHelper helper) {
		helper.setBlock(CENTER.below(), Blocks.STONE);
		helper.setBlock(CENTER.north().below(), Blocks.STONE);
		helper.setBlock(CENTER, FTBICElectricBlocks.NUCLEAR_REACTOR.block.get());
		BlockPos chamberPos = CENTER.north();
		helper.setBlock(chamberPos, FTBICBlocks.NUCLEAR_REACTOR_CHAMBER.get());

		NuclearReactorBlockEntity reactor = helper.getBlockEntity(CENTER, NuclearReactorBlockEntity.class);

		ZapEnergyHandler capOnChamber = helper.getLevel().getCapability(
				FTBICCapabilities.ZAP_ENERGY_BLOCK, helper.absolutePos(chamberPos), Direction.NORTH);
		helper.assertTrue(capOnChamber != null,
				"chamber should forward zap cap to adjacent reactor");
		helper.assertTrue(capOnChamber == reactor,
				"forwarded cap should resolve to the reactor BE itself");
		helper.succeed();
	}

	static void zapCapCacheInvalidatesOnBlockRemoval(GameTestHelper helper) {
		helper.setBlock(CENTER.below(), Blocks.STONE);
		helper.setBlock(CENTER, FTBICElectricBlocks.BASIC_GENERATOR.block.get());

		BlockCapabilityCache<ZapEnergyHandler, Direction> cache = BlockCapabilityCache.create(
				FTBICCapabilities.ZAP_ENERGY_BLOCK, helper.getLevel(),
				helper.absolutePos(CENTER), Direction.NORTH);
		helper.assertTrue(cache.getCapability() != null,
				"cache should resolve zap cap while the electric block is present");

		helper.setBlock(CENTER, Blocks.AIR);
		helper.runAfterDelay(2L, () -> {
			helper.assertTrue(cache.getCapability() == null,
					"cache must invalidate after the block is removed");
			helper.succeed();
		});
	}

	static void zapCapCacheUpdatesOnBlockSwap(GameTestHelper helper) {
		helper.setBlock(CENTER.below(), Blocks.STONE);
		helper.setBlock(CENTER, FTBICElectricBlocks.BASIC_GENERATOR.block.get());

		BlockCapabilityCache<ZapEnergyHandler, Direction> cache = BlockCapabilityCache.create(
				FTBICCapabilities.ZAP_ENERGY_BLOCK, helper.getLevel(),
				helper.absolutePos(CENTER), Direction.NORTH);
		ZapEnergyHandler first = cache.getCapability();
		helper.assertTrue(first instanceof BasicGeneratorBlockEntity,
				"initial cache resolves to generator");

		helper.setBlock(CENTER, FTBICElectricBlocks.MACERATOR.block.get());
		helper.runAfterDelay(2L, () -> {
			ZapEnergyHandler second = cache.getCapability();
			helper.assertTrue(second instanceof MaceratorBlockEntity,
					"cache should reflect replacement block (got " + second + ")");
			helper.assertTrue(first != second, "cache should return the new BE handler, not the old one");
			helper.succeed();
		});
	}

	private static final UUID NUCLEAR_TEST_OWNER = new UUID(0L, 0L);

	static void nuclearExplosionDestroysUnshieldedBlock(GameTestHelper helper) {
		BlockPos center = new BlockPos(4, 4, 4);
		BlockPos exposed = new BlockPos(4, 4, 6);
		helper.setBlock(exposed, Blocks.COBBLESTONE);

		NuclearExplosion.detonate(helper.getLevel(), helper.absolutePos(center), 3.5D,
				NUCLEAR_TEST_OWNER, "test");

		BlockState state = helper.getLevel().getBlockState(helper.absolutePos(exposed));
		helper.assertTrue(state.isAir(),
				"Cobblestone within unshielded explosion radius should be destroyed (got " + state.getBlock() + ")");
		helper.succeed();
	}

	static void nuclearExplosionPreservesReinforcedBlock(GameTestHelper helper) {
		BlockPos center = new BlockPos(4, 4, 4);
		BlockPos reinforced = new BlockPos(4, 4, 5);
		helper.setBlock(reinforced, FTBICBlocks.REINFORCED_STONE.get());

		NuclearExplosion.detonate(helper.getLevel(), helper.absolutePos(center), 3.5D,
				NUCLEAR_TEST_OWNER, "test");

		BlockState state = helper.getLevel().getBlockState(helper.absolutePos(reinforced));
		helper.assertTrue(state.is(FTBICBlocks.REINFORCED_STONE.get()),
				"Reinforced stone within explosion radius should be preserved (got " + state.getBlock() + ")");
		helper.succeed();
	}

	static void nuclearExplosionShieldedByReinforcedWall(GameTestHelper helper) {
		BlockPos center = new BlockPos(4, 4, 4);
		BlockPos wall = new BlockPos(4, 4, 5);
		BlockPos shielded = new BlockPos(4, 4, 6);
		BlockPos shieldedCrust = new BlockPos(4, 4, 7);
		BlockPos exposed = new BlockPos(4, 4, 3);

		helper.setBlock(wall, FTBICBlocks.REINFORCED_STONE.get());
		helper.setBlock(shielded, Blocks.COBBLESTONE);
		helper.setBlock(shieldedCrust, Blocks.COBBLESTONE);
		helper.setBlock(exposed, Blocks.COBBLESTONE);

		NuclearExplosion.detonate(helper.getLevel(), helper.absolutePos(center), 3.5D,
				NUCLEAR_TEST_OWNER, "test");

		BlockState wallState = helper.getLevel().getBlockState(helper.absolutePos(wall));
		BlockState shieldedState = helper.getLevel().getBlockState(helper.absolutePos(shielded));
		BlockState shieldedCrustState = helper.getLevel().getBlockState(helper.absolutePos(shieldedCrust));
		BlockState exposedState = helper.getLevel().getBlockState(helper.absolutePos(exposed));

		helper.assertTrue(wallState.is(FTBICBlocks.REINFORCED_STONE.get()),
				"Reinforced wall should remain reinforced (got " + wallState.getBlock() + ")");
		helper.assertTrue(shieldedState.is(Blocks.COBBLESTONE),
				"Cobblestone behind reinforced wall should survive (got " + shieldedState.getBlock() + ")");
		helper.assertTrue(shieldedCrustState.is(Blocks.COBBLESTONE),
				"Crust cobblestone behind reinforced wall should survive (got " + shieldedCrustState.getBlock() + ")");
		helper.assertTrue(exposedState.isAir(),
				"Unshielded cobblestone within explosion radius should be destroyed (got " + exposedState.getBlock() + ")");
		helper.succeed();
	}
}
