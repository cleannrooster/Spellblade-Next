package net.spellbladenext.entities;

import io.netty.buffer.Unpooled;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.entity.SpellProjectile;
import net.spell_engine.internals.SpellHelper;
import net.spell_engine.utils.TargetHelper;
import net.spell_power.api.SpellPower;
import net.spellbladenext.SpellbladeNext;
import net.spellbladenext.items.FriendshipBracelet;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class IcicleBarrierEntity extends SpellProjectile implements ItemSupplier {
    public LivingEntity target;
    public boolean inGround = false;
    public double damage = 1;
    public SpellPower.Result power;
    private int mode;
    public Spell spell;
    public SpellHelper.ImpactContext context;

    public IcicleBarrierEntity(EntityType<? extends IcicleBarrierEntity> p_36721_, Level p_36722_, Player player) {
        super(p_36721_, p_36722_);
        this.setOwner(player);
        Vec3 vec3 = player.getViewVector(0);
        this.setNoGravity(true);
        double d0 = vec3.horizontalDistance();
        this.setYRot(((float) (Mth.atan2(player.getViewVector(0).x, player.getViewVector(0).z) * (double) (180F / (float) Math.PI)))-90);
        this.setXRot((float) (Mth.atan2(player.getViewVector(0).y, d0) * (double) (180F / (float) Math.PI))-45);
        this.yRotO = ((float) (Mth.atan2(player.getViewVector(0).x, player.getViewVector(0).z) * (double) (180F / (float) Math.PI)));
        this.xRotO = (float) (Mth.atan2(player.getViewVector(0).y, d0) * (double) (180F / (float) Math.PI));

    }
    @Override
    public Behaviour behaviour() {
        return Behaviour.FLY;
    }

    public IcicleBarrierEntity(EntityType<? extends IcicleBarrierEntity> p_36721_, Level p_36722_) {
        super(p_36721_, p_36722_);
        this.setNoGravity(true);
        if(this.getOwner() != null && this.getOwner() instanceof Player player) {

            Vec3 vec3 = player.getViewVector(0);
            this.setNoGravity(true);

            double d0 = vec3.horizontalDistance();
            this.setYRot(((float) (Mth.atan2(player.getViewVector(0).x, player.getViewVector(0).z) * (double) (180F / (float) Math.PI))) - 90);
            this.setXRot((float) (Mth.atan2(player.getViewVector(0).y, d0) * (double) (180F / (float) Math.PI)) - 45);
            this.yRotO = ((float) (Mth.atan2(player.getViewVector(0).x, player.getViewVector(0).z) * (double) (180F / (float) Math.PI)));
            this.xRotO = (float) (Mth.atan2(player.getViewVector(0).y, d0) * (double) (180F / (float) Math.PI));
        }

    }

    @Override
    public void tick() {

        this.xOld = this.getX();
        this.yOld = this.getY();
        this.zOld = this.getZ();
        //ParticleHelper.play(this.getLevel(),this,this.getXRot(),this.getYRot(), new ParticleBatch("spell_engine:snowflake", ParticleBatch.Shape.CIRCLE, ParticleBatch.Origin.CENTER, ParticleBatch.Rotation.LOOK,3,0,0,0));

        if(this.tickCount > 50 && !this.getLevel().isClientSide()){
            this.discard();
        }
        if(this.getOwner() == null){
            this.discard();
            return;
        }
            double number2 = 0;
            float f7 = this.getOwner().getYRot()% 360;
            float f8 = this.getOwner().getYRot() + 60;
            float f9 = this.getOwner().getYRot() - 60;
            float f = this.getOwner().getXRot();
            float f1 = -Mth.sin(f7 * ((float) Math.PI / 180F)) * Mth.cos(f * ((float) Math.PI / 180F));
            float f2 = -Mth.sin(f * ((float) Math.PI / 180F));
            float f3 = Mth.cos(f7 * ((float) Math.PI / 180F)) * Mth.cos(f * ((float) Math.PI / 180F));
            int i = 1000-this.tickCount*20;

            double[] indices = IntStream.rangeClosed(0, (int) ((1000)))
                    .mapToDouble(x -> x).toArray();
            if(i < 0) {
                return;
            }
            double phi = Math.acos(1 - 2 * indices[i] / 1000);
            double theta = Math.PI * (1 + Math.pow(5, 0.5) * indices[i]);
            if(phi == Math.toRadians(180)  && theta == Math.toRadians(180)){
                this.setInvisible(true);
            }
            double x = cos(theta) * sin(phi);
            double y = -cos(phi);
            double z = Math.sin(theta) * sin(phi);
            Vec3 vec3d = rotate(x,y,z,-Math.toRadians(f7),Math.toRadians(f+90),0);

            this.setPos(this.getOwner().getEyePosition().x + 4 * vec3d.x + number2 * f1, this.getOwner().getEyePosition().y + 4 * vec3d.y + number2 * f2, this.getOwner().getEyePosition().z + 4 * vec3d.z + number2 * f3);
            Vec3 vec = new Vec3(4 * vec3d.x + number2 * f1,4 * vec3d.y + number2 * f2,4 * vec3d.z + number2 * f3);
            double d0 = vec.horizontalDistance();
            this.setYRot((float)(Mth.atan2(vec.x, vec.z) * (double)(180F / (float)Math.PI))-90);
            this.setXRot((float)(Mth.atan2(vec.y, d0) * (double)(180F / (float)Math.PI))+45);
            this.yRotO = this.getYRot();
            this.xRotO = this.getXRot();



            //this.setXRot((float) (f+phi*180/Math.PI));
            //this.setYRot((float) (f7+theta*180/Math.PI));
                        /*if(this.tickCount < 0){
                            this.setYRot((float)(Mth.atan2(vec3d.x, vec3d.z) * (double)(180F / (float)Math.PI))+45);
                            this.setXRot((float)(Mth.atan2(vec3d.y, d0) * (double)(180F / (float)Math.PI))+45);
                        }*/

        if (this.getOwner() instanceof Player player) {
            Predicate<Entity> selectionPredicate = (target) -> {
                return (TargetHelper.actionAllowed(TargetHelper.TargetingMode.AREA, TargetHelper.Intent.HARMFUL, player, target)
                        && FriendshipBracelet.PlayerFriendshipPredicate(player,target));
            };
            Spell.Release.Target.Area area = new Spell.Release.Target.Area();
            area.angle_degrees = 360;
            List<Entity> entities = this.getLevel().getEntitiesOfClass(Entity.class,this.getBoundingBox().inflate(1),selectionPredicate);
            entities.removeIf(entity -> entity == this);
            if (!entities.isEmpty()) {
                if (this.power != null && this.spell != null && this.context != null) {
                    for (Entity target : entities) {
                        if (target != null && this.getOwner() instanceof LivingEntity living && target != this.getOwner()) {
                            if (target.invulnerableTime <= 10) {
                                SpellHelper.performImpacts(this.getLevel(), living, target, this.spell, this.context);
                                target.invulnerableTime = 20;

                            }
                            if (level.getServer() != null) {
                                int intarray[];
                                intarray = new int[3];
                                intarray[0] = (int) Math.round(target.getBoundingBox().getCenter().x);
                                intarray[1] = (int) Math.round(target.getBoundingBox().getCenter().y);
                                intarray[2] = (int) Math.round(target.getBoundingBox().getCenter().z);
                                Stream<ServerPlayer> serverplayers = level.getServer().getPlayerList().getPlayers().stream();

                                for (ServerPlayer player2 : ((ServerLevel) level).getPlayers(serverPlayer -> serverPlayer.hasLineOfSight(this.getOwner()))) {
                                    FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer()).writeVarIntArray(intarray);
                                    Random rand = new Random();
                                    Vec3 vec3 = target.getBoundingBox().getCenter().add(new Vec3(rand.nextDouble(-1, 1), rand.nextDouble(-1, 1), rand.nextDouble(-1, 1)));
                                    ((ServerLevel) level).sendParticles(ParticleTypes.SWEEP_ATTACK, vec3.x(), vec3.y(), vec3.z(), 1, 0, 0, 0, 0);
                                }
                            }
                            //this.getLevel().playSound((Player) null, target.getX(), target.getY(), target.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, this.getOwner().getSoundSource(), 0.25F, 1.0F);
                            //this.getOwner().playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 0.005F, 1.0F);


                            Random rand = new Random();
                            Vec3 vec3 = target.getBoundingBox().getCenter().add(new Vec3(rand.nextDouble(-2, 2), rand.nextDouble(-2, 2), rand.nextDouble(-2, 2)));
                            Vec3 vec31 = target.getBoundingBox().getCenter().subtract(vec3).normalize();
                        }


                    }

                }

            }
        }

    }


    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        return;
    }

    @Override
    protected void onHitBlock(BlockHitResult blockHitResult) {

    }
    @Override
    public boolean isAttackable() {
        return false;
    }
    @Override
    public ItemStack getItem() {
        return SpellbladeNext.dummyfrost3.get().getDefaultInstance();
    }

    public Vec3 rotate(double x, double y, double z, double pitch, double roll, double yaw) {
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

}
