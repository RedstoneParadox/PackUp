package redstoneparadox.packup.test;

import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.registry.Registry;

public class PackUpTests {

    private static Block TEST_ONE_BLOCK = new Block(FabricBlockSettings.copy(Blocks.DIRT).build());
    private static BlockItem TEST_ONE_ITEM = new BlockItem(TEST_ONE_BLOCK, new Item.Settings());

    public static void setup() {
        Registry.register(Registry.BLOCK, "packup:test_one_block", TEST_ONE_BLOCK);
        Registry.register(Registry.ITEM, "packup:test_one_item", TEST_ONE_ITEM);
    }
}
