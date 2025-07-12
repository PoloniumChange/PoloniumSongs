package com.dxdrillbassx.poloniumsongs;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.PacketDistributor;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = PoloniumSongs.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class JukeboxEventHandler {

    // Карта соответствия дисков и эмоций
    private static final Map<Item, String> DISC_TO_EMOTE = new HashMap<>();
    // Карта соответствия дисков и эффектов
    private static final Map<Item, MobEffectInstance> DISC_TO_EFFECT = new HashMap<>();
    // Хранение активного проигрывателя: BlockPos -> эмоция
    private static final Map<BlockPos, String> ACTIVE_JUKEBOXES = new HashMap<>();
    // Хранение данных игроков: UUID -> {последняя позиция, время бездействия, состояние эмоции, время начала эмоции}
    private static final Map<UUID, PlayerData> PLAYER_DATA = new HashMap<>();
    // Радиус слышимости проигрывателя
    private static final double JUKEBOX_RANGE = 20.0;
    // Минимальный радиус полной громкости
    private static final double JUKEBOX_FULL_VOLUME_RANGE = 5.0;
    // Время бездействия (в тиках) до возобновления эмоции (5 секунд = 100 тиков)
    private static final int INACTIVITY_THRESHOLD = 100;
    // Базовая громкость для фонового радио
    private static final float BASE_VOLUME = 0.3F;

    private static class PlayerData {
        double lastX, lastY, lastZ;
        long lastMoveTime;
        boolean isEmoteActive;
        long emoteStartTime; // Время начала эмоции для синхронизации
        String emoteName; // Текущая эмоция

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

    // Инициализация карт в FMLCommonSetupEvent
    @Mod.EventBusSubscriber(modid = PoloniumSongs.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class SetupEvents {
        @SubscribeEvent
        public static void onCommonSetup(FMLCommonSetupEvent event) {
            event.enqueueWork(() -> {
                // Эмоции
                DISC_TO_EMOTE.put(PoloniumSongs.POLONIUM_DISC_1.get(), "spemotes.emote.name.SPE_JustDance");
                DISC_TO_EMOTE.put(PoloniumSongs.POLONIUM_DISC_2.get(), "spemotes.emote.name.SPE_Ankha Dance");
                DISC_TO_EMOTE.put(PoloniumSongs.POLONIUM_DISC_3.get(), "spemotes.emote.name.SPE_Boy With Luv");
                DISC_TO_EMOTE.put(PoloniumSongs.POLONIUM_DISC_4.get(), "spemotes.emote.name.SPE_Headphones");
                DISC_TO_EMOTE.put(PoloniumSongs.POLONIUM_DISC_5.get(), "spemotes.emote.name.SPE_Dance2");
                DISC_TO_EMOTE.put(PoloniumSongs.POLONIUM_DISC_6.get(), "spemotes.emote.name.SPE_Slick Back");
                DISC_TO_EMOTE.put(PoloniumSongs.POLONIUM_DISC_7.get(), "spemotes.emote.name.SPE_Boogie down");
                DISC_TO_EMOTE.put(PoloniumSongs.POLONIUM_DISC_8.get(), "spemotes.emote.name.SPE_Torture crackdown");
                DISC_TO_EMOTE.put(PoloniumSongs.POLONIUM_DISC_9.get(), "spemotes.emote.name.SPE_EgoRock");
                DISC_TO_EMOTE.put(PoloniumSongs.POLONIUM_DISC_10.get(), "Shrugging");

                // Эффекты
                DISC_TO_EFFECT.put(PoloniumSongs.POLONIUM_DISC_1.get(), new MobEffectInstance(MobEffects.GLOWING, 100, 0));
                DISC_TO_EFFECT.put(PoloniumSongs.POLONIUM_DISC_2.get(), new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 100, 1));
                DISC_TO_EFFECT.put(PoloniumSongs.POLONIUM_DISC_6.get(), new MobEffectInstance(MobEffects.GLOWING, 100, 0));
                DISC_TO_EFFECT.put(PoloniumSongs.POLONIUM_DISC_7.get(), new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 100, 1));
            });
        }
    }

    @SubscribeEvent
    public static void onJukeboxPlay(BlockEvent.EntityPlaceEvent event) {
        if (event.getLevel().isClientSide()) return; // Обрабатываем только на сервере

        if (event.getLevel() instanceof Level level) {
            if (level.getBlockEntity(event.getPos()) instanceof JukeboxBlockEntity jukebox) {
                Item disc = jukebox.getRecord().getItem();
                String emoteName = DISC_TO_EMOTE.get(disc);
                MobEffectInstance effect = DISC_TO_EFFECT.get(disc);
                if (emoteName != null) {
                    // Сохраняем активный проигрыватель и эмоцию
                    ACTIVE_JUKEBOXES.put(event.getPos(), emoteName);
                    // Устанавливаем дневное время и ясную погоду
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
                    // Запускаем эмоцию и эффект для всех игроков в радиусе
                    AABB aabb = new AABB(event.getPos()).inflate(JUKEBOX_RANGE);
                    for (Player player : level.getEntitiesOfClass(ServerPlayer.class, aabb)) {
                        try {
                            // Запускаем эмоцию
                            String command = "emotes play \"" + emoteName + "\" " + player.getName().getString() + " true";
                            level.getServer().getCommands().performPrefixedCommand(
                                    level.getServer().createCommandSourceStack().withPermission(4),
                                    command
                            );
                            // Применяем эффект, если он есть
                            if (effect != null && player instanceof ServerPlayer serverPlayer) {
                                serverPlayer.addEffect(new MobEffectInstance(effect));
                            }
                            // Сохраняем начальную позицию игрока, время начала эмоции и устанавливаем эмоцию как активную
                            PLAYER_DATA.put(player.getUUID(), new PlayerData(
                                    player.getX(), player.getY(), player.getZ(), level.getGameTime(), true, level.getGameTime(), emoteName
                            ));
                            // Отправляем сетевой пакет для синхронизации с низкой громкостью
                            float volume = BASE_VOLUME;
                            if (hasMerchantNearby(level, event.getPos())) {
                                volume = calculateVolume((float) player.distanceToSqr(event.getPos().getX() + 0.5, event.getPos().getY() + 0.5, event.getPos().getZ() + 0.5));
                            }
                            NetworkHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new JukeboxSyncPacket(event.getPos(), emoteName, volume));
                        } catch (Exception e) {
                            System.out.println("Failed to execute emote command for " + emoteName + " on player " + player.getName().getString() + ": " + e.getMessage());
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onJukeboxStop(BlockEvent.BreakEvent event) {
        if (event.getLevel().isClientSide()) return; // Обрабатываем только на сервере

        if (event.getLevel() instanceof Level level) {
            if (level.getBlockEntity(event.getPos()) instanceof JukeboxBlockEntity) {
                // Удаляем проигрыватель из активных
                ACTIVE_JUKEBOXES.remove(event.getPos());
                // Останавливаем эмоции и эффекты для всех игроков в радиусе
                AABB aabb = new AABB(event.getPos()).inflate(JUKEBOX_RANGE);
                for (Player player : level.getEntitiesOfClass(ServerPlayer.class, aabb)) {
                    try {
                        String command = "emotes stop " + player.getName().getString();
                        level.getServer().getCommands().performPrefixedCommand(
                                level.getServer().createCommandSourceStack().withPermission(4),
                                command
                        );
                        // Удаляем данные игрока
                        PLAYER_DATA.remove(player.getUUID());
                        // Отправляем пакет для остановки звука
                        NetworkHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new JukeboxSyncPacket(event.getPos(), "", 0.0F));
                    } catch (Exception e) {
                        System.out.println("Failed to execute emote stop command for player " + player.getName().getString() + ": " + e.getMessage());
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerInteractWithJukebox(PlayerInteractEvent.RightClickBlock event) {
        if (event.getLevel().isClientSide()) return; // Обрабатываем только на сервере

        BlockPos pos = event.getPos();
        Level level = event.getLevel();
        BlockState state = level.getBlockState(pos);

        // Проверяем, является ли блок проигрывателем
        if (state.is(Blocks.JUKEBOX)) {
            // Проверяем наличие MusicMerchantEntity в радиусе 5 блоков
            AABB merchantArea = new AABB(pos).inflate(5.0D);
            boolean hasMerchantNearby = !level.getEntitiesOfClass(MusicMerchantEntity.class, merchantArea).isEmpty();
            if (hasMerchantNearby) {
                // Отменяем взаимодействие с проигрывателем
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.getLevel().isClientSide()) return; // Обрабатываем только на сервере в конце тика

        Player player = event.player;
        if (!(player instanceof ServerPlayer serverPlayer)) return;

        Level level = serverPlayer.getLevel();
        UUID playerUUID = player.getUUID();
        PlayerData data = PLAYER_DATA.get(playerUUID);

        // Проверяем, находится ли игрок в зоне активного проигрывателя
        boolean inJukeboxRange = false;
        BlockPos activeJukeboxPos = null;
        String emoteName = null;
        Item activeDisc = null;

        for (Map.Entry<BlockPos, String> entry : ACTIVE_JUKEBOXES.entrySet()) {
            BlockPos jukeboxPos = entry.getKey();
            if (level.getBlockEntity(jukeboxPos) instanceof JukeboxBlockEntity jukebox && !jukebox.getRecord().isEmpty()) {
                double distance = player.distanceToSqr(jukeboxPos.getX() + 0.5, jukeboxPos.getY() + 0.5, jukeboxPos.getZ() + 0.5);
                if (distance <= JUKEBOX_RANGE * JUKEBOX_RANGE) {
                    inJukeboxRange = true;
                    activeJukeboxPos = jukeboxPos;
                    emoteName = entry.getValue();
                    activeDisc = jukebox.getRecord().getItem();
                    // Вычисляем громкость на основе расстояния
                    float volume = calculateVolume((float) Math.sqrt(distance));
                    // Отправляем пакет для синхронизации громкости
                    NetworkHandler.INSTANCE.send(
                            PacketDistributor.PLAYER.with(() -> serverPlayer),
                            new JukeboxSyncPacket(activeJukeboxPos, emoteName, volume)
                    );
                    break;
                }
            } else {
                // Удаляем неактивный проигрыватель
                ACTIVE_JUKEBOXES.remove(jukeboxPos);
                NetworkHandler.INSTANCE.send(
                        PacketDistributor.PLAYER.with(() -> serverPlayer),
                        new JukeboxSyncPacket(jukeboxPos, "", 0.0F)
                );
            }
        }

        if (!inJukeboxRange) {
            // Если игрок вне зоны, останавливаем эмоцию и звук
            if (data != null && data.isEmoteActive) {
                try {
                    String command = "emotes stop " + player.getName().getString();
                    level.getServer().getCommands().performPrefixedCommand(
                            level.getServer().createCommandSourceStack().withPermission(4),
                            command
                    );
                    data.isEmoteActive = false;
                    // Отправляем пакет для остановки звука
                    NetworkHandler.INSTANCE.send(
                            PacketDistributor.PLAYER.with(() -> serverPlayer),
                            new JukeboxSyncPacket(activeJukeboxPos, "", 0.0F)
                    );
                } catch (Exception e) {
                    System.out.println("Failed to execute emote stop command for player " + player.getName().getString() + ": " + e.getMessage());
                }
            }
            return;
        }

        // Применяем эффект, если он есть
        MobEffectInstance effect = DISC_TO_EFFECT.get(activeDisc);
        if (effect != null) {
            serverPlayer.addEffect(new MobEffectInstance(effect));
        }

        // Проверяем движение игрока
        if (data == null) {
            // Если данных нет, но игрок в зоне, инициализируем их
            data = new PlayerData(player.getX(), player.getY(), player.getZ(), level.getGameTime(), true, level.getGameTime(), emoteName);
            PLAYER_DATA.put(playerUUID, data);
            try {
                String command = "emotes play \"" + emoteName + "\" " + player.getName().getString() + " true";
                level.getServer().getCommands().performPrefixedCommand(
                        level.getServer().createCommandSourceStack().withPermission(4),
                        command
                );
                // Отправляем сетевой пакет для синхронизации
                float volume = calculateVolume((float) player.distanceToSqr(activeJukeboxPos.getX() + 0.5, activeJukeboxPos.getY() + 0.5, activeJukeboxPos.getZ() + 0.5));
                NetworkHandler.INSTANCE.send(
                        PacketDistributor.PLAYER.with(() -> serverPlayer),
                        new JukeboxSyncPacket(activeJukeboxPos, emoteName, volume)
                );
            } catch (Exception e) {
                System.out.println("Failed to execute emote command for " + emoteName + " on player " + player.getName().getString() + ": " + e.getMessage());
            }
        } else {
            // Проверяем, изменилась ли позиция игрока
            double dx = player.getX() - data.lastX;
            double dy = player.getY() - data.lastY;
            double dz = player.getZ() - data.lastZ;
            double distanceMoved = dx * dx + dy * dy + dz * dz;

            if (distanceMoved > 0.01) { // Порог движения (0.1 блока)
                // Игрок движется, останавливаем эмоцию
                if (data.isEmoteActive) {
                    try {
                        String command = "emotes stop " + player.getName().getString();
                        level.getServer().getCommands().performPrefixedCommand(
                                level.getServer().createCommandSourceStack().withPermission(4),
                                command
                        );
                        data.isEmoteActive = false;
                    } catch (Exception e) {
                        System.out.println("Failed to execute emote stop command for player " + player.getName().getString() + ": " + e.getMessage());
                    }
                }
                // Обновляем позицию и время
                data.lastX = player.getX();
                data.lastY = player.getY();
                data.lastZ = player.getZ();
                data.lastMoveTime = level.getGameTime();
            } else {
                // Игрок не движется, проверяем время бездействия
                long timeSinceLastMove = level.getGameTime() - data.lastMoveTime;
                if (!data.isEmoteActive && timeSinceLastMove >= INACTIVITY_THRESHOLD) {
                    // Прошло 5 секунд и эмоция не активна, возобновляем
                    try {
                        String command = "emotes play \"" + emoteName + "\" " + player.getName().getString() + " true";
                        level.getServer().getCommands().performPrefixedCommand(
                                level.getServer().createCommandSourceStack().withPermission(4),
                                command
                        );
                        data.isEmoteActive = true;
                        data.lastMoveTime = level.getGameTime();
                        data.emoteStartTime = level.getGameTime(); // Обновляем время начала для синхронизации
                        data.emoteName = emoteName;
                        // Отправляем сетевой пакет для синхронизации
                        float volume = calculateVolume((float) player.distanceToSqr(activeJukeboxPos.getX() + 0.5, activeJukeboxPos.getY() + 0.5, activeJukeboxPos.getZ() + 0.5));
                        NetworkHandler.INSTANCE.send(
                                PacketDistributor.PLAYER.with(() -> serverPlayer),
                                new JukeboxSyncPacket(activeJukeboxPos, emoteName, volume)
                        );
                    } catch (Exception e) {
                        System.out.println("Failed to execute emote command for " + emoteName + " on player " + player.getName().getString() + ": " + e.getMessage());
                    }
                }
            }
        }
    }

    // Вычисляем громкость на основе расстояния
    private static float calculateVolume(float distance) {
        if (distance <= JUKEBOX_FULL_VOLUME_RANGE) {
            return BASE_VOLUME; // Базовая громкость для фонового радио
        } else if (distance >= JUKEBOX_RANGE) {
            return 0.0F; // Звук выключен за пределами зоны
        } else {
            // Линейное затухание от BASE_VOLUME (на 5 блоках) до 0.0 (на 20 блоках)
            float t = (distance - (float) JUKEBOX_FULL_VOLUME_RANGE) / ((float) JUKEBOX_RANGE - (float) JUKEBOX_FULL_VOLUME_RANGE);
            return BASE_VOLUME * (1.0F - t);
        }
    }

    // Проверяем наличие MusicMerchantEntity рядом с проигрывателем
    private static boolean hasMerchantNearby(Level level, BlockPos jukeboxPos) {
        AABB merchantArea = new AABB(jukeboxPos).inflate(5.0D);
        return !level.getEntitiesOfClass(MusicMerchantEntity.class, merchantArea).isEmpty();
    }
}