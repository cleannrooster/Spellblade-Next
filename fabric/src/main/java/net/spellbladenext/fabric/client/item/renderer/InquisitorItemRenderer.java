package net.spellbladenext.fabric.client.item.renderer;

import net.spellbladenext.fabric.client.item.model.InquisitorItemModel;
import net.spellbladenext.fabric.items.armors.InquisitorSet;
import net.spellbladenext.fabric.items.armors.Robes;
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
