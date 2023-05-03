package net.spellbladenext.fabric.entities.ai;

import com.google.common.collect.ImmutableMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.EntityTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.phys.Vec3;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.internals.SpellRegistry;
import net.spell_engine.particle.ParticleHelper;
import net.spell_engine.utils.SoundHelper;
import net.spell_power.api.MagicSchool;
import net.spell_power.api.SpellDamageSource;
import net.spellbladenext.SpellbladeNext;
import net.spellbladenext.fabric.entities.Magus;

import java.util.List;
import java.util.Optional;

import static net.spellbladenext.fabric.entities.Magus.JUMPING;
import static net.spellbladenext.fabric.entities.Magus.TIER;

public class MagusJumpBack <E extends Magus> extends Behavior<E> {
    private final double tooCloseDistance;
    private final float strafeSpeed;
    float time = 0;
    boolean bool = true;

    public MagusJumpBack(double i, float f) {
        super(ImmutableMap.of( MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryStatus.VALUE_PRESENT));
        this.tooCloseDistance = i;
        this.strafeSpeed = f;
    }

    protected boolean checkExtraStartConditions(ServerLevel serverLevel, E mob) {
        return this.isTargetVisible(mob) && this.isTargetTooClose(mob) && mob.getMaxHealth()/10 < mob.damagetakensincelastthink;
    }

    protected void start(ServerLevel serverLevel, E mob, long l) {
        //System.out.println("backing up!");
        if(this.getTarget(mob).isPresent()) {
            mob.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, new EntityTracker(this.getTarget(mob).get(), true));
        }
        bool = serverLevel.random.nextBoolean();

        //mob.getMoveControl().strafe(-this.strafeSpeed, 0.0F);
        //mob.setYRot(Mth.rotateIfNecessary(mob.getYRot(), mob.yHeadRot, 0.0F));
    }

    @Override
    protected boolean canStillUse(ServerLevel serverLevel, E livingEntity, long l) {
        return this.time <=40;
    }

    @Override
    protected void stop(ServerLevel serverLevel, E livingEntity, long l) {
        this.time = 0;
        if(this.getTarget(livingEntity).isPresent()) {
            Vec3 vec31 = new Vec3(-this.getTarget(livingEntity).get().getX()+livingEntity.getX(),0,-this.getTarget(livingEntity).get().getZ()+livingEntity.getZ());
            Vec3 vec3 = new Vec3(vec31.normalize().x*1, 0.5,vec31.normalize().z*1 );
            livingEntity.setPos(livingEntity.position().add(0,0.2,0));
            livingEntity.setOnGround(false);
            livingEntity.setDeltaMovement(vec3);
            livingEntity.isthinking = true;
            livingEntity.thinktime = 0;
            livingEntity.damagetakensincelastthink = 0;
            livingEntity.casting = true;
            livingEntity.getEntityData().set(JUMPING,true);
            livingEntity.getEntityData().set(TIER,livingEntity.getEntityData().get(TIER)+1);
        }

        super.stop(serverLevel, livingEntity, l);
    }

    @Override
    protected void tick(ServerLevel serverLevel, E livingEntity, long l) {
        super.tick(serverLevel, livingEntity, l);
        int i = 1;
        if(bool){
            i = -1;
        }
        if(this.getTarget(livingEntity).isPresent()) {
            int ii = 1;
            if(this.isTargetTooClose(livingEntity)){
             ii = -1;
            }
            livingEntity.getMoveControl().strafe(ii, i);
            livingEntity.lookAt(this.getTarget(livingEntity).get(),999,999);
        }
        if(time % 10 == 0) {
            if (livingEntity.getMagicSchool() == MagicSchool.ARCANE) {
                Spell spell = SpellRegistry.getSpell(new ResourceLocation(SpellbladeNext.MOD_ID, "arcaneoverdrive"));
                if (!serverLevel.isClientSide()) {
                    ParticleHelper.sendBatches(livingEntity, spell.release.particles);
                    SoundHelper.playSound(serverLevel,livingEntity,spell.release.sound);

                }

                List<Entity> entities = serverLevel.getEntitiesOfClass(Entity.class, livingEntity.getBoundingBox().inflate(4, 2, 4), entity -> entity != livingEntity);
                for (Entity entity : entities) {
                    entity.hurt(SpellDamageSource.mob(MagicSchool.ARCANE, livingEntity), (float) livingEntity.getAttributeValue(Attributes.ATTACK_DAMAGE) * 0.2F);
                }
            }
            if (livingEntity.getMagicSchool() == MagicSchool.FIRE) {
                Spell spell = SpellRegistry.getSpell(new ResourceLocation(SpellbladeNext.MOD_ID, "fireoverdrive"));
                if (!serverLevel.isClientSide()) {
                    ParticleHelper.sendBatches(livingEntity, spell.release.particles);
                    SoundHelper.playSound(serverLevel,livingEntity,spell.release.sound);
                }

                List<Entity> entities = serverLevel.getEntitiesOfClass(Entity.class, livingEntity.getBoundingBox().inflate(4, 2, 4), entity -> entity != livingEntity);
                for (Entity entity : entities) {
                    entity.hurt(SpellDamageSource.mob(MagicSchool.FIRE, livingEntity), (float) livingEntity.getAttributeValue(Attributes.ATTACK_DAMAGE) * 0.2F);

                }
            }
            if (livingEntity.getMagicSchool() == MagicSchool.FROST) {
                Spell spell = SpellRegistry.getSpell(new ResourceLocation(SpellbladeNext.MOD_ID, "frostoverdrive"));
                if (!serverLevel.isClientSide()) {
                    ParticleHelper.sendBatches(livingEntity, spell.release.particles);
                    SoundHelper.playSound(serverLevel,livingEntity,spell.release.sound);

                }

                List<Entity> entities = serverLevel.getEntitiesOfClass(Entity.class, livingEntity.getBoundingBox().inflate(4, 2, 4), entity -> entity != livingEntity);
                for (Entity entity : entities) {
                    entity.hurt(SpellDamageSource.mob(MagicSchool.FROST, livingEntity), (float) livingEntity.getAttributeValue(Attributes.ATTACK_DAMAGE) * 0.2F);
                }
            }
        }

        time++;
    }

    private boolean isTargetVisible(E mob) {
        if(this.getTarget(mob).isPresent()) {

            return ((NearestVisibleLivingEntities) mob.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES).get()).contains(this.getTarget(mob).get());
        }
        return false;
    }

    private boolean isTargetTooClose(E mob) {
        if(this.getTarget(mob).isPresent()) {
        return this.getTarget(mob).get().closerThan(mob, (double)this.tooCloseDistance);
        }
        return false;
    }

    private Optional<LivingEntity> getTarget(E mob) {
        return mob.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET);
    }
}
