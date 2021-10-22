package dev.ftb.mods.ftbic.client;

import dev.ftb.mods.ftbic.FTBICCommon;
import dev.ftb.mods.ftbic.block.FTBICBlocks;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class FTBICClient extends FTBICCommon {
	@Override
	public void init() {
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
	}

	private void setup(FMLClientSetupEvent event) {
		ItemBlockRenderTypes.setRenderLayer(FTBICBlocks.REINFORCED_GLASS.get(), RenderType.cutoutMipped());
	}
}
