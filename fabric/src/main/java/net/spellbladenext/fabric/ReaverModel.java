package net.spellbladenext.fabric;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;
import net.spellbladenext.SpellbladeNext;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class ReaverModel<T extends Reaver> extends AnimatedGeoModel<Reaver> implements ArmedModel {

    @Override
    public ResourceLocation getModelResource(Reaver reaver) {
        return new ResourceLocation(SpellbladeNext.MOD_ID,"geo/hexblade.geo.json");
    }
    @Override
    public ResourceLocation getTextureResource(Reaver reaver) {
        return new ResourceLocation(SpellbladeNext.MOD_ID,"textures/mob/hexblade.png");
    }

    @Override
    public ResourceLocation getAnimationResource(Reaver reaver) {
        return new ResourceLocation(SpellbladeNext.MOD_ID,"animations/hexblade.animation.json");
    }
    public void translateToHand(HumanoidArm humanoidArm, PoseStack poseStack) {
        this.translateAndRotate(poseStack);
    }
    public void translateAndRotate(PoseStack arg) {
        arg.translate((double)(1), (double)(0 / 16.0F), (double)(0 / 16.0F));
        arg.scale(2, 2, 2);



    }
}
