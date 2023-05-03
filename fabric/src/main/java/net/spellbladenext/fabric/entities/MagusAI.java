package net.spellbladenext.fabric.entities;

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
import net.spellbladenext.fabric.entities.ai.MagusJumpBack;
import net.spellbladenext.fabric.entities.ai.MeleeAttack;

import java.util.List;
import java.util.Optional;

public class MagusAI {
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

        public MagusAI() {
        }
        protected static Brain<?> makeBrain(Magus magus, Brain<Magus> brain) {
            initCoreActivity(magus, brain);
            initIdleActivity(magus, brain);
            initFightActivity(magus, brain);
            brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
            brain.setDefaultActivity(Activity.IDLE);
            brain.useDefaultActivity();
            return brain;
        }

        protected static void initMemories(Magus magus) {
            GlobalPos globalPos = GlobalPos.of(magus.level.dimension(), magus.blockPosition());
            magus.getBrain().setMemory(MemoryModuleType.HOME, globalPos);
        }

        private static void initCoreActivity(Magus magus, Brain<Magus> brain) {
            brain.addActivity(Activity.CORE, 0, ImmutableList.of(new LookAtTargetSink(45, 90), new MoveToTargetSink(),  new InteractWithDoor(), new StopBeingAngryIfTargetDead<Magus>()));
        }

        private static void initIdleActivity(Magus magus1, Brain<Magus> brain) {
            brain.addActivity(Activity.IDLE, 10, ImmutableList.of(new RunIf<Magus>(magus -> !magus.isthinking,new StartAttacking<Magus>(MagusAI::findNearestValidAttackTarget)), createIdleLookBehaviors(), createIdleMovementBehaviors(), new SetLookAndInteract(EntityType.PLAYER, 4)));
        }

        private static void initFightActivity(Magus magus1, Brain<Magus> brain) {
            brain.addActivityAndRemoveMemoryWhenStopped(Activity.FIGHT, 10, ImmutableList.of(new StopAttackingIfTargetInvalid<Magus>((livingEntity) -> {
                return !isNearestValidAttackTarget(magus1, (LivingEntity) livingEntity);
            }),new MagusJumpBack<Magus>(4.5D,1F),new RunIf<Magus>(magus -> magus1.hasCustomName() &&magus1.getCustomName().equals(Component.translatable("Caster")), new BackUp<Magus>(10, 0.75F)),  new SetWalkTargetFromAttackTargetIfTargetOutOfReach(1.0F), new MeleeAttack(20)), MemoryModuleType.ATTACK_TARGET);
        }

        private static RunOne<Magus> createIdleLookBehaviors() {
            return new RunOne(ImmutableList.of(Pair.of(new SetEntityLookTarget(EntityType.PLAYER, 8.0F), 1), Pair.of(new SetEntityLookTarget(EntityType.PIGLIN, 8.0F), 1), Pair.of(new SetEntityLookTarget(EntityType.PIGLIN_BRUTE, 8.0F), 1), Pair.of(new SetEntityLookTarget(8.0F), 1), Pair.of(new DoNothing(30, 60), 1)));
        }

        private static RunOne<Magus> createIdleMovementBehaviors() {
            return new RunOne(ImmutableList.of(Pair.of(new RandomStroll(0.6F), 2), Pair.of(InteractWith.of(EntityType.PIGLIN, 8, MemoryModuleType.INTERACTION_TARGET, 0.6F, 2), 2), Pair.of(InteractWith.of(EntityType.PIGLIN_BRUTE, 8, MemoryModuleType.INTERACTION_TARGET, 0.6F, 2), 2), Pair.of(new StrollToPoi(MemoryModuleType.HOME, 0.6F, 2, 100), 2), Pair.of(new StrollAroundPoi(MemoryModuleType.HOME, 0.6F, 5), 2), Pair.of(new DoNothing(30, 60), 1)));
        }

        protected static void updateActivity(Magus magus) {
            Brain<?> brain = magus.getBrain();
            Activity activity = (Activity)brain.getActiveNonCoreActivity().orElse((Activity) null);
            //System.out.println(activity);
            brain.setActiveActivityToFirstValid(ImmutableList.of(Activity.FIGHT, Activity.IDLE));
            Activity activity2 = (Activity)brain.getActiveNonCoreActivity().orElse((Activity) null);
            if (activity != activity2) {
                playActivitySound(magus);
            }

            magus.setAggressive(brain.hasMemoryValue(MemoryModuleType.ATTACK_TARGET));
        }

        private static boolean isNearestValidAttackTarget(Magus Magus, LivingEntity livingEntity) {

            return findNearestValidAttackTarget(Magus).filter((livingEntity2) -> {
                return livingEntity2 == livingEntity;
            }).isPresent();
        }
    private static final TargetingConditions ATTACK_TARGET_CONDITIONS_IGNORE_INVISIBILITY_AND_LINE_OF_SIGHT = TargetingConditions.forCombat().range(40).ignoreLineOfSight().ignoreInvisibilityTesting();
    private static final TargetingConditions ATTACK_TARGET_CONDITIONS_IGNORE_LINE_OF_SIGHT = TargetingConditions.forCombat().range(40).ignoreLineOfSight();

    public static boolean isEntityAttackableIgnoringLineOfSight(LivingEntity livingEntity, LivingEntity livingEntity2) {
        return livingEntity.getBrain().isMemoryValue(MemoryModuleType.ATTACK_TARGET, livingEntity2) ? ATTACK_TARGET_CONDITIONS_IGNORE_INVISIBILITY_AND_LINE_OF_SIGHT.test(livingEntity, livingEntity2) :
                ATTACK_TARGET_CONDITIONS_IGNORE_LINE_OF_SIGHT.test(livingEntity, livingEntity2);
    }
        private static Optional<? extends LivingEntity> findNearestValidAttackTarget(Magus magus) {


            Optional<LivingEntity> optional = BehaviorUtils.getLivingEntityFromUUIDMemory(magus, MemoryModuleType.ANGRY_AT);
            if (optional.isPresent() && isEntityAttackableIgnoringLineOfSight(magus, (LivingEntity)optional.get())) {
                return optional;
            } else {
                Optional<? extends LivingEntity> optional2 = getTargetIfWithinRange(magus, MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER);
                return optional2.isPresent() ? optional2 : magus.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_NEMESIS);
            }
        }

        private static Optional<? extends LivingEntity> getTargetIfWithinRange(Magus magus, MemoryModuleType<? extends LivingEntity> memoryModuleType) {
            return magus.getBrain().getMemory(memoryModuleType).filter((livingEntity) -> {
                return livingEntity.closerThan(magus, 36);
            });
        }

        protected static void wasHurtBy(Magus magus, LivingEntity livingEntity) {
            if (!(livingEntity instanceof AbstractPiglin)) {
                maybeRetaliate(magus, livingEntity);
            }
        }
        protected static void maybeRetaliate(Magus abstractPiglin, LivingEntity livingEntity) {
            if (!abstractPiglin.getBrain().isActive(Activity.AVOID)) {
                if (isEntityAttackableIgnoringLineOfSight(abstractPiglin, livingEntity)) {
                    if (!BehaviorUtils.isOtherTargetMuchFurtherAwayThanCurrentAttackTarget(abstractPiglin, livingEntity, 4.0D)) {
                        if (livingEntity.getType() == EntityType.PLAYER && abstractPiglin.level.getGameRules().getBoolean(GameRules.RULE_UNIVERSAL_ANGER)) {
                            setAngerTargetToNearestTargetablePlayerIfFound(abstractPiglin, livingEntity);
                            broadcastUniversalAnger(abstractPiglin);
                        } else {
                            setAngerTarget(abstractPiglin, livingEntity);
                            broadcastAngerTarget(abstractPiglin, livingEntity);
                        }

                    }
                }
            }
        }
        protected static void broadcastUniversalAnger(Magus magus) {
            getAdultPiglins(magus).forEach((abstractPiglinx) -> {
                getNearestVisibleTargetablePlayer(abstractPiglinx).ifPresent((player) -> {
                    setAngerTarget(abstractPiglinx, player);
                });
            });
        }

        private static List<Magus> getAdultPiglins(Magus magus) {
            return (List)magus.getBrain().getMemory(MemoryModuleType.NEARBY_ADULT_PIGLINS).orElse(ImmutableList.of());
        }
        protected static void broadcastAngerTarget(Magus magus, LivingEntity livingEntity) {
            getAdultPiglins(magus).forEach((abstractPiglinx) -> {
                setAngerTargetIfCloserThanCurrent(abstractPiglinx, livingEntity);
            });
        }
        private static Optional<LivingEntity> getAngerTarget(Magus magus) {
            return BehaviorUtils.getLivingEntityFromUUIDMemory(magus, MemoryModuleType.ANGRY_AT);
        }
        private static void setAngerTargetIfCloserThanCurrent(Magus magus, LivingEntity livingEntity) {
            Optional<LivingEntity> optional = getAngerTarget(magus);
            LivingEntity livingEntity2 = BehaviorUtils.getNearestTarget(magus, optional, livingEntity);
            if (!optional.isPresent() || optional.get() != livingEntity2) {
                setAngerTarget(magus, livingEntity2);
            }
        }
        private static void setAngerTargetToNearestTargetablePlayerIfFound(Magus magus, LivingEntity livingEntity) {
            Optional<Player> optional = getNearestVisibleTargetablePlayer(magus);
            if (optional.isPresent()) {
                setAngerTarget(magus, (LivingEntity)optional.get());
            } else {
                setAngerTarget(magus, livingEntity);
            }

        }
        public static Optional<Player> getNearestVisibleTargetablePlayer(Magus magus) {
            return magus.getBrain().hasMemoryValue(MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER) ? magus.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER) : Optional.empty();
        }
        protected static void setAngerTarget(Magus piglinBrute, LivingEntity livingEntity) {
            piglinBrute.getBrain().eraseMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
            piglinBrute.getBrain().setMemoryWithExpiry(MemoryModuleType.ANGRY_AT, livingEntity.getUUID(), 600L);

        }

        protected static void maybePlayActivitySound(Magus magus) {
            if ((double)magus.level.random.nextFloat() < 0.0125D) {
                playActivitySound(magus);
            }
        }

        private static void playActivitySound(Magus magus) {
            magus.getBrain().getActiveNonCoreActivity().ifPresent((activity) -> {


            });
        }


}
