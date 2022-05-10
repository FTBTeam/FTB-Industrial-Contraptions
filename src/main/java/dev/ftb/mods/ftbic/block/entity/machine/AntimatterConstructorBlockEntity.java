package dev.ftb.mods.ftbic.block.entity.machine;

import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.block.entity.ElectricBlockEntity;
import dev.ftb.mods.ftbic.item.FTBICItems;
import dev.ftb.mods.ftbic.recipe.RecipeCache;
import dev.ftb.mods.ftbic.screen.AntimatterConstructorMenu;
import dev.ftb.mods.ftbic.screen.sync.SyncedData;
import dev.ftb.mods.ftbic.screen.sync.SyncedDataKey;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

public class AntimatterConstructorBlockEntity extends ElectricBlockEntity {
	public static final SyncedDataKey<Boolean> HAS_BOOST = new SyncedDataKey<>("has_boost", false);

	public double boost = 0D;
	private boolean hasBoost = false;

	public AntimatterConstructorBlockEntity(BlockPos pos, BlockState state) {
		super(FTBICElectricBlocks.ANTIMATTER_CONSTRUCTOR, pos, state);
	}

	@Override
	public void writeData(CompoundTag tag) {
		super.writeData(tag);
		tag.putDouble("Boost", boost);
	}

	@Override
	public void readData(CompoundTag tag) {
		super.readData(tag);
		boost = tag.getDouble("Boost");
	}

	@Override
	public void tick() {
		super.tick();

		if (energy >= energyCapacity) {
			if (outputItems[0].isEmpty()) {
				outputItems[0] = new ItemStack(FTBICItems.ANTIMATTER.item.get());
				energy -= energyCapacity;
				setChanged();
			} else if (outputItems[0].getCount() < outputItems[0].getMaxStackSize()) {
				outputItems[0].grow(1);
				energy -= energyCapacity;
				setChanged();
			}
		} else if (boost <= 0D) {
			boost = getBoost(inputItems[0]);

			if (boost > 0) {
				inputItems[0].shrink(1);

				if (inputItems[0].isEmpty()) {
					inputItems[0] = ItemStack.EMPTY;
				}

				setChanged();
			}
		}

		hasBoost = boost > 0D;
	}

	@Override
	public boolean isItemValid(int slot, @NotNull ItemStack stack) {
		return slot == 0 && getBoost(stack) > 0D;
	}

	private double getBoost(ItemStack item) {
		RecipeCache recipeCache = getRecipeCache();
		return recipeCache == null ? 0D : recipeCache.getAntimatterBoost(level, item);
	}

	@Override
	public double insertEnergy(double maxInsert, boolean simulate) {
		if (energy >= energyCapacity) {
			return 0D;
		}

		if (!simulate) {
			double boosted = Math.min(boost, maxInsert);
			boost -= boosted;
			maxInsert -= boosted;
			energy += boosted * FTBICConfig.MACHINES.ANTIMATTER_CONSTRUCTOR_BOOST.get() + maxInsert;
		}

		return maxInsert;
	}

	@Override
	public InteractionResult rightClick(Player player, InteractionHand hand, BlockHitResult hit) {
		if (!level.isClientSide()) {
			openMenu((ServerPlayer) player, (id, inventory) -> new AntimatterConstructorMenu(id, inventory, this));
		}

		return InteractionResult.SUCCESS;
	}

	@Override
	public void addSyncData(SyncedData data) {
		super.addSyncData(data);
		data.addBoolean(HAS_BOOST, () -> hasBoost);
	}
}