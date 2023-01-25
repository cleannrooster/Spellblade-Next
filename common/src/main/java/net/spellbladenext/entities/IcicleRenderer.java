package net.spellbladenext.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemStack;
import net.spellbladenext.items.spellblades.Spellblades;

public class IcicleRenderer<T extends Entity & ItemSupplier> extends EntityRenderer<T> {
    public static final ResourceLocation TEXTURE  = new ResourceLocation("spellbladenext", "textures/entity/sword1.png");
    public static final ResourceLocation TEXTURE2  = new ResourceLocation("spellbladenext", "textures/entity/sword2.png");
    private final ItemRenderer itemRenderer;
    private final float scale;
    private final boolean fullBright;
    private final IcicleModel model;
    public IcicleRenderer(EntityRendererProvider.Context p_174420_, float p_174417_, boolean p_174418_) {
        super(p_174420_);
        this.model = new IcicleModel<>(p_174420_.bakeLayer(IcicleModel.LAYER_LOCATION));
        this.itemRenderer = p_174420_.getItemRenderer();
        this.scale = p_174417_;
        this.fullBright = p_174418_;
    }
    public IcicleRenderer(EntityRendererProvider.Context p_174414_) {
        this(p_174414_, 1.0F, false);
    }
    protected int getBlockLightLevel(T entity, BlockPos blockPos) {
        return 15;
    }


    public void render(T p_116111_, float p_116112_, float p_116113_, PoseStack p_116114_, MultiBufferSource p_116115_, int p_116116_) {

        if (!p_116111_.isInvisible()) {
            if (p_116111_.tickCount >= 2) {
                p_116114_.pushPose();

                p_116114_.mulPose(Vector3f.YP.rotationDegrees( p_116111_.getYRot() - 90.0F));
                p_116114_.mulPose(Vector3f.ZP.rotationDegrees( p_116111_.getXRot() - 45.0F));
                //VertexConsumer vertexconsumer = ItemRenderer.getFoilBufferDirect(p_116115_, this.model.renderType(this.getTextureLocation(p_116111_)), false, false);
                this.itemRenderer.renderStatic(new ItemStack(Spellblades.dummyfrost), ItemTransforms.TransformType.NONE, p_116116_, OverlayTexture.NO_OVERLAY, p_116114_, p_116115_, p_116111_.getId());

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
