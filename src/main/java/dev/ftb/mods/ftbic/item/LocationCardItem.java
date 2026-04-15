package dev.ftb.mods.ftbic.item;

import dev.ftb.mods.ftbic.block.entity.machine.TeleporterBlockEntity;
import dev.ftb.mods.ftbic.registry.ModDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

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
			clear(stack);
			player.sendSystemMessage(Component.translatable("item.ftbic.location_card.cleared"));
		}
		return InteractionResult.SUCCESS;
	}

	@Override
	public InteractionResult useOn(UseOnContext ctx) {
		Level level = ctx.getLevel();
		BlockPos pos = ctx.getClickedPos();
		Player player = ctx.getPlayer();
		if (player == null) return InteractionResult.PASS;
		if (!(level.getBlockEntity(pos) instanceof TeleporterBlockEntity tele)) {
			return InteractionResult.PASS;
		}
		if (level.isClientSide()) return InteractionResult.SUCCESS;

		ItemStack stack = ctx.getItemInHand();
		String boundDim = stack.get(ModDataComponents.BOUND_DIMENSION.get());

		if (boundDim != null && player instanceof ServerPlayer sp) {
			Identifier id = Identifier.tryParse(boundDim);
			Integer bx = stack.get(ModDataComponents.BOUND_X.get());
			Integer by = stack.get(ModDataComponents.BOUND_Y.get());
			Integer bz = stack.get(ModDataComponents.BOUND_Z.get());
			if (id != null && bx != null && by != null && bz != null) {
				tele.select(sp, ResourceKey.create(Registries.DIMENSION, id), new BlockPos(bx, by, bz));
				String name = stack.get(ModDataComponents.LOCATION_NAME.get());
				player.sendSystemMessage(Component.translatable("item.ftbic.location_card.linked",
						name == null || name.isEmpty() ? Component.translatable("item.ftbic.location_card.unnamed") : Component.literal(name))
						.withStyle(ChatFormatting.GREEN));
				return InteractionResult.SUCCESS;
			}
		}

		String teleName = tele.name == null ? "" : tele.name;
		bind(stack, level.dimension().identifier().toString(), pos.getX(), pos.getY(), pos.getZ(), teleName);
		player.sendSystemMessage(Component.translatable("item.ftbic.location_card.bound",
				teleName.isEmpty() ? Component.translatable("item.ftbic.location_card.unnamed") : Component.literal(teleName))
				.withStyle(ChatFormatting.AQUA));
		return InteractionResult.SUCCESS;
	}

	@Override
	@SuppressWarnings("deprecation")
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
		} else {
			stack.remove(ModDataComponents.LOCATION_NAME.get());
		}
	}

	public static void clear(ItemStack stack) {
		stack.remove(ModDataComponents.BOUND_DIMENSION.get());
		stack.remove(ModDataComponents.BOUND_X.get());
		stack.remove(ModDataComponents.BOUND_Y.get());
		stack.remove(ModDataComponents.BOUND_Z.get());
		stack.remove(ModDataComponents.LOCATION_NAME.get());
	}
}
