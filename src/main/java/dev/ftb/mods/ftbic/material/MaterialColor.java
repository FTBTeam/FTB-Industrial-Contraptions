package dev.ftb.mods.ftbic.material;

/** Dominant midtone sampling, independent of the client renderer for validation. */
public final class MaterialColor {
    public static final int FALLBACK = 0xFFD1D6DA;

    public static int sample(int[] pixels) {
        long[] weights = new long[512];
        long[] red = new long[512], green = new long[512], blue = new long[512];
        for (int pixel : pixels) {
            if ((pixel >>> 24) < 128) continue;
            int r = (pixel >> 16) & 255, g = (pixel >> 8) & 255, b = pixel & 255;
            int max = Math.max(r, Math.max(g, b)), min = Math.min(r, Math.min(g, b));
            if (max < 40 || min > 240) continue;
            int bucket = (r >> 5) * 64 + (g >> 5) * 8 + (b >> 5);
            int weight = 1 + (max - min) / 32;
            weights[bucket] += weight;
            red[bucket] += (long) r * weight;
            green[bucket] += (long) g * weight;
            blue[bucket] += (long) b * weight;
        }
        int best = 0;
        for (int i = 1; i < weights.length; i++) if (weights[i] > weights[best]) best = i;
        if (weights[best] == 0) return FALLBACK;
        int r = (int) (red[best] / weights[best]),
                g = (int) (green[best] / weights[best]),
                b = (int) (blue[best] / weights[best]);
        // A bright tint preserves the neutral sprite's six-step shading, including dark metals.
        double scale = Math.min(1.6, 235D / Math.max(r, Math.max(g, b)));
        return 0xFF000000 | (int) (r * scale) << 16 | (int) (g * scale) << 8 | (int) (b * scale);
    }

    private MaterialColor() {}
}
