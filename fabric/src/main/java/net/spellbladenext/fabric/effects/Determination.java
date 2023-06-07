package net.spellbladenext.fabric.effects;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;

public class Determination extends MobEffect {
    public Determination(MobEffectCategory mobEffectCategory, int i) {
        super(mobEffectCategory, i);
    }

    @Override
    public boolean isDurationEffectTick(int i, int j) {
        return i <= 1;
    }

    @Override
    public void applyEffectTick(LivingEntity livingEntity, int i) {
        livingEntity.hurt(new DamageSource("determination"),10);
        super.applyEffectTick(livingEntity, i);
    }

    @Override
    public void addAttributeModifiers(LivingEntity livingEntity, AttributeMap attributeMap, int i) {
        super.addAttributeModifiers(livingEntity, attributeMap, i);
    }

    @Override
    public void removeAttributeModifiers(LivingEntity livingEntity, AttributeMap attributeMap, int i) {
        super.removeAttributeModifiers(livingEntity, attributeMap, i);
    }
}
