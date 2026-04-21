package dev.ftb.mods.ftbic.util;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.ftb.mods.ftbic.item.FTBICItems;
import dev.ftb.mods.ftbic.registry.ModDataComponents;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import dev.ftb.mods.ftbic.item.FluidCellItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.neoforged.neoforge.fluids.FluidStack;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;
import net.neoforged.neoforge.fluids.SimpleFluidContent;

import java.util.stream.Stream;

public record FluidCellIngredient(Fluid fluid) implements ICustomIngredient {
	public static final MapCodec<FluidCellIngredient> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
			BuiltInRegistries.FLUID.byNameCodec().fieldOf("fluid").forGetter(FluidCellIngredient::fluid)
	).apply(i, FluidCellIngredient::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, FluidCellIngredient> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.registry(Registries.FLUID), FluidCellIngredient::fluid,
			FluidCellIngredient::new);

	@Override
	public boolean test(ItemStack stack) {
		if (!stack.is(FTBICItems.FLUID_CELL.get())) return false;
		SimpleFluidContent stored = stack.get(ModDataComponents.FLUID_CELL_CONTENT.get());
		return stored != null && stored.getFluid() == fluid && stored.getAmount() >= 1000;
	}

	@Override
	public Stream<Holder<Item>> items() {
		return Stream.of(FTBICItems.FLUID_CELL.get().builtInRegistryHolder());
	}

	@Override
	public SlotDisplay display() {
		ItemStack filled = new ItemStack(FTBICItems.FLUID_CELL.get());
		FluidCellItem.setStored(filled, new FluidStack(fluid, FluidCellItem.capacity()));
		return new SlotDisplay.ItemStackSlotDisplay(ItemStackTemplate.fromNonEmptyStack(filled));
	}

	@Override
	public boolean isSimple() {
		return false;
	}

	@Override
	public IngredientType<?> getType() {
		return FTBICIngredientTypes.FLUID_CELL.get();
	}
}
