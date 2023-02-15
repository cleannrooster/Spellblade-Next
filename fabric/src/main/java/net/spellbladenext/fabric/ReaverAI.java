package net.spellbladenext.fabric;

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
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.entity.monster.piglin.PiglinBruteAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.level.GameRules;

import java.util.List;
import java.util.Optional;

import static net.minecraft.world.entity.monster.piglin.PiglinAi.getNearestVisibleTargetablePlayer;

public class ReaverAI {
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

    public ReaverAI() {
    }

    protected static Brain<?> makeBrain(PiglinBrute piglinBrute, Brain<PiglinBrute> brain) {
        initCoreActivity(piglinBrute, brain);
        initIdleActivity(piglinBrute, brain);
        initFightActivity(piglinBrute, brain);
        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.useDefaultActivity();
        return brain;
    }

    protected static void initMemories(PiglinBrute piglinBrute) {
        GlobalPos globalPos = GlobalPos.of(piglinBrute.level.dimension(), piglinBrute.blockPosition());
        piglinBrute.getBrain().setMemory(MemoryModuleType.HOME, globalPos);
    }

    private static void initCoreActivity(PiglinBrute piglinBrute, Brain<PiglinBrute> brain) {
        brain.addActivity(Activity.CORE, 0, ImmutableList.of(new LookAtTargetSink(45, 90), new MoveToTargetSink(),  new InteractWithDoor(), new StopBeingAngryIfTargetDead<PiglinBrute>()));
    }

    private static void initIdleActivity(PiglinBrute piglinBrute, Brain<PiglinBrute> brain) {
        brain.addActivity(Activity.IDLE, 10, ImmutableList.of(new StartAttacking<PiglinBrute>(ReaverAI::findNearestValidAttackTarget), createIdleLookBehaviors(), createIdleMovementBehaviors(), new SetLookAndInteract(EntityType.PLAYER, 4)));
    }

    private static void initFightActivity(AbstractPiglin piglinBrute, Brain<PiglinBrute> brain) {
        brain.addActivityAndRemoveMemoryWhenStopped(Activity.FIGHT, 10, ImmutableList.of(new StopAttackingIfTargetInvalid<AbstractPiglin>((livingEntity) -> {
            return !isNearestValidAttackTarget(piglinBrute, (LivingEntity) livingEntity);
        }),new RunIf<PiglinBrute>(asdf -> piglinBrute.hasCustomName() &&piglinBrute.getCustomName().equals(Component.translatable("Caster")), new BackUp<PiglinBrute>(10, 0.75F)),  new SetWalkTargetFromAttackTargetIfTargetOutOfReach(1.0F), new SetWalkTargetFromAttackTargetIfTargetOutOfReach(1.0F), new MeleeAttack(20),new SpellAttack<PiglinBrute, LivingEntity>()), MemoryModuleType.ATTACK_TARGET);
    }

    private static RunOne<PiglinBrute> createIdleLookBehaviors() {
        return new RunOne(ImmutableList.of(Pair.of(new SetEntityLookTarget(EntityType.PLAYER, 8.0F), 1), Pair.of(new SetEntityLookTarget(EntityType.PIGLIN, 8.0F), 1), Pair.of(new SetEntityLookTarget(EntityType.PIGLIN_BRUTE, 8.0F), 1), Pair.of(new SetEntityLookTarget(8.0F), 1), Pair.of(new DoNothing(30, 60), 1)));
    }

    private static RunOne<PiglinBrute> createIdleMovementBehaviors() {
        return new RunOne(ImmutableList.of(Pair.of(new RandomStroll(0.6F), 2), Pair.of(InteractWith.of(EntityType.PIGLIN, 8, MemoryModuleType.INTERACTION_TARGET, 0.6F, 2), 2), Pair.of(InteractWith.of(EntityType.PIGLIN_BRUTE, 8, MemoryModuleType.INTERACTION_TARGET, 0.6F, 2), 2), Pair.of(new StrollToPoi(MemoryModuleType.HOME, 0.6F, 2, 100), 2), Pair.of(new StrollAroundPoi(MemoryModuleType.HOME, 0.6F, 5), 2), Pair.of(new DoNothing(30, 60), 1)));
    }

    protected static void updateActivity(PiglinBrute piglinBrute) {
        Brain<PiglinBrute> brain = piglinBrute.getBrain();
        Activity activity = (Activity)brain.getActiveNonCoreActivity().orElse((Activity) null);
        brain.setActiveActivityToFirstValid(ImmutableList.of(Activity.FIGHT, Activity.IDLE));
        Activity activity2 = (Activity)brain.getActiveNonCoreActivity().orElse((Activity) null);
        if (activity != activity2) {
            playActivitySound(piglinBrute);
        }

        piglinBrute.setAggressive(brain.hasMemoryValue(MemoryModuleType.ATTACK_TARGET));
    }

    private static boolean isNearestValidAttackTarget(AbstractPiglin abstractPiglin, LivingEntity livingEntity) {
        return findNearestValidAttackTarget(abstractPiglin).filter((livingEntity2) -> {
            return livingEntity2 == livingEntity;
        }).isPresent();
    }

    private static Optional<? extends LivingEntity> findNearestValidAttackTarget(AbstractPiglin abstractPiglin) {
        Optional<LivingEntity> optional = BehaviorUtils.getLivingEntityFromUUIDMemory(abstractPiglin, MemoryModuleType.ANGRY_AT);
        if (optional.isPresent() && Sensor.isEntityAttackableIgnoringLineOfSight(abstractPiglin, (LivingEntity)optional.get())) {
            return optional;
        } else {
            Optional<? extends LivingEntity> optional2 = getTargetIfWithinRange(abstractPiglin, MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER);
            return optional2.isPresent() ? optional2 : abstractPiglin.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_NEMESIS);
        }
    }

    private static Optional<? extends LivingEntity> getTargetIfWithinRange(AbstractPiglin abstractPiglin, MemoryModuleType<? extends LivingEntity> memoryModuleType) {
        return abstractPiglin.getBrain().getMemory(memoryModuleType).filter((livingEntity) -> {
            return livingEntity.closerThan(abstractPiglin, 36);
        });
    }

    protected static void wasHurtBy(PiglinBrute piglinBrute, LivingEntity livingEntity) {
        if (!(livingEntity instanceof AbstractPiglin)) {
            maybeRetaliate(piglinBrute, livingEntity);
        }
    }
    protected static void maybeRetaliate(AbstractPiglin abstractPiglin, LivingEntity livingEntity) {
        if (!abstractPiglin.getBrain().isActive(Activity.AVOID)) {
            if (Sensor.isEntityAttackableIgnoringLineOfSight(abstractPiglin, livingEntity)) {
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
    protected static void broadcastUniversalAnger(AbstractPiglin abstractPiglin) {
        getAdultPiglins(abstractPiglin).forEach((abstractPiglinx) -> {
            getNearestVisibleTargetablePlayer(abstractPiglinx).ifPresent((player) -> {
                setAngerTarget(abstractPiglinx, player);
            });
        });
    }

    private static List<AbstractPiglin> getAdultPiglins(AbstractPiglin abstractPiglin) {
        return (List)abstractPiglin.getBrain().getMemory(MemoryModuleType.NEARBY_ADULT_PIGLINS).orElse(ImmutableList.of());
    }
    protected static void broadcastAngerTarget(AbstractPiglin abstractPiglin, LivingEntity livingEntity) {
        getAdultPiglins(abstractPiglin).forEach((abstractPiglinx) -> {
                setAngerTargetIfCloserThanCurrent(abstractPiglinx, livingEntity);
        });
    }
    private static Optional<LivingEntity> getAngerTarget(AbstractPiglin abstractPiglin) {
        return BehaviorUtils.getLivingEntityFromUUIDMemory(abstractPiglin, MemoryModuleType.ANGRY_AT);
    }
    private static void setAngerTargetIfCloserThanCurrent(AbstractPiglin abstractPiglin, LivingEntity livingEntity) {
        Optional<LivingEntity> optional = getAngerTarget(abstractPiglin);
        LivingEntity livingEntity2 = BehaviorUtils.getNearestTarget(abstractPiglin, optional, livingEntity);
        if (!optional.isPresent() || optional.get() != livingEntity2) {
            setAngerTarget(abstractPiglin, livingEntity2);
        }
    }
    private static void setAngerTargetToNearestTargetablePlayerIfFound(AbstractPiglin abstractPiglin, LivingEntity livingEntity) {
        Optional<Player> optional = getNearestVisibleTargetablePlayer(abstractPiglin);
        if (optional.isPresent()) {
            setAngerTarget(abstractPiglin, (LivingEntity)optional.get());
        } else {
            setAngerTarget(abstractPiglin, livingEntity);
        }

    }

    protected static void setAngerTarget(AbstractPiglin piglinBrute, LivingEntity livingEntity) {
        piglinBrute.getBrain().eraseMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
        piglinBrute.getBrain().setMemoryWithExpiry(MemoryModuleType.ANGRY_AT, livingEntity.getUUID(), 600L);
    }

    protected static void maybePlayActivitySound(PiglinBrute piglinBrute) {
        if ((double)piglinBrute.level.random.nextFloat() < 0.0125D) {
            playActivitySound(piglinBrute);
        }

    }

    private static void playActivitySound(PiglinBrute piglinBrute) {
        piglinBrute.getBrain().getActiveNonCoreActivity().ifPresent((activity) -> {


        });
    }
}


