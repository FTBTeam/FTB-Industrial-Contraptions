package dev.ftb.mods.ftbic.item;

import dev.ftb.mods.ftbic.world.ResourceType;
import net.minecraft.world.item.Item;

public class ResourceItem extends Item {
	public final ResourceType resourceType;

	public ResourceItem(Properties props, ResourceType t) {
		super(props);
		resourceType = t;
	}
}
