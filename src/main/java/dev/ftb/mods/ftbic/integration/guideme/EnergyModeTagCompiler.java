package dev.ftb.mods.ftbic.integration.guideme;

import dev.ftb.mods.ftbic.util.EnergyDisplay;
import guideme.compiler.PageCompiler;
import guideme.compiler.TagCompiler;
import guideme.document.block.LytBlockContainer;
import guideme.document.flow.LytFlowParent;
import guideme.libs.mdast.mdx.model.MdxJsxFlowElement;
import guideme.libs.mdast.mdx.model.MdxJsxTextElement;
import java.util.Set;

public class EnergyModeTagCompiler implements TagCompiler {
    private static final String FE_MODE = "FEMode";
    private static final String ZAP_MODE = "ZapMode";

    @Override
    public Set<String> getTagNames() {
        return Set.of(FE_MODE, ZAP_MODE);
    }

    @Override
    public void compileBlockContext(PageCompiler compiler, LytBlockContainer parent, MdxJsxFlowElement el) {
        if (active(el.name())) {
            compiler.compileBlockContext(el, parent);
        }
    }

    @Override
    public void compileFlowContext(PageCompiler compiler, LytFlowParent parent, MdxJsxTextElement el) {
        if (active(el.name())) {
            compiler.compileFlowContext(el, parent);
        }
    }

    private static boolean active(String tag) {
        return tag.equals(FE_MODE) == EnergyDisplay.isFE();
    }
}
