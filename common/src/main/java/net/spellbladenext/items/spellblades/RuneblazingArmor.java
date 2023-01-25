package net.spellbladenext.items.spellblades;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.spell_power.api.MagicSchool;
import net.spellbladenext.items.RunicArmor;

import java.util.Map;
import java.util.UUID;

public class RuneblazingArmor extends RunicArmor {

    public RuneblazingArmor(ArmorMaterial armorMaterial, EquipmentSlot equipmentSlot, Properties properties, MagicSchool school) {
        super(armorMaterial, equipmentSlot, properties, school);
    }

    public void inventoryTick(ItemStack itemStack, Level level, Entity entity, int i, boolean bl) {

        super.inventoryTick(itemStack, level, entity, i, bl);
    }


}
