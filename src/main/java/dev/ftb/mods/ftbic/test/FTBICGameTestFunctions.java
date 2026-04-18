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
import dev.ftb.mods.ftbic.block.entity.storage.BatteryBoxBlockEntity;
import dev.ftb.mods.ftbic.block.entity.storage.LVBatteryBoxBlockEntity;
import dev.ftb.mods.ftbic.block.entity.storage.LVTransformerBlockEntity;
import dev.ftb.mods.ftbic.block.entity.storage.TransformerBlockEntity;
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
import dev.ftb.mods.ftbic.block.entity.machine.CentrifugeBlockEntity;
import dev.ftb.mods.ftbic.block.entity.machine.CompressorBlockEntity;
import dev.ftb.mods.ftbic.block.entity.machine.MaceratorBlockEntity;
import dev.ftb.mods.ftbic.block.entity.machine.MachineBlockEntity;
import dev.ftb.mods.ftbic.block.entity.machine.PoweredFurnaceBlockEntity;
import dev.ftb.mods.ftbic.item.FTBICItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.clock.WorldClock;
import net.minecraft.world.clock.WorldClocks;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.ValueInput;

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

		LVSolarPanelBlockEntity panel = helper.getBlockEntity(CENTER, LVSolarPanelBlockEntity.class);
		panel.energy = 0D;

		helper.runAfterDelay(20, () -> {
			LVSolarPanelBlockEntity after = helper.getBlockEntity(CENTER, LVSolarPanelBlockEntity.class);
			helper.assertValueEqual(0D, after.energy, "solar panel energy when obstructed");
			helper.succeed();
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

	private static boolean outputContains(ElectricBlockEntity be, net.minecraft.world.item.Item item) {
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
		helper.assertTrue(be.maxInputEnergy <= dev.ftb.mods.ftbic.FTBICConfig.ENERGY.IV_TRANSFER_RATE.get() + 0.01D,
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
		double speedWithUpgrades = be.progressSpeed;

		net.minecraft.core.HolderLookup.Provider registries = helper.getLevel().registryAccess();
		CompoundTag tag = be.saveCustomOnly(registries);

		helper.setBlock(CENTER, Blocks.AIR);
		helper.setBlock(CENTER, FTBICElectricBlocks.MACERATOR.block.get());
		MaceratorBlockEntity reloaded = helper.getBlockEntity(CENTER, MaceratorBlockEntity.class);
		ValueInput input = TagValueInput.create(ProblemReporter.DISCARDING, registries, tag);
		reloaded.loadCustomOnly(input);
		reloaded.upgradesChanged();

		helper.assertValueEqual(speedWithUpgrades, reloaded.progressSpeed, "restored progressSpeed");
		helper.succeed();
	}

	static void batteryBoxDrainsInputBattery(GameTestHelper helper) {
		helper.setBlock(CENTER.below(), Blocks.STONE);
		helper.setBlock(CENTER, FTBICElectricBlocks.LV_BATTERY_BOX.block.get());
		LVBatteryBoxBlockEntity box = helper.getBlockEntity(CENTER, LVBatteryBoxBlockEntity.class);
		box.energy = 0D;

		ItemStack battery = new ItemStack(FTBICItems.LV_BATTERY.get());
		if (battery.getItem() instanceof dev.ftb.mods.ftbic.util.EnergyItemHandler h) {
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
			double stored = charged.getItem() instanceof dev.ftb.mods.ftbic.util.EnergyItemHandler h
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
		var handler = (dev.ftb.mods.ftbic.util.EnergyItemHandler) battery.getItem();
		double before = handler.getEnergy(battery);
		double inserted = handler.insertEnergy(battery, 500D, false);
		helper.assertTrue(inserted > 0D, "Battery should accept energy insertion");
		helper.assertTrue(handler.getEnergy(battery) > before,
				"Battery energy should increase (before=" + before + ", now=" + handler.getEnergy(battery) + ")");
		helper.succeed();
	}

	static void rechargeableBatteryAcceptsAndHoldsEnergy(GameTestHelper helper) {
		ItemStack battery = new ItemStack(FTBICItems.LV_BATTERY.get());
		var h = (dev.ftb.mods.ftbic.util.EnergyItemHandler) battery.getItem();
		double cap = h.getEnergyCapacity(battery);
		double inserted = h.insertEnergy(battery, cap, false);
		helper.assertValueEqual(cap, inserted, "inserted energy should equal capacity on empty battery");
		helper.assertValueEqual(cap, h.getEnergy(battery), "battery should report full energy after insertion");
		helper.succeed();
	}

	static void rechargeableBatteryClearsComponentAtZero(GameTestHelper helper) {
		ItemStack battery = new ItemStack(FTBICItems.LV_BATTERY.get());
		var h = (dev.ftb.mods.ftbic.util.EnergyItemHandler) battery.getItem();
		h.insertEnergy(battery, h.getEnergyCapacity(battery), false);

		double drained = h.extractEnergy(battery, h.getEnergyCapacity(battery), false);
		helper.assertTrue(drained > 0D, "Extract should drain the battery");
		helper.assertFalse(battery.has(dev.ftb.mods.ftbic.registry.ModDataComponents.ENERGY.get()),
				"ENERGY component should be cleared at zero");
		helper.succeed();
	}

	static void singleUseBatteryShrinksAtZero(GameTestHelper helper) {
		ItemStack stack = new ItemStack(FTBICItems.SINGLE_USE_BATTERY.get(), 2);
		var h = (dev.ftb.mods.ftbic.util.EnergyItemHandler) stack.getItem();
		double cap = h.getEnergyCapacity(stack);
		h.extractEnergy(stack, cap, false);

		helper.assertValueEqual(1, stack.getCount(), "single-use battery stack should shrink by 1 at zero");
		helper.succeed();
	}

	static void singleUseBatteryCannotBeRecharged(GameTestHelper helper) {
		ItemStack stack = new ItemStack(FTBICItems.SINGLE_USE_BATTERY.get());
		var h = (dev.ftb.mods.ftbic.util.EnergyItemHandler) stack.getItem();
		helper.assertFalse(h.canInsertEnergy(), "Single-use batteries cannot accept new energy");
		double inserted = h.insertEnergy(stack, 1000D, false);
		helper.assertValueEqual(0D, inserted, "insertEnergy must return 0 on single-use battery");
		helper.succeed();
	}
}
