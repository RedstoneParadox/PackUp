package redstoneparadox.packup.recipe;

import com.google.gson.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RecipeTemplateFiller {

    public static List<Pair<Identifier, JsonObject>> fillShapedTemplate(Identifier id, JsonObject original) {
        List<List<Pair<String, String>>> replacements = getReplacements(original.getAsJsonArray("replacements"));
        List<Pair<Identifier, JsonObject>> filledRecipes = new ArrayList<>();

        for (List<Pair<String, String>> replacement: replacements) {
            JsonObject copy = new JsonObject();
            copy.add("type", new JsonPrimitive("minecraft:crafting_shaped"));
            copy.add("pattern", original.get("pattern"));
            copy.add("key", fillKeyTemplate(original.getAsJsonObject("key"), replacement));
            copy.add("result", fillResultTemplate(original.getAsJsonObject("result"), replacement));

            Identifier identifier = new Identifier(id.getNamespace(), replace(original.get("id").getAsString(), replacement));

            filledRecipes.add(new Pair<>(identifier, copy));
        }

        return filledRecipes;
    }

    public static List<Pair<Identifier, JsonObject>> fillShapelessTemplate(Identifier id, JsonObject original) {
        List<List<Pair<String, String>>> replacements = getReplacements(original.getAsJsonArray("replacements"));
        List<Pair<Identifier, JsonObject>> filledRecipes = new ArrayList<>();

        for (List<Pair<String, String>> replacement: replacements) {
            JsonObject copy = new JsonObject();
            copy.add("type", new JsonPrimitive("minecraft:crafting_shapeless"));
            copy.add("ingredients", fillIngredientsTemplate(original.getAsJsonArray("ingredients"), replacement));
            copy.add("result", fillResultTemplate(original.getAsJsonObject("result"), replacement));

            Identifier identifier = new Identifier(id.getNamespace(), replace(original.get("id").getAsString(), replacement));

            filledRecipes.add(new Pair<>(identifier, copy));
        }

        return filledRecipes;
    }

    public static List<Pair<Identifier, JsonObject>> fillFurnaceTemplate(Identifier id, JsonObject original) {
        List<List<Pair<String, String>>> replacements = getReplacements(original.getAsJsonArray("replacements"));
        List<Pair<Identifier, JsonObject>> filledRecipes = new ArrayList<>();

        for (List<Pair<String, String>> replacement: replacements) {
            JsonObject copy = new JsonObject();
            copy.add("type", original.get("type"));
            copy.add("ingredient", fillSingleIngredient(original.getAsJsonObject("ingredient"), replacement));
            copy.add("experience", original.get("experience"));
            copy.add("cookingtime", original.get("cookingtime"));
            copy.add("result", new JsonPrimitive(replace(original.get("result").getAsString(), replacement)));

            Identifier identifier = new Identifier(id.getNamespace(), replace(original.get("id").getAsString(), replacement));

            filledRecipes.add(new Pair<>(identifier, copy));
        }

        return filledRecipes;
    }

    private static List<List<Pair<String, String>>> getReplacements(JsonArray replacementsArray) {
        List<List<Pair<String, String>>> replacements = new ArrayList<>();

        for (JsonElement element: replacementsArray) {
            if (element.isJsonObject()) {
                JsonObject obj = element.getAsJsonObject();
                List<Pair<String, String>> replacement = new ArrayList<>();
                for (Map.Entry<String, JsonElement> entry: obj.entrySet()) {
                    if (entry.getValue().isJsonPrimitive()) {
                        Pair<String, String> pair = new Pair<>(entry.getKey(), entry.getValue().getAsString());
                        replacement.add(pair);
                    }
                }
                replacements.add(replacement);
            }
        }

        return replacements;
    }

    private static JsonObject fillKeyTemplate(JsonObject object, List<Pair<String, String>> replacements) {
        JsonObject key = new JsonObject();

        for (Map.Entry<String, JsonElement> entry: object.entrySet()) {
            if (entry.getValue() instanceof JsonObject) {
                key.add(entry.getKey(), fillSingleIngredient(entry.getValue().getAsJsonObject(), replacements));
            }
        }
        return key;
    }

    private static JsonObject fillSingleIngredient(JsonObject original, List<Pair<String, String>> replacements) {
        JsonObject ingredientObject = new JsonObject();
        String property = itemOrTag(original);
        String filledString = replace(original.get(property).getAsString(), replacements);
        ingredientObject.add(property, new JsonPrimitive(filledString));
        return ingredientObject;
    }

    private static JsonArray fillIngredientsTemplate(JsonArray array, List<Pair<String, String>> replacements) {
        JsonArray ingredients = new JsonArray();

        for (JsonElement element: array) {
            if (element.isJsonObject()) {
                JsonObject ingredient = new JsonObject();
                String property = itemOrTag((JsonObject) element);
                String filledString = replace(((JsonObject) element).get(property).getAsString(), replacements);
                ingredient.add(property, new JsonPrimitive(filledString));
                ingredients.add(ingredient);
            }
        }

        return ingredients;
    }

    private static JsonObject fillResultTemplate(JsonObject object, List<Pair<String, String>> replacements) {
        JsonObject result = new JsonObject();
        result.add("count", object.get("count"));

        String item = replace(object.get("item").getAsString(), replacements);
        result.add("item", new JsonPrimitive(item));

        return result;
    }

    private static String replace(String string, List<Pair<String, String>> replacements) {
        String out = string;
        for (Pair<String, String> replacement: replacements) {
            out = out.replace("${" + replacement.getLeft() + "}", replacement.getRight());
        }
        return out;
    }

    private static String itemOrTag(JsonObject object) {
        if (object.has("item")) return "item";
        if (object.has("tag")) return "tag";
        throw new JsonSyntaxException("Malformed recipe!");
    }
}
