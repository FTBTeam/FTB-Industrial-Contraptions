package dev.ftb.mods.ftbic.item;

import dev.ftb.mods.ftbic.FTBIC;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;

import java.util.HashMap;
import java.util.Map;

public class CellItem extends Item {
	public static final Map<Fluid, CellItem> FLUID_TO_CELL_MAP = new HashMap<>();

	public final Fluid fluid;

	public CellItem(Fluid f) {
		super(new Properties().stacksTo(16).tab(FTBIC.TAB));
		fluid = f;
		FLUID_TO_CELL_MAP.put(fluid, this);
	}
}
