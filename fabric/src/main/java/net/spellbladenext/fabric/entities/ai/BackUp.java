package net.spellbladenext.fabric.entities.ai;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.EntityTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.spell_power.api.MagicSchool;
import net.spell_power.api.attributes.SpellAttributes;
import net.spellbladenext.fabric.entities.Magus;
import net.spellbladenext.fabric.entities.Reaver;

public class BackUp<E extends Mob> extends Behavior<E> {
    private final int tooCloseDistance;
    private final float strafeSpeed;

    public BackUp(int i, float f) {
        super(ImmutableMap.of( MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryStatus.VALUE_PRESENT));
        this.tooCloseDistance = i;
        this.strafeSpeed = f;
    }

    protected boolean checkExtraStartConditions(ServerLevel serverLevel, E mob) {
        if(this.getTarget(mob).getAttributeValue(Attributes.ATTACK_DAMAGE) > 40 ||
                this.getTarget(mob).getAttributeValue(SpellAttributes.POWER.get(MagicSchool.ARCANE).attribute) > mob.getMaxHealth()/2 ||
                this.getTarget(mob).getAttributeValue(SpellAttributes.POWER.get(MagicSchool.FROST).attribute) > mob.getMaxHealth()/2 ||
                this.getTarget(mob).getAttributeValue(SpellAttributes.POWER.get(MagicSchool.FIRE).attribute) > mob.getMaxHealth()/2 ||
                this.getTarget(mob).getAttributeValue(SpellAttributes.POWER.get(MagicSchool.HEALING).attribute) > mob.getMaxHealth()/2){
            return true;
        }
        return (mob instanceof Magus || (mob instanceof Reaver reaver && reaver.isCaster())) && this.isTargetVisible(mob) && this.isTargetTooClose(mob);
    }

    protected void start(ServerLevel serverLevel, E mob, long l) {
        //System.out.println("backing up!");
        mob.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, new EntityTracker(this.getTarget(mob), true));
        mob.getMoveControl().strafe(-this.strafeSpeed, 0.0F);
        mob.setYRot(Mth.rotateIfNecessary(mob.getYRot(), mob.yHeadRot, 0.0F));
    }

    private boolean isTargetVisible(E mob) {
        return ((NearestVisibleLivingEntities)mob.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES).get()).contains(this.getTarget(mob));
    }

    private boolean isTargetTooClose(E mob) {
        return this.getTarget(mob).closerThan(mob, (double)this.tooCloseDistance);
    }

    private LivingEntity getTarget(E mob) {
        return (LivingEntity)mob.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).get();
    }
}
