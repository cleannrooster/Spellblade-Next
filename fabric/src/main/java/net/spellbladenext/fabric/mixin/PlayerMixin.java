package net.spellbladenext.fabric.mixin;

import dev.emi.trinkets.TrinketSlot;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketInventory;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.Commands;
import net.minecraft.data.worldgen.Structures;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.phys.Vec3;
import net.spell_engine.api.item.trinket.SpellBookItem;
import net.spell_engine.api.item.trinket.SpellBooks;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.api.spell.SpellContainer;
import net.spell_engine.internals.*;
import net.spell_engine.mixin.PlayerEntityMixin;
import net.spell_power.api.enchantment.Enchantments_SpellPower;
import net.spellbladenext.fabric.SpellbladesFabric;
import net.spellbladenext.fabric.interfaces.PlayerDamageInterface;
import net.spellbladenext.fabric.items.armors.Armors;
import net.spellbladenext.fabric.items.spellblades.Spellblades;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;

import static net.minecraft.util.Mth.sqrt;
import static net.spellbladenext.SpellbladeNext.MOD_ID;
import static net.spellbladenext.fabric.SpellbladesFabric.BAREHANDS;
import static net.spellbladenext.fabric.SpellbladesFabric.UNARMED;

@Mixin(Player.class)
public class PlayerMixin implements PlayerDamageInterface {
    public float damageMultipler = 1F;
    public boolean overrideDamageMultiplier = false;
    public boolean shouldUnFortify = false;
    public int timesincefirsthurt = 0;
    public boolean offhand = false;
    public List<LivingEntity> list = new ArrayList<>();
    @Override
    public void setDamageMultiplier(float f) {
        this.damageMultipler = f;
    }

    @Override
    public void listAdd(LivingEntity entity) {
        list.add(entity);
    }

    @Override
    public void listRefresh() {
        list = new ArrayList<>();
    }

    @Override
    public boolean listContains(LivingEntity entity) {
        return list.contains(entity);
    }

    @Override
    public List<LivingEntity> getList() {
        return list;
    }

    @Override
    public void override(boolean bool) {
        overrideDamageMultiplier = bool;
    }
    @Inject(at = @At("HEAD"), method = "getAttackStrengthScale", cancellable = true)
    private void armor(float f, CallbackInfoReturnable<Float> info) {
        if(this.overrideDamageMultiplier){
            info.setReturnValue(sqrt((this.damageMultipler-0.2F)/0.8F));
        }
    }

    public void shouldUnfortify(boolean bool) {
        this.shouldUnFortify = bool;
    }



    @Inject(at = @At("HEAD"), method = "tick", cancellable = true)
    public void tickMix(CallbackInfo info) {
        Player player = ((Player) (Object) this);



        if(player instanceof SpellCasterEntity entity &&entity.getCooldownManager() != null && !entity.getCooldownManager().isCoolingDown(new ResourceLocation(MOD_ID,"courage")) && !player.getLevel().isClientSide() &&player.tickCount % 80 == 0 && SpellContainerHelper.containerWithProxy(new ItemStack(Spellblades.arcaneBlade.item()),player).spell_ids.contains("spellbladenext:courage")){
            if(player.getEffect(MobEffects.DAMAGE_RESISTANCE) != null) {
                int before = player.getEffect(MobEffects.DAMAGE_RESISTANCE).getAmplifier();
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE,100,Math.min(before+1,2), false,false));
            }
            else{
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE,100,0, false,false));

            }
        }

        if(!player.getLevel().isClientSide() &&this.shouldUnFortify){
            if(SpellContainerHelper.containerWithProxy(new ItemStack(Spellblades.arcaneBlade.item()),player).spell_ids.contains("spellbladenext:courage") &&this.timesincefirsthurt == 0 && player.getEffect(MobEffects.DAMAGE_RESISTANCE) != null) {
                int before = player.getEffect(MobEffects.DAMAGE_RESISTANCE).getAmplifier();
                player.removeEffect(MobEffects.DAMAGE_RESISTANCE);
                if(before > 0){
                    player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE,100,before-1, false,false));
                }
            }
            if(SpellContainerHelper.containerWithProxy(new ItemStack(Spellblades.arcaneBlade.item()),player).spell_ids.contains("spellbladenext:defiance") && this.timesincefirsthurt == 0 && player instanceof SpellCasterEntity entity &&entity.getCooldownManager() != null && !entity.getCooldownManager().isCoolingDown(new ResourceLocation(MOD_ID,"defiance"))){
                if(player.getEffect(MobEffects.REGENERATION) != null){
                    player.addEffect(new MobEffectInstance(MobEffects.REGENERATION,40,Math.min(3,player.getEffect(MobEffects.REGENERATION).getAmplifier()+1),false,false));
                }
                else{
                    player.addEffect(new MobEffectInstance(MobEffects.REGENERATION,40,0,false,false));

                }
            }
            if(this.timesincefirsthurt == 0){
                this.shouldUnfortify(false);
            }
            this.timesincefirsthurt--;

        }
    }


        @Inject(at = @At("HEAD"), method = "actuallyHurt", cancellable = true)
    protected void actuallyHurtMix(DamageSource damageSource, float f, CallbackInfo callbackInfo) {
        Player player = ((Player) (Object) this);
        if(!player.isDamageSourceBlocked(damageSource)) {
            if (!player.getLevel().isClientSide() && !player.isInvulnerableTo(damageSource) && SpellContainerHelper.containerWithProxy(new ItemStack(Spellblades.arcaneBlade.item()), player).spell_ids.contains("spellbladenext:courage")) {
                this.shouldUnfortify(true);
                this.timesincefirsthurt = 1;
            }
            if (!player.getLevel().isClientSide() && !player.isInvulnerableTo(damageSource) && SpellContainerHelper.containerWithProxy(new ItemStack(Spellblades.arcaneBlade.item()), player).spell_ids.contains("spellbladenext:defiance")) {
                this.shouldUnfortify(true);
                this.timesincefirsthurt = 1;
            }
        }
    }

}
