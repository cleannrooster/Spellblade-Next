package net.spellbladenext.fabric.entities;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class IceCrashEntity extends AbstractHurtingProjectile {

    public IceCrashEntity(EntityType<IceCrashEntity> entityType, Level level) {
        super(entityType, level);
        this.noPhysics = true;
        this.setPos(this.getX(),(int)((this.getY()*2)/2),this.getZ());
    }

    @Override
    protected ParticleOptions getTrailParticle() {
        return ParticleTypes.SNOWFLAKE;
    }

    @Override
    protected boolean shouldBurn() {
        return false;
    }

    @Override
    public void setNoGravity(boolean bl) {
        super.setNoGravity(bl);
    }
    @Override
    public boolean canBeCollidedWith() {
        return true;
    }


    @Override
    public boolean skipAttackInteraction(Entity entity) {
        return true;
    }

    @Override
    public boolean hurt(DamageSource damageSource, float f) {
        return false;
    }

    @Override
    public void tick() {
            super.tick();
            if(tickCount > 80 && !this.getLevel().isClientSide()){
                this.setPos(this.position().subtract(0,(this.getBbHeight()/(10*2)),0));
            }
            if(this.tickCount > 100 && !this.getLevel().isClientSide()){
                this.discard();
            }
    }

    @Override
    public boolean canCollideWith(Entity entity) {
        return !entity.isPassenger();
    }

    @Override
    public void defineSynchedData() {
        super.defineSynchedData();
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);

    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {

    }

    @Override
    protected void onHitBlock(BlockHitResult blockHitResult) {

    }

    @Override
    protected void onHit(HitResult hitResult) {

    }
}
