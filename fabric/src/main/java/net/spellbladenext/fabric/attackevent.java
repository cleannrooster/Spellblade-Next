package net.spellbladenext.fabric;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.Optional;

public class attackevent implements piglinsummon {
    public int tickCount;
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
        if(this.firstTick){
            Optional<netherPortalFrame> frame = piglinsummon.summonNetherPortal(this.level,this.player,false);
            int ii = 0;
            while(frame.isEmpty() && ii < 10){
                frame = piglinsummon.summonNetherPortal(this.level,this.player, false);
                ii++;
            }
        }

        if(this.tickCount % 80 == 0) {
        }
        this.firstTick = false;
        this.tickCount++;

    }
}
