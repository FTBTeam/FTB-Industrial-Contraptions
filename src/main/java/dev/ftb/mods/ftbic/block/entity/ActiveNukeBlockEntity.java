package dev.ftb.mods.ftbic.block.entity;

import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.util.NuclearExplosion;
import dev.ftb.mods.ftbic.util.NuclearFallout;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.util.Util;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.UUID;

public class ActiveNukeBlockEntity extends BlockEntity {
	private boolean armed = false;
	private int fuseTicks = 0;
	private double radius = 0D;
	private UUID ownerId = Util.NIL_UUID;
	private String ownerName = "";

	public ActiveNukeBlockEntity(BlockPos pos, BlockState state) {
		super(FTBICBlockEntities.ACTIVE_NUKE.get(), pos, state);
	}

	public void arm(int fuseTicks, double radius, UUID ownerId, String ownerName) {
		this.armed = true;
		this.fuseTicks = fuseTicks;
		this.radius = radius;
		this.ownerId = ownerId == null ? Util.NIL_UUID : ownerId;
		this.ownerName = ownerName == null ? "" : ownerName;
		setChanged();
	}

	public void serverTick() {
		if (!armed || !(level instanceof ServerLevel server)) return;
		if (fuseTicks > 0) {
			fuseTicks--;
			return;
		}
		detonate(server);
	}

	private void detonate(ServerLevel server) {
		armed = false;
		server.removeBlock(worldPosition, false);
		if (FTBICConfig.NUCLEAR.NUKE_RESPECTS_CLAIMS.get()) {
			server.explode(null, null, null,
					worldPosition.getX() + 0.5D, worldPosition.getY() + 0.5D, worldPosition.getZ() + 0.5D,
					(float) radius, true, Level.ExplosionInteraction.BLOCK);
			NuclearFallout.apply(server, worldPosition, radius);
		} else {
			NuclearExplosion.detonate(server, worldPosition, radius, ownerId, ownerName);
		}
	}

	@Override
	protected void saveAdditional(ValueOutput output) {
		super.saveAdditional(output);
		output.putBoolean("Armed", armed);
		output.putInt("FuseTicks", fuseTicks);
		output.putDouble("Radius", radius);
		if (!Util.NIL_UUID.equals(ownerId)) {
			output.store("OwnerId", UUIDUtil.CODEC, ownerId);
		}
		if (!ownerName.isEmpty()) {
			output.putString("OwnerName", ownerName);
		}
	}

	@Override
	protected void loadAdditional(ValueInput input) {
		super.loadAdditional(input);
		armed = input.getBooleanOr("Armed", false);
		fuseTicks = input.getIntOr("FuseTicks", 0);
		radius = input.getDoubleOr("Radius", 0D);
		ownerId = input.read("OwnerId", UUIDUtil.CODEC).orElse(Util.NIL_UUID);
		ownerName = input.getStringOr("OwnerName", "");
	}
}
