package net.spellbladenext.fabric.items;

import com.google.common.collect.Multimap;
import com.mojang.authlib.minecraft.client.MinecraftClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.spell_engine.api.item.ConfigurableAttributes;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.client.gui.SpellTooltip;
import net.spell_engine.internals.*;
import net.spell_engine.mixin.ItemStackMixin;
import net.spell_engine.utils.TargetHelper;
import net.spell_power.api.MagicSchool;
import net.spell_power.api.SpellPower;
import net.spell_power.api.attributes.EntityAttributes_SpellPower;
import net.spell_power.api.attributes.SpellAttributes;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static net.spell_power.api.SpellPower.*;
import static net.spellbladenext.SpellbladeNext.MOD_ID;
import static net.spellbladenext.fabric.SpellbladesFabric.DIREHEX;

public class HolySymbol extends AxeItem implements ConfigurableAttributes, Vanishable {
    private Multimap<Attribute, AttributeModifier> attributes;

    public HolySymbol(Properties properties) {
        super(Tiers.GOLD, 0, -2.6F, properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext useOnContext) {
        return InteractionResult.PASS;
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {

        if(attacker instanceof SpellCasterEntity caster && attacker instanceof Player player && SpellRegistry.getSpell(new ResourceLocation(MOD_ID,"fervoussmite")) != null && SpellRegistry.getSpell(new ResourceLocation(MOD_ID,"fervoussmiteheal")) != null){
                SpellPower.Result result = new SpellPower.Result(MagicSchool.HEALING, attacker.getAttributeValue(SpellAttributes.POWER.get(MagicSchool.HEALING).attribute), getCriticalChance(player, stack), getCriticalMultiplier(attacker, stack));
                SpellHelper.performImpacts(player.getLevel(), player, target, SpellRegistry.getSpell(new ResourceLocation(MOD_ID, "fervoussmite")),
                        new SpellHelper.ImpactContext(1, 1, null, result, TargetHelper.TargetingMode.DIRECT));

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
    @Environment(EnvType.CLIENT)
    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {
        Spell spell = SpellRegistry.getSpell(new ResourceLocation(MOD_ID,"fervoussmite"));
        if(level != null && level.isClientSide() && SpellRegistry.getSpell(new ResourceLocation(MOD_ID,"fervoussmite")) != null){
            Player caster = Minecraft.getInstance().player;
            if(caster != null) {

                Multimap<Attribute, AttributeModifier> heldAttributes = caster.getMainHandItem().getAttributeModifiers(EquipmentSlot.MAINHAND);
                Multimap<Attribute, AttributeModifier> itemAttributes = itemStack.getAttributeModifiers(EquipmentSlot.MAINHAND);
                caster.getAttributes().removeAttributeModifiers(heldAttributes);
                caster.getAttributes().addTransientAttributeModifiers(itemAttributes);

                MagicSchool actualSchool = MagicSchool.HEALING;
                int min = (int) Math.floor(SpellPower.getSpellPower(actualSchool, caster, itemStack).baseValue());
                int max = (int) Math.floor(SpellPower.getSpellPower(actualSchool, caster, itemStack).forcedCriticalValue());
                list.add(Component.translatable("Attacks with this weapon deal an additional " + min + " - " + max + " holy damage on hit.").withStyle(ChatFormatting.GRAY));
                caster.getAttributes().removeAttributeModifiers(itemAttributes);
                caster.getAttributes().addTransientAttributeModifiers(heldAttributes);


            }
        }
        super.appendHoverText(itemStack, level, list, tooltipFlag);
    }
}
