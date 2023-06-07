package net.spellbladenext.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.spell_engine.client.render.SpellProjectileRenderer;

import static net.spellbladenext.SpellbladeNext.LASERARROW;
import static net.spellbladenext.SpellbladeNext.LASERARROWITEM;

public class ThrownRendererEmissive<T extends Entity & ItemSupplier> extends EntityRenderer<T>{
    private final ItemRenderer itemRenderer;
    public ThrownRendererEmissive(EntityRendererProvider.Context context, float f, boolean bl) {
        super(context);
        this.itemRenderer = context.getItemRenderer();
        this.shadowRadius = 0.15F;
        this.shadowStrength = 0.75F;
    }

    public void render(T itemEntity, float f, float g, PoseStack poseStack, MultiBufferSource multiBufferSource, int i) {

        if(itemEntity.tickCount <= 2){
            return;
        }
        poseStack.pushPose();
        ItemStack itemStack = new ItemStack(LASERARROWITEM.get());
        int j = itemStack.isEmpty() ? 187 : Item.getId(itemStack.getItem()) + itemStack.getDamageValue();
        BakedModel bakedModel = this.itemRenderer.getModel(itemStack, itemEntity.level, (LivingEntity)null, itemEntity.getId());
        boolean bl = bakedModel.isGui3d();
        int k = 1;
        float h = 0.25F;
        float l = 0;
        float m = bakedModel.getTransforms().getTransform(ItemTransforms.TransformType.GROUND).scale.y();
        poseStack.translate(0.0D, (double)(l + 0.25F * m), 0.0D);
        float o = bakedModel.getTransforms().ground.scale.x();
        float p = bakedModel.getTransforms().ground.scale.y();
        float q = bakedModel.getTransforms().ground.scale.z();
        float s;
        float t;
        if (!bl) {
            float r = -0.0F * (float)(k - 1) * 0.5F * o;
            s = -0.0F * (float)(k - 1) * 0.5F * p;
            t = -0.09375F * (float)(k - 1) * 0.5F * q;
            poseStack.translate((double)r, (double)s, (double)t);
        }
        double y = itemEntity.getYRot();
        double x = itemEntity.getXRot();
        if(!(itemEntity instanceof IceThorn)){
            y = Mth.lerp(g, itemEntity.yRotO, itemEntity.getYRot());
            x = Mth.lerp(g, itemEntity.xRotO, itemEntity.getXRot());
        }

        for(int u = 0; u < k; ++u) {
            poseStack.pushPose();
            poseStack.mulPose(Vector3f.YP.rotationDegrees((float) (y-180)));
            poseStack.mulPose(Vector3f.XP.rotationDegrees((float) (x)));

            poseStack.scale(1.5F,1.5F,1.5F);
            this.itemRenderer.render(new ItemStack(LASERARROWITEM.get()), ItemTransforms.TransformType.GROUND, false, poseStack, multiBufferSource, i, OverlayTexture.NO_OVERLAY, bakedModel);
            poseStack.popPose();
            if (!bl) {
                poseStack.translate((double)(0.0F * o), (double)(0.0F * p), (double)(0.09375F * q));
            }
        }

        poseStack.popPose();
    }


    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return null;
    }
}
