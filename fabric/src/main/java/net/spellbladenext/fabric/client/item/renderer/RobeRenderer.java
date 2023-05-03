package net.spellbladenext.fabric.client.item.renderer;

import net.spellbladenext.fabric.client.item.model.RobeModel;
import net.spellbladenext.fabric.items.armors.Robes;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;

    public class RobeRenderer extends GeoArmorRenderer<Robes> {
        public RobeRenderer() {
            super(new RobeModel());

            //These values are what each bone name is in blockbench. So if your head bone is named "bone545",
            // make sure to do this.headBone = "bone545";

            // The default values are the ones that come with the default armor template in the geckolib blockbench plugin.
            this.headBone = "armorHead";
            this.bodyBone = "armorBody";
            this.rightArmBone = "armorRightArm";
            this.leftArmBone = "armorLeftArm";
            this.rightLegBone = "armorRightLeg";
            this.leftLegBone = "armorLeftLeg";
            this.rightBootBone = "armorRightBoot";
            this.leftBootBone = "armorLeftBoot";
        }
    }
