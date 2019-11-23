package redstoneparadox.packup.mixin.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.context.LootContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import redstoneparadox.packup.tag.PackUpBlockTags;

import java.util.ArrayList;
import java.util.List;

@Mixin(Block.class)
public abstract class BlockMixin {

    @Inject(method = "getDroppedStacks(Lnet/minecraft/block/BlockState;Lnet/minecraft/loot/context/LootContext$Builder;)Ljava/util/List;", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/loot/LootManager;getSupplier(Lnet/minecraft/util/Identifier;)Lnet/minecraft/loot/LootTable;"), locals = LocalCapture.CAPTURE_FAILEXCEPTION, cancellable = true)
    private void createDefaultDrops(BlockState state, LootContext.Builder builder, CallbackInfoReturnable<List<ItemStack>> cir, Identifier identifier, LootContext lootContext, ServerWorld serverWorld, LootTable lootTable) {
        Block self = (Block)(Object) this;

        if (lootTable == LootTable.EMPTY) {
            Item item = self.asItem();
            if (item != Items.AIR) {
                List<ItemStack> list = new ArrayList<>();
                list.add(new ItemStack(item));
                cir.setReturnValue(list);
                cir.cancel();
            }
        }
    }
}
