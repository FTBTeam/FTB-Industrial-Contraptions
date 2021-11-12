package dev.ftb.mods.ftbic.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.util.EnergyItemHandler;
import dev.ftb.mods.ftbic.util.EnergyTier;
import dev.ftb.mods.ftbic.util.FTBICArmorMaterial;
import dev.ftb.mods.ftbic.util.FTBICUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
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

public class EnergyArmorItem extends ArmorItem implements EnergyItemHandler {
	public EnergyArmorItem(FTBICArmorMaterial m, EquipmentSlot s) {
		super(m, s, new Properties().tab(FTBIC.TAB));
	}

	@Override
	public EnergyTier getEnergyTier() {
		return material == FTBICArmorMaterial.QUANTUM ? EnergyTier.EV : EnergyTier.HV;
	}

	public void damageEnergyItem(ItemStack stack, double amount) {
		double energy = getEnergy(stack);
		double e = Math.min(energy, amount);
		setEnergy(stack, energy - e);
	}

	@Override
	public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken) {
		damageEnergyItem(stack, FTBICConfig.ARMOR_DAMAGE_ENERGY * amount);
		return 0;
	}

	@Override
	public double getEnergyCapacity(ItemStack stack) {
		return ((FTBICArmorMaterial) material).capacity;
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
		list.add(FTBICUtils.formatEnergy(stack, this).withStyle(ChatFormatting.GRAY));
	}

	@Override
	public boolean canElytraFly(ItemStack stack, LivingEntity entity) {
		return material == FTBICArmorMaterial.QUANTUM && slot == EquipmentSlot.CHEST && getEnergy(stack) >= FTBICConfig.ARMOR_FLIGHT_ENERGY;
	}

	@Override
	public boolean elytraFlightTick(ItemStack stack, LivingEntity entity, int flightTicks) {
		if (!entity.level.isClientSide) {
			damageEnergyItem(stack, FTBICConfig.ARMOR_FLIGHT_ENERGY);
		}

		if (flightTicks >= 3 && entity.isCrouching()) {
			double d = 0.92D;
			Vec3 m = entity.getDeltaMovement();
			entity.setDeltaMovement(m.multiply(d, d, d));
		} else if (flightTicks >= 5 && entity.isSprinting()) {
			Vec3 v = entity.getLookAngle();
			double d0 = 1.5D;
			double d1 = 0.1D;
			Vec3 m = entity.getDeltaMovement();
			entity.setDeltaMovement(m.add(v.x * d1 + (v.x * d0 - m.x) * 0.5D, v.y * d1 + (v.y * d0 - m.y) * 0.5D, v.z * d1 + (v.z * d0 - m.z) * 0.5D));
		}

		return true;
	}
}
