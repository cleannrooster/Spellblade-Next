package net.spellbladenext;

import com.google.common.base.Suppliers;
import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.Registries;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.spell_engine.api.render.CustomModels;
import net.spell_engine.tinyconfig.ConfigManager;
import net.spell_power.api.MagicSchool;
import net.spell_power.api.attributes.SpellAttributes;
import net.spellbladenext.config.ItemConfig;
import net.spellbladenext.config.LootConfig;
import net.spellbladenext.effect.*;
import net.spellbladenext.entities.*;
import net.spellbladenext.items.spellblades.Spellblades;

import java.util.List;
import java.util.function.Supplier;

public class SpellbladeNext {
    public static final String MOD_ID = "spellbladenext";
    // We can use this if we don't want to use DeferredRegister
    public static final Supplier<Registries> REGISTRIES = Suppliers.memoize(() -> Registries.get(MOD_ID));
    // Registering a new creative tab
    public static final CreativeModeTab EXAMPLE_TAB = CreativeTabRegistry.create(new ResourceLocation(MOD_ID, "example_tab"), () ->
            new ItemStack(Spellblades.spellblade));

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(MOD_ID, Registry.ITEM_REGISTRY);
    public static EntityType<AmethystEntity> AMETHYST;

    public static EntityType<AmethystEntity2> AMETHYST2;

    public static EntityType<CleansingFlameEntity> CLEANSINGFLAME;
    public static EntityType<Eruption> ERUPTION;

    public static EntityType<FlameWindsEntity> FLAMEWINDS;
    public static EntityType<EndersGazeEntity> GAZE;

    public static EntityType<EndersGaze> GAZEHITTER;

    public static EntityType<MagmaOrbEntity> MAGMA;
    public static EntityType<IceThorn> ICETHORN;
    public static EntityType<ExplosionDummy> EXPLOSIONDUMMY;
    public static EntityType<ExplosionDummy> REALEXPLOSIONDUMMY;


    public static EntityType<IcicleBarrierEntity> ICICLEBARRIER;
    public static DeferredRegister<MobEffect> MOBEFFECTS = DeferredRegister.create(MOD_ID, Registry.MOB_EFFECT_REGISTRY);
    public static RegistrySupplier<MobEffect> FIREINFUSION = MOBEFFECTS.register("fireinfusion", () -> new Infusion(MobEffectCategory.BENEFICIAL, 0x990000).addAttributeModifier(
            SpellAttributes.POWER.get(MagicSchool.FIRE).attribute, "aef7de5e-2333-401b-8f19-d83258eab800", 2, AttributeModifier.Operation.ADDITION));
    public static RegistrySupplier<MobEffect> ARCANEINFUSION = MOBEFFECTS.register("arcaneinfusion", () -> new Infusion(MobEffectCategory.BENEFICIAL, 0x990000).addAttributeModifier(
            SpellAttributes.POWER.get(MagicSchool.ARCANE).attribute, "ace1b04e-4844-4780-9ff1-32d7f02d4d62", 2, AttributeModifier.Operation.ADDITION));
    public static RegistrySupplier<MobEffect> FROSTINFUSION = MOBEFFECTS.register("frostinfusion", () -> new Infusion(MobEffectCategory.BENEFICIAL, 0x990000).addAttributeModifier(
            SpellAttributes.POWER.get(MagicSchool.FROST).attribute, "22b2d3fe-ce03-46b7-9114-6740bd338c7e", 2, AttributeModifier.Operation.ADDITION));
    public static RegistrySupplier<MobEffect> FIREOVERDRIVE = MOBEFFECTS.register("fireoverdrive", () ->  new Overdrive(MobEffectCategory.BENEFICIAL,MagicSchool.FIRE, 0xFF5A4F).addAttributeModifier(Attributes.ATTACK_SPEED,"363f757c-33be-4254-a647-4d30100a4bfd",0.2, AttributeModifier.Operation.MULTIPLY_TOTAL));
    public static RegistrySupplier<MobEffect> ARCANEOVERDRIVE = MOBEFFECTS.register("arcaneoverdrive", () ->  new Overdrive(MobEffectCategory.BENEFICIAL,MagicSchool.ARCANE, 0x64329F).addAttributeModifier(Attributes.ATTACK_SPEED,"4abf6ee7-591f-4ff5-b36a-babf2ffe23ca",0.2, AttributeModifier.Operation.MULTIPLY_TOTAL));
    public static RegistrySupplier<MobEffect> FROSTOVERDRIVE = MOBEFFECTS.register("frostoverdrive", () ->
            new Overdrive(MobEffectCategory.BENEFICIAL,MagicSchool.FROST, 0x60939F).addAttributeModifier(Attributes.ATTACK_SPEED,"8422cbc0-a935-4992-b187-60d69dd02cba",0.2, AttributeModifier.Operation.MULTIPLY_TOTAL));
    public static RegistrySupplier<MobEffect> SOULFIRE = MOBEFFECTS.register("soulflame", () ->  new SoulFire(MobEffectCategory.BENEFICIAL, 0xFF5A4F).addAttributeModifier(SpellAttributes.POWER.get(MagicSchool.FIRE).attribute,"6b64d185-2b88-46c9-833e-5d1c33804eec",1, AttributeModifier.Operation.ADDITION));

    @ExpectPlatform
    public static <T extends Entity> Supplier<EntityType<T>> registerEntityType(
            String name,
            Supplier<EntityType<T>> entityType
    ) {
        throw new AssertionError();
    }
    public static final RegistrySupplier<MobEffect> RUNICABSORPTION = MOBEFFECTS.register("runic_absorption", () -> new RunicAbsorption(MobEffectCategory.BENEFICIAL, 0x994000));
    public static final RegistrySupplier<Item> RUNEBLAZEPLATING = ITEMS.register("runeblazing_ingot", () ->
            new Item(new Item.Properties().tab(EXAMPLE_TAB)));
    public static RegistrySupplier<Item> RUNEGLINTPLATING = ITEMS.register("runegleaming_ingot", () ->
            new Item(new Item.Properties().tab(EXAMPLE_TAB)));
    public static RegistrySupplier<Item> RUNEFROSTPLATING = ITEMS.register("runefrosted_ingot", () ->
            new Item(new Item.Properties().tab(EXAMPLE_TAB)));
    public static RegistrySupplier<Item> OFFERING = ITEMS.register("offering", () ->
            new Item(new Item.Properties().tab(EXAMPLE_TAB)));

    public static Item EXPLOSION = new Item(new Item.Properties());
    public static Item REALEXPLOSION = new Item(new Item.Properties());

   // public static FriendshipBracelet FRIENDSHIPBRACELET = new FriendshipBracelet(new Item.Properties().tab(EXAMPLE_TAB));

    public static MobEffect FLAMEAURA = new CleansingFlame(MobEffectCategory.BENEFICIAL, 0x990000);
    public static RangedAttribute HEX = new RangedAttribute("hex", 0, 0, 9999);

    public static void init() {
        CustomModels.registerModelIds(List.of(new ResourceLocation(MOD_ID,"projectile/magic_missile")));

        Registry.register(Registry.MOB_EFFECT,new ResourceLocation(MOD_ID,"cleansing_fire"), FLAMEAURA);
        MOBEFFECTS.register();
        //System.out.println(ExampleExpectPlatform.getConfigDirectory().toAbsolutePath().normalize().toString());

        Registry.register(Registry.ATTRIBUTE, new ResourceLocation(MOD_ID,"hex"), HEX);

        Registry.register(Registry.ITEM, new ResourceLocation(SpellbladeNext.MOD_ID, "explosion"),
                EXPLOSION);
        Registry.register(Registry.ITEM, new ResourceLocation(SpellbladeNext.MOD_ID, "realexplosion"),
                REALEXPLOSION);

    }
}
