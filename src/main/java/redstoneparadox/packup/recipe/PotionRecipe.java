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
import net.minecraft.item.PotionItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagReader;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PotionRecipe implements Recipe<Inventory> {

    private static Identifier IDENTIFIER = new Identifier("packup", "brewing");
    private static RecipeType<PotionRecipe> TYPE = Registry.register(Registry.RECIPE_TYPE, IDENTIFIER, new Type());
    private static RecipeSerializer<PotionRecipe> SERIALIZER = Registry.register(Registry.RECIPE_SERIALIZER, IDENTIFIER, new Serializer());

    public static List<PotionRecipe> RECIPES = new ArrayList<>();

    private final CompoundTag data;

    private final Ingredient item;
    private final Ingredient reagent;
    private final ItemStack result;

    private PotionRecipe(CompoundTag data, Ingredient item, Ingredient reagent, ItemStack result) {
        this.data = data;
        this.item = item;
        this.reagent = reagent;
        this.result = result;
    }

    @Override
    public boolean matches(Inventory inv, World world) {
        return false;
    }

    @Override
    public ItemStack craft(Inventory inv) {
        return ItemStack.EMPTY;
    }

    public ItemStack craft() {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean fits(int width, int height) {
        return false;
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

    private static class Type implements RecipeType<PotionRecipe> {
        @Override
        public <C extends Inventory> Optional<PotionRecipe> get(Recipe<C> recipe_1, World world_1, C inventory_1) {
            return Optional.empty();
        }
    }

    private static class Serializer implements RecipeSerializer<PotionRecipe> {

        @Override
        public PotionRecipe read(Identifier id, JsonObject json) {
            CompoundTag nbt = (CompoundTag) Dynamic.convert(JsonOps.INSTANCE, NbtOps.INSTANCE, json);

            Ingredient input = deserializeInput(nbt.getCompound("input"));
            Ingredient reagent = deserializeReagent(nbt.getCompound("reagent"));
            ItemStack result = deserializeResult(nbt.getCompound("result"));

            PotionRecipe recipe = new PotionRecipe(nbt, input, reagent, result);
            RECIPES.add(recipe);
            return recipe;
        }

        @Override
        public PotionRecipe read(Identifier id, PacketByteBuf buf) {
            CompoundTag nbt = buf.readCompoundTag();

            Ingredient input = deserializeInput(nbt.getCompound("input"));
            Ingredient reagent = deserializeReagent(nbt.getCompound("reagent"));
            ItemStack result = deserializeResult(nbt.getCompound("result"));

            PotionRecipe recipe = new PotionRecipe(nbt, input, reagent, result);
            RECIPES.add(recipe);
            return recipe;
        }

        @Override
        public void write(PacketByteBuf buf, PotionRecipe recipe) {
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

        private Ingredient deserializeReagent(CompoundTag tag) {
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

        private ItemStack deserializeResult(CompoundTag tag) {
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
