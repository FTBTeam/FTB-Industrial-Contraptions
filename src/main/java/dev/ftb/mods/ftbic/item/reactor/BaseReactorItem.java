package dev.ftb.mods.ftbic.item.reactor;

import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.screen.NuclearReactorMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class BaseReactorItem extends Item implements ReactorItem {
	private int noDamageStack = 16;

	public BaseReactorItem(int durability) {
		super(new Properties().durability(durability).tab(FTBIC.TAB));
	}

	public BaseReactorItem noDamageStack(int s) {
		noDamageStack = s;
		return this;
	}

	@Override
	public int getMaxStackSize(ItemStack stack) {
		return stack.getDamageValue() == 0 ? noDamageStack : super.getMaxStackSize(stack);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public final void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> list, TooltipFlag flag) {
		list.add(Component.literal("Nuclear Reactor Component").withStyle(ChatFormatting.DARK_GRAY));

		boolean shift = Screen.hasShiftDown();

		Player player = Minecraft.getInstance().player;

		if (player != null && player.containerMenu instanceof NuclearReactorMenu m) {
			for (int x = 0; x < 9; x++) {
				for (int y = 0; y < 6; y++) {
					if (m.entity.reactor.getAt(x, y) == stack) {
						reactorInfo(stack, list, shift, flag.isAdvanced(), m.entity.reactor, x, y);
						return;
					}
				}
			}
		}

		reactorInfo(stack, list, shift, flag.isAdvanced(), null, -1, -1);
	}

	public void reactorInfo(ItemStack stack, List<Component> list, boolean shift, boolean advanced, @Nullable NuclearReactor reactor, int x, int y) {
	}

	@Override
	public void reactorTickPre(NuclearReactor reactor, ItemStack stack, int x, int y) {
	}

	@Override
	public void reactorTickPost(NuclearReactor reactor, ItemStack stack, int x, int y) {
	}
}
