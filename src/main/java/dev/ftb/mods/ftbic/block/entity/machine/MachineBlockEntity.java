package dev.ftb.mods.ftbic.block.entity.machine;

import dev.ftb.mods.ftbic.block.ElectricBlockState;
import dev.ftb.mods.ftbic.block.entity.ElectricBlockEntity;
import dev.ftb.mods.ftbic.item.BatteryItem;
import dev.ftb.mods.ftbic.recipe.MachineRecipeResults;
import dev.ftb.mods.ftbic.recipe.MachineRecipeSerializer;
import dev.ftb.mods.ftbic.recipe.RecipeCache;
import dev.ftb.mods.ftbic.recipe.SimpleMachineRecipeResults;
import dev.ftb.mods.ftbic.screen.MachineMenu;
import dev.ftb.mods.ftbic.util.MachineProcessingResult;
import dev.ftb.mods.ftbic.util.PowerTier;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class MachineBlockEntity extends ElectricBlockEntity implements ContainerData {
	public final UpgradeInventory upgradeInventory;
	public final BatteryInventory batteryInventory;
	public int baseEnergyUse;
	public int progress;
	public int lastMaxProgress;
	public int acceleration;
	private boolean checkProcessing;

	public MachineBlockEntity(BlockEntityType<?> type, int inItems, int outItems) {
		super(type, inItems, outItems);
		upgradeInventory = new UpgradeInventory(this);
		batteryInventory = new BatteryInventory(this);
		inputPowerTier = PowerTier.LV;
		energyCapacity = 8000;
		baseEnergyUse = 20;
		progress = 0;
		lastMaxProgress = 0;
		acceleration = 0;
		checkProcessing = true;
	}

	public double getBaseRecipeTime() {
		return 200D;
	}

	@Override
	public void writeData(CompoundTag tag) {
		super.writeData(tag);
		tag.putInt("LastMaxProgress", lastMaxProgress);
		tag.putInt("Progress", progress);

		if (acceleration > 0) {
			tag.putInt("Acceleration", acceleration);
		}

		tag.put("Upgrades", upgradeInventory.serializeNBT().getList("Items", Constants.NBT.TAG_COMPOUND));

		if (!batteryInventory.getStackInSlot(0).isEmpty()) {
			tag.put("Battery", batteryInventory.getStackInSlot(0).serializeNBT());
		}
	}

	@Override
	public void readData(CompoundTag tag) {
		super.readData(tag);
		lastMaxProgress = tag.getInt("LastMaxProgress");
		progress = tag.getInt("Progress");
		acceleration = tag.getInt("Acceleration");

		CompoundTag tag1 = new CompoundTag();
		tag1.put("Items", tag.getList("Upgrades", Constants.NBT.TAG_COMPOUND));
		upgradeInventory.deserializeNBT(tag1);

		if (tag.contains("Battery")) {
			batteryInventory.setStackInSlot(0, ItemStack.of(tag.getCompound("Battery")));
		} else {
			batteryInventory.setStackInSlot(0, ItemStack.EMPTY);
		}
	}

	@Override
	public void tick() {
		handleEnergyInput();
		handleProcessing();
		handleChanges();
	}

	@Nullable
	private ItemStackHandler getOutput(MachineProcessingResult result, boolean simulate) {
		// Best way to check if output inventory fits all items is to just copy the current output array and try to insert all items
		ItemStackHandler output = new ItemStackHandler(NonNullList.withSize(outputItems.length, ItemStack.EMPTY));
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
	protected void handleEnergyInput() {
		if (!level.isClientSide() && energy < energyCapacity) {
			ItemStack battery = batteryInventory.getStackInSlot(0);

			if (!battery.isEmpty() && battery.getItem() instanceof BatteryItem) {
				BatteryItem item = (BatteryItem) battery.getItem();
				int be = BatteryItem.getEnergy(battery);
				int e = Math.min(energyCapacity - energy, Math.min(item.tier.batteryTransferRate, be));

				if (e > 0) {
					BatteryItem.setEnergy(battery, be - e);
					energyAdded += e;

					if (be - e == 0 && item.batteryType.singleUse) {
						battery.shrink(1);
					}

					setChanged();
				}
			}
		}

		super.handleEnergyInput();
	}

	public void handleProcessing() {
		if (progress > 0) {
			if (energy >= baseEnergyUse) {
				progress--;
				energy -= baseEnergyUse;
				changeState = ElectricBlockState.ON;

				if (energy < baseEnergyUse) {
					changeState = ElectricBlockState.OFF;
					setChanged();
				}

				if (shouldAccelerate()) {
					acceleration++;
				}
			}

			if (progress == 0) {
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
					}
				}

				checkProcessing = true;
				changeState = ElectricBlockState.OFF;
			}
		}

		if (progress == 0 && checkProcessing) {
			checkProcessing = false;

			MachineProcessingResult result = getResult(inputItems, true);

			if (result.exists() && getOutput(result, true) != null) {
				lastMaxProgress = Mth.ceil(result.time * getBaseRecipeTime());
				progress = lastMaxProgress;
				changeState = ElectricBlockState.ON;
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

	public boolean shouldAccelerate() {
		return false;
	}

	@Override
	public void inventoryChanged(int slot, int prevCount) {
		super.inventoryChanged(slot, prevCount);

		if (slot < inputItems.length || outputItems[slot - inputItems.length].getCount() < prevCount) {
			checkProcessing = true;
		}
	}

	@Override
	public void energyChanged(int prev) {
		super.energyChanged(prev);

		if (prev < baseEnergyUse && energy >= baseEnergyUse) {
			checkProcessing = true;
		}
	}

	public void shiftInputs() {
		if (inputItems.length <= 1) {
			return;
		}

		List<ItemStack> stacks = new ArrayList<>();

		for (int i = 0; i < inputItems.length; i++) {
			if (!inputItems[i].isEmpty()) {
				stacks.add(inputItems[i]);
				inputItems[i] = ItemStack.EMPTY;
			}
		}

		for (ItemStack stack : stacks) {
			// drop items that failed to shift for some reason? but that should be impossible unless max stack size has changed for that item...
			ItemHandlerHelper.insertItemStacked(this, stack, false);
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
				NetworkHooks.openGui((ServerPlayer) player, new MenuProvider() {
					@Override
					public Component getDisplayName() {
						return getBlockState().getBlock().getName();
					}

					@Override
					public AbstractContainerMenu createMenu(int id, Inventory playerInv, Player player1) {
						return new MachineMenu(id, playerInv, MachineBlockEntity.this, MachineBlockEntity.this, serializer);
					}
				}, this::writeMenu);
			}
		}

		return InteractionResult.SUCCESS;
	}

	private void writeMenu(FriendlyByteBuf buf) {
		buf.writeBlockPos(worldPosition);
		buf.writeResourceLocation(getRecipeSerializer().getRegistryName());
	}

	@Override
	public int get(int id) {
		switch (id) {
			case 0:
				return progress;
			case 1:
				return lastMaxProgress;
			case 2:
				return energy;
			case 3:
				return energyCapacity;
			case 4:
				return progress > 0 ? baseEnergyUse : 0;
			case 5:
				return acceleration;
			default:
				return 0;
		}
	}

	@Override
	public void set(int id, int value) {
	}

	@Override
	public int getCount() {
		return 6;
	}
}
