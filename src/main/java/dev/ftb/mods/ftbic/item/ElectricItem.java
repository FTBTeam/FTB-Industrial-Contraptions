package dev.ftb.mods.ftbic.item;

import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.util.EnergyItemHandler;
import dev.ftb.mods.ftbic.util.EnergyTier;
import dev.ftb.mods.ftbic.util.FTBICUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ElectricItem extends Item implements EnergyItemHandler {
	public final EnergyTier tier;
	public final double capacity;

	public ElectricItem(EnergyTier t, double cap) {
		super(new Properties().stacksTo(1).tab(FTBIC.TAB));
		tier = t;
		capacity = cap;
	}

	@Override
	public double getEnergyCapacity(ItemStack stack) {
		return capacity;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> list, TooltipFlag flag) {
		if (!isCreativeEnergyItem()) {
			list.add(FTBICUtils.energyTooltip(stack, this));
		}
	}

	@Override
	public boolean isBarVisible(ItemStack stack) {
		return stack.hasTag() && stack.getTag().contains("Energy");
	}

	@Override
	public int getBarWidth(ItemStack stack) {
		return Math.round((float) Mth.clamp((getEnergy(stack) / getEnergyCapacity(stack)) * 13D, 0D, 13D));
	}

	@Override
	public int getBarColor(ItemStack stack) {
		return 0xFFFF0000;
	}
}
