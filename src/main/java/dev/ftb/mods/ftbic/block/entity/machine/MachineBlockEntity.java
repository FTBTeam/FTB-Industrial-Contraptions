package dev.ftb.mods.ftbic.block.entity.machine;

import dev.ftb.mods.ftbic.block.entity.ElectricBlockEntity;
import dev.ftb.mods.ftbic.recipe.MachineRecipeResults;
import dev.ftb.mods.ftbic.recipe.RecipeCache;
import dev.ftb.mods.ftbic.util.FTBICUtils;
import dev.ftb.mods.ftbic.util.MachineProcessingResult;
import dev.ftb.mods.ftbic.util.PowerTier;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

public abstract class MachineBlockEntity extends ElectricBlockEntity {
	public final UpgradeInventory upgradeInventory;
	public int baseEnergyUse;
	public int progress;
	public int heat;

	public MachineBlockEntity(BlockEntityType<?> type, int inItems, int outItems) {
		super(type, inItems, outItems);
		upgradeInventory = new UpgradeInventory(this);
		inputPowerTier = PowerTier.LV;
		energyCapacity = 8000;
		baseEnergyUse = 20;
		progress = 0;
		heat = 0;
	}

	@Override
	public void writeData(CompoundTag tag) {
		super.writeData(tag);
	}

	@Override
	public void readData(CompoundTag tag) {
		super.readData(tag);
	}

	@Override
	public void tick() {
		handleEnergyInput();
		handleProcessing();
		handleChanges();
	}

	public void handleProcessing() {
	}

	public abstract MachineRecipeResults getRecipes(RecipeCache cache);

	public MachineProcessingResult getResult(ItemStack[] items) {
		RecipeCache cache = getRecipeCache();
		return cache != null ? getRecipes(cache).getResult(level, items) : MachineProcessingResult.NONE;
	}

	@Override
	public boolean isItemValid(int slot, @NotNull ItemStack stack) {
		RecipeCache cache = getRecipeCache();
		return cache != null && getRecipes(cache).canInsert(level, slot, stack);
	}

	@Override
	public InteractionResult rightClick(Player player, InteractionHand hand, BlockHitResult hit) {
		if (!level.isClientSide()) {
			player.displayClientMessage(new TextComponent("Energy: " + FTBICUtils.formatPower(energy, energyCapacity)), false);

			MachineProcessingResult result = getResult(new ItemStack[]{player.getItemInHand(hand)});

			if (result.exists()) {
				player.displayClientMessage(new TextComponent("Result: ").append(result.result.stack.getHoverName()), false);
			}
		}

		return InteractionResult.SUCCESS;
	}
}
