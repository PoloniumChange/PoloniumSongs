package com.dxdrillbassx.poloniumsongs;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = PoloniumSongs.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = net.minecraftforge.api.distmarker.Dist.CLIENT)
public class ClientSoundHandler {
    private static final double JUKEBOX_RANGE = 20.0;
    private static final double JUKEBOX_FULL_VOLUME_RANGE = 5.0;
    private static final float BASE_VOLUME = 0.3F; // Базовая громкость для фонового радио

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.level == null) return;

        ClientLevel level = minecraft.level;
        // Проверяем наличие проигрывателя рядом с MusicMerchantEntity
        AABB searchArea = new AABB(minecraft.player.blockPosition()).inflate(JUKEBOX_RANGE);
        boolean foundJukebox = false;
        float volume = 0.0F;

        for (BlockPos pos : BlockPos.betweenClosed(
                new BlockPos((int)searchArea.minX, (int)searchArea.minY, (int)searchArea.minZ),
                new BlockPos((int)searchArea.maxX, (int)searchArea.maxY, (int)searchArea.maxZ)
        )) {
            if (level.getBlockState(pos).is(net.minecraft.world.level.block.Blocks.JUKEBOX)) {
                // Проверяем наличие MusicMerchantEntity в радиусе 5 блоков
                AABB merchantArea = new AABB(pos).inflate(5.0D);
                if (!level.getEntitiesOfClass(MusicMerchantEntity.class, merchantArea).isEmpty()) {
                    JukeboxBlockEntity jukebox = (JukeboxBlockEntity) level.getBlockEntity(pos);
                    if (jukebox != null && !jukebox.getRecord().isEmpty()) {
                        double distance = minecraft.player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
                        if (distance <= JUKEBOX_RANGE * JUKEBOX_RANGE) {
                            foundJukebox = true;
                            volume = calculateVolume((float) Math.sqrt(distance));
                            // Учитываем настройки громкости "Музыкальные блоки"
                            volume *= minecraft.options.getSoundSourceVolume(net.minecraft.sounds.SoundSource.RECORDS);
                            minecraft.getSoundManager().updateSourceVolume(net.minecraft.sounds.SoundSource.RECORDS, volume);
                        }
                    }
                }
            }
        }

        if (!foundJukebox) {
            // Если игрок вне зоны, останавливаем звук
            minecraft.getSoundManager().stop(null, net.minecraft.sounds.SoundSource.RECORDS);
        }
    }

    private static float calculateVolume(float distance) {
        if (distance <= JUKEBOX_FULL_VOLUME_RANGE) {
            return BASE_VOLUME;
        } else if (distance >= JUKEBOX_RANGE) {
            return 0.0F;
        } else {
            // Линейное затухание от BASE_VOLUME (на 5 блоках) до 0.0 (на 20 блоках)
            float t = (distance - (float) JUKEBOX_FULL_VOLUME_RANGE) / ((float) JUKEBOX_RANGE - (float) JUKEBOX_FULL_VOLUME_RANGE);
            return BASE_VOLUME * (1.0F - t);
        }
    }
}