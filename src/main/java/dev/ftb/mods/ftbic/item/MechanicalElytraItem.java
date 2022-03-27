package dev.ftb.mods.ftbic.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.util.EnergyArmorMaterial;
import dev.ftb.mods.ftbic.util.EnergyItemHandler;
import dev.ftb.mods.ftbic.util.FTBICUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class MechanicalElytraItem extends ArmorItem implements EnergyItemHandler {
	public MechanicalElytraItem() {
		super(EnergyArmorMaterial.ELYTRA, EquipmentSlot.CHEST, new Properties().tab(FTBIC.TAB));
	}

	public void damageEnergyItem(ItemStack stack, double amount) {
		double energy = getEnergy(stack);
		double e = Math.min(energy, amount);
		setEnergy(stack, energy - e);
	}

	@Override
	public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken) {
		return 0;
	}

	@Override
	public double getEnergyCapacity(ItemStack stack) {
		return ((EnergyArmorMaterial) material).capacity;
	}

	@Override
	public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot s) {
		return ImmutableMultimap.of();
	}

	@Override
	public boolean isValidRepairItem(ItemStack stack, ItemStack item) {
		return false;
	}

	@Override
	public void fillItemCategory(CreativeModeTab tab, NonNullList<ItemStack> list) {
		if (allowdedIn(tab)) {
			list.add(new ItemStack(this));

			ItemStack full = new ItemStack(this);
			setEnergyRaw(full, getEnergyCapacity(full));
			list.add(full);
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> list, TooltipFlag flag) {
		list.add(FTBICUtils.energyTooltip(stack, this));
	}

	@Override
	public boolean canElytraFly(ItemStack stack, LivingEntity entity) {
		return getEnergy(stack) >= FTBICConfig.EQUIPMENT.ARMOR_FLIGHT_ENERGY.get();
	}

	@Override
	public boolean elytraFlightTick(ItemStack stack, LivingEntity entity, int flightTicks) {
		if (!entity.level.isClientSide) {
			damageEnergyItem(stack, FTBICConfig.EQUIPMENT.ARMOR_FLIGHT_ENERGY.get());
		}

		if (flightTicks >= 3 && entity.isCrouching()) {
			Vec3 m = entity.getDeltaMovement();
			double d = Math.max(Math.abs(m.y), Math.max(Math.abs(m.x), Math.abs(m.z)));
			d = Math.min(d, 1D);
			d = d * 0.91D;
			entity.setDeltaMovement(m.multiply(d, d, d));
			damageEnergyItem(stack, FTBICConfig.EQUIPMENT.ARMOR_FLIGHT_STOP.get());
		} else if (flightTicks >= 5 && entity.isSprinting()) {
			Vec3 v = entity.getLookAngle();
			double d0 = 1.5D;
			double d1 = 0.1D;
			Vec3 m = entity.getDeltaMovement();
			entity.setDeltaMovement(m.add(v.x * d1 + (v.x * d0 - m.x) * 0.5D, v.y * d1 + (v.y * d0 - m.y) * 0.5D, v.z * d1 + (v.z * d0 - m.z) * 0.5D));
			damageEnergyItem(stack, FTBICConfig.EQUIPMENT.ARMOR_FLIGHT_BOOST.get());
		}

		return true;
	}

	@Override
	public void onArmorTick(ItemStack stack, Level level, Player player) {
		if (FTBICConfig.EQUIPMENT.MECHANICAL_ELYTRA_RECHARGE.get() > 0D && !level.isClientSide() && !player.isFallFlying() && level.isDay() && level.canSeeSky(new BlockPos(player.getEyePosition(1F)))) {
			insertEnergy(stack, FTBICConfig.EQUIPMENT.MECHANICAL_ELYTRA_RECHARGE.get(), false);
		}
	}

	@Override
	public boolean isBarVisible(ItemStack stack) {
		return stack.hasTag() && stack.getTag().contains("Energy");
	}

	@Override
	public int getBarWidth(ItemStack stack) {
		return Math.round((float) Mth.clamp((getEnergy(stack) / getEnergyCapacity(stack)) * 13D, 0D, 13D));
	}

	@Override
	public int getBarColor(ItemStack stack) {
		return 0xFFFF0000;
	}
}
