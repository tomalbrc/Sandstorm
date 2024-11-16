package de.tomalbrc.sandstorm.component.particle;

import com.google.gson.*;
import de.tomalbrc.sandstorm.component.ParticleComponent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import java.lang.reflect.Type;

// Particle Lifetime Components
public class ParticleExpireIfInBlocks implements ParticleComponent<Block[]> {
    public Block[] value = new Block[0];

    @Override
    public Block[] value() {
        return this.value;
    }

    public static class Deserializer implements JsonDeserializer<ParticleExpireIfInBlocks> {
        @Override
        public ParticleExpireIfInBlocks deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonArray jsonArray = json.getAsJsonArray();
            ParticleExpireIfInBlocks component = new ParticleExpireIfInBlocks();
            component.value = new Block[jsonArray.size()];

            for (int i = 0; i < jsonArray.size(); i++) {
                if (jsonArray.isJsonPrimitive()) {
                    component.value[i] = BuiltInRegistries.BLOCK.get(ResourceLocation.parse(jsonArray.get(i).getAsString())).orElseThrow().value();
                }
            }

            return component;
        }
    }
}
