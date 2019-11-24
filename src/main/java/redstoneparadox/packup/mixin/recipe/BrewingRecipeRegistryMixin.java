package redstoneparadox.packup.mixin.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.BrewingRecipeRegistry;
import org.spongepowered.asm.mixin.Mixin;
import redstoneparadox.packup.recipe.PotionRecipe;

import java.util.ArrayList;
import java.util.List;

@Mixin(BrewingRecipeRegistry.class)
public abstract class BrewingRecipeRegistryMixin {

    private static void hasCustomRecipeIngredient(ItemStack itemStack) {
        for (PotionRecipe recipe: PotionRecipe.RECIPES) {

        }
    }

    private static void hasCustomRecipe(ItemStack itemStack, ItemStack itemStack2) {
        for (PotionRecipe recipe: PotionRecipe.RECIPES) {

        }
    }


}
