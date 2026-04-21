package dev.ftb.mods.ftbic.util;

import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.item.reactor.NuclearReactor;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public final class ReactorPresetLibrary {
	public static final int MAX_NAME_LENGTH = 64;
	private static final Pattern NAME_RX = Pattern.compile("[a-z0-9_\\-./]+");
	private static final Set<String> RESERVED_NAMES = Set.of(
			"con", "prn", "aux", "nul",
			"com1", "com2", "com3", "com4", "com5", "com6", "com7", "com8", "com9",
			"lpt1", "lpt2", "lpt3", "lpt4", "lpt5", "lpt6", "lpt7", "lpt8", "lpt9"
	);

	public record Preset(String name, boolean builtin, ReactorDesign design) {}

	private static final List<Preset> BUILTINS = List.of(
			new Preset("1 Chamber", true, buildEntryLevel()),
			new Preset("3 Chamber", true, buildMidTier()),
			new Preset("6 Chamber", true, buildMax())
	);

	public static List<Preset> listAll() {
		List<Preset> all = new ArrayList<>(BUILTINS);
		all.addAll(loadUserPresets());
		return all;
	}

	public static List<Preset> loadUserPresets() {
		List<Preset> out = new ArrayList<>();
		Path dir = getUserDir();
		if (!Files.isDirectory(dir)) return out;
		try (Stream<Path> stream = Files.list(dir)) {
			stream.filter(p -> p.toString().endsWith(".json")).sorted().forEach(p -> {
				try {
					String name = p.getFileName().toString();
					name = name.substring(0, name.length() - ".json".length());
					String json = Files.readString(p);
					ReactorDesign design = ReactorDesign.fromJson(json);
					out.add(new Preset(name, false, design));
				} catch (Exception e) {
					FTBIC.LOGGER.warn("Failed to load reactor preset {}: {}", p, e.getMessage());
				}
			});
		} catch (IOException e) {
			FTBIC.LOGGER.warn("Failed to list reactor preset directory {}: {}", dir, e.getMessage());
		}
		return out;
	}

	public static boolean save(String name, ReactorDesign design) {
		if (!isValidName(name)) return false;
		Path dir = getUserDir();
		try {
			Files.createDirectories(dir);
			Path file = dir.resolve(name + ".json");
			Path tmp = dir.resolve(name + ".json.tmp");
			Files.writeString(tmp, design.toJson());
			try {
				Files.move(tmp, file, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException atomicFail) {
				Files.move(tmp, file, StandardCopyOption.REPLACE_EXISTING);
			}
			return true;
		} catch (IOException e) {
			FTBIC.LOGGER.warn("Failed to save reactor preset {}: {}", name, e.getMessage());
			return false;
		}
	}

	public static boolean remove(String name) {
		if (!isValidName(name)) return false;
		Path file = getUserDir().resolve(name + ".json");
		try {
			return Files.deleteIfExists(file);
		} catch (IOException e) {
			FTBIC.LOGGER.warn("Failed to delete reactor preset {}: {}", name, e.getMessage());
			return false;
		}
	}

	public static boolean isValidName(String name) {
		if (name == null || name.isEmpty() || name.length() > MAX_NAME_LENGTH) return false;
		if (!NAME_RX.matcher(name).matches()) return false;
		if (name.contains("..") || name.startsWith("/") || name.startsWith("-") || name.startsWith(".")) return false;
		if (RESERVED_NAMES.contains(name)) return false;
		return true;
	}

	public static Path getUserDir() {
		Path gameDir = Minecraft.getInstance().gameDirectory.toPath();
		return gameDir.resolve("local").resolve("ftbic").resolve("reactor_layout");
	}

	private static ReactorDesign buildEntryLevel() {
		List<ReactorDesign.DesignSlot> slots = new ArrayList<>();
		Identifier rod = Identifier.fromNamespaceAndPath(FTBIC.MOD_ID, "dual_uranium_fuel_rod");
		Identifier over = Identifier.fromNamespaceAndPath(FTBIC.MOD_ID, "overclocked_heat_vent");
		String[] grid = {
				"RVVR",
				"VVVV",
				"VRVV",
				"VRVV",
				"VVVV",
				"RVVR"
		};
		fillGrid(slots, grid, rod, over);
		return new ReactorDesign(ReactorDesign.CURRENT_VERSION, 1, 1.0D, slots);
	}

	private static ReactorDesign buildMidTier() {
		List<ReactorDesign.DesignSlot> slots = new ArrayList<>();
		Identifier rod = Identifier.fromNamespaceAndPath(FTBIC.MOD_ID, "dual_uranium_fuel_rod");
		Identifier over = Identifier.fromNamespaceAndPath(FTBIC.MOD_ID, "overclocked_heat_vent");
		String[] grid = {
				"RVVVVR",
				"VVVVVV",
				"VRVVRV",
				"VRVVRV",
				"VVVVVV",
				"RVVVVR"
		};
		fillGrid(slots, grid, rod, over);
		return new ReactorDesign(ReactorDesign.CURRENT_VERSION, 3, 1.0D, slots);
	}

	private static void fillGrid(List<ReactorDesign.DesignSlot> slots, String[] grid, Identifier rod, Identifier vent) {
		for (int row = 0; row < grid.length; row++) {
			String line = grid[row];
			for (int col = 0; col < line.length(); col++) {
				char c = line.charAt(col);
				if (c == 'R') addSlot(slots, col, row, rod);
				else if (c == 'V') addSlot(slots, col, row, vent);
			}
		}
	}

	private static ReactorDesign buildMax() {
		List<ReactorDesign.DesignSlot> slots = new ArrayList<>();
		Identifier rod = Identifier.fromNamespaceAndPath(FTBIC.MOD_ID, "quad_uranium_fuel_rod");
		Identifier over = Identifier.fromNamespaceAndPath(FTBIC.MOD_ID, "overclocked_heat_vent");
		Identifier ref = Identifier.fromNamespaceAndPath(FTBIC.MOD_ID, "iridium_neutron_reflector");

		for (int col = 0; col < 9; col++) addSlot(slots, col, 0, over);
		for (int col = 0; col < 9; col++) addSlot(slots, col, 5, over);

		for (int col = 0; col < 9; col += 2) addSlot(slots, col, 1, over);
		for (int col = 1; col < 9; col += 2) addSlot(slots, col, 1, ref);

		for (int col = 0; col < 9; col += 2) addSlot(slots, col, 2, ref);
		for (int col = 1; col < 9; col += 2) addSlot(slots, col, 2, rod);

		addSlot(slots, 0, 3, over);
		addSlot(slots, 1, 3, ref);
		addSlot(slots, 2, 3, over);
		addSlot(slots, 3, 3, ref);
		addSlot(slots, 4, 3, rod);
		addSlot(slots, 5, 3, ref);
		addSlot(slots, 6, 3, over);
		addSlot(slots, 7, 3, ref);
		addSlot(slots, 8, 3, over);

		addSlot(slots, 0, 4, over);
		addSlot(slots, 1, 4, over);
		addSlot(slots, 2, 4, over);
		addSlot(slots, 3, 4, over);
		addSlot(slots, 4, 4, ref);
		addSlot(slots, 5, 4, over);
		addSlot(slots, 6, 4, over);
		addSlot(slots, 7, 4, over);
		addSlot(slots, 8, 4, over);

		return new ReactorDesign(ReactorDesign.CURRENT_VERSION, 6, 1.0D, slots);
	}

	private static void addSlot(List<ReactorDesign.DesignSlot> out, int col, int row, Identifier id) {
		out.add(new ReactorDesign.DesignSlot(row * NuclearReactor.MAX_COLUMNS + col, id));
	}

	private ReactorPresetLibrary() {}
}
