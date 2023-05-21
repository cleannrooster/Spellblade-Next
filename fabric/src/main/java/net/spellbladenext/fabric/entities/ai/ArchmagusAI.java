package net.spellbladenext.fabric.entities.ai;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.*;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.level.GameRules;
import net.spellbladenext.fabric.entities.Archmagus;

import java.util.List;
import java.util.Optional;

public class ArchmagusAI {
        private static final int ANGER_DURATION = 600;
        private static final int MELEE_ATTACK_COOLDOWN = 20;
        private static final double ACTIVITY_SOUND_LIKELIHOOD_PER_TICK = 0.0125D;
        private static final int MAX_LOOK_DIST = 8;
        private static final int INTERACTION_RANGE = 8;
        private static final double TARGETING_RANGE = 12.0D;
        private static final float SPEED_MULTIPLIER_WHEN_IDLING = 0.6F;
        private static final int HOME_CLOSE_ENOUGH_DISTANCE = 2;
        private static final int HOME_TOO_FAR_DISTANCE = 100;
        private static final int HOME_STROLL_AROUND_DISTANCE = 5;

        public ArchmagusAI() {
        }
        public static Brain<?> makeBrain(Archmagus Archmagus, Brain<Archmagus> brain) {
            initCoreActivity(Archmagus, brain);
            initIdleActivity(Archmagus, brain);
            initFightActivity(Archmagus, brain);
            brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
            brain.setDefaultActivity(Activity.IDLE);
            brain.useDefaultActivity();
            return brain;
        }

        protected static void initMemories(Archmagus Archmagus) {
            GlobalPos globalPos = GlobalPos.of(Archmagus.level.dimension(), Archmagus.blockPosition());
            Archmagus.getBrain().setMemory(MemoryModuleType.HOME, globalPos);
        }

        private static void initCoreActivity(Archmagus Archmagus, Brain<Archmagus> brain) {
            brain.addActivity(Activity.CORE, 0, ImmutableList.of(new LookAtTargetSink(45, 90), new MoveToTargetSink(),  new InteractWithDoor(), new StopBeingAngryIfTargetDead<Archmagus>()));
        }

        private static void initIdleActivity(Archmagus Archmagus1, Brain<Archmagus> brain) {
            brain.addActivity(Activity.IDLE, 10, ImmutableList.of(new RunIf<Archmagus>(Archmagus -> !Archmagus.isthinking,new StartAttacking<Archmagus>(ArchmagusAI::findNearestValidAttackTarget)), createIdleLookBehaviors(), createIdleMovementBehaviors(), new SetLookAndInteract(EntityType.PLAYER, 4)));
        }


        private static void initFightActivity(Archmagus Archmagus1, Brain<Archmagus> brain) {
            brain.addActivityAndRemoveMemoryWhenStopped(Activity.FIGHT, 10, ImmutableList.of(new StopAttackingIfTargetInvalid<Archmagus>((livingEntity) -> {
                return !isNearestValidAttackTarget(Archmagus1, (LivingEntity) livingEntity);
            }),new ArchmagusJumpBack<Archmagus>(4.5D,1F),  new SetWalkTargetFromAttackTargetIfTargetOutOfReach(1.0F), new MeleeAttack(20)), MemoryModuleType.ATTACK_TARGET);
        }

        private static RunOne<Archmagus> createIdleLookBehaviors() {
            return new RunOne(ImmutableList.of(Pair.of(new SetEntityLookTarget(EntityType.PLAYER, 8.0F), 1), Pair.of(new SetEntityLookTarget(EntityType.PIGLIN, 8.0F), 1), Pair.of(new SetEntityLookTarget(EntityType.PIGLIN_BRUTE, 8.0F), 1), Pair.of(new SetEntityLookTarget(8.0F), 1), Pair.of(new DoNothing(30, 60), 1)));
        }

        private static RunOne<Archmagus> createIdleMovementBehaviors() {
            return new RunOne(ImmutableList.of(Pair.of(new RandomStroll(0.6F), 2), Pair.of(InteractWith.of(EntityType.PIGLIN, 8, MemoryModuleType.INTERACTION_TARGET, 0.6F, 2), 2), Pair.of(InteractWith.of(EntityType.PIGLIN_BRUTE, 8, MemoryModuleType.INTERACTION_TARGET, 0.6F, 2), 2), Pair.of(new StrollToPoi(MemoryModuleType.HOME, 0.6F, 2, 100), 2), Pair.of(new StrollAroundPoi(MemoryModuleType.HOME, 0.6F, 5), 2), Pair.of(new DoNothing(30, 60), 1)));
        }

        public static void updateActivity(Archmagus Archmagus) {
            Brain<?> brain = Archmagus.getBrain();
            Activity activity = (Activity)brain.getActiveNonCoreActivity().orElse((Activity) null);
            brain.setActiveActivityToFirstValid(ImmutableList.of(Activity.FIGHT, Activity.IDLE));
            Activity activity2 = (Activity)brain.getActiveNonCoreActivity().orElse((Activity) null);
            if (activity != activity2) {
                playActivitySound(Archmagus);
            }

            Archmagus.setAggressive(brain.hasMemoryValue(MemoryModuleType.ATTACK_TARGET));
        }

        private static boolean isNearestValidAttackTarget(Archmagus Archmagus, LivingEntity livingEntity) {

            return findNearestValidAttackTarget(Archmagus).filter((livingEntity2) -> {
                return livingEntity2 == livingEntity;
            }).isPresent();
        }
    private static final TargetingConditions ATTACK_TARGET_CONDITIONS_IGNORE_INVISIBILITY_AND_LINE_OF_SIGHT = TargetingConditions.forCombat().range(40).ignoreLineOfSight().ignoreInvisibilityTesting();
    private static final TargetingConditions ATTACK_TARGET_CONDITIONS_IGNORE_LINE_OF_SIGHT = TargetingConditions.forCombat().range(40).ignoreLineOfSight();

    public static boolean isEntityAttackableIgnoringLineOfSight(LivingEntity livingEntity, LivingEntity livingEntity2) {
        return livingEntity.getBrain().isMemoryValue(MemoryModuleType.ATTACK_TARGET, livingEntity2) ? ATTACK_TARGET_CONDITIONS_IGNORE_INVISIBILITY_AND_LINE_OF_SIGHT.test(livingEntity, livingEntity2) :
                ATTACK_TARGET_CONDITIONS_IGNORE_LINE_OF_SIGHT.test(livingEntity, livingEntity2);
    }
        private static Optional<? extends LivingEntity> findNearestValidAttackTarget(Archmagus Archmagus) {


            Optional<LivingEntity> optional = BehaviorUtils.getLivingEntityFromUUIDMemory(Archmagus, MemoryModuleType.ANGRY_AT);
            if (optional.isPresent() && isEntityAttackableIgnoringLineOfSight(Archmagus, (LivingEntity)optional.get())) {
                return optional;
            } else {
                Optional<? extends LivingEntity> optional2 = getTargetIfWithinRange(Archmagus, MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER);
                return optional2.isPresent() ? optional2 : Archmagus.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_NEMESIS);
            }
        }

        private static Optional<? extends LivingEntity> getTargetIfWithinRange(Archmagus Archmagus, MemoryModuleType<? extends LivingEntity> memoryModuleType) {
            return Archmagus.getBrain().getMemory(memoryModuleType).filter((livingEntity) -> {
                return livingEntity.closerThan(Archmagus, 36);
            });
        }






        private static void playActivitySound(Archmagus Archmagus) {
            Archmagus.getBrain().getActiveNonCoreActivity().ifPresent((activity) -> {


            });
        }


}
