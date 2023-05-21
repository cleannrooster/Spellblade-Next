package net.spellbladenext.fabric.items.spellblades;

import com.google.common.collect.Multimap;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Tier;
import net.spellbladenext.fabric.config.ItemConfig;

import java.util.ArrayList;

public class FrostbornGavel extends Spellblade{
    public FrostbornGavel(Tier material, Multimap<Attribute, AttributeModifier> attributes, Properties settings, ArrayList<ItemConfig.SpellAttribute> school) {
        super(material, attributes, settings, school);
    }
}
