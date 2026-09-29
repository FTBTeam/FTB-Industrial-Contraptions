package dev.ftb.mods.ftbic.integration.guideme;

import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.util.EnergyDisplay;
import guideme.compiler.PageCompiler;
import guideme.compiler.tags.FlowTagCompiler;
import guideme.document.flow.LytFlowParent;
import guideme.libs.mdast.mdx.model.MdxJsxElementFields;
import java.util.Set;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.common.ModConfigSpec;

public class EnergyTagCompiler extends FlowTagCompiler {
    @Override
    public Set<String> getTagNames() {
        return Set.of("Energy", "EnergyUnit");
    }

    @Override
    protected void compile(PageCompiler compiler, LytFlowParent parent, MdxJsxElementFields el) {
        boolean rate = "true".equals(el.getAttributeString("rate", "false"));
        if (el.name().equals("EnergyUnit")) {
            parent.appendText(EnergyDisplay.isFE() ? (rate ? "FE/t" : "FE") : (rate ? "z/t" : "zaps"));
            return;
        }

        Double zaps = resolve(el);
        if (zaps == null) {
            parent.appendError(compiler, "Energy needs a numeric 'zaps' attribute or a valid 'config' path", el);
            return;
        }
        Double times = parse(el.getAttributeString("times", "1"));
        if (times == null) {
            parent.appendError(compiler, "Energy 'times' must be a number", el);
            return;
        }
        double value = zaps * times;
        Component text = rate ? EnergyDisplay.perTick(value) : EnergyDisplay.amount(value);
        parent.appendText(text.getString());
    }

    private static Double resolve(MdxJsxElementFields el) {
        String config = el.getAttributeString("config", null);
        if (config != null) {
            Object value = FTBICConfig.COMMON_SPEC.getValues().get(config);
            return value instanceof ModConfigSpec.ConfigValue<?> configValue
                            && configValue.get() instanceof Number number
                    ? number.doubleValue()
                    : null;
        }
        return parse(el.getAttributeString("zaps", null));
    }

    private static Double parse(String value) {
        if (value == null) return null;
        try {
            double parsed = Double.parseDouble(value.replace(",", ""));
            return Double.isFinite(parsed) ? parsed : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
