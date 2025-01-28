package de.tomalbrc.sandstorm.util;

import de.tomalbrc.sandstorm.Sandstorm;
import de.tomalbrc.sandstorm.component.ParticleComponents;
import de.tomalbrc.sandstorm.component.particle.ParticleAppearanceBillboard;
import de.tomalbrc.sandstorm.io.ParticleEffectFile;
import eu.pb4.polymer.resourcepack.api.ResourcePackBuilder;
import gg.moonflower.molangcompiler.api.MolangEnvironment;
import gg.moonflower.molangcompiler.api.MolangRuntime;
import gg.moonflower.molangcompiler.api.exception.MolangRuntimeException;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import org.joml.Vector2i;
import org.joml.Vector4i;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

public class ParticleModels {
    private static final Map<ParticleEffectFile, Int2ObjectArrayMap<ModelData>> POLYMER_MODEL_DATA = new Object2ObjectOpenHashMap<>();
    private static final Map<String, byte[]> DATA = new Object2ObjectOpenHashMap<>();

    public static ModelData modelData(ParticleEffectFile effectFile, int flipbookRnd, float normalizedLifetime, MolangEnvironment environment) throws MolangRuntimeException {
        var billboard = effectFile.effect.components.get(ParticleComponents.PARTICLE_APPEARANCE_BILLBOARD);
        var map = POLYMER_MODEL_DATA.get(effectFile);
        if (billboard != null && billboard.uv != null && billboard.uv.flipbook != null && billboard.uv.flipbook.stretch_to_lifetime) {
            var max = environment.resolve(billboard.uv.flipbook.max_frame);

            int rndIndex = flipbookRnd*1000;
            boolean containsRndIndex = false;
            for (Int2ObjectMap.Entry<ModelData> entry : map.int2ObjectEntrySet()) {
                if (entry.getIntKey() == rndIndex) {
                    containsRndIndex = true;
                    break;
                }
            }

            return map.get((int)((containsRndIndex ? rndIndex : 0) + Math.min(normalizedLifetime, 1.0) * (max-1)));
        }

        int idx = (int) (Math.random() * (map.keySet().size())-1);
        return map.get(map.keySet().toIntArray()[idx]);
    }

    public static ModelData modelData(ParticleEffectFile effectFile, int flipbookRnd, int index) {
        var list = POLYMER_MODEL_DATA.get(effectFile);
        int rndIndex = flipbookRnd*1000;
        boolean containsRndIndex = false;
        for (Int2ObjectMap.Entry<ModelData> entry : list.int2ObjectEntrySet()) {
            if (entry.getIntKey() == rndIndex) {
                containsRndIndex = true;
                break;
            }
        }
        return list.get((containsRndIndex ? rndIndex : 0) + index);
    }

    public static void addToResourcePack(ResourcePackBuilder builder) {
        for (Map.Entry<String, byte[]> entry : DATA.entrySet()) {
            builder.addData("assets/sandstorm/" + entry.getKey(), entry.getValue());
        }
        DATA.clear();
    }

    public static void addFrom(ParticleEffectFile effectFile, InputStream imageStream) throws IOException, MolangRuntimeException {
        var billboard = effectFile.effect.components.get(ParticleComponents.PARTICLE_APPEARANCE_BILLBOARD);
        var emissive = !effectFile.effect.components.has(ParticleComponents.PARTICLE_APPEARANCE_LIGHTING);

        BufferedImage image = ImageIO.read(imageStream);
        handleUV(effectFile, image, billboard, emissive);
        handleLifetimeFlipbook(effectFile, image, billboard, emissive);
    }

    private static void handleUV(ParticleEffectFile effectFile, BufferedImage image, ParticleAppearanceBillboard billboard, boolean emissive) throws IOException, MolangRuntimeException {
        Int2ObjectArrayMap<ModelData> map = new Int2ObjectArrayMap<>();
        if (billboard != null && billboard.uv != null) {
            ObjectOpenHashSet<Vector4i> vecs = new ObjectOpenHashSet<>();
            for (int i = 0; i < 10; i++) {
                var builder = MolangRuntime.runtime();
                builder.setVariable("particle_random_1", (float) Math.random());
                builder.setVariable("particle_random_2", (float) Math.random());
                builder.setVariable("particle_random_3", (float) Math.random());
                builder.setVariable("particle_random_4", (float) Math.random());
                var runtime = builder.create();

                if (billboard.uv.uv != null) {
                    boolean texel = billboard.uv.textureWidth != 1 && billboard.uv.textureHeight != 1;
                    float xs = texel ? 1.f : billboard.uv.textureWidth;
                    float ys = texel ? 1.f : billboard.uv.textureHeight;

                    var x = (int) (runtime.resolve(billboard.uv.uv[0]) * xs);
                    var y = (int) (runtime.resolve(billboard.uv.uv[1]) * ys);
                    var w = (int) (runtime.resolve(billboard.uv.uvSize[0]) * xs);
                    var h = (int) (runtime.resolve(billboard.uv.uvSize[1]) * ys);
                    Vector4i n = new Vector4i(x,y,w,h);
                    if (vecs.contains(n))
                        continue;

                    vecs.add(n);

                    BufferedImage newImage = image.getSubimage(x, y, w, h);
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    if (ImageIO.write(newImage, "png", out)) {
                        UUID id = UUID.randomUUID();
                        String texturePath = "textures/item/" + id + ".png";
                        String modelPath = "models/item/" + id + ".json";
                        DATA.put(texturePath, out.toByteArray());
                        DATA.put(modelPath, getModel("sandstorm:item/" + id, emissive));

                        map.put(map.size(), new ModelData(Items.LEATHER_HORSE_ARMOR, ResourceLocation.fromNamespaceAndPath(Sandstorm.MOD_ID, id.toString())));
                    }
                }
            }
            POLYMER_MODEL_DATA.put(effectFile, map);
        }
        else {
            UUID id = UUID.randomUUID();
            String texturePath = "textures/item/" + id + ".png";
            String modelPath = "models/item/" + id + ".json";

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            if (ImageIO.write(image, "png", out)) {
                DATA.put(texturePath, out.toByteArray());
                DATA.put(modelPath, getModel("sandstorm:item/" + id, emissive));

                map.put(map.size(), new ModelData(Items.LEATHER_HORSE_ARMOR, ResourceLocation.fromNamespaceAndPath(Sandstorm.MOD_ID, "item/" + id)));
                POLYMER_MODEL_DATA.put(effectFile, map);
            }
        }
    }

    private static void handleLifetimeFlipbook(ParticleEffectFile effectFile, BufferedImage image, ParticleAppearanceBillboard billboard, boolean emissive) throws IOException, MolangRuntimeException {
        if (billboard != null && billboard.uv != null && billboard.uv.flipbook != null) {
            boolean normalized = billboard.uv.textureWidth == 1 || billboard.uv.textureHeight == 1;
            float xs = !normalized ? 1.f : (float) image.getWidth() / billboard.uv.textureWidth;
            float ys = !normalized ? 1.f : (float) image.getHeight() / billboard.uv.textureHeight;

            ParticleAppearanceBillboard.Flipbook flipbook = billboard.uv.flipbook;
            int maxFrame = (int) flipbook.max_frame.getConstant();

            Int2ObjectArrayMap<ModelData> map = new Int2ObjectArrayMap<>();
            ObjectOpenHashSet<Vector2i> vecs = new ObjectOpenHashSet<>();
            int realI = 0;
            for (int randomIterator = 0; randomIterator < 10; randomIterator++) {
                var builder = MolangRuntime.runtime();
                builder.setVariable("particle_random_1", (float) Math.random());
                builder.setVariable("particle_random_2", (float) Math.random());
                builder.setVariable("particle_random_3", (float) Math.random());
                builder.setVariable("particle_random_4", (float) Math.random());
                var runtime = builder.create();

                int a = (int)runtime.resolve(flipbook.base_UV[0]);
                int b = (int)runtime.resolve(flipbook.base_UV[1]);
                Vector2i n = new Vector2i(a,b);
                if (vecs.contains(n))
                    continue;

                vecs.add(n);

                for (int currentFrame = 0; currentFrame < maxFrame; currentFrame++) {
                    var sub = image.getSubimage(
                            (int) ((a + currentFrame*flipbook.step_UV[0]) * xs),
                            (int) ((b + currentFrame*flipbook.step_UV[1]) * ys),
                            (int) ((flipbook.size_UV[0]) * xs),
                            (int) ((flipbook.size_UV[1]) * ys)
                    );

                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    if (ImageIO.write(sub, "png", out)) {
                        UUID id = UUID.randomUUID();
                        String texturePath = "textures/item/" + id + ".png";
                        String modelPath = "models/item/" + id + ".json";
                        DATA.put(texturePath, out.toByteArray());
                        DATA.put(modelPath, getModel("sandstorm:item/" + id, emissive));

                        map.put(realI*1000 + currentFrame, new ModelData(Items.LEATHER_HORSE_ARMOR, ResourceLocation.fromNamespaceAndPath(Sandstorm.MOD_ID, "item/" + id)));
                    }
                } // fori

                realI++;
            }


            POLYMER_MODEL_DATA.put(effectFile, map);
        }
    }

    private static byte[] getModel(String texturePath, boolean emissive) {
        try (var inputStream = Sandstorm.class.getResourceAsStream(emissive ? "/template_model_emissive.json" : "/template_model.json")) {
            assert inputStream != null;

            String jsonString = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            jsonString = jsonString.replace("@template", texturePath);
            return jsonString.getBytes(StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
