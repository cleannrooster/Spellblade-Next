package net.spellbladenext.fabric.items;

import com.google.common.collect.Multimap;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.spell_engine.api.item.ConfigurableAttributes;
import net.spell_power.api.MagicSchool;

public class RuneblazingArmor extends RunicArmor  {

    public RuneblazingArmor(ArmorMaterial armorMaterial, EquipmentSlot equipmentSlot, Properties properties, MagicSchool school) {
        super(armorMaterial, equipmentSlot, properties, school);
    }

    public void inventoryTick(ItemStack itemStack, Level level, Entity entity, int i, boolean bl) {

        super.inventoryTick(itemStack, level, entity, i, bl);
    }

    @Override
    public ItemStack getDefaultInstance() {
        return super.getDefaultInstance();
    }

}
