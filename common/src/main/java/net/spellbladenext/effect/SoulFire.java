package net.spellbladenext.effect;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.spellbladenext.SpellbladeNext;

import java.util.Random;

public class SoulFire extends MobEffect {
    public SoulFire(MobEffectCategory mobEffectCategory, int i) {
        super(mobEffectCategory, i);
    }
    public void applyEffectTick(LivingEntity livingEntity, int i) {
        if(!(livingEntity.fireImmune() || livingEntity.hasEffect(MobEffects.FIRE_RESISTANCE) || livingEntity.isDamageSourceBlocked(DamageSource.ON_FIRE))) {
            livingEntity.setSecondsOnFire(1);
            livingEntity.invulnerableTime = 0;
        }
        else{
            livingEntity.removeEffect(SpellbladeNext.SOULFIRE.get());
        }
    }

    public boolean isDurationEffectTick(int i, int j) {
        Random random = new Random();
        return j  > 4 && random.nextFloat() < 0+(j-4)*0.05;
    }

}
