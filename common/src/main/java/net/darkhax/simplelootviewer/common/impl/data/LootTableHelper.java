package net.darkhax.simplelootviewer.common.impl.data;

import com.google.common.collect.ArrayListMultimap;
import net.darkhax.bookshelf.common.api.loot.LootPoolEntryDescriptions;
import net.darkhax.bookshelf.common.impl.Constants;
import net.darkhax.simplelootviewer.common.impl.SimpleLootViewer;
import net.darkhax.simplelootviewer.common.impl.data.info.Name;
import net.darkhax.simplelootviewer.common.impl.data.info.NameType;
import net.darkhax.simplelootviewer.common.impl.data.info.TableInfo;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;

import java.text.DecimalFormat;
import java.util.Map;

public record LootTableHelper() {

    private static final DecimalFormat FORMAT = new DecimalFormat("#.##");
    private static int CACHE_VERSION = -1;
    private static ArrayListMultimap<LootType, TableInfo> CACHE;

    /**
     * Gets a cached version of the loot table data. The cache will be rebuilt if it is outdated.
     *
     * @param registries The current reloadable game registries. The loot table registry must be available.
     * @return A multimap of info sorted by type of loot table.
     */
    public static ArrayListMultimap<LootType, TableInfo> getCachedData(RegistryAccess registries) {
        if (CACHE == null || CACHE_VERSION != Constants.SERVER_REVISION) {
            SimpleLootViewer.LOG.info("Rebuilding table cache.");
            CACHE = collectData(registries);
            CACHE_VERSION = Constants.SERVER_REVISION;
        }
        return CACHE;
    }

    /**
     * Collects all data on loot tables and stores it in a usable format for our plugin.
     *
     * @param registries The current reloadable game registries. The loot table registry must be available.
     * @return A multimap of table info sorted by type of loot table.
     */
    public static ArrayListMultimap<LootType, TableInfo> collectData(RegistryAccess registries) {
        final long start = System.nanoTime();
        final ArrayListMultimap<LootType, TableInfo> info = ArrayListMultimap.create();
        final Registry<LootTable> lootTableRegistry = registries.registry(Registries.LOOT_TABLE).orElse(null);
        if (lootTableRegistry != null) {
            // Map loot tables to their corresponding blocks.
            final ArrayListMultimap<ResourceLocation, ResourceLocation> BLOCK_DROPS = ArrayListMultimap.create();
            for (Map.Entry<ResourceKey<Block>, Block> block : BuiltInRegistries.BLOCK.entrySet()) {
                BLOCK_DROPS.put(block.getValue().getLootTable().location(), block.getKey().location());
            }
            // Map loot tables to their corresponding mobs.
            final ArrayListMultimap<ResourceLocation, ResourceLocation> ENTITY_DROPS = ArrayListMultimap.create();
            for (Map.Entry<ResourceKey<EntityType<?>>, EntityType<?>> type : BuiltInRegistries.ENTITY_TYPE.entrySet()) {
                ENTITY_DROPS.put(type.getValue().getDefaultLootTable().location(), type.getKey().location());
            }
            // Map tables
            for (Map.Entry<ResourceKey<LootTable>, LootTable> entry : lootTableRegistry.entrySet()) {
                final ResourceLocation tableKey = entry.getKey().location();
                if (!SimpleLootViewer.CONFIG.get().hidden.get().contains(tableKey)) {
                    final LootType lootType = LootType.determineType(tableKey);
                    if (lootType.isEnabled()) {
                        final Name name = nameTable(tableKey, BLOCK_DROPS, ENTITY_DROPS);
                        info.put(lootType, new TableInfo(tableKey, name, LootPoolEntryDescriptions.getUniqueItems(registries, entry.getValue())));
                    }
                }
            }
        }
        else {
            SimpleLootViewer.LOG.warn("Unable to load loot tables from registry.");
        }
        final long end = System.nanoTime();
        SimpleLootViewer.LOG.info("Processed {} loot tables in {}ms", info.size(), FORMAT.format((end - start) / 1000000d));
        return info;
    }

    /**
     * Creates a custom name for a loot table.
     *
     * @param tableKey     The ID of the loot table.
     * @param blockTables  A map of blocks that produce each loot table.
     * @param entityTables A map of entities that produce each loot table.
     * @return A name for the loot table.
     */
    public static Name nameTable(ResourceLocation tableKey, ArrayListMultimap<ResourceLocation, ResourceLocation> blockTables, ArrayListMultimap<ResourceLocation, ResourceLocation> entityTables) {
        if (blockTables.containsKey(tableKey)) {
            return new Name(NameType.BLOCK, blockTables.get(tableKey).toArray(ResourceLocation[]::new));
        }
        else if (entityTables.containsKey(tableKey)) {
            return new Name(NameType.ENTITY, entityTables.get(tableKey).toArray(ResourceLocation[]::new));
        }
        else {
            return new Name(NameType.TABLE, tableKey);
        }
    }

    /**
     * Creates a translatable name based on the ID of the loot table. If the name is not translatable we will fall back
     * to the ID of the table.
     *
     * @param tableKey The ID of the loot table.
     * @return The name of the table.
     */
    public static Component nameTableFromId(ResourceLocation tableKey) {
        return Component.translatableWithFallback("table." + tableKey.getNamespace() + "." + tableKey.getPath().replaceAll("/", ".") + ".name", tableKey.toString());
    }
}