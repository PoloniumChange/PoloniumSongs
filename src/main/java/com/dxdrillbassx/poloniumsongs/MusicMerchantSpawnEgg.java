package com.dxdrillbassx.poloniumsongs;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;

public class MusicMerchantSpawnEgg extends ForgeSpawnEggItem {
    public MusicMerchantSpawnEgg() {
        super(() -> PoloniumSongs.MUSIC_MERCHANT_ENTITY.get(), 0xFF0000, 0x00FF00, new Item.Properties().tab(PoloniumSongs.POLONIUM_SONGS_TAB));
    }
}