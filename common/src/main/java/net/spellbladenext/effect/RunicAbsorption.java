package net.spellbladenext.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.player.Player;
import net.spellbladenext.SpellbladeNext;
import net.spellbladenext.items.RunicArmor;

public class RunicAbsorption extends MobEffect {
    public RunicAbsorption(MobEffectCategory mobEffectCategory, int i) {
        super(mobEffectCategory, i);
    }
    public void applyEffectTick(LivingEntity livingEntity, int i) {


    }
    public boolean isDurationEffectTick(int i, int j) {
            return true;
    }
    public void removeAttributeModifiers(LivingEntity p_19417_, AttributeMap p_19418_, int p_19419_) {
        //p_19417_.setAbsorptionAmount(p_19417_.getAbsorptionAmount() - (float)(1 * (p_19419_)));
        if(p_19417_.getAbsorptionAmount() < p_19419_*4) {
            p_19417_.setAbsorptionAmount((float) ((p_19419_*4)));
        }
        super.removeAttributeModifiers(p_19417_, p_19418_, p_19419_);
    }

    public void addAttributeModifiers(LivingEntity p_19421_, AttributeMap p_19422_, int p_19423_) {

        super.addAttributeModifiers(p_19421_, p_19422_, p_19423_);
    }
}
