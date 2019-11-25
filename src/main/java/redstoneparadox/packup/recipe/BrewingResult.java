package redstoneparadox.packup.recipe;

import net.minecraft.item.*;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;

import javax.annotation.Nullable;

public class BrewingResult {

    private final @Nullable ItemStack stack;
    private final @Nullable Potion potion;

    private BrewingResult(@Nullable ItemStack stack, @Nullable Potion potion) {
        this.stack = stack;
        this.potion = potion;
    }

    public static BrewingResult ofItemStack(ItemStack stack) {
        return new BrewingResult(stack, null);
    }

    public static BrewingResult ofPotion(Potion potion) {
        return new BrewingResult(null, potion);
    }

    public ItemStack toResult(ItemStack toTransform) {
        if (stack != null) {
            return stack.copy();
        }
        else if (potion != null && toTransform.getItem() instanceof PotionItem) {
            Item po = toTransform.getItem();
            if (po instanceof LingeringPotionItem) return PotionUtil.setPotion(new ItemStack(Items.LINGERING_POTION), potion);
            else if (po instanceof SplashPotionItem) return PotionUtil.setPotion(new ItemStack(Items.SPLASH_POTION), potion);
            return PotionUtil.setPotion(new ItemStack(Items.POTION), potion);
        }
        return ItemStack.EMPTY;
    }
}
