package net.spellbladenext.fabric.client.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.spellbladenext.SpellbladeNext;
import net.spellbladenext.fabric.client.entity.model.ColdModel;
import net.spellbladenext.fabric.entities.ColdAttack;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.renderers.geo.ExtendedGeoEntityRenderer;

public class ColdRenderer<T extends ColdAttack> extends ExtendedGeoEntityRenderer<ColdAttack> {

    private static final ResourceLocation DEFAULT_LOCATION = new ResourceLocation(SpellbladeNext.MOD_ID,"textures/mob/phantasm.png");
    private static final ResourceLocation FIRE = new ResourceLocation(SpellbladeNext.MOD_ID,"textures/mob/hexblade_fire.png");
    private static final ResourceLocation FROST = new ResourceLocation(SpellbladeNext.MOD_ID,"textures/mob/hexblade_cold.png");
    private static final ResourceLocation ARCANE = new ResourceLocation(SpellbladeNext.MOD_ID,"textures/mob/hexblade_arcane.png");


    public ColdRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ColdModel<>());

        //this.layerRenderers.add((GeoLayerRenderer<Reaver>) new GeoitemInHand<T,M>((IGeoRenderer<T>) this,renderManager.getItemInHandRenderer()));
    }
/*
    @Override
    public void render(GeoModel model, ColdAttack animatable, float partialTick, RenderType type, PoseStack poseStack, MultiBufferSource bufferSource, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        if(animatable.tickCount <= 1){
            return;
        }
        super.render(model, animatable, partialTick, type, poseStack, bufferSource, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }*/
    public ResourceLocation getTextureLocation(ColdAttack p_114891_) {

        return DEFAULT_LOCATION;
    }

    @Override
    public boolean shouldShowName(ColdAttack animatable) {
        return false;
    }

    @Override
    protected void renderNameTag(ColdAttack entity, Component component, PoseStack poseStack, MultiBufferSource multiBufferSource, int i) {
    }

    @Override
    protected boolean isArmorBone(GeoBone geoBone) {
        return false;
    }

    @Nullable
    @Override
    protected ResourceLocation getTextureForBone(String s, ColdAttack reaver) {
        return null;
    }

    @Nullable
    @Override
    protected ItemStack getHeldItemForBone(String s, ColdAttack reaver) {
        if(s.equals("rightItem") ) {
            return new ItemStack(SpellbladeNext.dummyfrost3.get());
        }
        else{
            return null;
        }
    }

    @Override
    protected ItemTransforms.TransformType getCameraTransformForItemAtBone(ItemStack itemStack, String s) {
            return ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND;


    }

    @Nullable
    @Override
    protected BlockState getHeldBlockForBone(String s, ColdAttack reaver) {
        return null;
    }

    @Override
    protected void preRenderItem(PoseStack poseStack, ItemStack itemStack, String s, ColdAttack reaver, IBone iBone) {
        poseStack.translate(0,0.1,-0.1);
    }

    @Override
    protected void preRenderBlock(PoseStack poseStack, BlockState blockState, String s, ColdAttack reaver) {

    }

    @Override
    protected void postRenderItem(PoseStack poseStack, ItemStack itemStack, String s, ColdAttack reaver, IBone iBone) {

    }

    @Override
    protected void postRenderBlock(PoseStack poseStack, BlockState blockState, String s, ColdAttack reaver) {

    }
}