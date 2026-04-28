package dev.ftb.mods.ftbic.entity;

import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.item.FTBICItems;
import dev.ftb.mods.ftbic.util.NuclearExplosion;
import dev.ftb.mods.ftbic.util.NuclearFallout;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Util;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

public final class NukeArrowEntity extends AbstractArrow {
	public NukeArrowEntity(EntityType<? extends NukeArrowEntity> type, Level level) {
		super(type, level);
	}

	public NukeArrowEntity(Level level, LivingEntity shooter) {
		super(FTBICEntities.NUKE_ARROW.get(), level);
		this.setOwner(shooter);
		this.setPos(shooter.getX(), shooter.getEyeY() - 0.1, shooter.getZ());
	}

	@Override
	protected ItemStack getDefaultPickupItem() {
		return new ItemStack(FTBICItems.NUKE_ARROW.get());
	}

	@Override
	protected void onHitBlock(BlockHitResult hit) {
		super.onHitBlock(hit);
		detonate(hit.getLocation());
	}

	@Override
	protected void onHitEntity(EntityHitResult result) {
		super.onHitEntity(result);
		detonate(result.getLocation());
	}

	private void detonate(Vec3 at) {
		if (level() instanceof ServerLevel server) {
			double radius = FTBICConfig.NUCLEAR.NUKE_RADIUS.get();
			BlockPos pos = BlockPos.containing(at);
			if (FTBICConfig.NUCLEAR.NUKE_RESPECTS_CLAIMS.get()) {
				server.explode(this, null, null, at.x, at.y, at.z,
						(float) radius, true, Level.ExplosionInteraction.BLOCK);
				NuclearFallout.apply(server, pos, radius);
			} else {
				Entity owner = getOwner();
				UUID ownerId = owner == null ? Util.NIL_UUID : owner.getUUID();
				String ownerName = owner instanceof Player p ? p.getScoreboardName() : (owner == null ? "" : owner.getName().getString());
				NuclearExplosion.detonate(server, pos, radius, ownerId, ownerName);
			}
			kill(server);
		}
	}
}
