package com.example.greeshma_prasad_project2.payment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.greeshma_prasad_project2.R;
import com.example.greeshma_prasad_project2.category.HomeActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import cdflynn.android.library.checkview.CheckView;

public class ThankyouActivity extends AppCompatActivity {
    private CheckView checkView;
    private String userId="";
    private FirebaseDatabase database;
    private Button btBackHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_thankyou);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        checkView=findViewById(R.id.imageview_success);
        btBackHome=findViewById(R.id.button_back_home);
        checkView.check();

        userId= FirebaseAuth.getInstance().getCurrentUser().getUid();
        database=FirebaseDatabase.getInstance();
        btBackHome.setOnClickListener(view -> {
            removeAllItemFromCart();
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        removeAllItemFromCart();
    }

    private void removeAllItemFromCart(){
        database.getReference("cart").child(userId).removeValue().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                homeNavigation();
            }
        });
    }

    private void homeNavigation(){
        Intent intent =new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}