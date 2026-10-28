package dev.ftb.mods.ftbic.datagen;

import dev.ftb.mods.ftbic.FTBIC;
import java.util.List;
import java.util.function.BiConsumer;
import net.minecraft.client.data.models.EquipmentAssetProvider;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;

public class FTBICEquipmentProvider extends EquipmentAssetProvider {
    public FTBICEquipmentProvider(PackOutput output) {
        super(output);
    }

    @Override
    protected void registerModels(BiConsumer<ResourceKey<EquipmentAsset>, EquipmentClientInfo> output) {
        for (String armor : List.of("carbon", "quantum")) {
            output.accept(
                    ResourceKey.create(EquipmentAssets.ROOT_ID, FTBIC.id(armor)),
                    EquipmentClientInfo.builder()
                            .addLayers(
                                    EquipmentClientInfo.LayerType.HUMANOID,
                                    new EquipmentClientInfo.Layer(FTBIC.id(armor)))
                            .addLayers(
                                    EquipmentClientInfo.LayerType.HUMANOID_LEGGINGS,
                                    new EquipmentClientInfo.Layer(FTBIC.id(armor)))
                            .build());
        }
        output.accept(
                ResourceKey.create(EquipmentAssets.ROOT_ID, FTBIC.id("mechanical_elytra")),
                EquipmentClientInfo.builder()
                        .addLayers(
                                EquipmentClientInfo.LayerType.WINGS,
                                new EquipmentClientInfo.Layer(FTBIC.id("mechanical_elytra")))
                        .build());
    }
}
