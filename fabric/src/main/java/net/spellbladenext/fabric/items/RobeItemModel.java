package net.spellbladenext.fabric.items;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.spell_power.api.MagicSchool;
import net.spellbladenext.SpellbladeNext;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;

public class RobeItemModel extends AnimatedGeoModel<Robes> {


    @Override
    public ResourceLocation getModelResource(Robes orb) {
        if(orb.slot == EquipmentSlot.CHEST){
            return new ResourceLocation(SpellbladeNext.MOD_ID,"geo/robeitem.geo.json");

        }
        if(orb.slot == EquipmentSlot.FEET){
            return new ResourceLocation(SpellbladeNext.MOD_ID,"geo/bootsitem.geo.json");

        }
        if(orb.slot == EquipmentSlot.LEGS){
            return new ResourceLocation(SpellbladeNext.MOD_ID,"geo/pantsitem.geo.json");

        }
        return new ResourceLocation(SpellbladeNext.MOD_ID,"geo/hooditem.json");
    }

    @Override
    public ResourceLocation getTextureResource(Robes orb) {

        return new ResourceLocation(SpellbladeNext.MOD_ID, "textures/item/robe.png");
    }

    @Override
    public ResourceLocation getAnimationResource(Robes orb) {
        return null;
    }
}
