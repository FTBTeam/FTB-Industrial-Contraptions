package dev.ftb.mods.ftbic.block.entity.machine;

import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.block.entity.ElectricBlockEntity;
import dev.ftb.mods.ftbic.util.EnergyItemHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import java.util.Random;

public class ChargePadBlockEntity extends ElectricBlockEntity {
	public ChargePadBlockEntity(BlockPos pos, BlockState state) {
		super(FTBICElectricBlocks.CHARGE_PAD, pos, state);
	}

	@Override
	public void stepOn(ServerPlayer player) {
		if (energy > 0D) {
			IItemHandler handler = player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElse(null);

			if (handler != null) {
				for (int i = 0; i < handler.getSlots(); i++) {
					ItemStack stack = handler.getStackInSlot(i);

					if (stack.getItem() instanceof EnergyItemHandler energyItemHandler) {
                        if (!energyItemHandler.isCreativeEnergyItem()) {
							double e = energyItemHandler.insertEnergy(stack, energy, false);

							if (e > 0D) {
								energy -= e;
								active = true;
								setChanged();

								if (energy <= 0D) {
									break;
								}
							}
						}
					}
				}
			}
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void spawnActiveParticles(Level level, double x, double y, double z, BlockState state, Random r) {
		for (int i = 0; i < 5; i++) {
			level.addParticle(DustParticleOptions.REDSTONE, x + r.nextFloat(), y + 1F + r.nextFloat() * 2F, z + r.nextFloat(), 0D, 0D, 0D);
		}
	}
}