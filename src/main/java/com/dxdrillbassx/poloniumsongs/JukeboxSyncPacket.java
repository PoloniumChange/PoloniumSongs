package com.dxdrillbassx.poloniumsongs;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class JukeboxSyncPacket {
    private final BlockPos jukeboxPos;
    private final String emoteName;

    public JukeboxSyncPacket(BlockPos jukeboxPos, String emoteName) {
        this.jukeboxPos = jukeboxPos;
        this.emoteName = emoteName;
    }

    public static void encode(JukeboxSyncPacket packet, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(packet.jukeboxPos);
        buffer.writeUtf(packet.emoteName);
    }

    public static JukeboxSyncPacket decode(FriendlyByteBuf buffer) {
        BlockPos pos = buffer.readBlockPos();
        String emoteName = buffer.readUtf();
        return new JukeboxSyncPacket(pos, emoteName);
    }

    public static void handle(JukeboxSyncPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            // Клиентская логика: выполнить команду /emotes play для игрока
            if (ctx.get().getSender() != null) {
                String command = "emotes play \"" + packet.emoteName + "\" " + ctx.get().getSender().getName().getString() + " true";
                ctx.get().getSender().getServer().getCommands().performPrefixedCommand(
                        ctx.get().getSender().getServer().createCommandSourceStack().withPermission(4),
                        command
                );
            }
        });
        ctx.get().setPacketHandled(true);
    }
}