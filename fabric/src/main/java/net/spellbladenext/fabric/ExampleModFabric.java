package net.spellbladenext.fabric;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.StatFormatter;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.entity.SpellProjectile;
import net.spell_engine.internals.SpellHelper;
import net.spell_engine.internals.SpellRegistry;
import net.spell_engine.utils.TargetHelper;
import net.spell_power.api.MagicSchool;
import net.spell_power.api.SpellPower;
import net.spell_power.api.attributes.SpellAttributes;
import net.spell_power.api.enchantment.MagicArmorEnchanting;
import net.spellbladenext.SpellbladeNext;
import net.fabricmc.api.ModInitializer;
import net.spellbladenext.entities.*;
import net.spellbladenext.fabric.items.DebugNetherPortal;
import net.spellbladenext.items.RuneblazingArmor;
import net.spellbladenext.items.FriendshipBracelet;
import net.spellbladenext.items.ModArmorMaterials;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static net.minecraft.core.Registry.ENTITY_TYPE;
import static net.spell_engine.internals.SpellHelper.impactTargetingMode;
import static net.spell_engine.internals.SpellHelper.launchPoint;
import static net.spellbladenext.SpellbladeNext.*;

public class ExampleModFabric implements ModInitializer {
    public static ArrayList<attackevent> attackeventArrayList = new ArrayList<>();
    public static final Item NETHERDEBUG = new DebugNetherPortal(new FabricItemSettings().group(EXAMPLE_TAB).stacksTo(1));;
    public static final ResourceLocation SINCELASTHEX = new ResourceLocation(MOD_ID, "lasthextime");
    public static final ResourceLocation HEXRAID = new ResourceLocation(MOD_ID, "hex");
    public static final EntityType<Reaver> REAVER;
    public static final EntityType<ColdAttack> COLDATTACK;

    public static final EntityType<SpinAttack> SPIN;
    public static DeferredRegister<MobEffect> MOBEFFECTS = DeferredRegister.create(MOD_ID, Registry.MOB_EFFECT_REGISTRY);

    public static RegistrySupplier<MobEffect> HEX = MOBEFFECTS.register("hex", () ->  new Hex(MobEffectCategory.HARMFUL, 0x64329F).addAttributeModifier(SpellAttributes.POWER.get(MagicSchool.FIRE).attribute,"6b64d185-2b88-46c9-833e-5d1c33804eec",1, AttributeModifier.Operation.ADDITION));

    public static final GameRules.Key<GameRules.BooleanValue> SHOULD_INVADE = GameRuleRegistry.register("hexbladeInvade", GameRules.Category.MOBS, GameRuleFactory.createBooleanRule(true));

    public static EntityType<netherPortal> NETHERPORTAL;
    public static EntityType<netherPortalFrame> NETHERPORTALFRAME;
    @Override
    public void onInitialize() {

        SpellbladeNext.init();

    }

    static {
        MOBEFFECTS.register();

        //Registry.register(Registry.ITEM, new ResourceLocation(SpellbladeNext.MOD_ID, "bandofpacifism"),
        //        FRIENDSHIPBRACELET);



        Registry.register(Registry.CUSTOM_STAT, "lasthextime", SINCELASTHEX);
        Registry.register(Registry.CUSTOM_STAT, "hex", HEXRAID);
        Stats.CUSTOM.get(SINCELASTHEX, StatFormatter.DEFAULT);
        Stats.CUSTOM.get(HEXRAID, StatFormatter.DEFAULT);
        Registry.register(Registry.ITEM, new ResourceLocation(MOD_ID, "debug"), NETHERDEBUG);

        ServerTickEvents.START_SERVER_TICK.register(server -> {
            for(ServerPlayer player : server.getPlayerList().getPlayers()){
                if (((int) (player.getLevel().getDayTime() % 24000L)) % 1200 == 0) {

                    if(player.getLevel().getGameRules().getBoolean(SHOULD_INVADE) && player.getStats().getValue(Stats.CUSTOM.get(HEXRAID)) > 0) {

                        player.awardStat(SINCELASTHEX, 1);
                        if (player.getRandom().nextFloat() < 0.01 * (player.getStats().getValue(Stats.CUSTOM.get(HEXRAID))/100F) * Math.pow((1.02930223664), player.getStats().getValue(Stats.CUSTOM.get(SINCELASTHEX)))) {
                            player.addEffect(new MobEffectInstance(HEX.get(),20*50,0,false,false));
                        }
                    }
                    player.getStats().setValue(player,Stats.CUSTOM.get(HEXRAID),0);
                }
            }
            attackeventArrayList.removeIf(attackevent -> attackevent.tickCount > 500);
            for (attackevent attackEvent : attackeventArrayList) {
                attackEvent.tick();
            }
        });
        HurtCallback.EVENT.register((damageSource, f) -> {
            //System.out.println("asdf");
            if(damageSource.isMagic() && damageSource.getEntity() instanceof Player player){
                player.awardStat(HEXRAID, (int) Math.ceil(f));
            }
            return InteractionResult.PASS;
        });

        ServerTickEvents.START_SERVER_TICK.register(server -> {
            for(ServerLevel level : server.getAllLevels()) {
                for (SpellProjectile projectile : level.getEntities(EntityTypeTest.forClass(SpellProjectile.class), asdf -> asdf instanceof SpellProjectile)) {
                    if (projectile.getSpell() != null && projectile.getOwner() instanceof Player player && projectile.getSpell().equals(SpellRegistry.getSpell(new ResourceLocation(MOD_ID, "magic_missile")))) {
                        if (projectile.tickCount >= 20) {
                            List<LivingEntity> living = projectile.getLevel().getEntitiesOfClass(LivingEntity.class, projectile.getBoundingBox().inflate(32));
                            living.removeIf(living1 -> !living1.hasLineOfSight(projectile));
                            Predicate<Entity> selectionPredicate = (target) -> {
                                return (TargetHelper.actionAllowed(TargetHelper.TargetingMode.AREA, TargetHelper.Intent.HARMFUL, player, target)
                                        && FriendshipBracelet.PlayerFriendshipPredicate(player,target) && target instanceof LivingEntity);
                            };
                            living.removeIf(living1 -> !selectionPredicate.test(living1));
                            List<LivingEntity> targeted = new ArrayList<>();
                            if(projectile.getFollowedTarget() instanceof LivingEntity livingEntity){
                                targeted.add(livingEntity);
                                targeted.add(livingEntity);
                                targeted.add(livingEntity);
                            }
                            int iii = 0;
                            while (!living.isEmpty() && targeted.size() < 3 && iii < 3) {
                                Optional.ofNullable(projectile.getLevel().getNearestEntity(living, TargetingConditions.forNonCombat(), player, projectile.getX(), projectile.getY(), projectile.getZ()))
                                        .ifPresent(asdf -> {targeted.add(asdf); living.remove(asdf);});
                                iii++;
                            }


                            for (int i = -1; i < targeted.size()-1; i++) {
                                LivingEntity asdf = targeted.get(i+1);

                                Vec3 launchPoint = projectile.position();
                                Spell spell = SpellRegistry.getSpell(new ResourceLocation(MOD_ID, "magic_missile_shard"));
                                SpellHelper.ImpactContext context = new SpellHelper.ImpactContext(1, 1.0F, (Vec3) null, SpellPower.getSpellPower(spell.school, player), impactTargetingMode(spell));
                                SpellProjectile projectile2 = new SpellProjectile(projectile.getLevel(), player, launchPoint.x(), launchPoint.y(), launchPoint.z(), SpellProjectile.Behaviour.FLY, spell, asdf, context);
                                Spell.ProjectileData projectileData = spell.release.target.projectile;

                                float velocity = projectileData.velocity;
                                float divergence = projectileData.divergence;
                                int ii = 0;
                                if (i == 0) {
                                    ii = 1;
                                }
                                projectile2.shootFromRotation(projectile, projectile.getXRot() - ii * 90, projectile.getYRot() + i * 90, 0, velocity, divergence);


                                projectile2.range = spell.range*4;
                                projectile2.getViewXRot(projectile.getXRot());
                                projectile2.setYRot(projectile.getYRot());
                                projectile.getLevel().addFreshEntity(projectile2);
                            }
                            projectile.discard();

                        }
                    }
                }
            }
        });

        SpellbladeNext.AMETHYST = Registry.register(
                ENTITY_TYPE,
                new ResourceLocation(MOD_ID, "amethyst"),
                FabricEntityTypeBuilder.<AmethystEntity>create(MobCategory.MISC, AmethystEntity::new)
                        .dimensions(EntityDimensions.fixed(0.25F, 0.25F)) // dimensions in Minecraft units of the render
                        .trackRangeBlocks(128)
                        .trackedUpdateRate(1)
                        .build()
        );
        SPIN = Registry.register(
                ENTITY_TYPE,
                new ResourceLocation(MOD_ID, "shade"),
                FabricEntityTypeBuilder.<SpinAttack>create(MobCategory.MISC, SpinAttack::new)
                        .dimensions(EntityDimensions.fixed(0.6F, 2F)) // dimensions in Minecraft units of the render
                        .trackRangeBlocks(128)
                        .trackedUpdateRate(1)
                        .build()
        );
        COLDATTACK = Registry.register(
                ENTITY_TYPE,
                new ResourceLocation(MOD_ID, "coldattack"),
                FabricEntityTypeBuilder.<ColdAttack>create(MobCategory.MISC, ColdAttack::new)
                        .dimensions(EntityDimensions.fixed(0.6F, 2F)) // dimensions in Minecraft units of the render
                        .trackRangeBlocks(128)
                        .trackedUpdateRate(1)
                        .build()
        );
        SpellbladeNext.AMETHYST2 = Registry.register(
                ENTITY_TYPE,
                new ResourceLocation(MOD_ID, "amethyst2"),
                FabricEntityTypeBuilder.<AmethystEntity2>create(MobCategory.MISC, AmethystEntity2::new)
                        .dimensions(EntityDimensions.fixed(0.25F, 0.25F)) // dimensions in Minecraft units of the render
                        .trackRangeBlocks(128)
                        .trackedUpdateRate(1)
                        .build()
        );
        SpellbladeNext.ICICLEBARRIER = Registry.register(
                ENTITY_TYPE,
                new ResourceLocation(MOD_ID, "iciclebarrier"),
                FabricEntityTypeBuilder.<IcicleBarrierEntity>create(MobCategory.MISC, IcicleBarrierEntity::new)
                        .dimensions(EntityDimensions.fixed(0.25F, 0.25F)) // dimensions in Minecraft units of the render
                        .trackRangeBlocks(128)
                        .trackedUpdateRate(1)
                        .build()
        );
        REAVER = Registry.register(
                ENTITY_TYPE,
                new ResourceLocation(MOD_ID, "reaver"),
                FabricEntityTypeBuilder.<Reaver>create(MobCategory.MISC, Reaver::new)
                        .dimensions(EntityDimensions.fixed(1F, 2F)) // dimensions in Minecraft units of the render
                        .trackRangeBlocks(128)
                        .trackedUpdateRate(1)
                        .build()
        );
        NETHERPORTALFRAME = Registry.register(
                ENTITY_TYPE,
                new ResourceLocation(MOD_ID, "netherportalframe"),
                FabricEntityTypeBuilder.<netherPortalFrame>create(MobCategory.MISC, netherPortalFrame::new)
                        .dimensions(EntityDimensions.fixed(1F, 1F)) // dimensions in Minecraft units of the render
                        .trackRangeBlocks(128)
                        .trackedUpdateRate(1)
                        .build()
        );
        NETHERPORTAL = Registry.register(
                ENTITY_TYPE,
                new ResourceLocation(MOD_ID, "netherportal"),
                FabricEntityTypeBuilder.<netherPortal>create(MobCategory.MISC, netherPortal::new)
                        .dimensions(EntityDimensions.fixed(1F, 1F)) // dimensions in Minecraft units of the render
                        .trackRangeBlocks(128)
                        .trackedUpdateRate(1)
                        .build()
        );
        SpellbladeNext.MAGMA = Registry.register(
                ENTITY_TYPE,
                new ResourceLocation(MOD_ID, "magma"),
                FabricEntityTypeBuilder.<MagmaOrbEntity>create(MobCategory.MISC, MagmaOrbEntity::new)
                        .dimensions(EntityDimensions.fixed(1F, 1F)) // dimensions in Minecraft units of the render
                        .trackRangeBlocks(128)
                        .trackedUpdateRate(1)
                        .build()
        );
        SpellbladeNext.FLAMEWINDS = Registry.register(
                ENTITY_TYPE,
                new ResourceLocation(MOD_ID, "flamewinds"),
                FabricEntityTypeBuilder.<FlameWindsEntity>create(MobCategory.MISC, FlameWindsEntity::new)
                        .dimensions(EntityDimensions.fixed(1F, 1F)) // dimensions in Minecraft units of the render
                        .trackRangeBlocks(128)
                        .trackedUpdateRate(1)
                        .build()
        );
        SpellbladeNext.CLEANSINGFLAME = Registry.register(
                ENTITY_TYPE,
                new ResourceLocation(MOD_ID, "cleansingflame"),
                FabricEntityTypeBuilder.<CleansingFlameEntity>create(MobCategory.MISC, CleansingFlameEntity::new)
                        .dimensions(EntityDimensions.fixed(1F, 1F)) // dimensions in Minecraft units of the render
                        .trackRangeBlocks(128)
                        .trackedUpdateRate(1)
                        .build()
        );
        SpellbladeNext.ERUPTION = Registry.register(
                ENTITY_TYPE,
                new ResourceLocation(MOD_ID, "eruption"),
                FabricEntityTypeBuilder.<Eruption>create(MobCategory.MISC, Eruption::new)
                        .dimensions(EntityDimensions.fixed(0.5F, 0.5F)) // dimensions in Minecraft units of the render
                        .trackRangeBlocks(128)
                        .trackedUpdateRate(1)
                        .build()
        );
        SpellbladeNext.GAZE = Registry.register(
                ENTITY_TYPE,
                new ResourceLocation(MOD_ID, "gaze"),
                FabricEntityTypeBuilder.<EndersGazeEntity>create(MobCategory.MISC, EndersGazeEntity::new)
                        .dimensions(EntityDimensions.fixed(0.5F, 0.5F)) // dimensions in Minecraft units of the render
                        .trackRangeBlocks(128)
                        .trackedUpdateRate(1)
                        .build()
        );
        FabricDefaultAttributeRegistry.register(REAVER,Reaver.createAttributes());
        FabricDefaultAttributeRegistry.register(SPIN,SpinAttack.createAttributes());
        FabricDefaultAttributeRegistry.register(COLDATTACK,ColdAttack.createAttributes());

        SpellbladeNext.GAZEHITTER = Registry.register(
                ENTITY_TYPE,
                new ResourceLocation(MOD_ID, "gazehitter"),
                FabricEntityTypeBuilder.<EndersGaze>create(MobCategory.MISC, EndersGaze::new)
                        .dimensions(EntityDimensions.fixed(0.5F, 0.5F)) // dimensions in Minecraft units of the render
                        .trackRangeBlocks(128)
                        .trackedUpdateRate(1)
                        .build()
        );
        SpellbladeNext.ICETHORN = Registry.register(
                ENTITY_TYPE,
                new ResourceLocation(MOD_ID, "icethorn"),
                FabricEntityTypeBuilder.<IceThorn>create(MobCategory.MISC, IceThorn::new)
                        .dimensions(EntityDimensions.fixed(0.5F, 0.5F)) // dimensions in Minecraft units of the render
                        .trackRangeBlocks(128)
                        .trackedUpdateRate(1)
                        .build()
        );
        SpellbladeNext.EXPLOSIONDUMMY = Registry.register(
                ENTITY_TYPE,
                new ResourceLocation(MOD_ID, "explosion"),
                FabricEntityTypeBuilder.<ExplosionDummy>create(MobCategory.MISC, ExplosionDummy::new)
                        .dimensions(EntityDimensions.fixed(0.5F, 0.5F)) // dimensions in Minecraft units of the render
                        .trackRangeBlocks(128)
                        .trackedUpdateRate(1)
                        .build()
        );
    }
    public static BlockHitResult getPlayerPOVHitResult(Level p_41436_, Player p_41437_, ClipContext.Fluid p_41438_) {
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
    public static Vec3 getPlayerPOVHitResultplus(Level p_41436_, Player p_41437_, ClipContext.Fluid p_41438_) {
        float f = 0;
        float f1 = p_41437_.getYRot()+30;
        Vec3 vec3 = p_41437_.getEyePosition();
        float f2 = Mth.cos(-f1 * ((float)Math.PI / 180F) - (float)Math.PI);
        float f3 = Mth.sin(-f1 * ((float)Math.PI / 180F) - (float)Math.PI);
        float f4 = -Mth.cos(-f * ((float)Math.PI / 180F));
        float f5 = Mth.sin(-f * ((float)Math.PI / 180F));
        float f6 = f3 * f4;
        float f7 = f2 * f4;
        double d0 = 2;
        Vec3 vec32 = new Vec3((double)f6 * d0, (double)f5 * d0, (double)f7 * d0);
        Vec3 vec33= Vec3.ZERO;
        if(vec32.horizontalDistance() != 0) {

             vec33 = new Vec3(vec32.x, 0, vec32.z);
             vec33.normalize().multiply(d0,d0,d0);
        }
        Vec3 vec31 = vec3.add(vec33);

        return vec31;
    }
    public static Vec3 getPlayerPOVHitResultminus(Level p_41436_, Player p_41437_, ClipContext.Fluid p_41438_) {
        float f = 0;
        float f1 = p_41437_.getYRot()-30;
        Vec3 vec3 = p_41437_.getEyePosition();
        float f2 = Mth.cos(-f1 * ((float)Math.PI / 180F) - (float)Math.PI);
        float f3 = Mth.sin(-f1 * ((float)Math.PI / 180F) - (float)Math.PI);
        float f4 = -Mth.cos(-f * ((float)Math.PI / 180F));
        float f5 = Mth.sin(-f * ((float)Math.PI / 180F));
        float f6 = f3 * f4;
        float f7 = f2 * f4;
        double d0 = 2;
        Vec3 vec32 = new Vec3((double)f6 * d0, (double)f5 * d0, (double)f7 * d0);
        Vec3 vec33= Vec3.ZERO;
        if(vec32.horizontalDistance() != 0) {

            vec33 = new Vec3(vec32.x, 0, vec32.z);
            vec33.normalize().multiply(d0,d0,d0);
        }
        Vec3 vec31 = vec3.add(vec33);

        return vec31;
    }
}
