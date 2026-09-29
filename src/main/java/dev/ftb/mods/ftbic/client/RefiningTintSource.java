package dev.ftb.mods.ftbic.client;

import com.mojang.serialization.MapCodec;
import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.item.RefiningItem;
import dev.ftb.mods.ftbic.material.MaterialColor;
import dev.ftb.mods.ftbic.material.RefiningCatalog;
import dev.ftb.mods.ftbic.registry.ModDataComponents;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.AddClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

@EventBusSubscriber(modid = FTBIC.MOD_ID, value = Dist.CLIENT)
public record RefiningTintSource() implements ItemTintSource {
    public static final MapCodec<RefiningTintSource> CODEC = MapCodec.unit(new RefiningTintSource());
    private static final Map<Identifier, Integer> CACHE = new HashMap<>();
    private static int revision = -1;
    private static final Map<String, Integer> KNOWN = Map.ofEntries(
            Map.entry("iron", 0xE5D3BD),
            Map.entry("copper", 0xEEA078),
            Map.entry("gold", 0xFFE35A),
            Map.entry("tin", 0xCDDBEA),
            Map.entry("lead", 0x9699D6),
            Map.entry("silver", 0xCCEBF5),
            Map.entry("nickel", 0xE0D4A1),
            Map.entry("aluminum", 0xD9E9ED),
            Map.entry("iridium", 0xD3C4EA),
            Map.entry("uranium", 0xA8E461),
            Map.entry("plutonium", 0xDA806C));

    @SubscribeEvent
    public static void register(RegisterColorHandlersEvent.ItemTintSources event) {
        event.register(FTBIC.id("refining_material"), CODEC);
    }

    @SubscribeEvent
    public static void reload(AddClientReloadListenersEvent event) {
        event.addListener(FTBIC.id("refining_colors"), (ResourceManagerReloadListener) manager -> CACHE.clear());
    }

    @SubscribeEvent
    public static void disconnect(ClientPlayerNetworkEvent.LoggingOut event) {
        ClientRecipeCache.disconnect();
    }

    @Override
    public MapCodec<? extends ItemTintSource> type() {
        return CODEC;
    }

    @Override
    public int calculate(ItemStack stack, ClientLevel level, LivingEntity owner) {
        if (revision != RefiningCatalog.revision) {
            CACHE.clear();
            revision = RefiningCatalog.revision;
        }
        Identifier material = stack.get(ModDataComponents.REFINING_MATERIAL.get());
        return material == null ? MaterialColor.FALLBACK : CACHE.computeIfAbsent(material, RefiningTintSource::color);
    }

    private static int color(Identifier id) {
        var definition = RefiningCatalog.materials().get(id);
        if (definition != null && definition.color() >= 0) return 0xFF000000 | definition.color();
        if (id.getNamespace().equals("c") && KNOWN.containsKey(id.getPath()))
            return 0xFF000000 | KNOWN.get(id.getPath());
        if (definition == null || definition.ingot().isEmpty()) return MaterialColor.FALLBACK;
        var item = BuiltInRegistries.ITEM.get(Identifier.parse(definition.ingot()));
        if (item.isEmpty() || item.get().value() instanceof RefiningItem) return MaterialColor.FALLBACK;
        Minecraft minecraft = Minecraft.getInstance();
        ItemStackRenderState renderState = new ItemStackRenderState();
        minecraft
                .getItemModelResolver()
                .updateForTopItem(
                        renderState, new ItemStack(item.get()), ItemDisplayContext.GUI, minecraft.level, null, 0);
        var material = renderState.pickParticleMaterial(RandomSource.create(0));
        if (material == null) return MaterialColor.FALLBACK;
        var sprite = material.sprite().contents();
        if (sprite.name().getPath().equals("missingno")) return MaterialColor.FALLBACK;
        var image = sprite.getOriginalImage();
        int[] pixels = new int[sprite.width() * sprite.height()];
        for (int y = 0; y < sprite.height(); y++)
            for (int x = 0; x < sprite.width(); x++) pixels[y * sprite.width() + x] = image.getPixel(x, y);
        return MaterialColor.sample(pixels);
    }
}
