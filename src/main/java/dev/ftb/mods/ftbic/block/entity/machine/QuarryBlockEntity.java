package dev.ftb.mods.ftbic.block.entity.machine;

import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.block.FTBICBlocks;
import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.screen.QuarryMenu;
import dev.ftb.mods.ftbic.util.FTBICUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class QuarryBlockEntity extends DiggingBaseBlockEntity {
	private static final Predicate<ItemEntity> ITEM_ENTITY_PREDICATE = entity -> true;
	private static final float[] LASER_COLOR = {1F, 0.1F, 0.1F};

	public QuarryBlockEntity(BlockPos pos, BlockState state) {
		super(FTBICElectricBlocks.QUARRY, pos, state);
	}

	@Override
	public void initProperties() {
		super.initProperties();
		diggingMineTicks = FTBICConfig.MACHINES.QUARRY_MINE_TICKS.get();
		diggingMoveTicks = FTBICConfig.MACHINES.QUARRY_MOVE_TICKS.get();
	}

	@Override
	public boolean isValidBlock(BlockState state, BlockPos pos) {
		return state.getMaterial().blocksMotion() && state.getDestroySpeed(level, pos) >= 0F;
	}

	@Override
	public void digBlock(BlockState state, BlockPos miningPos, double lx, double ly, double lz) {
		BlockEntity minedEntity = state.hasBlockEntity() ? level.getBlockEntity(miningPos) : null;
		LootContext.Builder lootContext = new LootContext.Builder((ServerLevel) level)
				.withRandom(level.random)
				.withParameter(LootContextParams.ORIGIN, new Vec3(lx, ly, lz))
				.withParameter(LootContextParams.TOOL, new ItemStack(Items.NETHERITE_PICKAXE))
				.withParameter(LootContextParams.BLOCK_STATE, state)
				.withOptionalParameter(LootContextParams.BLOCK_ENTITY, minedEntity);

		List<ItemStack> list = new ArrayList<>(state.getDrops(lootContext));

		level.removeBlock(miningPos, false);
		level.levelEvent(null, 2001, miningPos, Block.getId(state));

		AABB aabb = new AABB(lx - 0.7D, ly - 0.7D, lz - 0.7D, lx + 0.7D, ly + 2.7D, lz + 0.7D);
		List<ItemEntity> itemEntities = level.getEntitiesOfClass(ItemEntity.class, aabb, ITEM_ENTITY_PREDICATE);

		for (ItemEntity itemEntity : itemEntities) {
			list.add(itemEntity.getItem());
			itemEntity.kill();
		}

		if (!list.isEmpty()) {
			ejectOutputItems();

			for (ItemStack stack : list) {
				ItemStack stack1 = addOutput(stack);

				if (!stack1.isEmpty()) {
					Block.popResource(level, worldPosition.relative(getFacing(Direction.NORTH)), stack1);
					paused = true;
				}
			}

			ejectOutputItems();
		}

		for (Direction direction : FTBICUtils.DIRECTIONS) {
			if (direction != Direction.DOWN && level.getFluidState(miningPos.relative(direction)).getType() != Fluids.EMPTY) {
				BlockState replaceState = (FTBICConfig.MACHINES.QUARRY_REPLACE_FLUID_EXFLUID.get()) ? FTBICBlocks.EXFLUID.get().defaultBlockState() : Blocks.AIR.defaultBlockState();
				level.setBlock(miningPos, replaceState, 2);
				break;
			}
		}
	}

	@Override
	public InteractionResult rightClick(Player player, InteractionHand hand, BlockHitResult hit) {
		if (!level.isClientSide()) {
			if (player.isCrouching()) {
				paused = !paused;
				syncBlock();
			} else {
				openMenu((ServerPlayer) player, (id, inventory) -> new QuarryMenu(id, inventory, this));
			}
		}

		return InteractionResult.SUCCESS;
	}

	@Override
	public float[] getLaserColor() {
		return LASER_COLOR;
	}
}
