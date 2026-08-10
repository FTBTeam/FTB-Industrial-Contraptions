package dev.ftb.mods.industrialcontraptions.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.ftb.mods.industrialcontraptions.IC;
import dev.ftb.mods.industrialcontraptions.block.ElectricBlockInstance;
import dev.ftb.mods.industrialcontraptions.block.ICBlocks;
import dev.ftb.mods.industrialcontraptions.block.ICElectricBlocks;
import dev.ftb.mods.industrialcontraptions.block.entity.generator.NuclearReactorBlockEntity;
import dev.ftb.mods.industrialcontraptions.item.ICItems;
import dev.ftb.mods.industrialcontraptions.item.MaterialItem;
import dev.ftb.mods.industrialcontraptions.item.reactor.NuclearReactor;
import dev.ftb.mods.industrialcontraptions.registry.LegacyRegistryAliases;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
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
import java.util.function.Consumer;

@EventBusSubscriber(modid = IC.MOD_ID)
public final class ICCommands {
	private static final int PLATFORM_HALF = 25;
	private static final int PLATFORM_THICKNESS = 2;
	private static final int BLOCK_SPACING = 3;

	@SubscribeEvent
	public static void register(RegisterCommandsEvent event) {
		LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal("ic")
				.requires(s -> Commands.LEVEL_GAMEMASTERS.check(s.permissions()))
				.then(Commands.literal("showcase").executes(ICCommands::runShowcase));
		LiteralCommandNode<CommandSourceStack> node = event.getDispatcher().register(root);
		event.getDispatcher().register(Commands.literal(LegacyRegistryAliases.LEGACY_MOD_ID)
				.requires(s -> Commands.LEVEL_GAMEMASTERS.check(s.permissions()))
				.redirect(node));
	}

	private static int runShowcase(CommandContext<CommandSourceStack> ctx) {
		CommandSourceStack src = ctx.getSource();
		if (!(src.getEntity() instanceof ServerPlayer player)) {
			src.sendFailure(Component.literal("Must be run by a player."));
			return 0;
		}
		ServerLevel level = player.level();
		BlockPos centre = player.blockPosition();

		Block reinforced = ICBlocks.REINFORCED_STONE.get();
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

		boolean demoBuilt = buildTestReactor(level, centre, y0);
		BlockPos optimalPos = new BlockPos(centre.getX() + PLATFORM_HALF - 4 - 5, y0 + 2, centre.getZ() + PLATFORM_HALF - 4);
		boolean optimalBuilt = buildReactor(level, optimalPos, ICCommands::populateQuadRodOptimalReactor, true);

		String summary = String.format(
				"Industrial Contraptions showcase built: %d floor blocks, %d sample blocks, %d items in frames%s%s",
				placedFloor, placedBlocks, placedFrames,
				demoBuilt ? ", demo reactor online" : "",
				optimalBuilt ? ", optimal quad-rod reactor online" : "");
		src.sendSuccess(() -> Component.literal(summary), true);
		return 1;
	}

	private static boolean buildTestReactor(ServerLevel level, BlockPos centre, int floorTopY) {
		BlockPos reactorPos = new BlockPos(centre.getX() + PLATFORM_HALF - 4, floorTopY + 2, centre.getZ() + PLATFORM_HALF - 4);
		if (!buildReactor(level, reactorPos, ICCommands::populateReactor, false)) return false;

		// Dual and quad rods on a display shelf — this demo only drives single rods.
		BlockPos shelfBase = reactorPos.offset(0, 4, 0);
		level.setBlock(shelfBase, ICBlocks.REINFORCED_STONE.get().defaultBlockState(),
				Block.UPDATE_CLIENTS | Block.UPDATE_NEIGHBORS);
		level.setBlock(shelfBase.east(), ICBlocks.REINFORCED_STONE.get().defaultBlockState(),
				Block.UPDATE_CLIENTS | Block.UPDATE_NEIGHBORS);
		spawnFrame(level, shelfBase, Direction.UP, new ItemStack(ICItems.DUAL_URANIUM_FUEL_ROD.get()));
		spawnFrame(level, shelfBase.east(), Direction.UP, new ItemStack(ICItems.QUAD_URANIUM_FUEL_ROD.get()));
		return true;
	}

	private static boolean buildReactor(ServerLevel level, BlockPos reactorPos,
			Consumer<NuclearReactorBlockEntity> populator, boolean allowRedstone) {
		BlockState air = Blocks.AIR.defaultBlockState();
		for (int dx = -2; dx <= 2; dx++) {
			for (int dz = -2; dz <= 2; dz++) {
				for (int dy = -2; dy <= 2; dy++) {
					level.setBlock(reactorPos.offset(dx, dy, dz), air, Block.UPDATE_CLIENTS);
				}
			}
		}

		BlockState reactorState = ICElectricBlocks.NUCLEAR_REACTOR.block.get().defaultBlockState();
		level.setBlock(reactorPos, reactorState, Block.UPDATE_CLIENTS | Block.UPDATE_NEIGHBORS);

		BlockState chamberState = ICBlocks.NUCLEAR_REACTOR_CHAMBER.get().defaultBlockState();
		for (Direction dir : Direction.values()) {
			level.setBlock(reactorPos.relative(dir), chamberState, Block.UPDATE_CLIENTS | Block.UPDATE_NEIGHBORS);
		}

		BlockEntity be = level.getBlockEntity(reactorPos);
		if (!(be instanceof NuclearReactorBlockEntity reactor)) return false;
		reactor.recomputeActiveColumns();
		populator.accept(reactor);
		reactor.reactor.paused = false;
		reactor.reactor.allowRedstoneControl = allowRedstone;
		reactor.setChanged();
		level.sendBlockUpdated(reactorPos, reactorState, reactorState, 3);
		return true;
	}

	private static void populateQuadRodOptimalReactor(NuclearReactorBlockEntity reactor) {
		ItemStack[] grid = reactor.reactor.inputItems;
		Arrays.fill(grid, ItemStack.EMPTY);

		Item rod = ICItems.QUAD_URANIUM_FUEL_ROD.get();
		Item iRef = ICItems.IRIDIUM_NEUTRON_REFLECTOR.get();
		Item oVent = ICItems.OVERCLOCKED_HEAT_VENT.get();

		// Row 0: vent, reflector above rod, vent, vent, reflector, vent, vent, reflector, vent
		put(grid, 0, 0, oVent); put(grid, 1, 0, iRef); put(grid, 2, 0, oVent);
		put(grid, 3, 0, oVent); put(grid, 4, 0, iRef); put(grid, 5, 0, oVent);
		put(grid, 6, 0, oVent); put(grid, 7, 0, iRef); put(grid, 8, 0, oVent);

		// Row 1: reflector, rod, reflector (shared), reflector, rod, reflector (shared), ...
		put(grid, 0, 1, iRef);  put(grid, 1, 1, rod);  put(grid, 2, 1, iRef);
		put(grid, 3, 1, iRef);  put(grid, 4, 1, rod);  put(grid, 5, 1, iRef);
		put(grid, 6, 1, iRef);  put(grid, 7, 1, rod);  put(grid, 8, 1, iRef);

		// Row 2: reflectors below rods; vents elsewhere
		put(grid, 0, 2, oVent); put(grid, 1, 2, iRef); put(grid, 2, 2, oVent);
		put(grid, 3, 2, oVent); put(grid, 4, 2, iRef); put(grid, 5, 2, oVent);
		put(grid, 6, 2, oVent); put(grid, 7, 2, iRef); put(grid, 8, 2, oVent);

		// Rows 3-5: fully tiled with overclocked heat vents (27 vents)
		for (int row = 3; row <= 5; row++) {
			for (int col = 0; col < NuclearReactor.MAX_COLUMNS; col++) {
				put(grid, col, row, oVent);
			}
		}
	}

	private static void spawnFrame(ServerLevel level, BlockPos backingPos, Direction facing, ItemStack item) {
		BlockPos framePos = backingPos.relative(facing);
		ItemFrame frame = new ItemFrame(level, framePos, facing);
		frame.setItem(item, false);
		level.addFreshEntity(frame);
	}

	private static void populateReactor(NuclearReactorBlockEntity reactor) {
		ItemStack[] grid = reactor.reactor.inputItems;
		Arrays.fill(grid, ItemStack.EMPTY);

		Item p = ICItems.REACTOR_PLATING.get();
		Item P = ICItems.CONTAINMENT_REACTOR_PLATING.get();
		Item H = ICItems.HEAT_CAPACITY_REACTOR_PLATING.get();
		Item nRef = ICItems.NEUTRON_REFLECTOR.get();
		Item tRef = ICItems.THICK_NEUTRON_REFLECTOR.get();
		Item iRef = ICItems.IRIDIUM_NEUTRON_REFLECTOR.get();
		Item cSm = ICItems.SMALL_COOLANT_CELL.get();
		Item cMd = ICItems.MEDIUM_COOLANT_CELL.get();
		Item cLg = ICItems.LARGE_COOLANT_CELL.get();
		Item hVent = ICItems.HEAT_VENT.get();
		Item aVent = ICItems.ADVANCED_HEAT_VENT.get();
		Item rVent = ICItems.REACTOR_HEAT_VENT.get();
		Item oVent = ICItems.OVERCLOCKED_HEAT_VENT.get();
		Item cVent = ICItems.COMPONENT_HEAT_VENT.get();
		Item heX = ICItems.HEAT_EXCHANGER.get();
		Item eXAdv = ICItems.ADVANCED_HEAT_EXCHANGER.get();
		Item eXRec = ICItems.REACTOR_HEAT_EXCHANGER.get();
		Item eXCmp = ICItems.COMPONENT_HEAT_EXCHANGER.get();
		Item rod = ICItems.URANIUM_FUEL_ROD.get();

		//         col 0      1      2      3      4      5      6      7      8
		put(grid, 0, 0, p);     put(grid, 1, 0, hVent); put(grid, 2, 0, p);
		put(grid, 3, 0, H);     put(grid, 4, 0, aVent); put(grid, 5, 0, H);
		put(grid, 6, 0, P);     put(grid, 7, 0, rVent); put(grid, 8, 0, P);

		put(grid, 0, 1, iRef);  put(grid, 1, 1, rod);   put(grid, 2, 1, cSm);
		put(grid, 3, 1, nRef);  put(grid, 4, 1, rod);   put(grid, 5, 1, cMd);
		put(grid, 6, 1, tRef);  put(grid, 7, 1, rod);   put(grid, 8, 1, cLg);

		put(grid, 0, 2, H);     put(grid, 1, 2, heX);   put(grid, 2, 2, p);
		put(grid, 3, 2, P);     put(grid, 4, 2, eXRec); put(grid, 5, 2, H);
		put(grid, 6, 2, p);     put(grid, 7, 2, eXCmp); put(grid, 8, 2, P);

		put(grid, 0, 3, p);     put(grid, 1, 3, eXAdv); put(grid, 2, 3, H);
		put(grid, 3, 3, P);     put(grid, 4, 3, oVent); put(grid, 5, 3, p);
		put(grid, 6, 3, H);     put(grid, 7, 3, cMd);   put(grid, 8, 3, P);

		put(grid, 0, 4, nRef);  put(grid, 1, 4, rod);   put(grid, 2, 4, cSm);
		put(grid, 3, 4, tRef);  put(grid, 4, 4, rod);   put(grid, 5, 4, cMd);
		put(grid, 6, 4, iRef);  put(grid, 7, 4, rod);   put(grid, 8, 4, cLg);

		put(grid, 0, 5, p);     put(grid, 1, 5, cVent); put(grid, 2, 5, P);
		put(grid, 3, 5, H);     put(grid, 4, 5, hVent); put(grid, 5, 5, P);
		put(grid, 6, 5, p);     put(grid, 7, 5, aVent); put(grid, 8, 5, H);
	}

	private static void put(ItemStack[] grid, int col, int row, Item item) {
		grid[NuclearReactor.slotIndex(col, row)] = new ItemStack(item);
	}

	private static List<Block> collectShowcaseBlocks() {
		Set<Block> seen = new HashSet<>();
		List<Block> out = new ArrayList<>();
		Block[] statics = {
				ICBlocks.RUBBER_SHEET.get(), ICBlocks.REINFORCED_STONE.get(),
				ICBlocks.REINFORCED_GLASS.get(), ICBlocks.MACHINE_BLOCK.get(),
				ICBlocks.ADVANCED_MACHINE_BLOCK.get(), ICBlocks.IRON_FURNACE.get(),
				ICBlocks.LV_CABLE.get(), ICBlocks.MV_CABLE.get(),
				ICBlocks.HV_CABLE.get(), ICBlocks.EV_CABLE.get(),
				ICBlocks.IV_CABLE.get(), ICBlocks.BURNT_CABLE.get(),
				ICBlocks.LANDMARK.get(), ICBlocks.NUCLEAR_REACTOR_CHAMBER.get(),
				ICBlocks.NUKE.get(),
		};
		for (Block b : statics) if (seen.add(b)) out.add(b);
		for (ElectricBlockInstance ebi : ICElectricBlocks.ALL) {
			Block b = ebi.block.get();
			if (seen.add(b)) out.add(b);
		}
		return out;
	}

	private static List<ItemStack> collectShowcaseItems() {
		Set<Item> seen = new HashSet<>();
		List<ItemStack> out = new ArrayList<>();
		for (MaterialItem m : ICItems.MATERIALS) {
			if (m.item == null) continue;
			Item i = m.item.get();
			if (i != null && seen.add(i)) out.add(new ItemStack(i));
		}
		for (Item item : BuiltInRegistries.ITEM) {
			Identifier id = BuiltInRegistries.ITEM.getKey(item);
			if (id != null && IC.MOD_ID.equals(id.getNamespace()) && seen.add(item)) {
				out.add(new ItemStack(item));
			}
		}
		return out;
	}

	private ICCommands() {}
}
