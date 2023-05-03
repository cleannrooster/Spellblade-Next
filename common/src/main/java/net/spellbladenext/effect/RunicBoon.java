package net.spellbladenext.effect;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.player.Player;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.internals.SpellCast;
import net.spell_engine.internals.SpellHelper;
import net.spell_engine.internals.SpellRegistry;
import net.spell_engine.particle.ParticleHelper;
import net.spell_engine.utils.SoundHelper;
import net.spell_engine.utils.TargetHelper;
import net.spell_power.api.MagicSchool;
import net.spell_power.api.SpellPower;
import net.spellbladenext.SpellbladeNext;
import net.spellbladenext.items.FriendshipBracelet;

import java.util.List;
import java.util.function.Predicate;

public class RunicBoon extends MobEffect {
    public RunicBoon(MobEffectCategory mobEffectCategory, int i) {
        super(mobEffectCategory, i);
    }
    public void applyEffectTick(LivingEntity livingEntity, int i) {
        Predicate<Entity> selectionPredicate = (target2) -> {
            return (TargetHelper.actionAllowed(TargetHelper.TargetingMode.AREA, TargetHelper.Intent.HARMFUL, livingEntity, target2));
        };
        List<Entity> targets = livingEntity.getLevel().getEntities(livingEntity,livingEntity.getBoundingBox().inflate(6),selectionPredicate);
        targets.removeIf(entity -> entity instanceof AgeableMob);
        targets.removeIf(entity -> entity instanceof TamableAnimal);
        targets.removeIf(entity -> entity instanceof OwnableEntity);
        if(!livingEntity.getLevel().isClientSide()) {
            if (livingEntity instanceof Player player ) {
                Spell spell = SpellRegistry.getSpell(new ResourceLocation(SpellbladeNext.MOD_ID, "fireoverdrive"));
                ;
                ParticleHelper.sendBatches(livingEntity, spell.release.particles);
                SoundHelper.playSound(player.getLevel(), player, spell.release.sound);

                for(Entity target : targets){
                     SpellHelper.performImpacts(livingEntity.getLevel(),livingEntity, target,spell,new SpellHelper.ImpactContext(1.0F,1.0F,null, SpellPower.getSpellPower(MagicSchool.FIRE,livingEntity), TargetHelper.TargetingMode.AREA));
                }
            }
            if (livingEntity instanceof Player player ) {
                Spell spell = SpellRegistry.getSpell(new ResourceLocation(SpellbladeNext.MOD_ID, "frostoverdrive"));
                ParticleHelper.sendBatches(livingEntity, spell.release.particles);
                SoundHelper.playSound(player.getLevel(), player, spell.release.sound);

                for(Entity target : targets){
                    SpellHelper.performImpacts(livingEntity.getLevel(),livingEntity, target,spell,new SpellHelper.ImpactContext(1.0F,1.0F,null, SpellPower.getSpellPower(MagicSchool.FROST,livingEntity), TargetHelper.TargetingMode.AREA));
                }            }
            if (livingEntity instanceof Player player ) {
                Spell spell = SpellRegistry.getSpell(new ResourceLocation(SpellbladeNext.MOD_ID, "arcaneoverdrive"));
                ParticleHelper.sendBatches(livingEntity, spell.release.particles);
                SoundHelper.playSound(player.getLevel(), player, spell.release.sound);

                for(Entity target : targets){
                    SpellHelper.performImpacts(livingEntity.getLevel(),livingEntity, target,spell,new SpellHelper.ImpactContext(1.0F,1.0F,null, SpellPower.getSpellPower(MagicSchool.ARCANE,livingEntity), TargetHelper.TargetingMode.AREA));
                }
            }
        }

    }
    public boolean isDurationEffectTick(int i, int j) {
        return i % Math.max(8,(23-j*3)) == 0;
    }
    public void removeAttributeModifiers(LivingEntity p_19417_, AttributeMap p_19418_, int p_19419_) {
        //p_19417_.setAbsorptionAmount(p_19417_.getAbsorptionAmount() - (float)(1 * (p_19419_)));
        if(p_19417_.getAbsorptionAmount() < p_19419_*5) {
            p_19417_.setAbsorptionAmount((float) ((p_19419_*5)));
        }
        super.removeAttributeModifiers(p_19417_, p_19418_, p_19419_);
    }

    public void addAttributeModifiers(LivingEntity p_19421_, AttributeMap p_19422_, int p_19423_) {

        super.addAttributeModifiers(p_19421_, p_19422_, p_19423_);
    }
}
