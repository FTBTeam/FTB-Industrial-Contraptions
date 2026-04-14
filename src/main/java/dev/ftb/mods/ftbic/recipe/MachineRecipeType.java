package dev.ftb.mods.ftbic.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.ftb.mods.ftbic.util.IngredientWithCount;
import dev.ftb.mods.ftbic.util.StackWithChance;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
import java.util.function.Supplier;

/**
 * Holder that owns the paired `RecipeType` + `RecipeSerializer` for a single FTBIC machine recipe
 * flavour (smelting, macerating, canning, …).
 *
 * In 26.1 `RecipeSerializer<T>` is a **record** of `(MapCodec, StreamCodec)` — we can't subclass it to
 * attach per-flavour metadata, so instead this class builds both codecs with captured references back
 * to itself, registers them, and MachineRecipe stores its owning type so `getSerializer()` +
 * `getType()` can hand back the correct registry entries.
 *
 * Flags:
 *  - twoInputs: canning-style recipes that accept two ingredient slots
 *  - extraOutput: macerating/separating which can have chance-based secondary outputs
 */
public final class MachineRecipeType {
	public final String id;
	public final boolean twoInputs;
	public final boolean extraOutput;
	public final Supplier<RecipeType<MachineRecipe>> TYPE;
	public final Supplier<RecipeSerializer<MachineRecipe>> SERIALIZER;

	public MachineRecipeType(String id,
			boolean twoInputs,
			boolean extraOutput,
			DeferredRegister<RecipeType<?>> typeRegistry,
			DeferredRegister<RecipeSerializer<?>> serializerRegistry) {
		this.id = id;
		this.twoInputs = twoInputs;
		this.extraOutput = extraOutput;

		@SuppressWarnings("unchecked")
		Supplier<RecipeType<MachineRecipe>> typeSup = (Supplier<RecipeType<MachineRecipe>>) (Supplier<?>)
				typeRegistry.register(id, () -> new RecipeType<MachineRecipe>() {
					@Override public String toString() { return "ftbic:" + id; }
				});
		this.TYPE = typeSup;

		@SuppressWarnings("unchecked")
		Supplier<RecipeSerializer<MachineRecipe>> serSup = (Supplier<RecipeSerializer<MachineRecipe>>) (Supplier<?>)
				serializerRegistry.register(id, () -> new RecipeSerializer<>(buildMapCodec(), buildStreamCodec()));
		this.SERIALIZER = serSup;
	}

	private MapCodec<MachineRecipe> buildMapCodec() {
		return RecordCodecBuilder.mapCodec(i -> i.group(
				IngredientWithCount.CODEC.listOf().optionalFieldOf("inputs", List.of()).forGetter(r -> r.inputs),
				SizedFluidIngredient.CODEC.listOf().optionalFieldOf("input_fluids", List.of()).forGetter(r -> r.inputFluids),
				StackWithChance.CODEC.listOf().optionalFieldOf("outputs", List.of()).forGetter(r -> r.outputs),
				FluidStack.CODEC.listOf().optionalFieldOf("output_fluids", List.of()).forGetter(r -> r.outputFluids),
				Codec.DOUBLE.optionalFieldOf("processing_time", 1D).forGetter(r -> r.processingTime),
				Codec.BOOL.optionalFieldOf("hide_from_jei", false).forGetter(r -> r.hideFromJEI)
		).apply(i, (ins, inF, outs, outF, time, hide) ->
				new MachineRecipe(this, ins, inF, outs, outF, time, hide)));
	}

	private StreamCodec<RegistryFriendlyByteBuf, MachineRecipe> buildStreamCodec() {
		return StreamCodec.composite(
				IngredientWithCount.STREAM_CODEC.apply(ByteBufCodecs.list()), r -> r.inputs,
				SizedFluidIngredient.STREAM_CODEC.apply(ByteBufCodecs.list()), r -> r.inputFluids,
				StackWithChance.STREAM_CODEC.apply(ByteBufCodecs.list()), r -> r.outputs,
				FluidStack.STREAM_CODEC.apply(ByteBufCodecs.list()), r -> r.outputFluids,
				ByteBufCodecs.DOUBLE, r -> r.processingTime,
				ByteBufCodecs.BOOL, r -> r.hideFromJEI,
				(ins, inF, outs, outF, time, hide) -> new MachineRecipe(this, ins, inF, outs, outF, time, hide));
	}
}
