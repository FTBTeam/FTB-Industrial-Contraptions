package dev.ftb.mods.ftbic.block.entity;

import com.mojang.serialization.Codec;
import dev.ftb.mods.ftbic.block.ElectricBlock;
import dev.ftb.mods.ftbic.block.ElectricBlockInstance;
import dev.ftb.mods.ftbic.util.EnergyHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Util;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
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
import java.util.concurrent.atomic.AtomicLong;

/**
 * Base electric block entity. Carries energy / inputs / outputs / burnt state / placer UUID; ticks
 * subclass behaviour; saves/loads via {@code ValueInput}/{@code ValueOutput}; advertises itself as
 * an {@link EnergyHandler} to the FTBIC electric-network resolver.
 *
 * Foreign interop is attached externally via {@code CapabilityRegistrar} on the block-entity type
 * (item handler + energy handler + per-BE fluid handler). Subclasses override {@code initProperties}
 * to publish their final energy-capacity / input / output / usage numbers (which may be boosted by
 * upgrade slots — see {@link dev.ftb.mods.ftbic.block.entity.machine.BasicMachineBlockEntity#upgradesChanged}).
 */
public class ElectricBlockEntity extends BlockEntity implements EnergyHandler {
	private static final AtomicLong ELECTRIC_NETWORK_CHANGES = new AtomicLong(0L);

	public static void electricNetworkUpdated(LevelAccessor level, BlockPos pos) {
		ELECTRIC_NETWORK_CHANGES.incrementAndGet();
	}

	public static long getCurrentElectricNetwork(LevelAccessor level, BlockPos pos) {
		return ELECTRIC_NETWORK_CHANGES.get();
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

	/** Hook for subclasses (see BasicMachineBlockEntity) to recompute properties from upgrade slots. */
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

	// --- EnergyHandler ---

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

	// --- Tick + interaction hooks ---

	/** Ticks between active-state refreshes. Matches 1.18.2's STATE_UPDATE_TICKS throttling. */
	private int changeStateTicks = 0;

	public void tick() {
		if (level == null || level.isClientSide()) {
			return;
		}
		handleChanges();
	}

	/**
	 * Flushes the `active` flag to the block state every STATE_UPDATE_TICKS ticks. Subclass ticks
	 * set `active = true` whenever they do meaningful work; this method pushes that back to the
	 * block state and then clears the flag so the next window starts fresh.
	 */
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
		// Reset the flag; subclass ticks set it back to true each frame they do work.
		active = false;
		changeStateTicks = dev.ftb.mods.ftbic.FTBICConfig.MACHINES.STATE_UPDATE_TICKS.get();
	}

	public InteractionResult rightClick(Player player, InteractionHand hand, BlockHitResult hit) {
		if (level != null && !level.isClientSide() && player instanceof net.minecraft.server.level.ServerPlayer sp) {
			openMenu(sp);
		}
		return InteractionResult.SUCCESS;
	}

	/**
	 * Opens this BE's menu for the given player. Subclasses override {@link #createMenu(int, Inventory)}
	 * to provide their specific menu type; the default here builds a generic `MachineMenu`.
	 */
	public void openMenu(net.minecraft.server.level.ServerPlayer player) {
		player.openMenu(new net.minecraft.world.SimpleMenuProvider(
				(id, inv, p) -> createMenu(id, inv),
				net.minecraft.network.chat.Component.translatable(getBlockState().getBlock().getDescriptionId())
		), buf -> buf.writeBlockPos(worldPosition));
	}

	/** Subclass hook: returns the specific menu type for this BE (SolarPanelMenu, BasicGeneratorMenu, etc.). */
	public net.minecraft.world.inventory.AbstractContainerMenu createMenu(int id, net.minecraft.world.entity.player.Inventory inv) {
		return new dev.ftb.mods.ftbic.screen.MachineMenu(id, inv, this);
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

	public void stepOn(net.minecraft.server.level.ServerPlayer player) {
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

	/** Stored inventory entry: slot index + stack. */
	public record SlotStack(int slot, ItemStack stack) {
		public static final Codec<SlotStack> CODEC = com.mojang.serialization.codecs.RecordCodecBuilder.create(i -> i.group(
				Codec.INT.fieldOf("Slot").forGetter(SlotStack::slot),
				ItemStack.CODEC.fieldOf("Item").forGetter(SlotStack::stack)
		).apply(i, SlotStack::new));
	}
}
