package dev.ftb.mods.ftbic.item.reactor;

import dev.ftb.mods.ftbic.util.FTBICUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class HeatExchangerItem extends BaseReactorItem {
	public final int heatTransferToAdjacent;
	public final int heatTransferToCore;

	public HeatExchangerItem(int durability, int a, int c) {
		super(durability);
		heatTransferToAdjacent = a;
		heatTransferToCore = c;
	}

	@Override
	public boolean isHeatAcceptor(ItemStack stack) {
		return stack.getMaxDamage() > 0;
	}

	@Override
	public void reactorInfo(ItemStack stack, List<Component> list, boolean shift, boolean advanced, @Nullable NuclearReactor reactor, int x, int y) {
		if (stack.getMaxDamage() > 0) {
			list.add(new TextComponent("Coolant: ").append(FTBICUtils.formatHeat(stack.getMaxDamage() - stack.getDamageValue())).withStyle(ChatFormatting.GRAY));
		}

		list.add(new TextComponent("Heat Transfer to Adjacent: ").append(FTBICUtils.formatHeat(heatTransferToAdjacent)).append("/s").withStyle(ChatFormatting.GRAY));
		list.add(new TextComponent("Heat Transfer to Core: ").append(FTBICUtils.formatHeat(heatTransferToCore)).append("/s").withStyle(ChatFormatting.GRAY));
	}

	@Override
	public void reactorTickPre(NuclearReactor reactor, ItemStack stack, int x, int y) {
	}
}
