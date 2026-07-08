package net.mat0u5.tntsulfurcubes;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {

	public static final String MOD_ID = "tntsulfurcubes";
	public static final String MOD_VERSION = "1.2.0";
	public static final String MOD_FRIENDLY_NAME = "TNT Sulfur Cubes";
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

	public static void onInitialize() {
		LOGGER.info("Initializing {}", MOD_ID);
		LOGGER.info("{}: { version: {}; friendly_name: {} }", MOD_ID, MOD_VERSION, MOD_FRIENDLY_NAME);
	}
}
