package com.example.greeshma_prasad_project2.authentication;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.greeshma_prasad_project2.R;
import com.example.greeshma_prasad_project2.category.CategoryActivity;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;


    private EditText etPhoneNumber;
    private EditText etEmail;
    private EditText etPassword;
    private TextView tvRegister;
    private Button btContinue;
    private ProgressBar progressBar;

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
        FirebaseApp.initializeApp(this);
        mAuth=FirebaseAuth.getInstance();
        etPhoneNumber=findViewById(R.id.edittext_mobile);
        etEmail=findViewById(R.id.edittext_email);
        etPassword=findViewById(R.id.edittext_password);
        btContinue=findViewById(R.id.button_submit);
        tvRegister=findViewById(R.id.textview_register_button);
        progressBar=findViewById(R.id.pb_login);
        btContinue.setOnClickListener(view -> {
            String email=etEmail.getText().toString();
            String password=etPassword.getText().toString();
            if(isValidField(email,password)){
               showProgressBar();
                loginUsingEmailAndPassword();
            }

        });
        tvRegister.setOnClickListener(view -> {
            Intent intent=new Intent(this, RegisterActivity.class);
            startActivity(intent);
        });
    }


private void loginUsingEmailAndPassword(){
        try{
            mAuth.signInWithEmailAndPassword(etEmail.getText().toString(),etPassword.getText().toString())
                    .addOnCompleteListener(this, task -> {
                        if(task.isSuccessful()){
                            FirebaseUser user=mAuth.getCurrentUser();
                            Intent intent=new Intent(this, CategoryActivity.class);
                            intent.putExtra("userId",user.getUid());
                            startActivity(intent);
                            Log.i("TAG", "loginUsingEmailAndPassword: "+user.getEmail());
                        }else{
                            showAlertDialog("Alert","Username or password not found");
                        }
                        hideProgressBar();
                    });
        } catch (Exception e) {
            hideProgressBar();
            showAlertDialog("Alert","Something went wrong.");
        }


}

private void showProgressBar(){
    progressBar.setVisibility(VISIBLE);
    btContinue.setVisibility(GONE);
    etEmail.setEnabled(false);
    etPassword.setEnabled(false);
}
private void hideProgressBar(){
    progressBar.setVisibility(GONE);
    btContinue.setVisibility(VISIBLE);
    etEmail.setEnabled(true);
    etPassword.setEnabled(true);
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
    public void showAlertDialog(String title, String message) {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        AlertDialog alertDialog= alertDialogBuilder.create();
        alertDialog.show();
    }



}