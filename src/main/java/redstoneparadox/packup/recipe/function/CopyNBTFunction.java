package redstoneparadox.packup.recipe.function;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Identifier;

public class CopyNBTFunction extends RecipeFunction<CopyNBTFunction.Data> {
    static final Identifier IDENTIFIER = new Identifier("packup:copy_nbt");

    @Override
    ItemStack apply(ItemStack input, ItemStack output, Data data) {
        CompoundTag nbt = input.getOrCreateTag();

        if (nbt.contains(data.tag)) {
            Tag toCopy = nbt.get(data.tag).copy();
            output.getOrCreateTag().put(data.tag, toCopy);
        }

        return output;
    }

    @Override
    ItemStack apply(ItemStack[] input, ItemStack output, Data data) {
        CompoundTag nbt = input[data.slot].getTag();

        assert nbt != null;
        if (nbt.contains(data.tag)) {
            Tag toCopy = nbt.get(data.tag).copy();
            output.getTag().put(data.tag, toCopy);
        }

        return output;
    }

    @Override
    ConfiguredRecipeFunction<Data> configure(JsonObject json) {
        if (json.has("tag") && json.get("tag").isJsonPrimitive()) {
            JsonPrimitive primitive = json.get("tag").getAsJsonPrimitive();

            if (primitive.isString()) {
                Data data = new Data(primitive.getAsString(), 0);

                if (json.has("slot") && json.get("slot").isJsonPrimitive()) {
                    JsonPrimitive slot = json.getAsJsonPrimitive("slot");
                    if (slot.isNumber()) {
                        data = new Data(primitive.getAsString(), slot.getAsInt());
                    }
                }

                return new ConfiguredRecipeFunction<>(this, data);
            }
        }
        throw new JsonSyntaxException("Failed to parse recipe function " + IDENTIFIER + ".");
    }

    static class Data {
        private final String tag;
        private final Integer slot;

        private Data(String tag, Integer slot) {
            this.tag = tag;
            this.slot = slot;
        }
    }
}
