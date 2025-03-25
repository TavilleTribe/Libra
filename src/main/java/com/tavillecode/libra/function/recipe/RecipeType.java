package com.tavillecode.libra.function.recipe;

public enum RecipeType {
    SHAPED("shaped"),SHAPELESS("shapeless"),MATERIAL_SHAPED("material_shaped"),MATERIAL_SHAPELESS("material_shapeless");

    private String name;

    RecipeType(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
