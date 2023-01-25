package net.spellbladenext.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.TheEndGatewayBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.spell_engine.api.spell.ParticleBatch;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.client.SpellEngineClient;
import net.spell_engine.entity.SpellProjectile;
import net.spell_engine.internals.SpellHelper;
import net.spell_engine.particle.ParticleHelper;
import net.spell_engine.particle.Particles;
import net.spell_engine.utils.TargetHelper;
import net.spell_power.api.MagicSchool;
import net.spell_power.api.SpellDamageSource;
import net.spell_power.api.SpellPower;
import net.spellbladenext.items.FriendshipBracelet;

import java.util.List;
import java.util.function.Predicate;

public class MagmaOrbEntity extends SpellProjectile implements ItemSupplier {
    public SpellPower.Result power;
    private int count;
    public Spell spell;
    public SpellHelper.ImpactContext context;

    public MagmaOrbEntity(EntityType<? extends MagmaOrbEntity> entityType, Level level) {
        super(entityType, level);
    }
    public MagmaOrbEntity(EntityType<? extends MagmaOrbEntity> entityType, Level level, Player player) {
        super(entityType, level);
        this.setOwner(player);
    }
    int changetime = 0;
    @Override
    public Behaviour behaviour() {
        return Behaviour.FLY;
    }



    @Override
    public void tick() {

        if(firstTick){
            SoundEvent soundEvent = SoundEvents.BLAZE_SHOOT;
            this.playSound(soundEvent, 0.25F, 1F);
        }

        super.baseTick();
        this.xOld = this.getX();
        this.yOld = this.getY();
        this.zOld = this.getZ();
        if(this.getOwner() == null && !this.getLevel().isClientSide){
            this.discard();
        }
        if(this.changetime > 0) {
            this.changetime--;
        }
        HitResult hitresult = ProjectileUtil.getHitResult(this, this::canHitEntity);
        boolean flag = false;
        if (hitresult.getType() == HitResult.Type.BLOCK) {
            BlockPos blockpos = ((BlockHitResult)hitresult).getBlockPos();
            BlockState blockstate = this.level.getBlockState(blockpos);
            if (blockstate.is(Blocks.NETHER_PORTAL)) {
                this.handleInsidePortal(blockpos);
                flag = true;
            } else if (blockstate.is(Blocks.END_GATEWAY)) {
                BlockEntity blockentity = this.level.getBlockEntity(blockpos);
                if (blockentity instanceof TheEndGatewayBlockEntity && TheEndGatewayBlockEntity.canEntityTeleport(this)) {
                    TheEndGatewayBlockEntity.teleportEntity(this.level, blockpos, blockstate, this, (TheEndGatewayBlockEntity)blockentity);
                }

                flag = true;
            }
        }

        if (hitresult.getType() == HitResult.Type.BLOCK && !this.getLevel().isClientSide()  && !flag ) {
            this.onHit(hitresult);
        }

        this.checkInsideBlocks();
        Vec3 vec3 = this.getDeltaMovement();
        double d2 = this.getX() + vec3.x;
        double d0 = this.getY() + vec3.y;
        double d1 = this.getZ() + vec3.z;
        this.updateRotation();
        float f;
        if (this.isInWater()) {
            for(int i = 0; i < 4; ++i) {
                float f1 = 0.25F;
                this.level.addParticle(ParticleTypes.BUBBLE, d2 - vec3.x * 0.25D, d0 - vec3.y * 0.25D, d1 - vec3.z * 0.25D, vec3.x, vec3.y, vec3.z);
            }

            f = 0.8F;
        } else {
            f = 0.99F;
        }
        if(!this.getLevel().isClientSide()) {
            this.setDeltaMovement(vec3.scale((double) f));
            if (!this.isNoGravity()) {
                Vec3 vec31 = this.getDeltaMovement();
                this.setDeltaMovement(vec31.x, vec31.y - (double) this.getGravity(), vec31.z);
            }
        }

        this.setPos(d2, d0, d1);

    }

    @Override
    protected void onHit(HitResult hitResult) {
        if(hitResult instanceof BlockHitResult result && !this.getLevel().isClientSide()) {
            final int NUM_POINTS = 96;
            final double RADIUS = 4d;
            if (!this.level.isClientSide()) {
                if ((result.getDirection() == Direction.NORTH) || result.getDirection() == Direction.SOUTH) {
                    this.setDeltaMovement(1 * this.getDeltaMovement().x, 1 * this.getDeltaMovement().y, -1 * this.getDeltaMovement().z);
				/*for (int i = 0; i < NUM_POINTS; ++i)
				{
				    final double angle = Math.toRadians(((double) i / NUM_POINTS) * 360d);

				        double x = Math.cos(angle) * RADIUS;
				        double y = Math.sin(angle) * RADIUS;

					ThrownItems thrown = new ThrownItems(world, this.thrower);
					thrown.setNoGravity(true);
					Vec3d vec3 = new Vec3d(x,0,y);
					thrown.shoot(vec3.x, vec3.y, vec3.z, 1, 0);

				}
				if(count >= 8) {
					this.setDead();
				}
				this.count++;*/
                }
                if ((result.getDirection() == Direction.EAST || result.getDirection() == Direction.WEST)) {
                    this.setDeltaMovement(-1 * this.getDeltaMovement().x, 1 * this.getDeltaMovement().y, 1 * this.getDeltaMovement().z);
				/*for (int i = 0; i < NUM_POINTS; ++i)
				{
				    final double angle = Math.toRadians(((double) i / NUM_POINTS) * 360d);

				        double x = Math.cos(angle) * RADIUS;
				        double y = Math.sin(angle) * RADIUS;

					ThrownItems thrown = new ThrownItems(world, this.thrower);
					thrown.setNoGravity(true);
					Vec3d vec3 = new Vec3d(x,0,y);
					thrown.shoot(vec3.x, vec3.y, vec3.z, 1, 0);

				}
				if(count >= 8) {
					this.setDead();
				}
				this.count++;*/

                }

                if (result.getDirection() == Direction.UP || result.getDirection() == Direction.DOWN ) {
                    this.setDeltaMovement(1 * this.getDeltaMovement().x, -1 * this.getDeltaMovement().y, 1 * this.getDeltaMovement().z);
                    if (result.getDirection() == Direction.UP && this.getOwner() instanceof Player owner) {
                        if (this.getDeltaMovement().y < 0.2) {
                            this.setDeltaMovement(this.getDeltaMovement().x, 0.2, this.getDeltaMovement().z);
                        }
                        for (int i = 0; i < NUM_POINTS; ++i) {
                            final double angle = Math.toRadians(((double) i / NUM_POINTS) * 360d);

                            double x = Math.cos(angle) * RADIUS;
                            double y = Math.sin(angle) * RADIUS;

                            if(!this.getLevel().isClientSide())
                            ((ServerLevel) this.level).sendParticles(ParticleTypes.FLAME, this.getX(), this.getY(0.5), this.getZ(), 1, x, 0.0D, y, 0.2D);

                        }
                        Predicate<Entity> selectionPredicate = (target) -> {
                            return !SpellEngineClient.config.filterInvalidTargets || (TargetHelper.actionAllowed(TargetHelper.TargetingMode.AREA, TargetHelper.Intent.HARMFUL, owner, target)
                                    && FriendshipBracelet.PlayerFriendshipPredicate(owner,target));
                        };
                        Spell.Release.Target.Area area = new Spell.Release.Target.Area();
                        area.angle_degrees = 360;
                        List<Entity> list = TargetHelper.targetsFromArea(this, result.getLocation().add(0,(double)this.getBbHeight()*0.5,0), 4, area, selectionPredicate);
                        for(Entity living : list){
                            if(this.power != null && this.context != null && this.spell != null ) {
                                SpellHelper.performImpacts(this.getLevel(),owner,living,this.spell,this.context);
                            }
                        }
                        SoundEvent soundEvent = SoundEvents.BLAZE_SHOOT;
                        this.playSound(soundEvent, 1F, 1F);
                        if (count >= 4) {
                            this.discard();
                        }
                        this.count++;

                    }
                }
            }
            /*if (this.getOwner() != null) {

                SoundEvent soundEvent = SoundEvents.BLAZE_SHOOT;
                this.playSound(soundEvent, 0.25F, 1F);
                if(!this.level.isClientSide()){
                    this.discard();
                }
            }*/
        }
    }
    @Override
    public boolean isAttackable() {
        return false;
    }
    protected float getGravity() {
        return 0.06F;
    }

    @Override
    public ItemStack getItem() {
        return Items.FIRE_CHARGE.getDefaultInstance();
    }
}
