package net.spellbladenext.fabric;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import static net.spellbladenext.fabric.ExampleModFabric.HEX;
import static net.spellbladenext.fabric.ExampleModFabric.attackeventArrayList;

public class Hex extends MobEffect {
    protected Hex(MobEffectCategory mobEffectCategory, int i) {
        super(mobEffectCategory, i);
    }

    @Override
    public void applyEffectTick(LivingEntity livingEntity, int i) {
        if(livingEntity instanceof Player player && !player.getLevel().isClientSide()) {
            if (player.getLevel().dimension().equals(Level.OVERWORLD) && player.getLevel().canSeeSky(player.getOnPos().above().above())) {
                piglinsummon.summonNetherPortal(player.getLevel(),player,false).ifPresent(asdf -> player.removeEffect(HEX.get()));
            }
        }
        super.applyEffectTick(livingEntity, i);
    }

    @Override
    public boolean isDurationEffectTick(int i, int j) {
        return i % 5 == 0;
    }
}
