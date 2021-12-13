package dev.ftb.mods.ftbic.block.entity.generator;

import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.block.FTBICBlocks;
import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.item.reactor.NuclearReactor;
import dev.ftb.mods.ftbic.item.reactor.ReactorItem;
import dev.ftb.mods.ftbic.screen.NuclearReactorMenu;
import dev.ftb.mods.ftbic.util.FTBICUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NuclearReactorBlockEntity extends GeneratorBlockEntity {
	public static final int[] OFFSET_X = {0, 0, -1, 1};
	public static final int[] OFFSET_Y = {-1, 1, 0, 0};

	public int timeUntilNextCycle;
	public final NuclearReactor reactor;

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
	}

	@Override
	public void readData(CompoundTag tag) {
		super.readData(tag);
		timeUntilNextCycle = tag.getInt("TimeUntilNextCycle");
		reactor.paused = tag.getBoolean("Paused");
		reactor.chambers = Mth.clamp(tag.getByte("Chambers"), 0, 6);
		reactor.energyOutput = tag.getDouble("EnergyOutput");
		reactor.heat = tag.getInt("Heat");
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
		return 5;
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

		if (h >= 1F) {
			if (!level.isClientSide()) {
				boolean b = level.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS);
				level.getGameRules().getRule(GameRules.RULE_DOBLOCKDROPS).set(false, level.getServer());
				level.removeBlock(worldPosition, false);
				// replace with nuke
				level.explode(null, worldPosition.getX() + 0.5D, worldPosition.getY() + 0.5D, worldPosition.getZ() + 0.5D, (float) reactor.explosionStrength, Explosion.BlockInteraction.DESTROY);
				level.getGameRules().getRule(GameRules.RULE_DOBLOCKDROPS).set(b, level.getServer());
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
