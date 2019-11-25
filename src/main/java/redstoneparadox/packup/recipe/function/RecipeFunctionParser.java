package redstoneparadox.packup.recipe.function;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class RecipeFunctionParser {

    public static List<RecipeFunction.ConfiguredRecipeFunction<?>> parse(JsonArray array) {
        List<RecipeFunction.ConfiguredRecipeFunction<?>> functions = new ArrayList<>();

        for (JsonElement element: array) {
            if (element instanceof JsonObject) {
                functions.add(parseFunction(element.getAsJsonObject()));
            }
        }

        return functions;
    }

    private static RecipeFunction.ConfiguredRecipeFunction<?> parseFunction(JsonObject object) {
        if (object.has("id") && object.has("data")) {
            String id = object.get("id").getAsString();
            RecipeFunction function = RecipeFunction.get(new Identifier(id));
            return function.configure(object.getAsJsonObject("data"));
        }
        throw new JsonParseException("Unable to parse recipe functions!");
    }
}
