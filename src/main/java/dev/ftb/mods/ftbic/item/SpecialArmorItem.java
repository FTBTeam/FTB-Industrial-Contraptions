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
import net.minecraft.world.entity.ai.attributes.Attributes;
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
import java.util.UUID;
import java.util.function.Consumer;

public class SpecialArmorItem extends ArmorItem implements EnergyItemHandler {
	private static final UUID[] ARMOR_MODIFIER_UUID_PER_SLOT = new UUID[]{
			UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"),
			UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"),
			UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"),
			UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150")
	};

	private static final int[] DEFENSES = new int[]{3, 6, 8, 3};

	private final Multimap<Attribute, AttributeModifier> defaultModifiers;

	public SpecialArmorItem(FTBICArmorMaterial m, EquipmentSlot s) {
		super(m, s, new Properties().tab(FTBIC.TAB));
		int multiplier = m == FTBICArmorMaterial.QUANTUM ? 50 : 4;

		ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
		UUID uuid = ARMOR_MODIFIER_UUID_PER_SLOT[slot.getIndex()];
		builder.put(Attributes.ARMOR, new AttributeModifier(uuid, "Armor modifier", DEFENSES[slot.getIndex()] * multiplier, AttributeModifier.Operation.ADDITION));
		builder.put(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(uuid, "Armor toughness", 3F * multiplier, AttributeModifier.Operation.ADDITION));
		builder.put(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(uuid, "Armor knockback resistance", 0.25F * multiplier, AttributeModifier.Operation.ADDITION));
		defaultModifiers = builder.build();
	}

	@Override
	public EnergyTier getEnergyTier() {
		return material == FTBICArmorMaterial.QUANTUM ? EnergyTier.EV : EnergyTier.HV;
	}

	@Override
	public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken) {
		double energy = getEnergy(stack);
		double e = Math.min(energy, FTBICConfig.ARMOR_DAMAGE_ENERGY * amount);
		setEnergy(stack, energy - e);
		return 0;
	}

	@Override
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot s, ItemStack stack) {
		return s == slot && getEnergy(stack) >= FTBICConfig.ARMOR_DAMAGE_ENERGY ? defaultModifiers : ImmutableMultimap.of();
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
		if (!isCreativeEnergyItem()) {
			list.add(FTBICUtils.formatEnergy(getEnergy(stack), getEnergyCapacity(stack)).withStyle(ChatFormatting.GRAY));
		}
	}

	@Override
	public boolean canElytraFly(ItemStack stack, LivingEntity entity) {
		return material == FTBICArmorMaterial.QUANTUM && slot == EquipmentSlot.CHEST && getEnergy(stack) >= FTBICConfig.ARMOR_FLIGHT_ENERGY;
	}

	@Override
	public boolean elytraFlightTick(ItemStack stack, LivingEntity entity, int flightTicks) {
		if (!entity.level.isClientSide) {
			double energy = getEnergy(stack);
			double e = Math.min(energy, FTBICConfig.ARMOR_FLIGHT_ENERGY);
			setEnergy(stack, energy - e);
		}

		if (flightTicks >= 3 && entity.isCrouching()) {
			double d = 0.92D;
			Vec3 m = entity.getDeltaMovement();
			entity.setDeltaMovement(m.multiply(d, d, d));
		} else if (flightTicks >= 10 && entity.isSprinting()) {
			Vec3 v = entity.getLookAngle();
			double d0 = 1.5D;
			double d1 = 0.1D;
			Vec3 m = entity.getDeltaMovement();
			entity.setDeltaMovement(m.add(v.x * d1 + (v.x * d0 - m.x) * 0.5D, v.y * d1 + (v.y * d0 - m.y) * 0.5D, v.z * d1 + (v.z * d0 - m.z) * 0.5D));
		}

		return true;
	}
}
