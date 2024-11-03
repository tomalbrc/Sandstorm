package de.tomalbrc.heatwave.util;

import de.tomalbrc.heatwave.Heatwave;
import de.tomalbrc.heatwave.component.ParticleComponents;
import de.tomalbrc.heatwave.component.particle.ParticleAppearanceBillboard;
import de.tomalbrc.heatwave.io.ParticleEffectFile;
import eu.pb4.polymer.resourcepack.api.PolymerModelData;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import eu.pb4.polymer.resourcepack.api.ResourcePackBuilder;
import gg.moonflower.molangcompiler.api.MolangEnvironment;
import gg.moonflower.molangcompiler.api.MolangRuntime;
import gg.moonflower.molangcompiler.api.exception.MolangRuntimeException;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntSet;
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
    private static final Map<ParticleEffectFile, Int2ObjectArrayMap<PolymerModelData>> POLYMER_MODEL_DATA = new Object2ObjectOpenHashMap<>();

    private static final Map<String, byte[]> DATA = new Object2ObjectOpenHashMap<>();

    public static PolymerModelData modelData(ParticleEffectFile effectFile, float normalizedLifetime, MolangEnvironment environment) throws MolangRuntimeException {
        var billboard = effectFile.effect.components.get(ParticleComponents.PARTICLE_APPEARANCE_BILLBOARD);
        if (billboard != null && billboard.uv != null && billboard.uv.flipbook != null && billboard.uv.flipbook.stretch_to_lifetime) {
            var max = environment.resolve(billboard.uv.flipbook.max_frame);
            return POLYMER_MODEL_DATA.get(effectFile).get(Math.min((int)(normalizedLifetime * max), POLYMER_MODEL_DATA.get(effectFile).size()-1));
        }

        var map = POLYMER_MODEL_DATA.get(effectFile);
        return map.get((int) (Math.random() * (map.size()-1)));
    }

    public static void addToResourcePack(ResourcePackBuilder builder) {
        for (Map.Entry<String, byte[]> entry : DATA.entrySet()) {
            builder.addData("assets/heatwave/" + entry.getKey(), entry.getValue());
        }
        DATA.clear();
    }

    public static void addFrom(ParticleEffectFile effectFile) throws IOException, MolangRuntimeException {
        var billboard = effectFile.effect.components.get(ParticleComponents.PARTICLE_APPEARANCE_BILLBOARD);
        handleUV(effectFile, billboard);
        handleLifetimeFlipbook(effectFile, billboard);
    }

    private static void handleUV(ParticleEffectFile effectFile, ParticleAppearanceBillboard billboard) throws IOException, MolangRuntimeException {
        Int2ObjectArrayMap<PolymerModelData> map = new Int2ObjectArrayMap<>();
        for (int i = 0; i < 10; i++) {
            var builder = MolangRuntime.runtime();
            builder.setVariable("particle_random_1", (float) Math.random());
            builder.setVariable("particle_random_2", (float) Math.random());
            builder.setVariable("particle_random_3", (float) Math.random());
            builder.setVariable("particle_random_4", (float) Math.random());
            var runtime = builder.create();

            IntSet xSet = new IntArraySet();
            IntSet ySet = new IntArraySet();
            IntSet wSet = new IntArraySet();
            IntSet hSet = new IntArraySet();
            if (billboard != null && billboard.uv.uv != null) {
                var path = effectFile.effect.description.renderParameters.get("texture");
                InputStream resource = Heatwave.class.getResourceAsStream("/" + path + ".png");
                assert resource != null;

                boolean texel = billboard.uv.textureWidth != 1 && billboard.uv.textureHeight != 1;
                float xs = texel ? 1.f : billboard.uv.textureWidth;
                float ys = texel ? 1.f : billboard.uv.textureHeight;

                var x = (int) (runtime.resolve(billboard.uv.uv[0]) * xs);
                var y = (int) (runtime.resolve(billboard.uv.uv[1]) * ys);
                var w = (int) (runtime.resolve(billboard.uv.uvSize[0]) * xs);
                var h = (int) (runtime.resolve(billboard.uv.uvSize[1]) * ys);

                if (xSet.contains(x) && ySet.contains(y) && wSet.contains(w) && hSet.contains(h))
                    continue;

                xSet.add(x);
                ySet.add(y);
                wSet.add(w);
                hSet.add(h);

                BufferedImage image = ImageIO.read(resource);
                BufferedImage newImage = image.getSubimage(
                        x,
                        y,
                        w,
                        h
                );

                ByteArrayOutputStream out = new ByteArrayOutputStream();
                if (ImageIO.write(newImage, "png", out)) {
                    UUID id = UUID.randomUUID();
                    String texturePath = "textures/item/" + id + ".png";
                    String modelPath = "models/item/" + id + ".json";
                    DATA.put(texturePath, out.toByteArray());
                    DATA.put(modelPath, getModel("heatwave:item/" + id));

                    map.put(i, PolymerResourcePackUtils.requestModel(Items.LEATHER_HORSE_ARMOR, ResourceLocation.fromNamespaceAndPath(Heatwave.MOD_ID, "item/" + id)));
                }
            }
        }
        POLYMER_MODEL_DATA.put(effectFile, map);

    }

    private static void handleLifetimeFlipbook(ParticleEffectFile effectFile, ParticleAppearanceBillboard billboard) throws IOException {
        if (billboard != null && billboard.uv != null && billboard.uv.flipbook != null) {
            var path = effectFile.effect.description.renderParameters.get("texture");
            InputStream resource = Heatwave.class.getResourceAsStream("/" + path + ".png");
            assert resource != null;


            BufferedImage image = ImageIO.read(resource);

            boolean normalized = billboard.uv.textureWidth == 1 || billboard.uv.textureHeight == 1;
            float xs = !normalized ? 1.f : (float) image.getWidth() / billboard.uv.textureWidth;
            float ys = !normalized ? 1.f : (float) image.getHeight() / billboard.uv.textureHeight;

            ParticleAppearanceBillboard.Flipbook flipbook = billboard.uv.flipbook;
            int maxFrame = (int) flipbook.max_frame.getConstant();

            Int2ObjectArrayMap<PolymerModelData> map = new Int2ObjectArrayMap<>();
            for (int i = 0; i < maxFrame; i++) {
                var sub = image.getSubimage(
                        (int) ((flipbook.base_UV[0].getConstant() + i*flipbook.step_UV[0]) * xs),
                        (int) ((flipbook.base_UV[1].getConstant() + i*flipbook.step_UV[1]) * ys),
                        (int) ((flipbook.size_UV[0]) * xs),
                        (int) ((flipbook.size_UV[1]) * ys)
                );

                ByteArrayOutputStream out = new ByteArrayOutputStream();
                if (ImageIO.write(sub, "png", out)) {
                    UUID id = UUID.randomUUID();
                    String texturePath = "textures/item/" + id + ".png";
                    String modelPath = "models/item/" + id + ".json";
                    DATA.put(texturePath, out.toByteArray());
                    DATA.put(modelPath, getModel("heatwave:item/" + id));

                    map.put(i, PolymerResourcePackUtils.requestModel(Items.LEATHER_HORSE_ARMOR, ResourceLocation.fromNamespaceAndPath(Heatwave.MOD_ID, "item/" + id)));
                }
            }
            POLYMER_MODEL_DATA.put(effectFile, map);
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
