package com.example.dine;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.example.dine.model.Ingredient;
import com.example.dine.model.RecipeDetails;
import com.example.dine.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.widget.Toast;

public class RecipeDetailsFragment extends Fragment {
    private ImageView recipeImageView;
    private TextView titleTextView;
    private TextView descriptionTextView;
    private TextView ingredientsTextView;
    private TextView instructionsTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe_details, container, false);
        // Initialize UI components
        recipeImageView = view.findViewById(R.id.recipe_image);
        titleTextView = view.findViewById(R.id.recipe_title);
        descriptionTextView = view.findViewById(R.id.recipe_description);
        ingredientsTextView = view.findViewById(R.id.recipe_ingredients);
        instructionsTextView = view.findViewById(R.id.recipe_instructions);

        if (getArguments() != null) {
            int recipeId = getArguments().getInt("RECIPE_ID");
            loadRecipeDetails(recipeId);
        }
        return view;
    }

    private void loadRecipeDetails(int recipeId) {
        String apiKey = "9f2f420e7c8247819e2d80b0e2c4c463";
        Call<RecipeDetails> call = RetrofitClient.getInstance().getSpoonacularApi().getRecipeDetails(recipeId, apiKey);
        call.enqueue(new Callback<RecipeDetails>() {
            @Override
            public void onResponse(Call<RecipeDetails> call, Response<RecipeDetails> response) {
                if (response.isSuccessful() && response.body() != null) {
                    RecipeDetails recipe = response.body(); // Directly get the RecipeDetails
                    displayRecipeDetails(recipe);
                } else {
                    Toast.makeText(getActivity(), "Recipe not found", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<RecipeDetails> call, Throwable t) {
                Toast.makeText(getActivity(), "Failed to load recipe details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayRecipeDetails(RecipeDetails recipe) {
        titleTextView.setText(recipe.getTitle());
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            descriptionTextView.setText(Html.fromHtml(recipe.getSummary(), Html.FROM_HTML_MODE_LEGACY));
        }
        else {
            descriptionTextView.setText(recipe.getSummary());
        }
        Glide.with(this)
                .load(recipe.getImageUrl())
                .into(recipeImageView);
        StringBuilder ingredientsBuilder = new StringBuilder();
        for (Ingredient ingredient : recipe.getIngredients()) {
            ingredientsBuilder.append("- ").append(ingredient.getName()).append("\n");
        }
        ingredientsTextView.setText(ingredientsBuilder.toString());
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            instructionsTextView.setText(Html.fromHtml(recipe.getInstructions(), Html.FROM_HTML_MODE_LEGACY));
        }
        else {
            instructionsTextView.setText(recipe.getInstructions());
        }
    }
}
