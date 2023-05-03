package net.spellbladenext.fabric.items.armors;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.spell_engine.api.item.ConfigurableAttributes;
import net.spell_power.api.MagicSchool;
import net.spellbladenext.SpellbladeNext;

import java.util.UUID;

public class RunicArmor extends ArmorItem implements ConfigurableAttributes {
    protected final EquipmentSlot slot;
    private static final UUID[] ARMOR_MODIFIER_UUID_PER_SLOT = new UUID[]{UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"), UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"), UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"), UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150")};

    private final int defense;
    private final float toughness;
    protected final float knockbackResistance;
    protected final ArmorMaterial material;
    private Multimap<Attribute, AttributeModifier> defaultModifiers;
    private final MagicSchool magicschool;
    private final EquipmentSlot equipmentslot;

    public RunicArmor(ArmorMaterial armorMaterial, EquipmentSlot equipmentSlot, Properties properties, MagicSchool magicSchool) {
        super(armorMaterial, equipmentSlot, properties);
        //super(properties.defaultDurability(armorMaterial.getDurabilityForSlot(equipmentSlot)));
        this.material = armorMaterial;
        this.slot = equipmentSlot;
        this.defense = armorMaterial.getDefenseForSlot(equipmentSlot);
        this.toughness = armorMaterial.getToughness();
        this.knockbackResistance = armorMaterial.getKnockbackResistance();
        this.magicschool = magicSchool;
        this.equipmentslot = equipmentSlot;
        DispenserBlock.registerBehavior(this, DISPENSE_ITEM_BEHAVIOR);
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        UUID uUID = ARMOR_MODIFIER_UUID_PER_SLOT[equipmentSlot.getIndex()];
        this.defaultModifiers = builder.build();
    }

    public MagicSchool getMagicschool() {
        return magicschool;
    }


    @Override
    public ItemStack getDefaultInstance() {
        ItemStack item = new ItemStack(this);

        return item;
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
        if (defaultModifiers == null) {
            return super.getDefaultAttributeModifiers(equipmentSlot);
        }
        return equipmentSlot == this.slot ? this.defaultModifiers : super.getDefaultAttributeModifiers(equipmentSlot);
    }
    public void setAttributes(Multimap<Attribute, AttributeModifier> attributes) {
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.putAll(attributes);
        this.defaultModifiers = builder.build();
    }

}
