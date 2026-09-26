package dev.ftb.mods.ftbic.block.entity.storage;

import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.block.BatteryBankBlock;
import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.block.entity.ElectricBlockEntity;
import dev.ftb.mods.ftbic.block.entity.generator.GeneratorBlockEntity;
import dev.ftb.mods.ftbic.block.entity.machine.BatteryInventory;
import dev.ftb.mods.ftbic.screen.BankMenu;
import dev.ftb.mods.ftbic.util.SideConfiguration.Face;
import dev.ftb.mods.ftbic.util.SideConfiguration.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class BankPortBlockEntity extends GeneratorBlockEntity {
    public enum FaceStyle {
        PORT,
        GAUGE,
        BASIC;

        public FaceStyle next() {
            return values()[(ordinal() + 1) % values().length];
        }
    }

    public static final int CHARGE_SLOTS = 4;

    private int displayCharge;
    private FaceStyle faceStyle = FaceStyle.PORT;
    private long bankWalkTime = Long.MIN_VALUE;
    private long bankWalkNetwork;
    private List<ElectricBlockEntity> bankMembers = List.of();
    private List<BankCellBlockEntity> bankCells = List.of();
    public final List<BatteryInventory> chargeSlots;

    public BankPortBlockEntity(BlockPos pos, BlockState state) {
        super(FTBICElectricBlocks.INDUSTRIAL_BANK_PORT, pos, state);
        List<BatteryInventory> slots = new ArrayList<>();
        for (int i = 0; i < CHARGE_SLOTS; i++) {
            slots.add(new BatteryInventory(this, true));
        }
        chargeSlots = List.copyOf(slots);
    }

    @Override
    protected List<BatteryInventory> chargeInventories() {
        return chargeSlots;
    }

    @Override
    public void onBroken(Level level, BlockPos pos) {
        super.onBroken(level, pos);
        for (BatteryInventory slot : chargeSlots) {
            Block.popResource(level, pos, slot.getStackInSlot(0));
        }
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory) {
        return new BankMenu(id, inventory, this);
    }

    @Override
    public void initProperties() {
        super.initProperties();
        maxEnergyOutputTransfer = FTBICConfig.ENERGY.BANK_PORT_TRANSFER.get();
    }

    @Override
    public boolean keepsEnergyWhenBroken() {
        return true;
    }

    @Override
    public void handleGeneration() {
        if (level == null) return;
        walkBank();
        storeInCells(bankCells);
        // Stage bank energy for the generator's output pass this tick.
        refillFromCells(bankCells);
    }

    private void walkBank() {
        long time = level.getGameTime();
        long network = getCurrentElectricNetwork(level, worldPosition);
        if (time != bankWalkTime || network != bankWalkNetwork) {
            bankMembers = BankTopology.members(level, worldPosition);
            bankCells = BankTopology.cells(bankMembers);
            bankWalkTime = time;
            bankWalkNetwork = network;
        }
    }

    private void storeInCells(List<BankCellBlockEntity> cells) {
        double previousEnergy = energy;
        for (BankCellBlockEntity cell : cells) {
            double moved = Math.min(energy, Math.max(0D, cell.getEnergyCapacity() - cell.getEnergy()));
            if (moved > 0D) {
                energy -= moved;
                cell.setEnergyRaw(cell.getEnergy() + moved);
                cell.setChanged();
            }
        }
        if (energy != previousEnergy) setChanged();
    }

    private void refillFromCells(List<BankCellBlockEntity> cells) {
        double previousEnergy = energy;
        for (BankCellBlockEntity cell : cells) {
            double moved = Math.min(Math.max(0D, energyCapacity - energy), cell.getEnergy());
            if (moved > 0D) {
                energy += moved;
                cell.setEnergyRaw(cell.getEnergy() - moved);
                cell.setChanged();
            }
        }
        if (energy != previousEnergy) setChanged();
    }

    @Override
    public void handleEnergyOutput() {
        super.handleEnergyOutput();
        if (level != null && !level.isClientSide()) {
            // Keep the input buffer free for the next transfer; only a full bank retains port energy.
            walkBank();
            storeInCells(bankCells);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (level != null
                && !level.isClientSide()
                && (level.getGameTime() + Math.floorMod(worldPosition.hashCode(), 5)) % 5 == 0) {
            refreshChargeDisplay();
        }
    }

    public void refreshChargeDisplay() {
        if (level == null || level.isClientSide()) return;
        walkBank();
        int charge = BankTopology.chargeLevel(BankTopology.snapshot(bankMembers));
        for (ElectricBlockEntity member : bankMembers) {
            if (member instanceof BankPortBlockEntity port && port.displayCharge != charge) {
                port.displayCharge = charge;
                port.setChanged();
                BlockState state = port.getBlockState();
                level.sendBlockUpdated(port.getBlockPos(), state, state, Block.UPDATE_CLIENTS);
            }
        }
    }

    public int getDisplayCharge() {
        return displayCharge;
    }

    public FaceStyle getFaceStyle() {
        return faceStyle;
    }

    public FaceStyle cycleFaceStyle() {
        setFaceStyle(faceStyle.next());
        return faceStyle;
    }

    public void setFaceStyle(FaceStyle style) {
        if (faceStyle == style) return;
        faceStyle = style;
        setChanged();
        if (level != null && !level.isClientSide()) {
            BlockState state = getBlockState();
            level.sendBlockUpdated(worldPosition, state, state, Block.UPDATE_CLIENTS);
        }
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("DisplayCharge", displayCharge);
        output.putString("FaceStyle", faceStyle.name().toLowerCase(Locale.ROOT));
        for (int i = 0; i < chargeSlots.size(); i++) {
            ItemStack stack = chargeSlots.get(i).getStackInSlot(0);
            if (!stack.isEmpty()) {
                output.store("ChargeSlot" + i, ItemStack.CODEC, stack);
            }
        }
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        displayCharge = Math.clamp(input.getIntOr("DisplayCharge", 0), 0, 1000);
        String savedStyle = input.getStringOr("FaceStyle", "port");
        faceStyle = switch (savedStyle) {
            case "gauge" -> FaceStyle.GAUGE;
            case "basic" -> FaceStyle.BASIC;
            default -> FaceStyle.PORT;
        };
        if (!isClientSync(input)) {
            for (int i = 0; i < chargeSlots.size(); i++) {
                chargeSlots
                        .get(i)
                        .loadItem(input.read("ChargeSlot" + i, ItemStack.CODEC).orElse(ItemStack.EMPTY));
            }
        }
    }

    @Override
    public int supportedTransfers(Resource resource, Face face) {
        return resource == Resource.ENERGY ? 3 : 0;
    }

    @Override
    public boolean isValidEnergyInputSide(Direction direction) {
        return level != null
                && !(level.getBlockState(worldPosition.relative(direction)).getBlock() instanceof BatteryBankBlock)
                && allowsTransfer(Resource.ENERGY, direction, true);
    }

    @Override
    public boolean isValidEnergyOutputSide(Direction direction) {
        return level != null
                && !(level.getBlockState(worldPosition.relative(direction)).getBlock() instanceof BatteryBankBlock)
                && allowsTransfer(Resource.ENERGY, direction, false);
    }
}
