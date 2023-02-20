package net.spellbladenext.fabric.items;

import net.minecraft.resources.ResourceLocation;
import net.spell_power.api.MagicSchool;
import net.spellbladenext.SpellbladeNext;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class ArcaneOrbModel extends AnimatedGeoModel<Orb>{

    @Override
    public ResourceLocation getModelResource(Orb orb) {
        return new ResourceLocation(SpellbladeNext.MOD_ID,"geo/orb.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(Orb orb) {
        if(orb.getMagicSchool() == MagicSchool.FIRE) {
            return new ResourceLocation(SpellbladeNext.MOD_ID, "textures/item/orb_fire.png");
        }
        if(orb.getMagicSchool() == MagicSchool.FROST) {
            return new ResourceLocation(SpellbladeNext.MOD_ID, "textures/item/orb_frost.png");
        }

            return new ResourceLocation(SpellbladeNext.MOD_ID, "textures/item/orb_arcane.png");
    }

    @Override
    public ResourceLocation getAnimationResource(Orb orb) {
        return new ResourceLocation(SpellbladeNext.MOD_ID,"animations/orb.animations.json");
    }
}
