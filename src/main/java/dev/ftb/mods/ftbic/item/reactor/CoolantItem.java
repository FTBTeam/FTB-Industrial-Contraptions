package dev.ftb.mods.ftbic.item.reactor;

import dev.ftb.mods.ftbic.util.FTBICUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CoolantItem extends BaseReactorItem {
	public CoolantItem(int durability) {
		super(durability);
	}

	@Override
	public boolean isHeatAcceptor(ItemStack stack) {
		return stack.getMaxDamage() > 0;
	}

	@Override
	public void reactorInfo(ItemStack stack, List<Component> list, boolean shift, boolean advanced, @Nullable NuclearReactor reactor, int x, int y) {
		list.add(new TextComponent("Coolant: ").append(FTBICUtils.formatHeat(stack.getMaxDamage() - stack.getDamageValue())).withStyle(ChatFormatting.GRAY));
	}
}
