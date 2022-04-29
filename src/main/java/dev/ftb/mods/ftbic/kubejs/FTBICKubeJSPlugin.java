package dev.ftb.mods.ftbic.kubejs;

import dev.ftb.mods.ftbic.recipe.FTBICRecipes;
import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.recipe.RegisterRecipeHandlersEvent;

public class FTBICKubeJSPlugin extends KubeJSPlugin {
	@Override
	public void addRecipes(RegisterRecipeHandlersEvent event) {
		event.register(FTBICRecipes.SMELTING.get().getRegistryName(), MachineRecipeJS::new);
		event.register(FTBICRecipes.MACERATING.get().getRegistryName(), MachineRecipeJS::new);
		event.register(FTBICRecipes.SEPARATING.get().getRegistryName(), MachineRecipeJS::new);
		event.register(FTBICRecipes.COMPRESSING.get().getRegistryName(), MachineRecipeJS::new);
		event.register(FTBICRecipes.REPROCESSING.get().getRegistryName(), MachineRecipeJS::new);
		event.register(FTBICRecipes.CANNING.get().getRegistryName(), MachineRecipeJS::new);
		event.register(FTBICRecipes.ROLLING.get().getRegistryName(), MachineRecipeJS::new);
		event.register(FTBICRecipes.EXTRUDING.get().getRegistryName(), MachineRecipeJS::new);
	}
}
