package net.darkhax.simplelootviewer.common.lib;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public abstract class DataRecipe implements Recipe<EmptyRecipeContext> {

    @Override
    public boolean matches(@NotNull EmptyRecipeContext ctx, @NotNull Level level) {
        return false;
    }

    @NotNull
    @Override
    public ItemStack assemble(@NotNull EmptyRecipeContext ctx, @NotNull HolderLookup.Provider provider) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int x, int y) {
        return false;
    }

    @NotNull
    @Override
    public ItemStack getResultItem(@NotNull HolderLookup.Provider provider) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public boolean showNotification() {
        return false;
    }
}
