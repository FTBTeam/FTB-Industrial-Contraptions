package dev.ftb.mods.ftbic.recipe;

import net.minecraft.world.SimpleContainer;

public class NoContainer extends SimpleContainer {
	public static final NoContainer INSTANCE = new NoContainer();

	private NoContainer() {
		super(0);
	}
}
