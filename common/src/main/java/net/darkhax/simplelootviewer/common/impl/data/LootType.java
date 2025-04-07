package net.darkhax.simplelootviewer.common.impl.data;

import net.darkhax.bookshelf.common.api.function.CachedSupplier;
import net.darkhax.simplelootviewer.common.impl.SimpleLootViewer;
import net.darkhax.simplelootviewer.common.impl.config.CategoryConfig;
import net.darkhax.simplelootviewer.common.impl.config.Config;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

import java.util.Locale;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * The different categories of loot tables handled by this mod.
 */
public enum LootType {

    BLOCK(path -> path.startsWith("blocks/"), () -> Items.GOLDEN_PICKAXE, cfg -> cfg.block_drops),
    ENTITY(path -> path.startsWith("entities/"), () -> Items.CREEPER_SPAWN_EGG, cfg -> cfg.mob_drops),
    LOOT_CHEST(path -> path.startsWith("chests/"), () -> Items.CHEST, cfg -> cfg.loot_chests),
    FISHING(path -> path.startsWith("gameplay/fishing"), () -> Items.FISHING_ROD, cfg -> cfg.fishing),
    HERO(path -> path.startsWith("gameplay/hero"), () -> Items.VILLAGER_SPAWN_EGG, cfg -> cfg.village_hero),
    ARCHAEOLOGY(path -> path.startsWith("archaeology/"), () -> Items.BRUSH, cfg -> cfg.archaeology),
    DISPENSER(path -> path.startsWith("dispensers/"), () -> Items.DISPENSER, cfg -> cfg.dispensers),
    MISC(path -> true, () -> Items.BUNDLE, cfg -> cfg.misc);

    public final ResourceLocation id;
    public final Predicate<String> matcher;
    public final CachedSupplier<ItemLike> icon;
    public final Function<Config, CategoryConfig> configGetter;

    LootType(Predicate<String> matcher, Supplier<ItemLike> icon, Function<Config, CategoryConfig> configGetter) {
        this.id = SimpleLootViewer.id(this.name().toLowerCase(Locale.ROOT));
        this.icon = CachedSupplier.cache(icon);
        this.matcher = matcher;
        this.configGetter = configGetter;
    }

    public boolean isEnabled() {
        return this.configGetter.apply(SimpleLootViewer.CONFIG.get()).enabled;
    }

    public static LootType determineType(ResourceLocation tableId) {
        final LootType override = SimpleLootViewer.CONFIG.get().type_overrides.get().get(tableId);
        if (override != null) {
            return override;
        }
        final String path = tableId.getPath();
        for (LootType type : LootType.values()) {
            if (type.matcher.test(path)) {
                return type;
            }
        }
        return MISC;
    }
}