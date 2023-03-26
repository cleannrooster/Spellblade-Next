package net.spellbladenext.fabric.items;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.spell_engine.api.item.ConfigurableAttributes;
import net.spell_engine.internals.SpellCasterEntity;
import net.spell_engine.internals.SpellRegistry;
import net.spell_power.api.MagicSchool;
import net.spell_power.api.attributes.CustomEntityAttribute;
import net.spell_power.api.attributes.EntityAttributes_SpellPower;
import net.spellbladenext.SpellbladeNext;
import org.checkerframework.checker.units.qual.A;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

import java.util.*;

public class Robes extends ArmorItem implements IAnimatable, DyeableLeatherItem, ConfigurableAttributes {
    protected EquipmentSlot slot;
    Attribute attribute;
    Attribute attribute2;
    Attribute attribute3;
    AttributeModifier modifier;
    AttributeModifier modifier2;
    AttributeModifier modifier3;
    String uuid;
    private Multimap<Attribute, AttributeModifier> defaultModifiers;

    public Robes(ArmorMaterial armorMaterial, EquipmentSlot equipmentSlot, Properties properties) {
        super(armorMaterial, equipmentSlot, properties);
        this.slot = equipmentSlot;
        this.defense = armorMaterial.getDefenseForSlot(equipmentSlot);
        UUID uUID = ARMOR_MODIFIER_UUID_PER_SLOT[this.slot.getIndex()];
         attribute = EntityAttributes_SpellPower.POWER.get(MagicSchool.FROST);

         modifier = new AttributeModifier(uUID.toString(), 0.125, AttributeModifier.Operation.MULTIPLY_BASE);
         attribute2 = EntityAttributes_SpellPower.POWER.get(MagicSchool.FIRE);

         modifier2 = new AttributeModifier(uUID.toString(), 0.125, AttributeModifier.Operation.MULTIPLY_BASE);
         attribute3 = EntityAttributes_SpellPower.POWER.get(MagicSchool.ARCANE);

         modifier3 = new AttributeModifier(uUID.toString(), 0.125, AttributeModifier.Operation.MULTIPLY_BASE);
         uuid = ARMOR_MODIFIER_UUID_PER_SLOT[slot.getIndex()].toString();
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        this.defaultModifiers = builder.build();

    }
    private static final UUID[] ARMOR_MODIFIER_UUID_PER_SLOT = new UUID[]{UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"), UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"), UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"), UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150")};

    public double defense;
    String asdfuuid = "e17f655f-46b5-4885-8be3-5846f55c8fae";


    @Override
    public EquipmentSlot getSlot() {
        return slot;
    }
    public void setAttributes(Multimap<Attribute, AttributeModifier> attributes) {
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        // builder.putAll(super.getAttributeModifiers(this.slot));
        builder.putAll(attributes);
        this.defaultModifiers = builder.build();
    }

    @Override
    public void inventoryTick(ItemStack itemStack, Level level, Entity entity, int i, boolean bl) {
        boolean broken = false;
        MagicSchool school;

        if(!level.isClientSide()) {
            if(entity instanceof Player) {
                Player player = (Player)entity;

                int amount = 0;
                if(player.getInventory().getArmor(0).getItem() instanceof Robes){
                    amount++;
                }
                if(player.getInventory().getArmor(1).getItem() instanceof Robes){
                    amount++;
                }
                if(player.getInventory().getArmor(2).getItem() instanceof Robes){
                    amount++;
                }
                if(player.getInventory().getArmor(3).getItem() instanceof Robes){
                    amount++;
                }
                int ii = 0;
                if(player.hasEffect(SpellbladeNext.RUNICBOON.get())){
                    ii = player.getEffect(SpellbladeNext.RUNICBOON.get()).getAmplifier();
                }
                if(amount > 0 && !player.hasEffect(SpellbladeNext.RUNICBOON.get()) && player.getAbsorptionAmount() <= 0)
                    player.addEffect(new MobEffectInstance(SpellbladeNext.RUNICBOON.get(),20*5,amount, false, false));

            }
        }
    }


    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot equipmentSlot) {
        return equipmentSlot == this.slot ? this.defaultModifiers : super.getDefaultAttributeModifiers(equipmentSlot);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(ItemStack stack, EquipmentSlot slot) {
        return slot == this.slot ? this.defaultModifiers : super.getDefaultAttributeModifiers(slot);
    }

    public AnimationFactory factory = GeckoLibUtil.createFactory(this);
    @Override
    public void registerControllers(AnimationData animationData) {

    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }
}
