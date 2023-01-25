package net.spellbladenext.forge;

import dev.architectury.platform.forge.EventBuses;
import net.spellbladenext.SpellbladeNext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(SpellbladeNext.MOD_ID)
public class ExampleModForge {
    public ExampleModForge() {
        // Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(SpellbladeNext.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        SpellbladeNext.init();

    }
}
