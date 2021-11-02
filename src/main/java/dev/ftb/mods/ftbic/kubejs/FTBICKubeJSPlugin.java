package dev.ftb.mods.ftbic.kubejs;

import dev.ftb.mods.ftbic.recipe.FTBICRecipes;
import dev.latvian.kubejs.KubeJSPlugin;
import dev.latvian.kubejs.recipe.RegisterRecipeHandlersEvent;

public class FTBICKubeJSPlugin extends KubeJSPlugin {
	@Override
	public void addRecipes(RegisterRecipeHandlersEvent event) {
		event.register(FTBICRecipes.MACERATING.get().getRegistryName(), MachineRecipeJS::new);
		event.register(FTBICRecipes.EXTRACTING.get().getRegistryName(), MachineRecipeJS::new);
		event.register(FTBICRecipes.COMPRESSING.get().getRegistryName(), MachineRecipeJS::new);
		event.register(FTBICRecipes.CANNING.get().getRegistryName(), MachineRecipeJS::new);
	}
}