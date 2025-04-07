package net.darkhax.simplelootviewer.common.impl.config;

import net.darkhax.pricklemc.common.api.annotations.Value;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class CategoryConfig {

    @Value(comment = "Determines if the category is enabled. Disabled categories are not loaded or sent to players.")
    public boolean enabled = true;

    @Value(comment = "Adding a loot table ID to the override list will forcefully move it to another category.", writeDefault = false)
    public List<String> overrides;

    public CategoryConfig() {
        this(new ArrayList<>());
    }

    public CategoryConfig(List<String> overrides) {
        this.overrides = overrides;
    }
}