package net.spellbladenext.entities;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.spell_engine.SpellEngineMod;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.client.render.FlyingSpellEntity;
import net.spell_engine.entity.SpellProjectile;
import net.spell_engine.internals.SpellHelper;
import net.spell_engine.utils.RecordsWithGson;
import net.spell_engine.utils.VectorHelper;
import net.spellbladenext.SpellbladeNext;

import java.util.ArrayList;
import java.util.List;

public class Eruption extends SpellProjectile implements FlyingSpellEntity, ItemSupplier {
    public float range;
    private Spell spell;
    private SpellHelper.ImpactContext context;
    private Entity followedTarget;
    public Vec3 previousVelocity;
    public int vertical = 0;
    public int horizontal = 0;
    private Spell.ProjectileData clientSyncedData;
    private static String NBT_SPELL_DATA = "Spell.Data";
    private static String NBT_IMPACT_CONTEXT = "Impact.Context";
    private static final EntityDataAccessor<String> BEHAVIOUR;
    private static final EntityDataAccessor<String> CLIENT_DATA;
    private static final EntityDataAccessor<Integer> TARGET_ID;
    private boolean inGround = false;

    public Eruption(EntityType<? extends Eruption> entityType, Level world) {
        super(entityType, world);
    }

    protected Eruption(Level world, LivingEntity owner) {
        super(SpellEngineMod.SPELL_PROJECTILE, world);
        this.range = 128.0F;
        this.setOwner(owner);
    }

    public Eruption(Level world, LivingEntity caster, double x, double y, double z, SpellProjectile.Behaviour behaviour, Spell spell, Entity target, SpellHelper.ImpactContext context) {
        this(world, caster);
        this.setPos(x, y, z);
        this.spell = spell;
        Spell.ProjectileData projectileData = this.projectileData();
        Gson gson = new Gson();
        this.context = context;
        this.getEntityData().set(CLIENT_DATA, gson.toJson(projectileData));
        this.getEntityData().set(BEHAVIOUR, behaviour.toString());
        this.setFollowedTarget(target);
    }

    List<Vec3> vec3s = new ArrayList<>();
    public ItemStack getItem(){
        return Items.AIR.getDefaultInstance();
    }
    @Override
    public void tick() {
        this.baseTick();
        this.setNoGravity(true);
        if(this.tickCount % 3 == 1 && this.getImpactContext() != null && this.getSpell() != null && this.getOwner() != null) {
            this.vec3s.add(this.position());
            ProjectileUtil.rotateTowardsMovement(this, 1F);
            for (Vec3 vec3 : this.vec3s) {
                if (!this.getLevel().isClientSide()) {
                    for (int i = 0; i < 3; i++) {
                        CleansingFlameEntity cleansingFlameEntity = new CleansingFlameEntity(SpellbladeNext.CLEANSINGFLAME, this.getLevel());
                        cleansingFlameEntity.target = this.getFollowedTarget();
                        cleansingFlameEntity.context = this.getImpactContext();
                        cleansingFlameEntity.power = this.getImpactContext().power();
                        cleansingFlameEntity.spell = this.getSpell();
                        cleansingFlameEntity.setOwner(this.getOwner());
                        cleansingFlameEntity.setPos(vec3.add(-4 + 8 * this.random.nextDouble(), -4 + 8 * this.random.nextDouble(), -4 + 8 * this.random.nextDouble()));
                        int asdf = this.random.nextInt(2);
                        int asdf2 = -1;
                        if (asdf == 0) {
                            asdf2 = 1;
                        }
                        cleansingFlameEntity.life = 20;
                        cleansingFlameEntity.setXRot(this.getXRot());
                        cleansingFlameEntity.setYRot(this.getYRot());
                        if(vertical == 1)
                        cleansingFlameEntity.shootFromRotation(this, -cleansingFlameEntity.getXRot() + 90 * vertical * asdf2, cleansingFlameEntity.getYRot() + 90 * horizontal * asdf2, 0, 1, 0);
                        if(horizontal == 1)
                            cleansingFlameEntity.shootFromRotation(this, 0, cleansingFlameEntity.getYRot() + 90 * horizontal * asdf2, 0, 1, 0);

                        cleansingFlameEntity.setDeltaMovement(cleansingFlameEntity.getDeltaMovement().subtract(this.getDeltaMovement()));

                        //cleansingFlameEntity.setDeltaMovement(cleansingFlameEntity.getDeltaMovement().add(this.getDeltaMovement()));

                        this.getLevel().addFreshEntity(cleansingFlameEntity);

                    }
                }
            }
        }
        if(this.tickCount < 40 && !this.inGround) {
            this.setPos(this.position().add(this.getDeltaMovement()));
        }
        if(this.tickCount > 40 && !this.vec3s.isEmpty()){
            this.vec3s.remove(0);
        }
        if(this.tickCount > 40 && !this.getLevel().isClientSide() && this.vec3s.isEmpty())
            this.discard();
        if(this.tickCount > 60 && !this.getLevel().isClientSide() )
            this.discard();

    }
    public static void rotateTowards(Entity entity, Vec3 vec3, float f) {
        if (vec3.lengthSqr() != 0.0D) {
            double d = vec3.horizontalDistance();
            entity.setYRot((float)(Mth.atan2(vec3.z, vec3.x) * 57.2957763671875D) + 90.0F);
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
        }
    }

    public static Vec3 rotate(double x, double y, double z, double pitch, double roll, double yaw) {
        double cosa = Math.cos(yaw);
        double sina = Math.sin(yaw);

        double cosb = Math.cos(pitch);
        double sinb = Math.sin(pitch);
        double cosc = Math.cos(roll);
        double sinc = Math.sin(roll);

        double Axx = cosa * cosb;
        double Axy = cosa * sinb * sinc - sina * cosc;
        double Axz = cosa * sinb * cosc + sina * sinc;

        double Ayx = sina * cosb;
        double Ayy = sina * sinb * sinc + cosa * cosc;
        double Ayz = sina * sinb * cosc - cosa * sinc;

        double Azx = -sinb;
        double Azy = cosb * sinc;
        double Azz = cosb * cosc;

        Vec3 vec3 = new Vec3(Axx * x + Axy * y + Axz * z,Ayx * x + Ayy * y + Ayz * z,Azx * x + Azy * y + Azz * z);
        return vec3;
    }
    private void finishFalling() {
        Entity owner = this.getOwner();
        if (owner != null && !owner.isRemoved()) {
            if (owner instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity)owner;
                SpellHelper.fallImpact(livingEntity, this, this.spell, this.position(), this.context);
            }

        }
    }

    private void followTarget() {
        Entity target = this.getFollowedTarget();
        if (target != null && this.projectileData().homing_angle > 0.0F) {
            Vec3 distanceVector = target.position().add(0.0D, (double)(target.getBbHeight() / 2.0F), 0.0D).subtract(this.position().add(0.0D, (double)(this.getBbHeight() / 2.0F), 0.0D));
            Vec3 newVelocity = VectorHelper.rotateTowards(this.getDeltaMovement(), distanceVector, (double)this.projectileData().homing_angle);
            if (newVelocity.lengthSqr() > 0.0D) {
                this.setDeltaMovement(newVelocity);
                this.hasImpulse = true;
            }
        }

    }

    protected float getDrag() {
        return 1F;
    }

    protected void onHitEntity(EntityHitResult entityHitResult) {
    }

    public Spell getSpell() {
        return this.spell;
    }

    public SpellHelper.ImpactContext getImpactContext() {
        return this.context;
    }

    public Spell.ProjectileData.Client renderData() {
        Spell.ProjectileData data = this.projectileData();
        return data != null ? this.projectileData().client_data : null;
    }


    protected void onHitBlock(BlockHitResult blockHitResult) {
        this.inGround = true;
        super.onHitBlock(blockHitResult);

    }

    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        Gson gson = new Gson();
        nbt.putString(NBT_SPELL_DATA, gson.toJson(this.spell));
        nbt.putString(NBT_IMPACT_CONTEXT, gson.toJson(this.context));
    }

    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        if (nbt.contains(NBT_SPELL_DATA, 8)) {
            try {
                Gson gson = new Gson();
                this.spell = (Spell)gson.fromJson(nbt.getString(NBT_SPELL_DATA), Spell.class);
                Gson recordReader = (new GsonBuilder()).registerTypeAdapterFactory(new RecordsWithGson.RecordTypeAdapterFactory()).create();
                this.context = (SpellHelper.ImpactContext)recordReader.fromJson(nbt.getString(NBT_IMPACT_CONTEXT), SpellHelper.ImpactContext.class);
            } catch (Exception var4) {
                System.err.println("SpellProjectile - Failed to read spell data from NBT");
            }
        }

    }

    protected void defineSynchedData() {
        new Gson();
        this.getEntityData().define(CLIENT_DATA, "");
        this.getEntityData().define(TARGET_ID, 0);
        this.getEntityData().define(BEHAVIOUR, SpellProjectile.Behaviour.FLY.toString());
    }

    static {
        CLIENT_DATA = SynchedEntityData.defineId(SpellProjectile.class, EntityDataSerializers.STRING);
        TARGET_ID = SynchedEntityData.defineId(SpellProjectile.class, EntityDataSerializers.INT);
        BEHAVIOUR = SynchedEntityData.defineId(SpellProjectile.class, EntityDataSerializers.STRING);
    }

    public static enum Behaviour {
        FLY,
        FALL;

        private Behaviour() {
        }
    }
    private Spell.ProjectileData projectileData() {
        return this.level.isClientSide ? this.clientSyncedData : this.spell.release.target.projectile;
    }

    private void updateClientSideData() {
        if (this.clientSyncedData == null) {
            try {
                Gson gson = new Gson();
                String json = (String)this.getEntityData().get(CLIENT_DATA);
                Spell.ProjectileData data = (Spell.ProjectileData)gson.fromJson(json, Spell.ProjectileData.class);
                this.clientSyncedData = data;
            } catch (Exception var4) {
                System.err.println("Spell Projectile - Failed to read clientSyncedData");
            }

        }
    }

    private void setFollowedTarget(Entity target) {
        this.followedTarget = target;
        int id = 0;
        if (!this.level.isClientSide) {
            if (target != null) {
                id = target.getId();
            }

            this.getEntityData().set(TARGET_ID, id);
        }

    }

    public Entity getFollowedTarget() {
        Entity entityReference = null;
        if (this.level.isClientSide) {
            Integer id = (Integer)this.getEntityData().get(TARGET_ID);
            if (id != null && id != 0) {
                entityReference = this.level.getEntity(id);
            }
        } else {
            entityReference = this.followedTarget;
        }

        return entityReference != null && entityReference.isAttackable() && entityReference.isAlive() ? entityReference : entityReference;
    }

    public boolean shouldRenderAtSqrDistance(double distance) {
        double d0 = this.getBoundingBox().getSize() * 4.0D;
        if (Double.isNaN(d0)) {
            d0 = 4.0D;
        }

        d0 *= 128.0D;
        boolean result = distance < d0 * d0;
        return result;
    }

    public SpellProjectile.Behaviour behaviour() {
        String string = (String)this.getEntityData().get(BEHAVIOUR);
        return string != null && !string.isEmpty() ? SpellProjectile.Behaviour.valueOf(string) : SpellProjectile.Behaviour.FLY;
    }
}
