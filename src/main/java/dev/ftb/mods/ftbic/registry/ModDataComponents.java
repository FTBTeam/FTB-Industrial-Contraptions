package dev.ftb.mods.ftbic.registry;

import com.mojang.serialization.Codec;
import dev.ftb.mods.ftbic.FTBIC;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Unit;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public final class ModDataComponents {
	public static final DeferredRegister.DataComponents DATA_COMPONENTS =
			DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, FTBIC.MOD_ID);

	public static final DeferredHolder<DataComponentType<?>, DataComponentType<Double>> ENERGY =
			DATA_COMPONENTS.registerComponentType("energy", b -> b
					.persistent(Codec.DOUBLE)
					.networkSynchronized(ByteBufCodecs.DOUBLE));

	public static final DeferredHolder<DataComponentType<?>, DataComponentType<String>> BOUND_DIMENSION =
			DATA_COMPONENTS.registerComponentType("bound_dimension", b -> b
					.persistent(Codec.STRING)
					.networkSynchronized(ByteBufCodecs.STRING_UTF8));

	public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> BOUND_X =
			DATA_COMPONENTS.registerComponentType("bound_x", b -> b
					.persistent(Codec.INT)
					.networkSynchronized(ByteBufCodecs.VAR_INT));

	public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> BOUND_Y =
			DATA_COMPONENTS.registerComponentType("bound_y", b -> b
					.persistent(Codec.INT)
					.networkSynchronized(ByteBufCodecs.VAR_INT));

	public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> BOUND_Z =
			DATA_COMPONENTS.registerComponentType("bound_z", b -> b
					.persistent(Codec.INT)
					.networkSynchronized(ByteBufCodecs.VAR_INT));

	public static final DeferredHolder<DataComponentType<?>, DataComponentType<String>> LOCATION_NAME =
			DATA_COMPONENTS.registerComponentType("location_name", b -> b
					.persistent(Codec.STRING)
					.networkSynchronized(ByteBufCodecs.STRING_UTF8));

	public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> PAINT_COLOR =
			DATA_COMPONENTS.registerComponentType("paint_color", b -> b
					.persistent(Codec.INT)
					.networkSynchronized(ByteBufCodecs.VAR_INT));

	public static final DeferredHolder<DataComponentType<?>, DataComponentType<FluidStack>> FLUID_CELL_CONTENT =
			DATA_COMPONENTS.registerComponentType("fluid_cell_content", b -> b
					.persistent(FluidStack.CODEC)
					.networkSynchronized(FluidStack.STREAM_CODEC));

	public static final DeferredHolder<DataComponentType<?>, DataComponentType<Unit>> BATTERY_ACTIVE =
			DATA_COMPONENTS.registerComponentType("battery_active", b -> b
					.persistent(Unit.CODEC)
					.networkSynchronized(StreamCodec.unit(Unit.INSTANCE)));

	private ModDataComponents() {}
}
