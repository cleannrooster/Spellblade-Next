package net.spellbladenext.entities;

import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.scores.PlayerTeam;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.client.SpellEngineClient;
import net.spell_engine.entity.SpellProjectile;
import net.spell_engine.internals.SpellHelper;
import net.spell_engine.utils.TargetHelper;
import net.spell_power.api.MagicSchool;
import net.spell_power.api.SpellDamageSource;
import net.spell_power.api.SpellPower;
import net.spellbladenext.items.FriendshipBracelet;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.IntStream;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class FlameWindsEntity extends SpellProjectile implements ItemSupplier {
    public SpellPower.Result power;
    public Spell spell;
    public SpellHelper.ImpactContext context;

    public FlameWindsEntity(EntityType<? extends FlameWindsEntity> entityType, Level level) {
        super(entityType, level);
        this.setNoGravity(true);
        this.noPhysics = true;

    }
    public FlameWindsEntity(EntityType<? extends FlameWindsEntity> entityType, Level level, Player player) {
        super(entityType, level);
        this.setOwner(player);
        this.setNoGravity(true);
        this.noPhysics = true;
    }
    @Override
    public boolean isAttackable() {
        return false;
    }

    @Override
    public ItemStack getItem() {
        return Items.AIR.getDefaultInstance();
    }

    @Override
    public void tick() {
        if((this.tickCount > 50 || this.getDeltaMovement().length() < 0.2) && !this.getLevel().isClientSide()){
            this.discard();
        }
        if(this.tickCount % 4 == 0) {
            for (int ii = 0; ii < 100; ii++) {
                int i = (10 * ii + (this.tickCount % 10)) % 1000;
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
                    this.getLevel().addParticle(ParticleTypes.FLAME, this.getX() + 4 * x, this.getY() + 4 * y, this.getZ() + 4 * z, 0, 0, 0);
                }
            }
        }
        if(this.getOwner() instanceof Player player) {
            Predicate<Entity> selectionPredicate = (target) -> {
                return (TargetHelper.actionAllowed(TargetHelper.TargetingMode.AREA, TargetHelper.Intent.HARMFUL, player, target)
                        && FriendshipBracelet.PlayerFriendshipPredicate(player,target));
            };
            Spell.Release.Target.Area area = new Spell.Release.Target.Area();
            area.angle_degrees = 360;
            for (Entity entity : TargetHelper.targetsFromArea(this, this.position(), 4F, area, selectionPredicate)
            ) {
                if (!this.getLevel().isClientSide() && this.getOwner() instanceof Player owner) {
                    double a = 0;
                    if (entity instanceof LivingEntity living2) {
                        a = living2.getAttribute(Attributes.KNOCKBACK_RESISTANCE).getValue();
                    }
                    if (entity instanceof LivingEntity living2 && !TargetHelper.actionAllowed(TargetHelper.TargetingMode.DIRECT, TargetHelper.Intent.HARMFUL, owner, living2)) {
                        a = 1;
                    }
                    entity.setDeltaMovement(entity.getDeltaMovement().add(this.getDeltaMovement().scale((1 - a))));
                    if (entity.getDeltaMovement().length() > this.getDeltaMovement().length()) {
                        entity.setDeltaMovement(entity.getDeltaMovement().normalize().multiply(this.getDeltaMovement().length(), this.getDeltaMovement().length(), this.getDeltaMovement().length()));
                    }
                    if (this.power != null && this.spell != null && this.context != null && entity.invulnerableTime <= 10) {
                        SpellHelper.performImpacts(this.getLevel(), owner, entity, this.spell, this.context);
                        entity.invulnerableTime = 20;

                    }
                }

                //particleMultiplier = power.criticalDamage() + (double)vulnerability.criticalDamageBonus();
                //aster.setLastHurtMob(target);

            }
        }
        super.tick();
    }

    @Override
    public Behaviour behaviour() {
        return Behaviour.FLY;
    }

    @Override
    protected void onHit(HitResult hitResult) {
        super.onHit(hitResult);
    }

    @Override
    protected void onHitBlock(BlockHitResult blockHitResult) {
        this.discard();
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
    }
}
