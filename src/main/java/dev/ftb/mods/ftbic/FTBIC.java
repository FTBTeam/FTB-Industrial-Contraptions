package dev.ftb.mods.ftbic;

import dev.ftb.mods.ftbic.block.FTBICBlocks;
import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.block.entity.FTBICBlockEntities;
import dev.ftb.mods.ftbic.client.FTBICClient;
import dev.ftb.mods.ftbic.entity.FTBICEntities;
import dev.ftb.mods.ftbic.item.DummyEnergyArmorItem;
import dev.ftb.mods.ftbic.item.EnergyArmorItem;
import dev.ftb.mods.ftbic.item.FTBICItems;
import dev.ftb.mods.ftbic.net.FTBICNet;
import dev.ftb.mods.ftbic.recipe.FTBICRecipes;
import dev.ftb.mods.ftbic.screen.FTBICMenus;
import dev.ftb.mods.ftbic.sound.FTBICSounds;
import dev.ftb.mods.ftbic.util.FTBICUtils;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.IEventBus;
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

		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		modEventBus.addGenericListener(Item.class, FTBICRecipes::onItemRegistry);
		modEventBus.addGenericListener(Item.class, FTBICUtils::onItemRegistry);

		FTBICBlocks.REGISTRY.register(FMLJavaModLoadingContext.get().getModEventBus());
		FTBICItems.REGISTRY.register(FMLJavaModLoadingContext.get().getModEventBus());
		FTBICBlockEntities.REGISTRY.register(FMLJavaModLoadingContext.get().getModEventBus());
		FTBICRecipes.REGISTRY.register(FMLJavaModLoadingContext.get().getModEventBus());
		FTBICMenus.REGISTRY.register(FMLJavaModLoadingContext.get().getModEventBus());
		FTBICEntities.REGISTRY.register(FMLJavaModLoadingContext.get().getModEventBus());
		FTBICSounds.REGISTRY.register(FMLJavaModLoadingContext.get().getModEventBus());
		FTBICElectricBlocks.init();
		FTBICUtils.init();
		FTBICNet.init();
		FTBICConfig.init();
		PROXY.init();
	}

	private static boolean isDummyArmor(LivingDamageEvent event, EquipmentSlot slot, ArmorMaterial material) {
		Item item = event.getEntityLiving().getItemBySlot(slot).getItem();
		return item instanceof DummyEnergyArmorItem && ((DummyEnergyArmorItem) item).getMaterial() == material;
	}

	@SubscribeEvent
	public static void playerDamage(LivingDamageEvent event) {
		if (!event.getSource().isBypassInvul() && event.getEntityLiving() instanceof Player) {
			ItemStack stack = event.getEntityLiving().getItemBySlot(EquipmentSlot.CHEST);

			if (stack.getItem() instanceof EnergyArmorItem) {
				EnergyArmorItem armorItem = (EnergyArmorItem) stack.getItem();

				if (armorItem.getEnergy(stack) > 0D) {
					float protection = 0.35F;

					if (isDummyArmor(event, EquipmentSlot.HEAD, armorItem.getMaterial())) {
						protection += 0.25F;
					}

					if (isDummyArmor(event, EquipmentSlot.LEGS, armorItem.getMaterial())) {
						protection += 0.35F;
					}

					if (isDummyArmor(event, EquipmentSlot.FEET, armorItem.getMaterial())) {
						protection += 0.15F;
					}

					float amountReduced = event.getAmount() * Math.min(protection, 1F);
					double energy = FTBICConfig.ARMOR_DAMAGE_ENERGY * amountReduced;

					((EnergyArmorItem) stack.getItem()).damageEnergyItem(stack, energy);
					event.setAmount(event.getAmount() - amountReduced);
					//event.setCanceled(true);

					/*
					if (FMLLoader.isProduction() && !event.getEntityLiving().level.isClientSide()) {
						((Player) event.getEntityLiving()).displayClientMessage(new TextComponent("Absorbed " + amountReduced + " / " + event.getAmount() + " for " + energy + " zaps"), true);
					}
					 */
				}
			}
		}
	}

	@Nullable
	private static Item missingItem(String name) {
		return switch (name) {
			case "battery" -> FTBICItems.LV_BATTERY.get();
			case "crystal_battery" -> FTBICItems.MV_BATTERY.get();
			case "graphene_battery" -> FTBICItems.HV_BATTERY.get();
			case "iridium_battery" -> FTBICItems.EV_BATTERY.get();
			case "coolant_10k" -> FTBICItems.SMALL_COOLANT_CELL.get();
			case "coolant_30k" -> FTBICItems.MEDIUM_COOLANT_CELL.get();
			case "coolant_60k" -> FTBICItems.LARGE_COOLANT_CELL.get();
			default -> null;
		};
	}

	@Nullable
	private static Block missingBlock(String name) {
		return switch (name) {
			case "copper_cable" -> FTBICBlocks.LV_CABLE.get();
			case "gold_cable" -> FTBICBlocks.MV_CABLE.get(); // swapped HV and MV
			case "aluminum_cable" -> FTBICBlocks.HV_CABLE.get(); // swapped HV and MV
			case "enderium_cable" -> FTBICBlocks.EV_CABLE.get();
			case "glass_cable" -> FTBICBlocks.IV_CABLE.get();
			default -> null;
		};
	}

	@SubscribeEvent
	public static void missingItems(RegistryEvent.MissingMappings<Item> event) {
		for (RegistryEvent.MissingMappings.Mapping<Item> mapping : event.getAllMappings()) {
			if (mapping.key.getNamespace().equals(MOD_ID)) {
				Block block = missingBlock(mapping.key.getPath());

				if (block != null) {
					mapping.remap(block.asItem());
				} else {
					Item item = missingItem(mapping.key.getPath());

					if (item != null) {
						mapping.remap(item);
					}
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
