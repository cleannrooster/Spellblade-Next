package net.spellbladenext.fabric.client.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.spellbladenext.SpellbladeNext;
import net.spellbladenext.fabric.client.entity.model.SpinModel;
import net.spellbladenext.fabric.entities.ai.SpinAttack;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.renderers.geo.ExtendedGeoEntityRenderer;

public class SpinRenderer<T extends SpinAttack> extends ExtendedGeoEntityRenderer<SpinAttack> {

    private static final ResourceLocation DEFAULT_LOCATION = new ResourceLocation(SpellbladeNext.MOD_ID,"textures/mob/hexblade_none.png");
    private static final ResourceLocation FIRE = new ResourceLocation(SpellbladeNext.MOD_ID,"textures/mob/hexblade_fire.png");
    private static final ResourceLocation FROST = new ResourceLocation(SpellbladeNext.MOD_ID,"textures/mob/hexblade_cold.png");
    private static final ResourceLocation ARCANE = new ResourceLocation(SpellbladeNext.MOD_ID,"textures/mob/hexblade_arcane.png");


    public SpinRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new SpinModel<>());

        //this.layerRenderers.add((GeoLayerRenderer<Reaver>) new GeoitemInHand<T,M>((IGeoRenderer<T>) this,renderManager.getItemInHandRenderer()));
    }

    public ResourceLocation getTextureLocation(SpinAttack p_114891_) {

        return DEFAULT_LOCATION;
    }



    @Override
    protected boolean isArmorBone(GeoBone geoBone) {
        return false;
    }

    @Nullable
    @Override
    protected ResourceLocation getTextureForBone(String s, SpinAttack reaver) {
        return null;
    }

    @Nullable
    @Override
    protected ItemStack getHeldItemForBone(String s, SpinAttack reaver) {
        if(s.equals("rightItem") || s.equals("leftItem")) {
            return new ItemStack(SpellbladeNext.SPELLBLADEDUMMY.get());
        }
        else{
            return null;
        }
    }

    @Override
    protected ItemTransforms.TransformType getCameraTransformForItemAtBone(ItemStack itemStack, String s) {
        if(s.equals("rightItem")) {
            return ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND;

        }
        else if(s.equals("leftItem")){
            return ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND;

        }
        else{
            return ItemTransforms.TransformType.FIXED;
        }
    }

    @Nullable
    @Override
    protected BlockState getHeldBlockForBone(String s, SpinAttack reaver) {
        return null;
    }

    @Override
    protected void preRenderItem(PoseStack poseStack, ItemStack itemStack, String s, SpinAttack reaver, IBone iBone) {
        poseStack.translate(0,0.1,-0.1);
    }

    @Override
    protected void preRenderBlock(PoseStack poseStack, BlockState blockState, String s, SpinAttack reaver) {

    }

    @Override
    protected void postRenderItem(PoseStack poseStack, ItemStack itemStack, String s, SpinAttack reaver, IBone iBone) {

    }

    @Override
    protected void postRenderBlock(PoseStack poseStack, BlockState blockState, String s, SpinAttack reaver) {

    }
}