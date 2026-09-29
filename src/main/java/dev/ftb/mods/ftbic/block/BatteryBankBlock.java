package dev.ftb.mods.ftbic.block;

import dev.ftb.mods.ftbic.block.entity.storage.BankPortBlockEntity;
import java.util.Locale;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

/** Connections drive the seamless casing and are shared by cells and ports. */
public class BatteryBankBlock extends ElectricBlock {
    public static final BooleanProperty[] CONNECTION = {
        BooleanProperty.create("down"), BooleanProperty.create("up"),
        BooleanProperty.create("north"), BooleanProperty.create("south"),
        BooleanProperty.create("west"), BooleanProperty.create("east")
    };

    public BatteryBankBlock(ElectricBlockInstance instance, BlockBehaviour.Properties properties) {
        super(instance, properties);
        BlockState state = defaultBlockState();
        for (BooleanProperty property : CONNECTION) state = state.setValue(property, false);
        registerDefaultState(state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(CONNECTION);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        for (Direction direction : Direction.values()) {
            state = state.setValue(
                    CONNECTION[direction.ordinal()],
                    context.getLevel()
                                    .getBlockState(context.getClickedPos().relative(direction))
                                    .getBlock()
                            instanceof BatteryBankBlock);
        }
        return state;
    }

    @Override
    protected BlockState updateShape(
            BlockState state,
            LevelReader level,
            ScheduledTickAccess ticks,
            BlockPos pos,
            Direction direction,
            BlockPos neighborPos,
            BlockState neighbor,
            RandomSource random) {
        return state.setValue(CONNECTION[direction.ordinal()], neighbor.getBlock() instanceof BatteryBankBlock);
    }

    @Override
    protected InteractionResult useItemOn(
            ItemStack stack,
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BlockHitResult hit) {
        InteractionResult result = cyclePortStyle(level, pos, player);
        return result == InteractionResult.PASS ? super.useItemOn(stack, state, level, pos, player, hand, hit) : result;
    }

    @Override
    protected InteractionResult useWithoutItem(
            BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        InteractionResult result = cyclePortStyle(level, pos, player);
        return result == InteractionResult.PASS ? super.useWithoutItem(state, level, pos, player, hit) : result;
    }

    private static InteractionResult cyclePortStyle(Level level, BlockPos pos, Player player) {
        if (!player.isShiftKeyDown() || !(level.getBlockEntity(pos) instanceof BankPortBlockEntity port)) {
            return InteractionResult.PASS;
        }
        if (!player.mayBuild() || !level.mayInteract(player, pos)) return InteractionResult.PASS;
        if (!level.isClientSide()) {
            BankPortBlockEntity.FaceStyle style = port.cycleFaceStyle();
            player.sendOverlayMessage(Component.translatable(
                    "ftbic.bank_port.style." + style.name().toLowerCase(Locale.ROOT)));
        }
        return InteractionResult.SUCCESS;
    }
}
