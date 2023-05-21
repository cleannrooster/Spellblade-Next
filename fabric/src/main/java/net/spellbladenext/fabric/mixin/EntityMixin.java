package net.spellbladenext.fabric.mixin;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.AbstractSkullBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.WaterFluid;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.entity.SpellProjectile;
import net.spell_engine.internals.*;
import net.spell_engine.mixin.LivingEntityMixin;
import net.spell_engine.mixin.PlayerEntityMixin;
import net.spell_engine.mixin.client.ClientPlayerEntityMixin;
import net.spell_engine.utils.TargetHelper;
import net.spell_power.api.MagicSchool;
import net.spell_power.api.SpellDamageSource;
import net.spellbladenext.SpellbladeNext;
import net.spellbladenext.fabric.SpellbladesFabric;
import net.spellbladenext.fabric.callbacks.HurtCallback;
import net.spellbladenext.fabric.dim.MagusDimension;
import net.spellbladenext.fabric.items.armors.Armors;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Objects;

import static net.spellbladenext.SpellbladeNext.MOD_ID;

@Mixin(LivingEntity.class)
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
            if (SpellRegistry.getSpell(new ResourceLocation(MOD_ID, "roll")) != null && Objects.equals(caster.getCurrentSpellId(), new ResourceLocation(MOD_ID, "roll"))) {
                info.setReturnValue(true);
            }
        }
    }
        @Inject(at = @At("HEAD"), method = "hurt", cancellable = true)
    private void hurtreal(final DamageSource player, final float f, final CallbackInfoReturnable<Boolean> info) {
        InteractionResult result = HurtCallback.EVENT.invoker().interact(player, f);
        if(!(player instanceof SpellDamageSource damageSource &&  damageSource.getMagicSchool() == MagicSchool.HEALING) && player.getEntity() instanceof Player actualplayer){
            if( actualplayer.hasEffect(SpellbladesFabric.FERVOR.get())){
                ((LivingEntity) (Object) this).hurt(SpellDamageSource.player(MagicSchool.HEALING,actualplayer),f*0.25F);
                actualplayer.addEffect(new MobEffectInstance(SpellbladesFabric.FERVOR.get(),200,0));

            }
        }

        LivingEntity player2 = ((LivingEntity) (Object) this);
        if(player2 instanceof ServerPlayer actualplayer && actualplayer instanceof SpellCasterEntity caster ) {
            {
                if(SpellRegistry.getSpell(new ResourceLocation(MOD_ID, "riposte")) != null && Objects.equals(caster.getCurrentSpellId(), new ResourceLocation(MOD_ID, "riposte"))) {
                    Spell spell = caster.getCurrentSpell();
                    if(player2.isDamageSourceBlocked(player) && player2.getMainHandItem() != null && player.getEntity() != null && player2.isDamageSourceBlocked(player)) {
                        caster.clearCasting();

                        SpellHelper.performSpell(player2.getLevel(), actualplayer, new ResourceLocation(MOD_ID, "riposte"), TargetHelper.targetsFromArea(player2, spell.range, spell.release.target.area, null),
                                player2.getMainHandItem(), SpellCast.Action.RELEASE, InteractionHand.MAIN_HAND, 0);
                        info.cancel();

                    }
                }
                if(SpellRegistry.getSpell(new ResourceLocation(MOD_ID, "roll")) != null && Objects.equals(caster.getCurrentSpellId(), new ResourceLocation(MOD_ID, "roll"))) {
                    Spell spell = caster.getCurrentSpell();
                    if(player2.getMainHandItem() != null && player.getEntity() != null) {
                        if(player.getDirectEntity() instanceof LivingEntity living) {
                            caster.clearCasting();
                            List<Entity> entities = TargetHelper.targetsFromArea(player2, spell.range, spell.release.target.area, null);
                            if(!entities.contains(living)) {
                                entities.add(living);
                            }
                            SpellHelper.performSpell(player2.getLevel(), actualplayer, new ResourceLocation(MOD_ID, "roll"),entities ,
                                    player2.getMainHandItem(), SpellCast.Action.RELEASE, InteractionHand.MAIN_HAND, 0);
                        }
                        info.cancel();

                    }
                }
            }
        }
        if(result == InteractionResult.FAIL) {
            info.cancel();
        }
    }


}