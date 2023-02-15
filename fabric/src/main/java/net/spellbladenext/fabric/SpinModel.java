package net.spellbladenext.fabric;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;
import net.spellbladenext.SpellbladeNext;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class SpinModel<T extends SpinAttack> extends AnimatedGeoModel<SpinAttack> {

    @Override
    public ResourceLocation getModelResource(SpinAttack reaver) {
        if(reaver.getCustomName() != null && reaver.getCustomName().equals(Component.translatable("invisible"))){
            return new ResourceLocation(SpellbladeNext.MOD_ID,"geo/arms.geo.json");
        }
        return new ResourceLocation(SpellbladeNext.MOD_ID,"geo/arms.geo.json");
    }
    @Override
    public ResourceLocation getTextureResource(SpinAttack reaver) {
        return new ResourceLocation(SpellbladeNext.MOD_ID,"textures/mob/hexblade.png");
    }

    @Override
    public ResourceLocation getAnimationResource(SpinAttack reaver) {
        return new ResourceLocation(SpellbladeNext.MOD_ID,"animations/shade.animation.json");
    }
    public void translateAndRotate(PoseStack arg) {
        arg.translate((double)(1), (double)(0 / 16.0F), (double)(0 / 16.0F));
        arg.scale(2, 2, 2);



    }
}
