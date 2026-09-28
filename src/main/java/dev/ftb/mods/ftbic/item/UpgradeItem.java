package dev.ftb.mods.ftbic.item;

import dev.ftb.mods.ftbic.block.entity.machine.BasicMachineBlockEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class UpgradeItem extends Item {
    public final int upgradeId;

    public UpgradeItem(Properties props, int upgradeId) {
        super(props);
        this.upgradeId = upgradeId;
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        Player player = context.getPlayer();
        Level level = context.getLevel();
        if (player == null
                || !player.isSecondaryUseActive()
                || !player.mayBuild()
                || !level.mayInteract(player, context.getClickedPos())
                || !(level.getBlockEntity(context.getClickedPos()) instanceof BasicMachineBlockEntity machine)) {
            return InteractionResult.PASS;
        }
        if (!level.isClientSide()) {
            int inserted = machine.upgradeInventory.insert(stack);
            if (inserted > 0) {
                if (!player.isCreative()) stack.shrink(inserted);
                level.playSound(
                        null,
                        context.getClickedPos(),
                        SoundEvents.STONE_BUTTON_CLICK_ON,
                        SoundSource.BLOCKS,
                        0.3F,
                        1.2F);
            } else {
                player.sendOverlayMessage(Component.translatable("ftbic.upgrade.cannot_insert"));
            }
        }
        return InteractionResult.SUCCESS;
    }
}
