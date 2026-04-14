package dev.ftb.mods.ftbic.screen;

import dev.ftb.mods.ftbic.block.entity.ElectricBlockEntity;
import dev.ftb.mods.ftbic.block.entity.generator.NuclearReactorBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;

/**
 * 9×6 reactor chamber grid. The reactor BE has 54 input slots; each one is a cell in the chamber.
 * Synced data: paused, allowRedstoneControl, heat (0-1000 scaled), maxHeat (0-1000 fixed),
 * energyOutput (rounded short), runFlag (1 if currently producing energy).
 */
public class NuclearReactorMenu extends ElectricBlockMenu {
	public final DataSlot pausedSlot = DataSlot.standalone();
	public final DataSlot allowRedstoneSlot = DataSlot.standalone();
	public final DataSlot heatScaled = DataSlot.standalone();
	public final DataSlot maxHeatScaled = DataSlot.standalone();
	public final DataSlot energyOutShort = DataSlot.standalone();
	public final DataSlot runningFlag = DataSlot.standalone();

	public NuclearReactorMenu(int id, Inventory playerInv, FriendlyByteBuf buf) {
		super(FTBICMenus.NUCLEAR_REACTOR.get(), id, playerInv, buf);
		registerSlots();
	}

	public NuclearReactorMenu(int id, Inventory playerInv, ElectricBlockEntity be) {
		super(FTBICMenus.NUCLEAR_REACTOR.get(), id, playerInv, be);
		registerSlots();
	}

	private void registerSlots() {
		addDataSlot(pausedSlot);
		addDataSlot(allowRedstoneSlot);
		addDataSlot(heatScaled);
		addDataSlot(maxHeatScaled);
		addDataSlot(energyOutShort);
		addDataSlot(runningFlag);
	}

	@Override
	protected int getPlayerSlotOffset() {
		return 140;
	}

	@Override
	protected void addMachineSlots(Inventory playerInv) {
		if (blockEntity == null || blockEntity.getSlotCount() == 0) {
			machineSlotCount = 0;
			return;
		}
		ElectricBlockEntityContainer container = new ElectricBlockEntityContainer(blockEntity);

		int inputs = blockEntity.inputItems.length;
		for (int i = 0; i < inputs; i++) {
			int row = i / 9, col = i % 9;
			addSlot(new Slot(container, i, 8 + col * 18, 18 + row * 18));
		}

		machineSlotCount = inputs;
	}

	@Override
	public void broadcastChanges() {
		super.broadcastChanges();
		if (blockEntity instanceof NuclearReactorBlockEntity reactor) {
			pausedSlot.set(reactor.reactor.paused ? 1 : 0);
			allowRedstoneSlot.set(reactor.reactor.allowRedstoneControl ? 1 : 0);
			int max = Math.max(1, reactor.reactor.maxHeat);
			heatScaled.set((int) Math.min(1000L, Math.round(1000D * reactor.reactor.heat / max)));
			maxHeatScaled.set(Math.min(Short.MAX_VALUE, max));
			energyOutShort.set(Math.min(Short.MAX_VALUE, (int) Math.round(reactor.reactor.energyOutput)));
			runningFlag.set(reactor.reactor.energyOutput > 0D ? 1 : 0);
		}
	}

	public boolean isPaused()           { return pausedSlot.get() == 1; }
	public boolean allowRedstone()      { return allowRedstoneSlot.get() == 1; }
	public boolean isRunning()          { return runningFlag.get() == 1; }
	public float getHeatFraction()      { return heatScaled.get() / 1000F; }
	public int getEnergyOutput()        { return energyOutShort.get(); }

	@Override
	public boolean clickMenuButton(Player player, int id) {
		if (!(blockEntity instanceof NuclearReactorBlockEntity reactor)) return false;
		switch (id) {
			case 0 -> {
				reactor.reactor.paused = !reactor.reactor.paused;
				reactor.setChanged();
				return true;
			}
			case 1 -> {
				reactor.reactor.allowRedstoneControl = !reactor.reactor.allowRedstoneControl;
				reactor.setChanged();
				return true;
			}
		}
		return super.clickMenuButton(player, id);
	}
}
