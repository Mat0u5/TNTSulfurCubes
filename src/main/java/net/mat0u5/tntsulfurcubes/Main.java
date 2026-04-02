package net.mat0u5.tntsulfurcubes;

import net.fabricmc.api.ModInitializer;

import net.mat0u5.tntsulfurcubes.utils.ModRegistries;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main implements ModInitializer {
	public static final String MOD_ID = "tntsulfurcubes";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static MinecraftServer server;

	@Override
	public void onInitialize() {
		LOGGER.info("TNT Sulfur Cubes initialized!");
		ModRegistries.registerModStuff();
	}
}