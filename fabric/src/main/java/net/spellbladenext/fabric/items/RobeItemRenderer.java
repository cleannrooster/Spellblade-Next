package net.spellbladenext.fabric.items;

import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;

public class RobeItemRenderer extends GeoItemRenderer<Robes> {


    public RobeItemRenderer(AnimatedGeoModel<Robes> modelProvider) {
        super(new RobeItemModel());
    }
    public RobeItemRenderer() {
        super(new RobeItemModel());
    }
}
