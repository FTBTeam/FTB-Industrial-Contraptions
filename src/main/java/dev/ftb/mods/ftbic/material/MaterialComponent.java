package dev.ftb.mods.ftbic.material;

import java.util.Arrays;
import java.util.List;

public enum MaterialComponent {
	STONE_ORE(true, "{m} Ore", "ores", List.of("c:ores", "c:ores_in_ground/stone")),
	DEEPSLATE_ORE(true, "Deepslate {m} Ore", "ores", List.of("c:ores", "c:ores_in_ground/deepslate")),
	BLOCK(true, "Block of {m}", "storage_blocks", List.of("c:storage_blocks")),
	RAW_BLOCK(true, "Block of Raw {m}", "storage_blocks", List.of("c:storage_blocks"), "raw_"),
	RAW_ORE(false, "Raw {m}", "raw_materials", List.of("c:raw_materials")),
	INGOT(false, "{m} Ingot", "ingots", List.of("c:ingots")),
	NUGGET(false, "{m} Nugget", "nuggets", List.of("c:nuggets")),
	DUST(false, "{m} Dust", "dusts", List.of("c:dusts")),
	PLATE(false, "{m} Plate", "plates", List.of("c:plates")),
	ROD(false, "{m} Rod", "rods", List.of("c:rods")),
	GEAR(false, "{m} Gear", "gears", List.of("c:gears")),
	WIRE(false, "{m} Wire", "wires", List.of("c:wires")),
	GEM(false, "{m} Gem", "gems", List.of("c:gems"));

	private final boolean block;
	private final String translationPattern;
	private final String perMaterialRoot;
	private final List<String> containerTags;
	private final String perMaterialPrefix;

	MaterialComponent(boolean block, String translationPattern, String perMaterialRoot, List<String> containerTags) {
		this(block, translationPattern, perMaterialRoot, containerTags, "");
	}

	MaterialComponent(boolean block, String translationPattern, String perMaterialRoot, List<String> containerTags, String perMaterialPrefix) {
		this.block = block;
		this.translationPattern = translationPattern;
		this.perMaterialRoot = perMaterialRoot;
		this.containerTags = containerTags;
		this.perMaterialPrefix = perMaterialPrefix;
	}

	public boolean isBlock() {
		return block;
	}

	public String suffix() {
		return name().toLowerCase();
	}

	public String translation(String materialDisplay) {
		return translationPattern.replace("{m}", materialDisplay);
	}

	public List<String> containerTags() {
		return containerTags;
	}

	public String perMaterialTag(String materialName) {
		return "c:" + perMaterialRoot + "/" + perMaterialPrefix + materialName;
	}

	public static List<MaterialComponent> ores() {
		return Arrays.asList(STONE_ORE, DEEPSLATE_ORE);
	}
}
