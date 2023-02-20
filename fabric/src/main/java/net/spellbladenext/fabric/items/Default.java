package net.spellbladenext.fabric.items;

import net.minecraft.resources.ResourceLocation;
import net.spell_engine.api.spell.Spell;
import net.spellbladenext.SpellbladeNext;
import net.spellbladenext.config.ItemConfig;
import net.spellbladenext.config.LootConfig;
import net.spellbladenext.items.spellblades.Spellblades;

import java.util.List;

import static net.spellbladenext.SpellbladeNext.MOD_ID;

public class Default {
    public final static ItemConfig itemConfig;
    public final static LootConfig lootConfig;
    static {
        itemConfig = new ItemConfig();
        for (var weapon: Spellblades.entries) {
            itemConfig.weapons.put(weapon.name(), weapon.defaults());
        }
        for (var weapon: Spellblades.orbs) {
            itemConfig.weapons.put(weapon.name(), weapon.defaults());
        }
        for (var weapon: Spellblades.claymores) {
            itemConfig.weapons.put(weapon.name(), weapon.defaults());
        }


        lootConfig = new LootConfig();
        lootConfig.item_groups.put("spellblades", new LootConfig.ItemGroup(List.of(
                Spellblades.fireBlade.id().toString(),
                Spellblades.frostBlade.id().toString(),
                Spellblades.arcaneBlade.id().toString()),
                1
        ).chance(0.3F));
        lootConfig.item_groups.put("orbs", new LootConfig.ItemGroup(List.of(
                Orbs.arcaneOrb.id().toString(),
                Orbs.fireOrb.id().toString(),
                Orbs.frostOrb.id().toString()),
                1
        ).chance(0.3F));
        lootConfig.item_groups.put("claymores", new LootConfig.ItemGroup(List.of(
                Spellblades.arcaneClaymore.id().toString(),
                Spellblades.fireClaymore.id().toString(),
                Spellblades.frostClaymore.id().toString()),
                1
        ).chance(0.3F));
        lootConfig.item_groups.put("runic_armor", new LootConfig.ItemGroup(List.of(
                new ResourceLocation(MOD_ID, "runeblazinghelmet").toString(),
                new ResourceLocation(MOD_ID, "runeblazingbodyarmor").toString(),
                new ResourceLocation(MOD_ID, "runeblazingleggings").toString(),
                new ResourceLocation(MOD_ID, "runeblazingboots").toString(),
                 new ResourceLocation(MOD_ID, "runefrostedhelmet").toString(),
                new ResourceLocation(MOD_ID, "runefrostedbodyarmor").toString(),
                new ResourceLocation(MOD_ID, "runefrostedleggings").toString(),
                new ResourceLocation(MOD_ID, "runefrostedboots").toString(),
                new ResourceLocation(MOD_ID, "runegleaminghelmet").toString(),
                new ResourceLocation(MOD_ID, "runegleamingbodyarmor").toString(),
                new ResourceLocation(MOD_ID, "runegleamingleggings").toString(),
                new ResourceLocation(MOD_ID, "runegleamingboots").toString()),
                1
        ).chance(0.5F));

        List.of("minecraft:chests/abandoned_mineshaft",
                        "minecraft:chests/igloo_chest",
                        "minecraft:chests/ruined_portal",
                        "minecraft:chests/shipwreck_supply",
                        "minecraft:chests/jungle_temple")
                .forEach(id -> lootConfig.loot_tables.put(id, List.of("spellblades")));

        List.of("minecraft:chests/desert_pyramid",
                        "minecraft:chests/bastion_bridge",
                        "minecraft:chests/jungle_temple",
                        "minecraft:chests/pillager_outpost",
                        "minecraft:chests/simple_dungeon",
                        "minecraft:chests/stronghold_crossing")
                .forEach(id -> lootConfig.loot_tables.put(id, List.of("spellblades","claymores","orbs","runic_armor")));

        List.of("minecraft:chests/bastion_other",
                        "minecraft:chests/nether_bridge",
                        "minecraft:chests/underwater_ruin_small")
                .forEach(id -> lootConfig.loot_tables.put(id, List.of("spellblades")));

        List.of("minecraft:chests/shipwreck_treasure")
                .forEach(id -> lootConfig.loot_tables.put(id, List.of("runic_armor")));

        List.of("minecraft:chests/bastion_treasure",
                        "minecraft:chests/stronghold_library",
                        "minecraft:chests/underwater_ruin_big",
                        "minecraft:chests/woodland_mansion")
                .forEach(id -> lootConfig.loot_tables.put(id, List.of("spellblades","claymores","orbs", "runic_armor")));

        List.of("minecraft:chests/end_city_treasure",
                        "minecraft:chests/ancient_city",
                        "minecraft:chests/stronghold_library")
                .forEach(id -> lootConfig.loot_tables.put(id, List.of("spellblades","claymores","orbs", "runic_armor")));

    }
}