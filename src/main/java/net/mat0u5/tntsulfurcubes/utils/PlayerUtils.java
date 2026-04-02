package net.mat0u5.tntsulfurcubes.utils;

import net.mat0u5.tntsulfurcubes.Main;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.objects.PlayerSprite;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.*;
import static net.mat0u5.tntsulfurcubes.Main.server;

public class PlayerUtils {
    private static HashMap<Component, Integer> broadcastCooldown = new HashMap<>();

    public static void sendTitleWithSubtitle(ServerPlayer player, Component title, Component subtitle, int fadeIn, int stay, int fadeOut) {
        if (server == null) return;
        if (player == null) return;
        ClientboundSetTitlesAnimationPacket fadePacket = new ClientboundSetTitlesAnimationPacket(fadeIn, stay, fadeOut);
        player.connection.send(fadePacket);
        ClientboundSetTitleTextPacket titlePacket = new ClientboundSetTitleTextPacket(title);
        player.connection.send(titlePacket);
        ClientboundSetSubtitleTextPacket subtitlePacket = new ClientboundSetSubtitleTextPacket(subtitle);
        player.connection.send(subtitlePacket);
    }

    public static void sendTitle(ServerPlayer player, Component title, int fadeIn, int stay, int fadeOut) {
        if (server == null) return;
        if (player == null) return;
        ClientboundSetTitlesAnimationPacket fadePacket = new ClientboundSetTitlesAnimationPacket(fadeIn, stay, fadeOut);
        player.connection.send(fadePacket);
        ClientboundSetTitleTextPacket titlePacket = new ClientboundSetTitleTextPacket(title);
        player.connection.send(titlePacket);
    }

    public static void sendTitleToPlayers(Collection<ServerPlayer> players, Component title, int fadeIn, int stay, int fadeOut) {
        for (ServerPlayer player : players) {
            sendTitle(player, title, fadeIn, stay, fadeOut);
        }
    }

    public static void sendTitleWithSubtitleToPlayers(Collection<ServerPlayer> players, Component title, Component subtitle, int fadeIn, int stay, int fadeOut) {
        for (ServerPlayer player : players) {
            sendTitleWithSubtitle(player, title, subtitle, fadeIn, stay, fadeOut);
        }
    }

    public static void playSoundToPlayers(Collection<ServerPlayer> players, SoundEvent sound) {
        playSoundToPlayers(players,sound,SoundSource.MASTER,1,1);
    }
    public static void playSoundToPlayers(Collection<ServerPlayer> players, SoundEvent sound, float volume, float pitch) {
        playSoundToPlayers(players,sound, SoundSource.MASTER, volume, pitch);
    }
    public static void playNotifySound(ServerPlayer player, SoundEvent sound, SoundSource soundSource, float volume, float pitch) {
        player.connection
                .send(
                        new ClientboundSoundPacket(
                                BuiltInRegistries.SOUND_EVENT.wrapAsHolder(sound), soundSource, player.getX(), player.getY(), player.getZ(), volume, pitch, player.getRandom().nextLong()
                        )
                );
    }

    public static void playSoundToPlayers(Collection<ServerPlayer> players, SoundEvent sound, SoundSource soundCategory, float volume, float pitch) {
        for (ServerPlayer player : players) {
            if (player == null) continue;
            playNotifySound(player, sound, soundCategory, volume, pitch);
        }
    }

    public static void playSoundToPlayer(ServerPlayer player, SoundEvent sound) {
        playSoundToPlayer(player, sound, 1, 1);
    }

    public static void playSoundToPlayer(ServerPlayer player, SoundEvent sound, float volume, float pitch) {
        if (player == null) return;
        playNotifySound(player, sound, SoundSource.MASTER, volume, pitch);
    }

    private static final Random rnd = new Random();
    public static void playSoundWithSourceToPlayers(Entity source, SoundEvent sound, SoundSource soundCategory, float volume, float pitch) {
        playSoundWithSourceToPlayers(getAllPlayers(), source, sound, soundCategory, volume, pitch);
    }
    public static void playSoundWithSourceToPlayers(Collection<ServerPlayer> players, Entity source, SoundEvent sound, SoundSource soundCategory, float volume, float pitch) {
        ClientboundSoundEntityPacket packet = new ClientboundSoundEntityPacket(BuiltInRegistries.SOUND_EVENT.wrapAsHolder(sound), soundCategory, source, volume, pitch, rnd.nextLong());
        for (ServerPlayer player : players) {
            player.connection.send(packet);
        }
    }

    public static List<ServerPlayer> getAllPlayers() {
        List<ServerPlayer> result = new ArrayList<>();
        if (server == null) return result;
        result.addAll(server.getPlayerList().getPlayers());
        return result;
    }

    public static List<ServerPlayer> getAdminPlayers() {
        List<ServerPlayer> result = getAllPlayers();
        result.removeIf(player -> !PermissionManager.isAdmin(player));
        return result;
    }

    public static ServerPlayer getPlayer(String name) {
        if (server == null || name == null) return null;
        return server.getPlayerList().getPlayerByName(name);
    }

    public static ServerPlayer getPlayer(UUID uuid) {
        if (server == null || uuid == null) return null;
        return server.getPlayerList().getPlayer(uuid);
    }

    public static List<ItemStack> getPlayerInventory(ServerPlayer player) {
        List<ItemStack> list = new ArrayList<>();
        Container inventory = player.getInventory();
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack itemStack = inventory.getItem(i);
            if (!itemStack.isEmpty()) {
                list.add(itemStack);
            }
        }
        return list;
    }

    public static void clearItemStack(ServerPlayer player, ItemStack itemStack) {
        if (itemStack == null || itemStack.isEmpty()) return;
        Container inventory = player.getInventory();
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (stack.equals(itemStack)) {
                inventory.removeItemNoUpdate(i);
            }
        }
    }

    public static Entity getEntityLookingAt(ServerPlayer player, double maxDistance) {
        Vec3 start = player.getEyePosition(1.0F);
        Vec3 direction = player.getViewVector(1.0F).normalize().scale(maxDistance);
        Vec3 end = start.add(direction);

        HitResult entityHit = ProjectileUtil.getEntityHitResult(player, start, end,
                player.getBoundingBox().expandTowards(direction).inflate(1.0),
                entity -> !entity.isSpectator() && entity.isAlive(), maxDistance*maxDistance);

        if (entityHit instanceof EntityHitResult entityHitResult) {
            return entityHitResult.getEntity();
        }

        return null;
    }
    public static Vec3 getPosLookingAt(ServerPlayer player, double maxDistance) {
        HitResult blockHit = player.pick(maxDistance, 1, false);
        if (Math.sqrt(blockHit.distanceTo(player)) >= (maxDistance*0.99)) {
            return null;
        }
        if (blockHit instanceof BlockHitResult blockHitResult) {
            return blockHitResult.getLocation();
        }
        return null;
    }

    public static void resendCommandTree(ServerPlayer player) {
        if (player == null) return;
        if (server == null) return;
        server.getCommands().sendCommands(player);
    }

    public static void resendCommandTrees() {
        for (ServerPlayer player : getAllPlayers()) {
            resendCommandTree(player);
        }
    }

    public static ItemStack getEquipmentSlot(Player player, int slot) {
        return player.getInventory().getItem(slot + 36);
    }

    public static List<ItemStack> getArmorItems(ServerPlayer player) {
        List<ItemStack> result = new ArrayList<>();
        result.add(getEquipmentSlot(player, 0));
        result.add(getEquipmentSlot(player, 1));
        result.add(getEquipmentSlot(player, 2));
        result.add(getEquipmentSlot(player, 3));
        return result;
    }

    public static void broadcastMessage(Component message) {
        broadcastMessage(message, 1);
    }

    public static void broadcastMessageToAdmins(Component message) {
        broadcastMessageToAdmins(message, 1);
    }

    public static void broadcastMessage(List<ServerPlayer> players, Component message) {
        for (ServerPlayer player : players) {
            player.sendSystemMessage(message);
        }
    }

    public static void broadcastMessageExcept(Component message, ServerPlayer exceptPlayer) {
        for (ServerPlayer player : PlayerUtils.getAllPlayers()) {
            if (player == exceptPlayer) continue;
            player.sendSystemMessage(message);
        }
    }

    public static void broadcastMessage(Component message, int cooldownTicks) {
        if (broadcastCooldown.containsKey(message)) return;
        broadcastCooldown.put(message, cooldownTicks);

        for (ServerPlayer player : PlayerUtils.getAllPlayers()) {
            player.sendSystemMessage(message);
        }
    }

    public static void broadcastMessageToAdmins(Component message, int cooldownTicks) {
        if (broadcastCooldown.containsKey(message)) return;
        broadcastCooldown.put(message, cooldownTicks);

        for (ServerPlayer player : PlayerUtils.getAdminPlayers()) {
            player.sendSystemMessage(message);
        }
        Main.LOGGER.info(message.getString());
    }
}
