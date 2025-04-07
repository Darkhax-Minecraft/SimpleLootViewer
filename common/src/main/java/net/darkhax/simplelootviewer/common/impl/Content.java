package net.darkhax.simplelootviewer.common.impl;

import net.darkhax.bookshelf.common.api.registry.IContentProvider;
import net.darkhax.bookshelf.common.api.registry.register.Register;
import net.darkhax.bookshelf.common.api.registry.register.RegisterRecipeType;
import net.darkhax.simplelootviewer.common.impl.data.LootDataProviderRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class Content implements IContentProvider {

    @Override
    public void registerRecipeTypes(RegisterRecipeType registry) {
        registry.add("loot_table_info");
    }

    @Override
    public void registerRecipeSerializers(Register<RecipeSerializer<?>> registry) {
        registry.add("data", LootDataProviderRecipe.SERIALIZER);
    }

    @Override
    public String contentNamespace() {
        return SimpleLootViewer.MOD_ID;
    }
}