package dev.ftb.mods.ftbic.block;

import dev.ftb.mods.ftbic.block.entity.ElectricBlockEntity;
import dev.ftb.mods.ftbic.item.FTBICItems;
import dev.ftb.mods.ftbic.util.FTBICUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolType;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;

public class ElectricBlock extends Block implements SprayPaintable {
	public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

	public final ElectricBlockInstance electricBlockInstance;

	public ElectricBlock(ElectricBlockInstance m) {
		super(Properties.of(Material.METAL).strength(3.5F).sound(SoundType.METAL).harvestTool(ToolType.PICKAXE).requiresCorrectToolForDrops());
		electricBlockInstance = m;
		BlockState state = getStateDefinition().any().setValue(SprayPaintable.DARK, false);

		if (electricBlockInstance.facingProperty != null) {
			state = state.setValue(electricBlockInstance.facingProperty, Direction.SOUTH);
		}

		if (electricBlockInstance.canBeActive) {
			state = state.setValue(ACTIVE, false);
		}

		registerDefaultState(state);
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Nullable
	@Override
	public BlockEntity createTileEntity(BlockState state, BlockGetter level) {
		return electricBlockInstance.blockEntity.get().create();
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(SprayPaintable.DARK);

		if (ElectricBlockInstance.current.facingProperty != null) {
			builder.add(ElectricBlockInstance.current.facingProperty);
		}

		if (ElectricBlockInstance.current.canBeActive) {
			builder.add(ACTIVE);
		}
	}

	@Override
	@Deprecated
	public BlockState rotate(BlockState state, Rotation rotation) {
		return electricBlockInstance.facingProperty == null ? state : state.setValue(electricBlockInstance.facingProperty, rotation.rotate(state.getValue(electricBlockInstance.facingProperty)));
	}

	@Override
	@Deprecated
	public BlockState mirror(BlockState state, Mirror mirror) {
		return electricBlockInstance.facingProperty == null ? state : state.rotate(mirror.getRotation(state.getValue(electricBlockInstance.facingProperty)));
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext arg) {
		if (electricBlockInstance.facingProperty == null) {
			return defaultBlockState();
		} else if (electricBlockInstance.facingProperty == BlockStateProperties.HORIZONTAL_FACING) {
			return defaultBlockState().setValue(electricBlockInstance.facingProperty, arg.getHorizontalDirection().getOpposite());
		} else {
			return defaultBlockState().setValue(electricBlockInstance.facingProperty, arg.getNearestLookingDirection().getOpposite());
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState state, Level level, BlockPos pos, Random r) {
		if (electricBlockInstance.canBurn) {
			BlockEntity entity = level.getBlockEntity(pos);

			if (entity instanceof ElectricBlockEntity) {
				ElectricBlockEntity electricBlockEntity = (ElectricBlockEntity) entity;

				if (electricBlockEntity.isBurnt()) {
					double x = pos.getX();
					double y = pos.getY();
					double z = pos.getZ();

					if (r.nextInt(10) == 0) {
						level.playLocalSound(x + 0.5D, y + 0.5D, z + 0.5D, SoundEvents.FIRE_AMBIENT, SoundSource.BLOCKS, 1.0F + r.nextFloat(), r.nextFloat() * 0.7F + 0.3F, false);
					}

					for (int i = 0; i < 5; i++) {
						level.addParticle(ParticleTypes.SMOKE, x + r.nextFloat(), y + 1D, z + r.nextFloat(), 0D, 0D, 0D);

						level.addParticle(ParticleTypes.SMOKE, x, y + 0.05D + r.nextFloat(), z + r.nextFloat(), 0D, 0D, 0D);
						level.addParticle(ParticleTypes.SMOKE, x + 1D, y + 0.05D + r.nextFloat(), z + r.nextFloat(), 0D, 0D, 0D);
						level.addParticle(ParticleTypes.SMOKE, x + r.nextFloat(), y + 0.05D + r.nextFloat(), z, 0D, 0D, 0D);
						level.addParticle(ParticleTypes.SMOKE, x + r.nextFloat(), y + 0.05D + r.nextFloat(), z + 1D, 0D, 0D, 0D);

						if (r.nextInt(5) == 0) {
							level.addParticle(ParticleTypes.FLAME, x, y + 0.05D + r.nextFloat(), z + r.nextFloat(), 0D, 0D, 0D);
						}

						if (r.nextInt(5) == 0) {
							level.addParticle(ParticleTypes.FLAME, x + 1D, y + 0.05D + r.nextFloat(), z + r.nextFloat(), 0D, 0D, 0D);
						}

						if (r.nextInt(5) == 0) {
							level.addParticle(ParticleTypes.FLAME, x + r.nextFloat(), y + 0.05D + r.nextFloat(), z, 0D, 0D, 0D);
						}

						if (r.nextInt(5) == 0) {
							level.addParticle(ParticleTypes.FLAME, x + r.nextFloat(), y + 0.05D + r.nextFloat(), z + 1D, 0D, 0D, 0D);
						}
					}

					level.addParticle(ParticleTypes.FLAME, x + r.nextFloat(), y + 1.1D, z + r.nextFloat(), 0D, 0D, 0D);
					level.addParticle(ParticleTypes.LARGE_SMOKE, x + 0.5D, y + 1D, z + 0.5D, 0D, 0D, 0D);
				}
			}
		}
	}

	@Override
	@Deprecated
	public void onPlace(BlockState state, Level level, BlockPos pos, BlockState state1, boolean b) {
		super.onPlace(state, level, pos, state1, b);

		if (!level.isClientSide() && !state.getBlock().is(state1.getBlock())) {
			ElectricBlockEntity.electricNetworkUpdated(level, pos);
		}
	}

	@Override
	@Deprecated
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState state1, boolean b) {
		if (!level.isClientSide() && !state.getBlock().is(state1.getBlock())) {
			ElectricBlockEntity.electricNetworkUpdated(level, pos);

			BlockEntity entity = level.getBlockEntity(pos);

			if (entity instanceof ElectricBlockEntity) {
				((ElectricBlockEntity) entity).onBroken(level, pos);
			}
		}

		super.onRemove(state, level, pos, state1, b);
	}

	@Override
	@Deprecated
	public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos pos1, boolean b) {
		super.neighborChanged(state, level, pos, block, pos1, b);

		if (!level.isClientSide() && !level.getBlockState(pos1).getBlock().is(block)) {
			ElectricBlockEntity.electricNetworkUpdated(level, pos1);
		}
	}

	@Override
	@Deprecated
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		BlockEntity entity = level.getBlockEntity(pos);

		if (entity instanceof ElectricBlockEntity) {
			ElectricBlockEntity electricBlockEntity = (ElectricBlockEntity) entity;

			if (electricBlockEntity.isBurnt()) {
				if (player.getItemInHand(hand).getItem() == FTBICItems.FUSE.item.get()) {
					electricBlockEntity.setBurnt(false);
					level.playSound(player, pos, SoundEvents.STONE_BUTTON_CLICK_ON, SoundSource.BLOCKS, 0.3F, 0.6F);

					if (!level.isClientSide()) {
						player.getItemInHand(hand).shrink(1);
					}

					return InteractionResult.sidedSuccess(level.isClientSide());
				} else if (!level.isClientSide()) {
					player.sendMessage(new TranslatableComponent("ftbic.fuse_info"), Util.NIL_UUID);
				}

				return InteractionResult.SUCCESS;
			}

			return electricBlockEntity.rightClick(player, hand, hit);
		}

		return InteractionResult.PASS;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> list, TooltipFlag flag) {
		if (electricBlockInstance.energyOutput > 0D) {
			list.add(new TranslatableComponent("ftbic.energy_output", FTBICUtils.formatEnergy(electricBlockInstance.energyOutput).append("/t").withStyle(ChatFormatting.GRAY)).withStyle(ChatFormatting.DARK_GRAY));
		}

		if (electricBlockInstance.maxEnergyOutput > 0D) {
			list.add(new TranslatableComponent("ftbic.max_energy_output", FTBICUtils.formatEnergy(electricBlockInstance.maxEnergyOutput).append("/t").withStyle(ChatFormatting.GRAY)).withStyle(ChatFormatting.DARK_GRAY));
		}

		if (electricBlockInstance.energyUsage > 0D) {
			list.add(new TranslatableComponent("ftbic.energy_usage", FTBICUtils.formatEnergy(electricBlockInstance.energyUsage).append("/t").withStyle(ChatFormatting.GRAY)).withStyle(ChatFormatting.DARK_GRAY));
		}

		if (electricBlockInstance.maxInput > 0D) {
			list.add(new TranslatableComponent("ftbic.max_input", FTBICUtils.formatEnergy(electricBlockInstance.maxInput).append("/t").withStyle(ChatFormatting.GRAY)).withStyle(ChatFormatting.DARK_GRAY));
		}
	}
}
