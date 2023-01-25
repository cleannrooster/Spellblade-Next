package net.spellbladenext.items.spellblades;

import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.spell_engine.api.item.StaffItem;
import net.spell_power.SpellPowerMod;
import net.spell_power.api.MagicSchool;
import net.spell_power.api.SpellDamageSource;
import net.spell_power.api.SpellPower;
import net.spellbladenext.SpellbladeNext;
import net.spellbladenext.config.ItemConfig;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Spellblade extends StaffItem {
    private final ArrayList<ItemConfig.SpellAttribute> school;

    public Spellblade(Tier material, Multimap<Attribute, AttributeModifier> attributes, Properties settings, ArrayList<ItemConfig.SpellAttribute> school) {
        super(material,  settings);
        this.school = school;

            this.setAttributes(attributes);
    }
    public ArrayList<ItemConfig.SpellAttribute> getMagicSchools(){
        return this.school;
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        for(ItemConfig.SpellAttribute school: this.getMagicSchools().stream().toList()) {
            //System.out.println(school.name);
            MagicSchool actualSchool = MagicSchool.fromAttributeId(new ResourceLocation(SpellPowerMod.ID,school.name));

            SpellPower.Result power = SpellPower.getSpellPower(actualSchool, (LivingEntity) attacker);
            SpellPower.Vulnerability vulnerability = SpellPower.Vulnerability.none;

            vulnerability = SpellPower.getVulnerability(target, actualSchool);

            //SpellPower.Result power = SpellPower.getSpellPower(MagicSchool.ARCANE, (LivingEntity) this.getOwner());
            double amount = 2*power.randomValue(vulnerability)/3;


            //particleMultiplier = power.criticalDamage() + (double)vulnerability.criticalDamageBonus();
            target.invulnerableTime = 0;

            target.hurt(SpellDamageSource.create(actualSchool, (LivingEntity) attacker), (float) amount);
            switch(actualSchool){
                case FIRE -> {
                    if(!attacker.hasEffect(SpellbladeNext.FIREINFUSION.get())) {
                        attacker.addEffect(new MobEffectInstance(SpellbladeNext.FIREINFUSION.get(), 80, 0, false, false));
                    }
                    else{
                        attacker.addEffect(new MobEffectInstance(SpellbladeNext.FIREINFUSION.get(), 80, 1, false, false));
                    }
                }
                case FROST -> {
                    if(!attacker.hasEffect(SpellbladeNext.FROSTINFUSION.get())) {
                        attacker.addEffect(new MobEffectInstance(SpellbladeNext.FROSTINFUSION.get(), 80, 0, false, false));
                    }
                    else{
                        attacker.addEffect(new MobEffectInstance(SpellbladeNext.FROSTINFUSION.get(), 80, 1, false, false));
                    }
                }
                case ARCANE -> {
                    if(!attacker.hasEffect(SpellbladeNext.ARCANEINFUSION.get())) {
                        attacker.addEffect(new MobEffectInstance(SpellbladeNext.ARCANEINFUSION.get(), 80, 0, false, false));
                    }
                    else{
                        attacker.addEffect(new MobEffectInstance(SpellbladeNext.ARCANEINFUSION.get(), 80, 1, false, false));
                    }
                }
            }

        }
        stack.hurtAndBreak(1, attacker, (e) -> {
            e.broadcastBreakEvent(EquipmentSlot.MAINHAND);
        });
        return true;

    }
/*
    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {
            if((Minecraft.getInstance().player.getMainHandItem().getItem() == this || Minecraft.getInstance().player.getOffhandItem().getItem() == this)) {
                for (ItemConfig.SpellAttribute school : this.getMagicSchools().stream().toList()) {
                    MagicSchool actualSchool = MagicSchool.fromAttributeId(new ResourceLocation(SpellPowerMod.ID, school.name));

                    SpellPower.Result power = SpellPower.getSpellPower(actualSchool, (LivingEntity) Minecraft.getInstance().player);
                    SpellPower.Vulnerability vulnerability = SpellPower.Vulnerability.none;

                    //SpellPower.Result power = SpellPower.getSpellPower(MagicSchool.ARCANE, (LivingEntity) this.getOwner());
                    double amount = 2 * power.nonCriticalValue() / 3;
                    double amount2 = 2 * power.forcedCriticalValue() / 3;

                    ChatFormatting chatFormatting = ChatFormatting.GRAY;
                    switch (actualSchool) {
                        case FIRE -> {
                            chatFormatting = ChatFormatting.RED;
                        }
                        case FROST -> {
                            chatFormatting = ChatFormatting.AQUA;

                        }
                        case ARCANE -> {
                            chatFormatting = ChatFormatting.DARK_PURPLE;
                        }
                    }
                    MutableComponent component = Component.translatable("Adds " + amount + " to " + amount2 + " damage to attacks with this weapon.").withStyle(chatFormatting);
                    list.add(component);
                }
            }
            else{
                list.add(Component.translatable("Does additional damage on hit when equipped.").withStyle(ChatFormatting.GRAY));
            }

        super.appendHoverText(itemStack, level, list, tooltipFlag);
    }*/
}
