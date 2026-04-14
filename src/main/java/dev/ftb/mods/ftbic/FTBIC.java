package dev.ftb.mods.ftbic;

import com.mojang.logging.LogUtils;
import dev.ftb.mods.ftbic.block.FTBICBlocks;
import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.block.entity.FTBICBlockEntities;
import dev.ftb.mods.ftbic.entity.FTBICEntities;
import dev.ftb.mods.ftbic.item.FTBICItems;
import dev.ftb.mods.ftbic.recipe.FTBICRecipes;
import dev.ftb.mods.ftbic.registry.ModCreativeTabs;
import dev.ftb.mods.ftbic.registry.ModDataComponents;
import dev.ftb.mods.ftbic.screen.FTBICMenus;
import dev.ftb.mods.ftbic.sound.FTBICSounds;
import dev.ftb.mods.ftbic.util.FTBICUtils;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import org.slf4j.Logger;

@Mod(FTBIC.MOD_ID)
public class FTBIC {
	public static final String MOD_ID = "ftbic";
	public static final String MOD_NAME = "FTB Industrial Contraptions";
	public static final Logger LOGGER = LogUtils.getLogger();

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}

	public FTBIC(IEventBus eventBus, ModContainer container, Dist dist) {
		LOGGER.info("{} loading on {}", MOD_NAME, dist);

		FTBICElectricBlocks.init();

		FTBICBlocks.REGISTRY.getClass();
		FTBICItems.REGISTRY.getClass();
		FTBICSounds.REGISTRY.getClass();
		FTBICEntities.REGISTRY.getClass();
		FTBICMenus.REGISTRY.getClass();

		ModDataComponents.DATA_COMPONENTS.register(eventBus);
		ModCreativeTabs.TABS.register(eventBus);
		FTBICBlocks.REGISTRY.register(eventBus);
		FTBICItems.REGISTRY.register(eventBus);
		FTBICBlockEntities.REGISTRY.register(eventBus);
		FTBICSounds.REGISTRY.register(eventBus);
		FTBICEntities.REGISTRY.register(eventBus);
		FTBICMenus.REGISTRY.register(eventBus);
		FTBICRecipes.SERIALIZERS.register(eventBus);
		FTBICRecipes.TYPES.register(eventBus);

		container.registerConfig(ModConfig.Type.COMMON, FTBICConfig.COMMON_SPEC);
		FTBICConfig.init();
		FTBICUtils.init();

	}
}
