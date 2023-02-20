package net.spellbladenext.fabric.items;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.providers.number.*;
import net.spellbladenext.config.LootConfig;

public class LootHelper {
    public static void configure(ResourceLocation id, LootTable.Builder tableBuilder, LootConfig config) {
        var groups = config.loot_tables.get(id.toString());

        if (groups != null) {
            for(var groupName: groups) {
                var group = config.item_groups.get(groupName);
                if (group == null || group.ids.isEmpty() || group.weight <= 0) { continue; }
                var chance = group.chance > 0 ? group.chance : 1F;
                LootPool.Builder lootPoolBuilder = LootPool.lootPool();
                lootPoolBuilder.setRolls(BinomialDistributionGenerator.binomial(1, chance));
                lootPoolBuilder.setBonusRolls(ConstantValue.exactly(1.2F * chance));
                for (var entryId: group.ids) {
                    var item = SpellbladeItems.entries.get(entryId);
                    if (item == null) { continue; }
                    lootPoolBuilder.add(LootItem.lootTableItem(item).setWeight(group.weight));
                }
                tableBuilder.withPool(lootPoolBuilder);
            }
        }
    }

    private static TagKey<Item> itemTagKey(String id) {
        return TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(id));
    }
}