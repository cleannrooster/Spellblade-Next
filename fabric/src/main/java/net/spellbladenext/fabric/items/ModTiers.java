package net.spellbladenext.fabric.items;

import net.minecraft.tags.ItemTags;
import net.minecraft.util.LazyLoadedValue;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import java.util.function.Supplier;

public enum ModTiers implements Tier {
    WOOD(4, 2031, 9.0F, 0F, 15, () -> {
        return Ingredient.of(Items.STRIPPED_MANGROVE_WOOD);
    });

    private final int level;
    private final int uses;
    private final float speed;
    private final float damage;
    private final int enchantmentValue;
    private final LazyLoadedValue<Ingredient> repairIngredient;

    private ModTiers(int j, int k, float f, float g, int l, Supplier<Ingredient> supplier) {
        this.level = j;
        this.uses = k;
        this.speed = f;
        this.damage = g;
        this.enchantmentValue = l;
        this.repairIngredient = new LazyLoadedValue(supplier);
    }

    public int getUses() {
        return this.uses;
    }

    public float getSpeed() {
        return this.speed;
    }

    public float getAttackDamageBonus() {
        return this.damage;
    }

    public int getLevel() {
        return this.level;
    }

    public int getEnchantmentValue() {
        return this.enchantmentValue;
    }

    public Ingredient getRepairIngredient() {
        return (Ingredient)this.repairIngredient.get();
    }
}
