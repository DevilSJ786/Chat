package com.devil.chatapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.devil.chatapplication.databinding.ActivityFlashScreenBinding;
import com.google.firebase.auth.FirebaseAuth;

public class FlashScreen extends AppCompatActivity {
    protected static int Splash_timer = 1000;
    private ActivityFlashScreenBinding binding;
    private View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFlashScreenBinding.inflate(getLayoutInflater());
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        view=binding.appImage;
        view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE|View.SYSTEM_UI_FLAG_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        |View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY|View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        setContentView(binding.getRoot());

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                    Log.d("jaat", "flash:logout " + FirebaseAuth.getInstance().getCurrentUser());

                    startActivity(new Intent(FlashScreen.this, LoginUser.class));
                    finish();
                } else {
                    Log.d("jaat", "flash:login " + FirebaseAuth.getInstance().getCurrentUser().getUid());
                    startActivity(new Intent(FlashScreen.this, MainActivity.class));
                    finish();
                }

            }
        }, Splash_timer);
    }
}