package net.darkhax.simplelootviewer.common.impl.data.info;

import net.darkhax.bookshelf.common.api.function.CachedSupplier;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;

/**
 * While Mojang already has a component codec, it uses NBT under the hood and is actually pretty slow. In an attempt to
 * make things faster, we are storing names as the smallest amount of data required to reconstruct the component. For
 * example, rather than sending the name of a block we can send the block ID.
 */
public final class Name {

    public static StreamCodec<RegistryFriendlyByteBuf, Name> STREAM = StreamCodec.of(
            (buf, val) -> {
                buf.writeEnum(val.type);
                val.type.dataCodec.encode(buf, val.data);
            },
            buf -> {
                final NameType type = buf.readEnum(NameType.class);
                final Object data = type.dataCodec.decode(buf);
                return new Name(type, data);
            }
    );

    private final NameType type;
    private final Object data;
    private final CachedSupplier<Component> name = CachedSupplier.cache(() -> this.type().nameBuilder.apply(this.data()));

    /**
     * @param type The type of name being stored. This controls the serialization and processing on the stored data.
     * @param data Data used to produce the component. It is unsafe to use data that does not match the expected input.
     */
    public Name(NameType type, Object data) {
        this.type = type;
        this.data = data;
    }

    public Component name() {
        return this.name.get();
    }
    
    public NameType type() {
        return type;
    }

    public Object data() {
        return data;
    }

}