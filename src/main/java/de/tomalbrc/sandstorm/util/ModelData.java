package de.tomalbrc.sandstorm.util;

import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public record ModelData(Item item, ResourceLocation model) {
    public ItemStack asItemStack() {
        var itemStack = item.getDefaultInstance();
        itemStack.set(DataComponents.ITEM_MODEL, model);
        return itemStack;
    }
}
