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
import net.spellbladenext.fabric.client.entity.model.ArchmagusModel;
import net.spellbladenext.fabric.client.entity.model.MagusModel;
import net.spellbladenext.fabric.entities.Archmagus;
import net.spellbladenext.fabric.entities.Magus;
import net.spellbladenext.fabric.items.spellblades.Spellblade;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.renderers.geo.ExtendedGeoEntityRenderer;

public class ArchmagusRenderer<T extends Archmagus, M extends HumanoidModel<T>> extends ExtendedGeoEntityRenderer<Archmagus> {

    private static final ResourceLocation DEFAULT_LOCATION = new ResourceLocation(SpellbladeNext.MOD_ID,"textures/mob/archmagus_fire.png");
    private static final ResourceLocation FIRE = new ResourceLocation(SpellbladeNext.MOD_ID,"textures/mob/archmagus_fire.png");
    private static final ResourceLocation FROST = new ResourceLocation(SpellbladeNext.MOD_ID,"textures/mob/archmagus_frost.png");
    private static final ResourceLocation ARCANE = new ResourceLocation(SpellbladeNext.MOD_ID,"textures/mob/archmagus_arcane.png");


    public ArchmagusRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ArchmagusModel<>());

        //this.layerRenderers.add((GeoLayerRenderer<Reaver>) new GeoitemInHand<T,M>((IGeoRenderer<T>) this,renderManager.getItemInHandRenderer()));
    }



    public ResourceLocation getTextureLocation(Archmagus p_114891_) {
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
    public boolean shouldShowName(Archmagus animatable) {
        return false;
    }

    @Override
    protected void renderNameTag(Archmagus entity, Component component, PoseStack poseStack, MultiBufferSource multiBufferSource, int i) {
    }

    @Override
    protected boolean isArmorBone(GeoBone geoBone) {
        return false;
    }

    @Nullable
    @Override
    protected ResourceLocation getTextureForBone(String s, Archmagus reaver) {
        return null;
    }

    @Nullable
    @Override
    protected ItemStack getHeldItemForBone(String s, Archmagus reaver) {
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
    protected BlockState getHeldBlockForBone(String s, Archmagus reaver) {
        return null;
    }

    @Override
    protected void preRenderItem(PoseStack poseStack, ItemStack itemStack, String s, Archmagus reaver, IBone iBone) {
        poseStack.translate(0,0.1,-0.1);
    }

    @Override
    protected void preRenderBlock(PoseStack poseStack, BlockState blockState, String s, Archmagus reaver) {

    }

    @Override
    protected void postRenderItem(PoseStack poseStack, ItemStack itemStack, String s, Archmagus reaver, IBone iBone) {

    }

    @Override
    protected void postRenderBlock(PoseStack poseStack, BlockState blockState, String s, Archmagus reaver) {

    }
}