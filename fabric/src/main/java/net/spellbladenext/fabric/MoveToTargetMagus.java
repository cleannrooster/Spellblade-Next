package net.spellbladenext.fabric;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.MoveToTargetSink;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class MoveToTargetMagus extends MoveToTargetSink {
    private static final int MAX_COOLDOWN_BEFORE_RETRYING = 40;
    private int remainingCooldown;
    @Nullable
    private Path path;
    @Nullable
    private BlockPos lastTargetPos;
    private float speedModifier;
    protected boolean checkExtraStartConditions(ServerLevel serverLevel, Mob mob) {

        if(mob instanceof Magus magus && magus.isthinking){
            return false;
        }
        if (this.remainingCooldown > 0) {
            --this.remainingCooldown;
            return false;
        } else {
            Brain<?> brain = mob.getBrain();
            WalkTarget walkTarget = (WalkTarget)brain.getMemory(MemoryModuleType.WALK_TARGET).get();
            boolean bl = this.reachedTarget(mob, walkTarget);
            if (!bl && this.tryComputePath(mob, walkTarget, serverLevel.getGameTime())) {
                this.lastTargetPos = walkTarget.getTarget().currentBlockPosition();
                return true;
            } else {
                brain.eraseMemory(MemoryModuleType.WALK_TARGET);
                if (bl) {
                    brain.eraseMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
                }

                return false;
            }
        }
    }
    private boolean tryComputePath(Mob mob, WalkTarget walkTarget, long l) {
        BlockPos blockPos = walkTarget.getTarget().currentBlockPosition();
        this.path = mob.getNavigation().createPath(blockPos, 0);
        this.speedModifier = walkTarget.getSpeedModifier();
        Brain<?> brain = mob.getBrain();
        if (this.reachedTarget(mob, walkTarget)) {
            brain.eraseMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
        } else {
            boolean bl = this.path != null && this.path.canReach();
            if (bl) {
                brain.eraseMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
            } else if (!brain.hasMemoryValue(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE)) {
                brain.setMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, l);
            }

            if (this.path != null) {
                return true;
            }

            Vec3 vec3 = DefaultRandomPos.getPosTowards((PathfinderMob)mob, 10, 7, Vec3.atBottomCenterOf(blockPos), 1.5707963705062866D);
            if (vec3 != null) {
                this.path = mob.getNavigation().createPath(vec3.x, vec3.y, vec3.z, 0);
                return this.path != null;
            }
        }

        return false;
    }
    protected boolean canStillUse(ServerLevel serverLevel, Mob mob, long l) {

        if(mob instanceof Magus magus && magus.isthinking){
            return false;
        }
        if (this.path != null && this.lastTargetPos != null) {
            Optional<WalkTarget> optional = mob.getBrain().getMemory(MemoryModuleType.WALK_TARGET);
            PathNavigation pathNavigation = mob.getNavigation();
            return !pathNavigation.isDone() && optional.isPresent() && !this.reachedTarget(mob, (WalkTarget)optional.get());
        } else {
            return false;
        }
    }
    private boolean reachedTarget(Mob mob, WalkTarget walkTarget) {
        return walkTarget.getTarget().currentBlockPosition().distManhattan(mob.blockPosition()) <= walkTarget.getCloseEnoughDist();
    }
}
