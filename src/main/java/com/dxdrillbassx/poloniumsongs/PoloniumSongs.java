package com.dxdrillbassx.poloniumsongs;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.client.event.EntityRenderersEvent;

@Mod(PoloniumSongs.MODID)
public class PoloniumSongs {
    public static final String MODID = "poloniumsongs";

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, MODID);
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MODID);

    // Creative Tab
    public static final CreativeModeTab POLONIUM_SONGS_TAB = new CreativeModeTab(MODID) {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(POLONIUM_DISC_1.get());
        }
    };

    // Sound Events
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

    // Disc Items
    public static final RegistryObject<Item> POLONIUM_DISC_1 = ITEMS.register("polonium_disc_1",
            () -> new PoloniumDisc(POLONIUM_DISC_1_SOUND.get(), 2400));
    public static final RegistryObject<Item> POLONIUM_DISC_2 = ITEMS.register("polonium_disc_2",
            () -> new PoloniumDisc(POLONIUM_DISC_2_SOUND.get(), 2400));
    public static final RegistryObject<Item> POLONIUM_DISC_3 = ITEMS.register("polonium_disc_3",
            () -> new PoloniumDisc(POLONIUM_DISC_3_SOUND.get(), 2400));
    public static final RegistryObject<Item> POLONIUM_DISC_4 = ITEMS.register("polonium_disc_4",
            () -> new PoloniumDisc(POLONIUM_DISC_4_SOUND.get(), 2400));
    public static final RegistryObject<Item> POLONIUM_DISC_5 = ITEMS.register("polonium_disc_5",
            () -> new PoloniumDisc(POLONIUM_DISC_5_SOUND.get(), 2400));
    public static final RegistryObject<Item> POLONIUM_DISC_6 = ITEMS.register("polonium_disc_6",
            () -> new PoloniumDisc(POLONIUM_DISC_6_SOUND.get(), 2400));
    public static final RegistryObject<Item> POLONIUM_DISC_7 = ITEMS.register("polonium_disc_7",
            () -> new PoloniumDisc(POLONIUM_DISC_7_SOUND.get(), 2400));
    public static final RegistryObject<Item> POLONIUM_DISC_8 = ITEMS.register("polonium_disc_8",
            () -> new PoloniumDisc(POLONIUM_DISC_8_SOUND.get(), 2400));
    public static final RegistryObject<Item> POLONIUM_DISC_9 = ITEMS.register("polonium_disc_9",
            () -> new PoloniumDisc(POLONIUM_DISC_9_SOUND.get(), 2400));
    public static final RegistryObject<Item> POLONIUM_DISC_10 = ITEMS.register("polonium_disc_10",
            () -> new PoloniumDisc(POLONIUM_DISC_10_SOUND.get(), 2400));

    // Entity
    public static final RegistryObject<EntityType<MusicMerchantEntity>> MUSIC_MERCHANT_ENTITY = ENTITIES.register("music_merchant",
            () -> EntityType.Builder.of(MusicMerchantEntity::new, MobCategory.MISC)
                    .sized(0.6F, 1.95F) // Размер как у игрока
                    .build("music_merchant"));

    // Spawn Egg
    public static final RegistryObject<Item> MUSIC_MERCHANT_SPAWN_EGG = ITEMS.register("music_merchant_spawn_egg",
            () -> new MusicMerchantSpawnEgg());

    public PoloniumSongs() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ITEMS.register(modEventBus);
        SOUNDS.register(modEventBus);
        ENTITIES.register(modEventBus);
        MusicMerchantProfession.POI_TYPES.register(modEventBus);
        MusicMerchantProfession.PROFESSIONS.register(modEventBus);
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::clientSetup);
        modEventBus.addListener(this::registerEntityAttributes);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(NetworkHandler::register);
    }

    private void clientSetup(final EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(MUSIC_MERCHANT_ENTITY.get(), MusicMerchantRenderer::new);
    }

    private void registerEntityAttributes(final net.minecraftforge.event.entity.EntityAttributeCreationEvent event) {
        event.put(MUSIC_MERCHANT_ENTITY.get(), MusicMerchantEntity.createAttributes().build());
    }
}