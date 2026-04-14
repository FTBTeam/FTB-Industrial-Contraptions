package dev.ftb.mods.ftbic.item;

import net.minecraft.world.item.Item;

public class UpgradeItem extends Item {
	public final int upgradeId;

	public UpgradeItem(Properties props, int upgradeId) {
		super(props);
		this.upgradeId = upgradeId;
	}
}
