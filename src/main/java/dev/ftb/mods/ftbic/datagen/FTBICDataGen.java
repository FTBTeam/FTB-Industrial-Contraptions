package dev.ftb.mods.ftbic.datagen;

import dev.ftb.mods.ftbic.FTBIC;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = FTBIC.MOD_ID)
public final class FTBICDataGen {

    @SubscribeEvent
    public static void gatherClientData(GatherDataEvent.Client event) {
        event.createDatapackRegistryObjects(FTBICWorldgen.registries());
        event.createProvider(FTBICModelProvider::new);
        event.createProvider(FTBICEquipmentProvider::new);
        event.createProvider(FTBICSoundProvider::new);
        event.createProvider(FTBICTextureMetadataProvider::new);
        event.createProvider(FTBICContexProvider::new);
        event.createProvider(FTBICCompatRecipeProvider::new);
        event.createProvider(FTBICRecipeProvider.Runner::new);
        event.createProvider(FTBICLanguageProvider::new);
        event.createProvider(FTBICBlockTagsProvider::new);
        event.createProvider(FTBICItemTagsProvider::new);
        event.createProvider(FTBICLootTableProvider::new);
    }

    private FTBICDataGen() {}
}
