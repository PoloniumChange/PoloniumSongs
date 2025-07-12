package com.dxdrillbassx.poloniumsongs;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.dxdrillbassx.poloniumsongs.ModConstants.*;

@Mod.EventBusSubscriber(modid = PoloniumSongs.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class JukeboxEventHandler {
    private static final Map<BlockPos, String> ACTIVE_JUKEBOXES = new HashMap<>();
    private static final Map<UUID, PlayerData> PLAYER_DATA = new HashMap<>();

    private static class PlayerData {
        double lastX, lastY, lastZ;
        long lastMoveTime;
        boolean isEmoteActive;
        long emoteStartTime;
        String emoteName;

        PlayerData(double x, double y, double z, long time, boolean emoteActive, long emoteStartTime, String emoteName) {
            this.lastX = x;
            this.lastY = y;
            this.lastZ = z;
            this.lastMoveTime = time;
            this.isEmoteActive = emoteActive;
            this.emoteStartTime = emoteStartTime;
            this.emoteName = emoteName;
        }
    }

    @SubscribeEvent
    public static void onJukeboxPlay(BlockEvent.EntityPlaceEvent event) {
        if (event.getLevel().isClientSide()) return;
        Level level = (Level) event.getLevel();
        if (level.getBlockEntity(event.getPos()) instanceof JukeboxBlockEntity jukebox) {
            Item disc = jukebox.getRecord().getItem();
            String emoteName = DISC_TO_EMOTE.get(disc);
            if (emoteName != null) {
                ACTIVE_JUKEBOXES.put(event.getPos(), emoteName);
                try {
                    level.getServer().getCommands().performPrefixedCommand(
                            level.getServer().createCommandSourceStack().withPermission(4),
                            "time set day"
                    );
                    level.getServer().getCommands().performPrefixedCommand(
                            level.getServer().createCommandSourceStack().withPermission(4),
                            "weather clear"
                    );
                } catch (Exception e) {
                    System.out.println("Failed to execute time/weather command: " + e.getMessage());
                }
                AABB aabb = new AABB(event.getPos()).inflate(JUKEBOX_RANGE);
                for (ServerPlayer player : level.getEntitiesOfClass(ServerPlayer.class, aabb)) {
                    applyEmoteAndEffect(player, event.getPos(), emoteName, disc, level);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onJukeboxStop(BlockEvent.BreakEvent event) {
        if (event.getLevel().isClientSide()) return;
        Level level = (Level) event.getLevel();
        if (level.getBlockEntity(event.getPos()) instanceof JukeboxBlockEntity) {
            ACTIVE_JUKEBOXES.remove(event.getPos());
            AABB aabb = new AABB(event.getPos()).inflate(JUKEBOX_RANGE);
            for (ServerPlayer player : level.getEntitiesOfClass(ServerPlayer.class, aabb)) {
                try {
                    level.getServer().getCommands().performPrefixedCommand(
                            level.getServer().createCommandSourceStack().withPermission(4),
                            "emotes stop " + player.getName().getString()
                    );
                    PLAYER_DATA.remove(player.getUUID());
                    NetworkHandler.INSTANCE.send(
                            PacketDistributor.PLAYER.with(() -> player),
                            new JukeboxSyncPacket(event.getPos(), "", 0.0F)
                    );
                } catch (Exception e) {
                    System.out.println("Failed to execute emote stop command for player " + player.getName().getString() + ": " + e.getMessage());
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerInteractWithJukebox(PlayerInteractEvent.RightClickBlock event) {
        if (event.getLevel().isClientSide()) return;
        BlockPos pos = event.getPos();
        Level level = event.getLevel();
        if (level.getBlockState(pos).is(Blocks.JUKEBOX) && hasMerchantNearby(level, pos)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.getLevel().isClientSide() || !(event.player instanceof ServerPlayer player)) return;

        Level level = player.getLevel();
        UUID playerUUID = player.getUUID();
        PlayerData data = PLAYER_DATA.get(playerUUID);
        BlockPos activeJukeboxPos = null;
        String emoteName = null;
        Item activeDisc = null;

        for (Map.Entry<BlockPos, String> entry : ACTIVE_JUKEBOXES.entrySet()) {
            BlockPos jukeboxPos = entry.getKey();
            JukeboxBlockEntity jukebox = (JukeboxBlockEntity) level.getBlockEntity(jukeboxPos);
            if (jukebox != null && !jukebox.getRecord().isEmpty()) {
                double distance = player.distanceToSqr(jukeboxPos.getX() + 0.5, jukeboxPos.getY() + 0.5, jukeboxPos.getZ() + 0.5);
                if (distance <= JUKEBOX_RANGE * JUKEBOX_RANGE) {
                    activeJukeboxPos = jukeboxPos;
                    emoteName = entry.getValue();
                    activeDisc = jukebox.getRecord().getItem();
                    float volume = calculateVolume((float) Math.sqrt(distance));
                    NetworkHandler.INSTANCE.send(
                            PacketDistributor.PLAYER.with(() -> player),
                            new JukeboxSyncPacket(activeJukeboxPos, emoteName, volume)
                    );
                    break;
                }
            } else {
                ACTIVE_JUKEBOXES.remove(jukeboxPos);
                NetworkHandler.INSTANCE.send(
                        PacketDistributor.PLAYER.with(() -> player),
                        new JukeboxSyncPacket(jukeboxPos, "", 0.0F)
                );
            }
        }

        if (activeJukeboxPos == null) {
            if (data != null && data.isEmoteActive) {
                try {
                    level.getServer().getCommands().performPrefixedCommand(
                            level.getServer().createCommandSourceStack().withPermission(4),
                            "emotes stop " + player.getName().getString()
                    );
                    data.isEmoteActive = false;
                    NetworkHandler.INSTANCE.send(
                            PacketDistributor.PLAYER.with(() -> player),
                            new JukeboxSyncPacket(activeJukeboxPos, "", 0.0F)
                    );
                } catch (Exception e) {
                    System.out.println("Failed to execute emote stop command for player " + player.getName().getString() + ": " + e.getMessage());
                }
            }
            return;
        }

        MobEffectInstance effect = DISC_TO_EFFECT.get(activeDisc);
        if (effect != null) {
            player.addEffect(new MobEffectInstance(effect));
        }

        if (data == null) {
            applyEmoteAndEffect(player, activeJukeboxPos, emoteName, activeDisc, level);
        } else {
            double distanceMoved = player.getX() * player.getX() + player.getY() * player.getY() + player.getZ() * player.getZ() - (data.lastX * data.lastX + data.lastY * data.lastY + data.lastZ * data.lastZ);
            if (distanceMoved > 0.01) {
                if (data.isEmoteActive) {
                    try {
                        level.getServer().getCommands().performPrefixedCommand(
                                level.getServer().createCommandSourceStack().withPermission(4),
                                "emotes stop " + player.getName().getString()
                        );
                        data.isEmoteActive = false;
                    } catch (Exception e) {
                        System.out.println("Failed to execute emote stop command for player " + player.getName().getString() + ": " + e.getMessage());
                    }
                }
                data.lastX = player.getX();
                data.lastY = player.getY();
                data.lastZ = player.getZ();
                data.lastMoveTime = level.getGameTime();
            } else if (!data.isEmoteActive && level.getGameTime() - data.lastMoveTime >= INACTIVITY_THRESHOLD) {
                applyEmoteAndEffect(player, activeJukeboxPos, emoteName, activeDisc, level);
            }
        }
    }

    private static void applyEmoteAndEffect(ServerPlayer player, BlockPos jukeboxPos, String emoteName, Item disc, Level level) {
        try {
            level.getServer().getCommands().performPrefixedCommand(
                    level.getServer().createCommandSourceStack().withPermission(4),
                    "emotes play \"" + emoteName + "\" " + player.getName().getString() + " true"
            );
            PLAYER_DATA.put(player.getUUID(), new PlayerData(
                    player.getX(), player.getY(), player.getZ(), level.getGameTime(), true, level.getGameTime(), emoteName
            ));
            float volume = calculateVolume((float) player.distanceToSqr(jukeboxPos.getX() + 0.5, jukeboxPos.getY() + 0.5, jukeboxPos.getZ() + 0.5));
            NetworkHandler.INSTANCE.send(
                    PacketDistributor.PLAYER.with(() -> player),
                    new JukeboxSyncPacket(jukeboxPos, emoteName, volume)
            );
        } catch (Exception e) {
            System.out.println("Failed to execute emote command for " + emoteName + " on player " + player.getName().getString() + ": " + e.getMessage());
        }
    }

    private static float calculateVolume(float distance) {
        if (distance <= JUKEBOX_FULL_VOLUME_RANGE) return BASE_VOLUME;
        if (distance >= JUKEBOX_RANGE) return 0.0F;
        float t = (distance - (float) JUKEBOX_FULL_VOLUME_RANGE) / ((float) JUKEBOX_RANGE - (float) JUKEBOX_FULL_VOLUME_RANGE);
        return BASE_VOLUME * (1.0F - t);
    }

    private static boolean hasMerchantNearby(Level level, BlockPos jukeboxPos) {
        return !level.getEntitiesOfClass(MusicMerchantEntity.class, new AABB(jukeboxPos).inflate(5.0D)).isEmpty();
    }
}