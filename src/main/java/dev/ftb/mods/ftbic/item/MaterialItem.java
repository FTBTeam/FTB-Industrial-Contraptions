package dev.ftb.mods.ftbic.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Arrays;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class MaterialItem implements Supplier<Item> {
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

	@Override
	public Item get() {
		return item.get();
	}

	public ItemStack stack() {
		return new ItemStack(item.get());
	}

	public Ingredient ingredient() {
		return Ingredient.of(item.get());
	}
}
