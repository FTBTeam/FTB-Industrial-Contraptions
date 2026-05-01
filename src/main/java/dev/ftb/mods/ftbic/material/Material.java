package dev.ftb.mods.ftbic.material;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

public enum Material {
	ALUMINUM(Tool.STONE, components(MaterialComponent.STONE_ORE, MaterialComponent.DEEPSLATE_ORE,
			MaterialComponent.BLOCK, MaterialComponent.RAW_BLOCK, MaterialComponent.RAW_ORE,
			MaterialComponent.INGOT, MaterialComponent.DUST, MaterialComponent.PLATE,
			MaterialComponent.ROD, MaterialComponent.GEAR, MaterialComponent.WIRE)),
	BRONZE(Tool.STONE, components(MaterialComponent.BLOCK, MaterialComponent.INGOT,
			MaterialComponent.DUST, MaterialComponent.PLATE, MaterialComponent.ROD,
			MaterialComponent.GEAR, MaterialComponent.WIRE)),
	CHARCOAL(Tool.STONE, components(MaterialComponent.DUST)),
	COAL(Tool.STONE, components(MaterialComponent.DUST)),
	CONSTANTAN(Tool.STONE, components(MaterialComponent.DUST)),
	COPPER(Tool.STONE, components(MaterialComponent.DUST, MaterialComponent.PLATE,
			MaterialComponent.ROD, MaterialComponent.GEAR, MaterialComponent.WIRE)),
	DIAMOND(Tool.IRON, components(MaterialComponent.DUST, MaterialComponent.GEAR, MaterialComponent.WIRE)),
	ELECTRUM(Tool.STONE, components(MaterialComponent.BLOCK, MaterialComponent.INGOT,
			MaterialComponent.DUST, MaterialComponent.PLATE, MaterialComponent.ROD,
			MaterialComponent.GEAR, MaterialComponent.WIRE)),
	EMERALD(Tool.IRON, components(MaterialComponent.DUST)),
	GOLD(Tool.IRON, components(MaterialComponent.DUST, MaterialComponent.PLATE,
			MaterialComponent.ROD, MaterialComponent.GEAR, MaterialComponent.WIRE)),
	INVAR(Tool.IRON, components(MaterialComponent.BLOCK, MaterialComponent.INGOT,
			MaterialComponent.DUST, MaterialComponent.PLATE, MaterialComponent.ROD,
			MaterialComponent.GEAR, MaterialComponent.WIRE)),
	IRIDIUM(Tool.STONE, components(MaterialComponent.STONE_ORE, MaterialComponent.DEEPSLATE_ORE,
			MaterialComponent.BLOCK, MaterialComponent.RAW_BLOCK, MaterialComponent.RAW_ORE,
			MaterialComponent.INGOT, MaterialComponent.DUST, MaterialComponent.PLATE,
			MaterialComponent.ROD, MaterialComponent.GEAR, MaterialComponent.WIRE)),
	IRON(Tool.STONE, components(MaterialComponent.DUST, MaterialComponent.PLATE,
			MaterialComponent.ROD, MaterialComponent.GEAR, MaterialComponent.WIRE)),
	LAPIS_LAZULI(Tool.STONE, "lapis", components(MaterialComponent.DUST)),
	LEAD(Tool.STONE, components(MaterialComponent.STONE_ORE, MaterialComponent.DEEPSLATE_ORE,
			MaterialComponent.BLOCK, MaterialComponent.RAW_BLOCK, MaterialComponent.RAW_ORE,
			MaterialComponent.INGOT, MaterialComponent.DUST, MaterialComponent.PLATE,
			MaterialComponent.ROD, MaterialComponent.GEAR, MaterialComponent.WIRE)),
	NETHERITE(Tool.DIAMOND, components(MaterialComponent.DUST, MaterialComponent.PLATE,
			MaterialComponent.ROD, MaterialComponent.GEAR, MaterialComponent.WIRE)),
	NICKEL(Tool.STONE, components(MaterialComponent.STONE_ORE, MaterialComponent.DEEPSLATE_ORE,
			MaterialComponent.BLOCK, MaterialComponent.RAW_BLOCK, MaterialComponent.RAW_ORE,
			MaterialComponent.INGOT, MaterialComponent.DUST, MaterialComponent.PLATE,
			MaterialComponent.ROD, MaterialComponent.GEAR, MaterialComponent.WIRE)),
	OBSIDIAN(Tool.DIAMOND, components(MaterialComponent.DUST, MaterialComponent.PLATE,
			MaterialComponent.ROD, MaterialComponent.GEAR, MaterialComponent.WIRE)),
	PLUTONIUM(Tool.STONE, components(MaterialComponent.BLOCK, MaterialComponent.RAW_BLOCK,
			MaterialComponent.RAW_ORE, MaterialComponent.INGOT, MaterialComponent.DUST,
			MaterialComponent.PLATE, MaterialComponent.ROD, MaterialComponent.GEAR, MaterialComponent.WIRE)),
	QUARTZ(Tool.STONE, components(MaterialComponent.DUST, MaterialComponent.GEAR, MaterialComponent.WIRE)),
	SILICON(Tool.STONE, components(MaterialComponent.GEM)),
	SILVER(Tool.IRON, components(MaterialComponent.STONE_ORE, MaterialComponent.DEEPSLATE_ORE,
			MaterialComponent.BLOCK, MaterialComponent.RAW_BLOCK, MaterialComponent.RAW_ORE,
			MaterialComponent.INGOT, MaterialComponent.DUST, MaterialComponent.PLATE,
			MaterialComponent.ROD, MaterialComponent.GEAR, MaterialComponent.WIRE)),
	TIN(Tool.STONE, components(MaterialComponent.STONE_ORE, MaterialComponent.DEEPSLATE_ORE,
			MaterialComponent.BLOCK, MaterialComponent.RAW_BLOCK, MaterialComponent.RAW_ORE,
			MaterialComponent.INGOT, MaterialComponent.DUST, MaterialComponent.PLATE,
			MaterialComponent.ROD, MaterialComponent.GEAR, MaterialComponent.WIRE)),
	URANIUM(Tool.IRON, components(MaterialComponent.STONE_ORE, MaterialComponent.DEEPSLATE_ORE,
			MaterialComponent.BLOCK, MaterialComponent.RAW_BLOCK, MaterialComponent.RAW_ORE,
			MaterialComponent.INGOT, MaterialComponent.DUST, MaterialComponent.PLATE,
			MaterialComponent.ROD, MaterialComponent.GEAR, MaterialComponent.WIRE));

	public enum Tool { STONE, IRON, DIAMOND }

	private final Tool tool;
	private final String tagName;
	private final EnumSet<MaterialComponent> components;

	Material(Tool tool, EnumSet<MaterialComponent> components) {
		this(tool, null, components);
	}

	Material(Tool tool, String tagName, EnumSet<MaterialComponent> components) {
		this.tool = tool;
		this.tagName = tagName;
		this.components = components;
	}

	public String key() {
		return name().toLowerCase();
	}

	public String tagName() {
		return tagName != null ? tagName : key();
	}

	public String displayName() {
		return Arrays.stream(name().split("_"))
				.map(s -> s.charAt(0) + s.substring(1).toLowerCase())
				.collect(Collectors.joining(" "));
	}

	public Tool tool() {
		return tool;
	}

	public Set<MaterialComponent> components() {
		return components;
	}

	public boolean has(MaterialComponent component) {
		return components.contains(component);
	}

	private static EnumSet<MaterialComponent> components(MaterialComponent first, MaterialComponent... rest) {
		return EnumSet.of(first, rest);
	}
}
