package redstoneparadox.packup.recipe;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.datafixers.Dynamic;
import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.datafixers.NbtOps;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import redstoneparadox.packup.recipe.function.RecipeFunction;
import redstoneparadox.packup.recipe.function.RecipeFunctionParser;
import redstoneparadox.packup.util.FixedJsonOps;

import java.util.ArrayList;
import java.util.List;

public class BrewingRecipe implements Recipe<Inventory> {

    private static final Identifier IDENTIFIER = new Identifier("packup", "brewing");
    public static final RecipeType<BrewingRecipe> TYPE = RecipeType.register(IDENTIFIER.toString());
    public static final RecipeSerializer<BrewingRecipe> SERIALIZER = Registry.register(Registry.RECIPE_SERIALIZER, IDENTIFIER, new Serializer());

    private final CompoundTag data;

    private final BrewingIngredient input;
    private final Ingredient ingredient;
    private final BrewingResult result;

    private final List<RecipeFunction.ConfiguredRecipeFunction<?>> functions;

    private BrewingRecipe(CompoundTag data, BrewingIngredient input, Ingredient ingredient, BrewingResult result, List<RecipeFunction.ConfiguredRecipeFunction<?>> functions) {
        this.data = data;
        this.input = input;
        this.ingredient = ingredient;
        this.result = result;
        this.functions = functions;
    }

    @Override
    public boolean matches(Inventory inv, World world) {
        ItemStack ingredient = inv.getInvStack(3);

        if (!this.ingredient.test(ingredient)) return false;

        for (int i = 0; i < 3; i++) {
            if (matches(inv, i)) return true;
        }

        return false;
    }

    public boolean matches(Inventory inv, int slot) {
        ItemStack ingredient = inv.getInvStack(3);

        if (!this.ingredient.test(ingredient)) return false;

        ItemStack input = inv.getInvStack(slot);
        return this.input.test(input);
    }

    @Override
    public ItemStack craft(Inventory inv) {
        return ItemStack.EMPTY;
    }

    public ItemStack craft(ItemStack input) {
        ItemStack output = this.result.toResult(input);

        for (RecipeFunction.ConfiguredRecipeFunction<?> function: functions) {
            output = function.apply(input, output);
        }

        return output;
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getOutput() {
        return ItemStack.EMPTY;
    }

    @Override
    public Identifier getId() {
        return IDENTIFIER;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return TYPE;
    }

    @Override
    public ItemStack getRecipeKindIcon() {
        return new ItemStack(Items.BREWING_STAND);
    }

    private static class Serializer implements RecipeSerializer<BrewingRecipe> {

        @Override
        public BrewingRecipe read(Identifier id, JsonObject json) {
            CompoundTag nbt = (CompoundTag) Dynamic.convert(FixedJsonOps.INSTANCE, NbtOps.INSTANCE, json);

            BrewingIngredient input = deserializeInput(nbt.getCompound("input"));
            Ingredient ingredient = deserializeIngredient(nbt.getCompound("ingredient"));
            BrewingResult output = deserializeOutput(nbt.getCompound("output"), input.isPotion());

            List<RecipeFunction.ConfiguredRecipeFunction<?>> functions = new ArrayList<>();
            if (json.has("functions") && json.get("functions").isJsonArray()) {
                RecipeFunctionParser.parse(json.getAsJsonArray("functions"), functions);
            }

            return new BrewingRecipe(nbt, input, ingredient, output, functions);
        }

        @Override
        public BrewingRecipe read(Identifier id, PacketByteBuf buf) {
            CompoundTag nbt = buf.readCompoundTag();

            assert nbt != null;
            BrewingIngredient input = deserializeInput(nbt.getCompound("input"));
            Ingredient ingredient = deserializeIngredient(nbt.getCompound("ingredient"));
            BrewingResult output = deserializeOutput(nbt.getCompound("output"), input.isPotion());

            JsonObject json = (JsonObject) Dynamic.convert(NbtOps.INSTANCE, FixedJsonOps.INSTANCE, nbt);
            List<RecipeFunction.ConfiguredRecipeFunction<?>> functions = new ArrayList<>();
            if (json.has("functions") && json.get("functions").isJsonArray()) {
                RecipeFunctionParser.parse(json.getAsJsonArray("functions"), functions);
            }

            return new BrewingRecipe(nbt, input, ingredient, output, functions);
        }

        @Override
        public void write(PacketByteBuf buf, BrewingRecipe recipe) {
            buf.writeCompoundTag(recipe.data);
        }

        private BrewingIngredient deserializeInput(CompoundTag tag) {
            if (tag.contains("item")) {
                String id = tag.getString("item");
                Item item = Registry.ITEM.get(new Identifier(id));
                return BrewingIngredient.ofIngredient(Ingredient.ofItems(item));
            }
            else if (tag.contains("tag")) {
                String id = tag.getString("tag");
                Tag<Item> itemTag = TagRegistry.item(new Identifier(id));
                return BrewingIngredient.ofIngredient(Ingredient.fromTag(itemTag));
            }
            else if (tag.contains("potion")) {
                String id = tag.getString("potion");
                Potion potion = Registry.POTION.get(new Identifier(id));
                return BrewingIngredient.ofPotion(potion);
            }
            throw new JsonSyntaxException("");
        }

        private Ingredient deserializeIngredient(CompoundTag tag) {
            if (tag.contains("item")) {
                String id = tag.getString("item");
                Item item = Registry.ITEM.get(new Identifier(id));
                return Ingredient.ofItems(item);
            }
            else if (tag.contains("tag")) {
                String id = tag.getString("tag");
                Tag<Item> itemTag = TagRegistry.item(new Identifier(id));
                return Ingredient.fromTag(itemTag);
            }
            throw new JsonSyntaxException("");
        }

        private BrewingResult deserializeOutput(CompoundTag tag, boolean potionInput) {
            if (tag.contains("item")) {
                String id = tag.getString("item");
                Item item = Registry.ITEM.get(new Identifier(id));
                ItemStack stack = new ItemStack(item);

                if (tag.contains("apply")) {
                    return BrewingResult.ofItemStack(stack, tag.getBoolean("apply"));
                }

                return BrewingResult.ofItemStack(stack, false);
            }
            else if (tag.contains("potion")) {
                String id = tag.getString("potion");
                Potion potion = Registry.POTION.get(new Identifier(id));

                if (potionInput) {
                    return BrewingResult.ofPotion(potion);
                }

                ItemStack stack = new ItemStack(Items.POTION);

                return BrewingResult.ofItemStack(PotionUtil.setPotion(stack, potion), false);
            }
            throw new JsonSyntaxException("");
        }
    }
}
