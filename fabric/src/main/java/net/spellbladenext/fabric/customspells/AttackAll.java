package net.spellbladenext.fabric.customspells;

import it.unimi.dsi.fastutil.Function;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.spell_engine.api.spell.CustomSpellHandler;
import net.spellbladenext.fabric.interfaces.PlayerDamageInterface;

import java.util.List;

public class AttackAll {
    public static void attackAll(LivingEntity user, List<Entity> targets, float multiplier){
        if(user instanceof Player player && player instanceof PlayerDamageInterface damager && !targets.isEmpty()){
            damager.override(true);
            damager.setDamageMultiplier(multiplier);
            for(Entity entity : targets){
                if(entity instanceof LivingEntity living){
                    living.invulnerableTime = 0;

                }
                player.attack(entity);
            }
            damager.override(false);
        }
    }
}
