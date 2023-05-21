package net.spellbladenext.fabric.client.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.spell_engine.utils.TargetHelper;
import net.spell_power.SpellPowerMod;
import net.spell_power.api.MagicSchool;
import net.spell_power.api.attributes.SpellAttributes;
import net.spellbladenext.SpellbladeNext;
import net.spellbladenext.fabric.client.entity.model.ReaverModel;
import net.spellbladenext.fabric.entities.Reaver;
import net.spellbladenext.fabric.items.spellblades.Spellblade;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.renderers.geo.*;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class CivilizedPiglinRenderer<T extends Reaver, M extends HumanoidModel<T>> extends ExtendedGeoEntityRenderer<Reaver> {

    private static final ResourceLocation DEFAULT_LOCATION = new ResourceLocation(SpellbladeNext.MOD_ID,"textures/mob/hexblade_none.png");
    private static final ResourceLocation FIRE = new ResourceLocation(SpellbladeNext.MOD_ID,"textures/mob/hexblade_fire.png");
    private static final ResourceLocation FROST = new ResourceLocation(SpellbladeNext.MOD_ID,"textures/mob/hexblade_cold.png");
    private static final ResourceLocation ARCANE = new ResourceLocation(SpellbladeNext.MOD_ID,"textures/mob/hexblade_arcane.png");


    public CivilizedPiglinRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ReaverModel<>());

        //this.layerRenderers.add((GeoLayerRenderer<Reaver>) new GeoitemInHand<T,M>((IGeoRenderer<T>) this,renderManager.getItemInHandRenderer()));
    }


    @Override
    protected void applyRotations(Reaver animatable, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick) {
        List<Player> list =  animatable.getLevel().getNearbyEntities(Player.class, TargetingConditions.DEFAULT,animatable,animatable.getBoundingBox().inflate(12));
        if(list.stream().anyMatch(livingEntity ->
                    livingEntity.getAttributeValue(SpellAttributes.POWER.get(MagicSchool.ARCANE).attribute) > animatable.getMaxHealth()/2 ||
                    livingEntity.getAttributeValue(SpellAttributes.POWER.get(MagicSchool.FROST).attribute) > animatable.getMaxHealth()/2 ||
                    livingEntity.getAttributeValue(SpellAttributes.POWER.get(MagicSchool.FIRE).attribute) > animatable.getMaxHealth()/2 ||
                    livingEntity.getAttributeValue(SpellAttributes.POWER.get(MagicSchool.HEALING).attribute) > animatable.getMaxHealth()/2)

        ){
            rotationYaw += (float) (Math.cos((double) animatable.tickCount * 3.25D) * Math.PI * (double) 0.4F);
        }





        super.applyRotations(animatable, poseStack, ageInTicks, rotationYaw, partialTick);
    }

    public ResourceLocation getTextureLocation(Reaver p_114891_) {
        if(p_114891_.getMainHandItem().getItem() instanceof Spellblade spellblade){
            if(spellblade.getMagicSchools().stream().anyMatch(fire -> MagicSchool.fromAttributeId(new ResourceLocation(SpellPowerMod.ID,fire.name)).equals(MagicSchool.FIRE))){
                return FIRE;
            }
            if(spellblade.getMagicSchools().stream().anyMatch(frost -> MagicSchool.fromAttributeId(new ResourceLocation(SpellPowerMod.ID,frost.name)).equals(MagicSchool.FROST))){
                return FROST;
            }
            if(spellblade.getMagicSchools().stream().anyMatch(arcane -> MagicSchool.fromAttributeId(new ResourceLocation(SpellPowerMod.ID,arcane.name)).equals(MagicSchool.ARCANE))){
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