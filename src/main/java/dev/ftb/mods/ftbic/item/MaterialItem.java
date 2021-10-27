package dev.ftb.mods.ftbic.item;

import net.minecraft.world.item.Item;

import java.util.Arrays;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class MaterialItem {
	public final String id;
	public String name;
	public Supplier<Item> item;

	public MaterialItem(String i) {
		id = i;
		name = Arrays.stream(id.split("_")).map(s -> Character.toUpperCase(s.charAt(0)) + s.substring(1)).collect(Collectors.joining(" "));
	}

	public MaterialItem name(String n) {
		name = n;
		return this;
	}
}
