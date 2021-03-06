package dev.ftb.mods.ftbic.util;

import dev.ftb.mods.ftbic.recipe.MachineRecipe;

public class MachineProcessingResult {
	public static final MachineProcessingResult NONE = new MachineProcessingResult() {
		@Override
		public boolean exists() {
			return false;
		}
	};

	public final StackWithChance output;
	public final StackWithChance[] extra;
	public final double time;
	public final int[] consume;

	private MachineProcessingResult() {
		output = StackWithChance.EMPTY;
		extra = new StackWithChance[0];
		time = 1D;
		consume = new int[0];
	}

	public MachineProcessingResult(MachineRecipe recipe) {
		output = recipe.outputItems.get(0);

		if (recipe.outputItems.size() > 1) {
			extra = new StackWithChance[recipe.outputItems.size() - 1];

			for (int i = 1; i < recipe.outputItems.size(); i++) {
				extra[i - 1] = recipe.outputItems.get(i);
			}
		} else {
			extra = NONE.extra;
		}

		time = recipe.processingTime;
		consume = new int[recipe.inputItems.size()];
	}

	public boolean exists() {
		return true;
	}
}
