package dev.ftb.mods.ftbic.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.block.ElectricBlockInstance;
import dev.ftb.mods.ftbic.block.FTBICBlocks;
import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.item.FTBICItems;
import dev.ftb.mods.ftbic.item.MaterialItem;
import dev.ftb.mods.ftbic.world.ResourceElements;
import dev.ftb.mods.ftbic.world.ResourceType;
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

		String summary = String.format(
				"FTBIC showcase built: %d floor blocks, %d sample blocks, %d items in frames",
				placedFloor, placedBlocks, placedFrames);
		src.sendSuccess(() -> Component.literal(summary), true);
		return 1;
	}

	private static List<Block> collectShowcaseBlocks() {
		Set<Block> seen = new HashSet<>();
		List<Block> out = new ArrayList<>();

		for (ResourceElements el : ResourceElements.VALUES) {
			var ore = FTBICBlocks.RESOURCE_ORES.get(el);
			if (ore != null && seen.add(ore.get())) out.add(ore.get());
			var rb = FTBICBlocks.RESOURCE_BLOCKS_OF.get(el);
			if (rb != null && seen.add(rb.get())) out.add(rb.get());
		}
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
		for (var typeEntry : FTBICItems.RESOURCE_TYPE_MAP.entrySet()) {
			ResourceType type = typeEntry.getKey();
			if (type == ResourceType.ORE || type == ResourceType.BLOCK) continue;
			for (var sup : typeEntry.getValue().values()) {
				Item i = sup.get();
				if (i != null && seen.add(i)) out.add(new ItemStack(i));
			}
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
