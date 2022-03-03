package com.devil.chatapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.devil.chatapplication.databinding.ActivityLoginUserBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class LoginUser extends AppCompatActivity {

    private ActivityLoginUserBinding binding;
    private FirebaseAuth mAuth;
    private String verificationId, countryCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginUserBinding.inflate(getLayoutInflater());
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        countryCode = binding.countrycodepicker.getDefaultCountryCodeWithPlus();
        binding.countrycodepicker.setOnCountryChangeListener(() -> countryCode = binding.countrycodepicker.getSelectedCountryCodeWithPlus());
        binding.sendotpButton.setOnClickListener(v -> {
            if (binding.getphonenumber.getText().toString().length() != 10) {
                Toast.makeText(LoginUser.this, "Please enter a valid phone number.", Toast.LENGTH_SHORT).show();
            } else {

                String phone = countryCode + binding.getphonenumber.getText().toString();
                binding.getphonenumber.setVisibility(View.GONE);
                binding.progressbar.setVisibility(View.VISIBLE);
                binding.getotpnumber.setVisibility(View.VISIBLE);
                binding.verifyotpButton.setVisibility(View.VISIBLE);
                sendVerificationCode(phone);
            }
        });


        binding.verifyotpButton.setOnClickListener(v -> {
            if (TextUtils.isEmpty(binding.getotpnumber.getText().toString())) {
                Toast.makeText(LoginUser.this, "Please enter OTP", Toast.LENGTH_SHORT).show();
            } else {
                verifyCode(binding.getotpnumber.getText().toString());
            }
        });
    }

    private void signInWithCredential(PhoneAuthCredential credential) {

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Intent i = new Intent(LoginUser.this, Setprofile.class);
                        startActivity(i);

                    } else {
                        Toast.makeText(LoginUser.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void sendVerificationCode(String number) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(number)         // Phone number to verify
                        .setTimeout(90L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallBack)         // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks

            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            binding.progressbar.setVisibility(View.GONE);
            verificationId = s;
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            final String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                binding.getotpnumber.setText(code);
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(LoginUser.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    };

    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithCredential(credential);
    }

}
