package net.spellbladenext.entities;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.spell_engine.SpellEngineMod;
import net.spell_engine.api.spell.Sound;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.entity.SpellProjectile;
import net.spell_engine.internals.SpellHelper;
import net.spell_engine.utils.SoundHelper;
import net.spell_power.api.MagicSchool;
import net.spell_power.api.SpellDamageSource;
import net.spell_power.api.SpellPower;
import net.spellbladenext.SpellbladeNext;
import net.spellbladenext.effect.CleansingFlame;
import net.spellbladenext.items.FriendshipBracelet;

public class CleansingFlameEntity extends SpellProjectile implements ItemSupplier {
    public Entity target;
    public SpellPower.Result power;
    public SpellHelper.ImpactContext context;
    public Spell spell;
public int life = 0;
    public CleansingFlameEntity(EntityType<? extends CleansingFlameEntity> entityType, Level level) {
        super(entityType, level);
    }
    @Override
    public ItemStack getItem() {
        return SpellbladeNext.EXPLOSION.getDefaultInstance();
    }
    @Override
    public boolean isAttackable() {
        return false;
    }

    @Override
    public Behaviour behaviour() {
        return Behaviour.FLY;
    }

    @Override
    public void tick() {
        this.setNoGravity(true);
        if(this.firstTick && Registry.SOUND_EVENT.get(new ResourceLocation(SpellEngineMod.ID,"generic_fire_release")) != null)
            playSound(Registry.SOUND_EVENT.get(new ResourceLocation(SpellEngineMod.ID,"generic_fire_release")),0.1F,1F);
        if(this.target != null && this.getOwner() != null && this.power != null && this.context != null && this.getOwner() instanceof LivingEntity living) {
            Vec3 vec3 = this.target.getEyePosition().subtract(this.position());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.95D).add(vec3.normalize().scale(0.15)));
            if(this.getBoundingBox().expandTowards(this.getDeltaMovement()).intersects(this.target.getBoundingBox())){
            }
        }
        if(this.tickCount > 40 && !this.getLevel().isClientSide){
            this.discard();
        }
        if(this.life > 0 && this.tickCount > this.life && !this.getLevel().isClientSide){
            this.discard();
        }
        //this.setPos(this.position().add(this.getDeltaMovement()));
        super.tick();
    }


    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        if(this.getOwner() instanceof Player living && this.spell != null && this.context != null && FriendshipBracelet.PlayerFriendshipPredicate(living,entityHitResult.getEntity())) {
            if (entityHitResult.getEntity().invulnerableTime <= 10 && SpellHelper.performImpacts(this.getLevel(), living, entityHitResult.getEntity(), this.spell, this.context)) {
                entityHitResult.getEntity().invulnerableTime = 20;
            }
        }
        if(!this.getLevel().isClientSide()) {
            this.discard();
        }

        //super.onHitEntity(entityHitResult);
    }

    @Override
    protected void onHitBlock(BlockHitResult blockHitResult) {
        this.discard();
        super.onHitBlock(blockHitResult);
    }
}
