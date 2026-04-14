package dev.ftb.mods.ftbic.item;

import dev.ftb.mods.ftbic.registry.ModDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * Stores a dimension + block position via data components. Sneak+right-click clears the binding.
 * Binding *to* a position happens when the Teleporter BE accepts the card — that's wired in Phase 2d.
 */
public class LocationCardItem extends Item {
	public LocationCardItem(Properties props) {
		super(props.stacksTo(1));
	}

	@Override
	public boolean isFoil(ItemStack stack) {
		return stack.has(ModDataComponents.BOUND_DIMENSION.get());
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (player.isCrouching() && !level.isClientSide()) {
			stack.remove(ModDataComponents.BOUND_DIMENSION.get());
			stack.remove(ModDataComponents.BOUND_X.get());
			stack.remove(ModDataComponents.BOUND_Y.get());
			stack.remove(ModDataComponents.BOUND_Z.get());
			stack.remove(ModDataComponents.LOCATION_NAME.get());
			player.sendSystemMessage(Component.literal("Location cleared!"));
		}
		return InteractionResult.SUCCESS;
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display,
			Consumer<Component> tooltip, TooltipFlag flag) {
		super.appendHoverText(stack, context, display, tooltip, flag);
		String dim = stack.get(ModDataComponents.BOUND_DIMENSION.get());
		if (dim == null) {
			tooltip.accept(Component.translatable("item.ftbic.location_card.unbound")
					.withStyle(ChatFormatting.DARK_GRAY));
			return;
		}
		Integer x = stack.get(ModDataComponents.BOUND_X.get());
		Integer y = stack.get(ModDataComponents.BOUND_Y.get());
		Integer z = stack.get(ModDataComponents.BOUND_Z.get());
		String name = stack.get(ModDataComponents.LOCATION_NAME.get());
		if (name != null && !name.isEmpty()) {
			tooltip.accept(Component.literal(name).withStyle(ChatFormatting.WHITE));
		}
		tooltip.accept(Component.literal(dim).withStyle(ChatFormatting.GRAY));
		if (x != null && y != null && z != null) {
			tooltip.accept(Component.literal("[" + x + ", " + y + ", " + z + "]")
					.withStyle(ChatFormatting.DARK_GRAY));
		}
	}

	public static void bind(ItemStack stack, String dimension, int x, int y, int z, @Nullable String name) {
		stack.set(ModDataComponents.BOUND_DIMENSION.get(), dimension);
		stack.set(ModDataComponents.BOUND_X.get(), x);
		stack.set(ModDataComponents.BOUND_Y.get(), y);
		stack.set(ModDataComponents.BOUND_Z.get(), z);
		if (name != null && !name.isEmpty()) {
			stack.set(ModDataComponents.LOCATION_NAME.get(), name);
		}
	}
}
