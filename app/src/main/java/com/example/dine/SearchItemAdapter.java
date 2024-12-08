package com.example.dine;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

public class SearchItemAdapter extends ArrayAdapter<String> {
    private final List<String> items;
    private final RecipeFragment fragment;

    public SearchItemAdapter(@NonNull Context context, List<String> items, RecipeFragment fragment) {
        super(context, R.layout.search_item_layout, items);
        this.items = items;
        this.fragment = fragment;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.search_item_layout, parent, false);
        }

        TextView ingredientName = convertView.findViewById(R.id.ingredientName);
        Button addButton = convertView.findViewById(R.id.addButton);

        String ingredient = items.get(position);
        ingredientName.setText(ingredient);

        // OnClickListener for adding to cart
        addButton.setOnClickListener(v -> fragment.addToCart(ingredient));

        return convertView;
    }
}

