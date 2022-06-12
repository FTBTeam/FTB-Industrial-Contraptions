package dev.ftb.mods.ftbic.item.reactor;

import dev.ftb.mods.ftbic.block.entity.generator.NuclearReactorBlockEntity;
import dev.ftb.mods.ftbic.util.FTBICUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
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
			list.add(Component.literal("Coolant: ").append(FTBICUtils.formatHeat(stack.getMaxDamage() - stack.getDamageValue())).withStyle(ChatFormatting.GRAY));
		}

		list.add(Component.literal("Heat Transfer to Adjacent: ").append(FTBICUtils.formatHeat(heatTransferToAdjacent)).append("/s").withStyle(ChatFormatting.GRAY));
		list.add(Component.literal("Heat Transfer to Core: ").append(FTBICUtils.formatHeat(heatTransferToCore)).append("/s").withStyle(ChatFormatting.GRAY));
	}

	@Override
	public void reactorTickPre(NuclearReactor reactor, ItemStack stack, int x, int y) {
		int damage = 0;

		if (heatTransferToAdjacent > 0) {
			for (int i = 0; i < 4; i++) {
				ItemStack is = reactor.getAt(x + NuclearReactorBlockEntity.OFFSET_X[i], y + NuclearReactorBlockEntity.OFFSET_Y[i]);

				if (is.getItem() instanceof ReactorItem reactorItem && reactorItem.isHeatAcceptor(is)) {
                    double sh = getRelativeDamage(stack) * 100D;
					double rh = reactorItem.getRelativeDamage(is) * 100D;
					int heat = getHeatTransfer(sh, rh, is.getMaxDamage(), heatTransferToAdjacent);
					damage -= heat;
					damage += reactorItem.damageReactorItem(is, heat);
				}
			}
		}

		if (heatTransferToCore > 0) {
			double sh = getRelativeDamage(stack) * 100D;
			double rh = reactor.heat * 100D / (double) reactor.maxHeat;
			int heat = getHeatTransfer(sh, rh, reactor.maxHeat, heatTransferToCore);
			damage -= heat;
			reactor.addHeat(heat);
		}

		damageReactorItem(stack, damage);
	}

	private int getHeatTransfer(double sh, double rh, int max, int transfer) {
		double hh = rh + sh / 2D;
		int add = Math.min(Mth.floor(max * hh / 100D), transfer);

		if (hh < 0.25) {
			add = 1;
		} else if (hh < 0.5) {
			add = transfer / 8;
		} else if (hh < 0.75) {
			add = transfer / 4;
		} else if (hh < 1.0) {
			add = transfer / 2;
		}

		double frh = Mth.floor(rh * 10D) / 10D;
		double fsh = Mth.floor(sh * 10D) / 10D;

		if (frh > fsh) {
			add -= 2 * add;
		} else if (frh == fsh) {
			add = 0;
		}

		return add;
	}
}
