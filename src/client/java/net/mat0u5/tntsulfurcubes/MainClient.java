package net.mat0u5.tntsulfurcubes;

import net.fabricmc.api.ClientModInitializer;

public class MainClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        Main.LOGGER.info("[Client] TNT Sulfur Cubes initialized!");
    }
}
