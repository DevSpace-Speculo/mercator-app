package com.speculo.mercator.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.speculo.mercator.R;
import com.speculo.mercator.ResetPassDialog;

public class LoginFragment extends Fragment implements View.OnClickListener {

    // UI element
    private TextView feedbackText, register_now_btn, forgot_password;
    private TextInputEditText logEmail, logPassword;
    private TextInputLayout logEmailLayout, logPasswordLayout;
    private Button login_btn;
    private ProgressBar progressBar;
    private ConstraintLayout constraintLayout;

    // firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    // initialize NavController
    private NavController navController;

    private SharedPreferences sharedPreferences;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        navController = Navigation.findNavController(view);

        feedbackText = view.findViewById(R.id.start_feedback_text);
        logEmail = view.findViewById(R.id.emailInputField);
        logPassword = view.findViewById(R.id.logPasswordInputField);
        logEmailLayout = view.findViewById(R.id.email);
        logPasswordLayout = view.findViewById(R.id.logPasswordInputLayout);
        login_btn = view.findViewById(R.id.login_Button);
        register_now_btn = view.findViewById(R.id.register_now_btn);
        register_now_btn.setOnClickListener(this);
        forgot_password = view.findViewById(R.id.forgot_pass_btn);
        forgot_password.setOnClickListener(this);

        progressBar = view.findViewById(R.id.start_progressBar);
        constraintLayout = view.findViewById(R.id.login_root_layout);

        sharedPreferences = getActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        Window window = getActivity().getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(getActivity(),R.color.accent_blue));

        logEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                if(!TextUtils.isEmpty(logEmail.getText().toString().trim())) {
                    if(logEmailLayout.getError() == getString(R.string.empty_email_error)) {
                        logEmailLayout.setError(null);
                    }
                }

                if(logEmail.getText().toString().trim().contains("@")) {
                    if(logEmailLayout.getError() == getString(R.string.invalid_email_error)) {
                        logEmailLayout.setError(null);
                    }
                }
            }
        });

        logPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                if(!TextUtils.isEmpty(logPassword.getText())) {
                    if(logPasswordLayout.getError() == getString(R.string.empty_password_error)) {
                        logPasswordLayout.setError(null);
                    } else if(logPasswordLayout.getError() == "Incorrect Password") {
                        logPasswordLayout.setError(null);
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
            loginUser();
        } else {
            navController.navigate(R.id.action_loginFragment_to_mainFragment);
        }
    }

    private void loginUser() {
        login_btn.setOnClickListener(view -> {
            feedbackText.setVisibility(View.INVISIBLE);
            String email = logEmail.getText().toString().trim();
            String password = logPassword.getText().toString().trim();

            if(TextUtils.isEmpty(email)) {
                logEmailLayout.setError(getString(R.string.empty_email_error));
            } else if(TextUtils.isEmpty(password)) {
                logPasswordLayout.setError(getString(R.string.empty_password_error));
            } else if(!(email.contains("@"))) {
                logEmailLayout.setError(getString(R.string.invalid_email_error));
            } else {
                progressBar.setVisibility(View.VISIBLE);
                login_btn.setEnabled(false);
                register_now_btn.setEnabled(false);
                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        DocumentReference docRef = firebaseFirestore.collection("users").document(email);
                        docRef.get().addOnSuccessListener(documentSnapshot -> {
                            if(documentSnapshot.exists()){
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("name", documentSnapshot.getString("name"));
                                editor.putString("phone_number", documentSnapshot.getString("phone_number"));
                                editor.putString("email", documentSnapshot.getString("email"));
                                editor.apply();
                            }
                        });
                        navController.navigate(R.id.action_loginFragment_to_mainFragment);
                    } else {
                        progressBar.setVisibility(View.INVISIBLE);
                        login_btn.setEnabled(true);
                        register_now_btn.setEnabled(true);

                        if(task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            logEmailLayout.setError(null);
                            logPasswordLayout.setError("Incorrect Password");
                        } else if(task.getException() instanceof FirebaseAuthInvalidUserException) {
                            logPasswordLayout.setError(null);
                            logEmailLayout.setError("User does not exist");
                        } else if(task.getException() instanceof FirebaseTooManyRequestsException) {
                            feedbackText.setText("We have blocked all the requests from this device due to unusual activity. \n\n try again later");
                            feedbackText.setVisibility(View.VISIBLE);
                        }
                        else {
                            feedbackText.setText(task.getException().toString());
                            feedbackText.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.register_now_btn) {
            navController.navigate(R.id.action_loginFragment_to_registerFragment);
        } else if(view.getId() == R.id.forgot_pass_btn) {
            ResetPassDialog resetPassDialog = new ResetPassDialog();
            resetPassDialog.show(getFragmentManager(), "Password Reset");
        }
    }
}