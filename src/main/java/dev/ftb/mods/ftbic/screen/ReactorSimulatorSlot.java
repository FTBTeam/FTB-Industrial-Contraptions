package dev.ftb.mods.ftbic.screen;

import dev.ftb.mods.ftbic.block.entity.machine.ReactorSimulatorBlockEntity;
import dev.ftb.mods.ftbic.item.reactor.NuclearReactor;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ReactorSimulatorSlot extends Slot {
	public final ReactorSimulatorBlockEntity be;

	public ReactorSimulatorSlot(Container container, ReactorSimulatorBlockEntity be, int index, int x, int y) {
		super(container, index, x, y);
		this.be = be;
	}

	@Override
	public boolean mayPlace(ItemStack stack) {
		return false;
	}

	@Override
	public boolean mayPickup(Player player) {
		return false;
	}

	public boolean isActive() {
		int col = getContainerSlot() % NuclearReactor.MAX_COLUMNS;
		return col < be.getActiveColumns();
	}
}
