package dev.ftb.mods.ftbic.item.reactor;

import dev.ftb.mods.ftbic.FTBIC;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ReactorItem extends Item {
	public ReactorItem(int durability) {
		super(new Properties().durability(durability).tab(FTBIC.TAB));
	}

	@Override
	public void appendHoverText(ItemStack arg, @Nullable Level arg2, List<Component> list, TooltipFlag arg3) {
		list.add(new TextComponent("WIP!").withStyle(ChatFormatting.RED));
	}
}
