package dev.ftb.mods.ftbic.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.block.ElectricBlockInstance;
import dev.ftb.mods.ftbic.block.FTBICBlocks;
import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.block.entity.generator.NuclearReactorBlockEntity;
import dev.ftb.mods.ftbic.item.FTBICItems;
import dev.ftb.mods.ftbic.item.MaterialItem;
import dev.ftb.mods.ftbic.item.reactor.NuclearReactor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@EventBusSubscriber(modid = FTBIC.MOD_ID)
public final class FTBICCommands {
	private static final int PLATFORM_HALF = 25;
	private static final int PLATFORM_THICKNESS = 2;
	private static final int BLOCK_SPACING = 3;

	@SubscribeEvent
	public static void register(RegisterCommandsEvent event) {
		LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal("ftbic")
				.requires(s -> Commands.LEVEL_GAMEMASTERS.check(s.permissions()))
				.then(Commands.literal("showcase").executes(FTBICCommands::runShowcase));
		event.getDispatcher().register(root);
	}

	private static int runShowcase(com.mojang.brigadier.context.CommandContext<CommandSourceStack> ctx) {
		CommandSourceStack src = ctx.getSource();
		if (!(src.getEntity() instanceof ServerPlayer player)) {
			src.sendFailure(Component.literal("Must be run by a player."));
			return 0;
		}
		ServerLevel level = player.level();
		BlockPos centre = player.blockPosition();

		Block reinforced = FTBICBlocks.REINFORCED_STONE.get();
		BlockState rs = reinforced.defaultBlockState();

		int y0 = centre.getY() - 1;
		int y1 = y0 - (PLATFORM_THICKNESS - 1);
		int placedFloor = 0;
		for (int dx = -PLATFORM_HALF; dx < PLATFORM_HALF; dx++) {
			for (int dz = -PLATFORM_HALF; dz < PLATFORM_HALF; dz++) {
				for (int y = y1; y <= y0; y++) {
					level.setBlock(centre.offset(dx, y - centre.getY(), dz), rs, Block.UPDATE_CLIENTS | Block.UPDATE_NEIGHBORS);
					placedFloor++;
				}
			}
		}

		List<Block> blocks = collectShowcaseBlocks();

		int yPlace = y0 + 1;
		int row = 0, col = 0;
		int span = (PLATFORM_HALF * 2) / BLOCK_SPACING;
		int placedBlocks = 0;
		for (Block b : blocks) {
			int dx = -PLATFORM_HALF + 2 + col * BLOCK_SPACING;
			int dz = -PLATFORM_HALF + 2 + row * BLOCK_SPACING;
			BlockPos pos = centre.offset(dx, yPlace - centre.getY(), dz);
			level.setBlock(pos, b.defaultBlockState(), Block.UPDATE_CLIENTS | Block.UPDATE_NEIGHBORS);
			placedBlocks++;
			col++;
			if (col >= span) { col = 0; row++; }
		}

		List<ItemStack> items = collectShowcaseItems();
		int frameWallStartX = centre.getX() + PLATFORM_HALF + 1;
		int frameWallY = y0 + 2;
		int frameWallZ0 = centre.getZ() - PLATFORM_HALF;
		int frameWallWidth = PLATFORM_HALF * 2;
		int placedFrames = 0;
		for (int idx = 0; idx < items.size(); idx++) {
			int col2 = idx % frameWallWidth;
			int rowY = idx / frameWallWidth;
			BlockPos backingPos = new BlockPos(frameWallStartX, frameWallY + rowY, frameWallZ0 + col2);
			level.setBlock(backingPos, rs, Block.UPDATE_CLIENTS | Block.UPDATE_NEIGHBORS);
			BlockPos framePos = backingPos.relative(Direction.WEST);
			ItemFrame frame = new ItemFrame(level, framePos, Direction.WEST);
			frame.setItem(items.get(idx), false);
			level.addFreshEntity(frame);
			placedFrames++;
		}

		boolean reactorBuilt = buildTestReactor(level, centre, y0);

		String summary = String.format(
				"FTBIC showcase built: %d floor blocks, %d sample blocks, %d items in frames%s",
				placedFloor, placedBlocks, placedFrames,
				reactorBuilt ? ", test reactor + 6 chambers online" : "");
		src.sendSuccess(() -> Component.literal(summary), true);
		return 1;
	}

	/**
	 * Places a nuclear reactor floating above the far corner of the platform, attaches 6 chambers
	 * (one per face) to unlock the full 9×3 = 27-slot grid, populates every slot with a stable
	 * 3-single-rod layout covering every reactor-component item, and starts the reactor.
	 * Dual/quad rods are displayed in adjacent item frames since they exceed the cooling budget of
	 * this minimal test setup.
	 */
	private static boolean buildTestReactor(ServerLevel level, BlockPos centre, int floorTopY) {
		BlockPos reactorPos = new BlockPos(centre.getX() + PLATFORM_HALF - 4, floorTopY + 2, centre.getZ() + PLATFORM_HALF - 4);

		// Clear a 5×5×5 working volume centered on the reactor.
		BlockState air = Blocks.AIR.defaultBlockState();
		for (int dx = -2; dx <= 2; dx++) {
			for (int dz = -2; dz <= 2; dz++) {
				for (int dy = -2; dy <= 2; dy++) {
					level.setBlock(reactorPos.offset(dx, dy, dz), air, Block.UPDATE_CLIENTS);
				}
			}
		}

		// Place the reactor core first, then the 6 chambers. setBlock with UPDATE_NEIGHBORS will
		// fire neighborChanged on the reactor each time, so its pendingChamberRecompute flag is
		// already set by the time we populate slots.
		BlockState reactorState = FTBICElectricBlocks.NUCLEAR_REACTOR.block.get().defaultBlockState();
		level.setBlock(reactorPos, reactorState, Block.UPDATE_CLIENTS | Block.UPDATE_NEIGHBORS);

		BlockState chamberState = FTBICBlocks.NUCLEAR_REACTOR_CHAMBER.get().defaultBlockState();
		for (Direction dir : Direction.values()) {
			level.setBlock(reactorPos.relative(dir), chamberState, Block.UPDATE_CLIENTS | Block.UPDATE_NEIGHBORS);
		}

		BlockEntity be = level.getBlockEntity(reactorPos);
		if (!(be instanceof NuclearReactorBlockEntity reactor)) return false;
		reactor.recomputeActiveColumns();
		populateReactor(reactor);
		reactor.reactor.paused = false;
		reactor.reactor.allowRedstoneControl = false;
		reactor.setChanged();
		level.sendBlockUpdated(reactorPos, reactorState, reactorState, 3);

		// Dual and quad rods on display shelves next to the reactor — they need heavier cooling
		// than this 3-single-rod test build provides, so we frame them instead of inserting.
		BlockPos shelfBase = reactorPos.offset(0, 4, 0);
		level.setBlock(shelfBase, FTBICBlocks.REINFORCED_STONE.get().defaultBlockState(),
				Block.UPDATE_CLIENTS | Block.UPDATE_NEIGHBORS);
		level.setBlock(shelfBase.east(), FTBICBlocks.REINFORCED_STONE.get().defaultBlockState(),
				Block.UPDATE_CLIENTS | Block.UPDATE_NEIGHBORS);
		spawnFrame(level, shelfBase, Direction.UP,
				new ItemStack(FTBICItems.DUAL_URANIUM_FUEL_ROD.get()));
		spawnFrame(level, shelfBase.east(), Direction.UP,
				new ItemStack(FTBICItems.QUAD_URANIUM_FUEL_ROD.get()));

		return true;
	}

	private static void spawnFrame(ServerLevel level, BlockPos backingPos, Direction facing, ItemStack item) {
		BlockPos framePos = backingPos.relative(facing);
		ItemFrame frame = new ItemFrame(level, framePos, facing);
		frame.setItem(item, false);
		level.addFreshEntity(frame);
	}

	/**
	 * Stable 3-single-rod layout covering every in-grid reactor component. Rod at (1,1) has 1
	 * iridium reflector for a 2-pulse boost; rods at (4,1) and (7,1) run unboosted with fully
	 * acceptor-tiled neighbors so all rod heat is absorbed by surrounding coolant/vent/exchanger
	 * items — reactor-pool heat stays near zero.
	 */
	private static void populateReactor(NuclearReactorBlockEntity reactor) {
		ItemStack[] grid = reactor.reactor.inputItems;
		Arrays.fill(grid, ItemStack.EMPTY);

		Item p = FTBICItems.REACTOR_PLATING.get();
		Item P = FTBICItems.CONTAINMENT_REACTOR_PLATING.get();
		Item H = FTBICItems.HEAT_CAPACITY_REACTOR_PLATING.get();
		Item nRef = FTBICItems.NEUTRON_REFLECTOR.get();
		Item tRef = FTBICItems.THICK_NEUTRON_REFLECTOR.get();
		Item iRef = FTBICItems.IRIDIUM_NEUTRON_REFLECTOR.get();
		Item cSm = FTBICItems.SMALL_COOLANT_CELL.get();
		Item cMd = FTBICItems.MEDIUM_COOLANT_CELL.get();
		Item cLg = FTBICItems.LARGE_COOLANT_CELL.get();
		Item hVent = FTBICItems.HEAT_VENT.get();
		Item aVent = FTBICItems.ADVANCED_HEAT_VENT.get();
		Item rVent = FTBICItems.REACTOR_HEAT_VENT.get();
		Item oVent = FTBICItems.OVERCLOCKED_HEAT_VENT.get();
		Item cVent = FTBICItems.COMPONENT_HEAT_VENT.get();
		Item heX = FTBICItems.HEAT_EXCHANGER.get();
		Item eXAdv = FTBICItems.ADVANCED_HEAT_EXCHANGER.get();
		Item eXRec = FTBICItems.REACTOR_HEAT_EXCHANGER.get();
		Item eXCmp = FTBICItems.COMPONENT_HEAT_EXCHANGER.get();
		Item rod = FTBICItems.URANIUM_FUEL_ROD.get();

		//         col 0   1   2   3   4   5   6   7   8
		put(grid, 0, 0, p);   put(grid, 1, 0, hVent);put(grid, 2, 0, P);
		put(grid, 3, 0, nRef);put(grid, 4, 0, aVent);put(grid, 5, 0, tRef);
		put(grid, 6, 0, H);   put(grid, 7, 0, eXRec);put(grid, 8, 0, H);

		put(grid, 0, 1, iRef);put(grid, 1, 1, rod);  put(grid, 2, 1, cSm);
		put(grid, 3, 1, heX); put(grid, 4, 1, rod);  put(grid, 5, 1, cLg);
		put(grid, 6, 1, rVent);put(grid, 7, 1, rod); put(grid, 8, 1, eXCmp);

		put(grid, 0, 2, P);   put(grid, 1, 2, cMd); put(grid, 2, 2, p);
		put(grid, 3, 2, tRef);put(grid, 4, 2, oVent);put(grid, 5, 2, nRef);
		put(grid, 6, 2, H);   put(grid, 7, 2, cVent);put(grid, 8, 2, eXAdv);
	}

	private static void put(ItemStack[] grid, int col, int row, Item item) {
		grid[NuclearReactor.slotIndex(col, row)] = new ItemStack(item);
	}

	private static List<Block> collectShowcaseBlocks() {
		Set<Block> seen = new HashSet<>();
		List<Block> out = new ArrayList<>();
		Block[] statics = {
				FTBICBlocks.RUBBER_SHEET.get(), FTBICBlocks.REINFORCED_STONE.get(),
				FTBICBlocks.REINFORCED_GLASS.get(), FTBICBlocks.MACHINE_BLOCK.get(),
				FTBICBlocks.ADVANCED_MACHINE_BLOCK.get(), FTBICBlocks.IRON_FURNACE.get(),
				FTBICBlocks.LV_CABLE.get(), FTBICBlocks.MV_CABLE.get(),
				FTBICBlocks.HV_CABLE.get(), FTBICBlocks.EV_CABLE.get(),
				FTBICBlocks.IV_CABLE.get(), FTBICBlocks.BURNT_CABLE.get(),
				FTBICBlocks.LANDMARK.get(), FTBICBlocks.NUCLEAR_REACTOR_CHAMBER.get(),
				FTBICBlocks.NUKE.get(),
		};
		for (Block b : statics) if (seen.add(b)) out.add(b);
		for (ElectricBlockInstance ebi : FTBICElectricBlocks.ALL) {
			Block b = ebi.block.get();
			if (seen.add(b)) out.add(b);
		}
		return out;
	}

	private static List<ItemStack> collectShowcaseItems() {
		Set<Item> seen = new HashSet<>();
		List<ItemStack> out = new ArrayList<>();
		for (MaterialItem m : FTBICItems.MATERIALS) {
			if (m.item == null) continue;
			Item i = m.item.get();
			if (i != null && seen.add(i)) out.add(new ItemStack(i));
		}
		for (Item item : net.minecraft.core.registries.BuiltInRegistries.ITEM) {
			net.minecraft.resources.Identifier id = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(item);
			if (id != null && FTBIC.MOD_ID.equals(id.getNamespace()) && seen.add(item)) {
				out.add(new ItemStack(item));
			}
		}
		return out;
	}

	private FTBICCommands() {}
}
