package net.spellbladenext.fabric.entities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
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

import java.util.UUID;

public class MonkeyClone extends PathfinderMob implements InventoryCarrier, IAnimatable {
    private final SimpleContainer inventory = new SimpleContainer(8);
    private AnimationFactory factory = GeckoLibUtil.createFactory(this);
    public Player owner;
    private UUID owneruuid;
    public static final EntityDataAccessor<ItemStack> HEAD;
    public static final EntityDataAccessor<ItemStack> CHEST;
    public static final EntityDataAccessor<ItemStack> LEGS;
    public static final EntityDataAccessor<ItemStack> FEET;

    static {
        HEAD = SynchedEntityData.defineId(MonkeyClone.class, EntityDataSerializers.ITEM_STACK);
        CHEST = SynchedEntityData.defineId(MonkeyClone.class, EntityDataSerializers.ITEM_STACK);
        LEGS = SynchedEntityData.defineId(MonkeyClone.class, EntityDataSerializers.ITEM_STACK);
        FEET = SynchedEntityData.defineId(MonkeyClone.class, EntityDataSerializers.ITEM_STACK);

    }
    public MonkeyClone(EntityType<? extends PathfinderMob> entityType, Level level, Player owner) {
        super(entityType, level);
        this.setOwner(owner);
        this.entityData.set(HEAD,owner.getItemBySlot(EquipmentSlot.HEAD));
        this.entityData.set(CHEST,owner.getItemBySlot(EquipmentSlot.CHEST));
        this.entityData.set(LEGS,owner.getItemBySlot(EquipmentSlot.LEGS));
        this.entityData.set(FEET,owner.getItemBySlot(EquipmentSlot.FEET));

    }
    protected void defineSynchedData() {
        this.entityData.define(HEAD, ItemStack.EMPTY);
        this.entityData.define(CHEST, ItemStack.EMPTY);
        this.entityData.define(LEGS, ItemStack.EMPTY);
        this.entityData.define(FEET, ItemStack.EMPTY);
        super.defineSynchedData();
    }
    public MonkeyClone(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }
    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 20.00D).add(Attributes.MOVEMENT_SPEED, 0.3499999940395355D).add(Attributes.ATTACK_DAMAGE, 1.0D).add(Attributes.KNOCKBACK_RESISTANCE,0.5);
    }
    public static final RawAnimation ATTACK = new RawAnimation("animation.extendedrendererentity.staffswing", ILoopType.EDefaultLoopTypes.PLAY_ONCE);

    public void setOwner(@Nullable Entity entity) {
        if (entity != null) {
            this.owneruuid = entity.getUUID();
            this.cachedOwner = entity;
        }

    }

    @Override
    public boolean hurt(DamageSource damageSource, float f) {
        if( this.getOwner() != null && damageSource.getEntity() == this.getOwner()){
            return false;
        }
        return super.hurt(damageSource, f);
    }


    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2,new MeleeAttackGoal(this,1,false));

    }


    @Override
    public MoveControl getMoveControl() {
        return super.getMoveControl();
    }

    public void addAdditionalSaveData(CompoundTag compoundTag) {
        ItemStack head = (ItemStack)this.entityData.get(HEAD);
        if (!head.isEmpty()) {
            compoundTag.put("Head", head.save(new CompoundTag()));
        }
        ItemStack chest = (ItemStack)this.entityData.get(CHEST);
        if (!head.isEmpty()) {
            compoundTag.put("Chest", chest.save(new CompoundTag()));
        }
        ItemStack legs = (ItemStack)this.entityData.get(LEGS);
        if (!head.isEmpty()) {
            compoundTag.put("Legs", legs.save(new CompoundTag()));
        }
        ItemStack feet = (ItemStack)this.entityData.get(FEET);
        if (!head.isEmpty()) {
            compoundTag.put("feet", feet.save(new CompoundTag()));
        }
        if(this.getOwner() != null) {
            compoundTag.putUUID("Owner", this.getOwner().getUUID());
        }
        super.addAdditionalSaveData(compoundTag);

    }

    @Override
    public ItemStack getMainHandItem() {
        return ItemStack.EMPTY;
    }
    public Entity cachedOwner;

    public Entity getOwner() {
        if (this.cachedOwner != null && !this.cachedOwner.isRemoved()) {
            return this.cachedOwner;
        } else if (this.owneruuid != null && this.level instanceof ServerLevel) {
            this.cachedOwner = ((ServerLevel)this.level).getEntity(this.owneruuid);
            return this.cachedOwner;
        } else {
            return null;
        }
    }

    @Override
    protected void dropEquipment() {
    }

    @Override
    protected void dropExperience() {

    }

    @Override
    protected void dropAllDeathLoot(DamageSource damageSource) {

    }

    @Override
    protected void dropFromLootTable(DamageSource damageSource, boolean bl) {

    }

    @Override
    protected void dropCustomDeathLoot(DamageSource damageSource, int i, boolean bl) {

    }

    @Override
    public boolean shouldDropExperience() {
        return false;
    }

    @Override
    public void setDropChance(EquipmentSlot equipmentSlot, float f) {

    }

    @Override
    public void setGuaranteedDrop(EquipmentSlot equipmentSlot) {

    }

    @Override
    protected boolean shouldDropLoot() {
        return false;
    }

    @Override
    public void aiStep() {
        super.aiStep();
    }

    @Override
    protected void customServerAiStep() {
        if(this.getOwner() != null && this.getOwner() instanceof Player player) {
            if (player.getLastHurtMob() != null && !(player.getLastHurtMob() instanceof MonkeyClone monkeyClone && monkeyClone.getOwner() == this.getOwner())) {
                this.setTarget(player.getLastHurtMob());
                if(player.tickCount - player.getLastHurtMobTimestamp() > 160){
                    this.setTarget(null);
                }
            }
            BlockHitResult blockHitResult = getPlayerPOVHitResult(level,player, ClipContext.Fluid.NONE);
            if(this.getTarget() == null || this.getTarget().isDeadOrDying()) {
                this.getNavigation().moveTo(blockHitResult.getLocation().x, blockHitResult.getLocation().y, blockHitResult.getLocation().z, 1);
            }
        }
        super.customServerAiStep();
    }
    protected static BlockHitResult getPlayerPOVHitResult(Level p_41436_, Player p_41437_, ClipContext.Fluid p_41438_) {
        float f = p_41437_.getXRot();
        float f1 = p_41437_.getYRot();
        Vec3 vec3 = p_41437_.getEyePosition();
        float f2 = Mth.cos(-f1 * ((float)Math.PI / 180F) - (float)Math.PI);
        float f3 = Mth.sin(-f1 * ((float)Math.PI / 180F) - (float)Math.PI);
        float f4 = -Mth.cos(-f * ((float)Math.PI / 180F));
        float f5 = Mth.sin(-f * ((float)Math.PI / 180F));
        float f6 = f3 * f4;
        float f7 = f2 * f4;
        double d0 = 8;
        Vec3 vec31 = vec3.add((double)f6 * d0, (double)f5 * d0, (double)f7 * d0);
        return p_41436_.clip(new ClipContext(vec3, vec31, ClipContext.Block.OUTLINE, p_41438_, p_41437_));
    }
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        ItemStack head = ItemStack.of(compoundTag.getCompound("Head"));
        if (!head.isEmpty()) {
            this.entityData.set(HEAD, head);
        }
        ItemStack chest = ItemStack.of(compoundTag.getCompound("Chest"));
        if (!head.isEmpty()) {
            this.entityData.set(CHEST, chest);
        }
        ItemStack Legs = ItemStack.of(compoundTag.getCompound("Legs"));
        if (!head.isEmpty()) {
            this.entityData.set(LEGS, Legs);
        }
        ItemStack feet = ItemStack.of(compoundTag.getCompound("Feet"));
        if (!head.isEmpty()) {
            this.entityData.set(FEET, feet);
        }
        if (compoundTag.contains("Owner")) {
            this.owneruuid =  compoundTag.getUUID("Owner");
        }
        super.readAdditionalSaveData(compoundTag);

    }
    @Override
    public SimpleContainer getInventory() {
        return this.inventory;
    }
    private static Vec3 getInputVector(Vec3 vec3, float f, float g) {
        double d = vec3.lengthSqr();
        if (d < 1.0E-7D) {
            return Vec3.ZERO;
        } else {
            Vec3 vec32 = (d > 1.0D ? vec3.normalize() : vec3).scale((double)f);
            float h = Mth.sin(g * 0.017453292F);
            float i = Mth.cos(g * 0.017453292F);
            return new Vec3(vec32.x * (double)i - vec32.z * (double)h, vec32.y, vec32.z * (double)i + vec32.x * (double)h);
        }
    }
    public void moveRelative(Player owner,float f, Vec3 vec3) {
        Vec3 vec32 = getInputVector(vec3, f, owner.getYRot());
        this.setDeltaMovement(this.getDeltaMovement().add(vec32));
    }
    @Override
    public void tick() {
     /*   if(this.getOwner() != null && this.getOwner() instanceof Player player && this.tickCount <= 10){
            if(this.getOwner().isCrouching()) {
                moveRelative(player,(float) player.getAttribute(Attributes.MOVEMENT_SPEED).getValue(), new Vec3(-1, 0, 0));
            }
            else{
                moveRelative(player,(float) player.getAttribute(Attributes.MOVEMENT_SPEED).getValue(), new Vec3(1, 0, 0));
            }
        }*/
        if(this.tickCount > 320 && !this.getLevel().isClientSide()){
            this.discard();
        }
        super.tick();
    }

    @Override
    public void registerControllers(AnimationData animationData) {
        animationData.addAnimationController(new AnimationController<MonkeyClone>(this,"roll",0,this::rollpredicate));
        animationData.addAnimationController(new AnimationController<MonkeyClone>(this,"walk",0,this::predicate2));
        animationData.addAnimationController(new AnimationController<MonkeyClone>(this,"attack",0,this::predicate));



    }
    public static final RawAnimation WALK = new RawAnimation("animation.extendedrendererentity.walk", ILoopType.EDefaultLoopTypes.LOOP);
    public static final RawAnimation IDLE = new RawAnimation("animation.extendedrendererentity.new", ILoopType.EDefaultLoopTypes.LOOP);
    public static final RawAnimation ROLL = new RawAnimation("animation.extendedrendererentity.roll", ILoopType.EDefaultLoopTypes.PLAY_ONCE);

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        boolean second = this.random.nextBoolean();
        if(this.swinging) {
            event.getController().markNeedsReload();
            AnimationBuilder anim = new AnimationBuilder();
            anim.getRawAnimationList().add(ATTACK);
            event.getController().setAnimation(anim);
            this.swinging = false;
            return PlayState.CONTINUE;
        }
        return PlayState.CONTINUE;

    }
    private <E extends IAnimatable> PlayState predicate2(AnimationEvent<E> event) {
        if(event.isMoving()){

            AnimationBuilder anim = new AnimationBuilder();
            anim.getRawAnimationList().add(WALK);
            event.getController().setAnimation(anim);
            return PlayState.CONTINUE;

        }
        AnimationBuilder anim = new AnimationBuilder();
        anim.getRawAnimationList().add(IDLE);
        event.getController().setAnimation(anim);
        return PlayState.CONTINUE;
    }
    private <E extends IAnimatable> PlayState rollpredicate(AnimationEvent<E> event) {
        if(this.firstTick){

            AnimationBuilder anim = new AnimationBuilder();
            anim.getRawAnimationList().add(ROLL);
            event.getController().setAnimation(anim);
            return PlayState.CONTINUE;

        }
        return PlayState.CONTINUE;
    }
    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }
}
