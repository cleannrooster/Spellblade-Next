package net.spellbladenext.fabric;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.spellbladenext.SpellbladeNext;

import java.util.Optional;

import static java.lang.Math.sqrt;
import static net.spellbladenext.fabric.ExampleModFabric.SINCELASTHEX;

public interface piglinsummon {
     default void tick(){
     }
     public static Optional<netherPortalFrame> summonNetherPortal(Level level, LivingEntity player, boolean home){
          double xRand = -1+level.getRandom().nextDouble()*2;
          double zRand = -1+level.getRandom().nextDouble()*2;
          double d0 = sqrt(xRand*xRand+zRand*zRand);

          BlockHitResult result = level.clip(new ClipContext(player.getEyePosition(),new Vec3(player.getX()+40*xRand/d0,player.getY()-40*.2,player.getZ()+40*zRand/d0), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE,player));
          if(result.getType() != HitResult.Type.MISS) {
               BlockPos pos = result.getBlockPos();
               if(result.getLocation().subtract(player.getEyePosition()).horizontalDistance() > 2) {
                    boolean flag = level.getBlockState(pos.above()).isSuffocating(level, result.getBlockPos().above())
                            || level.getBlockState(pos.above().above()).isSuffocating(level, pos.above().above())
                            || level.getBlockState(pos.above().above().above()).isSuffocating(level, pos.above().above().above())
                            || level.getBlockState(pos.above().above().above().above()).isSuffocating(level, pos.above().above().above().above());
                    int ii = 0;
                    boolean found = true;
                    while (flag) {
                         pos = pos.above();
                         flag = level.getBlockState(pos.above()).isSuffocating(level, result.getBlockPos().above())
                                 || level.getBlockState(pos.above().above()).isSuffocating(level, pos.above().above())
                                 || level.getBlockState(pos.above().above().above()).isSuffocating(level, pos.above().above().above())
                                 || level.getBlockState(pos.above().above().above().above()).isSuffocating(level, pos.above().above().above().above());
                         ii++;
                         if (ii > 10) {
                              found = false;
                              break;
                         }
                    }
                    if (found) {
                         boolean bool = level.getRandom().nextBoolean();
                         netherPortal portal = new netherPortal(ExampleModFabric.NETHERPORTAL, level, player, pos, 0, bool,home);
                         netherPortalFrame frame = new netherPortalFrame(ExampleModFabric.NETHERPORTALFRAME, level, player, pos, 0, bool,home);
                         if(player instanceof ServerPlayer player1) {
                              player1.getStats().setValue(player1, Stats.CUSTOM.get(SINCELASTHEX), 0);
                         }

                         return Optional.of(frame);

                    }
               }
          }
          return Optional.empty();
     }
}
