package net.spellbladenext.fabric.client.entity.model;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;
import net.spellbladenext.SpellbladeNext;
import net.spellbladenext.fabric.entities.MonkeyClone;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class MonkeyCloneModel<T extends MonkeyClone> extends AnimatedGeoModel<MonkeyClone> implements ArmedModel {

    @Override
    public ResourceLocation getModelResource(MonkeyClone reaver) {
        return new ResourceLocation(SpellbladeNext.MOD_ID,"geo/biped.geo.json");
    }
    @Override
    public ResourceLocation getTextureResource(MonkeyClone reaver) {
        return new ResourceLocation(SpellbladeNext.MOD_ID,"textures/mob/biped.png");
    }

    @Override
    public ResourceLocation getAnimationResource(MonkeyClone reaver) {
        return new ResourceLocation(SpellbladeNext.MOD_ID,"animations/biped.animation.json");
    }
    public void translateToHand(HumanoidArm humanoidArm, PoseStack poseStack) {
        this.translateAndRotate(poseStack);
    }
    public void translateAndRotate(PoseStack arg) {
        arg.translate((double)(1), (double)(0 / 16.0F), (double)(0 / 16.0F));
        arg.scale(2, 2, 2);



    }
}
