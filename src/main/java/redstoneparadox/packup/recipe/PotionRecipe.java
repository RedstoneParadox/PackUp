package redstoneparadox.packup.recipe;

import com.google.gson.JsonObject;
import net.minecraft.container.BrewingStandContainer;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.world.World;
import org.lwjgl.system.CallbackI;

import java.util.Optional;

public class PotionRecipe implements Recipe<Inventory> {

    private static Identifier IDENTIFIER = new Identifier("packup", "brewing");

    @Override
    public boolean matches(Inventory inv, World world) {
        return false;
    }

    @Override
    public ItemStack craft(Inventory inv) {
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
        return null;
    }

    @Override
    public RecipeType<?> getType() {
        return null;
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
            return null;
        }

        @Override
        public PotionRecipe read(Identifier id, PacketByteBuf buf) {
            return null;
        }

        @Override
        public void write(PacketByteBuf buf, PotionRecipe recipe) {

        }
    }
}
