package dev.ftb.mods.ftbic.events;

import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.registry.ModDataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber(modid = FTBIC.MOD_ID)
public class LootBoxItemHandler {

    @SubscribeEvent
    public static void onUse(PlayerInteractEvent.RightClickItem event) {
        
        final ItemStack stack = event.getItemStack();
        if (!stack.has(ModDataComponents.LOOT_BOX)) return;

        final Level level = event.getLevel();
        final Player player = event.getEntity();

        if (level instanceof ServerLevel serverLevel) {
            Identifier lootTableId = stack.get(ModDataComponents.LOOT_BOX);
            LootTable lootTable = serverLevel.getServer().reloadableRegistries()
                .getLootTable(ResourceKey.create(Registries.LOOT_TABLE, lootTableId));
            if (lootTable == LootTable.EMPTY) {
                FTBIC.LOGGER.warn("Loot box {} references empty loot table {}", stack.getItem(), lootTableId);
                return;
            }
            LootParams params = new LootParams.Builder(serverLevel)
                .withParameter(LootContextParams.THIS_ENTITY, player)
                .withParameter(LootContextParams.ORIGIN, player.position())
                .create(LootContextParamSets.GIFT);

            int count = player.isShiftKeyDown() ? stack.getCount() : 1;
                
            for (int i = 0; i < count; i++) {
                for (ItemStack drop : lootTable.getRandomItems(params)) {
                    Containers.dropItemStack(serverLevel, player.getX(), player.getY(), player.getZ(), drop);
                }
            }

            if (!player.isCreative()) {
                stack.shrink(count);
            }

            event.setCancellationResult(InteractionResult.SUCCESS_SERVER);
            event.setCanceled(true);
            return;
        }

        event.setCancellationResult(InteractionResult.SUCCESS);
        event.setCanceled(true);
    }
}
