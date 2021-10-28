package dev.ftb.mods.ftbic.block.entity.generator;

import net.minecraft.world.level.block.entity.BlockEntityType;

public class SolarPanelBlockEntity extends GeneratorBlockEntity {
	public SolarPanelBlockEntity(BlockEntityType<?> type) {
		super(type);
	}

	public int getGeneration() {
		return 1;
	}

	@Override
	public void tick() {
		if (energy < energyCapacity && !level.isClientSide() && level.isDay() && level.canSeeSky(worldPosition.above())) {
			energy += Math.min(energyCapacity - energy, getGeneration());

			if (energy == energyCapacity) {
				setChanged();
			}
		}

		super.tick();
	}
}
