package redstoneparadox.packup.recipe.function;

import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public abstract class RecipeFunction<D> {

    private static final Map<Identifier, RecipeFunction<?>> REGISTRY = new HashMap<>();

    public ItemStack apply(ItemStack input, ItemStack output, D data) {
        return output;
    }

    public ItemStack apply(ItemStack[] input, ItemStack output, D data) {
        return output;
    }

    abstract ConfiguredRecipeFunction<D> configure(JsonObject input);

    public static void register(Identifier identifier, RecipeFunction function) {
        REGISTRY.put(identifier, function);
    }

    static RecipeFunction<?> get(Identifier identifier) {
        return REGISTRY.get(identifier);
    }

    public static class ConfiguredRecipeFunction<D> {
        private final RecipeFunction<D> function;
        private final D data;

        public ConfiguredRecipeFunction(RecipeFunction<D> function, D data) {
            this.function = function;
            this.data = data;
        }

        public ItemStack apply(ItemStack input, ItemStack output) {
            return function.apply(input, output, data);
        }

        public ItemStack apply(ItemStack[] input, ItemStack output) {
            return function.apply(input, output, data);
        }
    }
}
