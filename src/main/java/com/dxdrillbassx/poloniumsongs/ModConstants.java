package com.dxdrillbassx.poloniumsongs;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mod.EventBusSubscriber(modid = PoloniumSongs.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModConstants {
    // Список всех пластинок
    public static final List<RegistryObject<Item>> DISCS = Arrays.asList(
            PoloniumSongs.POLONIUM_DISC_1,
            PoloniumSongs.POLONIUM_DISC_2,
            PoloniumSongs.POLONIUM_DISC_3,
            PoloniumSongs.POLONIUM_DISC_4,
            PoloniumSongs.POLONIUM_DISC_5,
            PoloniumSongs.POLONIUM_DISC_6,
            PoloniumSongs.POLONIUM_DISC_7,
            PoloniumSongs.POLONIUM_DISC_8,
            PoloniumSongs.POLONIUM_DISC_9,
            PoloniumSongs.POLONIUM_DISC_10
    );

    // Карта соответствия дисков и эмоций
    public static final Map<Item, String> DISC_TO_EMOTE = new HashMap<>();
    // Карта соответствия дисков и эффектов
    public static final Map<Item, MobEffectInstance> DISC_TO_EFFECT = new HashMap<>();

    // Константы для проигрывателя
    public static final double JUKEBOX_RANGE = 20.0;
    public static final double JUKEBOX_FULL_VOLUME_RANGE = 5.0;
    public static final float BASE_VOLUME = 0.3F;
    public static final int JUKEBOX_CHECK_INTERVAL = 200;
    public static final int INACTIVITY_THRESHOLD = 100;

    static {
        // Инициализация эмоций
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

        // Инициализация эффектов
        DISC_TO_EFFECT.put(PoloniumSongs.POLONIUM_DISC_1.get(), new MobEffectInstance(MobEffects.GLOWING, 100, 0));
        DISC_TO_EFFECT.put(PoloniumSongs.POLONIUM_DISC_2.get(), new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 100, 1));
        DISC_TO_EFFECT.put(PoloniumSongs.POLONIUM_DISC_6.get(), new MobEffectInstance(MobEffects.GLOWING, 100, 0));
        DISC_TO_EFFECT.put(PoloniumSongs.POLONIUM_DISC_7.get(), new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 100, 1));
    }
}