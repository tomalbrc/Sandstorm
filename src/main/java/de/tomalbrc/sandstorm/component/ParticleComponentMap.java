package de.tomalbrc.sandstorm.component;

import com.google.gson.*;
import de.tomalbrc.sandstorm.Sandstorm;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.resources.ResourceLocation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.function.BiConsumer;

@SuppressWarnings("unchecked")
public class ParticleComponentMap {
    private final Map<ParticleComponentType<? extends ParticleComponent<?>>, ParticleComponent<?>> componentMap = new Object2ObjectOpenHashMap<>();

    public void put(ParticleComponentType<?> type, ParticleComponent<?> component) {
        this.componentMap.put(type, component);
    }

    public <T extends ParticleComponent<?>> T get(ParticleComponentType<T> type) {
        return (T) this.componentMap.get(type);
    }

    public <T extends ParticleComponent<?>> boolean has(ParticleComponentType<T> type) {
        return this.componentMap.containsKey(type);
    }

    public void from(ParticleComponentMap componentMap) {
        if (componentMap != null) componentMap.forEach(this::put);
    }

    public <T extends ParticleComponent<?>> void set(ParticleComponentType<T> type, ParticleComponent<?> value) {
        this.componentMap.put(type, value);
    }

    public <T extends ParticleComponent<?>> void forEach(BiConsumer<ParticleComponentType<T>, ParticleComponent<?>> biConsumer) {
        for (Map.Entry<ParticleComponentType<? extends ParticleComponent<?>>, ParticleComponent<?>> entry : this.componentMap.entrySet()) {
            biConsumer.accept((ParticleComponentType<T>) entry.getKey(), entry.getValue());
        }
    }

    public boolean isEmpty() {
        return this.componentMap.isEmpty();
    }

    public static class Deserializer implements JsonDeserializer<ParticleComponentMap> {
        @Override
        public ParticleComponentMap deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject object = jsonElement.getAsJsonObject();
            ParticleComponentMap particleComponentMap = new ParticleComponentMap();
            for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
                ResourceLocation resourceLocation;
                if (entry.getKey().contains(":"))
                    resourceLocation = ResourceLocation.parse(entry.getKey());
                else
                    resourceLocation = ResourceLocation.withDefaultNamespace(entry.getKey());

                ParticleComponentType<ParticleComponent<?>> componentType = ParticleComponentRegistry.getType(resourceLocation);

                if (componentType == null) {
                    Sandstorm.LOGGER.error("Could not load particle component {}", resourceLocation);
                    continue;
                }

                ParticleComponent<?> deserialized;
                if (entry.getValue().isJsonPrimitive()) {
                    String value = entry.getValue().getAsString();
                    deserialized = createComponent(componentType, value);
                } else {
                    deserialized = jsonDeserializationContext.deserialize(entry.getValue(), componentType.type());
                }

                particleComponentMap.put(ParticleComponentRegistry.getType(resourceLocation), deserialized);
            }
            return particleComponentMap;
        }
    }

    private static <T> ParticleComponent<?> createComponent(ParticleComponentType<ParticleComponent<?>> componentType, T value) {
        Class<? extends ParticleComponent<?>> clazz = componentType.type();
        try {
            Constructor<? extends ParticleComponent<?>> constructor = clazz.getConstructor(value.getClass());
            return constructor.newInstance(value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
