package redstoneparadox.packup.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.item.PotionItem;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.recipe.Ingredient;

import javax.annotation.Nullable;

public class BrewingIngredient extends ItemPredicate {

    private final @Nullable Ingredient ingredient;
    private final @Nullable Potion potion;

    private BrewingIngredient(@Nullable Ingredient ingredient, @Nullable Potion potion) {
        this.ingredient = ingredient;
        this.potion = potion;
    }

    public static BrewingIngredient ofIngredient(Ingredient ingredient) {
        return new BrewingIngredient(ingredient, null);
    }

    public static BrewingIngredient ofPotion(Potion potion) {
        return new BrewingIngredient(null, potion);
    }

    public boolean isIngredient() {
        return ingredient != null;
    }

    public boolean isPotion() {
        return potion != null;
    }

    @Override
    public boolean test(ItemStack stack) {
        if (ingredient != null) {
            return ingredient.test(stack);
        }
        else if (stack.getItem() instanceof PotionItem && potion != null) {
            return PotionUtil.getPotion(stack) == potion;
        }
        return false;
    }
}
