package net.mat0u5.tntsulfurcubes.platform.fabric;

//? fabric {

import net.mat0u5.tntsulfurcubes.Main;
import dev.kikugie.fletching_table.annotation.fabric.Entrypoint;
import net.fabricmc.api.ModInitializer;

@Entrypoint("main")
public class FabricEntrypoint implements ModInitializer {

	@Override
	public void onInitialize() {
		Main.onInitialize();
	}
}
//?}
