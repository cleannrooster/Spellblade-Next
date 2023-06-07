package net.spellbladenext.fabric.interfaces;

import dev.architectury.event.events.common.TickEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.spell_engine.internals.SpellCasterEntity;

import java.util.List;

public interface PlayerDamageInterface {
    void setDamageMultiplier(float f);
    void override(boolean bool);
    void shouldUnfortify(boolean bool);
    void listAdd(LivingEntity entity);
    void listRefresh();
    List<LivingEntity> getList();
    boolean listContains(LivingEntity entity);

}
