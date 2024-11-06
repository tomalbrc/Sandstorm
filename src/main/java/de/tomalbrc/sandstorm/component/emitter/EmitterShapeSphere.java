package de.tomalbrc.sandstorm.component.emitter;

import com.google.gson.*;
import com.google.gson.annotations.SerializedName;
import de.tomalbrc.sandstorm.component.ParticleComponent;
import de.tomalbrc.sandstorm.util.EmitterDirection;
import gg.moonflower.molangcompiler.api.MolangExpression;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.lang.reflect.Type;
import java.util.List;

public class EmitterShapeSphere implements ParticleComponent<EmitterShapeSphere> {
    @SerializedName("offset")
    public MolangExpression[] offset = new MolangExpression[]{MolangExpression.ZERO, MolangExpression.ZERO, MolangExpression.ZERO}; // default: [0, 0, 0]

    @SerializedName("radius")
    public MolangExpression radius = MolangExpression.of(1); // default: 1

    @SerializedName("surface_only")
    public boolean surfaceOnly = false; // default: false

    @SerializedName("direction")
    public EmitterDirection direction = EmitterDirection.OUTWARDS; // default: "outwards"

    @SerializedName("direction")
    public List<MolangExpression> directionList;

    public static class Deserializer implements JsonDeserializer<EmitterShapeSphere> {
        @Override
        public EmitterShapeSphere deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            EmitterShapeSphere sphere = new EmitterShapeSphere();

            if (jsonObject.has("offset")) {
                sphere.offset = context.deserialize(jsonObject.get("offset"), MolangExpression[].class);
            }

            if (jsonObject.has("radius")) {
                sphere.radius = context.deserialize(jsonObject.get("radius"), MolangExpression.class);
            }

            if (jsonObject.has("surface_only")) {
                sphere.surfaceOnly = jsonObject.get("surface_only").getAsBoolean();
            }

            if (jsonObject.has("direction")) {
                JsonElement directionElement = jsonObject.get("direction");
                if (directionElement.isJsonPrimitive()) {
                    try {
                        sphere.direction = EmitterDirection.valueOf(directionElement.getAsString().toUpperCase());
                    } catch (IllegalArgumentException e) {
                        throw new JsonParseException("Invalid value for EmitterDirection: " + directionElement.getAsString());
                    }
                } else if (directionElement.isJsonArray()) {
                    sphere.directionList = new ObjectArrayList<>();
                    for (JsonElement element : directionElement.getAsJsonArray()) {
                        sphere.directionList.add(context.deserialize(element, MolangExpression.class));
                    }
                }
            }

            return sphere;
        }
    }
}
