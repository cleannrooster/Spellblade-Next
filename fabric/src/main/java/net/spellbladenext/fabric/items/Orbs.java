package net.spellbladenext.fabric.items;

import com.google.common.collect.ImmutableMultimap;
import dev.architectury.platform.Platform;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.crafting.Ingredient;
import net.spell_engine.api.item.ConfigurableAttributes;

import net.spell_power.api.MagicSchool;
import net.spell_power.api.attributes.SpellAttributes;
import net.spellbladenext.SpellbladeNext;
import net.spellbladenext.fabric.config.ItemConfig;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

public class Orbs {

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

    public static final ArrayList<Entry> orbs = new ArrayList<>();

    private static Entry orbs(String requiredMod, String name, Material material, ItemConfig.Weapon defaults,MagicSchool magicSchool) {
        var config = defaults;

        Item orb = new Orb(material,new Item.Properties().tab(SpellbladeNext.EXAMPLE_TAB).durability(650),magicSchool);
        var entry = new Entry(SpellbladeNext.MOD_ID, name, material,orb, defaults, null);
        if (entry.isRequiredModInstalled()) {
            orbs.add(entry);
        }
        return entry;
    }
    public static final ArrayList<Entry> claymores = new ArrayList<>();


    // MARK: Material

    public static class Material implements Tier {
        public static Material matching(Tier vanillaMaterial, Item repairIngredient) {
            var material = new Material();
            material.miningLevel = vanillaMaterial.getLevel();
            material.durability = vanillaMaterial.getUses();
            material.miningSpeed = vanillaMaterial.getSpeed();
            material.enchantability = vanillaMaterial.getEnchantmentValue();
            material.ingredient = repairIngredient;
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
    private static Entry orb(String name, Material material, ItemConfig.Weapon defaults, MagicSchool magicSchool) {
        return orbs(null, name, material, defaults, magicSchool);
    }
    private static Entry orb(String name, Material material, MagicSchool magicSchool) {
        var settings = new Item.Properties().tab(SpellbladeNext.EXAMPLE_TAB);
        //var item = new StaffItem(material, settings);
        return orb(name, material, new ItemConfig.Weapon(wandAttackDamage, wandAttackSpeed),magicSchool);
    }

    public static final Entry arcaneOrb = orb("orb_arcane",
            Material.matching(Tiers.GOLD, SpellbladeNext.RUNEGLINTPLATING.get()),MagicSchool.ARCANE)
            .attribute(ItemConfig.SpellAttribute.bonus(SpellAttributes.POWER.get(MagicSchool.ARCANE), 4));
    public static final Entry fireOrb = orb("orb_fire",
            Material.matching(Tiers.GOLD, SpellbladeNext.RUNEBLAZEPLATING.get()),MagicSchool.FIRE)
            .attribute(ItemConfig.SpellAttribute.bonus(SpellAttributes.POWER.get(MagicSchool.FIRE), 4));
    public static final Entry frostOrb = orb("orb_frost",
            Material.matching(Tiers.IRON, SpellbladeNext.RUNEFROSTPLATING.get()),MagicSchool.FROST)
            .attribute(ItemConfig.SpellAttribute.bonus(SpellAttributes.POWER.get(MagicSchool.FROST), 4));

    // MARK: Register

    public static void register(Map<String, ItemConfig.Weapon> configs) {


        for(var entry: orbs) {
            var config = configs.get(entry.name);
            if (config == null) {
                config = entry.defaults;
                configs.put(entry.name(), config);
            }
            ;

            //entry.defaults().spell_attributes.removeIf(asdf -> Arrays.stream(MagicSchool.values()).noneMatch(asdf2 -> asdf2.toString().toLowerCase().equals(asdf.name)));
            var settings = new Item.Properties().tab(SpellbladeNext.EXAMPLE_TAB);
            var item = entry.item();
            ((ConfigurableAttributes) item).setAttributes(attributesFrom(config));
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
        public ItemAccessor(Properties settings) { super(settings); }
        public static UUID ATTACK_DAMAGE_MODIFIER_ID() { return BASE_ATTACK_DAMAGE_UUID; }
        public static UUID ATTACK_SPEED_MODIFIER_ID() { return BASE_ATTACK_SPEED_UUID; }
    }
}
