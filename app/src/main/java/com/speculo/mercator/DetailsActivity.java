package com.speculo.mercator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.navigation.Navigation;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;

public class DetailsActivity extends AppCompatActivity {

    private CollapsingToolbarLayout collapsingToolbarLayout;

    private FirebaseFirestore firebaseFirestore;

    private SharedPreferences sharedPreferences;

    private TextView seller_name, seller_number, seller_email, description, price, priceTv;
    private ImageView productImage;
    private Button cart_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        firebaseFirestore = FirebaseFirestore.getInstance();
        sharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        productImage = findViewById(R.id.details_image);
        collapsingToolbarLayout = findViewById(R.id.toolbar_layout);
        description = findViewById(R.id.details_product_desc);
        seller_name = findViewById(R.id.details_name_text);
        seller_number = findViewById(R.id.details_number_text);
        seller_email = findViewById(R.id.details_email_text);
        price = findViewById(R.id.details_price_text);
        priceTv = findViewById(R.id.details_product_price);

        collapsingToolbarLayout.setCollapsedTitleTextColor(Color.parseColor("#E9E9E9"));

        cart_btn = findViewById(R.id.details_cart_btn);

        Window window = getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.dark_grey));

        Intent receivedIntent = getIntent();
        String product_name1 = receivedIntent.getStringExtra("product_name");
        String product_image1 = receivedIntent.getStringExtra("product_image");
        String product_price1 = receivedIntent.getStringExtra("product_price");
        String product_desc1 = receivedIntent.getStringExtra("product_desc");
        String seller_name1 = receivedIntent.getStringExtra("seller_name");
        String seller_email1 = receivedIntent.getStringExtra("seller_email");
        String seller_number1 = receivedIntent.getStringExtra("seller_number");

        collapsingToolbarLayout.setTitle(product_name1);

        seller_name.setText(seller_name1);
        seller_email.setText(seller_email1);
        seller_number.setText(seller_number1);
        price.setText("₹ " + product_price1);
        description.setText(product_desc1);

        if(product_price1 == null){
            price.setVisibility(View.GONE);
            priceTv.setVisibility(View.GONE);
        }

        Glide.with(this)
                .load(product_image1)
                .centerCrop()
                .into(productImage);

        cart_btn.setOnClickListener(view -> {
            HashMap<String, String> numberMap = new HashMap<>();
            numberMap.put("product_description", product_desc1);
            numberMap.put("product_image", product_image1);
            numberMap.put("product_name", product_name1);
            if(product_price1 != null) {
                numberMap.put("product_price", product_price1);
            }
            numberMap.put("seller_email", seller_email1);
            numberMap.put("seller_name", seller_name1);
            numberMap.put("seller_number", seller_number1);
            firebaseFirestore.collection("users").document(sharedPreferences.getString("email", null)).collection("cart").document().set(numberMap);
        });
    }
}