package net.spellbladenext.fabric.client.entity.model;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;
import net.spellbladenext.SpellbladeNext;
import net.spellbladenext.fabric.entities.Archmagus;
import net.spellbladenext.fabric.entities.Magus;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class ArchmagusModel<T extends ArchmagusModel> extends AnimatedGeoModel<Archmagus> implements ArmedModel {

    @Override
    public ResourceLocation getModelResource(Archmagus reaver) {
        return new ResourceLocation(SpellbladeNext.MOD_ID,"geo/archmagus.geo.json");
    }
    @Override
    public ResourceLocation getTextureResource(Archmagus reaver) {
        return new ResourceLocation(SpellbladeNext.MOD_ID,"textures/mob/archmagus_fire.png");
    }

    @Override
    public ResourceLocation getAnimationResource(Archmagus reaver) {
        return new ResourceLocation(SpellbladeNext.MOD_ID,"animations/magus.animation.json");
    }
    public void translateToHand(HumanoidArm humanoidArm, PoseStack poseStack) {
        this.translateAndRotate(poseStack);
    }
    public void translateAndRotate(PoseStack arg) {
        arg.translate((double)(1), (double)(0 / 16.0F), (double)(0 / 16.0F));
        arg.scale(2, 2, 2);



    }
}
