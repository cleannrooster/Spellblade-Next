package net.spellbladenext.fabric;

import com.google.gson.Gson;
import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.FireworkRocketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.entity.SpellProjectile;
import net.spell_engine.internals.SpellHelper;
import org.checkerframework.checker.units.qual.A;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType;
import software.bernie.geckolib3.core.builder.RawAnimation;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

import java.util.List;
import java.util.OptionalInt;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class ColdAttack extends Reaver implements InventoryCarrier, IAnimatable {
    private  LivingEntity caster = null;
    private SimpleContainer inventory;
    public float range;
    private Spell spell;
    public int life = 20;
    private SpellHelper.ImpactContext context;
    private Entity followedTarget;
    public Vec3 previousVelocity;
    private Spell.ProjectileData clientSyncedData;
    private static  final EntityDataAccessor<Boolean> ATTACKING;
    private static String NBT_SPELL_DATA = "Spell.Data";
    private static String NBT_IMPACT_CONTEXT = "Impact.Context";
    private AnimationFactory factory = GeckoLibUtil.createFactory(this);
    public static final RawAnimation DASH1 = new RawAnimation("animation.hexblade.dash", ILoopType.EDefaultLoopTypes.LOOP);
    public static final RawAnimation ATTACK = new RawAnimation("animation.hexblade.dash2", ILoopType.EDefaultLoopTypes.LOOP);

    private boolean secondphase = false;
    private boolean isAttacking = false;

    public ColdAttack(EntityType<? extends ColdAttack> entityType, Level world) {
        super(entityType, world);
        this.getBrain().removeAllBehaviors();
        this.range = 128.0F;
        this.setNoAi(true);
        this.entityData.set(ATTACKING, false);


    }
    static {
        ATTACKING = SynchedEntityData.defineId(ColdAttack.class, EntityDataSerializers.BOOLEAN);
    }
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ATTACKING, false);
    }
    public ColdAttack(Level world, LivingEntity owner) {
        super(ExampleModFabric.COLDATTACK, world);
        this.getBrain().removeAllBehaviors();
        this.range = 128.0F;
        this.caster = owner;
        this.setNoAi(true);
        this.entityData.set(ATTACKING, false);

    }
    public ColdAttack(Level world, LivingEntity caster, double x, double y, double z, SpellProjectile.Behaviour behaviour, Spell spell, Entity target, SpellHelper.ImpactContext context) {
        this(world, caster);
        this.setPos(x, y, z);
        this.setXRot(caster.getViewXRot(1));

        this.setYRot(caster.getViewYRot(1));
        this.setYBodyRot(caster.getViewYRot(1));
        this.setYHeadRot(caster.getViewYRot(1));

        this.spell = spell;
        Spell.ProjectileData projectileData = this.projectileData();
        Gson gson = new Gson();
        this.context = context;
       // this.setFollowedTarget(target);
    }
    private Spell.ProjectileData projectileData() {
        return this.level.isClientSide ? this.clientSyncedData : this.spell.release.target.projectile;
    }

    @Override
    public boolean hurt(DamageSource damageSource, float f) {
        return false;
    }

    @Override
    public boolean isInvulnerable() {
        return true;
    }

    @Override
    public boolean isAttackable() {
        return false;
    }

    @Override
    public LookControl getLookControl() {
        return null;
    }
    public static void rotateTowardsMovement(Entity entity, float f) {
        Vec3 vec3 = entity.getDeltaMovement();
        if (vec3.lengthSqr() != 0.0D) {
            double d = vec3.horizontalDistance();
            entity.setYRot((float)(Mth.atan2(vec3.z, vec3.x) * 57.2957763671875D) - 90.0F);
            entity.setXRot((float)(Mth.atan2(d, vec3.y) * 57.2957763671875D) - 90.0F);

            while(entity.getXRot() - entity.xRotO < -180.0F) {
                entity.xRotO -= 360.0F;
            }

            while(entity.getXRot() - entity.xRotO >= 180.0F) {
                entity.xRotO += 360.0F;
            }

            while(entity.getYRot() - entity.yRotO < -180.0F) {
                entity.yRotO -= 360.0F;
            }

            while(entity.getYRot() - entity.yRotO >= 180.0F) {
                entity.yRotO += 360.0F;
            }

            entity.setXRot(Mth.lerp(f, entity.xRotO, entity.getXRot()));
            entity.setYRot(Mth.lerp(f, entity.yRotO, entity.getYRot()));
            entity.setYRot(Mth.lerp(f, entity.yRotO, entity.getYRot()));
            entity.setYBodyRot(Mth.lerp(f, -entity.yRotO, entity.getYRot()));
            entity.setYHeadRot(Mth.lerp(f, -entity.yRotO, entity.getYRot()));
        }
    }
    @Override
    public void tick() {
        rotateTowardsMovement(this,1);
        this.noPhysics = false;
        //System.out.println(this.getYRot());
        this.yRotO = this.getYRot();
        this.yBodyRotO = this.getYRot();
        LivingEntity entity = this.getLevel().getNearestEntity(LivingEntity.class,TargetingConditions.forNonCombat(),this,this.getX(),this.getY(),this.getZ(),this.getBoundingBox().expandTowards(this.getDeltaMovement()));
        if((this.tickCount > this.life || (entity != null && entity != this.caster && !(entity instanceof ColdAttack)) || this.getLevel().getBlockStates(this.getBoundingBox().expandTowards(this.getDeltaMovement())).anyMatch((asdf -> asdf.getMaterial().blocksMotion()))) && !this.getLevel().isClientSide() ){
            this.secondphase = true;
            List<ColdAttack> coldlist = this.getLevel().getNearbyEntities(ColdAttack.class,TargetingConditions.forNonCombat(),this,this.getBoundingBox().inflate(4));
            if(coldlist.stream().anyMatch(coldAttack -> coldAttack.caster == this.caster && coldAttack.secondphase && coldAttack.distanceTo(this) < 3) ){
                this.discard();
                return;
            }
            this.entityData.set(ATTACKING, true);
        }
        if(this.secondphase ){

            this.setDeltaMovement(Vec3.ZERO);
            double number2 = 0;
            float f7 = 0;
            float f8 = 0;
            float f9 = 0;
            float f = 0;
            float f1 = -Mth.sin(f7 * ((float) Math.PI / 180F)) * Mth.cos(f * ((float) Math.PI / 180F));
            float f2 = -Mth.sin(f * ((float) Math.PI / 180F));
            float f3 = Mth.cos(f7 * ((float) Math.PI / 180F)) * Mth.cos(f * ((float) Math.PI / 180F));
            int i = this.random.nextInt(50)*20;

            double[] indices = IntStream.rangeClosed(0, (int) ((1000)))
                    .mapToDouble(x -> x).toArray();
            if(i < 0 && !this.getLevel().isClientSide()) {
                this.discard();
                return;
            }
            if(i < 0) {
                return;
            }
                double phi = Math.acos(1 - 2 * indices[i] / 1000);
            double theta = Math.PI * (1 + Math.pow(5, 0.5) * indices[i]);
           /* if(phi == Math.toRadians(180)  && theta == Math.toRadians(180)){
                this.setInvisible(true);
            }*/
            double x = cos(theta) * sin(phi);
            double y = -cos(phi);
            double z = Math.sin(theta) * sin(phi);

            //this.setPos(this.getEyePosition().x + 4 * x + number2 * f1, this.getEyePosition().y + 4 * y + number2 * f2, this.getEyePosition().z + 4 * z + number2 * f3);
            Vec3 center = new Vec3(this.getEyePosition().x + 4 * x + number2 * f1,this.getEyePosition().y + 4 * y + number2 * f2,this.getEyePosition().z + 4 * z + number2 * f3);
            if(spell != null && this.tickCount % 10 == 5) {
                this.getLevel().getEntitiesOfClass(LivingEntity.class, AABB.ofSize(this.getEyePosition(), 8, 8, 8)).forEach(asdf -> SpellHelper.performImpacts(this.level, this.caster, asdf, this.spell, this.context));
            }
            if (level.getServer() != null) {

                for (ServerPlayer player2 : ((ServerLevel) level).getPlayers(serverPlayer -> serverPlayer.hasLineOfSight(this))) {
                    ((ServerLevel) level).sendParticles(ParticleTypes.SWEEP_ATTACK, center.x(), center.y(), center.z(), 1, 0, 0, 0, 0);
                }
            }
            this.getLevel().playSound((Player) null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 0.25F, 1.0F);

        }
        if(this.tickCount > 60 && !this.getLevel().isClientSide()) {
            this.discard();
        }
        this.setPos(this.getDeltaMovement().add(this.position()));

    }

    @Override
    public HumanoidArm getMainArm() {
        return null;
    }

    @Override
    public SimpleContainer getInventory() {
        return this.inventory;
    }


    private <E extends IAnimatable> PlayState predicate2(AnimationEvent<E> event) {
        if(             this.entityData.get(ATTACKING)){
            AnimationBuilder asdf2 = new AnimationBuilder();
            asdf2.getRawAnimationList().add(ATTACK);
            event.getController().setAnimation(asdf2);
            return PlayState.CONTINUE;

        }
        AnimationBuilder asdf2 = new AnimationBuilder();
        asdf2.getRawAnimationList().add(DASH1);
        event.getController().setAnimation(asdf2);
        return PlayState.CONTINUE;
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    @Override
    public boolean canCollideWith(Entity entity) {
        return false;
    }
    public void addAdditionalSaveData(CompoundTag compoundTag) {

        super.addAdditionalSaveData(compoundTag);

        compoundTag.putBoolean("Attacking", (Boolean)this.entityData.get(ATTACKING));
    }


    public void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);

        if (compoundTag.contains("Attacking")) {
            this.entityData.set(ATTACKING, compoundTag.getBoolean("Attackigg"));
        }
    }
    @Override
    public void registerControllers(AnimationData animationData) {
        animationData.addAnimationController(new AnimationController<ColdAttack>(this,"attack1",0,this::predicate2));
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }
}
