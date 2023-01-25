package net.spellbladenext.entities;

import net.minecraft.core.Registry;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.spell_engine.api.spell.Sound;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.client.SpellEngineClient;
import net.spell_engine.entity.SpellProjectile;
import net.spell_engine.internals.SpellHelper;
import net.spell_engine.utils.SoundHelper;
import net.spell_engine.utils.TargetHelper;
import net.spell_power.api.MagicSchool;
import net.spell_power.api.SpellDamageSource;
import net.spell_power.api.SpellPower;
import net.spellbladenext.items.FriendshipBracelet;
import net.spellbladenext.items.spellblades.Spellblades;

import java.util.List;
import java.util.function.Predicate;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class IceThorn extends SpellProjectile implements ItemSupplier {
    public SpellPower.Result power;
    public int number;
    private Entity target;
    public Spell spell;
    public SpellHelper.ImpactContext context;

    public IceThorn(EntityType<? extends IceThorn> p_36721_, Level p_36722_, Player player) {
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
    @Override
    public boolean isAttackable() {
        return false;
    }
    public IceThorn(EntityType<? extends IceThorn> p_36721_, Level p_36722_) {
        super(p_36721_, p_36722_);
        this.setNoGravity(true);

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

    @Override
    public Behaviour behaviour() {
        return Behaviour.FLY;
    }
    @Override
    public void tick() {
        if(this.tickCount < 60 && this.target != null && this.getOwner() instanceof Player living && this.spell != null && this.context != null && this.target.invulnerableTime <= 10){
            Vec3 vec3 = this.target.getEyePosition().subtract(this.position());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.95D).add(vec3.normalize().scale(0.05)));
        }
        if(this.tickCount > 40){
            super.tick();
            Vec3 vec3 = this.getDeltaMovement();
            double d0 = vec3.horizontalDistance();
            this.setYRot((float) (Mth.atan2(vec3.x, vec3.z) * (double) (180F / (float) Math.PI))-90);
            this.setXRot((float) (Mth.atan2(vec3.y, d0) * (double) (180F / (float) Math.PI))+45);
            this.yRotO = this.getYRot();
            this.xRotO = this.getXRot();
        }
        if(this.tickCount < 40 && this.getOwner() != null &&  this.getOwner().isAlive()) {
            double tridentEntity = this.getOwner().getYHeadRot() ;
            double f = 0;
            float g = (float) (-sin(tridentEntity * ((float) Math.PI / 180)) * cos(f * ((float) Math.PI / 180)));
            float h = (float) -sin(f * ((float) Math.PI / 180));
            float r = (float) (cos(tridentEntity * ((float) Math.PI / 180)) * cos(f * ((float) Math.PI / 180)));
            double x = (2D +  (getOwner().getBoundingBox().getXsize())) * 0.5  * cos(((((double) (this.tickCount % 40) / 40D)) * (2D * (double) Math.PI) + (2 * Math.PI * (double) (number % 4) / 4D)));
            double z = (2D + getOwner().getBoundingBox().getXsize()) * 0.5 * sin(((((double) (this.tickCount % 40) / 40D)) * (2 * Math.PI) + (2 * Math.PI * (double) (number % 4) / 4D)));
            Vec3 vec32 = rotate(x,0D,z, -Math.toRadians(tridentEntity+90),0D,0D);

            this.xOld = this.getX();
            this.yOld = this.getY();
            this.zOld = this.getZ();

            this.setPos(this.getOwner().getX() + vec32.x() , (double) this.getOwner().getBoundingBox().getCenter().y(), this.getOwner().getZ()  + vec32.z() );

            Vec3 vec3 = this.position().subtract(this.getOwner().position().add(0,this.getOwner().getBoundingBox().getYsize()/2,0));
            double d0 = vec3.horizontalDistance();

            this.setYRot((float) (Mth.atan2(vec3.x, vec3.z) * (double) (180F / (float) Math.PI))-90);
            this.setXRot((float) (Mth.atan2(vec3.y, d0) * (double) (180F / (float) Math.PI))+45);
            this.yRotO = this.getYRot();
            this.xRotO = this.getXRot();

        }
        if(this.tickCount == 40 && this.getOwner() instanceof Player living) {
            Predicate<Entity> selectionPredicate = (target) -> {
                return !SpellEngineClient.config.filterInvalidTargets || (TargetHelper.actionAllowed(TargetHelper.TargetingMode.AREA, TargetHelper.Intent.HARMFUL, living, target)
                        && FriendshipBracelet.PlayerFriendshipPredicate(living,target));
            };
            Spell.Release.Target.Area area = new Spell.Release.Target.Area();
            area.angle_degrees = 100;
            List<Entity> list = TargetHelper.targetsFromArea(living, living.getEyePosition(),16F,area,   selectionPredicate);
            if(!list.isEmpty()){
                this.target = list.get(this.random.nextInt(list.toArray().length));
            }
            /*AABB aabb = this.getBoundingBox().inflate(8,8,8).move(8*this.getOwner().getViewVector(1).x(),8*this.getOwner().getViewVector(1).y(),8*this.getOwner().getViewVector(1).z());
            List<LivingEntity> list = this.getLevel().getEntitiesOfClass(LivingEntity.class,aabb, asdf -> FriendshipBracelet.PlayerFriendshipPredicate(living,asdf) && TargetHelper.actionAllowed(TargetHelper.TargetingMode.DIRECT, TargetHelper.Intent.HARMFUL,living,asdf) &&  asdf.distanceToSqr(aabb.getCenter()) < 8*8);
            */
            if(!list.isEmpty()) {
                if(Registry.SOUND_EVENT.get(new ResourceLocation("spell_engine", "generic_frost_release")) != null) {
                    this.getLevel().playSound((Player) null, this.getX(), this.getY(), this.getZ(), Registry.SOUND_EVENT.get(new ResourceLocation("spell_engine", "generic_frost_release")), this.getOwner().getSoundSource(), 1F, 1.0F);
                }
            }
            if(this.target != null){
                this.setDeltaMovement(0,1,0);
            }
        }

        if(this.tickCount > 60 && this.target != null){
            if(this.getBoundingBox().expandTowards(this.getDeltaMovement()).intersects(this.target.getBoundingBox()) && this.power != null){
                if(this.getLevel() instanceof ServerLevel level && this.getOwner() instanceof Player living && this.spell != null && this.context != null){
                    for (int i = 0; i < 50; ++i) {
                        final double angle = Math.toRadians(((double) i / 50) * 360d);

                        double x = Math.cos(angle) * 2;
                        double y = Math.sin(angle) * 2;

                        if(!this.getLevel().isClientSide())
                            ((ServerLevel) this.level).sendParticles(ParticleTypes.SNOWFLAKE, target.getX(), target.getY(0.5D), target.getZ(), 1, x, 0.0D, y, 0.2D);

                    }
                    Predicate<Entity> selectionPredicate = (target) -> {
                        return !SpellEngineClient.config.filterInvalidTargets || (TargetHelper.actionAllowed(TargetHelper.TargetingMode.DIRECT, TargetHelper.Intent.HARMFUL, living, target)
                                && FriendshipBracelet.PlayerFriendshipPredicate(living,target));
                    };
                    Spell.Release.Target.Area area = new Spell.Release.Target.Area();
                    area.angle_degrees = 360;
                    List<Entity> list = TargetHelper.targetsFromArea(this, this.target.getEyePosition(),2F, area,  selectionPredicate);
                    list.add(this.target);
                    for (Entity entity: list
                    ) {
                        if(entity.invulnerableTime <= 10) {
                            SpellHelper.performImpacts(this.getLevel(), living, entity, this.spell, this.context);
                            entity.invulnerableTime = 20;

                        }
                    }
                    this.discard();

                }
                this.discard();
            }
            Vec3 vec3 = this.target.getEyePosition().subtract(this.position());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.95D).add(vec3.normalize().scale(0.5)));

        }

        if(this.tickCount > 40 && (this.target == null) &&  !this.getLevel().isClientSide()){
            this.discard();

        }
        if(this.tickCount > 200 && !this.getLevel().isClientSide())
        {
            this.discard();
        }
        if(this.getDeltaMovement().length() > 1){
            this.setDeltaMovement(this.getDeltaMovement().normalize());
        }
    }



    @Override
    protected void onHitBlock(BlockHitResult blockHitResult) {
        //this.getLevel().playSound((Player) null, blockHitResult.getBlockPos().getX(), blockHitResult.getBlockPos().getY(), blockHitResult.getBlockPos().getZ(), SoundEvents.SNOW_BREAK, this.getOwner().getSoundSource(), 1F, 1.0F);
        if(this.getLevel() instanceof ServerLevel level && this.getOwner() instanceof Player living && this.power != null && this.spell != null && this.context != null){
            for (int i = 0; i < 50; ++i) {
                final double angle = Math.toRadians(((double) i / 50) * 360d);

                double x = Math.cos(angle) * 2;
                double y = Math.sin(angle) * 2;

                if(!this.getLevel().isClientSide())
                    ((ServerLevel) this.level).sendParticles(ParticleTypes.SNOWFLAKE, blockHitResult.getBlockPos().getX(), blockHitResult.getBlockPos().getY(), blockHitResult.getBlockPos().getZ(), 1, x, 0.0D, y, 0.2D);

            }
            Predicate<Entity> selectionPredicate = (target) -> {
                return !SpellEngineClient.config.filterInvalidTargets || (TargetHelper.actionAllowed(TargetHelper.TargetingMode.DIRECT, TargetHelper.Intent.HARMFUL, living, target)
                        && FriendshipBracelet.PlayerFriendshipPredicate(living,target));
            };
            Spell.Release.Target.Area area = new Spell.Release.Target.Area();
            area.angle_degrees = 360;
            List<Entity> list = TargetHelper.targetsFromArea(this, Vec3.atCenterOf(blockHitResult.getBlockPos().above()),2F, area,  selectionPredicate);

            for (Entity entity: list
                 ) {
                if(entity.invulnerableTime <= 10 && SpellHelper.performImpacts(this.getLevel(), living, entity, this.spell, this.context)){

                    entity.invulnerableTime = 20;

                }
            }
            if(Registry.SOUND_EVENT.get(new ResourceLocation("spell_engine", "generic_frost_impact")) != null) {
                this.getLevel().playSound((Player) null, blockHitResult.getBlockPos().getX(), blockHitResult.getBlockPos().above().getY(), blockHitResult.getBlockPos().getZ(), Registry.SOUND_EVENT.get(new ResourceLocation("spell_engine", "generic_frost_impact")), this.getOwner().getSoundSource(), 1F, 1.0F);
            }
            this.discard();

        }

    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        if(this.getOwner() instanceof Player living && this.spell != null && this.context != null){
            for (int i = 0; i < 50; ++i) {
                final double angle = Math.toRadians(((double) i / 50) * 360d);

                double x = Math.cos(angle) * 2;
                double y = Math.sin(angle) * 2;

                if(this.getLevel() instanceof ServerLevel level)
                    level.sendParticles(ParticleTypes.SNOWFLAKE, target.getX(), target.getY(0.5D), target.getZ(), 1, x, 0.0D, y, 0.2D);

            }
            Predicate<Entity> selectionPredicate = (target) -> {
                return !SpellEngineClient.config.filterInvalidTargets || (TargetHelper.actionAllowed(TargetHelper.TargetingMode.DIRECT, TargetHelper.Intent.HARMFUL, living, target)
                        && FriendshipBracelet.PlayerFriendshipPredicate(living,target));
            };
            Spell.Release.Target.Area area = new Spell.Release.Target.Area();
            area.angle_degrees = 360;
            List<Entity> list = TargetHelper.targetsFromArea(this, entityHitResult.getEntity().getEyePosition(),2F, area,  selectionPredicate);
            //List<Entity> list = this.getLevel().getEntitiesOfClass(Entity.class, AABB.ofSize(this.target.position(), 2, 2, 2),selectionPredicate);
            list.add(entityHitResult.getEntity());

            for (Entity entity: list
            ) {
                if(entity.invulnerableTime <= 10 && SpellHelper.performImpacts(this.getLevel(), living, entity, this.spell, this.context)) {

                    entity.invulnerableTime = 20;

                }
            }
            if(!this.level.isClientSide()) {
                this.discard();
            }

        }
    }

    @Override
    public ItemStack getItem() {
        return Items.SNOWBALL.getDefaultInstance();
    }
}
