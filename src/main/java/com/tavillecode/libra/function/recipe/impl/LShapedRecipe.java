package com.tavillecode.libra.function.recipe.impl;

import com.tavillecode.itemStorage.utils.ItemGetter;
import com.tavillecode.libra.Libra;
import com.tavillecode.libra.function.recipe.LRecipe;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Interface39
 * @version 1.0
 * @description: TODO
 * @date 2025/3/5 15:04
 */
public class LShapedRecipe extends LRecipe {
    private final NamespacedKey namespacedKey;
    private final ItemStack result;
    private final int resultAmount;
    private final ItemStack blueMap;
    private final int expCost;

    private List<String> ingredients;
    private List<ItemStack> itemIngredients;

    public LShapedRecipe(String recipeName, ItemStack result, ItemStack blueMap, int expCost, List<String> ingredients, List<ItemStack> itemIngredients) {
        this.namespacedKey = new NamespacedKey(Libra.getInstance(),recipeName);
        this.result = result.clone();
        this.resultAmount = result.getAmount();
        this.blueMap = blueMap;
        this.expCost = expCost;
        this.ingredients = ingredients;
        this.itemIngredients = itemIngredients;
        this.assignByItem();
        GLOBAL_SHAPED_RECIPE_LIST.add(this);
    }

    public LShapedRecipe(String recipeName, ItemStack result, int resultAmount, ItemStack blueMap, int expCost, List<String> ingredients) {
        this.namespacedKey = new NamespacedKey(Libra.getInstance(),recipeName);
        this.result = result.clone();
        this.resultAmount = resultAmount;
        this.blueMap = blueMap;
        this.expCost = expCost;
        this.ingredients = ingredients;
        this.itemIngredients = new ArrayList<>();
        this.assignByString();
        GLOBAL_SHAPED_RECIPE_LIST.add(this);
    }

    private void assignByItem() {
        Map<ItemStack,Character> hash = new HashMap<>();
        hash.put(null,' ');
        int index = 0;
        int line = 0;

        StringBuilder builder = new StringBuilder();
        StringBuilder builder1 = new StringBuilder();
        ShapedRecipe shapedRecipe = new ShapedRecipe(this.namespacedKey,this.result);

        for (ItemStack i:itemIngredients) {
            if (!hash.containsKey(i)) {
                hash.put(i,placeHolder.get(index));
                index++;
            }
            if (i==null) {
                builder1.append("空气");
            }
            else {
                builder1.append(ItemGetter.getIdByComponent(i.displayName()));
            }
            builder.append(hash.get(i));
            if ((line+1) % 3 == 0) {
                builder1.append("#");
                builder.append("#");
            }
            else {
                builder1.append("|");
            }
            line++;
        }
        String[] shape = builder.toString().split("#");
        String[] ingredientString = builder1.toString().split("#");

        this.ingredients = new ArrayList<>(List.of(ingredientString));

        shapedRecipe.shape(shape[0],shape[1],shape[2]);
        for (ItemStack item:hash.keySet()) {
            if (item==null) {
                continue;
            }
            shapedRecipe.setIngredient(hash.get(item),item);
        }
        Bukkit.addRecipe(shapedRecipe);

        hash.clear();
    }

    private void assignByString() {
        Map<String, Character> hash = new ConcurrentHashMap<>();
        hash.put("空气",' ');
        int index = 0;

        this.result.setAmount(this.resultAmount);
        ShapedRecipe shapedRecipe = new ShapedRecipe(this.namespacedKey,this.result);
        List<String> shape = new ArrayList<>();


        for (String s:this.ingredients){
             StringBuilder builder = new StringBuilder();
             String[] values = s.split("\\|");
             for (String value : values) {
                 if (!hash.containsKey(value)) {
                     hash.put(value,placeHolder.get(index));
                     index++;
                 }
                 itemIngredients.add(ItemGetter.getItem(value));
                 builder.append(hash.get(value));
             }
             shape.add(builder.toString());
        }


        shapedRecipe.shape(shape.get(0),shape.get(1),shape.get(2));
        for (String item:hash.keySet()) {
            if (item.equals("空气")) {
                continue;
            }
            shapedRecipe.setIngredient(hash.get(item),ItemGetter.getItem(item));
        }
        Bukkit.addRecipe(shapedRecipe);

        shape.clear();
        hash.clear();
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

    public List<ItemStack> getItemIngredients() {
        return this.itemIngredients;
    }

    public List<String> getIngredients() {
        return this.ingredients;
    }

    private static final List<Character> placeHolder;

    public static List<LShapedRecipe> GLOBAL_SHAPED_RECIPE_LIST;

    public static LShapedRecipe getByRecipeName(String name) {
        for (LShapedRecipe recipe:LShapedRecipe.GLOBAL_SHAPED_RECIPE_LIST) {
            if (recipe.getNamespaceKey().getKey().equals(name)) {
                return recipe;
            }
        }
        return null;
    }

    static {
        placeHolder = new ArrayList<>();
        for (int i = 0;i < 9;i++) {
            placeHolder.add((char) ('A'+i));
        }

        GLOBAL_SHAPED_RECIPE_LIST = new ArrayList<>();
    }
}
