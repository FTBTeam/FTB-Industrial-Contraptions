package dev.ftb.mods.ftbic.item;

import dev.ftb.mods.ftbic.block.SprayPaintable;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Right-click a {@link SprayPaintable} block to toggle its `DARK` state. Dark=true repaints the
 * block to the dark variant; light version does the inverse. Matches the 1.18.2 reference: stacks
 * to 1, no durability, unlimited uses (paint cans never run out).
 */
public class SprayPaintCanItem extends Item {
	public final boolean dark;

	public SprayPaintCanItem(Properties props, boolean dark) {
		super(props.stacksTo(1));
		this.dark = dark;
	}

	@Override
	public InteractionResult useOn(UseOnContext ctx) {
		BlockState state = ctx.getLevel().getBlockState(ctx.getClickedPos());
		if (state.getBlock() instanceof SprayPaintable paintable) {
			if (paintable.paint(state, ctx.getLevel(), ctx.getClickedPos(), dark)
					&& ctx.getLevel().isClientSide()) {
				float pitch = 2.6F + (ctx.getLevel().getRandom().nextFloat() - ctx.getLevel().getRandom().nextFloat()) * 0.8F;
				ctx.getLevel().playSound(ctx.getPlayer(), ctx.getClickedPos(),
						SoundEvents.REDSTONE_TORCH_BURNOUT, SoundSource.BLOCKS, 0.5F, pitch);
			}
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.PASS;
	}
}
