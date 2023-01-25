package net.spellbladenext.entities;

import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
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
import net.spell_engine.entity.SpellProjectile;
import net.spell_engine.internals.SpellHelper;
import net.spell_engine.particle.ParticleHelper;
import net.spell_engine.utils.TargetHelper;
import net.spell_power.api.SpellPower;
import net.spellbladenext.SpellbladeNext;
import net.spellbladenext.items.FriendshipBracelet;

public class EndersGazeEntity extends SpellProjectile implements ItemSupplier {
    public SpellPower.Result power;
    public Spell spell;
    public SpellHelper.ImpactContext context;

    public EndersGazeEntity(EntityType<? extends EndersGazeEntity> entityType, Level level) {
        super(entityType, level);
    }
    public EndersGazeEntity(EntityType<? extends EndersGazeEntity> entityType, Level level, Player player) {
        super(entityType, level);
        this.setOwner(player);
    }
    @Override
    public SpellProjectile.Behaviour behaviour() {
        return SpellProjectile.Behaviour.FLY;
    }
    @Override
    public boolean isAttackable() {
        return false;
    }
    @Override
    public void tick() {
        this.setNoGravity(true);
        if(this.tickCount > 400 && !this.getLevel().isClientSide()){
            this.discard();
        }
        if(this.firstTick){
            this.playSound(SoundEvents.ILLUSIONER_CAST_SPELL);
        }
        ParticleHelper.play(this.getLevel(),this,this.getXRot(),this.getYRot(), new ParticleBatch("spell_engine:arcane_spell", ParticleBatch.Shape.CIRCLE, ParticleBatch.Origin.CENTER, ParticleBatch.Rotation.LOOK,3,0,0,0));

        super.tick();
    }


    @Override
    public ItemStack getItem() {
        return Items.ENDER_PEARL.getDefaultInstance();
    }

    @Override
    protected void onHitBlock(BlockHitResult blockHitResult) {
        this.discard();
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        if(this.getOwner() instanceof Player player && entityHitResult.getEntity() instanceof LivingEntity living && this.power != null && this.spell != null && this.context != null && TargetHelper.actionAllowed(TargetHelper.TargetingMode.DIRECT, TargetHelper.Intent.HARMFUL,player,entityHitResult.getEntity()) && FriendshipBracelet.PlayerFriendshipPredicate(player,living)) {
            for(int i = 1; i < 6; i++) {
                EndersGaze endersGaze = new EndersGaze(SpellbladeNext.GAZEHITTER, this.getLevel(), player, entityHitResult.getEntity(), i);
                endersGaze.setPos(entityHitResult.getEntity().getEyePosition());
                endersGaze.power = this.power;
                endersGaze.spell = this.spell;
                endersGaze.context = this.context;
                if (!this.getLevel().isClientSide()) {
                    this.getLevel().addFreshEntity(endersGaze);
                    this.discard();
                }
            }
        }
        //super.onHitEntity(entityHitResult);
    }
}
