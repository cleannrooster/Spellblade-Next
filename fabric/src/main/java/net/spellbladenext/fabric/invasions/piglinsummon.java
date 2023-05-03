package net.spellbladenext.fabric.invasions;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.spellbladenext.fabric.SpellbladesFabric;
import net.spellbladenext.fabric.entities.netherPortal;
import net.spellbladenext.fabric.entities.netherPortalFrame;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

import static java.lang.Math.sqrt;
import static net.spellbladenext.fabric.SpellbladesFabric.SINCELASTHEX;

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
                         netherPortal portal = new netherPortal(SpellbladesFabric.NETHERPORTAL, level, player, pos, 0, bool,home);
                         netherPortalFrame frame = new netherPortalFrame(SpellbladesFabric.NETHERPORTALFRAME, level, player, pos, 0, bool,home);
                         if(player instanceof ServerPlayer player1) {
                              player1.getStats().setValue(player1, Stats.CUSTOM.get(SINCELASTHEX), 0);
                         }

                         return Optional.of(frame);

                    }
               }
          }
          return Optional.empty();
     }

     @Nullable
     public static BlockPos getSafePositionAroundPlayer(Level level, BlockPos pos, int range)
     {
          if(range == 0)
          {
               return null;
          }
          BlockPos safestPos = null;
          for(int attempts = 0; attempts < 1; attempts++)
          {
               int a = -1;
               int b = -1;
               int c = -1;
               if(level.getRandom().nextBoolean()){
                    a = 1;
               }
               if(level.getRandom().nextBoolean()){
                    b = 1;
               }
               if(level.getRandom().nextBoolean()){
                    c = 1;
               }
               int posX = pos.getX()  + a*level.getRandom().nextInt(10 );
               int posY = pos.getY() + level.getRandom().nextInt(10) - 10 / 2;
               int posZ = pos.getZ()  + c* level.getRandom().nextInt(10);
               BlockPos testPos = findGround(level, new BlockPos(posX, posY, posZ));

               if(testPos != null && level.getFluidState(testPos).isEmpty() && level.getBlockState(testPos.below()).canOcclude())
               {
                    safestPos = testPos;
                    break;
               }
          }
          return safestPos;
     }

     @Nullable
     private static BlockPos findGround(Level level, BlockPos pos)
     {
          if(level.getBlockState(pos).isAir())
          {
               BlockPos downPos = pos;
               while(Level.isInSpawnableBounds(downPos.below()) && level.getBlockState(downPos.below()).isAir() && downPos.below().closerThan(pos, 20))
               {
                    downPos = downPos.below();
               }
               if(!level.getBlockState(downPos.below()).isAir())
               {
                    return downPos;
               }
          }
          else
          {
               BlockPos upPos = pos;
               while(Level.isInSpawnableBounds(upPos.above()) && !level.getBlockState(upPos.above()).isAir() && upPos.above().closerThan(pos, 20))
               {
                    upPos = upPos.above();
               }
               if(!level.getBlockState(upPos.above()).isAir())
               {
                    return upPos;
               }
          }
          return null;
     }
}
