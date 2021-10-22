package dev.ftb.mods.ftbic.item;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BatteryData implements ICapabilityProvider {
	private final ItemStack item;
	private final LazyOptional<?> thisOptional = LazyOptional.of(() -> this);

	public BatteryData(ItemStack is) {
		item = is;
	}

	@NotNull
	@Override
	public <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction arg) {
		return capability == CapabilityEnergy.ENERGY ? thisOptional.cast() : LazyOptional.empty();
	}
}
