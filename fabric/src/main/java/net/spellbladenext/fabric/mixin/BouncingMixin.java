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
import net.spellbladenext.entities.MagmaOrbEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerLevel.class)
public class BouncingMixin {
    @Inject(at = @At("HEAD"), method = "addFreshEntity", cancellable = true)
    private void setBounding(Entity entity, CallbackInfoReturnable<Boolean> info) {
        if(entity instanceof SpellProjectile spellProjectile && !(entity instanceof MagmaOrbEntity)){

            if(spellProjectile.getItem().getItem().equals(Items.FIRE_CHARGE) && spellProjectile.getOwner() instanceof Player player) {
                    MagmaOrbEntity amethyst = new MagmaOrbEntity(SpellbladeNext.MAGMA, entity.getLevel(), player);
                    amethyst.setPos(player.getEyePosition().add(player.getViewVector(1).normalize()));
                    amethyst.setDeltaMovement(player.getViewVector(1).multiply(0.5, 0.5, 0.5));
                    amethyst.setOwner(player);
                    SpellPower.Vulnerability vulnerability = SpellPower.Vulnerability.none;


                    SpellPower.Result power = SpellPower.getSpellPower(MagicSchool.FIRE, (LivingEntity) spellProjectile.getOwner());
                    amethyst.power = power;

                amethyst.spell = spellProjectile.getSpell();
                amethyst.context = spellProjectile.getImpactContext();
                    entity.getLevel().addFreshEntity(amethyst);

                info.cancel();
            }
        }
    }
}