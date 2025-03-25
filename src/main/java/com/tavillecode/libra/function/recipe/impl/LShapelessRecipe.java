package com.tavillecode.libra.function.recipe.impl;

import com.tavillecode.itemStorage.utils.ItemGetter;
import com.tavillecode.libra.Libra;
import com.tavillecode.libra.function.recipe.LRecipe;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Interface39
 * @version 1.0
 * @description: TODO
 * @date 2025/3/6 15:51
 */
public class LShapelessRecipe extends LRecipe {
    private final NamespacedKey namespacedKey;
    private final ItemStack result;
    private final int resultAmount;
    private final ItemStack blueMap;
    private final int expCost;

    private final Map<ItemStack,Integer> itemIngredients;
    private final Map<String,Integer> ingredients;

    public LShapelessRecipe(String namespacedKey, ItemStack result, ItemStack blueMap, int expCost, Map<String, Integer> ingredients, Map<ItemStack,Integer> itemIngredients) {
        this.namespacedKey = new NamespacedKey(Libra.getInstance(),namespacedKey);
        this.result = result;
        this.resultAmount = result.getAmount();
        this.blueMap = blueMap;
        this.expCost = expCost;
        this.ingredients = ingredients;
        this.itemIngredients = itemIngredients;
        this.assignByItem();
        GLOBAL_SHAPELESS_RECIPE_LIST.add(this);
    }

    public LShapelessRecipe(String namespacedKey, ItemStack result, int resultAmount,ItemStack blueMap, int expCost, Map<String, Integer> ingredients) {
        this.namespacedKey = new NamespacedKey(Libra.getInstance(),namespacedKey);
        this.result = result;
        this.resultAmount = resultAmount;
        this.blueMap = blueMap;
        this.expCost = expCost;
        this.ingredients = ingredients;
        this.itemIngredients = new HashMap<>();
        this.assignByString();
        GLOBAL_SHAPELESS_RECIPE_LIST.add(this);
    }

    private void assignByItem() {
        ShapelessRecipe shapelessRecipe = new ShapelessRecipe(this.namespacedKey,this.result);

        itemIngredients.keySet().forEach(i -> {
            if (i != null) {
                shapelessRecipe.addIngredient(itemIngredients.get(i),i);
                this.ingredients.put(ItemGetter.getIdByComponent(i.displayName()),itemIngredients.get(i));
            }
        });

        Bukkit.addRecipe(shapelessRecipe);
    }

    private void assignByString() {
        ShapelessRecipe shapelessRecipe = new ShapelessRecipe(this.namespacedKey,this.result);

        ingredients.keySet().forEach(s -> {
            shapelessRecipe.addIngredient(ingredients.get(s), ItemGetter.getItem(s));
            this.itemIngredients.put(ItemGetter.getItem(s),ingredients.get(s));
        });

        Bukkit.addRecipe(shapelessRecipe);
    }

    @Override
    public NamespacedKey getNamespaceKey() {
        return this.namespacedKey;
    }

    @Override
    public ItemStack getResult() {
        return this.result;
    }

    @Override
    public ItemStack getBlueMap() {
        return this.blueMap;
    }

    @Override
    public int getExpCost() {
        return this.expCost;
    }

    @Override
    public int getResultAmount() {
        return this.resultAmount;
    }

    public Map<ItemStack,Integer> getItemIngredients() {
        return this.itemIngredients;
    }

    public List<String> getIngredients() {
        List<String> stringIngredients = new ArrayList<>();
        this.ingredients.keySet().forEach(s -> {
            stringIngredients.add(s+":"+this.ingredients.get(s));
        });
        return stringIngredients;
    }

    public static LShapelessRecipe getByRecipeName(String name) {
        for (LShapelessRecipe recipe:LShapelessRecipe.GLOBAL_SHAPELESS_RECIPE_LIST) {
            if (recipe.getNamespaceKey().getKey().equals(name)) {
                return recipe;
            }
        }
        return null;
    }

    public static List<LShapelessRecipe> GLOBAL_SHAPELESS_RECIPE_LIST;

    static {
        GLOBAL_SHAPELESS_RECIPE_LIST = new ArrayList<>();
    }
}
