package com.tavillecode.libra.storage.impl;

import com.tavillecode.itemStorage.utils.ItemGetter;
import com.tavillecode.libra.Libra;
import com.tavillecode.libra.function.recipe.LRecipe;
import com.tavillecode.libra.function.recipe.RecipeType;
import com.tavillecode.libra.function.recipe.impl.LShapedRecipe;
import com.tavillecode.libra.function.recipe.impl.LShapelessRecipe;
import com.tavillecode.libra.storage.Storage;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Interface39
 * @version 1.0
 * @description: TODO
 * @date 2025/3/5 14:14
 */
public class YamlConfigurationStorage implements Storage {
    protected final boolean createIfNotExist, resource;
    protected final Libra plugin;
    protected FileConfiguration config;
    protected File file, path;
    protected String name;

    public YamlConfigurationStorage(Libra instance, File path, String name, boolean createIfNotExist, boolean resource) {
        this.plugin = instance;
        this.path = path;
        this.name = name + ".yml";
        this.createIfNotExist = createIfNotExist;
        this.resource = resource;
        create();
    }

    public YamlConfigurationStorage(Libra instance, String path, String name, boolean createIfNotExist, boolean resource) {
        this(instance, new File(path), name, createIfNotExist, resource);
    }

    public FileConfiguration getConfig() {
        return config;
    }

    @Override
    public void save() {
        try {
            config.save(file);
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    public File reloadFile() {
        file = new File(path, name);
        return file;
    }

    public FileConfiguration reloadConfig() {
        config = org.bukkit.configuration.file.YamlConfiguration.loadConfiguration(file);
        return config;
    }

    @SuppressWarnings("all")
    @Override
    public void reload() {
        try {
            for (LShapedRecipe l:LShapedRecipe.GLOBAL_SHAPED_RECIPE_LIST) {
                Bukkit.removeRecipe(l.getNamespaceKey());
            }
            LShapedRecipe.GLOBAL_SHAPED_RECIPE_LIST.clear();

            YamlConfiguration yml = (YamlConfiguration) reloadConfig();
            for (String key:yml.getKeys(false)) {
                RecipeType recipeType = RecipeType.valueOf(yml.getConfigurationSection(key).getString("type"));
                ItemStack result = ItemGetter.getItem(yml.getConfigurationSection(key).getString("result"));
                int resultAmount = yml.getConfigurationSection(key).getInt("result_amount");
                ItemStack blueMap = ItemGetter.getItem(yml.getConfigurationSection(key).getString("blue_map"));
                int expCost = yml.getConfigurationSection(key).getInt("unlock_exp");
                switch (recipeType) {
                    case SHAPED -> {
                        List<String> shape = yml.getConfigurationSection(key).getStringList("shape");
                        LShapedRecipe shapedRecipe = new LShapedRecipe(key,result,resultAmount,blueMap,expCost,shape);
                        break;
                    }
                    case SHAPELESS -> {
                        List<String> ingredients = yml.getConfigurationSection(key).getStringList("ingredients");
                        Map<String,Integer> ingredientsMap = new HashMap<>();
                        ingredients.forEach(s -> {
                            String[] spliter = s.split(":");
                            ingredientsMap.put(spliter[0],Integer.parseInt(spliter[1]));
                        });
                        LShapelessRecipe shapelessRecipe = new LShapelessRecipe(key,result,resultAmount,blueMap,expCost,ingredientsMap);
                        break;
                    }
                    default -> {

                    }
                }

            }
        } catch (Exception ex) {
            System.out.print("无法重载!");
        }
    }

    @SuppressWarnings("all")
    @Override
    public void create() {
        if (file == null) {
            reloadFile();
        }
        if (!createIfNotExist || file.exists()) {
            return;
        }
        file.getParentFile().mkdirs();
        if (resource) {
            plugin.saveResource(name, false);
        } else {
            try {
                file.createNewFile();
            } catch (Exception exc) {
                exc.printStackTrace();
            }
        }
        if (config == null) {
            reloadConfig();
        }
    }

    public void addRecipe(LRecipe recipe) {
        new BukkitRunnable() {
            @Override
            public void run() {
                YamlConfiguration yml = (YamlConfiguration) getConfig();
                String key = recipe.getNamespaceKey().getKey();
                ItemStack clone = recipe.getResult().clone();
                clone.setAmount(1);
                yml.createSection(key);
                Objects.requireNonNull(yml.getConfigurationSection(key)).set("type","SHAPED");
                Objects.requireNonNull(yml.getConfigurationSection(key)).set("result",ItemGetter.getIdByComponent(clone.displayName()));
                Objects.requireNonNull(yml.getConfigurationSection(key)).set("result_amount",recipe.getResultAmount());
                if (recipe.getBlueMap() == null) {
                    Objects.requireNonNull(yml.getConfigurationSection(key)).set("blue_map","空气");
                }
                else {
                    Objects.requireNonNull(yml.getConfigurationSection(key)).set("blue_map",ItemGetter.getIdByComponent(recipe.getBlueMap().displayName()));
                }
                Objects.requireNonNull(yml.getConfigurationSection(key)).set("unlock_exp",recipe.getExpCost());
                if (recipe instanceof LShapedRecipe shaped) {
                    Objects.requireNonNull(yml.getConfigurationSection(key)).set("shape",shaped.getIngredients());
                }
                else if (recipe instanceof LShapelessRecipe shapeless){
                    Objects.requireNonNull(yml.getConfigurationSection(key)).set("ingredients",shapeless.getIngredients());
                }
                save();
            }
        }.runTaskAsynchronously(plugin);
    }

    public void removeRecipe(LRecipe recipe) {
        new BukkitRunnable() {
            @Override
            public void run() {
                YamlConfiguration yml = (YamlConfiguration) getConfig();
                yml.set(recipe.getNamespaceKey().getKey(),null);
                save();
            }
        }.runTaskAsynchronously(plugin);
    }
}
