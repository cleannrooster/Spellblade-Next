package net.spellbladenext.fabric.invasions;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.spellbladenext.fabric.entities.netherPortalFrame;

import java.util.Optional;

public class attackevent implements piglinsummon {
    public int tickCount;
    public boolean done = false;
    Level level;
    Player player;
    boolean firstTick;

    public attackevent(Level world, Player serverPlayer) {
        this.tickCount = 0;
        this.level = world;
        this.player = serverPlayer;
        this.firstTick = true;
    }

    @Override
    public void tick() {
        if(this.tickCount % 5 == 0){
            Optional<netherPortalFrame> frame = piglinsummon.summonNetherPortal(this.level,this.player,false);
            if(frame.isPresent()){
                this.done = true;
            }
        }

        if(this.tickCount % 80 == 0) {
        }
        this.firstTick = false;
        this.tickCount++;

    }
}
