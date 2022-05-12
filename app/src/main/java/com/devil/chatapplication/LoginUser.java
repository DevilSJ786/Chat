package com.devil.chatapplication;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.devil.chatapplication.databinding.ActivityLoginUserBinding;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class LoginUser extends AppCompatActivity {

    private ActivityLoginUserBinding binding;
    private FirebaseAuth mAuth;
    String phone;
    private String verificationId, countryCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginUserBinding.inflate(getLayoutInflater());
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        countryCode = binding.countrycodepicker.getDefaultCountryCodeWithPlus();
        binding.countrycodepicker.setOnCountryChangeListener(() -> countryCode = binding.countrycodepicker.getSelectedCountryCodeWithPlus());

        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginUser.this);

        builder.setMessage("No Internet Connection!");
        builder.setTitle("Alert !");
        builder.setCancelable(true);
        AlertDialog alertDialog = builder.create();
        if (networkInfo != null && networkInfo.isConnected()) {
            alertDialog.cancel();
        } else {
            alertDialog.show();
        }

        binding.sendotpButton.setOnClickListener(v -> {
            if (Objects.requireNonNull(binding.getphonenumber.getText()).toString().length() != 10) {
                binding.phoneNumberlayout.setHelperText("Must be 10 Digits");
            } else {

                phone = countryCode + binding.getphonenumber.getText().toString();
                binding.getphonenumber.setVisibility(View.GONE);
                binding.phoneNumberlayout.setVisibility(View.GONE);
                binding.otpnumberlayout.setVisibility(View.VISIBLE);
                binding.progressbar.setVisibility(View.VISIBLE);
                binding.getotpnumber.setVisibility(View.VISIBLE);
                binding.verifyotpButton.setVisibility(View.VISIBLE);
                binding.sendotpButton.setClickable(false);
                sendVerificationCode(phone);
            }
        });


        binding.verifyotpButton.setOnClickListener(v -> {
            if (Objects.requireNonNull(binding.getotpnumber.getText()).toString().length() != 6) {
                binding.otpnumberlayout.setHelperText("Must be 6 Digits");
            } else if (Objects.requireNonNull(binding.getotpnumber.getText()).toString().length() == 0) {
                binding.otpnumberlayout.setHelperText("Enter 6 Digits OTP");
            } else
                verifyCode(binding.getotpnumber.getText().toString());
        });
    }


    private void signInWithCredential(PhoneAuthCredential credential) {

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Intent i = new Intent(LoginUser.this, Setprofile.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                        finishAffinity();

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

    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks

            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
