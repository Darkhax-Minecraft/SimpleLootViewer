package net.darkhax.simplelootviewer.common.impl.config;

import net.darkhax.bookshelf.common.api.function.CachedSupplier;
import net.darkhax.pricklemc.common.api.annotations.Value;
import net.darkhax.simplelootviewer.common.impl.SimpleLootViewer;
import net.darkhax.simplelootviewer.common.impl.data.LootType;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Config {

    @Value(comment = "Configures info about what blocks drop when broken.", writeDefault = false)
    public CategoryConfig block_drops = new CategoryConfig();

    @Value(comment = "Configures info about what mobs drop when killed.", writeDefault = false)
    public CategoryConfig mob_drops = new CategoryConfig();

    @Value(comment = "Configures info about what loot can be found in chests.", writeDefault = false)
    public CategoryConfig loot_chests = new CategoryConfig(List.of(
            "minecraft:spawners/trial_chamber/key",
            "minecraft:spawners/ominous/trial_chamber/key",
            "minecraft:spawners/trial_chamber/items_to_drop_when_ominous",
            "minecraft:pots/trial_chambers/corridor",
            "minecraft:spawners/trial_chamber/consumables",
            "minecraft:equipment/trial_chamber_ranged",
            "minecraft:spawners/ominous/trial_chamber/consumables",
            "minecraft:equipment/trial_chamber_melee",
            "minecraft:equipment/trial_chamber"
    ));

    @Value(comment = "Configures info about what items can be fished up.", writeDefault = false)
    public CategoryConfig fishing = new CategoryConfig();

    @Value(comment = "Configures info about what items villagers give to heroes of the village", writeDefault = false)
    public CategoryConfig village_hero = new CategoryConfig();

    @Value(comment = "Configures info about what can be found through archaeology", writeDefault = false)
    public CategoryConfig archaeology = new CategoryConfig();

    @Value(comment = "Configures info about what can be found in dispenser traps.", writeDefault = false)
    public CategoryConfig dispensers = new CategoryConfig(List.of(
            "minecraft:chests/jungle_temple_dispenser"
    ));

    @Value(comment = "Configures info about miscellaneous loot tables that are not otherwise categorized.", writeDefault = false)
    public CategoryConfig misc = new CategoryConfig();

    @Value(comment = "All loot tables in this list will be hidden from players.", writeDefault = false)
    public List<String> hidden_tables = List.of("minecraft:empty");

    public CachedSupplier<Map<ResourceLocation, LootType>> type_overrides = CachedSupplier.cache(() -> {
        final Map<ResourceLocation, LootType> map = new HashMap<>();
        for (LootType type : LootType.values()) {
            type.configGetter.apply(this).overrides.forEach(table -> {
                try {
                    map.put(ResourceLocation.tryParse(table), type);
                }
                catch (Exception e) {
                    SimpleLootViewer.LOG.error("Config override {} for category {} is not a valid ID. Check the format and try again!", table, type.name());
                }
            });
        }
        return map;
    });

    public CachedSupplier<List<ResourceLocation>> hidden = CachedSupplier.cache(() -> {
        final List<ResourceLocation> ids = new ArrayList<>();
        for (String hiddenTable : this.hidden_tables) {
            try {
                ids.add(ResourceLocation.tryParse(hiddenTable));
            }
            catch (Exception e) {
                SimpleLootViewer.LOG.error("Config tried to hide invalid table {}. Check the format and try again!", hiddenTable);
            }
        }
        return ids;
    });
}