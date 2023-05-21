package net.spellbladenext.fabric.client.item.model;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.spell_power.api.MagicSchool;
import net.spellbladenext.SpellbladeNext;
import net.spellbladenext.fabric.items.armors.InquisitorSet;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class InquisitorItemModel extends AnimatedGeoModel<InquisitorSet> {


    @Override
    public ResourceLocation getModelResource(InquisitorSet orb) {
        if(orb.getSlot() == EquipmentSlot.HEAD){
            if(orb.getMagicschool().contains(MagicSchool.ARCANE)){
                if(orb.getMagicschool().contains(MagicSchool.FIRE)){
                    if(orb.getMagicschool().contains(MagicSchool.FROST)){
                        return new ResourceLocation(SpellbladeNext.MOD_ID,"geo/magebreaker.geo.json");

                    }
                    return new ResourceLocation(SpellbladeNext.MOD_ID,"geo/magebane.geo.json");

                }
                if(orb.getMagicschool().contains(MagicSchool.FROST)){
                    return new ResourceLocation(SpellbladeNext.MOD_ID,"geo/mageseeker.geo.json");

                }
            }
            return new ResourceLocation(SpellbladeNext.MOD_ID,"geo/magebreaker.geo.json");

        }
        if(orb.getSlot() == EquipmentSlot.CHEST){
            return new ResourceLocation(SpellbladeNext.MOD_ID,"geo/inquisitor_chest.json");

        }
        if(orb.getSlot() == EquipmentSlot.FEET){
            return new ResourceLocation(SpellbladeNext.MOD_ID,"geo/inquisitor_feet.json");

        }
        if(orb.getSlot() == EquipmentSlot.LEGS){
            return new ResourceLocation(SpellbladeNext.MOD_ID,"geo/inquisitor_legs.json");

        }
        return new ResourceLocation(SpellbladeNext.MOD_ID,"geo/hooditem.json");
    }

    @Override
    public ResourceLocation getTextureResource(InquisitorSet orb) {

        if(orb.getSlot() == EquipmentSlot.HEAD){
            if(orb.getMagicschool().contains(MagicSchool.ARCANE)){
                if(orb.getMagicschool().contains(MagicSchool.FIRE)){
                    if(orb.getMagicschool().contains(MagicSchool.FROST)){
                        return new ResourceLocation(SpellbladeNext.MOD_ID,"textures/armor/spectral.png");

                    }
                    return new ResourceLocation(SpellbladeNext.MOD_ID,"textures/armor/magebane_crown.png");

                }
                if(orb.getMagicschool().contains(MagicSchool.FROST)){
                    return new ResourceLocation(SpellbladeNext.MOD_ID,"textures/armor/mageseeker_hat.png");

                }
            }
            return new ResourceLocation(SpellbladeNext.MOD_ID,"textures/armor/magebreaker_helmet.png");

        }
        if(orb.getMagicschool().contains(MagicSchool.ARCANE)){
            if(orb.getMagicschool().contains(MagicSchool.FIRE)){
                if(orb.getMagicschool().contains(MagicSchool.FROST)){
                    return new ResourceLocation(SpellbladeNext.MOD_ID,"textures/armor/spectral.png");

                }
                return new ResourceLocation(SpellbladeNext.MOD_ID,"textures/armor/aetherfire.png");

            }
            if(orb.getMagicschool().contains(MagicSchool.FROST)){
                return new ResourceLocation(SpellbladeNext.MOD_ID,"textures/armor/deathchill.png");

            }
        }
        return new ResourceLocation(SpellbladeNext.MOD_ID,"textures/armor/rimeblaze.png");
    }

    @Override
    public ResourceLocation getAnimationResource(InquisitorSet orb) {
        return null;
    }
}
