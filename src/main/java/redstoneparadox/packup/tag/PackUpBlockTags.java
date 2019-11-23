package redstoneparadox.packup.tag;

import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.block.Block;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

public class PackUpBlockTags {

    public static Tag<Block> DEFAULT_DROPS = register("default_drops");

    private static Tag<Block> register(String id) {
        return TagRegistry.block(new Identifier("packup", id));
    }
}
