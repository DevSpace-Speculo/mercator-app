package com.speculo.mercator.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.speculo.mercator.R;

public class MainFragment extends Fragment {

    BottomNavigationView bottomNavigationView;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener = item -> {
        switch (item.getItemId()){
            case R.id.nav_home:
                    if (!getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_container_1).getClass().getSimpleName().equals("HomeFragment")) {
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_1, new HomeFragment()).commit();
                    }
                    break;
            case R.id.nav_request:
                    if (!getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_container_1).getClass().getSimpleName().equals("RequestFragment")) {
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_1, new RequestFragment()).commit();
                    }
                    break;
            case R.id.nav_cart:
                    if (!getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_container).getClass().getSimpleName().equals("CartFragment")) {
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_1, new CartFragment()).commit();
                    }
                    break;
            case R.id.nav_account:
                    if (!getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_container_1).getClass().getSimpleName().equals("AccountFragment")) {
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_1, new AccountFragment()).commit();
                    }
                    break;
        }
        return true;
    };

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bottomNavigationView = view.findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_1, new HomeFragment()).commit();

        Window window = getActivity().getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(getActivity(),R.color.app_bar_bg));
    }
}