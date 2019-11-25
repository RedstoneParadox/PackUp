package redstoneparadox.packup.recipe.function;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.Optional;

public class RecipeFunctionData {
    private final JsonObject internal;

    public RecipeFunctionData(JsonObject internal) {
        this.internal = internal;
    }

    public Optional<String> getString(String key) {
        if (internal.has(key) && internal.get(key).isJsonPrimitive()) {
            JsonPrimitive primitive = internal.get(key).getAsJsonPrimitive();
            if (primitive.isString()) {
                return Optional.of(primitive.getAsString());
            }
        }
        return Optional.empty();
    }

    public Optional<Boolean> getBool(String key) {
        if (internal.has(key) && internal.get(key).isJsonPrimitive()) {
            JsonPrimitive primitive = internal.get(key).getAsJsonPrimitive();
            if (primitive.isBoolean()) {
                return Optional.of(primitive.getAsBoolean());
            }
        }
        return Optional.empty();
    }

    public Optional<Double> getDouble(String key) {
        if (internal.has(key) && internal.get(key).isJsonPrimitive()) {
            JsonPrimitive primitive = internal.get(key).getAsJsonPrimitive();
            if (primitive.isNumber()) {
                return Optional.of(primitive.getAsDouble());
            }
        }
        return Optional.empty();
    }

}
