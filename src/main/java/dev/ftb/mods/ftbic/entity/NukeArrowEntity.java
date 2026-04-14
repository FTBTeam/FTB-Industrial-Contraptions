package dev.ftb.mods.ftbic.entity;

import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.item.FTBICItems;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Nuclear-tipped arrow. Explodes on impact with a radius equal to the configured nuke radius.
 * Full 1.18.2 `NuclearExplosion` simulation (threaded block destruction + fallout) is deferred —
 * the vanilla Explosion at the configured radius is close enough.
 */
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
			server.explode(this, null, null, at.x, at.y, at.z,
					(float) radius, true, Level.ExplosionInteraction.BLOCK);
			dev.ftb.mods.ftbic.util.NuclearFallout.apply(server,
					net.minecraft.core.BlockPos.containing(at), radius);
			kill(server);
		}
	}
}
