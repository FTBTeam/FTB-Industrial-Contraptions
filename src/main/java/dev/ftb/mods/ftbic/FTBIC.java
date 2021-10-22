package dev.ftb.mods.ftbic;

import dev.ftb.mods.ftbic.block.FTBICBlocks;
import dev.ftb.mods.ftbic.block.entity.FTBICBlockEntities;
import dev.ftb.mods.ftbic.item.FTBICItems;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(FTBIC.MOD_ID)
public class FTBIC {
	public static final String MOD_ID = "ftbic";
	public static final String MOD_NAME = "FTB Industrial Contraptions";
	public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);

	public static final CreativeModeTab TAB = new CreativeModeTab(MOD_ID) {
		@Override
		@OnlyIn(Dist.CLIENT)
		public ItemStack makeIcon() {
			return new ItemStack(FTBICItems.RUBBER.get());
		}
	};

	public FTBIC() {
		FTBICBlocks.REGISTRY.register(FMLJavaModLoadingContext.get().getModEventBus());
		FTBICItems.REGISTRY.register(FMLJavaModLoadingContext.get().getModEventBus());
		FTBICBlockEntities.REGISTRY.register(FMLJavaModLoadingContext.get().getModEventBus());
	}
}
