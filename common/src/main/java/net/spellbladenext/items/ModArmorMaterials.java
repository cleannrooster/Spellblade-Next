package net.spellbladenext.items;


import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.LazyLoadedValue;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.spellbladenext.SpellbladeNext;

import java.util.function.Supplier;

import static net.spellbladenext.SpellbladeNext.WOOL_INGREDIENTS;

public enum ModArmorMaterials implements ArmorMaterial {

    RUNEBLAZING("runeblazing", 37, new int[]{1, 2, 3, 1}, 15, SoundEvents.ARMOR_EQUIP_CHAIN, 0, 0, () -> {
        return Ingredient.of(SpellbladeNext.RUNEBLAZEPLATING.get());
    }),
    RUNEFROSTED("runefrosted", 37, new int[]{1, 2, 3, 1}, 15, SoundEvents.ARMOR_EQUIP_CHAIN, 0, 0, () -> {
        return Ingredient.of(SpellbladeNext.RUNEFROSTPLATING.get());
    }),
    RUNEGLEAMING("runegleaming", 37, new int[]{1, 2, 3, 1}, 15, SoundEvents.ARMOR_EQUIP_CHAIN, 0, 0, () -> {
        return Ingredient.of(SpellbladeNext.RUNEGLINTPLATING.get());
    }),
    AETHERFIRE("aetherfire", 37, new int[]{1, 2, 3, 1}, 15, SoundEvents.ARMOR_EQUIP_CHAIN, 0, 0, () -> {
        return Ingredient.of(SpellbladeNext.RUNEBLAZEPLATING.get(),SpellbladeNext.RUNEFROSTPLATING.get());
    }),
    RIMEBLAZE("rimeblaze", 37, new int[]{1, 2, 3, 1}, 15, SoundEvents.ARMOR_EQUIP_CHAIN, 0, 0, () -> {
        return Ingredient.of(SpellbladeNext.RUNEFROSTPLATING.get(),SpellbladeNext.RUNEBLAZEPLATING.get());
    }),
    DEATHCHILL("deathchill", 37, new int[]{1, 2, 3, 1}, 15, SoundEvents.ARMOR_EQUIP_CHAIN, 0, 0, () -> {
        return Ingredient.of(SpellbladeNext.RUNEGLINTPLATING.get(),SpellbladeNext.RUNEFROSTPLATING.get());
    }),
    SPECTRAL("chainmail", 37, new int[]{2, 4, 6, 2}, 15, SoundEvents.ARMOR_EQUIP_CHAIN, 3.0F, 0.1F, Ingredient::of),
    WOOL("magus", 37, new int[]{2, 4, 6, 2}, 15, SoundEvents.ARMOR_EQUIP_LEATHER, 0, 0,
            WOOL_INGREDIENTS);







    private static final int[] HEALTH_PER_SLOT = new int[]{13, 15, 16, 11};
    private final String name;
    private final int durabilityMultiplier;
    private final int[] slotProtections;
    private final int enchantmentValue;
    private final SoundEvent sound;
    private final float toughness;
    private final float knockbackResistance;
    private final LazyLoadedValue<Ingredient> repairIngredient;

    private ModArmorMaterials(String p_40474_, int p_40475_, int[] p_40476_, int p_40477_, SoundEvent p_40478_, float p_40479_, float p_40480_, Supplier<Ingredient> p_40481_) {
        this.name = p_40474_;
        this.durabilityMultiplier = p_40475_;
        this.slotProtections = p_40476_;
        this.enchantmentValue = p_40477_;
        this.sound = p_40478_;
        this.toughness = p_40479_;
        this.knockbackResistance = p_40480_;
        this.repairIngredient = new LazyLoadedValue<>(p_40481_);
    }

    public int getDurabilityForSlot(EquipmentSlot p_40484_) {
        return HEALTH_PER_SLOT[p_40484_.getIndex()] * this.durabilityMultiplier;
    }

    public int getDefenseForSlot(EquipmentSlot p_40487_) {
        return this.slotProtections[p_40487_.getIndex()];
    }

    public int getEnchantmentValue() {
        return this.enchantmentValue;
    }

    public SoundEvent getEquipSound() {
        return this.sound;
    }

    public Ingredient getRepairIngredient() {
        return this.repairIngredient.get();
    }

    public String getName() {
        return this.name;
    }

    public float getToughness() {
        return this.toughness;
    }

    public float getKnockbackResistance() {
        return this.knockbackResistance;
    }

}
