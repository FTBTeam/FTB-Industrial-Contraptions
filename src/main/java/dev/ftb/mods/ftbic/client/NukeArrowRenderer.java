package dev.ftb.mods.ftbic.client;

import dev.ftb.mods.ftbic.entity.NukeArrowEntity;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.TippableArrowRenderer;
import net.minecraft.resources.ResourceLocation;

public class NukeArrowRenderer extends ArrowRenderer<NukeArrowEntity> {
	public NukeArrowRenderer(EntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public ResourceLocation getTextureLocation(NukeArrowEntity entity) {
		return TippableArrowRenderer.NORMAL_ARROW_LOCATION;
	}
}

