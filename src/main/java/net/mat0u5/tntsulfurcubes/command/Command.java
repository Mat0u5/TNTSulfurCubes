package net.mat0u5.tntsulfurcubes.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;

import static net.minecraft.commands.Commands.literal;

public class Command {

    public static void register(CommandDispatcher<CommandSourceStack> serverCommandSourceCommandDispatcher,
                                CommandBuildContext commandRegistryAccess,
                                Commands.CommandSelection registrationEnvironment) {
        serverCommandSourceCommandDispatcher.register(
                literal("tntsulfurcubes")
                        .then(literal("test")
                                .executes(context -> Command.testCommand(
                                        context.getSource())
                                )
                        )
        );
    }

    public static int testCommand(CommandSourceStack source) {
        MinecraftServer server = source.getServer();
        final Player self = source.getPlayer();



        return 1;
    }
}
