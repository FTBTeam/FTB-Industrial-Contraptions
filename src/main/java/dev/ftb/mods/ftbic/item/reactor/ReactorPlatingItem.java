package dev.ftb.mods.ftbic.item.reactor;

import dev.ftb.mods.ftbic.util.FTBICUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ReactorPlatingItem extends BaseReactorItem {
	public final int maxHeatBonus;
	public final double explosionModifier;

	public ReactorPlatingItem(int h, double e) {
		super(0);
		maxHeatBonus = h;
		explosionModifier = e;
	}

	@Override
	public void reactorInfo(ItemStack stack, List<Component> list, boolean shift, boolean advanced, @Nullable NuclearReactor reactor, int x, int y) {
		list.add(Component.literal("Max Heat: +").append(FTBICUtils.formatHeat(maxHeatBonus)).withStyle(ChatFormatting.GRAY));
		list.add(Component.literal("Explosion Size: -" + (int) (100D - explosionModifier * 100D) + "%").withStyle(ChatFormatting.GRAY));
	}

	@Override
	public void reactorTickPre(NuclearReactor reactor, ItemStack stack, int x, int y) {
		reactor.maxHeat += maxHeatBonus;
		reactor.explosionModifier *= explosionModifier;
	}
}
