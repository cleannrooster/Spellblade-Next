package net.spellbladenext;

import com.google.common.base.Suppliers;
import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.Registries;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.spell_engine.entity.SpellProjectile;
import net.spell_engine.tinyconfig.ConfigManager;
import net.spell_power.api.MagicSchool;
import net.spell_power.api.attributes.Attributes;
import net.spell_power.api.enchantment.MagicArmorEnchanting;
import net.spellbladenext.config.Default;
import net.spellbladenext.config.ItemConfig;
import net.spellbladenext.effect.CleansingFlame;
import net.spellbladenext.effect.Infusion;
import net.spellbladenext.effect.RunicAbsorption;
import net.spellbladenext.entities.*;
import net.spellbladenext.items.FriendshipBracelet;
import net.spellbladenext.items.ModArmorMaterials;
import net.spellbladenext.items.spellblades.RuneblazingArmor;
import net.spellbladenext.items.spellblades.Spellblades;

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
    public static DeferredRegister<MobEffect> MOBEFFECTS = DeferredRegister.create(MOD_ID,Registry.MOB_EFFECT_REGISTRY);
    public static RegistrySupplier<MobEffect> FIREINFUSION = MOBEFFECTS.register("fireinfusion", () -> new Infusion(MobEffectCategory.BENEFICIAL,0x990000).addAttributeModifier(
            Attributes.POWER.get(MagicSchool.FIRE).attribute,"aef7de5e-2333-401b-8f19-d83258eab800",2, AttributeModifier.Operation.ADDITION));
    public static RegistrySupplier<MobEffect> ARCANEINFUSION = MOBEFFECTS.register("arcaneinfusion", () ->new Infusion(MobEffectCategory.BENEFICIAL,0x990000).addAttributeModifier(
            Attributes.POWER.get(MagicSchool.ARCANE).attribute,"ace1b04e-4844-4780-9ff1-32d7f02d4d62",2, AttributeModifier.Operation.ADDITION));
    public static RegistrySupplier<MobEffect> FROSTINFUSION = MOBEFFECTS.register("frostinfusion", () ->new Infusion(MobEffectCategory.BENEFICIAL,0x990000).addAttributeModifier(
            Attributes.POWER.get(MagicSchool.FROST).attribute,"22b2d3fe-ce03-46b7-9114-6740bd338c7e",2, AttributeModifier.Operation.ADDITION));

    public static final RegistrySupplier<MobEffect> RUNICABSORPTION = MOBEFFECTS.register("runic_absorption", () -> new RunicAbsorption(MobEffectCategory.BENEFICIAL,0x994000));
    public static final RegistrySupplier<Item> RUNEBLAZEPLATING = ITEMS.register("runeblazing_ingot", () ->
           new Item( new Item.Properties().tab(EXAMPLE_TAB)));
    public static RegistrySupplier<Item> RUNEGLINTPLATING = ITEMS.register("runegleaming_ingot", () ->
            new Item( new Item.Properties().tab(EXAMPLE_TAB)));
    public static RegistrySupplier<Item> RUNEFROSTPLATING = ITEMS.register("runefrosted_ingot", () ->
            new Item( new Item.Properties().tab(EXAMPLE_TAB)));
    public static Item EXPLOSION = new Item(new Item.Properties());
    public static Item REALEXPLOSION = new Item(new Item.Properties());

    public static FriendshipBracelet FRIENDSHIPBRACELET = new FriendshipBracelet(new Item.Properties().tab(EXAMPLE_TAB));

    public static RuneblazingArmor RUNEBLAZINGHELMET = new RuneblazingArmor(ModArmorMaterials.RUNEBLAZING, EquipmentSlot.HEAD,new Item.Properties().tab(EXAMPLE_TAB),MagicSchool.FIRE);
    public static RuneblazingArmor RUNEBLAZINGCHEST = new RuneblazingArmor(ModArmorMaterials.RUNEBLAZING, EquipmentSlot.CHEST,new Item.Properties().tab(EXAMPLE_TAB),MagicSchool.FIRE);
    public static RuneblazingArmor RUNEBLAZINGLEGS = new RuneblazingArmor(ModArmorMaterials.RUNEBLAZING, EquipmentSlot.LEGS,new Item.Properties().tab(EXAMPLE_TAB),MagicSchool.FIRE);
    public static RuneblazingArmor RUNEBLAZINGBOOTS = new RuneblazingArmor(ModArmorMaterials.RUNEBLAZING, EquipmentSlot.FEET,new Item.Properties().tab(EXAMPLE_TAB),MagicSchool.FIRE);
    public static RuneblazingArmor RUNEFROSTEDHELMET = new RuneblazingArmor(ModArmorMaterials.RUNEFROSTED, EquipmentSlot.HEAD,new Item.Properties().tab(EXAMPLE_TAB),MagicSchool.FROST);
    public static RuneblazingArmor RUNEFROSTEDCHEST = new RuneblazingArmor(ModArmorMaterials.RUNEFROSTED, EquipmentSlot.CHEST,new Item.Properties().tab(EXAMPLE_TAB),MagicSchool.FROST);
    public static RuneblazingArmor RUNEFROSTEDLEGS = new RuneblazingArmor(ModArmorMaterials.RUNEFROSTED, EquipmentSlot.LEGS,new Item.Properties().tab(EXAMPLE_TAB),MagicSchool.FROST);
    public static RuneblazingArmor RUNEFROSTEDBOOTS = new RuneblazingArmor(ModArmorMaterials.RUNEFROSTED, EquipmentSlot.FEET,new Item.Properties().tab(EXAMPLE_TAB),MagicSchool.FROST);
    public static RuneblazingArmor RUNEGLEAMINGHELMET = new RuneblazingArmor(ModArmorMaterials.RUNEGLEAMING, EquipmentSlot.HEAD,new Item.Properties().tab(EXAMPLE_TAB),MagicSchool.ARCANE);
    public static RuneblazingArmor RUNEGLEAMINGCHEST = new RuneblazingArmor(ModArmorMaterials.RUNEGLEAMING, EquipmentSlot.CHEST,new Item.Properties().tab(EXAMPLE_TAB),MagicSchool.ARCANE);
    public static RuneblazingArmor RUNEGLEAMINGLEGS = new RuneblazingArmor(ModArmorMaterials.RUNEGLEAMING, EquipmentSlot.LEGS,new Item.Properties().tab(EXAMPLE_TAB),MagicSchool.ARCANE);
    public static RuneblazingArmor RUNEGLEAMINGBOOTS= new RuneblazingArmor(ModArmorMaterials.RUNEGLEAMING, EquipmentSlot.FEET,new Item.Properties().tab(EXAMPLE_TAB),MagicSchool.ARCANE);
    public static MobEffect FLAMEAURA = new CleansingFlame(MobEffectCategory.BENEFICIAL,0x990000);

    public static ConfigManager<ItemConfig> itemConfig = new ConfigManager<ItemConfig>
            ("items", Default.itemConfig)
            .builder()
            .setDirectory(MOD_ID)
            .sanitize(true)
            .build();

    public static void init() {
        Spellblades.register(itemConfig.value.weapons);
        Registry.register(Registry.MOB_EFFECT,new ResourceLocation(MOD_ID,"cleansing_fire"), FLAMEAURA);
        MOBEFFECTS.register();
        System.out.println(ExampleExpectPlatform.getConfigDirectory().toAbsolutePath().normalize().toString());
        ITEMS.register();

        Registry.register(Registry.ITEM, new ResourceLocation(SpellbladeNext.MOD_ID, "runeblazinghelmet"),
                RUNEBLAZINGHELMET);

        Registry.register(Registry.ITEM, new ResourceLocation(SpellbladeNext.MOD_ID, "runeblazingbodyarmor"),
                RUNEBLAZINGCHEST);
        Registry.register(Registry.ITEM, new ResourceLocation(SpellbladeNext.MOD_ID, "runeblazingleggings"),
                RUNEBLAZINGLEGS);
        Registry.register(Registry.ITEM, new ResourceLocation(SpellbladeNext.MOD_ID, "runeblazingboots"),RUNEBLAZINGBOOTS);
        Registry.register(Registry.ITEM, new ResourceLocation(SpellbladeNext.MOD_ID, "runefrostedhelmet"), RUNEFROSTEDHELMET);

        Registry.register(Registry.ITEM, new ResourceLocation(SpellbladeNext.MOD_ID, "runefrostedbodyarmor"),
                RUNEFROSTEDCHEST);
        Registry.register(Registry.ITEM, new ResourceLocation(SpellbladeNext.MOD_ID, "runefrostedleggings"),
                RUNEFROSTEDLEGS);
        Registry.register(Registry.ITEM, new ResourceLocation(SpellbladeNext.MOD_ID, "runefrostedboots"),
                RUNEFROSTEDBOOTS);
        Registry.register(Registry.ITEM, new ResourceLocation(SpellbladeNext.MOD_ID, "runegleaminghelmet"),
                RUNEGLEAMINGHELMET);
        Registry.register(Registry.ITEM, new ResourceLocation(SpellbladeNext.MOD_ID, "runegleamingbodyarmor"),
                RUNEGLEAMINGCHEST);
        Registry.register(Registry.ITEM, new ResourceLocation(SpellbladeNext.MOD_ID, "runegleamingleggings"),
                RUNEGLEAMINGLEGS);
        Registry.register(Registry.ITEM, new ResourceLocation(SpellbladeNext.MOD_ID, "runegleamingboots"),
                RUNEGLEAMINGBOOTS);
        Registry.register(Registry.ITEM, new ResourceLocation(SpellbladeNext.MOD_ID, "bandofpacifism"),
                FRIENDSHIPBRACELET);
        Registry.register(Registry.ITEM, new ResourceLocation(SpellbladeNext.MOD_ID, "explosion"),
                EXPLOSION);
        Registry.register(Registry.ITEM, new ResourceLocation(SpellbladeNext.MOD_ID, "realexplosion"),
                REALEXPLOSION);
        MagicArmorEnchanting.register(RUNEBLAZINGHELMET);
        MagicArmorEnchanting.register(RUNEBLAZINGCHEST);
        MagicArmorEnchanting.register(RUNEBLAZINGLEGS);
        MagicArmorEnchanting.register(RUNEBLAZINGBOOTS);
        MagicArmorEnchanting.register(RUNEFROSTEDHELMET);
        MagicArmorEnchanting.register(RUNEFROSTEDCHEST);
        MagicArmorEnchanting.register(RUNEFROSTEDLEGS);
        MagicArmorEnchanting.register(RUNEFROSTEDBOOTS);
        MagicArmorEnchanting.register(RUNEGLEAMINGHELMET);
        MagicArmorEnchanting.register(RUNEGLEAMINGCHEST);
        MagicArmorEnchanting.register(RUNEGLEAMINGLEGS);
        MagicArmorEnchanting.register(RUNEGLEAMINGBOOTS);



    }

}
