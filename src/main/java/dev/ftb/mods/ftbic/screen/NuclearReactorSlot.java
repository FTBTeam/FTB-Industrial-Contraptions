package dev.ftb.mods.ftbic.screen;

import dev.ftb.mods.ftbic.item.reactor.ReactorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

public class NuclearReactorSlot extends SimpleItemHandlerSlot {
	private final int index;
	private final NuclearReactorMenu menu;

	public NuclearReactorSlot(IItemHandler itemHandler, NuclearReactorMenu m, int index, int xPosition, int yPosition) {
		super(itemHandler, index, xPosition, yPosition);
		this.index = index;
		this.menu = m;
	}

	@Override
	public boolean mayPlace(@NotNull ItemStack stack) {
		return !stack.isEmpty() && (index % 9) < (menu.getChambers() + 3) && stack.getItem() instanceof ReactorItem;
	}

	@Override
	public void setChanged() {
		super.setChanged();
		// refresh stats?
	}
}
