package dev.ftb.mods.ftbic.block.entity.machine;

import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.block.ElectricBlock;
import dev.ftb.mods.ftbic.block.ElectricBlockState;
import dev.ftb.mods.ftbic.block.entity.ElectricBlockEntity;
import dev.ftb.mods.ftbic.item.FTBICItems;
import dev.ftb.mods.ftbic.recipe.MachineRecipeResults;
import dev.ftb.mods.ftbic.recipe.MachineRecipeSerializer;
import dev.ftb.mods.ftbic.recipe.RecipeCache;
import dev.ftb.mods.ftbic.recipe.SimpleMachineRecipeResults;
import dev.ftb.mods.ftbic.screen.MachineMenu;
import dev.ftb.mods.ftbic.util.EnergyItemHandler;
import dev.ftb.mods.ftbic.util.EnergyTier;
import dev.ftb.mods.ftbic.util.MachineProcessingResult;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class MachineBlockEntity extends ElectricBlockEntity implements ContainerData {
	public UpgradeInventory upgradeInventory;
	public BatteryInventory batteryInventory;
	public double progress;
	public double maxProgress;
	public int acceleration;
	private boolean checkProcessing;

	public boolean shouldAccelerate;
	public double energyUse;
	public double progressSpeed;
	public boolean autoEject;

	public MachineBlockEntity(BlockEntityType<?> type, int inItems, int outItems) {
		super(type, inItems, outItems);
		upgradeInventory = new UpgradeInventory(this, 4, FTBICConfig.UPGRADE_LIMIT_PER_SLOT);
		batteryInventory = new BatteryInventory(this);
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

		tag.put("Upgrades", upgradeInventory.serializeNBT().getList("Items", Constants.NBT.TAG_COMPOUND));

		if (!batteryInventory.getStackInSlot(0).isEmpty()) {
			tag.put("Battery", batteryInventory.getStackInSlot(0).serializeNBT());
		}
	}

	@Override
	public void readData(CompoundTag tag) {
		super.readData(tag);
		maxProgress = tag.getDouble("MaxProgress");
		progress = tag.getDouble("Progress");
		acceleration = tag.getInt("Acceleration");

		CompoundTag tag1 = new CompoundTag();
		tag1.put("Items", tag.getList("Upgrades", Constants.NBT.TAG_COMPOUND));
		upgradeInventory.deserializeNBT(tag1);

		if (tag.contains("Battery")) {
			batteryInventory.loadItem(ItemStack.of(tag.getCompound("Battery")));
		} else {
			batteryInventory.loadItem(ItemStack.EMPTY);
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
	protected void handleEnergyInput() {
		if (!level.isClientSide() && energy < energyCapacity) {
			ItemStack battery = batteryInventory.getStackInSlot(0);

			if (!battery.isEmpty() && battery.getItem() instanceof EnergyItemHandler) {
				EnergyItemHandler item = (EnergyItemHandler) battery.getItem();
				double e = item.extractEnergy(battery, energyCapacity - energy, false);

				if (e > 0) {
					energyAdded += e;

					if (battery.isEmpty()) {
						batteryInventory.setStackInSlot(0, ItemStack.EMPTY);
					}

					setChanged();
				}
			}
		}

		super.handleEnergyInput();
	}

	public void handleProcessing() {
		if (maxProgress > 0D && progress < maxProgress) {
			int eu = Mth.ceil(energyUse);

			if (eu > 0 && energy >= eu) {
				progress += progressSpeed;
				energy -= eu;
				changeState = ElectricBlockState.ON;

				if (energy < eu) {
					changeState = ElectricBlockState.OFF;
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

						if (autoEject) {
							Direction[] directions = getEjectDirections();

							for (int i = 0; i < outputItems.length; i++) {
								if (!outputItems[i].isEmpty()) {
									for (Direction direction : directions) {
										BlockEntity entity = level.getBlockEntity(worldPosition.relative(direction));
										IItemHandler itemHandler = entity == null ? null : entity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, direction.getOpposite()).orElse(null);

										if (itemHandler != null) {
											outputItems[i] = ItemHandlerHelper.insertItemStacked(itemHandler, outputItems[i].copy(), false);

											if (outputItems[i].isEmpty()) {
												outputItems[i] = ItemStack.EMPTY;
												break;
											}
										}
									}
								}
							}
						}
					}
				}

				checkProcessing = true;
				changeState = ElectricBlockState.OFF;
			}
		}

		if (checkProcessing) {
			checkProcessing = false;

			MachineProcessingResult result = getResult(inputItems, true);
			boolean hasResult = result.exists() && getOutput(result, true) != null;

			if (!hasResult) {
				progress = 0D;
				maxProgress = 0D;
				changeState = ElectricBlockState.OFF;
				setChanged();
			} else if (progress == 0D) {
				maxProgress = result.time * FTBICConfig.MACHINE_RECIPE_BASE_TICKS;
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

	private Direction[] getEjectDirections() {
		if (((ElectricBlock) getBlockState().getBlock()).electricBlockInstance.facingProperty != BlockStateProperties.HORIZONTAL_FACING) {
			return Direction.values();
		}

		Direction rot = getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
		Direction[] values = new Direction[6];
		values[0] = Direction.DOWN;
		values[1] = rot.getCounterClockWise();
		values[2] = rot.getOpposite();
		values[3] = rot.getClockWise();
		values[4] = rot;
		values[5] = Direction.UP;
		return values;
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
	public void onBroken(Level level, BlockPos pos) {
		super.onBroken(level, pos);

		for (int i = 0; i < upgradeInventory.getSlots(); i++) {
			Block.popResource(level, pos, upgradeInventory.getStackInSlot(i));
		}

		Block.popResource(level, pos, batteryInventory.getStackInSlot(0));
	}

	@Override
	public int get(int id) {
		switch (id) {
			case 0:
				// getProgressBar()
				return energyUse == 0 ? 0 : Mth.clamp(Mth.ceil(progress * 24D / maxProgress), 0, 24);
			case 1:
				// getEnergyBar()
				return energy == 0 ? 0 : Mth.clamp(Mth.ceil(energy * 14D / energyCapacity), 0, 14);
			case 2:
				// getAcceleration()
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
		return 3;
	}

	@Override
	public void initProperties() {
		super.initProperties();
		inputEnergyTier = EnergyTier.LV;
		energyCapacity = FTBICConfig.MACERATOR_CAPACITY;
		energyUse = FTBICConfig.MACERATOR_USE;
		progressSpeed = 1D;
		autoEject = false;
		shouldAccelerate = false;
	}

	@Override
	public void upgradesChanged() {
		super.upgradesChanged();

		int overclockers = upgradeInventory.countUpgrades(FTBICItems.OVERCLOCKER_UPGRADE.get());

		for (int i = 0; i < overclockers; i++) {
			energyUse *= FTBICConfig.OVERCLOCKER_ENERGY_USE;
			progressSpeed *= FTBICConfig.OVERCLOCKER_SPEED;
		}

		int transformers = upgradeInventory.countUpgrades(FTBICItems.TRANSFORMER_UPGRADE.get());

		while (transformers > 0) {
			transformers--;
			inputEnergyTier = inputEnergyTier.up();

			if (inputEnergyTier == EnergyTier.EV) {
				break;
			}
		}

		energyCapacity += upgradeInventory.countUpgrades(FTBICItems.ENERGY_STORAGE_UPGRADE.get()) * FTBICConfig.STORAGE_UPGRADE;
		autoEject = upgradeInventory.countUpgrades(FTBICItems.EJECTOR_UPGRADE.get()) > 0;
	}
}
