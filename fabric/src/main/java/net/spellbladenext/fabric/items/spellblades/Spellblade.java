package net.spellbladenext.fabric.items.spellblades;

import com.google.common.collect.Multimap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;

import net.minecraft.world.level.block.state.BlockState;
import net.spell_engine.api.item.ConfigurableAttributes;
import net.spell_engine.api.item.weapon.StaffItem;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.internals.*;
import net.spell_engine.utils.TargetHelper;
import net.spell_power.SpellPowerMod;
import net.spell_power.api.MagicSchool;
import net.spell_power.api.SpellDamageSource;
import net.spell_power.api.SpellPower;
import net.spell_power.api.attributes.SpellAttributeEntry;
import net.spell_power.api.attributes.SpellAttributes;
import net.spellbladenext.SpellbladeNext;
import net.spellbladenext.fabric.config.ItemConfig;
import net.spellbladenext.items.FriendshipBracelet;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import static net.spellbladenext.SpellbladeNext.MOD_ID;

public class Spellblade extends SwordItem implements ConfigurableAttributes {
    private final ArrayList<ItemConfig.SpellAttribute> school;
    private Multimap<Attribute, AttributeModifier> attributes;

    public Spellblade(Tier material, Multimap<Attribute, AttributeModifier> attributes, Properties settings, ArrayList<ItemConfig.SpellAttribute> school) {
        super(material,1,material.getAttackDamageBonus(),  settings);
        this.school = school;


        this.setAttributes(attributes);
    }
    public ArrayList<ItemConfig.SpellAttribute> getMagicSchools(){
        return this.school;
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        for(ItemConfig.SpellAttribute school: this.getMagicSchools().stream().toList()) {
            if(attacker instanceof Player player) {
                //System.out.println(school.name);
                MagicSchool actualSchool = MagicSchool.fromAttributeId(new ResourceLocation(SpellPowerMod.ID, school.name));
                if((int) (player.getAttributeValue(Attributes.ATTACK_DAMAGE)/2) > 0) {
                    if (actualSchool == MagicSchool.FROST) {
                        if (attacker.hasEffect(SpellbladeNext.FROSTINFUSION.get())) {
                            attacker.addEffect(new MobEffectInstance(SpellbladeNext.FROSTINFUSION.get(), 160, Math.min(attacker.getEffect(SpellbladeNext.FROSTINFUSION.get()).getAmplifier() + 1, (int) (player.getAttributeValue(Attributes.ATTACK_DAMAGE) / 2)- 1) , false, false));
                        } else {
                            attacker.addEffect(new MobEffectInstance(SpellbladeNext.FROSTINFUSION.get(), 160, 0, false, false));

                        }
                    }
                    if (actualSchool == MagicSchool.ARCANE) {
                        if (attacker.hasEffect(SpellbladeNext.ARCANEINFUSION.get())) {
                            attacker.addEffect(new MobEffectInstance(SpellbladeNext.ARCANEINFUSION.get(), 160, Math.min(attacker.getEffect(SpellbladeNext.ARCANEINFUSION.get()).getAmplifier() + 1, (int) (player.getAttributeValue(Attributes.ATTACK_DAMAGE) / 2)- 1) , false, false));
                        } else {
                            attacker.addEffect(new MobEffectInstance(SpellbladeNext.ARCANEINFUSION.get(), 160, 0, false, false));

                        }
                    }
                    if (actualSchool == MagicSchool.FIRE) {
                        if (attacker.hasEffect(SpellbladeNext.FIREINFUSION.get())) {
                            attacker.addEffect(new MobEffectInstance(SpellbladeNext.FIREINFUSION.get(), 160, Math.min(attacker.getEffect(SpellbladeNext.FIREINFUSION.get()).getAmplifier() + 1, (int) (player.getAttributeValue(Attributes.ATTACK_DAMAGE) / 2)- 1) , false, false));
                        } else {
                            attacker.addEffect(new MobEffectInstance(SpellbladeNext.FIREINFUSION.get(), 160, 0, false, false));

                        }
                    }
                }

            }

        }
        stack.hurtAndBreak(1, attacker, (e) -> {
            e.broadcastBreakEvent(EquipmentSlot.MAINHAND);
        });
        return true;

    }

    @Override
    public void inventoryTick(ItemStack itemStack, Level level, Entity entity, int i, boolean bl) {
        for(ItemConfig.SpellAttribute school: this.getMagicSchools().stream().toList()) {
            MagicSchool actualSchool = MagicSchool.fromAttributeId(new ResourceLocation(SpellPowerMod.ID, school.name));

            if (entity instanceof LivingEntity living) {
                if (living.hasEffect(SpellbladeNext.FIREINFUSION.get()) && actualSchool == MagicSchool.FIRE){
                    itemStack.getOrCreateTag().putInt("CustomModelData", (1));

                }
                else if (living.hasEffect(SpellbladeNext.ARCANEINFUSION.get()) && actualSchool == MagicSchool.ARCANE){
                    itemStack.getOrCreateTag().putInt("CustomModelData", (1));

                }
                else if (living.hasEffect(SpellbladeNext.FROSTINFUSION.get()) && actualSchool == MagicSchool.FROST){
                    itemStack.getOrCreateTag().putInt("CustomModelData", (1));

                }
                else{
                    itemStack.getOrCreateTag().putInt("CustomModelData", (0));

                }
            }
        }
        super.inventoryTick(itemStack, level, entity, i, bl);
    }

    public void setAttributes(Multimap<Attribute, AttributeModifier> attributes) {
        this.attributes = attributes;
    }

    public boolean canAttackBlock(BlockState state, Level world, BlockPos pos, Player miner) {
        return !miner.isCreative();
    }


    public boolean mineBlock(ItemStack stack, Level world, BlockState state, BlockPos pos, LivingEntity miner) {
        if (state.getDestroySpeed(world, pos) != 0.0F) {
            stack.hurtAndBreak(2, miner, (e) -> {
                e.broadcastBreakEvent(EquipmentSlot.MAINHAND);
            });
        }

        return true;
    }

    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
        if (this.attributes == null) {
            return super.getDefaultAttributeModifiers(slot);
        } else {
            return slot == EquipmentSlot.MAINHAND ? this.attributes : super.getDefaultAttributeModifiers(slot);
        }
    }
    @Environment(EnvType.CLIENT)
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {
        if(level != null && level.isClientSide){

            Player caster = Minecraft.getInstance().player;
            if(caster != null) {
                for(ItemConfig.SpellAttribute school : getMagicSchools()) {
                    MagicSchool actualSchool = MagicSchool.fromAttributeId(new ResourceLocation(SpellPowerMod.ID, school.name));
                    if(caster.getAttribute(SpellAttributes.POWER.get(actualSchool).attribute) != null) {
                        Set<AttributeModifier> base = caster.getAttribute(SpellAttributes.POWER.get(actualSchool).attribute).getModifiers(AttributeModifier.Operation.MULTIPLY_BASE);
                        Set<AttributeModifier> total = caster.getAttribute(SpellAttributes.POWER.get(actualSchool).attribute).getModifiers(AttributeModifier.Operation.MULTIPLY_TOTAL);
                        double modifierbase = 1;
                        double modifiertotal = 1;
                        for(AttributeModifier bases : base){
                            modifierbase += bases.getAmount();
                        }
                        for(AttributeModifier totals : total){
                            modifiertotal *= 1 + totals.getAmount();
                        }
                        double modifier = modifierbase*modifiertotal;
                        list.add(Component.translatable("Attacks with this weapon deal an additional " + (int)(50 * modifier) + "% " + actualSchool.name().toLowerCase() + " damage on hit.").
                                withStyle(ChatFormatting.GRAY));
                    }
                }
            }
        }
        super.appendHoverText(itemStack, level, list, tooltipFlag);
    }
}
