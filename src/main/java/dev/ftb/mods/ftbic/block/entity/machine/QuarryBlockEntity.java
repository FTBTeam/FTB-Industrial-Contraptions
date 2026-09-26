package dev.ftb.mods.ftbic.block.entity.machine;

import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.item.FTBICItems;
import dev.ftb.mods.ftbic.screen.QuarryMenu;
import dev.ftb.mods.ftbic.util.QuarryFilter;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class QuarryBlockEntity extends DiggingBaseBlockEntity {
    public ItemStack pickaxeStack = ItemStack.EMPTY;
    private QuarryFilter filter = QuarryFilter.DEFAULT;

    public QuarryFilter getFilter() {
        return filter;
    }

    public void setFilter(QuarryFilter value) {
        filter = value;
        skippedBlocks = 0;
        setChanged();
        if (level != null && !level.isClientSide())
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }

    public boolean hasFilterUpgrade() {
        return upgradeInventory.countUpgrades(FTBICItems.QUARRY_FILTER_UPGRADE.get()) > 0;
    }

    @Override
    protected boolean skipEmptyTargetsWithoutEnergy() {
        return hasFilterUpgrade();
    }

    @Override
    public boolean isValidBlock(BlockState state, BlockPos pos) {
        return super.isValidBlock(state, pos) && (!hasFilterUpgrade() || filter.matches(state));
    }

    public QuarryBlockEntity(BlockPos pos, BlockState state) {
        super(FTBICElectricBlocks.QUARRY, pos, state);
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv) {
        return new QuarryMenu(id, inv, this);
    }

    public Container pickaxeContainer() {
        return new PickaxeContainer();
    }

    @Override
    public void upgradesChanged() {
        super.upgradesChanged();
        if (!pickaxeStack.isEmpty() && pickaxeStack.is(ItemTags.PICKAXES) && level != null) {
            int efficiency = EnchantmentHelper.getItemEnchantmentLevel(
                    level.registryAccess().holderOrThrow(Enchantments.EFFICIENCY), pickaxeStack);
            if (efficiency > 0) {
                progressSpeed *= 1D + 0.1D * efficiency;
            }
        }
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        if (!pickaxeStack.isEmpty()) {
            output.store("Pickaxe", ItemStack.CODEC, pickaxeStack);
        }
        output.store("QuarryFilter", QuarryFilter.CODEC, filter);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        if (!isClientSync(input)) {
            pickaxeStack = input.read("Pickaxe", ItemStack.CODEC).orElse(ItemStack.EMPTY);
        }
        filter = input.read("QuarryFilter", QuarryFilter.CODEC).orElse(QuarryFilter.DEFAULT);
        if (!pickaxeStack.isEmpty()) {
            initProperties();
            upgradesChanged();
        }
    }

    @Override
    public void onBroken(Level level, BlockPos pos) {
        super.onBroken(level, pos);
        if (!pickaxeStack.isEmpty()) {
            Block.popResource(level, pos, pickaxeStack);
            pickaxeStack = ItemStack.EMPTY;
        }
    }

    @Override
    public void digBlock(BlockState state, BlockPos miningPos) {
        if (!(level instanceof ServerLevel server)) return;

        ItemStack tool = pickaxeStack;
        List<ItemStack> drops;
        if (!tool.isEmpty() && tool.is(ItemTags.PICKAXES)) {
            drops = Block.getDrops(state, server, miningPos, null, getFakePlayer(server), tool);
        } else {
            drops = Block.getDrops(state, server, miningPos, null);
        }

        if (!canFitAllDrops(drops)) {
            paused = true;
            setChanged();
            return;
        }
        clearMinedBlock(miningPos, state);
        for (ItemStack drop : drops) {
            addToOutputs(drop);
        }

        if (!tool.isEmpty()
                && tool.is(ItemTags.PICKAXES)
                && FTBICConfig.MACHINES.QUARRY_PICKAXE_TAKES_DAMAGE.get()
                && tool.isDamageableItem()) {
            tool.hurtAndBreak(1, server, null, item -> {
                pickaxeStack = ItemStack.EMPTY;
                initProperties();
                upgradesChanged();
            });
        }

        setChanged();
    }

    private class PickaxeContainer implements Container {
        @Override
        public int getContainerSize() {
            return 1;
        }

        @Override
        public boolean isEmpty() {
            return pickaxeStack.isEmpty();
        }

        @Override
        public ItemStack getItem(int slot) {
            return slot == 0 ? pickaxeStack : ItemStack.EMPTY;
        }

        @Override
        public ItemStack removeItem(int slot, int count) {
            if (slot != 0 || pickaxeStack.isEmpty() || count <= 0) return ItemStack.EMPTY;
            ItemStack taken = pickaxeStack.copy();
            taken.setCount(Math.min(count, pickaxeStack.getCount()));
            pickaxeStack.shrink(taken.getCount());
            if (pickaxeStack.isEmpty()) pickaxeStack = ItemStack.EMPTY;
            setChanged();
            return taken;
        }

        @Override
        public ItemStack removeItemNoUpdate(int slot) {
            if (slot != 0) return ItemStack.EMPTY;
            ItemStack removed = pickaxeStack;
            pickaxeStack = ItemStack.EMPTY;
            return removed;
        }

        @Override
        public void setItem(int slot, ItemStack stack) {
            if (slot != 0) return;
            pickaxeStack = stack == null ? ItemStack.EMPTY : stack;
            setChanged();
        }

        @Override
        public void setChanged() {
            if (hasLevel() && !level.isClientSide()) {
                initProperties();
                upgradesChanged();
            }
            QuarryBlockEntity.this.setChanged();
        }

        @Override
        public boolean stillValid(Player player) {
            return !QuarryBlockEntity.this.isRemoved();
        }

        @Override
        public void clearContent() {
            pickaxeStack = ItemStack.EMPTY;
            setChanged();
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }

        @Override
        public boolean canPlaceItem(int slot, ItemStack stack) {
            return slot == 0 && stack.is(ItemTags.PICKAXES);
        }
    }
}
