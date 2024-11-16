package de.tomalbrc.sandstorm.component.particle;

import com.google.gson.*;
import de.tomalbrc.sandstorm.component.ParticleComponent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import java.lang.reflect.Type;

public class ParticleExpireIfNotInBlocks implements ParticleComponent<Block[]> {
    public Block[] value = new Block[0];

    @Override
    public Block[] value() {
        return this.value;
    }

    public static class Deserializer implements JsonDeserializer<ParticleExpireIfNotInBlocks> {
        @Override
        public ParticleExpireIfNotInBlocks deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonArray jsonArray = json.getAsJsonArray();
            ParticleExpireIfNotInBlocks component = new ParticleExpireIfNotInBlocks();
            component.value = new Block[jsonArray.size()];

            for (int i = 0; i < jsonArray.size(); i++) {
                if (jsonArray.get(i).isJsonPrimitive()) {
                    component.value[i] = BuiltInRegistries.BLOCK.get(ResourceLocation.parse(jsonArray.get(i).getAsString())).orElseThrow().value();
                }
            }

            return component;
        }
    }
}
