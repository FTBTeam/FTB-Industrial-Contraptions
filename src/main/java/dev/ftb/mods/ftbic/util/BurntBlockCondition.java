package dev.ftb.mods.ftbic.util;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import dev.ftb.mods.ftbic.block.entity.ElectricBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

public class BurntBlockCondition implements LootItemCondition {
	@Override
	public LootItemConditionType getType() {
		return FTBICUtils.BURNT_BLOCK;
	}

	@Override
	public boolean test(LootContext ctx) {
		BlockEntity entity = ctx.getParamOrNull(LootContextParams.BLOCK_ENTITY);
		return entity instanceof ElectricBlockEntity && ((ElectricBlockEntity) entity).isBurnt();
	}

	public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<BurntBlockCondition> {
		@Override
		public void serialize(JsonObject json, BurntBlockCondition condition, JsonSerializationContext ctx) {
		}

		@Override
		public BurntBlockCondition deserialize(JsonObject json, JsonDeserializationContext ctx) {
			return new BurntBlockCondition();
		}
	}

	public static class Builder implements LootItemCondition.Builder {
		@Override
		public LootItemCondition build() {
			return new BurntBlockCondition();
		}
	}
}
