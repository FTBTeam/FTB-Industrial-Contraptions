package dev.ftb.mods.ftbic.item;

import dev.ftb.mods.ftbic.FTBIC;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.ForgeEventFactory;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class CellItem extends Item {
	public static final Map<Fluid, CellItem> FLUID_TO_CELL_MAP = new HashMap<>();

	public final Fluid fluid;

	public CellItem(Fluid f) {
		super(new Properties().stacksTo(f == Fluids.EMPTY ? 64 : 16).tab(FTBIC.TAB));
		fluid = f;
		FLUID_TO_CELL_MAP.put(fluid, this);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		BlockHitResult hit = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);
		InteractionResultHolder<ItemStack> ret = ForgeEventFactory.onBucketUse(player, level, stack, hit);

		if (ret != null) {
			return ret;
		} else if (hit.getType() == HitResult.Type.MISS) {
			return InteractionResultHolder.pass(stack);
		} else if (hit.getType() != HitResult.Type.BLOCK) {
			return InteractionResultHolder.pass(stack);
		} else if (fluid != Fluids.EMPTY) {
			return InteractionResultHolder.fail(stack);
		}

		BlockPos pos = hit.getBlockPos();
		Direction direction = hit.getDirection();
		BlockPos pos1 = pos.relative(direction);

		if (level.mayInteract(player, pos) && player.mayUseItemAt(pos1, direction, stack)) {
			BlockState state = level.getBlockState(pos);

			if (state.getBlock() instanceof BucketPickup) {
				Fluid fluid = ((BucketPickup) state.getBlock()).takeLiquid(level, pos, state);

				if (fluid != Fluids.EMPTY && FLUID_TO_CELL_MAP.containsKey(fluid)) {
					player.awardStat(Stats.ITEM_USED.get(this));
					SoundEvent soundevent = this.fluid.getAttributes().getFillSound();

					if (soundevent == null) {
						soundevent = fluid.is(FluidTags.LAVA) ? SoundEvents.BUCKET_FILL_LAVA : SoundEvents.BUCKET_FILL;
					}

					player.playSound(soundevent, 1F, 1F);
					ItemStack itemstack1 = ItemUtils.createFilledResult(stack, player, new ItemStack(FLUID_TO_CELL_MAP.get(fluid)));

					if (!level.isClientSide()) {
						CriteriaTriggers.FILLED_BUCKET.trigger((ServerPlayer) player, new ItemStack(fluid.getBucket()));
					}

					return InteractionResultHolder.sidedSuccess(itemstack1, level.isClientSide());
				}
			}
		}

		return InteractionResultHolder.fail(stack);
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
		return new CellData(stack);
	}
}
