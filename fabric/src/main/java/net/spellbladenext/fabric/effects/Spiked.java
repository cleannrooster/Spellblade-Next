package net.spellbladenext.fabric.effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.spellbladenext.fabric.SpellbladesFabric;
import net.spellbladenext.fabric.entities.IceSpikeEntity;

public class Spiked extends MobEffect {
    public Spiked(MobEffectCategory mobEffectCategory, int i) {
        super(mobEffectCategory, i);
    }

    @Override
    public void addAttributeModifiers(LivingEntity livingEntity, AttributeMap attributeMap, int i) {
        IceSpikeEntity iceSpikeEntity = new IceSpikeEntity(SpellbladesFabric.ICESPIKE,livingEntity.getLevel());
        iceSpikeEntity.setPos(livingEntity.position());
        livingEntity.getLevel().addFreshEntity(iceSpikeEntity);
        livingEntity.startRiding(iceSpikeEntity);
        super.addAttributeModifiers(livingEntity, attributeMap, i);
    }
}
