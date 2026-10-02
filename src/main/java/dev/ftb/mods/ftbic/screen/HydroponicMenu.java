package dev.ftb.mods.ftbic.screen;

import dev.ftb.mods.ftbic.block.entity.ElectricBlockEntity;
import dev.ftb.mods.ftbic.block.entity.machine.HydroponicBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;

public final class HydroponicMenu extends ElectricBlockMenu {
    public static final int WIDTH = 214;

    public boolean isAdvanced() {
        return blockEntity instanceof HydroponicBlockEntity machine && machine.isAdvanced();
    }

    public int laneY(int lane) {
        return isAdvanced() ? 35 + lane * 22 : 53;
    }

    public int inventoryY() {
        return isAdvanced() ? 156 : 122;
    }

    public int hotbarY() {
        return inventoryY() + 58;
    }

    public int screenHeight() {
        return hotbarY() + 24;
    }

    private final DataSlot mode = DataSlot.standalone();
    private final DataSlot[] laneProgress = new DataSlot[4];
    private final DataSlot[] laneDuration = new DataSlot[4];
    private final DataSlot[] laneOperations = new DataSlot[4];

    public HydroponicMenu(int id, Inventory inv, FriendlyByteBuf buf) {
        super(FTBICMenus.HYDROPONIC.get(), id, inv, buf);
        initData();
    }

    public HydroponicMenu(int id, Inventory inv, ElectricBlockEntity be) {
        super(FTBICMenus.HYDROPONIC.get(), id, inv, be);
        initData();
    }

    private void initData() {
        addDataSlot(mode);
        for (int i = 0; i < 4; i++) {
            laneProgress[i] = DataSlot.standalone();
            laneDuration[i] = DataSlot.standalone();
            laneOperations[i] = DataSlot.standalone();
            addDataSlot(laneProgress[i]);
            addDataSlot(laneDuration[i]);
            addDataSlot(laneOperations[i]);
        }
    }

    @Override
    protected void addMachineSlots(Inventory inv) {
        if (!(blockEntity instanceof HydroponicBlockEntity machine)) return;
        ElectricBlockEntityContainer container = new ElectricBlockEntityContainer(machine);
        if (machine.isAdvanced()) {
            for (int lane = 0; lane < 4; lane++) {
                int y = laneY(lane);
                addSlot(new InputSlot(container, lane * 2, 48, y));
                addSlot(new InputSlot(container, lane * 2 + 1, 66, y));
            }
            for (int lane = 0; lane < 4; lane++) {
                int y = laneY(lane);
                for (int kind = 0; kind < 3; kind++)
                    addSlot(new OutputSlot(container, 8 + lane * 3 + kind, 122 + kind * 18, y));
            }
            machineSlotCount = 20;
        } else {
            addSlot(new InputSlot(container, 0, 48, laneY(0)));
            addSlot(new InputSlot(container, 1, 66, laneY(0)));
            for (int i = 0; i < 3; i++) addSlot(new OutputSlot(container, 2 + i, 122 + i * 18, laneY(0)));
            machineSlotCount = 5;
        }
        if (machine.isAdvanced()) {
            addBatterySlot(188, 106);
            addUpgradeSlots(188, 30);
        } else {
            addBatterySlot(44, 85);
            UpgradeInventoryContainer upgrades = new UpgradeInventoryContainer(machine.upgradeInventory);
            for (int i = 0; i < 4; i++) {
                addSlot(new UpgradeSlot(upgrades, i, 106 + i * 18, 85));
                machineSlotCount++;
            }
        }
    }

    @Override
    protected void addPlayerInventorySlots(Inventory inv) {
        int inventoryY = inventoryY();
        int hotbarY = hotbarY();
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++)
                addSlot(new Slot(inv, col + row * 9 + 9, 26 + col * 18, inventoryY + row * 18));
        }
        for (int col = 0; col < 9; col++) addSlot(new Slot(inv, col, 26 + col * 18, hotbarY));
    }

    @Override
    public void broadcastChanges() {
        if (blockEntity instanceof HydroponicBlockEntity machine) {
            mode.set(machine.isMutationMode() ? 1 : 0);
            for (int i = 0; i < 4; i++) {
                int lane = machine.isMutationMode() && machine.isAdvanced() ? i / 2 * 2 : i;
                laneProgress[i].set(machine.getProgress(lane));
                laneDuration[i].set(machine.getDuration(lane));
                laneOperations[i].set(machine.getProgress(lane) > 0 ? machine.getOperations(lane) : 0);
            }
        }
        super.broadcastChanges();
    }

    public boolean mutationMode() {
        return mode.get() != 0;
    }

    public int laneOperations(int lane) {
        return laneOperations[lane].get();
    }

    public float laneFraction(int lane) {
        return laneDuration[lane].get() <= 0
                ? 0
                : Math.min(1F, laneProgress[lane].get() / (float) laneDuration[lane].get());
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (id != 0
                || !(blockEntity instanceof HydroponicBlockEntity machine)
                || !machine.isAdvanced()
                || !stillValid(player)
                || !player.mayBuild()
                || player.level() != machine.getLevel()
                || player.level().getBlockEntity(machine.getBlockPos()) != machine
                || !player.level().mayInteract(player, machine.getBlockPos())
                || player.distanceToSqr(machine.getBlockPos().getCenter()) > 64) return false;
        machine.setMutationMode(!machine.isMutationMode());
        return true;
    }
}
