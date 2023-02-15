package net.spellbladenext.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.spell_power.api.MagicSchool;

public class Overdrive extends MobEffect {

    private final MagicSchool school;

    public Overdrive(MobEffectCategory mobEffectCategory, MagicSchool school, int i) {
        super(mobEffectCategory, i);
        this.school = school;
    }

    public MagicSchool getSchool() {
        return school;
    }

    @Override
    public MobEffect addAttributeModifier(Attribute attribute, String string, double d, AttributeModifier.Operation operation) {
        return super.addAttributeModifier(attribute, string, d, operation);
    }
}
