package redstoneparadox.packup.recipe;

import net.minecraft.item.*;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;

import javax.annotation.Nullable;

public class BrewingResult {

    private final @Nullable ItemStack stack;
    private final @Nullable Potion potion;
    private final boolean apply;

    private BrewingResult(@Nullable ItemStack stack, @Nullable Potion potion, boolean apply) {
        this.stack = stack;
        this.potion = potion;
        this.apply = apply;
    }

    public static BrewingResult ofItemStack(ItemStack stack, boolean apply) {
        return new BrewingResult(stack, null, apply);
    }

    public static BrewingResult ofPotion(Potion potion) {
        return new BrewingResult(null, potion, false);
    }

    public ItemStack toResult(ItemStack input) {
        if (stack != null) {
            ItemStack output = stack.copy();
            Potion inputPotion = PotionUtil.getPotion(input);

            if (apply && inputPotion != Potions.EMPTY) PotionUtil.setPotion(output, inputPotion);

            return output;
        }
        else if (potion != null && input.getItem() instanceof PotionItem) {
            Item po = input.getItem();
            if (po instanceof LingeringPotionItem) return PotionUtil.setPotion(new ItemStack(Items.LINGERING_POTION), potion);
            else if (po instanceof SplashPotionItem) return PotionUtil.setPotion(new ItemStack(Items.SPLASH_POTION), potion);
            return PotionUtil.setPotion(new ItemStack(Items.POTION), potion);
        }
        return ItemStack.EMPTY;
    }
}
