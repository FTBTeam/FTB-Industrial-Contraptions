package dev.ftb.mods.ftbic.item;

import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.registry.ModDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.function.Consumer;

/**
 * Portable fluid storage cell. Right-click a fluid source to pick it up (like a bucket), right-click
 * into a tank-exposing block to deposit. Contents are stored in the {@code FLUID_CELL_CONTENT}
 * data component; foreign pipes can also drain via {@code Capabilities.Fluid.ITEM}.
 */
public class FluidCellItem extends Item {
	public FluidCellItem(Properties props) {
		super(props.stacksTo(16));
	}

	public static FluidStack getStored(ItemStack stack) {
		FluidStack fs = stack.get(ModDataComponents.FLUID_CELL_CONTENT.get());
		return fs == null ? FluidStack.EMPTY : fs;
	}

	public static void setStored(ItemStack stack, FluidStack fluid) {
		if (fluid == null || fluid.isEmpty()) {
			stack.remove(ModDataComponents.FLUID_CELL_CONTENT.get());
		} else {
			stack.set(ModDataComponents.FLUID_CELL_CONTENT.get(), fluid.copy());
		}
	}

	public static int capacity() {
		return FTBICConfig.NUCLEAR.FLUID_CELL_CAPACITY.get();
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		FluidStack stored = getStored(stack);
		BlockHitResult hit = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);
		if (hit.getType() != HitResult.Type.BLOCK) return InteractionResult.PASS;

		BlockPos pos = hit.getBlockPos();
		if (!level.mayInteract(player, pos)
				|| !player.mayUseItemAt(pos.relative(hit.getDirection()), hit.getDirection(), stack)) {
			return InteractionResult.FAIL;
		}

		BlockState state = level.getBlockState(pos);
		// Fill the cell from a BucketPickup block.
		if (stored.isEmpty() && state.getBlock() instanceof BucketPickup bucketPickup) {
			ItemStack picked = bucketPickup.pickupBlock(player, level, pos, state);
			if (picked.getItem() instanceof BucketItem bucketItem) {
				Fluid f = bucketItem.content;
				if (f != Fluids.EMPTY) {
					player.awardStat(Stats.ITEM_USED.get(this));
					bucketPickup.getPickupSound(state).ifPresent(s -> player.playSound(s, 1F, 1F));
					FluidStack newStored = new FluidStack(f, capacity());
					ItemStack filled = stack.copyWithCount(1);
					setStored(filled, newStored);
					ItemStack result = ItemUtils.createFilledResult(stack, player, filled);
					if (player instanceof ServerPlayer sp) {
						CriteriaTriggers.FILLED_BUCKET.trigger(sp, picked);
					}
					player.setItemInHand(hand, result);
					return InteractionResult.SUCCESS;
				}
			}
		}
		return InteractionResult.PASS;
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display,
			Consumer<Component> tooltip, TooltipFlag flag) {
		super.appendHoverText(stack, context, display, tooltip, flag);
		FluidStack stored = getStored(stack);
		if (stored.isEmpty()) {
			tooltip.accept(Component.translatable("item.ftbic.fluid_cell.empty")
					.withStyle(ChatFormatting.DARK_GRAY));
		} else {
			var fluidId = BuiltInRegistries.FLUID.getKey(stored.getFluid());
			tooltip.accept(Component.translatable("item.ftbic.fluid_cell.contents",
							stored.getAmount(), capacity(), fluidId == null ? "?" : fluidId.getPath())
					.withStyle(ChatFormatting.GRAY));
		}
	}
}
