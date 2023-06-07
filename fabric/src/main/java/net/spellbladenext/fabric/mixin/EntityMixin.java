package net.spellbladenext.fabric.mixin;

import com.google.common.collect.Multimap;
import net.bettercombat.mixin.LivingEntityMixin;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.AbstractSkullBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.WaterFluid;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.spell_engine.api.item.trinket.SpellBookItem;
import net.spell_engine.api.item.trinket.SpellBooks;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.client.gui.SpellTooltip;
import net.spell_engine.entity.SpellProjectile;
import net.spell_engine.internals.*;
import net.spell_engine.mixin.PlayerEntityMixin;
import net.spell_engine.mixin.client.ClientPlayerEntityMixin;
import net.spell_engine.particle.ParticleHelper;
import net.spell_engine.utils.TargetHelper;
import net.spell_power.SpellPowerMod;
import net.spell_power.api.MagicSchool;
import net.spell_power.api.SpellDamageSource;
import net.spell_power.api.SpellPower;
import net.spell_power.api.attributes.SpellAttributes;
import net.spellbladenext.SpellbladeNext;
import net.spellbladenext.fabric.SpellbladesFabric;
import net.spellbladenext.fabric.callbacks.HurtCallback;
import net.spellbladenext.fabric.config.ItemConfig;
import net.spellbladenext.fabric.dim.MagusDimension;
import net.spellbladenext.fabric.interfaces.PlayerDamageInterface;
import net.spellbladenext.fabric.items.armors.Armors;
import net.spellbladenext.fabric.items.spellblades.Claymores;
import net.spellbladenext.fabric.items.spellblades.Spellblade;
import net.spellbladenext.fabric.items.spellblades.Spellblades;
import net.spellbladenext.items.FriendshipBracelet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;
import java.util.function.Predicate;

import static net.spell_engine.internals.SpellHelper.impactTargetingMode;
import static net.spellbladenext.SpellbladeNext.MOD_ID;

@Mixin(value = LivingEntity.class, priority = 9999999)
public class EntityMixin {

    @Inject(at = @At("HEAD"), method = "canStandOnFluid", cancellable = true)
    private void hurt2(FluidState fluidState, CallbackInfoReturnable<Boolean> info) {
        if(fluidState.getType()==Fluids.WATER){
        LivingEntity entity = (LivingEntity) (Object) this;
        if(entity.getLevel().dimension().equals(SpellbladeNext.DIMENSIONKEY) ) {
            info.setReturnValue(true);
        }
        }
    }
    @Inject(at = @At("HEAD"), method = "getAttributeValue", cancellable = true)
    public void getAttributeValueMIX(Attribute attribute, CallbackInfoReturnable<Double> infoReturnable) {
        LivingEntity living = ((LivingEntity) (Object) this);
        if(living instanceof Player player && player instanceof SpellCasterEntity entity && entity.getCurrentSpellId() != null && entity.getCurrentSpellId().equals(new ResourceLocation(MOD_ID,"dualwield_whirlwind"))) {
            EquipmentSlot equipmentSlot = EquipmentSlot.MAINHAND;
            if (attribute == Attributes.ATTACK_DAMAGE) {
                Multimap<Attribute, AttributeModifier> heldAttributes = player.getOffhandItem().getAttributeModifiers(EquipmentSlot.MAINHAND);
                Multimap<Attribute, AttributeModifier> main = player.getMainHandItem().getAttributeModifiers(EquipmentSlot.MAINHAND);
                if(heldAttributes == null){
                    return;
                }
                List<AttributeModifier> mainattributes = main.get(attribute).stream().toList();

                List<AttributeModifier> attributeModifiers = heldAttributes.get(attribute).stream().toList();
                double value1 = player.getAttributes().getValue(attribute);
                double value = player.getAttributes().getValue(attribute);

                for(AttributeModifier attributeModifier : mainattributes){
                    if(attributeModifier.getOperation() == AttributeModifier.Operation.ADDITION) {
                        value1 += attributeModifier.getAmount();
                    }

                }
                for(AttributeModifier attributeModifier : attributeModifiers) {
                    if (attributeModifier.getOperation() == AttributeModifier.Operation.MULTIPLY_BASE) {
                        value1 *= attributeModifier.getAmount();
                    }
                }
                for(AttributeModifier attributeModifier : attributeModifiers) {
                    if (attributeModifier.getOperation() == AttributeModifier.Operation.MULTIPLY_TOTAL) {
                        value1 *= attributeModifier.getAmount();
                    }
                }

                for(AttributeModifier attributeModifier : attributeModifiers){
                    if(attributeModifier.getOperation() == AttributeModifier.Operation.ADDITION) {
                        value += attributeModifier.getAmount();
                    }

                }
                for(AttributeModifier attributeModifier : attributeModifiers) {
                    if (attributeModifier.getOperation() == AttributeModifier.Operation.MULTIPLY_BASE) {
                        value *= attributeModifier.getAmount();
                    }
                }
                for(AttributeModifier attributeModifier : attributeModifiers) {
                    if (attributeModifier.getOperation() == AttributeModifier.Operation.MULTIPLY_TOTAL) {
                        value *= attributeModifier.getAmount();
                    }
                }
                infoReturnable.setReturnValue((value+value1)*0.5);
            }
            if (attribute == Attributes.ATTACK_DAMAGE) {
                Multimap<Attribute, AttributeModifier> heldAttributes = player.getOffhandItem().getAttributeModifiers(EquipmentSlot.MAINHAND);
                Multimap<Attribute, AttributeModifier> main = player.getMainHandItem().getAttributeModifiers(EquipmentSlot.MAINHAND);
                List<AttributeModifier> mainattributes = main.get(attribute).stream().toList();

                List<AttributeModifier> attributeModifiers = heldAttributes.get(attribute).stream().toList();
                double value1 = player.getAttributes().getValue(attribute);
                double value = player.getAttributes().getValue(attribute);

                for(AttributeModifier attributeModifier : mainattributes){
                    if(attributeModifier.getOperation() == AttributeModifier.Operation.ADDITION) {
                        value1 += attributeModifier.getAmount();
                    }

                }
                for(AttributeModifier attributeModifier : attributeModifiers) {
                    if (attributeModifier.getOperation() == AttributeModifier.Operation.MULTIPLY_BASE) {
                        value1 *= attributeModifier.getAmount();
                    }
                }
                for(AttributeModifier attributeModifier : attributeModifiers) {
                    if (attributeModifier.getOperation() == AttributeModifier.Operation.MULTIPLY_TOTAL) {
                        value1 *= attributeModifier.getAmount();
                    }
                }

                for(AttributeModifier attributeModifier : attributeModifiers){
                    if(attributeModifier.getOperation() == AttributeModifier.Operation.ADDITION) {
                        value += attributeModifier.getAmount();
                    }

                }
                for(AttributeModifier attributeModifier : attributeModifiers) {
                    if (attributeModifier.getOperation() == AttributeModifier.Operation.MULTIPLY_BASE) {
                        value *= attributeModifier.getAmount();
                    }
                }
                for(AttributeModifier attributeModifier : attributeModifiers) {
                    if (attributeModifier.getOperation() == AttributeModifier.Operation.MULTIPLY_TOTAL) {
                        value *= attributeModifier.getAmount();
                    }
                }
                infoReturnable.setReturnValue((value+value1)*0.5);            }

        }
    }

        @ModifyVariable(at = @At("HEAD"), method = "setHealth")
    public float setHealthMix(float f,float original) {
        LivingEntity player = ((LivingEntity) (Object) this);

        if (!player.getLevel().isClientSide() && f <= 0 && player.getEffect(SpellbladesFabric.DETERM.get()) != null && player.getEffect(SpellbladesFabric.DETERM.get()).getDuration() != 1) {
            return 0.1F;
        }
        if(player instanceof Player player1 && !player.getLevel().isClientSide() && f <= 0&& SpellContainerHelper.containerWithProxy(new ItemStack(Spellblades.arcaneBlade.item()),player1).spell_ids.contains("spellbladenext:determination") && player instanceof SpellCasterEntity entity && SpellRegistry.getSpell(new ResourceLocation(MOD_ID,"determination")) != null && entity.getCooldownManager() != null &&  !entity.getCooldownManager().isCoolingDown(new ResourceLocation(MOD_ID,"determination"))){
            Spell spell = SpellRegistry.getSpell(new ResourceLocation(MOD_ID,"determination"));
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST,160,2,true,true));
            player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED,160,2,true,true));
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED,160,2,true,true));
            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION,160,2,true,true));

            player.addEffect(new MobEffectInstance(SpellbladesFabric.DETERM.get(),160,0,true,true));
            entity.getCooldownManager().set(new ResourceLocation(MOD_ID,"determination"),1800);
            return 0.1F;
        }
        return f;
    }

        @Inject(at = @At("HEAD"), method = "blockUsingShield", cancellable = true)
    private void block(LivingEntity living, CallbackInfo info) {
        LivingEntity player = ((LivingEntity) (Object) this);

        if(player instanceof ServerPlayer actualplayer && EnchantmentHelper.getEnchantmentLevel(SpellbladesFabric.SPEHHSHIELD.get(),player) > 0 && actualplayer instanceof SpellCasterEntity caster ) {
            {
                ServerPlayNetworking.send((ServerPlayer) actualplayer, new ResourceLocation(MOD_ID,"blockspell"), PacketByteBufs.empty());
            }
        }
    }
    @Inject(at = @At("HEAD"), method = "isBlocking", cancellable = true)
    private void blocking( final CallbackInfoReturnable<Boolean> info) {
        LivingEntity player2 = ((LivingEntity) (Object) this);
        if (player2 instanceof ServerPlayer actualplayer && actualplayer instanceof SpellCasterEntity caster) {

            if (SpellRegistry.getSpell(new ResourceLocation(MOD_ID, "riposte")) != null && Objects.equals(caster.getCurrentSpellId(), new ResourceLocation(MOD_ID, "riposte"))) {
                info.setReturnValue(true);
            }
            if (player2.getOffhandItem().getItem() instanceof ShieldItem && SpellRegistry.getSpell(new ResourceLocation(MOD_ID, "opportunity")) != null && Objects.equals(caster.getCurrentSpellId(), new ResourceLocation(MOD_ID, "opportunity"))) {
                info.setReturnValue(true);
            }
            if (SpellRegistry.getSpell(new ResourceLocation(MOD_ID, "roll")) != null && Objects.equals(caster.getCurrentSpellId(), new ResourceLocation(MOD_ID, "roll"))) {
                info.setReturnValue(true);
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "actuallyHurt", cancellable = true)
    private void actual(final DamageSource player,  float g, final CallbackInfo info) {
        LivingEntity player2 = ((LivingEntity) (Object) this);
        if (!(player instanceof SpellDamageSource damageSource && damageSource.getMagicSchool() == MagicSchool.HEALING) && player.getEntity() instanceof Player actualplayer) {
            if (actualplayer.hasEffect(SpellbladesFabric.FERVOR.get())) {
                player2.invulnerableTime = 0;

                ((LivingEntity) (Object) this).hurt(SpellDamageSource.player(MagicSchool.HEALING, actualplayer), g * 0.25F);
                player2.invulnerableTime = 0;

                actualplayer.addEffect(new MobEffectInstance(SpellbladesFabric.FERVOR.get(), 200, 0));

            }
        }

        if (player.getEntity() instanceof Player player1 && !(player instanceof SpellDamageSource)) {
            float f = (float) (0.5*g);
            LivingEntity target = player2;

            if (player1.getMainHandItem().getItem() instanceof Spellblade spellblade) {
                ArrayList<ItemConfig.SpellAttribute> spellattributes = spellblade.getMagicSchools();
                for (ItemConfig.SpellAttribute school : spellattributes) {

                    MagicSchool actualSchool = MagicSchool.fromAttributeId(new ResourceLocation(SpellPowerMod.ID, school.name));
                    if(player1.getAttribute(SpellAttributes.POWER.get(actualSchool).attribute) != null) {

                        Set<AttributeModifier> base = player1.getAttribute(SpellAttributes.POWER.get(actualSchool).attribute).getModifiers(AttributeModifier.Operation.MULTIPLY_BASE);
                        Set<AttributeModifier> total = player1.getAttribute(SpellAttributes.POWER.get(actualSchool).attribute).getModifiers(AttributeModifier.Operation.MULTIPLY_TOTAL);
                        double modifierbase = 1;
                        double modifiertotal = 1;
                        for (AttributeModifier bases : base) {
                            modifierbase += bases.getAmount();
                        }
                        for (AttributeModifier totals : total) {
                            modifiertotal *= 1 + totals.getAmount();
                        }
                        double modifier = modifierbase * modifiertotal;
                        target.invulnerableTime = 0;
                        target.hurt(SpellDamageSource.create(actualSchool, player1), f * (float) modifier);
                        target.invulnerableTime = 0;
                    }
                }

            }
            if (player1.getMainHandItem().getItem() instanceof Claymores spellblade && player1.getMainHandItem().getItem() != Spellblades.mulberrysword.item()) {
                ArrayList<ItemConfig.SpellAttribute> spellattributes = spellblade.getMagicSchools();
                for (ItemConfig.SpellAttribute school : spellattributes) {
                    MagicSchool actualSchool = MagicSchool.fromAttributeId(new ResourceLocation(SpellPowerMod.ID, school.name));
                    if(player1.getAttribute(SpellAttributes.POWER.get(actualSchool).attribute) != null) {

                        Set<AttributeModifier> base = player1.getAttribute(SpellAttributes.POWER.get(actualSchool).attribute).getModifiers(AttributeModifier.Operation.MULTIPLY_BASE);
                        Set<AttributeModifier> total = player1.getAttribute(SpellAttributes.POWER.get(actualSchool).attribute).getModifiers(AttributeModifier.Operation.MULTIPLY_TOTAL);
                        double modifierbase = 1;
                        double modifiertotal = 1;
                        for (AttributeModifier bases : base) {
                            modifierbase += bases.getAmount();
                        }
                        for (AttributeModifier totals : total) {
                            modifiertotal *= 1 + totals.getAmount();
                        }
                        double modifier = modifierbase * modifiertotal;
                        target.invulnerableTime = 0;
                        target.hurt(SpellDamageSource.create(actualSchool, player1), f * (float) modifier);
                        target.invulnerableTime = 0;
                    }
                }

            }

            ItemStack stack = player1.getMainHandItem();
            if (SpellContainerHelper.containerWithProxy(stack, player1) != null && SpellContainerHelper.containerWithProxy(stack, player1).spell_ids.contains("spellbladenext:frostoverdrive")) {
                Spell spell = SpellRegistry.getSpell(new ResourceLocation(SpellbladeNext.MOD_ID, "frostoverdrive"));

                Predicate<Entity> selectionPredicate = (target2) -> {
                    return (TargetHelper.actionAllowed(TargetHelper.TargetingMode.AREA, TargetHelper.Intent.HARMFUL, player1, target)
                            && FriendshipBracelet.PlayerFriendshipPredicate(player1, target));
                };
                if(player1 instanceof SpellCasterEntity entity && !entity.getCooldownManager().isCoolingDown(new ResourceLocation(SpellbladeNext.MOD_ID, "frostoverdrive"))) {
                    List<Entity> targets = player1.getLevel().getEntities(player1, player1.getBoundingBox().inflate(6), selectionPredicate);
                    int i = 0;
                    if(entity.getCurrentSpellId() != null) {
                        LivingEntity living = (LivingEntity) entity;
                        i = (int) (entity.getCurrentCastProgress() * SpellHelper.getCastDuration(living, SpellRegistry.getSpell(entity.getCurrentSpellId())));
                    }
                    if(spell != null) {
                        SpellHelper.ImpactContext context = new SpellHelper.ImpactContext(1.0F, 1.0F, (Vec3) null, SpellPower.getSpellPower(spell.school, player1), impactTargetingMode(spell));

                            for (Entity target1 : targets) {
                                SpellHelper.performImpacts(player1.getLevel(), player1, target1, SpellRegistry.getSpell(new ResourceLocation(SpellbladeNext.MOD_ID, "frostoverdrive")), new SpellHelper.ImpactContext());
                            }
                            entity.getCooldownManager().set(new ResourceLocation(SpellbladeNext.MOD_ID, "frostoverdrive"), (int) (20*SpellHelper.getCooldownDuration(player1,spell)));
                            ParticleHelper.sendBatches(player1, spell.release.particles);

                    }
                }
            }

            if (SpellContainerHelper.containerWithProxy(stack, player1) != null && SpellContainerHelper.containerWithProxy(stack, player1).spell_ids.contains("spellbladenext:fireoverdrive")) {
                Predicate<Entity> selectionPredicate = (target2) -> {
                    return (TargetHelper.actionAllowed(TargetHelper.TargetingMode.AREA, TargetHelper.Intent.HARMFUL, player1, target)
                            && FriendshipBracelet.PlayerFriendshipPredicate(player1, target));
                };
                Spell spell = SpellRegistry.getSpell(new ResourceLocation(SpellbladeNext.MOD_ID, "fireoverdrive"));

                if(player1 instanceof SpellCasterEntity entity && !entity.getCooldownManager().isCoolingDown(new ResourceLocation(SpellbladeNext.MOD_ID, "fireoverdrive"))) {
                    int i = 0;
                    List<Entity> targets = player1.getLevel().getEntities(player1, player1.getBoundingBox().inflate(6), selectionPredicate);

                    if(entity.getCurrentSpellId() != null) {
                        LivingEntity living = (LivingEntity) entity;
                        i = (int) (entity.getCurrentCastProgress() * SpellHelper.getCastDuration(living, SpellRegistry.getSpell(entity.getCurrentSpellId())));
                    }
                    if(spell != null) {
                        SpellHelper.ImpactContext context = new SpellHelper.ImpactContext(1.0F, 1.0F, (Vec3) null, SpellPower.getSpellPower(spell.school, player1), impactTargetingMode(spell));

                            for (Entity target1 : targets) {
                                SpellHelper.performImpacts(player1.getLevel(), player1, target1, SpellRegistry.getSpell(new ResourceLocation(SpellbladeNext.MOD_ID, "fireoverdrive")), new SpellHelper.ImpactContext());
                            }
                            entity.getCooldownManager().set(new ResourceLocation(SpellbladeNext.MOD_ID, "fireoverdrive"), (int) (20*SpellHelper.getCooldownDuration(player1,spell)));
                            ParticleHelper.sendBatches(player1, spell.release.particles);


                    }
                }
            }

            if (SpellContainerHelper.containerWithProxy(stack, player1) != null && SpellContainerHelper.containerWithProxy(stack, player1).spell_ids.contains("spellbladenext:arcaneoverdrive")) {
                Predicate<Entity> selectionPredicate = (target2) -> {
                    return (TargetHelper.actionAllowed(TargetHelper.TargetingMode.AREA, TargetHelper.Intent.HARMFUL, player1, target)
                            && FriendshipBracelet.PlayerFriendshipPredicate(player1, target));
                };
                Spell spell = SpellRegistry.getSpell(new ResourceLocation(SpellbladeNext.MOD_ID, "arcaneoverdrive"));
                if(player1 instanceof SpellCasterEntity entity && !entity.getCooldownManager().isCoolingDown(new ResourceLocation(SpellbladeNext.MOD_ID, "arcaneoverdrive"))) {
                    int i = 0;
                    List<Entity> targets = player1.getLevel().getEntities(player1, player1.getBoundingBox().inflate(6), selectionPredicate);

                    if(entity.getCurrentSpellId() != null) {
                        LivingEntity living = (LivingEntity) entity;
                        i = (int) (entity.getCurrentCastProgress() * SpellHelper.getCastDuration(living, SpellRegistry.getSpell(entity.getCurrentSpellId())));
                    }
                    if(spell != null) {
                        SpellHelper.ImpactContext context = new SpellHelper.ImpactContext(1.0F, 1.0F, (Vec3) null, SpellPower.getSpellPower(spell.school, player1), impactTargetingMode(spell));

                            for (Entity target1 : targets) {
                                SpellHelper.performImpacts(player1.getLevel(), player1, target1, SpellRegistry.getSpell(new ResourceLocation(SpellbladeNext.MOD_ID, "arcaneoverdrive")), new SpellHelper.ImpactContext());
                            }
                            entity.getCooldownManager().set(new ResourceLocation(SpellbladeNext.MOD_ID, "arcaneoverdrive"), (int) (20*SpellHelper.getCooldownDuration(player1,spell)));
                            ParticleHelper.sendBatches(player1, spell.release.particles);


                    }
                }
            }
        }
    }
        @Inject(at = @At("HEAD"), method = "hurt", cancellable = true)
    private void hurtreal(final DamageSource player,  float f, final CallbackInfoReturnable<Boolean> info) {
            InteractionResult result = HurtCallback.EVENT.invoker().interact(player, f);

            LivingEntity player2 = ((LivingEntity) (Object) this);
            if(player2 instanceof SpellCasterEntity entity && entity.getCurrentSpellId() != null &&  entity.getCurrentSpellId().equals(new ResourceLocation(MOD_ID,"monkeyslam")) && entity.getCurrentCastProgress() > 15F/160F){
                info.setReturnValue(false);
            }
            if (player2 instanceof ServerPlayer actualplayer && actualplayer instanceof SpellCasterEntity caster) {
                {
                    ItemStack stack = actualplayer.getMainHandItem();

                    if (SpellRegistry.getSpell(new ResourceLocation(MOD_ID, "riposte")) != null && Objects.equals(caster.getCurrentSpellId(), new ResourceLocation(MOD_ID, "riposte"))) {
                        Spell spell = caster.getCurrentSpell();
                        if (player2.isDamageSourceBlocked(player) && player2.getMainHandItem() != null && player.getEntity() != null && player2.isDamageSourceBlocked(player)) {
                            caster.clearCasting();
                            List<Entity> list = TargetHelper.targetsFromArea(player2, spell.range, spell.release.target.area, null);
                            if(player.getDirectEntity() != null && !list.contains(player.getDirectEntity()) && player.getDirectEntity() instanceof LivingEntity living && living.distanceTo(player2) <= 3){
                                list.add(player.getDirectEntity());
                            }
                            SpellHelper.performSpell(player2.getLevel(), actualplayer, new ResourceLocation(MOD_ID, "riposte"), list,
                                    player2.getMainHandItem(), SpellCast.Action.RELEASE, InteractionHand.MAIN_HAND, 0);
                            info.cancel();

                        }
                    }
                    if (SpellRegistry.getSpell(new ResourceLocation(MOD_ID, "roll")) != null && Objects.equals(caster.getCurrentSpellId(), new ResourceLocation(MOD_ID, "roll"))) {
                        Spell spell = caster.getCurrentSpell();
                        if (player2.getMainHandItem() != null && player.getEntity() != null) {
                            if (player.getDirectEntity() instanceof LivingEntity living) {
                                caster.clearCasting();
                                List<Entity> entities = TargetHelper.targetsFromArea(player2, spell.range, spell.release.target.area, null);
                                if (!entities.contains(living) && living.distanceTo(player2) <= 3) {
                                    entities.add(living);
                                }
                                SpellHelper.performSpell(player2.getLevel(), actualplayer, new ResourceLocation(MOD_ID, "roll"), entities,
                                        player2.getMainHandItem(), SpellCast.Action.RELEASE, InteractionHand.MAIN_HAND, 0);
                            }
                            info.cancel();

                        }
                    }
                }
            }
            if (result == InteractionResult.FAIL) {
                info.cancel();
            }

        }



}