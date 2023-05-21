package net.spellbladenext.fabric.client.entity.renderer;

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
import net.spellbladenext.fabric.client.entity.model.MagusModel;
import net.spellbladenext.fabric.entities.Magus;
import net.spellbladenext.fabric.items.spellblades.Spellblade;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.renderers.geo.ExtendedGeoEntityRenderer;

public class MagusRenderer<T extends Magus, M extends HumanoidModel<T>> extends ExtendedGeoEntityRenderer<Magus> {

    private static final ResourceLocation DEFAULT_LOCATION = new ResourceLocation(SpellbladeNext.MOD_ID,"textures/mob/magus.png");
    private static final ResourceLocation FIRE = new ResourceLocation(SpellbladeNext.MOD_ID,"textures/mob/magus.png");
    private static final ResourceLocation FROST = new ResourceLocation(SpellbladeNext.MOD_ID,"textures/mob/magus_frost.png");
    private static final ResourceLocation ARCANE = new ResourceLocation(SpellbladeNext.MOD_ID,"textures/mob/magus_arcane.png");


    public MagusRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new MagusModel<>());

        //this.layerRenderers.add((GeoLayerRenderer<Reaver>) new GeoitemInHand<T,M>((IGeoRenderer<T>) this,renderManager.getItemInHandRenderer()));
    }



    public ResourceLocation getTextureLocation(Magus p_114891_) {
        if(p_114891_.getMainHandItem().getItem() instanceof Spellblade spellblade){
            if(spellblade.getMagicSchools().stream().anyMatch(school -> MagicSchool.fromAttributeId(new ResourceLocation(SpellPowerMod.ID,school.name)).equals(MagicSchool.FIRE))){
                return FIRE;
            }
            if(spellblade.getMagicSchools().stream().anyMatch(school -> MagicSchool.fromAttributeId(new ResourceLocation(SpellPowerMod.ID,school.name)).equals(MagicSchool.FROST))){
                return FROST;
            }
            if(spellblade.getMagicSchools().stream().anyMatch(school -> MagicSchool.fromAttributeId(new ResourceLocation(SpellPowerMod.ID,school.name)).equals(MagicSchool.ARCANE))){
                return ARCANE;
            }
        }
        return DEFAULT_LOCATION;
    }

    @Override
    public boolean shouldShowName(Magus animatable) {
        return false;
    }

    @Override
    protected void renderNameTag(Magus entity, Component component, PoseStack poseStack, MultiBufferSource multiBufferSource, int i) {
    }

    @Override
    protected boolean isArmorBone(GeoBone geoBone) {
        return false;
    }

    @Nullable
    @Override
    protected ResourceLocation getTextureForBone(String s, Magus reaver) {
        return null;
    }

    @Nullable
    @Override
    protected ItemStack getHeldItemForBone(String s, Magus reaver) {
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
    protected BlockState getHeldBlockForBone(String s, Magus reaver) {
        return null;
    }

    @Override
    protected void preRenderItem(PoseStack poseStack, ItemStack itemStack, String s, Magus reaver, IBone iBone) {
        poseStack.translate(0,0.1,-0.1);
    }

    @Override
    protected void preRenderBlock(PoseStack poseStack, BlockState blockState, String s, Magus reaver) {

    }

    @Override
    protected void postRenderItem(PoseStack poseStack, ItemStack itemStack, String s, Magus reaver, IBone iBone) {

    }

    @Override
    protected void postRenderBlock(PoseStack poseStack, BlockState blockState, String s, Magus reaver) {

    }
}