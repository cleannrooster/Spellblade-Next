package net.spellbladenext.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.spell_power.api.MagicSchool;
import net.spell_power.api.SpellPower;
import net.spellbladenext.SpellbladeNext;
import net.spellbladenext.entities.CleansingFlameEntity;
import net.spellbladenext.items.FriendshipBracelet;

import java.util.List;

public class CleansingFlame extends MobEffect {
    public CleansingFlame(MobEffectCategory mobEffectCategory, int i) {
        super(mobEffectCategory, i);
    }
    public void applyEffectTick(LivingEntity livingEntity, int i) {

        Level level = livingEntity.getLevel();
        if(livingEntity instanceof Player player) {
            List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, livingEntity.getBoundingBox().inflate(4D), entity -> entity != livingEntity && FriendshipBracelet.PlayerFriendshipPredicate(player, entity) && entity.isAttackable() && !entity.isInvulnerable());


            entities.removeIf(Entity::isInvulnerable);
            entities.removeIf(entity -> !livingEntity.hasLineOfSight(entity));


            Object[] entitiesarray = entities.toArray();
            int iii = 0;
            for (Entity living2 : entities) {

                CleansingFlameEntity flux = new CleansingFlameEntity(SpellbladeNext.CLEANSINGFLAME, level);
                flux.target = living2;
                flux.setOwner(livingEntity);
                flux.setPos(livingEntity.getBoundingBox().getCenter().add(new Vec3((living2.getX() - livingEntity.getX()) * livingEntity.getBoundingBox().getXsize() / (living2.distanceTo(livingEntity)), (living2.getY() - livingEntity.getY()) * livingEntity.getBoundingBox().getYsize() / (living2.distanceTo(livingEntity)), (living2.getZ() - livingEntity.getZ()) * livingEntity.getBoundingBox().getZsize() / (living2.distanceTo(livingEntity)))));
                SpellPower.Result power = SpellPower.getSpellPower(MagicSchool.FIRE, (LivingEntity) livingEntity);
                flux.power = power;
                if (!level.isClientSide()) {
                    living2.level.addFreshEntity(flux);
                }

            }
        }
    }
    public boolean isDurationEffectTick(int i, int j) {
        return i % 5 == 4;
    }
}
