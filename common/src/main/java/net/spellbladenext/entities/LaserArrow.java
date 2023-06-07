package net.spellbladenext.entities;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.entity.SpellProjectile;
import net.spell_engine.internals.SpellCasterEntity;
import net.spell_engine.internals.SpellContainerHelper;
import net.spell_engine.internals.SpellHelper;
import net.spell_engine.internals.SpellRegistry;
import net.spell_engine.particle.ParticleHelper;
import net.spell_engine.utils.TargetHelper;
import net.spell_engine.utils.VectorHelper;
import net.spell_power.api.SpellPower;
import net.spellbladenext.SpellbladeNext;
import net.spellbladenext.items.FriendshipBracelet;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

import static net.spell_engine.internals.SpellHelper.impactTargetingMode;
import static net.spellbladenext.SpellbladeNext.LASERARROW;

public class LaserArrow extends AbstractArrow implements ItemSupplier {

    public LivingEntity target = null;
    public int chaining = 0;
    public boolean burst;
    private Entity cachedTarget;
    private UUID targetUUID;
    private LivingEntity old;

    public void setTarget(@Nullable Entity entity) {
        if (entity != null) {
            this.targetUUID = entity.getUUID();
            this.cachedTarget = entity;
        }

    }
    @Nullable
    public Entity getTarget() {
        if (this.cachedTarget != null && !this.cachedTarget.isRemoved()) {
            return this.cachedTarget;
        } else if (this.targetUUID != null && this.level instanceof ServerLevel) {
            this.cachedTarget = ((ServerLevel)this.level).getEntity(this.targetUUID);
            return this.cachedTarget;
        } else {
            return null;
        }
    }
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        if (this.target != null) {
            compoundTag.putUUID("Target", this.targetUUID);
        }
        super.addAdditionalSaveData(compoundTag);

    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        if(!this.getLevel().isClientSide() && entityHitResult.getEntity() != null && entityHitResult.getEntity() instanceof LivingEntity living && living != this.old && this.getOwner() instanceof Player player){
            living.invulnerableTime = 0;
            Predicate<Entity> selectionPredicate = (target) -> {
                return (TargetHelper.actionAllowed(TargetHelper.TargetingMode.AREA, TargetHelper.Intent.HARMFUL, player, target)
                        );
            };
            this.chaining--;
            List<LivingEntity> list = this.getLevel().getNearbyEntities(LivingEntity.class,TargetingConditions.DEFAULT,living,this.getBoundingBox().inflate(6));
            list.removeIf(entity -> !selectionPredicate.test(entity));
            if(!list.isEmpty() &&this.chaining > 0 && this.getLevel().getNearestEntity(list, TargetingConditions.DEFAULT,living,living.getX(),living.getY(),living.getZ()) != null){
                LaserArrow laserArrow = new LaserArrow(LASERARROW, player.getLevel());
                laserArrow.setOwner(player);
                laserArrow.setTarget(this.getLevel().getNearestEntity(list, TargetingConditions.DEFAULT,living,living.getX(),living.getY(),living.getZ()));
                laserArrow.old = living;
                laserArrow.setBaseDamage(this.getBaseDamage());
                laserArrow.setKnockback(this.getKnockback());
                if(this.isOnFire()){
                    laserArrow.setSecondsOnFire(100);
                }
                laserArrow.chaining = this.chaining;
                laserArrow.setPos(living.getX(),living.getY()+living.getBbHeight()/2,living.getZ());
                laserArrow.shoot(laserArrow.getTarget().getX()-living.getX(),laserArrow.getTarget().getY()-living.getY(),laserArrow.getTarget().getZ()-living.getZ(),2.0F,1.0F);
                living.getLevel().addFreshEntity(laserArrow);
            }
            Player player1 = player;
            ItemStack stack = player1.getMainHandItem();
            if(this.burst) {
                if (player.getRandom().nextInt(3) == 1) {
                    Spell spell = SpellRegistry.getSpell(new ResourceLocation(SpellbladeNext.MOD_ID, "frostoverdrive"));


                    if (player1 instanceof SpellCasterEntity entity && !entity.getCooldownManager().isCoolingDown(new ResourceLocation(SpellbladeNext.MOD_ID, "frostoverdrive"))) {
                        List<Entity> targets = player1.getLevel().getEntities((LivingEntity) null, living.getBoundingBox().inflate(6), selectionPredicate);
                        int i = 0;
                        if (entity.getCurrentSpellId() != null) {
                            i = (int) (entity.getCurrentCastProgress() * SpellHelper.getCastDuration(player1, SpellRegistry.getSpell(entity.getCurrentSpellId())));
                        }
                        if (spell != null) {
                            SpellHelper.ImpactContext context = new SpellHelper.ImpactContext(1.0F, 1.0F, (Vec3) null, SpellPower.getSpellPower(spell.school, player1), impactTargetingMode(spell));

                            if (SpellHelper.ammoForSpell(player1, SpellRegistry.getSpell(new ResourceLocation(SpellbladeNext.MOD_ID, "frostoverdrive")), stack).satisfied()) {
                                for (Entity target1 : targets) {
                                    SpellHelper.performImpacts(player1.getLevel(), player1, target1, SpellRegistry.getSpell(new ResourceLocation(SpellbladeNext.MOD_ID, "frostoverdrive")), new SpellHelper.ImpactContext());
                                }
                                entity.getCooldownManager().set(new ResourceLocation(SpellbladeNext.MOD_ID, "frostoverdrive"), (int) (20 * SpellHelper.getCooldownDuration(player1, spell)));
                                ParticleHelper.sendBatches(living, spell.release.particles);

                            }
                        }
                    }
                }
                if (player.getRandom().nextInt(3) == 1) {

                    Spell spell = SpellRegistry.getSpell(new ResourceLocation(SpellbladeNext.MOD_ID, "fireoverdrive"));

                    if (player1 instanceof SpellCasterEntity entity && !entity.getCooldownManager().isCoolingDown(new ResourceLocation(SpellbladeNext.MOD_ID, "fireoverdrive"))) {
                        int i = 0;
                        List<Entity> targets = player1.getLevel().getEntities((LivingEntity) null, living.getBoundingBox().inflate(6), selectionPredicate);

                        if (entity.getCurrentSpellId() != null) {
                            i = (int) (entity.getCurrentCastProgress() * SpellHelper.getCastDuration(player1, SpellRegistry.getSpell(entity.getCurrentSpellId())));
                        }
                        if (spell != null) {
                            SpellHelper.ImpactContext context = new SpellHelper.ImpactContext(1.0F, 1.0F, (Vec3) null, SpellPower.getSpellPower(spell.school, player1), impactTargetingMode(spell));

                            if (SpellHelper.ammoForSpell(player1, SpellRegistry.getSpell(new ResourceLocation(SpellbladeNext.MOD_ID, "fireoverdrive")), stack).satisfied()) {
                                for (Entity target1 : targets) {
                                    SpellHelper.performImpacts(player1.getLevel(), player1, target1, SpellRegistry.getSpell(new ResourceLocation(SpellbladeNext.MOD_ID, "fireoverdrive")), new SpellHelper.ImpactContext());
                                }
                                entity.getCooldownManager().set(new ResourceLocation(SpellbladeNext.MOD_ID, "fireoverdrive"), (int) (20 * SpellHelper.getCooldownDuration(player1, spell)));
                                ParticleHelper.sendBatches(living, spell.release.particles);

                            }
                        }
                    }
                }

                if (player.getRandom().nextInt(3) == 1) {

                    Spell spell = SpellRegistry.getSpell(new ResourceLocation(SpellbladeNext.MOD_ID, "arcaneoverdrive"));
                    if (player1 instanceof SpellCasterEntity entity && !entity.getCooldownManager().isCoolingDown(new ResourceLocation(SpellbladeNext.MOD_ID, "arcaneoverdrive"))) {
                        int i = 0;
                        List<Entity> targets = player1.getLevel().getEntities((LivingEntity) null, living.getBoundingBox().inflate(6), selectionPredicate);

                        if (entity.getCurrentSpellId() != null) {
                            i = (int) (entity.getCurrentCastProgress() * SpellHelper.getCastDuration(player1, SpellRegistry.getSpell(entity.getCurrentSpellId())));
                        }
                        if (spell != null) {
                            SpellHelper.ImpactContext context = new SpellHelper.ImpactContext(1.0F, 1.0F, (Vec3) null, SpellPower.getSpellPower(spell.school, player1), impactTargetingMode(spell));

                            if (SpellHelper.ammoForSpell(player1, SpellRegistry.getSpell(new ResourceLocation(SpellbladeNext.MOD_ID, "arcaneoverdrive")), stack).satisfied()) {
                                for (Entity target1 : targets) {
                                    SpellHelper.performImpacts(player1.getLevel(), player1, target1, SpellRegistry.getSpell(new ResourceLocation(SpellbladeNext.MOD_ID, "arcaneoverdrive")), new SpellHelper.ImpactContext());
                                }
                                entity.getCooldownManager().set(new ResourceLocation(SpellbladeNext.MOD_ID, "arcaneoverdrive"), (int) (20 * SpellHelper.getCooldownDuration(player1, spell)));
                                ParticleHelper.sendBatches(living, spell.release.particles);

                            }
                        }
                    }
                }
            }
            super.onHitEntity(entityHitResult);

            this.discard();
        }


    }

    public void readAdditionalSaveData(CompoundTag compoundTag) {
        if (compoundTag.hasUUID("Target")) {
            this.targetUUID = compoundTag.getUUID("Target");
        }
        super.readAdditionalSaveData(compoundTag);
    }
    public LaserArrow(EntityType<? extends AbstractArrow> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    @Override
    public ItemStack getItem() {
        return new ItemStack(SpellbladeNext.LASERARROWITEM.get());
    }

    @Override
    public void tick() {
        this.getLevel().addParticle(ParticleTypes.ELECTRIC_SPARK,this.getX(),this.getY(),this.getZ(),-this.getDeltaMovement().x(),-this.getDeltaMovement().y(),-this.getDeltaMovement().z);
    if(!this.getLevel().isClientSide()  && this.tickCount > 80){
        this.discard();
    }
    if(getTarget() != null) {
        Vec3 distanceVector = getTarget().position().add(0.0D, (double) (getTarget().getBbHeight() / 2.0F), 0.0D).subtract(this.position().add(0.0D, (double) (this.getBbHeight() / 2.0F), 0.0D));
        Vec3 newVelocity = VectorHelper.rotateTowards(this.getDeltaMovement(), distanceVector, (double) (this.tickCount));

        if (newVelocity.lengthSqr() > 0.0D) {
            this.setDeltaMovement(newVelocity);
            this.hasImpulse = true;
        }
    }
        //ProjectileUtil.rotateTowardsMovement(this,0.5F);

        super.tick();

    }

    @Override
    protected void onHitBlock(BlockHitResult blockHitResult) {
        this.discard();
    }

    @Override
    protected ItemStack getPickupItem() {
        return ItemStack.EMPTY;
    }
}
