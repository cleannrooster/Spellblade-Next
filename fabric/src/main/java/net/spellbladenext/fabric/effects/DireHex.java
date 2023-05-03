package net.spellbladenext.fabric.effects;

import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class DireHex extends MobEffect {
    public DireHex(MobEffectCategory mobEffectCategory, int i) {
        super(mobEffectCategory, i);
    }

    @Override
    public void applyEffectTick(LivingEntity livingEntity, int i) {
        super.applyEffectTick(livingEntity, i);
    }

    @Override
    public boolean isDurationEffectTick(int i, int j) {
        return true;
    }
    public void lookAt(LivingEntity living, Entity entity, float f, float g) {
        double d = entity.getX() - living.getX();
        double e = entity.getZ() - living.getZ();
        double h;
        if (entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity)entity;
            h = livingEntity.getEyeY() - living.getEyeY();
        } else {
            h = (entity.getBoundingBox().minY + entity.getBoundingBox().maxY) / 2.0D - living.getEyeY();
        }

        double i = Math.sqrt(d * d + e * e);
        float j = (float)(Mth.atan2(e, d) * 57.2957763671875D) - 90.0F;
        float k = (float)(-(Mth.atan2(h, i) * 57.2957763671875D));
        living.setYRot(j+180);
        living.yBodyRot=j+180;
        living.yHeadRot=j+180;
        living.yRotO =j+180;
        living.yBodyRot =j+180;
        living.yHeadRot =j+180;


    }
}
