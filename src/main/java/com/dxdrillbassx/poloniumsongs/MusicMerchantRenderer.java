package com.dxdrillbassx.poloniumsongs;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class MusicMerchantRenderer extends MobRenderer<MusicMerchantEntity, HumanoidModel<MusicMerchantEntity>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(PoloniumSongs.MODID, "textures/entity/music_merchant.png");

    public MusicMerchantRenderer(EntityRendererProvider.Context context) {
        super(context, new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER)), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(MusicMerchantEntity entity) {
        return TEXTURE;
    }
}