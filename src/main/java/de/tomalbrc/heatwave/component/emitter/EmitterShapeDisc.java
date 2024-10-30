package de.tomalbrc.heatwave.component.emitter;

import com.google.common.collect.ImmutableList;
import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import com.google.gson.annotations.SerializedName;
import de.tomalbrc.heatwave.component.ParticleComponent;
import de.tomalbrc.heatwave.util.EmitterDirection;
import gg.moonflower.molangcompiler.api.MolangExpression;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.lang.reflect.Type;
import java.util.List;

// Emitter Shape Components
public class EmitterShapeDisc implements ParticleComponent<EmitterShapeDisc> {
    @SerializedName("plane_normal")
    public List<MolangExpression> planeNormal = ImmutableList.of(MolangExpression.ZERO, MolangExpression.of(1), MolangExpression.ZERO); // default: [0, 1, 0]

    @SerializedName("offset")
    public List<MolangExpression> offset = ImmutableList.of(MolangExpression.ZERO, MolangExpression.ZERO, MolangExpression.ZERO); // default: [0, 0, 0]

    @SerializedName("radius")
    public MolangExpression radius = MolangExpression.of(1); // default: 1

    @SerializedName("surface_only")
    public boolean surfaceOnly = false; // default: false

    @SerializedName("direction")
    public EmitterDirection direction = EmitterDirection.OUTWARDS; // default: "outwards"

    @SerializedName("direction")
    public List<MolangExpression> directionList;

    public static class Deserializer implements JsonDeserializer<EmitterShapeDisc> {
        @Override
        public EmitterShapeDisc deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            EmitterShapeDisc disc = new EmitterShapeDisc();

            if (jsonObject.has("plane_normal")) {
                disc.planeNormal = context.deserialize(jsonObject.get("plane_normal"), new TypeToken<List<MolangExpression>>(){}.getType());
            }

            if (jsonObject.has("offset")) {
                disc.offset = context.deserialize(jsonObject.get("offset"), new TypeToken<List<MolangExpression>>(){}.getType());
            }

            if (jsonObject.has("radius")) {
                disc.radius = context.deserialize(jsonObject.get("radius"), MolangExpression.class);
            }

            if (jsonObject.has("surface_only")) {
                disc.surfaceOnly = jsonObject.get("surface_only").getAsBoolean();
            }

            if (jsonObject.has("direction")) {
                JsonElement directionElement = jsonObject.get("direction");
                if (directionElement.isJsonPrimitive()) {
                    try {
                        disc.direction = EmitterDirection.valueOf(directionElement.getAsString().toUpperCase());
                    } catch (IllegalArgumentException e) {
                        throw new JsonParseException("Invalid value for EmitterDirection: " + directionElement.getAsString());
                    }
                } else if (directionElement.isJsonArray()) {
                    disc.directionList = new ObjectArrayList<>();
                    for (JsonElement element : directionElement.getAsJsonArray()) {
                        disc.directionList.add(context.deserialize(element, MolangExpression.class));
                    }
                }
            }

            return disc;
        }
    }
}
