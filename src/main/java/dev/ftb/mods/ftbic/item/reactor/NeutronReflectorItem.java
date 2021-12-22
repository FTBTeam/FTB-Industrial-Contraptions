package dev.ftb.mods.ftbic.item.reactor;

import dev.ftb.mods.ftbic.block.entity.generator.NuclearReactorBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class NeutronReflectorItem extends BaseReactorItem implements NeutronReflectingReactorItem {
	public NeutronReflectorItem(int durability) {
		super(durability);
	}

	@Override
	public void reactorInfo(ItemStack stack, List<Component> list, boolean shift, boolean advanced, @Nullable NuclearReactor reactor, int x, int y) {
		list.add(new TextComponent(stack.getMaxDamage() > 0 ? String.format("Durability: %,d", stack.getMaxDamage() - stack.getDamageValue()) : "Durability: Infinite").withStyle(ChatFormatting.GRAY));
	}

	@Override
	public void reactorTickPost(NuclearReactor reactor, ItemStack stack, int x, int y) {
		if (reactor.paused || stack.getMaxDamage() <= 0) {
			return;
		}

		for (int i = 0; i < 4; i++) {
			ItemStack item = reactor.getAt(x + NuclearReactorBlockEntity.OFFSET_X[i], y + NuclearReactorBlockEntity.OFFSET_Y[i]);

			if (item.getItem() instanceof ReactorItem) {
				damageReactorItem(stack, ((ReactorItem) item.getItem()).getRods());
			}
		}
	}
}
