package net.spellbladenext.fabric.effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.phys.Vec3;
import net.spell_engine.utils.TargetHelper;
import net.spellbladenext.fabric.entities.IceCrashEntity;
import net.spellbladenext.fabric.entities.IceSpikeEntity;

import java.util.ArrayList;
import java.util.List;

import static net.spellbladenext.fabric.SpellbladesFabric.*;

public class IceSlam extends MobEffect {
    public IceSlam(MobEffectCategory mobEffectCategory, int i) {
        super(mobEffectCategory, i);
    }

    @Override
    public void addAttributeModifiers(LivingEntity livingEntity, AttributeMap attributeMap, int i) {
         int NUM_POINTS;
        final double RADIUS = 9* (livingEntity.getBoundingBox().getXsize()/2)/0.3;
        ArrayList<Vec3> list = new ArrayList<Vec3>();
        int circles = (int) RADIUS/3;
        for(int iii = 0; iii <= circles; iii++) {
            NUM_POINTS = (int) ((1+iii)*3*8/3.14);
            for (int ii = 0; ii < NUM_POINTS; ++ii) {
                final double angle = Math.toRadians(((double) ii / NUM_POINTS) * 360d);
                Vec3 vec3 = new Vec3(Math.cos(angle) * (1+iii)*3+livingEntity.getX(), livingEntity.getY(), livingEntity.getZ()+Math.sin(angle) * (1+iii)*3);
                //vec3.add(livingEntity.getViewVector(1F).scale(RADIUS));
                IceCrashEntity iceSpikeEntity = null;
                if(iii % 3 == 0){
                    iceSpikeEntity = new IceCrashEntity(ICECRASH, livingEntity.getLevel());
                }
                if(iii % 3 == 1){
                    iceSpikeEntity = new IceCrashEntity(ICECRASH2, livingEntity.getLevel());
                }
                if(iii % 3 == 2){
                    iceSpikeEntity = new IceCrashEntity(ICECRASH3, livingEntity.getLevel());
                }
                if(iceSpikeEntity != null) {
                    iceSpikeEntity.setPos(vec3);
                    iceSpikeEntity.setYRot(livingEntity.getViewYRot(1F));
                    iceSpikeEntity.setYBodyRot(livingEntity.getViewYRot(1F));
                    iceSpikeEntity.setYHeadRot(livingEntity.getViewYRot(1F));
                    livingEntity.getLevel().addFreshEntity(iceSpikeEntity);
                }
            }
        }
        for(LivingEntity living : livingEntity.getLevel().getEntitiesOfClass(LivingEntity.class,livingEntity.getBoundingBox().inflate(RADIUS))) {
            if (living == livingEntity || TargetHelper.getRelation(livingEntity, (Entity)living).equals(TargetHelper.Relation.FRIENDLY)) {
                break;
            }
            if (living.isOnGround() || (living.getFeetBlockState().getFluidState() != null && living.canStandOnFluid(living.getFeetBlockState().getFluidState()))){
                    living.addEffect(new MobEffectInstance(SPIKED.get(),5,0,false,false));
            }


        }
        super.addAttributeModifiers(livingEntity, attributeMap, i);
    }
}
