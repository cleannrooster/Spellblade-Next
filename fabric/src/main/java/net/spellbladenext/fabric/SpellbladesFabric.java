package net.spellbladenext.fabric;

import com.google.common.collect.Maps;
import com.ibm.icu.impl.CalendarCache;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.Registries;
import dev.architectury.registry.registries.RegistrySupplier;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.StatFormatter;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.spell_engine.api.item.trinket.SpellBookItem;
import net.spell_engine.api.item.trinket.SpellBooks;
import net.spell_engine.api.spell.CustomSpellHandler;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.api.spell.SpellContainer;
import net.spell_engine.entity.SpellProjectile;
import net.spell_engine.internals.*;

import net.spell_engine.utils.TargetHelper;
import net.spell_power.api.MagicSchool;
import net.spell_power.api.SpellDamageSource;
import net.spell_power.api.SpellPower;
import net.spell_power.api.attributes.SpellAttributes;
import net.spell_power.api.enchantment.Enchantments_SpellPower;
import net.spellbladenext.SpellbladeNext;
import net.fabricmc.api.ModInitializer;
import net.spellbladenext.fabric.block.Hexblade;
import net.spellbladenext.fabric.callbacks.HurtCallback;
import net.spellbladenext.fabric.config.ItemConfig;
import net.spellbladenext.fabric.config.LootConfig;
import net.spellbladenext.fabric.customspells.AttackAll;
import net.spellbladenext.fabric.dim.MagusDimension;
import net.spellbladenext.fabric.effects.*;
import net.spellbladenext.fabric.enchants.Spellshield;
import net.spellbladenext.fabric.entities.*;
import net.spellbladenext.fabric.entities.ai.SpinAttack;
import net.spellbladenext.fabric.interfaces.PlayerDamageInterface;
import net.spellbladenext.fabric.invasions.attackevent;
import net.spellbladenext.fabric.items.*;
import net.spellbladenext.entities.*;
import net.spellbladenext.fabric.items.armors.Armors;
import net.spellbladenext.fabric.items.spellblades.RuneDagger;
import net.spellbladenext.fabric.items.spellblades.Spellblade;
import net.spellbladenext.fabric.items.spellblades.Starforge;
import net.spellbladenext.items.FriendshipBracelet;
import net.spellbladenext.fabric.items.spellblades.Spellblades;
import net.tinyconfig.ConfigManager;

import java.util.*;
import java.util.function.Predicate;

import static net.minecraft.core.Registry.ENTITY_TYPE;
import static net.spell_engine.api.item.trinket.SpellBooks.itemIdFor;
import static net.spell_engine.internals.SpellHelper.impactTargetingMode;
import static net.spell_engine.internals.SpellHelper.launchPoint;
import static net.spellbladenext.SpellbladeNext.*;
import static net.spellbladenext.fabric.items.spellblades.Spellblades.attributesFrom;

public class SpellbladesFabric implements ModInitializer {

    public static ArrayList<attackevent> attackeventArrayList = new ArrayList<>();
    public static final Item NETHERDEBUG = new DebugNetherPortal(new FabricItemSettings().group(EXAMPLE_TAB).stacksTo(1));
    ;

    public static final ResourceLocation SINCELASTHEX = new ResourceLocation(MOD_ID, "lasthextime");
    public static final ResourceLocation HEXRAID = new ResourceLocation(MOD_ID, "hex");
    public static final EntityType<Reaver> REAVER;
    public static final EntityType<Magus> MAGUS;
    public static final EntityType<Archmagus> ARCHMAGUS;
    public static RegistrySupplier<Item> LASERBOW;
    public static final EntityType<MonkeyClone> MONKEYCLONE;

    public static final EntityType<ColdAttack> COLDATTACK;
    public static RegistrySupplier<Item> OFFERING = ITEMS.register("offering", () ->
            new Offering(new Item.Properties().tab(EXAMPLE_TAB)));
    public static RegistrySupplier<Item> PRISMATICEFFIGY = ITEMS.register("prismaticeffigy", () ->
            new PrismaticEffigy(new Item.Properties().tab(EXAMPLE_TAB)));
    public static RegistrySupplier<Item> MONKEYSTAFF = ITEMS.register("monkeystaff", () ->
            new MonkeyStaff(0, 0, new Item.Properties().tab(EXAMPLE_TAB)));
    public static RegistrySupplier<Item> MULBERRY = ITEMS.register("firestarter", () ->
            new Item(new Item.Properties().tab(EXAMPLE_TAB).stacksTo(1)));
    public static RegistrySupplier<Item> BAREHANDS = ITEMS.register("unarmed", () ->
            new Item(new Item.Properties().tab(EXAMPLE_TAB).stacksTo(1)));

    public static final EntityType<SpinAttack> SPIN;
    public static DeferredRegister<MobEffect> MOBEFFECTS = DeferredRegister.create(MOD_ID, Registry.MOB_EFFECT_REGISTRY);
    public static DeferredRegister<Enchantment> ENCHANTS = DeferredRegister.create(MOD_ID, Registry.ENCHANTMENT_REGISTRY);

    public static RegistrySupplier<MobEffect> SOULFIRE = MOBEFFECTS.register("soulflame", () ->  new SoulFire(MobEffectCategory.BENEFICIAL, 0xFF5A4F).addAttributeModifier(SpellAttributes.POWER.get(MagicSchool.FIRE).attribute,"6b64d185-2b88-46c9-833e-5d1c33804eec",1, AttributeModifier.Operation.ADDITION));
    public static RegistrySupplier<MobEffect> SPIKED = MOBEFFECTS.register("spiked", () ->  new Spiked(MobEffectCategory.HARMFUL, 0xFF5A4F));
    public static RegistrySupplier<MobEffect> CRASH = MOBEFFECTS.register("crash", () ->  new IceSlam(MobEffectCategory.BENEFICIAL, 0xFF5A4F));
    public static RegistrySupplier<MobEffect> UNARMED = MOBEFFECTS.register("unarmed", () ->  new Unarmed(MobEffectCategory.BENEFICIAL, 0xFF5A4F));

    public static EntityType<IceCrashEntity> ICECRASH;
    public static EntityType<IceCrashEntity> ICECRASH2;
    public static EntityType<IceCrashEntity> ICECRASH3;
    public static EntityType<IceSpikeEntity> ICESPIKE;

    public static RegistrySupplier<MobEffect> HEX = MOBEFFECTS.register("hex", () -> new Hex(MobEffectCategory.HARMFUL, 0x64329F));
    public static RegistrySupplier<MobEffect> DIREHEX = MOBEFFECTS.register("direhex", () -> new DireHex(MobEffectCategory.HARMFUL, 0x64329F));
    public static RegistrySupplier<MobEffect> SPLITLEFT = MOBEFFECTS.register("splitleft", () -> new SplitInvis(MobEffectCategory.BENEFICIAL, 0x64329F));
    public static RegistrySupplier<MobEffect> SLAMTARGET = MOBEFFECTS.register("slamtarget", () -> new SlamTarget(MobEffectCategory.HARMFUL, 0x64329F));
    public static RegistrySupplier<MobEffect> PORTALSICKNESS = MOBEFFECTS.register("portalsickness", () -> new PortalSickness(MobEffectCategory.HARMFUL, 0x64329F));
    public static RegistrySupplier<MobEffect> FERVOR = MOBEFFECTS.register("righteousfervor", () -> new RighteousFervor(MobEffectCategory.BENEFICIAL, 0x64329F));
    public static RegistrySupplier<MobEffect> FLAMES = MOBEFFECTS.register("risingflames", () -> new RisingFlames(MobEffectCategory.BENEFICIAL, 0x64329F).addAttributeModifier(SpellAttributes.HASTE.attribute, "0f75bc15-3595-4bf6-aeaf-f3cb04b0a0aa",0.05, AttributeModifier.Operation.MULTIPLY_TOTAL).addAttributeModifier(SpellAttributes.POWER.get(MagicSchool.FIRE).attribute,"5a3c223f-9172-4e76-bd2f-e77a6ca37429",0.05, AttributeModifier.Operation.MULTIPLY_TOTAL));
    public static RegistrySupplier<MobEffect> DETERM = MOBEFFECTS.register("determination", () -> new Determination(MobEffectCategory.BENEFICIAL, 0x64329F));

    public static RegistrySupplier<MobEffect> MAGEARMOR = MOBEFFECTS.register("magearmor", () -> new MageArmor(MobEffectCategory.BENEFICIAL, 0x64329F));
    public static RegistrySupplier<Enchantment> SPEHHSHIELD = ENCHANTS.register(new ResourceLocation(MOD_ID,"spellshield"), () -> new Spellshield(Enchantment.Rarity.RARE, EnchantmentCategory.VANISHABLE,EquipmentSlot.values()));
    public static final GameRules.Key<GameRules.BooleanValue> SHOULD_INVADE = GameRuleRegistry.register("hexbladeInvade", GameRules.Category.MOBS, GameRuleFactory.createBooleanRule(true));
/*
    public static RuneblazingArmor runeblazing_helmet = new RuneblazingArmor(ModArmorMaterials.RUNEBLAZING, EquipmentSlot.HEAD, new Item.Properties().tab(EXAMPLE_TAB), MagicSchool.FIRE);
    public static RuneblazingArmor RUNEBLAZINGCHEST = new RuneblazingArmor(ModArmorMaterials.RUNEBLAZING, EquipmentSlot.CHEST, new Item.Properties().tab(EXAMPLE_TAB), MagicSchool.FIRE);
    public static RuneblazingArmor RUNEBLAZINGLEGS = new RuneblazingArmor(ModArmorMaterials.RUNEBLAZING, EquipmentSlot.LEGS, new Item.Properties().tab(EXAMPLE_TAB), MagicSchool.FIRE);
    public static RuneblazingArmor runeblazing_feet = new RuneblazingArmor(ModArmorMaterials.RUNEBLAZING, EquipmentSlot.FEET, new Item.Properties().tab(EXAMPLE_TAB), MagicSchool.FIRE);
    public static RuneblazingArmor runefrosted_head = new RuneblazingArmor(ModArmorMaterials.RUNEFROSTED, EquipmentSlot.HEAD, new Item.Properties().tab(EXAMPLE_TAB), MagicSchool.FROST);
    public static RuneblazingArmor RUNEFROSTEDCHEST = new RuneblazingArmor(ModArmorMaterials.RUNEFROSTED, EquipmentSlot.CHEST, new Item.Properties().tab(EXAMPLE_TAB), MagicSchool.FROST);
    public static RuneblazingArmor RUNEFROSTEDLEGS = new RuneblazingArmor(ModArmorMaterials.RUNEFROSTED, EquipmentSlot.LEGS, new Item.Properties().tab(EXAMPLE_TAB), MagicSchool.FROST);
    public static RuneblazingArmor runefrosted_feet = new RuneblazingArmor(ModArmorMaterials.RUNEFROSTED, EquipmentSlot.FEET, new Item.Properties().tab(EXAMPLE_TAB), MagicSchool.FROST);
    public static RuneblazingArmor runegleaming_head = new RuneblazingArmor(ModArmorMaterials.RUNEGLEAMING, EquipmentSlot.HEAD, new Item.Properties().tab(EXAMPLE_TAB), MagicSchool.ARCANE);
    public static RuneblazingArmor RUNEGLEAMINGCHEST = new RuneblazingArmor(ModArmorMaterials.RUNEGLEAMING, EquipmentSlot.CHEST, new Item.Properties().tab(EXAMPLE_TAB), MagicSchool.ARCANE);
    public static RuneblazingArmor RUNEGLEAMINGLEGS = new RuneblazingArmor(ModArmorMaterials.RUNEGLEAMING, EquipmentSlot.LEGS, new Item.Properties().tab(EXAMPLE_TAB), MagicSchool.ARCANE);
    public static RuneblazingArmor runegleaming_feet = new RuneblazingArmor(ModArmorMaterials.RUNEGLEAMING, EquipmentSlot.FEET, new Item.Properties().tab(EXAMPLE_TAB), MagicSchool.ARCANE);
*/

/*
    public static Robes HOOD = new Robes(ModArmorMaterials.WOOL,EquipmentSlot.HEAD,new Item.Properties().tab(EXAMPLE_TAB));

    public static Robes ROBE = new Robes(ModArmorMaterials.WOOL,EquipmentSlot.CHEST,new Item.Properties().tab(EXAMPLE_TAB));
    public static Robes PANTS = new Robes(ModArmorMaterials.WOOL,EquipmentSlot.LEGS,new Item.Properties().tab(EXAMPLE_TAB));
    public static Robes BOOTS = new Robes(ModArmorMaterials.WOOL,EquipmentSlot.FEET,new Item.Properties().tab(EXAMPLE_TAB));
*/

    public static ConfigManager<ItemConfig> itemConfig;
    public static ConfigManager<LootConfig> lootConfig;

    public static EntityType<netherPortal> NETHERPORTAL;
    public static EntityType<netherPortalFrame> NETHERPORTALFRAME;

    public static final Block HEXBLADE = new Hexblade(FabricBlockSettings.of(Material.METAL).strength(5.0F, 6.0F).requiresTool().requiresCorrectToolForDrops().sound(SoundType.METAL).noOcclusion());
    static {
    }
    @Override
    public void onInitialize() {


        ServerTickEvents.START_SERVER_TICK.register(server -> {
                    for (Player player : server.getPlayerList().getPlayers()) {
                        if (SpellRegistry.getSpell(new ResourceLocation(MOD_ID, "roll")) != null && player instanceof SpellCasterEntity client && Objects.equals(client.getCurrentSpellId(), new ResourceLocation(MOD_ID, "roll"))) {
                            Spell spell = SpellRegistry.getSpell(new ResourceLocation(MOD_ID, "roll"));
                            player.getCooldowns().addCooldown(player.getUseItem().getItem(), (int) (spell.cost.cooldown_duration * 20));


                            if (client.getCurrentCastProgress() >= 1.0) {
                                client.clearCasting();
                            }
                        }
                        if (SpellRegistry.getSpell(new ResourceLocation(MOD_ID, "opportunity")) != null && player instanceof SpellCasterEntity client && Objects.equals(client.getCurrentSpellId(), new ResourceLocation(MOD_ID, "opportunity"))) {
                            Spell spell = SpellRegistry.getSpell(new ResourceLocation(MOD_ID, "opportunity"));
                            List<Entity> targets = TargetHelper.targetsFromArea(player, spell.range, spell.release.target.area, target -> (target.getY() != target.yOld || target.getX() != target.xOld || target.getZ() != target.zOld) && TargetHelper.actionAllowed(TargetHelper.TargetingMode.AREA, TargetHelper.Intent.HARMFUL,player,target));
                            if(!targets.isEmpty()) {
                                SpellHelper.performSpell(player.getLevel(), player, new ResourceLocation(MOD_ID, "opportunity"), TargetHelper.targetsFromArea(player, spell.range, spell.release.target.area, null),
                                        player.getMainHandItem(), SpellCast.Action.RELEASE, InteractionHand.MAIN_HAND, 0);
                            }
                            if (client.getCurrentCastProgress() >= 1.0) {
                                client.clearCasting();

                            }
                        }
                        if (SpellRegistry.getSpell(new ResourceLocation(MOD_ID, "riposte")) != null && player instanceof SpellCasterEntity client && Objects.equals(client.getCurrentSpellId(), new ResourceLocation(MOD_ID, "riposte"))) {
                            Spell spell = SpellRegistry.getSpell(new ResourceLocation(MOD_ID, "riposte"));
                            player.getCooldowns().addCooldown(player.getUseItem().getItem(), (int) (spell.cost.cooldown_duration * 20));


                            if (client.getCurrentCastProgress() >= 1.0) {
                                client.clearCasting();

                            }
                        }
                        if (player != null && player.getLevel() != null && !player.isShiftKeyDown()) {

                            if (SpellRegistry.getSpell(new ResourceLocation(MOD_ID, "monkeyreflect")) != null) {
                                Spell spell = SpellRegistry.getSpell(new ResourceLocation(MOD_ID, "monkeyreflect"));

                                if (player instanceof SpellCasterEntity caster) {
                                    if (Objects.equals(caster.getCurrentSpellId(), new ResourceLocation(MOD_ID, "monkeyreflect"))) {
                                        for(Projectile projectile : player.getLevel().getEntitiesOfClass(Projectile.class,player.getBoundingBox().inflate(3))){
                                            if(projectile.getDeltaMovement().dot(player.getPosition(1F).subtract(projectile.getPosition(1F)))<90){
                                                projectile.setDeltaMovement(projectile.getDeltaMovement().multiply(-1,-1,-1));
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
        );


        Registry.register(Registry.ITEM, new ResourceLocation(MOD_ID,"hexblade"), new BlockItem(HEXBLADE, new FabricItemSettings().tab(EXAMPLE_TAB).stacksTo(1)));

        //System.out.println(Registry.BANNER_PATTERN.getTag(tag2).get().toString());
        Registry.register(Registry.BLOCK, new ResourceLocation(MOD_ID,"hex"), HEXBLADE);

        SpellbladeNext.init();

        itemConfig  = new ConfigManager<ItemConfig>
                ("items", Default.itemConfig)
                .builder()
                .setDirectory(MOD_ID)
                .sanitize(true)
                .build();

        lootConfig = new ConfigManager<LootConfig>
                ("loot", Default.lootConfig)
                .builder()
                .setDirectory(MOD_ID)
                .sanitize(true)
                .constrain(LootConfig::constrainValues)
                .build();
        MOBEFFECTS.register();
        ENCHANTS.register();
        lootConfig.refresh();
        itemConfig.refresh();
        LaserBow laserbow = new LaserBow(new FabricItemSettings().group(EXAMPLE_TAB).stacksTo(1).durability(2500));
        if(itemConfig.value.weapons.get("laser_bow") != null) {
            laserbow.setAttributes(attributesFrom(itemConfig.value.weapons.get("laser_bow")));
        }
        else{
            ItemConfig.Weapon config = new ItemConfig.Weapon(0,-2)
                    .add(ItemConfig.SpellAttribute.bonus(SpellAttributes.POWER.get(MagicSchool.ARCANE), 8))
                    .add(ItemConfig.SpellAttribute.bonus(SpellAttributes.POWER.get(MagicSchool.FROST), 8))
                    .add(ItemConfig.SpellAttribute.bonus(SpellAttributes.POWER.get(MagicSchool.FIRE), 8));
            laserbow.setAttributes(attributesFrom(config));
            itemConfig.value.weapons.put("laser_bow", config);
        }
        LASERBOW = ITEMS.register("laser_bow",() -> laserbow);

        Spellblades.register(itemConfig.value.weapons);

        Orbs.register(itemConfig.value.weapons);
        Armors.register(itemConfig.value.armor_sets);

        SpellBooks.register(SpellBooks.create(new ResourceLocation(MOD_ID,"acrobat"),EXAMPLE_TAB));
        SpellBooks.register(SpellBooks.create(new ResourceLocation(MOD_ID,"warriorone"),EXAMPLE_TAB));

        SpellBooks.register(SpellBooks.create(new ResourceLocation(MOD_ID,"warriortwo"),EXAMPLE_TAB));

        SpellBooks.register(SpellBooks.create(new ResourceLocation(MOD_ID,"warriorpolearm"),EXAMPLE_TAB));

        SpellBooks.register(SpellBooks.create(new ResourceLocation(MOD_ID,"duelist"),EXAMPLE_TAB));
        SpellBooks.register(SpellBooks.create(new ResourceLocation(MOD_ID,"juggone"),EXAMPLE_TAB));
        SpellBooks.register(SpellBooks.create(new ResourceLocation(MOD_ID,"juggtwo"),EXAMPLE_TAB));

        SpellBooks.register(SpellBooks.create(new ResourceLocation(MOD_ID,"orb_fire"),EXAMPLE_TAB));
        SpellBooks.register(SpellBooks.create(new ResourceLocation(MOD_ID,"orb_frost"),EXAMPLE_TAB));
        SpellBooks.register(SpellBooks.create(new ResourceLocation(MOD_ID,"orb_arcane"),EXAMPLE_TAB));
        SpellBooks.register(SpellBooks.create(new ResourceLocation(MOD_ID,"spellblade_arcane"),EXAMPLE_TAB));
        SpellBooks.register(SpellBooks.create(new ResourceLocation(MOD_ID,"spellblade_frost"),EXAMPLE_TAB));
        SpellBooks.register(SpellBooks.create(new ResourceLocation(MOD_ID,"spellblade_fire"),EXAMPLE_TAB));
        SpellBooks.register(SpellBooks.create(new ResourceLocation(MOD_ID,"assassin"),EXAMPLE_TAB));



        //Registry.register(Registry.ITEM, new ResourceLocation(SpellbladeNext.MOD_ID, "bandofpacifism"),
        //        FRIENDSHIPBRACELET);


        Registry.register(Registry.CUSTOM_STAT, "lasthextime", SINCELASTHEX);
        Registry.register(Registry.CUSTOM_STAT, "hex", HEXRAID);

        Stats.CUSTOM.get(SINCELASTHEX, StatFormatter.DEFAULT);
        Stats.CUSTOM.get(HEXRAID, StatFormatter.DEFAULT);
        Registry.register(Registry.ITEM, new ResourceLocation(MOD_ID, "debug"), NETHERDEBUG);

      /*  Registry.register(Registry.ITEM, new ResourceLocation(MOD_ID, "hood"),
                HOOD);
        Registry.register(Registry.ITEM, new ResourceLocation(MOD_ID, "robe"),
                ROBE);
        Registry.register(Registry.ITEM, new ResourceLocation(MOD_ID, "pants"),
                PANTS);
        Registry.register(Registry.ITEM, new ResourceLocation(MOD_ID, "boots"),
                BOOTS);
        Registry.register(Registry.ITEM, new ResourceLocation(MOD_ID, "runeblazing_helmet"),
                runeblazing_helmet);

        Registry.register(Registry.ITEM, new ResourceLocation(MOD_ID, "runeblazing_chest"),
                RUNEBLAZINGCHEST);
        Registry.register(Registry.ITEM, new ResourceLocation(MOD_ID, "runeblazing_legs"),
                RUNEBLAZINGLEGS);
        Registry.register(Registry.ITEM, new ResourceLocation(MOD_ID, "runeblazing_feet"),runeblazing_feet);
        Registry.register(Registry.ITEM, new ResourceLocation(MOD_ID, "runefrosted_head"), runefrosted_head);

        Registry.register(Registry.ITEM, new ResourceLocation(MOD_ID, "runefrosted_chest"),
                RUNEFROSTEDCHEST);
        Registry.register(Registry.ITEM, new ResourceLocation(MOD_ID, "runefrosted_legs"),
                RUNEFROSTEDLEGS);
        Registry.register(Registry.ITEM, new ResourceLocation(MOD_ID, "runefrosted_feet"),
                runefrosted_feet);
        Registry.register(Registry.ITEM, new ResourceLocation(MOD_ID, "runegleaming_head"),
                runegleaming_head);
        Registry.register(Registry.ITEM, new ResourceLocation(MOD_ID, "runegleaming_chest"),
                RUNEGLEAMINGCHEST);
        Registry.register(Registry.ITEM, new ResourceLocation(MOD_ID, "runegleaming_legs"),
                RUNEGLEAMINGLEGS);
        Registry.register(Registry.ITEM, new ResourceLocation(MOD_ID, "runegleaming_feet"),
                runegleaming_feet);
*/
        ServerTickEvents.START_SERVER_TICK.register(server -> {
                for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                    if (player.getLevel().dimension().equals(DIMENSIONKEY) && player.getY() < -32) {
                        player.teleportTo(player.getX(), 150, player.getY());
                    }

                    if (((int) (player.getLevel().getDayTime() % 24000L)) % 1200 == 0 && server.getGameRules().getBoolean(SHOULD_INVADE)) {
                        if (player.getLevel().dimension().equals(DIMENSIONKEY) && !player.hasEffect(PORTALSICKNESS.get())) {
                            attackeventArrayList.add(new attackevent(player.getLevel(), player));
                        }
                        if (player.getStats().getValue(Stats.CUSTOM.get(HEXRAID)) > 0 && !player.hasEffect(PORTALSICKNESS.get())) {

                            player.awardStat(SINCELASTHEX, 1);

                            if (!player.hasEffect(HEX.get()) && player.getStats().getValue(Stats.CUSTOM.get(SINCELASTHEX)) > 10 && player.getRandom().nextFloat() < 0.01 * (player.getStats().getValue(Stats.CUSTOM.get(HEXRAID)) / 100F) * Math.pow((1.02930223664), player.getStats().getValue(Stats.CUSTOM.get(SINCELASTHEX)))) {
                                Optional<BlockPos> pos2 = BlockPos.findClosestMatch(player.blockPosition(), 64, 128,
                                        blockPos -> player.getLevel().getBlockState(blockPos).getBlock().equals(HEXBLADE));
                                if (pos2.isPresent() || player.getInventory().hasAnyOf(Set.of(Item.BY_BLOCK.get(HEXBLADE)))) {
                                } else {
                                    player.addEffect(new MobEffectInstance(HEX.get(), 20 * 60 * 3, 0, false, false));
                                }
                            }
                        }
                        player.getStats().setValue(player, Stats.CUSTOM.get(HEXRAID), 0);
                    }

            }
            attackeventArrayList.removeIf(attackevent -> attackevent.tickCount > 500 || attackevent.done);
            for (attackevent attackEvent : attackeventArrayList) {
                attackEvent.tick();
            }
        });
        HurtCallback.EVENT.register((damageSource, f) -> {
            if(damageSource.isMagic() && damageSource.getEntity() instanceof Player player){
                player.awardStat(HEXRAID, (int) Math.ceil(f));
            }
            return InteractionResult.PASS;
        });
        CustomSpellHandler.register(new ResourceLocation(MOD_ID,"flicker_strike"),(data) -> {

            CustomSpellHandler.Data data1 = (CustomSpellHandler.Data) data;

            if(data1.caster() instanceof PlayerDamageInterface player) {
                List<LivingEntity> list = new ArrayList<>();
                for(Entity entity: data1.targets()){
                    if(entity instanceof LivingEntity living && (!player.getList().contains(living) || (data1.targets().size() == 1 && data1.targets().contains(data1.caster().getLastHurtMob())))){
                        list.add(living);
                    }
                }
                if(list.isEmpty()){
                    player.listRefresh();
                    return false;

                }
                LivingEntity closest = data1.caster().getLevel().getNearestEntity(list,TargetingConditions.DEFAULT, data1.caster(),data1.caster().getX(),data1.caster().getY(),data1.caster().getZ());

                if(closest!= null) {
                    data1.caster().teleportTo(closest.getX()-((closest.getBbWidth()+1)*data1.caster().getViewVector(1.0F).subtract(0,data1.caster().getViewVector(1.0F).y(),0).normalize().x()),closest.getY(),closest.getZ()-((closest.getBbWidth()+1)*data1.caster().getViewVector(1.0F).subtract(0,data1.caster().getViewVector(1.0F).y(),0).normalize().z()));

                    AttackAll.attackAll(data1.caster(), List.of(closest), 1.0F * 0.2F * (float) data1.caster().getAttributeValue(Attributes.ATTACK_SPEED));
                    player.listAdd(closest);
                    return false;
                }
                else{
                    player.listRefresh();
                    return true;
                }

            }
            return false;
        });
        CustomSpellHandler.register(new ResourceLocation(MOD_ID,"barrage"),(data) -> {
                    CustomSpellHandler.Data data1 = (CustomSpellHandler.Data) data;
                    double i = 30;
                    Player player = data1.caster();

                        Predicate<Entity> selectionPredicate = (target) -> {
                            return (TargetHelper.actionAllowed(TargetHelper.TargetingMode.AREA, TargetHelper.Intent.HARMFUL, player, target)
                                    );
                        };
                        if(!data1.targets().isEmpty() && data1.targets().get(0) instanceof LivingEntity living) {
                            Entity target = data1.targets().get(0);
                            LivingEntity add1 = null;
                            LivingEntity add2 = null;
                            List<LivingEntity> list = living.getLevel().getNearbyEntities(LivingEntity.class,TargetingConditions.forCombat(),living,target.getBoundingBox().inflate(6));
                            list.removeIf((entity) -> !selectionPredicate.test(entity));
                            add1 = living.getLevel().getNearestEntity(list,TargetingConditions.DEFAULT,living,living.getX(),living.getY(),living.getZ());
                            list.remove(add1);
                            if(add1 != null)
                            add2 = add1.getLevel().getNearestEntity(list,TargetingConditions.DEFAULT,add1,add1.getX(),add1.getY(),add1.getZ());
                            if(player instanceof SpellCasterEntity caster && SpellContainerHelper.containerWithProxy(player.getMainHandItem(), player) != null && SpellContainerHelper.containerWithProxy(player.getMainHandItem(), player).spell_ids.contains("spellbladenext:multishot")) {
                                if (add1 != null)
                                    data1.targets().add(add1);
                                if (add2 != null)
                                    data1.targets().add(add2);
                            }
                            for (Entity living2 : data1.targets()) {
                                LaserArrow laserArrow = new LaserArrow(LASERARROW, data1.caster().getLevel());
                                laserArrow.setBaseDamage(player.getAttributeValue(SpellAttributes.POWER.get(MagicSchool.FROST).attribute)*0.5);
                                if(player instanceof SpellCasterEntity caster &&  SpellContainerHelper.containerWithProxy(player.getMainHandItem(), player) != null &&SpellContainerHelper.containerWithProxy(player.getMainHandItem(), player).spell_ids.contains("spellbladenext:chain")) {

                                laserArrow.chaining = 2;
                                }
                                if(player instanceof SpellCasterEntity caster &&  SpellContainerHelper.containerWithProxy(player.getMainHandItem(), player) != null && SpellContainerHelper.containerWithProxy(player.getMainHandItem(), player).spell_ids.contains("spellbladenext:primordialburst")) {
                                    laserArrow.burst = true;

                                }
                                laserArrow.setOwner(player);
                                laserArrow.setTarget(living2);
                                i = living2.distanceTo(data1.caster());


                                i *= 0.5;
                                if (i > 30) {
                                    i = 30;
                                }
                                laserArrow.setPos(data1.caster().getEyePosition().subtract(0, 0.10000000149011612D * (data1.caster().getBbHeight() / 1.8), 0));
                                int k = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.POWER_ARROWS, data1.caster().getUseItem());
                                if (k > 0) {
                                    laserArrow.setBaseDamage(laserArrow.getBaseDamage() + (double) k * 0.5D + 0.5D);
                                }
                                int l = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PUNCH_ARROWS, data1.caster().getUseItem());
                                if (l > 0) {
                                    laserArrow.setKnockback(l);
                                }

                                if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FLAMING_ARROWS, data1.caster().getUseItem()) > 0) {
                                    laserArrow.setSecondsOnFire(100);
                                }


                                data1.caster().getLevel().playSound((Player) null, data1.caster().getX(), data1.caster().getY(), data1.caster().getZ(), SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F / (data1.caster().getRandom().nextFloat() * 0.4F + 1.2F) + 1 * 0.5F);
                                laserArrow.shootFromRotation(data1.caster(), (float) (data1.caster().getXRot() - data1.caster().getRandom().nextDouble() * i), (float) (data1.caster().getYRot() + (-i + data1.caster().getRandom().nextDouble() * i * 2)), 0.0F, 1 * 2F, 1.0F);
                                data1.caster().getLevel().addFreshEntity(laserArrow);

                            }
                        }
                        else{
                            LaserArrow laserArrow = new LaserArrow(LASERARROW, data1.caster().getLevel());
                            laserArrow.chaining = 2;
                            laserArrow.setOwner(player);
                            i = 60;


                            i *= 0.5;
                            if (i > 30) {
                                i = 30;
                            }
                            laserArrow.setPos(data1.caster().getEyePosition().subtract(0, 0.10000000149011612D * (data1.caster().getBbHeight() / 1.8), 0));
                            int k = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.POWER_ARROWS, data1.caster().getUseItem());
                            if (k > 0) {
                                laserArrow.setBaseDamage(laserArrow.getBaseDamage() + (double) k * 0.5D + 0.5D);
                            }
                            int l = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PUNCH_ARROWS, data1.caster().getUseItem());
                            if (l > 0) {
                                laserArrow.setKnockback(l);
                            }

                            if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FLAMING_ARROWS, data1.caster().getUseItem()) > 0) {
                                laserArrow.setSecondsOnFire(100);
                            }


                            data1.caster().getLevel().playSound((Player) null, data1.caster().getX(), data1.caster().getY(), data1.caster().getZ(), SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F / (data1.caster().getRandom().nextFloat() * 0.4F + 1.2F) + 1 * 0.5F);
                            laserArrow.shootFromRotation(data1.caster(), (float) (data1.caster().getXRot() - data1.caster().getRandom().nextDouble() * i), (float) (data1.caster().getYRot() + (-i + data1.caster().getRandom().nextDouble() * i * 2)), 0.0F, 1 * 2F, 1.0F);
                            data1.caster().getLevel().addFreshEntity(laserArrow);
                        }
            return false;


        });
        CustomSpellHandler.register(new ResourceLocation(MOD_ID,"monkeyslam"),(data) -> {

            return false;
        });
        CustomSpellHandler.register(new ResourceLocation(MOD_ID,"opportunity"),(data) -> {

            CustomSpellHandler.Data data1 = (CustomSpellHandler.Data) data;
            AttackAll.attackAll(data1.caster(),data1.targets(),2.0F*data1.impactContext().channel());
            for(Entity target : data1.targets()){
                if(target instanceof LivingEntity living){
                    ((LivingEntity) target).addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,20,9,false,false));
                }
            }
            return true;
        });
        CustomSpellHandler.register(new ResourceLocation(MOD_ID,"backstab"),(data) -> {

            CustomSpellHandler.Data data1 = (CustomSpellHandler.Data) data;
            if(data1.caster().getMainHandItem().getItem() instanceof RuneDagger) {
                data1.caster().addEffect(new MobEffectInstance(DIREHEX.get(), 80, 0, false, false));
            }

            for(Entity target : data1.targets()){
                if(target instanceof LivingEntity living){
                    ((LivingEntity) target).addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,20,9,false,false));
                    Vec3 vec3 = target.position().subtract(target.getViewVector(1F).subtract(0,target.getViewVector(1F).y(),0).normalize().scale(1+0.5+(target.getBoundingBox().getXsize() / 2)));
                    if(target.getLevel().getBlockStates(new AABB(new BlockPos(vec3))).noneMatch(block -> block.isSuffocating(target.getLevel(),new BlockPos(vec3)))) {
                        AttackAll.attackAll(data1.caster(), List.of(living), 2.0F * data1.impactContext().channel());
                        data1.caster().teleportTo(vec3.x(),vec3.y(),vec3.z());
                    }
                }
            }

            return true;
        });
        CustomSpellHandler.register(new ResourceLocation(MOD_ID,"monkeyspin"),(data) -> {

            CustomSpellHandler.Data data1 = (CustomSpellHandler.Data) data;
            float modifier = 0.4F+0.6F/(float)data1.targets().size()+(0.6F-0.6F/(float)data1.targets().size())*Math.min(3,EnchantmentHelper.getEnchantmentLevel(Enchantments.SWEEPING_EDGE,data1.caster()))/3;

            AttackAll.attackAll(data1.caster(),data1.targets(),modifier*1.5F*0.2F*(float)data1.caster().getAttributeValue(Attributes.ATTACK_SPEED));
            return false;
        });
        CustomSpellHandler.register(new ResourceLocation(MOD_ID,"whirlwind"),(data) -> {

            CustomSpellHandler.Data data1 = (CustomSpellHandler.Data) data;
            float modifier = 0.4F+0.6F/(float)data1.targets().size()+(0.6F-0.6F/(float)data1.targets().size())*Math.min(3,EnchantmentHelper.getEnchantmentLevel(Enchantments.SWEEPING_EDGE,data1.caster()))/3;
            AttackAll.attackAll(data1.caster(),data1.targets(),modifier * 3.0F*0.2F*(float)data1.caster().getAttributeValue(Attributes.ATTACK_SPEED));
            return false;
        });
        CustomSpellHandler.register(new ResourceLocation(MOD_ID,"whirlwind_polearm"),(data) -> {

            CustomSpellHandler.Data data1 = (CustomSpellHandler.Data) data;
            float modifier = 0.4F+0.6F/(float)data1.targets().size()+(0.6F-0.6F/(float)data1.targets().size())*Math.min(3,EnchantmentHelper.getEnchantmentLevel(Enchantments.SWEEPING_EDGE,data1.caster()))/3;

            AttackAll.attackAll(data1.caster(),data1.targets(),modifier*3.0F*0.2F*(float)data1.caster().getAttributeValue(Attributes.ATTACK_SPEED));
            return false;
        });
        CustomSpellHandler.register(new ResourceLocation(MOD_ID,"dualwield_whirlwind"),(data) -> {

            CustomSpellHandler.Data data1 = (CustomSpellHandler.Data) data;
            float modifier = 0.4F+0.6F/(float)data1.targets().size()+(0.6F-0.6F/(float)data1.targets().size())*Math.min(3,EnchantmentHelper.getEnchantmentLevel(Enchantments.SWEEPING_EDGE,data1.caster()))/3;

            AttackAll.attackAll(data1.caster(),data1.targets(),modifier*1.5F*0.2F*(float)data1.caster().getAttributeValue(Attributes.ATTACK_SPEED));
            return false;
        });
        CustomSpellHandler.register(new ResourceLocation(MOD_ID,"1hslam"),(data) -> {

            CustomSpellHandler.Data data1 = (CustomSpellHandler.Data) data;
            AttackAll.attackAll(data1.caster(),data1.targets(),2*data1.impactContext().channel());
            return true;
        });
        CustomSpellHandler.register(new ResourceLocation(MOD_ID,"2hslam"),(data) -> {

            CustomSpellHandler.Data data1 = (CustomSpellHandler.Data) data;
            AttackAll.attackAll(data1.caster(),data1.targets(),2*data1.impactContext().channel());
            return true;
        });
        CustomSpellHandler.register(new ResourceLocation(MOD_ID,"riposte"),(data) -> {

            CustomSpellHandler.Data data1 = (CustomSpellHandler.Data) data;
            AttackAll.attackAll(data1.caster(),data1.targets(),1.5F*data1.impactContext().channel());
            return true;
        });
        CustomSpellHandler.register(new ResourceLocation(MOD_ID,"roll"),(data) -> {

            CustomSpellHandler.Data data1 = (CustomSpellHandler.Data) data;
            AttackAll.attackAll(data1.caster(),data1.targets(),1.5F*data1.impactContext().channel());
            return true;
        });

        CustomSpellHandler.register(new ResourceLocation(MOD_ID,"frostslam"),(data) -> {

            CustomSpellHandler.Data data1 = (CustomSpellHandler.Data) data;
            AttackAll.attackAll(data1.caster(),data1.targets(),2*data1.impactContext().channel());
            return true;
        });
        ServerTickEvents.START_SERVER_TICK.register(server -> {
            for(ServerLevel level : server.getAllLevels()) {
                for (SpellProjectile projectile : level.getEntities(EntityTypeTest.forClass(SpellProjectile.class), Objects::nonNull)) {
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
                                        .ifPresent(entity -> {targeted.add(entity); living.remove(entity);});
                                iii++;
                            }


                            for (int i = -1; i < targeted.size()-1; i++) {
                                LivingEntity entity = targeted.get(i+1);

                                Vec3 launchPoint = projectile.position();
                                Spell spell = SpellRegistry.getSpell(new ResourceLocation(MOD_ID, "magic_missile_shard"));
                                SpellHelper.ImpactContext context = new SpellHelper.ImpactContext(1, 1.0F, (Vec3) null, SpellPower.getSpellPower(spell.school, player), impactTargetingMode(spell));
                                SpellProjectile projectile2 = new SpellProjectile(projectile.getLevel(), player, launchPoint.x(), launchPoint.y(), launchPoint.z(), SpellProjectile.Behaviour.FLY, spell, entity, context);
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

        itemConfig.save();

        LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {
            LootHelper.configure(id, tableBuilder, lootConfig.value);
        });

    }

    static {
        ICECRASH = Registry.register(
                ENTITY_TYPE,
                new ResourceLocation(MOD_ID, "icecrash"),
                FabricEntityTypeBuilder.<IceCrashEntity>create(MobCategory.MISC, IceCrashEntity::new)
                        .dimensions(EntityDimensions.scalable(0.75F, 0.75F)) // dimensions in Minecraft units of the render
                        .trackRangeBlocks(128)
                        .trackedUpdateRate(1)
                        .build()
        );
        ICESPIKE = Registry.register(
                ENTITY_TYPE,
                new ResourceLocation(MOD_ID, "icespike"),
                FabricEntityTypeBuilder.<IceSpikeEntity>create(MobCategory.MISC, IceSpikeEntity::new)
                        .dimensions(EntityDimensions.fixed(1F, 3F)) // dimensions in Minecraft units of the render
                        .trackRangeBlocks(128)
                        .trackedUpdateRate(1)
                        .build()
        );
        ICECRASH2 = Registry.register(
                ENTITY_TYPE,
                new ResourceLocation(MOD_ID, "icecrash2"),
                FabricEntityTypeBuilder.<IceCrashEntity>create(MobCategory.MISC, IceCrashEntity::new)
                        .dimensions(EntityDimensions.scalable(1.5F, 1.5F)) // dimensions in Minecraft units of the render
                        .trackRangeBlocks(128)
                        .trackedUpdateRate(1)
                        .build()
        );
        ICECRASH3 = Registry.register(
                ENTITY_TYPE,
                new ResourceLocation(MOD_ID, "icecrash3"),
                FabricEntityTypeBuilder.<IceCrashEntity>create(MobCategory.MISC, IceCrashEntity::new)
                        .dimensions(EntityDimensions.scalable(3F, 3F)) // dimensions in Minecraft units of the render
                        .trackRangeBlocks(128)
                        .trackedUpdateRate(1)
                        .build()
        );
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
        MAGUS = Registry.register(
                ENTITY_TYPE,
                new ResourceLocation(MOD_ID, "magus"),
                FabricEntityTypeBuilder.<Magus>create(MobCategory.MISC, Magus::new)
                        .dimensions(EntityDimensions.fixed(1F, 2F)) // dimensions in Minecraft units of the render
                        .trackRangeBlocks(128)
                        .trackedUpdateRate(1)
                        .build()
        );
        ARCHMAGUS = Registry.register(
                ENTITY_TYPE,
                new ResourceLocation(MOD_ID, "archmagus"),
                FabricEntityTypeBuilder.<Archmagus>create(MobCategory.MISC, Archmagus::new)
                        .dimensions(EntityDimensions.fixed(1F, 2F)) // dimensions in Minecraft units of the render
                        .trackRangeBlocks(128)
                        .trackedUpdateRate(1)
                        .build()
        );
        MONKEYCLONE = Registry.register(
                ENTITY_TYPE,
                new ResourceLocation(MOD_ID, "monkeyclone"),
                FabricEntityTypeBuilder.<MonkeyClone>create(MobCategory.MISC, MonkeyClone::new)
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
        FabricDefaultAttributeRegistry.register(MONKEYCLONE,MonkeyClone.createAttributes());

        FabricDefaultAttributeRegistry.register(MAGUS,Magus.createAttributes());
        FabricDefaultAttributeRegistry.register(ARCHMAGUS,Archmagus.createAttributes());

        FabricDefaultAttributeRegistry.register(SPIN, SpinAttack.createAttributes());
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
        LASERARROW = Registry.register(
                ENTITY_TYPE,
                new ResourceLocation(MOD_ID, "laserarrow"),
                FabricEntityTypeBuilder.<LaserArrow>create(MobCategory.MISC, LaserArrow::new)
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
