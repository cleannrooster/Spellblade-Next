package net.spellbladenext.fabric.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.spell_engine.entity.SpellProjectile;
import net.spell_power.api.MagicSchool;
import net.spell_power.api.SpellPower;
import net.spellbladenext.SpellbladeNext;
import net.spellbladenext.entities.AmethystEntity;
import net.spellbladenext.entities.AmethystEntity2;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerLevel.class)
public class SpellMixin {
    @Inject(at = @At("HEAD"), method = "addFreshEntity", cancellable = true)
    private void setEntity(Entity entity, CallbackInfoReturnable<Boolean> info) {
        if(entity instanceof SpellProjectile spellProjectile && !(entity instanceof AmethystEntity2)){

            if(spellProjectile.getItem().getItem().equals(Items.AMETHYST_SHARD) && spellProjectile.getOwner() instanceof Player player) {
                for(int ii = 0; ii < 4; ii++) {
                    AmethystEntity amethyst = new AmethystEntity(SpellbladeNext.AMETHYST, entity.getLevel(), player);
                    amethyst.setPos(player.getEyePosition().add(player.getViewVector(1).normalize()));
                    amethyst.setDeltaMovement(player.getViewVector(1).add(-0.5 + 1 * player.getRandom().nextDouble(), -0.5 + 1 * player.getRandom().nextDouble(), -0.5 + 1 * player.getRandom().nextDouble()).normalize().multiply(2, 2, 2));
                    amethyst.setOwner(player);
                    amethyst.spell = spellProjectile.getSpell();
                    amethyst.context = spellProjectile.getImpactContext();

                    SpellPower.Vulnerability vulnerability = SpellPower.Vulnerability.none;


                    SpellPower.Result power = SpellPower.getSpellPower(MagicSchool.ARCANE, (LivingEntity) spellProjectile.getOwner());
                    amethyst.power = power;
                    entity.getLevel().addFreshEntity(amethyst);
                }
                info.cancel();
            }
        }
    }
}