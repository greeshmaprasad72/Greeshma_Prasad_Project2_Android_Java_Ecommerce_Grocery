package com.example.greeshma_prasad_project2.cart;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.example.greeshma_prasad_project2.payment.PaymentActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AddressDetailsActivity extends AppCompatActivity {
    private EditText etName,etEmail,etBuildingName,etPostalCode,etArea,etAddressLabel,etMobile;
    private Button btPayment;
    private ImageView imBackArrow;
    private FirebaseDatabase database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_address_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        database=FirebaseDatabase.getInstance();

        etName=findViewById(R.id.edittext_name);
        etEmail=findViewById(R.id.edittext_email);
        etBuildingName=findViewById(R.id.edittext_building_name);
        etPostalCode=findViewById(R.id.edittext_postal_code);
        etArea=findViewById(R.id.edittext_area);
        etAddressLabel=findViewById(R.id.edittext_address_label);
        btPayment=findViewById(R.id.button_proceed_to_payment);
        imBackArrow=findViewById(R.id.imageview_back_arrow);
        etMobile=findViewById(R.id.edittext_mobile);
        imBackArrow.setOnClickListener(view -> {
            finish();
        });
        getUserDetails();
        validatePostalCode();
        btPayment.setOnClickListener(view -> {
            if(isValidField()){
                Intent intent =new Intent(this, PaymentActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void  validatePostalCode(){
        etPostalCode.addTextChangedListener(new TextWatcher() {
            private boolean isUpdating = false;
            private String previousText="";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                previousText = s.toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable editable) {
                if (isUpdating) return;

                String input = editable.toString().replaceAll("[^A-Za-z0-9]", "");
                if (input.length() > 6) {
                    input = input.substring(0, 6);
                }

                isUpdating = true;
                if (input.isEmpty() || input.length() < previousText.replaceAll("[^A-Za-z0-9]", "").length()) {
                    etPostalCode.setText(input.toUpperCase());
                    etPostalCode.setSelection(input.length());
                }else if (input.length() > 3) {
                    String formatted = input.substring(0, 3) + " " + input.substring(3);
                    etPostalCode.setText(formatted.toUpperCase());
                    etPostalCode.setSelection(formatted.length());
                } else {
                    etPostalCode.setText(input.toUpperCase());
                    etPostalCode.setSelection(input.length());
                }

                isUpdating = false;
            }


        });
    }

    private boolean isValidCanadianPostalCode(String postalcode){
            String pattern ="^[A-Z][0-9][A-Z]\\s[0-9][A-Z][0-9]$";
            return postalcode.matches(pattern);
    }

    private void getUserDetails(){
        String userId= FirebaseAuth.getInstance().getCurrentUser().getUid();
        database.getReference("users").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String username=snapshot.child("username").getValue(String.class);
                    String email=snapshot.child("email").getValue(String.class);
                    String mobile=snapshot.child("mobile").getValue(String.class);
                    etName.setText(username);
                    etEmail.setText(email);
                    etMobile.setText(mobile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private boolean isValidField(){
        if(etName.getText().toString().isEmpty()){
            etName.setError("Please enter name");
            return false;
        }else if(etMobile.getText().toString().isEmpty()){
            etMobile.setError("Please enter mobile number");
            return false;
        }
        else if(etEmail.getText().toString().isEmpty()){
            etEmail.setError("Please enter email");
            return false;
        }else if(etBuildingName.getText().toString().isEmpty()){
            etBuildingName.setError("Please enter delivery address");
            return false;
        }else if(etPostalCode.getText().toString().isEmpty()){
            etPostalCode.setError("Please enter postal code");
            return false;
        } else if ( !isValidCanadianPostalCode(etPostalCode.getText().toString())) {
            etPostalCode.setError("Invalid postal code");
            return false;
        } else if(etArea.getText().toString().isEmpty()){
            etArea.setError("Please enter area");
            return false;
        } else{
            etName.setError(null);
            etEmail.setError(null);
            etBuildingName.setError(null);
            etPostalCode.setError(null);
            etName.setError(null);
            return true;
        }
    }
}