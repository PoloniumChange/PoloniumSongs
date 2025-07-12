package com.dxdrillbassx.poloniumsongs;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class MusicMerchantEntity extends AbstractVillager {
    public MusicMerchantEntity(EntityType<? extends AbstractVillager> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void rewardTradeXp(MerchantOffer offer) {
        if (offer.shouldRewardExp()) {
            int xp = 3 + this.random.nextInt(5);
            this.level.addFreshEntity(new net.minecraft.world.entity.ExperienceOrb(this.level, this.getX(), this.getY() + 0.5D, this.getZ(), xp));
        }
    }

    @Override
    protected void updateTrades() {
        MerchantOffers offers = this.getOffers();
        // Уровень 1: Торговля дисками за изумруды
        offers.add(new VillagerTrades.ItemListing() {
            @Override
            public MerchantOffer getOffer(Entity trader, RandomSource rand) {
                return new MerchantOffer(new ItemStack(Items.EMERALD, 5), new ItemStack(PoloniumSongs.POLONIUM_DISC_1.get(), 1), 8, 5, 0.05F);
            }
        }.getOffer(this, this.random));
        offers.add(new VillagerTrades.ItemListing() {
            @Override
            public MerchantOffer getOffer(Entity trader, RandomSource rand) {
                return new MerchantOffer(new ItemStack(Items.EMERALD, 5), new ItemStack(PoloniumSongs.POLONIUM_DISC_2.get(), 1), 8, 5, 0.05F);
            }
        }.getOffer(this, this.random));
        offers.add(new VillagerTrades.ItemListing() {
            @Override
            public MerchantOffer getOffer(Entity trader, RandomSource rand) {
                return new MerchantOffer(new ItemStack(Items.EMERALD, 5), new ItemStack(PoloniumSongs.POLONIUM_DISC_3.get(), 1), 8, 5, 0.05F);
            }
        }.getOffer(this, this.random));
        offers.add(new VillagerTrades.ItemListing() {
            @Override
            public MerchantOffer getOffer(Entity trader, RandomSource rand) {
                return new MerchantOffer(new ItemStack(Items.EMERALD, 5), new ItemStack(PoloniumSongs.POLONIUM_DISC_4.get(), 1), 8, 5, 0.05F);
            }
        }.getOffer(this, this.random));

        // Уровень 2: Торговля дисками за изумруды + нотный блок
        offers.add(new VillagerTrades.ItemListing() {
            @Override
            public MerchantOffer getOffer(Entity trader, RandomSource rand) {
                return new MerchantOffer(new ItemStack(Items.EMERALD, 7), new ItemStack(Items.NOTE_BLOCK, 1), new ItemStack(PoloniumSongs.POLONIUM_DISC_5.get(), 1), 8, 10, 0.05F);
            }
        }.getOffer(this, this.random));
        offers.add(new VillagerTrades.ItemListing() {
            @Override
            public MerchantOffer getOffer(Entity trader, RandomSource rand) {
                return new MerchantOffer(new ItemStack(Items.EMERALD, 7), new ItemStack(Items.NOTE_BLOCK, 1), new ItemStack(PoloniumSongs.POLONIUM_DISC_6.get(), 1), 8, 10, 0.05F);
            }
        }.getOffer(this, this.random));
        offers.add(new VillagerTrades.ItemListing() {
            @Override
            public MerchantOffer getOffer(Entity trader, RandomSource rand) {
                return new MerchantOffer(new ItemStack(Items.EMERALD, 7), new ItemStack(Items.NOTE_BLOCK, 1), new ItemStack(PoloniumSongs.POLONIUM_DISC_7.get(), 1), 8, 10, 0.05F);
            }
        }.getOffer(this, this.random));

        // Уровень 3: Торговля редкими дисками за больше изумрудов
        offers.add(new VillagerTrades.ItemListing() {
            @Override
            public MerchantOffer getOffer(Entity trader, RandomSource rand) {
                return new MerchantOffer(new ItemStack(Items.EMERALD, 10), new ItemStack(PoloniumSongs.POLONIUM_DISC_8.get(), 1), 8, 15, 0.05F);
            }
        }.getOffer(this, this.random));
        offers.add(new VillagerTrades.ItemListing() {
            @Override
            public MerchantOffer getOffer(Entity trader, RandomSource rand) {
                return new MerchantOffer(new ItemStack(Items.EMERALD, 10), new ItemStack(PoloniumSongs.POLONIUM_DISC_9.get(), 1), 8, 15, 0.05F);
            }
        }.getOffer(this, this.random));
        offers.add(new VillagerTrades.ItemListing() {
            @Override
            public MerchantOffer getOffer(Entity trader, RandomSource rand) {
                return new MerchantOffer(new ItemStack(Items.EMERALD, 10), new ItemStack(PoloniumSongs.POLONIUM_DISC_10.get(), 1), 8, 15, 0.05F);
            }
        }.getOffer(this, this.random));
    }

    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return null; // Сущность не размножается
    }

    public static AttributeSupplier.Builder createAttributes() {
        return AbstractVillager.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.5D);
    }
}