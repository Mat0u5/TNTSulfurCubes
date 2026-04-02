package net.mat0u5.tntsulfurcubes.events;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.mat0u5.tntsulfurcubes.Main;
import net.minecraft.server.MinecraftServer;

public class Events {
    public static void register() {
        ServerLifecycleEvents.SERVER_STARTING.register(Events::onServerStarting);
        ServerLifecycleEvents.SERVER_STARTED.register(Events::onServerStart);
        ServerLifecycleEvents.SERVER_STOPPING.register(Events::onServerStopping);
        ServerTickEvents.END_SERVER_TICK.register(Events::onServerTickEnd);
    }

    private static void onServerStopping(MinecraftServer server) {
    }

    private static void onServerStarting(MinecraftServer server) {
        Main.server = server;
    }

    private static void onServerStart(MinecraftServer server) {
        Main.server = server;
    }

    private static void onServerTickEnd(MinecraftServer server) {

    }
}
