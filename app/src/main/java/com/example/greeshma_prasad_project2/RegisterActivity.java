package com.example.greeshma_prasad_project2;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.greeshma_prasad_project2.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {
    private EditText etUSername,etMobile,etEmail,etPassword,etConfirmPassword;
    private Button buttonRegister;
    private TextView tvLogin;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference myRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        FirebaseApp.initializeApp(this);
        mAuth=FirebaseAuth.getInstance();
        mDatabase=FirebaseDatabase.getInstance();
        myRef = mDatabase.getReference("users");
        etUSername=findViewById(R.id.edittext_username);
        etMobile=findViewById(R.id.edittext_mobile);
        etEmail=findViewById(R.id.edittext_email);
        etPassword=findViewById(R.id.edittext_password);
        etConfirmPassword=findViewById(R.id.edittext_confirm_password);
        buttonRegister=findViewById(R.id.button_submit);
        tvLogin=findViewById(R.id.textview_login);
        buttonRegister.setOnClickListener(view -> {
            String email= etEmail.getText().toString();
            String password=etPassword.getText().toString();
            String username=etUSername.getText().toString();
            String mobile=etMobile.getText().toString();
            String confirmPassword=etConfirmPassword.getText().toString();
            if(isValidFields(mobile,username,email,password,confirmPassword)){
                saveRegister(email,password,username,mobile);
            }

        });
        tvLogin.setOnClickListener(view -> {
            finish();
        });
    }


    private void saveRegister(String email,String password,String username,String mobile){
        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, task -> {
                    if(task.isSuccessful()){
                        FirebaseUser user=mAuth.getCurrentUser();
                        saveUSerDetails(user,email,username,mobile);
                        Log.e("TAG", "saveRegister: "+user);
                    }else{
                        Toast.makeText(this, "Failed to register"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }


    private void saveUSerDetails(FirebaseUser firebaseUser,String email,String username,String mobile){
        User user= new User(username,email,mobile);
        myRef.child(firebaseUser.getUid()).setValue(user)
                .addOnCompleteListener(this, task -> {
                    if(task.isSuccessful()){
                        Toast.makeText(this, "Registration Completed Successfully.", Toast.LENGTH_SHORT).show();
                        Intent intent =new Intent(this,LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }else {
                        Toast.makeText(this, "Registration failed please try again.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private boolean isValidFields(String mobile,String username,String email,String passsword,String confirmPassword){
        if(username.isEmpty()){
            etUSername.setError("Please enter username");
            return false;
        }else if(mobile.isEmpty()){
            etMobile.setError("Please enter mobile number");
            return false;
        }else if(mobile.length()!=10){
            etMobile.setError("Please enter valid mobile number");
            return false;
        }else if(email.isEmpty()){
            etEmail.setError("Please enter email");
            return false;
        }else if(!isValidEmail(email)){
            etEmail.setError("Please enter valid email");
            return false;
        }else if(passsword.isEmpty()){
            etPassword.setError("Please enter password");
            return false;
        }else if(passsword.length()<8 && !isValidPassword(passsword)){
            etPassword.setError("Please enter valid password. Password should contain at lease 8 characters,at least one lower case , one digit and one special characters");
            return false;
        }else if(confirmPassword.isEmpty()){
            etConfirmPassword.setError("Please enter confirm password");

            return false;
        }else if(!passsword.equals(confirmPassword)){
            etConfirmPassword.setError("Passwords do not match");
            return false;
        }else{
            etUSername.setError(null);
            etMobile.setError(null);
            etEmail.setError(null);
            etPassword.setError(null);
            etConfirmPassword.setError(null);
            return  true;
        }

    }

    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
    public static boolean isValidPassword(final String password) {

        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();

    }

}