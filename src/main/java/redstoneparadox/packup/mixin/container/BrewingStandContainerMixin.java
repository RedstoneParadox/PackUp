package redstoneparadox.packup.mixin.container;

import net.minecraft.container.*;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import redstoneparadox.packup.util.Slots;

import javax.annotation.Nullable;

@Mixin(BrewingStandContainer.class)
public abstract class BrewingStandContainerMixin extends Container {

    @Mutable
    @Shadow @Final private Slot ingredientSlot;

    protected BrewingStandContainerMixin(@Nullable ContainerType<?> type, int syncId) {
        super(type, syncId);
    }

    @Inject(method = "<init>(ILnet/minecraft/entity/player/PlayerInventory;Lnet/minecraft/inventory/Inventory;Lnet/minecraft/container/PropertyDelegate;)V", at = @At("RETURN"))
    private void constructor(int i, PlayerInventory playerInventory, Inventory inventory, PropertyDelegate propertyDelegate, CallbackInfo ci) {
        slotList.clear();
        addSlot(new Slots.PotionSlot(inventory, 0, 56, 51));
        addSlot(new Slots.PotionSlot(inventory, 1, 79, 58));
        addSlot(new Slots.PotionSlot(inventory, 2, 102, 51));
        ingredientSlot = addSlot(new Slots.IngredientSlot(inventory, 3, 79, 17));
        addSlot(new Slots.FuelSlot(inventory, 4, 17, 17));

        int l;
        for(l = 0; l < 3; ++l) {
            for(int k = 0; k < 9; ++k) {
                addSlot(new Slot(playerInventory, k + l * 9 + 9, 8 + k * 18, 84 + l * 18));
            }
        }

        for(l = 0; l < 9; ++l) {
            addSlot(new Slot(playerInventory, l, 8 + l * 18, 142));
        }
    }
}
