package dev.ftb.mods.ftbic.block;

import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.block.entity.ElectricBlockEntity;
import dev.ftb.mods.ftbic.util.EnergyHandler;
import dev.ftb.mods.ftbic.util.EnergyTier;
import dev.ftb.mods.ftbic.util.FTBICUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.energy.CapabilityEnergy;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CableBlock extends BaseCableBlock {
	public final EnergyTier tier;

	public CableBlock(EnergyTier _tier, int border, SoundType soundType) {
		super(border, soundType);
		tier = _tier;
	}

	@Override
	@Deprecated
	public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos pos, BlockPos facingPos) {
		if (state.getValue(BlockStateProperties.WATERLOGGED)) {
			level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
		}

		if (facingState.getBlock() instanceof BurntCableBlock) {
			return state;
		}

		boolean c = canCableConnectFrom(facingState, level, facingPos, facing.getOpposite());

		if (!level.isClientSide() && facingState.getBlock() != this && c != state.getValue(CONNECTION[facing.ordinal()])) {
			ElectricBlockEntity.electricNetworkUpdated(level, facingPos);
		}

		return state.setValue(CONNECTION[facing.ordinal()], c);
	}

	private boolean canCableConnectFrom(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		if (state.getBlock() instanceof CableBlock) {
			return state.getBlock() == this;
		} else if (state.getBlock() instanceof ElectricBlock) {
			return true;
		} else if (!state.isAir()) {
			BlockEntity t = world.getBlockEntity(pos);
			return t instanceof EnergyHandler || FTBICConfig.ZAP_TO_FE_CONVERSION_RATE > 0D && t != null && t.getCapability(CapabilityEnergy.ENERGY, face).isPresent();
		}

		return false;
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		Level world = context.getLevel();
		BlockPos pos = context.getClickedPos();
		BlockState state = defaultBlockState();

		for (Direction direction : Direction.values()) {
			BlockPos p = pos.relative(direction);
			BlockState s = world.getBlockState(p);

			if (canCableConnectFrom(s, world, p, direction.getOpposite())) {
				state = state.setValue(CONNECTION[direction.ordinal()], true);
			}
		}

		return state.setValue(BlockStateProperties.WATERLOGGED, world.getFluidState(pos).getType() == Fluids.WATER);
	}

	@Override
	@Deprecated
	public void onPlace(BlockState state, Level level, BlockPos pos, BlockState state1, boolean b) {
		super.onPlace(state, level, pos, state1, b);

		if (!level.isClientSide() && !state.is(state1.getBlock())) {
			ElectricBlockEntity.electricNetworkUpdated(level, pos);
		}
	}

	@Override
	@Deprecated
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState state1, boolean b) {
		super.onRemove(state, level, pos, state1, b);

		if (!level.isClientSide() && !state.is(state1.getBlock())) {
			ElectricBlockEntity.electricNetworkUpdated(level, pos);
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> list, TooltipFlag flag) {
		list.add(new TranslatableComponent("ftbic.max_input", FTBICUtils.formatEnergy(tier.transferRate).append("/t").withStyle(ChatFormatting.GRAY)).withStyle(ChatFormatting.DARK_GRAY));
	}
}
