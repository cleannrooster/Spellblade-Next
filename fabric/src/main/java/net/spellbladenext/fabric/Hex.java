package net.spellbladenext.fabric;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

import static net.spellbladenext.fabric.ExampleModFabric.HEX;
import static net.spellbladenext.fabric.ExampleModFabric.attackeventArrayList;

public class Hex extends MobEffect {
    protected Hex(MobEffectCategory mobEffectCategory, int i) {
        super(mobEffectCategory, i);
    }

    @Override
    public void applyEffectTick(LivingEntity livingEntity, int i) {
        if(livingEntity instanceof Player player && player.getLevel() instanceof ServerLevel level){

            Reaver reaver1 = player.getLevel().getNearestEntity(Reaver.class, TargetingConditions.forNonCombat(),player,player.getX(),player.getY(),player.getZ(),player.getBoundingBox().inflate(50,50,50));
            Reaver reaver = new Reaver(ExampleModFabric.REAVER, player.getLevel());
            reaver.isScout = true;
            reaver.nemesis = player;
            BlockPos pos = piglinsummon.getSafePositionAroundPlayer(player.getLevel(), player.getOnPos(), 50);

            if (pos != null) {
                boolean bool = StreamSupport.stream(level.getAllEntities().spliterator(),true).toList().stream().noneMatch(asdf -> asdf instanceof Reaver reaver2 && reaver2.isScout() && reaver2.nemesis == player);
                System.out.println(bool);
                if(bool) {
                    System.out.println(pos);
                    reaver.setPos(pos.getX(), pos.getY(), pos.getZ());
                    reaver.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(player.position(), 2, 2));
                    player.getLevel().addFreshEntity(reaver);
                }

            }
            if(reaver1 != null && reaver1.isScout() &&  reaver.nemesis == player && !reaver1.returninghome && !player.hasEffect(MobEffects.INVISIBILITY)){
                reaver1.getBrain().setMemory(MemoryModuleType.WALK_TARGET,new WalkTarget(player,1.4F,1));
            }

        }
        super.applyEffectTick(livingEntity, i);
    }

    @Override
    public boolean isDurationEffectTick(int i, int j) {
        return true;
    }

    @Override
    public void addAttributeModifiers(LivingEntity livingEntity, AttributeMap attributeMap, int i) {
        super.addAttributeModifiers(livingEntity, attributeMap, i);
    }
}
