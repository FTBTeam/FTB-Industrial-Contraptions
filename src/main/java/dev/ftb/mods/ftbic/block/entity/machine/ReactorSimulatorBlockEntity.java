package dev.ftb.mods.ftbic.block.entity.machine;

import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.item.reactor.FuelRodItem;
import dev.ftb.mods.ftbic.item.reactor.NuclearReactor;
import dev.ftb.mods.ftbic.item.reactor.ReactorItem;
import dev.ftb.mods.ftbic.screen.ReactorSimulatorMenu;
import dev.ftb.mods.ftbic.util.ReactorDesign;
import net.minecraft.util.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.Arrays;

public class ReactorSimulatorBlockEntity extends ElectricBlockEntityRef {
	public static final byte VERDICT_UNKNOWN = 0;
	public static final byte VERDICT_STABLE = 1;
	public static final byte VERDICT_UNSTABLE = 2;

	public static final int[] SPEED_CYCLES_PER_TICK = {1, 5, 25, 50};
	public static final int[] SPEED_LABELS = {20, 100, 500, 1000};

	public final NuclearReactor simReactor;

	public boolean running;
	public boolean paused;
	public int speedIndex = 0;
	public int chambers = 0;
	public int waterThousandths = 0;

	public double totalEnergy;
	public long elapsedCycles;
	public int peakHeat;
	public double lastEnergyOutput;

	public byte verdict = VERDICT_UNKNOWN;
	public long unstableCycle = -1L;

	private volatile boolean analyzeInProgress = false;

	public ReactorSimulatorBlockEntity(BlockPos pos, BlockState state) {
		super(FTBICElectricBlocks.REACTOR_SIMULATOR, pos, state);
		simReactor = new NuclearReactor(inputItems);
		simReactor.activeColumns = 3;
		simReactor.simulation = true;
	}

	@Override
	public AbstractContainerMenu createMenu(int id, Inventory inv) {
		return new ReactorSimulatorMenu(id, inv, this);
	}

	public boolean isLocked() {
		return running && !paused;
	}

	public int getActiveColumns() {
		return Math.max(3, Math.min(NuclearReactor.MAX_COLUMNS, 3 + chambers));
	}

	public double getWaterFactor() {
		return Math.max(0D, Math.min(1D, waterThousandths / 1000D));
	}

	public double getEnvCoolingMultiplier() {
		double max = FTBICConfig.NUCLEAR.WATER_COOLING_MULTIPLIER.get();
		if (max <= 1D) return 1D;
		return 1D + getWaterFactor() * (max - 1D);
	}

	public void setChambers(int value) {
		if (isLocked()) return;
		int clamped = Math.max(0, Math.min(6, value));
		if (clamped == chambers) return;
		if (clamped < chambers) ejectFromColumns(3 + clamped, 3 + chambers);
		chambers = clamped;
		simReactor.activeColumns = getActiveColumns();
		verdict = VERDICT_UNKNOWN;
		setChanged();
	}

	public void setWaterThousandths(int value) {
		if (isLocked()) return;
		int clamped = Math.max(0, Math.min(1000, value));
		if (clamped == waterThousandths) return;
		waterThousandths = clamped;
		verdict = VERDICT_UNKNOWN;
		setChanged();
	}

	public void setSpeedIndex(int value) {
		int clamped = Math.max(0, Math.min(SPEED_CYCLES_PER_TICK.length - 1, value));
		if (clamped == speedIndex) return;
		speedIndex = clamped;
		setChanged();
	}

	public void start() {
		if (running && !paused) return;
		if (running && paused) {
			paused = false;
			setChanged();
			return;
		}
		if (!hasAnyFuelRod()) return;
		running = true;
		paused = false;
		simReactor.activeColumns = getActiveColumns();
		simReactor.envCoolingMultiplier = getEnvCoolingMultiplier();
		simReactor.simulation = true;
		simReactor.paused = false;
		simReactor.heat = 0;
		totalEnergy = 0D;
		elapsedCycles = 0L;
		peakHeat = 0;
		lastEnergyOutput = 0D;
		setChanged();
	}

	private boolean hasAnyFuelRod() {
		for (ItemStack s : inputItems) {
			if (!s.isEmpty() && s.getItem() instanceof FuelRodItem) return true;
		}
		return false;
	}

	public void pause() {
		if (!running) return;
		paused = true;
		setChanged();
	}

	public void reset() {
		running = false;
		paused = false;
		Arrays.fill(inputItems, ItemStack.EMPTY);
		totalEnergy = 0D;
		elapsedCycles = 0L;
		peakHeat = 0;
		lastEnergyOutput = 0D;
		simReactor.heat = 0;
		simReactor.energyOutput = 0D;
		verdict = VERDICT_UNKNOWN;
		unstableCycle = -1L;
		setChanged();
	}

	public void restart() {
		running = true;
		paused = false;
		simReactor.heat = 0;
		simReactor.energyOutput = 0D;
		simReactor.paused = false;
		simReactor.simulation = true;
		simReactor.activeColumns = getActiveColumns();
		simReactor.envCoolingMultiplier = getEnvCoolingMultiplier();
		totalEnergy = 0D;
		elapsedCycles = 0L;
		peakHeat = 0;
		lastEnergyOutput = 0D;
		for (int i = 0; i < inputItems.length; i++) {
			ItemStack s = inputItems[i];
			if (!s.isEmpty() && s.isDamageableItem()) {
				ItemStack fresh = s.copy();
				fresh.setDamageValue(0);
				inputItems[i] = fresh;
			}
		}
		setChanged();
	}

	public boolean setSlotItem(int slot, ItemStack stack) {
		if (isLocked()) return false;
		if (slot < 0 || slot >= inputItems.length) return false;
		int col = slot % NuclearReactor.MAX_COLUMNS;
		int row = slot / NuclearReactor.MAX_COLUMNS;
		if (row >= NuclearReactor.ROWS || col >= getActiveColumns()) return false;
		if (!stack.isEmpty() && !(stack.getItem() instanceof ReactorItem)) return false;
		inputItems[slot] = stack.isEmpty() ? ItemStack.EMPTY : stack.copyWithCount(1);
		verdict = VERDICT_UNKNOWN;
		setChanged();
		return true;
	}

	public void clearSlot(int slot) {
		setSlotItem(slot, ItemStack.EMPTY);
	}

	public void runStabilityCheck() {
		if (analyzeInProgress) return;
		int longestLife = 0;
		for (ItemStack s : inputItems) {
			if (!s.isEmpty() && s.getItem() instanceof FuelRodItem) {
				longestLife = Math.max(longestLife, s.getMaxDamage());
			}
		}
		if (longestLife <= 0) {
			verdict = VERDICT_STABLE;
			unstableCycle = -1L;
			setChanged();
			return;
		}

		ItemStack[] clone = new ItemStack[inputItems.length];
		for (int i = 0; i < inputItems.length; i++) clone[i] = inputItems[i].copy();
		int activeCols = getActiveColumns();
		double envCool = getEnvCoolingMultiplier();
		long cap = Math.min(longestLife, 20_000L);

		analyzeInProgress = true;
		Util.backgroundExecutor().execute(() -> {
			NuclearReactor copy = new NuclearReactor(clone);
			copy.activeColumns = activeCols;
			copy.envCoolingMultiplier = envCool;
			copy.simulation = true;
			copy.paused = false;
			long failedAt = -1L;
			for (long cycle = 0; cycle < cap; cycle++) {
				boolean stop = copy.tick();
				if (copy.heat >= copy.maxHeat) {
					failedAt = cycle;
					break;
				}
				if (stop) break;
			}
			final long result = failedAt;
			if (level instanceof ServerLevel sl) {
				sl.getServer().execute(() -> {
					analyzeInProgress = false;
					if (result >= 0L) {
						verdict = VERDICT_UNSTABLE;
						unstableCycle = result;
					} else {
						verdict = VERDICT_STABLE;
						unstableCycle = -1L;
					}
					setChanged();
				});
			} else {
				analyzeInProgress = false;
			}
		});
	}

	public ReactorDesign exportDesign() {
		return ReactorDesign.fromReactor(chambers, getWaterFactor(), inputItems);
	}

	public boolean applyDesign(ReactorDesign design) {
		if (isLocked()) return false;
		reset();
		chambers = Math.max(0, Math.min(6, design.chambers()));
		waterThousandths = Math.max(0, Math.min(1000, (int) Math.round(design.water() * 1000D)));
		simReactor.activeColumns = getActiveColumns();
		design.applyToInventory(inputItems, getActiveColumns());
		setChanged();
		return true;
	}

	private void ejectFromColumns(int fromCol, int toColExclusive) {
		for (int y = 0; y < NuclearReactor.ROWS; y++) {
			for (int x = fromCol; x < toColExclusive; x++) {
				inputItems[NuclearReactor.slotIndex(x, y)] = ItemStack.EMPTY;
			}
		}
	}

	@Override
	public void tick() {
		super.tick();
		if (level == null || level.isClientSide()) return;
		if (!running || paused) return;

		simReactor.activeColumns = getActiveColumns();
		simReactor.envCoolingMultiplier = getEnvCoolingMultiplier();
		simReactor.simulation = true;
		simReactor.paused = false;

		int cyclesThisTick = SPEED_CYCLES_PER_TICK[speedIndex];
		for (int i = 0; i < cyclesThisTick; i++) {
			boolean stop = simReactor.tick();
			elapsedCycles++;
			if (simReactor.energyOutput > 0D) {
				totalEnergy += simReactor.energyOutput;
			}
			if (simReactor.heat > peakHeat) peakHeat = simReactor.heat;
			lastEnergyOutput = simReactor.energyOutput;
			if (stop || simReactor.heat >= simReactor.maxHeat) {
				running = false;
				break;
			}
		}
		setChanged();
	}

	@Override
	public boolean isItemValid(int slot, ItemStack stack) {
		if (isLocked()) return false;
		if (slot < 0 || slot >= inputItems.length) return false;
		int col = slot % NuclearReactor.MAX_COLUMNS;
		int row = slot / NuclearReactor.MAX_COLUMNS;
		if (row >= NuclearReactor.ROWS || col >= getActiveColumns()) return false;
		return stack.isEmpty() || stack.getItem() instanceof ReactorItem;
	}

	@Override
	public boolean isSlotExtractable(int slot) {
		return !isLocked();
	}

	@Override
	protected void saveAdditional(ValueOutput output) {
		super.saveAdditional(output);
		if (running) output.putBoolean("Running", true);
		if (paused) output.putBoolean("Paused", true);
		if (speedIndex != 0) output.putInt("Speed", speedIndex);
		if (chambers != 0) output.putInt("Chambers", chambers);
		if (waterThousandths != 0) output.putInt("WaterT", waterThousandths);
		if (totalEnergy != 0D) output.putDouble("TotalEnergy", totalEnergy);
		if (elapsedCycles != 0L) output.putLong("ElapsedCycles", elapsedCycles);
		if (peakHeat != 0) output.putInt("PeakHeat", peakHeat);
		if (lastEnergyOutput != 0D) output.putDouble("LastEnergy", lastEnergyOutput);
		if (verdict != VERDICT_UNKNOWN) output.putByte("Verdict", verdict);
		if (unstableCycle >= 0L) output.putLong("UnstableCycle", unstableCycle);
		if (simReactor.heat != 0) output.putInt("SimHeat", simReactor.heat);
		if (simReactor.energyOutput != 0D) output.putDouble("SimOutput", simReactor.energyOutput);
	}

	@Override
	protected void loadAdditional(ValueInput input) {
		super.loadAdditional(input);
		running = input.getBooleanOr("Running", false);
		paused = input.getBooleanOr("Paused", false);
		speedIndex = Math.max(0, Math.min(SPEED_CYCLES_PER_TICK.length - 1, input.getIntOr("Speed", 0)));
		chambers = Math.max(0, Math.min(6, input.getIntOr("Chambers", 0)));
		waterThousandths = Math.max(0, Math.min(1000, input.getIntOr("WaterT", 0)));
		totalEnergy = input.getDoubleOr("TotalEnergy", 0D);
		elapsedCycles = input.getLongOr("ElapsedCycles", 0L);
		peakHeat = input.getIntOr("PeakHeat", 0);
		lastEnergyOutput = input.getDoubleOr("LastEnergy", 0D);
		verdict = input.getByteOr("Verdict", VERDICT_UNKNOWN);
		unstableCycle = input.getLongOr("UnstableCycle", -1L);
		simReactor.heat = input.getIntOr("SimHeat", 0);
		simReactor.energyOutput = input.getDoubleOr("SimOutput", 0D);
		simReactor.activeColumns = getActiveColumns();
		simReactor.simulation = true;
	}
}
