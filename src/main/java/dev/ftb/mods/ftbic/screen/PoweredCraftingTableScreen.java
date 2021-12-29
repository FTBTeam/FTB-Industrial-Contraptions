package dev.ftb.mods.ftbic.screen;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.ftb.mods.ftbic.screen.sync.SyncedData;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class PoweredCraftingTableScreen extends ElectricBlockScreen<PoweredCraftingTableMenu> {
	private final Ingredient[] clientIngredients;
	private final ItemStack[][] clientItemStacks;

	public PoweredCraftingTableScreen(PoweredCraftingTableMenu m, Inventory inv, Component c) {
		super(m, inv, c);
		energyX = 16;
		energyY = 26;
		clientIngredients = new Ingredient[9];
		clientItemStacks = new ItemStack[9][0];
	}

	@Override
	protected void renderBg(PoseStack poseStack, float delta, int mouseX, int mouseY) {
		super.renderBg(poseStack, delta, mouseX, mouseY);

		for (int i = 0; i < menu.entity.upgradeInventory.getSlots(); i++) {
			drawSlot(poseStack, leftPos + 151, topPos + 7 + i * 18);
		}

		drawSlot(poseStack, leftPos + 14, topPos + 43);

		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 3; x++) {
				drawSlot(poseStack, leftPos + 43 + x * 18, topPos + 16 + y * 18);
			}
		}

		drawLargeSlot(poseStack, leftPos + 110, topPos + 30);

		Lighting.setupFor3DItems();
		RenderSystem.color4f(1F, 1F, 1F, 1F);
		RenderSystem.enableRescaleNormal();
		itemRenderer.blitOffset = 100.0F;
		RenderSystem.enableDepthTest();
		RenderSystem.disableBlend();
		RenderSystem.enableLighting();

		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 3; x++) {
				int i = x + y * 3;

				if (clientIngredients[i] != menu.entity.ingredients[i]) {
					clientIngredients[i] = menu.entity.ingredients[i];
					clientItemStacks[i] = clientIngredients[i].getItems();
				}

				if (clientItemStacks[i].length > 0 && menu.entity.inputItems[i].isEmpty()) {
					int ix = leftPos + 44 + x * 18;
					int iy = topPos + 17 + y * 18;

					itemRenderer.renderAndDecorateItem(clientItemStacks[i][(int) ((System.currentTimeMillis() / 1000L) % clientItemStacks[i].length)], ix, iy);

					RenderSystem.disableLighting();
					RenderSystem.enableBlend();
					RenderSystem.disableDepthTest();
					RenderSystem.disableTexture();
					fillGradient(poseStack, ix, iy, ix + 16, iy + 16, 0x8B8B8B8B, 0x8B8B8B8B);
					RenderSystem.enableTexture();
					RenderSystem.enableDepthTest();
					RenderSystem.disableBlend();
					RenderSystem.enableLighting();
				}
			}
		}

		itemRenderer.blitOffset = 0.0F;
		Lighting.turnOff();

		drawProgressBar(poseStack, leftPos + 110, topPos + 58, menu.data.get(SyncedData.BAR));
	}
}
