package dev.ftb.mods.ftbic.item;

import dev.ftb.mods.ftbic.FTBIC;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LocationCardItem extends Item {
	public LocationCardItem() {
		super(new Properties().stacksTo(1).tab(FTBIC.TAB));
	}

	@Override
	public boolean isFoil(ItemStack stack) {
		return stack.hasTag() && stack.getTag().contains("Dimension");
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);

		if (player.isCrouching() && !level.isClientSide()) {
			stack.removeTagKey("Dimension");
			stack.removeTagKey("PosX");
			stack.removeTagKey("PosY");
			stack.removeTagKey("PosZ");
			player.displayClientMessage(Component.literal("Location cleared!"), true);
		}

		return InteractionResultHolder.success(stack);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> list, TooltipFlag flag) {
		if (stack.hasTag() && stack.getTag().contains("Dimension")) {
			list.add(Component.literal(String.format("X %d Y %d Z %d in %s", stack.getTag().getInt("PosX"), stack.getTag().getInt("PosY"), stack.getTag().getInt("PosZ"), stack.getTag().getString("Dimension"))).withStyle(ChatFormatting.GRAY));
		}

		// list.add(Component.literal("< " + FTBICConfig.FLUID_CELL_CAPACITY.get() + " mB of ").append(Component.translatable(fluid.getAttributes().getTranslationKey())).append(" >").withStyle(ChatFormatting.GRAY));
	}
}
