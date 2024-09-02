package net.runes.crafting;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.input.RecipeInput;

public record RuneCraftingRecipeInput(ItemStack base, ItemStack addition) implements RecipeInput {
    @Override
    public ItemStack getStackInSlot(int slot) {
        return switch (slot) {
            case 0 -> this.base;
            case 1 -> this.addition;
            default -> throw new IllegalArgumentException("Recipe does not contain slot " + slot);
        };
    }

    @Override
    public int getSize() {
        return 2;
    }

    @Override
    public boolean isEmpty() {
        return this.base.isEmpty() && this.addition.isEmpty();
    }
}
