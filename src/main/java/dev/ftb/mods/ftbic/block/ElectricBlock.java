package dev.ftb.mods.ftbic.block;

import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.block.entity.ElectricBlockEntity;
import dev.ftb.mods.ftbic.item.FTBICItems;
import dev.ftb.mods.ftbic.util.FTBICUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ElectricBlock extends Block implements EntityBlock, SprayPaintable {
	public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

	public final ElectricBlockInstance electricBlockInstance;

	public ElectricBlock(ElectricBlockInstance m) {
		super(Properties.of(Material.METAL).strength(3.5F).sound(SoundType.METAL).requiresCorrectToolForDrops());
		electricBlockInstance = m;
		BlockState state = getStateDefinition().any().setValue(SprayPaintable.DARK, false);

		if (m.facingProperty != null) {
			state = state.setValue(electricBlockInstance.facingProperty, Direction.SOUTH);
		}

		if (electricBlockInstance.canBeActive) {
			state = state.setValue(ACTIVE, false);
		}

		registerDefaultState(state);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return electricBlockInstance.blockEntity.get().create(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return electricBlockInstance.tickClientSide || !level.isClientSide() ? ElectricBlockEntity::ticker : null;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(SprayPaintable.DARK);

		// Needs to call static instance because createBlockStateDefinition is called before constructor

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
	public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource r) {
		boolean active = electricBlockInstance.canBeActive && state.getValue(ACTIVE);
		if (active || electricBlockInstance.canBurn) {
			BlockEntity entity = level.getBlockEntity(pos);

			if (entity instanceof ElectricBlockEntity electricBlockEntity) {
				double x = pos.getX();
				double y = pos.getY();
				double z = pos.getZ();

				if (electricBlockEntity.isBurnt()) {
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
				} else if (active) {
					electricBlockEntity.spawnActiveParticles(level, x, y, z, state, r);
				}
			}
		}
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
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack) {
		super.setPlacedBy(level, pos, state, entity, stack);

		BlockEntity blockEntity = level.getBlockEntity(pos);

		if (blockEntity instanceof ElectricBlockEntity) {
			((ElectricBlockEntity) blockEntity).onPlacedBy(entity, stack);
		}
	}

	@Override
	@Deprecated
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState state1, boolean b) {
		if (!level.isClientSide() && !state.is(state1.getBlock())) {
			ElectricBlockEntity.electricNetworkUpdated(level, pos);

			BlockEntity entity = level.getBlockEntity(pos);

			if (entity instanceof ElectricBlockEntity) {
				((ElectricBlockEntity) entity).onBroken(level, pos);
			}

			level.updateNeighbourForOutputSignal(pos, this);
		}

		super.onRemove(state, level, pos, state1, b);
	}

	@Override
	@Deprecated
	public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos pos1, boolean b) {
		super.neighborChanged(state, level, pos, block, pos1, b);

		if (!level.isClientSide()) {
			BlockEntity entity = level.getBlockEntity(pos);

			if (entity instanceof ElectricBlockEntity) {
				((ElectricBlockEntity) entity).neighborChanged(pos1, block);
			}
		}
	}

	@Override
	@Deprecated
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		BlockEntity entity = level.getBlockEntity(pos);

		if (entity instanceof ElectricBlockEntity electricBlockEntity) {
			if (electricBlockEntity.isBurnt()) {
				if (player.getItemInHand(hand).getItem() == FTBICItems.FUSE.item.get()) {
					electricBlockEntity.setBurnt(false);
					level.playSound(player, pos, SoundEvents.STONE_BUTTON_CLICK_ON, SoundSource.BLOCKS, 0.3F, 0.6F);

					if (!level.isClientSide() && !player.isCreative()) {
						player.getItemInHand(hand).shrink(1);
					}

					return InteractionResult.sidedSuccess(level.isClientSide());
				} else if (!level.isClientSide()) {
					player.displayClientMessage(Component.translatable("ftbic.fuse_info"), true);
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
		if (electricBlockInstance.wip) {
			list.add(Component.literal("WIP!").withStyle(ChatFormatting.RED));
		}

		if (electricBlockInstance.maxEnergyOutput > 0D) {
			list.add(Component.translatable("ftbic.energy_output", FTBICUtils.formatEnergy(electricBlockInstance.maxEnergyOutput).append("/t").withStyle(ChatFormatting.GRAY)).withStyle(ChatFormatting.DARK_GRAY));
			var feRatio = FTBICConfig.ENERGY.ZAP_TO_FE_CONVERSION_RATE.get();
			if (feRatio > 0D) {
				list.add(Component.translatable("ftbic.zap_to_fe_conversion", FTBICConfig.ENERGY_FORMAT, feRatio).withStyle(ChatFormatting.DARK_GRAY));
			}
		}

		if (electricBlockInstance.energyUsage > 0D) {
			if (electricBlockInstance.energyUsageIsPerTick) {
				list.add(Component.translatable("ftbic.energy_usage", FTBICUtils.formatEnergy(electricBlockInstance.energyUsage).append("/t").withStyle(ChatFormatting.GRAY)).withStyle(ChatFormatting.DARK_GRAY));
			} else {
				list.add(Component.translatable("ftbic.energy_usage", FTBICUtils.formatEnergy(electricBlockInstance.energyUsage).withStyle(ChatFormatting.GRAY)).withStyle(ChatFormatting.DARK_GRAY));
			}
		}

		if (electricBlockInstance.maxEnergyInput > 0D) {
			list.add(Component.translatable("ftbic.max_input", FTBICUtils.formatEnergy(electricBlockInstance.maxEnergyInput).append("/t").withStyle(ChatFormatting.GRAY)).withStyle(ChatFormatting.DARK_GRAY));
		}

		if (electricBlockInstance.energyCapacity > 0D && Screen.hasShiftDown()) {
			list.add(Component.translatable("ftbic.energy_capacity", FTBICUtils.formatEnergy(electricBlockInstance.energyCapacity).withStyle(ChatFormatting.GRAY)).withStyle(ChatFormatting.DARK_GRAY));
		}

		if (stack.hasTag() && stack.getTag().contains("BlockEntityTag", 10)) {
			CompoundTag tag = stack.getTag().getCompound("BlockEntityTag").copy();
			tag.remove("x");
			tag.remove("y");
			tag.remove("z");
			tag.remove("id");
			tag.remove("Inventory");
			tag.remove("Upgrades");
			tag.remove("Battery");
			tag.remove("ChargeBattery");
			tag.remove("PlacerId");
			tag.remove("PlacerName");

			for (String key : tag.getAllKeys()) {
				list.add(Component.literal("- " + key + ": " + tag.get(key)).withStyle(ChatFormatting.DARK_GRAY));
			}
		}
	}

	@Override
	public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
		if (entity instanceof ServerPlayer) {
			BlockEntity blockEntity = level.getBlockEntity(pos);

			if (blockEntity instanceof ElectricBlockEntity) {
				((ElectricBlockEntity) blockEntity).stepOn((ServerPlayer) entity);
			}
		}
	}

	@Override
	@Deprecated
	public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
		if (!level.isClientSide()) {
			BlockEntity entity = level.getBlockEntity(pos);

			if (entity instanceof ElectricBlockEntity) {
				return ((ElectricBlockEntity) entity).getRedstoneOutputSignalEnergyStorage();
			}
		}

		return 0;
	}

	@Override
	@Deprecated
	public boolean hasAnalogOutputSignal(BlockState state) {
		return true;
	}
}
