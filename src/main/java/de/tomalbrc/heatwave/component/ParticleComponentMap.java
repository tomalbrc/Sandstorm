package de.tomalbrc.heatwave.component;

import com.google.gson.*;
import de.tomalbrc.heatwave.Heatwave;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.resources.ResourceLocation;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.function.BiConsumer;

@SuppressWarnings("unchecked")
public class ParticleComponentMap {
    private final Map<ParticleComponentType<? extends ParticleComponent<?>, ?>, Object> componentMap = new Object2ObjectOpenHashMap<>();

    public void put(ParticleComponentType<?, ?> type, Object config) {
        this.componentMap.put(type, config);
    }

    public <T extends ParticleComponent<E>, E> E get(ParticleComponentType<T, E> type) {
        return (E) this.componentMap.get(type);
    }

    public <T extends ParticleComponent<E>, E> boolean has(ParticleComponentType<T, E> type) {
        return this.componentMap.containsKey(type);
    }

    public <T extends ParticleComponent<E>, E> void forEach(BiConsumer<ParticleComponentType<T, E>, Object> biConsumer) {
        for (Map.Entry<ParticleComponentType<? extends ParticleComponent<?>, ?>, Object> entry : this.componentMap.entrySet()) {
            biConsumer.accept((ParticleComponentType<T, E>) entry.getKey(), entry.getValue());
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
                    resourceLocation = ResourceLocation.fromNamespaceAndPath(Heatwave.MOD_ID, entry.getKey());

                var componentType = ParticleComponentRegistry.getType(resourceLocation);

                if (componentType == null || componentType.configType() == null) {
                    Heatwave.LOGGER.error("Could not load particle component {}", resourceLocation);
                    continue;
                }
                var clazz = componentType.configType();
                Object deserialized = jsonDeserializationContext.deserialize(entry.getValue(), clazz);
                particleComponentMap.put(ParticleComponentRegistry.getType(resourceLocation), deserialized);
            }
            return particleComponentMap;
        }
    }
}
