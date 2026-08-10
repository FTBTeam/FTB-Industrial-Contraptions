package dev.ftb.mods.industrialcontraptions.datagen;

import dev.ftb.mods.industrialcontraptions.IC;
import dev.ftb.mods.industrialcontraptions.item.ICItems;
import dev.ftb.mods.industrialcontraptions.material.Material;
import dev.ftb.mods.industrialcontraptions.material.MaterialComponent;
import dev.ftb.mods.industrialcontraptions.recipe.AntimatterBoostRecipe;
import dev.ftb.mods.industrialcontraptions.recipe.BasicGeneratorFuelRecipe;
import dev.ftb.mods.industrialcontraptions.recipe.ICRecipes;
import dev.ftb.mods.industrialcontraptions.recipe.MachineRecipe;
import dev.ftb.mods.industrialcontraptions.recipe.MachineRecipeType;
import dev.ftb.mods.industrialcontraptions.util.FluidCellIngredient;
import dev.ftb.mods.industrialcontraptions.util.IngredientWithCount;
import dev.ftb.mods.industrialcontraptions.util.StackWithChance;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.BlastingRecipe;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.item.crafting.SmokingRecipe;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.crafting.CompoundIngredient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ICRecipeProvider extends RecipeProvider {

	private static final String[] MACERATE_INGOTS = {
			"aluminum", "bronze", "constantan", "copper", "electrum", "enderium", "gold", "invar",
			"iridium", "iron", "lead", "netherite", "nickel", "plutonium", "silver", "steel", "tin",
			"uranium",
	};

	private static final String[] MACERATE_ORES = {
			"aluminum", "coal", "copper", "diamond", "emerald", "gold", "iridium", "iron",
			"lapis_lazuli", "lead", "nickel", "quartz", "silver", "tin", "uranium",
	};

	private static final String[] MACERATE_RAW = {
			"aluminum", "copper", "gold", "iridium", "iron", "lead", "nickel", "plutonium",
			"silver", "tin", "uranium",
	};

	public ICRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
		super(registries, output);
	}

	@Override
	protected void buildRecipes() {
		basicGeneratorFuels();
		maceratingRecipes();
		extrudingRecipes();
		rollingRecipes();
		canningRecipes();
		compressingRecipes();
		separatingRecipes();
		reprocessingRecipes();
		alloySmelterRecipes();
		antimatterBoostRecipes();
		smeltingRecipes();
		blastingRecipes();
		smokingRecipes();
		campfireRecipes();
		shapedRecipes();
		shapelessRecipes();
	}


	private void basicGeneratorFuels() {
		fuel("coal", tag(ItemTags.COALS), 1600);
		fuel("coal_block", Ingredient.of(Items.COAL_BLOCK), 16000);
		fuel("log", tag(ItemTags.LOGS_THAT_BURN), 300);
		fuel("planks", tag(ItemTags.PLANKS), 300);
		fuel("sapling", tag(ItemTags.SAPLINGS), 100);
		fuel("stick", Ingredient.of(Items.STICK), 100);
		fuel("sugar_cane", Ingredient.of(Items.SUGAR_CANE), 100);
		fuel("cactus", Ingredient.of(Items.CACTUS), 200);
		fuel("scrap", Ingredient.of(ICItems.SCRAP.item.get()), 400);
	}

	private void fuel(String name, Ingredient ingredient, int ticks) {
		BasicGeneratorFuelRecipe recipe = new BasicGeneratorFuelRecipe(ingredient, ticks);
		ResourceKey<Recipe<?>> key = recipeKey("basic_generator_fuel/" + name);
		output.accept(key, recipe, null);
	}


	private void maceratingRecipes() {
		macerate("blaze_powder", commonTag("rods/blaze"), stack(Items.BLAZE_POWDER, 5));
		macerate("bone_meal", Ingredient.of(Items.BONE), stack(Items.BONE_MEAL, 5));
		macerate("charcoal_dust", Ingredient.of(Items.CHARCOAL), icStack("charcoal_dust", 1));
		macerate("coal_dust", Ingredient.of(Items.COAL), icStack("coal_dust", 1));
		macerate("cobblestone", commonTag("stones"), stack(Items.COBBLESTONE, 1));
		macerate("gravel", commonTag("cobblestones"), stack(Items.GRAVEL, 1));
		macerate("obsidian_dust", commonTag("obsidians"), icStack("obsidian_dust", 1));
		macerate("sand", commonTag("gravels"), stack(Items.SAND, 1));
		macerate("snowball", Ingredient.of(Items.SNOW_BLOCK), stack(Items.SNOWBALL, 1));
		macerate("string", tag(ItemTags.WOOL), stack(Items.STRING, 4));

		macerate("gems/diamond_to_dust", commonTag("gems/diamond"), icStack("diamond_dust", 1));

		for (String m : MACERATE_INGOTS) {
			macerate("ingots/" + m + "_to_dust", commonTag("ingots/" + m), icStack(m + "_dust", 1));
		}
		for (String m : MACERATE_ORES) {
			String dust = oreDustName(m);
			macerate("ores/" + m + "_to_dust", commonTag("ores/" + m), icStack(dust, 2));
		}
		for (String m : MACERATE_RAW) {
			ItemStackTemplate dust = icStack(m + "_dust", 1);
			macerateRaw("raw_materials/" + m + "_to_dust", commonTag("raw_materials/" + m),
					dust, dust, 0.35);
		}

		// IC2C: vanilla block-family conversions
		macerate("stone_to_gravel", Ingredient.of(Items.STONE), stack(Items.GRAVEL, 1));
		macerate("sandstone_to_sand", Ingredient.of(Items.SANDSTONE), stack(Items.SAND, 1));
		macerate("red_sandstone_to_sand", Ingredient.of(Items.RED_SANDSTONE), stack(Items.RED_SAND, 1));
		macerate("ice_to_snowball", Ingredient.of(Items.ICE), stack(Items.SNOWBALL, 1));

		// IC2C: redstone ore bonus output (bypasses fortune)
		macerate("ores/redstone_to_dust", commonTag("ores/redstone"), stack(Items.REDSTONE, 6));

		// IC2C: storage-block → 9 dust (direct dust extraction from compact storage)
		macerate("storage_blocks/coal_to_dust", Ingredient.of(Items.COAL_BLOCK), icStack("coal_dust", 9));
		for (String m : MACERATE_INGOTS) {
			macerate("storage_blocks/" + m + "_to_dust", commonTag("storage_blocks/" + m), icStack(m + "_dust", 9));
		}
		for (String m : MACERATE_RAW) {
			macerate("storage_blocks/raw_" + m + "_to_dust", commonTag("storage_blocks/raw_" + m), icStack(m + "_dust", 9));
		}

		macerateChance("sticky_resin_from_log", tag(ItemTags.LOGS), 1, icStack("sticky_resin", 1), 0.25D);

		macerate("advanced_alloy_to_mixed_metal_blend",
				Ingredient.of(ICItems.ADVANCED_ALLOY.item.get()),
				new ItemStackTemplate(ICItems.MIXED_METAL_BLEND.item.get(), 1));
	}

	private void macerate(String path, Ingredient input, ItemStackTemplate result) {
		if (result == null) return;
		MachineRecipe recipe = new MachineRecipe(
				ICRecipes.MACERATING,
				List.of(new IngredientWithCount(input, 1)),
				List.of(),
				List.of(new StackWithChance(result, 1D)),
				List.of(),
				1D,
				false);
		output.accept(recipeKey("macerating/" + path), recipe, null);
	}

	private void macerateRaw(String path, Ingredient input, ItemStackTemplate primary, ItemStackTemplate bonus, double chance) {
		if (primary == null || bonus == null) return;
		MachineRecipe recipe = new MachineRecipe(
				ICRecipes.MACERATING,
				List.of(new IngredientWithCount(input, 1)),
				List.of(),
				List.of(new StackWithChance(primary, 1D), new StackWithChance(bonus, chance)),
				List.of(),
				1D,
				false);
		output.accept(recipeKey("macerating/" + path), recipe, null);
	}

	private void macerateChance(String path, Ingredient input, int inputCount, ItemStackTemplate result, double chance) {
		if (input == null || result == null) return;
		MachineRecipe recipe = new MachineRecipe(
				ICRecipes.MACERATING,
				List.of(new IngredientWithCount(input, inputCount)),
				List.of(),
				List.of(new StackWithChance(result, chance)),
				List.of(),
				1D,
				false);
		output.accept(recipeKey("macerating/" + path), recipe, null);
	}


	private static final String[] EXTRUDE_INGOT_METALS = {
			"aluminum", "bronze", "constantan", "copper", "electrum", "enderium", "gold", "invar",
			"iridium", "iron", "lead", "netherite", "nickel", "plutonium", "silver", "steel", "tin",
			"uranium",
	};

	private static final String[] EXTRUDE_PLATE_ROD_MATERIALS = {
			"aluminum", "bronze", "constantan", "copper", "electrum", "enderium", "gold",
			"invar", "iridium", "iron", "lead", "netherite", "nickel", "obsidian", "plutonium",
			"silver", "steel", "tin", "uranium",
	};

	private void extrudingRecipes() {
		// ingots → rod (×2)
		for (String m : EXTRUDE_INGOT_METALS) {
			extrude("ingots/" + m + "_to_" + m + "_rod", commonTag("ingots/" + m), 1,
					resolveShape(m, "rod"), 2);
		}
		// plates (×4) → gear (×1)
		for (String m : EXTRUDE_PLATE_ROD_MATERIALS) {
			extrude("plates/" + m + "_to_" + m + "_gear", commonTag("plates/" + m), 4,
					resolveShape(m, "gear"), 1);
		}
		// rods → wire (×2)
		for (String m : EXTRUDE_PLATE_ROD_MATERIALS) {
			extrude("rods/" + m + "_to_" + m + "_wire", commonTag("rods/" + m), 1,
					resolveShape(m, "wire"), 2);
		}
		extrude("dusts/obsidian_to_obsidian_rod", commonTag("dusts/obsidian"), 1,
				icStack("obsidian_rod", 1), 2);
	}

	private void extrude(String path, Ingredient input, int inputCount, ItemStackTemplate result, int outputCount) {
		if (result == null) return;
		ItemStackTemplate scaled = new ItemStackTemplate(result.item(), outputCount);
		MachineRecipe recipe = new MachineRecipe(
				ICRecipes.EXTRUDING,
				List.of(new IngredientWithCount(input, inputCount)),
				List.of(),
				List.of(new StackWithChance(scaled, 1D)),
				List.of(),
				1D,
				false);
		output.accept(recipeKey("extruding/" + path), recipe, null);
	}

	private ItemStackTemplate resolveShape(String metal, String shape) {
		return icStack(metal + "_" + shape, 1);
	}


	private void rollingRecipes() {
		for (String m : EXTRUDE_INGOT_METALS) {
			ItemStackTemplate plate = resolveShape(m, "plate");
			if (plate == null) continue;
			Ingredient input = commonTag("ingots/" + m);
			MachineRecipe recipe = new MachineRecipe(
					ICRecipes.ROLLING,
					List.of(new IngredientWithCount(input, 1)),
					List.of(),
					List.of(new StackWithChance(plate, 1D)),
					List.of(),
					1D,
					false);
			output.accept(recipeKey("rolling/ingots/" + m + "_to_" + m + "_plate"), recipe, null);
		}
	}


	private void compressingRecipes() {
		compress("carbon_plate", i("ic:carbon_fiber_mesh"), 1, icStack("carbon_plate", 1));
		compress("compressed_coal_ball", i("ic:coal_ball"), 1, icStack("compressed_coal_ball", 1));
		compress("dense_copper_plate", commonTag("plates/copper"), 8, icStack("dense_copper_plate", 1));
		compress("diamond", i("ic:graphene"), 1, stack(Items.DIAMOND, 1));
		compress("ice", i("minecraft:snowball"), 1, stack(Items.ICE, 1));
		compress("industrial_grade_metal", commonTag("ingots/iron"), 3, icStack("industrial_grade_metal", 3));
		compress("paper", i("minecraft:sugar_cane"), 3, stack(Items.PAPER, 5));
		compress("protein_bar", i("ic:canned_food"), 1, icStack("protein_bar", 1));
		compress("snowball", fluidCell(Fluids.WATER), 1, stack(Items.SNOWBALL, 1));

		// IC2C-inspired compressions
		compress("sand_to_sandstone", Ingredient.of(Items.SAND), 4, stack(Items.SANDSTONE, 1));
		compress("red_sand_to_red_sandstone", Ingredient.of(Items.RED_SAND), 4, stack(Items.RED_SANDSTONE, 1));
		compress("netherrack_to_nether_bricks", Ingredient.of(Items.NETHERRACK), 4, stack(Items.NETHER_BRICKS, 1));
		compress("snow_to_ice", Ingredient.of(Items.SNOW_BLOCK), 1, stack(Items.ICE, 1));
		compress("ice_to_packed_ice", Ingredient.of(Items.ICE), 1, stack(Items.PACKED_ICE, 1));
		compress("packed_ice_to_blue_ice", Ingredient.of(Items.PACKED_ICE), 1, stack(Items.BLUE_ICE, 1));
		compress("blaze_powder_to_blaze_rod", Ingredient.of(Items.BLAZE_POWDER), 4, stack(Items.BLAZE_ROD, 1));
		compress("flint_to_gunpowder", Ingredient.of(Items.FLINT), 4, stack(Items.GUNPOWDER, 1));
		compress("diamond_dust_to_diamond", i("ic:diamond_dust"), 2, stack(Items.DIAMOND, 1));
		compress("obsidian_dust_to_obsidian_plate", i("ic:obsidian_dust"), 1, icStack("obsidian_plate", 1));

		compress("rubber_from_resin", i("ic:sticky_resin"), 1, icStack("rubber", 3));
		compress("rubber_from_latex", i("ic:latex_ball"), 2, icStack("rubber", 1));
	}

	private void compress(String path, Ingredient input, int inputCount, ItemStackTemplate result) {
		if (input == null || result == null) return;
		MachineRecipe recipe = new MachineRecipe(
				ICRecipes.COMPRESSING,
				List.of(new IngredientWithCount(input, inputCount)),
				List.of(),
				List.of(new StackWithChance(result, 1D)),
				List.of(),
				1D,
				false);
		output.accept(recipeKey("compressing/" + path), recipe, null);
	}


	private void separatingRecipes() {
		Item silicon = BuiltInRegistries.ITEM.getValue(IC.id("silicon_gem"));
		separate("flint", commonTag("gravels"), 1, Arrays.asList(out(Items.FLINT, 1, 1D)));
		separate("silicon_from_quartz", commonTag("gems/quartz"), 1, Arrays.asList(out(silicon, 3, 1D)));
		separate("silicon_from_sand", tag(ItemTags.SAND), 1, List.of(
				out(silicon, 1, 0.2D),
				out(silicon, 1, 0.05D)));
		separate("slime_ball", Ingredient.of(Items.MAGMA_CREAM), 1, List.of(
				out(Items.SLIME_BALL, 1, 1D),
				out(Items.BLAZE_POWDER, 1, 0.25D)));
		separate("sugar", Ingredient.of(Items.SUGAR_CANE), 1, List.of(
				out(Items.SUGAR, 2, 1D),
				out(Items.PAPER, 1, 0.5D)));
		separate("glowstone_dust", Ingredient.of(Items.SEA_PICKLE), 1, List.of(
				out(Items.SEAGRASS, 1, 0.5D),
				out(Items.GLOWSTONE_DUST, 1, 0.03D)));
		Item smallCoolant = BuiltInRegistries.ITEM.getValue(IC.id("small_coolant_cell"));
		separate("small_coolant_cell", fluidCell(Fluids.WATER), 1,
				Arrays.asList(out(smallCoolant, 1, 1D)));

		// IC2C: ore → gem extraction (bypasses silk touch)
		separate("coal_from_ore", commonTag("ores/coal"), 1, Arrays.asList(out(Items.COAL, 1, 1D)));
		separate("lapis_from_ore", commonTag("ores/lapis"), 1, Arrays.asList(out(Items.LAPIS_LAZULI, 6, 1D)));
		separate("diamond_from_ore", commonTag("ores/diamond"), 1, Arrays.asList(out(Items.DIAMOND, 1, 1D)));
		separate("emerald_from_ore", commonTag("ores/emerald"), 1, Arrays.asList(out(Items.EMERALD, 1, 1D)));
		separate("quartz_from_ore", commonTag("ores/quartz"), 1, Arrays.asList(out(Items.QUARTZ, 1, 1D)));

		// IC2C: flower → dye extraction
		flowerDye("poppy", Items.POPPY, Items.RED_DYE);
		flowerDye("dandelion", Items.DANDELION, Items.YELLOW_DYE);
		flowerDye("blue_orchid", Items.BLUE_ORCHID, Items.LIGHT_BLUE_DYE);
		flowerDye("allium", Items.ALLIUM, Items.MAGENTA_DYE);
		flowerDye("azure_bluet", Items.AZURE_BLUET, Items.LIGHT_GRAY_DYE);
		flowerDye("red_tulip", Items.RED_TULIP, Items.RED_DYE);
		flowerDye("orange_tulip", Items.ORANGE_TULIP, Items.ORANGE_DYE);
		flowerDye("white_tulip", Items.WHITE_TULIP, Items.LIGHT_GRAY_DYE);
		flowerDye("pink_tulip", Items.PINK_TULIP, Items.PINK_DYE);
		flowerDye("oxeye_daisy", Items.OXEYE_DAISY, Items.LIGHT_GRAY_DYE);
		flowerDye("cornflower", Items.CORNFLOWER, Items.BLUE_DYE);
		flowerDye("lily_of_the_valley", Items.LILY_OF_THE_VALLEY, Items.WHITE_DYE);
		flowerDye("wither_rose", Items.WITHER_ROSE, Items.BLACK_DYE);
		flowerDye("beetroot", Items.BEETROOT, Items.RED_DYE);
		flowerDye("sunflower", Items.SUNFLOWER, Items.YELLOW_DYE);
		flowerDye("lilac", Items.LILAC, Items.MAGENTA_DYE);
		flowerDye("rose_bush", Items.ROSE_BUSH, Items.RED_DYE);
		flowerDye("peony", Items.PEONY, Items.PINK_DYE);

		separate("latex_from_saplings", tag(ItemTags.SAPLINGS), 2,
				Arrays.asList(out(ICItems.LATEX_BALL.item.get(), 1, 1D)));
		separate("latex_from_kelp", Ingredient.of(Items.KELP), 6,
				Arrays.asList(out(ICItems.LATEX_BALL.item.get(), 1, 1D)));
	}

	private void flowerDye(String name, Item flower, Item dye) {
		separate(name + "_to_dye", Ingredient.of(flower), 1, Arrays.asList(out(dye, 2, 1D)));
	}

	private StackWithChance out(Item item, int count, double chance) {
		if (item == null || item == Items.AIR) return null;
		return new StackWithChance(new ItemStackTemplate(item, count), chance);
	}

	private void separate(String path, Ingredient input, int inputCount, List<StackWithChance> outputs) {
		if (input == null) return;
		List<StackWithChance> cleaned = new ArrayList<>();
		for (StackWithChance o : outputs) if (o != null) cleaned.add(o);
		if (cleaned.isEmpty()) return;
		MachineRecipe recipe = new MachineRecipe(
				ICRecipes.SEPARATING,
				List.of(new IngredientWithCount(input, inputCount)),
				List.of(),
				cleaned,
				List.of(),
				1D,
				false);
		ItemStackTemplate[] templates = cleaned.stream().map(StackWithChance::template).toArray(ItemStackTemplate[]::new);
		output.accept(recipeKey("separating/" + path), recipe, null);
	}

	private void separateMulti(String path, List<IngredientWithCount> inputs, List<StackWithChance> outputs) {
		if (inputs.isEmpty()) return;
		List<StackWithChance> cleaned = new ArrayList<>();
		for (StackWithChance o : outputs) if (o != null) cleaned.add(o);
		if (cleaned.isEmpty()) return;
		MachineRecipe recipe = new MachineRecipe(
				ICRecipes.SEPARATING,
				inputs,
				List.of(),
				cleaned,
				List.of(),
				1D,
				false);
		ItemStackTemplate[] templates = cleaned.stream().map(StackWithChance::template).toArray(ItemStackTemplate[]::new);
		Ingredient[] ingredients = inputs.stream().map(IngredientWithCount::ingredient).toArray(Ingredient[]::new);
		output.accept(recipeKey("separating/" + path), recipe, null);
	}


	private void reprocessingRecipes() {
		Ingredient input = tag(TagKey.create(Registries.ITEM, IC.id("scrappable")));
		ItemStackTemplate scrap = icStack("scrap", 1);
		if (scrap == null) return;
		MachineRecipe recipe = new MachineRecipe(
				ICRecipes.REPROCESSING,
				List.of(new IngredientWithCount(input, 1)),
				List.of(),
				List.of(new StackWithChance(scrap, 0.125D)),
				List.of(),
				1D,
				false);
		output.accept(recipeKey("reprocessing/scrappable_to_scrap"), recipe, null);
	}


	private void alloySmelterRecipes() {
		alloy("netherite_ingot",
				List.of(new IngredientWithCount(commonTag("ingots/gold"), 2),
						new IngredientWithCount(Ingredient.of(Items.NETHERITE_SCRAP), 2)),
				new ItemStackTemplate(Items.NETHERITE_INGOT, 1));
		alloy("enderium_ingot",
				List.of(new IngredientWithCount(commonTag("ingots/lead"), 3),
						new IngredientWithCount(commonTag("dusts/diamond"), 1),
						new IngredientWithCount(Ingredient.of(Items.ENDER_PEARL), 2)),
				icStack("enderium_ingot", 2));
		alloy("bronze_ingot",
				List.of(new IngredientWithCount(commonTag("ingots/copper"), 3),
						new IngredientWithCount(commonTag("ingots/tin"), 1)),
				icStack("bronze_ingot", 4));
		alloy("electrum_ingot",
				List.of(new IngredientWithCount(commonTag("ingots/silver"), 1),
						new IngredientWithCount(commonTag("ingots/gold"), 1)),
				icStack("electrum_ingot", 2));
		alloy("invar_ingot",
				List.of(new IngredientWithCount(commonTag("ingots/iron"), 2),
						new IngredientWithCount(commonTag("ingots/nickel"), 1)),
				icStack("invar_ingot", 3));
		alloy("constantan_ingot",
				List.of(new IngredientWithCount(commonTag("ingots/copper"), 1),
						new IngredientWithCount(commonTag("ingots/nickel"), 1)),
				icStack("constantan_ingot", 2));
		alloy("steel_ingot_from_coal",
				List.of(new IngredientWithCount(Ingredient.of(ICItems.INDUSTRIAL_GRADE_METAL.item.get()), 1),
						new IngredientWithCount(Ingredient.of(Items.COAL), 1)),
				icStack("steel_ingot", 1));
		alloy("steel_ingot_from_charcoal",
				List.of(new IngredientWithCount(Ingredient.of(ICItems.INDUSTRIAL_GRADE_METAL.item.get()), 1),
						new IngredientWithCount(Ingredient.of(Items.CHARCOAL), 1)),
				icStack("steel_ingot", 1));

		ItemStackTemplate advancedAlloy = new ItemStackTemplate(ICItems.ADVANCED_ALLOY.item.get(), 1);
		alloy("advanced_alloy_steel_bronze_aluminum",
				List.of(new IngredientWithCount(commonTag("ingots/steel"), 1),
						new IngredientWithCount(commonTag("ingots/bronze"), 2),
						new IngredientWithCount(commonTag("ingots/aluminum"), 1)),
				advancedAlloy);
		alloy("advanced_alloy_steel_electrum_aluminum",
				List.of(new IngredientWithCount(commonTag("ingots/steel"), 1),
						new IngredientWithCount(commonTag("ingots/electrum"), 1),
						new IngredientWithCount(commonTag("ingots/aluminum"), 1)),
				advancedAlloy);
		alloy("advanced_alloy_steel_invar_aluminum",
				List.of(new IngredientWithCount(commonTag("ingots/steel"), 1),
						new IngredientWithCount(commonTag("ingots/invar"), 2),
						new IngredientWithCount(commonTag("ingots/aluminum"), 1)),
				advancedAlloy);
	}

	private void alloy(String path, List<IngredientWithCount> inputs, ItemStackTemplate result) {
		if (result == null) return;
		MachineRecipe recipe = new MachineRecipe(
				ICRecipes.ALLOY_SMELTING,
				inputs,
				List.of(),
				List.of(new StackWithChance(result, 1D)),
				List.of(),
				1D,
				false);
		output.accept(recipeKey("alloy_smelting/" + path), recipe, null);
	}


	private void antimatterBoostRecipes() {
		antimatterBoost("scrap", i("ic:scrap"), 5000D);
		antimatterBoost("scrap_box", i("ic:scrap_box"), 45000D);
	}

	private void antimatterBoost(String path, Ingredient input, double boost) {
		if (input == null) return;
		AntimatterBoostRecipe recipe =
				new AntimatterBoostRecipe(input, boost);
		output.accept(recipeKey("antimatter_boost/" + path), recipe, null);
	}


	private static final String[] CANNABLE_FOODS = {
			"apple", "baked_potato", "beef", "beetroot_soup", "bread", "carrot", "chicken",
			"chorus_fruit", "cod", "cooked_beef", "cooked_chicken", "cooked_cod", "cooked_mutton",
			"cooked_porkchop", "cooked_rabbit", "cooked_salmon", "cookie", "glow_berries",
			"golden_carrot", "melon_slice", "mushroom_stew", "mutton", "porkchop", "pumpkin_pie",
			"rabbit", "rabbit_stew", "salmon", "sweet_berries",
	};

	private void canningRecipes() {
		Item emptyCan = ICItems.EMPTY_CAN.item.get();
		Ingredient emptyCanIng = Ingredient.of(emptyCan);
		ItemStackTemplate cannedFood = new ItemStackTemplate(
				ICItems.CANNED_FOOD.get(), 1);

		for (String food : CANNABLE_FOODS) {
			Ingredient foodIng = i("minecraft:" + food);
			if (foodIng == null) continue;
			canning(food, foodIng, emptyCanIng, cannedFood);
		}

		// Spray paint cans — use a FluidCellIngredient (water).
		Ingredient waterCell = fluidCell(Fluids.WATER);
		canning("light_spray_can", waterCell, commonTag("dyes/white"),
				new ItemStackTemplate(ICItems.LIGHT_SPRAY_PAINT_CAN.get(), 1));
		canning("dark_spray_can", waterCell, commonTag("dyes/black"),
				new ItemStackTemplate(ICItems.DARK_SPRAY_PAINT_CAN.get(), 1));

		// Uranium fuel rod — water cell + uranium dust.
		canning("uranium_fuel_rod", waterCell, commonTag("dusts/uranium"),
				new ItemStackTemplate(ICItems.URANIUM_FUEL_ROD.get(), 1));
	}

	private void canning(String path, Ingredient a, Ingredient b, ItemStackTemplate result) {
		if (a == null || b == null || result == null) return;
		MachineRecipe recipe = new MachineRecipe(
				ICRecipes.CANNING,
				List.of(new IngredientWithCount(a, 1), new IngredientWithCount(b, 1)),
				List.of(),
				List.of(new StackWithChance(result, 1D)),
				List.of(),
				1D,
				false);
		output.accept(recipeKey("canning/" + path), recipe, null);
	}

	private static Ingredient fluidCell(Fluid fluid) {
		return new Ingredient(new FluidCellIngredient(fluid));
	}


	private void smeltingRecipes() {
		cookSmelt("rubber", commonTag("slime_balls"), stack(ICItems.RUBBER.item.get(), 8), 0F, 300);
		cookSmelt("advanced_alloy", Ingredient.of(ICItems.MIXED_METAL_BLEND.item.get()),
				stack(ICItems.ADVANCED_ALLOY.item.get(), 1), 0F, 400);
		cookSmelt("industrial_grade_metal", commonTag("ingots/iron"),
				stack(ICItems.INDUSTRIAL_GRADE_METAL.item.get(), 1), 0F, 400);

		materialFurnaceRecipes(true);
	}

	private void blastingRecipes() {
		cookBlast("advanced_alloy", Ingredient.of(ICItems.MIXED_METAL_BLEND.item.get()),
				stack(ICItems.ADVANCED_ALLOY.item.get(), 1), 0F, 200);
		cookBlast("industrial_grade_metal", commonTag("ingots/iron"),
				stack(ICItems.INDUSTRIAL_GRADE_METAL.item.get(), 1), 0F, 200);

		materialFurnaceRecipes(false);
	}

	private void materialFurnaceRecipes(boolean smelt) {
		for (Material m : Material.values()) {
			ItemStackTemplate ingot = ingotFor(m);
			if (ingot == null) continue;

			if (m.has(MaterialComponent.DUST)) {
				materialCook(smelt, m.key() + "_dust_to_" + ingotPathSuffix(m),
						Ingredient.of(icItem(m.key() + "_dust")), ingot, 0F);
			}
			if (m.has(MaterialComponent.STONE_ORE)) {
				materialCook(smelt, m.key() + "_stone_ore_to_" + ingotPathSuffix(m),
						Ingredient.of(icItem(m.key() + "_stone_ore")), ingot, 0.7F);
			}
			if (m.has(MaterialComponent.DEEPSLATE_ORE)) {
				materialCook(smelt, m.key() + "_deepslate_ore_to_" + ingotPathSuffix(m),
						Ingredient.of(icItem(m.key() + "_deepslate_ore")), ingot, 0.7F);
			}
			if (m.has(MaterialComponent.RAW_ORE)) {
				materialCook(smelt, m.key() + "_raw_ore_to_" + ingotPathSuffix(m),
						Ingredient.of(icItem(m.key() + "_raw_ore")), ingot, 0.7F);
			}
		}

		ItemStackTemplate enderiumIngot = icStack("enderium_ingot", 1);
		Item enderiumDust = icItem("enderium_dust");
		if (enderiumIngot != null && enderiumDust != null) {
			materialCook(smelt, "enderium_dust_to_enderium_ingot",
					Ingredient.of(enderiumDust), enderiumIngot, 0F);
		}
	}

	private ItemStackTemplate ingotFor(Material m) {
		return switch (m) {
			case COPPER -> stack(Items.COPPER_INGOT, 1);
			case GOLD -> stack(Items.GOLD_INGOT, 1);
			case IRON -> stack(Items.IRON_INGOT, 1);
			default -> m.has(MaterialComponent.INGOT) ? icStack(m.key() + "_ingot", 1) : null;
		};
	}

	private static String ingotPathSuffix(Material m) {
		return switch (m) {
			case COPPER -> "copper_ingot";
			case GOLD -> "gold_ingot";
			case IRON -> "iron_ingot";
			default -> m.key() + "_ingot";
		};
	}

	private void materialCook(boolean smelt, String path, Ingredient input, ItemStackTemplate result, float xp) {
		if (input == null || result == null) return;
		int ticks = smelt ? 200 : 100;
		String folder = smelt ? "smelting" : "blasting";
		Recipe<?> r = smelt
				? new SmeltingRecipe(cookingCommon(), cookingBook(CookingBookCategory.MISC), input, result, xp, ticks)
				: new BlastingRecipe(cookingCommon(), cookingBook(CookingBookCategory.MISC), input, result, xp, ticks);
		output.accept(recipeKey(folder + "/" + path), r, null);
	}

	private static Item icItem(String path) {
		Item item = BuiltInRegistries.ITEM.getValue(IC.id(path));
		return item == Items.AIR ? null : item;
	}

	private void smokingRecipes() {
		cookSmoke("rubber", commonTag("slime_balls"), stack(ICItems.RUBBER.item.get(), 8), 0F, 150);
	}

	private void campfireRecipes() {
		cookCampfire("rubber", commonTag("slime_balls"), stack(ICItems.RUBBER.item.get(), 8), 0F, 600);
	}

	private void cookSmelt(String path, Ingredient input, ItemStackTemplate result, float xp, int ticks) {
		if (result == null) return;
		Recipe<?> r = new SmeltingRecipe(cookingCommon(), cookingBook(CookingBookCategory.MISC), input, result, xp, ticks);
		output.accept(recipeKey("smelting/" + path), r, null);
	}

	private void cookBlast(String path, Ingredient input, ItemStackTemplate result, float xp, int ticks) {
		if (result == null) return;
		Recipe<?> r = new BlastingRecipe(cookingCommon(), cookingBook(CookingBookCategory.MISC), input, result, xp, ticks);
		output.accept(recipeKey("blasting/" + path), r, null);
	}

	private void cookSmoke(String path, Ingredient input, ItemStackTemplate result, float xp, int ticks) {
		if (result == null) return;
		Recipe<?> r = new SmokingRecipe(cookingCommon(), cookingBook(CookingBookCategory.FOOD), input, result, xp, ticks);
		output.accept(recipeKey("smoking/" + path), r, null);
	}

	private void cookCampfire(String path, Ingredient input, ItemStackTemplate result, float xp, int ticks) {
		if (result == null) return;
		Recipe<?> r = new CampfireCookingRecipe(cookingCommon(), cookingBook(CookingBookCategory.FOOD), input, result, xp, ticks);
		output.accept(recipeKey("campfire_cooking/" + path), r, null);
	}

	private static Recipe.CommonInfo cookingCommon() {
		return new Recipe.CommonInfo(false);
	}

	private static AbstractCookingRecipe.CookingBookInfo cookingBook(CookingBookCategory category) {
		return new AbstractCookingRecipe.CookingBookInfo(category, "");
	}



	private void shapedRecipes() {
		// The bulk of shaped crafting recipes live here, transpiled from the original JSONs.
		// See the shaped(...) helper below; 'i' takes an item id, 'commonOrTag' a tag path,
		// 'compound' an OR of sub-ingredients, and 'icStack' / 'modStack' build the output.
		shapedBatch();
	}

	private void shapelessRecipes() {
		shapelessBatch();

		shapeless("guide", icStack("guide", 1), i("minecraft:book"), i("minecraft:redstone"));
	}

	protected Ingredient i(String id) {
		Identifier rid = Identifier.parse(id);
		Item item = BuiltInRegistries.ITEM.getValue(rid);
		if (item == null || item == Items.AIR) return null;
		return Ingredient.of(item);
	}

	protected Ingredient commonOrTag(String path) {
		Identifier rid = Identifier.parse(path.contains(":") ? path : ("c:" + path));
		return tag(TagKey.create(Registries.ITEM, rid));
	}

	protected static Ingredient compound(Ingredient... children) {
		return CompoundIngredient.of(children);
	}

	protected static ItemStackTemplate icStack(String path, int count) {
		return modStack("ic:" + path, count);
	}

	protected static ItemStackTemplate modStack(String id, int count) {
		Identifier rid = Identifier.parse(id);
		Item item = BuiltInRegistries.ITEM.getValue(rid);
		if (item == null || item == Items.AIR) return null;
		return new ItemStackTemplate(item, count);
	}

	protected void shaped(String name, ItemStackTemplate result, String[] pattern, Object... pairs) {
		if (result == null) return;
		Map<Character, Ingredient> key = new HashMap<>();
		List<Ingredient> allIngredients = new ArrayList<>();
		for (int i = 0; i < pairs.length; i += 2) {
			Character ch = (Character) pairs[i];
			Ingredient ing = (Ingredient) pairs[i + 1];
			if (ing == null) return;
			key.put(ch, ing);
			allIngredients.add(ing);
		}
		ShapedRecipePattern shaped = ShapedRecipePattern.of(key, pattern);
		ShapedRecipe recipe = new ShapedRecipe(
				new Recipe.CommonInfo(false),
				new CraftingRecipe.CraftingBookInfo(CraftingBookCategory.MISC, ""),
				shaped,
				result);
		output.accept(recipeKey("shaped/" + name), recipe, null);
	}

	protected void shapeless(String name, ItemStackTemplate result, Ingredient... ingredients) {
		if (result == null) return;
		for (Ingredient ing : ingredients) {
			if (ing == null) return;
		}
		ShapelessRecipe recipe = new ShapelessRecipe(
				new Recipe.CommonInfo(false),
				new CraftingRecipe.CraftingBookInfo(CraftingBookCategory.MISC, ""),
				result,
				List.of(ingredients));
		output.accept(recipeKey("shapeless/" + name), recipe, null);
	}


	private void machineRecipe(String path, MachineRecipeType type, Ingredient input, ItemStackTemplate result) {
		MachineRecipe recipe = new MachineRecipe(
				type,
				List.of(new IngredientWithCount(input, 1)),
				List.of(),
				List.of(new StackWithChance(result, 1D)),
				List.of(),
				1D,
				false);
		output.accept(recipeKey(path), recipe, null);
	}

	private static ItemStackTemplate stack(Item item, int count) {
		return new ItemStackTemplate(item, count);
	}


	private static String oreDustName(String oreName) {
		return switch (oreName) {
			case "apatite", "fluorite", "monazite", "niter", "salt", "sulfur",
				 "diamond", "emerald", "lapis_lazuli", "ruby", "sapphire", "coal", "quartz" ->
					oreName + "_dust";
			default -> oreName + "_dust";
		};
	}

	protected Ingredient tag(TagKey<Item> tag) {
		return Ingredient.of(items.getOrThrow(tag));
	}

	protected Ingredient commonTag(String path) {
		return tag(TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", path)));
	}

	private static ResourceKey<Recipe<?>> recipeKey(String path) {
		return ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(IC.MOD_ID, path));
	}


	private void shapedBatch() {
		shaped("advanced_centrifuge", icStack("advanced_centrifuge", 1), new String[] {"CCC", "CFC", "WMW"}, 'C', i("ic:fluid_cell"), 'F', i("ic:centrifuge"), 'M', i("ic:advanced_machine_block"), 'W', i("ic:copper_coil"));
		shaped("advanced_compressor", icStack("advanced_compressor", 1), new String[] {"CCC", "CFC", "WMW"}, 'C', i("minecraft:obsidian"), 'F', i("ic:compressor"), 'M', i("ic:advanced_machine_block"), 'W', i("ic:copper_coil"));
		shaped("advanced_heat_exchanger", icStack("advanced_heat_exchanger", 1), new String[] {"WCW", "VBV", "WCW"}, 'C', i("ic:electronic_circuit"), 'B', i("ic:dense_copper_plate"), 'V', i("ic:heat_exchanger"), 'W', i("ic:iv_cable"));
		shaped("advanced_heat_vent", icStack("advanced_heat_vent", 1), new String[] {"IVI", "IDI", "IVI"}, 'I', i("minecraft:iron_bars"), 'D', commonOrTag("c:gems/diamond"), 'V', i("ic:heat_vent"));
		shaped("advanced_macerator", icStack("advanced_macerator", 1), new String[] {"CCC", "CFC", "WMW"}, 'C', i("ic:industrial_grade_metal"), 'F', i("ic:macerator"), 'M', i("ic:advanced_machine_block"), 'W', i("ic:copper_coil"));
		shaped("advanced_powered_furnace", icStack("advanced_powered_furnace", 1), new String[] {"CCC", "CFC", "WMW"}, 'C', commonOrTag("c:ingots/copper"), 'F', i("ic:powered_furnace"), 'M', i("ic:advanced_machine_block"), 'W', i("ic:copper_coil"));
		shaped("alloy_smelter", icStack("alloy_smelter", 1), new String[] {"CAC", "FWF", "CDC"}, 'C', i("ic:carbon_plate"), 'A', i("ic:advanced_circuit"), 'F', i("ic:powered_furnace"), 'W', i("ic:copper_coil"), 'D', commonOrTag("c:gems/diamond"));
		shaped("antimatter_crystal", icStack("antimatter_crystal", 1), new String[] {"AAC", "ANA", "CAA"}, 'A', i("ic:antimatter"), 'C', i("ic:energy_crystal"), 'N', i("minecraft:nether_star"));
		shaped("antimatter_fabricator", icStack("antimatter_constructor", 1), new String[] {"GCG", "MEM", "GCG"}, 'G', commonOrTag("c:dusts/glowstone"), 'M', i("ic:advanced_machine_block"), 'C', i("ic:iridium_circuit"), 'E', i("minecraft:nether_star"));
		shaped("basic_generator", icStack("basic_generator", 1), new String[] {" B ", "MMM", " F "}, 'B', i("ic:lv_battery"), 'M', i("ic:industrial_grade_metal"), 'F', i("ic:iron_furnace"));
		shaped("battery", icStack("lv_battery", 1), new String[] {" C ", "TRT", "TRT"}, 'C', i("ic:lv_cable"), 'T', commonOrTag("c:ingots/tin"), 'R', commonOrTag("c:dusts/redstone"));
		shaped("bronze_dust", icStack("bronze_dust", 4), new String[] {"CC", "CT"}, 'C', commonOrTag("c:dusts/copper"), 'T', commonOrTag("c:dusts/tin"));
		shaped("canning_machine", icStack("canning_machine", 1), new String[] {"TCT", "TMT", "TTT"}, 'T', commonOrTag("c:ingots/tin"), 'M', i("ic:machine_block"), 'C', i("ic:electronic_circuit"));
		shaped("carbon_boots", icStack("carbon_boots", 1), new String[] {"CEC", "CAC"}, 'C', i("ic:carbon_plate"), 'E', i("ic:energy_crystal"), 'A', i("minecraft:netherite_boots"));
		shaped("carbon_chestplate", icStack("carbon_chestplate", 1), new String[] {"CEC", "CAC", "CCC"}, 'C', i("ic:carbon_plate"), 'E', i("ic:hv_battery"), 'A', i("minecraft:netherite_chestplate"));
		shaped("carbon_fibers", icStack("carbon_fibers", 1), new String[] {"DD", "DD"}, 'D', commonOrTag("c:dusts/coal"));
		shaped("carbon_helmet", icStack("carbon_helmet", 1), new String[] {"CEC", "CAC"}, 'C', i("ic:carbon_plate"), 'E', i("ic:energy_crystal"), 'A', i("minecraft:netherite_helmet"));
		shaped("carbon_leggings", icStack("carbon_leggings", 1), new String[] {"CEC", "CAC", "C C"}, 'C', i("ic:carbon_plate"), 'E', i("ic:energy_crystal"), 'A', i("minecraft:netherite_leggings"));
		shaped("centrifuge", icStack("centrifuge", 1), new String[] {"GMG", "GCG"}, 'G', i("minecraft:glass_bottle"), 'M', i("ic:machine_block"), 'C', i("ic:electronic_circuit"));
		shaped("charge_pad", icStack("charge_pad", 1), new String[] {"WWW", "CMC"}, 'M', i("ic:machine_block"), 'C', i("ic:advanced_circuit"), 'W', i("ic:copper_coil"));
		shaped("coal_ball", icStack("coal_ball", 1), new String[] {"CCC", "CFC", "CCC"}, 'C', commonOrTag("minecraft:coals"), 'F', i("minecraft:flint"));
		shaped("component_heat_exchanger", icStack("component_heat_exchanger", 1), new String[] {" P ", "PVP", " P "}, 'P', commonOrTag("c:plates/tin"), 'V', i("ic:heat_exchanger"));
		shaped("component_heat_vent", icStack("component_heat_vent", 1), new String[] {"IPI", "PVP", "IPI"}, 'I', i("minecraft:iron_bars"), 'P', commonOrTag("c:plates/tin"), 'V', i("ic:heat_vent"));
		shaped("compressor", icStack("compressor", 1), new String[] {"S S", "SMS", "SCS"}, 'S', commonOrTag("c:stones"), 'M', i("ic:machine_block"), 'C', i("ic:electronic_circuit"));
		shaped("copper_coil", icStack("copper_coil", 1), new String[] {"WWW", "WRW", "WWW"}, 'W', commonOrTag("c:wires/copper"), 'R', commonOrTag("c:rods/iron"));
		shaped("dual_uranium_fuel_rod", icStack("dual_uranium_fuel_rod", 1), new String[] {"RMR"}, 'R', i("ic:uranium_fuel_rod"), 'M', i("ic:dense_copper_plate"));
		shaped("ejector_upgrade", icStack("ejector_upgrade", 1), new String[] {"T T", " P ", "T T"}, 'P', i("minecraft:piston"), 'T', commonOrTag("c:ingots/tin"));
		shaped("electric_furnace", icStack("powered_furnace", 1), new String[] {" C ", "RFR"}, 'C', i("ic:electronic_circuit"), 'R', commonOrTag("c:dusts/redstone"), 'F', i("ic:iron_furnace"));
		shaped("empty_can", icStack("empty_can", 10), new String[] {"T T", "TTT"}, 'T', commonOrTag("c:ingots/tin"));
		shaped("enderium_ingot_to_enderium_block", icStack("enderium_block", 1), new String[] {"XXX", "XXX", "XXX"}, 'X', commonOrTag("c:ingots/enderium"));
		shaped("energy_storage_upgrade", icStack("energy_storage_upgrade", 1), new String[] {"PPP", "WBW", "PCP"}, 'P', commonOrTag("minecraft:planks"), 'W', i("ic:lv_cable"), 'B', i("ic:lv_battery"), 'C', i("ic:electronic_circuit"));
		shaped("ev_battery_box", icStack("ev_battery_box", 1), new String[] {"GCG", "EXE", "GMG"}, 'C', i("ic:iridium_circuit"), 'G', i("ic:advanced_alloy"), 'E', i("ic:antimatter_crystal"), 'X', i("ic:hv_battery_box"), 'M', i("ic:advanced_machine_block"));
		shaped("ev_cable", icStack("ev_cable", 6), new String[] {"RRR", "MMM", "RRR"}, 'R', i("ic:rubber"), 'M', commonOrTag("c:ingots/enderium"));
		shaped("ev_rectifier", icStack("ev_rectifier", 1), new String[] {"RWR", "PTP", "RWR"}, 'R', i("minecraft:redstone_block"), 'W', i("ic:ev_cable"), 'P', commonOrTag("c:ingots/iron"), 'T', i("ic:ev_transformer"));
		shaped("ev_solar_panel", icStack("ev_solar_panel", 1), new String[] {"SAS", "SCS", "SNS"}, 'S', i("ic:hv_solar_panel"), 'A', i("ic:antimatter"), 'C', i("ic:large_coolant_cell"), 'N', i("minecraft:netherite_block"));
		shaped("ev_transformer", icStack("ev_transformer", 1), new String[] {" W ", "CTE", " W "}, 'W', i("ic:ev_cable"), 'C', i("ic:advanced_circuit"), 'E', i("ic:advanced_alloy"), 'T', i("ic:hv_transformer"));
		shaped("extruder", icStack("extruder", 1), new String[] {"SCS", "SMS"}, 'S', commonOrTag("c:rods/iron"), 'M', i("ic:machine_block"), 'C', i("ic:electronic_circuit"));
		shaped("fluid_cell", icStack("fluid_cell", 4), new String[] {" T ", "T T", " T "}, 'T', commonOrTag("c:ingots/tin"));
		shaped("fuse", icStack("fuse", 24), new String[] {"GGG", "MMM", "GGG"}, 'G', commonOrTag("c:glass_blocks/colorless"), 'M', i("ic:industrial_grade_metal"));
		shaped("geothermal_generator", icStack("geothermal_generator", 1), new String[] {"LCL", "LCL", "MGM"}, 'L', commonOrTag("c:glass_blocks/colorless"), 'C', i("ic:fluid_cell"), 'M', i("ic:industrial_grade_metal"), 'G', i("ic:basic_generator"));
		shaped("graphene", icStack("graphene", 1), new String[] {"CCC", "COC", "CCC"}, 'C', i("ic:compressed_coal_ball"), 'O', commonOrTag("c:obsidians"));
		shaped("heat_exchanger", icStack("heat_exchanger", 1), new String[] {" C ", "PBP", " P "}, 'C', i("ic:electronic_circuit"), 'B', i("ic:dense_copper_plate"), 'P', commonOrTag("c:plates/iron"));
		shaped("heat_vent", icStack("heat_vent", 1), new String[] {"MIM", "ICI", "MIM"}, 'I', i("minecraft:iron_bars"), 'C', i("ic:copper_coil"), 'M', i("ic:industrial_grade_metal"));
		shaped("hv_battery_box", icStack("hv_battery_box", 1), new String[] {"GCG", "EXE", "GMG"}, 'C', i("ic:advanced_circuit"), 'G', i("ic:graphene"), 'E', i("ic:energy_crystal"), 'X', i("ic:mv_battery_box"), 'M', i("ic:advanced_machine_block"));
		shaped("hv_cable", icStack("hv_cable", 6), new String[] {"RRR", "MMM", "RRR"}, 'R', i("ic:rubber"), 'M', commonOrTag("c:ingots/gold"));
		shaped("hv_rectifier", icStack("hv_rectifier", 1), new String[] {"RWR", "PTP", "RWR"}, 'R', i("minecraft:redstone"), 'W', i("ic:hv_cable"), 'P', commonOrTag("c:ingots/iron"), 'T', i("ic:hv_transformer"));
		shaped("hv_solar_panel", icStack("hv_solar_panel", 1), new String[] {"SGS", "SAS", "SCS"}, 'S', i("ic:mv_solar_panel"), 'G', i("ic:graphene"), 'A', i("ic:advanced_machine_block"), 'C', i("ic:iridium_circuit"));
		shaped("hv_transformer", icStack("hv_transformer", 1), new String[] {" W ", "CTE", " W "}, 'W', i("ic:hv_cable"), 'C', i("ic:electronic_circuit"), 'E', i("ic:energy_crystal"), 'T', i("ic:mv_transformer"));
		shaped("ingots/enderium_to_enderium_gear", icStack("enderium_gear", 1), new String[] {" X ", "XIX", " X "}, 'X', commonOrTag("c:ingots/enderium"), 'I', i("minecraft:iron_nugget"));
		shaped("ingots/enderium_to_enderium_rod", icStack("enderium_rod", 1), new String[] {" X ", " X "}, 'X', commonOrTag("c:ingots/enderium"));
		shaped("iridium_alloy", icStack("iridium_alloy", 1), new String[] {"IAI", "ADA", "IAI"}, 'I', commonOrTag("c:ingots/iridium"), 'A', i("ic:advanced_alloy"), 'D', commonOrTag("c:gems/diamond"));
		shaped("iridium_neutron_reflector", icStack("iridium_neutron_reflector", 1), new String[] {"NNN", "NPN", "NNN"}, 'N', i("ic:thick_neutron_reflector"), 'P', i("ic:iridium_alloy"));
		shaped("iron_furnace", icStack("iron_furnace", 1), new String[] {" I ", "I I", "IFI"}, 'I', commonOrTag("c:ingots/iron"), 'F', i("minecraft:furnace"));
		shaped("iron_rod", icStack("iron_rod", 1), new String[] {"I", "I"}, 'I', commonOrTag("c:ingots/iron"));
		shaped("iv_cable", icStack("iv_cable", 6), new String[] {"GGG", " C ", "GGG"}, 'G', commonOrTag("c:glass_blocks/colorless"), 'C', i("ic:energy_crystal"));
		shaped("iv_rectifier", icStack("iv_rectifier", 1), new String[] {"RWR", "PTP", "RWR"}, 'R', i("minecraft:redstone_block"), 'W', i("ic:ev_cable"), 'P', commonOrTag("c:ingots/iridium"), 'T', i("ic:ev_rectifier"));
		shaped("large_coolant_cell", icStack("large_coolant_cell", 1), new String[] {"TCT", "TAT", "TCT"}, 'T', commonOrTag("c:ingots/tin"), 'C', i("ic:medium_coolant_cell"), 'A', i("ic:dense_copper_plate"));
		shaped("location_card", icStack("location_card", 1), new String[] {" P ", "PCP", " P "}, 'P', i("minecraft:paper"), 'C', i("ic:electronic_circuit"));
		shaped("lv_battery_box", icStack("lv_battery_box", 1), new String[] {"PWP", "BBB", "PPP"}, 'W', i("ic:lv_cable"), 'B', i("ic:lv_battery"), 'P', commonOrTag("minecraft:planks"));
		shaped("lv_cable", icStack("lv_cable", 6), new String[] {"RRR", "MMM", "RRR"}, 'R', i("ic:rubber"), 'M', commonOrTag("c:ingots/copper"));
		shaped("lv_rectifier", icStack("lv_rectifier", 1), new String[] {"RWR", "PTP", "RWR"}, 'R', i("minecraft:redstone"), 'W', i("ic:lv_cable"), 'P', commonOrTag("c:ingots/copper"), 'T', i("ic:lv_transformer"));
		shaped("lv_solar_panel", icStack("lv_solar_panel", 1), new String[] {"LLL", "DSD", "CGC"}, 'L', commonOrTag("c:glass_blocks/colorless"), 'D', commonOrTag("c:dusts/coal"), 'S', commonOrTag("c:silicon"), 'G', i("ic:basic_generator"), 'C', i("ic:electronic_circuit"));
		shaped("lv_transformer", icStack("lv_transformer", 1), new String[] {"PWP", "CCC", "PWP"}, 'W', i("ic:lv_cable"), 'C', commonOrTag("c:ingots/copper"), 'P', commonOrTag("minecraft:planks"));
		shaped("macerator", icStack("macerator", 1), new String[] {"FFF", "SMS", " C "}, 'F', i("minecraft:flint"), 'S', commonOrTag("c:cobblestones"), 'M', i("ic:machine_block"), 'C', i("ic:electronic_circuit"));
		shaped("machine_block", icStack("machine_block", 1), new String[] {"MMM", "MFM", "MMM"}, 'M', i("ic:industrial_grade_metal"), 'F', i("ic:fuse"));
		shaped("mechanical_elytra", icStack("mechanical_elytra", 1), new String[] {"CBC", "CSC", "VLV"}, 'C', i("ic:carbon_plate"), 'B', i("ic:mv_battery"), 'L', i("minecraft:elytra"), 'S', i("ic:lv_solar_panel"), 'V', i("ic:heat_vent"));
		shaped("medium_coolant_cell", icStack("medium_coolant_cell", 1), new String[] {"TTT", "CCC", "TTT"}, 'T', commonOrTag("c:ingots/tin"), 'C', i("ic:small_coolant_cell"));
		shaped("mv_battery_box", icStack("mv_battery_box", 1), new String[] {"WBW", "BMB", "WBW"}, 'W', i("ic:mv_cable"), 'B', i("ic:energy_crystal"), 'M', i("ic:machine_block"));
		shaped("mv_cable", icStack("mv_cable", 6), new String[] {"RRR", "MMM", "RRR"}, 'R', i("ic:rubber"), 'M', commonOrTag("c:ingots/aluminum"));
		shaped("mv_rectifier", icStack("mv_rectifier", 1), new String[] {"RWR", "PTP", "RWR"}, 'R', i("minecraft:redstone"), 'W', i("ic:mv_cable"), 'P', commonOrTag("c:ingots/iron"), 'T', i("ic:mv_transformer"));
		shaped("mv_solar_panel", icStack("mv_solar_panel", 1), new String[] {"SES", "SAS", "SCS"}, 'S', i("ic:lv_solar_panel"), 'E', i("ic:energy_crystal"), 'A', i("ic:advanced_alloy"), 'C', i("ic:advanced_circuit"));
		shaped("mv_transformer", icStack("mv_transformer", 1), new String[] {"W", "M", "W"}, 'W', i("ic:mv_cable"), 'M', i("ic:machine_block"));
		shaped("neutron_reflector", icStack("neutron_reflector", 1), new String[] {"TCT", "TPT", "TCT"}, 'T', commonOrTag("c:dusts/tin"), 'C', commonOrTag("c:dusts/coal"), 'P', i("ic:dense_copper_plate"));
		shaped("nuclear_reactor", icStack("nuclear_reactor", 1), new String[] {"HCH", "HHH", "HGH"}, 'H', i("ic:nuclear_reactor_chamber"), 'C', i("ic:iridium_circuit"), 'G', i("ic:basic_generator"));
		shaped("nuclear_reactor_chamber", icStack("nuclear_reactor_chamber", 3), new String[] {"HPH", "PMP", "HPH"}, 'M', i("ic:advanced_machine_block"), 'P', i("ic:dense_copper_plate"), 'H', i("ic:reactor_plating"));
		shaped("powered_crafting_table", icStack("powered_crafting_table", 1), new String[] {"PCP", "PMP", "PTP"}, 'P', tag(ItemTags.PLANKS), 'C', i("ic:electronic_circuit"), 'M', i("ic:machine_block"), 'T', i("minecraft:crafting_table"));
		shaped("nuggets/enderium_to_enderium_ingot", icStack("enderium_ingot", 1), new String[] {"XXX", "XXX", "XXX"}, 'X', commonOrTag("c:nuggets/enderium"));
		shaped("nuke", icStack("nuke", 1), new String[] {"UCU", "TMT", "UCU"}, 'T', i("minecraft:tnt"), 'U', i("ic:quad_uranium_fuel_rod"), 'M', i("ic:advanced_machine_block"), 'C', i("ic:advanced_circuit"));
		shaped("overclocked_heat_vent", icStack("overclocked_heat_vent", 1), new String[] {"P", "V", "P"}, 'P', commonOrTag("c:plates/enderium"), 'V', i("ic:reactor_heat_vent"));
		shaped("overclocker_upgrade", icStack("overclocker_upgrade", 1), new String[] {"UUU", "WCW"}, 'U', i("ic:small_coolant_cell"), 'W', i("ic:lv_cable"), 'C', i("ic:electronic_circuit"));
		shaped("pump", icStack("pump", 1), new String[] {"FFF", "CMC", "ADA"}, 'M', i("ic:advanced_machine_block"), 'C', i("ic:advanced_circuit"), 'D', i("minecraft:bucket"), 'F', i("ic:fluid_cell"), 'A', i("ic:advanced_alloy"));
		shaped("quad_uranium_fuel_rod", icStack("quad_uranium_fuel_rod", 1), new String[] {" R ", "MMM", " R "}, 'R', i("ic:dual_uranium_fuel_rod"), 'M', i("ic:dense_copper_plate"));
		shaped("quantum_boots", icStack("quantum_boots", 1), new String[] {"IAI", "SLS"}, 'A', i("ic:carbon_boots"), 'I', i("ic:iridium_alloy"), 'S', i("ic:reinforced_stone"), 'L', i("minecraft:leather_boots"));
		shaped("quantum_chestplate", icStack("quantum_chestplate", 1), new String[] {"MBM", "NAN", "IEI"}, 'A', i("ic:carbon_chestplate"), 'I', i("ic:iridium_alloy"), 'B', i("ic:ev_battery"), 'M', i("ic:advanced_machine_block"), 'E', i("ic:mechanical_elytra"), 'N', i("ic:antimatter"));
		shaped("quantum_helmet", icStack("quantum_helmet", 1), new String[] {" A ", "INI", "CGC"}, 'A', i("ic:carbon_helmet"), 'I', i("ic:iridium_alloy"), 'N', commonOrTag("c:dusts/glowstone"), 'C', i("ic:iridium_circuit"), 'G', i("ic:reinforced_glass"));
		shaped("quantum_leggings", icStack("quantum_leggings", 1), new String[] {"III", "MAM", "G G"}, 'A', i("ic:carbon_leggings"), 'I', i("ic:iridium_alloy"), 'M', i("ic:advanced_machine_block"), 'G', commonOrTag("c:dusts/glowstone"));
		shaped("quarry", icStack("quarry", 1), new String[] {"CMC", "ADA"}, 'M', i("ic:advanced_machine_block"), 'C', i("ic:advanced_circuit"), 'D', i("minecraft:diamond_pickaxe"), 'A', i("ic:advanced_alloy"));
		shaped("reactor_heat_exchanger", icStack("reactor_heat_exchanger", 1), new String[] {"P", "V", "P"}, 'P', commonOrTag("c:plates/lead"), 'V', i("ic:heat_exchanger"));
		shaped("reactor_heat_vent", icStack("reactor_heat_vent", 1), new String[] {"P", "V", "P"}, 'P', commonOrTag("c:plates/lead"), 'V', i("ic:heat_vent"));
		shaped("recycler", icStack("reprocessor", 1), new String[] {"MDM", "MCM"}, 'D', i("minecraft:composter"), 'C', i("ic:compressor"), 'M', i("ic:industrial_grade_metal"));
		shaped("reinforced_glass", icStack("reinforced_glass", 4), new String[] {"RGR", "G G", "RGR"}, 'R', i("ic:reinforced_stone"), 'G', commonOrTag("c:glass_blocks/colorless"));
		shaped("reactor_simulator", icStack("reactor_simulator", 1), new String[] {"HPH", "PBP", "HPH"}, 'H', i("ic:reactor_plating"), 'P', i("ic:dense_copper_plate"), 'B', i("minecraft:writable_book"));
		shaped("reinforced_stone", icStack("reinforced_stone", 4), new String[] {"SSS", "SAS", "SSS"}, 'S', i("minecraft:smooth_stone"), 'A', i("ic:advanced_alloy"));
		shaped("roller", icStack("roller", 1), new String[] {"PCP", "PMP"}, 'P', i("minecraft:piston"), 'M', i("ic:machine_block"), 'C', i("ic:electronic_circuit"));
		shaped("rubber_sheet", icStack("rubber_sheet", 1), new String[] {"III"}, 'I', i("ic:rubber"));
		shaped("scrap_box", icStack("scrap_box", 1), new String[] {"SSS", "SSS", "SSS"}, 'S', i("ic:scrap"));
		shaped("single_use_battery", icStack("single_use_battery", 1), new String[] {"C", "R", "O"}, 'C', i("ic:lv_cable"), 'O', commonOrTag("minecraft:coals"), 'R', commonOrTag("c:dusts/redstone"));
		shaped("teleporter", icStack("teleporter", 1), new String[] {"EIE", "CMC", "EPE"}, 'E', i("minecraft:ender_pearl"), 'I', i("ic:iridium_alloy"), 'C', i("ic:advanced_circuit"), 'M', i("ic:advanced_machine_block"), 'P', i("ic:energy_crystal"));
		shaped("thick_neutron_reflector", icStack("thick_neutron_reflector", 1), new String[] {"PNP", "NPN", "PNP"}, 'N', i("ic:neutron_reflector"), 'P', commonOrTag("c:plates/copper"));
		shaped("transformer_upgrade", icStack("transformer_upgrade", 1), new String[] {"GGG", "WTW", "GCG"}, 'G', commonOrTag("c:glass_blocks/colorless"), 'W', i("ic:mv_cable"), 'T', i("ic:mv_transformer"), 'C', i("ic:electronic_circuit"));
		shaped("wind_mill", icStack("wind_mill", 1), new String[] {" P ", "CGC", " P "}, 'P', i("ic:carbon_plate"), 'G', i("ic:basic_generator"), 'C', i("ic:electronic_circuit"));
	}

	private void shapelessBatch() {
		shapeless("advanced_circuit", icStack("advanced_circuit", 1), commonOrTag("c:dusts/redstone"), commonOrTag("c:dusts/glowstone"), commonOrTag("c:dusts/redstone"), commonOrTag("c:silicon"), i("ic:electronic_circuit"), commonOrTag("c:silicon"), commonOrTag("c:dusts/redstone"), commonOrTag("c:dusts/glowstone"), commonOrTag("c:dusts/redstone"));
		shapeless("advanced_machine_block", icStack("advanced_machine_block", 1), i("ic:copper_coil"), i("ic:carbon_plate"), i("ic:copper_coil"), i("ic:advanced_alloy"), i("ic:machine_block"), i("ic:advanced_alloy"), i("ic:copper_coil"), i("ic:carbon_plate"), i("ic:copper_coil"));
		shapeless("carbon_fiber_mesh", icStack("carbon_fiber_mesh", 1), i("ic:carbon_fibers"), i("ic:carbon_fibers"), i("ic:carbon_fibers"), i("ic:carbon_fibers"));
		shapeless("containment_reactor_plating", icStack("containment_reactor_plating", 1), i("ic:reactor_plating"), i("ic:advanced_alloy"), i("ic:advanced_alloy"));
		shapeless("electronic_circuit", icStack("electronic_circuit", 1), i("ic:lv_cable"), i("ic:lv_cable"), i("ic:lv_cable"), commonOrTag("c:dusts/redstone"), i("ic:industrial_grade_metal"), commonOrTag("c:dusts/redstone"), i("ic:lv_cable"), i("ic:lv_cable"), i("ic:lv_cable"));
		shapeless("electrum_dust", icStack("electrum_dust", 2), commonOrTag("c:dusts/gold"), commonOrTag("c:dusts/silver"));
		shapeless("enderium_block_to_enderium_ingot", icStack("enderium_ingot", 9), commonOrTag("c:storage_blocks/enderium"));
		shapeless("enderium_ingot_to_enderium_nugget", icStack("enderium_nugget", 9), commonOrTag("c:ingots/enderium"));
		shapeless("energy_crystal", icStack("energy_crystal", 1), commonOrTag("c:dusts/redstone"), commonOrTag("c:dusts/glowstone"), commonOrTag("c:dusts/redstone"), commonOrTag("c:silicon"), commonOrTag("c:gems/diamond"), commonOrTag("c:silicon"), commonOrTag("c:dusts/redstone"), commonOrTag("c:dusts/glowstone"), commonOrTag("c:dusts/redstone"));
		shapeless("ev_battery", icStack("ev_battery", 1), i("ic:hv_battery"), i("ic:iridium_alloy"));
		shapeless("ev_cable", icStack("ev_cable", 1), commonOrTag("c:wires/enderium"), i("ic:rubber"));
		shapeless("ev_reinforced_cable", icStack("ev_reinforced_cable", 1), i("ic:reinforced_stone"), i("ic:ev_cable"));
		shapeless("heat_capacity_reactor_plating", icStack("heat_capacity_reactor_plating", 1), i("ic:reactor_plating"), i("ic:dense_copper_plate"), i("ic:dense_copper_plate"));
		shapeless("hv_battery", icStack("hv_battery", 1), i("ic:mv_battery"), i("ic:graphene"));
		shapeless("hv_cable", icStack("hv_cable", 1), commonOrTag("c:wires/gold"), i("ic:rubber"));
		shapeless("hv_reinforced_cable", icStack("hv_reinforced_cable", 1), i("ic:reinforced_stone"), i("ic:hv_cable"));
		shapeless("iv_reinforced_cable", icStack("iv_reinforced_cable", 1), i("ic:reinforced_stone"), i("ic:iv_cable"));
		shapeless("iridium_circuit", icStack("iridium_circuit", 2), i("ic:advanced_alloy"), i("ic:graphene"), i("ic:advanced_alloy"), i("ic:advanced_circuit"), i("ic:iridium_alloy"), i("ic:advanced_circuit"), i("ic:advanced_alloy"), i("ic:graphene"), i("ic:advanced_alloy"));
		shapeless("landmark", icStack("landmark", 1), i("minecraft:redstone_torch"), commonOrTag("c:gems/lapis"));
		shapeless("lv_cable", icStack("lv_cable", 1), commonOrTag("c:wires/copper"), i("ic:rubber"));
		shapeless("lv_reinforced_cable", icStack("lv_reinforced_cable", 1), i("ic:reinforced_stone"), i("ic:lv_cable"));
		shapeless("mv_battery", icStack("mv_battery", 1), i("ic:lv_battery"), i("ic:energy_crystal"));
		shapeless("mv_cable", icStack("mv_cable", 1), commonOrTag("c:wires/aluminum"), i("ic:rubber"));
		shapeless("mv_reinforced_cable", icStack("mv_reinforced_cable", 1), i("ic:reinforced_stone"), i("ic:mv_cable"));
		shapeless("nuke_arrow", icStack("nuke_arrow", 1), i("ic:nuke"), i("minecraft:arrow"));
		shapeless("reactor_plating", icStack("reactor_plating", 1), i("ic:advanced_alloy"), commonOrTag("c:plates/lead"), commonOrTag("c:plates/lead"));
		shapeless("rubber", icStack("rubber", 3), i("ic:rubber_sheet"));
		shapeless("scrap", icStack("scrap", 9), i("ic:scrap_box"));
		shapeless("scrap_from_burnt_cable", icStack("scrap", 1), i("ic:burnt_cable"));
		shapeless("small_coolant_cell", icStack("small_coolant_cell", 1), i("ic:fluid_cell"), i("minecraft:water_bucket"), commonOrTag("c:ingots/tin"), commonOrTag("c:ingots/tin"), commonOrTag("c:ingots/tin"), commonOrTag("c:ingots/tin"));
	}


	public static final class Runner extends RecipeProvider.Runner {
		public Runner(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
			super(output, registries);
		}

		@Override
		protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
			return new ICRecipeProvider(registries, output);
		}

		@Override
		public String getName() {
			return "Industrial Contraptions Recipes";
		}
	}
}
