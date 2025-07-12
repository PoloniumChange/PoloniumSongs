package com.dxdrillbassx.poloniumsongs;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.Item;
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
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import static com.dxdrillbassx.poloniumsongs.ModConstants.*;

public class MusicMerchantEntity extends AbstractVillager {
    private int jukeboxCheckTimer = 0;

    public MusicMerchantEntity(EntityType<? extends AbstractVillager> entityType, Level level) {
        super(entityType, level);
        this.setNoAi(true);
        this.setInvulnerable(true);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (this.isAlive() && !this.isTrading() && !player.isSpectator()) {
            if (player.isShiftKeyDown() && hand == InteractionHand.MAIN_HAND && !this.level.isClientSide()) {
                ItemStack spawnEgg = new ItemStack(PoloniumSongs.MUSIC_MERCHANT_SPAWN_EGG.get());
                if (!player.getInventory().add(spawnEgg)) {
                    this.spawnAtLocation(spawnEgg);
                }
                this.discard();
                return InteractionResult.sidedSuccess(false);
            } else if (!this.level.isClientSide()) {
                System.out.println("Player " + player.getName().getString() + " interacted with MusicMerchantEntity");
                this.setTradingPlayer(player);
                this.openTradingScreen(player, this.getDisplayName(), 1);
                return InteractionResult.sidedSuccess(false);
            }
            return InteractionResult.sidedSuccess(true);
        }
        return InteractionResult.PASS;
    }

    @Override
    protected void rewardTradeXp(MerchantOffer offer) {
        if (offer.shouldRewardExp()) {
            this.level.addFreshEntity(new net.minecraft.world.entity.ExperienceOrb(this.level, this.getX(), this.getY() + 0.5D, this.getZ(), 3 + this.random.nextInt(5)));
        }
    }

    @Override
    protected void updateTrades() {
        MerchantOffers offers = this.getOffers();
        // Уровень 1: Диски за 5 изумрудов
        for (int i = 0; i < 4; i++) {
            int discIndex = i;
            offers.add(new VillagerTrades.ItemListing() {
                @Override
                public MerchantOffer getOffer(Entity trader, RandomSource rand) {
                    return new MerchantOffer(new ItemStack(Items.EMERALD, 5), new ItemStack(DISCS.get(discIndex).get(), 1), 8, 5, 0.05F);
                }
            }.getOffer(this, this.random));
        }
        // Уровень 2: Диски за 7 изумрудов + нотный блок
        for (int i = 4; i < 7; i++) {
            int discIndex = i;
            offers.add(new VillagerTrades.ItemListing() {
                @Override
                public MerchantOffer getOffer(Entity trader, RandomSource rand) {
                    return new MerchantOffer(new ItemStack(Items.EMERALD, 7), new ItemStack(Items.NOTE_BLOCK, 1), new ItemStack(DISCS.get(discIndex).get(), 1), 8, 10, 0.05F);
                }
            }.getOffer(this, this.random));
        }
        // Уровень 3: Редкие диски за 10 изумрудов
        for (int i = 7; i < 10; i++) {
            int discIndex = i;
            offers.add(new VillagerTrades.ItemListing() {
                @Override
                public MerchantOffer getOffer(Entity trader, RandomSource rand) {
                    return new MerchantOffer(new ItemStack(Items.EMERALD, 10), new ItemStack(DISCS.get(discIndex).get(), 1), 8, 15, 0.05F);
                }
            }.getOffer(this, this.random));
        }
    }

    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return null;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return AbstractVillager.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.0D);
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean hurt(net.minecraft.world.damagesource.DamageSource source, float amount) {
        return false;
    }

    @Override
    public void move(net.minecraft.world.entity.MoverType type, Vec3 pos) {
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level.isClientSide()) {
            AABB searchArea = this.getBoundingBox().inflate(8.0D);
            Player nearestPlayer = this.level.getNearestPlayer(this, 8.0D);
            if (nearestPlayer != null && searchArea.contains(nearestPlayer.getX(), nearestPlayer.getY(), nearestPlayer.getZ())) {
                double dX = nearestPlayer.getX() - this.getX();
                double dZ = nearestPlayer.getZ() - this.getZ();
                float yaw = (float) (Math.toDegrees(Math.atan2(dZ, dX)) - 90.0F);
                this.setYRot(yaw);
                this.setYHeadRot(yaw);
                double dY = (nearestPlayer.getY() + nearestPlayer.getEyeHeight()) - (this.getY() + this.getEyeHeight());
                double distanceXZ = Math.sqrt(dX * dX + dZ * dZ);
                float pitch = -(float) (Math.toDegrees(Math.atan2(dY, distanceXZ)));
                pitch = Math.max(-30.0F, Math.min(30.0F, pitch));
                this.setXRot(pitch);
            }

            if (++jukeboxCheckTimer >= JUKEBOX_CHECK_INTERVAL) {
                jukeboxCheckTimer = 0;
                AABB jukeboxArea = this.getBoundingBox().inflate(5.0D);
                for (BlockPos pos : BlockPos.betweenClosed(
                        new BlockPos((int)jukeboxArea.minX, (int)jukeboxArea.minY, (int)jukeboxArea.minZ),
                        new BlockPos((int)jukeboxArea.maxX, (int)jukeboxArea.maxY, (int)jukeboxArea.maxZ))) {
                    BlockState state = this.level.getBlockState(pos);
                    if (state.is(Blocks.JUKEBOX)) {
                        JukeboxBlockEntity jukebox = (JukeboxBlockEntity) this.level.getBlockEntity(pos);
                        if (jukebox != null && jukebox.getRecord().isEmpty()) {
                            Item disc = DISCS.get(this.random.nextInt(DISCS.size())).get();
                            jukebox.setRecord(new ItemStack(disc));
                            this.level.levelEvent(null, 1010, pos, Item.getId(disc));
                        }
                    }
                }
            }
        }
    }
}