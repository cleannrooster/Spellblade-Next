package net.spellbladenext.fabric.items;

import net.minecraft.resources.ResourceLocation;
import net.spell_power.api.MagicSchool;
import net.spellbladenext.SpellbladeNext;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;

public class OrbRenderer extends GeoItemRenderer<Orb> {


    public OrbRenderer(AnimatedGeoModel<Orb> modelProvider) {
        super(new ArcaneOrbModel());
    }
    public OrbRenderer() {
        super(new ArcaneOrbModel());
    }
}
