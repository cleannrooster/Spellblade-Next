package net.spellbladenext.fabric.effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class PortalSickness extends MobEffect {
    public PortalSickness(MobEffectCategory mobEffectCategory, int i) {
        super(mobEffectCategory, i);
    }

    @Override
    public void applyEffectTick(LivingEntity livingEntity, int i) {
        livingEntity.setPortalCooldown();
        super.applyEffectTick(livingEntity, i);
    }

    @Override
    public boolean isDurationEffectTick(int i, int j) {
        return true;
    }
}
