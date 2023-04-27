package net.spellbladenext.fabric.items.spellblades;

import com.google.common.collect.Multimap;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
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
import net.spellbladenext.SpellbladeNext;
import net.spellbladenext.fabric.config.ItemConfig;
import net.spellbladenext.items.FriendshipBracelet;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

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

                SpellPower.Result power = SpellPower.getSpellPower(actualSchool, (LivingEntity) attacker);
                SpellPower.Vulnerability vulnerability = SpellPower.Vulnerability.none;

                vulnerability = SpellPower.getVulnerability(target, actualSchool);

                //SpellPower.Result power = SpellPower.getSpellPower(MagicSchool.ARCANE, (LivingEntity) this.getOwner());
                double amount = 2 * power.randomValue(vulnerability) / 3;


                //particleMultiplier = power.criticalDamage() + (double)vulnerability.criticalDamageBonus();
                target.invulnerableTime = 0;
                    target.hurt(SpellDamageSource.create(actualSchool, (LivingEntity) attacker), (float) amount);
                switch (actualSchool) {
                    case FIRE -> {
                        if(SpellContainerHelper.containerFromItemStack(stack).spell_ids.contains("spellbladenext:fireoverdrive")){
                            Predicate<Entity> selectionPredicate = (target2) -> {
                                return (TargetHelper.actionAllowed(TargetHelper.TargetingMode.AREA, TargetHelper.Intent.HARMFUL, player, target)
                                        && FriendshipBracelet.PlayerFriendshipPredicate(player,target));
                            };
                            List<Entity> targets = player.getLevel().getEntities(player,player.getBoundingBox().inflate(6),selectionPredicate);

                            if(SpellHelper.ammoForSpell(player, SpellRegistry.getSpell(new ResourceLocation(SpellbladeNext.MOD_ID,"fireoverdrive")),stack).satisfied()) {
                                SpellHelper.performSpell(player.level,player, new ResourceLocation(SpellbladeNext.MOD_ID,"fireoverdrive"), targets,stack, SpellCast.Action.RELEASE, InteractionHand.MAIN_HAND, 0);
                            }
                        }
                        if (attacker.hasEffect(SpellbladeNext.FIREINFUSION.get())){
                            attacker.addEffect(new MobEffectInstance(SpellbladeNext.FIREINFUSION.get(), 100, Math.min(attacker.getEffect(SpellbladeNext.FIREINFUSION.get()).getAmplifier()+1,2)));
                        }
                    }
                    case FROST -> {
                        if(SpellContainerHelper.containerFromItemStack(stack).spell_ids.contains("spellbladenext:frostoverdrive")){
                            Predicate<Entity> selectionPredicate = (target2) -> {
                                return (TargetHelper.actionAllowed(TargetHelper.TargetingMode.AREA, TargetHelper.Intent.HARMFUL, player, target)
                                        && FriendshipBracelet.PlayerFriendshipPredicate(player,target));
                            };
                            List<Entity> targets = player.getLevel().getEntities(player,player.getBoundingBox().inflate(6),selectionPredicate);
                            if(SpellHelper.ammoForSpell(player, SpellRegistry.getSpell(new ResourceLocation(SpellbladeNext.MOD_ID,"frostoverdrive")),stack).satisfied()) {

                                SpellHelper.performSpell(player.level,player, new ResourceLocation(SpellbladeNext.MOD_ID,"frostoverdrive"), targets,stack, SpellCast.Action.RELEASE, InteractionHand.MAIN_HAND, 0);
                            }
                        }
                        if (attacker.hasEffect(SpellbladeNext.FROSTINFUSION.get())) {
                            attacker.addEffect(new MobEffectInstance(SpellbladeNext.FROSTINFUSION.get(), 100, Math.min(attacker.getEffect(SpellbladeNext.FROSTINFUSION.get()).getAmplifier()+1,2)));
                        }
                    }
                    case ARCANE -> {

                        if(SpellContainerHelper.containerFromItemStack(stack).spell_ids.contains("spellbladenext:arcaneoverdrive")){
                            Predicate<Entity> selectionPredicate = (target2) -> {
                                return (TargetHelper.actionAllowed(TargetHelper.TargetingMode.AREA, TargetHelper.Intent.HARMFUL, player, target)
                                        && FriendshipBracelet.PlayerFriendshipPredicate(player,target));
                            };
                            List<Entity> targets = player.getLevel().getEntities(player,player.getBoundingBox().inflate(6),selectionPredicate);

                            if(SpellHelper.ammoForSpell(player, SpellRegistry.getSpell(new ResourceLocation(SpellbladeNext.MOD_ID,"arcaneoverdrive")),stack).satisfied()) {

                                SpellHelper.performSpell(player.level,player, new ResourceLocation(SpellbladeNext.MOD_ID,"arcaneoverdrive"), targets,stack, SpellCast.Action.RELEASE, InteractionHand.MAIN_HAND, 0);
                            }
                        }
                        if (attacker.hasEffect(SpellbladeNext.ARCANEINFUSION.get())){
                            attacker.addEffect(new MobEffectInstance(SpellbladeNext.ARCANEINFUSION.get(), 100, Math.min(attacker.getEffect(SpellbladeNext.ARCANEINFUSION.get()).getAmplifier()+1,2)));
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
