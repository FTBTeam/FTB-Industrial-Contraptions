package dev.ftb.mods.ftbic.integration.jade;

import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.block.CableBlock;
import dev.ftb.mods.ftbic.block.entity.ElectricBlockEntity;
import dev.ftb.mods.ftbic.block.entity.generator.GeothermalGeneratorBlockEntity;
import dev.ftb.mods.ftbic.block.entity.generator.NuclearReactorBlockEntity;
import dev.ftb.mods.ftbic.block.entity.machine.MachineBlockEntity;
import dev.ftb.mods.ftbic.block.entity.machine.PumpBlockEntity;
import dev.ftb.mods.ftbic.util.FTBICUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;
import snownee.jade.api.config.IPluginConfig;

/**
 * Jade "Waila" tooltip plugin for FTBIC. Shows energy / capacity / burnt state for any electric
 * block, lava + water tank level for Geothermal/Pump, current recipe progress for processing
 * machines, heat fraction + energy output for the Nuclear Reactor, and cable tier for cables.
 */
@WailaPlugin
public class FTBICJadePlugin implements IWailaPlugin {
	private static final Identifier ENERGY_UID = FTBIC.id("energy");
	private static final Identifier CABLE_TIER_UID = FTBIC.id("cable_tier");

	@Override
	public void register(IWailaCommonRegistration registration) {
		registration.registerBlockDataProvider(EnergyServerDataProvider.INSTANCE, ElectricBlockEntity.class);
	}

	@Override
	public void registerClient(IWailaClientRegistration registration) {
		registration.registerBlockComponent(EnergyClientProvider.INSTANCE, net.minecraft.world.level.block.Block.class);
		registration.registerBlockComponent(CableTierProvider.INSTANCE, CableBlock.class);
	}

	/** Cable tier line — pure client-side from the block class. */
	public static final class CableTierProvider implements IBlockComponentProvider {
		public static final CableTierProvider INSTANCE = new CableTierProvider();

		@Override
		public Identifier getUid() {
			return CABLE_TIER_UID;
		}

		@Override
		public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
			if (accessor.getBlock() instanceof CableBlock cable) {
				tooltip.add(Component.translatable("ftbic.jade.cable_tier", cable.tier.name.toUpperCase())
						.withStyle(ChatFormatting.AQUA));
			}
		}
	}

	/** Server-data provider: writes the energy snapshot into the block accessor's server-data NBT. */
	public static final class EnergyServerDataProvider implements IServerDataProvider<BlockAccessor> {
		public static final EnergyServerDataProvider INSTANCE = new EnergyServerDataProvider();

		@Override
		public Identifier getUid() {
			return ENERGY_UID;
		}

		@Override
		public void appendServerData(net.minecraft.nbt.CompoundTag data, BlockAccessor accessor) {
			if (accessor.getBlockEntity() instanceof ElectricBlockEntity be) {
				data.putDouble("ftbic_energy", be.getEnergy());
				data.putDouble("ftbic_energy_capacity", be.getEnergyCapacity());
				if (be.isBurnt()) {
					data.putBoolean("ftbic_burnt", true);
				}
				if (be instanceof GeothermalGeneratorBlockEntity geo) {
					data.putInt("ftbic_fluid", geo.fluidAmount);
					data.putInt("ftbic_fluid_capacity", geo.getTankCapacity());
					data.putString("ftbic_fluid_name", "lava");
				}
				if (be instanceof PumpBlockEntity pump) {
					data.putInt("ftbic_fluid", pump.fluidAmount);
					data.putInt("ftbic_fluid_capacity", pump.getTankCapacity());
					Identifier fluidId = pump.storedFluid == Fluids.EMPTY
							? null : BuiltInRegistries.FLUID.getKey(pump.storedFluid);
					data.putString("ftbic_fluid_name", fluidId == null ? "empty" : fluidId.getPath());
				}
				if (be instanceof MachineBlockEntity m && m.maxProgress > 0) {
					data.putInt("ftbic_progress", m.progress);
					data.putInt("ftbic_max_progress", m.maxProgress);
				}
				if (be instanceof NuclearReactorBlockEntity reactor) {
					data.putInt("ftbic_reactor_heat", reactor.reactor.heat);
					data.putInt("ftbic_reactor_max_heat", Math.max(1, reactor.reactor.maxHeat));
					data.putDouble("ftbic_reactor_output", reactor.reactor.energyOutput);
					data.putBoolean("ftbic_reactor_paused", reactor.reactor.paused);
				}
			}
		}

	}

	/** Client-side tooltip provider: reads the energy snapshot and appends tooltip lines. */
	public static final class EnergyClientProvider implements IBlockComponentProvider {
		public static final EnergyClientProvider INSTANCE = new EnergyClientProvider();

		@Override
		public Identifier getUid() {
			return ENERGY_UID;
		}

		@Override
		public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
			net.minecraft.nbt.CompoundTag data = accessor.getServerData();
			if (data.contains("ftbic_burnt")) {
				tooltip.add(Component.translatable("ftbic.jade.burnt").withStyle(ChatFormatting.RED));
				return;
			}
			if (data.contains("ftbic_energy_capacity")) {
				double energy = data.getDoubleOr("ftbic_energy", 0D);
				double capacity = data.getDoubleOr("ftbic_energy_capacity", 0D);
				if (capacity > 0D) {
					tooltip.add(Component.literal("")
							.append(FTBICUtils.formatEnergy(energy).withStyle(ChatFormatting.GRAY))
							.append(" / ")
							.append(FTBICUtils.formatEnergy(capacity).withStyle(ChatFormatting.GRAY))
							.withStyle(ChatFormatting.DARK_GRAY));
				}
			}
			if (data.contains("ftbic_progress") && data.contains("ftbic_max_progress")) {
				int prog = data.getIntOr("ftbic_progress", 0);
				int max = data.getIntOr("ftbic_max_progress", 0);
				if (max > 0 && prog > 0) {
					int pct = Math.min(100, Math.round(100F * prog / (float) max));
					tooltip.add(Component.translatable("ftbic.jade.progress", pct)
							.withStyle(ChatFormatting.GREEN));
				}
			}
			if (data.contains("ftbic_reactor_max_heat")) {
				int heat = data.getIntOr("ftbic_reactor_heat", 0);
				int maxHeat = data.getIntOr("ftbic_reactor_max_heat", 1);
				double out = data.getDoubleOr("ftbic_reactor_output", 0D);
				boolean paused = data.getBooleanOr("ftbic_reactor_paused", false);
				int pct = Math.min(100, Math.round(100F * heat / (float) maxHeat));
				ChatFormatting heatColor = pct >= 75 ? ChatFormatting.RED
						: pct >= 50 ? ChatFormatting.GOLD
						: pct >= 25 ? ChatFormatting.YELLOW
						: ChatFormatting.GREEN;
				tooltip.add(Component.translatable("ftbic.jade.reactor_heat", pct).withStyle(heatColor));
				tooltip.add(Component.translatable(paused ? "ftbic.jade.reactor_paused"
								: "ftbic.jade.reactor_output", FTBICUtils.formatEnergy(out))
						.withStyle(paused ? ChatFormatting.GRAY : ChatFormatting.AQUA));
			}
			if (data.contains("ftbic_fluid_capacity")) {
				int fluid = data.getIntOr("ftbic_fluid", 0);
				int fluidCap = data.getIntOr("ftbic_fluid_capacity", 0);
				String fluidName = data.getStringOr("ftbic_fluid_name", "");
				if (fluidCap > 0) {
					ChatFormatting color = switch (fluidName) {
						case "water" -> ChatFormatting.BLUE;
						case "lava" -> ChatFormatting.GOLD;
						default -> ChatFormatting.GRAY;
					};
					String key = switch (fluidName) {
						case "lava" -> "ftbic.jade.lava";
						case "water" -> "ftbic.jade.water";
						case "empty" -> "ftbic.jade.fluid_empty";
						default -> "ftbic.jade.fluid";
					};
					if (fluidName.equals("empty")) {
						tooltip.add(Component.translatable(key).withStyle(color));
					} else {
						tooltip.add(Component.translatable(key, fluid, fluidCap).withStyle(color));
					}
				}
			}
		}
	}
}
