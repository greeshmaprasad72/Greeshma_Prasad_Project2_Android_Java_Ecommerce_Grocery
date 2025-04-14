package com.example.greeshma_prasad_project2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.greeshma_prasad_project2.authentication.LoginActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Button btnGetStarted=findViewById(R.id.get_started);
        ImageView imBackground=findViewById(R.id.im_background);
        Glide.with(this)
                .load(R.drawable.bg)
                .override(500, 500)
                .into(imBackground);


        btnGetStarted.setOnClickListener(view -> {
            Intent intent=new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }
}