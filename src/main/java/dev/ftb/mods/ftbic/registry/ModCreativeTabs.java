package dev.ftb.mods.ftbic.registry;

import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.item.FTBICItems;
import dev.ftb.mods.ftbic.item.MaterialItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModCreativeTabs {
	public static final DeferredRegister<CreativeModeTab> TABS =
			DeferredRegister.create(Registries.CREATIVE_MODE_TAB, FTBIC.MOD_ID);

	public static final DeferredHolder<CreativeModeTab, CreativeModeTab> FTBIC_TAB =
			TABS.register("ftbic", () -> CreativeModeTab.builder()
					.title(Component.translatable("itemGroup." + FTBIC.MOD_ID))
					.icon(() -> new ItemStack(FTBICElectricBlocks.POWERED_FURNACE.item.get()))
					.displayItems((params, out) -> {
						if (ModList.get().isLoaded("guideme")) {
							out.accept(FTBICItems.GUIDE.get());
						}

						FTBICElectricBlocks.ALL.forEach(inst -> out.accept(inst.item.get()));

						out.accept(FTBICItems.RUBBER_SHEET.get());
						out.accept(FTBICItems.REINFORCED_STONE.get());
						out.accept(FTBICItems.REINFORCED_GLASS.get());
						out.accept(FTBICItems.MACHINE_BLOCK.get());
						out.accept(FTBICItems.ADVANCED_MACHINE_BLOCK.get());
						out.accept(FTBICItems.IRON_FURNACE.get());
						out.accept(FTBICItems.LV_CABLE.get());
						out.accept(FTBICItems.MV_CABLE.get());
						out.accept(FTBICItems.HV_CABLE.get());
						out.accept(FTBICItems.EV_CABLE.get());
						out.accept(FTBICItems.IV_CABLE.get());
						out.accept(FTBICItems.BURNT_CABLE.get());
						out.accept(FTBICItems.LV_REINFORCED_CABLE.get());
						out.accept(FTBICItems.MV_REINFORCED_CABLE.get());
						out.accept(FTBICItems.HV_REINFORCED_CABLE.get());
						out.accept(FTBICItems.EV_REINFORCED_CABLE.get());
						out.accept(FTBICItems.IV_REINFORCED_CABLE.get());
						out.accept(FTBICItems.BURNT_REINFORCED_CABLE.get());
						out.accept(FTBICItems.LANDMARK.get());
						out.accept(FTBICItems.EXFLUID.get());
						out.accept(FTBICItems.NUCLEAR_REACTOR_CHAMBER.get());
						out.accept(FTBICItems.NUKE.get());
						out.accept(FTBICItems.ENDERIUM_BLOCK.get());

						for (MaterialItem mat : FTBICItems.MATERIALS) {
							if (mat.item != null) {
								out.accept(mat.item.get());
							}
						}

						out.accept(FTBICItems.SINGLE_USE_BATTERY.get());
						out.accept(FTBICItems.LV_BATTERY.get());
						out.accept(FTBICItems.MV_BATTERY.get());
						out.accept(FTBICItems.HV_BATTERY.get());
						out.accept(FTBICItems.EV_BATTERY.get());
						out.accept(FTBICItems.CREATIVE_BATTERY.get());
						out.accept(FTBICItems.FLUID_CELL.get());
						out.accept(FTBICItems.LOCATION_CARD.get());

						out.accept(FTBICItems.SMALL_COOLANT_CELL.get());
						out.accept(FTBICItems.MEDIUM_COOLANT_CELL.get());
						out.accept(FTBICItems.LARGE_COOLANT_CELL.get());
						out.accept(FTBICItems.URANIUM_FUEL_ROD.get());
						out.accept(FTBICItems.DUAL_URANIUM_FUEL_ROD.get());
						out.accept(FTBICItems.QUAD_URANIUM_FUEL_ROD.get());
						out.accept(FTBICItems.HEAT_VENT.get());
						out.accept(FTBICItems.ADVANCED_HEAT_VENT.get());
						out.accept(FTBICItems.REACTOR_HEAT_VENT.get());
						out.accept(FTBICItems.COMPONENT_HEAT_VENT.get());
						out.accept(FTBICItems.OVERCLOCKED_HEAT_VENT.get());
						out.accept(FTBICItems.HEAT_EXCHANGER.get());
						out.accept(FTBICItems.ADVANCED_HEAT_EXCHANGER.get());
						out.accept(FTBICItems.REACTOR_HEAT_EXCHANGER.get());
						out.accept(FTBICItems.COMPONENT_HEAT_EXCHANGER.get());
						out.accept(FTBICItems.REACTOR_PLATING.get());
						out.accept(FTBICItems.CONTAINMENT_REACTOR_PLATING.get());
						out.accept(FTBICItems.HEAT_CAPACITY_REACTOR_PLATING.get());
						out.accept(FTBICItems.NEUTRON_REFLECTOR.get());
						out.accept(FTBICItems.THICK_NEUTRON_REFLECTOR.get());
						out.accept(FTBICItems.IRIDIUM_NEUTRON_REFLECTOR.get());

						out.accept(FTBICItems.CANNED_FOOD.get());
						out.accept(FTBICItems.PROTEIN_BAR.get());

						out.accept(FTBICItems.DARK_SPRAY_PAINT_CAN.get());
						out.accept(FTBICItems.LIGHT_SPRAY_PAINT_CAN.get());
						out.accept(FTBICItems.OVERCLOCKER_UPGRADE.get());
						out.accept(FTBICItems.ENERGY_STORAGE_UPGRADE.get());
						out.accept(FTBICItems.TRANSFORMER_UPGRADE.get());
						out.accept(FTBICItems.EJECTOR_UPGRADE.get());

						out.accept(FTBICItems.MECHANICAL_ELYTRA.get());
						out.accept(FTBICItems.CARBON_HELMET.get());
						out.accept(FTBICItems.CARBON_CHESTPLATE.get());
						out.accept(FTBICItems.CARBON_LEGGINGS.get());
						out.accept(FTBICItems.CARBON_BOOTS.get());
						out.accept(FTBICItems.QUANTUM_HELMET.get());
						out.accept(FTBICItems.QUANTUM_CHESTPLATE.get());
						out.accept(FTBICItems.QUANTUM_LEGGINGS.get());
						out.accept(FTBICItems.QUANTUM_BOOTS.get());
						out.accept(FTBICItems.NUKE_ARROW.get());
					})
					.build());

	private ModCreativeTabs() {}
}
