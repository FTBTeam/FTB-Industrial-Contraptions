package dev.ftb.mods.ftbic.block.entity.generator;

import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.block.FTBICBlocks;
import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.block.NuclearReactorChamberBlock;
import dev.ftb.mods.ftbic.item.reactor.NuclearReactor;
import dev.ftb.mods.ftbic.item.reactor.ReactorItem;
import dev.ftb.mods.ftbic.screen.NuclearReactorMenu;
import dev.ftb.mods.ftbic.screen.sync.SyncedData;
import dev.ftb.mods.ftbic.screen.sync.SyncedDataKey;
import dev.ftb.mods.ftbic.sound.FTBICSounds;
import dev.ftb.mods.ftbic.util.FTBICUtils;
import dev.ftb.mods.ftbic.util.NuclearExplosion;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectMap;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class NuclearReactorBlockEntity extends GeneratorBlockEntity {
	public static final int[] OFFSET_X = {0, 0, -1, 1};
	public static final int[] OFFSET_Y = {-1, 1, 0, 0};

	public static final SyncedDataKey<Double> ENERGY_OUTPUT = new SyncedDataKey<>("energy_output", 0D);
	public static final SyncedDataKey<Integer> HEAT = new SyncedDataKey<>("heat", 0);
	public static final SyncedDataKey<Integer> MAX_HEAT = new SyncedDataKey<>("max_heat", 0);

	public int timeUntilNextCycle;
	public final NuclearReactor reactor;
	public int debugSpeed = 0;
	public Byte2ObjectOpenHashMap<Item> plan;

	public NuclearReactorBlockEntity(BlockPos pos, BlockState state) {
		super(FTBICElectricBlocks.NUCLEAR_REACTOR, pos, state);
		reactor = new NuclearReactor(inputItems);
	}

	@Override
	public void initProperties() {
		super.initProperties();
		maxEnergyOutputTransfer = FTBICConfig.ENERGY.IV_TRANSFER_RATE.get();
	}

	@Override
	public void writeData(CompoundTag tag) {
		super.writeData(tag);
		tag.putInt("TimeUntilNextCycle", timeUntilNextCycle);
		tag.putBoolean("Paused", reactor.paused);
		tag.putBoolean("AllowRedstoneControl", reactor.allowRedstoneControl);
		tag.putDouble("EnergyOutput", reactor.energyOutput);
		tag.putInt("Heat", reactor.heat);

		if (debugSpeed > 0) {
			tag.putInt("DebugSpeed", debugSpeed);
		}

		if (plan != null && !plan.isEmpty()) {
			HashMap<Item, ByteArrayList> pmap = new HashMap<>();

			for (Byte2ObjectMap.Entry<Item> entry : plan.byte2ObjectEntrySet()) {
				pmap.computeIfAbsent(entry.getValue(), k -> new ByteArrayList()).add(entry.getByteKey());
			}

			CompoundTag ptag = new CompoundTag();

			for (Map.Entry<Item, ByteArrayList> entry : pmap.entrySet()) {
				ptag.putByteArray(Registry.ITEM.getKey(entry.getKey()).toString(), entry.getValue().toByteArray());
			}

			tag.put("Plan", ptag);
		}
	}

	@Override
	public void readData(CompoundTag tag) {
		super.readData(tag);
		timeUntilNextCycle = tag.getInt("TimeUntilNextCycle");
		reactor.paused = tag.getBoolean("Paused");
		reactor.allowRedstoneControl = tag.getBoolean("AllowRedstoneControl");
		reactor.energyOutput = tag.getDouble("EnergyOutput");
		reactor.heat = tag.getInt("Heat");
		debugSpeed = tag.getInt("DebugSpeed");

		CompoundTag ptag = tag.getCompound("Plan");

		if (!ptag.isEmpty()) {
			plan = new Byte2ObjectOpenHashMap<>();

			for (String s : ptag.getAllKeys()) {
				Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(s));

				if (item instanceof ReactorItem) {
					for (byte b : ptag.getByteArray(s)) {
						plan.put(b, item);
					}
				}
			}

			if (plan.isEmpty()) {
				plan = null;
			}
		} else {
			plan = null;
		}
	}

	@Override
	public int getSlotLimit(int slot) {
		return 1;
	}

	@Override
	public boolean isItemValid(int slot, @NotNull ItemStack stack) {
		return stack.getItem() instanceof ReactorItem && (plan == null || plan.get((byte) slot) == stack.getItem());
	}

	@Override
	public boolean savePlacer() {
		return true;
	}

	@Override
	public void addSyncData(SyncedData data) {
		super.addSyncData(data);
		data.addBoolean(SyncedData.PAUSED, () -> reactor.paused);
		data.addBoolean(SyncedData.ALLOW_REDSTONE_CONTROL, () -> reactor.allowRedstoneControl);
		data.addDouble(ENERGY_OUTPUT, () -> reactor.energyOutput);
		data.addInt(HEAT, () -> reactor.heat);
		data.addInt(MAX_HEAT, () -> reactor.maxHeat);
	}

	@Override
	public InteractionResult rightClick(Player player, InteractionHand hand, BlockHitResult hit) {
		if (!level.isClientSide()) {
			openMenu((ServerPlayer) player, (id, inventory) -> new NuclearReactorMenu(id, inventory, this));
		}

		return InteractionResult.SUCCESS;
	}

	private void checkPoweredState(Level level, BlockPos pos, BlockState state) {
		if (reactor.allowRedstoneControl) {
			reactor.paused = !level.hasNeighborSignal(pos);
		}
	}

	@Override
	public void handleGeneration() {
		timeUntilNextCycle--;

		if (timeUntilNextCycle <= 0) {
			timeUntilNextCycle = 20;

			if (debugSpeed <= 0) {
				handleReactor();
			}
		}

		if (debugSpeed > 0) {
			for (int i = 0; i < debugSpeed; i++) {
				handleReactor();
			}
		}

		if (reactor.energyOutput > 0) {
			active = true;
			energy += Math.min(reactor.energyOutput, energyCapacity - energy);
		}

		if (level != null) {
			checkPoweredState(level, worldPosition, getBlockState());
		}

		if (level == null || level.isClientSide() || reactor.heat <= 0 || reactor.maxHeat <= 0) {
			return;
		}

		float h = reactor.heat / (float) reactor.maxHeat;

		if (h >= 1F) {
			if (debugSpeed > 0) {
				level.getServer().getPlayerList().broadcastSystemMessage(Component.literal(String.format("Debug Nuclear Reactor at %d, %d, %d exploded:", worldPosition.getX(), worldPosition.getY(), worldPosition.getZ())), ChatType.SYSTEM);
				level.getServer().getPlayerList().broadcastSystemMessage(Component.literal(String.format("- Radius: %,d", Mth.ceil(reactor.explosionRadius))), ChatType.SYSTEM);
				level.getServer().getPlayerList().broadcastSystemMessage(Component.literal(String.format("- Heat: %s / %s \uD83D\uDD25", FTBICUtils.formatEnergyValue(reactor.heat), FTBICUtils.formatEnergyValue(reactor.maxHeat))), ChatType.SYSTEM);
				level.getServer().getPlayerList().broadcastSystemMessage(Component.literal(String.format("- Energy: %s/t", FTBICUtils.formatEnergyValue(reactor.energyOutput))), ChatType.SYSTEM);

				reactor.paused = true;
				reactor.heat = reactor.maxHeat - 1;
				setChanged();
			} else {
				Arrays.fill(inputItems, ItemStack.EMPTY);
				setChanged();

				level.setBlock(worldPosition, FTBICBlocks.ACTIVE_NUKE.get().defaultBlockState(), 3);

				for (Direction direction : FTBICUtils.DIRECTIONS) {
					if (level.getBlockState(worldPosition.relative(direction)).getBlock() instanceof NuclearReactorChamberBlock) {
						level.setBlock(worldPosition.relative(direction), FTBICBlocks.ACTIVE_NUKE.get().defaultBlockState(), 3);
					}
				}

				NuclearExplosion.builder((ServerLevel) level, worldPosition, reactor.explosionRadius, placerId, placerName)
						.preExplosion(() -> {
							level.getServer().getPlayerList().broadcastSystemMessage(Component.translatable("block.ftbic.nuclear_reactor.broadcast", placerName), ChatType.SYSTEM);

							Player player = level.getServer().getPlayerList().getPlayer(placerId);

							if (player != null) {
								player.sendSystemMessage(Component.literal(String.format("%s : [%d, %d, %d]", level.dimension().location(), worldPosition.getX(), worldPosition.getY(), worldPosition.getZ())).withStyle(ChatFormatting.GRAY));
							}

							level.removeBlock(worldPosition, false);
						})
						.create()
				;
			}
		} else if (h >= 0.75F) {
			if (level.getGameTime() % 25L == 0L && reactor.energyOutput > 0D) {
				level.playSound(null, worldPosition, FTBICSounds.RADIATION.get(), SoundSource.BLOCKS, 0.5F, 1F);
			}
		}
	}

	public void handleReactor() {
		if (level == null || level.isClientSide()) {
			return;
		}

		double peo = reactor.energyOutput;
		int ph = reactor.heat;

		reactor.tick();

		if (peo != reactor.energyOutput || ph != reactor.heat) {
			setChanged();
		}
	}

	@Override
	public void onBroken(Level level, BlockPos pos) {
		if (debugSpeed <= 0) {
			super.onBroken(level, pos);
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void spawnActiveParticles(Level level, double x, double y, double z, BlockState state, RandomSource r) {
	}
}
