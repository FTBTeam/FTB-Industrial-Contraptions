package dev.ftb.mods.ftbic.block.entity.machine;

import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.block.ElectricBlockInstance;
import dev.ftb.mods.ftbic.recipe.MachineRecipeResults;
import dev.ftb.mods.ftbic.recipe.MachineRecipeSerializer;
import dev.ftb.mods.ftbic.recipe.RecipeCache;
import dev.ftb.mods.ftbic.recipe.SimpleMachineRecipeResults;
import dev.ftb.mods.ftbic.screen.MachineMenu;
import dev.ftb.mods.ftbic.screen.sync.SyncedData;
import dev.ftb.mods.ftbic.util.MachineProcessingResult;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public abstract class MachineBlockEntity extends BasicMachineBlockEntity {
	public double progress;
	public double maxProgress;
	public int acceleration;
	private boolean checkProcessing;

	public boolean shouldAccelerate;

	public MachineBlockEntity(ElectricBlockInstance type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		progress = 0;
		maxProgress = 0D;
		acceleration = 0;
		checkProcessing = true;
	}

	@Override
	public void writeData(CompoundTag tag) {
		super.writeData(tag);
		tag.putDouble("MaxProgress", maxProgress);
		tag.putDouble("Progress", progress);

		if (acceleration > 0) {
			tag.putInt("Acceleration", acceleration);
		}
	}

	@Override
	public void readData(CompoundTag tag) {
		super.readData(tag);
		maxProgress = tag.getDouble("MaxProgress");
		progress = tag.getDouble("Progress");
		acceleration = tag.getInt("Acceleration");
	}

	@Nullable
	private ItemStackHandler getOutput(MachineProcessingResult result, boolean simulate) {
		// Best way to check if output inventory fits all items is to just copy the current output array and try to insert all items
		ItemStackHandler output = new ItemStackHandler(NonNullList.withSize(outputItems.length, ItemStack.EMPTY));

		for (int i = 0; i < outputItems.length; i++) {
			output.setStackInSlot(i, outputItems[i].copy());
		}

		Random random = level != null ? level.random : new Random();

		if (simulate || random.nextDouble() < result.output.chance) {
			if (!ItemHandlerHelper.insertItemStacked(output, result.output.stack.copy(), false).isEmpty()) {
				return null;
			}
		}

		for (int i = 0; i < result.extra.length; i++) {
			if (result.extra[i].chance >= 1D) {
				if (!ItemHandlerHelper.insertItemStacked(output, result.extra[i].stack.copy(), false).isEmpty()) {
					return null;
				}
			} else if (!simulate && random.nextDouble() < result.extra[i].chance) {
				if (!ItemHandlerHelper.insertItemStacked(output, result.extra[i].stack.copy(), false).isEmpty()) {
					return null;
				}
			}
		}

		return output;
	}

	@Override
	public void handleProcessing() {
		if (isBurnt() || level.isClientSide()) {
			return;
		}

		if (maxProgress > 0D && progress < maxProgress) {
			int eu = Mth.ceil(energyUse);

			if (eu > 0 && energy >= eu) {
				progress += progressSpeed;
				energy -= eu;
				active = true;

				if (energy < eu) {
					setChanged();
				}

				if (shouldAccelerate) {
					acceleration++;
				}
			}

			if (progress >= maxProgress) {
				progress = 0D;
				setChanged();
				shiftInputs();
				MachineProcessingResult result = getResult(inputItems, true);

				if (result.exists()) {
					ItemStackHandler out = getOutput(result, false);

					if (out != null) {
						for (int i = 0; i < result.consume.length; i++) {
							inputItems[i].shrink(result.consume[i]);

							if (inputItems[i].isEmpty()) {
								inputItems[i] = ItemStack.EMPTY;
							}
						}

						for (int i = 0; i < outputItems.length; i++) {
							outputItems[i] = out.getStackInSlot(i);
						}

						shiftInputs();
						ejectOutputItems();
					}
				}

				checkProcessing = true;
			}
		}

		if (checkProcessing) {
			checkProcessing = false;

			MachineProcessingResult result = getResult(inputItems, true);
			boolean hasResult = result.exists() && getOutput(result, true) != null;

			if (!hasResult) {
				progress = 0D;
				maxProgress = 0D;
				setChanged();
			} else if (progress <= 0D) {
				maxProgress = result.time * FTBICConfig.MACHINE_RECIPE_BASE_TICKS;
				active = true;
				setChanged();
			}
		}

		if (acceleration > 0) {
			acceleration--;

			if (acceleration == 0) {
				setChanged();
			}
		}
	}

	@Override
	public void inventoryChanged(int slot, @Nullable ItemStack prev) {
		super.inventoryChanged(slot, prev);
		checkProcessing = true;
	}

	@Override
	public void energyChanged(int prev) {
		super.energyChanged(prev);

		if (energyUse != 0 && prev < energyUse && energy >= energyUse) {
			checkProcessing = true;
		}
	}

	public abstract MachineRecipeResults getRecipes(RecipeCache cache);

	public MachineProcessingResult getResult(ItemStack[] items, boolean checkCount) {
		RecipeCache cache = getRecipeCache();
		return cache != null ? getRecipes(cache).getResult(level, items, checkCount) : MachineProcessingResult.NONE;
	}

	@Override
	public boolean isItemValid(int slot, @NotNull ItemStack stack) {
		RecipeCache cache = getRecipeCache();
		return cache != null && getRecipes(cache).canInsert(level, slot, stack);
	}

	@Nullable
	public MachineRecipeSerializer getRecipeSerializer() {
		RecipeCache cache = getRecipeCache();
		MachineRecipeResults results = cache == null ? null : getRecipes(cache);
		return results instanceof SimpleMachineRecipeResults ? ((SimpleMachineRecipeResults) results).recipeSerializer.get() : null;
	}

	@Override
	public InteractionResult rightClick(Player player, InteractionHand hand, BlockHitResult hit) {
		if (!level.isClientSide()) {
			MachineRecipeSerializer serializer = getRecipeSerializer();

			if (serializer != null) {
				openMenu((ServerPlayer) player, (id, inventory) -> new MachineMenu(id, inventory, this, serializer));
			} else {
				player.sendMessage(new TextComponent("No GUI yet!"), Util.NIL_UUID);
			}
		}

		return InteractionResult.SUCCESS;
	}

	@Override
	public void writeMenu(ServerPlayer player, FriendlyByteBuf buf) {
		super.writeMenu(player, buf);
		buf.writeResourceLocation(getRecipeSerializer().getRegistryName());
	}

	@Override
	public void addSyncData(SyncedData data) {
		super.addSyncData(data);
		data.addShort(SyncedData.BAR, () -> energyUse == 0 ? 0 : Mth.clamp(Mth.ceil(progress * 24D / maxProgress), 0, 24));
		data.addShort(SyncedData.ACCELERATION, () -> acceleration);
	}

	@Override
	public void initProperties() {
		super.initProperties();
		shouldAccelerate = false;
	}
}
