package net.spellbladenext.items.spellblades;

import com.google.common.collect.ImmutableMultimap;
import dev.architectury.platform.Platform;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.spell_engine.SpellEngineMod;
import net.spell_engine.api.item.ConfigurableAttributes;
import net.spell_engine.api.item.StaffItem;
import net.spell_engine.api.spell.Spell;
import net.spell_power.SpellPowerMod;
import net.spell_power.api.MagicSchool;
import net.spell_power.api.attributes.SpellAttributes;
import net.spellbladenext.SpellbladeNext;
import net.spellbladenext.config.ItemConfig;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

public class Spellblades {

    public static final class Entry {
        private final String namespace;
        private final String name;
        private final Material material;
        private final ItemConfig.Weapon defaults;
        private @Nullable String requiredMod;

        public Entry(String namespace, String name, Material material, ItemConfig.Weapon defaults, @Nullable String requiredMod) {
            this.namespace = namespace;
            this.name = name;
            this.material = material;
            this.defaults = defaults;
            this.requiredMod = requiredMod;
        }

        public ResourceLocation id() {
            return new ResourceLocation(SpellbladeNext.MOD_ID, name);
        }

        public Entry attribute(ItemConfig.SpellAttribute attribute) {
            defaults.add(attribute);
            return this;
        }

        public Entry requires(String modName) {
            this.requiredMod = modName;
            return this;
        }

        public boolean isRequiredModInstalled() {
            if (requiredMod == null || requiredMod.isEmpty()) {
                return true;
            }
            return Platform.isModLoaded(requiredMod);
        }

        public String name() {
            return name;
        }

        public Material material() {
            return material;
        }


        public ItemConfig.Weapon defaults() {
            return defaults;
        }

        public @Nullable String requiredMod() {
            return requiredMod;
        }
    }

    public static final ArrayList<Entry> entries = new ArrayList<>();
    private static Entry entry(String requiredMod, String name, Material material, ItemConfig.Weapon defaults) {
        var entry = new Entry(SpellbladeNext.MOD_ID, name, material, defaults, null);
        if (entry.isRequiredModInstalled()) {
            entries.add(entry);
        }
        return entry;
    }
    public static final ArrayList<Entry> orbs = new ArrayList<>();

    private static Entry orbs(String requiredMod, String name, Material material, ItemConfig.Weapon defaults) {
        var entry = new Entry(SpellbladeNext.MOD_ID, name, material, defaults, null);
        if (entry.isRequiredModInstalled()) {
            orbs.add(entry);
        }
        return entry;
    }
    public static final ArrayList<Entry> claymores = new ArrayList<>();

    private static Entry claymores(String requiredMod, String name, Material material, ItemConfig.Weapon defaults) {
        var entry = new Entry(SpellbladeNext.MOD_ID, name, material, defaults, null);
        if (entry.isRequiredModInstalled()) {
            claymores.add(entry);
        }
        return entry;
    }

    // MARK: Material

    public static class Material implements Tier {
        public static Material matching(Tier vanillaMaterial, Supplier repairIngredient) {
            var material = new Material();
            material.miningLevel = vanillaMaterial.getLevel();
            material.durability = vanillaMaterial.getUses();
            material.miningSpeed = vanillaMaterial.getSpeed();
            material.enchantability = vanillaMaterial.getEnchantmentValue();
            material.ingredient = Items.IRON_INGOT;
            return material;
        }

        private int miningLevel = 0;
        private int durability = 0;
        private float miningSpeed = 0;
        private int enchantability = 0;
        private Item ingredient = null;

        @Override
        public int getUses() {
            return durability;
        }

        @Override
        public float getSpeed() {
            return miningSpeed;
        }

        @Override
        public float getAttackDamageBonus() {
            return 0;
        }

        @Override
        public int getLevel() {
            return miningLevel;
        }

        @Override
        public int getEnchantmentValue() {
            return enchantability;
        }

        @Override
        public Ingredient getRepairIngredient() {
            return Ingredient.of(ingredient);
        }
    }

    // MARK: Wands

    private static final float wandAttackDamage = 2;
    private static final float wandAttackSpeed = -2.4F;


 /*   public static final Entry noviceWand = wand("wand_novice",
            Material.matching(Tiers.WOOD, () -> Ingredient.of(Items.STICK)))
            .add(ItemConfig.SpellAttribute.bonus(Attributes.POWER.get(MagicSchool.FIRE), 1));
    public static final Entry arcaneWand = wand("wand_arcane",
            Material.matching(Tiers.IRON, () -> Ingredient.of(Items.GOLD_INGOT)))
            .add(ItemConfig.SpellAttribute.bonus(Attributes.POWER.get(MagicSchool.ARCANE), 2));
    public static final Entry fireWand = wand("wand_fire",
            Material.matching(Tiers.IRON, () -> Ingredient.of(Items.GOLD_INGOT)))
            .add(ItemConfig.SpellAttribute.bonus(Attributes.POWER.get(MagicSchool.FIRE), 2));
    public static final Entry frostWand = wand("wand_frost",
            Material.matching(Tiers.IRON, () -> Ingredient.of(Items.IRON_INGOT)))
            .add(ItemConfig.SpellAttribute.bonus(Attributes.POWER.get(MagicSchool.FROST), 2));
*/
    // MARK: Staves

    private static final float staffAttackDamage = 2;
    private static final float staffAttackSpeed = -2.4F;
    private static Entry entry(String name, Material material, ItemConfig.Weapon defaults) {
        return entry(null, name, material, defaults);
    }
    private static Entry orb(String name, Material material, ItemConfig.Weapon defaults) {
        return orbs(null, name, material, defaults);
    }
    private static Entry claymore(String name, Material material, ItemConfig.Weapon defaults) {
        return claymores(null, name, material, defaults);
    }
    private static Entry sword(String name, Material material) {
        var settings = new Item.Properties().tab(SpellbladeNext.EXAMPLE_TAB);
        //var item = new StaffItem(material, settings);
        return entry(name, material, new ItemConfig.Weapon(wandAttackDamage, wandAttackSpeed));
    }
    private static Entry orb(String name, Material material) {
        var settings = new Item.Properties().tab(SpellbladeNext.EXAMPLE_TAB);
        //var item = new StaffItem(material, settings);
        return orb(name, material, new ItemConfig.Weapon(wandAttackDamage, wandAttackSpeed));
    }
    private static Entry claymore(String name, Material material) {
        var settings = new Item.Properties().tab(SpellbladeNext.EXAMPLE_TAB);
        //var item = new StaffItem(material, settings);
        return claymore(name, material, new ItemConfig.Weapon(wandAttackDamage, wandAttackSpeed));
    }

    public static final Entry arcaneBlade = sword("blade_arcane",
            Material.matching(Tiers.IRON, () -> Ingredient.of(Items.GOLD_INGOT)))
            .attribute(ItemConfig.SpellAttribute.bonus(SpellAttributes.POWER.get(MagicSchool.ARCANE), 2));
    public static final Entry fireBlade = sword("blade_fire",
            Material.matching(Tiers.IRON, () -> Ingredient.of(Items.COPPER_INGOT)))
            .attribute(ItemConfig.SpellAttribute.bonus(SpellAttributes.POWER.get(MagicSchool.FIRE), 2));
    public static final Entry frostBlade = sword("blade_frost",
            Material.matching(Tiers.IRON, () -> Ingredient.of(Items.IRON_INGOT)))
            .attribute(ItemConfig.SpellAttribute.bonus(SpellAttributes.POWER.get(MagicSchool.FROST), 2));
    public static final Entry arcaneClaymore = claymore("claymore_arcane",
            Material.matching(Tiers.IRON, () -> Ingredient.of(Items.GOLD_INGOT)))
            .attribute(ItemConfig.SpellAttribute.bonus(SpellAttributes.POWER.get(MagicSchool.ARCANE), 2));
    public static final Entry fireClaymore = claymore("claymore_fire",
            Material.matching(Tiers.IRON, () -> Ingredient.of(Items.COPPER_INGOT)))
            .attribute(ItemConfig.SpellAttribute.bonus(SpellAttributes.POWER.get(MagicSchool.FIRE), 2));
    public static final Entry frostClaymore = claymore("claymore_frost",
            Material.matching(Tiers.IRON, () -> Ingredient.of(Items.IRON_INGOT)))
            .attribute(ItemConfig.SpellAttribute.bonus(SpellAttributes.POWER.get(MagicSchool.FROST), 2));
    public static final Entry arcaneOrb = orb("orb_arcane",
            Material.matching(Tiers.IRON, () -> Ingredient.of(SpellbladeNext.RUNEGLINTPLATING.get())))
            .attribute(ItemConfig.SpellAttribute.bonus(SpellAttributes.POWER.get(MagicSchool.ARCANE), 4));
    public static final Entry fireOrb = orb("orb_fire",
            Material.matching(Tiers.IRON, () -> Ingredient.of(SpellbladeNext.RUNEBLAZEPLATING.get())))
            .attribute(ItemConfig.SpellAttribute.bonus(SpellAttributes.POWER.get(MagicSchool.FIRE), 4));
    public static final Entry frostOrb = orb("orb_frost",
            Material.matching(Tiers.IRON, () -> Ingredient.of(SpellbladeNext.RUNEFROSTPLATING.get())))
            .attribute(ItemConfig.SpellAttribute.bonus(SpellAttributes.POWER.get(MagicSchool.FROST), 4));
    public static final StaffItem spellblade = new StaffItem(Tiers.DIAMOND, new Item.Properties());
    public static final StaffItem dummyfrost = new StaffItem(Tiers.DIAMOND, new Item.Properties());
    public static final StaffItem dummyfrost2 = new StaffItem(Tiers.DIAMOND, new Item.Properties());
    public static final StaffItem dummyfrost3 = new StaffItem(Tiers.DIAMOND, new Item.Properties());

    // MARK: Register

    public static void register(Map<String, ItemConfig.Weapon> configs) {
        for(var entry: entries) {
            var config = configs.get(entry.name);
            if (config == null) {
                config = entry.defaults;
                configs.put(entry.name(), config);
            };

            //entry.defaults().spell_attributes.removeIf(asdf -> Arrays.stream(MagicSchool.values()).noneMatch(asdf2 -> asdf2.toString().toLowerCase().equals(asdf.name)));
            var settings = new Item.Properties().tab(SpellbladeNext.EXAMPLE_TAB);
            var item = new Spellblade(entry.material,attributesFrom(config),new Item.Properties().tab(SpellbladeNext.EXAMPLE_TAB).durability(650),
            config.spell_attributes);
            ((ConfigurableAttributes)item).setAttributes(attributesFrom(config));
            Registry.register(Registry.ITEM, entry.id(), item);
        }

        for(var entry: orbs) {
            var config = configs.get(entry.name);
            if (config == null) {
                config = entry.defaults;
                configs.put(entry.name(), config);
            };

            //entry.defaults().spell_attributes.removeIf(asdf -> Arrays.stream(MagicSchool.values()).noneMatch(asdf2 -> asdf2.toString().toLowerCase().equals(asdf.name)));
            var settings = new Item.Properties().tab(SpellbladeNext.EXAMPLE_TAB);
            var item = new StaffItem(entry.material,new Item.Properties().tab(SpellbladeNext.EXAMPLE_TAB).durability(650));
            ((ConfigurableAttributes)item).setAttributes(attributesFrom(config));
            Registry.register(Registry.ITEM, entry.id(), item);
        }
        for(var entry: claymores) {
            var config = configs.get(entry.name);
            if (config == null) {
                config = entry.defaults;
                configs.put(entry.name(), config);
            };

            //entry.defaults().spell_attributes.removeIf(asdf -> Arrays.stream(MagicSchool.values()).noneMatch(asdf2 -> asdf2.toString().toLowerCase().equals(asdf.name)));
            var settings = new Item.Properties().tab(SpellbladeNext.EXAMPLE_TAB);
            var item = new Claymores(entry.material,attributesFrom(config),new Item.Properties().tab(SpellbladeNext.EXAMPLE_TAB).durability(800),
                    config.spell_attributes);
            ((ConfigurableAttributes)item).setAttributes(claymoreAttributes(config));
            Registry.register(Registry.ITEM, entry.id(), item);
        }
        Registry.register(Registry.ITEM, new ResourceLocation(SpellbladeNext.MOD_ID, "spellblade"), spellblade);
        Registry.register(Registry.ITEM, new ResourceLocation(SpellbladeNext.MOD_ID, "icicle"), dummyfrost);
        Registry.register(Registry.ITEM, new ResourceLocation(SpellbladeNext.MOD_ID, "icicle2"), dummyfrost2);
        Registry.register(Registry.ITEM, new ResourceLocation(SpellbladeNext.MOD_ID, "frostblade"), dummyfrost3);
    }

    private static ImmutableMultimap<Attribute, AttributeModifier> attributesFrom(ItemConfig.Weapon config) {
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE,
                new AttributeModifier(
                        ItemAccessor.ATTACK_DAMAGE_MODIFIER_ID(),
                        "Weapon modifier",
                        (double)config.attack_damage,
                        AttributeModifier.Operation.ADDITION));
        builder.put(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_SPEED,
                new AttributeModifier(
                        ItemAccessor.ATTACK_SPEED_MODIFIER_ID(),
                        "Weapon modifier",
                        config.attack_speed,
                        AttributeModifier.Operation.ADDITION));
        for(var attribute: config.spell_attributes) {
            try {
                var entityAttribute = SpellAttributes.all.get(attribute.name).attribute;
                builder.put(entityAttribute,
                        new AttributeModifier(
                                entityAttribute.weaponUUID,
                                "Weapon modifier",
                                attribute.value,
                                attribute.operation));
            } catch (Exception e) {
                System.err.println("Failed to add item attribute modifier: " + e.getMessage());
            }
        }
        return builder.build();
    }
    private static ImmutableMultimap<Attribute, AttributeModifier> claymoreAttributes(ItemConfig.Weapon config) {
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE,
                new AttributeModifier(
                        ItemAccessor.ATTACK_DAMAGE_MODIFIER_ID(),
                        "Weapon modifier",
                        (double)2,
                        AttributeModifier.Operation.ADDITION));
        builder.put(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_SPEED,
                new AttributeModifier(
                        ItemAccessor.ATTACK_SPEED_MODIFIER_ID(),
                        "Weapon modifier",
                        -3,
                        AttributeModifier.Operation.ADDITION));
        for(var attribute: config.spell_attributes) {
            try {
                var entityAttribute = SpellAttributes.all.get(attribute.name).attribute;
                builder.put(entityAttribute,
                        new AttributeModifier(
                                entityAttribute.weaponUUID,
                                "Weapon modifier",
                                attribute.value,
                                attribute.operation));
            } catch (Exception e) {
                System.err.println("Failed to add item attribute modifier: " + e.getMessage());
            }
        }
        return builder.build();
    }

    static abstract class ItemAccessor extends Item {
        public ItemAccessor(Item.Properties settings) { super(settings); }
        public static UUID ATTACK_DAMAGE_MODIFIER_ID() { return BASE_ATTACK_DAMAGE_UUID; }
        public static UUID ATTACK_SPEED_MODIFIER_ID() { return BASE_ATTACK_SPEED_UUID; }
    }
}
