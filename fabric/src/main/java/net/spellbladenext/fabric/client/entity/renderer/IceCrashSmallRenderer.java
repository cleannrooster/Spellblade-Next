package net.spellbladenext.fabric.client.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.ShulkerBulletRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.spell_engine.client.render.SpellProjectileRenderer;
import net.spellbladenext.entities.AmethystModel;
import net.spellbladenext.fabric.client.entity.model.icecrash_smallmodel;
import net.spellbladenext.fabric.entities.IceCrashEntity;

public class IceCrashSmallRenderer<T extends IceCrashEntity> extends EntityRenderer<T> {
    public static final ResourceLocation TEXTURE  = new ResourceLocation("spellbladenext", "textures/entity/sword1.png");
    public static final ResourceLocation TEXTURE2  = new ResourceLocation("spellbladenext", "textures/entity/sword2.png");
    private final ItemRenderer itemRenderer;
    private final float scale;
    private final boolean fullBright;
    private final icecrash_smallmodel model;
    public IceCrashSmallRenderer(EntityRendererProvider.Context p_174420_, float p_174417_, boolean p_174418_) {
        super(p_174420_);
        this.model = new icecrash_smallmodel<>(p_174420_.bakeLayer(icecrash_smallmodel.LAYER_LOCATION));
        this.itemRenderer = p_174420_.getItemRenderer();
        this.scale = p_174417_;
        this.fullBright = p_174418_;
    }

    @Override
    public ResourceLocation getTextureLocation(IceCrashEntity entity) {
        return new ResourceLocation("spellbladenext", "textures/entity/icecrash.png");
    }

    @Override
    public void render(T entity, float f, float g, PoseStack poseStack, MultiBufferSource multiBufferSource, int i) {
        poseStack.translate(0,-0.25,0);

        if(entity.tickCount < 5){
            poseStack.translate(0,-(entity.getBbHeight()/(5*2))*(5-entity.tickCount),0);
        }
        poseStack.scale(this.scale,this.scale,this.scale);
        VertexConsumer vertexConsumer = multiBufferSource.getBuffer(this.model.renderType(getTextureLocation(entity)));

        this.model.renderToBuffer(poseStack,vertexConsumer,i, OverlayTexture.NO_OVERLAY,1.0F,1.0F,1.0F,0.15F);
        super.render(entity, f, g, poseStack, multiBufferSource, i);
    }
}
