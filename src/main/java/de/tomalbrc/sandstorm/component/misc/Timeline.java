package de.tomalbrc.sandstorm.component.misc;

import com.google.gson.*;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class Timeline {
    private final Map<Float, List<String>> timeEvents;

    public Timeline(Map<Float, List<String>> timeEvents) {
        this.timeEvents = timeEvents;
    }

    public List<String> getEventsInRange(float time, float dt) {
        float lowerBound = time - dt;
        List<String> eventsInRange = new ObjectArrayList<>();

        for (Map.Entry<Float, List<String>> entry : timeEvents.entrySet()) {
            Float eventTime = entry.getKey();
            if (eventTime >= lowerBound && eventTime < time) {
                eventsInRange.addAll(entry.getValue());
            }
        }

        return eventsInRange.isEmpty() ? null : eventsInRange;
    }

    public static class Deserializer implements JsonDeserializer<Timeline> {
        @Override
        public Timeline deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            Map<Float, List<String>> timeEvents = new Object2ObjectOpenHashMap<>();

            JsonObject timelineObject = json.getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : timelineObject.entrySet()) {
                try {
                    Float time = Float.parseFloat(entry.getKey());
                    JsonElement value = entry.getValue();

                    List<String> events = new ObjectArrayList<>();
                    if (value.isJsonArray()) {
                        for (JsonElement element : value.getAsJsonArray()) {
                            events.add(element.getAsString());
                        }
                    } else if (value.isJsonPrimitive()) {
                        events.add(value.getAsString());
                    }

                    timeEvents.put(time, events);
                } catch (NumberFormatException e) {
                    throw new JsonParseException("Invalid time format: " + entry.getKey(), e);
                }
            }

            return new Timeline(timeEvents);
        }
    }
}

