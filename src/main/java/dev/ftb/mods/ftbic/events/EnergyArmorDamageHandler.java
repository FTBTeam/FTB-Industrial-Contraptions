package dev.ftb.mods.ftbic.events;

import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.item.DummyEnergyArmorItem;
import dev.ftb.mods.ftbic.item.EnergyArmorItem;
import dev.ftb.mods.ftbic.item.MechanicalElytraItem;
import dev.ftb.mods.ftbic.util.EnergyArmorMaterial;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

@EventBusSubscriber(modid = FTBIC.MOD_ID)
public final class EnergyArmorDamageHandler {

	@SubscribeEvent
	public static void onLivingDamage(LivingDamageEvent.Pre event) {
		if (!(event.getEntity() instanceof Player player)) {
			return;
		}

		ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
		if (!(chest.getItem() instanceof EnergyArmorItem armor)) {
			return;
		}
		if (armor.getEnergy(chest) <= 0D) {
			return;
		}

		float incoming = event.getNewDamage();
		if (incoming <= 0F) {
			return;
		}

		float protection = 0.35F;
		if (isMatchingDummy(player, EquipmentSlot.HEAD, armor.getMaterial())) protection += 0.25F;
		if (isMatchingDummy(player, EquipmentSlot.LEGS, armor.getMaterial())) protection += 0.35F;
		if (isMatchingDummy(player, EquipmentSlot.FEET, armor.getMaterial())) protection += 0.15F;
		protection = Math.min(protection, 1F);

		float absorbed = incoming * protection;
		double energyCost = FTBICConfig.EQUIPMENT.ARMOR_DAMAGE_ENERGY.get() * absorbed;
		armor.damageEnergyItem(chest, energyCost);
		event.setNewDamage(incoming - absorbed);
	}

	@SubscribeEvent
	public static void onFlyIntoWall(LivingDamageEvent.Pre event) {
		if (!event.getSource().is(DamageTypes.FLY_INTO_WALL)) {
			return;
		}
		ItemStack chest = event.getEntity().getItemBySlot(EquipmentSlot.CHEST);
		if (!(chest.getItem() instanceof MechanicalElytraItem elytra)) {
			return;
		}
		if (!elytra.isCreativeEnergyItem() && elytra.getEnergy(chest) <= 0D) {
			return;
		}
		float incoming = event.getNewDamage();
		if (incoming <= 0F) {
			return;
		}
		double cost = FTBICConfig.EQUIPMENT.ARMOR_DAMAGE_ENERGY.get() * incoming;
		elytra.damageEnergyItem(chest, cost);
		event.setNewDamage(0F);
	}

	private static boolean isMatchingDummy(Player player, EquipmentSlot slot, EnergyArmorMaterial material) {
		ItemStack stack = player.getItemBySlot(slot);
		return stack.getItem() instanceof DummyEnergyArmorItem dummy && dummy.getMaterial() == material;
	}

	private EnergyArmorDamageHandler() {}
}
