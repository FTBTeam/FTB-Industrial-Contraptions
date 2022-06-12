package dev.ftb.mods.ftbic;

import com.mojang.serialization.Codec;
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
import dev.ftb.mods.ftbic.world.OreBiomeModifier;
import dev.ftb.mods.ftbic.world.OreGeneration;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

@Mod(FTBIC.MOD_ID)
@Mod.EventBusSubscriber(modid = FTBIC.MOD_ID)
public class FTBIC {
	public static final String MOD_ID = "ftbic";
	public static final String MOD_NAME = "FTB Industrial Contraptions";
	public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);
	public static FTBICCommon PROXY;

	private static final DeferredRegister<Codec<? extends BiomeModifier>> BIOME_SERIALIZERS = DeferredRegister.create(ForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, MOD_ID);
	public static final RegistryObject<Codec<OreBiomeModifier>> ORE_BIOME_MODIFIER = BIOME_SERIALIZERS.register("ore_biome_modifiers", () -> OreBiomeModifier.CODEC);

	public static final List<DeferredRegister<?>> REGISTERS = List.of(
			FTBICBlocks.REGISTRY,
			FTBICItems.REGISTRY,
			FTBICBlockEntities.REGISTRY,
			FTBICRecipes.REGISTRY,
			FTBICRecipes.REGISTRY_TYPE,
			FTBICMenus.REGISTRY,
			FTBICEntities.REGISTRY,
			FTBICSounds.REGISTRY,
			FTBICUtils.LOOT_REGISTRY,
			OreGeneration.FEATURE_REGISTRY,
			OreGeneration.PLACED_FEATURE_REGISTRY,
			BIOME_SERIALIZERS
	);

	public static final CreativeModeTab TAB = new CreativeModeTab(MOD_ID) {
		@Override
		@OnlyIn(Dist.CLIENT)
		public ItemStack makeIcon() {
			return new ItemStack(FTBICElectricBlocks.POWERED_FURNACE.item.get());
		}
	};

	public FTBIC() {
		PROXY = DistExecutor.safeRunForDist(() -> FTBICClient::new, () -> FTBICCommon::new);

		// Config setup
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, FTBICConfig.COMMON_CONFIG);
		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

		modEventBus.addListener(this::setup);

		// Register all the registries
		REGISTERS.forEach(e -> e.register(modEventBus));

		FTBICElectricBlocks.init();
		FTBICUtils.init();
		FTBICNet.init();
		FTBICConfig.init();

		PROXY.init();
	}

	private void setup(final FMLCommonSetupEvent event) {
		event.enqueueWork(OreGeneration::init);
	}

//	@SubscribeEvent
//	public static void biomeLoadEvent(BiomeLoadingEvent event) {
//		var biome = event.getCategory();
//		if (biome == Biome.BiomeCategory.THEEND || biome == Biome.BiomeCategory.NONE || biome == Biome.BiomeCategory.NETHER) {
//			return;
//		}
//
//		OreGeneration.PLACEMENTS.forEach(e -> event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, e));
//	}

	private static boolean isDummyArmor(LivingDamageEvent event, EquipmentSlot slot, ArmorMaterial material) {
		Item item = event.getEntityLiving().getItemBySlot(slot).getItem();
		return item instanceof DummyEnergyArmorItem && ((DummyEnergyArmorItem) item).getMaterial() == material;
	}

	@SubscribeEvent
	public static void playerDamage(LivingDamageEvent event) {
		if (!event.getSource().isBypassInvul() && event.getEntityLiving() instanceof Player) {
			ItemStack stack = event.getEntityLiving().getItemBySlot(EquipmentSlot.CHEST);

			if (stack.getItem() instanceof EnergyArmorItem armorItem) {
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
					double energy = FTBICConfig.EQUIPMENT.ARMOR_DAMAGE_ENERGY.get() * amountReduced;

					armorItem.damageEnergyItem(stack, energy);
					event.setAmount(event.getAmount() - amountReduced);
					//event.setCanceled(true);

					/*
					if (FMLLoader.isProduction() && !event.getEntityLiving().level.isClientSide()) {
						((Player) event.getEntityLiving()).displayClientMessage(Component.literal("Absorbed " + amountReduced + " / " + event.getAmount() + " for " + energy + " zaps"), true);
					}
					 */
				}
			}
		}
	}
}
