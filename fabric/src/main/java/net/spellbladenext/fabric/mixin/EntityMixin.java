package net.spellbladenext.fabric.mixin;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.spellbladenext.fabric.callbacks.HurtCallback;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class EntityMixin {

    @Inject(at = @At("HEAD"), method = "hurt", cancellable = true)
    private void hurt(final DamageSource player, final float f, final CallbackInfoReturnable<Boolean> info) {
        InteractionResult result = HurtCallback.EVENT.invoker().interact(player, f);

        if(result == InteractionResult.FAIL) {
            info.cancel();
        }
    }
}