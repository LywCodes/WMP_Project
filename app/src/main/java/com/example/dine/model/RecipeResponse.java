package com.example.dine.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class RecipeResponse {

    @SerializedName("recipes")
    private List<Recipe> recipes;

    public List<Recipe> getRecipes() {
        return recipes;
    }
}

