package dev.ftb.mods.ftbic.item;

import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.block.SprayPaintable;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SprayPaintCanItem extends Item {
	public final boolean dark;

	public SprayPaintCanItem(boolean d) {
		super(new Properties().stacksTo(1).tab(FTBIC.TAB));
		dark = d;
	}

	@Override
	public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
		BlockState state = context.getLevel().getBlockState(context.getClickedPos());

		if (state.getBlock() instanceof SprayPaintable) {
			if (((SprayPaintable) state.getBlock()).paint(state, context.getLevel(), context.getClickedPos(), dark) && context.getLevel().isClientSide()) {
				context.getLevel().playSound(context.getPlayer(), context.getClickedPos(), SoundEvents.REDSTONE_TORCH_BURNOUT, SoundSource.BLOCKS, 0.5F, 2.6F + (context.getLevel().random.nextFloat() - context.getLevel().random.nextFloat()) * 0.8F);
			}

			return InteractionResult.SUCCESS;
		}

		return InteractionResult.PASS;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> list, TooltipFlag flag) {
		list.add(Component.translatable("item.ftbic.spray_paint_can.tooltip").withStyle(ChatFormatting.GRAY));
	}
}
