package dev.ftb.mods.ftbic.block.entity.generator;

import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.block.CableBlock;
import dev.ftb.mods.ftbic.block.ElectricBlockInstance;
import dev.ftb.mods.ftbic.block.NuclearReactorChamberBlock;
import dev.ftb.mods.ftbic.block.SuperconductingCableBlock;
import dev.ftb.mods.ftbic.block.entity.ElectricBlockEntity;
import dev.ftb.mods.ftbic.block.entity.SuperconductingCableBlockEntity;
import dev.ftb.mods.ftbic.block.entity.machine.BatteryInventory;
import dev.ftb.mods.ftbic.util.BatterySlotHelper;
import dev.ftb.mods.ftbic.util.CachedEnergyStorage;
import dev.ftb.mods.ftbic.util.CachedEnergyStorageOrigin;
import dev.ftb.mods.ftbic.util.EnergyItemHandler;
import dev.ftb.mods.ftbic.util.EnergyTier;
import dev.ftb.mods.ftbic.util.FTBICCapabilities;
import dev.ftb.mods.ftbic.util.FTBICUtils;
import dev.ftb.mods.ftbic.util.SideConfiguration;
import dev.ftb.mods.ftbic.util.ZapEnergyHandler;
import dev.ftb.mods.ftbic.util.ZapFEConversion;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;

public class GeneratorBlockEntity extends ElectricBlockEntity {
    public double maxEnergyOutput;
    public double maxEnergyOutputTransfer;

    public final BatteryInventory chargeBatteryInventory;
    private long currentElectricNetwork = -1L;
    private CachedEnergyStorage[] connectedEnergyBlocks;
    private int scannedEnergyNeighbours;
    private int[] validConsumerIndices;
    private final Map<Long, BlockCapabilityCache<EnergyHandler, Direction>> fePushCaches = new HashMap<>();
    private final Map<Long, BlockCapabilityCache<ZapEnergyHandler, Direction>> zapPushCaches = new HashMap<>();
    private final Map<Long, BlockCapabilityCache<EnergyHandler, Direction>> feFindCaches = new HashMap<>();
    private final Map<Long, BlockCapabilityCache<ZapEnergyHandler, Direction>> zapFindCaches = new HashMap<>();

    public GeneratorBlockEntity(ElectricBlockInstance type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        chargeBatteryInventory = new BatteryInventory(this, true);
    }

    @Override
    public void initProperties() {
        super.initProperties();
        maxEnergyOutput = electricBlockInstance.maxEnergyOutput.get();
        maxEnergyOutputTransfer = FTBICConfig.ENERGY.LV_TRANSFER_RATE.get();
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        ItemStack chargeStack = chargeBatteryInventory.getStackInSlot(0);
        if (!chargeStack.isEmpty()) {
            output.store("ChargeBattery", ItemStack.CODEC, chargeStack);
        }
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        if (!isClientSync(input)) {
            chargeBatteryInventory.loadItem(
                    input.read("ChargeBattery", ItemStack.CODEC).orElse(ItemStack.EMPTY));
        }
    }

    @Override
    public void onBroken(Level level, BlockPos pos) {
        super.onBroken(level, pos);
        Block.popResource(level, pos, chargeBatteryInventory.getStackInSlot(0));
    }

    public void handleGeneration() {}

    protected List<BatteryInventory> chargeInventories() {
        return List.of(chargeBatteryInventory);
    }

    private double chargeItem(ItemStack battery, double remainingOutput) {
        if (battery.isEmpty()) return 0D;
        if (battery.getItem() instanceof EnergyItemHandler item) {
            double transfer = item.isCreativeEnergyItem()
                    ? Double.POSITIVE_INFINITY
                    : remainingOutput * FTBICConfig.MACHINES.ITEM_TRANSFER_EFFICIENCY.get();
            return item.insertEnergy(battery, Math.min(energy, Math.min(transfer, remainingOutput)), false);
        }
        double transfer = remainingOutput * FTBICConfig.MACHINES.ITEM_TRANSFER_EFFICIENCY.get();
        return BatterySlotHelper.chargeForeignItem(battery, Math.min(energy, Math.min(transfer, remainingOutput)));
    }

    public void handleEnergyOutput() {
        if (level == null || level.isClientSide()) {
            return;
        }

        double remainingOutput = maxEnergyOutputTransfer - pushFEToNeighbours();

        boolean chargedItem = false;
        for (BatteryInventory inventory : chargeInventories()) {
            if (energy <= 0D || remainingOutput <= 0D) break;
            ItemStack battery = inventory.getStackInSlot(0);
            boolean neededCharge = BatterySlotHelper.needsCharge(battery);
            double accepted = chargeItem(battery, remainingOutput);
            if (accepted > 0) {
                chargedItem |= neededCharge;
                energy -= accepted;
                remainingOutput -= accepted;
                active = true;
                setChanged();
            }
        }

        if (chargedItem
                && chargeInventories().stream()
                        .noneMatch(inventory -> BatterySlotHelper.needsCharge(inventory.getStackInSlot(0)))) {
            playChargeCompleteSound();
        }

        double transferable = Math.min(energy, remainingOutput);
        if (transferable <= 0D) {
            return;
        }

        CachedEnergyStorage[] blocks = getConnectedEnergyBlocks();
        if (blocks.length == 0) {
            return;
        }
        if (validConsumerIndices == null || validConsumerIndices.length < blocks.length) {
            validConsumerIndices = new int[blocks.length];
        }
        int[] valid = validConsumerIndices;
        int validBlocks = 0;
        for (int i = 0; i < blocks.length; i++) {
            CachedEnergyStorage storage = blocks[i];
            if (storage.isInvalid()) {
                electricNetworkUpdated(level, storage.blockEntity.getBlockPos());
            } else if (storage.shouldReceiveEnergy()) {
                valid[validBlocks++] = i;
            }
        }

        if (validBlocks == 0) {
            return;
        }

        double share = transferable / validBlocks;
        boolean changed = false;
        for (int vi = 0; vi < validBlocks; vi++) {
            CachedEnergyStorage storage = blocks[valid[vi]];
            double thisShare = share;
            if (storage.feHandlerCache != null) {
                thisShare = Math.min(thisShare, storage.origin.cableTransferRate);
            } else if (storage.origin.cableTransferRate < share) {
                burnCableNetwork(storage.origin.cablePos, storage.origin.cableTier);
                storage.origin.cableBurnt = true;
                continue;
            }

            double accepted = storage.insertZaps(Math.min(thisShare, energy));
            if (accepted > 0D) {
                for (BlockPos cablePos : storage.superconductingPath) {
                    if (level.getBlockEntity(cablePos) instanceof SuperconductingCableBlockEntity cable)
                        cable.recordTransfer();
                }
                energy -= accepted;
                active = true;
                changed = true;
            }
            if (energy < share) {
                break;
            }
        }
        if (changed) {
            setChanged();
        }
    }

    @Override
    public void tick() {
        if (level != null && !level.isClientSide()) {
            handleGeneration();
        }
        handleEnergyOutput();
        super.tick();
    }

    public boolean isValidEnergyOutputSide(Direction direction) {
        return allowsTransfer(SideConfiguration.Resource.ENERGY, direction, false);
    }

    private double pushFEToNeighbours() {
        boolean canFEOutput = electricBlockInstance.feCapMode == ElectricBlockInstance.FECapMode.EXTRACT_ONLY
                || electricBlockInstance.feCapMode == ElectricBlockInstance.FECapMode.INSERT_AND_EXTRACT
                || (FTBICConfig.ENERGY.FULL_FE_MODE.get() && maxEnergyOutput > 0D);
        if (!canFEOutput) return 0D;
        if (energy <= 0D || maxEnergyOutputTransfer <= 0D) return 0D;
        if (!(level instanceof ServerLevel serverLevel)) return 0D;
        double remaining = maxEnergyOutputTransfer;
        for (BlockPos outputPos : energyOutputPositions()) {
            for (Direction dir : FTBICUtils.DIRECTIONS) {
                if (!isValidEnergyOutputSide(dir)) continue;
                // Native cable output below already handles these routes and voltage rules.
                if (level.getBlockState(outputPos.relative(dir)).getBlock() instanceof CableBlock) continue;
                if (zapPushCache(serverLevel, outputPos, dir).getCapability() != null) continue;
                EnergyHandler fe = fePushCache(serverLevel, outputPos, dir).getCapability();
                if (fe == null) continue;
                double zapsAvailable = Math.min(energy, remaining);
                int feToOffer = ZapFEConversion.zapsToFEFloor(zapsAvailable);
                if (feToOffer <= 0) continue;
                try (Transaction tx = Transaction.openRoot()) {
                    int feAccepted = fe.insert(feToOffer, tx);
                    if (feAccepted > 0) {
                        double zapsConsumed = Math.min(ZapFEConversion.feToZaps(feAccepted), energy);
                        energy -= zapsConsumed;
                        remaining -= zapsConsumed;
                        tx.commit();
                        active = true;
                        setChanged();
                    }
                }
                if (energy <= 0D || remaining <= 0D) break;
            }
            if (energy <= 0D || remaining <= 0D) break;
        }
        return maxEnergyOutputTransfer - remaining;
    }

    protected List<BlockPos> energyOutputPositions() {
        return List.of(worldPosition);
    }

    private BlockCapabilityCache<EnergyHandler, Direction> fePushCache(
            ServerLevel serverLevel, BlockPos outputPos, Direction dir) {
        BlockPos target = outputPos.relative(dir);
        long key = target.asLong() ^ ((long) dir.ordinal() << 56);
        return fePushCaches.computeIfAbsent(
                key,
                ignored ->
                        BlockCapabilityCache.create(Capabilities.Energy.BLOCK, serverLevel, target, dir.getOpposite()));
    }

    private BlockCapabilityCache<ZapEnergyHandler, Direction> zapPushCache(
            ServerLevel serverLevel, BlockPos outputPos, Direction dir) {
        BlockPos target = outputPos.relative(dir);
        long key = target.asLong() ^ ((long) dir.ordinal() << 56);
        return zapPushCaches.computeIfAbsent(
                key,
                ignored -> BlockCapabilityCache.create(
                        FTBICCapabilities.ZAP_ENERGY_BLOCK, serverLevel, target, dir.getOpposite()));
    }

    @Override
    public boolean isValidEnergyInputSide(Direction direction) {
        return false;
    }

    @Override
    public double getMaxOutputEnergy() {
        return maxEnergyOutputTransfer;
    }

    public CachedEnergyStorage[] getConnectedEnergyBlocks() {
        if (level == null || level.isClientSide()) {
            return CachedEnergyStorage.EMPTY;
        }

        long currentId = getCurrentElectricNetwork(level, worldPosition);
        if (connectedEnergyBlocks != null && currentElectricNetwork == currentId) {
            return connectedEnergyBlocks;
        }

        Set<CachedEnergyStorage> set = new HashSet<>();
        LongOpenHashSet traversed = new LongOpenHashSet();
        traversed.add(worldPosition.asLong());
        int maxCableLength = FTBICConfig.ENERGY.MAX_CABLE_LENGTH.get();

        for (Direction direction : FTBICUtils.DIRECTIONS) {
            if (isValidEnergyOutputSide(direction)
                    || (this instanceof NuclearReactorBlockEntity
                            && level.getBlockState(worldPosition.relative(direction))
                                            .getBlock()
                                    instanceof NuclearReactorChamberBlock)) {
                CachedEnergyStorageOrigin origin = new CachedEnergyStorageOrigin();
                origin.direction = direction;
                find(traversed, set, origin, 0, maxCableLength, worldPosition, direction, new ArrayList<>());
            }
        }

        connectedEnergyBlocks = set.toArray(CachedEnergyStorage.EMPTY);
        currentElectricNetwork = currentId;
        scannedEnergyNeighbours = energyNeighbours();
        return connectedEnergyBlocks;
    }

    @Override
    public void neighborChanged(BlockPos neighborPos, Block neighborBlock) {
        super.neighborChanged(neighborPos, neighborBlock);
        if (connectedEnergyBlocks != null && energyNeighbours() != scannedEnergyNeighbours) {
            connectedEnergyBlocks = null;
        }
    }

    private int energyNeighbours() {
        int mask = 0;
        if (level == null) {
            return mask;
        }
        for (Direction direction : FTBICUtils.DIRECTIONS) {
            BlockPos pos = worldPosition.relative(direction);
            if (!level.isLoaded(pos)) {
                continue;
            }
            BlockState state = level.getBlockState(pos);
            if (state.getBlock() instanceof NuclearReactorChamberBlock
                    || (state.hasBlockEntity()
                            && (level.getCapability(FTBICCapabilities.ZAP_ENERGY_BLOCK, pos, direction.getOpposite())
                                            != null
                                    || level.getCapability(Capabilities.Energy.BLOCK, pos, direction.getOpposite())
                                            != null))) {
                mask |= 1 << direction.ordinal();
            }
        }
        return mask;
    }

    private void find(
            LongOpenHashSet traversed,
            Set<CachedEnergyStorage> set,
            CachedEnergyStorageOrigin origin,
            int distance,
            int maxCableLength,
            BlockPos currentPos,
            Direction direction,
            List<BlockPos> superconductingPath) {
        if (level == null || distance > maxCableLength) {
            return;
        }

        BlockPos pos = currentPos.relative(direction);
        if (traversed.contains(pos.asLong()) || !level.isLoaded(pos)) {
            return;
        }

        BlockState state = level.getBlockState(pos);

        if (state.getBlock() instanceof CableBlock cableBlock) {
            traversed.add(pos.asLong());
            boolean superconducting = cableBlock instanceof SuperconductingCableBlock;
            if (superconducting) superconductingPath.add(pos);
            double rate = cableBlock.tier.transferRate();
            if (rate < origin.cableTransferRate) {
                origin.cableTier = cableBlock.tier;
                origin.cableTransferRate = rate;
                origin.cablePos = pos;
            }
            for (Direction dir : FTBICUtils.DIRECTIONS) {
                if (state.getValue(CableBlock.CONNECTION[dir.get3DDataValue()])) {
                    find(traversed, set, origin, distance + 1, maxCableLength, pos, dir, superconductingPath);
                }
            }
            if (superconducting) superconductingPath.removeLast();
            return;
        }

        if (state.getBlock() instanceof NuclearReactorChamberBlock) {
            traversed.add(pos.asLong());
            for (Direction dir : FTBICUtils.DIRECTIONS) {
                if (this instanceof NuclearReactorBlockEntity && !isValidEnergyOutputSide(dir)) continue;
                find(traversed, set, origin, distance + 1, maxCableLength, pos, dir, superconductingPath);
            }
            return;
        }

        if (!state.hasBlockEntity()) {
            return;
        }
        BlockEntity entity = level.getBlockEntity(pos);
        if (entity == null) {
            return;
        }

        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }
        long key = pos.asLong() ^ ((long) direction.ordinal() << 56);

        BlockCapabilityCache<ZapEnergyHandler, Direction> zapCache = zapFindCaches.get(key);
        if (zapCache == null) {
            zapCache = BlockCapabilityCache.create(
                    FTBICCapabilities.ZAP_ENERGY_BLOCK, serverLevel, pos, direction.getOpposite());
            zapFindCaches.put(key, zapCache);
        }
        ZapEnergyHandler zapHandler = zapCache.getCapability();
        if (zapHandler != null && zapHandler != this) {
            if (zapHandler.getMaxInputEnergy() > 0D
                    && !zapHandler.isBurnt()
                    && zapHandler.isValidEnergyInputSide(direction.getOpposite())) {
                // A rejected face must not hide another valid route to this consumer.
                traversed.add(pos.asLong());
                CachedEnergyStorage s = new CachedEnergyStorage();
                s.origin = origin;
                s.superconductingPath = List.copyOf(superconductingPath);
                s.distance = distance;
                s.blockEntity = entity;
                s.energyHandler = zapHandler;
                set.add(s);
            }
            return;
        }

        BlockCapabilityCache<EnergyHandler, Direction> feCache = feFindCaches.get(key);
        if (feCache == null) {
            feCache = BlockCapabilityCache.create(Capabilities.Energy.BLOCK, serverLevel, pos, direction.getOpposite());
            feFindCaches.put(key, feCache);
        }
        if (feCache.getCapability() == null) {
            return;
        }
        traversed.add(pos.asLong());
        CachedEnergyStorage s = new CachedEnergyStorage();
        s.origin = origin;
        s.superconductingPath = List.copyOf(superconductingPath);
        s.distance = distance;
        s.blockEntity = entity;
        s.feHandlerCache = feCache;
        set.add(s);
    }

    private void burnCableNetwork(BlockPos startPos, EnergyTier tier) {
        if (level == null || tier == null) {
            return;
        }
        LongOpenHashSet visited = new LongOpenHashSet();
        ArrayDeque<BlockPos> queue = new ArrayDeque<>();
        visited.add(startPos.asLong());
        queue.add(startPos);
        while (!queue.isEmpty()) {
            BlockPos pos = queue.poll();
            if (!level.isLoaded(pos)) {
                continue;
            }
            BlockState state = level.getBlockState(pos);
            if (!(state.getBlock() instanceof CableBlock cable) || cable.tier != tier) {
                continue;
            }
            level.setBlock(pos, cable.getBurntState(state), 3);
            level.levelEvent(1502, pos, 0);
            for (Direction dir : FTBICUtils.DIRECTIONS) {
                if (!state.getValue(CableBlock.CONNECTION[dir.get3DDataValue()])) {
                    continue;
                }
                BlockPos next = pos.relative(dir);
                if (visited.add(next.asLong())) {
                    queue.add(next);
                }
            }
        }
    }
}
