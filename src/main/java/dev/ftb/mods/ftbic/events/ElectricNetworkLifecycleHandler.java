package dev.ftb.mods.ftbic.events;

import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.block.entity.ElectricBlockEntity;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.LevelEvent;

@EventBusSubscriber(modid = FTBIC.MOD_ID)
public final class ElectricNetworkLifecycleHandler {
	@SubscribeEvent
	public static void onLevelUnload(LevelEvent.Unload event) {
		if (event.getLevel() instanceof Level l) {
			ElectricBlockEntity.forgetElectricNetwork(l);
		}
	}

	private ElectricNetworkLifecycleHandler() {}
}
