package net.spellbladenext.fabric.mixin;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.spell_engine.entity.SpellProjectile;
import net.spell_engine.utils.TargetHelper;
import net.spell_power.api.MagicSchool;
import net.spell_power.api.SpellPower;
import net.spellbladenext.SpellbladeNext;
import net.spellbladenext.entities.Eruption;
import net.spellbladenext.entities.ExplosionDummy;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerLevel.class)
public class TNTmixin {
    @Inject(at = @At("HEAD"), method = "addFreshEntity", cancellable = true)
    private void setFlaming(Entity entity, CallbackInfoReturnable<Boolean> info) {
        if(entity instanceof SpellProjectile spellProjectile && !(entity instanceof ExplosionDummy)){

            if(spellProjectile.getItem().getItem().equals(Items.TNT) && spellProjectile.getOwner() instanceof Player player) {
                    ExplosionDummy amethyst = new ExplosionDummy(SpellbladeNext.EXPLOSIONDUMMY,spellProjectile.getLevel(),player);
                    amethyst.setOwner(player);
                    if(spellProjectile.getFollowedTarget() != null) {
                        amethyst.setPos(spellProjectile.getFollowedTarget().position().add(0, spellProjectile.getFollowedTarget().getBoundingBox().getYsize() / 2, 0));
                    }
                    else{
                        BlockHitResult blockHitResult = player.level.clip(new ClipContext(player.getEyePosition(), player.getEyePosition().add(player.getViewVector(1).scale(spellProjectile.getSpell().range)), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
                        if(blockHitResult.getType() == HitResult.Type.BLOCK){
                            amethyst.setPos(blockHitResult.getLocation().subtract(player.getViewVector(1)));
                        }
                        else{
                            amethyst.setPos(player.getEyePosition());
                        }
                    }
                        amethyst.spell = spellProjectile.getSpell();
                        amethyst.context = spellProjectile.getImpactContext();
                        amethyst.setOwner(player);
                        SpellPower.Vulnerability vulnerability = SpellPower.Vulnerability.none;

                         amethyst.power = SpellPower.getSpellPower(MagicSchool.FIRE, (LivingEntity) spellProjectile.getOwner());

                        entity.getLevel().addFreshEntity(amethyst);

                if(player.getLevel() instanceof ServerLevel level){
                    //level.playSound(null,player, SoundEvents.WARDEN_SONIC_BOOM, SoundSource.PLAYERS,3.0F,1);
                    int num_pts_line = 40;
                    for(int ii = 0; ii < 4; ii++) {
                        for (int iii = 0; iii < num_pts_line; iii++) {
                            if(iii % 2 == player.getRandom().nextInt(2)) {
                                double X = player.getX() + (amethyst.getX() - player.getX()) * ((double) iii / (num_pts_line));
                                double Y = player.getEyeY() + (amethyst.getY() - player.getEyeY()) * ((double) iii / (num_pts_line));
                                double Z = player.getZ() + (amethyst.getZ() - player.getZ()) * ((double) iii / (num_pts_line));

                                int num_pts = 10;
                                Vec3 targetcenter = new Vec3(X, Y, Z);
                                for (ServerPlayer player1 : level.players()) {
                                    level.sendParticles(player1, ParticleTypes.SMALL_FLAME, true, targetcenter.x + -0.5 + player.getRandom().nextDouble(), targetcenter.y+ -0.5 + player.getRandom().nextDouble(), targetcenter.z+ -0.5 + player.getRandom().nextDouble(), 1, 0, 0, 0, 0F);
                                }
                            }
                            if(iii % 3 == player.getRandom().nextInt(3) && ii % 4 == 1) {
                                double X = player.getX() + (amethyst.getX() - player.getX()) * ((double) iii / (num_pts_line));
                                double Y = player.getEyeY() + (amethyst.getY() - player.getEyeY()) * ((double) iii / (num_pts_line));
                                double Z = player.getZ() + (amethyst.getZ() - player.getZ()) * ((double) iii / (num_pts_line));

                                int num_pts = 10;
                                Vec3 targetcenter = new Vec3(X, Y, Z);
                                for (ServerPlayer player1 : level.players()) {
                                    level.sendParticles(player1, ParticleTypes.ELECTRIC_SPARK, true, targetcenter.x+ -0.25 + 0.5*player.getRandom().nextDouble(), targetcenter.y+ -0.25 + 0.5*player.getRandom().nextDouble(), targetcenter.z+ -0.25 + 0.5*player.getRandom().nextDouble(), 1, 0, 0, 0, 0F);
                                }
                            }
                        }
                    }
                }

                info.cancel();
            }

        }
    }
}