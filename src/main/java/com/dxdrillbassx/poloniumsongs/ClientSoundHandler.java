package com.dxdrillbassx.poloniumsongs;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.dxdrillbassx.poloniumsongs.ModConstants.*;

@Mod.EventBusSubscriber(modid = PoloniumSongs.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = net.minecraftforge.api.distmarker.Dist.CLIENT)
public class ClientSoundHandler {
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.level == null) return;

        ClientLevel level = minecraft.level;
        AABB searchArea = new AABB(minecraft.player.blockPosition()).inflate(JUKEBOX_RANGE);
        float volume = 0.0F;

        for (BlockPos pos : BlockPos.betweenClosed(
                new BlockPos((int)searchArea.minX, (int)searchArea.minY, (int)searchArea.minZ),
                new BlockPos((int)searchArea.maxX, (int)searchArea.maxY, (int)searchArea.maxZ))) {
            if (level.getBlockState(pos).is(net.minecraft.world.level.block.Blocks.JUKEBOX)) {
                AABB merchantArea = new AABB(pos).inflate(5.0D);
                if (!level.getEntitiesOfClass(MusicMerchantEntity.class, merchantArea).isEmpty()) {
                    JukeboxBlockEntity jukebox = (JukeboxBlockEntity) level.getBlockEntity(pos);
                    if (jukebox != null && !jukebox.getRecord().isEmpty()) {
                        double distance = minecraft.player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
                        if (distance <= JUKEBOX_RANGE * JUKEBOX_RANGE) {
                            volume = calculateVolume((float) Math.sqrt(distance)) * minecraft.options.getSoundSourceVolume(net.minecraft.sounds.SoundSource.RECORDS);
                            minecraft.getSoundManager().updateSourceVolume(net.minecraft.sounds.SoundSource.RECORDS, volume);
                            return;
                        }
                    }
                }
            }
        }
        minecraft.getSoundManager().stop(null, net.minecraft.sounds.SoundSource.RECORDS);
    }

    private static float calculateVolume(float distance) {
        if (distance <= JUKEBOX_FULL_VOLUME_RANGE) return BASE_VOLUME;
        if (distance >= JUKEBOX_RANGE) return 0.0F;
        float t = (distance - (float) JUKEBOX_FULL_VOLUME_RANGE) / ((float) JUKEBOX_RANGE - (float) JUKEBOX_FULL_VOLUME_RANGE);
        return BASE_VOLUME * (1.0F - t);
    }
}