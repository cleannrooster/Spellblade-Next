package net.spellbladenext.fabric.items;

import com.google.common.collect.Multimap;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.BowItem;
import net.spell_engine.api.item.ConfigurableAttributes;

public class LaserBow extends BowItem implements ConfigurableAttributes {
    public Multimap<Attribute, AttributeModifier> attributes;

    public LaserBow(Properties properties) {
        super(properties);
    }
    public void setAttributes(Multimap<Attribute, AttributeModifier> attributes) {
        this.attributes = attributes;
    }

    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
        if (this.attributes == null) {
            return super.getDefaultAttributeModifiers(slot);
        } else {
            return slot == EquipmentSlot.MAINHAND ? this.attributes : super.getDefaultAttributeModifiers(slot);
        }
    }
}
