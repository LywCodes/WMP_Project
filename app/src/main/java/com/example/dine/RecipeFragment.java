package com.example.dine;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.SearchView;

import com.example.dine.model.Ingredient;
import com.example.dine.model.Recipe;
import com.example.dine.network.RetrofitClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecipeFragment extends Fragment {

    private SearchView searchView;
    private ListView listView;
    private ListView cartListView;
    private SearchItemAdapter searchAdapter;
    private ArrayAdapter<String> cartAdapter;
    private List<String> searchResults;
    private List<String> cartItems;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe, container, false);

        searchView = view.findViewById(R.id.searchView);
        listView = view.findViewById(R.id.listView);
        cartListView = view.findViewById(R.id.cartListView);

        searchResults = new ArrayList<>();
        cartItems = new ArrayList<>();

        // Retrieve ingredients from SharedPreferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        String ingredients = sharedPreferences.getString("ingredients", ""); // Default to empty string if not found
        if (!ingredients.isEmpty()) {
            cartItems.addAll(Arrays.asList(ingredients.split(", "))); // Split and add to cartItems
        }

        // Use custom adapter for search results
        searchAdapter = new SearchItemAdapter(requireContext(), searchResults, this);
        listView.setAdapter(searchAdapter);

        // Use default adapter for cart items
        cartAdapter = new CartItemAdapter(requireContext(), cartItems, position -> {
            removeItemFromCart(position);
            Toast.makeText(requireContext(), "Item removed", Toast.LENGTH_SHORT).show();
        });
        cartListView.setAdapter(cartAdapter);

        // Listener for search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                fetchIngredientSuggestions(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                fetchIngredientSuggestions(newText);
                return true;
            }
        });

        // Listen to cart item changes (for recipe search)
        view.findViewById(R.id.btnSearchMenu).setOnClickListener(v -> searchRecipesBasedOnCartItems());

        return view;
    }

    private void fetchIngredientSuggestions(String query) {
        if (query == null || query.trim().isEmpty()) {
            searchResults.clear();
            searchAdapter.notifyDataSetChanged();
            return;
        }

        RetrofitClient.getInstance().getSpoonacularApi()
                .autocompleteIngredients(query, 5, "9f2f420e7c8247819e2d80b0e2c4c463")
                .enqueue(new Callback<List<Ingredient>>() {
                    @Override
                    public void onResponse(Call<List<Ingredient>> call, Response<List<Ingredient>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            searchResults.clear();
                            for (Ingredient ingredient : response.body()) {
                                searchResults.add(ingredient.getName());
                            }
                            searchAdapter.notifyDataSetChanged();
                        } else {
                            Log.e("API_ERROR", "Error: " + response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Ingredient>> call, Throwable t) {
                        Log.e("API_ERROR", "Failure: " + t.getMessage());
                    }
                });
    }

    private void searchRecipesBasedOnCartItems() {
        if (cartItems.isEmpty()) {
            Toast.makeText(requireContext(), "Please add some ingredients to the cart.", Toast.LENGTH_SHORT).show();
            return;
        }

        String ingredientsQuery = String.join(",", cartItems); // Join the cart items into a single query string
        RetrofitClient.getInstance().getSpoonacularApi()
                .findByIngredients(ingredientsQuery, "9f2f420e7c8247819e2d80b0e2c4c463")
                .enqueue(new Callback<List<Recipe>>() {
                    @Override
                    public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<Recipe> recipes = response.body();
                            if (!recipes.isEmpty()) {
                                // Handle the recipe results, for example, display them in a new ListView or RecyclerView
                                displayRecipes(recipes);
                            } else {
                                Toast.makeText(requireContext(), "No recipes found.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.e("API_ERROR", "Error: " + response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Recipe>> call, Throwable t) {
                        Log.e("API_ERROR", "Failure: " + t.getMessage());
                    }
                });
    }

    private void displayRecipes(List<Recipe> recipes) {
        // Prepare the list of recipe titles for display
        List<String> recipeNames = new ArrayList<>();
        for (Recipe recipe : recipes) {
            recipeNames.add(recipe.getTitle());
        }

        // Create an ArrayAdapter for the ListView
        ArrayAdapter<String> recipeAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, recipeNames);
        listView.setAdapter(recipeAdapter);

        // Set an item click listener on the ListView to handle recipe clicks
        listView.setOnItemClickListener((parent, view, position, id) -> {
            // Get the clicked recipe's ID
            int recipeId = recipes.get(position).getId();

            // Create a bundle and pass the recipe ID to the RecipeDetailsFragment
            Bundle bundle = new Bundle();
            bundle.putInt("RECIPE_ID", recipeId);

            // Create the RecipeDetailsFragment and set the arguments
            RecipeDetailsFragment detailsFragment = new RecipeDetailsFragment();
            detailsFragment.setArguments(bundle);

            // Replace the current fragment with the RecipeDetailsFragment
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, detailsFragment)
                    .addToBackStack(null)  // Optional, adds this fragment transaction to the back stack
                    .commit();
        });
    }



    public void addToCart(String ingredient) {
        if (!cartItems.contains(ingredient)) {
            cartItems.add(ingredient);
            cartAdapter.notifyDataSetChanged();
        }
    }

    public void removeItemFromCart(int position) {
        cartItems.remove(position);
        cartAdapter.notifyDataSetChanged();
    }
}
