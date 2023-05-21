package net.spellbladenext.fabric.enchants;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.spell_engine.api.spell.SpellContainer;
import net.spell_engine.internals.SpellCasterItemStack;
import net.spell_engine.internals.SpellContainerHelper;
import net.spell_engine.mixin.ItemStackMixin;

public class Spellshield extends Enchantment {
    public Spellshield(Rarity rarity, EnchantmentCategory enchantmentCategory, EquipmentSlot[] equipmentSlots) {
        super(rarity, enchantmentCategory, equipmentSlots);
    }
    public int getMinCost(int p_45000_) {
        return 10 + 20 * (p_45000_ - 1);
    }

    public int getMaxCost(int p_45002_) {
        return super.getMinCost(p_45002_) + 50;
    }

    public int getMaxLevel() {
        return 1;
    }


    @Override
    public Component getFullname(int p_44701_) {
        return super.getFullname(p_44701_);
    }

    @Override
    public boolean canEnchant(ItemStack p_44689_) {
        if(p_44689_.getItem() instanceof ShieldItem || SpellContainerHelper.hasValidContainer(p_44689_)) {
            return true;
        }
        else{
            return false;
        }
    }


}
