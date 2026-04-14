package dev.ftb.mods.ftbic.datagen;

import dev.ftb.mods.ftbic.FTBIC;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = FTBIC.MOD_ID)
public final class FTBICDataGen {

	@SubscribeEvent
	public static void gatherClientData(GatherDataEvent.Client event) {
		event.createProvider(FTBICModelProvider::new);
		event.createProvider((PackOutput o, CompletableFuture<HolderLookup.Provider> l) ->
				new FTBICBlockTagsProvider(o, l));
		event.createProvider((PackOutput o, CompletableFuture<HolderLookup.Provider> l) ->
				new FTBICItemTagsProvider(o, l));
		event.createProvider((PackOutput o, CompletableFuture<HolderLookup.Provider> l) ->
				new LootTableProvider(o, Set.<ResourceKey<LootTable>>of(),
						List.of(new LootTableProvider.SubProviderEntry(
								FTBICBlockLootProvider::new, LootContextParamSets.BLOCK)),
						l));
	}

	private FTBICDataGen() {}
}
