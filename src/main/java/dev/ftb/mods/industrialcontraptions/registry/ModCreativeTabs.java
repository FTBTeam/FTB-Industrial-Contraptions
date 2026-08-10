package dev.ftb.mods.industrialcontraptions.registry;

import dev.ftb.mods.industrialcontraptions.IC;
import dev.ftb.mods.industrialcontraptions.block.ICElectricBlocks;
import dev.ftb.mods.industrialcontraptions.item.ICItems;
import dev.ftb.mods.industrialcontraptions.item.MaterialItem;
import dev.ftb.mods.industrialcontraptions.material.MaterialEntries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModCreativeTabs {
	public static final DeferredRegister<CreativeModeTab> TABS =
			DeferredRegister.create(Registries.CREATIVE_MODE_TAB, IC.MOD_ID);

	public static final DeferredHolder<CreativeModeTab, CreativeModeTab> IC_TAB =
			TABS.register("ic", () -> CreativeModeTab.builder()
					.title(Component.translatable("itemGroup." + IC.MOD_ID))
					.icon(() -> new ItemStack(ICElectricBlocks.POWERED_FURNACE.item.get()))
					.displayItems((params, out) -> {
						if (ModList.get().isLoaded("guideme")) {
							out.accept(ICItems.GUIDE.get());
						}

						ICElectricBlocks.ALL.forEach(inst -> out.accept(inst.item.get()));

						out.accept(ICItems.RUBBER_SHEET.get());
						out.accept(ICItems.REINFORCED_STONE.get());
						out.accept(ICItems.REINFORCED_GLASS.get());
						out.accept(ICItems.MACHINE_BLOCK.get());
						out.accept(ICItems.ADVANCED_MACHINE_BLOCK.get());
						out.accept(ICItems.IRON_FURNACE.get());
						out.accept(ICItems.LV_CABLE.get());
						out.accept(ICItems.MV_CABLE.get());
						out.accept(ICItems.HV_CABLE.get());
						out.accept(ICItems.EV_CABLE.get());
						out.accept(ICItems.IV_CABLE.get());
						out.accept(ICItems.BURNT_CABLE.get());
						out.accept(ICItems.LV_REINFORCED_CABLE.get());
						out.accept(ICItems.MV_REINFORCED_CABLE.get());
						out.accept(ICItems.HV_REINFORCED_CABLE.get());
						out.accept(ICItems.EV_REINFORCED_CABLE.get());
						out.accept(ICItems.IV_REINFORCED_CABLE.get());
						out.accept(ICItems.BURNT_REINFORCED_CABLE.get());
						out.accept(ICItems.LANDMARK.get());
						out.accept(ICItems.EXFLUID.get());
						out.accept(ICItems.NUCLEAR_REACTOR_CHAMBER.get());
						out.accept(ICItems.NUKE.get());
						out.accept(ICItems.ENDERIUM_BLOCK.get());

						for (MaterialItem mat : ICItems.MATERIALS) {
							if (mat.item != null) {
								out.accept(mat.item.get());
							}
						}

						MaterialEntries.all().forEach(entry -> out.accept(entry.item().get()));

						out.accept(ICItems.SINGLE_USE_BATTERY.get());
						out.accept(ICItems.LV_BATTERY.get());
						out.accept(ICItems.MV_BATTERY.get());
						out.accept(ICItems.HV_BATTERY.get());
						out.accept(ICItems.EV_BATTERY.get());
						out.accept(ICItems.CREATIVE_BATTERY.get());
						out.accept(ICItems.FLUID_CELL.get());
						out.accept(ICItems.LOCATION_CARD.get());

						out.accept(ICItems.SMALL_COOLANT_CELL.get());
						out.accept(ICItems.MEDIUM_COOLANT_CELL.get());
						out.accept(ICItems.LARGE_COOLANT_CELL.get());
						out.accept(ICItems.URANIUM_FUEL_ROD.get());
						out.accept(ICItems.DUAL_URANIUM_FUEL_ROD.get());
						out.accept(ICItems.QUAD_URANIUM_FUEL_ROD.get());
						out.accept(ICItems.HEAT_VENT.get());
						out.accept(ICItems.ADVANCED_HEAT_VENT.get());
						out.accept(ICItems.REACTOR_HEAT_VENT.get());
						out.accept(ICItems.COMPONENT_HEAT_VENT.get());
						out.accept(ICItems.OVERCLOCKED_HEAT_VENT.get());
						out.accept(ICItems.HEAT_EXCHANGER.get());
						out.accept(ICItems.ADVANCED_HEAT_EXCHANGER.get());
						out.accept(ICItems.REACTOR_HEAT_EXCHANGER.get());
						out.accept(ICItems.COMPONENT_HEAT_EXCHANGER.get());
						out.accept(ICItems.REACTOR_PLATING.get());
						out.accept(ICItems.CONTAINMENT_REACTOR_PLATING.get());
						out.accept(ICItems.HEAT_CAPACITY_REACTOR_PLATING.get());
						out.accept(ICItems.NEUTRON_REFLECTOR.get());
						out.accept(ICItems.THICK_NEUTRON_REFLECTOR.get());
						out.accept(ICItems.IRIDIUM_NEUTRON_REFLECTOR.get());

						out.accept(ICItems.CANNED_FOOD.get());
						out.accept(ICItems.PROTEIN_BAR.get());

						out.accept(ICItems.DARK_SPRAY_PAINT_CAN.get());
						out.accept(ICItems.LIGHT_SPRAY_PAINT_CAN.get());
						out.accept(ICItems.OVERCLOCKER_UPGRADE.get());
						out.accept(ICItems.ENERGY_STORAGE_UPGRADE.get());
						out.accept(ICItems.TRANSFORMER_UPGRADE.get());
						out.accept(ICItems.EJECTOR_UPGRADE.get());

						out.accept(ICItems.MECHANICAL_ELYTRA.get());
						out.accept(ICItems.CARBON_HELMET.get());
						out.accept(ICItems.CARBON_CHESTPLATE.get());
						out.accept(ICItems.CARBON_LEGGINGS.get());
						out.accept(ICItems.CARBON_BOOTS.get());
						out.accept(ICItems.QUANTUM_HELMET.get());
						out.accept(ICItems.QUANTUM_CHESTPLATE.get());
						out.accept(ICItems.QUANTUM_LEGGINGS.get());
						out.accept(ICItems.QUANTUM_BOOTS.get());
						out.accept(ICItems.NUKE_ARROW.get());
					})
					.build());

	private ModCreativeTabs() {}
}
