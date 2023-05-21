package net.spellbladenext.fabric.effects;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.spellbladenext.SpellbladeNext;
import net.spellbladenext.fabric.SpellbladesFabric;
import net.spellbladenext.fabric.items.spellblades.Spellblades;

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
            livingEntity.removeEffect(SpellbladesFabric.SOULFIRE.get());
        }
    }

    @Override
    public void addAttributeModifiers(LivingEntity livingEntity, AttributeMap attributeMap, int i) {
        if(livingEntity instanceof Player player){
            if(player.getMainHandItem().isEmpty() || player.getMainHandItem().getItem() == SpellbladesFabric.MULBERRY.get() ){
                player.getItemBySlot(EquipmentSlot.MAINHAND).setCount(0);
                player.setItemSlot(EquipmentSlot.MAINHAND,new ItemStack(Spellblades.mulberrysword.item()));
            }
        }
        super.addAttributeModifiers(livingEntity, attributeMap, i);
    }

    public boolean isDurationEffectTick(int i, int j) {
        Random random = new Random();
        return j  > 4 && random.nextFloat() < 0+(j-4)*0.05;
    }

}
