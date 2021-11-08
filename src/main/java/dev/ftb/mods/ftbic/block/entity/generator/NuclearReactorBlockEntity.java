package dev.ftb.mods.ftbic.block.entity.generator;

import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.item.ReactorItem;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class NuclearReactorBlockEntity extends GeneratorBlockEntity {
	public NuclearReactorBlockEntity() {
		super(FTBICElectricBlocks.NUCLEAR_REACTOR.blockEntity.get(), 54, 0);
	}

	@Override
	public void initProperties() {
		super.initProperties();
	}

	@Override
	public int getSlotLimit(int slot) {
		return 1;
	}

	@Override
	public boolean isItemValid(int slot, @NotNull ItemStack stack) {
		return super.isItemValid(slot, stack) && stack.getItem() instanceof ReactorItem;
	}
}
