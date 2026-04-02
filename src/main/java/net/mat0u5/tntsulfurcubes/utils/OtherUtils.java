package net.mat0u5.tntsulfurcubes.utils;

import net.mat0u5.tntsulfurcubes.Main;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import static net.mat0u5.tntsulfurcubes.Main.server;

public class OtherUtils {

    public static void log(Component message) {
        for (ServerPlayer player : PlayerUtils.getAllPlayers()) {
            player.sendSystemMessage(message);
        }
        Main.LOGGER.info(message.getString());
    }

    public static void log(String string) {
        Component message = Component.nullToEmpty(string);
        for (ServerPlayer player : PlayerUtils.getAllPlayers()) {
            player.sendSystemMessage(message);
        }
        Main.LOGGER.info(string);
    }

    public static void logConsole(String string) {
        Main.LOGGER.info(string);
    }
    public static void executeCommand(String command) {
        try {
            if (server == null) return;
            Commands manager = server.getCommands();
            CommandSourceStack commandSource = server.createCommandSourceStack().withSuppressedOutput();
            manager.performPrefixedCommand(commandSource, command);
        } catch (Exception e) {
            Main.LOGGER.error("Error executing command: " + command, e);
        }
    }
/*
    public static void logIfClient(String string) {
        if (Main.hasClient()) {
            Main.LOGGER.info(string);
        }
    }
*/
}
