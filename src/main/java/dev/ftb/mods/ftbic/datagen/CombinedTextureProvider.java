package dev.ftb.mods.ftbic.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.util.Mth;
import net.minecraftforge.common.data.ExistingFileHelper;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author LatvianModder
 */
public abstract class CombinedTextureProvider implements DataProvider {
	private static final ExistingFileHelper.ResourceType TEXTURE = new ExistingFileHelper.ResourceType(PackType.CLIENT_RESOURCES, ".png", "textures");

	public static class TextureData {
		public final int width;
		public final int height;
		public final float[] pixels;

		public TextureData(int w, int h) {
			width = w;
			height = h;
			pixels = new float[w * h * 4];
		}

		public TextureData combine(TextureData data) {
			if (width != data.width || height != data.height) {
				throw new IllegalArgumentException("Width or height didn't match source texture!");
			}

			TextureData d = new TextureData(width, height);

			for (int i = 0; i < d.pixels.length; i += 4) {
				float a = data.pixels[i];
				d.pixels[i] = Math.min(pixels[i] + a, 1F);
				d.pixels[i + 1] = Mth.clamp(Mth.lerp(a, pixels[i + 1], data.pixels[i + 1]), 0F, 1F);
				d.pixels[i + 2] = Mth.clamp(Mth.lerp(a, pixels[i + 2], data.pixels[i + 2]), 0F, 1F);
				d.pixels[i + 3] = Mth.clamp(Mth.lerp(a, pixels[i + 3], data.pixels[i + 3]), 0F, 1F);
			}

			return d;
		}
	}

	private final DataGenerator gen;
	private final String modid;
	private final ExistingFileHelper existingFileHelper;
	private final Map<ResourceLocation, TextureData> textureCache;
	private final Map<ResourceLocation, TextureData> map;

	public CombinedTextureProvider(DataGenerator g, String mod, ExistingFileHelper efh) {
		gen = g;
		modid = mod;
		existingFileHelper = efh;
		textureCache = new HashMap<>();
		map = new HashMap<>();
	}

	public ResourceLocation modLoc(String path) {
		return new ResourceLocation(modid, path);
	}

	public TextureData load(ResourceLocation id) {
		return textureCache.computeIfAbsent(id, id0 -> {
			try (InputStream stream = existingFileHelper.getResource(id0, PackType.CLIENT_RESOURCES, ".png", "textures").getInputStream()) {
				BufferedImage img = ImageIO.read(stream);
				TextureData data = new TextureData(img.getWidth(), img.getHeight());

				for (int y = 0; y < data.height; y++) {
					for (int x = 0; x < data.width; x++) {
						int i = (x + y * data.width) * 4;
						int col = img.getRGB(x, y);
						data.pixels[i] = ((col >> 24) & 0xFF) / 255F;
						data.pixels[i + 1] = ((col >> 16) & 0xFF) / 255F;
						data.pixels[i + 2] = ((col >> 8) & 0xFF) / 255F;
						data.pixels[i + 3] = ((col >> 0) & 0xFF) / 255F;
					}
				}

				return data;
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		});
	}

	public void make(ResourceLocation path, TextureData data) {
		map.put(path, data);
	}

	public abstract void registerTextures();

	@Override
	public void run(HashCache cache) throws IOException {
		registerTextures();

		for (Map.Entry<ResourceLocation, TextureData> entry : map.entrySet()) {
			TextureData data = entry.getValue();
			BufferedImage image = new BufferedImage(data.width, data.height, BufferedImage.TYPE_INT_ARGB);

			for (int y = 0; y < data.height; y++) {
				for (int x = 0; x < data.width; x++) {
					int i = (x + y * data.width) * 4;
					int a = Mth.clamp((int) (data.pixels[i] * 255F), 0, 255);
					int r = Mth.clamp((int) (data.pixels[i + 1] * 255F), 0, 255);
					int g = Mth.clamp((int) (data.pixels[i + 2] * 255F), 0, 255);
					int b = Mth.clamp((int) (data.pixels[i + 3] * 255F), 0, 255);
					image.setRGB(x, y, (a << 24) | (r << 16) | (g << 8) | b);
				}
			}

			Path target = gen.getOutputFolder().resolve("assets/" + entry.getKey().getNamespace() + "/textures/" + entry.getKey().getPath() + ".png");

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ImageIO.write(image, "PNG", out);
			byte[] bytes = out.toByteArray();

			String hash = DataProvider.SHA1.hashBytes(bytes).toString();

			if (!Objects.equals(cache.getHash(target), hash) || !Files.exists(target)) {
				Files.createDirectories(target.getParent());

				try (OutputStream outputStream = Files.newOutputStream(target)) {
					outputStream.write(bytes);
				}
			}

			cache.putNew(target, hash);
			existingFileHelper.trackGenerated(entry.getKey(), TEXTURE);
		}
	}

	@Override
	public String getName() {
		return "CombinedTextures";
	}
}
