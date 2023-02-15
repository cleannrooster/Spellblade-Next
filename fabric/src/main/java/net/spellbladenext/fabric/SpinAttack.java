package net.spellbladenext.fabric;

import com.google.gson.Gson;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.spell_engine.SpellEngineMod;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.entity.SpellProjectile;
import net.spell_engine.internals.SpellHelper;
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

public class SpinAttack extends Reaver implements InventoryCarrier, IAnimatable {
    private  LivingEntity caster = null;
    private SimpleContainer inventory;
    public float range;
    private Spell spell;
    private SpellHelper.ImpactContext context;
    private Entity followedTarget;
    public Vec3 previousVelocity;
    private Spell.ProjectileData clientSyncedData;
    private static String NBT_SPELL_DATA = "Spell.Data";
    private static String NBT_IMPACT_CONTEXT = "Impact.Context";
    private AnimationFactory factory = GeckoLibUtil.createFactory(this);

    public static final RawAnimation IDLE = new RawAnimation("animation.hexblade.spin", ILoopType.EDefaultLoopTypes.LOOP);
    public SpinAttack(EntityType<? extends SpinAttack> entityType, Level world) {
        super(entityType, world);
        this.getBrain().removeAllBehaviors();
        this.range = 128.0F;
    }

    public SpinAttack(Level world, LivingEntity owner) {
        super(ExampleModFabric.SPIN, world);
        this.getBrain().removeAllBehaviors();
        this.range = 128.0F;
        this.caster = owner;
    }
    public SpinAttack(Level world, LivingEntity caster, double x, double y, double z, SpellProjectile.Behaviour behaviour, Spell spell, Entity target, SpellHelper.ImpactContext context) {
        this(world, caster);
        this.setPos(x, y, z);
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
    public void tick() {

        if(this.hero != null){
            //System.out.println(this.hero);
            this.setPos(this.hero.position());
        }
        if(tickCount == 5 && this.hero != null){
            List<LivingEntity> list = this.getLevel().getNearbyEntities(LivingEntity.class, TargetingConditions.forNonCombat(),this.hero,this.getBoundingBox().inflate(1.5,0,1.5));
            list.forEach(asdf -> SpellHelper.performImpacts(this.getLevel(),this.hero,asdf,this.spell,this.context));
        }
        if(tickCount == 5 && this.hero == null && this.caster != null){
            List<LivingEntity> list = this.getLevel().getNearbyEntities(LivingEntity.class, TargetingConditions.forNonCombat(),this.caster,this.getBoundingBox().inflate(1.5,0,1.5));
            list.forEach(asdf -> SpellHelper.performImpacts(this.getLevel(),this.caster,asdf,this.spell,this.context));
        }
        if(tickCount >=15 && !this.getLevel().isClientSide){
            this.discard();
        }
        this.baseTick();
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

        AnimationBuilder asdf2 = new AnimationBuilder();
        asdf2.getRawAnimationList().add(IDLE);
        event.getController().setAnimation(asdf2);
        return PlayState.CONTINUE;
    }



    @Override
    public void registerControllers(AnimationData animationData) {
        animationData.addAnimationController(new AnimationController<SpinAttack>(this,"spin",0,this::predicate2));
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }
}
