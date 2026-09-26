package dev.ftb.mods.ftbic.item.reactor;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public abstract class BaseReactorItem extends Item implements ReactorItem {
    public BaseReactorItem(Properties props) {
        super(props.stacksTo(1));
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return 64;
    }

    @Override
    public boolean isDamaged(ItemStack stack) {
        // ItemStack uses this flag to prohibit stacking. Damage remains in the stack's
        // data, so only components with identical wear can merge.
        return false;
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return stack.isDamageableItem() && stack.getDamageValue() > 0;
    }

    @Override
    public void reactorTickPre(NuclearReactor reactor, ItemStack stack, int x, int y) {}

    @Override
    public void reactorTickPost(NuclearReactor reactor, ItemStack stack, int x, int y) {}
}
