package dev.ftb.mods.industrialcontraptions.registry;

import dev.ftb.mods.industrialcontraptions.IC;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class LegacyRegistryAliases {
	public static final String LEGACY_MOD_ID = "ftbic";

	private LegacyRegistryAliases() {}

	public static void apply(DeferredRegister<?>... registries) {
		for (DeferredRegister<?> registry : registries) {
			aliasAll(registry);
		}
	}

	private static <T> void aliasAll(DeferredRegister<T> registry) {
		for (DeferredHolder<T, ? extends T> holder : registry.getEntries()) {
			Identifier id = holder.getId();

			if (IC.MOD_ID.equals(id.getNamespace())) {
				registry.addAlias(Identifier.fromNamespaceAndPath(LEGACY_MOD_ID, id.getPath()), id);
			}
		}
	}
}
