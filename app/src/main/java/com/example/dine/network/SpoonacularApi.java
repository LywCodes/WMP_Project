package com.example.dine.network;

import com.example.dine.model.Ingredient;
import com.example.dine.model.Recipe;
import com.example.dine.model.RecipeResponse;
import com.example.dine.model.RecipeDetails;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface SpoonacularApi {

    // Fetch random recipes
    @GET("recipes/random")
    Call<RecipeResponse> getRandomRecipes(
            @Query("apiKey") String apiKey,
            @Query("number") int number
    );

    // Fetch recipe details by ID
    @GET("recipes/{id}/information")
    Call<RecipeDetails> getRecipeDetails(
            @Path("id") int id,
            @Query("apiKey") String apiKey
    );

    @GET("recipes/findByIngredients")
    Call<List<Recipe>> findByIngredients(
            @Query("ingredients") String ingredients,
            @Query("apiKey") String apiKey
    );

    @GET("food/ingredients/autocomplete")
    Call<List<Ingredient>> autocompleteIngredients(
            @Query("query") String query,
            @Query("number") int number,
            @Query("apiKey") String apiKey
    );
}

