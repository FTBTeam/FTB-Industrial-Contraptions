package dev.ftb.mods.ftbic.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.util.EnergyArmorMaterial;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class DummyEnergyArmorItem extends ArmorItem {
	public DummyEnergyArmorItem(EnergyArmorMaterial m, EquipmentSlot s) {
		super(m, s, new Properties().tab(FTBIC.TAB).fireResistant());
	}

	@Override
	public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken) {
		ItemStack chest = entity.getItemBySlot(EquipmentSlot.CHEST);
		return chest.getItem() instanceof EnergyArmorItem ? chest.getItem().damageItem(stack, amount, entity, onBroken) : 0;
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
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> list, TooltipFlag flag) {
		list.add(new TranslatableComponent("ftbic.requires_chestplate").withStyle(ChatFormatting.GRAY));
	}
}
