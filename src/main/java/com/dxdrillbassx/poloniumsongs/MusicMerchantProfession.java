package com.dxdrillbassx.poloniumsongs;

import com.google.common.collect.ImmutableSet;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Set;

@Mod.EventBusSubscriber(modid = PoloniumSongs.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MusicMerchantProfession {

    public static final DeferredRegister<PoiType> POI_TYPES = DeferredRegister.create(ForgeRegistries.POI_TYPES, PoloniumSongs.MODID);
    public static final DeferredRegister<VillagerProfession> PROFESSIONS = DeferredRegister.create(ForgeRegistries.VILLAGER_PROFESSIONS, PoloniumSongs.MODID);

    // POI для музыкального торговца (привязка к проигрывателю)
    public static final RegistryObject<PoiType> MUSIC_MERCHANT_POI = POI_TYPES.register("music_merchant",
            () -> new PoiType(Set.of(Blocks.JUKEBOX.defaultBlockState()), 1, 1));

    // Профессия музыкального торговца
    public static final RegistryObject<VillagerProfession> MUSIC_MERCHANT = PROFESSIONS.register("music_merchant",
            () -> new VillagerProfession(
                    "music_merchant",
                    holder -> holder.get() == MUSIC_MERCHANT_POI.get(),
                    holder -> holder.get() == MUSIC_MERCHANT_POI.get(),
                    ImmutableSet.of(),
                    ImmutableSet.of(Blocks.JUKEBOX),
                    null // Нет специфического звука
            ));
}