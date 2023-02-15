package net.spellbladenext.fabric;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.behavior.EntityTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

public class Scatter<E extends Mob> extends Behavior<E> {
    private final double tooCloseDistance;
    private final float strafeSpeed;

    public Scatter(double i, float f) {
        super(ImmutableMap.of());
        this.tooCloseDistance = i;
        this.strafeSpeed = f;
    }

    protected boolean checkExtraStartConditions(ServerLevel serverLevel, E mob) {
        return this.isTargetTooClose(mob);
    }

    protected void start(ServerLevel serverLevel, E mob, long l) {
        Reaver target = mob.getLevel().getNearestEntity(Reaver.class, TargetingConditions.forNonCombat(),mob,mob.getX()+mob.getViewVector(1).x()*(tooCloseDistance+mob.getBoundingBox().getXsize()),mob.getY()+mob.getViewVector(1).y()*(tooCloseDistance+mob.getBoundingBox().getYsize()),mob.getZ() + +mob.getViewVector(1).z()*(tooCloseDistance+mob.getBoundingBox().getZsize()),mob.getBoundingBox().inflate(tooCloseDistance));
        if(target != null) {
            //System.out.println("backing up!");
            //Reaver target = mob.getLevel().getNearestEntity(Reaver.class, TargetingConditions.forNonCombat(),mob,mob.getX(),mob.getY(),mob.getZ(),mob.getBoundingBox().inflate(tooCloseDistance));
            mob.lookAt(mob,180,180);
            mob.getMoveControl().strafe(-this.strafeSpeed, 0.0F);
        }
        mob.setYRot(Mth.rotateIfNecessary(mob.getYRot(), mob.yHeadRot, 0.0F));
    }

    private boolean isTargetVisible(E mob) {
        return ((NearestVisibleLivingEntities)mob.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES).get()).contains(this.getTarget(mob));
    }

    private boolean isTargetTooClose(E mob) {
        return mob.getLevel().getNearestEntity(Reaver.class, TargetingConditions.forNonCombat(),mob,mob.getX()+mob.getViewVector(1).x()*(tooCloseDistance+mob.getBoundingBox().getXsize()),mob.getY()+mob.getViewVector(1).y()*(tooCloseDistance+mob.getBoundingBox().getYsize()),mob.getZ() + +mob.getViewVector(1).z()*(tooCloseDistance+mob.getBoundingBox().getZsize()),mob.getBoundingBox().inflate(tooCloseDistance)) != null;
    }

    private LivingEntity getTarget(E mob) {
        return (LivingEntity)mob.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).get();
    }
}
