package com.dxdrillbassx.poloniumsongs;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.RecordItem;

public class PoloniumDisc extends RecordItem {
    public PoloniumDisc(SoundEvent sound, int lengthInTicks) {
        super(15, sound, new Properties().tab(PoloniumSongs.POLONIUM_SONGS_TAB).stacksTo(1), lengthInTicks);
    }
}