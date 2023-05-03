package net.spellbladenext.fabric.items.armors;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.spell_engine.api.item.ItemConfig;
import net.spell_engine.api.item.armor.Armor;
import net.spell_power.api.MagicSchool;
import net.spell_power.api.attributes.SpellAttributes;
import net.spellbladenext.SpellbladeNext;
import net.spellbladenext.items.ModArmorMaterials;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static net.spellbladenext.SpellbladeNext.*;

public class Armors {
    private static final Supplier<Ingredient> WOOL_INGREDIENTS = () -> { return Ingredient.of(
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

    public static final ArrayList<Armor.Entry> entries = new ArrayList<>();
    private static Armor.Entry create(Armor.CustomMaterial material, ItemConfig.ArmorSet defaults) {
        return new Armor.Entry(material, null, defaults);
    }



    private static final float specializedRobeSpellPower = 0.25F;
    private static final float specializedRobeCritDamage = 0.1F;
    private static final float specializedRobeCritChance = 0.02F;
    private static final float specializedRobeHaste = 0.03F;

    public static final Armor.Set runegleaming =
            create(
                    new Armor.CustomMaterial(
                            "runegleaming",
                            20,
                            10,
                            SoundEvents.ARMOR_EQUIP_CHAIN,
                            () -> Ingredient.of(RUNEGLINTPLATING.get())
                    ),
                    ItemConfig.ArmorSet.with(
                            new ItemConfig.ArmorSet.Piece(1)
                                    .addAll(List.of(
                                            ItemConfig.SpellAttribute.multiply(SpellAttributes.POWER.get(MagicSchool.ARCANE), specializedRobeSpellPower)
                                    )),
                            new ItemConfig.ArmorSet.Piece(3)
                                    .addAll(List.of(
                                            ItemConfig.SpellAttribute.multiply(SpellAttributes.POWER.get(MagicSchool.ARCANE), specializedRobeSpellPower)
                                    )),
                            new ItemConfig.ArmorSet.Piece(2)
                                    .addAll(List.of(
                                            ItemConfig.SpellAttribute.multiply(SpellAttributes.POWER.get(MagicSchool.ARCANE), specializedRobeSpellPower)
                                    )),
                            new ItemConfig.ArmorSet.Piece(1)
                                    .addAll(List.of(
                                            ItemConfig.SpellAttribute.multiply(SpellAttributes.POWER.get(MagicSchool.ARCANE), specializedRobeSpellPower)
                                    ))
                    ))
                    .armorSet(material -> new Armor.Set(SpellbladeNext.MOD_ID,
                            new RuneblazingArmor(ModArmorMaterials.RUNEGLEAMING, EquipmentSlot.HEAD, new Item.Properties().tab(EXAMPLE_TAB), MagicSchool.FIRE),
                            new RuneblazingArmor(ModArmorMaterials.RUNEGLEAMING, EquipmentSlot.CHEST, new Item.Properties().tab(EXAMPLE_TAB), MagicSchool.FIRE),
                            new RuneblazingArmor(ModArmorMaterials.RUNEGLEAMING, EquipmentSlot.LEGS, new Item.Properties().tab(EXAMPLE_TAB), MagicSchool.FIRE),
                            new RuneblazingArmor(ModArmorMaterials.RUNEGLEAMING, EquipmentSlot.FEET, new Item.Properties().tab(EXAMPLE_TAB), MagicSchool.FIRE)
                    ), entries);

    public static final Armor.Set runeblazing =
            create(
                    new Armor.CustomMaterial(
                            "runeblazing",
                            20,
                            10,
                            SoundEvents.ARMOR_EQUIP_CHAIN,
                            () -> Ingredient.of(RUNEBLAZEPLATING.get())
                    ),
                    ItemConfig.ArmorSet.with(
                            new ItemConfig.ArmorSet.Piece(1)
                                    .addAll(List.of(
                                            ItemConfig.SpellAttribute.multiply(SpellAttributes.POWER.get(MagicSchool.FIRE), specializedRobeSpellPower)
                                    )),
                            new ItemConfig.ArmorSet.Piece(3)
                                    .addAll(List.of(
                                            ItemConfig.SpellAttribute.multiply(SpellAttributes.POWER.get(MagicSchool.FIRE), specializedRobeSpellPower)

                                    )),
                            new ItemConfig.ArmorSet.Piece(2)
                                    .addAll(List.of(
                                            ItemConfig.SpellAttribute.multiply(SpellAttributes.POWER.get(MagicSchool.FIRE), specializedRobeSpellPower)

                                    )),
                            new ItemConfig.ArmorSet.Piece(1)
                                    .addAll(List.of(
                                            ItemConfig.SpellAttribute.multiply(SpellAttributes.POWER.get(MagicSchool.FIRE), specializedRobeSpellPower)

                                    ))
                    ))
                    .armorSet(material -> new Armor.Set(SpellbladeNext.MOD_ID,
                            new RuneblazingArmor(ModArmorMaterials.RUNEBLAZING, EquipmentSlot.HEAD, new Item.Properties().tab(EXAMPLE_TAB), MagicSchool.FIRE),
                                new RuneblazingArmor(ModArmorMaterials.RUNEBLAZING, EquipmentSlot.CHEST, new Item.Properties().tab(EXAMPLE_TAB), MagicSchool.FIRE),
                                new RuneblazingArmor(ModArmorMaterials.RUNEBLAZING, EquipmentSlot.LEGS, new Item.Properties().tab(EXAMPLE_TAB), MagicSchool.FIRE),
                                new RuneblazingArmor(ModArmorMaterials.RUNEBLAZING, EquipmentSlot.FEET, new Item.Properties().tab(EXAMPLE_TAB), MagicSchool.FIRE)
                                                ), entries);

    public static final Armor.Set runefrosted =
            create(
                    new Armor.CustomMaterial(
                            "runefrosted",
                            20,
                            10,
                            SoundEvents.ARMOR_EQUIP_CHAIN,
                            () -> Ingredient.of(RUNEFROSTPLATING.get())
                    ),
                    ItemConfig.ArmorSet.with(
                            new ItemConfig.ArmorSet.Piece(1)
                                    .addAll(List.of(
                                            ItemConfig.SpellAttribute.multiply(SpellAttributes.POWER.get(MagicSchool.FROST), specializedRobeSpellPower)

                                    )),
                            new ItemConfig.ArmorSet.Piece(3)
                                    .addAll(List.of(
                                            ItemConfig.SpellAttribute.multiply(SpellAttributes.POWER.get(MagicSchool.FROST), specializedRobeSpellPower)

                                    )),
                            new ItemConfig.ArmorSet.Piece(2)
                                    .addAll(List.of(
                                            ItemConfig.SpellAttribute.multiply(SpellAttributes.POWER.get(MagicSchool.FROST), specializedRobeSpellPower)

                                    )),
                            new ItemConfig.ArmorSet.Piece(1)
                                    .addAll(List.of(
                                            ItemConfig.SpellAttribute.multiply(SpellAttributes.POWER.get(MagicSchool.FROST), specializedRobeSpellPower)

                                    ))
                    ))
                    .armorSet(material -> new Armor.Set(SpellbladeNext.MOD_ID,
                            new RuneblazingArmor(ModArmorMaterials.RUNEFROSTED, EquipmentSlot.HEAD, new Item.Properties().tab(EXAMPLE_TAB), MagicSchool.FIRE),
                            new RuneblazingArmor(ModArmorMaterials.RUNEFROSTED, EquipmentSlot.CHEST, new Item.Properties().tab(EXAMPLE_TAB), MagicSchool.FIRE),
                            new RuneblazingArmor(ModArmorMaterials.RUNEFROSTED, EquipmentSlot.LEGS, new Item.Properties().tab(EXAMPLE_TAB), MagicSchool.FIRE),
                            new RuneblazingArmor(ModArmorMaterials.RUNEFROSTED, EquipmentSlot.FEET, new Item.Properties().tab(EXAMPLE_TAB), MagicSchool.FIRE)
                    ), entries);
    public static final Armor.Set aetherfire =
            create(
                    new Armor.CustomMaterial(
                            "aetherfire",
                            20,
                            10,
                            SoundEvents.ARMOR_EQUIP_CHAIN,
                            () -> Ingredient.of(RUNEGLINTPLATING.get(),RUNEBLAZEPLATING.get())
                    ),
                    ItemConfig.ArmorSet.with(
                            new ItemConfig.ArmorSet.Piece(2)
                                    .addAll(List.of(
                                            ItemConfig.SpellAttribute.multiply(SpellAttributes.CRITICAL_DAMAGE, 0.4F),
                                            ItemConfig.SpellAttribute.multiply(SpellAttributes.HASTE, 0.12F)


                                    )),
                            new ItemConfig.ArmorSet.Piece(6)
                                    .addAll(List.of(
                                            ItemConfig.SpellAttribute.multiply(SpellAttributes.POWER.get(MagicSchool.FIRE), 0.2F),
                                            ItemConfig.SpellAttribute.multiply(SpellAttributes.POWER.get(MagicSchool.ARCANE), 0.2F)


                                    )),
                            new ItemConfig.ArmorSet.Piece(4)
                                    .addAll(List.of(
                                            ItemConfig.SpellAttribute.multiply(SpellAttributes.POWER.get(MagicSchool.FIRE), 0.2F),
                                            ItemConfig.SpellAttribute.multiply(SpellAttributes.POWER.get(MagicSchool.ARCANE), 0.2F)


                                    )),
                            new ItemConfig.ArmorSet.Piece(2)
                                    .addAll(List.of(
                                            ItemConfig.SpellAttribute.multiply(SpellAttributes.POWER.get(MagicSchool.FIRE), 0.2F),
                                            ItemConfig.SpellAttribute.multiply(SpellAttributes.POWER.get(MagicSchool.ARCANE), 0.2F)


                                    ))
                    ))
                    .armorSet(material -> new Armor.Set(SpellbladeNext.MOD_ID,
                            new InquisitorSet(ModArmorMaterials.AETHERFIRE, EquipmentSlot.HEAD, new Item.Properties().tab(EXAMPLE_TAB), List.of(MagicSchool.FIRE,MagicSchool.ARCANE)),
                            new InquisitorSet(ModArmorMaterials.AETHERFIRE, EquipmentSlot.CHEST, new Item.Properties().tab(EXAMPLE_TAB), List.of(MagicSchool.FIRE,MagicSchool.ARCANE)),
                            new InquisitorSet(ModArmorMaterials.AETHERFIRE, EquipmentSlot.LEGS, new Item.Properties().tab(EXAMPLE_TAB), List.of(MagicSchool.FIRE,MagicSchool.ARCANE)),
                            new InquisitorSet(ModArmorMaterials.AETHERFIRE, EquipmentSlot.FEET, new Item.Properties().tab(EXAMPLE_TAB), List.of(MagicSchool.FIRE,MagicSchool.ARCANE))
                    ), entries);
    public static final Armor.Set rimeblaze =
            create(
                    new Armor.CustomMaterial(
                            "rimeblaze",
                            20,
                            10,
                            SoundEvents.ARMOR_EQUIP_CHAIN,
                            () -> Ingredient.of(RUNEFROSTPLATING.get(),RUNEBLAZEPLATING.get())
                    ),
                    ItemConfig.ArmorSet.with(
                            new ItemConfig.ArmorSet.Piece(2)
                                    .addAll(List.of(
                                            ItemConfig.SpellAttribute.multiply(SpellAttributes.CRITICAL_CHANCE, 0.08F),
                                            ItemConfig.SpellAttribute.multiply(SpellAttributes.HASTE, 0.12F)


                                    )),                            new ItemConfig.ArmorSet.Piece(6)
                                    .addAll(List.of(
                                            ItemConfig.SpellAttribute.multiply(SpellAttributes.POWER.get(MagicSchool.FIRE), 0.2F),
                                            ItemConfig.SpellAttribute.multiply(SpellAttributes.POWER.get(MagicSchool.FROST), 0.2F)


                                    )),
                            new ItemConfig.ArmorSet.Piece(4)
                                    .addAll(List.of(
                                            ItemConfig.SpellAttribute.multiply(SpellAttributes.POWER.get(MagicSchool.FIRE), 0.2F),
                                            ItemConfig.SpellAttribute.multiply(SpellAttributes.POWER.get(MagicSchool.FROST), 0.2F)


                                    )),
                            new ItemConfig.ArmorSet.Piece(2)
                                    .addAll(List.of(
                                            ItemConfig.SpellAttribute.multiply(SpellAttributes.POWER.get(MagicSchool.FIRE), 0.2F),
                                            ItemConfig.SpellAttribute.multiply(SpellAttributes.POWER.get(MagicSchool.FROST), 0.2F)


                                    ))
                    ))
                    .armorSet(material -> new Armor.Set(SpellbladeNext.MOD_ID,
                            new InquisitorSet(ModArmorMaterials.RIMEBLAZE, EquipmentSlot.HEAD, new Item.Properties().tab(EXAMPLE_TAB), List.of(MagicSchool.FIRE,MagicSchool.FROST)),
                            new InquisitorSet(ModArmorMaterials.RIMEBLAZE, EquipmentSlot.CHEST, new Item.Properties().tab(EXAMPLE_TAB), List.of(MagicSchool.FIRE,MagicSchool.FROST)),
                            new InquisitorSet(ModArmorMaterials.RIMEBLAZE, EquipmentSlot.LEGS, new Item.Properties().tab(EXAMPLE_TAB), List.of(MagicSchool.FIRE,MagicSchool.FIRE)),
                            new InquisitorSet(ModArmorMaterials.RIMEBLAZE, EquipmentSlot.FEET, new Item.Properties().tab(EXAMPLE_TAB), List.of(MagicSchool.FIRE,MagicSchool.FIRE))
                    ), entries);
    public static final Armor.Set deathchill =
            create(
                    new Armor.CustomMaterial(
                            "deathchill",
                            20,
                            10,
                            SoundEvents.ARMOR_EQUIP_CHAIN,
                            () -> Ingredient.of(RUNEGLINTPLATING.get(),RUNEBLAZEPLATING.get())
                    ),
                    ItemConfig.ArmorSet.with(
                            new ItemConfig.ArmorSet.Piece(2)
                                    .addAll(List.of(
                                            ItemConfig.SpellAttribute.multiply(SpellAttributes.CRITICAL_CHANCE, 0.08F),
                                            ItemConfig.SpellAttribute.multiply(SpellAttributes.CRITICAL_DAMAGE, 0.4F)

                                    )),
                            new ItemConfig.ArmorSet.Piece(6)
                                    .addAll(List.of(
                                            ItemConfig.SpellAttribute.multiply(SpellAttributes.POWER.get(MagicSchool.ARCANE), 0.2F),
                                            ItemConfig.SpellAttribute.multiply(SpellAttributes.POWER.get(MagicSchool.FROST), 0.2F)


                                    )),
                            new ItemConfig.ArmorSet.Piece(4)
                                    .addAll(List.of(
                                            ItemConfig.SpellAttribute.multiply(SpellAttributes.POWER.get(MagicSchool.ARCANE), 0.2F),
                                            ItemConfig.SpellAttribute.multiply(SpellAttributes.POWER.get(MagicSchool.FROST), 0.2F)


                                    )),
                            new ItemConfig.ArmorSet.Piece(2)
                                    .addAll(List.of(
                                            ItemConfig.SpellAttribute.multiply(SpellAttributes.POWER.get(MagicSchool.ARCANE), 0.2F),
                                            ItemConfig.SpellAttribute.multiply(SpellAttributes.POWER.get(MagicSchool.FROST), 0.2F)


                                    ))
                    ))
                    .armorSet(material -> new Armor.Set(SpellbladeNext.MOD_ID,
                            new InquisitorSet(ModArmorMaterials.DEATHCHILL, EquipmentSlot.HEAD, new Item.Properties().tab(EXAMPLE_TAB), List.of(MagicSchool.FROST,MagicSchool.ARCANE)),
                            new InquisitorSet(ModArmorMaterials.DEATHCHILL, EquipmentSlot.CHEST, new Item.Properties().tab(EXAMPLE_TAB), List.of(MagicSchool.FROST,MagicSchool.ARCANE)),
                            new InquisitorSet(ModArmorMaterials.DEATHCHILL, EquipmentSlot.LEGS, new Item.Properties().tab(EXAMPLE_TAB), List.of(MagicSchool.FROST,MagicSchool.ARCANE)),
                            new InquisitorSet(ModArmorMaterials.DEATHCHILL, EquipmentSlot.FEET, new Item.Properties().tab(EXAMPLE_TAB), List.of(MagicSchool.FROST,MagicSchool.ARCANE))
                    ), entries);
    public static final Armor.Set magus =
            create(
                    new Armor.CustomMaterial(
                            "magus",
                            20,
                            10,
                            SoundEvents.ARMOR_EQUIP_CHAIN,
                            WOOL_INGREDIENTS
                    ),
                    ItemConfig.ArmorSet.with(
                            new ItemConfig.ArmorSet.Piece(2)
                                    .addAll(List.of(
                                            ItemConfig.SpellAttribute.multiply(SpellAttributes.POWER.get(MagicSchool.FROST), 0.125F),
                                            ItemConfig.SpellAttribute.multiply(SpellAttributes.POWER.get(MagicSchool.ARCANE), 0.125F),
                                            ItemConfig.SpellAttribute.multiply(SpellAttributes.POWER.get(MagicSchool.FIRE), 0.125F)
                                    )),
                            new ItemConfig.ArmorSet.Piece(6)
                                    .addAll(List.of(
                                            ItemConfig.SpellAttribute.multiply(SpellAttributes.POWER.get(MagicSchool.FROST), 0.125F),
                                            ItemConfig.SpellAttribute.multiply(SpellAttributes.POWER.get(MagicSchool.ARCANE), 0.125F),
                                            ItemConfig.SpellAttribute.multiply(SpellAttributes.POWER.get(MagicSchool.FIRE), 0.125F)                                 )),
                            new ItemConfig.ArmorSet.Piece(6)
                                    .addAll(List.of(
                                            ItemConfig.SpellAttribute.multiply(SpellAttributes.POWER.get(MagicSchool.FROST), 0.125F),
                                            ItemConfig.SpellAttribute.multiply(SpellAttributes.POWER.get(MagicSchool.ARCANE), 0.125F),
                                            ItemConfig.SpellAttribute.multiply(SpellAttributes.POWER.get(MagicSchool.FIRE), 0.125F)                                )),
                            new ItemConfig.ArmorSet.Piece(2)
                                    .addAll(List.of(
                                            ItemConfig.SpellAttribute.multiply(SpellAttributes.POWER.get(MagicSchool.FROST), 0.125F),
                                            ItemConfig.SpellAttribute.multiply(SpellAttributes.POWER.get(MagicSchool.ARCANE), 0.125F),
                                            ItemConfig.SpellAttribute.multiply(SpellAttributes.POWER.get(MagicSchool.FIRE), 0.125F)                               ))
                    ))
                    .armorSet(material -> new Armor.Set(SpellbladeNext.MOD_ID,
   new Robes(ModArmorMaterials.WOOL,EquipmentSlot.HEAD,new Item.Properties().tab(EXAMPLE_TAB)),

   new Robes(ModArmorMaterials.WOOL,EquipmentSlot.CHEST,new Item.Properties().tab(EXAMPLE_TAB)),
   new Robes(ModArmorMaterials.WOOL,EquipmentSlot.LEGS,new Item.Properties().tab(EXAMPLE_TAB)),
   new Robes(ModArmorMaterials.WOOL,EquipmentSlot.FEET,new Item.Properties().tab(EXAMPLE_TAB))
                    ), entries);


    public static void register(Map<String, ItemConfig.ArmorSet> configs) {
        Armor.register(configs, entries);
    }
}