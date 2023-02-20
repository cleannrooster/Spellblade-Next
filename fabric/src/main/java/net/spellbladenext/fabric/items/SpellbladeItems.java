package net.spellbladenext.fabric.items;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.spellbladenext.items.spellblades.Spellblades;

import java.util.HashMap;

import static net.spellbladenext.SpellbladeNext.*;
import static  net.spellbladenext.fabric.ExampleModFabric.*;

public class SpellbladeItems {
    public static final HashMap<String, Item> entries;
    static {
        entries = new HashMap<>();
        for(var weaponEntry: Spellblades.entries) {
            entries.put(weaponEntry.id().toString(), weaponEntry.item());
        }
        for(var weaponEntry: Spellblades.orbs) {
            entries.put(weaponEntry.id().toString(), weaponEntry.item());
        }
        for(var weaponEntry: Spellblades.claymores) {
            entries.put(weaponEntry.id().toString(), weaponEntry.item());
        }
        entries.put(new ResourceLocation(MOD_ID, "runeblazinghelmet").toString(), RUNEBLAZINGHELMET);
        entries.put(new ResourceLocation(MOD_ID, "runeblazingbodyarmor").toString(), RUNEBLAZINGCHEST);
        entries.put(new ResourceLocation(MOD_ID, "runeblazingleggings").toString(), RUNEBLAZINGLEGS);
        entries.put(new ResourceLocation(MOD_ID, "runeblazingboots").toString(), RUNEBLAZINGBOOTS);
        entries.put(new ResourceLocation(MOD_ID, "runefrostedhelmet").toString(), RUNEFROSTEDHELMET);
        entries.put(new ResourceLocation(MOD_ID, "runefrostedbodyarmor").toString(), RUNEFROSTEDCHEST);
        entries.put(new ResourceLocation(MOD_ID, "runefrostedleggings").toString(), RUNEFROSTEDLEGS);
        entries.put(new ResourceLocation(MOD_ID, "runefrostedboots").toString(), RUNEFROSTEDBOOTS);
        entries.put(new ResourceLocation(MOD_ID, "runegleaminghelmet").toString(), RUNEGLEAMINGHELMET);
        entries.put(new ResourceLocation(MOD_ID, "runegleamingbodyarmor").toString(), RUNEGLEAMINGCHEST);
        entries.put(new ResourceLocation(MOD_ID, "runegleamingleggings").toString(), RUNEGLEAMINGLEGS);
        entries.put(new ResourceLocation(MOD_ID, "runegleamingboots").toString(), RUNEFROSTEDBOOTS);

    }
}
