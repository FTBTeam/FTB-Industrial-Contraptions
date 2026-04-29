package dev.ftb.mods.ftbic.screen;

import dev.ftb.mods.ftbic.block.entity.ElectricBlockEntity;
import dev.ftb.mods.ftbic.block.entity.machine.ReactorSimulatorBlockEntity;
import dev.ftb.mods.ftbic.item.reactor.NuclearReactor;
import dev.ftb.mods.ftbic.item.reactor.ReactorItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ReactorSimulatorMenu extends ElectricBlockMenu {
	public final DataSlot runningSlot = DataSlot.standalone();
	public final DataSlot pausedSlot = DataSlot.standalone();
	public final DataSlot speedSlot = DataSlot.standalone();
	public final DataSlot chambersSlot = DataSlot.standalone();
	public final DataSlot waterSlot = DataSlot.standalone();
	public final DataSlot peakHeatSlot = DataSlot.standalone();
	public final DataSlot lastEnergySlot = DataSlot.standalone();
	public final DataSlot totalEnergy0 = DataSlot.standalone();
	public final DataSlot totalEnergy1 = DataSlot.standalone();
	public final DataSlot totalEnergy2 = DataSlot.standalone();
	public final DataSlot totalEnergy3 = DataSlot.standalone();
	public final DataSlot elapsed0 = DataSlot.standalone();
	public final DataSlot elapsed1 = DataSlot.standalone();
	public final DataSlot elapsed2 = DataSlot.standalone();
	public final DataSlot elapsed3 = DataSlot.standalone();
	public final DataSlot verdictSlot = DataSlot.standalone();
	public final DataSlot unstable0 = DataSlot.standalone();
	public final DataSlot unstable1 = DataSlot.standalone();
	public final DataSlot unstable2 = DataSlot.standalone();
	public final DataSlot unstable3 = DataSlot.standalone();
	public final DataSlot maxHeatSlot = DataSlot.standalone();
	public final DataSlot heatScaledSlot = DataSlot.standalone();

	public ReactorSimulatorMenu(int id, Inventory inv, FriendlyByteBuf buf) {
		super(FTBICMenus.REACTOR_SIMULATOR.get(), id, inv, buf);
		registerSlots();
	}

	public ReactorSimulatorMenu(int id, Inventory inv, ElectricBlockEntity be) {
		super(FTBICMenus.REACTOR_SIMULATOR.get(), id, inv, be);
		registerSlots();
	}

	private void registerSlots() {
		addDataSlot(runningSlot);
		addDataSlot(pausedSlot);
		addDataSlot(speedSlot);
		addDataSlot(chambersSlot);
		addDataSlot(waterSlot);
		addDataSlot(peakHeatSlot);
		addDataSlot(lastEnergySlot);
		addDataSlot(totalEnergy0);
		addDataSlot(totalEnergy1);
		addDataSlot(totalEnergy2);
		addDataSlot(totalEnergy3);
		addDataSlot(elapsed0);
		addDataSlot(elapsed1);
		addDataSlot(elapsed2);
		addDataSlot(elapsed3);
		addDataSlot(verdictSlot);
		addDataSlot(unstable0);
		addDataSlot(unstable1);
		addDataSlot(unstable2);
		addDataSlot(unstable3);
		addDataSlot(maxHeatSlot);
		addDataSlot(heatScaledSlot);
	}

	@Override
	protected void addPlayerInventorySlots(Inventory inv) {
		// Simulator is fed via JEI ghost-drag; player inventory is intentionally hidden.
	}

	public static int slotScreenY(int row) {
		return 18 + row * 18;
	}

	@Override
	protected void addMachineSlots(Inventory playerInv) {
		if (blockEntity == null || blockEntity.getSlotCount() == 0) {
			machineSlotCount = 0;
			return;
		}
		ElectricBlockEntityContainer container = new ElectricBlockEntityContainer(blockEntity);
		ReactorSimulatorBlockEntity sim = blockEntity instanceof ReactorSimulatorBlockEntity s ? s : null;
		if (sim == null) {
			machineSlotCount = 0;
			return;
		}

		for (int row = 0; row < NuclearReactor.ROWS; row++) {
			for (int col = 0; col < NuclearReactor.MAX_COLUMNS; col++) {
				int idx = NuclearReactor.slotIndex(col, row);
				addSlot(new ReactorSimulatorSlot(container, sim,
						() -> Math.max(3, Math.min(NuclearReactor.MAX_COLUMNS, 3 + chambersSlot.get())),
						idx, 8 + col * 18, slotScreenY(row)));
			}
		}
		machineSlotCount = NuclearReactor.MAX_SLOTS;
	}

	private ReactorSimulatorBlockEntity sim() {
		return blockEntity instanceof ReactorSimulatorBlockEntity s ? s : null;
	}

	@Override
	public void broadcastChanges() {
		ReactorSimulatorBlockEntity s = sim();
		if (s != null) {
			runningSlot.set(s.running ? 1 : 0);
			pausedSlot.set(s.paused ? 1 : 0);
			speedSlot.set(s.speedIndex);
			chambersSlot.set(s.chambers);
			waterSlot.set(s.waterThousandths);
			peakHeatSlot.set(Math.min(Short.MAX_VALUE, s.peakHeat));
			lastEnergySlot.set((int) Math.min(Short.MAX_VALUE, Math.round(s.lastEnergyOutput)));
			long te = Math.max(0L, Math.round(s.totalEnergy));
			packLong(te, totalEnergy0, totalEnergy1, totalEnergy2, totalEnergy3);
			packLong(Math.max(0L, s.elapsedCycles), elapsed0, elapsed1, elapsed2, elapsed3);
			verdictSlot.set(s.verdict);
			long uc = s.unstableCycle + 1L;
			packLong(Math.max(0L, uc), unstable0, unstable1, unstable2, unstable3);
			maxHeatSlot.set(Math.min(Short.MAX_VALUE, s.simReactor.maxHeat));
			int max = Math.max(1, s.simReactor.maxHeat);
			heatScaledSlot.set((int) Math.min(1000L, Math.round(1000D * s.simReactor.heat / max)));
		}
		super.broadcastChanges();
	}

	private static void packLong(long v, DataSlot a, DataSlot b, DataSlot c, DataSlot d) {
		a.set((int) (v & 0xFFFF));
		b.set((int) ((v >>> 16) & 0xFFFF));
		c.set((int) ((v >>> 32) & 0xFFFF));
		d.set((int) ((v >>> 48) & 0xFFFF));
	}

	private static long unpackLong(DataSlot a, DataSlot b, DataSlot c, DataSlot d) {
		return (a.get() & 0xFFFFL)
				| ((b.get() & 0xFFFFL) << 16)
				| ((c.get() & 0xFFFFL) << 32)
				| ((d.get() & 0xFFFFL) << 48);
	}

	public boolean isRunning() { return runningSlot.get() == 1; }
	public boolean isPaused() { return pausedSlot.get() == 1; }
	public int getSpeedIndex() { return speedSlot.get(); }
	public int getChambers() { return chambersSlot.get(); }
	public int getWaterThousandths() { return waterSlot.get(); }
	public int getPeakHeat() { return peakHeatSlot.get(); }
	public int getLastEnergy() { return lastEnergySlot.get(); }
	public long getTotalEnergy() { return unpackLong(totalEnergy0, totalEnergy1, totalEnergy2, totalEnergy3); }
	public long getElapsedCycles() { return unpackLong(elapsed0, elapsed1, elapsed2, elapsed3); }
	public byte getVerdict() { return (byte) verdictSlot.get(); }
	public long getUnstableCycle() { return unpackLong(unstable0, unstable1, unstable2, unstable3) - 1L; }
	public int getMaxHeat() { return maxHeatSlot.get(); }
	public float getHeatFraction() { return heatScaledSlot.get() / 1000F; }
	public boolean isEditLocked() { return isRunning() && !isPaused(); }

	@Override
	public ItemStack quickMoveStack(Player player, int index) {
		Slot slot = slots.get(index);
		if (!slot.hasItem()) return ItemStack.EMPTY;
		ReactorSimulatorBlockEntity s = sim();
		if (s == null || s.isLocked()) return ItemStack.EMPTY;

		if (slot instanceof ReactorSimulatorSlot sim) {
			s.clearSlot(sim.getContainerSlot());
			return ItemStack.EMPTY;
		}

		ItemStack stack = slot.getItem();
		if (stack.isEmpty() || !(stack.getItem() instanceof ReactorItem)) return ItemStack.EMPTY;
		for (int i = 0; i < machineSlotCount; i++) {
			Slot target = slots.get(i);
			if (target instanceof ReactorSimulatorSlot sim && !sim.hasItem() && sim.isActive()) {
				s.setSlotItem(sim.getContainerSlot(), stack);
				break;
			}
		}
		return ItemStack.EMPTY;
	}

}
