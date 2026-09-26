package dev.ftb.mods.ftbic.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.ftb.mods.ftbic.FTBICConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.conditions.ICondition;

public record RecipeToggleCondition(String option) implements ICondition {
    public static final MapCodec<RecipeToggleCondition> CODEC = RecordCodecBuilder.mapCodec(
            i -> i.group(Codec.STRING.fieldOf("option").forGetter(RecipeToggleCondition::option))
                    .apply(i, RecipeToggleCondition::new));

    @Override
    public boolean test(IContext context) {
        return !(FTBICConfig.COMMON_SPEC.getValues().get("recipes." + option)
                        instanceof ModConfigSpec.BooleanValue value)
                || value.get();
    }

    @Override
    public MapCodec<? extends ICondition> codec() {
        return CODEC;
    }

    @Override
    public String toString() {
        return "ftbic:recipe_toggle(\"" + option + "\")";
    }
}
