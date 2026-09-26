package dev.ftb.mods.ftbic.item;

import dev.ftb.mods.ftbic.block.entity.ElectricBlockEntity;
import dev.ftb.mods.ftbic.block.entity.machine.BatchFeederBlockEntity;
import dev.ftb.mods.ftbic.block.entity.machine.QuarryBlockEntity;
import dev.ftb.mods.ftbic.registry.ModDataComponents;
import dev.ftb.mods.ftbic.util.MachineConfiguration;
import dev.ftb.mods.ftbic.util.QuarryFilter;
import dev.ftb.mods.ftbic.util.SideConfiguration;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.neoforged.neoforge.fluids.SimpleFluidContent;

public class ConfigurationCardItem extends Item {
    public ConfigurationCardItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        var player = context.getPlayer();
        if (player == null
                || !(context.getLevel().getBlockEntity(context.getClickedPos()) instanceof ElectricBlockEntity machine))
            return InteractionResult.PASS;
        boolean supported = false;
        for (var resource : SideConfiguration.Resource.values()) supported |= machine.supportsResource(resource);
        if (!supported) return InteractionResult.PASS;
        if (context.getLevel().isClientSide()) return InteractionResult.SUCCESS;
        if (!context.getLevel().mayInteract(player, context.getClickedPos()) || !player.mayBuild())
            return InteractionResult.FAIL;
        ItemStack card = context.getItemInHand();
        var type = BuiltInRegistries.BLOCK.getKey(machine.getBlockState().getBlock());
        String message;
        if (player.isShiftKeyDown()) {
            card.set(
                    ModDataComponents.MACHINE_CONFIGURATION.get(),
                    new MachineConfiguration(
                            type,
                            machine.getSideConfiguration(),
                            machine.getInputLocks(),
                            machine instanceof BatchFeederBlockEntity feeder ? feeder.getBatch() : List.of(),
                            machine instanceof BatchFeederBlockEntity feeder
                                    ? SimpleFluidContent.copyOf(feeder.getBatchFluid())
                                    : SimpleFluidContent.EMPTY,
                            machine instanceof QuarryBlockEntity quarry ? quarry.getFilter() : QuarryFilter.DEFAULT));
            player.getInventory().setChanged();
            message = "copied";
        } else {
            var saved = card.get(ModDataComponents.MACHINE_CONFIGURATION.get());
            if (saved == null) message = "blank";
            else if (!saved.machine().equals(type)) message = "mismatch";
            else {
                machine.setSideConfiguration(saved.sides());
                machine.setInputLocks(saved.inputLocks());
                if (machine instanceof QuarryBlockEntity quarry) quarry.setFilter(saved.quarryFilter());
                if (machine instanceof BatchFeederBlockEntity feeder) {
                    feeder.setBatch(saved.batch());
                    feeder.setBatchFluid(saved.batchFluid().copy());
                }
                message = "applied";
            }
        }
        player.sendOverlayMessage(Component.translatable("item.ftbic.configuration_card." + message));
        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return stack.has(ModDataComponents.MACHINE_CONFIGURATION.get());
    }

    @Override
    @SuppressWarnings("deprecation")
    public void appendHoverText(
            ItemStack stack,
            TooltipContext context,
            TooltipDisplay display,
            Consumer<Component> tooltip,
            TooltipFlag flag) {
        var saved = stack.get(ModDataComponents.MACHINE_CONFIGURATION.get());
        if (saved != null) {
            var block = BuiltInRegistries.BLOCK.getValue(saved.machine());
            tooltip.accept(Component.translatable("item.ftbic.configuration_card.contents", block.getName())
                    .withStyle(ChatFormatting.AQUA));
        }
        tooltip.accept(
                Component.translatable("item.ftbic.configuration_card.copy").withStyle(ChatFormatting.GRAY));
        tooltip.accept(
                Component.translatable("item.ftbic.configuration_card.use").withStyle(ChatFormatting.GRAY));
    }
}
