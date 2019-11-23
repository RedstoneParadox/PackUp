package redstoneparadox.packup.recipe;

import com.google.gson.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RecipeTemplateFiller {

    public static List<Pair<Identifier, JsonObject>> fillShapedTemplate(Identifier id, JsonObject original) {
        JsonArray templateArray = original.getAsJsonArray("replacements");
        List<Template> templates = new ArrayList<>();
        List<Pair<Identifier, JsonObject>> filledRecipes = new ArrayList<>();

        for (JsonElement element: templateArray) {
            if (element.isJsonObject()) {
                JsonObject obj = element.getAsJsonObject();
                List<Pair<String, String>> pairs = new ArrayList<>();
                for (Map.Entry<String, JsonElement> entry: obj.entrySet()) {
                    if (entry.getValue().isJsonPrimitive()) {
                        Pair<String, String> pair = new Pair<>(entry.getKey(), entry.getValue().getAsString());
                        pairs.add(pair);
                    }
                }
                templates.add(new Template(pairs));
            }
        }

        for (Template template: templates) {
            JsonObject copy = new JsonObject();
            copy.add("type", new JsonPrimitive("minecraft:crafting_shaped"));
            copy.add("pattern", original.get("pattern"));
            copy.add("key", fillKeyTemplate(original.getAsJsonObject("key"), template));
            copy.add("result", fillResultTemplate(original.getAsJsonObject("result"), template));

            Identifier identifier = new Identifier(id.getNamespace(), replace(original.get("id").getAsString(), template));

            filledRecipes.add(new Pair<>(identifier, copy));
        }

        return filledRecipes;
    }

    public static List<Pair<Identifier, JsonObject>> fillShapelessTemplate(Identifier id, JsonObject original) {
        JsonArray templateArray = original.getAsJsonArray("replacements");
        List<Template> templates = new ArrayList<>();
        List<Pair<Identifier, JsonObject>> filledRecipes = new ArrayList<>();

        for (JsonElement element: templateArray) {
            if (element.isJsonObject()) {
                JsonObject obj = element.getAsJsonObject();
                List<Pair<String, String>> pairs = new ArrayList<>();
                for (Map.Entry<String, JsonElement> entry: obj.entrySet()) {
                    if (entry.getValue().isJsonPrimitive()) {
                        Pair<String, String> pair = new Pair<>(entry.getKey(), entry.getValue().getAsString());
                        pairs.add(pair);
                    }
                }
                templates.add(new Template(pairs));
            }
        }

        for (Template template: templates) {
            JsonObject copy = new JsonObject();
            copy.add("type", new JsonPrimitive("minecraft:crafting_shapeless"));
            copy.add("ingredients", fillIngredientsTemplate(original.getAsJsonArray("ingredients"), template));
            copy.add("result", fillResultTemplate(original.getAsJsonObject("result"), template));

            Identifier identifier = new Identifier(id.getNamespace(), replace(original.get("id").getAsString(), template));

            filledRecipes.add(new Pair<>(identifier, copy));
        }

        return filledRecipes;
    }

    private static JsonObject fillKeyTemplate(JsonObject object, Template template) {
        JsonObject key = new JsonObject();

        for (Map.Entry<String, JsonElement> entry: object.entrySet()) {
            if (entry.getValue() instanceof JsonObject) {
                JsonObject keyObject = new JsonObject();
                String property = itemOrTag((JsonObject) entry.getValue());
                String filledString = replace(((JsonObject) entry.getValue()).get(property).getAsString(), template);
                keyObject.add(property, new JsonPrimitive(filledString));
                key.add(entry.getKey(), keyObject);
            }
        }
        return key;
    }

    private static JsonArray fillIngredientsTemplate(JsonArray array, Template template) {
        JsonArray ingredients = new JsonArray();

        for (JsonElement element: array) {
            if (element.isJsonObject()) {
                JsonObject ingredient = new JsonObject();
                String property = itemOrTag((JsonObject) element);
                String filledString = replace(((JsonObject) element).get(property).getAsString(), template);
                ingredient.add(property, new JsonPrimitive(filledString));
                ingredients.add(ingredient);
            }
        }

        return ingredients;
    }

    private static JsonObject fillResultTemplate(JsonObject object, Template template) {
        JsonObject result = new JsonObject();
        result.add("count", object.get("count"));

        String item = replace(object.get("item").getAsString(), template);
        result.add("item", new JsonPrimitive(item));

        return result;
    }

    private static String replace(String string, Template template) {
        String out = string;
        for (Pair<String, String> pair: template.fills) {
            out = out.replace("${" + pair.getLeft() +"}", pair.getRight());
        }
        return out;
    }

    private static String itemOrTag(JsonObject object) {
        if (object.has("item")) return "item";
        if (object.has("tag")) return "tag";
        throw new JsonSyntaxException("Malformed recipe!");
    }
}
