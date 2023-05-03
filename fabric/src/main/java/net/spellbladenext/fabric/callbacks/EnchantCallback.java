package net.spellbladenext.fabric.callbacks;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;

public interface EnchantCallback {

    Event<EnchantCallback> EVENT = EventFactory.createArrayBacked(EnchantCallback.class,
            (listeners) -> (damage, amount) -> {
                for (EnchantCallback listener : listeners) {
                    InteractionResult result = listener.interact(damage, amount);

                    if(result != InteractionResult.PASS) {
                        return result;
                    }
                }

                return InteractionResult.PASS;
            });

    InteractionResult interact(DamageSource source, float f);
}