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
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.spell_engine.api.item.weapon.StaffItem;
import net.spell_engine.api.render.CustomModels;
import net.spell_power.api.MagicSchool;
import net.spell_power.api.attributes.SpellAttributes;
import net.spellbladenext.effect.*;
import net.spellbladenext.entities.*;

import java.util.List;
import java.util.function.Supplier;

public class SpellbladeNext {
    public static final String MOD_ID = "spellbladenext";
    // We can use this if we don't want to use DeferredRegister
    public static final Supplier<Registries> REGISTRIES = Suppliers.memoize(() -> Registries.get(MOD_ID));
    // Registering a new creative tab
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(MOD_ID, Registry.ITEM_REGISTRY);

    public static RegistrySupplier<Item> SPELLBLADEDUMMY = ITEMS.register("spellblade", () ->
            new Item(new Item.Properties()));
    public static final CreativeModeTab EXAMPLE_TAB = CreativeTabRegistry.create(new ResourceLocation(MOD_ID, "example_tab"), () ->
            new ItemStack(SPELLBLADEDUMMY.get()));

    public static EntityType<AmethystEntity> AMETHYST;

    public static EntityType<AmethystEntity2> AMETHYST2;
    public static Supplier<Ingredient> WOOL_INGREDIENTS = () -> { return Ingredient.of(
            Items.WHITE_WOOL,
            Items.ORANGE_WOOL,
            Items.MAGENTA_WOOL,
            Items.LIGHT_BLUE_WOOL,
            Items.YELLOW_WOOL,
            Items.LIME_WOOL,
            Items.PINK_WOOL,
            Items.GRAY_WOOL,
            Items.LIGHT_GRAY_WOOL,
            Items.CYAN_WOOL,
            Items.PURPLE_WOOL,
            Items.BLUE_WOOL,
            Items.BROWN_WOOL,
            Items.GREEN_WOOL,
            Items.RED_WOOL,
            Items.BLACK_WOOL);
    };
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
    public static RegistrySupplier<MobEffect> DOUSED = MOBEFFECTS.register("doused", () -> new Infusion(MobEffectCategory.HARMFUL, 0x990000).addAttributeModifier(
            SpellAttributes.POWER.get(MagicSchool.FIRE).attribute, "de7cee4d-954a-44a0-8b85-9b72fa475336", -0.95, AttributeModifier.Operation.MULTIPLY_TOTAL));
    public static RegistrySupplier<MobEffect> INERT = MOBEFFECTS.register("inert", () -> new Infusion(MobEffectCategory.HARMFUL, 0x990000).addAttributeModifier(
            SpellAttributes.POWER.get(MagicSchool.ARCANE).attribute, "7882eb98-c23c-4d4f-a13f-e14993ca12f6", -0.95, AttributeModifier.Operation.MULTIPLY_TOTAL));
    public static RegistrySupplier<MobEffect> MELTED = MOBEFFECTS.register("melted", () -> new Infusion(MobEffectCategory.HARMFUL, 0x990000).addAttributeModifier(
            SpellAttributes.POWER.get(MagicSchool.FROST).attribute, "c4337ce6-57ca-44cf-9eec-a765a179529d", -0.95, AttributeModifier.Operation.MULTIPLY_TOTAL));

    @ExpectPlatform
    public static <T extends Entity> Supplier<EntityType<T>> registerEntityType(
            String name,
            Supplier<EntityType<T>> entityType
    ) {
        throw new AssertionError();
    }
    public static final RegistrySupplier<MobEffect> RUNICABSORPTION = MOBEFFECTS.register("runic_absorption", () -> new RunicAbsorption(MobEffectCategory.BENEFICIAL, 0x994000));
    public static final RegistrySupplier<MobEffect> RUNICBOON = MOBEFFECTS.register("runic_boon", () -> new RunicBoon(MobEffectCategory.BENEFICIAL, 0x994000));


    public static final RegistrySupplier<Item> RUNEBLAZEPLATING = ITEMS.register("runeblazing_ingot", () ->
            new Item(new Item.Properties().tab(EXAMPLE_TAB)));
    public static RegistrySupplier<Item> RUNEGLINTPLATING = ITEMS.register("runegleaming_ingot", () ->
            new Item(new Item.Properties().tab(EXAMPLE_TAB)));
    public static RegistrySupplier<Item> RUNEFROSTPLATING = ITEMS.register("runefrosted_ingot", () ->
            new Item(new Item.Properties().tab(EXAMPLE_TAB)));
    public static final RegistrySupplier<Item> FIRETOTEM = ITEMS.register("nullifying_fire_totem", () ->
            new Item(new Item.Properties().tab(EXAMPLE_TAB)));
    public static RegistrySupplier<Item> FROSTTOTEM = ITEMS.register("nullifying_frost_totem", () ->
            new Item(new Item.Properties().tab(EXAMPLE_TAB)));
    public static RegistrySupplier<Item> ARCANETOTEM = ITEMS.register("nullifying_arcane_totem", () ->
            new Item(new Item.Properties().tab(EXAMPLE_TAB)));

    public static RegistrySupplier<Item> dummyfrost = ITEMS.register("icicle", () ->
            new Item(new Item.Properties()));
    public static RegistrySupplier<Item> dummyfrost2 = ITEMS.register("icicle2", () ->
            new Item(new Item.Properties()));
    public static RegistrySupplier<Item> dummyfrost3 = ITEMS.register("frostblade", () ->
            new Item(new Item.Properties()));

    public static Item EXPLOSION = new Item(new Item.Properties());
    public static Item REALEXPLOSION = new Item(new Item.Properties());

   // public static FriendshipBracelet FRIENDSHIPBRACELET = new FriendshipBracelet(new Item.Properties().tab(EXAMPLE_TAB));

    public static MobEffect FLAMEAURA = new CleansingFlame(MobEffectCategory.BENEFICIAL, 0x990000);
    public static RangedAttribute HEX = new RangedAttribute("hex", 0, 0, 9999);

    public static void init() {
        ITEMS.register();
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
