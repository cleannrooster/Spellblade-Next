package net.spellbladenext.items;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.spell_power.api.MagicSchool;
import net.spellbladenext.SpellbladeNext;

import java.util.UUID;

public class RunicArmor extends ArmorItem {
    protected final EquipmentSlot slot;
    private static final UUID[] ARMOR_MODIFIER_UUID_PER_SLOT = new UUID[]{UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"), UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"), UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"), UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150")};

    private final int defense;
    private final float toughness;
    protected final float knockbackResistance;
    protected final ArmorMaterial material;
    private final Multimap<Attribute, AttributeModifier> defaultModifiers;
    public RunicArmor(ArmorMaterial armorMaterial, EquipmentSlot equipmentSlot, Properties properties, MagicSchool magicSchool) {
        super(armorMaterial, equipmentSlot, properties);
        //super(properties.defaultDurability(armorMaterial.getDurabilityForSlot(equipmentSlot)));
        this.material = armorMaterial;
        this.slot = equipmentSlot;
        this.defense = armorMaterial.getDefenseForSlot(equipmentSlot);
        this.toughness = armorMaterial.getToughness();
        this.knockbackResistance = armorMaterial.getKnockbackResistance();
        DispenserBlock.registerBehavior(this, DISPENSE_ITEM_BEHAVIOR);
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        UUID uUID = ARMOR_MODIFIER_UUID_PER_SLOT[equipmentSlot.getIndex()];
        builder.put(Attributes.ARMOR, new AttributeModifier(uUID, "Armor modifier", (double)this.defense, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(uUID, "Armor toughness", (double)this.toughness, AttributeModifier.Operation.ADDITION));
        if (armorMaterial == ArmorMaterials.NETHERITE) {
            builder.put(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(uUID, "Armor knockback resistance", (double)this.knockbackResistance, AttributeModifier.Operation.ADDITION));
        }
        builder.put(net.spell_power.api.attributes.Attributes.POWER.get(magicSchool).attribute, new AttributeModifier(uUID, "Armor Fire Power", 2, AttributeModifier.Operation.ADDITION));

        this.defaultModifiers = builder.build();

    }

    @Override
    public void inventoryTick(ItemStack itemStack, Level level, Entity entity, int i, boolean bl) {
        if(!level.isClientSide()) {
            if(entity instanceof Player) {
                Player player = (Player)entity;

                int amount = 0;
                if(player.getInventory().getArmor(0).getItem() instanceof RunicArmor){
                    amount++;
                }
                if(player.getInventory().getArmor(1).getItem() instanceof RunicArmor){
                    amount++;
                }
                if(player.getInventory().getArmor(2).getItem() instanceof RunicArmor){
                    amount++;
                }
                if(player.getInventory().getArmor(3).getItem() instanceof RunicArmor){
                    amount++;
                }
                int ii = 0;
                if(player.hasEffect(SpellbladeNext.RUNICABSORPTION.get())){
                    ii = player.getEffect(SpellbladeNext.RUNICABSORPTION.get()).getAmplifier();
                }
                if(amount > 0 && !player.hasEffect(SpellbladeNext.RUNICABSORPTION.get()) && player.getAbsorptionAmount() <= 0)
                player.addEffect(new MobEffectInstance(SpellbladeNext.RUNICABSORPTION.get(),20*5,amount, false, false));

            }
        }
        super.inventoryTick(itemStack, level, entity, i, bl);
    }
    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot equipmentSlot) {
        if(this.slot == equipmentSlot) {
            return this.defaultModifiers;
        }
        else{
            return ImmutableMultimap.of();
        }
    }
}
