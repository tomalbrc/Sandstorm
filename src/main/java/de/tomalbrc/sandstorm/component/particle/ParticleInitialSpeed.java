package de.tomalbrc.sandstorm.component.particle;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import de.tomalbrc.sandstorm.Sandstorm;
import de.tomalbrc.sandstorm.component.ParticleComponent;
import gg.moonflower.molangcompiler.api.MolangExpression;
import gg.moonflower.molangcompiler.api.exception.MolangSyntaxException;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.lang.reflect.Type;
import java.util.List;

// Particle Initial State Components
public class ParticleInitialSpeed implements ParticleComponent<List<MolangExpression>> {
    List<MolangExpression> value = ImmutableList.of(MolangExpression.ZERO);

    public ParticleInitialSpeed(List<MolangExpression> v) {
        this.value = v;
    }

    public ParticleInitialSpeed(float v) {
        this.value = ImmutableList.of(MolangExpression.of(v));
    }

    public ParticleInitialSpeed(String v) {
        try {
            this.value = ImmutableList.of(Sandstorm.MOLANG.compile(v));
        } catch (MolangSyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<MolangExpression> value() {
        return value;
    }

    public static class Deserializer implements JsonDeserializer<ParticleInitialSpeed> {
        @Override
        public ParticleInitialSpeed deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            List<MolangExpression> list = new ObjectArrayList<>();
            try {
                if (json.isJsonPrimitive()) {
                    list.add(Sandstorm.MOLANG.compile(json.getAsString()));
                } else if (json.isJsonArray()) {
                    for (JsonElement jsonElement : json.getAsJsonArray()) {
                        if (json.isJsonPrimitive()) {
                            list.add(Sandstorm.MOLANG.compile(jsonElement.getAsString()));
                        }
                    }
                } else {
                    throw new JsonParseException("Unexpected JSON type for 'color'");
                }
            } catch (MolangSyntaxException e) {
                throw new RuntimeException(e);
            }

            return new ParticleInitialSpeed(list);
        }
    }
}
