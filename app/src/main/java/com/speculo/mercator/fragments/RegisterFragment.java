package com.speculo.mercator.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.speculo.mercator.R;

import java.util.HashMap;
import java.util.Map;

public class RegisterFragment extends Fragment implements View.OnClickListener {

    // UI element
    private TextView feedbackText, login_now_btn;
    private TextInputEditText regName, regEmail, regPassword, regNumber;
    private TextInputLayout regNameLayout, regEmailLayout, regPasswordLayout, regNumberLayout;
    private Button register_btn;
    private ProgressBar progressBar;

    // firebase authentication
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    // initialize NavController
    private NavController navController;

    private SharedPreferences sharedPreferences;

    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        navController = Navigation.findNavController(view);

        feedbackText = view.findViewById(R.id.registration_feedback_text);
        regName = view.findViewById(R.id.regNameInputField);
        regEmail = view.findViewById(R.id.regEmailInputField);
        regPassword = view.findViewById(R.id.regPassInputField);
        regNumber = view.findViewById(R.id.regNumInputField);
        regNameLayout = view.findViewById(R.id.regNameInputLayout);
        regEmailLayout = view.findViewById(R.id.regEmailInputLayout);
        regPasswordLayout = view.findViewById(R.id.regPassInputLayout);
        regNumberLayout = view.findViewById(R.id.regNumInputLayout);
        register_btn = view.findViewById(R.id.register_button);
        login_now_btn = view.findViewById(R.id.login_now_btn);
        login_now_btn.setOnClickListener(this);
        progressBar = view.findViewById(R.id.register_progressBar);

        sharedPreferences = getActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        Window window = getActivity().getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(getActivity(),R.color.accent_blue));

        regName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                if(!TextUtils.isEmpty(regName.getText().toString().trim())) {
                    if(regNameLayout.getError() == getString(R.string.empty_name_error)) {
                        regNameLayout.setError(null);
                    }
                }
            }
        });

        regEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                if(!TextUtils.isEmpty(regEmail.getText().toString().trim())) {
                    if(regEmailLayout.getError() == getString(R.string.empty_email_error)) {
                        regEmailLayout.setError(null);
                    }
                }

                if(regEmail.getText().toString().trim().contains("@")) {
                    if(regEmailLayout.getError() == getString(R.string.invalid_email_error)) {
                        regEmailLayout.setError(null);
                    }
                }
            }
        });

        regPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                if(!TextUtils.isEmpty(regPassword.getText())) {
                    if(regPasswordLayout.getError() == getString(R.string.empty_password_error)) {
                        regPasswordLayout.setError(null);
                    }
                }
            }
        });

        regNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                if(!TextUtils.isEmpty(regNumber.getText())) {
                    if(regNumberLayout.getError() == getString(R.string.empty_num_error)) {
                        regNumberLayout.setError(null);
                    }
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if(currentUser == null) {
            registerUser();
        } else {
            navController.navigate(R.id.action_registerFragment_to_mainFragment);
        }
    }

    private void registerUser() {
        register_btn.setOnClickListener(view -> {
            feedbackText.setVisibility(View.INVISIBLE);
            String name = regName.getText().toString().trim();
            String email = regEmail.getText().toString().trim();
            String password = regPassword.getText().toString().trim();
            String number = regNumber.getText().toString().trim();

            if(TextUtils.isEmpty(email)) {
                regEmailLayout.setError(getString(R.string.empty_email_error));
            }else if(TextUtils.isEmpty(name)){
                regNameLayout.setError(getString(R.string.empty_name_error));
            }else if(!(email.contains("@"))) {
                regEmailLayout.setError(getString(R.string.invalid_email_error));
            } else if(TextUtils.isEmpty(password)) {
                regPasswordLayout.setError(getString(R.string.empty_password_error));
            } else if(TextUtils.isEmpty(number)) {
                regNumberLayout.setError(getString(R.string.empty_num_error));
            } else {
                progressBar.setVisibility(View.VISIBLE);
                register_btn.setEnabled(false);
                firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        assert user != null;
                        String user_email = user.getEmail();

                        Map<String, Object> new_user = new HashMap<>();
                        new_user.put("name", name);
                        new_user.put("phone_number", number);
                        new_user.put("email", user_email);

                        firebaseFirestore.collection("users").document(user_email).set(new_user);

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("name", name);
                        editor.putString("phone_number", number);
                        editor.putString("email", user_email);
                        editor.apply();

                        progressBar.setVisibility(View.INVISIBLE);
                        navController.navigate(R.id.action_registerFragment_to_mainFragment);
                        register_btn.setEnabled(true);
                    } else {
                        feedbackText.setText(task.getException().toString());
                        feedbackText.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.INVISIBLE);
                        register_btn.setEnabled(true);

                        if(task.getException() instanceof FirebaseAuthUserCollisionException) {
                            regEmailLayout.setError("Email already in use");
                            regPasswordLayout.setError(null);
                            regNumberLayout.setError(null);
                            regNameLayout.setError(null);
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.login_now_btn) {
            navController.navigate(R.id.action_registerFragment_to_loginFragment);
        }
    }
}