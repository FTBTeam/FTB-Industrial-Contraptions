package dev.ftb.mods.ftbic.block.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.block.ElectricBlock;
import dev.ftb.mods.ftbic.block.ElectricBlockInstance;
import dev.ftb.mods.ftbic.block.NuclearReactorChamberBlock;
import dev.ftb.mods.ftbic.block.entity.generator.GeneratorBlockEntity;
import dev.ftb.mods.ftbic.block.entity.generator.GeothermalGeneratorBlockEntity;
import dev.ftb.mods.ftbic.block.entity.machine.BatchFeederBlockEntity;
import dev.ftb.mods.ftbic.block.entity.machine.PumpBlockEntity;
import dev.ftb.mods.ftbic.block.entity.machine.ReactorSimulatorBlockEntity;
import dev.ftb.mods.ftbic.block.entity.machine.TeleporterBlockEntity;
import dev.ftb.mods.ftbic.block.entity.storage.BatteryBoxBlockEntity;
import dev.ftb.mods.ftbic.block.entity.storage.EnergyRectifierBlockEntity;
import dev.ftb.mods.ftbic.block.entity.storage.TransformerBlockEntity;
import dev.ftb.mods.ftbic.registry.ModDataComponents;
import dev.ftb.mods.ftbic.screen.MachineMenu;
import dev.ftb.mods.ftbic.sound.FTBICSounds;
import dev.ftb.mods.ftbic.util.GhostItem;
import dev.ftb.mods.ftbic.util.SideConfiguration;
import dev.ftb.mods.ftbic.util.SideConfiguration.Face;
import dev.ftb.mods.ftbic.util.SideConfiguration.Mode;
import dev.ftb.mods.ftbic.util.SideConfiguration.Resource;
import dev.ftb.mods.ftbic.util.ZapEnergyHandler;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Util;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class ElectricBlockEntity extends BlockEntity implements ZapEnergyHandler {
    private static final Map<ResourceKey<Level>, long[]> ELECTRIC_NETWORK_CHANGES = new HashMap<>();
    private static final String CLIENT_SYNC = "ClientSync";
    private static final Set<String> MENU_SYNCED_KEYS =
            Set.of("Inventory", "Upgrades", "Battery", "ChargeBattery", "Pickaxe", "PlacerId", "PlacerName");

    public static void electricNetworkUpdated(LevelAccessor level, BlockPos pos) {
        if (level instanceof ServerLevel l) {
            ELECTRIC_NETWORK_CHANGES.computeIfAbsent(l.dimension(), k -> new long[1])[0]++;
        }
    }

    public static long getCurrentElectricNetwork(LevelAccessor level, BlockPos pos) {
        if (level instanceof ServerLevel l) {
            long[] counter = ELECTRIC_NETWORK_CHANGES.get(l.dimension());
            return counter == null ? 0L : counter[0];
        }
        return 0L;
    }

    public static void forgetElectricNetwork(Level level) {
        ELECTRIC_NETWORK_CHANGES.remove(level.dimension());
    }

    public final ElectricBlockInstance electricBlockInstance;
    public double energy;
    public final ItemStack[] inputItems;
    public final ItemStack[] outputItems;
    public boolean active;
    private boolean burnt;

    public double energyCapacity;
    public double maxInputEnergy;
    public boolean autoEject;
    private List<GhostItem> inputLocks = List.of();

    public List<GhostItem> getInputLocks() {
        return inputLocks;
    }

    public ItemStack getInputLock(int slot) {
        for (GhostItem lock : inputLocks)
            if (lock.slot() == slot) return lock.item().create();
        return ItemStack.EMPTY;
    }

    public void setInputLock(int slot, ItemStack stack) {
        if (!supportsInputLocks() || slot < 0 || slot >= inputItems.length) return;
        var locks = new ArrayList<>(inputLocks);
        locks.removeIf(lock -> lock.slot() == slot);
        if (!stack.isEmpty()) locks.add(GhostItem.of(slot, stack, 1));
        setInputLocks(locks);
    }

    public void setInputLocks(List<GhostItem> locks) {
        var normalized = new ArrayList<GhostItem>();
        for (GhostItem lock : locks) {
            if (!supportsInputLocks() || lock.slot() < 0 || lock.slot() >= inputItems.length) continue;
            normalized.removeIf(existing -> existing.slot() == lock.slot());
            normalized.add(GhostItem.of(lock.slot(), lock.item().create(), 1));
        }
        inputLocks = List.copyOf(normalized);
        setChanged();
        if (level != null && !level.isClientSide())
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }

    public boolean supportsInputLocks() {
        return inputItems.length > 0 && inputItems.length <= 9 && !(this instanceof BatchFeederBlockEntity);
    }

    private SideConfiguration sideConfiguration = SideConfiguration.DEFAULT;

    public SideConfiguration getSideConfiguration() {
        return sideConfiguration;
    }

    public void setSideConfiguration(SideConfiguration settings) {
        sideConfiguration = settings;
        setChanged();
        if (level != null && !level.isClientSide()) {
            level.invalidateCapabilities(worldPosition);
            // Chamber capabilities forward the reactor's live settings.
            for (Direction side : Direction.values()) {
                BlockPos adjacent = worldPosition.relative(side);
                if (level.getBlockState(adjacent).getBlock() instanceof NuclearReactorChamberBlock) {
                    level.invalidateCapabilities(adjacent);
                }
            }
            electricNetworkUpdated(level, worldPosition);
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    public int supportedTransfers(Resource resource, Face face) {
        if (this instanceof ReactorSimulatorBlockEntity) return 0;
        if (resource == Resource.ITEMS) {
            if (this instanceof TeleporterBlockEntity) return 3;
            int mask = inputItems.length > 0 ? 1 : 0;
            for (int slot = 0; slot < getSlotCount(); slot++)
                if (isSlotExtractable(slot)) {
                    mask |= 2;
                    break;
                }
            return mask;
        }
        if (resource == Resource.FLUIDS) {
            if (this instanceof TeleporterBlockEntity) return 3;
            if (this instanceof PumpBlockEntity) return 2;
            return this instanceof GeothermalGeneratorBlockEntity ? 1 : 0;
        }
        // These machines have dedicated electrical ports with different meanings.
        if (this instanceof TransformerBlockEntity || this instanceof EnergyRectifierBlockEntity)
            return face == Face.FRONT ? 1 : 2;
        if (this instanceof BatteryBoxBlockEntity) return face == Face.FRONT ? 2 : 1;
        return (electricBlockInstance.maxEnergyInput.get() > 0 ? 1 : 0)
                | (this instanceof GeneratorBlockEntity && getMaxOutputEnergy() > 0 ? 2 : 0);
    }

    public boolean supportsResource(Resource resource) {
        for (Face face : Face.values()) if (supportedTransfers(resource, face) != 0) return true;
        return false;
    }

    public boolean supportsSideMode(Resource resource, Face face, Mode mode) {
        int mask = supportedTransfers(resource, face);
        return switch (mode) {
            case DEFAULT, DISABLED -> true;
            case INPUT -> (mask & 1) != 0;
            case OUTPUT -> (mask & 2) != 0;
            case BOTH -> mask == 3;
        };
    }

    public boolean allowsTransfer(Resource resource, @Nullable Direction side, boolean input) {
        if (isRemoved()) return false;
        if (side == null) {
            for (Direction direction : Direction.values()) if (allowsTransfer(resource, direction, input)) return true;
            return false;
        }
        Face face = Face.relative(getFacing(Direction.NORTH), side);
        Mode mode = sideConfiguration.mode(resource, face);
        // DEFAULT deliberately retains legacy FE capability behavior as well as native machine rules.
        return mode == Mode.DEFAULT
                || (mode.allows(input) && (supportedTransfers(resource, face) & (input ? 1 : 2)) != 0);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void setBlockState(BlockState state) {
        Direction previous = getFacing(Direction.NORTH);
        super.setBlockState(state);
        if (level != null && !level.isClientSide() && previous != getFacing(Direction.NORTH)) {
            level.invalidateCapabilities(worldPosition);
            electricNetworkUpdated(level, worldPosition);
        }
    }

    @Override
    public boolean isValidEnergyInputSide(Direction direction) {
        return allowsTransfer(Resource.ENERGY, direction, true);
    }

    public UUID placerId = Util.NIL_UUID;
    public String placerName = "";

    public ElectricBlockEntity(ElectricBlockInstance type, BlockPos pos, BlockState state) {
        super(type.blockEntity.get(), pos, state);
        electricBlockInstance = type;
        inputItems = new ItemStack[type.inputItemCount];
        outputItems = new ItemStack[type.outputItemCount];
        Arrays.fill(inputItems, ItemStack.EMPTY);
        Arrays.fill(outputItems, ItemStack.EMPTY);
        initProperties();
    }

    public void initProperties() {
        energyCapacity = electricBlockInstance.energyCapacity.get();
        maxInputEnergy = electricBlockInstance.maxEnergyInput.get();
        autoEject = false;
    }

    public void upgradesChanged() {}

    public int getSlotCount() {
        return inputItems.length + outputItems.length;
    }

    public ItemStack getStackInSlot(int slot) {
        if (slot < inputItems.length) {
            return inputItems[slot];
        }
        return outputItems[slot - inputItems.length];
    }

    public void setStackInSlot(int slot, ItemStack stack) {
        if (slot < inputItems.length) {
            inputItems[slot] = stack;
        } else {
            outputItems[slot - inputItems.length] = stack;
        }
    }

    public boolean isItemValid(int slot, ItemStack stack) {
        ItemStack lock = getInputLock(slot);
        return slot >= 0
                && slot < inputItems.length
                && (lock.isEmpty() || stack.isEmpty() || ItemStack.isSameItemSameComponents(lock, stack));
    }

    public boolean isSlotExtractable(int slot) {
        return slot >= inputItems.length && slot < getSlotCount();
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putDouble("Energy", energy);
        if (supportsInputLocks()) output.store("InputLocks", GhostItem.LIST_CODEC, inputLocks);
        output.store("SideConfiguration", SideConfiguration.CODEC, sideConfiguration);
        if (burnt) {
            output.putBoolean("Burnt", true);
        }
        if (!placerId.equals(Util.NIL_UUID)) {
            output.store("PlacerId", UUIDUtil.CODEC, placerId);
            output.putString("PlacerName", placerName);
        }
        if (getSlotCount() > 0) {
            ValueOutput.TypedOutputList<SlotStack> list = output.list("Inventory", SlotStack.CODEC);
            for (int slot = 0; slot < getSlotCount(); slot++) {
                ItemStack stack = getStackInSlot(slot);
                if (!stack.isEmpty()) {
                    list.add(new SlotStack(slot, stack));
                }
            }
        }
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        energy = input.getDoubleOr("Energy", 0D);
        sideConfiguration =
                input.read("SideConfiguration", SideConfiguration.CODEC).orElse(SideConfiguration.DEFAULT);
        burnt = input.getBooleanOr("Burnt", false);
        inputLocks = supportsInputLocks()
                ? input.read("InputLocks", GhostItem.LIST_CODEC).orElse(List.of())
                : List.of();
        boolean clientSync = isClientSync(input);
        if (!clientSync) {
            placerId = input.read("PlacerId", UUIDUtil.CODEC).orElse(Util.NIL_UUID);
            placerName = input.getStringOr("PlacerName", "");
        }
        if (!clientSync && getSlotCount() > 0) {
            Arrays.fill(inputItems, ItemStack.EMPTY);
            Arrays.fill(outputItems, ItemStack.EMPTY);
            input.listOrEmpty("Inventory", SlotStack.CODEC).forEach(e -> {
                if (e.slot >= 0 && e.slot < getSlotCount()) {
                    setStackInSlot(e.slot, e.stack);
                }
            });
        }
        initProperties();
        upgradesChanged();
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
        if (keepsEnergyWhenBroken() && energy > 0D) {
            components.set(ModDataComponents.ENERGY.get(), energy);
        }
    }

    @Override
    protected void applyImplicitComponents(DataComponentGetter components) {
        super.applyImplicitComponents(components);
        if (keepsEnergyWhenBroken()) {
            Double stored = components.get(ModDataComponents.ENERGY.get());
            if (stored != null) {
                energy = Math.clamp(stored, 0D, getEnergyCapacity());
            }
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public void removeComponentsFromTag(ValueOutput output) {
        super.removeComponentsFromTag(output);
        if (keepsEnergyWhenBroken()) {
            output.discard("Energy");
        }
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = saveCustomOnly(registries);
        for (String key : List.copyOf(tag.keySet())) {
            if (MENU_SYNCED_KEYS.contains(key) || key.startsWith("ChargeSlot")) {
                tag.remove(key);
            }
        }
        tag.putBoolean(CLIENT_SYNC, true);
        return tag;
    }

    protected static boolean isClientSync(ValueInput input) {
        return input.getBooleanOr(CLIENT_SYNC, false);
    }

    @Override
    public double getEnergyCapacity() {
        return energyCapacity;
    }

    @Override
    public double getEnergy() {
        return energy;
    }

    @Override
    public void setEnergyRaw(double e) {
        energy = e;
    }

    @Override
    public double getMaxInputEnergy() {
        return maxInputEnergy;
    }

    @Override
    public boolean canBurn() {
        return electricBlockInstance.canBurn;
    }

    @Override
    public void setBurnt(boolean b) {
        if (burnt != b && level != null && !level.isClientSide() && canBurn()) {
            burnt = b;
            setChanged();
            if (burnt) {
                level.levelEvent(1502, worldPosition, 0);
                if (electricBlockInstance.canBeActive) {
                    level.setBlock(worldPosition, getBlockState().setValue(ElectricBlock.ACTIVE, false), 3);
                }
            }
            electricNetworkUpdated(level, worldPosition);
        }
    }

    @Override
    public boolean isBurnt() {
        return burnt;
    }

    private int changeStateTicks = 0;

    public void tick() {
        if (level == null || level.isClientSide()) {
            return;
        }
        handleChanges();
    }

    protected void handleChanges() {
        if (changeStateTicks > 0) {
            changeStateTicks--;
            return;
        }
        if (!isBurnt()
                && electricBlockInstance.canBeActive
                && getBlockState().getBlock() instanceof ElectricBlock
                && getBlockState().getValue(ElectricBlock.ACTIVE) != active) {
            level.setBlock(worldPosition, getBlockState().setValue(ElectricBlock.ACTIVE, active), 3);
            setChanged();
        }
        active = false;
        changeStateTicks = FTBICConfig.MACHINES.STATE_UPDATE_TICKS.get();
    }

    public InteractionResult rightClick(Player player, InteractionHand hand, BlockHitResult hit) {
        if (level != null && !level.isClientSide() && player instanceof ServerPlayer sp) {
            openMenu(sp);
        }
        return InteractionResult.SUCCESS;
    }

    public void openMenu(ServerPlayer player) {
        player.openMenu(
                new SimpleMenuProvider(
                        (id, inv, p) -> createMenu(id, inv),
                        Component.translatable(getBlockState().getBlock().getDescriptionId())),
                buf -> buf.writeBlockPos(worldPosition));
    }

    public AbstractContainerMenu createMenu(int id, Inventory inv) {
        return new MachineMenu(id, inv, this);
    }

    public int getRedstoneOutputSignalEnergyStorage() {
        return energyCapacity <= 0D ? 0 : Math.round((float) ((energy / energyCapacity) * 15));
    }

    public void onBroken(Level level, BlockPos pos) {
        for (ItemStack stack : inputItems) {
            Block.popResource(level, pos, stack);
        }
        for (ItemStack stack : outputItems) {
            Block.popResource(level, pos, stack);
        }
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        super.preRemoveSideEffects(pos, state);
        if (level != null) {
            onBroken(level, pos);
        }
    }

    public void onPlacedBy(@Nullable LivingEntity entity, ItemStack stack) {
        if (savePlacer() && entity != null) {
            placerId = entity.getUUID();
            placerName = entity.getScoreboardName();
        }
    }

    public boolean savePlacer() {
        return false;
    }

    public boolean keepsEnergyWhenBroken() {
        return false;
    }

    public void neighborChanged(BlockPos neighborPos, Block neighborBlock) {}

    private long nextChargeChimeTick;

    protected void playChargeCompleteSound() {
        if (level != null && !level.isClientSide() && level.getGameTime() >= nextChargeChimeTick) {
            level.playSound(null, worldPosition, FTBICSounds.CHARGE_COMPLETE.get(), SoundSource.BLOCKS, 0.5F, 1F);
            nextChargeChimeTick = level.getGameTime() + 40;
        }
    }

    public void stepOn(ServerPlayer player) {}

    public void spawnActiveParticles(Level level, double x, double y, double z, BlockState state, RandomSource r) {}

    public Direction getFacing(Direction def) {
        if (electricBlockInstance.facingProperty == null) {
            return def;
        }
        BlockState state = getBlockState();
        if (state.getBlock() instanceof ElectricBlock) {
            return state.getValue(electricBlockInstance.facingProperty);
        }
        return def;
    }

    public static <T extends BlockEntity> void ticker(Level level, BlockPos pos, BlockState state, T entity) {
        ((ElectricBlockEntity) entity).tick();
    }

    public record SlotStack(int slot, ItemStack stack) {
        public static final Codec<SlotStack> CODEC = RecordCodecBuilder.create(i -> i.group(
                        Codec.INT.fieldOf("Slot").forGetter(SlotStack::slot),
                        ItemStack.CODEC.fieldOf("Item").forGetter(SlotStack::stack))
                .apply(i, SlotStack::new));
    }
}
