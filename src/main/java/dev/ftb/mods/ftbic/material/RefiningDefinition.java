package dev.ftb.mods.ftbic.material;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.ftb.mods.ftbic.util.StackWithChance;
import java.util.List;
import net.minecraft.resources.Identifier;

/** A datapack material profile. Empty selectors use the corresponding common material tag. */
public record RefiningDefinition(
        Identifier material,
        boolean enabled,
        String name,
        int color,
        String oreInput,
        String rawInput,
        String rawBlockInput,
        String rawOutput,
        String ingot,
        Yields yields,
        Costs costs,
        List<StackWithChance> byproducts) {
    public static final Codec<RefiningDefinition> CODEC = RecordCodecBuilder.create(i -> i.group(
                    Identifier.CODEC.fieldOf("material").forGetter(RefiningDefinition::material),
                    Codec.BOOL.optionalFieldOf("enabled", true).forGetter(RefiningDefinition::enabled),
                    Codec.STRING.optionalFieldOf("name", "").forGetter(RefiningDefinition::name),
                    Codec.intRange(-1, 0xFFFFFF).optionalFieldOf("color", -1).forGetter(RefiningDefinition::color),
                    Codec.STRING.optionalFieldOf("ore_input", "").forGetter(RefiningDefinition::oreInput),
                    Codec.STRING.optionalFieldOf("raw_input", "").forGetter(RefiningDefinition::rawInput),
                    Codec.STRING.optionalFieldOf("raw_block_input", "").forGetter(RefiningDefinition::rawBlockInput),
                    Codec.STRING.optionalFieldOf("raw_output", "").forGetter(RefiningDefinition::rawOutput),
                    Codec.STRING.optionalFieldOf("ingot", "").forGetter(RefiningDefinition::ingot),
                    Yields.CODEC.optionalFieldOf("yields", Yields.DEFAULT).forGetter(RefiningDefinition::yields),
                    Costs.CODEC.optionalFieldOf("costs", Costs.DEFAULT).forGetter(RefiningDefinition::costs),
                    StackWithChance.CODEC
                            .listOf()
                            .optionalFieldOf("byproducts", List.of())
                            .forGetter(RefiningDefinition::byproducts))
            .apply(i, RefiningDefinition::new));

    public static RefiningDefinition automatic(Identifier id) {
        return new RefiningDefinition(id, true, "", -1, "", "", "", "", "", Yields.DEFAULT, Costs.DEFAULT, List.of());
    }

    public RefiningDefinition resolved(String raw, String output) {
        return new RefiningDefinition(
                material,
                enabled,
                name,
                color,
                oreInput,
                rawInput,
                rawBlockInput,
                raw,
                output,
                yields,
                costs,
                byproducts);
    }

    public String selector(String prefix, String override) {
        return override.isEmpty() ? "#c:" + prefix + material.getPath() : override;
    }

    public record Yields(
            int oreToRaw, int rawToCrushed, int washInput, int washOutput, int refineInput, int refineOutput) {
        public static final Yields DEFAULT = new Yields(3, 2, 2, 3, 3, 5);
        private static final Codec<Integer> COUNT = Codec.intRange(1, 64);
        public static final Codec<Yields> CODEC = RecordCodecBuilder.create(i -> i.group(
                        COUNT.optionalFieldOf("ore_to_raw", 3).forGetter(Yields::oreToRaw),
                        COUNT.optionalFieldOf("raw_to_crushed", 2).forGetter(Yields::rawToCrushed),
                        COUNT.optionalFieldOf("wash_input", 2).forGetter(Yields::washInput),
                        COUNT.optionalFieldOf("wash_output", 3).forGetter(Yields::washOutput),
                        COUNT.optionalFieldOf("refine_input", 3).forGetter(Yields::refineInput),
                        COUNT.optionalFieldOf("refine_output", 5).forGetter(Yields::refineOutput))
                .apply(i, Yields::new));
    }

    public record Costs(double crushTime, double washTime, double refineTime, String washFluid, int fluidAmount) {
        public static final Costs DEFAULT = new Costs(1, 2, 3, "minecraft:water", 1000);
        public static final Codec<Costs> CODEC = RecordCodecBuilder.create(i -> i.group(
                        Codec.doubleRange(0.01, 10000)
                                .optionalFieldOf("crush_time", 1D)
                                .forGetter(Costs::crushTime),
                        Codec.doubleRange(0.01, 10000)
                                .optionalFieldOf("wash_time", 2D)
                                .forGetter(Costs::washTime),
                        Codec.doubleRange(0.01, 10000)
                                .optionalFieldOf("refine_time", 3D)
                                .forGetter(Costs::refineTime),
                        Codec.STRING
                                .optionalFieldOf("wash_fluid", "minecraft:water")
                                .forGetter(Costs::washFluid),
                        Codec.intRange(1, 16000)
                                .optionalFieldOf("fluid_amount", 1000)
                                .forGetter(Costs::fluidAmount))
                .apply(i, Costs::new));
    }
}
