package net.darkhax.simplelootviewer.common.impl;

import net.darkhax.bookshelf.common.api.data.codecs.stream.StreamCodecs;
import net.darkhax.bookshelf.common.api.function.CachedSupplier;
import net.darkhax.pricklemc.common.api.config.ConfigManager;
import net.darkhax.simplelootviewer.common.impl.config.Config;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.List;

public class SimpleLootViewer {

    public static final String MOD_ID = "simplelootviewer";
    public static final String MOD_NAME = "Simple Loot Viewer";
    public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);

    public static final Component SEPARATOR = Component.literal(", ");
    public static StreamCodec<RegistryFriendlyByteBuf, ResourceLocation> ID_STREAM = StreamCodec.of(FriendlyByteBuf::writeResourceLocation, FriendlyByteBuf::readResourceLocation);
    public static StreamCodec<RegistryFriendlyByteBuf, Object> UNSAFE_ID_STREAM = StreamCodec.of((buf, val) -> buf.writeResourceLocation((ResourceLocation) val), FriendlyByteBuf::readResourceLocation);
    public static StreamCodec<RegistryFriendlyByteBuf, Object> UNSAFE_ID_ARRAY_STREAM = StreamCodecs.list(ID_STREAM).map(list -> list.toArray(ResourceLocation[]::new), array -> Arrays.asList((ResourceLocation[]) array));
    public static final StreamCodec<RegistryFriendlyByteBuf, List<ItemStack>> ITEM_STACK_LIST = StreamCodecs.list(ItemStack.OPTIONAL_STREAM_CODEC);

    public static final CachedSupplier<Config> CONFIG;

    public static ResourceLocation id(String path) {
        return ResourceLocation.tryBuild(MOD_ID, path);
    }

    public static WeakReference<RegistryAccess> REGISTRY_ACCESS;

    static {
        CONFIG = CachedSupplier.cache(() -> ConfigManager.load(MOD_ID, new Config()));
        CONFIG.get();
    }

    public static void updateRegistryAccess(RegistryAccess access) {
        REGISTRY_ACCESS = new WeakReference<>(access);
        LOG.info("Updating registry access.");
    }
}