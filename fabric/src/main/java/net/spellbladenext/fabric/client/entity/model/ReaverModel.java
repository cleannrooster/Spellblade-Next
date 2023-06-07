package net.spellbladenext.fabric.client.entity.model;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;
import net.spell_power.SpellPowerMod;
import net.spell_power.api.MagicSchool;
import net.spellbladenext.SpellbladeNext;
import net.spellbladenext.fabric.entities.Reaver;
import net.spellbladenext.fabric.items.spellblades.Spellblade;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class ReaverModel<T extends Reaver> extends AnimatedGeoModel<Reaver> implements ArmedModel {

    @Override
    public ResourceLocation getModelResource(Reaver reaver) {
        if(reaver.getMainHandItem().getItem() instanceof Spellblade spellblade){
            if(spellblade.getMagicSchools().stream().anyMatch(fire -> MagicSchool.fromAttributeId(new ResourceLocation(SpellPowerMod.ID,fire.name)).equals(MagicSchool.FIRE))){
                return new ResourceLocation(SpellbladeNext.MOD_ID,"geo/firehexblade.json");
            }
            if(spellblade.getMagicSchools().stream().anyMatch(frost -> MagicSchool.fromAttributeId(new ResourceLocation(SpellPowerMod.ID,frost.name)).equals(MagicSchool.FROST))){
                return new ResourceLocation(SpellbladeNext.MOD_ID,"geo/frosthexblade.geo.json");
            }
            if(spellblade.getMagicSchools().stream().anyMatch(arcane -> MagicSchool.fromAttributeId(new ResourceLocation(SpellPowerMod.ID,arcane.name)).equals(MagicSchool.ARCANE))){
                return new ResourceLocation(SpellbladeNext.MOD_ID,"geo/arcanehexblade.geo.json");
            }
        }
        return new ResourceLocation(SpellbladeNext.MOD_ID,"geo/arcanehexblade.geo.json");
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
