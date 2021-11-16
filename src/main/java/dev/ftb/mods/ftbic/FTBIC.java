package dev.ftb.mods.ftbic;

import dev.ftb.mods.ftbic.block.FTBICBlocks;
import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.block.entity.FTBICBlockEntities;
import dev.ftb.mods.ftbic.client.FTBICClient;
import dev.ftb.mods.ftbic.item.EnergyArmorItem;
import dev.ftb.mods.ftbic.item.FTBICItems;
import dev.ftb.mods.ftbic.recipe.FTBICRecipes;
import dev.ftb.mods.ftbic.screen.FTBICMenus;
import dev.ftb.mods.ftbic.util.FTBICUtils;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

@Mod(FTBIC.MOD_ID)
@Mod.EventBusSubscriber(modid = FTBIC.MOD_ID)
public class FTBIC {
	public static final String MOD_ID = "ftbic";
	public static final String MOD_NAME = "FTB Industrial Contraptions";
	public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);
	public static FTBICCommon PROXY;

	public static final CreativeModeTab TAB = new CreativeModeTab(MOD_ID) {
		@Override
		@OnlyIn(Dist.CLIENT)
		public ItemStack makeIcon() {
			return new ItemStack(FTBICElectricBlocks.POWERED_FURNACE.item.get());
		}
	};

	public FTBIC() {
		PROXY = DistExecutor.safeRunForDist(() -> FTBICClient::new, () -> FTBICCommon::new);
		FTBICBlocks.REGISTRY.register(FMLJavaModLoadingContext.get().getModEventBus());
		FTBICItems.REGISTRY.register(FMLJavaModLoadingContext.get().getModEventBus());
		FTBICBlockEntities.REGISTRY.register(FMLJavaModLoadingContext.get().getModEventBus());
		FTBICRecipes.REGISTRY.register(FMLJavaModLoadingContext.get().getModEventBus());
		FTBICMenus.REGISTRY.register(FMLJavaModLoadingContext.get().getModEventBus());
		FTBICElectricBlocks.init();
		FTBICUtils.init();
		PROXY.init();
	}

	@SubscribeEvent
	public static void playerDamage(LivingDamageEvent event) {
		if (!event.getSource().isBypassInvul()) {
			ItemStack stack = event.getEntityLiving().getItemBySlot(EquipmentSlot.CHEST);

			if (stack.getItem() instanceof EnergyArmorItem && ((EnergyArmorItem) stack.getItem()).getEnergy(stack) > 0D) {
				((EnergyArmorItem) stack.getItem()).damageEnergyItem(stack, FTBICConfig.ARMOR_DAMAGE_ENERGY * event.getAmount());
				event.setAmount(0F);
				//event.setCanceled(true);
			}
		}
	}

	@Nullable
	private static Block missingBlock(String name) {
		switch (name) {
			case "copper_cable":
				return FTBICBlocks.LV_CABLE.get();
			case "gold_cable": // swapped HV and MV
				return FTBICBlocks.MV_CABLE.get();
			case "aluminum_cable": // swapped HV and MV
				return FTBICBlocks.HV_CABLE.get();
			case "enderium_cable":
				return FTBICBlocks.EV_CABLE.get();
			case "glass_cable":
				return FTBICBlocks.IV_CABLE.get();
			default:
				return null;
		}
	}

	@SubscribeEvent
	public static void missingItems(RegistryEvent.MissingMappings<Item> event) {
		for (RegistryEvent.MissingMappings.Mapping<Item> mapping : event.getAllMappings()) {
			if (mapping.key.getNamespace().equals(MOD_ID)) {
				Block block = missingBlock(mapping.key.getPath());

				if (block != null) {
					mapping.remap(block.asItem());
				}
			}
		}
	}

	@SubscribeEvent
	public static void missingBlocks(RegistryEvent.MissingMappings<Block> event) {
		for (RegistryEvent.MissingMappings.Mapping<Block> mapping : event.getAllMappings()) {
			if (mapping.key.getNamespace().equals(MOD_ID)) {
				Block block = missingBlock(mapping.key.getPath());

				if (block != null) {
					mapping.remap(block);
				}
			}
		}
	}
}
