package com.dxdrillbassx.poloniumsongs;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.RecordItem;

@SuppressWarnings("deprecation")
public class PoloniumDisc extends RecordItem {
    public PoloniumDisc(SoundEvent sound, int lengthInSeconds) {
        super(15, sound, new Item.Properties().tab(PoloniumSongs.POLONIUM_SONGS_TAB).stacksTo(1), lengthInSeconds);
    }
}