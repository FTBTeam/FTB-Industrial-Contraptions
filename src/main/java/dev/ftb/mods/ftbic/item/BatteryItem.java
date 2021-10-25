package dev.ftb.mods.ftbic.item;

import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.util.FTBICUtils;
import dev.ftb.mods.ftbic.util.PowerTier;
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

public class BatteryItem extends Item {
	public final BatteryType batteryType;
	public final PowerTier tier;
	public final int capacity;

	public BatteryItem(BatteryType b, PowerTier t, int cap) {
		super(new Properties().stacksTo(1).tab(FTBIC.TAB));
		batteryType = b;
		tier = t;
		capacity = cap;
	}

	@Nullable
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
		if (!batteryType.creative) {
			CompoundTag t = stack.getOrCreateTag();

			if (!t.contains("Energy")) {
				t.putInt("Energy", capacity);
			}
		}

		return new BatteryData(this, stack);
	}

	@Override
	public boolean showDurabilityBar(ItemStack stack) {
		return true;
	}

	@Override
	public double getDurabilityForDisplay(ItemStack stack) {
		return batteryType.creative ? 0.5D : stack.hasTag() ? (1D - stack.getTag().getInt("Energy") / (double) capacity) : 1D;
	}

	@Override
	public int getRGBDurabilityForDisplay(ItemStack stack) {
		return 0xFFFF0000;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> list, TooltipFlag flag) {
		if (!batteryType.creative) {
			list.add(new TextComponent(FTBICUtils.formatPower((stack.hasTag() ? stack.getTag().getInt("Energy") : 0), capacity)).withStyle(ChatFormatting.GRAY));
		}
	}
}
