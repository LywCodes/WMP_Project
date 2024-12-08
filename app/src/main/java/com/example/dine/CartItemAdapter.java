package com.example.dine;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class CartItemAdapter extends ArrayAdapter<String> {

    private final List<String> cartItems;
    private final OnItemRemoveListener removeListener;

    public interface OnItemRemoveListener {
        void onItemRemove(int position);
    }

    public CartItemAdapter(Context context, List<String> cartItems, OnItemRemoveListener removeListener) {
        super(context, R.layout.cart_item_layout, cartItems);
        this.cartItems = cartItems;
        this.removeListener = removeListener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.cart_item_layout, parent, false);
        }

        TextView itemText = convertView.findViewById(R.id.cart_item_text);
        ImageView deleteIcon = convertView.findViewById(R.id.cart_item_delete_icon);

        String ingredient = cartItems.get(position);
        itemText.setText(ingredient);

        deleteIcon.setOnClickListener(v -> {
            if (removeListener != null) {
                removeListener.onItemRemove(position);
            }
        });

        return convertView;
    }
}
