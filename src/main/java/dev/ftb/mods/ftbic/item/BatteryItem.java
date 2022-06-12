package dev.ftb.mods.ftbic.item;

import dev.ftb.mods.ftbic.util.EnergyItemHandler;
import dev.ftb.mods.ftbic.util.EnergyTier;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.jetbrains.annotations.Nullable;

public class BatteryItem extends ElectricItem {
	public final BatteryType batteryType;

	public BatteryItem(BatteryType b, EnergyTier t, double cap) {
		super(t, cap);
		batteryType = b;
	}

	@Nullable
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
		if (batteryType.singleUse) {
			CompoundTag t = stack.getOrCreateTag();

			if (!t.contains("Energy")) {
				t.putDouble("Energy", capacity);
			}
		}

		return null;
	}

	@Override
	public boolean isFoil(ItemStack stack) {
		return stack.hasTag() && stack.getTag().getBoolean("Active");
	}

	@Override
	public boolean canInsertEnergy() {
		return !batteryType.singleUse;
	}

	@Override
	public boolean canExtractEnergy() {
		return true;
	}

	@Override
	public boolean isCreativeEnergyItem() {
		return batteryType.creative;
	}

	@Override
	public double extractEnergy(ItemStack stack, double maxExtract, boolean simulate) {
		double d = super.extractEnergy(stack, maxExtract, simulate);

		if (!simulate && getEnergy(stack) <= 0D) {
			if (batteryType.singleUse) {
				stack.shrink(1);
			} else {
				stack.removeTagKey("Energy");
			}
		}

		return d;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);

		if (stack.hasTag() && stack.getTag().getBoolean("Active")) {
			stack.removeTagKey("Active");
		} else {
			stack.addTagElement("Active", ByteTag.valueOf(true));
		}

		return InteractionResultHolder.success(stack);
	}

	@Override
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean bl) {
		if (entity instanceof LivingEntity && stack.hasTag() && stack.getTag().getBoolean("Active") && getEnergy(stack) > 0D) {
			for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
				ItemStack is = ((LivingEntity) entity).getItemBySlot(equipmentSlot);

				if (is != stack && is.getItem() instanceof EnergyItemHandler handler) {
                    double e = getEnergy(stack);

					if (e > 0D) {
						double d = handler.insertEnergy(is, e, false);
						extractEnergy(stack, d, false);
					} else {
						break;
					}
				}
			}
		}
	}
}
