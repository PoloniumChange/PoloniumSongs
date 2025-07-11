package com.dxdrillbassx.poloniumsongs;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;

@Mod(PoloniumSongs.MODID)
public class PoloniumSongs {
    public static final String MODID = "poloniumsongs";

    // Регистрация предметов
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    // Регистрация звуков
    private static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, MODID);

    // Регистрация звуков для пластинок
    public static final RegistryObject<SoundEvent> POLONIUM_DISC_1_SOUND = SOUNDS.register("polonium_disc_1",
            () -> new SoundEvent(new ResourceLocation(MODID, "polonium_disc_1")));
    public static final RegistryObject<SoundEvent> POLONIUM_DISC_2_SOUND = SOUNDS.register("polonium_disc_2",
            () -> new SoundEvent(new ResourceLocation(MODID, "polonium_disc_2")));
    public static final RegistryObject<SoundEvent> POLONIUM_DISC_3_SOUND = SOUNDS.register("polonium_disc_3",
            () -> new SoundEvent(new ResourceLocation(MODID, "polonium_disc_3")));
    public static final RegistryObject<SoundEvent> POLONIUM_DISC_4_SOUND = SOUNDS.register("polonium_disc_4",
            () -> new SoundEvent(new ResourceLocation(MODID, "polonium_disc_4")));
    public static final RegistryObject<SoundEvent> POLONIUM_DISC_5_SOUND = SOUNDS.register("polonium_disc_5",
            () -> new SoundEvent(new ResourceLocation(MODID, "polonium_disc_5")));
    public static final RegistryObject<SoundEvent> POLONIUM_DISC_6_SOUND = SOUNDS.register("polonium_disc_6",
            () -> new SoundEvent(new ResourceLocation(MODID, "polonium_disc_6")));
    public static final RegistryObject<SoundEvent> POLONIUM_DISC_7_SOUND = SOUNDS.register("polonium_disc_7",
            () -> new SoundEvent(new ResourceLocation(MODID, "polonium_disc_7")));
    public static final RegistryObject<SoundEvent> POLONIUM_DISC_8_SOUND = SOUNDS.register("polonium_disc_8",
            () -> new SoundEvent(new ResourceLocation(MODID, "polonium_disc_8")));
    public static final RegistryObject<SoundEvent> POLONIUM_DISC_9_SOUND = SOUNDS.register("polonium_disc_9",
            () -> new SoundEvent(new ResourceLocation(MODID, "polonium_disc_9")));
    public static final RegistryObject<SoundEvent> POLONIUM_DISC_10_SOUND = SOUNDS.register("polonium_disc_10",
            () -> new SoundEvent(new ResourceLocation(MODID, "polonium_disc_10")));

    // Регистрация пластинок с индивидуальной длительностью
    public static final RegistryObject<Item> POLONIUM_DISC_1 = ITEMS.register("polonium_disc_1",
            () -> new PoloniumDisc(POLONIUM_DISC_1_SOUND.get(), 7200)); // 6 минут
    public static final RegistryObject<Item> POLONIUM_DISC_2 = ITEMS.register("polonium_disc_2",
            () -> new PoloniumDisc(POLONIUM_DISC_2_SOUND.get(), 5520)); // 4 минуты 36 секунд
    public static final RegistryObject<Item> POLONIUM_DISC_3 = ITEMS.register("polonium_disc_3",
            () -> new PoloniumDisc(POLONIUM_DISC_3_SOUND.get(), 2680)); // 2 минуты 14 секунд
    public static final RegistryObject<Item> POLONIUM_DISC_4 = ITEMS.register("polonium_disc_4",
            () -> new PoloniumDisc(POLONIUM_DISC_4_SOUND.get(), 1220)); // 1 минута 1 секунда
    public static final RegistryObject<Item> POLONIUM_DISC_5 = ITEMS.register("polonium_disc_5",
            () -> new PoloniumDisc(POLONIUM_DISC_5_SOUND.get(), 75760)); // 1 час 3 минуты 8 секунд
    public static final RegistryObject<Item> POLONIUM_DISC_6 = ITEMS.register("polonium_disc_6",
            () -> new PoloniumDisc(POLONIUM_DISC_6_SOUND.get(), 2400)); // 2 минуты
    public static final RegistryObject<Item> POLONIUM_DISC_7 = ITEMS.register("polonium_disc_7",
            () -> new PoloniumDisc(POLONIUM_DISC_7_SOUND.get(), 2360)); // 1 минута 58 секунд
    public static final RegistryObject<Item> POLONIUM_DISC_8 = ITEMS.register("polonium_disc_8",
            () -> new PoloniumDisc(POLONIUM_DISC_8_SOUND.get(), 1820)); // 1 минута 31 секунда
    public static final RegistryObject<Item> POLONIUM_DISC_9 = ITEMS.register("polonium_disc_9",
            () -> new PoloniumDisc(POLONIUM_DISC_9_SOUND.get(), 2160)); // 1 минута 48 секунд
    public static final RegistryObject<Item> POLONIUM_DISC_10 = ITEMS.register("polonium_disc_10",
            () -> new PoloniumDisc(POLONIUM_DISC_10_SOUND.get(), 4580)); // 3 минуты 49 секунд

    // Кастомная вкладка
    public static final CreativeModeTab POLONIUM_SONGS_TAB = new CreativeModeTab(MODID + ".polonium_songs_tab") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(POLONIUM_DISC_1.get());
        }

        @Override
        public void fillItemList(net.minecraft.core.NonNullList<ItemStack> items) {
            items.add(new ItemStack(POLONIUM_DISC_1.get()));
            items.add(new ItemStack(POLONIUM_DISC_2.get()));
            items.add(new ItemStack(POLONIUM_DISC_3.get()));
            items.add(new ItemStack(POLONIUM_DISC_4.get()));
            items.add(new ItemStack(POLONIUM_DISC_5.get()));
            items.add(new ItemStack(POLONIUM_DISC_6.get()));
            items.add(new ItemStack(POLONIUM_DISC_7.get()));
            items.add(new ItemStack(POLONIUM_DISC_8.get()));
            items.add(new ItemStack(POLONIUM_DISC_9.get()));
            items.add(new ItemStack(POLONIUM_DISC_10.get()));
        }
    };

    public PoloniumSongs() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ITEMS.register(modEventBus);
        SOUNDS.register(modEventBus);
    }
}