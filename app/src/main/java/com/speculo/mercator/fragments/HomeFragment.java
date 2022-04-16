package com.speculo.mercator.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.FirebaseFirestore;
import com.speculo.mercator.R;
import com.speculo.mercator.models.ItemsModel;

import java.util.List;

public class HomeFragment extends Fragment {

    private CardView buy_card;
    private NavController navController;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        buy_card = view.findViewById(R.id.buy_card);
        navController = Navigation.findNavController(view);

        buy_card.setOnClickListener(view1 -> navController.navigate(R.id.action_mainFragment_to_buyFragment));
    }
}