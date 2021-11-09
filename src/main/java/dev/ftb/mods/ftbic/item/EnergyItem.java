package dev.ftb.mods.ftbic.item;

import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.util.EnergyItemHandler;
import dev.ftb.mods.ftbic.util.EnergyTier;
import dev.ftb.mods.ftbic.util.FTBICUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EnergyItem extends Item implements EnergyItemHandler {
	public final EnergyTier tier;
	public final int capacity;

	public EnergyItem(EnergyTier t, int cap) {
		super(new Properties().stacksTo(1).tab(FTBIC.TAB));
		tier = t;
		capacity = cap;
	}

	@Nullable
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
		if (!isCreativeEnergyItem()) {
			CompoundTag t = stack.getOrCreateTag();

			if (!t.contains("Energy")) {
				t.putInt("Energy", capacity);
			}
		}

		return null;
	}

	@Override
	public double getEnergyCapacity(ItemStack stack) {
		return capacity;
	}

	@Override
	public boolean showDurabilityBar(ItemStack stack) {
		return true;
	}

	@Override
	public double getDurabilityForDisplay(ItemStack stack) {
		return 1D - getEnergy(stack) / (double) capacity;
	}

	@Override
	public int getRGBDurabilityForDisplay(ItemStack stack) {
		return 0xFFFF0000;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> list, TooltipFlag flag) {
		if (!isCreativeEnergyItem()) {
			list.add(new TextComponent(FTBICUtils.formatPower(getEnergy(stack), capacity)).withStyle(ChatFormatting.GRAY));
		}
	}
}
