package dev.ftb.mods.ftbic.recipe;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;

public abstract class MachineRecipe implements Recipe<NoContainer> {
	public final ResourceLocation id;

	public MachineRecipe(ResourceLocation id) {
		this.id = id;
	}

	@Override
	public boolean matches(NoContainer container, Level level) {
		return true;
	}

	@Override
	public ItemStack assemble(NoContainer container) {
		return ItemStack.EMPTY;
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return true;
	}

	@Override
	public ItemStack getResultItem() {
		return ItemStack.EMPTY;
	}

	@Override
	public ResourceLocation getId() {
		return id;
	}

	public void parse(JsonObject json) {
	}

	public void read(FriendlyByteBuf buf) {
	}

	public void write(FriendlyByteBuf buf) {
	}
}
