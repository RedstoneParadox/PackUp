package redstoneparadox.packup.util;

import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.container.Slot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.PotionItem;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.server.network.ServerPlayerEntity;

public class Slots {

    public static class IngredientSlot extends Slot {
        public IngredientSlot(Inventory inventory, int invSlot, int xPosition, int yPosition) {
            super(inventory, invSlot, xPosition, yPosition);
        }

        @Override
        public boolean canInsert(ItemStack stack) {
            return true;
        }

        @Override
        public int getMaxStackAmount() {
            return 64;
        }
    }

    public static class PotionSlot extends Slot {

        public PotionSlot(Inventory inventory, int invSlot, int xPosition, int yPosition) {
            super(inventory, invSlot, xPosition, yPosition);
        }

        @Override
        public boolean canInsert(ItemStack stack) {
            return true;
        }

        @Override
        public int getMaxStackAmount() {
            return 1;
        }

        @Override
        public ItemStack onTakeItem(PlayerEntity player, ItemStack stack) {
            if (stack.getItem() instanceof PotionItem) {
                Potion potion = PotionUtil.getPotion(stack);
                if (player instanceof ServerPlayerEntity) {
                    Criterions.BREWED_POTION.trigger((ServerPlayerEntity)player, potion);
                }
            }

            super.onTakeItem(player, stack);
            return stack;
        }
    }

    public static class FuelSlot extends Slot {

        public FuelSlot(Inventory inventory, int invSlot, int xPosition, int yPosition) {
            super(inventory, invSlot, xPosition, yPosition);
        }

        @Override
        public boolean canInsert(ItemStack stack) {
            return stack.getItem() == Items.BLAZE_POWDER;
        }
    }
}
