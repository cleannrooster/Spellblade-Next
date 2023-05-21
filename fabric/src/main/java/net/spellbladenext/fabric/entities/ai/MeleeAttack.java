package net.spellbladenext.fabric.entities.ai;

import com.google.common.collect.ImmutableMap;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.spell_power.api.MagicSchool;
import net.spell_power.api.attributes.SpellAttributes;

public class MeleeAttack  extends Behavior<Mob> {
    private final int cooldownBetweenAttacks;

    public MeleeAttack(int i) {
        super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT, MemoryModuleType.ATTACK_COOLING_DOWN, MemoryStatus.VALUE_ABSENT));
        this.cooldownBetweenAttacks = i;
    }

    protected boolean checkExtraStartConditions(ServerLevel serverLevel, Mob mob) {
        LivingEntity livingEntity = this.getAttackTarget(mob);

        return !( mob.hasCustomName() &&mob.getCustomName().equals(Component.translatable("Caster"))) && BehaviorUtils.canSee(mob, livingEntity) && mob.isWithinMeleeAttackRange(livingEntity);
    }

    private boolean isHoldingUsableProjectileWeapon(Mob mob) {
        return mob.isHolding((itemStack) -> {
            Item item = itemStack.getItem();
            return item instanceof ProjectileWeaponItem && mob.canFireProjectileWeapon((ProjectileWeaponItem)item);
        });
    }

    protected void start(ServerLevel serverLevel, Mob mob, long l) {
        LivingEntity livingEntity = this.getAttackTarget(mob);
        //BehaviorUtils.lookAtEntity(mob, livingEntity);
        mob.swing(InteractionHand.MAIN_HAND);
        mob.doHurtTarget(livingEntity);
        mob.getBrain().setMemoryWithExpiry(MemoryModuleType.ATTACK_COOLING_DOWN, true, (long)this.cooldownBetweenAttacks);
    }

    private LivingEntity getAttackTarget(Mob mob) {
        return (LivingEntity)mob.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).get();
    }
}
