package net.spellbladenext.fabric;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;
import net.spellbladenext.SpellbladeNext;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class MagusModel<T extends MagusModel> extends AnimatedGeoModel<Magus> implements ArmedModel {

    @Override
    public ResourceLocation getModelResource(Magus reaver) {
        return new ResourceLocation(SpellbladeNext.MOD_ID,"geo/magus.geo.json");
    }
    @Override
    public ResourceLocation getTextureResource(Magus reaver) {
        return new ResourceLocation(SpellbladeNext.MOD_ID,"textures/mob/magus.png");
    }

    @Override
    public ResourceLocation getAnimationResource(Magus reaver) {
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
