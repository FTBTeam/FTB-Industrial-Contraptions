package dev.ftb.mods.ftbic.screen;

import dev.ftb.mods.ftbic.FTBIC;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public interface FTBICMenus {
	DeferredRegister<MenuType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.CONTAINERS, FTBIC.MOD_ID);

	RegistryObject<MenuType<MachineMenu>> MACHINE = REGISTRY.register("machine", () -> new MenuType<>((IContainerFactory<MachineMenu>) MachineMenu::new));
}
