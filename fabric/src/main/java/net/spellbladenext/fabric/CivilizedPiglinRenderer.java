package net.spellbladenext.fabric;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.spell_power.SpellPowerMod;
import net.spell_power.api.MagicSchool;
import net.spellbladenext.SpellbladeNext;
import net.spellbladenext.fabric.items.spellblades.Spellblade;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.renderers.geo.*;

public class CivilizedPiglinRenderer<T extends Reaver, M extends HumanoidModel<T>> extends ExtendedGeoEntityRenderer<Reaver> {

    private static final ResourceLocation DEFAULT_LOCATION = new ResourceLocation(SpellbladeNext.MOD_ID,"textures/mob/hexblade_none.png");
    private static final ResourceLocation FIRE = new ResourceLocation(SpellbladeNext.MOD_ID,"textures/mob/hexblade_fire.png");
    private static final ResourceLocation FROST = new ResourceLocation(SpellbladeNext.MOD_ID,"textures/mob/hexblade_cold.png");
    private static final ResourceLocation ARCANE = new ResourceLocation(SpellbladeNext.MOD_ID,"textures/mob/hexblade_arcane.png");


    public CivilizedPiglinRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ReaverModel<>());

        //this.layerRenderers.add((GeoLayerRenderer<Reaver>) new GeoitemInHand<T,M>((IGeoRenderer<T>) this,renderManager.getItemInHandRenderer()));
    }



    public ResourceLocation getTextureLocation(Reaver p_114891_) {
        if(p_114891_.getMainHandItem().getItem() instanceof Spellblade spellblade){
            if(spellblade.getMagicSchools().stream().anyMatch(asdf -> MagicSchool.fromAttributeId(new ResourceLocation(SpellPowerMod.ID,asdf.name)).equals(MagicSchool.FIRE))){
                return FIRE;
            }
            if(spellblade.getMagicSchools().stream().anyMatch(asdf -> MagicSchool.fromAttributeId(new ResourceLocation(SpellPowerMod.ID,asdf.name)).equals(MagicSchool.FROST))){
                return FROST;
            }
            if(spellblade.getMagicSchools().stream().anyMatch(asdf -> MagicSchool.fromAttributeId(new ResourceLocation(SpellPowerMod.ID,asdf.name)).equals(MagicSchool.ARCANE))){
                return ARCANE;
            }
        }
        return DEFAULT_LOCATION;
    }

    @Override
    public boolean shouldShowName(Reaver animatable) {
        return false;
    }

    @Override
    protected void renderNameTag(Reaver entity, Component component, PoseStack poseStack, MultiBufferSource multiBufferSource, int i) {
    }

    @Override
    protected boolean isArmorBone(GeoBone geoBone) {
        return false;
    }

    @Nullable
    @Override
    protected ResourceLocation getTextureForBone(String s, Reaver reaver) {
        return null;
    }

    @Nullable
    @Override
    protected ItemStack getHeldItemForBone(String s, Reaver reaver) {
        if(s.equals("rightItem")) {
            return reaver.getMainHandItem();
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
    protected BlockState getHeldBlockForBone(String s, Reaver reaver) {
        return null;
    }

    @Override
    protected void preRenderItem(PoseStack poseStack, ItemStack itemStack, String s, Reaver reaver, IBone iBone) {
        poseStack.translate(0,0.1,-0.1);
    }

    @Override
    protected void preRenderBlock(PoseStack poseStack, BlockState blockState, String s, Reaver reaver) {

    }

    @Override
    protected void postRenderItem(PoseStack poseStack, ItemStack itemStack, String s, Reaver reaver, IBone iBone) {

    }

    @Override
    protected void postRenderBlock(PoseStack poseStack, BlockState blockState, String s, Reaver reaver) {

    }
}