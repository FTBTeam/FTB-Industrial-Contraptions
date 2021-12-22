package dev.ftb.mods.ftbic.item.reactor;

import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.block.entity.generator.NuclearReactorBlockEntity;
import dev.ftb.mods.ftbic.util.FTBICUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FuelRodItem extends BaseReactorItem implements NeutronReflectingReactorItem {
	public final int rods;
	public final int pulses;
	public final double energyMultiplier;
	public final double heatMultiplier;

	public FuelRodItem(int durability, int r, double e, double h) {
		super(durability);
		rods = r;
		pulses = rods == 1 ? 1 : rods == 2 ? 2 : 3;
		energyMultiplier = e;
		heatMultiplier = h;
	}

	@Override
	public void reactorInfo(ItemStack stack, List<Component> list, boolean shift, boolean advanced, @Nullable NuclearReactor reactor, int x, int y) {
		list.add(new TextComponent(String.format("Lifespan: %,d s", stack.getMaxDamage() - stack.getDamageValue())).withStyle(ChatFormatting.GRAY));

		int p = pulses;

		if (reactor != null) {
			for (int i = 0; i < 4; i++) {
				if (reactor.getAt(x + NuclearReactorBlockEntity.OFFSET_X[i], y + NuclearReactorBlockEntity.OFFSET_Y[i]).getItem() instanceof NeutronReflectingReactorItem) {
					p++;
				}
			}
		}

		list.add(new TextComponent("Energy Output: ").append(FTBICUtils.formatEnergy(p * energyMultiplier)).append("/t").withStyle(ChatFormatting.GRAY));
		list.add(new TextComponent("Heat Produced: ").append(FTBICUtils.formatHeat((int) (heatMultiplier * p * (p + 1)))).append("/s").withStyle(ChatFormatting.GRAY));
	}

	@Override
	public int getRods() {
		return rods;
	}

	@Override
	public void reactorTickPre(NuclearReactor reactor, ItemStack stack, int x, int y) {
		if (reactor.paused) {
			return;
		}

		reactor.explosionRadius += getRods() * FTBICConfig.NUCLEAR_REACTOR_EXPLOSION_MULTIPLIER;

		int p = pulses;
		ItemStack[] around = new ItemStack[4];

		for (int i = 0; i < 4; i++) {
			around[i] = reactor.getAt(x + NuclearReactorBlockEntity.OFFSET_X[i], y + NuclearReactorBlockEntity.OFFSET_Y[i]);

			if (around[i].getItem() instanceof NeutronReflectingReactorItem) {
				p++;
			}
		}

		reactor.distributeHeat(around, (int) (heatMultiplier * p * (p + 1)));
	}

	@Override
	public void reactorTickPost(NuclearReactor reactor, ItemStack stack, int x, int y) {
		if (reactor.paused) {
			return;
		}

		int p = pulses;
		ItemStack[] around = new ItemStack[4];

		for (int i = 0; i < 4; i++) {
			around[i] = reactor.getAt(x + NuclearReactorBlockEntity.OFFSET_X[i], y + NuclearReactorBlockEntity.OFFSET_Y[i]);

			if (around[i].getItem() instanceof NeutronReflectingReactorItem) {
				p++;
			}
		}

		reactor.energyOutput += p * energyMultiplier;
		damageReactorItem(stack, 1);
	}
}
