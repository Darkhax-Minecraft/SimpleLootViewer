package net.darkhax.simplelootviewer.common.impl.jei;

import com.google.common.collect.ArrayListMultimap;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.darkhax.simplelootviewer.common.impl.SimpleLootViewer;
import net.darkhax.simplelootviewer.common.impl.data.LootDataProviderRecipe;
import net.darkhax.simplelootviewer.common.impl.data.LootType;
import net.darkhax.simplelootviewer.common.impl.data.info.TableInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@JeiPlugin
public class Plugin implements IModPlugin {

    public static final ResourceLocation PLUGIN_ID = SimpleLootViewer.id("jei_plugin");

    private final Map<LootType, RecipeType<TableInfo>> info = new HashMap<>();

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        final IGuiHelper gui = registration.getJeiHelpers().getGuiHelper();
        for (LootType type : LootType.values()) {
            final RecipeType<TableInfo> recipeType = RecipeType.create(type.id.getNamespace(), type.id.getPath(), TableInfo.class);
            registration.addRecipeCategories(new LootCategory(recipeType, type, gui));
            info.put(type, recipeType);
        }
    }

    @Override
    public void registerRecipes(@NotNull IRecipeRegistration registration) {
        if (Minecraft.getInstance().level != null) {
            for (RecipeHolder<LootDataProviderRecipe> data : Minecraft.getInstance().level.getRecipeManager().getAllRecipesFor(LootDataProviderRecipe.TYPE.get())) {
                final ArrayListMultimap<LootType, TableInfo> entries = data.value().getData();
                for (LootType type : entries.keySet()) {
                    registration.addRecipes(info.get(type), entries.get(type));
                    SimpleLootViewer.LOG.info("Registering {} {} entries.", entries.get(type).size(), type);
                }
            }
        }
    }

    @NotNull
    @Override
    public ResourceLocation getPluginUid() {
        return PLUGIN_ID;
    }
}
