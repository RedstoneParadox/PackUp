package redstoneparadox.packup;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import redstoneparadox.packup.test.PackUpTests;

@SuppressWarnings("unused")
public class PackUp implements ModInitializer {

    @Override
    public void onInitialize() {
        if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
            PackUpTests.setup();
        }
    }
}
