package net.spellbladenext.fabric;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;

import java.util.stream.StreamSupport;

import static net.spellbladenext.fabric.ExampleModFabric.DIREHEX;

public class DireHex extends MobEffect {
    protected DireHex(MobEffectCategory mobEffectCategory, int i) {
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
