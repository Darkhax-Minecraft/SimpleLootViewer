package net.darkhax.simplelootviewer.common.impl.data;

import com.google.common.collect.ArrayListMultimap;
import com.mojang.serialization.MapCodec;
import net.darkhax.bookshelf.common.api.function.CachedSupplier;
import net.darkhax.bookshelf.common.api.util.DataHelper;
import net.darkhax.simplelootviewer.common.impl.SimpleLootViewer;
import net.darkhax.simplelootviewer.common.impl.data.info.TableInfo;
import net.darkhax.simplelootviewer.common.lib.DataRecipe;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * A built-in recipe type to handle syncing all the data to clients. While this can be done in other ways, using a
 * recipe ensures the data is reloadable, synced to the client, and made available before has loaded plugins.
 */
public class LootDataProviderRecipe extends DataRecipe {

    public static final ResourceLocation TYPE_ID = SimpleLootViewer.id("loot_table_info");
    public static final Supplier<RecipeType<LootDataProviderRecipe>> TYPE = CachedSupplier.of(BuiltInRegistries.RECIPE_TYPE, TYPE_ID).cast();
    public static final MapCodec<LootDataProviderRecipe> CODEC = MapCodec.unit(() -> new LootDataProviderRecipe(LootTableHelper.getCachedData(Objects.requireNonNull(SimpleLootViewer.REGISTRY_ACCESS.get()))));

    public static final StreamCodec<RegistryFriendlyByteBuf, LootDataProviderRecipe> STREAM = StreamCodec.of(
            (buf, val) -> {
                buf.writeVarInt(val.data.keySet().size());
                val.data.keySet().forEach(type -> {
                    buf.writeEnum(type);
                    TableInfo.LIST_STREAM.encode(buf, val.data.get(type));
                });
            },
            buf -> {
                final ArrayListMultimap<LootType, TableInfo> data = ArrayListMultimap.create();
                final int size = buf.readVarInt();
                for (int i = 0; i < size; i++) {
                    final LootType type = buf.readEnum(LootType.class);
                    List<TableInfo> info = TableInfo.LIST_STREAM.decode(buf);
                    data.putAll(type, info);
                }
                return new LootDataProviderRecipe(data);
            }
    );
    public static final RecipeSerializer<LootDataProviderRecipe> SERIALIZER = DataHelper.recipeSerializer(CODEC, STREAM);

    private final ArrayListMultimap<LootType, TableInfo> data;

    public LootDataProviderRecipe(ArrayListMultimap<LootType, TableInfo> data) {
        this.data = data;
    }

    @NotNull
    @Override
    public RecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }

    @NotNull
    @Override
    public RecipeType<?> getType() {
        return TYPE.get();
    }

    public ArrayListMultimap<LootType, TableInfo> getData() {
        return this.data;
    }
}
