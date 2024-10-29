package de.tomalbrc.heatwave.util;

import de.tomalbrc.heatwave.Heatwave;
import de.tomalbrc.heatwave.component.ParticleComponents;
import de.tomalbrc.heatwave.io.ParticleEffectFile;
import eu.pb4.polymer.resourcepack.api.PolymerModelData;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import eu.pb4.polymer.resourcepack.api.ResourcePackBuilder;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

public class ParticleModels {
    private static final Map<ParticleEffectFile, PolymerModelData> POLYMER_MODEL_DATA = new Object2ObjectOpenHashMap<>();

    private static final Map<String, byte[]> DATA = new Object2ObjectOpenHashMap<>();

    public static PolymerModelData modelData(ParticleEffectFile effectFile) {
        return POLYMER_MODEL_DATA.get(effectFile);
    }

    public static void addToResourcePack(ResourcePackBuilder builder) {
        for (Map.Entry<String, byte[]> entry : DATA.entrySet()) {
            builder.addData("assets/heatwave/" + entry.getKey(), entry.getValue());
        }
        DATA.clear();
    }

    public static void addFrom(ParticleEffectFile effectFile) throws IOException {
        var billboard = effectFile.effect.components.get(ParticleComponents.PARTICLE_APPEARANCE_BILLBOARD);
        if (billboard != null && billboard.uv.uv != null) {
            InputStream resource = Heatwave.class.getResourceAsStream("/textures/particle/particles.png");
            assert resource != null;

            boolean texel = billboard.uv.textureWidth != 1 && billboard.uv.textureHeight != 1;
            float xs = texel ? 1.f : billboard.uv.textureWidth;
            float ys = texel ? 1.f : billboard.uv.textureHeight;

            BufferedImage image = ImageIO.read(resource);
            BufferedImage newImage = !billboard.uv.uv.get(0).isConstant() ? image : image.getSubimage(
                    (int) (billboard.uv.uv.get(0).getConstant() * xs),
                    (int) (billboard.uv.uv.get(1).getConstant() * ys),
                    (int) (billboard.uv.uvSize.get(0).getConstant() * xs),
                    (int) (billboard.uv.uvSize.get(1).getConstant() * ys));

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            if (ImageIO.write(newImage, "png", out)) {
                UUID id = UUID.randomUUID();
                String texturePath = "textures/item/" + id + ".png";
                String modelPath = "models/item/" + id + ".json";
                DATA.put(texturePath, out.toByteArray());
                DATA.put(modelPath, getModel("heatwave:item/" + id));
                POLYMER_MODEL_DATA.put(effectFile, PolymerResourcePackUtils.requestModel(Items.LEATHER_HORSE_ARMOR, ResourceLocation.fromNamespaceAndPath(Heatwave.MOD_ID, "item/" + id)));
            }
        }
    }

    private static byte[] getModel(String texturePath) {
        try (var inputStream = Heatwave.class.getResourceAsStream("/template_model.json")) {
            assert inputStream != null;

            String jsonString = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            jsonString = jsonString.replace("@template", texturePath);
            return jsonString.getBytes(StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
