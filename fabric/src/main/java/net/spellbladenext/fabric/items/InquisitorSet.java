package net.spellbladenext.fabric.items;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;
import net.spell_engine.api.item.ConfigurableAttributes;
import net.spell_power.api.MagicSchool;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class InquisitorSet extends ArmorItem implements IAnimatable, ConfigurableAttributes {
    protected final EquipmentSlot slot;
    private static final UUID[] ARMOR_MODIFIER_UUID_PER_SLOT = new UUID[]{UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"), UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"), UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"), UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150")};

    private final int defense;
    private final float toughness;
    protected final float knockbackResistance;
    protected final ArmorMaterial material;
    private Multimap<Attribute, AttributeModifier> defaultModifiers;
    private final List<MagicSchool> magicschool = new ArrayList<>();
    private final EquipmentSlot equipmentslot;
    public InquisitorSet(ArmorMaterial armorMaterial, EquipmentSlot equipmentSlot, Properties properties, List<MagicSchool> magicSchool) {
        super(armorMaterial, equipmentSlot, properties);
        //super(properties.defaultDurability(armorMaterial.getDurabilityForSlot(equipmentSlot)));
        this.material = armorMaterial;
        this.slot = equipmentSlot;
        this.defense = armorMaterial.getDefenseForSlot(equipmentSlot);
        this.toughness = armorMaterial.getToughness();
        this.knockbackResistance = armorMaterial.getKnockbackResistance();
        this.magicschool.addAll(magicSchool);
        this.equipmentslot = equipmentSlot;
        DispenserBlock.registerBehavior(this, DISPENSE_ITEM_BEHAVIOR);
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        UUID uUID = ARMOR_MODIFIER_UUID_PER_SLOT[equipmentSlot.getIndex()];
        this.defaultModifiers = builder.build();
    }

    public List<MagicSchool> getMagicschool() {
        return magicschool;
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
        // builder.putAll(super.getAttributeModifiers(this.slot));
        System.out.println(attributes);
        builder.putAll(attributes);
        this.defaultModifiers = builder.build();
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
