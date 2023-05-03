package net.spellbladenext.fabric.effects;

import com.google.common.collect.ImmutableMultimap;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.spellbladenext.fabric.SpellbladesFabric;
import net.spellbladenext.fabric.entities.MonkeyClone;

import java.util.UUID;

public class SplitInvis extends MobEffect {
    public SplitInvis(MobEffectCategory mobEffectCategory, int i) {
        super(mobEffectCategory, i);
    }

    @Override
    public void applyEffectTick(LivingEntity livingEntity, int i) {
/*
        if(livingEntity.isCrouching()) {
            livingEntity.moveRelative((float) livingEntity.getAttribute(Attributes.MOVEMENT_SPEED).getValue(), new Vec3(1, 0, 0));
        }
        else{
            livingEntity.moveRelative((float) livingEntity.getAttribute(Attributes.MOVEMENT_SPEED).getValue(), new Vec3(-1, 0, 0));
        }
        livingEntity.hurtMarked = true;
*/
        super.applyEffectTick(livingEntity, i);
    }

    @Override
    public boolean isDurationEffectTick(int i, int j) {
        return i >= 10;
    }

    @Override
    public void addAttributeModifiers(LivingEntity livingEntity, AttributeMap attributeMap, int i) {
        livingEntity.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY,160,0,false,false));
        if(livingEntity instanceof Player player && !livingEntity.getLevel().isClientSide()) {
            MonkeyClone clone = new MonkeyClone(SpellbladesFabric.MONKEYCLONE, livingEntity.getLevel(), player);
            clone.setPos(livingEntity.position());
            clone.getAttributes().addTransientAttributeModifiers(ImmutableMultimap.of(Attributes.ATTACK_DAMAGE,new AttributeModifier(UUID.randomUUID().toString(),livingEntity.getAttributeValue(Attributes.ATTACK_DAMAGE)/2, AttributeModifier.Operation.ADDITION)));
            EquipmentSlot[] var6 = EquipmentSlot.values();
            livingEntity.getLevel().addFreshEntity(clone);

        }
        super.addAttributeModifiers(livingEntity, attributeMap, i);
    }
}
