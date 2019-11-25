package redstoneparadox.packup.recipe;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.JsonOps;
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

import java.util.Optional;

public class BrewingRecipe implements Recipe<Inventory> {

    private static final Identifier IDENTIFIER = new Identifier("packup", "brewing");
    public static final RecipeType<BrewingRecipe> TYPE = RecipeType.register(IDENTIFIER.toString());
    public static final RecipeSerializer<BrewingRecipe> SERIALIZER = Registry.register(Registry.RECIPE_SERIALIZER, IDENTIFIER, new Serializer());

    private final CompoundTag data;

    private final Ingredient input;
    private final Ingredient ingredient;
    private final ItemStack result;

    private BrewingRecipe(CompoundTag data, Ingredient input, Ingredient ingredient, ItemStack result) {
        this.data = data;
        this.input = input;
        this.ingredient = ingredient;
        this.result = result;
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
        ItemStack input = inv.getInvStack(slot);
        return this.input.test(input);
    }

    @Override
    public ItemStack craft(Inventory inv) {
        return result.copy();
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getOutput() {
        return result.copy();
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
            CompoundTag nbt = (CompoundTag) Dynamic.convert(JsonOps.INSTANCE, NbtOps.INSTANCE, json);

            Ingredient input = deserializeInput(nbt.getCompound("input"));
            Ingredient ingredient = deserializeIngredient(nbt.getCompound("ingredient"));
            ItemStack output = deserializeOutput(nbt.getCompound("output"));

            BrewingRecipe recipe = new BrewingRecipe(nbt, input, ingredient, output);
            return recipe;
        }

        @Override
        public BrewingRecipe read(Identifier id, PacketByteBuf buf) {
            CompoundTag nbt = buf.readCompoundTag();

            Ingredient input = deserializeInput(nbt.getCompound("input"));
            Ingredient ingredient = deserializeIngredient(nbt.getCompound("ingredient"));
            ItemStack output = deserializeOutput(nbt.getCompound("output"));

            BrewingRecipe recipe = new BrewingRecipe(nbt, input, ingredient, output);
            return recipe;
        }

        @Override
        public void write(PacketByteBuf buf, BrewingRecipe recipe) {
            buf.writeCompoundTag(recipe.data);
        }

        private Ingredient deserializeInput(CompoundTag tag) {
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
            else if (tag.contains("potion")) {
                String id = tag.getString("potion");
                Potion potion = Registry.POTION.get(new Identifier(id));


                ItemStack stack = new ItemStack(Items.POTION);
                if (tag.contains("type")) {
                    if (tag.getString("type").equals("splash")) stack = new ItemStack(Items.SPLASH_POTION);
                    else if (tag.getString("type").equals("lingering")) stack = new ItemStack(Items.LINGERING_POTION);
                }

                PotionUtil.setPotion(stack, potion);

                return Ingredient.ofStacks(stack);
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

        private ItemStack deserializeOutput(CompoundTag tag) {
            if (tag.contains("item")) {
                String id = tag.getString("item");
                Item item = Registry.ITEM.get(new Identifier(id));
                return new ItemStack(item);
            }
            else if (tag.contains("potion")) {
                String id = tag.getString("potion");
                Potion potion = Registry.POTION.get(new Identifier(id));


                ItemStack stack = new ItemStack(Items.POTION);
                if (tag.contains("type")) {
                    if (tag.getString("type").equals("splash")) stack = new ItemStack(Items.SPLASH_POTION);
                    else if (tag.getString("type").equals("lingering")) stack = new ItemStack(Items.LINGERING_POTION);
                }

                PotionUtil.setPotion(stack, potion);

                return stack;
            }
            throw new JsonSyntaxException("");
        }
    }
}
