package net.spellbladenext.fabric.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import net.spell_engine.api.spell.ParticleBatch;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.client.SpellEngineClient;
import net.spell_engine.entity.SpellProjectile;
import net.spell_engine.internals.SpellHelper;
import net.spell_engine.particle.ParticleHelper;
import net.spell_engine.utils.TargetHelper;
import net.spell_power.api.MagicSchool;
import net.spell_power.api.SpellPower;
import net.spellbladenext.SpellbladeNext;
import net.spellbladenext.entities.AmethystEntity;
import net.spellbladenext.entities.CleansingFlameEntity;
import net.spellbladenext.items.FriendshipBracelet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.function.Predicate;

@Mixin(ServerLevel.class)
public class cleansingfiremixin {
    @Inject(at = @At("HEAD"), method = "addFreshEntity", cancellable = true)
    private void setFlaming(Entity entity, CallbackInfoReturnable<Boolean> info) {
        if(entity instanceof SpellProjectile spellProjectile && !(entity instanceof CleansingFlameEntity)){

            if(spellProjectile.getItem().getItem().equals(Items.BLAZE_ROD) && spellProjectile.getOwner() instanceof Player player) {
                Predicate<Entity> selectionPredicate = (target) -> {
                    return !SpellEngineClient.config.filterInvalidTargets || (TargetHelper.actionAllowed(TargetHelper.TargetingMode.AREA, TargetHelper.Intent.HARMFUL, player, target)
                            && FriendshipBracelet.PlayerFriendshipPredicate(player,target));
                };
                Spell.Release.Target.Area area = new Spell.Release.Target.Area();
                area.angle_degrees = 360;
                List<Entity> entities = TargetHelper.targetsFromArea(player, player.getEyePosition(),4F, area,  selectionPredicate);
                entities.removeIf(Entity::isInvulnerable);
                entities.removeIf(asdf -> !player.hasLineOfSight(asdf));


                Object[] entitiesarray = entities.toArray();
                int iii = 0;
                for (Entity living2 : entities) {

                    CleansingFlameEntity flux = new CleansingFlameEntity(SpellbladeNext.CLEANSINGFLAME, spellProjectile.getLevel());
                    flux.target = living2;
                    flux.setOwner(player);
                    flux.setPos(player.getBoundingBox().getCenter().add(new Vec3((living2.getX() - player.getX()) * player.getBoundingBox().getXsize() / (living2.distanceTo(player)), (living2.getY() - player.getY()) * player.getBoundingBox().getYsize() / (living2.distanceTo(player)), (living2.getZ() - player.getZ()) * player.getBoundingBox().getZsize() / (living2.distanceTo(player)))));
                    SpellPower.Result power = SpellPower.getSpellPower(MagicSchool.FIRE, (LivingEntity) player);
                    flux.power = power;
                    flux.spell = spellProjectile.getSpell();
                    flux.context = spellProjectile.getImpactContext().channeled(1);
                    if (!spellProjectile.getLevel().isClientSide() && living2 instanceof LivingEntity living3 && FriendshipBracelet.PlayerFriendshipPredicate(player,living3)) {
                        living2.level.addFreshEntity(flux);
                    }
                    if(living2 instanceof LivingEntity living3 && TargetHelper.actionAllowed(TargetHelper.TargetingMode.DIRECT, TargetHelper.Intent.HARMFUL,player,living3) && FriendshipBracelet.PlayerFriendshipPredicate(player,living3)){
                        living3.knockback(1,-(living3.position().x-spellProjectile.getOwner().position().x),-(living3.position().z-spellProjectile.getOwner().position().z));
                    }
                    //entities.remove(living2);

                }
                ParticleBatch[] asdf = new ParticleBatch[2];
                ParticleHelper.sendBatches(spellProjectile.getOwner(),spellProjectile.getSpell().release.particles);

                info.cancel();
            }
        }
    }
}