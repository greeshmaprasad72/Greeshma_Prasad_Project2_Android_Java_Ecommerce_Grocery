package com.example.greeshma_prasad_project2.payment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.greeshma_prasad_project2.R;
import com.example.greeshma_prasad_project2.models.Checkout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CheckoutActivity extends AppCompatActivity {
    private EditText etFirstName, etLastName, etEmail, etAddress, etZipCode, etCity, etCountry;
    private Button btProceed;
    private ImageView imBackArrow;
    private DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_checkout);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        database=FirebaseDatabase.getInstance().getReference();
        etFirstName = findViewById(R.id.edittext_first_name);
        etLastName = findViewById(R.id.edittext_last_name);
        etEmail = findViewById(R.id.edittext_email);
        etAddress = findViewById(R.id.edittext_address);
        etZipCode = findViewById(R.id.edittext_zipCode);
        etCity = findViewById(R.id.edittext_city);
        etCity = findViewById(R.id.edittext_country);
        btProceed = findViewById(R.id.button_proceed_to_payment);
        imBackArrow = findViewById(R.id.imageview_back_arrow);
        btProceed.setOnClickListener(view -> {
            if(isValidField()){
                insertCheckoutDetails();
            }
        });
        imBackArrow.setOnClickListener(view -> {
            finish();
        });
    }

    private void insertCheckoutDetails(){
        String userId= FirebaseAuth.getInstance().getCurrentUser().getUid();
        database.child("users").child(userId).child("userDetails").setValue(Checkout.class)
                .addOnSuccessListener(task -> {
                        moveToPaymentScreen();
                });

    }

    private void moveToPaymentScreen(){
        Intent intent=new Intent(this,PaymentActivity.class);
        startActivity(intent);
    }

    private boolean isValidField() {

        if (etFirstName.getText().toString().isEmpty()) {
            etFirstName.setError("Please enter firstname");
            return false;

        }else  if (etLastName.getText().toString().isEmpty()) {
            etLastName.setError("Please enter lastname");
            return false;

        }else  if (etEmail.getText().toString().isEmpty()) {
            etEmail.setError("Please enter email");
            return false;

        }else if(!isValidEmail(etEmail.getText().toString())){
            etEmail.setError("Please enter valid email");
            return false;
        }else  if (etAddress.getText().toString().isEmpty()) {
            etAddress.setError("Please enter address");
            return false;

        }else  if (etZipCode.getText().toString().isEmpty()) {
            etZipCode.setError("Please enter ZipCode");
            return false;

        }else  if (etCity.getText().toString().isEmpty()) {
            etCity.setError("Please enter city");
            return false;

        }else  if (etCountry.getText().toString().isEmpty()) {
            etCountry.setError("Please enter country");
            return false;

        }
        else {
            etFirstName.setError(null);
            etLastName.setError(null);
            etEmail.setError(null);
            etAddress.setError(null);
            etZipCode.setError(null);
            etCity.setError(null);
            etCountry.setError(null);

            return true;
        }
    }
    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
}