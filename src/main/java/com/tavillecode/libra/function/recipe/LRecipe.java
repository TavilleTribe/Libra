package com.tavillecode.libra.function.recipe;


import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

/**
 * @author Interface39
 * @version 1.0
 * @description: TODO
 * @date 2025/3/5 10:34
 */
public abstract class LRecipe {
    public abstract NamespacedKey getNamespaceKey();

    public abstract ItemStack getResult();

    public abstract ItemStack getBlueMap();

    public abstract int getExpCost();

    public abstract int getResultAmount();
}
