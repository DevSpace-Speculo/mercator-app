package com.speculo.mercator.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.speculo.mercator.R;
import com.speculo.mercator.adapters.CartAdapter;
import com.speculo.mercator.adapters.DonationAdapter;
import com.speculo.mercator.models.DonationModel;
import com.speculo.mercator.models.ItemsModel;

import java.util.ArrayList;
import java.util.List;

public class GetDonationsFragment extends Fragment {

    private RecyclerView recyclerView;
    private FirebaseFirestore firebaseFirestore;
    private List<DonationModel> buyItems;
    private DonationAdapter adapter;

    public GetDonationsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_get_donations, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firebaseFirestore = FirebaseFirestore.getInstance();
        recyclerView = view.findViewById(R.id.donations_recView);
        buyItems = new ArrayList<>();
        adapter = new DonationAdapter(buyItems, requireActivity());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        firebaseFirestore.collection("donations").get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
            for(DocumentSnapshot d : list){
                DonationModel obj = d.toObject(DonationModel.class);
                buyItems.add(obj);
            }
            adapter.notifyDataSetChanged();
        });
    }
}