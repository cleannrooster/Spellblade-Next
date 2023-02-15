package net.spellbladenext.fabric;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;

public interface HurtCallback {

    Event<HurtCallback> EVENT = EventFactory.createArrayBacked(HurtCallback.class,
            (listeners) -> (damage, amount) -> {
                for (HurtCallback listener : listeners) {
                    InteractionResult result = listener.interact(damage, amount);

                    if(result != InteractionResult.PASS) {
                        return result;
                    }
                }

                return InteractionResult.PASS;
            });

    InteractionResult interact(DamageSource source, float f);
}