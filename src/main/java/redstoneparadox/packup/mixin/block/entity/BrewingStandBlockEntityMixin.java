package redstoneparadox.packup.mixin.block.entity;

import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.BrewingStandBlockEntity;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.DefaultedList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import redstoneparadox.packup.recipe.BrewingRecipe;

import java.util.List;

@Mixin(BrewingStandBlockEntity.class)
public abstract class BrewingStandBlockEntityMixin extends LockableContainerBlockEntity implements SidedInventory {

    @Shadow private DefaultedList<ItemStack> inventory;

    @Shadow public abstract ItemStack getInvStack(int slot);

    protected BrewingStandBlockEntityMixin(BlockEntityType<?> blockEntityType) {
        super(blockEntityType);
    }

    @Inject(method = "canCraft", at = @At("HEAD"), cancellable = true)
    private void canCraft(CallbackInfoReturnable<Boolean> cir) {
       List<BrewingRecipe> recipes = world.getRecipeManager().getAllMatches(BrewingRecipe.TYPE, this, world);

       if (!recipes.isEmpty()) {
           cir.setReturnValue(true);
           cir.cancel();
       }
    }

    @Inject(method = "craft", at = @At("HEAD"), cancellable = true)
    private void craft(CallbackInfo ci) {
        List<BrewingRecipe> recipes = world.getRecipeManager().getAllMatches(BrewingRecipe.TYPE, this, world);

        for (int i = 0; i < 3; i++) {
            for (BrewingRecipe recipe: recipes) {
                if (recipe.matches(this, i)) {
                    inventory.set(i, recipe.craft(this));
                }
            }
        }
    }

    /**
     * @author RedstoneParadox
     *
     * I'm so, so, very sorry.
     */
    @Overwrite()
    public boolean isValidInvStack(int slot, ItemStack stack) {
        if (slot == 4) return stack.getItem() == Items.BLAZE_POWDER;
        return getInvStack(slot).isEmpty();
    }
}
