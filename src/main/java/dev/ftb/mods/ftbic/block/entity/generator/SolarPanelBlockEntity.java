package dev.ftb.mods.ftbic.block.entity.generator;

import net.minecraft.world.level.block.entity.BlockEntityType;

public class SolarPanelBlockEntity extends GeneratorBlockEntity {
	public double solarOutput;

	public SolarPanelBlockEntity(BlockEntityType<?> type) {
		super(type, 0, 0);
	}

	@Override
	public void initProperties() {
		super.initProperties();
		solarOutput = 0D;
	}

	@Override
	public void handleGeneration() {
		if (energy < energyCapacity && level.isDay() && level.canSeeSky(worldPosition.above())) {
			energy += Math.min(energyCapacity - energy, solarOutput);

			if (energy >= energyCapacity) {
				setChanged();
			}
		}
	}
}
