package dev.ftb.mods.ftbic.block.entity;

import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.sound.FTBICSounds;
import dev.ftb.mods.ftbic.util.NuclearExplosion;
import dev.ftb.mods.ftbic.util.NuclearFallout;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Util;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class ActiveNukeBlockEntity extends BlockEntity {
    private boolean armed = false;
    private int fuseTicks = 0;
    private long nextBeepTick;
    private double radius = 0D;
    private UUID ownerId = Util.NIL_UUID;
    private String ownerName = "";

    public ActiveNukeBlockEntity(BlockPos pos, BlockState state) {
        super(FTBICBlockEntities.ACTIVE_NUKE.get(), pos, state);
    }

    public void arm(int fuseTicks, double radius, UUID ownerId, String ownerName) {
        if (!armed && level != null && !level.isClientSide()) {
            level.playSound(null, worldPosition, FTBICSounds.NUKE_ARM.get(), SoundSource.BLOCKS, 0.8F, 1F);
            nextBeepTick = level.getGameTime() + 30;
        }
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
            if (server.getGameTime() >= nextBeepTick) {
                server.playSound(null, worldPosition, FTBICSounds.NUKE_TICK.get(), SoundSource.BLOCKS, 0.8F, 1F);
                nextBeepTick = server.getGameTime() + (fuseTicks <= 60 ? 10 : 20);
            }
            fuseTicks--;
            return;
        }
        detonate(server);
    }

    private void detonate(ServerLevel server) {
        armed = false;
        server.removeBlock(worldPosition, false);
        if (FTBICConfig.NUCLEAR.NUKE_RESPECTS_CLAIMS.get()) {
            NuclearExplosion.explodeWithClaims(
                    server,
                    null,
                    worldPosition.getX() + 0.5D,
                    worldPosition.getY() + 0.5D,
                    worldPosition.getZ() + 0.5D,
                    (float) radius);
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
