package net.spellbladenext.fabric.items;

import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;

public class InquisitorItemRenderer extends GeoItemRenderer<InquisitorSet> {


    public InquisitorItemRenderer(AnimatedGeoModel<Robes> modelProvider) {
        super(new InquisitorItemModel());
    }
    public InquisitorItemRenderer() {
        super(new InquisitorItemModel());
    }
}
