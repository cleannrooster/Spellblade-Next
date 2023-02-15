package net.spellbladenext.entities;

import com.google.common.collect.ImmutableMultimap;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.spell_engine.api.spell.ParticleBatch;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.client.SpellEngineClient;
import net.spell_engine.entity.SpellProjectile;
import net.spell_engine.internals.SpellHelper;
import net.spell_engine.particle.ParticleHelper;
import net.spell_engine.utils.TargetHelper;
import net.spell_power.api.MagicSchool;
import net.spell_power.api.SpellDamageSource;
import net.spell_power.api.SpellPower;
import net.spellbladenext.items.FriendshipBracelet;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.IntStream;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class EndersGaze extends SpellProjectile implements ItemSupplier {
    public int number;
    public Entity target;
    public SpellPower.Result power;
    public SpellHelper.ImpactContext context;
    public Spell spell;
    public int life = 80;

    public EndersGaze(EntityType<? extends EndersGaze> entityType, Level level) {
        super(entityType, level);
    }
    public EndersGaze(EntityType<? extends EndersGaze> entityType, Level level, Player player, Entity target, int number) {
        super(entityType, level);
        this.setOwner(player);
        this.target = target;
        this.number = number;
    }

    @Override
    public Behaviour behaviour() {
        return Behaviour.FLY;
    }

    @Override
    public void tick() {
        this.setNoGravity(true);
        ParticleHelper.play(this.getLevel(),this,this.getXRot(),this.getYRot(), new ParticleBatch("spell_engine:arcane_spell", ParticleBatch.Shape.CIRCLE, ParticleBatch.Origin.CENTER, ParticleBatch.Rotation.LOOK,3,0,0,0));
        if(this.firstTick){
            this.playSound(SoundEvents.ILLUSIONER_CAST_SPELL);
        }
        if(this.target != null &&  this.target.isAlive()) {
            double tridentEntity = 0;
            double f = 0;
            float g = (float) (-sin(tridentEntity * ((float) Math.PI / 180)) * cos(f * ((float) Math.PI / 180)));
            float h = (float) -sin(f * ((float) Math.PI / 180));
            float r = (float) (cos(tridentEntity * ((float) Math.PI / 180)) * cos(f * ((float) Math.PI / 180)));
            this.setPos(this.target.getX() + (1D + target.getBoundingBox().getXsize()) * 0.5 * (1-0.25*Math.pow((((double) (this.tickCount % 8) / 8D)-1),2)) * cos((((double) (this.tickCount % 40) / 40D)) * (2D * (double) Math.PI) + (2 * Math.PI * (double) (number % 5) / 5D)), (double) this.target.getBoundingBox().getCenter().y(), this.target.getZ()  + (1D + target.getBoundingBox().getXsize()) * 0.5 * (1-0.25*Math.pow((((double) (this.tickCount % 8) / 8D)-1),2)) * sin((((double) (this.tickCount % 40) / 40D)) * (2 * Math.PI) + (2 * Math.PI * (double) (number % 5) / 5D)));

        }
        if(this.tickCount % 8 == 0){
            for (int ii = 0; ii < 25; ii++) {
                int i = (ii*40) % 1000;
                if (i <= 1000) {

                    double[] indices = IntStream.rangeClosed(0, (int) ((1000)))
                            .mapToDouble(x -> x).toArray();
                    if (i < 0) {
                        return;
                    }
                    double phi = Math.acos(1 - 2 * indices[i] / 1000);
                    double theta = Math.PI * (1 + Math.pow(5, 0.5) * indices[i]);
                    if (phi == Math.toRadians(180) && theta == Math.toRadians(180)) {
                        this.setInvisible(true);
                    }
                    double x = cos(theta) * sin(phi);
                    double y = -cos(phi);
                    double z = Math.sin(theta) * sin(phi);
                    this.getLevel().addParticle(ParticleTypes.WITCH, this.getX() + 2 * x, this.getY() + 2 * y, this.getZ() + 2 * z, 0, 0, 0);
                }
            }
            if(!this.getLevel().isClientSide() && this.power != null && this.context != null && this.getOwner() instanceof LivingEntity living2 && this.spell != null) {
                if (this.power != null && this.target != null && this.spell != null && this.context != null) {
                    SpellPower.Vulnerability vulnerability = SpellPower.Vulnerability.none;
                    vulnerability = SpellPower.Vulnerability.none;
                    if(target instanceof LivingEntity living)
                    vulnerability = SpellPower.getVulnerability(living, MagicSchool.ARCANE);

                    //SpellPower.Result power = SpellPower.getSpellPower(MagicSchool.ARCANE, (LivingEntity) this.getOwner());
                    double amount = this.power.randomValue(vulnerability);
                    amount *= (double) 0.1;
                    AttributeModifier modifier = new AttributeModifier(UUID.randomUUID(),"knockbackresist",1, AttributeModifier.Operation.ADDITION);
                    ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
                    builder.put(Attributes.KNOCKBACK_RESISTANCE, modifier);
                    if(target instanceof LivingEntity living)
                        living.getAttributes().addTransientAttributeModifiers(builder.build());

                    target.invulnerableTime = 0;
                    SpellHelper.performImpacts(this.getLevel(),living2,this.target,this.spell,this.context);
                    if(target instanceof LivingEntity living)
                        living.getAttributes().removeAttributeModifiers(builder.build());

                    //this.playSound(SoundEvents.ILLUSIONER_CAST_SPELL);

                }
            }
        }
            if(this.tickCount > life && !this.getLevel().isClientSide()){
            this.discard();
        }
            if(this.getOwner() instanceof Player living && !this.getLevel().isClientSide()) {
                if (this.target == null || !this.target.isAlive() || (this.target instanceof LivingEntity living2 && living2.isDeadOrDying())) {
                    Predicate<Entity> selectionPredicate = (target) -> {
                        return (TargetHelper.actionAllowed(TargetHelper.TargetingMode.AREA, TargetHelper.Intent.HARMFUL, living, target)
                                && FriendshipBracelet.PlayerFriendshipPredicate(living,target));
                    };
                    Spell.Release.Target.Area area = new Spell.Release.Target.Area();
                    area.angle_degrees = 360;
                    List<Entity> list = TargetHelper.targetsFromArea(this, this.position(),4F, area,  selectionPredicate);
                    list.removeIf(asdf -> asdf instanceof EndersGaze);
                    if(this.target != null) {
                        list.removeIf(asdf -> asdf == this.target);
                    }

                    this.target = getNearestEntity(list, TargetingConditions.forNonCombat(), living, this.getX(), this.getY(), this.getZ());
                    if(this.target != null){
                        this.tickCount = 0;
                    }
                    else{
                        this.discard();
                    }
                }
            }
        //super.tick();
        this.firstTick = false;
    }
    @Nullable
    public <T extends Entity> T getNearestEntity(List<? extends T> list, TargetingConditions targetingConditions, @Nullable Entity livingEntity, double d, double e, double f) {
        double g = -1.0D;
        T livingEntity2 = null;
        Iterator var13 = list.iterator();

        while(true) {
            T livingEntity3;
            double h;
            do {
                do {
                    if (!var13.hasNext()) {
                        return livingEntity2;
                    }

                    livingEntity3 = (T)var13.next();
                } while(livingEntity == livingEntity3);

                h = livingEntity3.distanceToSqr(d, e, f);
            } while(g != -1.0D && !(h < g));

            g = h;
            livingEntity2 = livingEntity3;
        }
    }


    @Override
    public ItemStack getItem() {
        return Items.ENDER_EYE.getDefaultInstance();
    }


    @Override
    protected void onHitBlock(BlockHitResult blockHitResult) {
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
    }

    @Override
    public boolean hurt(DamageSource damageSource, float f) {
        if (damageSource.getEntity() == this.getOwner()) {
            return false;
        }
        this.playSound(SoundEvents.GLASS_BREAK);
        this.discard();
        return super.hurt(damageSource, f);
    }
}
