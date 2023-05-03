package net.spellbladenext.entities;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.spell_engine.api.spell.ParticleBatch;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.client.SpellEngineClient;
import net.spell_engine.internals.SpellHelper;
import net.spell_engine.particle.ParticleHelper;
import net.spell_engine.utils.TargetHelper;
import net.spell_power.api.SpellPower;
import net.spellbladenext.SpellbladeNext;
import net.spellbladenext.items.FriendshipBracelet;

import java.util.List;
import java.util.function.Predicate;

public class AmethystEntity extends AbstractArrow implements ItemSupplier {
    public boolean inGround = false;
    public double damage = 1;
    public SpellPower.Result power;
    int i = 0;
    List<Entity> detected;
    public SpellHelper.ImpactContext context;
    public Spell spell;


    public AmethystEntity(EntityType<? extends AmethystEntity> p_36721_, Level p_36722_,Player player) {
        super(p_36721_, p_36722_);
        this.setOwner(player);
        Vec3 vec3 = player.getViewVector(0);
        this.setNoGravity(true);

        double d0 = vec3.horizontalDistance();
        this.setYRot(((float) (Mth.atan2(player.getViewVector(0).x, player.getViewVector(0).z) * (double) (180F / (float) Math.PI))));
        this.setXRot((float) (Mth.atan2(player.getViewVector(0).y, d0) * (double) (180F / (float) Math.PI)));
        this.yRotO = ((float) (Mth.atan2(player.getViewVector(0).x, player.getViewVector(0).z) * (double) (180F / (float) Math.PI)));
        this.xRotO = (float) (Mth.atan2(player.getViewVector(0).y, d0) * (double) (180F / (float) Math.PI));

    }

    public AmethystEntity(EntityType<? extends AmethystEntity> p_36721_, Level p_36722_) {
        super(p_36721_, p_36722_);
        this.setNoGravity(true);

    }

    @Override
    public void tick() {
        if(this.tickCount > 200 && !this.getLevel().isClientSide()){
            this.discard();
        }
        ParticleHelper.play(this.getLevel(),this,this.getXRot(),this.getYRot(), new ParticleBatch("spell_engine:arcane_spell", ParticleBatch.Shape.CIRCLE, ParticleBatch.Origin.CENTER, ParticleBatch.Rotation.LOOK,3,0,0,0));
        if(this.firstTick){
            this.playSound(SoundEvents.AMETHYST_BLOCK_CHIME);
        }

        if(this.tickCount <= 20 && !this.getLevel().isClientSide()){
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.8,0.8,0.8));
        }

        /*if(this.target != null&& !this.target.isDeadOrDying()  && this.tickCount > 20 && !this.inGround){
            this.setDeltaMovement(this.getDeltaMovement().add((this.target.position().add(new Vec3(0,this.target.getBoundingBox().getYsize()/2,0).subtract(this.position()))).normalize().multiply(1,1,1)));
            if(this.getDeltaMovement().length() > 2){
                this.setDeltaMovement(this.getDeltaMovement().normalize().multiply(2,2,2));
            }
        }*/
        if(this.tickCount > 20) {
            this.setNoGravity(false);
        }
        if(this.tickCount > 20) {
            Vec3 vec3 = this.getDeltaMovement();
            double d0 = vec3.horizontalDistance();
            this.xRotO = this.getXRot();

            this.setXRot(this.xRotO+144);
            double d5 = vec3.x;
            double d6 = vec3.y;
            double d1 = vec3.z;
            double d7 = this.getX() + d5;
            double d2 = this.getY() + d6;
            double d3 = this.getZ() + d1;
            double d4 = vec3.horizontalDistance();

            //this.setYRot((float)(Mth.atan2(d5, d1) * (double)(180F / (float)Math.PI)));

            //this.setXRot((float)(Mth.atan2(d6, d4) * (double)(180F / (float)Math.PI)));
            //this.setYRot(lerpRotation(this.yRotO, this.getYRot()));
            Vec3 vec32 = this.position();
            Vec3 vec33 = vec32.add(vec3);
            this.setXRot(Mth.lerp(0.2F, this.xRotO, this.getXRot()));

            if(this.getOwner() instanceof LivingEntity && !this.getLevel().isClientSide()) {
                if (this.getOwner() instanceof Player player && this.detected != null) {
                    if (!this.detected.isEmpty() && ((int) (this.i / 4D)) < this.detected.toArray().length && this.i / 4D < 10 && this.power != null) {
                        AmethystEntity2 amethystEntity2 = new AmethystEntity2(SpellbladeNext.AMETHYST2, this.getLevel(), player);
                        Vec3 vec31 = (this.detected.get((int) (this.i / 4D))).position().add(0, this.detected.get((int) (this.i / 4D)).getBoundingBox().getYsize() / 2, 0).subtract(this.position()).normalize();
                        amethystEntity2.power = this.power;
                        amethystEntity2.context = this.context;
                        amethystEntity2.spell = this.spell;
                        amethystEntity2.setPos(this.position());
                        amethystEntity2.shoot(vec31.x, vec31.y, vec31.z, 2, 0);
                        this.getLevel().addFreshEntity(amethystEntity2);
                        this.i = this.i + 1;
                    } else if (!this.getLevel().isClientSide()) {
                        this.discard();
                    }
                }

                if (this.detected == null && !this.getLevel().isClientSide()) {
                    this.discard();
                }
            }
        }
        else{
            super.baseTick();
            this.setPos(this.position().add(this.getDeltaMovement()));
            if(!this.inGround && !this.getLevel().isClientSide()) {
                Vec3 vec3 = this.getDeltaMovement();
                double d0 = vec3.horizontalDistance();
                this.xRotO = this.getXRot();

                this.setXRot(this.xRotO+144);
                double d5 = vec3.x;
                double d6 = vec3.y;
                double d1 = vec3.z;
                double d7 = this.getX() + d5;
                double d2 = this.getY() + d6;
                double d3 = this.getZ() + d1;
                double d4 = vec3.horizontalDistance();

                //this.setYRot((float)(Mth.atan2(d5, d1) * (double)(180F / (float)Math.PI)));

                //this.setXRot((float)(Mth.atan2(d6, d4) * (double)(180F / (float)Math.PI)));
                //this.setYRot(lerpRotation(this.yRotO, this.getYRot()));
                Vec3 vec32 = this.position();
                Vec3 vec33 = vec32.add(vec3);
            }
            this.setXRot(Mth.lerp(0.2F, this.xRotO, this.getXRot()));



            Vec3 vec3 = this.getDeltaMovement();
            Vec3 vec32 = this.position();
            Vec3 vec33 = vec32.add(vec3);
            HitResult hitresult = this.level.clip(new ClipContext(vec32, vec33, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));

            if (hitresult.getType() == HitResult.Type.BLOCK) {
                vec33 = hitresult.getLocation();
                this.discard();
            }
        }
        if(this.getOwner() instanceof Player player && !this.getLevel().isClientSide()) {
            if (this.tickCount == 20) {
                if(this.detected == null) {
                    Predicate<Entity> selectionPredicate = (target) -> {
                        return (TargetHelper.actionAllowed(TargetHelper.TargetingMode.AREA, TargetHelper.Intent.HARMFUL, player, target)
                                && FriendshipBracelet.PlayerFriendshipPredicate(player,target) && target instanceof LivingEntity);
                    };

                    Spell.Release.Target.Area area = new Spell.Release.Target.Area();
                    area.angle_degrees = 360;
                    this.detected = TargetHelper.targetsFromArea(this, this.position(),16F, area,  selectionPredicate);

                }
            }
        }
    }
    @Override
    protected ItemStack getPickupItem() {
        return Items.AIR.getDefaultInstance();
    }

    @Override
    protected SoundEvent getDefaultHitGroundSoundEvent() {
        return SoundEvents.AMETHYST_BLOCK_BREAK;
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        return;
    }
    @Override
    protected boolean tryPickup(Player player) {
        return false;
    }
    @Override
    protected void onHitBlock(BlockHitResult blockHitResult) {

        this.discard();
        super.onHitBlock(blockHitResult);
    }

    @Override
    public ItemStack getItem() {
        return Items.AMETHYST_SHARD.getDefaultInstance();
    }

    @Override
    public boolean isAttackable() {
        return false;
    }
}
