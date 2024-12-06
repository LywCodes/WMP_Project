package com.example.dine;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.SearchView;

import com.example.dine.model.Ingredient;
import com.example.dine.network.RetrofitClient;

import java.util.ArrayList;
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

        // Use custom adapter for search results
        searchAdapter = new SearchItemAdapter(requireContext(), searchResults, this);
        listView.setAdapter(searchAdapter);

        // Use default adapter for cart items
        cartAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, cartItems);
        cartListView.setAdapter(cartAdapter);

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

    // Method to add item to the cart from the adapter
    public void addToCart(String ingredient) {
        if (!cartItems.contains(ingredient)) {
            cartItems.add(ingredient);
            cartAdapter.notifyDataSetChanged();
        }
    }
}

