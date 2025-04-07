package net.darkhax.simplelootviewer.common.lib;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import org.jetbrains.annotations.NotNull;

public class EmptyRecipeContext implements RecipeInput {

    public static final EmptyRecipeContext EMPTY = new EmptyRecipeContext();

    @NotNull
    @Override
    public ItemStack getItem(int slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public int size() {
        return 0;
    }
}