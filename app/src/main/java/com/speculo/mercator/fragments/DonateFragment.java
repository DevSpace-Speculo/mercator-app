package com.speculo.mercator.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.speculo.mercator.R;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DonateFragment extends Fragment {

    private TextInputEditText name, description;
    private TextInputLayout nameLayout, descriptionLayout;
    private Button donate_btn, upload_img;
    private ProgressBar progressBar;
    private  boolean imageAdded = false;
    Map<String, Object> new_item = new HashMap<>();

    // firebase authentication
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    private SharedPreferences sharedPreferences;

    public DonateFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_donate, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        name = view.findViewById(R.id.donate_name_input_field);
        description = view.findViewById(R.id.donate_desc_input_field);
        nameLayout = view.findViewById(R.id.donate_name_input_layout);
        descriptionLayout = view.findViewById(R.id.donate_desc_input_layout);
        donate_btn = view.findViewById(R.id.donate_submit_button);
        upload_img = view.findViewById(R.id.donate_add_image);
        progressBar = view.findViewById(R.id.donate_progressBar);

        sharedPreferences = getActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                if(!TextUtils.isEmpty(name.getText().toString().trim())) {
                    if(nameLayout.getError() == getString(R.string.empty_name_error)) {
                        nameLayout.setError(null);
                    }
                }
            }
        });

        description.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                if(!TextUtils.isEmpty(description.getText())) {
                    if(descriptionLayout.getError() == getString(R.string.empty_desc_error)) {
                        descriptionLayout.setError(null);
                    }
                }
            }
        });

        donate_btn.setOnClickListener(view1 -> {
            String product_name = name.getText().toString().trim();
            String product_desc = description.getText().toString().trim();

            if(TextUtils.isEmpty(product_name)) {
                nameLayout.setError(getString(R.string.empty_name_error));
            }else if(TextUtils.isEmpty(product_desc)) {
                descriptionLayout.setError(getString(R.string.empty_desc_error));
            } else if(!imageAdded) {
                Toast.makeText(getActivity(), "please add image", Toast.LENGTH_SHORT).show();
            } else {
                progressBar.setVisibility(View.VISIBLE);
                donate_btn.setEnabled(false);

                new_item.put("seller_name", sharedPreferences.getString("name", null));
                new_item.put("seller_number", sharedPreferences.getString("phone_number", null));
                new_item.put("seller_email", sharedPreferences.getString("email", null));
                new_item.put("product_name", product_name);
                new_item.put("product_description", product_desc);

                firebaseFirestore.collection("donations").document().set(new_item);

                Toast.makeText(getActivity(), "Item added...", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(view).popBackStack();
            }
        });

        upload_img.setOnClickListener(view12 -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_PICK);
            galleryLauncher.launch(intent);
        });
    }

    ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    Uri imageUri = data.getData();
                    uploadPicture(imageUri);
                }
            }
    );

    private void uploadPicture(Uri imageUri){
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.show();

        final String randomName = UUID.randomUUID().toString();

        StorageReference storageRef = storageReference.child("item_images/" + randomName);

        storageRef.putFile(imageUri).addOnCompleteListener(task -> storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            new_item.put("product_image", uri.toString());
            imageAdded = true;
            progressDialog.dismiss();
        })).addOnProgressListener(snapshot -> {
            double progressPercent = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
            progressDialog.setMessage("Uploading " + (int)progressPercent + "%");
        }).addOnFailureListener(e -> {
            Toast.makeText(getActivity(), "Upload unsuccessful. Please try later...", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        });
    }
}