package com.example.greeshma_prasad_project2.payment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.greeshma_prasad_project2.R;

public class PaymentActivity extends AppCompatActivity {
    private ImageView imBackArrow;
    private ConstraintLayout clCard,clPayPal,clCash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_payment);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        imBackArrow=findViewById(R.id.imageview_back_arrow);
        clCard=findViewById(R.id.cl_payment_card);
        clPayPal=findViewById(R.id.cl_payment_paypal);
        clCash=findViewById(R.id.cl_payment_cash);
        clCard.setOnClickListener(view -> {
            Intent intent=new Intent(this, CardDetailsActivity.class);
            startActivity(intent);
        });
        clPayPal.setOnClickListener(view -> {
            Toast.makeText(this, "Coming soon!", Toast.LENGTH_SHORT).show();
        });
        clCash.setOnClickListener(view -> {
            Intent intent=new Intent(this, ThankyouActivity.class);
            startActivity(intent);

        });
    }
}