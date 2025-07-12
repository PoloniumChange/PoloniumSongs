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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.RegistryObject;

import java.util.Arrays;
import java.util.List;

public class MusicMerchantEntity extends AbstractVillager {
    // Список всех пластинок для случайного выбора
    private static final List<RegistryObject<Item>> DISCS = Arrays.asList(
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
    // Таймер для проверки проигрывателя (каждые 200 тиков = ~10 секунд)
    private int jukeboxCheckTimer = 0;
    private static final int JUKEBOX_CHECK_INTERVAL = 200;

    public MusicMerchantEntity(EntityType<? extends AbstractVillager> entityType, Level level) {
        super(entityType, level);
        // Делаем сущность неподвижной
        this.setNoAi(true);
        // Делаем сущность неуязвимой
        this.setInvulnerable(true);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (this.isAlive() && !this.isTrading() && !player.isSpectator()) {
            // Проверяем, нажат ли Shift и используется ли ПКМ
            if (player.isShiftKeyDown() && hand == InteractionHand.MAIN_HAND) {
                if (!this.level.isClientSide()) {
                    // Создаём яйцо спавна
                    ItemStack spawnEgg = new ItemStack(PoloniumSongs.MUSIC_MERCHANT_SPAWN_EGG.get());
                    // Даём игроку яйцо
                    if (!player.getInventory().add(spawnEgg)) {
                        // Если инвентарь полон, выбрасываем яйцо на землю
                        this.spawnAtLocation(spawnEgg);
                    }
                    // Удаляем сущность
                    this.discard();
                    return InteractionResult.sidedSuccess(this.level.isClientSide());
                }
            } else if (!this.level.isClientSide()) {
                System.out.println("Player " + player.getName().getString() + " interacted with MusicMerchantEntity");
                this.setTradingPlayer(player);
                // Используем фиксированный уровень 1, так как getVillagerData() недоступен
                this.openTradingScreen(player, this.getDisplayName(), 1);
            }
            return InteractionResult.sidedSuccess(this.level.isClientSide());
        }
        return InteractionResult.PASS;
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
        System.out.println("Updating trades for MusicMerchantEntity. Initial offers size: " + offers.size());

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

        System.out.println("Trades updated. Final offers size: " + offers.size());
    }

    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return null; // Сущность не размножается
    }

    public static AttributeSupplier.Builder createAttributes() {
        return AbstractVillager.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.0D); // Устанавливаем скорость 0 для неподвижности
    }

    // Переопределяем метод для предотвращения толкания
    @Override
    public boolean isPushable() {
        return false;
    }

    // Переопределяем метод для предотвращения получения урона
    @Override
    public boolean hurt(net.minecraft.world.damagesource.DamageSource source, float amount) {
        return false; // Игнорируем любой урон
    }

    // Переопределяем метод для предотвращения движения
    @Override
    public void move(net.minecraft.world.entity.MoverType type, Vec3 pos) {
        // Ничего не делаем, чтобы сущность не двигалась
    }

    // Переопределяем tick для отслеживания ближайшего игрока и управления проигрывателем
    @Override
    public void tick() {
        super.tick();
        if (!this.level.isClientSide()) {
            // Отслеживание ближайшего игрока
            AABB searchArea = this.getBoundingBox().inflate(8.0D);
            Player nearestPlayer = this.level.getNearestPlayer(this, 8.0D);
            if (nearestPlayer != null && searchArea.contains(nearestPlayer.getX(), nearestPlayer.getY(), nearestPlayer.getZ())) {
                // Поворачиваем тело по горизонтали (yaw)
                double dX = nearestPlayer.getX() - this.getX();
                double dZ = nearestPlayer.getZ() - this.getZ();
                float yaw = (float) (Math.toDegrees(Math.atan2(dZ, dX)) - 90.0F);
                this.setYRot(yaw);
                this.setYHeadRot(yaw);

                // Поворачиваем голову по вертикали (pitch)
                double dY = (nearestPlayer.getY() + nearestPlayer.getEyeHeight()) - (this.getY() + this.getEyeHeight());
                double distanceXZ = Math.sqrt(dX * dX + dZ * dZ);
                float pitch = -(float) (Math.toDegrees(Math.atan2(dY, distanceXZ))); // Инвертируем угол
                // Ограничиваем угол наклона головы (±30 градусов)
                pitch = Math.max(-30.0F, Math.min(30.0F, pitch));
                this.setXRot(pitch);
            }

            // Проверка проигрывателя каждые JUKEBOX_CHECK_INTERVAL тиков
            if (++jukeboxCheckTimer >= JUKEBOX_CHECK_INTERVAL) {
                jukeboxCheckTimer = 0; // Сбрасываем таймер
                AABB jukeboxArea = this.getBoundingBox().inflate(5.0D); // Радиус 5 блоков
                BlockPos.betweenClosed(
                        new BlockPos((int)jukeboxArea.minX, (int)jukeboxArea.minY, (int)jukeboxArea.minZ),
                        new BlockPos((int)jukeboxArea.maxX, (int)jukeboxArea.maxY, (int)jukeboxArea.maxZ)
                ).forEach(pos -> {
                    BlockState state = this.level.getBlockState(pos);
                    if (state.is(Blocks.JUKEBOX)) {
                        JukeboxBlockEntity jukebox = (JukeboxBlockEntity) this.level.getBlockEntity(pos);
                        if (jukebox != null && jukebox.getRecord().isEmpty()) {
                            // Выбираем случайную пластинку
                            Item disc = DISCS.get(this.random.nextInt(DISCS.size())).get();
                            ItemStack discStack = new ItemStack(disc);
                            // Устанавливаем пластинку в проигрыватель
                            jukebox.setRecord(discStack);
                            // Запускаем воспроизведение
                            this.level.levelEvent(null, 1010, pos, Item.getId(disc));
                        }
                    }
                });
            }
        }
    }
}