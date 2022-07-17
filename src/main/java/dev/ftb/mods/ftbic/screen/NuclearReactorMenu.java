package dev.ftb.mods.ftbic.screen;

import dev.ftb.mods.ftbic.block.entity.generator.NuclearReactorBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public class NuclearReactorMenu extends ElectricBlockMenu<NuclearReactorBlockEntity> {
	public NuclearReactorMenu(int id, Inventory playerInv, NuclearReactorBlockEntity r) {
		super(FTBICMenus.NUCLEAR_REACTOR.get(), id, playerInv, r, null);
	}

	public NuclearReactorMenu(int id, Inventory playerInv, FriendlyByteBuf buf) {
		this(id, playerInv, (NuclearReactorBlockEntity) playerInv.player.level.getBlockEntity(buf.readBlockPos()));
	}

	@Override
	public int getPlayerSlotOffset() {
		return 140;
	}

	@Override
	public void addBlockSlots(@Nullable Object extra) {
		for (int y = 0; y < 6; y++) {
			for (int x = 0; x < 9; x++) {
				addSlot(new NuclearReactorSlot(entity, y * 9 + x, 8 + x * 18, 18 + y * 18));
			}
		}
	}

	@Override
	public boolean clickMenuButton(Player player, int button) {
		if (button == 0) {
			entity.reactor.paused = !entity.reactor.paused;
			entity.setChanged();
			return true;
		}

		if (button == 1) {
			entity.reactor.allowRedstoneControl = !entity.reactor.allowRedstoneControl;
			entity.setChanged();
			return true;
		}

		return false;
	}
}
