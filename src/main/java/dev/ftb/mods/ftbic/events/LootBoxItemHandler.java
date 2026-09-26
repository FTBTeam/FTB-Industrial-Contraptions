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
        ItemStack stack = event.getItemStack();
        Identifier tableId = stack.get(ModDataComponents.LOOT_BOX.get());
        if (tableId == null) return;

        if (!(event.getLevel() instanceof ServerLevel serverLevel)) {
            event.setCancellationResult(InteractionResult.SUCCESS);
            event.setCanceled(true);
            return;
        }

        LootTable table = serverLevel
                .getServer()
                .reloadableRegistries()
                .getLootTable(ResourceKey.create(Registries.LOOT_TABLE, tableId));
        if (table == LootTable.EMPTY) {
            FTBIC.LOGGER.warn("Loot box {} references missing loot table {}", stack.getItem(), tableId);
            return;
        }

        Player player = event.getEntity();
        LootParams params = new LootParams.Builder(serverLevel)
                .withParameter(LootContextParams.THIS_ENTITY, player)
                .withParameter(LootContextParams.ORIGIN, player.position())
                .create(LootContextParamSets.GIFT);
        int count = player.isShiftKeyDown() ? stack.getCount() : 1;
        for (int i = 0; i < count; i++) {
            for (ItemStack reward : table.getRandomItems(params)) {
                Containers.dropItemStack(serverLevel, player.getX(), player.getY(), player.getZ(), reward);
            }
        }
        stack.consume(count, player);
        event.setCancellationResult(InteractionResult.SUCCESS_SERVER);
        event.setCanceled(true);
    }
}
