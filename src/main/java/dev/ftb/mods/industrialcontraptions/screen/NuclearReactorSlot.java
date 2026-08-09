package dev.ftb.mods.industrialcontraptions.screen;

import dev.ftb.mods.industrialcontraptions.block.entity.ElectricBlockEntity;
import dev.ftb.mods.industrialcontraptions.item.reactor.NuclearReactor;
import dev.ftb.mods.industrialcontraptions.item.reactor.ReactorItem;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class NuclearReactorSlot extends Slot {
	private final ElectricBlockEntity be;

	public NuclearReactorSlot(Container container, ElectricBlockEntity be, int index, int x, int y) {
		super(container, index, x, y);
		this.be = be;
	}

	@Override
	public boolean mayPlace(ItemStack stack) {
		return be.isItemValid(getContainerSlot(), stack);
	}

	@Override
	public boolean mayPickup(Player player) {
		return true;
	}

	public boolean isActive(int activeColumns) {
		int col = getContainerSlot() % NuclearReactor.MAX_COLUMNS;
		return col < activeColumns;
	}

	public static boolean isReactorItem(ItemStack stack) {
		return stack.isEmpty() || stack.getItem() instanceof ReactorItem;
	}
}
