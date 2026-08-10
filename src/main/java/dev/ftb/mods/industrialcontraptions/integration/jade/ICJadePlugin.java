package dev.ftb.mods.industrialcontraptions.integration.jade;

import dev.ftb.mods.industrialcontraptions.IC;
import dev.ftb.mods.industrialcontraptions.block.CableBlock;
import dev.ftb.mods.industrialcontraptions.block.entity.ElectricBlockEntity;
import dev.ftb.mods.industrialcontraptions.block.entity.generator.GeothermalGeneratorBlockEntity;
import dev.ftb.mods.industrialcontraptions.block.entity.generator.NuclearReactorBlockEntity;
import dev.ftb.mods.industrialcontraptions.block.entity.machine.MachineBlockEntity;
import dev.ftb.mods.industrialcontraptions.block.entity.machine.PumpBlockEntity;
import dev.ftb.mods.industrialcontraptions.block.entity.machine.TeleporterBlockEntity;
import dev.ftb.mods.industrialcontraptions.util.ICUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluids;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;
import snownee.jade.api.config.IPluginConfig;

@WailaPlugin
public class ICJadePlugin implements IWailaPlugin {
	private static final Identifier ENERGY_UID = IC.id("energy");
	private static final Identifier CABLE_TIER_UID = IC.id("cable_tier");

	@Override
	public void register(IWailaCommonRegistration registration) {
		registration.registerBlockDataProvider(EnergyServerDataProvider.INSTANCE, ElectricBlockEntity.class);
	}

	@Override
	public void registerClient(IWailaClientRegistration registration) {
		registration.registerBlockComponent(EnergyClientProvider.INSTANCE, Block.class);
		registration.registerBlockComponent(CableTierProvider.INSTANCE, CableBlock.class);
	}

	public static final class CableTierProvider implements IBlockComponentProvider {
		public static final CableTierProvider INSTANCE = new CableTierProvider();

		@Override
		public Identifier getUid() {
			return CABLE_TIER_UID;
		}

		@Override
		public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
			if (accessor.getBlock() instanceof CableBlock cable) {
				tooltip.add(Component.translatable("ic.jade.cable_tier", cable.tier.name.toUpperCase())
						.withStyle(ChatFormatting.AQUA));
			}
		}
	}

	public static final class EnergyServerDataProvider implements IServerDataProvider<BlockAccessor> {
		public static final EnergyServerDataProvider INSTANCE = new EnergyServerDataProvider();

		@Override
		public Identifier getUid() {
			return ENERGY_UID;
		}

		@Override
		public void appendServerData(CompoundTag data, BlockAccessor accessor) {
			if (accessor.getBlockEntity() instanceof ElectricBlockEntity be) {
				data.putDouble("ic_energy", be.getEnergy());
				data.putDouble("ic_energy_capacity", be.getEnergyCapacity());
				if (be.isBurnt()) {
					data.putBoolean("ic_burnt", true);
				}
				if (be instanceof GeothermalGeneratorBlockEntity geo) {
					data.putInt("ic_fluid", geo.fluidAmount);
					data.putInt("ic_fluid_capacity", geo.getTankCapacity());
					data.putString("ic_fluid_name", "lava");
				}
				if (be instanceof PumpBlockEntity pump) {
					data.putInt("ic_fluid", pump.fluidAmount);
					data.putInt("ic_fluid_capacity", pump.getTankCapacity());
					Identifier fluidId = pump.storedFluid == Fluids.EMPTY
							? null : BuiltInRegistries.FLUID.getKey(pump.storedFluid);
					data.putString("ic_fluid_name", fluidId == null ? "empty" : fluidId.getPath());
				}
				if (be instanceof MachineBlockEntity m && m.maxProgress > 0) {
					data.putInt("ic_progress", m.progress);
					data.putInt("ic_max_progress", m.maxProgress);
				}
				if (be instanceof MachineBlockEntity m && m.starving) {
					data.putBoolean("ic_starving", true);
				}
				if (be instanceof NuclearReactorBlockEntity reactor) {
					data.putInt("ic_reactor_heat", reactor.reactor.heat);
					data.putInt("ic_reactor_max_heat", Math.max(1, reactor.reactor.maxHeat));
					data.putDouble("ic_reactor_output", reactor.reactor.energyOutput);
					data.putBoolean("ic_reactor_paused", reactor.reactor.paused);
				}
				if (be instanceof TeleporterBlockEntity tele) {
					int sendCount = 0;
					for (ItemStack s : tele.sendItems) if (!s.isEmpty()) sendCount += s.getCount();
					int receiveCount = 0;
					for (ItemStack s : tele.receiveItems) if (!s.isEmpty()) receiveCount += s.getCount();
					data.putInt("ic_tele_send_items", sendCount);
					data.putInt("ic_tele_receive_items", receiveCount);
					data.putInt("ic_tele_send_fluid_amount", tele.sendFluidAmount);
					data.putInt("ic_tele_receive_fluid_amount", tele.receiveFluidAmount);
					data.putInt("ic_tele_tank_capacity", TeleporterBlockEntity.TANK_CAPACITY);
					if (tele.sendFluid != Fluids.EMPTY) {
						Identifier fid = BuiltInRegistries.FLUID.getKey(tele.sendFluid);
						if (fid != null) data.putString("ic_tele_send_fluid", fid.toString());
					}
					if (tele.receiveFluid != Fluids.EMPTY) {
						Identifier fid = BuiltInRegistries.FLUID.getKey(tele.receiveFluid);
						if (fid != null) data.putString("ic_tele_receive_fluid", fid.toString());
					}
				}
			}
		}

	}

	private static String prettyFluidId(String raw) {
		if (raw.isEmpty()) return "Fluid";
		int colon = raw.indexOf(':');
		String path = colon >= 0 ? raw.substring(colon + 1) : raw;
		String[] parts = path.split("_");
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < parts.length; i++) {
			if (parts[i].isEmpty()) continue;
			if (sb.length() > 0) sb.append(' ');
			sb.append(Character.toUpperCase(parts[i].charAt(0)));
			if (parts[i].length() > 1) sb.append(parts[i].substring(1));
		}
		return sb.toString();
	}

	public static final class EnergyClientProvider implements IBlockComponentProvider {
		public static final EnergyClientProvider INSTANCE = new EnergyClientProvider();

		@Override
		public Identifier getUid() {
			return ENERGY_UID;
		}

		@Override
		public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
			CompoundTag data = accessor.getServerData();
			if (data.contains("ic_burnt")) {
				tooltip.add(Component.translatable("ic.jade.burnt").withStyle(ChatFormatting.RED));
				return;
			}
			if (data.contains("ic_energy_capacity")) {
				double energy = data.getDoubleOr("ic_energy", 0D);
				double capacity = data.getDoubleOr("ic_energy_capacity", 0D);
				if (capacity > 0D) {
					tooltip.add(Component.literal("")
							.append(ICUtils.formatEnergy(energy).withStyle(ChatFormatting.GRAY))
							.append(" / ")
							.append(ICUtils.formatEnergy(capacity).withStyle(ChatFormatting.GRAY))
							.withStyle(ChatFormatting.DARK_GRAY));
				}
			}
			if (data.contains("ic_progress") && data.contains("ic_max_progress")) {
				int prog = data.getIntOr("ic_progress", 0);
				int max = data.getIntOr("ic_max_progress", 0);
				if (max > 0 && prog > 0) {
					int pct = Math.min(100, Math.round(100F * prog / (float) max));
					tooltip.add(Component.translatable("ic.jade.progress", pct)
							.withStyle(ChatFormatting.GREEN));
				}
			}
			if (data.getBooleanOr("ic_starving", false)) {
				tooltip.add(Component.translatable("ic.jade.starving").withStyle(ChatFormatting.RED));
			}
			if (data.contains("ic_reactor_max_heat")) {
				int heat = data.getIntOr("ic_reactor_heat", 0);
				int maxHeat = data.getIntOr("ic_reactor_max_heat", 1);
				double out = data.getDoubleOr("ic_reactor_output", 0D);
				boolean paused = data.getBooleanOr("ic_reactor_paused", false);
				int pct = Math.min(100, Math.round(100F * heat / (float) maxHeat));
				ChatFormatting heatColor = pct >= 75 ? ChatFormatting.RED
						: pct >= 50 ? ChatFormatting.GOLD
						: pct >= 25 ? ChatFormatting.YELLOW
						: ChatFormatting.GREEN;
				tooltip.add(Component.translatable("ic.jade.reactor_heat", pct).withStyle(heatColor));
				tooltip.add(Component.translatable(paused ? "ic.jade.reactor_paused"
								: "ic.jade.reactor_output", ICUtils.formatEnergy(out))
						.withStyle(paused ? ChatFormatting.GRAY : ChatFormatting.AQUA));
			}
			if (data.contains("ic_tele_tank_capacity")) {
				int sendItems = data.getIntOr("ic_tele_send_items", 0);
				int receiveItems = data.getIntOr("ic_tele_receive_items", 0);
				int sendFluid = data.getIntOr("ic_tele_send_fluid_amount", 0);
				int receiveFluid = data.getIntOr("ic_tele_receive_fluid_amount", 0);
				int tankCap = data.getIntOr("ic_tele_tank_capacity", 1);
				String sendFluidId = data.getStringOr("ic_tele_send_fluid", "");
				String receiveFluidId = data.getStringOr("ic_tele_receive_fluid", "");
				tooltip.add(Component.translatable("ic.jade.tele_power").withStyle(ChatFormatting.GOLD));
				tooltip.add(Component.translatable("ic.jade.tele_send_items", sendItems).withStyle(ChatFormatting.AQUA));
				tooltip.add(Component.translatable("ic.jade.tele_receive_items", receiveItems).withStyle(ChatFormatting.GREEN));
				if (sendFluid > 0) {
					String label = prettyFluidId(sendFluidId);
					tooltip.add(Component.translatable("ic.jade.tele_send_fluid", label, sendFluid, tankCap).withStyle(ChatFormatting.AQUA));
				} else {
					tooltip.add(Component.translatable("ic.jade.tele_send_fluid_empty", tankCap).withStyle(ChatFormatting.DARK_AQUA));
				}
				if (receiveFluid > 0) {
					String label = prettyFluidId(receiveFluidId);
					tooltip.add(Component.translatable("ic.jade.tele_receive_fluid", label, receiveFluid, tankCap).withStyle(ChatFormatting.GREEN));
				} else {
					tooltip.add(Component.translatable("ic.jade.tele_receive_fluid_empty", tankCap).withStyle(ChatFormatting.DARK_GREEN));
				}
			}
			if (data.contains("ic_fluid_capacity")) {
				int fluid = data.getIntOr("ic_fluid", 0);
				int fluidCap = data.getIntOr("ic_fluid_capacity", 0);
				String fluidName = data.getStringOr("ic_fluid_name", "");
				if (fluidCap > 0) {
					ChatFormatting color = switch (fluidName) {
						case "water" -> ChatFormatting.BLUE;
						case "lava" -> ChatFormatting.GOLD;
						default -> ChatFormatting.GRAY;
					};
					String key = switch (fluidName) {
						case "lava" -> "ic.jade.lava";
						case "water" -> "ic.jade.water";
						case "empty" -> "ic.jade.fluid_empty";
						default -> "ic.jade.fluid";
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
