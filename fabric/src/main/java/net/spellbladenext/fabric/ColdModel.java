package net.spellbladenext.fabric;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.spellbladenext.SpellbladeNext;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class ColdModel<T extends ColdAttack> extends AnimatedGeoModel<ColdAttack> {

    @Override
    public ResourceLocation getModelResource(ColdAttack reaver) {
        if(reaver.getCustomName() != null && reaver.getCustomName().equals(Component.translatable("invisible"))){
            return new ResourceLocation(SpellbladeNext.MOD_ID,"geo/arms.geo.json");
        }
        return new ResourceLocation(SpellbladeNext.MOD_ID,"geo/hexblade.geo.json");
    }
    @Override
    public ResourceLocation getTextureResource(ColdAttack reaver) {
        return new ResourceLocation(SpellbladeNext.MOD_ID,"textures/mob/hexblade.png");
    }

    @Override
    public ResourceLocation getAnimationResource(ColdAttack reaver) {
        return new ResourceLocation(SpellbladeNext.MOD_ID,"animations/coldfury.animation.json");
    }
    public void translateAndRotate(PoseStack arg) {
        arg.translate((double)(1), (double)(0 / 16.0F), (double)(0 / 16.0F));
        arg.scale(2, 2, 2);



    }
}
