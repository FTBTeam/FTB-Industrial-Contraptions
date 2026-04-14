package dev.ftb.mods.ftbic.block.entity.generator;

import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Consumes lava to produce energy. Exposes a proper {@code ResourceHandler<FluidResource>} tank via
 * {@link dev.ftb.mods.ftbic.events.CapabilityRegistrar} so foreign pipes can fill the tank directly.
 * Lava buckets placed in input slot 0 are also auto-consumed and returned to output slot 0 as empty
 * buckets. Right-clicking the block with a lava bucket drains it straight into the tank.
 *
 * Each tick with fluid present, one mB of lava is consumed and {@code maxEnergyOutput} zaps are
 * produced (default 20 zaps/mB, so a full 8000 mB tank generates 160k zaps).
 */
public class GeothermalGeneratorBlockEntity extends GeneratorBlockEntity {
	public int fluidAmount = 0;

	public GeothermalGeneratorBlockEntity(BlockPos pos, BlockState state) {
		super(FTBICElectricBlocks.GEOTHERMAL_GENERATOR, pos, state);
	}

	@Override
	public net.minecraft.world.inventory.AbstractContainerMenu createMenu(int id, net.minecraft.world.entity.player.Inventory inv) {
		return new dev.ftb.mods.ftbic.screen.GeothermalGeneratorMenu(id, inv, this);
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

	/**
	 * Right-click handler — drains a lava bucket in the player's hand straight into the tank when
	 * there's room; otherwise falls through to the menu-opening default.
	 */
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
					net.minecraft.sounds.SoundEvents.BUCKET_EMPTY_LAVA,
					net.minecraft.sounds.SoundSource.BLOCKS, 1F, 1F);
			setChanged();
			return InteractionResult.SUCCESS;
		}
		return super.rightClick(player, hand, hit);
	}
}
