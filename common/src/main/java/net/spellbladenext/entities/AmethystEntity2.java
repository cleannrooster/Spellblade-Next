package net.spellbladenext.entities;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ItemSupplier;
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
import net.spell_engine.entity.SpellProjectile;
import net.spell_engine.internals.SpellHelper;
import net.spell_engine.particle.ParticleHelper;
import net.spell_power.api.MagicSchool;
import net.spell_power.api.SpellDamageSource;
import net.spell_power.api.SpellPower;

import java.util.List;
import java.util.stream.Stream;

public class AmethystEntity2 extends SpellProjectile implements ItemSupplier {
    public LivingEntity target;
    public boolean inGround = false;
    public double damage = 1;
    public SpellPower.Result power;
    int i = 0;
    public Spell spell;
    public SpellHelper.ImpactContext context;


    public AmethystEntity2(EntityType<? extends AmethystEntity2> p_36721_, Level p_36722_, Player player) {
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
    public AmethystEntity2(EntityType<? extends AmethystEntity2> p_36721_, Level p_36722_) {
        super(p_36721_, p_36722_);
        this.setNoGravity(true);

    }
    @Override
    public Behaviour behaviour() {
        return Behaviour.FLY;
    }
    @Override
    public void tick() {
        if(this.tickCount > 80 && !this.getLevel().isClientSide()) {
            this.discard();
        }
        super.tick();
        Vec3 vec3 = this.getDeltaMovement();

        double d0 = vec3.horizontalDistance();
        this.setYRot(((float) (Mth.atan2(vec3.x, vec3.z) * (double) (180F / (float) Math.PI))));
        this.setXRot((float) (Mth.atan2(vec3.y, d0) * (double) (180F / (float) Math.PI)));
        this.yRotO = this.getYRot();
        this.xRotO = this.getXRot();

    }

    @Override
    public boolean isAttackable() {
        return false;
    }
    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        //float knockbackMultiplier = Math.max(0.0F, damageData.knockback * context.total());
        if(this.getOwner() instanceof LivingEntity owner && this.power != null && this.spell != null && this.context != null && entityHitResult.getEntity().invulnerableTime <= 10) {
            if(SpellHelper.performImpacts(this.getLevel(),owner, entityHitResult.getEntity(), this.spell,this.context)) {
                entityHitResult.getEntity().invulnerableTime = 20;
            }
        }
        this.discard();
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

}
