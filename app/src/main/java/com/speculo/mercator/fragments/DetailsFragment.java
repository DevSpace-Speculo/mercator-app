package com.speculo.mercator.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.speculo.mercator.R;

public class DetailsFragment extends Fragment {

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    private SharedPreferences sharedPreferences;

    private TextView seller_name, seller_number, seller_email, description, price;
    private ImageView productImage;
    private Button cart_btn;

    private CollapsingToolbarLayout collapsingToolbarLayout;

    private NavController navController;

    public DetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firebaseFirestore = FirebaseFirestore.getInstance();
        sharedPreferences = getActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        navController = Navigation.findNavController(view);

        productImage = view.findViewById(R.id.details_image);
        collapsingToolbarLayout = view.findViewById(R.id.toolbar_layout);
        description = view.findViewById(R.id.details_product_desc);
        seller_name = view.findViewById(R.id.details_name_text);
        seller_number = view.findViewById(R.id.details_number_text);
        seller_email = view.findViewById(R.id.details_email_text);
        price = view.findViewById(R.id.details_price_text);

        collapsingToolbarLayout.setCollapsedTitleTextColor(Color.parseColor("#E9E9E9"));

        cart_btn = view.findViewById(R.id.details_cart_btn);

        Window window = getActivity().getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(getActivity(),R.color.dark_grey));
    }
}