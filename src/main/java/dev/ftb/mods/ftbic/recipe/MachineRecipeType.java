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
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;

public final class MachineRecipeType {
	public final String id;
	public final boolean twoInputs;
	public final boolean extraOutput;
	public final DeferredHolder<RecipeType<?>, RecipeType<MachineRecipe>> TYPE;
	public final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<MachineRecipe>> SERIALIZER;

	@SuppressWarnings({"unchecked", "rawtypes"})
	public MachineRecipeType(String id,
			boolean twoInputs,
			boolean extraOutput,
			DeferredRegister<RecipeType<?>> typeRegistry,
			DeferredRegister<RecipeSerializer<?>> serializerRegistry) {
		this.id = id;
		this.twoInputs = twoInputs;
		this.extraOutput = extraOutput;

		this.TYPE = (DeferredHolder) typeRegistry.register(id, () -> new RecipeType<MachineRecipe>() {
			@Override public String toString() { return "ftbic:" + id; }
		});

		this.SERIALIZER = (DeferredHolder) serializerRegistry.register(id,
				() -> new RecipeSerializer<>(buildMapCodec(), buildStreamCodec()));
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
