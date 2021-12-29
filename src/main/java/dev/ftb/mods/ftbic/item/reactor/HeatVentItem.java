package dev.ftb.mods.ftbic.item.reactor;

import dev.ftb.mods.ftbic.block.entity.generator.NuclearReactorBlockEntity;
import dev.ftb.mods.ftbic.util.FTBICUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class HeatVentItem extends BaseReactorItem {
	public final int selfCooling;
	public final int reactorCooling;
	public final int componentCooling;

	public HeatVentItem(int durability, int s, int h, int c) {
		super(durability);
		selfCooling = s;
		reactorCooling = h;
		componentCooling = c;
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

		list.add(new TextComponent("Self Cooling: ").append(FTBICUtils.formatHeat(selfCooling)).append("/s").withStyle(ChatFormatting.GRAY));
		list.add(new TextComponent("Reactor Cooling: ").append(FTBICUtils.formatHeat(reactorCooling)).append("/s").withStyle(ChatFormatting.GRAY));
		list.add(new TextComponent("Component Cooling: ").append(FTBICUtils.formatHeat(componentCooling)).append("/s").withStyle(ChatFormatting.GRAY));
	}

	@Override
	public void reactorTickPre(NuclearReactor reactor, ItemStack stack, int x, int y) {
		if (reactorCooling > 0) {
			reactor.addHeat(-reactorCooling);
		}

		if (selfCooling > 0) {
			damageReactorItem(stack, -selfCooling);
		}

		if (componentCooling > 0) {
			for (int i = 0; i < 4; i++) {
				ItemStack is = reactor.getAt(x + NuclearReactorBlockEntity.OFFSET_X[i], y + NuclearReactorBlockEntity.OFFSET_Y[i]);

				if (is.getItem() instanceof ReactorItem && ((ReactorItem) is.getItem()).isCoolant(is)) {
					((ReactorItem) is.getItem()).damageReactorItem(is, -componentCooling);
				}
			}
		}
	}
}
