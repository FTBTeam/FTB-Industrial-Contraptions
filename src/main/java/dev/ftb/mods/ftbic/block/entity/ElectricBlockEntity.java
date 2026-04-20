package dev.ftb.mods.ftbic.block.entity;

import com.mojang.serialization.Codec;
import dev.ftb.mods.ftbic.block.ElectricBlock;
import dev.ftb.mods.ftbic.block.ElectricBlockInstance;
import dev.ftb.mods.ftbic.screen.MachineMenu;
import dev.ftb.mods.ftbic.util.EnergyHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
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

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class ElectricBlockEntity extends BlockEntity implements EnergyHandler {
	private static final ConcurrentHashMap<ResourceKey<Level>, AtomicLong> ELECTRIC_NETWORK_CHANGES = new ConcurrentHashMap<>();

	public static void electricNetworkUpdated(LevelAccessor level, BlockPos pos) {
		if (level instanceof Level l) {
			ELECTRIC_NETWORK_CHANGES.computeIfAbsent(l.dimension(), k -> new AtomicLong()).incrementAndGet();
		}
	}

	public static long getCurrentElectricNetwork(LevelAccessor level, BlockPos pos) {
		if (level instanceof Level l) {
			AtomicLong counter = ELECTRIC_NETWORK_CHANGES.get(l.dimension());
			return counter == null ? 0L : counter.get();
		}
		return 0L;
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

	public void upgradesChanged() {
	}

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
		return slot >= 0 && slot < inputItems.length;
	}

	public boolean isSlotExtractable(int slot) {
		return slot >= inputItems.length && slot < getSlotCount();
	}

	@Override
	protected void saveAdditional(ValueOutput output) {
		super.saveAdditional(output);
		output.putDouble("Energy", energy);
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
		burnt = input.getBooleanOr("Burnt", false);
		placerId = input.read("PlacerId", UUIDUtil.CODEC).orElse(Util.NIL_UUID);
		placerName = input.getStringOr("PlacerName", "");
		if (getSlotCount() > 0) {
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
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
		return saveCustomOnly(registries);
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
		changeStateTicks = dev.ftb.mods.ftbic.FTBICConfig.MACHINES.STATE_UPDATE_TICKS.get();
	}

	public InteractionResult rightClick(Player player, InteractionHand hand, BlockHitResult hit) {
		if (level != null && !level.isClientSide() && player instanceof ServerPlayer sp) {
			openMenu(sp);
		}
		return InteractionResult.SUCCESS;
	}

	public void openMenu(ServerPlayer player) {
		player.openMenu(new SimpleMenuProvider(
				(id, inv, p) -> createMenu(id, inv),
				Component.translatable(getBlockState().getBlock().getDescriptionId())
		), buf -> buf.writeBlockPos(worldPosition));
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

	public void onPlacedBy(@Nullable LivingEntity entity, ItemStack stack) {
		if (savePlacer() && entity != null) {
			placerId = entity.getUUID();
			placerName = entity.getScoreboardName();
		}
	}

	public boolean savePlacer() {
		return false;
	}

	public void neighborChanged(BlockPos neighborPos, Block neighborBlock) {
		if (level != null && !level.getBlockState(neighborPos).is(neighborBlock)) {
			electricNetworkUpdated(level, neighborPos);
		}
	}

	public void stepOn(ServerPlayer player) {
	}

	public void spawnActiveParticles(Level level, double x, double y, double z, BlockState state, RandomSource r) {
	}

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
		public static final Codec<SlotStack> CODEC = com.mojang.serialization.codecs.RecordCodecBuilder.create(i -> i.group(
				Codec.INT.fieldOf("Slot").forGetter(SlotStack::slot),
				ItemStack.CODEC.fieldOf("Item").forGetter(SlotStack::stack)
		).apply(i, SlotStack::new));
	}
}
