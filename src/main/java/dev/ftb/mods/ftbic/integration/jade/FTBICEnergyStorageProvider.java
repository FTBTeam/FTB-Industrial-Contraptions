package dev.ftb.mods.ftbic.integration.jade;

import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.block.entity.ElectricBlockEntity;
import dev.ftb.mods.ftbic.block.entity.storage.BankCellBlockEntity;
import dev.ftb.mods.ftbic.block.entity.storage.BankPortBlockEntity;
import dev.ftb.mods.ftbic.block.entity.storage.BankTopology;
import dev.ftb.mods.ftbic.util.EnergyDisplay;
import dev.ftb.mods.ftbic.util.ZapFEConversion;
import java.util.List;
import net.minecraft.resources.Identifier;
import snownee.jade.api.Accessor;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.view.ClientViewGroup;
import snownee.jade.api.view.EnergyView;
import snownee.jade.api.view.IClientExtensionProvider;
import snownee.jade.api.view.IServerExtensionProvider;
import snownee.jade.api.view.ViewGroup;

public final class FTBICEnergyStorageProvider
        implements IServerExtensionProvider<EnergyView.Data>, IClientExtensionProvider<EnergyView.Data, EnergyView> {
    public static final FTBICEnergyStorageProvider INSTANCE = new FTBICEnergyStorageProvider();
    private static final Identifier UID = FTBIC.id("bank_energy_storage");

    @Override
    public Identifier getUid() {
        return UID;
    }

    @Override
    public List<ViewGroup<EnergyView.Data>> getGroups(Accessor<?> accessor) {
        if (!(accessor instanceof BlockAccessor block) || !(block.getBlockEntity() instanceof ElectricBlockEntity be)) {
            return null;
        }
        if (!(be instanceof BankCellBlockEntity || be instanceof BankPortBlockEntity)) {
            return List.of();
        }
        BankTopology.Snapshot bank = BankTopology.snapshot(block.getLevel(), block.getPosition());
        if (bank.capacity() <= 0D) return List.of();
        double factor = FTBICConfig.ENERGY.FULL_FE_MODE.get() ? ZapFEConversion.rate() : 1D;
        return List.of(new ViewGroup<>(
                List.of(new EnergyView.Data((long) (bank.stored() * factor), (long) (bank.capacity() * factor)))));
    }

    @Override
    public List<ClientViewGroup<EnergyView>> getClientGroups(
            Accessor<?> accessor, List<ViewGroup<EnergyView.Data>> groups) {
        String unit = EnergyDisplay.isFE() ? "FE" : "zaps";
        return groups.stream()
                .map(group -> new ClientViewGroup<>(group.views.stream()
                        .map(data -> EnergyView.read(data, unit))
                        .toList()))
                .toList();
    }
}
