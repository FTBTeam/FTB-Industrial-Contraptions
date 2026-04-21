package dev.ftb.mods.ftbic.client.renderer;

import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.entity.NukeArrowEntity;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.ArrowRenderState;
import net.minecraft.resources.Identifier;

public class NukeArrowRenderer extends ArrowRenderer<NukeArrowEntity, ArrowRenderState> {
	private static final Identifier TEXTURE = FTBIC.id("textures/item/nuke_arrow.png");

	public NukeArrowRenderer(EntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public ArrowRenderState createRenderState() {
		return new ArrowRenderState();
	}

	@Override
	protected Identifier getTextureLocation(ArrowRenderState state) {
		return TEXTURE;
	}
}
