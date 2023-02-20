package net.spellbladenext.fabric;

import com.google.common.collect.ImmutableMap;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.behavior.CrossbowAttack;
import net.minecraft.world.entity.ai.behavior.EntityTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.monster.CrossbowAttackMob;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.entity.SpellProjectile;
import net.spell_engine.internals.SpellHelper;
import net.spell_engine.internals.SpellRegistry;
import net.spell_engine.utils.SoundHelper;
import net.spell_power.api.MagicSchool;
import net.spell_power.api.SpellPower;
import net.spellbladenext.SpellbladeNext;

import static net.spell_engine.internals.SpellHelper.impactTargetingMode;
import static net.spell_engine.internals.SpellHelper.launchPoint;

public class SpellAttack<E extends Mob, T extends LivingEntity> extends Behavior<E> {
    private static final int TIMEOUT = 1200;
    private int attackDelay = 20;
    private CrossbowState crossbowState;
    public SpellAttack() {
        super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT), 1200);
        this.crossbowState = CrossbowState.UNCHARGED;
    }

    protected boolean checkExtraStartConditions(ServerLevel serverLevel, E mob) {
        LivingEntity livingEntity = getAttackTarget(mob);
        return  mob instanceof Reaver reaver && reaver.isCaster() && BehaviorUtils.canSee(mob, livingEntity) && mob.distanceTo(livingEntity) < 32;
    }

    protected boolean canStillUse(ServerLevel serverLevel, E mob, long l) {
        return mob.getBrain().hasMemoryValue(MemoryModuleType.ATTACK_TARGET) && this.checkExtraStartConditions(serverLevel, mob);
    }

    protected void tick(ServerLevel serverLevel, E mob, long l) {
        LivingEntity livingEntity = getAttackTarget(mob);
        //this.lookAtTarget(mob, livingEntity);
        this.crossbowAttack(mob, livingEntity);
    }

    protected void stop(ServerLevel serverLevel, E mob, long l) {
        if (mob.isUsingItem()) {
            mob.stopUsingItem();
        }


    }

    private void crossbowAttack(E mob, LivingEntity target) {
        if(attackDelay <= 0) {
            if(mob instanceof Reaver reaver && reaver.getMagicSchool() == MagicSchool.ARCANE) {
                Spell spell = SpellRegistry.getSpell(new ResourceLocation(SpellbladeNext.MOD_ID, "arcane_missile"));
                SpellHelper.ImpactContext context = new SpellHelper.ImpactContext(1, 1.0F, (Vec3) null, new SpellPower.Result(MagicSchool.ARCANE, mob.getAttributeValue(Attributes.ATTACK_DAMAGE)*0.8, 0, 1), impactTargetingMode(spell));
                Vec3 launchPoint = launchPoint(mob);
                SpellProjectile projectile = new SpellProjectile(mob.level, mob, launchPoint.x(), launchPoint.y(), launchPoint.z(), SpellProjectile.Behaviour.FLY, spell, target, context);
                Spell.ProjectileData projectileData = spell.release.target.projectile;
                projectileData.homing_angle = 15;
                float velocity = projectileData.velocity;
                float divergence = projectileData.divergence;
                SoundHelper.playSoundEvent(mob.getLevel(), mob, SoundEvents.ILLUSIONER_CAST_SPELL, 1, 1);
                Vec3 look = target.getBoundingBox().getCenter().subtract(launchPoint).normalize();
                projectile.shoot(0, 1, 0, velocity, divergence);

                projectile.range = spell.range;
                projectile.getViewXRot(mob.getXRot());
                projectile.setYRot(mob.getYRot());
                mob.getLevel().addFreshEntity(projectile);
                attackDelay = 20;
            }
            if(mob instanceof Reaver reaver && reaver.getMagicSchool() == MagicSchool.FROST) {
                Spell spell = SpellRegistry.getSpell(new ResourceLocation(SpellbladeNext.MOD_ID, "frostbolt"));
                SpellHelper.ImpactContext context = new SpellHelper.ImpactContext(1, 1.0F, (Vec3) null, new SpellPower.Result(MagicSchool.FROST, mob.getAttributeValue(Attributes.ATTACK_DAMAGE)*0.6, 0, 1), impactTargetingMode(spell));
                Vec3 launchPoint = launchPoint(mob);
                SpellProjectile projectile = new SpellProjectile(mob.level, mob, launchPoint.x(), launchPoint.y(), launchPoint.z(), SpellProjectile.Behaviour.FLY, spell, target, context);
                Spell.ProjectileData projectileData = spell.release.target.projectile;
                float additional = 7.5F*reaver.getRandom().nextFloat();
                projectileData.homing_angle = 7.5F+additional;
                float velocity = projectileData.velocity;
                float divergence = projectileData.divergence;
                SoundHelper.playSoundEvent(mob.getLevel(), mob, SoundEvents.ILLUSIONER_CAST_SPELL, 1, 1.2F);
                Vec3 look = target.getBoundingBox().getCenter().subtract(launchPoint).normalize();
                projectile.shoot(0, 1, 0, velocity, divergence);

                projectile.range = spell.range;
                projectile.getViewXRot(mob.getXRot());
                projectile.setYRot(mob.getYRot());
                mob.getLevel().addFreshEntity(projectile);
                attackDelay = 10;
            }
            if(mob instanceof Reaver reaver && reaver.getMagicSchool() == MagicSchool.FIRE) {
                Spell spell = SpellRegistry.getSpell(new ResourceLocation(SpellbladeNext.MOD_ID, "fireball"));
                SpellHelper.ImpactContext context = new SpellHelper.ImpactContext(1, 1.0F, (Vec3) null, new SpellPower.Result(MagicSchool.FIRE, mob.getAttributeValue(Attributes.ATTACK_DAMAGE)*1, 0, 1), impactTargetingMode(spell));
                Vec3 launchPoint = launchPoint(mob);
                SpellProjectile projectile = new SpellProjectile(mob.level, mob, launchPoint.x(), launchPoint.y(), launchPoint.z(), SpellProjectile.Behaviour.FLY, spell, target, context);
                Spell.ProjectileData projectileData = spell.release.target.projectile;
                projectileData.homing_angle = 15;
                float velocity = projectileData.velocity;
                float divergence = projectileData.divergence;
                SoundHelper.playSoundEvent(mob.getLevel(), mob, SoundEvents.BLAZE_SHOOT, 1, 1);
                Vec3 look = target.getBoundingBox().getCenter().subtract(launchPoint).normalize();
                projectile.shoot(0, 1, 0, velocity, divergence);

                projectile.range = spell.range;
                projectile.getViewXRot(mob.getXRot());
                projectile.setYRot(mob.getYRot());
                mob.getLevel().addFreshEntity(projectile);
                attackDelay = 40;
            }
        }
        else{
            attackDelay--;
        }
    }

    private void lookAtTarget(Mob mob, LivingEntity livingEntity) {
        mob.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, new EntityTracker(livingEntity, true));
    }

    private static LivingEntity getAttackTarget(LivingEntity livingEntity) {
        return (LivingEntity)livingEntity.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).get();
    }

    static enum CrossbowState {
        UNCHARGED,
        CHARGING,
        CHARGED,
        READY_TO_ATTACK;

        private CrossbowState() {
        }
    }
}

