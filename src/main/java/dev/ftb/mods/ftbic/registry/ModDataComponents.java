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

import java.util.function.Supplier;

/**
 * Mod-wide data component registry. Replaces CompoundTag-based per-item state that was used in 1.18.2.
 */
public final class ModDataComponents {
	public static final DeferredRegister.DataComponents DATA_COMPONENTS =
			DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, FTBIC.MOD_ID);

	/** Stored energy on an ElectricItem/BatteryItem/EnergyArmorItem/MechanicalElytra. */
	public static final Supplier<DataComponentType<Double>> ENERGY =
			DATA_COMPONENTS.registerComponentType("energy", b -> b
					.persistent(Codec.DOUBLE)
					.networkSynchronized(ByteBufCodecs.DOUBLE));

	/** Bound dimension ID for a Location Card / Teleporter target. */
	public static final Supplier<DataComponentType<String>> BOUND_DIMENSION =
			DATA_COMPONENTS.registerComponentType("bound_dimension", b -> b
					.persistent(Codec.STRING)
					.networkSynchronized(ByteBufCodecs.STRING_UTF8));

	/** Bound block X coord. */
	public static final Supplier<DataComponentType<Integer>> BOUND_X =
			DATA_COMPONENTS.registerComponentType("bound_x", b -> b
					.persistent(Codec.INT)
					.networkSynchronized(ByteBufCodecs.VAR_INT));

	public static final Supplier<DataComponentType<Integer>> BOUND_Y =
			DATA_COMPONENTS.registerComponentType("bound_y", b -> b
					.persistent(Codec.INT)
					.networkSynchronized(ByteBufCodecs.VAR_INT));

	public static final Supplier<DataComponentType<Integer>> BOUND_Z =
			DATA_COMPONENTS.registerComponentType("bound_z", b -> b
					.persistent(Codec.INT)
					.networkSynchronized(ByteBufCodecs.VAR_INT));

	public static final Supplier<DataComponentType<String>> LOCATION_NAME =
			DATA_COMPONENTS.registerComponentType("location_name", b -> b
					.persistent(Codec.STRING)
					.networkSynchronized(ByteBufCodecs.STRING_UTF8));

	/** Spray-paint colour index (0-15 or -1 for reset). */
	public static final Supplier<DataComponentType<Integer>> PAINT_COLOR =
			DATA_COMPONENTS.registerComponentType("paint_color", b -> b
					.persistent(Codec.INT)
					.networkSynchronized(ByteBufCodecs.VAR_INT));

	/** FluidCell contents — fluid type + amount stored as a FluidStack. */
	public static final Supplier<DataComponentType<FluidStack>> FLUID_CELL_CONTENT =
			DATA_COMPONENTS.registerComponentType("fluid_cell_content", b -> b
					.persistent(FluidStack.CODEC)
					.networkSynchronized(FluidStack.STREAM_CODEC));

	/** Marker component on a {@link dev.ftb.mods.ftbic.item.BatteryItem} when the player has toggled
	 * its auto-charge mode on (right-click). Presence-only — no payload. */
	public static final Supplier<DataComponentType<Unit>> BATTERY_ACTIVE =
			DATA_COMPONENTS.registerComponentType("battery_active", b -> b
					.persistent(Unit.CODEC)
					.networkSynchronized(StreamCodec.unit(Unit.INSTANCE)));

	private ModDataComponents() {}
}
