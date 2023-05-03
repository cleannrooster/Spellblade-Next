package net.spellbladenext.fabric.client.item.renderer;

import net.spellbladenext.fabric.client.item.model.ArcaneOrbModel;
import net.spellbladenext.fabric.items.Orb;
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
