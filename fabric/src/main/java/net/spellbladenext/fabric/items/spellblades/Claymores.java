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
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.spell_engine.api.item.ConfigurableAttributes;
import net.spell_engine.api.item.weapon.StaffItem;
import net.spell_engine.internals.SpellCast;
import net.spell_engine.internals.SpellContainerHelper;
import net.spell_engine.internals.SpellHelper;
import net.spell_engine.internals.SpellRegistry;
import net.spell_engine.utils.TargetHelper;
import net.spell_power.SpellPowerMod;
import net.spell_power.api.MagicSchool;
import net.spell_power.api.SpellDamageSource;
import net.spell_power.api.SpellPower;
import net.spellbladenext.SpellbladeNext;
import net.spellbladenext.fabric.SpellbladesFabric;
import net.spellbladenext.fabric.config.ItemConfig;
import net.spellbladenext.items.FriendshipBracelet;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class Claymores extends SwordItem implements ConfigurableAttributes {
    private final ArrayList<ItemConfig.SpellAttribute> school;
    private Multimap<Attribute, AttributeModifier> attributes;

    public Claymores(Tier material, Multimap<Attribute, AttributeModifier> attributes, Item.Properties settings, ArrayList<ItemConfig.SpellAttribute> school) {
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
                double amount = 3 * power.randomValue(vulnerability) / 3;


                //particleMultiplier = power.criticalDamage() + (double)vulnerability.criticalDamageBonus();
                target.invulnerableTime = 0;
                    target.hurt(SpellDamageSource.create(actualSchool, (LivingEntity) attacker), (float) amount);
                if(stack.getItem() == Spellblades.mulberrysword.item()){
                    target.hurt(SpellDamageSource.create(MagicSchool.HEALING, (LivingEntity) attacker), (float) amount/4);
                }
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

        if(level != null && level.isClientSide ){

            Player caster = Minecraft.getInstance().player;
            if(caster != null) {

                Multimap<Attribute, AttributeModifier> heldAttributes = caster.getMainHandItem().getAttributeModifiers(EquipmentSlot.MAINHAND);
                Multimap<Attribute, AttributeModifier> itemAttributes = itemStack.getAttributeModifiers(EquipmentSlot.MAINHAND);
                caster.getAttributes().removeAttributeModifiers(heldAttributes);
                caster.getAttributes().addTransientAttributeModifiers(itemAttributes);

                for (ItemConfig.SpellAttribute school : getMagicSchools()) {
                    MagicSchool actualSchool = MagicSchool.fromAttributeId(new ResourceLocation(SpellPowerMod.ID, school.name));

                    int min = (int) Math.floor(SpellPower.getSpellPower(actualSchool, caster, itemStack).baseValue());
                    int max = (int) Math.floor(SpellPower.getSpellPower(actualSchool, caster, itemStack).forcedCriticalValue());
                    list.add(Component.translatable("Attacks with this weapon deal an additional " + min + " - " + max + " " + actualSchool.name().toLowerCase() + " damage on hit.").withStyle(ChatFormatting.GRAY));
                }
                if (itemStack.getItem() == Spellblades.mulberrysword.item()) {

                    int min = (int) Math.floor(SpellPower.getSpellPower(MagicSchool.FIRE, caster, itemStack).baseValue() * 0.25);
                    int max = (int) Math.floor(SpellPower.getSpellPower(MagicSchool.FIRE, caster, itemStack).forcedCriticalValue() * 0.25);
                    list.add(Component.translatable("Attacks with this weapon deal an additional " + min + " - " + max + " " + MagicSchool.HEALING.name().toLowerCase() + " damage on hit.").withStyle(ChatFormatting.GRAY));

                }
            }
        }
        if(itemStack.getItem() == Spellblades.mulberrysword.item()){

            list.add(Component.translatable("If you have at least 10 Fire Spell Power, you can summon this").withStyle(ChatFormatting.GRAY));
            list.add(Component.translatable("item by shift right clicking with an empty hand.").withStyle(ChatFormatting.GRAY));

        }
        super.appendHoverText(itemStack, level, list, tooltipFlag);
    }

}
