package dev.ftb.mods.ftbic.kubejs;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.ftb.mods.ftbic.util.FTBICUtils;
import dev.latvian.mods.kubejs.fluid.FluidStackJS;
import dev.latvian.mods.kubejs.item.ItemStackJS;
import dev.latvian.mods.kubejs.item.ingredient.IngredientJS;
import dev.latvian.mods.kubejs.item.ingredient.IngredientStackJS;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.util.ListJS;

import java.util.ArrayList;
import java.util.List;

public class MachineRecipeJS extends RecipeJS {
	public List<FluidStackJS> inputFluids = new ArrayList<>();
	public List<FluidStackJS> outputFluids = new ArrayList<>();

	@Override
	public void create(ListJS args) {
		for (Object o : ListJS.orSelf(args.get(0))) {
			if (o instanceof FluidStackJS) {
				outputFluids.add((FluidStackJS) o);
			} else {
				outputItems.add(parseResultItem(o));
			}
		}

		for (Object o : ListJS.orSelf(args.get(1))) {
			if (o instanceof FluidStackJS) {
				inputFluids.add((FluidStackJS) o);
			} else {
				inputItems.add(parseIngredientItem(o).asIngredientStack());
			}
		}
	}

	@Override
	public void deserialize() {
		inputItems.addAll(FTBICUtils.listFromJson(json, "inputItems", json -> parseIngredientItem(json).asIngredientStack()));
		inputFluids.addAll(FTBICUtils.listFromJson(json, "inputFluids", FluidStackJS::fromJson));
		outputItems.addAll(FTBICUtils.listFromJson(json, "outputItems", this::parseResultItem));
		outputFluids.addAll(FTBICUtils.listFromJson(json, "outputFluids", FluidStackJS::fromJson));
	}

	@Override
	public void serialize() {
		FTBICUtils.listToJson(inputItems, json, "inputItems", IngredientJS::toJson);
		FTBICUtils.listToJson(inputFluids, json, "inputFluids", FluidStackJS::toJson);
		FTBICUtils.listToJson(outputItems, json, "outputItems", ItemStackJS::toResultJson);
		FTBICUtils.listToJson(outputFluids, json, "outputFluids", FluidStackJS::toJson);
	}

	public MachineRecipeJS processingTime(double p) {
		json.addProperty("processingTime", p);
		save();
		return this;
	}

	@Override
	public JsonElement serializeIngredientStack(IngredientStackJS in) {
		JsonObject o = new JsonObject();
		o.add("ingredient", in.ingredient.toJson());
		o.addProperty("count", in.getCount());
		return o;
	}
}
