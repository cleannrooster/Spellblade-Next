package net.spellbladenext.fabric.items;

import net.minecraft.world.item.Item;
import net.spellbladenext.fabric.items.spellblades.Spellblades;

import java.util.HashMap;

import static net.spellbladenext.SpellbladeNext.MOD_ID;

public class SpellbladeItems {
    public static final HashMap<String, Item> entries;
    static {
        entries = new HashMap<>();
        for (var weaponEntry : Spellblades.entries) {
            entries.put(weaponEntry.id().toString(), weaponEntry.item());
        }
        for (var weaponEntry : Orbs.orbs) {
            entries.put(weaponEntry.id().toString(), weaponEntry.item());
        }
        for (var weaponEntry : Spellblades.claymores) {
            entries.put(weaponEntry.id().toString(), weaponEntry.item());
        }
        for (var weaponEntry : Spellblades.runedaggers) {
            entries.put(weaponEntry.id().toString(), weaponEntry.item());
        }
        for (var entry : Armors.entries) {
            if (entry.armorSet().head instanceof RunicArmor) {
                entries.put(MOD_ID+":"+entry.armorSet().head.toString(),
                        entry.armorSet().head);
                entries.put(MOD_ID+":"+entry.armorSet().chest.toString(),
                        entry.armorSet().chest);
                entries.put(MOD_ID+":"+entry.armorSet().legs.toString(),
                        entry.armorSet().legs);
                entries.put(MOD_ID+":"+entry.armorSet().feet.toString(),
                        entry.armorSet().feet);
            }

        }
        System.out.println("asdf " + entries);
    }
}
