package dev.ftb.mods.ftbic.entity;

import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.item.FTBICItems;
import dev.ftb.mods.ftbic.util.NuclearExplosion;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.network.NetworkHooks;

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
		launchNuke(hit.getBlockPos(), hit.getDirection());
		kill();
	}

	@Override
	protected void onHitEntity(EntityHitResult result) {
		super.onHitEntity(result);
		launchNuke(result.getEntity().getOnPos(), result.getEntity().getDirection());
		kill();
	}

	private void launchNuke(BlockPos pos, Direction direction) {
		Entity owner = getOwner();
		if (level instanceof ServerLevel && ownerId != null) {
			Component ownerName = owner == null ? new TextComponent("Unknown") : owner.getDisplayName();

			NuclearExplosion.builder((ServerLevel) level, pos.relative(direction), FTBICConfig.NUCLEAR.NUKE_RADIUS.get(), ownerId, ownerName.getString())
					.preExplosion(() -> level.getServer().getPlayerList().broadcastMessage(new TranslatableComponent("block.ftbic.nuke.broadcast", ownerName), ChatType.SYSTEM, Util.NIL_UUID))
					.create()
			;
		}
	}

	@Override
	public Packet<?> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}
}
