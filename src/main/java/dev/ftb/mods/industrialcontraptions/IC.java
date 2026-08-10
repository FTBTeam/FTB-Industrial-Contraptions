package dev.ftb.mods.industrialcontraptions;

import com.mojang.logging.LogUtils;
import dev.ftb.mods.industrialcontraptions.block.ICBlocks;
import dev.ftb.mods.industrialcontraptions.block.ICElectricBlocks;
import dev.ftb.mods.industrialcontraptions.block.entity.ICBlockEntities;
import dev.ftb.mods.industrialcontraptions.entity.ICEntities;
import dev.ftb.mods.industrialcontraptions.item.ICItems;
import dev.ftb.mods.industrialcontraptions.material.MaterialEntries;
import dev.ftb.mods.industrialcontraptions.recipe.ICRecipes;
import dev.ftb.mods.industrialcontraptions.registry.LegacyConfigMigration;
import dev.ftb.mods.industrialcontraptions.registry.LegacyRegistryAliases;
import dev.ftb.mods.industrialcontraptions.registry.ModCreativeTabs;
import dev.ftb.mods.industrialcontraptions.registry.ModDataComponents;
import dev.ftb.mods.industrialcontraptions.screen.ICMenus;
import dev.ftb.mods.industrialcontraptions.sound.ICSounds;
import dev.ftb.mods.industrialcontraptions.test.ICGameTests;
import dev.ftb.mods.industrialcontraptions.util.ICIngredientTypes;
import dev.ftb.mods.industrialcontraptions.util.ICUtils;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import org.slf4j.Logger;

@Mod(IC.MOD_ID)
public class IC {
	public static final String MOD_ID = "ic";
	public static final String MOD_NAME = "Industrial Contraptions";
	public static final Logger LOGGER = LogUtils.getLogger();

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}

	public IC(IEventBus eventBus, ModContainer container) {
		ICElectricBlocks.init();
		MaterialEntries.register();

		ModDataComponents.DATA_COMPONENTS.register(eventBus);
		ModCreativeTabs.TABS.register(eventBus);
		ICBlocks.REGISTRY.register(eventBus);
		ICItems.REGISTRY.register(eventBus);
		ICBlockEntities.REGISTRY.register(eventBus);
		ICSounds.REGISTRY.register(eventBus);
		ICEntities.REGISTRY.register(eventBus);
		ICMenus.REGISTRY.register(eventBus);
		ICRecipes.SERIALIZERS.register(eventBus);
		ICRecipes.TYPES.register(eventBus);
		ICIngredientTypes.REGISTRY.register(eventBus);
		ICGameTests.TEST_INSTANCE_TYPES.register(eventBus);

		LegacyRegistryAliases.apply(
				ModDataComponents.DATA_COMPONENTS,
				ModCreativeTabs.TABS,
				ICBlocks.REGISTRY,
				ICItems.REGISTRY,
				ICBlockEntities.REGISTRY,
				ICSounds.REGISTRY,
				ICEntities.REGISTRY,
				ICMenus.REGISTRY,
				ICRecipes.SERIALIZERS,
				ICRecipes.TYPES,
				ICIngredientTypes.REGISTRY
		);

		LegacyConfigMigration.run();
		container.registerConfig(ModConfig.Type.COMMON, ICConfig.COMMON_SPEC);
		ICConfig.init();
		ICUtils.init();
	}
}
