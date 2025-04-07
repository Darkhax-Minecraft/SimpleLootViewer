package net.darkhax.simplelootviewer.common.impl.data.info;

import net.darkhax.bookshelf.common.api.data.codecs.stream.StreamCodecs;
import net.darkhax.simplelootviewer.common.impl.SimpleLootViewer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * Represents a loot table and the data associated with it.
 *
 * @param lootTable The ID of the loot table.
 * @param name      The name of the loot table.
 * @param items     A list of items that can be dropped by the loot table.
 */
public record TableInfo(ResourceLocation lootTable, Name name, List<ItemStack> items) {

    public static StreamCodec<RegistryFriendlyByteBuf, TableInfo> STREAM = StreamCodec.of(
            (buf, val) -> {
                buf.writeResourceLocation(val.lootTable);
                Name.STREAM.encode(buf, val.name);
                SimpleLootViewer.ITEM_STACK_LIST.encode(buf, val.items);
            },
            buf -> {
                final ResourceLocation tableKey = buf.readResourceLocation();
                final Name name = Name.STREAM.decode(buf);
                final List<ItemStack> items = SimpleLootViewer.ITEM_STACK_LIST.decode(buf);
                return new TableInfo(tableKey, name, items);
            }
    );
    public static StreamCodec<RegistryFriendlyByteBuf, List<TableInfo>> LIST_STREAM = StreamCodecs.list(STREAM);
}