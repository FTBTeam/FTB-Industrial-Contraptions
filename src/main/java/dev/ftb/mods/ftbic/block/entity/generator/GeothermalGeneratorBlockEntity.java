package dev.ftb.mods.ftbic.block.entity.generator;

import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.screen.GeothermalGeneratorMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.BlockHitResult;

public class GeothermalGeneratorBlockEntity extends GeneratorBlockEntity {
	public int fluidAmount = 0;

	public GeothermalGeneratorBlockEntity(BlockPos pos, BlockState state) {
		super(FTBICElectricBlocks.GEOTHERMAL_GENERATOR, pos, state);
	}

	@Override
	public AbstractContainerMenu createMenu(int id, Inventory inv) {
		return new GeothermalGeneratorMenu(id, inv, this);
	}

	public int getTankCapacity() {
		return FTBICConfig.MACHINES.GEOTHERMAL_GENERATOR_TANK_SIZE.get();
	}

	@Override
	protected void saveAdditional(ValueOutput output) {
		super.saveAdditional(output);
		if (fluidAmount > 0) output.putInt("FluidAmount", fluidAmount);
	}

	@Override
	protected void loadAdditional(ValueInput input) {
		super.loadAdditional(input);
		fluidAmount = input.getIntOr("FluidAmount", 0);
	}

	@Override
	public void handleGeneration() {
		if (level == null) return;

		int tankCap = getTankCapacity();

		// Auto-drain lava buckets from input slot 0 if there's room.
		if (inputItems.length > 0 && !inputItems[0].isEmpty()
				&& inputItems[0].getItem() == Items.LAVA_BUCKET
				&& fluidAmount + 1000 <= tankCap) {
			fluidAmount += 1000;
			inputItems[0] = ItemStack.EMPTY;
			if (outputItems.length > 0) {
				if (outputItems[0].isEmpty()) {
					outputItems[0] = new ItemStack(Items.BUCKET);
				} else if (outputItems[0].getItem() == Items.BUCKET
						&& outputItems[0].getCount() < outputItems[0].getMaxStackSize()) {
					outputItems[0].grow(1);
				}
			}
			setChanged();
		}

		// Burn 1 mB / tick into maxEnergyOutput zaps.
		if (fluidAmount > 0 && energy < energyCapacity) {
			double produced = Math.min(energyCapacity - energy, maxEnergyOutput);
			if (produced > 0D) {
				energy += produced;
				fluidAmount--;
				active = true;
				if (fluidAmount == 0 || fluidAmount % 100 == 0) setChanged();
			}
		}
	}

	@Override
	public InteractionResult rightClick(Player player, InteractionHand hand, BlockHitResult hit) {
		if (level == null || level.isClientSide()) return InteractionResult.SUCCESS;
		ItemStack held = player.getItemInHand(hand);
		if (held.getItem() == Items.LAVA_BUCKET && fluidAmount + 1000 <= getTankCapacity()) {
			fluidAmount += 1000;
			if (!player.isCreative()) {
				player.setItemInHand(hand, new ItemStack(Items.BUCKET));
			}
			level.playSound(null, worldPosition,
					SoundEvents.BUCKET_EMPTY_LAVA,
					SoundSource.BLOCKS, 1F, 1F);
			setChanged();
			return InteractionResult.SUCCESS;
		}
		return super.rightClick(player, hand, hit);
	}
}
