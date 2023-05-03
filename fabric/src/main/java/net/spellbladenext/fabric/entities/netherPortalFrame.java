package net.spellbladenext.fabric.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.spellbladenext.fabric.SpellbladesFabric;

public class netherPortalFrame extends FallingBlockEntity {

    public Player owner;
    public boolean ishome = false;
    public int hometicks = 0;
    public boolean goinghome = false;
    public BlockPos origin = BlockPos.ZERO;
    public netherPortalFrame(EntityType<netherPortalFrame> netherPortalFrameEntityType, Level level) {
        super(netherPortalFrameEntityType, level);
    }


    public BlockState getBlockState() {
        return Blocks.OBSIDIAN.defaultBlockState();
    }
    public float damage = 6;
    public boolean isTip;
    public netherPortalFrame(Level p_201972_, double v, double y, double v1, LivingEntity player, int size, float damage) {
        super(SpellbladesFabric.NETHERPORTALFRAME,p_201972_);
        this.blocksBuilding = true;
        this.setPos(v, y-5, v1);
        this.setDeltaMovement(Vec3.ZERO);
        this.xo = v;
        this.yo = y-5;
        this.zo = v1;
        this.setStartPos(this.blockPosition());
        if(player instanceof Player) {
            this.setOwner((Player) player);
        }
        this.damage = damage;
    }

    @Override
    public boolean shouldShowName() {
        return false;
    }

    public void setOwner(Player player){
        this.owner = player;
    }


    public netherPortalFrame(EntityType<? extends netherPortalFrame> p_36833_, Level p_36834_, LivingEntity player, BlockPos blockPos, float damage, boolean bool, boolean home) {
        super(p_36833_, p_36834_);


        if(bool){
            netherPortalFrame.fall(level,blockPos.above().above().above().above().west(),player,0, damage,  home);
            netherPortalFrame.fall(level,blockPos.above().above().above().above().east(),player,0, damage,  home);
            netherPortalFrame.fall(level,blockPos.above().above().above().above().east().east(),player,0, damage,  home);
            netherPortalFrame.fall(level,blockPos.above().above().above().above(),player,0, damage,  home);

            netherPortalFrame.fall(level,blockPos.above().above().above().west(),player,0, damage,  home);
            netherPortalFrame.fall(level,blockPos.above().above().above().east().east(),player,0, damage,  home);

            netherPortalFrame.fall(level,blockPos.above().above().west(),player,0, damage,  home);
            netherPortalFrame.fall(level,blockPos.above().above().east().east(),player,0, damage,  home);

            netherPortalFrame.fall(level,blockPos.above().east().east(),player,0, damage,  home);
            netherPortalFrame.fall(level,blockPos.above().west(),player,0, damage,  home);

            netherPortalFrame.fall(level,blockPos.west(),player,0, damage,  home);
            netherPortalFrame.fall(level,blockPos.east(),player,0, damage,  home);
            netherPortalFrame.fall(level,blockPos.east().east(),player,0, damage,  home);
            netherPortalFrame.fall(level,blockPos,player,0, damage,  home);
        }
        else{
            netherPortalFrame.fall(level,blockPos.above().above().above().above().north(),player,0, damage,  home);
            netherPortalFrame.fall(level,blockPos.above().above().above().above().south(),player,0, damage,  home);
            netherPortalFrame.fall(level,blockPos.above().above().above().above().south().south(),player,0, damage,  home);
            netherPortalFrame.fall(level,blockPos.above().above().above().above(),player,0, damage,  home);

            netherPortalFrame.fall(level,blockPos.above().above().above().north(),player,0, damage,  home);
            netherPortalFrame.fall(level,blockPos.above().above().above().south().south(),player,0, damage,  home);

            netherPortalFrame.fall(level,blockPos.above().above().north(),player,0, damage,  home);
            netherPortalFrame.fall(level,blockPos.above().above().south().south(),player,0, damage,  home);

            netherPortalFrame.fall(level,blockPos.above().south().south(),player,0, damage,  home);
            netherPortalFrame.fall(level,blockPos.above().north(),player,0, damage,  home);

            netherPortalFrame.fall(level,blockPos.north(),player,0, damage,  home);
            netherPortalFrame.fall(level,blockPos.south(),player,0, damage,  home);
            netherPortalFrame.fall(level,blockPos.south().south(),player,0, damage,  home);
            netherPortalFrame.fall(level,blockPos,player,0, damage,  home);


        }

    }
    public static netherPortalFrame fall(Level p_201972_, BlockPos p_201973_, LivingEntity player, int size, float damage, boolean home) {
        netherPortalFrame fallingblockentity = new netherPortalFrame(p_201972_, (double)p_201973_.getX() + 0.5D, (double)p_201973_.getY(), (double)p_201973_.getZ() + 0.5D,player,size, damage);
        fallingblockentity.damage = damage;
        fallingblockentity.ishome = home;
        if(!p_201972_.isClientSide()) {
            p_201972_.addFreshEntity(fallingblockentity);
        }
        return fallingblockentity;
    }
    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    public boolean canCollideWith(Entity p_20303_) {
        return true;
    }

    @Override
    public void tick() {
        if(firstTick){
            SoundEvent soundEvent = SoundEvents.BLAZE_SHOOT;
            this.playSound(soundEvent, 1F, 0.5F);
        }

        this.setNoGravity(true);
        this.noPhysics = true;
        if(tickCount < 20){
            this.setPos(this.position().add(0,6F/20F,0));
        }

        if(tickCount > 220 && !this.ishome){
            this.setPos(this.position().add(0,-6F/20F,0));

        }
        if(tickCount > 240 && !this.ishome){
            this.discard();
        }
        if(this.ishome && this.getLevel().getNearestEntity(Reaver.class, TargetingConditions.forNonCombat(),null,this.origin.getX(),this.origin.getY(),this.origin.getZ(),this.getBoundingBox().inflate(32)) == null){
            goinghome = true;
            this.setPos(this.position().add(0,-6F/20F,0));

        }
        if(goinghome){
            hometicks++;
        }
        if(this.hometicks > 20 && this.ishome){
            this.discard();
        }
        this.firstTick = false;
    }

}
