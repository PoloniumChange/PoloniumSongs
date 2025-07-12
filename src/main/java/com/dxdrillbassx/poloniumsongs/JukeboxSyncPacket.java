package com.dxdrillbassx.poloniumsongs;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import net.minecraftforge.network.NetworkEvent;
import java.util.function.Supplier;

public class JukeboxSyncPacket {
    private final BlockPos pos;
    private final String emoteName;
    private final float volume;

    public JukeboxSyncPacket(BlockPos pos, String emoteName, float volume) {
        this.pos = pos;
        this.emoteName = emoteName;
        this.volume = volume;
    }

    public static void encode(JukeboxSyncPacket msg, FriendlyByteBuf buf) {
        buf.writeBlockPos(msg.pos);
        buf.writeUtf(msg.emoteName);
        buf.writeFloat(msg.volume);
    }

    public static JukeboxSyncPacket decode(FriendlyByteBuf buf) {
        return new JukeboxSyncPacket(buf.readBlockPos(), buf.readUtf(32767), buf.readFloat());
    }

    public static void handle(JukeboxSyncPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ClientLevel level = Minecraft.getInstance().level;
            if (level != null && level.getBlockEntity(msg.pos) instanceof JukeboxBlockEntity jukebox) {
                if (msg.emoteName.isEmpty() || msg.volume <= 0.0F) {
                    // Останавливаем звук
                    Minecraft.getInstance().getSoundManager().stop(null, net.minecraft.sounds.SoundSource.RECORDS);
                } else {
                    // Возобновляем или обновляем звук с указанной громкостью
                    Minecraft.getInstance().getSoundManager().updateSourceVolume(net.minecraft.sounds.SoundSource.RECORDS, msg.volume);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}