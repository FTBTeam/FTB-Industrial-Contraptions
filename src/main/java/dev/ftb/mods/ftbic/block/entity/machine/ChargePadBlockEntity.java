package dev.ftb.mods.ftbic.block.entity.machine;

import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.util.EnergyItemHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class ChargePadBlockEntity extends ElectricBlockEntityRef {
	public ChargePadBlockEntity(BlockPos pos, BlockState state) {
		super(FTBICElectricBlocks.CHARGE_PAD, pos, state);
	}

	@Override
	public void stepOn(ServerPlayer player) {
		if (energy <= 0D) return;
		Inventory inv = player.getInventory();
		for (int i = 0; i < inv.getContainerSize(); i++) {
			ItemStack stack = inv.getItem(i);
			if (!(stack.getItem() instanceof EnergyItemHandler eh)) continue;
			if (eh.isCreativeEnergyItem()) continue;
			double accepted = eh.insertEnergy(stack, energy, false);
			if (accepted > 0D) {
				energy -= accepted;
				active = true;
				setChanged();
				if (energy <= 0D) return;
			}
		}
	}

	@Override
	public void spawnActiveParticles(Level level, double x, double y, double z, BlockState state, RandomSource r) {
		for (int i = 0; i < 5; i++) {
			level.addParticle(DustParticleOptions.REDSTONE,
					x + r.nextFloat(),
					y + 1F + r.nextFloat() * 2F,
					z + r.nextFloat(),
					0D, 0D, 0D);
		}
	}
}
