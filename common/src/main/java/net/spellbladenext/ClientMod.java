package net.spellbladenext;

import net.minecraft.resources.ResourceLocation;
import net.spell_engine.api.render.CustomModels;

import java.util.List;
public class ClientMod {
    public static void initialize() {

        CustomModels.registerModelIds(List.of(
                new ResourceLocation(SpellbladeNext.MOD_ID, "cleansingflame")
        ));

    }
}