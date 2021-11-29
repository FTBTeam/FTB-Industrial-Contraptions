package dev.ftb.mods.ftbic.block.entity.generator;

import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.item.reactor.ReactorItem;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class NuclearReactorBlockEntity extends GeneratorBlockEntity {
	public NuclearReactorBlockEntity() {
		super(FTBICElectricBlocks.NUCLEAR_REACTOR);
	}

	@Override
	public void initProperties() {
		super.initProperties();
		maxEnergyOutputTransfer = FTBICConfig.IV_TRANSFER_RATE;
	}

	@Override
	public int getSlotLimit(int slot) {
		return 1;
	}

	@Override
	public boolean isItemValid(int slot, @NotNull ItemStack stack) {
		return super.isItemValid(slot, stack) && stack.getItem() instanceof ReactorItem;
	}

	@Override
	public boolean savePlacer() {
		return true;
	}
}
