package net.spellbladenext.fabric.items.spellblades;

import com.google.common.collect.Multimap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.item.v1.FabricItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.item.v1.FabricItemStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.spell_engine.api.item.ConfigurableAttributes;
import net.spell_engine.api.item.weapon.StaffItem;
import net.spell_engine.internals.*;
import net.spell_engine.utils.TargetHelper;
import net.spell_power.SpellPowerMod;
import net.spell_power.api.MagicSchool;
import net.spell_power.api.SpellDamageSource;
import net.spell_power.api.SpellPower;
import net.spell_power.api.attributes.SpellAttributes;
import net.spellbladenext.SpellbladeNext;
import net.spellbladenext.fabric.SpellbladesFabric;
import net.spellbladenext.fabric.config.ItemConfig;
import net.spellbladenext.items.FriendshipBracelet;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import static net.spellbladenext.SpellbladeNext.MOD_ID;

public class Claymores extends SwordItem implements ConfigurableAttributes {
    private final ArrayList<ItemConfig.SpellAttribute> school;
    private Multimap<Attribute, AttributeModifier> attributes;

    public Claymores(Tier material, Multimap<Attribute, AttributeModifier> attributes, Item.Properties settings, ArrayList<ItemConfig.SpellAttribute> school) {
        super(material, 1, material.getAttackDamageBonus(), settings);
        this.school = school;

        this.setAttributes(attributes);
    }

    public ArrayList<ItemConfig.SpellAttribute> getMagicSchools() {
        return this.school;
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        for (ItemConfig.SpellAttribute school : this.getMagicSchools().stream().toList()) {
            if (attacker instanceof Player player && stack.getItem() == Spellblades.mulberrysword.item()) {
                //System.out.println(school.name);
                MagicSchool actualSchool = MagicSchool.fromAttributeId(new ResourceLocation(SpellPowerMod.ID, school.name));

                SpellPower.Result power = SpellPower.getSpellPower(actualSchool, (LivingEntity) attacker);
                SpellPower.Vulnerability vulnerability = SpellPower.Vulnerability.none;
                double modifier = 0.75;

                if(player instanceof SpellCasterEntity caster){
                    if(caster.getCurrentSpellId() != null &&
                            (caster.getCurrentSpellId().equals(new ResourceLocation(MOD_ID,"whirlwind")) ||
                                    caster.getCurrentSpellId().equals(new ResourceLocation(MOD_ID,"dualwield_whirlwind")) ||
                                    caster.getCurrentSpellId().equals(new ResourceLocation(MOD_ID,"flicker_strike")))){
                        modifier *= 0.2;
                        modifier *= player.getAttributeValue(Attributes.ATTACK_SPEED);

                    }
                }
                vulnerability = SpellPower.getVulnerability(target, actualSchool);

                //SpellPower.Result power = SpellPower.getSpellPower(MagicSchool.ARCANE, (LivingEntity) this.getOwner());
                double amount = modifier*3 * power.randomValue(vulnerability) / 3;


                //particleMultiplier = power.criticalDamage() + (double)vulnerability.criticalDamageBonus();
                target.invulnerableTime = 0;
                target.hurt(SpellDamageSource.create(actualSchool, (LivingEntity) attacker), (float) amount);
                target.invulnerableTime = 0;
                target.hurt(SpellDamageSource.create(MagicSchool.HEALING, (LivingEntity) attacker), (float) amount / 4);
            }
            if(attacker instanceof Player player) {
                //System.out.println(school.name);
                MagicSchool actualSchool = MagicSchool.fromAttributeId(new ResourceLocation(SpellPowerMod.ID, school.name));
                if ((int) (player.getAttributeValue(Attributes.ATTACK_DAMAGE) / 2) > 0) {

                    if (actualSchool == MagicSchool.FROST) {
                        if (attacker.getEffect(SpellbladeNext.FROSTINFUSION.get()) != null) {
                            attacker.addEffect(new MobEffectInstance(SpellbladeNext.FROSTINFUSION.get(), 160, Math.min(attacker.getEffect(SpellbladeNext.FROSTINFUSION.get()).getAmplifier() + 1, (int) ((player.getAttributeValue(Attributes.ATTACK_DAMAGE) / 2) - 1)), false, false));
                        } else {
                            attacker.addEffect(new MobEffectInstance(SpellbladeNext.FROSTINFUSION.get(), 160, 0, false, false));

                        }
                    }
                    if (actualSchool == MagicSchool.ARCANE) {
                        if (attacker.getEffect(SpellbladeNext.ARCANEINFUSION.get()) != null) {
                            attacker.addEffect(new MobEffectInstance(SpellbladeNext.ARCANEINFUSION.get(), 160, Math.min(attacker.getEffect(SpellbladeNext.ARCANEINFUSION.get()).getAmplifier() + 1, (int) ((player.getAttributeValue(Attributes.ATTACK_DAMAGE) / 2) - 1)), false, false));
                        } else {
                            attacker.addEffect(new MobEffectInstance(SpellbladeNext.ARCANEINFUSION.get(), 160, 0, false, false));

                        }
                    }
                    if (actualSchool == MagicSchool.FIRE) {
                        if (attacker.getEffect(SpellbladeNext.FIREINFUSION.get()) != null) {
                            attacker.addEffect(new MobEffectInstance(SpellbladeNext.FIREINFUSION.get(), 160, Math.min(attacker.getEffect(SpellbladeNext.FIREINFUSION.get()).getAmplifier() + 1, (int) ((player.getAttributeValue(Attributes.ATTACK_DAMAGE) / 2) - 1)), false, false));
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

    @Override
    public void inventoryTick(ItemStack itemStack, Level level, Entity entity, int i, boolean bl) {
        if(itemStack.getItem() == Spellblades.mulberrysword.item()) {
            if (entity instanceof LivingEntity living) {
                if(!bl) {
                    itemStack.hurtAndBreak((int) (itemStack.getMaxDamage() / (20 * 5)), living, (p_43122_) -> {
                    });
                }
                else{
                    itemStack.setDamageValue(0);
                }
            }
        }
        super.inventoryTick(itemStack, level, entity, i, bl);
    }
    @Environment(EnvType.CLIENT)
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {

        if(level != null && level.isClientSide  && itemStack.getItem() == Spellblades.mulberrysword.item()){

            Player caster = Minecraft.getInstance().player;
            if(caster != null) {

                Multimap<Attribute, AttributeModifier> heldAttributes = caster.getMainHandItem().getAttributeModifiers(EquipmentSlot.MAINHAND);
                Multimap<Attribute, AttributeModifier> itemAttributes = itemStack.getAttributeModifiers(EquipmentSlot.MAINHAND);
                caster.getAttributes().removeAttributeModifiers(heldAttributes);
                caster.getAttributes().addTransientAttributeModifiers(itemAttributes);

                for (ItemConfig.SpellAttribute school : getMagicSchools()) {
                    MagicSchool actualSchool = MagicSchool.fromAttributeId(new ResourceLocation(SpellPowerMod.ID, school.name));

                    int min = (int) (0.75*Math.floor(SpellPower.getSpellPower(actualSchool, caster, itemStack).baseValue()));
                    int max = (int) (0.75*Math.floor(SpellPower.getSpellPower(actualSchool, caster, itemStack).forcedCriticalValue()));
                    list.add(Component.translatable("Attacks with this weapon deal an additional " + min + " - " + max + " " + actualSchool.name().toLowerCase() + " damage on hit.").withStyle(ChatFormatting.RED));
                }

                    int min = (int) Math.floor(SpellPower.getSpellPower(MagicSchool.FIRE, caster, itemStack).baseValue() * 0.25);
                    int max = (int) Math.floor(SpellPower.getSpellPower(MagicSchool.FIRE, caster, itemStack).forcedCriticalValue() * 0.25);
                    list.add(Component.translatable("Attacks with this weapon deal an additional " + min + " - " + max + " " + MagicSchool.HEALING.name().toLowerCase() + " damage on hit.").withStyle(ChatFormatting.YELLOW));
                caster.getAttributes().removeAttributeModifiers(itemAttributes);
                caster.getAttributes().addTransientAttributeModifiers(heldAttributes);

            }
        }
        if(itemStack.getItem() == Spellblades.mulberrysword.item()){

            list.add(Component.translatable("If you have at least 10 Fire Spell Power, you can summon this").withStyle(ChatFormatting.GRAY));
            list.add(Component.translatable("item by shift right clicking with an empty hand.").withStyle(ChatFormatting.GRAY));

        }
        else{
            if(level != null && level.isClientSide ){

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
                               modifiertotal *= 1+totals.getAmount();
                           }
                           double modifier = modifierbase*modifiertotal;
                           list.add(Component.translatable("Attacks with this weapon deal an additional " + (int)(50 * modifier) + "% " + actualSchool.name().toLowerCase() + " damage on hit.").
                                   withStyle(ChatFormatting.GRAY));

                       }
                    }
                }
            }
        }
        super.appendHoverText(itemStack, level, list, tooltipFlag);
    }

}
