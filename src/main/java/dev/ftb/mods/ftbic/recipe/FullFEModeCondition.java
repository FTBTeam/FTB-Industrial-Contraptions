package dev.ftb.mods.ftbic.recipe;

import com.mojang.serialization.MapCodec;
import dev.ftb.mods.ftbic.FTBICConfig;
import net.neoforged.neoforge.common.conditions.ICondition;

public enum FullFEModeCondition implements ICondition {
    INSTANCE;

    public static final MapCodec<FullFEModeCondition> CODEC = MapCodec.unit(INSTANCE);

    @Override
    public boolean test(IContext context) {
        return FTBICConfig.ENERGY.FULL_FE_MODE.get();
    }

    @Override
    public MapCodec<? extends ICondition> codec() {
        return CODEC;
    }

    @Override
    public String toString() {
        return "ftbic:full_fe_mode";
    }
}
