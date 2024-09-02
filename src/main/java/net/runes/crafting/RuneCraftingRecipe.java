package net.runes.crafting;

import com.google.gson.JsonObject;
import net.minecraft.block.Blocks;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.*;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.World;

import java.util.stream.Stream;

public class RuneCraftingRecipe implements Recipe<Inventory> {
    final Ingredient base;
    final Ingredient addition;
    final ItemStack result;
    private final Identifier id;

    public RuneCraftingRecipe(Identifier id, Ingredient base, Ingredient addition, ItemStack result) {
        this.id = id;
        this.base = base;
        this.addition = addition;
        this.result = result;
    }

    public boolean matches(Inventory inventory, World world) {
        return this.base.test(inventory.getStack(0)) && this.addition.test(inventory.getStack(1));
    }

    @Override
    public ItemStack craft(Inventory input, RegistryWrapper.WrapperLookup lookup) {
        return this.result.copy();
    }

    public boolean fits(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public ItemStack getResult(RegistryWrapper.WrapperLookup registriesLookup) {
        return null;
    }

    @Override
    public ItemStack getOutput(DynamicRegistryManager registryManager) {
        return this.result;
    }

    public boolean testAddition(ItemStack stack) {
        return this.addition.test(stack);
    }

    public ItemStack createIcon() {
        return new ItemStack(Blocks.SMITHING_TABLE);
    }

    public Identifier getId() {
        return this.id;
    }

    public RecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }

    public RecipeType<?> getType() {
        return TYPE;
    }

    public boolean isEmpty() {
        return Stream.of(this.base, this.addition).anyMatch((ingredient) -> {
            return ingredient.getMatchingStacks().length == 0;
        });
    }

    public static final String NAME = "crafting";

    public static final RecipeType<RuneCraftingRecipe> TYPE = new RecipeType<RuneCraftingRecipe>() {
        public String toString() {
            return NAME;
        }
    };

    public static final Serializer SERIALIZER = new Serializer();

    public static class Serializer implements RecipeSerializer<RuneCraftingRecipe> {
        public RuneCraftingRecipe read(Identifier identifier, JsonObject jsonObject) {
            Ingredient ingredient = Ingredient.fromJson(JsonHelper.getObject(jsonObject, "base"));
            Ingredient ingredient2 = Ingredient.fromJson(JsonHelper.getObject(jsonObject, "addition"));
            ItemStack itemStack = ShapedRecipe.outputFromJson(JsonHelper.getObject(jsonObject, "result"));
            return new RuneCraftingRecipe(identifier, ingredient, ingredient2, itemStack);
        }

        public RuneCraftingRecipe read(Identifier identifier, PacketByteBuf packetByteBuf) {
            Ingredient ingredient = Ingredient.fromPacket(packetByteBuf);
            Ingredient ingredient2 = Ingredient.fromPacket(packetByteBuf);
            ItemStack itemStack = packetByteBuf.readItemStack();
            return new RuneCraftingRecipe(identifier, ingredient, ingredient2, itemStack);
        }

        public void write(PacketByteBuf packetByteBuf, RuneCraftingRecipe recipe) {
            recipe.base.write(packetByteBuf);
            recipe.addition.write(packetByteBuf);
            packetByteBuf.writeItemStack(recipe.result);
        }
    }
}
