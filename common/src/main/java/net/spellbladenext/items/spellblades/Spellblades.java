package net.spellbladenext.items.spellblades;

import com.google.common.collect.ImmutableMultimap;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.spell_engine.SpellEngineMod;
import net.spell_engine.api.item.StaffItem;
import net.spell_power.SpellPowerMod;
import net.spell_power.api.MagicSchool;
import net.spell_power.api.attributes.Attributes;
import net.spellbladenext.SpellbladeNext;
import net.spellbladenext.config.ItemConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

public class Spellblades {

    public record Entry(String name, Material material, ItemConfig.Weapon defaults) {
        public ResourceLocation id() {
            return new ResourceLocation(SpellbladeNext.MOD_ID, name);
        }
        public Entry add(ItemConfig.SpellAttribute attribute) {
            defaults.add(attribute);
            return this;
        }
    }

    public static final ArrayList<Entry> entries = new ArrayList<>();
    private static Entry entry(String name, Material material, ItemConfig.Weapon defaults) {
        var entry = new Entry(name, material, defaults);
        entries.add(entry);
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
    private static Entry wand(String name, Material material) {
        return entry(name, material, new ItemConfig.Weapon(wandAttackDamage, wandAttackSpeed));
    }

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
    private static Entry sword(String name, Material material) {
        return entry(name, material, new ItemConfig.Weapon(staffAttackDamage, staffAttackSpeed));
    }

    public static final Entry arcaneBlade = sword("blade_arcane",
            Material.matching(Tiers.IRON, () -> Ingredient.of(Items.GOLD_INGOT)))
            .add(ItemConfig.SpellAttribute.bonus(Attributes.POWER.get(MagicSchool.ARCANE), 2));
    public static final Entry fireBlade = sword("blade_fire",
            Material.matching(Tiers.IRON, () -> Ingredient.of(Items.GOLD_INGOT)))
            .add(ItemConfig.SpellAttribute.bonus(Attributes.POWER.get(MagicSchool.FIRE), 2));
    public static final Entry frostBlade = sword("blade_frost",
            Material.matching(Tiers.IRON, () -> Ingredient.of(Items.IRON_INGOT)))
            .add(ItemConfig.SpellAttribute.bonus(Attributes.POWER.get(MagicSchool.FROST), 2));
    public static final StaffItem spellblade = new StaffItem(Tiers.DIAMOND, new Item.Properties());
    public static final StaffItem dummyfrost = new StaffItem(Tiers.DIAMOND, new Item.Properties());
    public static final StaffItem dummyfrost2 = new StaffItem(Tiers.DIAMOND, new Item.Properties());

    // MARK: Register

    public static void register(Map<String, ItemConfig.Weapon> configs) {
        for(var entry: entries) {
            var config = configs.get(entry.name);
            if (config == null) {
                config = entry.defaults;
                configs.put(entry.name(), config);
            };

            entry.defaults().spell_attributes.removeIf(asdf -> Arrays.stream(MagicSchool.values()).noneMatch(asdf2 -> asdf2.toString().toLowerCase().equals(asdf.name)));
            var settings = new Item.Properties().tab(SpellbladeNext.EXAMPLE_TAB);
            var item = new Spellblade(entry.material(), attributesFrom(config), settings.durability(650), entry.defaults.spell_attributes);
            Registry.register(Registry.ITEM, entry.id(), item);
        }
        Registry.register(Registry.ITEM, new ResourceLocation(SpellbladeNext.MOD_ID, "spellblade"), spellblade);
        Registry.register(Registry.ITEM, new ResourceLocation(SpellbladeNext.MOD_ID, "icicle"), dummyfrost);
        Registry.register(Registry.ITEM, new ResourceLocation(SpellbladeNext.MOD_ID, "icicle2"), dummyfrost2);


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
                var entityAttribute = Attributes.all.get(attribute.name).attribute;
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
