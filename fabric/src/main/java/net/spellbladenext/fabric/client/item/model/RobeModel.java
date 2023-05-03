package net.spellbladenext.fabric.client.item.model;

import net.minecraft.resources.ResourceLocation;
import net.spellbladenext.SpellbladeNext;
import net.spellbladenext.fabric.items.armors.Robes;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class RobeModel extends AnimatedGeoModel<Robes> {

    @Override
    public ResourceLocation getModelResource(Robes orb) {

        return new ResourceLocation(SpellbladeNext.MOD_ID,"geo/robes.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(Robes orb) {
        return new ResourceLocation(SpellbladeNext.MOD_ID, "textures/armor/robestexture_default.png");
    }

    @Override
    public ResourceLocation getAnimationResource(Robes orb) {
        return null;
    }
}
