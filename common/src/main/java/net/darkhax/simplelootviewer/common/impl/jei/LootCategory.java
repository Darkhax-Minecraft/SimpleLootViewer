package net.darkhax.simplelootviewer.common.impl.jei;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.darkhax.bookshelf.common.api.function.CachedSupplier;
import net.darkhax.simplelootviewer.common.impl.data.LootType;
import net.darkhax.simplelootviewer.common.impl.data.info.TableInfo;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class LootCategory implements IRecipeCategory<TableInfo> {

    private final RecipeType<TableInfo> type;
    private final IGuiHelper guiHelper;
    private final Component title;
    private final CachedSupplier<IDrawable> icon;
    private final IDrawableStatic background;

    public LootCategory(RecipeType<TableInfo> recipeType, LootType type, IGuiHelper guiHelper) {
        this(recipeType, guiHelper, g -> g.createDrawableItemLike(type.icon.get()));
    }

    public LootCategory(RecipeType<TableInfo> type, IGuiHelper guiHelper, Function<IGuiHelper, IDrawable> icon) {
        this.type = type;
        this.guiHelper = guiHelper;
        this.title = Component.translatable("gui.jei.category." + type.getUid().getNamespace() + "." + type.getUid().getPath() + ".name");
        this.icon = CachedSupplier.cache(() -> icon.apply(this.guiHelper));
        this.background = guiHelper.createBlankDrawable(182, 67);
    }

    @NotNull
    @Override
    public RecipeType<TableInfo> getRecipeType() {
        return this.type;
    }

    @NotNull
    @Override
    public Component getTitle() {
        return this.title;
    }

    @Override
    public int getWidth() {
        return 182;
    }

    @Override
    public int getHeight() {
        return 67;
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return this.icon.get();
    }

    @Override
    public void setRecipe(@NotNull IRecipeLayoutBuilder builder, @NotNull TableInfo recipe, @NotNull IFocusGroup focuses) {
        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 3; y++) {
                final int slotIndex = (y * 10) + x;
                final IRecipeSlotBuilder dropSlot = builder.addSlot(RecipeIngredientRole.OUTPUT, 2 + (x * 18), 12 + (y * 18));
                if (slotIndex < recipe.items().size()) {
                    dropSlot.addItemStack(recipe.items().get(slotIndex));
                    dropSlot.addRichTooltipCallback((view, tooltip) -> {
                        if (Minecraft.getInstance().options.advancedItemTooltips) {
                            tooltip.add(Component.literal(recipe.lootTable().toString()).withStyle(ChatFormatting.GRAY));
                        }
                    });
                }
            }
        }
    }

    @Override
    public void draw(@NotNull TableInfo recipe, @NotNull IRecipeSlotsView recipeSlotsView, @NotNull GuiGraphics graphics, double mouseX, double mouseY) {
        this.background.draw(graphics);
        graphics.drawString(Minecraft.getInstance().font, loopingString(recipe.name().name().getString(), 34), 2, 0, 5592405, false);
        final IDrawableStatic slot = guiHelper.getSlotDrawable();
        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 3; y++) {
                slot.draw(graphics, 1 + (x * 18), 11 + (y * 18));
            }
        }
    }

    public static String loopingString(String input, int maxLength) {
        if (input.length() <= maxLength) {
            return input;
        }
        input += "    ";
        final int startIndex = Math.abs((int) (Util.getMillis() / 1000L)) % input.length();
        final StringBuilder result = new StringBuilder(maxLength);
        for (int outputIndex = 0; outputIndex < maxLength; outputIndex++) {
            final int inputIndex = (startIndex + outputIndex) % input.length();
            result.append(input.charAt(inputIndex));
        }
        return result.toString();
    }
}