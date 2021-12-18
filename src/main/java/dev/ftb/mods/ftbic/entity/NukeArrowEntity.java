package dev.ftb.mods.ftbic.entity;

import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.item.FTBICItems;
import dev.ftb.mods.ftbic.util.NukeExplosion;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

import java.util.UUID;

public class NukeArrowEntity extends AbstractArrow {
	private UUID ownerId;

	public NukeArrowEntity(EntityType<? extends NukeArrowEntity> arg, Level level) {
		super(arg, level);
	}

	public NukeArrowEntity(Level level, LivingEntity entity) {
		super(FTBICEntities.NUKE_ARROW.get(), entity, level);
		ownerId = entity.getUUID();
	}

	@Override
	protected ItemStack getPickupItem() {
		return new ItemStack(FTBICItems.NUKE_ARROW.get());
	}

	@Override
	public void readAdditionalSaveData(CompoundTag arg) {
		super.readAdditionalSaveData(arg);
		ownerId = arg.hasUUID("Owner") ? arg.getUUID("Owner") : null;
	}

	@Override
	protected void onHitBlock(BlockHitResult hit) {
		super.onHitBlock(hit);
		Entity owner = getOwner();
		kill();

		if (level instanceof ServerLevel && ownerId != null) {
			NukeExplosion.create((ServerLevel) level, hit.getBlockPos(), FTBICConfig.NUKE_RADIUS, ownerId, owner == null ? "Unknown" : owner.getScoreboardName());
		}
	}
}
