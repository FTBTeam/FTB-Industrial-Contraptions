package dev.ftb.mods.ftbic.screen;

import dev.ftb.mods.ftbic.block.entity.machine.MachineBlockEntity;
import dev.ftb.mods.ftbic.recipe.MachineRecipeSerializer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class MachineMenu extends ElectricBlockMenu<MachineBlockEntity> {
	public final MachineRecipeSerializer serializer;

	public MachineMenu(int id, Inventory playerInv, MachineBlockEntity r, MachineRecipeSerializer s) {
		super(FTBICMenus.MACHINE.get(), id, playerInv, r, s);
		serializer = s;
	}

	public MachineMenu(int id, Inventory playerInv, FriendlyByteBuf buf) {
		this(id, playerInv, (MachineBlockEntity) playerInv.player.level.getBlockEntity(buf.readBlockPos()), (MachineRecipeSerializer) ForgeRegistries.RECIPE_SERIALIZERS.getValue(buf.readResourceLocation()));
	}

	@Override
	public void addBlockSlots(@Nullable Object extra) {
		MachineRecipeSerializer s = Objects.requireNonNull((MachineRecipeSerializer) extra);

		int wx = 78 - (s.guiWidth / 2);
		int wy = 16;

		for (int y = 0; y < entity.upgradeInventory.getSlots(); y++) {
			addSlot(new SimpleItemHandlerSlot(entity.upgradeInventory, y, 152, 8 + y * 18));
		}

		addSlot(new SimpleItemHandlerSlot(entity.batteryInventory, 0, wx + 1 + s.batteryX, wy + 1 + s.batteryY));

		for (int x = 0; x < entity.inputItems.length; x++) {
			addSlot(new SimpleItemHandlerSlot(entity, x, wx + 1 + x * 18, wy + 1));
		}

		for (int x = 0; x < entity.outputItems.length; x++) {
			addSlot(new SimpleItemHandlerSlot(entity, entity.inputItems.length + x, wx + s.outputX + 5 + x * 25, wy + s.outputY + 5));
		}
	}
}
