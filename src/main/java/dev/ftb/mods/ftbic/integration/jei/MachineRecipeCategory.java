package dev.ftb.mods.ftbic.integration.jei;

import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.block.ElectricBlockInstance;
import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.recipe.FTBICRecipes;
import dev.ftb.mods.ftbic.recipe.MachineRecipe;
import dev.ftb.mods.ftbic.recipe.MachineRecipeType;
import dev.ftb.mods.ftbic.util.EnergyDisplay;
import dev.ftb.mods.ftbic.util.FTBICUtils;
import dev.ftb.mods.ftbic.util.IngredientWithCount;
import dev.ftb.mods.ftbic.util.RefiningIngredient;
import dev.ftb.mods.ftbic.util.StackWithChance;
import java.util.ArrayList;
import java.util.List;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeHolderType;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;

public class MachineRecipeCategory extends AbstractRecipeCategory<RecipeHolder<MachineRecipe>> {
    public static final int HEIGHT = 26;

    private static final int SLOT_Y = 4;

    private final ElectricBlockInstance machine;
    private final int maxInputs;
    private final int inputXEnd;
    private final int arrowX;
    private final int outputX;
    private final boolean separating;
    private final boolean fluidProcessing;
    private final boolean hydroponic;
    private final boolean mutation;
    private final int slotY;

    public MachineRecipeCategory(MachineRecipeType type, ElectricBlockInstance machine, IGuiHelper helper) {
        this(type, machine, helper, 2);
    }

    public MachineRecipeCategory(
            MachineRecipeType type, ElectricBlockInstance machine, IGuiHelper helper, int maxInputs) {
        super(
                jeiRecipeType(type),
                Component.translatable(
                        type == FTBICRecipes.HYDROPONIC_MUTATION
                                ? "ftbic.jei.hydroponic_mutation"
                                : type == FTBICRecipes.HYDROPONIC_GROWTH
                                        ? "ftbic.jei.hydroponic_growth"
                                        : "block.ftbic." + machine.id),
                helper.createDrawableItemStack(new ItemStack(machine.item.get())),
                type == FTBICRecipes.HYDROPONIC_MUTATION
                        ? 156
                        : widthFor(maxInputs)
                                + (type == FTBICRecipes.SEPARATING || type == FTBICRecipes.HYDROPONIC_GROWTH ? 36 : 0),
                type == FTBICRecipes.HYDROPONIC_MUTATION
                        ? 84
                        : type == FTBICRecipes.SEPARATING
                                        || type == FTBICRecipes.WASHING
                                        || type == FTBICRecipes.HYDROPONIC_GROWTH
                                        || type == FTBICRecipes.HYDROPONIC_MUTATION
                                ? 64
                                : HEIGHT);
        this.separating = type == FTBICRecipes.SEPARATING;
        this.hydroponic = type == FTBICRecipes.HYDROPONIC_GROWTH || type == FTBICRecipes.HYDROPONIC_MUTATION;
        this.mutation = type == FTBICRecipes.HYDROPONIC_MUTATION;
        this.fluidProcessing = separating || type == FTBICRecipes.WASHING || hydroponic;
        this.machine = machine;
        this.maxInputs = Math.max(1, maxInputs);
        this.slotY = mutation ? 18 : SLOT_Y;
        this.inputXEnd = mutation ? 24 : 22 + Math.max(0, this.maxInputs - 2) * 18;
        this.arrowX = inputXEnd + 22;
        this.outputX = mutation ? 84 : arrowX + 28;
    }

    private static int widthFor(int maxInputs) {
        return 112 + Math.max(0, maxInputs - 2) * 18;
    }

    @SuppressWarnings("unchecked")
    private static IRecipeHolderType<MachineRecipe> jeiRecipeType(MachineRecipeType type) {
        return IRecipeType.create((RecipeType<MachineRecipe>) (RecipeType<?>) type.TYPE.get());
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<MachineRecipe> holder, IFocusGroup focuses) {
        MachineRecipe recipe = holder.value();
        if (fluidProcessing) {
            for (var input : recipe.inputFluids) {
                var slot = builder.addInputSlot(mutation ? 6 : inputXEnd, mutation ? 48 : 28)
                        .setStandardSlotBackground()
                        .setFluidRenderer(input.amount(), false, 16, 16);
                for (var fluid : input.ingredient().fluids()) slot.add(fluid.value(), input.amount());
            }
            for (var output : recipe.outputFluids) {
                builder.addOutputSlot(outputX, 28)
                        .setStandardSlotBackground()
                        .setFluidRenderer(output.getAmount(), false, 16, 16)
                        .add(output.getFluid(), output.getAmount(), output.getComponentsPatch());
            }
        }

        int inputCount = Math.min(maxInputs, recipe.inputs.size());
        if (hydroponic && !mutation && !recipe.soilOptions.isEmpty()) {
            var soilSlot = builder.addInputSlot(4, 28).setStandardSlotBackground();
            for (var soil : recipe.soilOptions) soilSlot.add(soil.ingredient());
            soilSlot.addRichTooltipCallback((view, tooltip) -> {
                for (var soil : recipe.soilOptions)
                    tooltip.add(
                            Component.translatable("ftbic.hydro.soil_speed", FTBICUtils.fmtDouble(soil.speed(), 2)));
            });
        }
        int idx = 0;
        for (IngredientWithCount in : recipe.inputs) {
            if (idx >= maxInputs) break;
            int x = inputXEnd - (inputCount - 1 - idx) * 18;
            var slot = builder.addInputSlot(x, slotY).setStandardSlotBackground();
            int cnt = in.count();
            if (in.ingredient().getCustomIngredient() instanceof RefiningIngredient refining) {
                slot.add(refining.stack(cnt));
            } else if (cnt > 1) {
                List<ItemStack> stacks = new ArrayList<>();
                in.ingredient().items().forEach(h -> {
                    ItemStack stack = new ItemStack(h);
                    stack.setCount(cnt);
                    stacks.add(stack);
                });
                slot.addItemStacks(stacks);
            } else {
                slot.add(in.ingredient());
            }
            idx++;
        }

        int visibleOutputs = 0;
        for (StackWithChance out : recipe.outputs) {
            if (!out.stack().isEmpty()) visibleOutputs++;
        }
        boolean singleOutput = visibleOutputs == 1;
        int ox = outputX;
        for (StackWithChance out : recipe.outputs) {
            ItemStack stack = out.stack();
            if (stack.isEmpty()) continue;
            var slot = builder.addOutputSlot(ox, slotY);
            if (singleOutput) slot.setOutputSlotBackground();
            else slot.setStandardSlotBackground();
            slot.add(stack);
            double chance = out.chance();
            if (chance < 1.0D) {
                slot.addRichTooltipCallback((slotView, tooltip) ->
                        tooltip.add(Component.translatable("ftbic.jei.chance", FTBICUtils.fmtDouble(chance * 100D, 1))
                                .withStyle(ChatFormatting.GRAY)));
            }
            ox += 18;
        }
    }

    @Override
    public void createRecipeExtras(
            IRecipeExtrasBuilder builder, RecipeHolder<MachineRecipe> holder, IFocusGroup focuses) {
        builder.addAnimatedRecipeArrow(50).setPosition(arrowX, slotY + 1);
        if (separating && holder.value().outputs.size() > 2) {
            builder.addText(Component.translatable("ftbic.jei.advanced_centrifuge_required"), getWidth(), 12)
                    .setPosition(0, 50)
                    .setColor(0xFF404040);
        }
        if (mutation) {
            builder.addText(Component.translatable("ftbic.jei.parents"), 70, 10)
                    .setPosition(4, 2)
                    .setColor(0xFF404040);
            builder.addText(Component.translatable("ftbic.jei.mutation_result"), 70, 10)
                    .setPosition(82, 2)
                    .setColor(0xFF404040);
            if (!holder.value().outputs.isEmpty()) {
                builder.addText(
                                Component.literal(
                                        FTBICUtils.fmtDouble(
                                                        holder.value()
                                                                        .outputs
                                                                        .getFirst()
                                                                        .chance()
                                                                * 100,
                                                        1) + "%"),
                                44,
                                10)
                        .setPosition(110, 22)
                        .setColor(0xFF286C55);
            }
            builder.addText(Component.translatable("ftbic.jei.mutation_failed"), 120, 10)
                    .setPosition(32, 46)
                    .setColor(0xFF404040);
            builder.addText(Component.translatable("ftbic.jei.mutation_returns"), 120, 10)
                    .setPosition(32, 58)
                    .setColor(0xFF404040);
            double seconds = holder.value().processingTime * FTBICConfig.MACHINES.MACHINE_RECIPE_BASE_TICKS.get() / 20;
            builder.addText(
                            Component.translatable(
                                    "ftbic.jei.mutation_cost",
                                    FTBICUtils.fmtDouble(seconds, 1),
                                    EnergyDisplay.perTick(machine.energyUsage.get())),
                            150,
                            10)
                    .setPosition(4, 74)
                    .setColor(0xFF404040);
        }
    }

    @Override
    public void getTooltip(
            ITooltipBuilder tooltip,
            RecipeHolder<MachineRecipe> recipe,
            IRecipeSlotsView slots,
            double mouseX,
            double mouseY) {
        int hitX0 = inputXEnd + 18;
        int hitX1 = outputX;
        if (mouseX >= hitX0 && mouseX < hitX1 && mouseY >= slotY && mouseY < slotY + 18) {
            double baseTicks = FTBICConfig.MACHINES.MACHINE_RECIPE_BASE_TICKS.get();
            double ticks = recipe.value().processingTime * baseTicks;
            double energyPerTick = (separating && recipe.value().outputs.size() > 2
                            ? FTBICElectricBlocks.ADVANCED_CENTRIFUGE
                            : machine)
                    .energyUsage.get();
            long zaps = Math.round(ticks * energyPerTick);
            double seconds = ticks / 20.0D;
            tooltip.add(Component.translatable(
                    "ftbic.jei.recipe_time_energy", FTBICUtils.fmtDouble(seconds, 1), EnergyDisplay.amount(zaps)));
            tooltip.add(EnergyDisplay.perTick(energyPerTick).withStyle(ChatFormatting.DARK_GRAY));
        }
    }
}
