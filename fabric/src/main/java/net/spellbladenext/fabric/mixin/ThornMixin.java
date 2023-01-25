package net.spellbladenext.fabric.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.spell_engine.entity.SpellProjectile;
import net.spell_engine.internals.SpellHelper;
import net.spell_power.api.MagicSchool;
import net.spell_power.api.SpellPower;
import net.spellbladenext.SpellbladeNext;
import net.spellbladenext.entities.AmethystEntity;
import net.spellbladenext.entities.EndersGazeEntity;
import net.spellbladenext.entities.IceThorn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerLevel.class)
public class ThornMixin {
    @Inject(at = @At("HEAD"), method = "addFreshEntity", cancellable = true)
    private void setBounding(Entity entity, CallbackInfoReturnable<Boolean> info) {
        if(entity instanceof SpellProjectile spellProjectile && !(entity instanceof IceThorn)){

            if(spellProjectile.getItem().getItem().equals(Items.SNOWBALL) && spellProjectile.getOwner() instanceof Player player) {
                for(int i = 0; i < 3; i++) {
                    IceThorn amethyst = new IceThorn(SpellbladeNext.ICETHORN, entity.getLevel(), player);
                    amethyst.setPos(player.getEyePosition().add(player.getViewVector(1).normalize()));
                    //amethyst.setDeltaMovement(player.getViewVector(1).multiply(0.5, 0.5, 0.5));
                    amethyst.setOwner(player);
                    SpellPower.Vulnerability vulnerability = SpellPower.Vulnerability.none;

                    SpellPower.Result power = SpellPower.getSpellPower(MagicSchool.FROST, (LivingEntity) spellProjectile.getOwner());
                    amethyst.power = spellProjectile.getImpactContext().power();
                    amethyst.spell = spellProjectile.getSpell();
                    amethyst.context = spellProjectile.getImpactContext().channeled(1);

                    entity.getLevel().addFreshEntity(amethyst);

                }
                info.cancel();
            }
        }
    }
}