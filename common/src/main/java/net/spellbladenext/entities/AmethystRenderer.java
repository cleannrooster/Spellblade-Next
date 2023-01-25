package net.spellbladenext.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.particle.SpellParticle;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.spell_engine.client.render.SpellProjectileRenderer;
import net.spell_engine.particle.ParticleHelper;

public class AmethystRenderer<T extends Entity & ItemSupplier> extends EntityRenderer<T> {
    public static final ResourceLocation TEXTURE  = new ResourceLocation("spellbladenext", "textures/entity/sword1.png");
    public static final ResourceLocation TEXTURE2  = new ResourceLocation("spellbladenext", "textures/entity/sword2.png");
    private final ItemRenderer itemRenderer;
    private final float scale;
    private final boolean fullBright;
    private final AmethystModel model;
    public AmethystRenderer(EntityRendererProvider.Context p_174420_, float p_174417_, boolean p_174418_) {
        super(p_174420_);
        this.model = new AmethystModel<>(p_174420_.bakeLayer(AmethystModel.LAYER_LOCATION));
        this.itemRenderer = p_174420_.getItemRenderer();
        this.scale = p_174417_;
        this.fullBright = p_174418_;
    }

    @Override
    protected int getBlockLightLevel(T entity, BlockPos blockPos) {
        return 15;
    }
    public AmethystRenderer(EntityRendererProvider.Context p_174414_) {
        this(p_174414_, 1.0F, false);
    }

    public void render(T p_116111_, float p_116112_, float p_116113_, PoseStack p_116114_, MultiBufferSource p_116115_, int p_116116_) {

        if (!p_116111_.isInvisible()) {
            if (p_116111_.tickCount >= 2 || !(this.entityRenderDispatcher.camera.getEntity().distanceToSqr(p_116111_) < 12.25D)) {
                p_116114_.pushPose();
                double y = p_116111_.getYRot();
                double x = p_116111_.getXRot();
                if(!(p_116111_ instanceof IceThorn)){
                y = Mth.lerp(p_116113_, p_116111_.yRotO, p_116111_.getYRot());
                x = Mth.lerp(p_116113_, p_116111_.xRotO, p_116111_.getXRot());
                }
                p_116114_.mulPose(Vector3f.YP.rotationDegrees((float) (y - 90.0F)));
                p_116114_.mulPose(Vector3f.ZP.rotationDegrees((float) (x - 45.0F)));
                VertexConsumer vertexconsumer = ItemRenderer.getFoilBufferDirect(p_116115_, this.model.renderType(this.getTextureLocation(p_116111_)), true, true);
                this.itemRenderer.renderStatic(p_116111_.getItem(), ItemTransforms.TransformType.NONE, p_116116_, OverlayTexture.NO_OVERLAY, p_116114_, p_116115_, p_116111_.getId());

                p_116114_.popPose();
                return;
            }
        }

    }




    @Override
    public ResourceLocation getTextureLocation(T p_114482_) {
        return TEXTURE;
    }
}
