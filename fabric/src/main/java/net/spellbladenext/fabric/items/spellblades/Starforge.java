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
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.spell_engine.api.item.ConfigurableAttributes;
import net.spell_engine.internals.*;
import net.spell_engine.particle.ParticleHelper;
import net.spell_engine.particle.Particles;
import net.spell_engine.utils.TargetHelper;
import net.spell_power.SpellPowerMod;
import net.spell_power.api.MagicSchool;
import net.spell_power.api.SpellDamageSource;
import net.spell_power.api.SpellPower;
import net.spell_power.api.attributes.SpellAttributes;
import net.spellbladenext.SpellbladeNext;
import net.spellbladenext.fabric.config.ItemConfig;
import net.spellbladenext.items.FriendshipBracelet;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static net.spellbladenext.SpellbladeNext.MOD_ID;

public class Starforge extends SwordItem implements ConfigurableAttributes {
    private Multimap<Attribute, AttributeModifier> attributes;

    public Starforge(Tier material, Multimap<Attribute, AttributeModifier> attributes, Item.Properties settings, ArrayList<ItemConfig.SpellAttribute> school) {
        super(material, 1, material.getAttackDamageBonus(), settings);

        this.setAttributes(attributes);
    }
    @Override
    public void inventoryTick(ItemStack itemStack, Level level, Entity entity, int i, boolean bl) {
        if(bl &&  entity instanceof LivingEntity livingEntity){
            int infusion = (int)((livingEntity.getAttributeValue(SpellAttributes.POWER.get(MagicSchool.FROST).attribute)
                    +livingEntity.getAttributeValue(SpellAttributes.POWER.get(MagicSchool.FIRE).attribute))/2);
            ((LivingEntity) entity).addEffect(new MobEffectInstance(SpellbladeNext.ARCANEINFUSION.get(),5,infusion-1,false,false));

        }
        super.inventoryTick(itemStack, level, entity, i, bl);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
            if (attacker instanceof Player player) {
                //System.out.println(school.name);
                MagicSchool actualSchool = MagicSchool.ARCANE;
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
                SpellPower.Result power = SpellPower.getSpellPower(actualSchool, (LivingEntity) attacker);
                SpellPower.Vulnerability vulnerability = SpellPower.Vulnerability.none;

                vulnerability = SpellPower.getVulnerability(target, actualSchool);

                //SpellPower.Result power = SpellPower.getSpellPower(MagicSchool.ARCANE, (LivingEntity) this.getOwner());
                double amount = modifier * 3 * power.randomValue(vulnerability) / 3;


                //particleMultiplier = power.criticalDamage() + (double)vulnerability.criticalDamageBonus();
                target.invulnerableTime = 0;
                target.hurt(SpellDamageSource.create(actualSchool, (LivingEntity) attacker), (float) amount);

            }
            return super.hurtEnemy(stack,target,attacker);
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
    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {

        if (level != null && level.isClientSide) {

            Player caster = Minecraft.getInstance().player;
            if(caster != null) {
                Multimap<Attribute, AttributeModifier> heldAttributes = caster.getMainHandItem().getAttributeModifiers(EquipmentSlot.MAINHAND);
                Multimap<Attribute, AttributeModifier> itemAttributes = itemStack.getAttributeModifiers(EquipmentSlot.MAINHAND);
                caster.getAttributes().removeAttributeModifiers(heldAttributes);
                caster.getAttributes().addTransientAttributeModifiers(itemAttributes);

                MagicSchool actualSchool = MagicSchool.ARCANE;

                int min = (int) ((0.75)*Math.floor(SpellPower.getSpellPower(actualSchool, caster, itemStack).baseValue()));
                int max = (int) ((0.75)*Math.floor(SpellPower.getSpellPower(actualSchool, caster, itemStack).forcedCriticalValue()));
                list.add(Component.translatable("Attacks with this weapon deal an additional " + min + " - " + max + " " + actualSchool.name().toLowerCase() + " damage on hit.").withStyle(ChatFormatting.LIGHT_PURPLE));
                caster.getAttributes().removeAttributeModifiers(itemAttributes);
                caster.getAttributes().addTransientAttributeModifiers(heldAttributes);
                list.add(Component.translatable("While holding this weapon, you gain Arcane Infusion equal to half of your total FROST and FIRE power.").withStyle(ChatFormatting.LIGHT_PURPLE));

            }
        }
    }
}
