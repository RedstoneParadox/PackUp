package redstoneparadox.packup;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import redstoneparadox.packup.recipe.BrewingRecipe;
import redstoneparadox.packup.recipe.DummyRecipe;
import redstoneparadox.packup.test.PackUpTests;

@SuppressWarnings("unused")
public class PackUp implements ModInitializer {

    @Override
    public void onInitialize() {
        if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
            PackUpTests.setup();
        }
    }

    static {
        final RecipeType<BrewingRecipe> type = BrewingRecipe.TYPE;
        final RecipeSerializer<BrewingRecipe> serializer = BrewingRecipe.SERIALIZER;

        final RecipeType<DummyRecipe> dummyType = DummyRecipe.TYPE;
        final RecipeSerializer<DummyRecipe> serializer1 = DummyRecipe.SERIALIZER;
    }
}
