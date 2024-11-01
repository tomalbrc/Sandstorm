package de.tomalbrc.heatwave.io;

import com.google.gson.*;
import de.tomalbrc.heatwave.Heatwave;
import de.tomalbrc.heatwave.component.ParticleComponentMap;
import de.tomalbrc.heatwave.component.emitter.EmitterShapeBox;
import de.tomalbrc.heatwave.component.emitter.EmitterShapeDisc;
import de.tomalbrc.heatwave.component.emitter.EmitterShapeSphere;
import de.tomalbrc.heatwave.component.particle.ParticleAppearanceTinting;
import de.tomalbrc.heatwave.curve.BezierCurve;
import de.tomalbrc.heatwave.curve.CatmullRomCurve;
import de.tomalbrc.heatwave.curve.Curve;
import de.tomalbrc.heatwave.curve.LinearCurve;
import gg.moonflower.molangcompiler.api.MolangExpression;
import gg.moonflower.molangcompiler.api.exception.MolangSyntaxException;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.Block;

import java.lang.reflect.Type;

public class Json {
    public static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeHierarchyAdapter(ResourceLocation.class, new ResourceLocation.Serializer())
            .registerTypeHierarchyAdapter(ItemDisplayContext.class, new ItemDisplayContextDeserializer())
            .registerTypeHierarchyAdapter(Block.class, new RegistryDeserializer<>(BuiltInRegistries.BLOCK))
            .registerTypeHierarchyAdapter(Item.class, new RegistryDeserializer<>(BuiltInRegistries.ITEM))
            .registerTypeHierarchyAdapter(SoundEvent.class, new RegistryDeserializer<>(BuiltInRegistries.SOUND_EVENT))
            .registerTypeHierarchyAdapter(ParticleComponentMap.class, new ParticleComponentMap.Deserializer())
            .registerTypeHierarchyAdapter(MolangExpression.class, new MolangExpressionDeserializer())
            .registerTypeAdapter(ParticleAppearanceTinting.class, new ParticleAppearanceTinting.Deserializer())
            .registerTypeAdapter(EmitterShapeSphere.class, new EmitterShapeSphere.Deserializer())
            .registerTypeAdapter(EmitterShapeBox.class, new EmitterShapeBox.Deserializer())
            .registerTypeAdapter(EmitterShapeDisc.class, new EmitterShapeDisc.Deserializer())
            .registerTypeAdapter(Curve.class, new CurveDeserializer())
            .create();

    public static class CurveDeserializer implements JsonDeserializer<Curve> {
        @Override
        public Curve deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (json.isJsonObject()) {
                var typeElement = json.getAsJsonObject().get("type");
                if (typeElement != null && typeElement.isJsonPrimitive() && typeElement.getAsJsonPrimitive().isString()) {
                    String type = typeElement.getAsJsonPrimitive().getAsString();
                    switch (type) {
                        case "linear":
                            return context.deserialize(json, LinearCurve.class);
                        case "catmull_rom":
                            return context.deserialize(json, CatmullRomCurve.class);
                        case "bezier":
                            return context.deserialize(json, BezierCurve.class);
                        case "bezier_chain":
                            throw new UnsupportedOperationException("not implemented");
                    }
                }
            }
            return null;
        }
    }

    public static class MolangExpressionDeserializer implements JsonDeserializer<MolangExpression> {
        @Override
        public MolangExpression deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            try {
                return Heatwave.MOLANG.compile(json.getAsString());
            } catch (MolangSyntaxException e) {
                throw new JsonParseException(e);
            }
        }
    }

    private static class ItemDisplayContextDeserializer implements JsonDeserializer<ItemDisplayContext> {
        @Override
        public ItemDisplayContext deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
            if (!element.isJsonPrimitive() || !element.getAsJsonPrimitive().isString()) {
                throw new JsonParseException("Expected string, got " + element);
            }

            String value = element.getAsString().toUpperCase();
            try {
                return ItemDisplayContext.valueOf(value);
            } catch (IllegalArgumentException e) {
                throw new JsonParseException("Invalid ItemDisplayContext value: " + value, e);
            }
        }
    }

    private record RegistryDeserializer<T>(Registry<T> registry) implements JsonDeserializer<T> {
        @Override
        public T deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
            return this.registry.get(ResourceLocation.parse(element.getAsString()));
        }
    }
}
