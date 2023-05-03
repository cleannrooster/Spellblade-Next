package net.spellbladenext.fabric.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.NetherPortalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.Vec3;
import net.spellbladenext.fabric.SpellbladesFabric;
import net.spellbladenext.fabric.items.spellblades.Spellblade;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class netherPortal extends FallingBlockEntity {
    public Player owner;
    public boolean spawn = false;
    public boolean firstPiglin = true;
    public boolean ishome = false;
    public int hometicks = 0;
    public boolean goinghome = false;
    public BlockPos origin = BlockPos.ZERO;
    public netherPortal(EntityType<netherPortal> netherPortalFrameEntityType, Level level) {
        super(netherPortalFrameEntityType, level);
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    public BlockState getBlockState() {
        BlockState state = Blocks.NETHER_PORTAL.defaultBlockState();
        state = state.setValue(NetherPortalBlock.AXIS, Direction.Axis.X);

        if(Objects.equals(this.getCustomName(),  Component.translatable("rotated"))){
            state = state.setValue(NetherPortalBlock.AXIS, Direction.Axis.Z);
        }
        return state;
    }

    @Override
    public boolean isCustomNameVisible() {
        return false;
    }

    public float damage = 6;
    public boolean isTip;
    public netherPortal(Level p_201972_, double v, double y, double v1, LivingEntity player, int size, float damage) {
        super(SpellbladesFabric.NETHERPORTAL,p_201972_);
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
        if(size == 1){
            this.spawn = true;
        }
    }

    @Override
    public boolean shouldShowName() {
        return false;
    }

    public void setOwner(Player player){
        this.owner = player;
    }


    public netherPortal(EntityType<? extends netherPortal> p_36833_, Level p_36834_, LivingEntity player, BlockPos blockPos, float damage, boolean bool, boolean home) {
        super(p_36833_, p_36834_);


        if(bool){

            netherPortal.fall(level,blockPos.above().above().above().east(),player,0, damage,false,home);
            netherPortal.fall(level,blockPos.above().above().above(),player,0, damage,false,home);

            netherPortal.fall(level,blockPos.above().above(),player,0, damage,false,home);
            netherPortal.fall(level,blockPos.above().above().east(),player,0, damage,false,home);
            netherPortal.fall(level,blockPos.above().east(),player,0, damage,false,home);
            netherPortal.fall(level,blockPos.above(),player,1, damage,false,home);
        }
        else{

            netherPortal.fall(level,blockPos.above().above().above().south(),player,0, damage, true,home);
            netherPortal.fall(level,blockPos.above().above().above(),player,0, damage, true,home);

            netherPortal.fall(level,blockPos.above().above(),player,0, damage, true,home);
            netherPortal.fall(level,blockPos.above().above().south(),player,0, damage, true,home);
            netherPortal.fall(level,blockPos.above().south(),player,0, damage, true,home);
            netherPortal.fall(level,blockPos.above(),player,1, damage, true,home);


        }

    }
    public static netherPortal fall(Level p_201972_, BlockPos p_201973_, LivingEntity player, int size, float damage, boolean bool, boolean home) {
        netherPortal fallingblockentity = new netherPortal(p_201972_, (double)p_201973_.getX() + 0.5D, (double)p_201973_.getY(), (double)p_201973_.getZ() + 0.5D,player,size, damage);

        fallingblockentity.damage = damage;
        fallingblockentity.origin = p_201973_;
        fallingblockentity.ishome = home;
        if(player instanceof Player) {
            fallingblockentity.setOwner((Player) player);
        }
        if(bool)
        fallingblockentity.setCustomName(Component.translatable("rotated"));
        if(!p_201972_.isClientSide()) {
            p_201972_.addFreshEntity(fallingblockentity);
        }
        return fallingblockentity;
    }


    @Override
    public void tick() {

        if(this.ishome && this.spawn){
            List<Reaver> piglins = this.getLevel().getEntities(EntityTypeTest.forClass(Reaver.class),this.getBoundingBox().inflate(48),piglin -> piglin.returninghome);
            for(Reaver piglin : piglins){
                piglin.getBrain().setMemory(MemoryModuleType.WALK_TARGET,new WalkTarget(this,1.4F,1));
                if(piglin.distanceTo(this) < 3){
                    piglin.discard();
                }
            }
        }
        List<Reaver> piglins = this.getLevel().getEntities(EntityTypeTest.forClass(Reaver.class),this.getBoundingBox().inflate(3), piglin -> piglin.returninghome);
        for(Reaver piglin : piglins){
                piglin.discard();
        }
        this.setNoGravity(true);
        this.noPhysics = true;
        if(tickCount < 20){
            this.setPos(this.position().add(0,6F/20F,0));
        }
        else if(this.spawn && this.tickCount < 100 && this.tickCount % 10 == 5 && !this.ishome){
            ArrayList<ItemStack> spellblades = new ArrayList<ItemStack>();
            for(Item item : Registry.ITEM.stream().toList()){
                if(item instanceof Spellblade){
                    spellblades.add(new ItemStack(item));
                }
            }
            ItemStack spellblade = spellblades.get(this.random.nextInt(spellblades.size()));
            Reaver piglin = new Reaver(SpellbladesFabric.REAVER,this.getLevel());
            piglin.equipItemIfPossible(spellblade);
            piglin.setPos(this.position());
            if(firstPiglin){
                piglin.isleader = true;
            }
            if(this.random.nextInt(5) < 2){
                piglin.isCaster = true;
            }

            if(this.owner != null){
                //System.out.println(this.owner);
                piglin.nemesis = this.owner;
                piglin.getBrain().setMemoryWithExpiry(MemoryModuleType.WALK_TARGET,new WalkTarget(this.owner,1.5F,5),160);

            }
            this.getLevel().addFreshEntity(piglin);
            firstPiglin = false;
        }
        if(tickCount > 220 && !this.ishome){
            this.setPos(this.position().add(0,-6F/20F,0));

        }
        if(tickCount > 240 && !this.ishome){
            this.discard();
        }
        if(this.ishome && this.getLevel().getNearestEntity(Reaver.class, TargetingConditions.forNonCombat().ignoreLineOfSight(),null,this.origin.getX(),this.origin.getY(),this.origin.getZ(),this.getBoundingBox().inflate(32)) == null){
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
