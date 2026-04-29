package dev.ftb.mods.ftbic.integration.guideme;

import guideme.GuidesCommon;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.neoforged.fml.ModList;

public class FTBICGuideItem extends Item {
	public FTBICGuideItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		if (level.isClientSide() && ModList.get().isLoaded("guideme")) {
			GuidesCommon.openGuide(player, FTBICGuide.GUIDE_ID);
			return InteractionResult.CONSUME;
		}
		return InteractionResult.SUCCESS;
	}
}
