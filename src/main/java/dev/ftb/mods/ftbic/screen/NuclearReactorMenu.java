package dev.ftb.mods.ftbic.screen;

import dev.ftb.mods.ftbic.block.entity.generator.NuclearReactorBlockEntity;
import dev.ftb.mods.ftbic.util.FTBICUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import org.jetbrains.annotations.Nullable;

public class NuclearReactorMenu extends ElectricBlockMenu<NuclearReactorBlockEntity> {
	public NuclearReactorMenu(int id, Inventory playerInv, NuclearReactorBlockEntity r, ContainerData d) {
		super(FTBICMenus.NUCLEAR_REACTOR.get(), id, playerInv, r, d, null);
	}

	public NuclearReactorMenu(int id, Inventory playerInv, FriendlyByteBuf buf) {
		this(id, playerInv, (NuclearReactorBlockEntity) playerInv.player.level.getBlockEntity(buf.readBlockPos()), new SimpleContainerData(5));
	}

	@Override
	public int getPlayerSlotOffset() {
		return 140;
	}

	@Override
	public void addBlockSlots(@Nullable Object extra) {
		for (int y = 0; y < 6; y++) {
			for (int x = 0; x < 9; x++) {
				addSlot(new SimpleItemHandlerSlot(entity, y * 9 + x, 8 + x * 18, 18 + y * 18));
			}
		}
	}

	public int getEnergyOutput() {
		return FTBICUtils.unpackInt(containerData.get(1), 150000);
	}

	public int getHeat() {
		return FTBICUtils.unpackInt(containerData.get(2), 101800);
	}

	public int getChambers() {
		return containerData.get(3);
	}

	public boolean isPaused() {
		return containerData.get(4) != 0;
	}

	@Override
	public boolean clickMenuButton(Player player, int button) {
		if (button == 0) {
			entity.reactor.paused = !entity.reactor.paused;
			entity.setChanged();
			return true;
		}

		return false;
	}
}
