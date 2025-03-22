package com.example.greeshma_prasad_project2;

import static com.basgeekball.awesomevalidation.ValidationStyle.BASIC;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;


    private EditText etPhoneNumber;
    private EditText etEmail;
    private EditText etPassword;
    private TextView tvRegister;
    private Button btContinue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etPhoneNumber=findViewById(R.id.edittext_mobile);
        etEmail=findViewById(R.id.edittext_email);
        etPassword=findViewById(R.id.edittext_password);
        btContinue=findViewById(R.id.button_submit);
        tvRegister=findViewById(R.id.textview_register_button);
        btContinue.setOnClickListener(view -> {
            String email=etEmail.getText().toString();
            String password=etPassword.getText().toString();
            if(isValidField(email,password)){
                loginUsingEmailAndPassword();
            }

        });
        tvRegister.setOnClickListener(view -> {
            Intent intent=new Intent(this,RegisterActivity.class);
            startActivity(intent);
        });
    }


private void loginUsingEmailAndPassword(){
        mAuth.signInWithEmailAndPassword(etEmail.getText().toString(),etPassword.getText().toString())
                .addOnCompleteListener(this, task -> {
                    if(task.isSuccessful()){
                        FirebaseUser user=mAuth.getCurrentUser();
                        Intent intent=new Intent(this,HomeActivity.class);
                        intent.putExtra("userId",user.getUid());
                        startActivity(intent);
                        Log.i("TAG", "loginUsingEmailAndPassword: "+user.getEmail());
                    }
                });

}

private boolean isValidField(String email,String password){
        if(email.isEmpty()){
            etEmail.setError("Please enter valid email");
            return false;
        }else if(!isValidEmail(email)){
            etEmail.setError("Please enter valid email");
            return false;
        }else if(password.isEmpty()){
            etPassword.setError("Please enter password");
            return false;
        }else if(password.length()<8 && !isValidPassword(password)){
            etPassword.setError("Please enter valid password. Password should contain at lease 8 characters,at least one lower case , one digit and one special characters");
            return false;
        } else{
            etEmail.setError(null);
            etPassword.setError(null);
            return true;
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