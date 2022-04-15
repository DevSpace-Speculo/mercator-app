package com.speculo.mercator.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.speculo.mercator.R;

import java.util.HashMap;

public class AccountFragment extends Fragment {

    private FirebaseFirestore firebaseFirestore;

    private ImageButton nameBtn, numberBtn;
    private TextView nameText, emailText, numberText;
    private SharedPreferences preferences;

    public AccountFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firebaseFirestore = FirebaseFirestore.getInstance();

        nameBtn = view.findViewById(R.id.edit_name_icon);
        numberBtn = view.findViewById(R.id.edit_number_icon);
        nameText = view.findViewById(R.id.profile_name);
        numberText = view.findViewById(R.id.tv_number);
        emailText = view.findViewById(R.id.tv_email);

        preferences = requireActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        String name = preferences.getString("name", null);
        String number = preferences.getString("phone_number", null);
        String email = preferences.getString("email", null);

        nameText.setText(name);
        numberText.setText(number);
        emailText.setText(email);

        nameBtn.setOnClickListener(view1 -> {
            AlertDialog.Builder nameDialog = new AlertDialog.Builder(getActivity());
            nameDialog.setTitle("Change Name");

            final EditText nameEditText = new EditText(getActivity());
            nameEditText.setHint("name");
            nameDialog.setView(nameEditText);

            nameDialog.setPositiveButton("ok", (dialogInterface, i) -> {
                String edt_name = nameEditText.getText().toString();
                edt_name = edt_name.trim();
                if(!edt_name.equals("")) {
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("name", edt_name);
                    editor.apply();

                    HashMap<String, String> nameMap = new HashMap<>();
                    nameMap.put("name", edt_name);
                    firebaseFirestore.collection("users").document(email).set(nameMap, SetOptions.merge());
                    nameText.setText(edt_name);
                }
            });
            nameDialog.setNegativeButton("Cancel", (dialogInterface, i) -> {});
            nameDialog.show();
        });

        numberBtn.setOnClickListener(view1 -> {
            AlertDialog.Builder nameDialog = new AlertDialog.Builder(getActivity());
            nameDialog.setTitle("Change Phone Number");

            final EditText numberEditText = new EditText(getActivity());
            numberEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
            numberEditText.setHint("phone number");
            nameDialog.setView(numberEditText);

            nameDialog.setPositiveButton("ok", (dialogInterface, i) -> {
                String edt_number = numberEditText.getText().toString().trim();
                if(!edt_number.equals("")) {
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("phone_number", edt_number);
                    editor.apply();

                    HashMap<String, String> numberMap = new HashMap<>();
                    numberMap.put("phone_number", edt_number);
                    firebaseFirestore.collection("users").document(email).set(numberMap, SetOptions.merge());
                    numberText.setText(edt_number);
                }
            });
            nameDialog.setNegativeButton("Cancel", (dialogInterface, i) -> {});
            nameDialog.show();
        });
    }
}