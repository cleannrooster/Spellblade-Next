package net.spellbladenext.config;

import net.spellbladenext.items.spellblades.Spellblades;

public class Default {
    public final static ItemConfig itemConfig;
    static {
        itemConfig = new ItemConfig();
        for (var weapon: Spellblades.entries) {
            itemConfig.weapons.put(weapon.name(), weapon.defaults());
        }
        /*for (var armorSet: Armors.entries) {
            itemConfig.armor_sets.put(armorSet.name(), armorSet.defaults());
        }*/
    }
}