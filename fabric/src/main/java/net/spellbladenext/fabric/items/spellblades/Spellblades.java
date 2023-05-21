package net.spellbladenext.fabric.items.spellblades;

import com.google.common.collect.ImmutableMultimap;
import dev.architectury.platform.Platform;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.spell_engine.api.item.ConfigurableAttributes;

import net.spell_engine.api.item.weapon.StaffItem;
import net.spell_power.api.MagicSchool;
import net.spell_power.api.attributes.SpellAttributes;
import net.spellbladenext.SpellbladeNext;
import net.spellbladenext.fabric.config.ItemConfig;
import net.spellbladenext.fabric.items.HolySymbol;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

import static net.spellbladenext.fabric.SpellbladesFabric.itemConfig;


public class Spellblades {


    public static final class Entry {
        private final String namespace;
        private final String name;
        private final Material material;
        private final ItemConfig.Weapon defaults;
        private final Item item;
        private @Nullable String requiredMod;


        public Entry(String namespace, String name, Material material, Item item, ItemConfig.Weapon defaults, @Nullable String requiredMod) {
            this.namespace = namespace;
            this.name = name;
            this.material = material;
            this.defaults = defaults;
            this.requiredMod = requiredMod;
            this.item = item;
        }
        public Item item(){
            return item;
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


    public static final ArrayList<Entry> runedaggers = new ArrayList<>();
    public static final ArrayList<Entry> starforge = new ArrayList<>();
    public static final ArrayList<Entry> mulberry = new ArrayList<>();
    private static Entry entry(String requiredMod, String name, Material material, ItemConfig.Weapon defaults) {
        var config = defaults;

        Item spellblade = new Spellblade(material,attributesFrom(config),new Item.Properties().tab(SpellbladeNext.EXAMPLE_TAB).durability(650),
                config.spell_attributes);
        var entry = new Entry(SpellbladeNext.MOD_ID, name, material, spellblade, defaults, null);
        if (entry.isRequiredModInstalled()) {
            entries.add(entry);
        }
        return entry;
    }
    private static Entry runedaggers(String requiredMod, String name, Material material, ItemConfig.Weapon defaults) {
        var config = defaults;

        Item spellblade = new RuneDagger(material,attributesFrom(config),new Item.Properties().tab(SpellbladeNext.EXAMPLE_TAB).durability(650),
                config.spell_attributes);
        var entry = new Entry(SpellbladeNext.MOD_ID, name, material, spellblade, defaults, null);
        if (entry.isRequiredModInstalled()) {
            runedaggers.add(entry);
        }
        return entry;
    }
    private static Entry starforge(String requiredMod, String name, Material material, ItemConfig.Weapon defaults) {
        var config = defaults;

        Item spellblade = new Starforge(material,attributesFrom(config),new Item.Properties().tab(SpellbladeNext.EXAMPLE_TAB).durability(1650),
                config.spell_attributes);
        var entry = new Entry(SpellbladeNext.MOD_ID, name, material, spellblade, defaults, null);
        if (entry.isRequiredModInstalled()) {
            starforge.add(entry);
        }
        return entry;
    }
    public static final ArrayList<Entry> orbs = new ArrayList<>();

    private static Entry orbs(String requiredMod, String name, Material material, ItemConfig.Weapon defaults) {
        var config = defaults;

        Item orb = new StaffItem(material,new Item.Properties().tab(SpellbladeNext.EXAMPLE_TAB).durability(650));
        var entry = new Entry(SpellbladeNext.MOD_ID, name, material,orb, defaults, null);
        if (entry.isRequiredModInstalled()) {
            orbs.add(entry);
        }
        return entry;
    }
    public static final ArrayList<Entry> claymores = new ArrayList<>();

    private static Entry claymores(String requiredMod, String name, Material material, ItemConfig.Weapon defaults) {
        var config = defaults;
        Item claymore = new Claymores(material,attributesFrom(config),new Item.Properties().tab(SpellbladeNext.EXAMPLE_TAB).durability(800),
                config.spell_attributes);
        var entry = new Entry(SpellbladeNext.MOD_ID, name, material, claymore, defaults, null);
        if (entry.isRequiredModInstalled()) {
            claymores.add(entry);
        }
        return entry;
    }
    private static Entry mulberry(String requiredMod, String name, Material material, ItemConfig.Weapon defaults) {
        var config = defaults;
        Item claymore = new Claymores(material,attributesFrom(config),new Item.Properties().tab(SpellbladeNext.EXAMPLE_TAB).durability(800),
                config.spell_attributes);
        var entry = new Entry(SpellbladeNext.MOD_ID, name, material, claymore, defaults, null);
        if (entry.isRequiredModInstalled()) {
            mulberry.add(entry);
        }
        return entry;
    }

    // MARK: Material

    public static class Material implements Tier {
        public static Material matching(Tier vanillaMaterial, Item repairIngredient) {
            var material = new Material();
            material.miningLevel = vanillaMaterial.getLevel();
            material.durability = vanillaMaterial.getUses();
            material.miningSpeed = vanillaMaterial.getSpeed();
            material.enchantability = vanillaMaterial.getEnchantmentValue();
            material.ingredient = (Item) repairIngredient;
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


    private static final float wandAttackDamage = 2;
    private static final float wandAttackSpeed = -2.4F;


    private static Entry entry(String name, Material material, ItemConfig.Weapon defaults) {
        return entry(null, name, material, defaults);
    }
    private static Entry orb(String name, Material material, ItemConfig.Weapon defaults) {
        return orbs(null, name, material, defaults);
    }
    private static Entry claymore(String name, Material material, ItemConfig.Weapon defaults) {
        return claymores(null, name, material, defaults);
    }
    private static Entry mulberry(String name, Material material, ItemConfig.Weapon defaults) {
        return mulberry(null, name, material, defaults);
    }
    private static Entry sword(String name, Material material) {
        var settings = new Item.Properties().tab(SpellbladeNext.EXAMPLE_TAB);
        //var item = new StaffItem(material, settings);
        return entry(name, material, new ItemConfig.Weapon(wandAttackDamage, wandAttackSpeed));
    }
    private static Entry runedagger(String name, Material material) {
        var settings = new Item.Properties().tab(SpellbladeNext.EXAMPLE_TAB);
        //var item = new StaffItem(material, settings);
        return runedaggers(null, name,material, new ItemConfig.Weapon(3, -2));
    }
    private static Entry starforge(String name, Material material) {
        var settings = new Item.Properties().tab(SpellbladeNext.EXAMPLE_TAB);
        //var item = new StaffItem(material, settings);
        return starforge(null, name,material, new ItemConfig.Weapon(3, -2));
    }
    private static Entry orb(String name, Material material) {
        var settings = new Item.Properties().tab(SpellbladeNext.EXAMPLE_TAB);
        //var item = new StaffItem(material, settings);
        return orb(name, material, new ItemConfig.Weapon(wandAttackDamage, wandAttackSpeed));
    }
    private static Entry claymore(String name, Material material) {
        var settings = new Item.Properties().tab(SpellbladeNext.EXAMPLE_TAB);
        //var item = new StaffItem(material, settings);
        return claymore(name, material, new ItemConfig.Weapon(3, -3F));
    }
    private static Entry mulberry(String name, Material material) {
        var settings = new Item.Properties().tab(SpellbladeNext.EXAMPLE_TAB);
        //var item = new StaffItem(material, settings);
        return mulberry(name, material, new ItemConfig.Weapon(3, -3F));
    }

    public static final Entry arcaneBlade = sword("blade_arcane",
            Material.matching(Tiers.GOLD, SpellbladeNext.RUNEGLINTPLATING.get()))
            .attribute(ItemConfig.SpellAttribute.bonus(SpellAttributes.POWER.get(MagicSchool.ARCANE), 2));
    public static final Entry fireBlade = sword("blade_fire",
            Material.matching(Tiers.GOLD, SpellbladeNext.RUNEBLAZEPLATING.get()))
            .attribute(ItemConfig.SpellAttribute.bonus(SpellAttributes.POWER.get(MagicSchool.FIRE), 2));
    public static final Entry frostBlade = sword("blade_frost",
            Material.matching(Tiers.IRON, SpellbladeNext.RUNEFROSTPLATING.get()))
            .attribute(ItemConfig.SpellAttribute.bonus(SpellAttributes.POWER.get(MagicSchool.FROST), 2));
    public static final Entry runedagger = runedagger("rune_dagger",
            Material.matching(Tiers.IRON, Items.IRON_INGOT)).attribute(ItemConfig.SpellAttribute.bonus(SpellAttributes.POWER.get(MagicSchool.ARCANE), 4)).attribute(ItemConfig.SpellAttribute.bonus(SpellAttributes.POWER.get(MagicSchool.FROST), 4)).attribute(ItemConfig.SpellAttribute.bonus(SpellAttributes.POWER.get(MagicSchool.FIRE), 4));
    public static final Entry starforge1 = starforge("starforge",
            Material.matching(Tiers.NETHERITE, Items.NETHER_STAR)).attribute(ItemConfig.SpellAttribute.bonus(SpellAttributes.POWER.get(MagicSchool.ARCANE), 3)).attribute(ItemConfig.SpellAttribute.bonus(SpellAttributes.POWER.get(MagicSchool.FROST), 3)).attribute(ItemConfig.SpellAttribute.bonus(SpellAttributes.POWER.get(MagicSchool.FIRE), 3));

    public static final Entry arcaneClaymore = claymore("claymore_arcane",
            Material.matching(Tiers.GOLD, SpellbladeNext.RUNEGLINTPLATING.get()))
            .attribute(ItemConfig.SpellAttribute.bonus(SpellAttributes.POWER.get(MagicSchool.ARCANE), 3));
    public static final Entry fireClaymore = claymore("claymore_fire",
            Material.matching(Tiers.GOLD, SpellbladeNext.RUNEBLAZEPLATING.get()))
            .attribute(ItemConfig.SpellAttribute.bonus(SpellAttributes.POWER.get(MagicSchool.FIRE), 3));
    public static final Entry frostClaymore = claymore("claymore_frost",
            Material.matching(Tiers.IRON, SpellbladeNext.RUNEFROSTPLATING.get()))
            .attribute(ItemConfig.SpellAttribute.bonus(SpellAttributes.POWER.get(MagicSchool.FROST), 3));
    public static ItemConfig.Weapon frost = new ItemConfig.Weapon(4,-3);
    public static ItemConfig.Weapon holy = new ItemConfig.Weapon(0,-2.4F);

    static {
        frost.add(ItemConfig.SpellAttribute.bonus(SpellAttributes.POWER.get(MagicSchool.FROST), 4));
        holy.add(ItemConfig.SpellAttribute.multiply(SpellAttributes.POWER.get(MagicSchool.HEALING), 1));


    }
    public static Item frostGavel = new Spellblade(Spellblades.Material.matching(Tiers.IRON, (SpellbladeNext.RUNEFROSTPLATING.get())),attributesFrom(frost),
            new Item.Properties().tab(SpellbladeNext.EXAMPLE_TAB).durability(800),frost.spell_attributes);
    private static final Item HOLYSYMBOL =  new HolySymbol(new Item.Properties().tab(SpellbladeNext.EXAMPLE_TAB).durability(1800));;


    public static final Entry mulberrysword = mulberry("mulberry",
Material.matching(Tiers.IRON, null))
            .attribute(ItemConfig.SpellAttribute.multiply(SpellAttributes.POWER.get(MagicSchool.FIRE),1));
/*

    public static final StaffItem spellblade = new StaffItem(Tiers.DIAMOND, new Item.Properties());
    public static final StaffItem dummyfrost = new StaffItem(Tiers.DIAMOND, new Item.Properties());
    public static final StaffItem dummyfrost2 = new StaffItem(Tiers.DIAMOND, new Item.Properties());
    public static final StaffItem dummyfrost3 = new StaffItem(Tiers.DIAMOND, new Item.Properties());
*/

    // MARK: Register
    public static void register(Map<String, ItemConfig.Weapon> configs) {
        for(var entry: entries) {
            var config = configs.get(entry.name);
            if (config == null) {
                config = entry.defaults;
                configs.put(entry.name(), config);
            };
            var item = entry.item;

            ((ConfigurableAttributes)item).setAttributes(attributesFrom(config));

            Registry.register(Registry.ITEM, entry.id(), item);
            //Registry.register(Registry.ITEM, entry.id(), item);

        }
        var config2 = configs.get("frostborn_gavel");
        if (config2 == null) {
            config2 = frost;
            configs.put("frostborn_gavel", config2);
        };
        var config3 = configs.get("holysymbol");
        if (config3 == null) {
            config3 = holy;
            configs.put("holysymbol", config3);
        };
        ((ConfigurableAttributes)frostGavel).setAttributes(attributesFrom(config2));
        Registry.register(Registry.ITEM,new ResourceLocation(SpellbladeNext.MOD_ID, "frostborn_gavel"),frostGavel);
        ((ConfigurableAttributes)HOLYSYMBOL).setAttributes(attributesFrom(config3));
        Registry.register(Registry.ITEM,new ResourceLocation(SpellbladeNext.MOD_ID, "holysymbol"),HOLYSYMBOL);

        for(var entry: claymores) {
            var config = configs.get(entry.name);
            if (config == null) {
                config = entry.defaults;
                configs.put(entry.name(), config);
            };

            var item = entry.item();
            ((ConfigurableAttributes)item).setAttributes(attributesFrom(config));
            Registry.register(Registry.ITEM, entry.id(), item);
        }
        for(var entry: runedaggers) {
            var config = configs.get(entry.name);
            if (config == null) {
                config = entry.defaults;
                configs.put(entry.name(), config);
            };

            var item = entry.item;

            ((ConfigurableAttributes)item).setAttributes(attributesFrom(config));
            Registry.register(Registry.ITEM, entry.id(), item);
        }
        for(var entry: starforge) {
            var config = configs.get(entry.name);
            if (config == null) {
                config = entry.defaults;
                configs.put(entry.name(), config);
            };

            var item = entry.item;

            ((ConfigurableAttributes)item).setAttributes(attributesFrom(config));
            Registry.register(Registry.ITEM, entry.id(), item);
        }
        for(var entry: mulberry) {
            var config = configs.get(entry.name);
            if (config == null) {
                config = entry.defaults;
                configs.put(entry.name(), config);
            };

            var item = entry.item;

            ((ConfigurableAttributes)item).setAttributes(attributesFrom(config));
            Registry.register(Registry.ITEM, entry.id(), item);
        }
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
    private static ImmutableMultimap<Attribute, AttributeModifier> attributesFromDagger(ItemConfig.Weapon config) {
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE,
                new AttributeModifier(
                        ItemAccessor.ATTACK_DAMAGE_MODIFIER_ID(),
                        "Weapon modifier",
                        (double)3,
                        AttributeModifier.Operation.ADDITION));
        builder.put(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_SPEED,
                new AttributeModifier(
                        ItemAccessor.ATTACK_SPEED_MODIFIER_ID(),
                        "Weapon modifier",
                        -2,
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
                        (double)3,
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
