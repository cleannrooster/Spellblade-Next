package net.spellbladenext.fabric;

import carpet.script.language.Sys;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.authlib.minecraft.client.MinecraftClient;
import com.mojang.serialization.Dynamic;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundAddPlayerPacket;
import net.minecraft.network.protocol.game.ClientboundAnimatePacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.behavior.*;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.CrossbowAttackMob;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.piglin.*;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.spell_engine.api.spell.Sound;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.utils.SoundHelper;
import net.spell_engine.utils.TargetHelper;
import net.spell_power.SpellPowerMod;
import net.spell_power.api.MagicSchool;
import net.spell_power.api.SpellDamageSource;
import net.spellbladenext.SpellbladeNext;
import net.spellbladenext.items.spellblades.Spellblade;
import net.spellbladenext.items.spellblades.Spellblades;
import org.jetbrains.annotations.Nullable;
import software.bernie.example.entity.GeoExampleEntity;
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
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.function.Predicate;

public class Reaver extends PathfinderMob implements  InventoryCarrier, IAnimatable, Merchant {
    public Player nemesis;
    public boolean isScout = false;
    private boolean hasntthrownitems = true;
    private boolean firstattack = false;
    private boolean secondattack = false;
    private boolean isstopped = false;
    boolean isCaster = false;
    private Player tradingplayer;

    public Reaver(EntityType<? extends Reaver> p_34652_, Level p_34653_) {
        super(p_34652_, p_34653_);
    }
    private final SimpleContainer inventory = new SimpleContainer(8);
    private static final Set<Item> WANTED_ITEMS = ImmutableSet.of( Items.POTATO, Items.CARROT, Items.WHEAT, Items.WHEAT_SEEDS, Items.BEETROOT, Items.BEETROOT_SEEDS);
    public boolean returninghome = false;
    public boolean isleader = false;
    public int homecount = 0;
    public int homecount2 = 0;
    public Player hero = null;
    public boolean canGiveGifts = false;
    private AnimationFactory factory = GeckoLibUtil.createFactory(this);
    public static final RawAnimation ATTACK = new RawAnimation("animation.hexblade.new", ILoopType.EDefaultLoopTypes.PLAY_ONCE);
    public static final RawAnimation ATTACK2 = new RawAnimation("animation.hexblade.new2", ILoopType.EDefaultLoopTypes.PLAY_ONCE);
    public static final RawAnimation WALK = new RawAnimation("animation.hexblade.walk", ILoopType.EDefaultLoopTypes.LOOP);
    public static final RawAnimation WALK2 = new RawAnimation("animation.hexblade.walk2", ILoopType.EDefaultLoopTypes.LOOP);
    public static final RawAnimation IDLE = new RawAnimation("idle", ILoopType.EDefaultLoopTypes.LOOP);
    public static final RawAnimation IDLE1 = new RawAnimation("idle", ILoopType.EDefaultLoopTypes.PLAY_ONCE);
    protected static final ImmutableList<MemoryModuleType<?>> MEMORY_TYPES = ImmutableList.of(MemoryModuleType.LOOK_TARGET, MemoryModuleType.DOORS_TO_CLOSE, MemoryModuleType.NEAREST_LIVING_ENTITIES, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryModuleType.NEAREST_VISIBLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLINS, MemoryModuleType.NEARBY_ADULT_PIGLINS, MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM, MemoryModuleType.HURT_BY, MemoryModuleType.HURT_BY_ENTITY, MemoryModuleType.WALK_TARGET, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.ATTACK_TARGET, MemoryModuleType.ATTACK_COOLING_DOWN, MemoryModuleType.INTERACTION_TARGET, MemoryModuleType.PATH, MemoryModuleType.ANGRY_AT, MemoryModuleType.UNIVERSAL_ANGER, MemoryModuleType.AVOID_TARGET, MemoryModuleType.ADMIRING_ITEM, MemoryModuleType.TIME_TRYING_TO_REACH_ADMIRE_ITEM, MemoryModuleType.ADMIRING_DISABLED, MemoryModuleType.DISABLE_WALK_TO_ADMIRE_ITEM, MemoryModuleType.CELEBRATE_LOCATION, MemoryModuleType.DANCING, MemoryModuleType.HUNTED_RECENTLY, MemoryModuleType.NEAREST_VISIBLE_BABY_HOGLIN, MemoryModuleType.NEAREST_VISIBLE_NEMESIS, MemoryModuleType.NEAREST_VISIBLE_ZOMBIFIED, MemoryModuleType.RIDE_TARGET, MemoryModuleType.VISIBLE_ADULT_PIGLIN_COUNT, MemoryModuleType.VISIBLE_ADULT_HOGLIN_COUNT, MemoryModuleType.NEAREST_VISIBLE_HUNTABLE_HOGLIN, MemoryModuleType.NEAREST_TARGETABLE_PLAYER_NOT_WEARING_GOLD, MemoryModuleType.NEAREST_PLAYER_HOLDING_WANTED_ITEM, MemoryModuleType.ATE_RECENTLY, MemoryModuleType.NEAREST_REPELLENT);
    protected static final ImmutableList<SensorType<? extends Sensor<? super Reaver>>> SENSOR_TYPES = ImmutableList.of(SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_PLAYERS, SensorType.NEAREST_ITEMS, SensorType.HURT_BY, SensorType.PIGLIN_SPECIFIC_SENSOR);

    @Override
    public void setDropChance(EquipmentSlot equipmentSlot, float f) {
    }

    @Override
    protected float getEquipmentDropChance(EquipmentSlot equipmentSlot) {
        return 0;
    }

    public boolean isCaster(){
        return this.isCaster;
    }
    public boolean isScout(){
        return this.isScout;
    }

    public boolean canGiveGifts(){
        return this.canGiveGifts;
    }
    private static Optional<? extends LivingEntity> findNearestValidAttackTarget(Reaver piglin) {
        Brain<Reaver> brain = piglin.getBrain();
            Optional<LivingEntity> optional = BehaviorUtils.getLivingEntityFromUUIDMemory(piglin, MemoryModuleType.ANGRY_AT);
            if (optional.isPresent() && Sensor.isEntityAttackableIgnoringLineOfSight(piglin, (LivingEntity)optional.get())) {
                return optional;
            } else {
                Optional optional2;
                if (brain.hasMemoryValue(MemoryModuleType.UNIVERSAL_ANGER)) {
                    optional2 = brain.getMemory(MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER);
                    if (optional2.isPresent()) {
                        return optional2;
                    }
                }

                optional2 = brain.getMemory(MemoryModuleType.NEAREST_VISIBLE_NEMESIS);
                    return optional2;

            }
    }

    @Override
    public Brain<Reaver> getBrain() {
        return (Brain<Reaver>) super.getBrain();
    }

    static boolean isNearestValidAttackTarget(Reaver piglin, LivingEntity livingEntity) {
        return findNearestValidAttackTarget(piglin).filter((livingEntity2) -> {
            return livingEntity2 == livingEntity;
        }).isPresent();
    }
    public void addAdditionalSaveData(CompoundTag compoundTag) {

        super.addAdditionalSaveData(compoundTag);

        if (this.isCaster) {
            compoundTag.putBoolean("Caster", true);
        }
        else{
            compoundTag.putBoolean("Caster", false);

        }
        if (this.isScout) {
            compoundTag.putBoolean("Scout", true);
        }
        else{
            compoundTag.putBoolean("Scout", false);

        }
    }


    public void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);

        this.isCaster = compoundTag.getBoolean("Caster");
        this.isScout = compoundTag.getBoolean("Scout");

    }



    protected boolean isImmuneToZombification() {
        return true;
    }
    public boolean attacking = false;
    public boolean justattacked = false;
    public int attackTicker = 0;
    public int attackTime = 0;

    @Override
    public boolean shouldShowName() {
        return false;
    }

    @Override
    public boolean isCustomNameVisible() {
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        if(this instanceof SpinAttack || this instanceof ColdAttack){
            return;
        }
        if(homecount2 > 1000){
            this.playSound(SoundEvents.CHORUS_FRUIT_TELEPORT);
            this.discard();
        }
        List<Reaver> piglins = this.getLevel().getEntities(EntityTypeTest.forClass(Reaver.class),this.getBoundingBox().inflate(32), piglin -> !piglin.getBrain().isActive(Activity.IDLE) || piglin.tickCount < 1000);
        if(piglins.isEmpty() && this.tickCount % 10 == 0){
            List<Reaver> piglins2 = this.getLevel().getEntities(EntityTypeTest.forClass(Reaver.class),this.getBoundingBox().inflate(32), piglin -> true);
            for(Reaver piglin : piglins2){
                piglin.returninghome = true;
            }
        }
        List<Reaver> piglins2 = this.getLevel().getEntities(EntityTypeTest.forClass(Reaver.class),this.getBoundingBox().inflate(32), piglin -> piglin.isleader);
        if(piglins2.isEmpty()){
            this.isleader = true;
            this.homecount = -200;
        }
        if(returninghome && isleader){
            homecount++;
        }
        if(returninghome){
            homecount2++;
        }
        if(this.swingTime == 12){
            SoundHelper.playSoundEvent(this.getLevel(),this, SoundEvents.PLAYER_ATTACK_SWEEP);
            Spell.Release.Target.Area area = new Spell.Release.Target.Area();
            area.angle_degrees = 180;
            Predicate<Entity> selectionPredicate = (target) -> {
                return !(target instanceof Reaver);
            };
            List<Entity> list = TargetHelper.targetsFromArea(this, this.getBoundingBox().getCenter(),2.5F, area,  selectionPredicate);
            for(Entity entity : list){
                if(entity.hurt(SpellDamageSource.mob(getMagicSchool(),this),(float)this.getAttributeValue(Attributes.ATTACK_DAMAGE)/2)) {
                    entity.invulnerableTime = 0;
                    entity.hurt(DamageSource.mobAttack(this), (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE)/2);
                }
            }
        }
        this.attackTime++;
        if(this.attackTime > 18){
            this.justattacked = false;
            this.attackTime = 0;
            this.firstattack = false;
            this.secondattack = false;
        }

        if(this.homecount % 200 == 1){
            Optional<netherPortalFrame> frame = piglinsummon.summonNetherPortal(this.level,this,true);
            int ii = 0;
            while(frame.isEmpty() && ii < 10){
                frame = piglinsummon.summonNetherPortal(this.level,this,true);
                ii++;
            }
        }


    }

    protected SoundEvent getAmbientSound() {
        return null;
    }

    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return null;
    }

    protected SoundEvent getDeathSound() {
        return null;
    }

    protected void playStepSound(BlockPos blockPos, BlockState blockState) {
        this.playSound(SoundEvents.PIGLIN_BRUTE_STEP, 0.15F, 1.0F);
    }

    protected void playAngrySound() {
    }
    public MagicSchool getMagicSchool(){
        if(this.getMainHandItem().getItem() instanceof Spellblade spellblade){
            if(spellblade.getMagicSchools().stream().anyMatch(asdf -> MagicSchool.fromAttributeId(new ResourceLocation(SpellPowerMod.ID,asdf.name)).equals(MagicSchool.FIRE))){
                return MagicSchool.FIRE;
            }
            if(spellblade.getMagicSchools().stream().anyMatch(asdf -> MagicSchool.fromAttributeId(new ResourceLocation(SpellPowerMod.ID,asdf.name)).equals(MagicSchool.FROST))){
                return MagicSchool.FROST;
            }
            if(spellblade.getMagicSchools().stream().anyMatch(asdf -> MagicSchool.fromAttributeId(new ResourceLocation(SpellPowerMod.ID,asdf.name)).equals(MagicSchool.ARCANE))){
                return MagicSchool.ARCANE;
            }
        }
        return MagicSchool.ARCANE;
    }

    @Override
    protected boolean shouldDropLoot() {
        return true;
    }

    @Override
    public boolean checkSpawnRules(LevelAccessor levelAccessor, MobSpawnType mobSpawnType) {
        return super.checkSpawnRules(levelAccessor, mobSpawnType);
    }

    @Override
    public boolean hurt(DamageSource damageSource, float f) {
        if(damageSource.getDirectEntity() instanceof Player player && this.swingTime >= 8 && this.swingTime < 12){
            SoundHelper.playSoundEvent(this.getLevel(),this,SoundEvents.ANVIL_LAND,1,0.8F);
            this.knockback(1D,-this.getX()+damageSource.getDirectEntity().getX(),-this.getZ()+damageSource.getDirectEntity().getZ());
            this.swingTime = 18;
            this.swinging = false;
            this.isstopped = true;
            return false;
        }
        return super.hurt(damageSource, f);

    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 50.0D).add(Attributes.MOVEMENT_SPEED, 0.3499999940395355D).add(Attributes.ATTACK_DAMAGE, 4.0D).add(Attributes.KNOCKBACK_RESISTANCE,0.5);
    }
    protected void updateSwingTime() {
        int i = 18;
        if (this.swinging) {
            ++this.swingTime;
            if (this.swingTime >= i) {
                this.swingTime = 0;
                this.swinging = false;
            }
        } else {
            this.swingTime = 0;
        }

        this.attackAnim = (float)this.swingTime / (float)i;
    }
    @Override
    public void swing(InteractionHand interactionHand, boolean bl) {
        if (!this.swinging || this.swingTime >= 18 || this.swingTime < 0) {
            this.swingTime = -1;
            this.swinging = true;
            this.swingingArm = interactionHand;
            if (this.level instanceof ServerLevel) {
                ClientboundAnimatePacket clientboundAnimatePacket = new ClientboundAnimatePacket(this, interactionHand == InteractionHand.MAIN_HAND ? 0 : 3);
                ServerChunkCache serverChunkCache = ((ServerLevel)this.level).getChunkSource();
                if (bl) {
                    serverChunkCache.broadcastAndSend(this, clientboundAnimatePacket);
                } else {
                    serverChunkCache.broadcast(this, clientboundAnimatePacket);
                }
            }
        }

    }
    @Override
    public boolean doHurtTarget(Entity entity) {
        return false;
    }

    protected static boolean canHarvest(Reaver piglin){
        return piglin.getMainHandItem().getItem() instanceof HoeItem;
    }



    protected void pickUpItem(ItemEntity p_35467_) {


    }

    @Override
    protected Brain<?> makeBrain(Dynamic<?> dynamic) {
        return ReaverAI.makeBrain(this,brainProvider().makeBrain(dynamic));
    }
    protected Brain.Provider<Reaver> brainProvider() {
        return Brain.provider(MEMORY_TYPES, SENSOR_TYPES);
    }
    protected void customServerAiStep() {
        this.level.getProfiler().push("reaverBrain");
        this.getBrain().tick((ServerLevel)this.level, this);
        this.level.getProfiler().pop();
        ReaverAI.updateActivity(this);
        super.customServerAiStep();
    }
    @Override
    public SimpleContainer getInventory() {
        return this.inventory;
    }

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        boolean second = this.random.nextBoolean();
        if(this.swinging && !second) {
            event.getController().markNeedsReload();
            AnimationBuilder asdf3 = new AnimationBuilder();
            asdf3.getRawAnimationList().add(ATTACK);
            event.getController().setAnimation(asdf3);
            this.secondattack = true;
            this.swinging = false;
            return PlayState.CONTINUE;
        }
        if(this.swinging) {
            event.getController().markNeedsReload();
            AnimationBuilder asdf3 = new AnimationBuilder();
            asdf3.getRawAnimationList().add(ATTACK2);
            event.getController().setAnimation(asdf3);
            this.secondattack = false;
            this.swinging = false;
            return PlayState.CONTINUE;
        }
        return PlayState.CONTINUE;

    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand interactionHand) {
        if (this.getOffers().isEmpty() || !this.isScout()) {
            return InteractionResult.sidedSuccess(this.level.isClientSide);
        } else {
            if (!this.level.isClientSide) {
                this.setTradingPlayer(player);
                this.openTradingScreen(player, this.getDisplayName(), 1);
            }

            return InteractionResult.sidedSuccess(this.level.isClientSide);
        }
    }

    private <E extends IAnimatable> PlayState predicate2(AnimationEvent<E> event) {
        if(event.isMoving()){
            if(this.isAggressive()){
                AnimationBuilder asdf3 = new AnimationBuilder();
                asdf3.getRawAnimationList().add(WALK2);
                event.getController().setAnimation(asdf3);
                return PlayState.CONTINUE;

            }
            AnimationBuilder asdf2 = new AnimationBuilder();
            asdf2.getRawAnimationList().add(WALK);
            event.getController().setAnimation(asdf2);
            return PlayState.CONTINUE;

        }
        AnimationBuilder asdf2 = new AnimationBuilder();
        asdf2.getRawAnimationList().add(IDLE);
        event.getController().setAnimation(asdf2);
        return PlayState.CONTINUE;
    }



    @Override
    public void registerControllers(AnimationData animationData) {
        animationData.addAnimationController(new AnimationController<Reaver>(this,"walk",0,this::predicate2));

        animationData.addAnimationController(new AnimationController<Reaver>(this,"attack",0,this::predicate));


    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }

    @Override
    public void setTradingPlayer(@Nullable Player player) {
        this.tradingplayer = player;
    }

    @Nullable
    @Override
    public Player getTradingPlayer() {
        return this.tradingplayer;
    }

    @Override
    public MerchantOffers getOffers() {
        MerchantOffers offers = new MerchantOffers();
        ItemStack offering = new ItemStack(SpellbladeNext.OFFERING.get());
        offers.add(new MerchantOffer(
                new ItemStack(SpellbladeNext.RUNEBLAZEPLATING.get(),8),
                offering,10,8,0.02F));
        offers.add(new MerchantOffer(
                new ItemStack(SpellbladeNext.RUNEGLINTPLATING.get(),8),
                offering,10,8,0.02F));
        offers.add(new MerchantOffer(
                new ItemStack(SpellbladeNext.RUNEFROSTPLATING.get(),8),
                offering,10,8,0.02F));
        for(var entry : Spellblades.entries) {
            offers.add(new MerchantOffer(
                    new ItemStack(entry.item(), 8),
                    offering, 10, 8, 0.02F));
        }

        return offers;
    }

    @Override
    public void overrideOffers(MerchantOffers merchantOffers) {

    }

    @Override
    public void notifyTrade(MerchantOffer merchantOffer) {

    }

    @Override
    public void notifyTradeUpdated(ItemStack itemStack) {

    }

    @Override
    public int getVillagerXp() {
        return 0;
    }

    @Override
    public void overrideXp(int i) {

    }

    @Override
    public boolean showProgressBar() {
        return false;
    }

    @Override
    public SoundEvent getNotifyTradeSound() {
        return null;
    }

    public void openTradingScreen(Player player, Component component, int i) {
        OptionalInt optionalInt = player.openMenu(new SimpleMenuProvider((ix, inventory, playerx) -> {
            return new MerchantMenu(ix, inventory, this);
        }, component));
        if (optionalInt.isPresent() && this.isScout()) {
            MerchantOffers merchantOffers = this.getOffers();
            if (!merchantOffers.isEmpty()) {
                player.sendMerchantOffers(optionalInt.getAsInt(), merchantOffers, i, this.getVillagerXp(), this.showProgressBar(), this.canRestock());
            }
        }

    }

    @Override
    public boolean isClientSide() {
        return false;
    }
}

