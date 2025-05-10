package de.tomalbrc.sandstorm.io;

import com.google.gson.*;
import de.tomalbrc.sandstorm.Sandstorm;
import de.tomalbrc.sandstorm.component.ParticleComponentMap;
import de.tomalbrc.sandstorm.component.emitter.EmitterShapeBox;
import de.tomalbrc.sandstorm.component.emitter.EmitterShapeDisc;
import de.tomalbrc.sandstorm.component.emitter.EmitterShapeSphere;
import de.tomalbrc.sandstorm.component.misc.ColorConfig;
import de.tomalbrc.sandstorm.component.particle.ParticleAppearanceTinting;
import de.tomalbrc.sandstorm.component.particle.ParticleExpireIfInBlocks;
import de.tomalbrc.sandstorm.component.particle.ParticleExpireIfNotInBlocks;
import de.tomalbrc.sandstorm.curve.BezierCurve;
import de.tomalbrc.sandstorm.curve.CatmullRomCurve;
import de.tomalbrc.sandstorm.curve.Curve;
import de.tomalbrc.sandstorm.curve.LinearCurve;
import gg.moonflower.molangcompiler.api.MolangExpression;
import gg.moonflower.molangcompiler.api.exception.MolangSyntaxException;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.Block;

import javax.naming.spi.ResolveResult;
import java.lang.reflect.Type;

public class Json {
    public static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeHierarchyAdapter(ResourceLocation.class, new ResourceLocationSerializer())
            .registerTypeHierarchyAdapter(ItemDisplayContext.class, new ItemDisplayContextDeserializer())
            .registerTypeHierarchyAdapter(Block.class, new RegistryDeserializer<>(BuiltInRegistries.BLOCK))
            .registerTypeHierarchyAdapter(Item.class, new RegistryDeserializer<>(BuiltInRegistries.ITEM))
            .registerTypeHierarchyAdapter(SoundEvent.class, new RegistryDeserializer<>(BuiltInRegistries.SOUND_EVENT))
            .registerTypeHierarchyAdapter(ParticleComponentMap.class, new ParticleComponentMap.Deserializer())
            .registerTypeHierarchyAdapter(MolangExpression.class, new MolangExpressionDeserializer())
            .registerTypeAdapter(ParticleExpireIfNotInBlocks.class, new ParticleExpireIfNotInBlocks.Deserializer())
            .registerTypeAdapter(ParticleExpireIfInBlocks.class, new ParticleExpireIfInBlocks.Deserializer())
            .registerTypeAdapter(ParticleAppearanceTinting.class, new ParticleAppearanceTinting.Deserializer())
            .registerTypeAdapter(EmitterShapeSphere.class, new EmitterShapeSphere.Deserializer())
            .registerTypeAdapter(EmitterShapeBox.class, new EmitterShapeBox.Deserializer())
            .registerTypeAdapter(EmitterShapeDisc.class, new EmitterShapeDisc.Deserializer())
            .registerTypeAdapter(Curve.class, new CurveDeserializer())
            .registerTypeAdapter(ColorConfig.class, new ColorConfig.Deserializer())
            .registerTypeAdapter(String[].class, new StringArrayDeserializer())
            .create();

    public static class StringArrayDeserializer implements JsonDeserializer<String[]> {
        @Override
        public String[] deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (json.isJsonArray()) {
                return context.deserialize(json, String[].class);
            } else if (json.isJsonPrimitive() && json.getAsJsonPrimitive().isString()) {
                return new String[]{json.getAsString()};
            } else {
                throw new JsonParseException("Unexpected type for creation_event field");
            }
        }
    }

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
                return Sandstorm.MOLANG.compile(json.getAsString());
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
            return this.registry.get(ResourceLocation.parse(element.getAsString())).orElseThrow().value();
        }
    }

    private static class ResourceLocationSerializer implements JsonDeserializer<ResourceLocation>, JsonSerializer<ResourceLocation> {
        public ResourceLocation deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return ResourceLocation.parse(GsonHelper.convertToString(jsonElement, "location"));
        }

        public JsonElement serialize(ResourceLocation identifier, Type type, JsonSerializationContext jsonSerializationContext) {
            return new JsonPrimitive(identifier.toString());
        }
    }
}
