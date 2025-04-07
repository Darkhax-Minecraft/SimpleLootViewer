package net.darkhax.simplelootviewer.common.impl.data.info;

import net.darkhax.bookshelf.common.api.util.TextHelper;
import net.darkhax.simplelootviewer.common.impl.SimpleLootViewer;
import net.darkhax.simplelootviewer.common.impl.data.LootTableHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Different types of names that we can serialize.
 */
public enum NameType {

    /**
     * Handles block names. Only the ID of each block is serialized, the client will look up these IDs and join each
     * unique name together.
     */
    BLOCK(SimpleLootViewer.UNSAFE_ID_ARRAY_STREAM, data -> TextHelper.joinUnique(SimpleLootViewer.SEPARATOR, Arrays.stream((ResourceLocation[]) data).map(id -> BuiltInRegistries.BLOCK.get(id).getName()).collect(Collectors.toList()))),

    /**
     * Handles entity names. Only the ID of each entity is serialized, the client will look up these IDs and join eac
     * unique name together.
     */
    ENTITY(SimpleLootViewer.UNSAFE_ID_ARRAY_STREAM, data -> TextHelper.joinUnique(SimpleLootViewer.SEPARATOR, Arrays.stream((ResourceLocation[]) data).map(id -> BuiltInRegistries.ENTITY_TYPE.get(id).getDescription()).collect(Collectors.toList()))),

    /**
     * Handles loot tables with names derived from their resource location.
     */
    TABLE(SimpleLootViewer.UNSAFE_ID_STREAM, data -> LootTableHelper.nameTableFromId((ResourceLocation) data)),

    /**
     * Handles names that are sent as only a localization key.
     */
    KEY(StreamCodec.of((buf, val) -> buf.writeUtf((String) val), FriendlyByteBuf::readUtf), data -> Component.translatable((String) data)),

    /**
     * Handles full components without any size mitigating strategy. This is unused but may be used by addons or used in
     * the future.
     */
    COMPONENT(StreamCodec.of((buf, val) -> ComponentSerialization.STREAM_CODEC.encode(buf, (Component) val), ComponentSerialization.STREAM_CODEC::decode), data -> (Component) data);

    /**
     * Handles serializing the data across the network.
     */
    public final StreamCodec<RegistryFriendlyByteBuf, Object> dataCodec;

    /**
     * Handles converting the data into a name.
     */
    public final Function<Object, Component> nameBuilder;

    NameType(StreamCodec<RegistryFriendlyByteBuf, Object> dataCodec, Function<Object, Component> nameBuilder) {
        this.dataCodec = dataCodec;
        this.nameBuilder = nameBuilder;
    }
}