package com.tavillecode.libra.function.recipe;

public enum RecipeType {
    SHAPED("shaped"),SHAPELESS("shapeless");

    private String name;

    RecipeType(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
