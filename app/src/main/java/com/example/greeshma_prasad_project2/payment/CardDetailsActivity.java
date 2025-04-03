package com.example.greeshma_prasad_project2.payment;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.greeshma_prasad_project2.R;

public class CardDetailsActivity extends AppCompatActivity {
    private ImageView imBackArrow;
    private EditText etCardNumber,etExpiryDate,etCVV,etCountry,etPostalCode;
    private Button btPayment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_card_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        imBackArrow=findViewById(R.id.imageview_back_arrow);
        etCardNumber=findViewById(R.id.edittext_card_number);
        etExpiryDate=findViewById(R.id.edittext_expiry_date);
        etCVV=findViewById(R.id.edittext_cvv);
        etCountry=findViewById(R.id.edittext_country);
        etPostalCode=findViewById(R.id.edittext_postal_code);
        btPayment=findViewById(R.id.button_proceed_to_payment);
        validatePostalCode();
        imBackArrow.setOnClickListener(view -> finish());
        btPayment.setOnClickListener(view -> {
            if(isValidFields()){
                Intent intent=new Intent(this, ThankyouActivity.class);
                startActivity(intent);
            }
        });
    }
    private boolean isValidCanadianPostalCode(String postalcode){
        String pattern ="^[A-Z][0-9][A-Z]\\s[0-9][A-Z][0-9]$";
        return postalcode.matches(pattern);
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

    private Boolean isValidFields(){
        if(etCardNumber.getText().toString().isEmpty()){
            etCardNumber.setError("Please enter card number");
            return false;
        }else if(etCardNumber.getText().toString().length()!=16){
           etCardNumber.setError("Please enter valid card number");
            return false;
        }else if(etExpiryDate.getText().toString().isEmpty()){
            etExpiryDate.setError("Please enter expiry date");
            return false;
        }else if(etCVV.getText().toString().isEmpty()){
            etCVV.setError("Please enter cvv");
            return false;
        }else if(etCVV.getText().toString().length()!=3){
            etCVV.setError("Please enter valid cvv");
            return false;
        }else if(etCountry.getText().toString().isEmpty()){
            etCountry.setError("Please enter country");
            return false;
        }else if(etPostalCode.getText().toString().isEmpty()){
            etPostalCode.setError("Please enter postal code");
            return false;
        }else if ( !isValidCanadianPostalCode(etPostalCode.getText().toString())) {
            etPostalCode.setError("Invalid postal code");
            return false;
        } else{
            etCardNumber.setError(null);
            etExpiryDate.setError(null);
            etCVV.setError(null);
            etCountry.setError(null);
            etPostalCode.setError(null);
            return true;
        }
    }
}