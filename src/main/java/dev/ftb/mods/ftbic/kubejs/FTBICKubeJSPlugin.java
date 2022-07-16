package dev.ftb.mods.ftbic.kubejs;

import dev.ftb.mods.ftbic.recipe.FTBICRecipes;
import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.recipe.RegisterRecipeHandlersEvent;

public class FTBICKubeJSPlugin extends KubeJSPlugin {
	@Override
	public void addRecipes(RegisterRecipeHandlersEvent event) {
		event.register(FTBICRecipes.SMELTING.getId(), MachineRecipeJS::new);
		event.register(FTBICRecipes.MACERATING.getId(), MachineRecipeJS::new);
		event.register(FTBICRecipes.SEPARATING.getId(), MachineRecipeJS::new);
		event.register(FTBICRecipes.COMPRESSING.getId(), MachineRecipeJS::new);
		event.register(FTBICRecipes.REPROCESSING.getId(), MachineRecipeJS::new);
		event.register(FTBICRecipes.CANNING.getId(), MachineRecipeJS::new);
		event.register(FTBICRecipes.ROLLING.getId(), MachineRecipeJS::new);
		event.register(FTBICRecipes.EXTRUDING.getId(), MachineRecipeJS::new);
	}
}
