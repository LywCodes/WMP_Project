package com.example.dine;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;

public class AccountFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Pastikan Anda menggunakan layout yang benar, misalnya: fragment_account
        return inflater.inflate(R.layout.fragment_account, container, false);
    }
}

