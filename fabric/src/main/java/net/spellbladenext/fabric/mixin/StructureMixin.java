package net.spellbladenext.fabric.mixin;

import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
import java.util.function.Predicate;

public class StructureMixin {
    public void generate(RegistryAccess p_226597_, ChunkGenerator p_226598_, BiomeSource p_226599_, RandomState p_226600_, StructureTemplateManager p_226601_, long p_226602_, ChunkPos p_226603_, int p_226604_, LevelHeightAccessor p_226605_, Predicate<Holder<Biome>> p_226606_, CallbackInfoReturnable<StructureStart> infoReturnable) {
        if(p_226598_.getSeaLevel() != 62){
            infoReturnable.setReturnValue( StructureStart.INVALID_START);
        }
    }
}
