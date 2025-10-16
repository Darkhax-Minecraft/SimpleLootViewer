package net.darkhax.simplelootviewer.common.impl;

import net.darkhax.bookshelf.common.api.registry.ContentProvider;
import net.darkhax.bookshelf.common.api.registry.adapters.GameRegistryAdapter;
import net.darkhax.bookshelf.common.impl.registry.adapter.RecipeTypeAdapter;
import net.darkhax.simplelootviewer.common.impl.data.LootDataProviderRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class Content implements ContentProvider {

    @Override
    public void defineRecipeTypes(RecipeTypeAdapter registry) {
        registry.add("loot_table_info");
    }

    @Override
    public void defineRecipeSerializers(GameRegistryAdapter<RecipeSerializer<?>> registry) {
        registry.add("data", LootDataProviderRecipe.SERIALIZER);
    }

    @Override
    public String namespace() {
        return SimpleLootViewer.MOD_ID;
    }
}