package com.example.greeshma_prasad_project2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.chaos.view.PinView;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class OtpActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private PinView mPinView;
    private Button mContinue;

    private String resendVerificationId;

    private String phoneNumber="";
    PhoneAuthProvider.ForceResendingToken resendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    private TextView textviewResend;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_otp);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        mAuth=FirebaseAuth.getInstance();
       mCallbacks= new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                // Automatically sign in if verification is complete
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(OtpActivity.this, "Verification failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                super.onCodeSent(verificationId, token);

                Toast.makeText(OtpActivity.this, "OTP resent", Toast.LENGTH_SHORT).show();
                resendVerificationId = verificationId;
               resendToken = token;
            }
        };

        mPinView=findViewById(R.id.otpPinView);
        mContinue=findViewById(R.id.button_continue_otp);
        resendVerificationId=getIntent().getStringExtra("verificationId");
        phoneNumber=getIntent().getStringExtra("phoneNumber");
        resendToken=getIntent().getParcelableExtra("resendToken");
        mContinue.setOnClickListener(view -> {
            String otp=mPinView.getText().toString();
            if(isValidateOTP(otp)){
                verifyOtp(otp);
            }
        });
        textviewResend.setOnClickListener(view -> {
            resendOTP();

        });

    }

    private void resendOTP(){
        PhoneAuthOptions provider= PhoneAuthOptions.newBuilder().
                setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(mCallbacks)
                .setForceResendingToken(resendToken)
                .build();
        PhoneAuthProvider.verifyPhoneNumber(provider);
    }

    private void verifyOtp(String otp){
        PhoneAuthCredential credential= PhoneAuthProvider.getCredential(resendVerificationId,otp);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential){
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if(task.isSuccessful()){
                        FirebaseUser user=task.getResult().getUser();
                        Intent intent =new Intent(OtpActivity.this,SetUsernameActivity.class);
                        intent.putExtra("user",user);
                        startActivity(intent);
                    }else{
                        Toast.makeText(this, "Failed"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    boolean isValidateOTP(String otp){
        if(otp.isEmpty()){
            Toast.makeText(this, "Please enter OTP", Toast.LENGTH_SHORT).show();
            return false;
        }else{
            return true;
        }
    }
}