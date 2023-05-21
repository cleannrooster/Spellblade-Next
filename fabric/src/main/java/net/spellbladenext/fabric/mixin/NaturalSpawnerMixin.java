package net.spellbladenext.fabric.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.spellbladenext.SpellbladeNext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NaturalSpawner.class)
public class NaturalSpawnerMixin {
    @Inject(method = "spawnCategoryForChunk",
            at = @At("HEAD"),
            cancellable = true)
    private static void spawnEntitiesInChunk(MobCategory mobCategory, ServerLevel world, LevelChunk levelChunk, NaturalSpawner.SpawnPredicate spawnPredicate, NaturalSpawner.AfterSpawnCallback afterSpawnCallback, CallbackInfo ci) {

        if (
                world.dimension().equals(SpellbladeNext.DIMENSIONKEY))
        {
            ci.cancel();
        }
    }
}
