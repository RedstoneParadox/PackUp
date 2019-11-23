package redstoneparadox.packup.recipe;

import net.minecraft.util.Pair;

import java.util.List;

class Template {
    final List<Pair<String, String>> fills;

    Template(List<Pair<String, String>> fills) {
        this.fills = fills;
    }
}
