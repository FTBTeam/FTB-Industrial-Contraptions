package dev.ftb.mods.ftbic;

import dev.ftb.mods.ftbic.block.FTBICBlocks;
import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.block.entity.FTBICBlockEntities;
import dev.ftb.mods.ftbic.client.FTBICClient;
import dev.ftb.mods.ftbic.item.FTBICItems;
import dev.ftb.mods.ftbic.recipe.FTBICRecipes;
import io.alwa.mods.myrtrees.common.MyrtreesConfig;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(FTBIC.MOD_ID)
public class FTBIC {
	public static final String MOD_ID = "ftbic";
	public static final String MOD_NAME = "FTB Industrial Contraptions";
	public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);
	public static FTBICCommon PROXY;

	public static final CreativeModeTab TAB = new CreativeModeTab(MOD_ID) {
		@Override
		@OnlyIn(Dist.CLIENT)
		public ItemStack makeIcon() {
			return new ItemStack(FTBICElectricBlocks.ELECTRIC_FURNACE.item.get());
		}
	};

	public FTBIC() {
		PROXY = DistExecutor.safeRunForDist(() -> FTBICClient::new, () -> FTBICCommon::new);
		FTBICBlocks.REGISTRY.register(FMLJavaModLoadingContext.get().getModEventBus());
		FTBICItems.REGISTRY.register(FMLJavaModLoadingContext.get().getModEventBus());
		FTBICBlockEntities.REGISTRY.register(FMLJavaModLoadingContext.get().getModEventBus());
		FTBICRecipes.REGISTRY.register(FMLJavaModLoadingContext.get().getModEventBus());
		FTBICElectricBlocks.init();
		PROXY.init();
		MyrtreesConfig.ALL_BIOMES = true;
	}
}
