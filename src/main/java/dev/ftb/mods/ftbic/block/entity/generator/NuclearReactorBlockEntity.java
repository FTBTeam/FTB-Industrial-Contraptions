package dev.ftb.mods.ftbic.block.entity.generator;

import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.block.FTBICBlocks;
import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.block.NuclearReactorChamberBlock;
import dev.ftb.mods.ftbic.item.reactor.NuclearReactor;
import dev.ftb.mods.ftbic.item.reactor.ReactorItem;
import dev.ftb.mods.ftbic.screen.NuclearReactorMenu;
import dev.ftb.mods.ftbic.util.FTBICUtils;
import dev.ftb.mods.ftbic.util.NuclearExplosion;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class NuclearReactorBlockEntity extends GeneratorBlockEntity {
	public static final int[] OFFSET_X = {0, 0, -1, 1};
	public static final int[] OFFSET_Y = {-1, 1, 0, 0};

	public int timeUntilNextCycle;
	public final NuclearReactor reactor;
	public int simulateTicks = 0;
	public boolean exploding;

	public NuclearReactorBlockEntity() {
		super(FTBICElectricBlocks.NUCLEAR_REACTOR);
		reactor = new NuclearReactor(inputItems);
	}

	@Override
	public void initProperties() {
		super.initProperties();
		maxEnergyOutputTransfer = FTBICConfig.IV_TRANSFER_RATE;
	}

	@Override
	public void writeData(CompoundTag tag) {
		super.writeData(tag);
		tag.putInt("TimeUntilNextCycle", timeUntilNextCycle);
		tag.putBoolean("Paused", reactor.paused);
		tag.putByte("Chambers", (byte) reactor.chambers);
		tag.putDouble("EnergyOutput", reactor.energyOutput);
		tag.putInt("Heat", reactor.heat);

		if (simulateTicks > 0) {
			tag.putInt("SimulateTicks", simulateTicks);
		}
	}

	@Override
	public void readData(CompoundTag tag) {
		super.readData(tag);
		timeUntilNextCycle = tag.getInt("TimeUntilNextCycle");
		reactor.paused = tag.getBoolean("Paused");
		reactor.chambers = Mth.clamp(tag.getByte("Chambers"), 0, 6);
		reactor.energyOutput = tag.getDouble("EnergyOutput");
		reactor.heat = tag.getInt("Heat");
		simulateTicks = tag.getInt("SimulateTicks");
	}

	@Override
	public void writeNetData(CompoundTag tag) {
		super.writeNetData(tag);

		if (exploding) {
			tag.putBoolean("Exploding", true);
		}
	}

	@Override
	public void readNetData(CompoundTag tag) {
		super.readNetData(tag);
		exploding = tag.getBoolean("Exploding");
	}

	@Override
	public int getSlotLimit(int slot) {
		return 1;
	}

	@Override
	public boolean isItemValid(int slot, @NotNull ItemStack stack) {
		return (slot % 9) < (reactor.chambers + 3) && stack.getItem() instanceof ReactorItem;
	}

	@Override
	public boolean savePlacer() {
		return true;
	}

	@Override
	public int getCount() {
		return 6;
	}

	@Override
	public int get(int id) {
		switch (id) {
			case 1:
				// getEnergyOutput()
				return FTBICUtils.packInt(Mth.ceil(reactor.energyOutput), 150000);
			case 2:
				// getHeat()
				return FTBICUtils.packInt(reactor.heat, 101800);
			case 3:
				// getChambers()
				return reactor.chambers;
			case 4:
				// isPaused()
				return reactor.paused ? 1 : 0;
			case 5:
				// getMaxHeat()
				return FTBICUtils.packInt(reactor.maxHeat, 101800);
			default:
				return super.get(id);
		}
	}

	@Override
	public InteractionResult rightClick(Player player, InteractionHand hand, BlockHitResult hit) {
		if (!level.isClientSide()) {
			openMenu((ServerPlayer) player, (id, inventory) -> new NuclearReactorMenu(id, inventory, this, this));
		}

		return InteractionResult.SUCCESS;
	}

	@Override
	public void handleGeneration() {
		timeUntilNextCycle--;

		if (timeUntilNextCycle <= 0) {
			timeUntilNextCycle = 20;
			handleReactor();
		}

		if (simulateTicks > 0) {
			handleReactor();
			simulateTicks--;
		}

		if (reactor.energyOutput > 0) {
			active = true;
		}
	}

	public void handleReactor() {
		double peo = reactor.energyOutput;
		int ph = reactor.heat;

		reactor.tick();

		if (peo != reactor.energyOutput || ph != reactor.heat) {
			setChanged();
		}

		float h = reactor.heat / (float) reactor.maxHeat;

		if (h >= 1F && !level.isClientSide()) {
			if (!exploding) {
				exploding = true;
				Arrays.fill(inputItems, ItemStack.EMPTY);
				setChanged();

				level.setBlock(worldPosition, FTBICBlocks.ACTIVE_NUKE.get().defaultBlockState(), 3);

				for (Direction direction : FTBICUtils.DIRECTIONS) {
					if (level.getBlockState(worldPosition.relative(direction)).getBlock() instanceof NuclearReactorChamberBlock) {
						level.setBlock(worldPosition.relative(direction), FTBICBlocks.ACTIVE_NUKE.get().defaultBlockState(), 3);
					}
				}

				NuclearExplosion.builder((ServerLevel) level, worldPosition, reactor.explosionRadius, placerId, placerName)
						.preExplosion(() -> {
							level.getServer().getPlayerList().broadcastMessage(new TranslatableComponent("block.ftbic.nuclear_reactor.broadcast", placerName), ChatType.SYSTEM, Util.NIL_UUID);
							level.removeBlock(worldPosition, false);
						})
						.create()
				;
			}
		}
		// other reactor big bads
	}

	@Override
	public void neighborChanged(BlockPos pos1, Block block1) {
		super.neighborChanged(pos1, block1);
		updateChambers();
	}

	@Override
	public void onPlacedBy(@Nullable LivingEntity entity, ItemStack stack) {
		super.onPlacedBy(entity, stack);

		if (!level.isClientSide()) {
			updateChambers();
		}
	}

	public void updateChambers() {
		int c = reactor.chambers;
		reactor.chambers = 0;

		for (Direction direction : FTBICUtils.DIRECTIONS) {
			BlockState state = level.getBlockState(worldPosition.relative(direction));

			if (state.getBlock() == FTBICBlocks.NUCLEAR_REACTOR_CHAMBER.get() && state.getValue(BlockStateProperties.FACING) == direction) {
				reactor.chambers++;
			}
		}

		if (c != reactor.chambers) {
			setChanged();
		}
	}
}
